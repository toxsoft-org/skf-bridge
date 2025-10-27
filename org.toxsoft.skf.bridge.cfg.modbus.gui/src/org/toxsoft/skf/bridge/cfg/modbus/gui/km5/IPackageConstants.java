package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.l10n.ISkBridgeCfgModbusGuiSharedResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tsgui.bricks.actions.*;

/**
 * Package internal constants.
 *
 * @author hazard157
 */
interface IPackageConstants {

  String ACTID_MANUAL_ADD  = SK_ID + ".act.manual_add";  //$NON-NLS-1$
  String ACTID_MANUAL_EDIT = SK_ID + ".act.manual_edit"; //$NON-NLS-1$

  TsActionDef ACDEF_MANUAL_ADD = TsActionDef.ofPush2( ACTID_MANUAL_ADD, //
      STR_ACT_MANUAL_ADD_D, STR_ACT_MANUAL_ADD_D, ICONID_LIST_ADD );

  TsActionDef ACDEF_MANUAL_EDIT = TsActionDef.ofPush2( ACTID_MANUAL_EDIT, //
      STR_ACT_MANUAL_EDIT, STR_ACT_MANUAL_EDIT_D, ICONID_DOCUMENT_EDIT );

  /**
   * Id of translator refbook.
   */
  String REG_TRANSLATOR_REFBOOK = "reg.translator"; //$NON-NLS-1$

  /**
   * Id of parametor words count in translator refbook.
   */
  String REG_TRANS_REF_ATTR_WORDS_COUNT = "wordsCount"; //$NON-NLS-1$

  /**
   * Id of parametor request type in translator refbook.
   */
  String REG_TRANS_REF_ATTR_REQ_TYPE = "requestType"; //$NON-NLS-1$

  /**
   * Id of parametor value type in translator refbook.
   */
  String REG_TRANS_REF_ATTR_VAL_TYPE = "valueType"; //$NON-NLS-1$

  /**
   * Id of parametor params in translator refbook.
   */
  String REG_TRANS_REF_ATTR_PARAMS = "params"; //$NON-NLS-1$

}
