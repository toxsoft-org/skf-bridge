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
  String CMD_PATH_PREFIX = AdminPluginGateways.CMD_PATH;

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

  /**
   * Аргумент : Строковый идентификатор моста
   */
  IAdminCmdArgDef ARG_ID = new AdminCmdArgDef( "id", DT_STRING, STR_ARG_ID );

  /**
   * Аргумент : Имя моста
   */
  IAdminCmdArgDef ARG_NAME = new AdminCmdArgDef( "name", DT_STRING_EMPTY, STR_ARG_NAME );

  /**
   * Аргумент : Описание моста
   */
  IAdminCmdArgDef ARG_DESCR = new AdminCmdArgDef( "descr", DT_STRING_EMPTY, STR_ARG_DESCR );

  /**
   * Аргумент : логин
   */
  IAdminCmdArgDef ARG_LOGIN = new AdminCmdArgDef( "login", DT_STRING, STR_ARG_LOGIN );

  /**
   * Аргумент : пароль
   */
  IAdminCmdArgDef ARG_PASSW = new AdminCmdArgDef( "passw", DT_STRING, STR_ARG_PASSW );

  /**
   * Аргумент : хост
   */
  IAdminCmdArgDef ARG_HOST = new AdminCmdArgDef( "host", DT_STRING, STR_ARG_HOST );

  /**
   * Аргумент : порт
   */
  IAdminCmdArgDef ARG_PORT = new AdminCmdArgDef( "port", DT_INTEGER, STR_ARG_PORT );

  /**
   * Аргумент : конфигурация передачи текущих данных
   */
  IAdminCmdArgDef ARG_CURRDATA = new AdminCmdArgDef( "currdata", DT_STRING_NULLABLE, STR_ARG_CURRDATA );

  /**
   * Аргумент : конфигурация передачи хранимых данных
   */
  IAdminCmdArgDef ARG_HISTDATA = new AdminCmdArgDef( "histdata", DT_STRING_NULLABLE, STR_ARG_HISTDATA );

  /**
   * Аргумент : конфигурация передачи событий
   */
  IAdminCmdArgDef ARG_EVENTS = new AdminCmdArgDef( "events", DT_STRING_NULLABLE, STR_ARG_EVENTS );

  /**
   * Аргумент : конфигурация регистрации исполнителей команд
   */
  IAdminCmdArgDef ARG_EXECUTORS = new AdminCmdArgDef( "executors", DT_STRING_NULLABLE, STR_ARG_EXECUTORS );
}
