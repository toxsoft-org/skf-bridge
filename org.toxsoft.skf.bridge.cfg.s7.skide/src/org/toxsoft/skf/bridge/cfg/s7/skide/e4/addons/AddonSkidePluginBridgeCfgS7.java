package org.toxsoft.skf.bridge.cfg.s7.skide.e4.addons;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.mws.bases.*;
import org.toxsoft.skf.bridge.cfg.s7.skide.*;
import org.toxsoft.skf.bridge.cfg.s7.skide.main.*;
import org.toxsoft.skide.core.api.*;

/**
 * Plugin addon.
 *
 * @author hazard157
 */
public class AddonSkidePluginBridgeCfgS7
    extends MwsAbstractAddon {

  /**
   * Constructor.
   */
  public AddonSkidePluginBridgeCfgS7() {
    super( Activator.PLUGIN_ID );
  }

  // ------------------------------------------------------------------------------------
  // MwsAbstractAddon
  //

  @Override
  protected void initApp( IEclipseContext aAppContext ) {
    ISkideEnvironment skEnv = aAppContext.get( ISkideEnvironment.class );
    skEnv.pluginsRegistrator().registerPlugin( SkidePluginBridgeCfgS7.INSTANCE );
  }

  @Override
  protected void initWin( IEclipseContext aWinContext ) {
    ISkidePluginBridgeCfgS7Constants.init( aWinContext );
    //
  }

}
