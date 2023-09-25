package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.sdk.core.nodes.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.tsnodes.*;
import org.toxsoft.core.tsgui.bricks.tstree.tmm.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.OpcUaNodesSelector.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * Panel to browser sub tree of opc ua server nodes and select list of UaVariableNode .
 *
 * @author dima
 */
public class OpcUaNodesSelector
    extends AbstractTsDialogPanel<IList<UaNode>, OpcUaNodesSelectorContext> {

  static class OpcUaNodesSelectorContext {

    private final NodeId      topNode;
    private final OpcUaClient client;
    private final boolean     hideVariableNodes;

    public OpcUaNodesSelectorContext( NodeId aTopNode, OpcUaClient aClient, boolean isHideVariableNodes ) {
      topNode = aTopNode;
      client = aClient;
      hideVariableNodes = isHideVariableNodes;
    }

  }

  private final ISkConnection conn;

  private IM5CollectionPanel<UaTreeNode> opcUaNodePanel;

  /**
   * Constructor
   *
   * @param aParent Composite - parent component.
   * @param aEnviroment - enviroment of run.
   */
  protected OpcUaNodesSelector( Composite aParent, TsDialog<IList<UaNode>, OpcUaNodesSelectorContext> aEnviroment ) {
    super( aParent, aEnviroment );
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = tsContext().get( ISkConnectionSupplier.class );
    conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<UaTreeNode> model = m5.getModel( OpcUaNodeModel.MODEL_ID, UaTreeNode.class );

    IM5LifecycleManager<UaTreeNode> lm = new OpcUaNodeM5LifecycleManager( model, environ().client, environ().topNode );
    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    ctx.params().addAll( tsContext().params() );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_FALSE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // делаем возможность ставить "крыжики"
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_CHECKS.setValue( ctx.params(), AV_TRUE );
    // обнуляем действие по умолчанию на dbl click
    IMultiPaneComponentConstants.OPDEF_DBLCLICK_ACTION_ID.setValue( ctx.params(), AvUtils.AV_STR_EMPTY );

    MultiPaneComponentModown<UaTreeNode> componentModown =
        new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm );
    UaNodesTreeMaker treeMaker = new UaNodesTreeMaker();
    treeMaker.hideVariableNodes = environ().hideVariableNodes;

    componentModown.tree().setTreeMaker( treeMaker );

    componentModown.treeModeManager().addTreeMode( new TreeModeInfo<>( TMID_GROUP_BY_OPC_UA_ORIGIN,
        STR_N_BY_OPC_NODES_STRUCT, STR_D_BY_OPC_NODES_STRUCT, null, treeMaker ) );
    componentModown.treeModeManager().setCurrentMode( TMID_GROUP_BY_OPC_UA_ORIGIN );

    opcUaNodePanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );
    opcUaNodePanel.createControl( this );

  }

  // TODO копия кода из класса OpcUaServerNodesBrowserPanel
  private static class UaNodesTreeMaker
      implements ITsTreeMaker<UaTreeNode> {

    private static final String IGNORE_REFIX = "/"; //$NON-NLS-1$

    private final ITsNodeKind<UaTreeNode> kind =
        new TsNodeKind<>( "UaTreeNode", UaTreeNode.class, true, IOpcUaServerConnCfgConstants.ICONID_APP_ICON ); //$NON-NLS-1$

    private boolean hideVariableNodes;

    @Override
    public IList<ITsNode> makeRoots( ITsNode aRootNode, IList<UaTreeNode> aItems ) {

      IListEdit<ITsNode> result = new ElemArrayList<>();
      IListEdit<UaTreeNode> roots = new ElemArrayList<>();

      for( UaTreeNode uaTreeNode : aItems ) {
        UaTreeNode parent = uaTreeNode;
        while( parent.getParent() != null ) {
          parent = parent.getParent();
        }

        if( roots.hasElem( parent ) ) {
          continue;
        }

        roots.add( parent );

        DefaultTsNode<UaTreeNode> rootNode = new DefaultTsNode<>( kind, aRootNode, parent );

        formTree( rootNode );
        result.add( rootNode );
      }

      return result;
    }

    private void formTree( DefaultTsNode<UaTreeNode> aParentNode ) {
      for( UaTreeNode child : aParentNode.entity().getChildren() ) {
        if( hideVariableNodes && child.getUaNode() instanceof VariableNode ) {
          continue;
        }
        if( !hideVariableNodes && child.getUaNode() instanceof VariableNode ) {
          // отсекаем узлы у которых имя начинается с символа '/'
          VariableNode varNode = (VariableNode)child.getUaNode();
          String name = varNode.getDisplayName().getText();
          if( name.startsWith( IGNORE_REFIX ) ) {
            continue;
          }
        }
        DefaultTsNode<UaTreeNode> childNode = new DefaultTsNode<>( kind, aParentNode, child );
        if( childNode.entity().getUaNode() instanceof VariableNode ) {
          // FIXME иконки не отображаются, выяснить у Гоги пачему
          childNode.setIconId( ICONID_VARIABLE_NODE );
        }
        if( childNode.entity().getUaNode() instanceof ObjectNode ) {
          childNode.setIconId( ICONID_OBJECT_NODE );
        }
        aParentNode.addNode( childNode );
        formTree( childNode );
      }
    }

    @Override
    public boolean isItemNode( ITsNode aNode ) {
      return true;
    }
  }

  // ------------------------------------------------------------------------------------
  // Реализация интерфейса
  //

  @Override
  protected void doSetDataRecord( IList<UaNode> aData ) {
    // TODO

  }

  @Override
  protected IList<UaNode> doGetDataRecord() {
    IListEdit<UaNode> retVal = new ElemArrayList<>();
    for( UaTreeNode treeNode : opcUaNodePanel.checkSupport().listCheckedItems( true ) ) {
      retVal.add( treeNode.getUaNode() );
    }
    return retVal;
  }

  // ------------------------------------------------------------------------------------
  // Статический метод вызова диалога
  //

  /**
   * Вызов дилога для выбора узлов OPC UA для создания описания класса
   *
   * @param aTsContext ITsGuiContext - соответствующий контекст
   * @param aTopNode - верхний узел поддерева
   * @param aClient - OPC UA
   * @return IList<UaNode> - список выбранных узлов или <b>null</b> в случае отказа от выбора
   */
  public static IList<UaNode> selectUaNodes4Class( ITsGuiContext aTsContext, NodeId aTopNode, OpcUaClient aClient ) {
    ITsDialogInfo cdi =
        new TsDialogInfo( aTsContext, "Создание класса из дерева узлов OPC UA", "Пометьте нужные узлы и нажмите Ok" );
    OpcUaNodesSelectorContext ctx = new OpcUaNodesSelectorContext( aTopNode, aClient, false );

    IDialogPanelCreator<IList<UaNode>, OpcUaNodesSelectorContext> creator = OpcUaNodesSelector::new;
    TsDialog<IList<UaNode>, OpcUaNodesSelectorContext> d = new TsDialog<>( cdi, null, ctx, creator );
    return d.execData();
  }

  /**
   * Вызов дилога для выбора узлов OPC UA для создания объектов
   *
   * @param aTsContext ITsGuiContext - соответствующий контекст
   * @param aTopNode - верхний узел поддерева
   * @param aClient - OPC UA
   * @return IList<UaNode> - список выбранных узлов или <b>null</b> в случае отказа от выбора
   */
  public static IList<UaNode> selectUaNodes4Objects( ITsGuiContext aTsContext, NodeId aTopNode, OpcUaClient aClient ) {
    ITsDialogInfo cdi =
        new TsDialogInfo( aTsContext, "Создание объектов из дерева узлов OPC UA", "Пометьте нужные узлы и нажмите Ok" );
    OpcUaNodesSelectorContext ctx = new OpcUaNodesSelectorContext( aTopNode, aClient, true );

    IDialogPanelCreator<IList<UaNode>, OpcUaNodesSelectorContext> creator = OpcUaNodesSelector::new;
    TsDialog<IList<UaNode>, OpcUaNodesSelectorContext> d = new TsDialog<>( cdi, null, ctx, creator );
    return d.execData();
  }

}