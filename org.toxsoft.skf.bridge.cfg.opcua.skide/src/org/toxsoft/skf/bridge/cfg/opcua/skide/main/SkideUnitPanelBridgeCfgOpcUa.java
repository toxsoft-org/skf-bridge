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
class SkideUnitPanelBridgeCfgOpcUa
    extends AbstractSkideUnitPanel {

  public SkideUnitPanelBridgeCfgOpcUa( ITsGuiContext aContext, ISkideUnit aUnit ) {
    super( aContext, aUnit );
  }

  @Override
  protected Control doCreateControl( Composite aParent ) {
    TsGuiContext reportContext = new TsGuiContext( tsContext() );
    OpcUaServerConnCfgEditorPanel panel = new OpcUaServerConnCfgEditorPanel( aParent, reportContext );
    panel.setLayoutData( BorderLayout.CENTER );
    return panel;
  }

}
