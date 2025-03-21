package org.toxsoft.skf.bridge.cfg.opcua.skide.main;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.skide.ISkidePluginBridgeCfgOpcUaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.skide.ISkidePluginBridgeCfgOpcUaSharedResources.*;
import static org.toxsoft.skide.core.ISkideCoreConstants.*;
import static org.toxsoft.skide.core.api.ucateg.ISkideUnitCategoryConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.impl.*;

/**
 * SkiDE unit: unit Opc UA bridge cfg to S5..
 *
 * @author max
 */
public class SkideUnitBridgeCfgOpcUaToS5
    extends AbstractSkideUnit {

  /**
   * The plugin ID.
   */
  public static final String UNIT_ID = SKIDE_FULL_ID + ".unit.bridge.cfg.opcua.to.s5"; //$NON-NLS-1$

  SkideUnitBridgeCfgOpcUaToS5( ITsGuiContext aContext, AbstractSkidePlugin aCreator ) {
    super( UNIT_ID, OptionSetUtils.createOpSet( //
        TSID_NAME, STR_SKIDE_BRIDGE_CFG_OPC_UA_TO_S5_UNIT, //
        TSID_DESCRIPTION, STR_SKIDE_BRIDGE_CFG_OPC_UA_TO_S5_UNIT_D, //
        OPDEF_SKIDE_UNIT_CATEGORY, UCATEGID_EXTERNAL_SYSTEMS, //
        TSID_ICON_ID, ICONID_SD_SKIDE_PLUGIN //
    ), aContext, aCreator );
    unitActions().add( ACDEF_ABOUT );
  }

  @Override
  protected AbstractSkideUnitPanel doCreateUnitPanel( ITsGuiContext aContext ) {
    return new SkideUnitPanelBridgeCfgOpcUaToS5( aContext, this );
  }
}
