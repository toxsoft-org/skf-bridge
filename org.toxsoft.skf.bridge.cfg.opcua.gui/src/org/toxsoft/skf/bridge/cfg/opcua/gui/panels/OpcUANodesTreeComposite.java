package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import java.util.*;
import java.util.List;
import java.util.concurrent.*;

import org.eclipse.jface.viewers.*;
import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.stack.core.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.slf4j.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.tsnodes.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tsgui.widgets.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;

/**
 * Дерево узлов OPC UA сервера построенное лениво
 *
 * @author max, dima
 */
public class OpcUANodesTreeComposite
    extends TsComposite {

  /**
   * Журнал работы
   */
  // private ILogger logger = LoggerWrapper.getLogger( this.getClass().getName() );
  private final Logger logger = LoggerFactory.getLogger( getClass() );

  // корень дерева
  private NodeId            topNodeId = Identifiers.RootFolder;
  private final OpcUaClient clientOpcUA;

  private static final String UA_NODE_KIND_ID = "uaNode"; //$NON-NLS-1$

  static ITsNodeKind<UaNode> nodeKind =
      new TsNodeKind<>( UA_NODE_KIND_ID, "UA node", "OPC UA node", UaNode.class, true, null ); //$NON-NLS-1$ //$NON-NLS-2$

  IListEdit<ITsNode>   roots = new ElemArrayList<>();
  /**
   * Дерево OPC UA nodes.
   */
  OpcUANodesTreeViewer opcUaNodesTreeViewer;

  /**
   * Контекст.
   */
  ITsGuiContext context;

  /**
   * Конструктор.
   *
   * @param aParent Composite - родительский компонент.
   * @param aContext {@link ITsGuiContext} - контекст.
   * @param aOpcUaClient {@link OpcUaClient} - подключение к OPC UA server
   */
  public OpcUANodesTreeComposite( Composite aParent, ITsGuiContext aContext, OpcUaClient aOpcUaClient ) {
    super( aParent, SWT.NONE );
    setLayout( new BorderLayout() );
    context = aContext;
    clientOpcUA = aOpcUaClient;

    opcUaNodesTreeViewer = new OpcUANodesTreeViewer( aContext );
    opcUaNodesTreeViewer.createControl( this ).setLayoutData( BorderLayout.CENTER );
  }

  /**
   * Возвращает выделенные узлы .
   *
   * @return IList<UaNode> - выделенные узлы.
   */
  public IList<UaNode> getCheckedNodes() {
    Object[] checked = opcUaNodesTreeViewer.getCheckedElements();

    IListEdit<UaNode> retVal = new ElemArrayList<>();
    for( Object checkObj : checked ) {
      if( ((ITsNode)checkObj).kind() == nodeKind ) {
        UaTreeNode uaNode = (UaTreeNode)((ITsNode)checkObj).entity();
        retVal.add( uaNode.getUaNode() );
      }
    }

    return retVal;
  }

  /**
   * Устанавливает верхний узел дерева
   *
   * @param aTopNodeId {@link NodeId} корневой узел дерева
   */
  public void setRoot( NodeId aTopNodeId ) {
    topNodeId = aTopNodeId;

    UaNode rootNode;
    try {
      rootNode = clientOpcUA.getAddressSpace().getNode( topNodeId );
    }
    catch( UaException ex ) {
      LoggerUtils.errorLogger().error( ex );
      return;
    }

    UaTreeNode root = new UaTreeNode( null, rootNode );

    // browseNode( clientOpcUA, root, children );
    // for( UaTreeNode node : children ) {
    // ITsNode tsNode = createNode( opcUaNodesTreeViewer, node );
    // if( tsNode != null ) {
    // roots.add( tsNode );
    // }
    // }
    browseNodeAsync( rootNode ).thenAccept( nodes -> {
      for( UaNode node : nodes ) {
        ITsNode tsNode = createNode( opcUaNodesTreeViewer, node );
        if( tsNode != null ) {
          roots.add( tsNode );
        }
      }
      refresh();
    } );

    // opcUaNodesTreeViewer.setRootNodes( roots );
  }

  private ITsNode createNode( ITsNode aParentNode, UaNode aUaNode ) {

    ChildedTsNode<UaNode> retVal = new ChildedTsNode<>( nodeKind, aParentNode, aUaNode ) {

      @Override
      protected String doGetName() {
        return String.format( "%s [%s]", entity().getBrowseName().getName(), //$NON-NLS-1$
            entity().getNodeId().toParseableString() );
      }

      // @Override
      // protected void doCollectNodes( IListEdit<ITsNode> aChilds ) {
      // // лениво запрашиваем своих детей
      // IListEdit<UaTreeNode> children = new ElemArrayList<>();
      // browseNode( clientOpcUA, aUaTreeNode, children );
      // for( UaTreeNode node : children ) {
      // ITsNode tsNode = createNode( this, node );
      // if( tsNode != null ) {
      // aChilds.add( tsNode );
      // }
      // }
      // }
      @Override
      protected void doCollectNodes( IListEdit<ITsNode> aChilds ) {
        // лениво запрашиваем своих детей
        try {
          browseNodeAsync( aUaNode ).thenAccept( nodes -> {
            for( UaNode uaNode : nodes ) {
              ITsNode tsNode = createNode( this, uaNode );
              aChilds.add( tsNode );
            }
            // refresh();
          } ).get();
        }
        catch( InterruptedException | ExecutionException ex ) {
          LoggerUtils.errorLogger().error( ex );
        }
      }

    };

    return retVal;

  }

  protected void refresh() {
    getDisplay().syncExec( () -> opcUaNodesTreeViewer.setRootNodes( roots ) );
  }

  //
  // -----------------------------------------------------------------------
  // API добавления и удаления слушателей событий установки чек-боксов

  /**
   * Удаляет слушателя событий изменения состояния выбора узлов дерева.
   *
   * @param listener ICheckStateListener - слушатель.
   */
  public void removeCheckStateListener( ICheckStateListener listener ) {
    opcUaNodesTreeViewer.removeCheckStateListener( listener );
  }

  /**
   * Добавляет слушателя событий изменения состояния выбора узлов дерева.
   *
   * @param listener ICheckStateListener - слушатель.
   */
  public void addCheckStateListener( ICheckStateListener listener ) {
    opcUaNodesTreeViewer.addCheckStateListener( listener );
  }

  private void browseNode( OpcUaClient client, UaTreeNode aParent, IListEdit<UaTreeNode> rNodes ) {
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

        logger.info( " NodE=%s", eNodeId );//$NON-NLS-1$
        // browse to children
        Optional<NodeId> oNodeId = rd.getNodeId().toNodeId( client.getNamespaceTable() );

        if( oNodeId.isPresent() ) {
          UaNode uaNode = client.getAddressSpace().getNode( oNodeId.get() );

          UaTreeNode treeNode = new UaTreeNode( aParent, uaNode );
          rNodes.add( treeNode );
        }
      }
    }
    catch( Exception e ) {
      // logger.error( e, "Browsing nodeId=%s failed: %s", aParent.getUaNode().getNodeId().toParseableString(),
      // //$NON-NLS-1$
      // e.getMessage() );
    }
  }

  private CompletableFuture<List<? extends UaNode>> browseNodeAsync( UaNode aParent ) {
    return clientOpcUA.getAddressSpace().browseNodesAsync( aParent );
  }

  // private static List<? extends UaNode> fillTreeNodes( List<? extends UaNode> aNodes, UaTreeNode aParent,
  // IListEdit<UaTreeNode> aResultNodes ) {
  // for( UaNode node : aNodes ) {
  // UaTreeNode treeNode = new UaTreeNode( aParent, node );
  // aResultNodes.add( treeNode );
  // }
  //
  // return aNodes;
  // }
}
