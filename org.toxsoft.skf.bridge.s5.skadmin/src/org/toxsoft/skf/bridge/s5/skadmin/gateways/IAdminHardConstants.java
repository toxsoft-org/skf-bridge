package org.toxsoft.skf.bridge.s5.skadmin.gateways;

import static org.toxsoft.core.tslib.utils.TsLibUtils.*;
import static org.toxsoft.skf.bridge.s5.skadmin.gateways.IAdminHardResources.*;
import static org.toxsoft.uskat.skadmin.core.plugins.AdminPluginUtils.*;

import org.toxsoft.skf.bridge.s5.skadmin.AdminPluginGateways;
import org.toxsoft.uskat.skadmin.core.IAdminCmdArgDef;
import org.toxsoft.uskat.skadmin.core.impl.AdminCmdArgDef;

/**
 * Константы пакета.
 *
 * @author mvk
 */
@SuppressWarnings( "nls" )
interface IAdminHardConstants {

  /**
   * Префикс идентификаторов команд и их алиасов плагина
   */
  String CMD_PATH_PREFIX = AdminPluginGateways.GATEWAY_CMD_PATH;

  /**
   * Аргумент : Строковый идентификатор моста
   */
  IAdminCmdArgDef ARG_ID     = new AdminCmdArgDef( "id", DT_STRING_NULLABLE, STR_ARG_ID );
  /**
   * Аргумент : требование на все вопросы отвечать "yes"
   */
  IAdminCmdArgDef ARG_YES_ID = new AdminCmdArgDef( "y", DT_BOOLEAN_FALSE, STR_ARG_YES );

  // ------------------------------------------------------------------------------------
  // AdminCmdListConfigs
  //
  String CMD_LIST_CONFIGS_ID    = CMD_PATH_PREFIX + "listConfigs";
  String CMD_LIST_CONFIGS_ALIAS = EMPTY_STRING;
  String CMD_LIST_CONFIGS_NAME  = EMPTY_STRING;
  String CMD_LIST_CONFIGS_DESCR = STR_CMD_LIST_CONFIGS;

  // ------------------------------------------------------------------------------------
  // AdminCmdRemoveConfig
  //
  String CMD_REMOVE_CONFIG_ID    = CMD_PATH_PREFIX + "removeConfig";
  String CMD_REMOVE_CONFIG_ALIAS = EMPTY_STRING;
  String CMD_REMOVE_CONFIG_NAME  = EMPTY_STRING;
  String CMD_REMOVE_CONFIG_DESCR = STR_CMD_REMOVE_CONFIG;

  // ------------------------------------------------------------------------------------
  // AdminCmdAddConfig
  //
  String CMD_ADD_CONFIG_ID    = CMD_PATH_PREFIX + "addConfig";
  String CMD_ADD_CONFIG_ALIAS = EMPTY_STRING;
  String CMD_ADD_CONFIG_NAME  = EMPTY_STRING;
  String CMD_ADD_CONFIG_DESCR = STR_CMD_ADD_CONFIG;
}
