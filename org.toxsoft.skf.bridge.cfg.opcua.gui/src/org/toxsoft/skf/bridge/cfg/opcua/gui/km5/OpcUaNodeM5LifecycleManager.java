package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import java.util.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.stack.core.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.toxsoft.core.log4j.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.logs.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;

/**
 * Lifecycle Manager of {@link UaTreeNode} entities.
 *
 * @author max
 */
public class OpcUaNodeM5LifecycleManager
    extends M5LifecycleManager<UaTreeNode, OpcUaClient> {

  private NodeId  topNodeId = Identifiers.RootFolder;
  /**
   * Журнал работы
   */
  private ILogger logger    = LoggerWrapper.getLogger( this.getClass().getName() );

  /**
   * Constructor by M5 model and service
   *
   * @param aModel IM5Model - model
   * @param aClient OpcUaClient - opc ua client
   */
  public OpcUaNodeM5LifecycleManager( IM5Model<UaTreeNode> aModel, OpcUaClient aClient ) {
    super( aModel, false, false, false, true, aClient );

  }

  /**
   * Constructor by M5 model and service and top node
   *
   * @param aModel {@link IM5Model} - model
   * @param aClient {@link OpcUaClient} - opc ua client
   * @param aTopNodeId {@link NodeId} NodeId - top node id
   */
  public OpcUaNodeM5LifecycleManager( IM5Model<UaTreeNode> aModel, OpcUaClient aClient, NodeId aTopNodeId ) {
    super( aModel, false, false, false, true, aClient );
    topNodeId = aTopNodeId;
  }

  @Override
  protected IList<UaTreeNode> doListEntities() {
    IListEdit<UaTreeNode> result = new ElemArrayList<>();

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

        logger.info( "%s NodE=%s", indent, eNodeId );// .toParseableString()); //$NON-NLS-1$
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
      logger.error( e, "Browsing nodeId=%s failed: %s", aParent.getUaNode().getNodeId().toParseableString(), //$NON-NLS-1$
          e.getMessage() );
    }
  }

}
