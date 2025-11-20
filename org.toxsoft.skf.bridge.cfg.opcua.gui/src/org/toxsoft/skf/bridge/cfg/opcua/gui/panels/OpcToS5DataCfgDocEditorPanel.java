package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.stack.core.*;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tsgui.widgets.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.bricks.strid.more.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.login.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skide.plugin.exconn.service.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * Editor panel for creating, editing, deleting opc to s5 cfg docs.
 *
 * @author max
 */
public class OpcToS5DataCfgDocEditorPanel
    extends TsPanel {

  final static String ACTID_EDIT_UNITS = SK_ID + ".users.gui.EditUnits"; //$NON-NLS-1$

  final static String ACTID_EDIT_NODES = SK_ID + ".users.gui.EditNodes"; //$NON-NLS-1$

  final static String ACTID_AUTO_LINK = SK_ID + "bridge.cfg.opcua.to.s5.auto.link"; //$NON-NLS-1$

  /**
   * action id генерации конфигурационных файлов моста
   */
  public final static String ACTID_GENERATE_FILE = SK_ID + "bridge.cfg.opcua.to.s5.generate.file"; //$NON-NLS-1$

  final static TsActionDef ACDEF_AUTO_LINK =
      TsActionDef.ofPush2( ACTID_AUTO_LINK, STR_N_AUTO_LINK, STR_D_AUTO_LINK, ICONID_AUTO_LINK );

  /**
   * Генерация конфигурационных файлов моста
   */
  public final static TsActionDef ACDEF_GENERATE_FILE =
      TsActionDef.ofPush2( ACTID_GENERATE_FILE, STR_N_GENERATE_CFG, STR_D_GENERATE_CFG, ICONID_SHOW_GENERATE_DLMCFG );

  final static TsActionDef ACDEF_EDIT_UNITS =
      TsActionDef.ofPush2( ACTID_EDIT_UNITS, STR_N_EDIT_CONFIG_SET, STR_D_EDIT_CONFIG_SET, ICONID_EDIT_UNITS );

  final static TsActionDef ACDEF_EDIT_NODES =
      TsActionDef.ofPush2( ACTID_EDIT_NODES, STR_N_EDIT_OPC_UA_NODES, STR_D_EDIT_OPC_UA_NODES, ICONID_EDIT_NODES );

  final static String ACTID_SAVE_DOC = SK_ID + "bridge.cfg.opcua.to.s5.save.doc"; //$NON-NLS-1$

  final static TsActionDef ACDEF_SAVE_DOC =
      TsActionDef.ofPush2( ACTID_SAVE_DOC, STR_N_SAVE_CONFIG, STR_D_SAVE_CONFIG, ICONID_SAVE_DOC );

  final static String ACTID_S5_SERVER_SELECT = SK_ID + "bridge.cfg.opcua.to.s5.s5.server.select"; //$NON-NLS-1$

  final static String ACTID_OPC_SERVER_SELECT = SK_ID + "bridge.cfg.opcua.to.s5.opc.server.select"; //$NON-NLS-1$

  final static TsActionDef ACDEF_S5_SERVER_SELECT = TsActionDef.ofPush2( ACTID_S5_SERVER_SELECT, STR_N_SELECT_S5_SERVER,
      STR_D_SELECT_S5_SERVER, ICONID_S5_SERVER_SELECT );

  final static TsActionDef ACDEF_OPC_SERVER_SELECT = TsActionDef.ofPush2( ACTID_OPC_SERVER_SELECT,
      STR_N_SELECT_OPC_UA_SERVER, STR_D_SELECT_OPC_UA_SERVER, ICONID_OPC_SERVER_SELECT );

  final ISkConnection conn;

  IM5CollectionPanel<OpcToS5DataCfgDoc> opcToS5DataCfgDocPanel;

  private CTabFolder tabFolder;

  private TextControlContribution textContr1;
  private TextControlContribution textContr2;

  private IStringMapEdit<CTabItem> tabItemsMap = new StringMap<>();

  private IM5CollectionPanel<CfgOpcUaNode> cfgNodesPanel;

  /**
   * Конструктор панели.
   * <p>
   * Конструктор просто запоминает ссылку на контекст, без создания копии.
   *
   * @param aParent {@link Composite} - родительская панель
   * @param aContext {@link ITsGuiContext} - контекст панели
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public OpcToS5DataCfgDocEditorPanel( Composite aParent, ITsGuiContext aContext ) {
    super( aParent, aContext );
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<OpcToS5DataCfgDoc> model = m5.getModel( OpcToS5DataCfgDocM5Model.MODEL_ID, OpcToS5DataCfgDoc.class );

    OpcToS5DataCfgDocService docService = aContext.get( OpcToS5DataCfgDocService.class );

    IM5LifecycleManager<OpcToS5DataCfgDoc> lm = new OpcToS5DataCfgDocM5LifecycleManager( model, docService );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );

    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    // IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );

    MultiPaneComponentModown<OpcToS5DataCfgDoc> componentModown =
        new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm ) {

          @Override
          protected ITsToolbar doCreateToolbar( ITsGuiContext aContext2, String aName, EIconSize aIconSize,
              IListEdit<ITsActionDef> aActs ) {
            aActs.add( ACDEF_SEPARATOR );
            aActs.add( ACDEF_EDIT_UNITS );
            // aActs.add( ACDEF_EDIT_NODES );

            ITsToolbar toolbar =

                super.doCreateToolbar( aContext2, aName, aIconSize, aActs );

            toolbar.addListener( aActionId -> {
              // nop

            } );

            toolbar.setIconSize( EIconSize.IS_24X24 );
            return toolbar;
          }

          @Override
          protected void doProcessAction( String aActionId ) {
            OpcToS5DataCfgDoc selDoc = selectedItem();

            switch( aActionId ) {

              case ACTID_EDIT_UNITS:
                editOpcCfgDoc( selDoc );
                break;

              case ACTID_EDIT_NODES:
                // editOpcCfgNodes( selDoc );
                break;

              default:
                throw new TsNotAllEnumsUsedRtException( aActionId );
            }
          }
        };
    opcToS5DataCfgDocPanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );

    SashForm sf = new SashForm( this, SWT.HORIZONTAL );
    opcToS5DataCfgDocPanel.createControl( sf );

    tabFolder = new CTabFolder( sf, SWT.BORDER );
    tabFolder.setLayout( new BorderLayout() );

    sf.setWeights( 300, 500 );

  }

  protected void editOpcCfgDoc( OpcToS5DataCfgDoc aSelDoc ) {
    // if configuration is already edited, activate item and return
    CTabItem tabItem = tabItemsMap.findByKey( aSelDoc.id() );
    if( tabItem != null ) {
      tabFolder.setSelection( tabItem );
      return;
    }

    // создаем новую общую закладку закладку
    tabItem = new CTabItem( tabFolder, SWT.CLOSE );
    tabItemsMap.put( aSelDoc.id(), tabItem );
    tabItem.addDisposeListener( e -> tabItemsMap.removeByKey( aSelDoc.id() ) );

    tabItem.setText( aSelDoc.nmName() );
    tabItem.setToolTipText( aSelDoc.description() );

    TsComposite frame = new TsComposite( tabFolder );
    frame.setLayout( new BorderLayout() );

    tabItem.setControl( frame );
    tabFolder.setSelection( tabItem );

    CTabFolder tabSubFolder = new CTabFolder( frame, SWT.BORDER );
    tabSubFolder.setLayoutData( BorderLayout.CENTER );

    // Создаём закладку для конфигурации связей opc-sk
    CTabItem tabCfgUnitsItem = new CTabItem( tabSubFolder, SWT.NONE );
    tabCfgUnitsItem.setText( STR_LINKS );

    // Создаём закладку для конфигурации узлов opc
    CTabItem tabCfgNodesItem = new CTabItem( tabSubFolder, SWT.NONE );
    tabCfgNodesItem.setText( STR_NODES );

    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    ctx.params().addAll( tsContext().params() );

    TsToolbar toolBar = new TsToolbar( ctx );
    toolBar.setIconSize( EIconSize.IS_24X24 );
    toolBar.addActionDef( ACDEF_GENERATE_FILE );
    toolBar.addActionDef( ACDEF_S5_SERVER_SELECT );
    toolBar.addActionDef( ACDEF_OPC_SERVER_SELECT );

    toolBar.addSeparator();

    Control toolbarCtrl = toolBar.createControl( frame );
    toolbarCtrl.setLayoutData( BorderLayout.NORTH );

    textContr1 = new TextControlContribution( "Label", 200, STR_SK_CONN_DESCR, SWT.NONE ); //$NON-NLS-1$
    toolBar.addContributionItem( textContr1 );
    toolBar.addSeparator();
    textContr2 = new TextControlContribution( "Label2", 200, STR_OPC_UA_DESCR, SWT.NONE ); //$NON-NLS-1$
    toolBar.addContributionItem( textContr2 );

    IM5Domain m5 = conn.scope().get( IM5Domain.class );

    toolBar.addListener( aActionId -> {
      if( aActionId.equals( ACDEF_GENERATE_FILE.id() ) ) {
        OpcUaUtils.generateDlmCfgFileFromCurrState( aSelDoc, ctx );
        OpcUaUtils.generateDevCfgFileFromCurrState( aSelDoc, ctx );
        return;
      }
      if( aActionId.equals( ACDEF_S5_SERVER_SELECT.id() ) ) {
        ISkideExternalConnectionsService connService =
            ctx.eclipseContext().get( ISkideExternalConnectionsService.class );
        // IdChain idChain = connService.selectConfigAndOpenConnection( ctx );

        IdChain idChain = null;
        String cfgId = connService.selectConfig( ctx );
        if( cfgId != null ) {
          idChain = connService.openConnection( cfgId, ctx, new LoginInfo( "root", "1", "" ) );
        }

        if( idChain != null ) {
          ctx.put( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_S5_CONNECTION, idChain );
          textContr1.setText( STR_SK_CONN_DESCR + idChain.first() );
        }

        return;
      }
      if( aActionId.equals( ACDEF_OPC_SERVER_SELECT.id() ) ) {
        IOpcUaServerConnCfg conConf = OpcUaUtils.selectOpcServerConfig( ctx );
        // dima 13.10.23 сохраним в контекст
        ctx.put( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG, conConf );

        Display.getDefault().asyncExec( () -> textContr2
            .setText( STR_SK_CONN_DESCR + (conConf == null ? TsLibUtils.EMPTY_STRING : conConf.nmName()) ) );

        if( conConf == null ) {
          return;
        }

        OpcUaUtils.runInWaitingDialog( ctx, STR_OPC_UA_CONNECTING_PROCESS, monitor -> {
          try {

            monitor.subTask( STR_CHECK_OPC_UA_CACHE );
            boolean hasCach = OpcUaUtils.hasCachedOpcUaNodesTreeFor( ctx, conConf );
            // если есть кэш - не пытаться соединится
            if( hasCach ) {
              OpcUaUtils.updateNodesInfoesFromCache( ctx, conConf, aSelDoc );
              Display.getDefault().asyncExec( () -> {
                textContr2.setText( STR_OPC_UA_CACHE + conConf.nmName() );
                TsDialogUtils.info( getShell(), STR_USE_OPC_UA_CACHE );
              } );
              return;
            }
          }
          catch( Exception ex ) {
            // Display.getDefault().asyncExec( () -> TsDialogUtils.error( getShell(), ex ) );
            LoggerUtils.errorLogger().error( ex );
            // return;
          }

          OpcUaClient client = null;

          try {
            monitor.subTask( STR_OPC_UA_CLIENT_CREATE );
            client = OpcUaUtils.createClient( conConf );

            monitor.subTask( STR_OPC_UA_CLIENT_CONNECTING );
            // В диалоге ожидания попытаться соединится
            client.connect().get();
            ctx.put( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_OPC_CONNECTION, client );

            Display.getDefault().asyncExec( () -> textContr2.setText( STR_OPC_UA_CONNECTED + conConf.nmName() ) );
          }
          catch( Exception ex ) {
            Display.getDefault()
                .asyncExec( () -> TsDialogUtils.error( getShell(), STR_OPC_UA_CONNECTING_PROCESS_FAIL ) );
            LoggerUtils.errorLogger().error( ex );
            return;
          }

          try {
            monitor.subTask( STR_USE_OPC_UA_CACHE_CREATING );
            // при успешном соединении - записать кэш
            IM5Model<UaTreeNode> model = m5.getModel( OpcUaNodeModel.MODEL_ID, UaTreeNode.class );
            IM5LifecycleManager<UaTreeNode> lm =
                new OpcUaNodeM5LifecycleManager( model, client, Identifiers.RootFolder, ctx, conConf );
            lm.itemsProvider().listItems();
            OpcUaUtils.updateNodesInfoesFromCache( ctx, conConf, aSelDoc );
            Display.getDefault().asyncExec( () -> {
              textContr2.setText( STR_OPC_UA_CACHE + conConf.nmName() );
              TsDialogUtils.info( getShell(), STR_USE_OPC_UA_CACHE_CREATED );
            } );
          }
          catch( Exception ex ) {
            Display.getDefault()
                .asyncExec( () -> TsDialogUtils.error( getShell(), STR_USE_OPC_UA_CACHE_CREATION_FAIL ) );
            LoggerUtils.errorLogger().error( ex );
            return;
          }
        } );

        return;
      }

    } );

    IdChain defaultConnIdChain = ISkConnectionSupplier.DEF_CONN_ID;
    ctx.put( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_S5_CONNECTION, defaultConnIdChain );
    String defConnName = defaultConnIdChain.first() != null ? defaultConnIdChain.first() : STR_DEFAULT_WORKROOM_SK_CONN;
    textContr1.setText( STR_SK_CONN_DESCR + defConnName );

    // Связи
    IM5Model<OpcToS5DataCfgUnit> model =
        m5.getModel( OpcToS5DataCfgUnitM5Model.MODEL_ID_TEMPLATE + ".opcua", OpcToS5DataCfgUnit.class );

    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );

    IM5LifecycleManager<OpcToS5DataCfgUnit> lm = new OpcToS5DataCfgUnitM5LifecycleManager( model, aSelDoc );
    // IM5CollectionPanel<OpcToS5DataCfgUnit> opcToS5DataCfgUnitPanel =
    // model.panelCreator().createCollEditPanel( ctx, lm.itemsProvider(), lm );

    MultiPaneComponentModown<OpcToS5DataCfgUnit> mpc =
        new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm ) {

          @Override
          protected ITsToolbar doCreateToolbar( ITsGuiContext aaContext, String aName, EIconSize aIconSize,
              IListEdit<ITsActionDef> aActs ) {
            aActs.add( ACDEF_SEPARATOR );
            aActs.add( ACDEF_AUTO_LINK );
            // aActs.add( ACDEF_GENERATE_FILE );

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
              case ACTID_GENERATE_FILE:
                // OpcToS5DataCfgUnitM5LifecycleManager.generateFileFromCurrState( aSelDoc, ctx );
                break;

              case ACTID_AUTO_LINK:

                // вынести в отделный класс реализации
                IdChain connIdChain =
                    (IdChain)tsContext().find( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_S5_CONNECTION );
                ISkConnectionSupplier connSup = ctx.get( ISkConnectionSupplier.class );

                // dima 06.02.24 работаем теперь через справочник
                ISkConnection currConn = connSup.getConn( connIdChain );

                OpcUaServerConnCfg conConf =
                    (OpcUaServerConnCfg)ctx.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );
                if( conConf == null ) {
                  OpcUaUtils.selectOpcConfigAndOpenConnection( ctx );
                  conConf = (OpcUaServerConnCfg)ctx.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );
                }
                else {
                  ctx.put( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG, conConf );
                }

                // TODO - процессор брать из контекста
                // процесс долгий, загоним его в фон и повесим бегунок
                OpcUaUtils.runInWaitingDialog( ctx, STR_CONFIG_AUTO_GENERATION, monitor -> {
                  // первый этап - устанавливаем связи
                  monitor.subTask( STR_LINKING );
                  OpcUaServerConnCfg connConf =
                      (OpcUaServerConnCfg)ctx.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );
                  IList<OpcToS5DataCfgUnit> cfgUnits = new StoredMetaInfoAutoLinkConfigurationProcess()
                      .formCfgUnitsFromAutoElements( ctx, currConn, connConf );

                  ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).addCfgUnits( cfgUnits );

                  // второй этап - обновляем узлы OPC UA
                  monitor.subTask( STR_FILL_NODES );
                  OpcUaUtils.synchronizeNodesCfgs( aSelDoc, ctx, true );
                  OpcToS5DataCfgDocService service = ctx.get( OpcToS5DataCfgDocService.class );
                  // третий этап - обновляем узлы OPC UA
                  monitor.subTask( STR_SAVE_CONFIG );
                  service.saveCfgDoc( aSelDoc );
                  // последний этап - обновление информации об OPC UA nodes
                  monitor.subTask( STR_UPDATE_NODES_INFO );
                  OpcUaUtils.updateNodesInfoesFromCache( ctx, connConf, aSelDoc );

                } );
                // обновляем GUI из потока GUI
                doFillTree();
                cfgNodesPanel.refresh();
                break;

              default:
                throw new TsNotAllEnumsUsedRtException( aActionId );
            }
          }
        };

    IM5CollectionPanel<OpcToS5DataCfgUnit> opcToS5DataCfgUnitPanel =
        new M5CollectionPanelMpcModownWrapper<>( mpc, false );

    tabCfgUnitsItem.setControl( opcToS5DataCfgUnitPanel.createControl( tabSubFolder ) );

    // Узлы
    ITsGuiContext ctx2 = new TsGuiContext( ctx );
    ctx2.params().addAll( ctx.params() );
    IM5Model<CfgOpcUaNode> nodeModel = m5.getModel( CfgOpcUaNodeM5Model.MODEL_ID, CfgOpcUaNode.class );

    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx2.params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx2.params(), AvUtils.AV_TRUE );

    IM5LifecycleManager<CfgOpcUaNode> nodeLm = nodeModel.getLifecycleManager( aSelDoc );

    cfgNodesPanel = nodeModel.panelCreator().createCollEditPanel( ctx2, nodeLm.itemsProvider(), nodeLm );

    lm.itemsProvider().genericChangeEventer().addListener( aSource -> {
      OpcUaUtils.synchronizeNodesCfgs( aSelDoc, ctx, true );
      OpcToS5DataCfgDocService service = ctx.get( OpcToS5DataCfgDocService.class );
      service.saveCfgDoc( aSelDoc );
      cfgNodesPanel.refresh();
    } );

    nodeLm.itemsProvider().genericChangeEventer().addListener( aSource -> {
      OpcToS5DataCfgDocService service = ctx.get( OpcToS5DataCfgDocService.class );
      service.saveCfgDoc( aSelDoc );
    } );

    tabCfgNodesItem.setControl( cfgNodesPanel.createControl( tabSubFolder ) );

    tabSubFolder.setSelection( tabCfgUnitsItem );
  }

}
