package org.toxsoft.skf.bridge.cfg.s7.skide.main;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.skf.brifge.cfg.s7.gui.panels.S7ToS5CfgDocEditorPanel;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.impl.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * {@link AbstractSkideUnitPanel} implementation.
 *
 * @author dima
 */
class SkidePanelBridgeCfgS7SkMapping
    extends AbstractSkideUnitPanel {

  public SkidePanelBridgeCfgS7SkMapping( ITsGuiContext aContext, ISkideUnit aUnit ) {
    super( aContext, aUnit );
  }

  @Override
  protected Control doCreateControl( Composite aParent ) {
    ISkConnectionSupplier connSup = tsContext().get( ISkConnectionSupplier.class );

    IM5Domain m5 = connSup.defConn().scope().get( IM5Domain.class );
    TsGuiContext reportContext = new TsGuiContext( m5.tsContext() );

    S7ToS5CfgDocEditorPanel panel = new S7ToS5CfgDocEditorPanel( aParent, reportContext );
    panel.setLayoutData( BorderLayout.CENTER );
    return panel;
  }

}
