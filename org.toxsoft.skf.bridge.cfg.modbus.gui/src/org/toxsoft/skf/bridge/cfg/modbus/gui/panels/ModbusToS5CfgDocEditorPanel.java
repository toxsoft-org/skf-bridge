package org.toxsoft.skf.bridge.cfg.modbus.gui.panels;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

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
import org.toxsoft.core.tsgui.widgets.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.bricks.strid.more.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.utils.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skide.plugin.exconn.service.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * Editor panel for creating, editing, deleting modbus to s5 cfg docs.
 *
 * @author max
 */
public class ModbusToS5CfgDocEditorPanel
    extends TsPanel {

  final static String ACTID_EDIT_UNITS = SK_ID + ".users.gui.EditUnits"; //$NON-NLS-1$

  final static String ACTID_EDIT_NODES = SK_ID + ".users.gui.EditNodes"; //$NON-NLS-1$

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

  IM5CollectionPanel<ModbusToS5CfgDoc> opcToS5DataCfgDocPanel;

  private CTabFolder tabFolder;

  private TextControlContribution textContr1;

  /**
   * Конструктор панели.
   * <p>
   * Конструктор просто запоминает ссылку на контекст, без создания копии.
   *
   * @param aParent {@link Composite} - родительская панель
   * @param aContext {@link ITsGuiContext} - контекст панели
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public ModbusToS5CfgDocEditorPanel( Composite aParent, ITsGuiContext aContext ) {
    super( aParent, aContext );
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<ModbusToS5CfgDoc> model = m5.getModel( ModbusToS5CfgDocM5Model.MODEL_ID, ModbusToS5CfgDoc.class );

    ModbusToS5CfgDocService docService = new ModbusToS5CfgDocService( aContext );
    aContext.put( ModbusToS5CfgDocService.class, docService );

    IM5LifecycleManager<ModbusToS5CfgDoc> lm = new ModbusToS5CfgDocM5LifecycleManager( model, docService );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );

    // IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
    // avValobj( EBorderLayoutPlacement.SOUTH ) );
    // IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    // IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );

    MultiPaneComponentModown<ModbusToS5CfgDoc> componentModown =
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
            ModbusToS5CfgDoc selDoc = selectedItem();

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

  protected void editOpcCfgDoc( ModbusToS5CfgDoc aSelDoc ) {

    // создаем новую общую закладку закладку
    CTabItem tabItem = new CTabItem( tabFolder, SWT.CLOSE );
    tabItem.setText( aSelDoc.nmName() );

    TsComposite frame = new TsComposite( tabFolder );
    frame.setLayout( new BorderLayout() );

    tabItem.setControl( frame );
    tabFolder.setSelection( tabItem );

    CTabFolder tabSubFolder = new CTabFolder( frame, SWT.BORDER );
    tabSubFolder.setLayoutData( BorderLayout.CENTER );

    // Создаём закладку для конфигурации связей opc-sk
    CTabItem tabCfgUnitsItem = new CTabItem( tabSubFolder, SWT.NONE );
    tabCfgUnitsItem.setText( STR_LINKS );

    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    ctx.params().addAll( tsContext().params() );

    TsToolbar toolBar = new TsToolbar( ctx );
    toolBar.setIconSize( EIconSize.IS_24X24 );
    toolBar.addActionDef( ACDEF_SAVE_DOC );
    toolBar.addActionDef( ACDEF_S5_SERVER_SELECT );

    toolBar.addSeparator();

    Control toolbarCtrl = toolBar.createControl( frame );
    toolbarCtrl.setLayoutData( BorderLayout.NORTH );

    textContr1 = new TextControlContribution( "Label", 200, STR_SK_CONN_DESCR, SWT.NONE ); //$NON-NLS-1$
    toolBar.addContributionItem( textContr1 );

    IM5Domain m5 = conn.scope().get( IM5Domain.class );

    toolBar.addListener( aActionId -> {
      if( aActionId.equals( ACDEF_SAVE_DOC.id() ) ) {
        ModbusToS5CfgDocService service = ctx.get( ModbusToS5CfgDocService.class );
        service.saveCfgDoc( aSelDoc );
        return;
      }
      if( aActionId.equals( ACDEF_S5_SERVER_SELECT.id() ) ) {
        ISkideExternalConnectionsService connService =
            ctx.eclipseContext().get( ISkideExternalConnectionsService.class );
        IdChain idChain = connService.selectConfigAndOpenConnection( ctx );
        if( idChain != null ) {
          ctx.put( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_S5_CONNECTION, idChain );
          textContr1.setText( STR_SK_CONN_DESCR + idChain.first() );
        }

        return;
      }

    } );

    // ISkConnectionSupplier connSup = ctx.get( ISkConnectionSupplier.class );
    // установить по умолчанию s5 соединение рабочего пространства
    // IdChain defaultConnIdChain = connSup.getDefaultConnectionKey();
    IdChain defaultConnIdChain = ISkConnectionSupplier.DEF_CONN_ID;
    ctx.put( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_S5_CONNECTION, defaultConnIdChain );
    String defConnName = defaultConnIdChain.first() != null ? defaultConnIdChain.first() : STR_DEFAULT_WORKROOM_SK_CONN;
    textContr1.setText( STR_SK_CONN_DESCR + defConnName );

    // Связи
    IM5Model<OpcToS5DataCfgUnit> model =
        m5.getModel( OpcToS5DataCfgUnitM5Model.MODEL_ID_TEMPLATE + ".modbus", OpcToS5DataCfgUnit.class );

    // IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
    // avValobj( EBorderLayoutPlacement.SOUTH ) );
    // IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );

    IM5LifecycleManager<OpcToS5DataCfgUnit> lm = new ModbusToS5CfgUnitM5LifecycleManager( model, aSelDoc );

    MultiPaneComponentModown<OpcToS5DataCfgUnit> mpc =
        new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm ) {

          @Override
          protected ITsToolbar doCreateToolbar( ITsGuiContext aaContext, String aName, EIconSize aIconSize,
              IListEdit<ITsActionDef> aActs ) {
            aActs.add( ACDEF_SEPARATOR );
            aActs.add( OpcToS5DataCfgDocEditorPanel.ACDEF_GENERATE_FILE );

            ITsToolbar toolbar = super.doCreateToolbar( aaContext, aName, aIconSize, aActs );

            toolbar.addListener( aActionId -> {
              // nop
            } );

            toolbar.setIconSize( EIconSize.IS_24X24 );
            return toolbar;
          }

          @Override
          protected void doProcessAction( String aActionId ) {

            switch( aActionId ) {
              case OpcToS5DataCfgDocEditorPanel.ACTID_GENERATE_FILE:
                ((ModbusToS5CfgUnitM5LifecycleManager)lifecycleManager()).generateDlmFileFromCurrState( ctx );
                ((ModbusToS5CfgUnitM5LifecycleManager)lifecycleManager()).generateDevFileFromCurrState( ctx );
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

    tabSubFolder.setSelection( tabCfgUnitsItem );
  }

}
