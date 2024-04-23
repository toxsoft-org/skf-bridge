package org.toxsoft.skf.bridge.cfg.modbus.skide.main;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.panels.*;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.impl.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * {@link AbstractSkideUnitPanel} implementation.
 *
 * @author max
 */
public class SkideUnitPanelBridgeCfgModbusS5Mapping
    extends AbstractSkideUnitPanel {

  /**
   * Constructor by context and unit
   *
   * @param aContext ITsGuiContext - context.
   * @param aUnit ISkideUnit - unit.
   */
  public SkideUnitPanelBridgeCfgModbusS5Mapping( ITsGuiContext aContext, ISkideUnit aUnit ) {
    super( aContext, aUnit );
  }

  @Override
  protected Control doCreateControl( Composite aParent ) {
    ISkConnectionSupplier connSup = tsContext().get( ISkConnectionSupplier.class );

    IM5Domain m5 = connSup.defConn().scope().get( IM5Domain.class );
    TsGuiContext reportContext = new TsGuiContext( m5.tsContext() );

    ModbusToS5CfgDocEditorPanel panel = new ModbusToS5CfgDocEditorPanel( aParent, reportContext );
    panel.setLayoutData( BorderLayout.CENTER );
    return panel;
  }

}
