package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
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
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
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

    // ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    // TsInternalErrorRtException.checkNull( workroom );
    // IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();

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
          protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
              IListEdit<ITsActionDef> aActs ) {
            aActs.add( ACDEF_SEPARATOR );
            aActs.add( ACDEF_EDIT_UNITS );
            aActs.add( ACDEF_EDIT_NODES );

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
            OpcToS5DataCfgDoc selDoc = selectedItem();

            switch( aActionId ) {

              case ACTID_EDIT_UNITS:
                editOpcCfgDoc( selDoc );
                break;

              case ACTID_EDIT_NODES:
                editOpcCfgNodes( selDoc );
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

    // создаем новую закладку
    CTabItem tabItem = new CTabItem( tabFolder, SWT.CLOSE );
    tabItem.setText( aSelDoc.nmName() );

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<OpcToS5DataCfgUnit> model = m5.getModel( OpcToS5DataCfgUnitM5Model.MODEL_ID, OpcToS5DataCfgUnit.class );

    // ITsGuiContext ctx = new TsGuiContext( tsContext() );
    // ctx.params().addAll( tsContext().params() );

    // IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
    // avValobj( EBorderLayoutPlacement.SOUTH ) );
    // IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( tsContext().params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( tsContext().params(), AvUtils.AV_TRUE );

    // MultiPaneComponentModown<OpcToS5DataCfgUnit> componentModown2 =
    // new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm );
    // IM5CollectionPanel<OpcToS5DataCfgUnit> opcToS5DataCfgUnitPanel =
    // new M5CollectionPanelMpcModownWrapper<>( componentModown2, false );

    IM5LifecycleManager<OpcToS5DataCfgUnit> lm = new OpcToS5DataCfgUnitM5LifecycleManager( model, aSelDoc );
    IM5CollectionPanel<OpcToS5DataCfgUnit> opcToS5DataCfgUnitPanel =
        model.panelCreator().createCollEditPanel( tsContext(), lm.itemsProvider(), lm );

    tabItem.setControl( opcToS5DataCfgUnitPanel.createControl( tabFolder ) );

    tabFolder.setSelection( tabItem );

  }

  protected void editOpcCfgNodes( OpcToS5DataCfgDoc aSelDoc ) {

    // создаем новую закладку
    CTabItem tabItem = new CTabItem( tabFolder, SWT.CLOSE );
    tabItem.setText( "Узлы " + aSelDoc.nmName() );

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<CfgOpcUaNode> model = m5.getModel( CfgOpcUaNodeM5Model.MODEL_ID, CfgOpcUaNode.class );

    // ITsGuiContext ctx = new TsGuiContext( tsContext() );
    // ctx.params().addAll( tsContext().params() );

    // IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
    // avValobj( EBorderLayoutPlacement.SOUTH ) );
    // IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( tsContext().params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( tsContext().params(), AvUtils.AV_TRUE );

    // MultiPaneComponentModown<OpcToS5DataCfgUnit> componentModown2 =
    // new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm );
    // IM5CollectionPanel<OpcToS5DataCfgUnit> opcToS5DataCfgUnitPanel =
    // new M5CollectionPanelMpcModownWrapper<>( componentModown2, false );

    ensureNodesCfgs( tsContext(), aSelDoc );

    IM5LifecycleManager<CfgOpcUaNode> lm = model.getLifecycleManager( aSelDoc );

    IM5CollectionPanel<CfgOpcUaNode> cfgNodesPanel =
        model.panelCreator().createCollEditPanel( tsContext(), lm.itemsProvider(), lm );

    tabItem.setControl( cfgNodesPanel.createControl( tabFolder ) );

    tabFolder.setSelection( tabItem );

  }

  /**
   * Synchronizes loaded and existed in units nodes cfgs.
   */
  private void ensureNodesCfgs( ITsGuiContext aContext, OpcToS5DataCfgDoc aDoc ) {

    // OpcUaServerConnCfg conConf =
    // (OpcUaServerConnCfg)aContext.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );
    //
    // if( conConf == null ) {
    // TsDialogUtils.askYesNoCancel( getShell(),
    // "Для корректного автоматического определения типа узлов OPC UA следует выбрать соединение с сервером OPC UA" );
    // }

    IList<OpcToS5DataCfgUnit> dataCfgUnits = aDoc.dataUnits();

    IList<CfgOpcUaNode> nodesCfgsList = aDoc.getNodesCfgs();
    IStringMapEdit<CfgOpcUaNode> nodesCfgs = new StringMap<>();
    // for( CfgOpcUaNode node : nodesCfgsList ) {
    // nodesCfgs.put( node.getNodeId(), node );
    // }

    for( OpcToS5DataCfgUnit unit : dataCfgUnits ) {
      IList<NodeId> nodes = unit.getDataNodes();

      String relizationTypeId = unit.getRelizationTypeId();
      CfgUnitRealizationTypeRegister typeReg2 = m5().tsContext().get( CfgUnitRealizationTypeRegister.class );

      ICfgUnitRealizationType realType = typeReg2.getTypeOfRealizationById( unit.getTypeOfCfgUnit(), relizationTypeId );

      for( int i = 0; i < nodes.size(); i++ ) {
        NodeId node = nodes.get( i );
        if( !nodesCfgs.hasKey( node.toParseableString() ) ) {
          nodesCfgs.put( node.toParseableString(),
              realType.createInitCfg( aContext, node.toParseableString(), i, nodes.size() ) );
          // new CfgOpcUaNode( node.toParseableString(), false, true, false, EAtomicType.INTEGER ) );
        }
      }

      aDoc.setNodesCfgs( nodesCfgs.values() );
    }
  }

}
