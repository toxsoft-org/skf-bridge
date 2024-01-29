package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.utils.OpcUaUtils.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import java.io.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.valeds.singlelookup.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tsgui.valed.controls.basic.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.more.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skide.plugin.exconn.service.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * M5 model realization for {@link OpcToS5DataCfgUnit} entities.
 *
 * @author max
 */
public class OpcToS5DataCfgUnitM5Model
    extends M5Model<OpcToS5DataCfgUnit> {

  private String ODS_EXT          = "*.ods";                //$NON-NLS-1$
  private String DEFAULT_PATH_STR = TsLibUtils.EMPTY_STRING;

  public static final String OPCUA_BRIDGE_CFG_S5_CONNECTION = "opcua.bridge.cfg.s5.connection";

  public static final String OPCUA_BRIDGE_CFG_OPC_CONNECTION = "opcua.bridge.cfg.opc.connection";
  public static final String OPCUA_OPC_CONNECTION_CFG        = "opcua.bridge.connection.cfg";

  private final static String CFG_CMD_UNIT_ID_FORMAT = "opctos5.bridge.cfg.cmd.unit.id%d.%s";

  private final static String CFG_DATA_UNIT_ID_FORMAT = "opctos5.bridge.cfg.data.unit.id%d.%s.%s";

  private final static String CFG_RRI_UNIT_ID_FORMAT = "opctos5.bridge.cfg.rri.unit.id%d.%s.%s";

  private final static String CFG_EVENT_UNIT_ID_FORMAT = "opctos5.bridge.cfg.event.unit.id%d.%s.%s";

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.opcua.m5.OpcToS5DataCfgUnitM5Model"; //$NON-NLS-1$

  final static String ACTID_SAVE_DOC = SK_ID + "bridge.cfg.opcua.to.s5.save.doc"; //$NON-NLS-1$

  final static String ACTID_S5_SERVER_SELECT = SK_ID + "bridge.cfg.opcua.to.s5.s5.server.select"; //$NON-NLS-1$

  final static String ACTID_OPC_SERVER_SELECT = SK_ID + "bridge.cfg.opcua.to.s5.opc.server.select"; //$NON-NLS-1$

  final static String ACTID_VALIDATE = SK_ID + "bridge.cfg.opcua.to.s5.validate"; //$NON-NLS-1$

  final static String ACTID_AUTO_LINK = SK_ID + "bridge.cfg.opcua.to.s5.auto.link"; //$NON-NLS-1$

  final static String ACTID_SHOW_NON_VALID = SK_ID + "bridge.cfg.opcua.to.s5.show.non.valid"; //$NON-NLS-1$

  final static String ACTID_GENERATE_FILE = SK_ID + "bridge.cfg.opcua.to.s5.generate.file"; //$NON-NLS-1$

  final static String ACTID_READ_FILE = SK_ID + "bridge.cfg.opcua.to.s5.read.file"; //$NON-NLS-1$

  final static TsActionDef ACDEF_SAVE_DOC =
      TsActionDef.ofPush2( ACTID_SAVE_DOC, STR_N_SAVE_CONFIG, STR_D_SAVE_CONFIG, ICONID_SAVE_DOC );

  final static TsActionDef ACDEF_S5_SERVER_SELECT = TsActionDef.ofPush2( ACTID_S5_SERVER_SELECT, STR_N_SELECT_S5_SERVER,
      STR_D_SELECT_S5_SERVER, ICONID_S5_SERVER_SELECT );

  final static TsActionDef ACDEF_OPC_SERVER_SELECT = TsActionDef.ofPush2( ACTID_OPC_SERVER_SELECT,
      STR_N_SELECT_OPC_UA_SERVER, STR_D_SELECT_OPC_UA_SERVER, ICONID_OPC_SERVER_SELECT );

  final static TsActionDef ACDEF_VALIDATE =
      TsActionDef.ofPush2( ACTID_VALIDATE, STR_N_VALIDATE_CONFIG, STR_D_VALIDATE_CONFIG, ICONID_VALIDATE );

  final static TsActionDef ACDEF_AUTO_LINK =
      TsActionDef.ofPush2( ACTID_AUTO_LINK, STR_N_AUTO_LINK, STR_D_AUTO_LINK, ICONID_AUTO_LINK );

  final static TsActionDef ACDEF_SHOW_NON_VALID = TsActionDef.ofPush2( ACTID_SHOW_NON_VALID, STR_N_SHOW_UNMATCHED_UNITS,
      STR_D_SHOW_UNMATCHED_UNITS, ICONID_SHOW_NON_VALID );

  final static TsActionDef ACDEF_GENERATE_FILE = TsActionDef.ofPush2( ACTID_GENERATE_FILE, STR_N_GENERATE_DLMCFG,
      STR_D_GENERATE_DLMCFG, ICONID_SHOW_GENERATE_DLMCFG );

  final static TsActionDef ACDEF_READ_FILE =
      TsActionDef.ofPush2( ACTID_READ_FILE, STR_N_READ_CONFIG_FILES, STR_D_READ_CONFIG_FILES, ICONID_READ_FILE );

  /**
   * string id of cfg nodes field
   */
  public static final String FID_NODES = "nodes"; //$NON-NLS-1$
  /**
   * string id of cfg unit
   */
  public static final String FID_STRID = "strid"; //$NON-NLS-1$

  /**
   * display name of cfg unit
   */
  public static final String FID_DISPLAY_NAME = "display.name"; //$NON-NLS-1$

  /**
   * Realization options of cfg unit
   */
  public static final String FID_REALIZATION = "realization.opts"; //$NON-NLS-1$

  /**
   * Realization type of cfg unit
   */
  public static final String FID_REALIZATION_TYPE = "realization.type"; //$NON-NLS-1$

  /**
   * string id of cfg gwids field
   */
  public static final String FID_GWIDS = "gwids"; //$NON-NLS-1$

  /**
   * string id of cfg type field
   */
  public static final String FID_TYPE = "type"; //$NON-NLS-1$

  /**
   * Attribute {@link OpcToS5DataCfgUnit#id() } string id
   */
  final M5AttributeFieldDef<OpcToS5DataCfgUnit> STRID = new M5AttributeFieldDef<>( FID_STRID, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_STRID, //
      TSID_DESCRIPTION, STR_D_PARAM_STRID, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_READ_ONLY | M5FF_HIDDEN );
    }

    protected IAtomicValue doGetFieldValue( OpcToS5DataCfgUnit aEntity ) {
      return avStr( aEntity.id() );
    }

  };

  /**
   * Attribute {@link OpcToS5DataCfgUnit#nmName() } display name
   */
  final M5AttributeFieldDef<OpcToS5DataCfgUnit> DISPLAY_NAME =
      new M5AttributeFieldDef<>( FID_DISPLAY_NAME, EAtomicType.STRING, //
          TSID_NAME, STR_N_PARAM_DISPLAY_NAME, //
          TSID_DESCRIPTION, STR_D_PARAM_DISPLAY_NAME, //
          OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( OpcToS5DataCfgUnit aEntity ) {
          return avStr( aEntity.nmName() );
        }

      };

  /**
   * Attribute {@link OpcToS5DataCfgUnit#getDataGwids() } gwids list
   */
  final IM5MultiModownFieldDef<OpcToS5DataCfgUnit, Gwid> GWIDS =
      new M5MultiModownFieldDef<>( FID_GWIDS, GwidsForCfgM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( "Gwids", "Gwids" ); //$NON-NLS-1$ //$NON-NLS-2$
          setFlags( M5FF_COLUMN | M5FF_DETAIL );
          // задаем нормальный размер!
          params().setInt( IValedControlConstants.OPDEF_VERTICAL_SPAN, 5 );
        }

        protected IList<Gwid> doGetFieldValue( OpcToS5DataCfgUnit aEntity ) {
          return aEntity.getDataGwids();
        }

        protected String doGetFieldValueName( OpcToS5DataCfgUnit aEntity ) {
          IList<Gwid> gwids = aEntity.getDataGwids();
          StringBuilder result = new StringBuilder();

          if( gwids.size() > 0 ) {
            result.append( gwids.get( 0 ).asString() );
          }

          if( gwids.size() > 1 ) {
            result.append( ", ..." ); //$NON-NLS-1$
          }

          return result.toString();
        }
      };

  // M5EnumModelBase<Enum<T>>

  /**
   * Attribute {@link OpcToS5DataCfgUnit#getTypeOfCfgUnit() } type
   */
  final M5AttributeFieldDef<OpcToS5DataCfgUnit> TYPE = new M5AttributeFieldDef<>( FID_TYPE, EAtomicType.VALOBJ, //
      TSID_NAME, STR_N_NODE_VALUE_TYPE, //
      TSID_DESCRIPTION, STR_D_NODE_VALUE_TYPE, //
      TSID_KEEPER_ID, ECfgUnitType.KEEPER_ID //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
      setDefaultValue( avValobj( ECfgUnitType.DATA ) );
    }

    protected IAtomicValue doGetFieldValue( OpcToS5DataCfgUnit aEntity ) {
      return avValobj( aEntity.getTypeOfCfgUnit() );
    }
  };

  /**
   * Attribute {@link OpcToS5DataCfgUnit#getDataNodes() } gwids list
   */
  final IM5MultiModownFieldDef<OpcToS5DataCfgUnit, NodeId> NODES =
      new M5MultiModownFieldDef<>( FID_NODES, NodesForCfgM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( "Nodes", "Nodes" ); //$NON-NLS-1$ //$NON-NLS-2$
          setFlags( M5FF_COLUMN | M5FF_DETAIL );
          // задаем нормальный размер!
          params().setInt( IValedControlConstants.OPDEF_VERTICAL_SPAN, 8 );
        }

        protected IList<NodeId> doGetFieldValue( OpcToS5DataCfgUnit aEntity ) {
          return aEntity.getDataNodes();
        }

        protected String doGetFieldValueName( OpcToS5DataCfgUnit aEntity ) {
          IList<NodeId> nodes = aEntity.getDataNodes();
          StringBuilder result = new StringBuilder();

          if( nodes.size() > 0 ) {
            result.append( nodes.get( 0 ).toParseableString() );
          }

          if( nodes.size() > 1 ) {
            result.append( ", ..." ); //$NON-NLS-1$
          }

          return result.toString();
        }
      };

  /**
   * Attribute {@link OpcToS5DataCfgUnit#getRelizationTypeId() } realization options
   */

  final IM5SingleLookupFieldDef<OpcToS5DataCfgUnit, ICfgUnitRealizationType> REALIZATION_TYPE =
      new M5SingleLookupFieldDef<>( FID_REALIZATION_TYPE, CfgUnitRealizationTypeM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_TYPE_REALIZ, STR_D_TYPE_REALIZ );
          setFlags( M5FF_COLUMN );
          CfgUnitRealizationTypeRegister typeReg2 = m5().tsContext().get( CfgUnitRealizationTypeRegister.class );
          setDefaultValue( typeReg2.getTypesOfRealizationForCfgUnitType( ECfgUnitType.DATA ).first() );
          setLookupProvider( () -> {
            CfgUnitRealizationTypeRegister typeReg = m5().tsContext().get( CfgUnitRealizationTypeRegister.class );

            return typeReg.getTypesOfRealizationForCfgUnitType( ECfgUnitType.DATA );
          } );
          setValedEditor( ValedSingleLookupEditor.FACTORY_NAME );
          // setDefaultValue( IOptionSet.NULL );
          params().setBool( IValedControlConstants.OPDEF_IS_WIDTH_FIXED, false );
        }

        @Override
        protected ICfgUnitRealizationType doGetFieldValue( OpcToS5DataCfgUnit aEntity ) {
          CfgUnitRealizationTypeRegister typeReg = m5().tsContext().get( CfgUnitRealizationTypeRegister.class );
          return typeReg.getTypeOfRealizationById( aEntity.getTypeOfCfgUnit(), aEntity.getRelizationTypeId() );
        }
      };

  /**
   * Attribute {@link OpcToS5DataCfgUnit#getRealizationOpts() } realization options
   */
  final IM5FieldDef<OpcToS5DataCfgUnit, IOptionSet> REALIZATION =
      new M5FieldDef<>( FID_REALIZATION, IOptionSet.class ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_REALIZ, STR_D_REALIZ );
          setFlags( M5FF_COLUMN );
          setValedEditor( ValedOptionSet.FACTORY_NAME );
          CfgUnitRealizationTypeRegister typeReg2 = m5().tsContext().get( CfgUnitRealizationTypeRegister.class );
          setDefaultValue(
              typeReg2.getTypesOfRealizationForCfgUnitType( ECfgUnitType.DATA ).first().getDefaultValues() );
          params().setBool( IValedControlConstants.OPDEF_IS_WIDTH_FIXED, false );
        }

        protected IOptionSet doGetFieldValue( OpcToS5DataCfgUnit aEntity ) {
          return aEntity.getRealizationOpts();
        }

        protected String doGetFieldValueName( OpcToS5DataCfgUnit aEntity ) {
          // TODO Сделать внятное отображение реализации
          return TsLibUtils.EMPTY_STRING + aEntity.getRealizationOpts().size();
        }

      };

  /**
   * Constructor.
   */
  public OpcToS5DataCfgUnitM5Model() {
    super( MODEL_ID, OpcToS5DataCfgUnit.class );

    addFieldDefs( STRID, DISPLAY_NAME, TYPE, REALIZATION_TYPE, REALIZATION, GWIDS, NODES );

    setPanelCreator( new M5DefaultPanelCreator<>() {

      class Controller
          extends M5EntityPanelWithValedsController<OpcToS5DataCfgUnit> {

        @Override
        public void beforeSetValues( IM5Bunch<OpcToS5DataCfgUnit> aValues ) {
          IAtomicValue unitTypeVal = (IAtomicValue)aValues.get( FID_TYPE );
          ECfgUnitType unitType = unitTypeVal.asValobj();
          prepareRealizationsComboEditor( unitType );
          ICfgUnitRealizationType realizationType = (ICfgUnitRealizationType)aValues.get( FID_REALIZATION_TYPE );
          prepareValusEditor( realizationType );
          ((GwidsForCfgM5Model)GWIDS.itemModel()).setCfgUnitType( unitType );
        }

        @Override
        public boolean doProcessEditorValueChange( IValedControl<?> aEditor,
            IM5FieldDef<OpcToS5DataCfgUnit, ?> aFieldDef, boolean aEditFinished ) {
          switch( aFieldDef.id() ) {
            case FID_REALIZATION_TYPE:
              // when changing the provider, change the value editor
              ICfgUnitRealizationType realizationType =
                  (ICfgUnitRealizationType)editors().getByKey( FID_REALIZATION_TYPE ).getValue();
              prepareValusEditor( realizationType );
              // we will try to use the available value as much as possible
              // ValedOptionSet vops = getEditor( FID_REALIZATION, ValedOptionSet.class );
              // vops.setValue( lastValues().getAs( FID_REALIZATION, IOptionSet.class ) );
              break;
            case FID_TYPE:
              IAtomicValue unitTypeVal = (IAtomicValue)editors().getByKey( FID_TYPE ).getValue();
              ECfgUnitType unitType = unitTypeVal.asValobj();
              prepareRealizationsComboEditor( unitType );

              ValedSingleLookupComboEditor<ICfgUnitRealizationType> lEditor =
                  getEditor( FID_REALIZATION_TYPE, ValedSingleLookupComboEditor.class );

              // ValedMultiModownTableEditor<Gwid> gwidsEditor = getEditor( FID_GWIDS, ValedMultiModownTableEditor.class
              // );

              ((GwidsForCfgM5Model)GWIDS.itemModel()).setCfgUnitType( unitType );

              // gwidsEditor.

              CfgUnitRealizationTypeRegister typeReg2 = m5().tsContext().get( CfgUnitRealizationTypeRegister.class );
              ICfgUnitRealizationType realizationType2 =
                  typeReg2.getTypesOfRealizationForCfgUnitType( unitType ).first();
              lEditor.setValue( realizationType2 );

              prepareValusEditor( realizationType2 );
              break;
            default:
              break;
          }
          return true;
        }

        private void prepareValusEditor( ICfgUnitRealizationType aRealizationType ) {
          ValedOptionSet vops = getEditor( FID_REALIZATION, ValedOptionSet.class );
          vops.setOptionDefs( aRealizationType.paramDefenitions() );
          vops.setValue( aRealizationType.getDefaultValues() );
        }

        private void prepareRealizationsComboEditor( ECfgUnitType aUnitType ) {
          ValedSingleLookupComboEditor<ICfgUnitRealizationType> lEditor =
              getEditor( FID_REALIZATION_TYPE, ValedSingleLookupComboEditor.class );
          lEditor.setLookupProvider( () -> {
            CfgUnitRealizationTypeRegister typeReg = m5().tsContext().get( CfgUnitRealizationTypeRegister.class );

            return typeReg.getTypesOfRealizationForCfgUnitType( aUnitType );
          } );
        }

      }

      @Override
      protected IM5EntityPanel<OpcToS5DataCfgUnit> doCreateEntityEditorPanel( ITsGuiContext aContext,
          IM5LifecycleManager<OpcToS5DataCfgUnit> aLifecycleManager ) {
        return new M5DefaultEntityControlledPanel<>( aContext, model(), aLifecycleManager, new Controller() );
      }

      protected IM5CollectionPanel<OpcToS5DataCfgUnit> doCreateCollEditPanel( ITsGuiContext aContext,
          IM5ItemsProvider<OpcToS5DataCfgUnit> aItemsProvider,
          IM5LifecycleManager<OpcToS5DataCfgUnit> aLifecycleManager ) {
        MultiPaneComponentModown<OpcToS5DataCfgUnit> mpc =
            new MultiPaneComponentModown<>( aContext, model(), aItemsProvider, aLifecycleManager ) {

              @Override
              protected ITsToolbar doCreateToolbar( ITsGuiContext aaContext, String aName, EIconSize aIconSize,
                  IListEdit<ITsActionDef> aActs ) {
                aActs.add( ACDEF_SEPARATOR );
                // aActs.add( ACDEF_SAVE_DOC );
                // aActs.add( ACDEF_S5_SERVER_SELECT );
                // aActs.add( ACDEF_OPC_SERVER_SELECT );
                // aActs.add( ACDEF_VALIDATE );
                aActs.add( ACDEF_AUTO_LINK );
                // aActs.add( ACDEF_SHOW_NON_VALID );
                aActs.add( ACDEF_GENERATE_FILE );
                // aActs.add( ACDEF_READ_FILE );

                ITsToolbar toolbar =

                    super.doCreateToolbar( aaContext, aName, aIconSize, aActs );

                toolbar.addListener( aActionId -> {
                  // nop

                } );

                toolbar.setIconSize( EIconSize.IS_24X24 );
                return toolbar;
              }

              @Override
              protected void doProcessAction( String aActionId ) {

                switch( aActionId ) {
                  case ACTID_SAVE_DOC:
                    ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).saveCurrState( tsContext() );
                    break;

                  case ACTID_S5_SERVER_SELECT:
                    // dima 20.10.23 FIXME использовать IdChain для передачи информации о соединении. Цитата от Гоги:
                    // Кстати, напомню, что "класть соединение в контекст" нельзя,
                    // можно только использовать ISkConnectionSupplier
                    // IdChain connIdChain = selectConnection( aContext );
                    // if( connIdChain != null ) {
                    // aContext.put( OPCUA_BRIDGE_CFG_S5_CONNECTION, connIdChain );
                    // }

                    break;

                  case ACTID_OPC_SERVER_SELECT:
                    OpcUaClient selectedOpcConnection = selectOpcConfigAndOpenConnection( aContext );
                    if( selectedOpcConnection != null ) {
                      aContext.put( OPCUA_BRIDGE_CFG_OPC_CONNECTION, selectedOpcConnection );
                    }

                    break;

                  case ACTID_GENERATE_FILE:
                    ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).generateFileFromCurrState( aContext );
                    break;

                  case ACTID_AUTO_LINK:

                    // вынести в отделный класс реализации
                    IdChain connIdChain =
                        (IdChain)tsContext().find( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_S5_CONNECTION );

                    IStringMap<IStringMap<Integer>> cmdOpcCodes = new StringMap<>();

                    String cmdFileDescr = getDescrFile(
                        org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.SELECT_FILE_4_IMPORT_CMD );
                    if( cmdFileDescr != null ) {
                      File file = new File( cmdFileDescr );
                      try {
                        cmdOpcCodes = Ods2DtoCmdInfoParser.parseOpcCmdCodes( file );
                        TsDialogUtils.info( getShell(), MSG_LOADED_CMDS_DESCR, cmdFileDescr );
                      }
                      catch( IOException ex ) {
                        LoggerUtils.errorLogger().error( ex );
                      }
                    }

                    StringMap<StringMap<IList<BitIdx2DtoRtData>>> clsId2RtDataInfoes = new StringMap<>();
                    StringMap<StringMap<IList<BitIdx2RriDtoAttr>>> clsId2RriAttrInfoes = new StringMap<>();
                    StringMap<StringMap<IList<BitIdx2DtoEvent>>> clsId2EventInfoes = new StringMap<>();

                    String bitRtdataFileDescr = getDescrFile(
                        org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.SELECT_FILE_4_IMPORT_BIT_RTDATA );
                    if( bitRtdataFileDescr != null ) {
                      File file = new File( bitRtdataFileDescr );
                      try {
                        Ods2DtoRtDataInfoParser.parse( file );
                        clsId2RtDataInfoes = Ods2DtoRtDataInfoParser.getRtdataInfoesMap();
                        clsId2EventInfoes = Ods2DtoRtDataInfoParser.getEventInfoesMap();
                        clsId2RriAttrInfoes = Ods2DtoRtDataInfoParser.getRriAttrInfoesMap();
                        TsDialogUtils.info( getShell(), MSG_LOADED_BIT_MASKS_DESCR, bitRtdataFileDescr );
                      }
                      catch( IOException ex ) {
                        LoggerUtils.errorLogger().error( ex );
                      }
                    }

                    // Commands
                    // dima
                    OpcUaServerConnCfg conConf =
                        (OpcUaServerConnCfg)aContext.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );
                    if( conConf == null ) {
                      selectOpcConfigAndOpenConnection( aContext );
                      conConf = (OpcUaServerConnCfg)aContext.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );
                    }
                    IList<CmdGwid2UaNodes> autoElements = OpcUaUtils.loadCmdGwid2Nodes( aContext, conConf );
                    System.out.println( "Auto elements size = " + autoElements.size() ); //$NON-NLS-1$
                    for( CmdGwid2UaNodes cmd2Nodes : autoElements ) {

                      IList<Gwid> gwids = new ElemArrayList<>( cmd2Nodes.gwid() );
                      String cmdArgParam = null;
                      IListEdit<NodeId> nodes = new ElemArrayList<>();
                      nodes.add( cmd2Nodes.getNodeCmdId() );
                      if( cmd2Nodes.getNodeCmdArgInt() != null ) {
                        nodes.add( cmd2Nodes.getNodeCmdArgInt() );
                        cmdArgParam = Ods2DtoCmdInfoParser.CMD_ARG_INT_ID;
                      }
                      else
                        if( cmd2Nodes.getNodeCmdArgFlt() != null ) {
                          nodes.add( cmd2Nodes.getNodeCmdArgFlt() );
                          cmdArgParam = Ods2DtoCmdInfoParser.CMD_ARG_FLT_ID;
                        }
                      nodes.add( cmd2Nodes.getNodeCmdFeedback() );

                      String strid = String.format( CFG_CMD_UNIT_ID_FORMAT, Long.valueOf( System.currentTimeMillis() ),
                          cmd2Nodes.gwid().strid() );
                      ECfgUnitType type = ECfgUnitType.COMMAND;

                      CfgUnitRealizationTypeRegister typeReg2 =
                          m5().tsContext().get( CfgUnitRealizationTypeRegister.class );

                      ICfgUnitRealizationType realType =
                          typeReg2.getTypeOfRealizationById( type, CFG_UNIT_REALIZATION_TYPE_VALUE_COMMAND_BY_ONE_TAG );
                      OptionSet realization = new OptionSet();
                      OpcUaUtils.OP_CMD_JAVA_CLASS.setValue( realization,
                          avStr( COMMANDS_JAVA_CLASS_VALUE_COMMAND_BY_ONE_TAG_EXEC ) );
                      if( cmdArgParam != null ) {
                        OpcUaUtils.OP_CMD_VALUE_PARAM_ID.setValue( realization, avStr( cmdArgParam ) );
                      }
                      int cmdOpcCode = 1;

                      if( cmdOpcCodes.hasKey( cmd2Nodes.gwid().classId() ) ) {
                        IStringMap<Integer> classCodes = cmdOpcCodes.getByKey( cmd2Nodes.gwid().classId() );

                        if( classCodes.hasKey( cmd2Nodes.gwid().propId() ) ) {
                          cmdOpcCode = classCodes.getByKey( cmd2Nodes.gwid().propId() ).intValue();
                        }
                      }

                      OpcUaUtils.OP_CMD_OPC_ID.setValue( realization, avInt( cmdOpcCode ) );

                      String name = STR_LINK_PREFIX + cmd2Nodes.gwid().asString();

                      OpcToS5DataCfgUnit result = new OpcToS5DataCfgUnit( strid, name );
                      result.setDataNodes( nodes );
                      result.setDataGwids( gwids );
                      result.setTypeOfCfgUnit( type );
                      result.setRelizationTypeId( realType.id() );
                      result.setRealizationOpts( realization );

                      ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).addCfgUnit( result );
                    }
                    // Data
                    IList<UaNode2Gwid> nodes2Gwids = OpcUaUtils.loadNodes2RtdGwids( aContext, conConf );
                    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
                    ISkConnection currConn = connSup.getConn( connIdChain );
                    for( UaNode2Gwid dataNode : nodes2Gwids ) {

                      // битовый индекс для данного
                      BitIdx2DtoRtData bitIndex =
                          OpcUaUtils.getDataBitIndexForRtDataGwid( dataNode.gwid(), clsId2RtDataInfoes );

                      Gwid gwid = dataNode.gwid();
                      IList<Gwid> gwids = new ElemArrayList<>( gwid );

                      IListEdit<NodeId> nodes = new ElemArrayList<>( dataNode.getNodeId() );

                      String strid = String.format( CFG_DATA_UNIT_ID_FORMAT, Long.valueOf( System.currentTimeMillis() ),
                          gwid.strid(), gwid.propId() );

                      ECfgUnitType type = ECfgUnitType.DATA;

                      CfgUnitRealizationTypeRegister typeReg2 =
                          m5().tsContext().get( CfgUnitRealizationTypeRegister.class );

                      ICfgUnitRealizationType realType = typeReg2.getTypeOfRealizationById( type,
                          bitIndex == null ? CFG_UNIT_REALIZATION_TYPE_ONT_TO_ONE_DATA
                              : CFG_UNIT_REALIZATION_TYPE_ONE_INT_TO_ONE_BIT_DATA );

                      OptionSet realization = new OptionSet( realType.getDefaultValues() );
                      if( bitIndex != null ) {
                        OpcUaUtils.OP_BIT_INDEX.setValue( realization, avInt( bitIndex.bitIndex() ) );
                      }

                      ISkCoreApi api = currConn.coreApi();
                      ISkSysdescr sysDescr = api.sysdescr();
                      ISkClassInfo classInfo = sysDescr.getClassInfo( gwid.classId() );
                      ISkClassProps<IDtoRtdataInfo> dataInfoes = classInfo.rtdata();
                      IDtoRtdataInfo dataInfo = dataInfoes.list().getByKey( gwid.propId() );

                      OpcUaUtils.OP_SYNCH_PERIOD.setValue( realization, avInt( dataInfo.isSync() ? 1000 : 0 ) );
                      OpcUaUtils.OP_IS_CURR.setValue( realization, avBool( dataInfo.isCurr() ) );
                      OpcUaUtils.OP_IS_HIST.setValue( realization, avBool( dataInfo.isHist() ) );

                      String name = STR_LINK_PREFIX + gwid.asString();

                      OpcToS5DataCfgUnit result = new OpcToS5DataCfgUnit( strid, name );
                      result.setDataNodes( nodes );
                      result.setDataGwids( gwids );
                      result.setTypeOfCfgUnit( type );
                      result.setRelizationTypeId( realType.id() );
                      result.setRealizationOpts( realization );

                      ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).addCfgUnit( result );
                    }

                    // dima 18.01.24 RRI attrs
                    nodes2Gwids = OpcUaUtils.loadNodes2RriGwids( aContext, conConf );
                    for( UaNode2Gwid rriAttrNode : nodes2Gwids ) {

                      // битовый индекс для rriAttr
                      BitIdx2RriDtoAttr bitIndex =
                          OpcUaUtils.getDataBitIndexForRriAttrGwid( rriAttrNode.gwid(), clsId2RriAttrInfoes );

                      Gwid gwid = rriAttrNode.gwid();
                      IList<Gwid> gwids = new ElemArrayList<>( gwid );

                      IListEdit<NodeId> nodes = new ElemArrayList<>( rriAttrNode.getNodeId() );

                      String strid = String.format( CFG_RRI_UNIT_ID_FORMAT, Long.valueOf( System.currentTimeMillis() ),
                          gwid.strid(), gwid.propId() );

                      ECfgUnitType type = ECfgUnitType.RRI;

                      CfgUnitRealizationTypeRegister typeReg2 =
                          m5().tsContext().get( CfgUnitRealizationTypeRegister.class );

                      ICfgUnitRealizationType realType = typeReg2.getTypeOfRealizationById( type,
                          bitIndex == null ? CFG_UNIT_REALIZATION_TYPE_ONE_TO_ONE_RRI
                              : CFG_UNIT_REALIZATION_TYPE_ONE_INT_TO_ONE_BIT_RRI );

                      OptionSet realization = new OptionSet( realType.getDefaultValues() );
                      if( bitIndex != null ) {
                        OpcUaUtils.OP_BIT_INDEX.setValue( realization, avInt( bitIndex.bitIndex() ) );
                      }

                      String name = STR_LINK_PREFIX + gwid.asString();

                      OpcToS5DataCfgUnit result = new OpcToS5DataCfgUnit( strid, name );
                      result.setDataNodes( nodes );
                      result.setDataGwids( gwids );
                      result.setTypeOfCfgUnit( type );
                      result.setRelizationTypeId( realType.id() );
                      result.setRealizationOpts( realization );

                      ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).addCfgUnit( result );
                    }

                    // events
                    IList<UaNode2EventGwid> autoEvents = OpcUaUtils.loadNodes2EvtGwids( aContext, conConf );

                    for( UaNode2Gwid evtNode : autoEvents ) {
                      Gwid gwid = evtNode.gwid();

                      BitIdx2DtoEvent bitIndex = OpcUaUtils.getBitIndexForEvtGwid( gwid, clsId2EventInfoes );

                      IList<Gwid> gwids = new ElemArrayList<>( gwid );

                      IListEdit<NodeId> nodes = new ElemArrayList<>( evtNode.getNodeId() );

                      String strid = String.format( CFG_EVENT_UNIT_ID_FORMAT,
                          Long.valueOf( System.currentTimeMillis() ), gwid.strid(), gwid.propId() );

                      ECfgUnitType type = ECfgUnitType.EVENT;

                      CfgUnitRealizationTypeRegister typeReg2 =
                          m5().tsContext().get( CfgUnitRealizationTypeRegister.class );

                      ICfgUnitRealizationType realType = typeReg2.getTypeOfRealizationById( type,
                          bitIndex == null ? OpcUaUtils.CFG_UNIT_REALIZATION_TYPE_TAG_VALUE_CHANGED
                              : OpcUaUtils.CFG_UNIT_REALIZATION_TYPE_BIT_SWITCH_EVENT );

                      OptionSet realization = new OptionSet( realType.getDefaultValues() );

                      if( bitIndex != null ) {
                        int index = bitIndex.bitIndex();
                        boolean genUp = bitIndex.isGenerateUp();
                        boolean genDn = bitIndex.isGenerateDn();

                        OpcUaUtils.OP_BIT_INDEX.setValue( realization, avInt( index ) );
                        OpcUaUtils.OP_CONDITION_SWITCH_ON.setValue( realization, avBool( genUp ) );
                        OpcUaUtils.OP_CONDITION_SWITCH_OFF.setValue( realization, avBool( genDn ) );
                      }

                      String name = STR_LINK_PREFIX + gwid.asString();

                      OpcToS5DataCfgUnit result = new OpcToS5DataCfgUnit( strid, name );
                      result.setDataNodes( nodes );
                      result.setDataGwids( gwids );
                      result.setTypeOfCfgUnit( type );
                      result.setRelizationTypeId( realType.id() );
                      result.setRealizationOpts( realization );

                      ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).addCfgUnit( result );
                    }

                    doFillTree();
                    break;

                  default:
                    throw new TsNotAllEnumsUsedRtException( aActionId );
                }
              }

              @Override
              protected OpcToS5DataCfgUnit doAddItem() {
                return super.doAddItem();
              }

              @Override
              protected OpcToS5DataCfgUnit doEditItem( OpcToS5DataCfgUnit aItem ) {
                return super.doEditItem( aItem );
              }

              @Override
              protected boolean doRemoveItem( OpcToS5DataCfgUnit aItem ) {
                return super.doRemoveItem( aItem );
              }

            };
        return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
      }
    } );
  }

  IdChain selectConnection( ITsGuiContext aContext ) {
    ISkideExternalConnectionsService connService =
        aContext.eclipseContext().get( ISkideExternalConnectionsService.class );
    IdChain idChain = connService.selectConfigAndOpenConnection( aContext );

    return idChain;
  }

  /**
   * Выбор и подключение к OPC UA серверу
   *
   * @param aContext контекст приложения
   * @return подключение
   */
  public OpcUaClient selectOpcConfigAndOpenConnection( ITsGuiContext aContext ) {
    IOpcUaServerConnCfg conConf = OpcUaUtils.selectOpcServerConfig( aContext );
    // dima 13.10.23 сохраним в контекст
    aContext.put( OPCUA_OPC_CONNECTION_CFG, conConf );
    if( conConf == null ) {
      return null;
    }

    try {
      OpcUaClient client = OpcUaUtils.createClient( conConf );
      client.connect().get();
      return client;
    }
    catch( Exception ex ) {
      LoggerUtils.errorLogger().error( ex );
      return null;
    }

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

}
