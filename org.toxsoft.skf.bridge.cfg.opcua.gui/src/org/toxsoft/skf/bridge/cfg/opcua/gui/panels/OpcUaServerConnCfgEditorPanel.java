package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import java.io.*;
import java.lang.reflect.*;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
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
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.bricks.apprefs.*;
import org.toxsoft.core.tslib.bricks.apprefs.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.service.*;
import org.toxsoft.skf.bridge.cfg.opcua.service.impl.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.glib.query.*;

/**
 * Editor panel for creating, editing, deleting opc ua server connections.
 *
 * @author max
 */
public class OpcUaServerConnCfgEditorPanel
    extends TsPanel {

  final ISkConnection conn;

  final IOpcUaServerConnCfgService cfgService;

  IM5CollectionPanel<IOpcUaServerConnCfg> opcUaConnCfgPanel;

  private CTabFolder tabFolder;

  private OpcUaServerNodesBrowserPanel nodesBrowser;

  final static String ACTID_BROWSE_CONN = SK_ID + ".bridge.cfg.opcua.BrowseConn"; //$NON-NLS-1$

  final static TsActionDef ACDEF_BROWSE_CONN =
      TsActionDef.ofPush2( ACTID_BROWSE_CONN, STR_N_BROWSE_CONN, STR_D_BROWSE_CONN, ITsStdIconIds.ICONID_VIEW_AS_TREE );

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
            // aActs.add( ReportTemplateEditorPanel.ACDEF_COPY_TEMPLATE );
            //
            // if( SHOW_APPLY_BUTTON.getValue( aContext.params() ).asBool() ) {
            // aActs.add( ACDEF_SEPARATOR );
            aActs.add( ACDEF_BROWSE_CONN );
            // }

            ITsToolbar toolbar =

                super.doCreateToolbar( aContext, aName, aIconSize, aActs );

            toolbar.addListener( aActionId -> {
              // TODO Auto-generated method stub

            } );

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
              default:
                throw new TsNotAllEnumsUsedRtException( aActionId );
            }
          }
        };
    opcUaConnCfgPanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );

    opcUaConnCfgPanel.createControl( sf );
    // пока не выбрано ни одно соединение, отключаем
    componentModown.toolbar().getAction( ACDEF_BROWSE_CONN.id() ).setEnabled( false );
    componentModown.addTsSelectionListener( ( aSource, aSelectedItem ) -> {
      // просто активируем кнопку подключения/просмотра
      boolean enableRunBttn = (aSelectedItem != null);
      componentModown.toolbar().getAction( ACDEF_BROWSE_CONN.id() ).setEnabled( enableRunBttn );
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
    SkQueryDialog progressDialog = new SkQueryDialog( getShell(), STR_LOADING_OPC_UA_NODES_STRUCT, timeout );
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
          nodesBrowser = new OpcUaServerNodesBrowserPanel( verticalSashForm, browserContext, aSelCfg );
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
