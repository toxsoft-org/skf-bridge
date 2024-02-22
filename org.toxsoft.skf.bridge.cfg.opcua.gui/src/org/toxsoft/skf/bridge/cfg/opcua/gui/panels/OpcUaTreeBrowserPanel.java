package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.core.tslib.gw.IGwHardConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.skide.IGreenWorldRefbooks.*;
import static org.toxsoft.uskat.core.gui.km5.sded.IKM5SdedConstants.*;

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
import org.toxsoft.core.tslib.bricks.strid.coll.*;
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
import org.toxsoft.skf.refbooks.lib.*;
import org.toxsoft.skf.rri.lib.*;
import org.toxsoft.skf.rri.lib.impl.*;
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

  private static final String           nodeCmdIdBrowseName = "CmdId";       //$NON-NLS-1$
  private static final String           nodeCmdArgInt       = "CmdArgInt";   //$NON-NLS-1$
  private static final String           nodeCmdArgFlt       = "CmdArgFlt";   //$NON-NLS-1$
  private static final String           nodeCmdFeedback     = "CmdFeedback"; //$NON-NLS-1$
  private StringMap<IList<IDtoCmdInfo>> clsId2CmdInfoes     = null;

  /**
   * карта id класса - > его BitIdx2DtoRtData
   */
  private StringMap<StringMap<IList<BitIdx2DtoRtData>>>  clsId2RtDataInfoes  = null;
  /**
   * карта id класса - > его BitIdx2DtoRriAttr
   */
  private StringMap<StringMap<IList<BitIdx2RriDtoAttr>>> clsId2RriAttrInfoes = null;
  private ISkRriSection                                  rriSection          = null;

  /**
   * карта id класса - > его BitIdx2DtoEvent
   */
  private StringMap<StringMap<IList<BitIdx2DtoEvent>>> clsId2EventInfoes = new StringMap<>(); // TODO требует реализации

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
  static private String       RRI_PREFIX = "rri"; //$NON-NLS-1$
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
    // rriService = (ISkRegRefInfoService)conn.coreApi().services().getByKey( ISkRegRefInfoService.SERVICE_ID );

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
        aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_CREATE_CLASS_POLIGONE_OPC_UA_ITEM );
        aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_CREATE_OBJS_POLIGONE_OPC_UA_ITEM );
        aActs.add( ITsStdActionDefs.ACDEF_SEPARATOR );
        aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_CREATE_CLASS_SIEMENS_OPC_UA_ITEM );
        aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_CREATE_OBJS_SIEMENS_OPC_UA_ITEM );
        aActs.add( ITsStdActionDefs.ACDEF_SEPARATOR );
        aActs.add( IOpcUaServerConnCfgConstants.ACTDEF_SHOW_OPC_UA_NODE_2_GWID );

        ITsToolbar toolBar = super.doCreateToolbar( aContext, aName, aIconSize, aActs );

        toolBar.addListener( aActionId -> {
          if( aActionId == CREATE_CINFO_FROM_POLIGONE_OPC_UA_ACT_ID ) {
            // first of all ensure all needed files are loaded
            ensureCmdDescription();
            ensureBitMaskDescription();
            if( ensureRriSection( aContext ) ) {
              createClassFromNodes( aContext, EOPCUATreeType.POLIGONE );
            }
          }

          if( aActionId == CREATE_CINFO_FROM_SIEMENS_OPC_UA_ACT_ID ) {
            // first of all ensure all needed files are loaded
            ensureCmdDescription();
            ensureBitMaskDescription();
            if( ensureRriSection( aContext ) ) {
              createClassFromNodes( aContext, EOPCUATreeType.SIEMENS );
            }
          }

          if( aActionId == CREATE_OBJS_FROM_POLIGONE_OPC_UA_ACT_ID ) {
            // first of all ensure file bitMask loaded
            ensureBitMaskDescription();
            if( ensureRriSection( aContext ) ) {
              createObjsFromNodes( aContext, EOPCUATreeType.POLIGONE );
            }
          }
          if( aActionId == CREATE_OBJS_FROM_SIEMENS_OPC_UA_ACT_ID ) {
            // first of all ensure file bitMask loaded
            ensureBitMaskDescription();
            if( ensureRriSection( aContext ) ) {
              createObjsFromNodes( aContext, EOPCUATreeType.SIEMENS );
            }
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

        } );
        toolBar.setIconSize( EIconSize.IS_32X32 );
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
    componentModown.toolbar().getAction( CREATE_CINFO_FROM_POLIGONE_OPC_UA_ACT_ID ).setEnabled( false );
    componentModown.toolbar().getAction( CREATE_CINFO_FROM_SIEMENS_OPC_UA_ACT_ID ).setEnabled( false );
    componentModown.toolbar().getAction( CREATE_OBJS_FROM_POLIGONE_OPC_UA_ACT_ID ).setEnabled( false );
    componentModown.toolbar().getAction( CREATE_OBJS_FROM_SIEMENS_OPC_UA_ACT_ID ).setEnabled( false );
    componentModown.toolbar().getAction( SHOW_OPC_UA_NODE_2_GWID_ACT_ID ).setEnabled( false );
    componentModown.addTsSelectionListener( ( aSource, aSelectedItem ) -> {
      // просто активируем кнопки создания/обновления классов/объектов
      boolean enableCreateClassBttn = isCtreateClassEnable( aSelectedItem );
      boolean enableCreateObjBttn = isCtreateObjEnable( aSelectedItem );
      boolean enableCheckLinkBttn = isCheckLinkEnable( aSelectedItem );
      componentModown.toolbar().getAction( CREATE_CINFO_FROM_POLIGONE_OPC_UA_ACT_ID )
          .setEnabled( enableCreateClassBttn );
      componentModown.toolbar().getAction( CREATE_CINFO_FROM_SIEMENS_OPC_UA_ACT_ID )
          .setEnabled( enableCreateClassBttn );
      componentModown.toolbar().getAction( CREATE_OBJS_FROM_POLIGONE_OPC_UA_ACT_ID ).setEnabled( enableCreateObjBttn );
      // TODO код установки disable picture
      // ITsIconManager iconManager = aContext.get( ITsIconManager.class );
      // // тут подгружаем disable картинки
      // ImageDescriptor imd = iconManager.loadStdDescriptor( ICONID_REMOVE_NODE, EIconSize.IS_32X32 );
      // componentModown.toolbar().getAction( CREATE_OBJS_FROM_POLIGONE_OPC_UA_ACT_ID ).setDisabledImageDescriptor( imd
      // );
      componentModown.toolbar().getAction( CREATE_OBJS_FROM_SIEMENS_OPC_UA_ACT_ID ).setEnabled( enableCreateObjBttn );
      componentModown.toolbar().getAction( SHOW_OPC_UA_NODE_2_GWID_ACT_ID ).setEnabled( enableCheckLinkBttn );

      if( aSelectedItem != null ) {
        selectedNode = aSelectedItem;
      }

    } );

  }

  protected void ensureCmdDescription() {
    if( clsId2CmdInfoes == null ) {
      clsId2CmdInfoes = OpcUaUtils.readClass2CmdInfoes( conn );
    }
  }

  protected void ensureBitMaskDescription() {

    // dima 07.02.24 работаем через файл
    // TODO реализовать события
    if( clsId2RtDataInfoes == null || clsId2RriAttrInfoes == null ) {
      // if( clsId2RtDataInfoes == null || clsId2EventInfoes == null || clsId2RriAttrInfoes == null ) {
      // loadBitMaskDescrFile();
      clsId2RtDataInfoes = OpcUaUtils.readRtDataInfoes( conn );
      clsId2RriAttrInfoes = OpcUaUtils.readRriAttrInfoes( conn );

    }
  }

  private boolean ensureRriSection( ITsGuiContext aContext ) {
    boolean retVal = false;
    if( rriSection == null ) {
      // если секция одна, то выбирать не нужно
      ISkRegRefInfoService rriService =
          (ISkRegRefInfoService)conn.coreApi().services().getByKey( ISkRegRefInfoService.SERVICE_ID );
      if( rriService.listSections().size() == 1 ) {
        rriSection = rriService.listSections().first();
      }
      else {
        rriSection = PanelRriSectionSelector.selectRriSection( null, aContext );
      }
    }
    if( rriSection == null ) {
      TsDialogUtils.error( getShell(), "Не выбрана секция НСИ" );
    }
    else {
      retVal = true;
    }
    return retVal;
  }

  /**
   * Load ask user and load file with mask descriptions
   */
  // private void loadBitMaskDescrFile() {
  // String bitRtdataFileDescr = getDescrFile( SELECT_FILE_4_IMPORT_BIT_RTDATA );
  // if( bitRtdataFileDescr != null ) {
  // File file = new File( bitRtdataFileDescr );
  // try {
  // Ods2DtoRtDataInfoParser.parse( file );
  // clsId2RtDataInfoes = Ods2DtoRtDataInfoParser.getRtdataInfoesMap();
  // clsId2EventInfoes = Ods2DtoRtDataInfoParser.getEventInfoesMap();
  // clsId2RriAttrInfoes = Ods2DtoRtDataInfoParser.getRriAttrInfoesMap();
  // TsDialogUtils.info( getShell(), STR_BITMASK_FILE_LOADED, bitRtdataFileDescr );
  // }
  // catch( IOException ex ) {
  // LoggerUtils.errorLogger().error( ex );
  // }
  // }
  // }

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
    Gwid gwid = OpcUaUtils.uaNode2rtdGwid( aContext, nodeId, opcUaServerConnCfg );
    String checkResult = String.format( "%s [%s] -> %s", selectedNode.getUaNode().getBrowseName().getName(),
        nodeId.toParseableString(), gwid == null ? TsLibUtils.EMPTY_STRING : gwid.asString() );
    TsDialogUtils.info( getShell(), "Check link result:\n %s", checkResult );
  }

  private static class UaNodesTreeMaker
      implements ITsTreeMaker<UaTreeNode> {

    private final ITsNodeKind<UaTreeNode> kind =
        new TsNodeKind<>( "UaTreeNode", UaTreeNode.class, true, ICONID_TSAPP_WINDOWS_ICON ); //$NON-NLS-1$

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

  private void createClassFromNodes( ITsGuiContext aContext, EOPCUATreeType aTreeType ) {
    // создать класс из информации об UaNode
    IList<UaTreeNode> selNodes = OpcUaNodesSelector.selectUaNodes4Class( aContext, selectedNode.getUaNode().getNodeId(),
        client, opcUaServerConnCfg );
    if( selNodes != null ) {
      // создаем описание класса из списка выбранных узлов
      // отредактируем список узлов чтобы в нем была вся необходимая информация для описания класса
      IList<UaTreeNode> nodes4DtoClass = nodes4DtoClass( selectedNode, selNodes, aTreeType );
      // TODO здесь очищаем список от rri нодов и формируем отдельный для rtd
      IListEdit<UaNode2Gwid> node2ClassGwidList = new ElemArrayList<>();
      Pair<IDtoClassInfo, IDtoClassInfo> pairClassInfo = makeDtoClassInfo( nodes4DtoClass, node2ClassGwidList );
      IDtoClassInfo dtoClassInfo = pairClassInfo.left();
      IDtoClassInfo rriDtoClassInfo = pairClassInfo.right();
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
        // просим редактировать описание НСИ класса и нажать Ok
        dtoClassInfo =
            M5GuiUtils.askEdit( tsContext(), modelDto, dtoClassInfo, cdi, modelDto.getLifecycleManager( conn ) );
        // если пользователь нажал Ok, то создаем основной и теневой (НСИ) класс
        if( dtoClassInfo != null && rriSection != null && !rriDtoClassInfo.attrInfos().isEmpty() ) {
          // создаем временный IM5LifecycleManager задача которого ничего не делать, а просто отобразить содержимое
          // описания класса
          IM5LifecycleManager<IDtoClassInfo> localLM = localLifeCycleManager4DtoClassInfo( modelDto );
          // фильтруем содержимое НСИ так чтобы атрибуты НСИ не совпадали с rtData базового класса
          rriDtoClassInfo = filterRriClassInfo( dtoClassInfo, rriDtoClassInfo );
          // создаем НСИ атрибуты и связи
          cdi = new TsDialogInfo( tsContext(), null, "Параметры НСИ класса",
              "Отредактируйте НСИ атрибуты и связи класса", 0 );
          // установим нормальный размер диалога
          cdi.setMinSize( new TsPoint( -30, -80 ) );
          rriDtoClassInfo = M5GuiUtils.askEdit( tsContext(), modelDto, rriDtoClassInfo, cdi, localLM );
          if( rriDtoClassInfo != null ) {
            defineRriParams( aContext, rriDtoClassInfo );
          }
        }
        // чистим список привязок ClassGwid -> NodeId
        node2ClassGwidList = filterNode2ClassGwidList( dtoClassInfo, rriDtoClassInfo, node2ClassGwidList );
        // заливаем в хранилище
        OpcUaUtils.updateNodes2GwidsInStore( aContext, node2ClassGwidList,
            OpcUaUtils.SECTID_OPC_UA_NODES_2_CLS_GWIDS_TEMPLATE, UaNode2Gwid.KEEPER, opcUaServerConnCfg );

        ISkidList skids2Remove = conn.coreApi().objService().listSkids( currDtoClassInfo.id(), false );
        // перепривязываем объекты
        ISkClassInfo updatedClassInfo = conn.coreApi().sysdescr().getClassInfo( currDtoClassInfo.id() );
        IListEdit<IDtoObject> listObj2Update = new ElemArrayList<>();
        for( Skid skid : skids2Remove ) {
          ISkObject obj2Update = conn.coreApi().objService().find( skid );
          listObj2Update.add( DtoObject.createFromSk( obj2Update, conn.coreApi() ) );
        }
        OpcUaUtils.clearCache( opcUaServerConnCfg );
        generateNode2GwidLinks( aContext, updatedClassInfo, listObj2Update, aTreeType,
            rriSection == null ? IStridablesList.EMPTY : rriSection.listParamInfoes( updatedClassInfo.id() ) );
        TsDialogUtils.info( getShell(), STR_SUCCESS_CLASS_UPDATED, currDtoClassInfo.id() );
      }
      else {
        // создаем пучок из модели
        IM5Bunch<IDtoClassInfo> bunchOfFieldVals = modelDto.valuesOf( dtoClassInfo );
        // просим прользователя верифицировать/редактировать описание класса и нажать Ok
        dtoClassInfo =
            M5GuiUtils.askCreate( tsContext(), modelDto, bunchOfFieldVals, cdi, modelDto.getLifecycleManager( conn ) );
        if( dtoClassInfo != null ) {
          // FIXME copy|paste code, see up need refactoring

          // создаем временный IM5LifecycleManager задача которого ничего не делать, а просто отобразить содержимое
          // описания класса
          IM5LifecycleManager<IDtoClassInfo> localLM = localLifeCycleManager4DtoClassInfo( modelDto );
          // фильтруем содержимое НСИ так чтобы атрибуты НСИ не совпадали с rtData базового класса
          rriDtoClassInfo = filterRriClassInfo( dtoClassInfo, rriDtoClassInfo );
          // создаем НСИ атрибуты и связи
          cdi = new TsDialogInfo( tsContext(), null, "Параметры НСИ класса",
              "Отредактируйте НСИ атрибуты и связи класса", 0 );
          // установим нормальный размер диалога
          cdi.setMinSize( new TsPoint( -30, -80 ) );
          rriDtoClassInfo = M5GuiUtils.askEdit( tsContext(), modelDto, rriDtoClassInfo, cdi, localLM );
          if( rriDtoClassInfo != null ) {
            defineRriParams( aContext, rriDtoClassInfo );
          }

          // чистим список привязок ClassGwid -> NodeId
          node2ClassGwidList = filterNode2ClassGwidList( dtoClassInfo, rriDtoClassInfo, node2ClassGwidList );
          // заливаем в хранилище
          OpcUaUtils.updateNodes2GwidsInStore( aContext, node2ClassGwidList,
              OpcUaUtils.SECTID_OPC_UA_NODES_2_CLS_GWIDS_TEMPLATE, UaNode2Gwid.KEEPER, opcUaServerConnCfg );
          OpcUaUtils.clearCache( opcUaServerConnCfg );
          // подтверждаем успешное создание класса
          TsDialogUtils.info( getShell(), STR_SUCCESS_CLASS_CREATED, dtoClassInfo.id() );
        }
      }
    }
  }

  /**
   * По описания атрибутов создает НСИ атрибуты
   *
   * @param aContext контекст приложения
   * @param aRriDtoClassInfo - описание НСИ
   */
  public void defineRriParams( ITsGuiContext aContext, IDtoClassInfo aRriDtoClassInfo ) {
    // TODO refactoring создаем/обновляем НСИ параметры
    IStridablesListEdit<IDtoRriParamInfo> rriParamInfoes = new StridablesList<>();
    for( IDtoAttrInfo attrInfo : aRriDtoClassInfo.attrInfos() ) {
      rriParamInfoes.add( new DtoRriParamInfo( attrInfo ) );
    }
    for( IDtoLinkInfo linkInfo : aRriDtoClassInfo.linkInfos() ) {
      rriParamInfoes.add( new DtoRriParamInfo( linkInfo ) );
    }
    rriSection.defineParam( aRriDtoClassInfo.id(), rriParamInfoes );
  }

  private static IDtoClassInfo filterRriClassInfo( IDtoClassInfo aDtoClassInfo, IDtoClassInfo aRriDtoClassInfo ) {
    // копируем свойства исходного
    DtoClassInfo retVal =
        new DtoClassInfo( aRriDtoClassInfo.id(), aRriDtoClassInfo.parentId(), aRriDtoClassInfo.params() );
    retVal.attrInfos().addAll( aRriDtoClassInfo.attrInfos() );
    retVal.linkInfos().addAll( aRriDtoClassInfo.linkInfos() );
    // удаляем из класса описания НСИ все атрибуты которые совпадают с rtData базового класса
    for( IDtoRtdataInfo rtData : aDtoClassInfo.rtdataInfos() ) {
      String nodeName = rtData.id().substring( 3 );
      for( IDtoAttrInfo attr : aRriDtoClassInfo.attrInfos() ) {
        String rriAttrId = attr.id().substring( 3 );
        if( rriAttrId.compareTo( nodeName ) == 0 ) {
          retVal.attrInfos().remove( attr );
        }
      }
    }
    return retVal;
  }

  /**
   * Фильтрует ранее подготовленный список привязок NodeId->ClassGwid удаляее элементы которые пользователь выбросил.
   * Оставляет
   *
   * @param aDtoClassInfo - описание класса отредактированное пользователем
   * @param aRriDtoClassInfo - описание НСИшной части класса отредактированное пользователем
   * @param aNode2ClassGwidList - ранее подготовленный список привязок NodeId->ClassGwid
   * @return отфильрованный список
   */
  private static IListEdit<UaNode2Gwid> filterNode2ClassGwidList( IDtoClassInfo aDtoClassInfo,
      IDtoClassInfo aRriDtoClassInfo, IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
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
    for( IDtoAttrInfo attrInfo : aRriDtoClassInfo.attrInfos() ) {
      Gwid gwid = Gwid.createAttr( aDtoClassInfo.id(), attrInfo.id() );
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
    // копируем свойства нового
    dtoClass.attrInfos().setAll( aDtoClassInfo.attrInfos() );
    dtoClass.rtdataInfos().setAll( aDtoClassInfo.rtdataInfos() );
    dtoClass.cmdInfos().setAll( aDtoClassInfo.cmdInfos() );
    dtoClass.eventInfos().setAll( aDtoClassInfo.eventInfos() );

    // обновляем атрибуты
    for( IDtoAttrInfo attrInfo : aCurrDtoClassInfo.attrInfos() ) {
      if( !dtoClass.attrInfos().hasKey( attrInfo.id() ) ) {
        dtoClass.attrInfos().add( attrInfo );
      }
    }
    // обновляем rtData
    for( IDtoRtdataInfo rtDataInfo : aCurrDtoClassInfo.rtdataInfos() ) {
      if( !dtoClass.rtdataInfos().hasKey( rtDataInfo.id() ) ) {
        dtoClass.rtdataInfos().add( rtDataInfo );
      }
    }
    // обновляем cmds
    for( IDtoCmdInfo cmdInfo : aCurrDtoClassInfo.cmdInfos() ) {
      if( !dtoClass.cmdInfos().hasKey( cmdInfo.id() ) ) {
        dtoClass.cmdInfos().add( cmdInfo );
      }
    }
    // обновляем events
    for( IDtoEventInfo evtInfo : aCurrDtoClassInfo.eventInfos() ) {
      if( !dtoClass.eventInfos().hasKey( evtInfo.id() ) ) {
        dtoClass.eventInfos().add( evtInfo );
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
   * @param aTreeType тип дерева OPC UA
   * @return список узлов один из которых описание класса, остальные описание RtData этого класса
   */
  private static IList<UaTreeNode> nodes4DtoClass( UaTreeNode aClassNode, IList<UaTreeNode> aSelNodes,
      EOPCUATreeType aTreeType ) {
    IListEdit<UaTreeNode> retVal = new ElemArrayList<>();
    // первый элемент - описание класса
    retVal.add( getClassNode( aTreeType, aClassNode ) );
    for( UaTreeNode node : aSelNodes ) {
      if( node.getNodeClass().equals( NodeClass.Variable ) ) {
        retVal.add( node );
      }
    }
    return retVal;
  }

  private static UaTreeNode getClassNode( EOPCUATreeType aTreeType, UaTreeNode aNode ) {
    UaTreeNode retVal = null;
    switch( aTreeType ) {
      case POLIGONE: {
        // Для Poligone узел класса и есть переданный узел
        retVal = aNode;
        break;
      }
      case OTHER:
        throw new TsUnsupportedFeatureRtException( "Unsupported tree type: %s", aTreeType ); //$NON-NLS-1$
      case SIEMENS:
        // Для Siemens узел класса это родительский узел
        retVal = aNode.getParent();
        break;
      default:
        throw new TsUnsupportedFeatureRtException( "Unsupported tree type: %s", aTreeType ); //$NON-NLS-1$
    }
    return retVal;
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //
  private Pair<IDtoClassInfo, IDtoClassInfo> makeDtoClassInfo( IList<UaTreeNode> aNodes,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // узел типа Object, его данные используются для создания описания класса
    UaTreeNode classNode = objNode( aNodes );

    String id = classNode.getBrowseName();
    String name = classNode.getDisplayName();
    String description = classNode.getDescription().trim().length() > 0 ? classNode.getDescription() : name;

    IOptionSetEdit params = new OptionSet();

    params.setStr( FID_NAME, name );
    params.setStr( FID_DESCRIPTION, description );
    DtoClassInfo cinf = new DtoClassInfo( id, IGwHardConstants.GW_ROOT_CLASS_ID, params );
    DtoClassInfo rriCinf = new DtoClassInfo( id, IGwHardConstants.GW_ROOT_CLASS_ID, params );
    for( UaTreeNode node : aNodes ) {
      if( node.getNodeClass().equals( NodeClass.Variable ) ) {
        try {
          UaVariableNode varNode = client.getAddressSpace().getVariableNode( NodeId.parse( node.getNodeId() ) );
          readDataInfo( cinf, varNode, aNode2ClassGwidList );
          // НСИ атрибут
          readRriAttrInfo( rriCinf, varNode, aNode2ClassGwidList );
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
    // добавим битовые RRI Param, если они описаны
    if( clsId2RriAttrInfoes != null ) {
      if( clsId2RriAttrInfoes.hasKey( id ) ) {
        StringMap<IList<BitIdx2RriDtoAttr>> rriAttrInfioesMap = clsId2RriAttrInfoes.getByKey( id );
        for( String key : rriAttrInfioesMap.keys() ) {
          IList<BitIdx2RriDtoAttr> list = rriAttrInfioesMap.getByKey( key );
          for( BitIdx2RriDtoAttr dto : list ) {
            rriCinf.attrInfos().add( dto.dtoAttrInfo() );
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

    return new Pair<>( cinf, rriCinf );
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
   * Читает описание Rt данного и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aDtoClass текущее описание класса
   * @param aVariableNode описание узла типа переменная
   * @param aNode2ClassGwidList список для хранения привязки node -> Class Gwid
   */
  private void readRriAttrInfo( DtoClassInfo aDtoClass, UaVariableNode aVariableNode,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // id атрибута
    String attrId = aVariableNode.getBrowseName().getName();
    if( isIgnore4RriAttr( aDtoClass.id(), attrId ) ) {
      return;
    }
    // соблюдаем соглашения о наименовании
    if( !attrId.startsWith( RRI_PREFIX ) ) {
      attrId = RRI_PREFIX + attrId;
    }
    // название
    String name = aVariableNode.getDisplayName().getText();
    // описание
    String descr = aVariableNode.getDescription().getText();
    // описание
    if( (descr == null) || descr.isBlank() ) {
      descr = name;
    }

    // тип данных атрибута
    Class<?> clazz = OpcUaUtils.getNodeDataTypeClass( aVariableNode );
    EAtomicType type = OpcUaUtils.getAtomicType( clazz );
    DataType ddType = DataType.create( type );

    IDtoAttrInfo atrInfo = DtoAttrInfo.create2( attrId, ddType, //
        TSID_NAME, name, //
        TSID_DESCRIPTION, descr );

    aDtoClass.attrInfos().add( atrInfo );

    // TODO boilerplate code сохраним привязку для использования в автоматическом связывании
    NodeId nodeId = aVariableNode.getNodeId();
    Gwid classGwid = Gwid.createAttr( aDtoClass.id(), attrId );
    UaNode2Gwid uaNode2Gwid = new UaNode2Gwid( nodeId.toParseableString(), descr, classGwid );
    aNode2ClassGwidList.add( uaNode2Gwid );
  }

  /**
   * Читает описание Rt данного и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aDtoClass текущее описание класса
   * @param aVariableNode описание узла типа переменная
   * @param aNode2ClassGwidList список для хранения привязки node -> Class Gwid
   * @return {@link IDtoRtdataInfo} - созданное описание RtData или null
   */
  private IDtoRtdataInfo readDataInfo( DtoClassInfo aDtoClass, UaVariableNode aVariableNode,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // id данного
    String dataId = aVariableNode.getBrowseName().getName();
    if( isIgnore4RtData( aDtoClass.id(), dataId ) ) {
      return null;
    }
    // соблюдаем соглашения о наименовании
    if( !dataId.startsWith( RTD_PREFIX ) ) {
      dataId = RTD_PREFIX + dataId;
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
    return dataInfo;
  }

  private boolean isIgnore4RriAttr( String aClassId, String aAttrId ) {
    // используем только узлы для работы с НСИ
    if( aAttrId.startsWith( RRI_PREFIX ) ) {
      return false;
    }
    return !existInRriRefbook( aClassId, aAttrId );
  }

  private boolean isIgnore4RtData( String aClassId, String aDataId ) {
    // игнорируем узлы для работы с НСИ
    if( aDataId.startsWith( RRI_PREFIX ) ) {
      return true;
    }
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
    // последним пунктом проверки на содержание в справочнике НСИ
    return existInRriRefbook( aClassId, aDataId );
  }

  /**
   * Проверяет наличие в справочнике RBID_RRI_OPCUA элемента с составным strid
   *
   * @param aClassId - префикс составного strid
   * @param aDataId - суффикс составного strid
   * @return true если элемент с таким strid есть в справочнике
   */
  private boolean existInRriRefbook( String aClassId, String aDataId ) {
    // читаем справочник НСИ и фильтруем то что предназначено для этого
    ISkRefbookService skRefServ = (ISkRefbookService)conn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    IList<ISkRefbookItem> rbItems = skRefServ.findRefbook( RBID_RRI_OPCUA ).listItems();
    // создаем id элемента справочника
    String rbItemStrid = new StringBuffer( aClassId ).append( "." ).append( aDataId ).toString();
    for( ISkRefbookItem rbItem : rbItems ) {
      if( rbItem.strid().equals( rbItemStrid ) ) {
        return true;
      }
    }
    return false;
  }

  private boolean isIgnore4Event( String aClassId, String aEvId ) {
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

    // последним пунктом проверки на содержание в справочнике НСИ
    return existInRriRefbook( aClassId, aEvId );
  }

  /**
   * Читает описание события и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aDtoClass текущее описание класса
   * @param aVariableNode описание узла типа переменная
   * @param aNode2ClassGwidList список привязок Node -> Class Gwid
   */
  private void readEventInfo( DtoClassInfo aDtoClass, UaVariableNode aVariableNode,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // id события
    String evtId = aVariableNode.getBrowseName().getName();
    if( isIgnore4Event( aDtoClass.id(), evtId ) ) {
      return;
    }
    // соблюдаем соглашения о наименовании
    if( !evtId.startsWith( EVT_PREFIX ) ) {
      evtId = EVT_PREFIX + evtId;
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

  private void createObjsFromNodes( ITsGuiContext aContext, EOPCUATreeType aTreeType ) {
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

        IM5LifecycleManager<IDtoObject> localLM = localLifeCycleManager4DtoObject( modelSk, llObjs, itemProvider );

        // ITsGuiContext ctxSk = new TsGuiContext( tsContext() );

        IM5CollectionPanel<IDtoObject> skObjsPanel =
            modelSk.panelCreator().createCollEditPanel( aContext, localLM.itemsProvider(), localLM );
        // show dialog
        TsDialogInfo di = new TsDialogInfo( aContext, getShell(), DLG_C_NEW_OBJS, DLG_T_NEW_OBJS, 0 );
        di.setMinSizeShellRelative( 10, 50 );
        if( M5GuiUtils.showCollPanel( di, skObjsPanel ) != null ) {
          // сохраним привязки node -> Skid
          OpcUaUtils.updateNodes2SkidsInStore( aContext, itemProvider.node2SkidList, opcUaServerConnCfg );
          String userMsg = createSelObjs( localLM );
          // делаем автоматическую привязку NodeId -> Gwid
          generateNode2GwidLinks( aContext, selectedClassInfo, localLM.itemsProvider().listItems(), aTreeType,
              rriSection == null ? IStridablesList.EMPTY : rriSection.listParamInfoes( selectedClassInfo.id() ) );
          // подтверждаем успешное создание объектов
          TsDialogUtils.info( getShell(), STR_SUCCESS_OBJS_UPDATED, userMsg );
        }
      }
    }
  }

  private void generateNode2GwidLinks( ITsGuiContext aContext, ISkClassInfo aClassInfo, IList<IDtoObject> aObjList,
      EOPCUATreeType aTreeType, IStridablesList<IDtoRriParamInfo> aRriParamInfoes ) {
    IListEdit<UaNode2Gwid> node2RtdGwidList = new ElemArrayList<>();
    IListEdit<UaNode2Gwid> node2RriGwidList = new ElemArrayList<>();
    IListEdit<UaNode2EventGwid> node2EvtGwidList = new ElemArrayList<>();
    IListEdit<CmdGwid2UaNodes> cmdGwid2UaNodesList = new ElemArrayList<>();
    IListEdit<CmdGwid2UaNodes> rriAttrCmdGwid2UaNodesList = new ElemArrayList<>();
    // в этом месте у нас 100% уже загружено дерево узлов OPC UA
    IList<UaTreeNode> treeNodes = ((OpcUaNodeM5LifecycleManager)componentModown.lifecycleManager()).getCached();
    // идем по списку объектов
    for( IDtoObject obj : aObjList ) {
      // находим родительский UaNode
      // UaTreeNode parentNode = aItemProvider.nodeById( obj.id() );
      Skid parentSkid = new Skid( aClassInfo.id(), obj.id() );
      OpcUaUtils.getTreeSectionNameByConfig( OpcUaUtils.SECTID_OPC_UA_NODES_2_SKIDS_TEMPLATE, opcUaServerConnCfg );

      NodeId parentNodeId = OpcUaUtils.nodeBySkid( aContext, parentSkid, opcUaServerConnCfg );
      TsIllegalStateRtException.checkNull( parentNodeId,
          "Can't find nodeId for Skid: %s .\n Check section %s in file data-storage.kt", parentSkid.toString(),
          OpcUaUtils.getTreeSectionNameByConfig( OpcUaUtils.SECTID_OPC_UA_NODES_2_SKIDS_TEMPLATE,
              opcUaServerConnCfg ) );
      UaTreeNode parentNode = findParentNode( treeNodes, parentNodeId );
      // привязываем команды
      for( IDtoCmdInfo cmdInfo : aClassInfo.cmds().list() ) {
        CmdGwid2UaNodes cmdGwid2UaNodes = findCmdNodes( obj, cmdInfo, parentNode, aTreeType );
        cmdGwid2UaNodesList.add( cmdGwid2UaNodes );
      }
      // идем по списку его rtdProperties
      for( IDtoRtdataInfo rtdInfo : aClassInfo.rtdata().list() ) {
        // находим свой UaNode
        // сначала ищем в данных битовой маски
        UaTreeNode uaNode = tryBitMaskRtData( aClassInfo, parentNode, rtdInfo, aTreeType );
        if( uaNode == null ) {
          uaNode = switch( aTreeType ) {
            case OTHER -> throw new TsUnsupportedFeatureRtException();
            // version for Poligone tree
            case POLIGONE -> findVarNodeByClassGwid( aContext, Gwid.createRtdata( aClassInfo.id(), rtdInfo.id() ),
                parentNode.getChildren() );
            // version for Siemens tree
            case SIEMENS -> findVarNodeByPropName( rtdInfo, parentNode, aTreeType );
            default -> throw new TsUnsupportedFeatureRtException();
          };
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
      // идем по списку его rriProperties
      for( IDtoRriParamInfo aParamInfo : aRriParamInfoes ) {
        if( aParamInfo.isLink() ) {
          continue;
        }
        IDtoAttrInfo attrInfo = aParamInfo.attrInfo();
        // привязываем атрибуты к командным тегам
        CmdGwid2UaNodes rriAttrCmdGwid2UaNodes = findAttrCmdNodes( obj, attrInfo, parentNode, aTreeType );
        rriAttrCmdGwid2UaNodesList.add( rriAttrCmdGwid2UaNodes );

        // находим свой UaNode
        // сначала ищем в данных битовой маски
        UaTreeNode uaNode = tryBitMaskRriAttr( aClassInfo, parentNode, attrInfo, aTreeType );
        if( uaNode == null ) {
          uaNode = switch( aTreeType ) {
            case OTHER -> throw new TsUnsupportedFeatureRtException();
            // version for Poligone tree
            case POLIGONE -> findVarNodeByClassGwid( aContext, Gwid.createAttr( aClassInfo.id(), attrInfo.id() ),
                parentNode.getChildren() );
            // version for Siemens tree
            case SIEMENS -> findVarNodeByPropName( attrInfo, parentNode, aTreeType );
            default -> throw new TsUnsupportedFeatureRtException();
          };
        }
        Gwid gwid = Gwid.createAttr( obj.classId(), obj.id(), attrInfo.id() );
        if( uaNode != null ) {
          LoggerUtils.defaultLogger().debug( "%s [%s] -> RRI attr %s", uaNode.getBrowseName(), uaNode.getNodeId(), //$NON-NLS-1$
              gwid.asString() );
          String nodeDescr = parentNode.getBrowseName() + "::" + uaNode.getBrowseName(); //$NON-NLS-1$
          UaNode2Gwid node2Gwid = new UaNode2Gwid( uaNode.getNodeId(), nodeDescr, gwid );
          node2RriGwidList.add( node2Gwid );
        }
        else {
          LoggerUtils.errorLogger().error( "Can't match rtData: ? -> %s", gwid.asString() ); //$NON-NLS-1$
        }
      }
      // идем по списку его events
      for( IDtoEventInfo evtInfo : aClassInfo.events().list() ) {
        // находим свой UaNode
        // сначала ищем в событиях битовой маске
        UaTreeNode uaNode = tryBitMaskEvent( aClassInfo, parentNode, evtInfo, aTreeType );
        if( uaNode == null ) {
          uaNode = switch( aTreeType ) {
            case OTHER -> throw new TsUnsupportedFeatureRtException();
            case POLIGONE -> findVarNodeByClassGwid( aContext, Gwid.createEvent( aClassInfo.id(), evtInfo.id() ),
                parentNode.getChildren() );
            case SIEMENS -> findVarNodeByPropName( evtInfo, parentNode, aTreeType );
            default -> throw new TsUnsupportedFeatureRtException();
          };

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
    OpcUaUtils.updateNodes2GwidsInStore( aContext, node2RtdGwidList,
        OpcUaUtils.SECTID_OPC_UA_NODES_2_RTD_GWIDS_TEMPLATE, UaNode2Gwid.KEEPER, opcUaServerConnCfg );
    OpcUaUtils.updateNodes2GwidsInStore( aContext, node2RriGwidList,
        OpcUaUtils.SECTID_OPC_UA_NODES_2_RRI_GWIDS_TEMPLATE, UaNode2Gwid.KEEPER, opcUaServerConnCfg );
    OpcUaUtils.updateNodes2GwidsInStore( aContext, node2EvtGwidList,
        OpcUaUtils.SECTID_OPC_UA_NODES_2_EVT_GWIDS_TEMPLATE, UaNode2EventGwid.KEEPER, opcUaServerConnCfg );
    OpcUaUtils.updateCmdGwid2NodesInStore( aContext, cmdGwid2UaNodesList, opcUaServerConnCfg );
    OpcUaUtils.updateRriAttrGwid2NodesInStore( aContext, rriAttrCmdGwid2UaNodesList, opcUaServerConnCfg );
  }

  private static IList<UaTreeNode> getVariableNodes( UaTreeNode aObjectNode, EOPCUATreeType aTreeType ) {
    IList<UaTreeNode> retVal = null;
    switch( aTreeType ) {
      case SIEMENS: {
        // у Siemens узлы переменных находятся ниже подузла Static
        UaTreeNode staticNode = aObjectNode.getChildren().first();
        retVal = staticNode.getChildren();
        break;
      }
      case POLIGONE:
        // у Poligone узлы переменных сразу под описанием класса
        retVal = aObjectNode.getChildren();
        break;
      case OTHER:
      default:
        throw new TsUnsupportedFeatureRtException();
    }
    return retVal;
  }

  private UaTreeNode tryBitMaskRtData( ISkClassInfo aClassInfo, UaTreeNode aParentNode, IDtoRtdataInfo aRtDataInfo,
      EOPCUATreeType aTreeType ) {
    if( clsId2RtDataInfoes != null && clsId2RtDataInfoes.hasKey( aClassInfo.id() ) ) {
      StringMap<IList<BitIdx2DtoRtData>> strid2Bits = clsId2RtDataInfoes.getByKey( aClassInfo.id() );
      for( String strid : strid2Bits.keys() ) {
        IList<BitIdx2DtoRtData> rtDataBits = strid2Bits.getByKey( strid );
        for( BitIdx2DtoRtData bit2rtData : rtDataBits ) {
          if( bit2rtData.dtoRtdataInfo().id().equals( aRtDataInfo.id() ) ) {
            // нашли свое данное, значит у него 100пудово должен быть его узел
            String bitArrayNode = bit2rtData.bitArrayWordStrid().substring( RTD_PREFIX.length() );
            // получаем список узлов в котором описаны переменные класса
            IList<UaTreeNode> variableNodes = getVariableNodes( aParentNode, aTreeType );
            UaTreeNode retVal = findNodeByBrowseName( bitArrayNode, variableNodes );
            TsIllegalStateRtException.checkNull( retVal,
                "Subtree %s, can't find node %s for class: %s, bit mask rtData: %s", bitArrayNode, //$NON-NLS-1$
                aParentNode.getBrowseName(), aClassInfo.id(), aRtDataInfo.id() );
            return retVal;
          }
        }
      }
    }
    // данное не из битовой маски, возвращаем null
    return null;
  }

  private UaTreeNode tryBitMaskRriAttr( ISkClassInfo aClassInfo, UaTreeNode aParentNode, IDtoAttrInfo aRriAttrInfo,
      EOPCUATreeType aTreeType ) {
    if( clsId2RriAttrInfoes != null && clsId2RriAttrInfoes.hasKey( aClassInfo.id() ) ) {
      StringMap<IList<BitIdx2RriDtoAttr>> strid2RriBits = clsId2RriAttrInfoes.getByKey( aClassInfo.id() );
      for( String strid : strid2RriBits.keys() ) {
        IList<BitIdx2RriDtoAttr> rriAttrBits = strid2RriBits.getByKey( strid );
        for( BitIdx2RriDtoAttr bit2rriAttr : rriAttrBits ) {
          if( bit2rriAttr.dtoAttrInfo().id().equals( aRriAttrInfo.id() ) ) {
            // нашли свой НСИ атрибут, значит у него 100пудово должен быть его узел
            String bitArrayNode = bit2rriAttr.bitArrayWordStrid().substring( RTD_PREFIX.length() );
            // получаем список узлов в котором описаны переменные класса
            IList<UaTreeNode> variableNodes = getVariableNodes( aParentNode, aTreeType );
            UaTreeNode retVal = findNodeByBrowseName( bitArrayNode, variableNodes );
            TsIllegalStateRtException.checkNull( retVal,
                "Subtree %s, can't find node %s for class: %s, bit mask rriAttr: %s", bitArrayNode, //$NON-NLS-1$
                aParentNode.getBrowseName(), aClassInfo.id(), aRriAttrInfo.id() );
            return retVal;
          }
        }
      }
    }
    // НСИ атрибут не из битовой маски, возвращаем null
    return null;
  }

  private UaTreeNode tryBitMaskEvent( ISkClassInfo aClassInfo, UaTreeNode aParentNode, IDtoEventInfo aEvtInfo,
      EOPCUATreeType aTreeType ) {
    if( clsId2RtDataInfoes != null && clsId2EventInfoes.hasKey( aClassInfo.id() ) ) {
      StringMap<IList<BitIdx2DtoEvent>> strid2Bits = clsId2EventInfoes.getByKey( aClassInfo.id() );
      for( String strid : strid2Bits.keys() ) {
        IList<BitIdx2DtoEvent> eventBits = strid2Bits.getByKey( strid );
        for( BitIdx2DtoEvent bit2Event : eventBits ) {
          if( bit2Event.dtoEventInfo().id().equals( aEvtInfo.id() ) ) {
            // нашли свое событие, значит у него 100пудово должно быть его узел
            // получаем список узлов в котором описаны переменные класса
            IList<UaTreeNode> variableNodes = getVariableNodes( aParentNode, aTreeType );
            UaTreeNode retVal =
                findNodeByBrowseName( bit2Event.bitArrayWordStrid().substring( RTD_PREFIX.length() ), variableNodes );
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
   * @param aTreeType тип дерева
   * @return контейнер с описание узлов или null
   */
  private static CmdGwid2UaNodes findCmdNodes( IDtoObject aObj, IDtoCmdInfo aCmdInfo, UaTreeNode aObjNode,
      EOPCUATreeType aTreeType ) {
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
    // получаем список узлов в котором описаны переменные класса
    IList<UaTreeNode> variableNodes = getVariableNodes( aObjNode, aTreeType );
    // перебираем все узлы и заполняем нужные нам для описания связи
    for( UaTreeNode varNode : variableNodes ) {
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
    retVal = switch( argType ) {
      case INTEGER, FLOATING, NONE -> new CmdGwid2UaNodes( gwid, nodeDescr, niCmdId, niCmdArgInt, niCmdArgFlt,
          niCmdFeedback, argType );
      case BOOLEAN, STRING, TIMESTAMP, VALOBJ -> throw new TsNotAllEnumsUsedRtException( argType.name() );
    };
    return retVal;
  }

  /**
   * По описанию атрибута ищем подходящие командные UaNodes
   *
   * @param aObj описание объекта
   * @param aAttrInfo описание атрибута
   * @param aObjNode родительский узел описывающий объект
   * @param aTreeType тип дерева
   * @return контейнер с описание узлов или null
   */
  private static CmdGwid2UaNodes findAttrCmdNodes( IDtoObject aObj, IDtoAttrInfo aAttrInfo, UaTreeNode aObjNode,
      EOPCUATreeType aTreeType ) {
    CmdGwid2UaNodes retVal = null;
    Gwid gwid = Gwid.createCmd( aObj.classId(), aObj.id(), aAttrInfo.id() );
    String nodeDescr = aObjNode.getBrowseName();
    String niCmdId = null;
    String niCmdArgInt = null; // может и не быть
    String niCmdArgFlt = null;
    String niCmdFeedback = null;
    // получаем тип
    EAtomicType argType = aAttrInfo.dataType().atomicType();
    // получаем список узлов в котором описаны переменные класса
    IList<UaTreeNode> variableNodes = getVariableNodes( aObjNode, aTreeType );
    // перебираем все узлы и заполняем нужные нам для описания связи
    for( UaTreeNode varNode : variableNodes ) {
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
    retVal = switch( argType ) {
      case BOOLEAN, INTEGER, FLOATING, NONE -> new CmdGwid2UaNodes( gwid, nodeDescr, niCmdId, niCmdArgInt, niCmdArgFlt,
          niCmdFeedback, argType );
      case STRING, TIMESTAMP, VALOBJ -> throw new TsNotAllEnumsUsedRtException( argType.name() );
    };
    return retVal;
  }

  /**
   * По описанию параметра ищем подходящий UaNode
   *
   * @param aPropInfo описание свойства класса
   * @param aObjectNode узел дерева объекта
   * @param aTreeType тип дерева
   * @return подходящий узел или null
   */
  private static UaTreeNode findVarNodeByPropName( IDtoClassPropInfoBase aPropInfo, UaTreeNode aObjectNode,
      EOPCUATreeType aTreeType ) {
    UaTreeNode retVal = null;
    IList<UaTreeNode> varNodes = getVariableNodes( aObjectNode, aTreeType );
    for( UaTreeNode varNode : varNodes ) {
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
   * По значению Gwid класса ищем его UaNode
   *
   * @param aContext ITsGuiContext - context.
   * @param aClassGwid {@link Gwid} Gwid класса
   * @param aVarNodes список узлов типа Variable
   * @return подходящий узел или null
   */
  private UaTreeNode findVarNodeByClassGwid( ITsGuiContext aContext, Gwid aClassGwid, IList<UaTreeNode> aVarNodes ) {
    UaTreeNode retVal = null;
    NodeId classNodeId = OpcUaUtils.classGwid2uaNode( aContext, aClassGwid, opcUaServerConnCfg );
    if( classNodeId != null ) {
      // TsIllegalStateRtException.checkNull( classNodeId, "Can't find nodeId for Gwid: %s", aClassGwid.asString() );
      // //$NON-NLS-1$
      // отрабатываем вариант когда 1 класс - 1 объект, в таком случае ищем полное совпадение nodeId
      for( UaTreeNode varNode : aVarNodes ) {
        NodeId nodeId = NodeId.parse( varNode.getNodeId() );
        if( nodeId.equals( classNodeId ) ) {
          return varNode;
        }
      }
      // здесь отрабатываем вариант когда класс отдельно, объекты отдельно. В этом варианте опираемся на правило
      // равенства
      // namespace у узлов описания класса и объекта
      int classNamespace = classNodeId.getNamespaceIndex().intValue();
      for( UaTreeNode varNode : aVarNodes ) {
        NodeId nodeId = NodeId.parse( varNode.getNodeId() );
        int ns = nodeId.getNamespaceIndex().intValue();

        if( ns == classNamespace ) {
          retVal = varNode;
          break;
        }
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
   * @param aModel M5 модель {@link IDtoClassInfo}
   * @return lm заточенный под редактирование списка параметров НСИ без реального обновления на сервере
   */
  private static IM5LifecycleManager<IDtoClassInfo> localLifeCycleManager4DtoClassInfo(
      IM5Model<IDtoClassInfo> aModel ) {
    IM5LifecycleManager<IDtoClassInfo> retVal = new M5LifecycleManager<>( aModel, false, true, true, true, null ) {

      private IDtoClassInfo makeDtoClassInfo( IM5Bunch<IDtoClassInfo> aValues ) {
        String id = aValues.getAsAv( FID_CLASS_ID ).asString();
        String parentId = aValues.getAs( FID_PARENT_ID, String.class );
        IOptionSetEdit params = new OptionSet();
        if( aValues.originalEntity() != null ) {
          params.setAll( aValues.originalEntity().params() );
        }
        params.setStr( FID_NAME, aValues.getAsAv( FID_NAME ).asString() );
        params.setStr( FID_DESCRIPTION, aValues.getAsAv( FID_DESCRIPTION ).asString() );
        DtoClassInfo cinf;
        if( id.equals( IGwHardConstants.GW_ROOT_CLASS_ID ) ) {
          cinf = new DtoClassInfo( params );
        }
        else {
          cinf = new DtoClassInfo( id, parentId, params );
        }
        IList<IDtoAttrInfo> attrList = aValues.get( IKM5SdedConstants.FID_SELF_ATTR_INFOS );
        cinf.attrInfos().setAll( attrList );
        IList<IDtoLinkInfo> linkList = aValues.get( IKM5SdedConstants.FID_SELF_LINK_INFOS );
        cinf.linkInfos().setAll( linkList );
        return cinf;
      }

      @Override
      protected IDtoClassInfo doEdit( IM5Bunch<IDtoClassInfo> aValues ) {
        return makeDtoClassInfo( aValues );
      }

    };
    return retVal;
  }

  /**
   * @param aModel M5 модель {@link IDtoObject}
   * @param aListObjs список объектов для верификации пользователем перед реальным созданием в БД сервера
   * @param aItemProvider локальный поставщик списка сущностей
   * @return lm заточенный под редактирование списка сущностей без реального обновления на сервере
   */
  private IM5LifecycleManager<IDtoObject> localLifeCycleManager4DtoObject( IM5Model<IDtoObject> aModel,
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

}
