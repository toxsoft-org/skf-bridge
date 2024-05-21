package org.toxsoft.skf.bridge.s5.skadmin.gateways;

/**
 * Локализуемые ресурсы пакета.
 *
 * @author mvk
 */
@SuppressWarnings( "nls" )
interface IAdminHardResources {

  // ------------------------------------------------------------------------------------
  // AdminCmdListConfigs
  //
  String STR_ARG_YES = "Требование отвечать на все вопросы утвердительно. По умолчанию: false";

  String STR_ARG_ID    = "Идентификатор моста";
  String STR_ARG_NAME  = "Имя моста";
  String STR_ARG_DESCR = "Описание моста";

  String STR_CMD_LIST_CONFIGS  = "Вывод списка текущих конфигураций моста.";
  String STR_CMD_REMOVE_CONFIG = "Удаление конфигурации моста.";
  String STR_CMD_ADD_CONFIG    = "Добавление/обновление конфигурации моста.";

  String MSG_CONFIG      = ""                                                                                    //
      + "id:          %s\n"                                                                                      //
      + "name:        %s\n"                                                                                      //
      + "description: %s\n"                                                                                      //
      + "connect:     %s@%s\n"                                                                                   //
      + "currdata:    %s\n"                                                                                      //
      + "histdata:    %s\n"                                                                                      //
      + "events:      %s\n"                                                                                      //
      + "executors:   %s\n";
  String MSG_CONFIG_LINE = " --------------------------------------------------------------------------------\n";

  String MSG_CMD_CONFIRM_REMOVE = "Подтвердите запрос удаления";
  String MSG_CMD_REMOVED        = "Мост удален.\n";
  String MSG_CMD_NOT_FOUND      = "Мост не найден.\n";
  String MSG_CMD_REJECT         = "Отказ пользователя от продолжения выполнения команды.";

  String MSG_CMD_TIME = "Время выполнения команды: %d (мсек).";
}
