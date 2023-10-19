package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skide.plugin.exconn.ISkidePluginExconnSharedResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;
//import static org.toxsoft.uskat.core.gui.conn.cfg.m5.IConnectionConfigM5Constants.*;

import java.io.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
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
import org.toxsoft.core.tslib.bricks.apprefs.*;
import org.toxsoft.core.tslib.bricks.apprefs.impl.*;
import org.toxsoft.core.tslib.bricks.strid.more.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skf.bridge.cfg.opcua.service.impl.*;
import org.toxsoft.skide.plugin.exconn.service.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * M5 model realization for {@link OpcToS5DataCfgUnit} entities.
 *
 * @author max
 */
public class OpcToS5DataCfgUnitM5Model
    extends M5Model<OpcToS5DataCfgUnit> {

  public static final String OPCUA_BRIDGE_CFG_S5_CONNECTION = "opcua.bridge.cfg.s5.connection";

  public static final String OPCUA_BRIDGE_CFG_OPC_CONNECTION = "opcua.bridge.cfg.opc.connection";
  public static final String OPCUA_OPC_CONNECTION_CFG        = "opcua.bridge.connection.cfg";

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

  final static TsActionDef ACDEF_SAVE_DOC = TsActionDef.ofPush2( ACTID_SAVE_DOC, "Сохранить конфигурацию",
      "Сохранить конфигурацию в SKIDE", ICONID_SAVE_DOC );

  final static TsActionDef ACDEF_S5_SERVER_SELECT =
      TsActionDef.ofPush2( ACTID_S5_SERVER_SELECT, "Выбрать S5 сервер", "Выбрать S5 сервер", ICONID_S5_SERVER_SELECT );

  final static TsActionDef ACDEF_OPC_SERVER_SELECT = TsActionDef.ofPush2( ACTID_OPC_SERVER_SELECT,
      "Выбрать OPC UA сервер", "Выбрать OPC UA сервер", ICONID_OPC_SERVER_SELECT );

  final static TsActionDef ACDEF_VALIDATE = TsActionDef.ofPush2( ACTID_VALIDATE, "Проверить конфигурацию",
      "Проверить конфигурацию на несоответствие gwid и nodeId имеющимся на выбранных серверах", ICONID_VALIDATE );

  final static TsActionDef ACDEF_AUTO_LINK = TsActionDef.ofPush2( ACTID_AUTO_LINK, "Автоматическое связывание",
      "Автоматическое связывание", ICONID_AUTO_LINK );

  final static TsActionDef ACDEF_SHOW_NON_VALID =
      TsActionDef.ofPush2( ACTID_SHOW_NON_VALID, "Показать несоответствующие единицы конфигурации",
          "Показать несоответствующие единицы конфигурации", ICONID_SHOW_NON_VALID );

  final static TsActionDef ACDEF_GENERATE_FILE = TsActionDef.ofPush2( ACTID_GENERATE_FILE,
      "Сгенерировать файлы конфигурации", "Сгенерировать файлы конфигурации", ICONID_SHOW_GENERATE_DLMCFG );

  final static TsActionDef ACDEF_READ_FILE = TsActionDef.ofPush2( ACTID_READ_FILE, "Прочитать файлы конфигурации",
      "Прочитать файлы конфигурации", ICONID_READ_FILE );

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
  static M5AttributeFieldDef<OpcToS5DataCfgUnit> STRID = new M5AttributeFieldDef<>( FID_STRID, EAtomicType.STRING, //
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
  static M5AttributeFieldDef<OpcToS5DataCfgUnit> DISPLAY_NAME =
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
  static IM5MultiModownFieldDef<OpcToS5DataCfgUnit, Gwid> GWIDS =
      new M5MultiModownFieldDef<>( FID_GWIDS, GwidsForCfgM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( "Gwids", "Gwids" );
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
  static M5AttributeFieldDef<OpcToS5DataCfgUnit> TYPE = new M5AttributeFieldDef<>( FID_TYPE, EAtomicType.VALOBJ, //
      TSID_NAME, "Тип", //
      TSID_DESCRIPTION, "Тип", //
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
  static IM5MultiModownFieldDef<OpcToS5DataCfgUnit, NodeId> NODES =
      new M5MultiModownFieldDef<>( FID_NODES, NodesForCfgM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( "Nodes", "Nodes" );
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

  static IM5SingleLookupFieldDef<OpcToS5DataCfgUnit, ICfgUnitRealizationType> REALIZATION_TYPE =
      new M5SingleLookupFieldDef<>( FID_REALIZATION_TYPE, CfgUnitRealizationTypeM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( "Тип реализации", "Тип реализации" );
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
  static IM5FieldDef<OpcToS5DataCfgUnit, IOptionSet> REALIZATION =
      new M5FieldDef<>( FID_REALIZATION, IOptionSet.class ) {

        @Override
        protected void doInit() {
          setNameAndDescription( "Реализация", "Реализация" );
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
          return "" + aEntity.getRealizationOpts().size();
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
              protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
                  IListEdit<ITsActionDef> aActs ) {
                aActs.add( ACDEF_SEPARATOR );
                aActs.add( ACDEF_SAVE_DOC );
                aActs.add( ACDEF_S5_SERVER_SELECT );
                aActs.add( ACDEF_OPC_SERVER_SELECT );
                aActs.add( ACDEF_VALIDATE );
                aActs.add( ACDEF_AUTO_LINK );
                aActs.add( ACDEF_SHOW_NON_VALID );
                aActs.add( ACDEF_GENERATE_FILE );
                aActs.add( ACDEF_READ_FILE );

                ITsToolbar toolbar =

                    super.doCreateToolbar( aContext, aName, aIconSize, aActs );

                toolbar.addListener( aActionId -> {
                  // nop

                } );

                toolbar.setIconSize( EIconSize.IS_48X48 );
                return toolbar;
              }

              @Override
              protected void doProcessAction( String aActionId ) {

                switch( aActionId ) {
                  case ACTID_SAVE_DOC:
                    ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).saveCurrState( tsContext() );
                    break;

                  case ACTID_S5_SERVER_SELECT:
                    ISkConnection selectedConnection = selectConnection( aContext );
                    if( selectedConnection != null ) {
                      aContext.put( OPCUA_BRIDGE_CFG_S5_CONNECTION, selectedConnection );
                    }

                    break;

                  case ACTID_OPC_SERVER_SELECT:
                    OpcUaClient selectedOpcConnection = selectOpcConfigAndOpenConnection( aContext );
                    if( selectedOpcConnection != null ) {
                      aContext.put( OPCUA_BRIDGE_CFG_OPC_CONNECTION, selectedOpcConnection );
                    }

                    break;

                  case ACTID_GENERATE_FILE:
                    ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).generateFileFromCurrState();
                    break;

                  case ACTID_AUTO_LINK:

                    IStringMap<IStringMap<Integer>> cmdOpcCodes = new StringMap<>();

                    String cmdFileDescr = getCmdDescrFile();
                    if( cmdFileDescr != null ) {
                      File file = new File( cmdFileDescr );
                      try {
                        cmdOpcCodes = Ods2DtoCmdInfoParser.parseOpcCmdCodes( file );
                        // TsDialogUtils.info( getShell(), "Loaded command description from file: %s", cmdFileDescr );
                      }
                      catch( IOException ex ) {
                        LoggerUtils.errorLogger().error( ex );
                      }
                    }

                    // Commands
                    IList<CmdGwid2UaNodes> autoElements = OpcUaUtils.loadCmdGwid2Nodes( aContext );
                    System.out.println( "Auto elements size = " + autoElements.size() );
                    for( CmdGwid2UaNodes cmd2Nodes : autoElements ) {

                      IList<Gwid> gwids = new ElemArrayList<>( cmd2Nodes.gwid() );
                      String cmdArgParam = null;
                      IListEdit<NodeId> nodes = new ElemArrayList<>();
                      nodes.add( cmd2Nodes.getNodeCmdId() );
                      if( cmd2Nodes.getNodeCmdArgInt() != null ) {
                        nodes.add( cmd2Nodes.getNodeCmdArgInt() );
                        cmdArgParam = "argInt";
                      }
                      else
                        if( cmd2Nodes.getNodeCmdArgFlt() != null ) {
                          nodes.add( cmd2Nodes.getNodeCmdArgFlt() );
                          cmdArgParam = "argFlt";
                        }
                      nodes.add( cmd2Nodes.getNodeCmdFeedback() );

                      String strid = "opctos5.bridge.cfg.cmd.unit.id" + System.currentTimeMillis() + "."
                          + cmd2Nodes.gwid().strid();// OpcToS5DataCfgUnitM5Model.STRID.getFieldValue(
                      ECfgUnitType type = ECfgUnitType.COMMAND;

                      CfgUnitRealizationTypeRegister typeReg2 =
                          m5().tsContext().get( CfgUnitRealizationTypeRegister.class );

                      ICfgUnitRealizationType realType =
                          typeReg2.getTypeOfRealizationById( type, "val.command.one.tag" );
                      OptionSet realization = new OptionSet();
                      OpcUaUtils.OP_CMD_JAVA_CLASS.setValue( realization,
                          avStr( "ru.toxsoft.l2.dlm.opc_bridge.submodules.commands.ValCommandByOneTagWithParamExec" ) );
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

                      String name = "generated for " + cmd2Nodes.gwid().asString();

                      OpcToS5DataCfgUnit result = new OpcToS5DataCfgUnit( strid, name );
                      result.setDataNodes( nodes );
                      result.setDataGwids( gwids );
                      result.setTypeOfCfgUnit( type );
                      result.setRelizationTypeId( realType.id() );
                      result.setRealizationOpts( realization );

                      ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).addCfgUnit( result );
                      // master().addDataUnit( result );
                    }
                    // Data
                    IList<UaNode2RtdGwid> nodes2Gwids = OpcUaUtils.loadNodes2Gwids( aContext );
                    for( UaNode2RtdGwid dataNode : nodes2Gwids ) {

                      IList<Gwid> gwids = new ElemArrayList<>( dataNode.gwid() );

                      IListEdit<NodeId> nodes = new ElemArrayList<>( dataNode.getNodeId() );

                      String strid = "opctos5.bridge.cfg.data.unit.id" + System.currentTimeMillis() + "."
                          + dataNode.gwid().strid();// OpcToS5DataCfgUnitM5Model.STRID.getFieldValue(
                      ECfgUnitType type = ECfgUnitType.DATA;

                      CfgUnitRealizationTypeRegister typeReg2 =
                          m5().tsContext().get( CfgUnitRealizationTypeRegister.class );

                      ICfgUnitRealizationType realType = typeReg2.getTypeOfRealizationById( type,
                          OpcUaUtils.CFG_UNIT_REALIZATION_TYPE_ONT_TO_ONE_DATA );
                      OptionSet realization = new OptionSet();
                      OpcUaUtils.OP_CMD_JAVA_CLASS.setValue( realization,
                          avStr( "ru.toxsoft.l2.dlm.opc_bridge.submodules.data.OneToOneDataTransmitterFactory" ) );

                      String name = "generated for " + dataNode.gwid().asString();

                      OpcToS5DataCfgUnit result = new OpcToS5DataCfgUnit( strid, name );
                      result.setDataNodes( nodes );
                      result.setDataGwids( gwids );
                      result.setTypeOfCfgUnit( type );
                      result.setRelizationTypeId( realType.id() );
                      result.setRealizationOpts( realization );

                      ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).addCfgUnit( result );
                      // master().addDataUnit( result );
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

  ISkConnection selectConnection( ITsGuiContext aContext ) {
    ISkideExternalConnectionsService connService =
        aContext.eclipseContext().get( ISkideExternalConnectionsService.class );
    IdChain idChain = connService.selectConfigAndOpenConnection( aContext );
    if( idChain == null ) {
      return null;
    }
    ISkConnectionSupplier connSupp = aContext.eclipseContext().get( ISkConnectionSupplier.class );
    return connSupp.allConns().getByKey( idChain );
  }

  public OpcUaClient selectOpcConfigAndOpenConnection( ITsGuiContext aContext ) {
    TsNullArgumentRtException.checkNull( aContext );

    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    ISkConnection conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    // занесем параметры из файла в контекст
    AbstractAppPreferencesStorage apStorage =
        new AppPreferencesConfigIniStorage( new File( OPC_UA_SERVER_CONN_CFG_STORE_FILE ) );
    IAppPreferences appPreferences = new AppPreferences( apStorage );
    OpcUaServerConnCfgService cfgService = new OpcUaServerConnCfgService( appPreferences );

    IM5Model<IOpcUaServerConnCfg> model = m5.getModel( OpcUaServerConnCfgModel.MODEL_ID, IOpcUaServerConnCfg.class );

    IM5LifecycleManager<IOpcUaServerConnCfg> lm = new OpcUaServerConnCfgM5LifecycleManager( model, cfgService );

    TsDialogInfo di = new TsDialogInfo( aContext, DLG_SELECT_CFG_AND_OPEN, DLG_SELECT_CFG_AND_OPEN_D );

    IOpcUaServerConnCfg conConf = M5GuiUtils.askSelectItem( di, model, null, lm.itemsProvider(), lm );
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

  String         SELECT_FILE_4_IMPORT_CMD = "Выберите файл с описанием команд";
  private String ODS_EXT                  = "*.ods";                           //$NON-NLS-1$
  private String DEFAULT_PATH_STR         = "";

  private String getCmdDescrFile() {
    FileDialog fd = new FileDialog( getShell(), SWT.OPEN );
    fd.setText( SELECT_FILE_4_IMPORT_CMD );
    fd.setFilterPath( DEFAULT_PATH_STR );
    String[] filterExt = { ODS_EXT };
    fd.setFilterExtensions( filterExt );
    String selected = fd.open();
    return selected;
  }

}
