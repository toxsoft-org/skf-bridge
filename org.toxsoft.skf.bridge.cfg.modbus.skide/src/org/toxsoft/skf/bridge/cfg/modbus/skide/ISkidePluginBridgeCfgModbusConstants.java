package org.toxsoft.skf.bridge.cfg.modbus.skide;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.graphics.icons.*;

/**
 * Application common constants.
 *
 * @author max
 */
@SuppressWarnings( "javadoc" )
public interface ISkidePluginBridgeCfgModbusConstants {

  // ------------------------------------------------------------------------------------
  // Icons

  String PREFIX_OF_ICON_FIELD_NAME = "ICONID_";          //$NON-NLS-1$
  String ICONID_APP_MODBUS_INOUT   = "app-modbus-inout"; //$NON-NLS-1$
  String ICONID_APP_MODBUS_EDITOR  = "app-modbus";       //$NON-NLS-1$

  /**
   * Constants registration.
   *
   * @param aWinContext {@link IEclipseContext} - windows level context
   */
  static void init( IEclipseContext aWinContext ) {
    ITsIconManager iconManager = aWinContext.get( ITsIconManager.class );
    iconManager.registerStdIconByIds( Activator.PLUGIN_ID, ISkidePluginBridgeCfgModbusConstants.class,
        PREFIX_OF_ICON_FIELD_NAME );
    //
  }

}
