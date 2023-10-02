package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import java.util.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.stack.core.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.core.txtproj.lib.storage.*;
import org.toxsoft.core.txtproj.lib.workroom.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;

/**
 * Lifecycle Manager of {@link UaTreeNode} entities.
 *
 * @author max
 * @author dima
 */
public class OpcUaNodeM5LifecycleManager
    extends M5LifecycleManager<UaTreeNode, OpcUaClient> {

  /**
   * id secton for cached OPC UA nodes
   */
  public static final String SECTID_OPC_UA_NODES = "opc.ua.nodes"; //$NON-NLS-1$
  private IList<UaTreeNode>  cached;

  private NodeId              topNodeId = Identifiers.RootFolder;
  private UaTreeNode          topNode   = null;
  private final ITsGuiContext context;

  /**
   * Constructor by M5 model and service
   *
   * @param aModel IM5Model - model
   * @param aClient OpcUaClient - opc ua client
   * @param aContext {@link ITsGuiContext} - app context
   */
  public OpcUaNodeM5LifecycleManager( IM5Model<UaTreeNode> aModel, OpcUaClient aClient, ITsGuiContext aContext ) {
    super( aModel, false, false, false, true, aClient );
    context = aContext;
  }

  /**
   * Constructor by M5 model and service and top node
   *
   * @param aModel {@link IM5Model} - model
   * @param aClient {@link OpcUaClient} - opc ua client
   * @param aTopNodeId {@link NodeId} NodeId - top node id
   * @param aContext {@link ITsGuiContext} - app context
   */
  public OpcUaNodeM5LifecycleManager( IM5Model<UaTreeNode> aModel, OpcUaClient aClient, NodeId aTopNodeId,
      ITsGuiContext aContext ) {
    super( aModel, false, false, false, true, aClient );
    topNodeId = aTopNodeId;
    context = aContext;
  }

  @SuppressWarnings( "boxing" )
  @Override
  protected IList<UaTreeNode> doListEntities() {
    cached = loadUaTreeNodes( master() );
    if( !cached.isEmpty() ) {
      return cached;
    }

    IListEdit<UaTreeNode> result = new ElemArrayList<>();
    long startTime = System.currentTimeMillis();

    UaNode rootNode;
    try {
      rootNode = master().getAddressSpace().getNode( topNodeId );
    }
    catch( UaException ex ) {
      LoggerUtils.errorLogger().error( ex );
      return result;
    }
    UaTreeNode root = new UaTreeNode( null, rootNode );
    result.add( root );
    browseNode( TsLibUtils.EMPTY_STRING, master(), root, result );
    long endTime = System.currentTimeMillis();

    // сохраним загруженное дерево, чтобы в дальнейшем не ждать годами :)
    saveUaTreeNodes( result );
    long delta = endTime - startTime;
    LoggerUtils.defaultLogger().debug( "Browse took %d[ms]", delta ); //$NON-NLS-1$

    return result;
  }

  boolean stopBrowse = false;

  private void browseNode( String indent, OpcUaClient client, UaTreeNode aParent, IListEdit<UaTreeNode> rNodes ) {
    if( stopBrowse ) {
      return;
    }
    BrowseDescription browse =
        new BrowseDescription( aParent.getUaNode().getNodeId(), BrowseDirection.Forward, Identifiers.References,
            Boolean.TRUE, UInteger.valueOf( NodeClass.Object.getValue() | NodeClass.Variable.getValue() ),
            UInteger.valueOf( BrowseResultMask.All.getValue() ) );

    try {

      BrowseResult browseResult = client.browse( browse ).get();

      ReferenceDescription[] descrs = browseResult.getReferences();
      List<ReferenceDescription> references = descrs != null ? Arrays.asList( descrs ) : Collections.emptyList();

      for( ReferenceDescription rd : references ) {
        ExpandedNodeId eNodeId = rd.getNodeId();

        // recursively browse to children
        Optional<NodeId> oNodeId = rd.getNodeId().toNodeId( client.getNamespaceTable() );

        if( oNodeId.isPresent() ) {
          UaNode uaNode = client.getAddressSpace().getNode( oNodeId.get() );

          UaTreeNode treeNode = new UaTreeNode( aParent, uaNode );
          rNodes.add( treeNode );
          // dima 01.08.23 останавливаемся когда текущий node это node типа VariableNode

          // if( uaNode instanceof UaObjectNode ) {
          if( uaNode instanceof UaVariableNode ) {
            System.out.println( String.format( "%s Node=%s, display=%s", indent, rd.getBrowseName().toString(), //$NON-NLS-1$
                uaNode.getDisplayName() ) );
            // FIXME для ускорения загрузки
            // останавливаемся после первого узла типа переменная
            // stopBrowse = true;
            // return;
          }
          else {
            browseNode( indent + " ", client, treeNode, rNodes ); //$NON-NLS-1$
          }
        }

      }
    }
    catch( Exception e ) {
      LoggerUtils.errorLogger().error( e, "Browsing nodeId=%s failed: %s", //$NON-NLS-1$
          aParent.getUaNode().getNodeId().toParseableString(), e.getMessage() );
    }
  }

  private IList<UaTreeNode> loadUaTreeNodes( OpcUaClient aOpcUaClient ) {
    ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IList<UaTreeNode> retVal = new ElemArrayList<>( storage.readColl( SECTID_OPC_UA_NODES, UaTreeNode.KEEPER ) );
    // на этой стадии у нас "сырые" узлы которые необходимо проинициализировать
    for( UaTreeNode uaNode : retVal ) {
      UaTreeNode parent = findParent( uaNode, retVal );
      uaNode.init( parent, aOpcUaClient );
      if( uaNode.getNodeId().equals( topNodeId.toParseableString() ) ) {
        topNode = uaNode;
      }
    }
    if( !topNodeId.equals( Identifiers.RootFolder ) ) {
      retVal = createSubTree( retVal );
    }
    return retVal;
  }

  private IList<UaTreeNode> createSubTree( IList<UaTreeNode> aWholeNodes ) {
    IListEdit<UaTreeNode> retVal = new ElemArrayList<>();
    for( UaTreeNode node : aWholeNodes ) {
      if( isSubNode( node ) ) {
        retVal.add( node );
      }
    }
    topNode.clearParent();
    return retVal;
  }

  private boolean isSubNode( UaTreeNode aNode ) {
    if( aNode.getParent() == null ) {
      return false;
    }
    if( aNode.getParentNodeId().isBlank() ) {
      return false;
    }
    if( aNode.getParentNodeId().equals( topNodeId.toParseableString() ) ) {
      return true;
    }
    return isSubNode( aNode.getParent() );
  }

  private static UaTreeNode findParent( UaTreeNode aUaNode, IList<UaTreeNode> aNodesCandidates ) {
    UaTreeNode retVal = null;
    if( aUaNode.getParentNodeId().isBlank() ) {
      return retVal;
    }
    for( UaTreeNode nodeCandidate : aNodesCandidates ) {
      if( aUaNode.getParentNodeId().equals( nodeCandidate.getNodeId() ) ) {
        retVal = nodeCandidate;
        break;
      }
    }
    return retVal;
  }

  private void saveUaTreeNodes( IList<UaTreeNode> aUaTreeNodes ) {
    ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();

    storage.writeColl( SECTID_OPC_UA_NODES, aUaTreeNodes, UaTreeNode.KEEPER );
  }

}
