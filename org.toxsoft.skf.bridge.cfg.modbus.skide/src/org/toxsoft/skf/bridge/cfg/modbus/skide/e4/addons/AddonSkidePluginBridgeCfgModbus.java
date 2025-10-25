package org.toxsoft.skf.bridge.cfg.modbus.skide.e4.addons;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.mws.bases.*;
import org.toxsoft.skf.bridge.cfg.modbus.skide.*;
import org.toxsoft.skf.bridge.cfg.modbus.skide.main.*;
import org.toxsoft.skide.core.api.*;

/**
 * Plugin addon.
 *
 * @author max
 */
public class AddonSkidePluginBridgeCfgModbus
    extends MwsAbstractAddon {

  /**
   * Constructor.
   */
  public AddonSkidePluginBridgeCfgModbus() {
    super( Activator.PLUGIN_ID );
  }

  // ------------------------------------------------------------------------------------
  // MwsAbstractAddon
  //

  @Override
  protected void doRegisterQuants( IQuantRegistrator aQuantRegistrator ) {
    aQuantRegistrator.registerQuant( new org.toxsoft.skf.bridge.cfg.modbus.gui.QuantBridgeCfgModbus() );
  }

  @Override
  protected void initApp( IEclipseContext aAppContext ) {
    ISkideEnvironment skEnv = aAppContext.get( ISkideEnvironment.class );
    skEnv.pluginsRegistrator().registerPlugin( SkidePluginBridgeCfgModbus.INSTANCE );
  }

  @Override
  protected void initWin( IEclipseContext aWinContext ) {
    ISkidePluginBridgeCfgModbusConstants.init( aWinContext );
  }

}
