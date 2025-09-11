package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.widgets.*;
import org.jopendocument.dom.spreadsheet.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.ITsGuiContext;
import org.toxsoft.core.tsgui.bricks.ctx.impl.TsGuiContext;
import org.toxsoft.core.tsgui.dialogs.ETsDialogCode;
import org.toxsoft.core.tsgui.dialogs.TsDialogUtils;
import org.toxsoft.core.tsgui.graphics.icons.EIconSize;
import org.toxsoft.core.tsgui.m5.IM5Domain;
import org.toxsoft.core.tsgui.m5.IM5Model;
import org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.MultiPaneComponentModown;
import org.toxsoft.core.tsgui.m5.gui.panels.IM5CollectionPanel;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.M5CollectionPanelMpcModownWrapper;
import org.toxsoft.core.tsgui.m5.model.IM5LifecycleManager;
import org.toxsoft.core.tsgui.panels.TsPanel;
import org.toxsoft.core.tsgui.panels.toolbar.ITsToolbar;
import org.toxsoft.core.tsgui.utils.layout.BorderLayout;
import org.toxsoft.core.tsgui.utils.layout.EBorderLayoutPlacement;
import org.toxsoft.core.tslib.av.impl.AvUtils;
import org.toxsoft.core.tslib.bricks.apprefs.IAppPreferences;
import org.toxsoft.core.tslib.bricks.apprefs.impl.*;
import org.toxsoft.core.tslib.coll.IListEdit;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.LoggerUtils;
import org.toxsoft.core.txtproj.lib.storage.IKeepablesStorage;
import org.toxsoft.core.txtproj.lib.workroom.ITsWorkroom;
import org.toxsoft.skf.bridge.cfg.opcua.gui.Activator;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skf.bridge.cfg.opcua.service.IOpcUaServerConnCfgService;
import org.toxsoft.skf.bridge.cfg.opcua.service.impl.OpcUaServerConnCfgService;
import org.toxsoft.uskat.core.connection.ISkConnection;
import org.toxsoft.uskat.core.gui.conn.ISkConnectionSupplier;
import org.toxsoft.uskat.core.gui.glib.query.SkProgressDialog;

/**
 * Editor panel for creating, editing, deleting opc ua server connections.
 *
 * @author max
 * @author dima
 */
public class OpcUaServerConnCfgEditorPanel
    extends TsPanel {

  final ISkConnection conn;

  final IOpcUaServerConnCfgService cfgService;

  IM5CollectionPanel<IOpcUaServerConnCfg> opcUaConnCfgPanel;

  private CTabFolder tabFolder;

  private OpcUaTreeBrowserPanel nodesBrowser;

  final static String ACTID_BROWSE_CONN = SK_ID + ".bridge.cfg.opcua.BrowseConn"; //$NON-NLS-1$

  final static TsActionDef ACDEF_BROWSE_CONN =
      TsActionDef.ofPush2( ACTID_BROWSE_CONN, STR_N_BROWSE_CONN, STR_D_BROWSE_CONN, ICONID_LOAD_TREE );

  /**
   * id действия "remove OPC UA nodes cache"
   */
  final static String REMOVE_CACHED_NODES_OPC_UA_ACT_ID = "remove_cached_nodes_opc_ua_act_id"; //$NON-NLS-1$

  TsActionDef ACDEF_REMOVE_CACHE = TsActionDef.ofPush2( REMOVE_CACHED_NODES_OPC_UA_ACT_ID,
      STR_N_REMOVE_CACHED_NODES_OPC_UA, STR_D_REMOVE_CACHED_NODES_OPC_UA, ICONID_CLEAR_CASH );

  /**
   * id действия "import BitMask refbook"
   */
  final static String IMPORT_BITMASK_REFBOOK_ACT_ID = "import_BitMask_refbook_act_id"; //$NON-NLS-1$

  TsActionDef ACDEF_IMPORT_BITMASK_REFBOOK = TsActionDef.ofPush2( IMPORT_BITMASK_REFBOOK_ACT_ID,
      STR_N_IMPORT_BITMASK_REFBOOK, STR_D_IMPORT_BITMASK_REFBOOK, ICONID_IMPORT_BLACK );

  /**
   * Конструктор панели.
   * <p>
   * Конструктор просто запоминает ссылку на контекст, без создания копии.
   *
   * @param aParent {@link Composite} - родительская панель
   * @param aContext {@link ITsGuiContext} - контекст панели
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public OpcUaServerConnCfgEditorPanel( Composite aParent, ITsGuiContext aContext ) {
    super( aParent, aContext );
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();

    // занесем параметры из файла в контекст
    AbstractAppPreferencesStorage apStorage =
        new AppPreferencesConfigIniStorage( new File( OPC_UA_SERVER_CONN_CFG_STORE_FILE ) );
    IAppPreferences appPreferences = new AppPreferences( apStorage );
    cfgService = new OpcUaServerConnCfgService( appPreferences );

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<IOpcUaServerConnCfg> model = m5.getModel( OpcUaServerConnCfgModel.MODEL_ID, IOpcUaServerConnCfg.class );

    IM5LifecycleManager<IOpcUaServerConnCfg> lm = new OpcUaServerConnCfgM5LifecycleManager( model, cfgService );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    // IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );

    SashForm sf = new SashForm( this, SWT.HORIZONTAL );
    MultiPaneComponentModown<IOpcUaServerConnCfg> componentModown =
        new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm ) {

          @Override
          protected ITsToolbar doCreateToolbar( @SuppressWarnings( "hiding" ) ITsGuiContext aContext, String aName,
              EIconSize aIconSize, IListEdit<ITsActionDef> aActs ) {
            aActs.add( ITsStdActionDefs.ACDEF_SEPARATOR );
            aActs.add( ACDEF_BROWSE_CONN );
            aActs.add( ACDEF_REMOVE_CACHE );
            aActs.add( ITsStdActionDefs.ACDEF_SEPARATOR );
            aActs.add( ACDEF_IMPORT_BITMASK_REFBOOK );

            ITsToolbar toolbar =

                super.doCreateToolbar( aContext, aName, aIconSize, aActs );

            toolbar.addListener( aActionId -> {
              // TODO Auto-generated method stub

            } );
            // установим пока 32, там посмотрим
            toolbar.setIconSize( EIconSize.IS_32X32 );
            return toolbar;
          }

          @Override
          protected void doProcessAction( String aActionId ) {
            super.doProcessAction( aActionId );
            IOpcUaServerConnCfg selConfig = selectedItem();

            switch( aActionId ) {
              case ACTID_BROWSE_CONN:
                browseConn( selConfig );
                break;
              case REMOVE_CACHED_NODES_OPC_UA_ACT_ID:
                Shell shell = tsContext().get( Shell.class );
                if( TsDialogUtils.askYesNoCancel( shell, MSG_ASK_REMOVE_CACHE_CONFIRM ) == ETsDialogCode.YES ) {

                  ITsWorkroom workroom = tsContext().eclipseContext().get( ITsWorkroom.class );
                  TsInternalErrorRtException.checkNull( workroom );
                  IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();

                  storage.removeSection( OpcUaUtils.getCachedTreeSectionName( selConfig ) );
                }
                break;
              case IMPORT_BITMASK_REFBOOK_ACT_ID:
                FileDialog dlg = new FileDialog( getShell() );
                dlg.setFilterExtensions( new String[] { "*.ods", "*.*" } ); //$NON-NLS-1$ //$NON-NLS-2$
                String filePath = dlg.open();
                if( filePath != null ) {
                  File refbookFile = new File( filePath );
                  RefbookGenerator rbImporter = new RefbookGenerator( connSup.defConn(), getShell() );
                  rbImporter.importPoligonBitMaskRefbook( refbookFile );
                  // notify user
                  TsDialogUtils.info( getShell(), "Справочник битовых масок импортирован" );
                }
                break;
              default:
                throw new TsNotAllEnumsUsedRtException( aActionId );
            }
          }
        };
    opcUaConnCfgPanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );

    opcUaConnCfgPanel.createControl( sf );
    // пока не выбрано ни одно соединение, отключаем
    componentModown.toolbar().getAction( ACDEF_BROWSE_CONN.id() ).setEnabled( false );
    componentModown.toolbar().getAction( ACDEF_REMOVE_CACHE.id() ).setEnabled( false );
    componentModown.addTsSelectionListener( ( aSource, aSelectedItem ) -> {
      // просто активируем кнопку подключения/просмотра
      boolean enableRunBttn = (aSelectedItem != null);
      componentModown.toolbar().getAction( ACDEF_BROWSE_CONN.id() ).setEnabled( enableRunBttn );
      componentModown.toolbar().getAction( ACDEF_REMOVE_CACHE.id() ).setEnabled( enableRunBttn );
    } );

    tabFolder = new CTabFolder( sf, SWT.BORDER );
    tabFolder.setLayout( new BorderLayout() );

    sf.setWeights( 300, 500 );

  }

  protected void browseConn( IOpcUaServerConnCfg aSelCfg ) {
    ISkConnectionSupplier conSupp = tsContext().get( ISkConnectionSupplier.class );

    doBrowseConn( aSelCfg, conSupp.defConn() );
  }

  protected void doBrowseConn( IOpcUaServerConnCfg aSelCfg,
      @SuppressWarnings( "unused" ) ISkConnection aReportDataConnection ) {
    Shell shell = tsContext().get( Shell.class );

    // Максимальное время выполнения запроса (мсек)
    long timeout = 3000;

    // Создание диалога прогресса выполнения запроса
    SkProgressDialog progressDialog = new SkProgressDialog( getShell(), STR_LOADING_OPC_UA_NODES_STRUCT, timeout );
    // fork = true, cancelable = true
    try {
      progressDialog.run( true, true, aMonitor -> {
        shell.getDisplay().asyncExec( () -> {

          ITsGuiContext browserContext = new TsGuiContext( tsContext() );

          // выясняем текущего пользователя
          // ISkConnectionSupplier conSupp = tsContext().get( ISkConnectionSupplier.class );
          // ISkConnection connectionForUser = conSupp.defConn();

          // создаем новую закладку
          CTabItem tabItem = new CTabItem( tabFolder, SWT.CLOSE );
          tabItem.setText( aSelCfg.nmName() );
          // create vertical sash
          SashForm verticalSashForm = new SashForm( tabFolder, SWT.VERTICAL );
          nodesBrowser = new OpcUaTreeBrowserPanel( verticalSashForm, browserContext, aSelCfg );
          UaVariableNodeListPanel nodesInspector =
              new UaVariableNodeListPanel( verticalSashForm, browserContext, nodesBrowser.getOpcUaClient() );
          nodesBrowser.setInspector( nodesInspector );
          tabItem.setControl( verticalSashForm );

          tabFolder.setSelection( tabItem );
          // nodesBrowser.setConnectionCfg( aSelCfg );
          // reportV.requestLayout();
        } );

      } );
    }
    catch( InvocationTargetException | InterruptedException ex ) {
      LoggerUtils.errorLogger().error( ex );
    }

  }

}
