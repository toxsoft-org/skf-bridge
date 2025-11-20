package org.toxsoft.skf.bridge.cfg.modbus.gui.panels;

import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.IBridgeCfgModbusGuiConstants.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.l10n.ISkBridgeCfgModbusGuiSharedResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tsgui.bricks.actions.*;

/**
 * Package internal constants.
 *
 * @author hazard157
 */
interface IPackageConstants {

  String ACTID_EDIT_UNITS        = SK_ID + ".act.EditUnits";         //$NON-NLS-1$
  String ACTID_FILTER_BY_ADDRESS = SK_ID + ".act.filter_by_address"; //$NON-NLS-1$

  String ACTID_COPY_ALL = SK_ID + ".act.bridge.cfg.modbus.copy.all"; //$NON-NLS-1$
  String ACTID_SAVE_DOC = SK_ID + ".act.save_doc";                   //$NON-NLS-1$

  TsActionDef ACDEF_EDIT_UNITS =
      TsActionDef.ofPush2( ACTID_EDIT_UNITS, STR_N_EDIT_CONFIG_SET, STR_D_EDIT_CONFIG_SET, ICONID_MODBUS_INOUT_EDIT );

  TsActionDef ACDEF_FILTER_BY_ADDRESS = TsActionDef.ofCheck2( ACTID_FILTER_BY_ADDRESS, STR_ACT_FILTER_BY_ADDRESS,
      STR_ACT_FILTER_BY_ADDRESS_D, ICONID_VIEW_FILTER );

  TsActionDef ACDEF_SAVE_DOC =
      TsActionDef.ofPush2( ACTID_SAVE_DOC, STR_N_SAVE_CONFIG, STR_D_SAVE_CONFIG, ICONID_DOCUMENT_SAVE );

  TsActionDef ACDEF_COPY_ALL =
      TsActionDef.ofPush2( ACTID_COPY_ALL, STR_ACT_MULTI_COPY, STR_ACT_MULTI_COPY_D, ICONID_LIST_ADD_ALL );

}
