package org.toxsoft.skf.bridge.cfg.s7.skide.main;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.s7.skide.l10n.ISkidePluginTemplateSharedResources.*;
import static org.toxsoft.skf.bridge.cfg.s7.skide.ISkidePluginBridgeCfgS7Constants.*;
import static org.toxsoft.skide.core.ISkideCoreConstants.*;
import static org.toxsoft.skide.core.api.ucateg.ISkideUnitCategoryConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.impl.*;

/**
 * SkiDE unit: unit template 1.
 *
 * @author hazard157
 */
public class SkideUnitBridgeCfgS7
    extends AbstractSkideUnit {

  /**
   * The plugin ID.
   */
  public static final String UNIT_ID = SKIDE_FULL_ID + ".unit.S7.cfg.editor"; //$NON-NLS-1$

  SkideUnitBridgeCfgS7( ITsGuiContext aContext, AbstractSkidePlugin aCreator ) {
    super( UNIT_ID, OptionSetUtils.createOpSet( //
        TSID_NAME, STR_UNIT_BRIDGE_CFG_S7, //
        TSID_DESCRIPTION, STR_UNIT_BRIDGE_CFG_S7_D, //
        OPDEF_SKIDE_UNIT_CATEGORY, UCATEGID_EXTERNAL_SYSTEMS, //
        TSID_ICON_ID, ICONID_APP_S7_EDITOR //
    ), aContext, aCreator );
    unitActions().add( ACDEF_ABOUT );
  }

  @Override
  protected AbstractSkideUnitPanel doCreateUnitPanel( ITsGuiContext aContext ) {
    return new SkidePanelBridgeCfgS7SkMapping( aContext, this );
  }

}
