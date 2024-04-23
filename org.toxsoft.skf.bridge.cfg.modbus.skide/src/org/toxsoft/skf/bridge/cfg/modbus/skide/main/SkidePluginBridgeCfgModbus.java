package org.toxsoft.skf.bridge.cfg.modbus.skide.main;

import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.modbus.skide.ISkidePluginBridgeCfgModbusConstants.*;
import static org.toxsoft.skide.core.ISkideCoreConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.skide.core.api.*;

/**
 * SkIDE plugin: Modbus bridge cfg.
 *
 * @author max
 */
public class SkidePluginBridgeCfgModbus
    extends AbstractSkidePlugin {

  /**
   * The plugin ID.
   */
  public static final String SKIDE_PLUGIN_ID = SKIDE_FULL_ID + ".plugin.bridge.cfg.modbus"; //$NON-NLS-1$

  /**
   * The singleton instance.
   */
  public static final AbstractSkidePlugin INSTANCE = new SkidePluginBridgeCfgModbus();

  SkidePluginBridgeCfgModbus() {
    super( SKIDE_PLUGIN_ID, OptionSetUtils.createOpSet( //
        TSID_NAME, "STR_SKIDE_PLUGIN_BRIDGE_CFG_OPC_UA", //
        TSID_DESCRIPTION, "STR_SKIDE_PLUGIN_BRIDGE_CFG_OPC_UA_D", //
        TSID_ICON_ID, ICONID_SKIDE_PLUGIN //
    ) );
  }

  @Override
  protected void doCreateUnits( ITsGuiContext aContext, IStridablesListEdit<ISkideUnit> aUnitsList ) {
    aUnitsList.add( new SkideUnitBridgeCfgModbusS5Mapping( aContext, this ) );
    // aUnitsList.add( new SkideUnitBridgeCfgOpcUaToS5( aContext, this ) );
  }

}
