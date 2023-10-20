package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
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
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
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

  final ISkConnection conn;

  IM5CollectionPanel<OpcToS5DataCfgDoc> opcToS5DataCfgDocPanel;

  private CTabFolder tabFolder;

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

    CTabFolder tabSubFolder = new CTabFolder( tabFolder, SWT.BORDER );

    tabItem.setControl( tabSubFolder );
    tabFolder.setSelection( tabItem );

    // Создаём закладку для конфигурации связей opc-sk
    CTabItem tabCfgUnitsItem = new CTabItem( tabSubFolder, SWT.NONE );
    tabCfgUnitsItem.setText( "Связи" );

    // Создаём закладку для конфигурации связей opc-sk
    CTabItem tabCfgNodesItem = new CTabItem( tabSubFolder, SWT.NONE );
    tabCfgNodesItem.setText( "Узлы" );

    IM5Domain m5 = conn.scope().get( IM5Domain.class );

    // Связи
    IM5Model<OpcToS5DataCfgUnit> model = m5.getModel( OpcToS5DataCfgUnitM5Model.MODEL_ID, OpcToS5DataCfgUnit.class );

    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    ctx.params().addAll( tsContext().params() );

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

}
