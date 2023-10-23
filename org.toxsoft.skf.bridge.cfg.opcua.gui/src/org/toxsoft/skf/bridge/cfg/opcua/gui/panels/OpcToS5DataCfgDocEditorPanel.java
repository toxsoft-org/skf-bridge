package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.eclipse.jface.action.*;
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
import org.toxsoft.core.tslib.bricks.geometry.impl.TsPoint;
import org.toxsoft.core.tslib.bricks.strid.more.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
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

  final static TsActionDef ACDEF_EDIT_UNITS = TsActionDef.ofPush2( ACTID_EDIT_UNITS, "Редактировать состав",
      "Редактировать состав единиц конфигурации", ICONID_EDIT_UNITS );

  final static TsActionDef ACDEF_EDIT_NODES = TsActionDef.ofPush2( ACTID_EDIT_NODES, "Редактировать свойства узлов OPC",
      "Редактировать конфигурационные свойства узлов OPC", ICONID_EDIT_NODES );

  final static String ACTID_SAVE_DOC = SK_ID + "bridge.cfg.opcua.to.s5.save.doc"; //$NON-NLS-1$

  final static TsActionDef ACDEF_SAVE_DOC = TsActionDef.ofPush2( ACTID_SAVE_DOC, "Сохранить конфигурацию",
      "Сохранить конфигурацию в SKIDE", ICONID_SAVE_DOC );

  final static String ACTID_S5_SERVER_SELECT = SK_ID + "bridge.cfg.opcua.to.s5.s5.server.select"; //$NON-NLS-1$

  final static String ACTID_OPC_SERVER_SELECT = SK_ID + "bridge.cfg.opcua.to.s5.opc.server.select"; //$NON-NLS-1$

  final static TsActionDef ACDEF_S5_SERVER_SELECT =
      TsActionDef.ofPush2( ACTID_S5_SERVER_SELECT, "Выбрать S5 сервер", "Выбрать S5 сервер", ICONID_S5_SERVER_SELECT );

  final static TsActionDef ACDEF_OPC_SERVER_SELECT = TsActionDef.ofPush2( ACTID_OPC_SERVER_SELECT,
      "Выбрать OPC UA сервер", "Выбрать OPC UA сервер", ICONID_OPC_SERVER_SELECT );

  final ISkConnection conn;

  IM5CollectionPanel<OpcToS5DataCfgDoc> opcToS5DataCfgDocPanel;

  private CTabFolder tabFolder;

  private TextControlContribution textContr1;
  private TextControlContribution textContr2;

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

    OpcToS5DataCfgDocService docService = new OpcToS5DataCfgDocService( aContext );
    aContext.put( OpcToS5DataCfgDocService.class, docService );

    IM5LifecycleManager<OpcToS5DataCfgDoc> lm = new OpcToS5DataCfgDocM5LifecycleManager( model, docService );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );

    // IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
    // avValobj( EBorderLayoutPlacement.SOUTH ) );
    // IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
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

            toolbar.setIconSize( EIconSize.IS_48X48 );
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
    tabCfgUnitsItem.setText( "Связи" );

    // Создаём закладку для конфигурации связей opc-sk
    CTabItem tabCfgNodesItem = new CTabItem( tabSubFolder, SWT.NONE );
    tabCfgNodesItem.setText( "Узлы" );

    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    ctx.params().addAll( tsContext().params() );

    TsToolbar toolBar = new TsToolbar( ctx );
    toolBar.setIconSize( EIconSize.IS_48X48 );
    toolBar.addActionDef( ACDEF_SAVE_DOC );
    toolBar.addActionDef( ACDEF_S5_SERVER_SELECT );
    toolBar.addActionDef( ACDEF_OPC_SERVER_SELECT );

    toolBar.addSeparator();

    Control toolbarCtrl = toolBar.createControl( frame );
    toolbarCtrl.setLayoutData( BorderLayout.NORTH );

    textContr1 = new TextControlContribution( "Label", 200, "Sk Connection: workroom", SWT.NONE );
    toolBar.addContributionItem( textContr1 );
    toolBar.addSeparator();
    textContr2 = new TextControlContribution( "Label2", 200, "Opc ua:", SWT.NONE );
    toolBar.addContributionItem( textContr2 );

    IM5Domain m5 = conn.scope().get( IM5Domain.class );

    toolBar.addListener( aActionId -> {
      if( aActionId.equals( ACDEF_SAVE_DOC.id() ) ) {
        OpcToS5DataCfgDocService service = ctx.get( OpcToS5DataCfgDocService.class );
        service.saveCfgDoc( aSelDoc );
        return;
      }
      if( aActionId.equals( ACDEF_S5_SERVER_SELECT.id() ) ) {
        ISkideExternalConnectionsService connService =
            ctx.eclipseContext().get( ISkideExternalConnectionsService.class );
        IdChain idChain = connService.selectConfigAndOpenConnection( ctx );
        if( idChain != null ) {
          ctx.put( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_S5_CONNECTION, idChain );
          textContr1.setText( "Sk Connection: " + idChain.first() );
        }

        return;
      }
      if( aActionId.equals( ACDEF_OPC_SERVER_SELECT.id() ) ) {
        IOpcUaServerConnCfg conConf = OpcUaUtils.selectOpcServerConfig( ctx );
        // dima 13.10.23 сохраним в контекст
        ctx.put( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG, conConf );

        Display.getDefault()
            .asyncExec( () -> textContr2.setText( "Opc ua: " + (conConf == null ? "" : conConf.nmName()) ) );

        if( conConf == null ) {
          return;
        }

        OpcUaUtils.runInWaitingDialog( ctx, "Создание и подключение к OPC UA", monitor -> {
          try {

            monitor.subTask( "Проверка наличия кэша киента opc ua" );
            boolean hasCach = OpcUaUtils.hasCachedOpcUaNodesTreeFor( ctx, conConf );
            // если есть кэш - не пытаться соединится
            if( hasCach ) {
              Display.getDefault().asyncExec( () -> {
                textContr2.setText( "Opc ua: Cach: " + conConf.nmName() );
                TsDialogUtils.info( getShell(), "Кэш соединения обнаружен и будет использоваться в редакторе" );
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
            monitor.subTask( "Создание киента opc ua" );
            client = OpcUaUtils.createClient( conConf );

            monitor.subTask( "Попытка соединения киента opc ua с сервером" );
            // В диалоге ожидания попытаться соединится
            client.connect().get();
            ctx.put( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_OPC_CONNECTION, client );

            Display.getDefault().asyncExec( () -> textContr2.setText( "Opc ua: connected: " + conConf.nmName() ) );        
          }
          catch( Exception ex ) {
            Display.getDefault()
                .asyncExec( () -> TsDialogUtils.error( getShell(), "Ошибка создания соединения с OPC UA сервером" ) );
            LoggerUtils.errorLogger().error( ex );
            return;
          }

          try {
            monitor.subTask( "Создание кэша киента opc ua" );
            // при успешном соединении - записать кэш
            IM5Model<UaTreeNode> model = m5.getModel( OpcUaNodeModel.MODEL_ID, UaTreeNode.class );
            IM5LifecycleManager<UaTreeNode> lm =
                new OpcUaNodeM5LifecycleManager( model, client, Identifiers.RootFolder, ctx, conConf );
            lm.itemsProvider().listItems();
            Display.getDefault().asyncExec( () -> {
              textContr2.setText( "Opc ua: Cach: " + conConf.nmName() );
              TsDialogUtils.info( getShell(), "Кэш соединения создан и будет использоваться в редакторе" );
            } );
          }
          catch( Exception ex ) {
            Display.getDefault().asyncExec(
                () -> TsDialogUtils.error( getShell(), "Ошибка создания кэша соединения с OPC UA сервером" ) );
            LoggerUtils.errorLogger().error( ex );
            return;
          }
        } );

        return;
      }

    } );

    // Связи
    IM5Model<OpcToS5DataCfgUnit> model = m5.getModel( OpcToS5DataCfgUnitM5Model.MODEL_ID, OpcToS5DataCfgUnit.class );

    // IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
    // avValobj( EBorderLayoutPlacement.SOUTH ) );
    // IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );

    IM5LifecycleManager<OpcToS5DataCfgUnit> lm = new OpcToS5DataCfgUnitM5LifecycleManager( model, aSelDoc );
    IM5CollectionPanel<OpcToS5DataCfgUnit> opcToS5DataCfgUnitPanel =
        model.panelCreator().createCollEditPanel( ctx, lm.itemsProvider(), lm );

    tabCfgUnitsItem.setControl( opcToS5DataCfgUnitPanel.createControl( tabSubFolder ) );

    // Узлы
    ITsGuiContext ctx2 = new TsGuiContext( ctx );
    ctx2.params().addAll( ctx.params() );
    IM5Model<CfgOpcUaNode> nodeModel = m5.getModel( CfgOpcUaNodeM5Model.MODEL_ID, CfgOpcUaNode.class );

    // IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
    // avValobj( EBorderLayoutPlacement.SOUTH ) );
    // IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx2.params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx2.params(), AvUtils.AV_TRUE );

    IM5LifecycleManager<CfgOpcUaNode> nodeLm = nodeModel.getLifecycleManager( aSelDoc );

    IM5CollectionPanel<CfgOpcUaNode> cfgNodesPanel =
        nodeModel.panelCreator().createCollEditPanel( ctx2, nodeLm.itemsProvider(), nodeLm );

    tabCfgNodesItem.setControl( cfgNodesPanel.createControl( tabSubFolder ) );

    tabSubFolder.setSelection( tabCfgUnitsItem );
  }

  static class TextControlContribution
      extends ControlContribution {

    private final int width;
    private final int swtStyle;
    private String    text;
    CLabel            label;

    /**
     * Конструктор.
     *
     * @param aId String - ИД элемента
     * @param aWidth int - ширина текстового поля
     * @param aText String - текст
     * @param aSwtStyle int - swt стиль
     */
    public TextControlContribution( String aId, int aWidth, String aText, int aSwtStyle ) {
      super( aId );
      width = aWidth;
      swtStyle = aSwtStyle;
      text = aText;
    }

    // ------------------------------------------------------------------------------------
    // ControlContribution
    //

    @Override
    protected Control createControl( Composite aParent ) {
      label = new CLabel( aParent, swtStyle );
      label.setText( text );
      label.setAlignment( SWT.LEFT );
      return label;
    }

    @Override
    protected int computeWidth( Control aControl ) {
      if( width == SWT.DEFAULT ) {
        return super.computeWidth( aControl );
      }
      return width;
    }

    // ------------------------------------------------------------------------------------
    // API
    //

    /**
     * Возвращает текстовое поле.
     *
     * @return CLabel - текстовое поле
     */
    public CLabel label() {
      return label;
    }

    void setText( String aText ) {
      label.setText( aText );
      label.redraw();
    }

  }

}
