package org.toxsoft.skf.bridge.cfg.s7.skide.main;

import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.s7.skide.ISkidePluginBridgeCfgS7Constants.*;
import static org.toxsoft.skf.bridge.cfg.s7.skide.l10n.ISkidePluginTemplateSharedResources.*;
import static org.toxsoft.skide.core.ISkideCoreConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.skide.core.api.*;

/**
 * SkIDE plugin: S7 confuguration editor.
 *
 * @author dima
 */
public class SkidePluginBridgeCfgS7
    extends AbstractSkidePlugin {

  /**
   * The plugin ID.
   */
  public static final String SKIDE_PLUGIN_ID = SKIDE_FULL_ID + ".plugin.bridge.cfg.S7"; //$NON-NLS-1$

  /**
   * The singleton instance.
   */
  public static final AbstractSkidePlugin INSTANCE = new SkidePluginBridgeCfgS7();

  SkidePluginBridgeCfgS7() {
    super( SKIDE_PLUGIN_ID, OptionSetUtils.createOpSet( //
        TSID_NAME, STR_PLUGIN_BRIDGE_CFG_S7, //
        TSID_DESCRIPTION, STR_PLUGIN_BRIDGE_CFG_S7_D, //
        TSID_ICON_ID, ICONID_APP_S7_EDITOR //
    ) );
  }

  @Override
  protected void doCreateUnits( ITsGuiContext aContext, IStridablesListEdit<ISkideUnit> aUnitsList ) {
    aUnitsList.add( new SkideUnitBridgeCfgS7( aContext, this ) );
  }

}
