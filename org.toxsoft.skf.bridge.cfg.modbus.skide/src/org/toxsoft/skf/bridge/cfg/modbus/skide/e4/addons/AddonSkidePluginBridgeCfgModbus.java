package org.toxsoft.skf.bridge.cfg.modbus.skide.e4.addons;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.mws.bases.*;
import org.toxsoft.skf.bridge.cfg.modbus.skide.*;
import org.toxsoft.skf.bridge.cfg.modbus.skide.main.*;
import org.toxsoft.skide.core.api.*;

import ru.toxsoft.val.lib.*;

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
    // dima 18.12.24 FIXME тупо заплатка для того чтобы использовать в SkIDE enum'ы предметной оласти проекта VALCOM
    ValcomUtils.initialize();
  }

  // ------------------------------------------------------------------------------------
  // MwsAbstractAddon
  //

  @Override
  protected void initApp( IEclipseContext aAppContext ) {
    ISkideEnvironment skEnv = aAppContext.get( ISkideEnvironment.class );
    skEnv.pluginsRegistrator().registerPlugin( SkidePluginBridgeCfgModbus.INSTANCE );
  }

  @Override
  protected void initWin( IEclipseContext aWinContext ) {
    ISkidePluginBridgeCfgModbusConstants.init( aWinContext );
    //
  }

  @Override
  protected void doRegisterQuants( IQuantRegistrator aQuantRegistrator ) {
    aQuantRegistrator.registerQuant( new org.toxsoft.skf.bridge.cfg.modbus.gui.QuantBridgeCfgModbus() );
  }

}
