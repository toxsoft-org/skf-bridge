package org.toxsoft.skf.bridge.cfg.modbus.gui;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.graphics.icons.*;

/**
 * Константы пакета.
 *
 * @author max
 */
@SuppressWarnings( { "javadoc" } )
public interface IBridgeCfgModbusGuiConstants {

  //
  // ------------------------------------------------------------------------------------
  // Icons

  String PREFIX_OF_ICON_FIELD_NAME = "ICONID_";           //$NON-NLS-1$
  String ICONID_MODBUS_LOGO        = "modbus-logo";       //$NON-NLS-1$
  String ICONID_MODBUS_INOUT       = "modbus-inout";      //$NON-NLS-1$
  String ICONID_MODBUS_INOUT_EDIT  = "modbus-inout-edit"; //$NON-NLS-1$

  /**
   * Constants registration.
   *
   * @param aWinContext {@link IEclipseContext} - windows level context
   */
  static void init( IEclipseContext aWinContext ) {
    ITsIconManager iconManager = aWinContext.get( ITsIconManager.class );
    iconManager.registerStdIconByIds( Activator.PLUGIN_ID, IBridgeCfgModbusGuiConstants.class,
        PREFIX_OF_ICON_FIELD_NAME );
  }
}
