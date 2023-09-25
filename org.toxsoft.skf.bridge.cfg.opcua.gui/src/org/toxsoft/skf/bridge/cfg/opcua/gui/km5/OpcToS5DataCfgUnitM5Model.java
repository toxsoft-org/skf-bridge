package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skide.plugin.exconn.ISkidePluginExconnSharedResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import java.io.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
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
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.bricks.apprefs.*;
import org.toxsoft.core.tslib.bricks.apprefs.impl.*;
import org.toxsoft.core.tslib.bricks.strid.more.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
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
      "Сохранить конфигурацию в SKIDE", ICONID_DOCUMENT_SAVE );

  final static TsActionDef ACDEF_S5_SERVER_SELECT =
      TsActionDef.ofPush2( ACTID_S5_SERVER_SELECT, "Выбрать S5 сервер", "Выбрать S5 сервер", ICONID_EDIT_FIND );

  final static TsActionDef ACDEF_OPC_SERVER_SELECT = TsActionDef.ofPush2( ACTID_OPC_SERVER_SELECT,
      "Выбрать OPC UA сервер", "Выбрать OPC UA сервер", ICONID_EDIT_FIND );

  final static TsActionDef ACDEF_VALIDATE = TsActionDef.ofPush2( ACTID_VALIDATE, "Проверить конфигурацию",
      "Проверить конфигурацию на несоответствие gwid и nodeId имеющимся на выбранных серверах", ICONID_VIEW_FILTER );

  final static TsActionDef ACDEF_AUTO_LINK = TsActionDef.ofPush2( ACTID_AUTO_LINK, "Автоматическое связывание",
      "Автоматическое связывание", ICONID_DOCUMENT_REVERT );

  final static TsActionDef ACDEF_SHOW_NON_VALID =
      TsActionDef.ofPush2( ACTID_SHOW_NON_VALID, "Показать несоответствующие единицы конфигурации",
          "Показать несоответствующие единицы конфигурации", ICONID_HIDE_FILTER_PANE );

  final static TsActionDef ACDEF_GENERATE_FILE = TsActionDef.ofPush2( ACTID_GENERATE_FILE,
      "Сгенерировать файлы конфигурации", "Сгенерировать файлы конфигурации", ICONID_DOCUMENT_NEW );

  final static TsActionDef ACDEF_READ_FILE = TsActionDef.ofPush2( ACTID_READ_FILE, "Прочитать файлы конфигурации",
      "Прочитать файлы конфигурации", ICONID_DOCUMENT_OPEN );

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
      setFlags( M5FF_READ_ONLY );
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
   * Attribute {@link OpcToS5DataCfgUnit#getTypeOfDataCfg() } type
   */
  static M5AttributeFieldDef<OpcToS5DataCfgUnit> TYPE = new M5AttributeFieldDef<>( FID_TYPE, EAtomicType.VALOBJ, //
      TSID_NAME, "Реализация", //
      TSID_DESCRIPTION, "Реализация", //
      TSID_KEEPER_ID, EDataCfgType.KEEPER_ID //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( OpcToS5DataCfgUnit aEntity ) {
      return avValobj( aEntity.getTypeOfDataCfg() );
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
   * Constructor.
   */
  public OpcToS5DataCfgUnitM5Model() {
    super( MODEL_ID, OpcToS5DataCfgUnit.class );

    addFieldDefs( STRID, DISPLAY_NAME, TYPE, GWIDS, NODES );

    setPanelCreator( new M5DefaultPanelCreator<>() {

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

                return toolbar;
              }

              @Override
              protected void doProcessAction( String aActionId ) {

                switch( aActionId ) {
                  case ACTID_SAVE_DOC:
                    ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).saveCurrState();
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
}