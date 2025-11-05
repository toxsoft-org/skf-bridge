package org.toxsoft.skf.bridge.cfg.opcua.skide.main;

import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.skide.ISkidePluginBridgeCfgOpcUaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.skide.l10n.ISkfBridgeCfgOpcUaSkideSharedResources.*;
import static org.toxsoft.skide.core.ISkideCoreConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.skide.core.api.*;

/**
 * SkIDE plugin: Opc UA bridge cfg.
 *
 * @author max
 */
public class SkidePluginBridgeCfgOpcUa
    extends AbstractSkidePlugin {

  /**
   * The plugin ID.
   */
  public static final String SKIDE_PLUGIN_ID = SKIDE_FULL_ID + ".plugin.bridge.cfg.opcua"; //$NON-NLS-1$

  /**
   * The singleton instance.
   */
  public static final AbstractSkidePlugin INSTANCE = new SkidePluginBridgeCfgOpcUa();

  SkidePluginBridgeCfgOpcUa() {
    super( SKIDE_PLUGIN_ID, OptionSetUtils.createOpSet( //
        TSID_NAME, STR_SKIDE_PLUGIN_BRIDGE_CFG_OPC_UA, //
        TSID_DESCRIPTION, STR_SKIDE_PLUGIN_BRIDGE_CFG_OPC_UA_D, //
        TSID_ICON_ID, ICONID_OPCUA_RUN_1 //
    ) );
  }

  @Override
  protected void doCreateUnits( ITsGuiContext aContext, IStridablesListEdit<ISkideUnit> aUnitsList ) {
    aUnitsList.add( new SkideUnitBridgeCfgOpcUa( aContext, this ) );
    aUnitsList.add( new SkideUnitBridgeCfgOpcUaToS5( aContext, this ) );
  }

}
