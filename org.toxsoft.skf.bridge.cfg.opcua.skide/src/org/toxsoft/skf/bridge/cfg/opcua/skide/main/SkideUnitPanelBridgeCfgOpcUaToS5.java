package org.toxsoft.skf.bridge.cfg.opcua.skide.main;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.*;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.impl.*;

/**
 * {@link AbstractSkideUnitPanel} implementation.
 *
 * @author max
 */
public class SkideUnitPanelBridgeCfgOpcUaToS5
    extends AbstractSkideUnitPanel {

  /**
   * Constructor by context and unit
   *
   * @param aContext ITsGuiContext - context.
   * @param aUnit ISkideUnit - unit.
   */
  public SkideUnitPanelBridgeCfgOpcUaToS5( ITsGuiContext aContext, ISkideUnit aUnit ) {
    super( aContext, aUnit );
  }

  @Override
  protected Control doCreateControl( Composite aParent ) {
    TsGuiContext reportContext = new TsGuiContext( tsContext() );

    OpcToS5DataCfgDocEditorPanel panel = new OpcToS5DataCfgDocEditorPanel( aParent, reportContext );
    panel.setLayoutData( BorderLayout.CENTER );
    return panel;
  }

}
