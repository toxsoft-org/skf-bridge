package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.core.*;
import org.eclipse.milo.opcua.stack.core.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.tsnodes.*;
import org.toxsoft.core.tsgui.bricks.tstree.tmm.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.bricks.geometry.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.OpcUaNodesSelector.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

import com.google.common.collect.*;

/**
 * Panel to browser sub tree of opc ua server nodes and select list of UaVariableNode .
 *
 * @author dima
 */
public class OpcUaNodesSelector
    extends AbstractTsDialogPanel<IList<UaTreeNode>, OpcUaNodesSelectorContext> {

  static class OpcUaNodesSelectorContext {

    private final NodeId                    topNode;
    private final NodeId                    selNode;
    private final OpcUaClient               client;
    private final boolean                   hideVariableNodes;
    private final boolean                   checkable;
    private final IOpcUaServerConnCfg       serverConnCfg;
    private final ImmutableSet<AccessLevel> accessLevel;

    public OpcUaNodesSelectorContext( NodeId aTopNode, OpcUaClient aClient, boolean isHideVariableNodes,
        boolean isCheckable, IOpcUaServerConnCfg aServerConnCfg, ImmutableSet<AccessLevel> aAccessLevel ) {
      topNode = aTopNode;
      selNode = NodeId.NULL_VALUE;
      client = aClient;
      hideVariableNodes = isHideVariableNodes;
      checkable = isCheckable;
      serverConnCfg = aServerConnCfg;
      accessLevel = aAccessLevel;
    }

    public OpcUaNodesSelectorContext( NodeId aTopNode, NodeId aInitNode, OpcUaClient aClient,
        boolean isHideVariableNodes, boolean isCheckable, IOpcUaServerConnCfg aServerConnCfg,
        ImmutableSet<AccessLevel> aAccessLevel ) {
      topNode = aTopNode;
      selNode = aInitNode;
      client = aClient;
      hideVariableNodes = isHideVariableNodes;
      checkable = isCheckable;
      serverConnCfg = aServerConnCfg;
      accessLevel = aAccessLevel;
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
  protected OpcUaNodesSelector( Composite aParent,
      TsDialog<IList<UaTreeNode>, OpcUaNodesSelectorContext> aEnviroment ) {
    super( aParent, aEnviroment );
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = tsContext().get( ISkConnectionSupplier.class );
    conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<UaTreeNode> model = m5.getModel( OpcUaNodeModel.MODEL_ID, UaTreeNode.class );

    IM5LifecycleManager<UaTreeNode> lm = new OpcUaNodeM5LifecycleManager( model, environ().client, environ().topNode,
        tsContext(), environ().serverConnCfg );
    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    ctx.params().addAll( tsContext().params() );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_FALSE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );

    // возможность ставить "крыжики"
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_CHECKS.setValue( ctx.params(),
        environ().checkable ? AV_TRUE : AV_FALSE );
    // обнуляем действие по умолчанию на dbl click
    IMultiPaneComponentConstants.OPDEF_DBLCLICK_ACTION_ID.setValue( ctx.params(), AvUtils.AV_STR_EMPTY );

    MultiPaneComponentModown<UaTreeNode> componentModown =
        new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm ) {

          // добавляем tool bar
          @Override
          protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
              IListEdit<ITsActionDef> aActs ) {
            aActs.add( ITsStdActionDefs.ACDEF_SEPARATOR );
            aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_FILTER_READ_ONLY );
            aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_FILTER_WRITE_ONLY );
            aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_FILTER_WRITE_READ );
            aActs.add( ITsStdActionDefs.ACDEF_SEPARATOR );
            aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_FILTER_READ_ONLY_POLIGON );

            ITsToolbar toolBar = super.doCreateToolbar( aContext, aName, aIconSize, aActs );

            toolBar.addListener( aActionId -> {
              if( aActionId == FILTER_READ_ONLY_ACT_ID ) {
                recreateTree( AccessLevel.READ_ONLY );
              }
              if( aActionId == FILTER_WRITE_ONLY_ACT_ID ) {
                recreateTree( AccessLevel.WRITE_ONLY );
              }
              if( aActionId == FILTER_WRITE_READ_ACT_ID ) {
                recreateTree( AccessLevel.READ_WRITE );
              }
              if( aActionId == FILTER_READ_ONLY_POLIGON_ACT_ID ) {
                UaNodesTreeMaker treeMaker = new UaNodesTreeMaker( environ().topNode );
                treeMaker.hideRonPoligon = toolBar.getAction( ACTDEF_FILTER_READ_ONLY_POLIGON.id() ).isChecked();
                tree().setTreeMaker( treeMaker );
                tree().refresh();
                tree().console().expandAll();
              }
            } );
            toolBar.setIconSize( EIconSize.IS_24X24 );
            return toolBar;
          }

          private void recreateTree( ImmutableSet<AccessLevel> aAccessLevel ) {
            UaNodesTreeMaker treeMaker = new UaNodesTreeMaker( environ().topNode );
            treeMaker.accessLevel = aAccessLevel;
            tree().setTreeMaker( treeMaker );
            tree().refresh();
            tree().console().expandAll();
          }

        };

    UaNodesTreeMaker treeMaker = new UaNodesTreeMaker( environ().topNode );
    treeMaker.hideVariableNodes = environ().hideVariableNodes;
    treeMaker.accessLevel = environ().accessLevel;

    componentModown.tree().setTreeMaker( treeMaker );

    componentModown.treeModeManager().addTreeMode( new TreeModeInfo<>( TMID_GROUP_BY_OPC_UA_ORIGIN,
        STR_N_BY_OPC_NODES_STRUCT, STR_D_BY_OPC_NODES_STRUCT, null, treeMaker ) );
    componentModown.treeModeManager().setCurrentMode( TMID_GROUP_BY_OPC_UA_ORIGIN );

    opcUaNodePanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );
    opcUaNodePanel.createControl( this );
    // сразу расхлопнем все дерево
    componentModown.tree().console().expandAll();
  }

  // TODO копия кода из класса OpcUaServerNodesBrowserPanel
  private static class UaNodesTreeMaker
      implements ITsTreeMaker<UaTreeNode> {

    private static final String IGNORE_PREFIX    = "null";   //$NON-NLS-1$
    private static final String STATIC_NODE_NAME = "Static"; //$NON-NLS-1$

    private final ITsNodeKind<UaTreeNode> kind =
        new TsNodeKind<>( "UaTreeNode", UaTreeNode.class, true, ICONID_TSAPP_WINDOWS_ICON ); //$NON-NLS-1$

    private boolean                   hideVariableNodes;
    private ImmutableSet<AccessLevel> accessLevel = AccessLevel.NONE;
    protected boolean                 hideRonPoligon;
    final private NodeId              topNodeId;

    public UaNodesTreeMaker( NodeId aTopNode ) {
      topNodeId = aTopNode;
    }

    @Override
    public IList<ITsNode> makeRoots( ITsNode aRootNode, IList<UaTreeNode> aItems ) {
      IListEdit<ITsNode> result = new ElemArrayList<>();
      IListEdit<UaTreeNode> roots = new ElemArrayList<>();

      // check list is empty
      if( aItems.isEmpty() ) {
        return result;
      }
      // if top is Root just add one node
      if( topNodeId.equals( Identifiers.RootFolder ) ) {
        UaTreeNode root = aItems.first();
        roots.add( root );
        DefaultTsNode<UaTreeNode> rootNode = new DefaultTsNode<>( kind, aRootNode, root );

        formTree( rootNode );
        result.add( rootNode );
        return result;
      }

      for( UaTreeNode uaTreeNode : aItems ) {
        UaTreeNode parent = uaTreeNode;
        // version with connection
        while( !parent.getParent().getUaNode().getNodeId().equals( topNodeId ) ) {
          parent = parent.getParent();
        }
        // old version without connection
        // while( parent.getParent() != null ) {
        // parent = parent.getParent();
        // }

        // filter empty nodes (type Variable)
        if( !isThroughtVariableFilter( parent ) ) {
          continue;
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
        if( hideVariableNodes && child.getNodeClass().equals( NodeClass.Variable ) ) {
          continue;
        }
        if( hideRonPoligon && child.getNodeClass().equals( NodeClass.Variable ) ) {
          NodeId nodeId = NodeId.parse( child.getNodeId() );
          UShort ni = nodeId.getNamespaceIndex();
          if( (ni.intValue() & 0x8000) > 0 ) {
            continue;
          }
        }
        if( hideVariableNodes && child.getNodeClass().equals( NodeClass.Object ) ) {
          String name = child.getBrowseName();
          // отрезаем листья Static & null
          if( name.compareTo( STATIC_NODE_NAME ) == 0 || name.startsWith( IGNORE_PREFIX ) ) {
            continue;
          }
        }

        if( !hideVariableNodes && isThroughtVariableFilter( child ) ) {
          // if( !hideVariableNodes && child.getNodeClass().equals( NodeClass.Variable ) ) {
          // отсекаем узлы у которых имя начинается с символа '/'
          // String name = child.getBrowseName();
          // if( name.startsWith( IGNORE_PREFIX ) ) {
          // continue;
          // }
          // String descr = child.getDescription();
          // if( descr.startsWith( "\\" ) || descr.startsWith( "/" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
          // continue;
          // }
          // фильтруем узлы по уровню доступа
          if( !accessLevel.equals( AccessLevel.NONE ) && !accessLevel.containsAll( child.accessLevel() ) ) {
            continue;
          }
        }
        DefaultTsNode<UaTreeNode> childNode = new DefaultTsNode<>( kind, aParentNode, child );
        if( childNode.entity().getNodeClass().equals( NodeClass.Variable ) ) {
          // FIXME иконки не отображаются, выяснить у Гоги пачему
          childNode.setIconId( ICONID_VARIABLE_NODE );
        }
        if( childNode.entity().getNodeClass().equals( NodeClass.Object ) ) {
          childNode.setIconId( ICONID_OBJECT_NODE );
        }
        aParentNode.addNode( childNode );
        formTree( childNode );
      }
    }

    private static boolean isThroughtVariableFilter( UaTreeNode aCandidate ) {
      if( aCandidate.getNodeClass().equals( NodeClass.Variable ) ) {
        // отсекаем узлы у которых имя начинается с символа '/'
        String browseName = aCandidate.getBrowseName();
        if( browseName.startsWith( IGNORE_PREFIX ) ) {
          return false;
        }
        String descr = aCandidate.getDescription();
        if( descr.startsWith( "\\" ) || descr.startsWith( "/" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
          return false;
        }
      }
      return true;
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
  protected void doSetDataRecord( IList<UaTreeNode> aInitNodes ) {
    UaTreeNode selTreeNode = getSelTreeNode();
    if( selTreeNode != null ) {
      opcUaNodePanel.setSelectedItem( selTreeNode );
    }
    if( opcUaNodePanel.checkSupport().isChecksSupported() && aInitNodes != null ) {
      opcUaNodePanel.checkSupport().setItemsCheckState( aInitNodes, true );
    }
  }

  private UaTreeNode getSelTreeNode() {
    NodeId selNodeId = environ().selNode;
    for( UaTreeNode uaTreeNode : opcUaNodePanel.items() ) {
      NodeId nodeId = NodeId.parse( uaTreeNode.getNodeId() );
      if( nodeId.equals( selNodeId ) ) {
        return uaTreeNode;
      }
    }
    return null;
  }

  @Override
  protected IList<UaTreeNode> doGetDataRecord() {
    IListEdit<UaTreeNode> retVal = new ElemArrayList<>();
    if( opcUaNodePanel.checkSupport().isChecksSupported() ) {
      for( UaTreeNode treeNode : opcUaNodePanel.checkSupport().listCheckedItems( true ) ) {
        if( environ().hideVariableNodes && treeNode.getNodeClass().equals( NodeClass.Variable ) ) {
          // игнорируем узлы описания переменных
          continue;
        }
        retVal.add( treeNode );
      }
    }
    else {
      retVal.add( opcUaNodePanel.selectedItem() );
    }
    return retVal;
  }

  // ------------------------------------------------------------------------------------
  // Статические метод вызова диалога
  //

  /**
   * Вызов диалога для выбора одного произвольного узла OPC UA
   *
   * @param aTsContext ITsGuiContext - соответствующий контекст
   * @param aClient - OPC UA
   * @param aInitNode - текущий выбранный узел
   * @param aServerConnCfg - параметры текущего подключения к OPC UA
   * @return IList<UaTreeNode> - список выбранных узлов или <b>null</b> в случае отказа от выбора
   */
  public static IList<UaTreeNode> selectUaNode( ITsGuiContext aTsContext, OpcUaClient aClient, NodeId aInitNode,
      IOpcUaServerConnCfg aServerConnCfg ) {
    ITsDialogInfo cdi = new TsDialogInfo( aTsContext, STR_MSG_SELECT_NODE, STR_DESCR_SELECT_NODE );
    OpcUaNodesSelectorContext ctx = new OpcUaNodesSelectorContext( Identifiers.RootFolder, aInitNode, aClient, false,
        false, aServerConnCfg, AccessLevel.READ_WRITE );

    IDialogPanelCreator<IList<UaTreeNode>, OpcUaNodesSelectorContext> creator = OpcUaNodesSelector::new;
    TsDialog<IList<UaTreeNode>, OpcUaNodesSelectorContext> d = new TsDialog<>( cdi, IList.EMPTY, ctx, creator );
    return d.execData();
  }

  /**
   * Вызов диалога для выбора узлов OPC UA для создания описания класса
   *
   * @param aTsContext ITsGuiContext - соответствующий контекст
   * @param aTopNode - верхний узел поддерева
   * @param aClient - OPC UA
   * @param aServerConnCfg - параметры текущего подключения к OPC UA
   * @return IList<UaTreeNode> - список выбранных узлов или <b>null</b> в случае отказа от выбора
   */
  public static IList<UaTreeNode> selectUaNodes4Class( ITsGuiContext aTsContext, NodeId aTopNode, OpcUaClient aClient,
      IOpcUaServerConnCfg aServerConnCfg ) {
    TsDialogInfo cdi = new TsDialogInfo( aTsContext, STR_MSG_SELECT_NODE_4_CLASS, STR_DESCR_SELECT_NODE );
    // установим нормальный размер диалога
    cdi.setMinSize( new TsPoint( -30, -60 ) );
    OpcUaNodesSelectorContext ctx =
        new OpcUaNodesSelectorContext( aTopNode, aClient, false, true, aServerConnCfg, AccessLevel.READ_WRITE );

    IDialogPanelCreator<IList<UaTreeNode>, OpcUaNodesSelectorContext> creator = OpcUaNodesSelector::new;
    TsDialog<IList<UaTreeNode>, OpcUaNodesSelectorContext> d = new TsDialog<>( cdi, null, ctx, creator );
    return d.execData();
  }

  /**
   * Вызов дилога для выбора узлов OPC UA для создания объектов
   *
   * @param aTsContext ITsGuiContext - соответствующий контекст
   * @param aTopNode - верхний узел поддерева
   * @param aClient - OPC UA
   * @param aServerConnCfg - параметры текущего подключения к OPC UA
   * @return IList<UaTreeNode> - список выбранных узлов или <b>null</b> в случае отказа от выбора
   */
  public static IList<UaTreeNode> selectUaNodes4Objects( ITsGuiContext aTsContext, NodeId aTopNode, OpcUaClient aClient,
      IOpcUaServerConnCfg aServerConnCfg ) {
    TsDialogInfo cdi = new TsDialogInfo( aTsContext, STR_MSG_SELECT_NODE_4_OBJS, STR_DESCR_SELECT_NODE );
    // установим нормальный размер диалога
    cdi.setMinSize( new TsPoint( -30, -60 ) );
    OpcUaNodesSelectorContext ctx =
        new OpcUaNodesSelectorContext( aTopNode, aClient, true, true, aServerConnCfg, AccessLevel.READ_WRITE );

    IDialogPanelCreator<IList<UaTreeNode>, OpcUaNodesSelectorContext> creator = OpcUaNodesSelector::new;
    TsDialog<IList<UaTreeNode>, OpcUaNodesSelectorContext> d = new TsDialog<>( cdi, null, ctx, creator );
    return d.execData();
  }

}
