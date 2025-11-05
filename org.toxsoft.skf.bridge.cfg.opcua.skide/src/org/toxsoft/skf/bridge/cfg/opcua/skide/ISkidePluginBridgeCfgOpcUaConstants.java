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

  String PREFIX_OF_ICON_FIELD_NAME = "ICONID_";     //$NON-NLS-1$
  String ICONID_OPCUA_RUN_1        = "opcua-run-1"; //$NON-NLS-1$
  String ICONID_OPCUA_RUN_2        = "opcua-run-2"; //$NON-NLS-1$
  String ICONID_OPCUA_SKIDE        = "opcua-skide"; //$NON-NLS-1$
  String ICONID_OPCUA_EDIT         = "opcua-edit";  //$NON-NLS-1$

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
