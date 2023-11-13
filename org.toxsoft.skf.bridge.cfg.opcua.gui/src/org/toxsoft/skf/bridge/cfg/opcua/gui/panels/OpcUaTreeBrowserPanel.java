package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.core.tslib.gw.IGwHardConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.*;

import java.io.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.sdk.core.nodes.*;
import org.eclipse.milo.opcua.stack.core.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.tsnodes.*;
import org.toxsoft.core.tsgui.bricks.tstree.tmm.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.events.change.*;
import org.toxsoft.core.tslib.bricks.geometry.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.km5.sded.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Browser of OPC UA tree.
 *
 * @author max
 * @author dima
 */
public class OpcUaTreeBrowserPanel
    extends TsPanel {

  private static final String                           nodeCmdIdBrowseName = "CmdId";       //$NON-NLS-1$
  private static final String                           nodeCmdArgInt       = "CmdArgInt";   //$NON-NLS-1$
  private static final String                           nodeCmdArgFlt       = "CmdArgFlt";   //$NON-NLS-1$
  private static final String                           nodeCmdFeedback     = "CmdFeedback"; //$NON-NLS-1$
  private StringMap<IList<IDtoCmdInfo>>                 clsId2CmdInfoes     = null;
  private StringMap<StringMap<IList<BitIdx2DtoRtData>>> clsId2RtDataInfoes  = null;
  /**
   * карта id класса - > его BitIdx2DtoEvent
   */
  private StringMap<StringMap<IList<BitIdx2DtoEvent>>>  clsId2EventInfoes   = null;

  private final IOpcUaServerConnCfg opcUaServerConnCfg;
  /**
   * Аргумент команды: значение.
   * <p>
   * Аргумент имеет тип {@link EAtomicType#FLOATING}.
   */
  static String                     CMDARGID_VALUE = "value"; //$NON-NLS-1$

  /**
   * id параметра события: старое значение.
   * <p>
   * Параметр имеет тип {@link EAtomicType#FLOATING}.
   */
  static String EVPID_OLD_VAL = "oldVal"; //$NON-NLS-1$

  /**
   * Параметр события: старое значение.
   * <p>
   * Параметр имеет тип {@link EAtomicType#FLOATING}.
   */
  static DataDef EVPDD_OLD_VAL_FLOAT =
      DataDef.create( EVPID_OLD_VAL, EAtomicType.FLOATING, TSID_NAME, STR_N_EV_PARAM_OLD_VAL, //
          TSID_DESCRIPTION, STR_D_EV_PARAM_OLD_VAL, //
          TSID_IS_NULL_ALLOWED, AV_TRUE );

  /**
   * /** Параметр события: старое значение.
   * <p>
   * Параметр имеет тип {@link EAtomicType#INTEGER}.
   */
  static DataDef EVPDD_OLD_VAL_INT =
      DataDef.create( EVPID_OLD_VAL, EAtomicType.INTEGER, TSID_NAME, STR_N_EV_PARAM_OLD_VAL, //
          TSID_DESCRIPTION, STR_D_EV_PARAM_OLD_VAL, //
          TSID_IS_NULL_ALLOWED, AV_TRUE );

  /**
   * id параметра события: новое значение.
   */

  static String EVPID_NEW_VAL = "newVal"; //$NON-NLS-1$

  /**
   * Параметр события: новое значение.
   * <p>
   * Параметр имеет тип {@link EAtomicType#FLOATING}.
   */
  static DataDef EVPDD_NEW_VAL_FLOAT =
      DataDef.create( EVPID_NEW_VAL, EAtomicType.FLOATING, TSID_NAME, STR_N_EV_PARAM_NEW_VAL, //
          TSID_DESCRIPTION, STR_D_EV_PARAM_NEW_VAL, //
          TSID_IS_NULL_ALLOWED, AV_FALSE, //
          TSID_DEFAULT_VALUE, AV_STR_EMPTY );

  /**
   * Параметр события: новое значение.
   * <p>
   * Параметр имеет тип {@link EAtomicType#INTEGER}.
   */
  static DataDef EVPDD_NEW_VAL_INT =
      DataDef.create( EVPID_NEW_VAL, EAtomicType.INTEGER, TSID_NAME, STR_N_EV_PARAM_NEW_VAL, //
          TSID_DESCRIPTION, STR_D_EV_PARAM_NEW_VAL, //
          TSID_IS_NULL_ALLOWED, AV_FALSE, //
          TSID_DEFAULT_VALUE, AV_STR_EMPTY );

  /**
   * Параметр события: включен.
   * <p>
   * Параметр имеет тип {@link EAtomicType#BOOLEAN}.
   */
  static String EVPID_ON = "on"; //$NON-NLS-1$

  /**
   * Параметр события: on.
   * <p>
   * Параметр имеет тип {@link EAtomicType#BOOLEAN}.
   */
  public static DataDef EVPDD_ON = DataDef.create( EVPID_ON, EAtomicType.BOOLEAN, TSID_NAME, STR_N_EV_PARAM_ON, //
      TSID_DESCRIPTION, STR_D_EV_PARAM_ON, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, AV_FALSE );

  private final ISkConnection conn;
  static private String       RTD_PREFIX = "rtd"; //$NON-NLS-1$
  static private String       EVT_PREFIX = "evt"; //$NON-NLS-1$

  private IM5CollectionPanel<UaTreeNode> opcUaNodePanel;

  /**
   * Панель подробного просмотра
   */
  private UaVariableNodeListPanel inspectorPanel;

  /**
   * Собсно сама панель
   */
  private MultiPaneComponentModown<UaTreeNode> componentModown;
  /**
   * Собственно клиент OPC UA сервера
   */
  private OpcUaClient                          client;
  private UaTreeNode                           selectedNode     = null;
  private String                               ODS_EXT          = "*.ods";                //$NON-NLS-1$
  private String                               DEFAULT_PATH_STR = TsLibUtils.EMPTY_STRING;

  /**
   * Items provider for ISkObject created on OPC UA node.
   *
   * @author dima
   */
  static class OpcUANode2SkObjectItemsProvider
      implements IM5ItemsProvider<IDtoObject> {

    private IListEdit<IDtoObject> items = new ElemArrayList<>();
    private final ISkCoreApi      coreApi;
    // private Map<String, UaTreeNode> id2Node = new HashMap<>();
    IListEdit<UaNode2Skid> node2SkidList = new ElemArrayList<>();

    // Создаем IDpuObject и инициализируем его значениями из узла
    private IDtoObject makeObjDto( String aClassId, UaTreeNode aObjNode ) {
      String id = aObjNode.getBrowseName();
      Skid skid = new Skid( aClassId, id );
      DtoObject dtoObj = DtoObject.createDtoObject( skid, coreApi );
      dtoObj.attrs().setValue( FID_NAME, AvUtils.avStr( aObjNode.getDisplayName() ) );
      dtoObj.attrs().setValue( FID_DESCRIPTION, AvUtils.avStr( aObjNode.getDescription() ) );
      return dtoObj;
    }

    OpcUANode2SkObjectItemsProvider( IList<UaTreeNode> aSelectedNodes, ISkCoreApi aSkCoreApi,
        ISkClassInfo aSelectedClassInfo ) {
      coreApi = aSkCoreApi;
      for( UaTreeNode objNode : aSelectedNodes ) {
        IDtoObject dtoObj = makeObjDto( aSelectedClassInfo.id(), objNode );
        items.add( dtoObj );
        // запоминаем привязку узла к Skid
        // id2Node.put( dtoObj.id(), objNode );
        Skid skid = new Skid( aSelectedClassInfo.id(), dtoObj.id() );
        node2SkidList.add( new UaNode2Skid( objNode.getNodeId(), objNode.getDisplayName(), skid ) );
      }
    }

    @Override
    public IGenericChangeEventer genericChangeEventer() {
      return NoneGenericChangeEventer.INSTANCE;
    }

    @Override
    public IList<IDtoObject> listItems() {
      return items;
    }

    // public UaTreeNode nodeById( String aObjId ) {
    // return id2Node.get( aObjId );
    // }

  }

  /**
   * Constructor
   *
   * @param aParent Composite - parent component.
   * @param aContext ITsGuiContext - context.
   * @param aOpcUaServerConnCfg IOpcUaServerConnCfg - opc ua server connection configuration.
   */
  public OpcUaTreeBrowserPanel( Composite aParent, ITsGuiContext aContext, IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    super( aParent, aContext );
    opcUaServerConnCfg = aOpcUaServerConnCfg;
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<UaTreeNode> model = m5.getModel( OpcUaNodeModel.MODEL_ID, UaTreeNode.class );

    try {
      client = OpcUaUtils.createClient( aOpcUaServerConnCfg );
      client.connect().get();
    }
    catch( Exception ex ) {
      LoggerUtils.errorLogger().error( ex );
      TsDialogUtils.error( getShell(), "Can't connect to OPC UA server: %s\n Error message: %s",
          aOpcUaServerConnCfg.host(), ex.getMessage() );
      return;
    }

    IM5LifecycleManager<UaTreeNode> lm =
        new OpcUaNodeM5LifecycleManager( model, client, aContext, aOpcUaServerConnCfg );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_FALSE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // обнуляем действие по умолчанию на dbl click
    IMultiPaneComponentConstants.OPDEF_DBLCLICK_ACTION_ID.setValue( ctx.params(), AvUtils.AV_STR_EMPTY );

    componentModown = new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm ) {

      // добавляем tool bar
      @Override
      protected ITsToolbar doCreateToolbar( @SuppressWarnings( "hiding" ) ITsGuiContext aContext, String aName,
          EIconSize aIconSize, IListEdit<ITsActionDef> aActs ) {
        aActs.add( ITsStdActionDefs.ACDEF_SEPARATOR );
        aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_CREATE_CLASS_OPC_UA_ITEM );
        aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_CREATE_OBJS_OPC_UA_ITEM );
        aActs.add( ITsStdActionDefs.ACDEF_SEPARATOR );
        aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_SHOW_OPC_UA_NODE_2_GWID );
        aActs.add( ITsStdActionDefs.ACDEF_SEPARATOR );
        aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_LOAD_CMD_DESCR );
        aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_LOAD_BIT_RTDATA_DESCR );

        ITsToolbar toolBar = super.doCreateToolbar( aContext, aName, aIconSize, aActs );

        toolBar.addListener( aActionId -> {
          if( aActionId == CREATE_CINFO_FROM_OPC_UA_ACT_ID ) {
            // first of all ensure all needed files are loaded
            ensureCmdDescription();
            ensureBitMaskDescription();
            createClassFromNodes( aContext );
          }

          if( aActionId == CREATE_OBJS_FROM_OPC_UA_ACT_ID ) {
            // first of all ensure file bitMask loaded
            ensureBitMaskDescription();
            createObjsFromNodes( aContext );
          }

          if( aActionId == SHOW_OPC_UA_NODE_2_GWID_ACT_ID ) {
            // for debug
            // IList<UaNode2EventGwid> evList = OpcUaUtils.loadNodes2EvtGwids( aContext );
            // for( UaNode2EventGwid node2Evt : evList ) {
            // LoggerUtils.defaultLogger()
            // .debug( node2Evt.nodeDescr() + "->" + node2Evt.gwid() + ":" + node2Evt.paramIds() );
            // }
            // EAtomicType ret = OpcUaUtils.getValueTypeOfNode( aContext, aOpcUaServerConnCfg,
            // selectedNode.getUaNode().getNodeId().toParseableString() );
            // LoggerUtils.defaultLogger().debug( ret.toString() );
            checkNode2Gwid( aContext );
          }

          if( aActionId == ACT_ID_LOAD_BIT_RTDATA ) {
            loadBitMaskDescrFile();
          }
          if( aActionId == LOAD_CMD_DESCR_ACT_ID ) {
            loadCmdDescrFile();
          }

        } );
        toolBar.setIconSize( EIconSize.IS_24X24 );
        return toolBar;
      }

    };
    UaNodesTreeMaker treeMaker = new UaNodesTreeMaker();

    componentModown.tree().setTreeMaker( treeMaker );

    componentModown.treeModeManager().addTreeMode( new TreeModeInfo<>( TMID_GROUP_BY_OPC_UA_ORIGIN,
        STR_N_BY_OPC_NODES_STRUCT, STR_D_BY_OPC_NODES_STRUCT, null, treeMaker ) );
    componentModown.treeModeManager().setCurrentMode( TMID_GROUP_BY_OPC_UA_ORIGIN );

    componentModown.addTsDoubleClickListener( ( aSource, aSelectedItem ) -> {

      if( aSelectedItem != null ) {
        UaNode selNode = aSelectedItem.getUaNode();
        // реагируем только на тип UaVariableNode
        if( selNode instanceof VariableNode ) {
          UaVariableNode variableNode = (UaVariableNode)selNode;
          inspectorPanel.addNode( variableNode );
        }
      }
    } );

    opcUaNodePanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );
    opcUaNodePanel.createControl( this );
    // пока не выбран ни один узел, отключаем
    componentModown.toolbar().getAction( CREATE_CINFO_FROM_OPC_UA_ACT_ID ).setEnabled( false );
    componentModown.toolbar().getAction( CREATE_OBJS_FROM_OPC_UA_ACT_ID ).setEnabled( false );
    componentModown.toolbar().getAction( SHOW_OPC_UA_NODE_2_GWID_ACT_ID ).setEnabled( false );
    componentModown.addTsSelectionListener( ( aSource, aSelectedItem ) -> {
      // просто активируем кнопки создания/обновления классов/объектов
      boolean enableCreateClassBttn = isCtreateClassEnable( aSelectedItem );
      boolean enableCreateObjBttn = isCtreateObjEnable( aSelectedItem );
      boolean enableCheckLinkBttn = isCheckLinkEnable( aSelectedItem );
      componentModown.toolbar().getAction( CREATE_CINFO_FROM_OPC_UA_ACT_ID ).setEnabled( enableCreateClassBttn );
      componentModown.toolbar().getAction( CREATE_OBJS_FROM_OPC_UA_ACT_ID ).setEnabled( enableCreateObjBttn );
      componentModown.toolbar().getAction( SHOW_OPC_UA_NODE_2_GWID_ACT_ID ).setEnabled( enableCheckLinkBttn );

      if( aSelectedItem != null ) {
        selectedNode = aSelectedItem;
      }

    } );

  }

  protected void ensureCmdDescription() {
    if( clsId2CmdInfoes == null ) {
      loadCmdDescrFile();
    }
  }

  protected void ensureBitMaskDescription() {
    if( clsId2RtDataInfoes == null || clsId2EventInfoes == null ) {
      loadBitMaskDescrFile();
    }
  }

  /**
   * Load ask user and load file with mask descriptions
   */
  private void loadBitMaskDescrFile() {
    String bitRtdataFileDescr = getDescrFile( SELECT_FILE_4_IMPORT_BIT_RTDATA );
    if( bitRtdataFileDescr != null ) {
      File file = new File( bitRtdataFileDescr );
      try {
        Ods2DtoRtDataInfoParser.parse( file );
        clsId2RtDataInfoes = Ods2DtoRtDataInfoParser.getRtdataInfoesMap();
        clsId2EventInfoes = Ods2DtoRtDataInfoParser.getEventInfoesMap();
        TsDialogUtils.info( getShell(), STR_BITMASK_FILE_LOADED, bitRtdataFileDescr );
      }
      catch( IOException ex ) {
        LoggerUtils.errorLogger().error( ex );
      }
    }
  }

  private static boolean isCheckLinkEnable( UaTreeNode aSelectedItem ) {
    boolean enable = false;
    // узел у которого тип Variable
    if( aSelectedItem != null ) {
      if( aSelectedItem.getNodeClass().equals( NodeClass.Variable ) ) {
        enable = true;
      }
    }
    return enable;
  }

  private String getDescrFile( String aTitle ) {
    FileDialog fd = new FileDialog( getShell(), SWT.OPEN );
    fd.setText( aTitle );// SELECT_FILE_4_IMPORT_CMD
    fd.setFilterPath( DEFAULT_PATH_STR );
    String[] filterExt = { ODS_EXT };
    fd.setFilterExtensions( filterExt );
    String selected = fd.open();
    return selected;
  }

  private static boolean isCtreateObjEnable( UaTreeNode aSelectedItem ) {
    boolean enable = false;
    // узел у которого есть прямые дети с типом Object
    if( aSelectedItem != null ) {
      IList<UaTreeNode> children = aSelectedItem.getChildren();
      for( UaTreeNode child : children ) {
        if( child.getNodeClass().equals( NodeClass.Object ) ) {
          enable = true;
          break;
        }
      }
    }
    return enable;
  }

  private static boolean isCtreateClassEnable( UaTreeNode aSelectedItem ) {
    boolean enable = false;
    // узел у которого есть прямые дети с типом Variable
    if( aSelectedItem != null ) {
      IList<UaTreeNode> children = aSelectedItem.getChildren();
      for( UaTreeNode child : children ) {
        if( child.getNodeClass().equals( NodeClass.Variable ) ) {
          enable = true;
          break;
        }
      }
    }
    return enable;
  }

  @SuppressWarnings( "nls" )
  protected void checkNode2Gwid( ITsGuiContext aContext ) {
    NodeId nodeId = selectedNode.getUaNode().getNodeId();
    Gwid gwid = OpcUaUtils.uaNode2rtdGwid( aContext, nodeId );
    String checkResult = String.format( "%s [%s] -> %s", selectedNode.getUaNode().getBrowseName().getName(),
        nodeId.toParseableString(), gwid == null ? TsLibUtils.EMPTY_STRING : gwid.asString() );
    TsDialogUtils.info( getShell(), "Check link result:\n %s", checkResult );
  }

  private static class UaNodesTreeMaker
      implements ITsTreeMaker<UaTreeNode> {

    private final ITsNodeKind<UaTreeNode> kind =
        new TsNodeKind<>( "UaTreeNode", UaTreeNode.class, true, IOpcUaServerConnCfgConstants.ICONID_APP_ICON ); //$NON-NLS-1$

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

    @Override
    public boolean isItemNode( ITsNode aNode ) {
      return true;
    }

  }

  /**
   * Установить панель подробного просмотра
   *
   * @param aNodesInspector панель инспектора
   */
  public void setInspector( UaVariableNodeListPanel aNodesInspector ) {
    inspectorPanel = aNodesInspector;
  }

  /**
   * Получить клиента для работы с OPC UA сервером
   *
   * @return {@link OpcUaClient} клиент для работы с OPC UA сервером
   */
  public OpcUaClient getOpcUaClient() {
    return client;
  }

  private void createClassFromNodes( ITsGuiContext aContext ) {
    // создать класс из информации об UaNode
    IList<UaTreeNode> selNodes = OpcUaNodesSelector.selectUaNodes4Class( aContext, selectedNode.getUaNode().getNodeId(),
        client, opcUaServerConnCfg );
    if( selNodes != null ) {
      // создаем описание класса из списка выбранных узлов
      // отредактируем список узлов чтобы в нем была вся необходимая информация для описания класса
      IList<UaTreeNode> nodes4DtoClass = nodes4DtoClass( selectedNode, selNodes );
      IListEdit<UaNode2Gwid> node2ClassGwidList = new ElemArrayList<>();

      IDtoClassInfo dtoClassInfo = makeDtoClassInfo( nodes4DtoClass, node2ClassGwidList );
      IM5Model<IDtoClassInfo> modelDto = conn.scope().get( IM5Domain.class )
          .getModel( IKM5SdedConstants.MID_SDED_DTO_CLASS_INFO, IDtoClassInfo.class );
      TsDialogInfo cdi = new TsDialogInfo( tsContext(), null, DLG_C_NEW_CLASS, DLG_T_NEW_CLASS, 0 );
      // установим нормальный размер диалога
      cdi.setMinSize( new TsPoint( -30, -60 ) );
      // проверяем наличие класса
      ISkClassInfo existClsInfo = conn.coreApi().sysdescr().findClassInfo( dtoClassInfo.id() );
      // если он уже существует, то обновляем все существующие поля
      if( existClsInfo != null ) {
        IDtoClassInfo currDtoClassInfo = DtoClassInfo.createFromSk( existClsInfo, false );
        dtoClassInfo = updateDtoClassInfo( dtoClassInfo, currDtoClassInfo );
        // просим редактировать описание класса и нажать Ok
        dtoClassInfo =
            M5GuiUtils.askEdit( tsContext(), modelDto, dtoClassInfo, cdi, modelDto.getLifecycleManager( conn ) );
        if( dtoClassInfo != null ) {
          // чистим список привязок ClassGwid -> NodeId
          node2ClassGwidList = filterNode2ClassGwidList( dtoClassInfo, node2ClassGwidList );
          // заливаем в хранилище
          OpcUaUtils.updateNodes2GwidsInStore( aContext, node2ClassGwidList, OpcUaUtils.SECTID_OPC_UA_NODES_2_CLS_GWIDS,
              UaNode2Gwid.KEEPER );

          ISkidList skids2Remove = conn.coreApi().objService().listSkids( dtoClassInfo.id(), false );
          // перепривязываем объекты
          ISkClassInfo updatedClassInfo = conn.coreApi().sysdescr().getClassInfo( dtoClassInfo.id() );
          IListEdit<IDtoObject> listObj2Update = new ElemArrayList<>();
          for( Skid skid : skids2Remove ) {
            ISkObject obj2Update = conn.coreApi().objService().find( skid );
            listObj2Update.add( DtoObject.createFromSk( obj2Update, conn.coreApi() ) );
          }
          OpcUaUtils.clearCache();
          generateNode2GwidLinks( aContext, updatedClassInfo, listObj2Update );
          TsDialogUtils.info( getShell(), STR_SUCCESS_CLASS_UPDATED, dtoClassInfo.id() );
        }
      }
      else {
        // создаем пучок из модели
        IM5Bunch<IDtoClassInfo> bunchOfFieldVals = modelDto.valuesOf( dtoClassInfo );
        // просим прользователя верифицировать/редактировать описание класса и нажать Ok
        dtoClassInfo =
            M5GuiUtils.askCreate( tsContext(), modelDto, bunchOfFieldVals, cdi, modelDto.getLifecycleManager( conn ) );
        if( dtoClassInfo != null ) {
          // чистим список привязок ClassGwid -> NodeId
          node2ClassGwidList = filterNode2ClassGwidList( dtoClassInfo, node2ClassGwidList );
          // заливаем в хранилище
          OpcUaUtils.updateNodes2GwidsInStore( aContext, node2ClassGwidList, OpcUaUtils.SECTID_OPC_UA_NODES_2_CLS_GWIDS,
              UaNode2Gwid.KEEPER );
          OpcUaUtils.clearCache();
          // подтверждаем успешное создание класса
          TsDialogUtils.info( getShell(), STR_SUCCESS_CLASS_CREATED, dtoClassInfo.id() );
        }
      }
    }
  }

  /**
   * Фильтрует ранее подготовленный список привязок NodeId->ClassGwid удаляее элементы которые пользователь выбросил
   *
   * @param aDtoClassInfo - описание класса отредактирвоанное пользователем
   * @param aNode2ClassGwidList - ранее подготовленный список привязок NodeId->ClassGwid
   * @return отфильрованный список
   */
  private static IListEdit<UaNode2Gwid> filterNode2ClassGwidList( IDtoClassInfo aDtoClassInfo,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    IListEdit<UaNode2Gwid> retVal = new ElemArrayList<>();
    // выкидываем из списка события и rtData которые пользователь удалил
    for( IDtoRtdataInfo rtDataInfo : aDtoClassInfo.rtdataInfos() ) {
      Gwid gwid = Gwid.createRtdata( aDtoClassInfo.id(), rtDataInfo.id() );
      for( UaNode2Gwid uaNode2Gwid : aNode2ClassGwidList ) {
        if( gwid.equals( uaNode2Gwid.gwid() ) ) {
          retVal.add( uaNode2Gwid );
        }
      }
    }
    for( IDtoEventInfo evtInfo : aDtoClassInfo.eventInfos() ) {
      Gwid gwid = Gwid.createEvent( aDtoClassInfo.id(), evtInfo.id() );
      for( UaNode2Gwid uaNode2Gwid : aNode2ClassGwidList ) {
        if( gwid.equals( uaNode2Gwid.gwid() ) ) {
          retVal.add( uaNode2Gwid );
        }
      }
    }
    return retVal;
  }

  private static DtoClassInfo updateDtoClassInfo( IDtoClassInfo aDtoClassInfo, IDtoClassInfo aCurrDtoClassInfo ) {
    DtoClassInfo dtoClass;
    if( aDtoClassInfo.id().equals( IGwHardConstants.GW_ROOT_CLASS_ID ) ) {
      dtoClass = new DtoClassInfo( aDtoClassInfo.params() );
    }
    else {
      dtoClass = new DtoClassInfo( aDtoClassInfo.id(), aDtoClassInfo.parentId(), aDtoClassInfo.params() );
    }
    // копируем свойства исходного
    dtoClass.attrInfos().setAll( aDtoClassInfo.attrInfos() );
    dtoClass.rtdataInfos().setAll( aDtoClassInfo.rtdataInfos() );
    dtoClass.cmdInfos().setAll( aDtoClassInfo.cmdInfos() );
    dtoClass.eventInfos().setAll( aDtoClassInfo.eventInfos() );

    // обновляем атрибуты
    for( IDtoAttrInfo attrInfo : aDtoClassInfo.attrInfos() ) {
      if( aCurrDtoClassInfo.attrInfos().hasKey( attrInfo.id() ) ) {
        // если такой атрибут есть в существующем классе, то заменяем его в результирующем
        IDtoAttrInfo removeAttr = aDtoClassInfo.attrInfos().getByKey( attrInfo.id() );
        dtoClass.attrInfos().remove( removeAttr );
        dtoClass.attrInfos().add( aCurrDtoClassInfo.attrInfos().getByKey( attrInfo.id() ) );
      }
    }
    // обновляем rtData
    for( IDtoRtdataInfo rtDataInfo : aDtoClassInfo.rtdataInfos() ) {
      if( aCurrDtoClassInfo.rtdataInfos().hasKey( rtDataInfo.id() ) ) {
        // если такой rtData есть в существующем классе, то заменяем его в результирующем
        IDtoRtdataInfo removeRtData = aDtoClassInfo.rtdataInfos().getByKey( rtDataInfo.id() );
        dtoClass.rtdataInfos().remove( removeRtData );
        dtoClass.rtdataInfos().add( aCurrDtoClassInfo.rtdataInfos().getByKey( rtDataInfo.id() ) );
      }
    }
    // обновляем cmds
    for( IDtoCmdInfo cmdInfo : aDtoClassInfo.cmdInfos() ) {
      if( aCurrDtoClassInfo.cmdInfos().hasKey( cmdInfo.id() ) ) {
        // если такой cmd есть в существующем классе, то заменяем его в результирующем
        IDtoCmdInfo removeCmd = aDtoClassInfo.cmdInfos().getByKey( cmdInfo.id() );
        dtoClass.cmdInfos().remove( removeCmd );
        dtoClass.cmdInfos().add( aCurrDtoClassInfo.cmdInfos().getByKey( cmdInfo.id() ) );
      }
    }
    // обновляем events
    for( IDtoEventInfo evtInfo : aDtoClassInfo.eventInfos() ) {
      if( aCurrDtoClassInfo.eventInfos().hasKey( evtInfo.id() ) ) {
        // если такой event есть в существующем классе, то заменяем его в результирующем
        IDtoEventInfo removeEvent = aDtoClassInfo.eventInfos().getByKey( evtInfo.id() );
        dtoClass.eventInfos().remove( removeEvent );
        dtoClass.eventInfos().add( aCurrDtoClassInfo.eventInfos().getByKey( evtInfo.id() ) );
      }
    }
    return dtoClass;
  }

  /**
   * На основе переданного списка узлов создаем новый список который содержит в себе всю необходимую для создания класса
   * информацию. А именно узел описания класса и список узлов с описаниями RtData. При этом удаляем узел описывающий
   * объект.
   *
   * @param aClassNode узел описывающий класс
   * @param aSelNodes выбранные узлы
   * @return список узлов один из которых описание класса, остальные описание RtData этого класса
   */
  private static IList<UaTreeNode> nodes4DtoClass( UaTreeNode aClassNode, IList<UaTreeNode> aSelNodes ) {
    IListEdit<UaTreeNode> retVal = new ElemArrayList<>();
    // первый элемент - описание класса
    retVal.add( aClassNode );
    for( UaTreeNode node : aSelNodes ) {
      if( node.getNodeClass().equals( NodeClass.Variable ) ) {
        retVal.add( node );
      }
    }
    return retVal;
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //
  private IDtoClassInfo makeDtoClassInfo( IList<UaTreeNode> aNodes, IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // узел типа Object, его данные используются для создания описания класса
    UaTreeNode classNode = objNode( aNodes );

    String id = classNode.getBrowseName();
    String name = classNode.getDisplayName();
    String description = classNode.getDescription().trim().length() > 0 ? classNode.getDescription() : name;

    IOptionSetEdit params = new OptionSet();

    params.setStr( FID_NAME, name );
    params.setStr( FID_DESCRIPTION, description );
    DtoClassInfo cinf = new DtoClassInfo( id, IGwHardConstants.GW_ROOT_CLASS_ID, params );
    for( UaTreeNode node : aNodes ) {
      if( node.getNodeClass().equals( NodeClass.Variable ) ) {
        try {
          UaVariableNode varNode = client.getAddressSpace().getVariableNode( NodeId.parse( node.getNodeId() ) );
          readDataInfo( cinf, varNode, aNode2ClassGwidList );
          readEventInfo( cinf, varNode, aNode2ClassGwidList );
        }
        catch( UaRuntimeException | UaException ex ) {
          LoggerUtils.errorLogger().error( ex );
          TsDialogUtils.error( getShell(), ERR_MSG_CACHE_OUTDATED );
        }
      }
    }
    // добавим атрибут который сигнализирует что класс из OPC UA node
    markClassOPC_UA( cinf );
    // добавим команды, если они описаны
    if( clsId2CmdInfoes != null ) {
      if( clsId2CmdInfoes.hasKey( id ) ) {
        IList<IDtoCmdInfo> cmdInfioes = clsId2CmdInfoes.getByKey( id );
        cinf.cmdInfos().addAll( cmdInfioes );
      }
    }
    // добавим битовые rtData, если они описаны
    if( clsId2RtDataInfoes != null ) {
      if( clsId2RtDataInfoes.hasKey( id ) ) {
        StringMap<IList<BitIdx2DtoRtData>> rtDataInfioesMap = clsId2RtDataInfoes.getByKey( id );
        for( String key : rtDataInfioesMap.keys() ) {
          IList<BitIdx2DtoRtData> list = rtDataInfioesMap.getByKey( key );
          for( BitIdx2DtoRtData dto : list ) {
            cinf.rtdataInfos().add( dto.dtoRtdataInfo() );
          }
        }
      }
    }
    // добавим битовые события, если они описаны
    if( clsId2EventInfoes != null ) {
      if( clsId2EventInfoes.hasKey( id ) ) {
        StringMap<IList<BitIdx2DtoEvent>> evtInfioesMap = clsId2EventInfoes.getByKey( id );
        for( String key : evtInfioesMap.keys() ) {
          IList<BitIdx2DtoEvent> list = evtInfioesMap.getByKey( key );
          for( BitIdx2DtoEvent dte : list ) {
            cinf.eventInfos().add( dte.dtoEventInfo() );
          }
        }
      }
    }

    return cinf;
  }

  private static UaTreeNode objNode( IList<UaTreeNode> aNodes ) {
    UaTreeNode retVal = null;
    // ищем элемент типа Object
    for( UaTreeNode node : aNodes ) {
      if( node.getNodeClass().equals( NodeClass.Object ) ) {
        retVal = node;
        break;
      }
    }
    return retVal;
  }

  /**
   * Читает описание данного и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aDtoClass текущее описание класса
   * @param aVariableNode описание узла типа переменная
   * @param aNode2ClassGwidList список для хранения привязки node -> Class Gwid
   */
  private static void readDataInfo( DtoClassInfo aDtoClass, UaVariableNode aVariableNode,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // id данного
    String dataId = aVariableNode.getBrowseName().getName();
    // соблюдаем соглашения о наименовании
    if( !dataId.startsWith( RTD_PREFIX ) ) {
      dataId = RTD_PREFIX + dataId;
    }
    if( isIgnore4RtData( dataId ) ) {
      return;
    }
    // название
    String name = aVariableNode.getDisplayName().getText();
    // описание
    String descr = aVariableNode.getDescription().getText();
    // описание
    if( (descr == null) || descr.isBlank() ) {
      descr = name;
    }

    // тип данного
    Class<?> clazz = OpcUaUtils.getNodeDataTypeClass( aVariableNode );
    EAtomicType type = OpcUaUtils.getAtomicType( clazz );
    // sync
    boolean sync = false; // по умолчанию асинхронное
    int deltaT = sync ? 1000 : 1;

    IDtoRtdataInfo dataInfo = DtoRtdataInfo.create1( dataId, new DataType( type ), //
        true, // isCurr
        true, // isHist
        sync, // isSync
        deltaT, // deltaT
        OptionSetUtils.createOpSet( //
            TSID_NAME, name, //
            TSID_DESCRIPTION, descr //
        ) );

    aDtoClass.rtdataInfos().add( dataInfo );
    // TODO boilerplate code сохраним привязку для использования в автоматическом связывании
    NodeId nodeId = aVariableNode.getNodeId();
    Gwid classGwid = Gwid.createRtdata( aDtoClass.id(), dataId );
    UaNode2Gwid uaNode2Gwid = new UaNode2Gwid( nodeId.toParseableString(), descr, classGwid );
    aNode2ClassGwidList.add( uaNode2Gwid );
  }

  private static boolean isIgnore4RtData( String aDataId ) {
    // игнорируем узлы для работы с командами
    if( aDataId.indexOf( nodeCmdIdBrowseName ) >= 0 ) {
      return true;
    }
    if( aDataId.indexOf( nodeCmdArgFlt ) >= 0 ) {
      return true;
    }
    if( aDataId.indexOf( nodeCmdArgInt ) >= 0 ) {
      return true;
    }
    return false;
  }

  private static boolean isIgnore4Event( String aEvId ) {
    // игнорируем узлы для работы с командами
    if( aEvId.indexOf( nodeCmdIdBrowseName ) >= 0 ) {
      return true;
    }
    if( aEvId.indexOf( nodeCmdArgFlt ) >= 0 ) {
      return true;
    }
    if( aEvId.indexOf( nodeCmdArgInt ) >= 0 ) {
      return true;
    }
    if( aEvId.indexOf( nodeCmdFeedback ) >= 0 ) {
      return true;
    }
    return false;
  }

  /**
   * Читает описание события и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aDtoClass текущее описание класса
   * @param aVariableNode описание узла типа переменная
   * @param aNode2ClassGwidList список привязок Node -> Class Gwid
   */
  private static void readEventInfo( DtoClassInfo aDtoClass, UaVariableNode aVariableNode,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // id события
    String evtId = aVariableNode.getBrowseName().getName();
    // соблюдаем соглашения о наименовании
    if( !evtId.startsWith( EVT_PREFIX ) ) {
      evtId = EVT_PREFIX + evtId;
    }
    if( isIgnore4Event( evtId ) ) {
      return;
    }
    // название
    String name = aVariableNode.getDisplayName().getText();
    // описание
    String descr = aVariableNode.getDescription().getText();
    // описание
    if( descr == null ) {
      descr = name;
    }

    // тип данного
    Class<?> clazz = OpcUaUtils.getNodeDataTypeClass( aVariableNode );
    EAtomicType type = OpcUaUtils.getAtomicType( clazz );

    StridablesList<IDataDef> evParams;
    evParams = switch( type ) {
      case INTEGER -> new StridablesList<>( EVPDD_OLD_VAL_INT, EVPDD_NEW_VAL_INT );
      case BOOLEAN -> new StridablesList<>( EVPDD_ON );
      case FLOATING -> new StridablesList<>( EVPDD_OLD_VAL_FLOAT, EVPDD_NEW_VAL_FLOAT );
      case NONE -> throw invalidParamTypeExcpt( type );
      case STRING -> throw invalidParamTypeExcpt( type );
      case TIMESTAMP -> throw invalidParamTypeExcpt( type );
      case VALOBJ -> throw invalidParamTypeExcpt( type );
    };

    IDtoEventInfo evtInfo = DtoEventInfo.create1( evtId, true, //
        evParams, //
        OptionSetUtils.createOpSet( //
            IAvMetaConstants.TSID_NAME, name, //
            IAvMetaConstants.TSID_DESCRIPTION, descr //
        ) ); //

    aDtoClass.eventInfos().add( evtInfo );
    // TODO boilerplate code сохраним привязку для использования в автоматическом связывании
    NodeId nodeId = aVariableNode.getNodeId();
    Gwid classGwid = Gwid.createEvent( aDtoClass.id(), evtId );
    UaNode2Gwid uaNode2Gwid = new UaNode2Gwid( nodeId.toParseableString(), descr, classGwid );
    aNode2ClassGwidList.add( uaNode2Gwid );
  }

  private static TsIllegalArgumentRtException invalidParamTypeExcpt( EAtomicType type ) {
    return new TsIllegalArgumentRtException( "Can't create event parameters with type %s", //$NON-NLS-1$
        type.id() );
  }

  private static void markClassOPC_UA( DtoClassInfo aCinf ) {
    IDataDef ddType = DDEF_BOOLEAN;
    // название
    String name = STR_N_OPC_UA_MARKER;
    // описание
    String descr = STR_D_OPC_UA_MARKER;

    IDtoAttrInfo atrInfo = DtoAttrInfo.create2( IOpcUaServerConnCfgConstants.OPC_AU_CLASS_MARKER, ddType, //
        TSID_NAME, name, //
        TSID_DESCRIPTION, descr );

    aCinf.attrInfos().add( atrInfo );
  }

  private void createObjsFromNodes( ITsGuiContext aContext ) {
    // создать объекты по списку UaNode
    IList<UaTreeNode> selNodes = OpcUaNodesSelector.selectUaNodes4Objects( aContext,
        selectedNode.getUaNode().getNodeId(), client, opcUaServerConnCfg );
    if( selNodes != null ) {
      // получаем М5 модель IDtoObject
      IM5Model<IDtoObject> modelSk =
          conn.scope().get( IM5Domain.class ).getModel( DtoObjectM5Model.MODEL_ID, IDtoObject.class );

      // выбор класса
      ISkClassInfo baseClassInfo = conn.coreApi().sysdescr().findClassInfo( GW_ROOT_CLASS_ID );
      ISkClassInfo selectedClassInfo = PanelClassInfoSelector.selectChildClass( baseClassInfo, tsContext() );
      if( selectedClassInfo != null ) {

        // подсунем свой item provider
        // IM5ItemsProvider<ISkObject> ip = lmSk.itemsProvider();
        OpcUANode2SkObjectItemsProvider itemProvider =
            new OpcUANode2SkObjectItemsProvider( selNodes, conn.coreApi(), selectedClassInfo );

        IListEdit<IDtoObject> llObjs = new ElemArrayList<>();
        for( IDtoObject dtoObj : itemProvider.listItems() ) {
          llObjs.add( dtoObj );
        }

        IM5LifecycleManager<IDtoObject> localLM = localLifeCycleManager( modelSk, llObjs, itemProvider );

        // ITsGuiContext ctxSk = new TsGuiContext( tsContext() );

        IM5CollectionPanel<IDtoObject> skObjsPanel =
            modelSk.panelCreator().createCollEditPanel( aContext, localLM.itemsProvider(), localLM );
        // show dialog
        TsDialogInfo di = new TsDialogInfo( aContext, getShell(), DLG_C_NEW_OBJS, DLG_T_NEW_OBJS, 0 );
        di.setMinSizeShellRelative( 10, 50 );
        if( M5GuiUtils.showCollPanel( di, skObjsPanel ) != null ) {
          // сохраним привязки node -> Skid
          OpcUaUtils.updateNodes2SkidsInStore( aContext, itemProvider.node2SkidList,
              OpcUaUtils.SECTID_OPC_UA_NODES_2_SKIDS );
          String userMsg = createSelObjs( localLM );
          // пробуем автоматическую привязку NodeId -> Gwid
          generateNode2GwidLinks( aContext, selectedClassInfo, localLM.itemsProvider().listItems() );
          // подтверждаем успешное создание объектов
          TsDialogUtils.info( getShell(), STR_SUCCESS_OBJS_UPDATED, userMsg );
        }
      }
    }
  }

  private void generateNode2GwidLinks( ITsGuiContext aContext, ISkClassInfo aClassInfo, IList<IDtoObject> aObjList ) {
    IListEdit<UaNode2Gwid> node2RtdGwidList = new ElemArrayList<>();
    IListEdit<UaNode2EventGwid> node2EvtGwidList = new ElemArrayList<>();
    IListEdit<CmdGwid2UaNodes> cmdGwid2UaNodesList = new ElemArrayList<>();
    // в это месте у нас 100% уже загружено дерево узлов OPC UA
    IList<UaTreeNode> treeNodes = ((OpcUaNodeM5LifecycleManager)componentModown.lifecycleManager()).getCached();
    // идем по списку объектов
    for( IDtoObject obj : aObjList ) {
      // находим родительский UaNode
      // UaTreeNode parentNode = aItemProvider.nodeById( obj.id() );
      NodeId parentNodeId = OpcUaUtils.nodeBySkid( aContext, new Skid( aClassInfo.id(), obj.id() ) );
      UaTreeNode parentNode = findParentNode( treeNodes, parentNodeId );
      // привязываем команды
      for( IDtoCmdInfo cmdInfo : aClassInfo.cmds().list() ) {
        CmdGwid2UaNodes cmdGwid2UaNodes = findCmdNodes( obj, cmdInfo, parentNode );
        cmdGwid2UaNodesList.add( cmdGwid2UaNodes );
      }
      // идем по списку его rtdProperties
      for( IDtoRtdataInfo rtdInfo : aClassInfo.rtdata().list() ) {
        // находим свой UaNode
        // сначала ищем в данных битовой маски
        UaTreeNode uaNode = tryBitMaskRtData( aClassInfo, parentNode, rtdInfo );
        if( uaNode == null ) {
          // old version
          // uaNode = findVarNodeByPropName( rtdInfo, parentNode.getChildren() );
          uaNode = findVarNodeByClassGwid( aContext, Gwid.createRtdata( aClassInfo.id(), rtdInfo.id() ),
              parentNode.getChildren() );
        }
        Gwid gwid = Gwid.createRtdata( obj.classId(), obj.id(), rtdInfo.id() );
        if( uaNode != null ) {
          LoggerUtils.defaultLogger().debug( "%s [%s] -> %s", uaNode.getBrowseName(), uaNode.getNodeId(), //$NON-NLS-1$
              gwid.asString() );
          String nodeDescr = parentNode.getBrowseName() + "::" + uaNode.getBrowseName(); //$NON-NLS-1$
          UaNode2Gwid node2Gwid = new UaNode2Gwid( uaNode.getNodeId(), nodeDescr, gwid );
          node2RtdGwidList.add( node2Gwid );
        }
        else {
          LoggerUtils.errorLogger().error( "Can't match rtData: ? -> %s", gwid.asString() ); //$NON-NLS-1$
        }
      }
      // идем по списку его events
      for( IDtoEventInfo evtInfo : aClassInfo.events().list() ) {
        // находим свой UaNode
        // сначала ищем в событиях битовой маске
        UaTreeNode uaNode = tryBitMaskEvent( aClassInfo, parentNode, evtInfo );
        if( uaNode == null ) {
          // old version
          // uaNode = findVarNodeByPropName( evtInfo, parentNode.getChildren() );
          uaNode = findVarNodeByClassGwid( aContext, Gwid.createEvent( aClassInfo.id(), evtInfo.id() ),
              parentNode.getChildren() );
        }
        Gwid gwid = Gwid.createEvent( obj.classId(), obj.id(), evtInfo.id() );
        if( uaNode != null ) {
          LoggerUtils.defaultLogger().debug( "%s [%s] -> %s", uaNode.getBrowseName(), uaNode.getNodeId(), //$NON-NLS-1$
              gwid.asString() );
          String nodeDescr = parentNode.getBrowseName() + "::" + uaNode.getBrowseName(); //$NON-NLS-1$
          IStringListEdit paramIds = new StringArrayList();
          for( String paramId : evtInfo.paramDefs().keys() ) {
            paramIds.add( paramId );
          }
          UaNode2EventGwid node2EventGwid = new UaNode2EventGwid( uaNode.getNodeId(), nodeDescr, gwid, paramIds );
          node2EvtGwidList.add( node2EventGwid );
        }
        else {
          LoggerUtils.errorLogger().error( "Can't match event: ? -> %s", gwid.asString() ); //$NON-NLS-1$
        }
      }
    }
    // заливаем в хранилище
    OpcUaUtils.updateNodes2GwidsInStore( aContext, node2RtdGwidList, OpcUaUtils.SECTID_OPC_UA_NODES_2_RTD_GWIDS,
        UaNode2Gwid.KEEPER );
    OpcUaUtils.updateNodes2GwidsInStore( aContext, node2EvtGwidList, OpcUaUtils.SECTID_OPC_UA_NODES_2_EVT_GWIDS,
        UaNode2EventGwid.KEEPER );
    OpcUaUtils.updateCmdGwid2NodesInStore( aContext, cmdGwid2UaNodesList );
  }

  private UaTreeNode tryBitMaskRtData( ISkClassInfo aClassInfo, UaTreeNode aParentNode, IDtoRtdataInfo aRtDataInfo ) {
    if( clsId2RtDataInfoes.hasKey( aClassInfo.id() ) ) {
      StringMap<IList<BitIdx2DtoRtData>> strid2Bits = clsId2RtDataInfoes.getByKey( aClassInfo.id() );
      for( String strid : strid2Bits.keys() ) {
        IList<BitIdx2DtoRtData> rtDataBits = strid2Bits.getByKey( strid );
        for( BitIdx2DtoRtData bit2rtData : rtDataBits ) {
          if( bit2rtData.dtoRtdataInfo().id().equals( aRtDataInfo.id() ) ) {
            // нашли свое данное, значит у него 100пудово должен быть его узел
            String bitArrayNode = bit2rtData.bitArrayWordStrid().substring( RTD_PREFIX.length() );
            UaTreeNode retVal = findNodeByBrowseName( bitArrayNode, aParentNode.getChildren() );
            TsIllegalStateRtException.checkNull( retVal,
                "Subtree %s, can't find node %s for class: %s, bit mask rtData: %s", bitArrayNode, //$NON-NLS-1$
                aParentNode.getBrowseName(), aClassInfo.id(), aRtDataInfo.id() );
            return retVal;
          }
        }
      }
    }
    // событие не из битовой маски, возвращаем null
    return null;
  }

  private UaTreeNode tryBitMaskEvent( ISkClassInfo aClassInfo, UaTreeNode aParentNode, IDtoEventInfo aEvtInfo ) {
    if( clsId2EventInfoes.hasKey( aClassInfo.id() ) ) {
      StringMap<IList<BitIdx2DtoEvent>> strid2Bits = clsId2EventInfoes.getByKey( aClassInfo.id() );
      for( String strid : strid2Bits.keys() ) {
        IList<BitIdx2DtoEvent> eventBits = strid2Bits.getByKey( strid );
        for( BitIdx2DtoEvent bit2Event : eventBits ) {
          if( bit2Event.dtoEventInfo().id().equals( aEvtInfo.id() ) ) {
            // нашли свое событие, значит у него 100пудово должно быть его узел
            UaTreeNode retVal = findNodeByBrowseName( bit2Event.bitArrayWordStrid().substring( RTD_PREFIX.length() ),
                aParentNode.getChildren() );
            TsIllegalStateRtException.checkNull( retVal,
                "Subtree %s, can't find node for class: %s, bit mask event: %s", aParentNode.getBrowseName(), //$NON-NLS-1$
                aClassInfo.id(), aEvtInfo.id() );
            return retVal;
          }
        }
      }
    }
    // событие не из битовой маски, возвращаем null
    return null;
  }

  private static UaTreeNode findParentNode( IList<UaTreeNode> aListItems, NodeId aParentNodeId ) {
    for( UaTreeNode node : aListItems ) {
      if( node.getNodeId().equals( aParentNodeId.toParseableString() ) ) {
        return node;
      }
    }
    return null;
  }

  private String createSelObjs( IM5LifecycleManager<IDtoObject> localLM ) {
    StringBuilder sb = new StringBuilder();
    // создаем выбранные объекты
    for( IDtoObject obj : localLM.itemsProvider().listItems() ) {
      conn.coreApi().objService().defineObject( obj );
      sb.append( "\n" + obj.skid() + " - " + obj.nmName() ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return sb.toString();
  }

  /**
   * По описанию команды ищем подходящие UaNodes
   *
   * @param aObj описание объекта
   * @param aCmdInfo описание command
   * @param aObjNode родительский узел описывающий объект
   * @return контейнер с описание узлов или null
   */
  private static CmdGwid2UaNodes findCmdNodes( IDtoObject aObj, IDtoCmdInfo aCmdInfo, UaTreeNode aObjNode ) {
    CmdGwid2UaNodes retVal = null;
    Gwid gwid = Gwid.createCmd( aObj.classId(), aObj.id(), aCmdInfo.id() );
    String nodeDescr = aObjNode.getBrowseName();
    String niCmdId = null;
    String niCmdArgInt = null; // может и не быть
    String niCmdArgFlt = null;
    String niCmdFeedback = null;
    // получаем тип аргумента команды
    EAtomicType argType = EAtomicType.NONE;
    if( !aCmdInfo.argDefs().isEmpty() ) {
      argType = aCmdInfo.argDefs().first().atomicType();
    }
    // перебираем все узлы и заполняем нужные нам для описания связи
    for( UaTreeNode varNode : aObjNode.getChildren() ) {
      if( varNode.getNodeClass().equals( NodeClass.Variable ) ) {
        String candidateBrowseName = varNode.getBrowseName();
        if( nodeCmdIdBrowseName.compareTo( candidateBrowseName ) == 0 ) {
          niCmdId = varNode.getNodeId();
          continue;
        }
        if( nodeCmdArgInt.compareTo( candidateBrowseName ) == 0 ) {
          niCmdArgInt = varNode.getNodeId();
          continue;
        }
        if( nodeCmdArgFlt.compareTo( candidateBrowseName ) == 0 ) {
          niCmdArgFlt = varNode.getNodeId();
          continue;
        }
        if( nodeCmdFeedback.compareTo( candidateBrowseName ) == 0 ) {
          niCmdFeedback = varNode.getNodeId();
          continue;
        }
      }
    }
    switch( argType ) {
      case INTEGER: {
        retVal = new CmdGwid2UaNodes( gwid, nodeDescr, niCmdId, niCmdArgInt, null, niCmdFeedback );
        break;
      }
      case FLOATING: {
        retVal = new CmdGwid2UaNodes( gwid, nodeDescr, niCmdId, null, niCmdArgFlt, niCmdFeedback );
        break;
      }
      case NONE: {
        retVal = new CmdGwid2UaNodes( gwid, nodeDescr, niCmdId, null, null, niCmdFeedback );
        break;
      }
      case BOOLEAN:
      case STRING:
      case TIMESTAMP:
      case VALOBJ:
      default:
        throw new TsNotAllEnumsUsedRtException( argType.name() );
    }
    return retVal;
  }

  /**
   * По описанию параметра ищем подходящий UaNode
   *
   * @param aPropInfo описание свойства класса
   * @param aVarNodes список узлов типа Variable
   * @return подходящий узел или null
   */
  private static UaTreeNode findVarNodeByPropName( IDtoClassPropInfoBase aPropInfo, IList<UaTreeNode> aVarNodes ) {
    UaTreeNode retVal = null;
    for( UaTreeNode varNode : aVarNodes ) {
      if( varNode.getNodeClass().equals( NodeClass.Variable ) ) {
        String name4Search = varNode.getBrowseName();
        if( aPropInfo.id().indexOf( name4Search ) >= 0 ) {
          retVal = varNode;
          break;
        }
      }
    }
    return retVal;
  }

  /**
   * По значению namespace ищем подходящий UaNode
   *
   * @param aContext ITsGuiContext - context.
   * @param aClassGwid {@link Gwid}
   * @param aVarNodes список узлов типа Variable
   * @return подходящий узел или null
   */
  private static UaTreeNode findVarNodeByClassGwid( ITsGuiContext aContext, Gwid aClassGwid,
      IList<UaTreeNode> aVarNodes ) {
    UaTreeNode retVal = null;
    NodeId classNodeId = OpcUaUtils.classGwid2uaNode( aContext, aClassGwid );
    TsIllegalStateRtException.checkNull( classNodeId, "Can't find nodeId for Gwid: %s", aClassGwid.asString() ); //$NON-NLS-1$
    int classNamespace = classNodeId.getNamespaceIndex().intValue();
    for( UaTreeNode varNode : aVarNodes ) {
      NodeId nodeId = NodeId.parse( varNode.getNodeId() );
      int ns = nodeId.getNamespaceIndex().intValue();

      if( ns == classNamespace ) {
        retVal = varNode;
        break;
      }
    }
    return retVal;
  }

  /**
   * По названию узла ищем UaNode
   *
   * @param aBrowseName - browseName узла
   * @param aVarNodes список узлов типа Variable
   * @return подходящий узел или null
   */
  private static UaTreeNode findNodeByBrowseName( String aBrowseName, IList<UaTreeNode> aVarNodes ) {
    UaTreeNode retVal = null;
    for( UaTreeNode varNode : aVarNodes ) {
      if( varNode.getNodeClass().equals( NodeClass.Variable ) ) {
        String name4Search = varNode.getBrowseName();
        if( aBrowseName.compareTo( name4Search ) == 0 ) {
          retVal = varNode;
          break;
        }
      }
    }
    return retVal;
  }

  /**
   * @param aModel M5 модель {@link IDtoObject}
   * @param aListObjs список объектов для верификации пользователем перед реальным созданием в БД сервера
   * @param aItemProvider локальный поставщик списка сущностей
   * @return lm заточенный под редактирование списка сущностей без реального обновления на сервере
   */
  private IM5LifecycleManager<IDtoObject> localLifeCycleManager( IM5Model<IDtoObject> aModel,
      IListEdit<IDtoObject> aListObjs, OpcUANode2SkObjectItemsProvider aItemProvider ) {
    IM5LifecycleManager<IDtoObject> retVal = new M5LifecycleManager<>( aModel, false, true, true, true, null ) {

      private IDtoObject makeDtoObject( IM5Bunch<IDtoObject> aValues ) {
        // Создаем IDpuObject и инициализируем его значениями из пучка
        String classId = aValues.getAsAv( DtoObjectM5Model.FID_CLASSID ).asString();
        String id = aValues.getAsAv( DtoObjectM5Model.FID_STRID ).asString();
        Skid skid = new Skid( classId, id );
        DtoObject dtoObject = DtoObject.createDtoObject( skid, conn.coreApi() );
        dtoObject.attrs().setValue( DtoObjectM5Model.FID_NAME, aValues.getAsAv( DtoObjectM5Model.FID_NAME ) );
        dtoObject.attrs().setValue( DtoObjectM5Model.FID_DESCRIPTION,
            aValues.getAsAv( DtoObjectM5Model.FID_DESCRIPTION ) );
        return dtoObject;
      }

      @Override
      protected IDtoObject doEdit( IM5Bunch<IDtoObject> aValues ) {
        IDtoObject edited = makeDtoObject( aValues );
        int index = aListObjs.indexOf( aValues.originalEntity() );
        // обновим кеш провайдера FIXME
        // UaTreeNode treeNode = aItemProvider.id2Node.remove( aValues.originalEntity().id() );
        aListObjs.set( index, edited );
        // aItemProvider.id2Node.put( edited.id(), treeNode );
        return edited;
      }

      @Override
      protected IList<IDtoObject> doListEntities() {
        return aListObjs;
      }
    };
    return retVal;
  }

  private void loadCmdDescrFile() {
    String cmdFileDescr = getDescrFile( SELECT_FILE_4_IMPORT_CMD );
    if( cmdFileDescr != null ) {
      File file = new File( cmdFileDescr );
      try {
        clsId2CmdInfoes = Ods2DtoCmdInfoParser.parse( file );
        TsDialogUtils.info( getShell(), MSG_CMDS_DESCR_LOADED, cmdFileDescr );
      }
      catch( IOException ex ) {
        LoggerUtils.errorLogger().error( ex );
      }
    }
  }

}
