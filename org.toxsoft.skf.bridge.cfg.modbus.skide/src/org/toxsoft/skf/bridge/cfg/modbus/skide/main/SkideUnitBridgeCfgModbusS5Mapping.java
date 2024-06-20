package org.toxsoft.skf.bridge.cfg.modbus.skide.main;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.modbus.skide.ISkidePluginBridgeCfgModbusConstants.*;
import static org.toxsoft.skf.bridge.cfg.modbus.skide.main.ISkResources.*;
import static org.toxsoft.skide.core.ISkideCoreConstants.*;
import static org.toxsoft.skide.core.api.ucateg.ISkideUnitCategoryConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.impl.*;

/**
 * SkiDE unit: unit Modbus bridge cfg - mapping of registeres and s5 entities.
 *
 * @author max
 */
public class SkideUnitBridgeCfgModbusS5Mapping
    extends AbstractSkideUnit {

  /**
   * The plugin ID.
   */
  public static final String UNIT_ID = SKIDE_FULL_ID + ".unit.bridge.cfg.modbus.s5.mapping"; //$NON-NLS-1$

  SkideUnitBridgeCfgModbusS5Mapping( ITsGuiContext aContext, AbstractSkidePlugin aCreator ) {
    super( UNIT_ID, OptionSetUtils.createOpSet( //
        TSID_NAME, STR_N_MODBUS_S5_CONFIG_EDITOR, //
        TSID_DESCRIPTION, STR_D_MODBUS_S5_CONFIG_EDITOR, //
        OPDEF_SKIDE_UNIT_CATEGORY, UCATEGID_EXTERNAL_SYSTEMS, //
        TSID_ICON_ID, ICONID_APP_MODBUS_EDITOR //
    ), aContext, aCreator );
    unitActions().add( ACDEF_ABOUT );
  }

  @Override
  protected AbstractSkideUnitPanel doCreateUnitPanel( ITsGuiContext aContext ) {
    return new SkideUnitPanelBridgeCfgModbusS5Mapping( aContext, this );
  }

}
