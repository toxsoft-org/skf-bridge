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
  String STR_CMD_LIST_CONFIGS  = "Вывод списка текущих конфигураций моста.";
  String STR_CMD_REMOVE_CONFIG = "Удаление конфигурации моста.";
  String STR_CMD_ADD_CONFIG    = "Добавление/обновление конфигурации моста.";
  String MSG_CONFIG            = ""                                                                                    //
      + "id:          %s\n"                                                                                            //
      + "name:        %s\n"                                                                                            //
      + "description: %s\n"                                                                                            //
      + "connect:     %s@%s\n"                                                                                         //
      + "currdata:    %s\n"                                                                                            //
      + "histdata:    %s\n"                                                                                            //
      + "events:      %s\n"                                                                                            //
      + "executors:   %s\n";
  String MSG_CONFIG_LINE       = " --------------------------------------------------------------------------------\n";

  // ------------------------------------------------------------------------------------
  // AdminCmdRemoveConfig
  //
  String STR_ARG_YES            = "Требование отвечать на все вопросы утвердительно. По умолчанию: false";
  String MSG_CMD_CONFIRM_REMOVE = "Подтвердите запрос удаления";
  String MSG_CMD_REMOVED        = "Мост удален.\n";
  String MSG_CMD_NOT_FOUND      = "Мост не найден.\n";
  String MSG_CMD_REJECT         = "Отказ пользователя от продолжения выполнения команды.";

  // ------------------------------------------------------------------------------------
  // AdminCmdAddConfig
  //
  String STR_ARG_ID        = "Идентификатор моста";
  String STR_ARG_NAME      = "Имя моста";
  String STR_ARG_DESCR     = "Описание моста";
  String STR_ARG_HOST      = "IP-адрес для подключения к главному серверу";
  String STR_ARG_PORT      = "Порт для подключения к главному серверу";
  String STR_ARG_LOGIN     = "Логин пользователя для подключения к главному серверу";
  String STR_ARG_PASSW     = "Пароль пользователя для подключения к главному серверу";
  String STR_ARG_CURRDATA  = "Конфигурация передачи текущих данных";
  String STR_ARG_HISTDATA  = "Конфигурация передачи хранимых данных";
  String STR_ARG_EVENTS    = "Конфигурация передачи событий";
  String STR_ARG_EXECUTORS = "Конфигурация регистрации исполнителей команд";
  String MSG_CMD_ADDED     = "Мост добавлен.\n";
  String MSG_CMD_UPDATED   = "Мост обновлен.\n";

  String MSG_CMD_TIME         = "Время выполнения команды: %d (мсек).";
  String ERR_READ_GWID_CONFIG =
      "Ошибка чтения значения аргумента %s. Значение должно быть в формате SkGatewayGwids.KEEPER. Причина: %s";
}
