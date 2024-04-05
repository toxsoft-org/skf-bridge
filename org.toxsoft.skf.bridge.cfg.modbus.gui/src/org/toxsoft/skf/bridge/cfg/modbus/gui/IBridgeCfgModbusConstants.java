package org.toxsoft.skf.bridge.cfg.modbus.gui;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.graphics.icons.*;

/**
 * Константы пакета.
 *
 * @author max
 */
@SuppressWarnings( { "javadoc" } )
public interface IBridgeCfgModbusConstants {

  //
  // ------------------------------------------------------------------------------------
  // Icons

  String PREFIX_OF_ICON_FIELD_NAME = "ICONID_"; //$NON-NLS-1$

  String ICONID_EDIT_UNITS = "edit_units"; //$NON-NLS-1$

  /**
   * Constants registration.
   *
   * @param aWinContext {@link IEclipseContext} - windows level context
   */
  static void init( IEclipseContext aWinContext ) {
    ITsIconManager iconManager = aWinContext.get( ITsIconManager.class );
    iconManager.registerStdIconByIds( Activator.PLUGIN_ID, IBridgeCfgModbusConstants.class, PREFIX_OF_ICON_FIELD_NAME );
  }
}
