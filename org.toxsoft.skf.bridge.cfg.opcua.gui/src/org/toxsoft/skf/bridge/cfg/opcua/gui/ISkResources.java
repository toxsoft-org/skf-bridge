package org.toxsoft.skf.bridge.cfg.opcua.gui;

/**
 * Localizable resources.
 *
 * @author dima
 */
interface ISkResources {

  /**
   * {@link IOpcUaServerConnCfgConstants}
   */
  String STR_N_OPC_UA_SERVER_URI        = "URL сервера";
  String STR_D_OPC_UA_SERVER_URI        = "Полный адрес сервера (протокол, ip адрес, порт)";
  String STR_N_LOGIN                    = "Login";
  String STR_D_LOGIN                    = "Логин входа на сервер OPC UA";
  String STR_N_PASSWORD                 = "Пароль";
  String STR_D_PASSWORD                 = "Пароль входа на сервер OPC UA";
  String STR_N_CREATE_CINFO_FROM_OPC_UA = "Создать новый класс";
  String STR_D_CREATE_CINFO_FROM_OPC_UA = "Используя описания узла OPC UA создать класс в Sk server";
  String STR_N_CREATE_OBJS_FROM_OPC_UA  = "Создать новые объекты";
  String STR_D_CREATE_OBJS_FROM_OPC_UA  = "Используя описания узлов OPC UA создать новые объекты в Sk server";
  String STR_N_SHOW_OPC_UA_NODE_2_GWID  = "Check UaNode->Gwid";
  String STR_D_SHOW_OPC_UA_NODE_2_GWID  = "Отобразить наличие связи между UaNode и Gwid";
  String STR_N_LOAD_CMD_DESCR           = "Загрузить описание команд";
  String STR_D_LOAD_CMD_DESCR           = "Загрузить описание команд из файла электронных таблиц";
  String STR_N_LOAD_BIT_RTDATA          = "Загрузить описание битовых rtData";
  String STR_D_LOAD_BIT_RTDATA          = "Загрузить описание битовых rtData из файла электронных таблиц";

  String STR_N_FILTER_READ_ONLY         = "Read only";
  String STR_D_FILTER_READ_ONLY         = "Узлы с доступом только на чтение";
  String STR_N_FILTER_WRITE_ONLY        = "Write only";
  String STR_D_FILTER_WRITE_ONLY        = "Узлы с доступом только на запись";
  String STR_N_FILTER_WRITE_READ        = "Read and Write only";
  String STR_D_FILTER_WRITE_READ        = "Узлы с доступом на чтение/запись";
  String STR_N_FILTER_READ_ONLY_POLIGON = "Hide read only nodes (Poligon)";
  String STR_D_FILTER_READ_ONLY_POLIGON = "Спртать узлы с доступом только на чтение для Poligon";

}
