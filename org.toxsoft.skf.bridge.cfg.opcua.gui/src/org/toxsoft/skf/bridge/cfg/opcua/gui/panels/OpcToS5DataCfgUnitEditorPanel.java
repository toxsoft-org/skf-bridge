package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * Editor panel for creating, editing, deleting opc to s5 cfg units.
 *
 * @author max
 */
public class OpcToS5DataCfgUnitEditorPanel
    extends TsPanel {

  final ISkConnection conn;

  IM5CollectionPanel<OpcToS5DataCfgUnit> opcUaConnCfgPanel;

  /**
   * Конструктор панели.
   * <p>
   * Конструктор просто запоминает ссылку на контекст, без создания копии.
   *
   * @param aParent {@link Composite} - родительская панель
   * @param aContext {@link ITsGuiContext} - контекст панели
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public OpcToS5DataCfgUnitEditorPanel( Composite aParent, ITsGuiContext aContext ) {
    super( aParent, aContext );
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<OpcToS5DataCfgUnit> model = m5.getModel( OpcToS5DataCfgUnitM5Model.MODEL_ID, OpcToS5DataCfgUnit.class );

    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );
    // IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
    // avValobj( EBorderLayoutPlacement.SOUTH ) );
    // IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    // IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );

    // MultiPaneComponentModown<OpcToS5DataCfgUnit> componentModown =
    // new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm );
    IM5LifecycleManager<OpcToS5DataCfgUnit> lm = new OpcToS5DataCfgUnitM5LifecycleManager( model,
        new OpcToS5DataCfgDoc( TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING ) );
    opcUaConnCfgPanel = model.panelCreator().createCollEditPanel( ctx, lm.itemsProvider(), lm );

    // new M5CollectionPanelMpcModownWrapper<>( componentModown, false );

    opcUaConnCfgPanel.createControl( this );

  }

}
