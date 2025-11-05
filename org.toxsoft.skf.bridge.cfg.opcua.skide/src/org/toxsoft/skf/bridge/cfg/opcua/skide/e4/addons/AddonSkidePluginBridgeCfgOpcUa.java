package org.toxsoft.skf.bridge.cfg.opcua.skide.e4.addons;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.mws.bases.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;
import org.toxsoft.skf.bridge.cfg.opcua.skide.*;
import org.toxsoft.skf.bridge.cfg.opcua.skide.Activator;
import org.toxsoft.skf.bridge.cfg.opcua.skide.main.*;
import org.toxsoft.skide.core.api.*;

/**
 * Plugin addon.
 *
 * @author max
 */
public class AddonSkidePluginBridgeCfgOpcUa
    extends MwsAbstractAddon {

  /**
   * Constructor.
   */
  public AddonSkidePluginBridgeCfgOpcUa() {
    super( Activator.PLUGIN_ID );
  }

  // ------------------------------------------------------------------------------------
  // MwsAbstractAddon
  //

  @Override
  protected void doRegisterQuants( IQuantRegistrator aQuantRegistrator ) {
    aQuantRegistrator.registerQuant( new QuantBridgeCfgOpcUa() );
  }

  @Override
  protected void initApp( IEclipseContext aAppContext ) {
    ISkideEnvironment skEnv = aAppContext.get( ISkideEnvironment.class );
    skEnv.pluginsRegistrator().registerPlugin( SkidePluginBridgeCfgOpcUa.INSTANCE );
  }

  @Override
  protected void initWin( IEclipseContext aWinContext ) {
    ISkidePluginBridgeCfgOpcUaConstants.init( aWinContext );
    //
  }

}
