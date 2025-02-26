package org.toxsoft.skf.bridge.cfg.opcua.skide;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.graphics.icons.*;

/**
 * Application common constants.
 *
 * @author max
 */
@SuppressWarnings( "javadoc" )
public interface ISkidePluginBridgeCfgOpcUaConstants {

  // ------------------------------------------------------------------------------------
  // Icons

  String PREFIX_OF_ICON_FIELD_NAME = "ICONID_";          //$NON-NLS-1$
  String ICONID_SKIDE_PLUGIN       = "opc_ua_browser-2"; //$NON-NLS-1$
  String ICONID_CFG_UNIT           = "opc_ua_browser-1"; //$NON-NLS-1$
  String ICONID_SD_SKIDE_PLUGIN    = "OPS-configs";      //$NON-NLS-1$
  String ICONID_CFG_SKIDE_PLUGIN   = "OPS-edit";         //$NON-NLS-1$

  /**
   * Constants registration.
   *
   * @param aWinContext {@link IEclipseContext} - windows level context
   */
  static void init( IEclipseContext aWinContext ) {
    ITsIconManager iconManager = aWinContext.get( ITsIconManager.class );
    iconManager.registerStdIconByIds( Activator.PLUGIN_ID, ISkidePluginBridgeCfgOpcUaConstants.class,
        PREFIX_OF_ICON_FIELD_NAME );
    //
  }

}
