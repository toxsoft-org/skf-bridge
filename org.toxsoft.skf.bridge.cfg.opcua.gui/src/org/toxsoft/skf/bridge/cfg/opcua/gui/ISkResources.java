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
  String STR_N_OPC_UA_SERVER_URI              = "URL сервера";
  String STR_D_OPC_UA_SERVER_URI              = "Полный адрес сервера (протокол, ip адрес, порт)";
  String STR_N_LOGIN                          = "Login";
  String STR_D_LOGIN                          = "Логин входа на сервер OPC UA";
  String STR_N_PASSWORD                       = "Пароль";
  String STR_D_PASSWORD                       = "Пароль входа на сервер OPC UA";
  String STR_N_CREATE_CINFO_FROM_OPC_UA       = "Создать новый класс";
  String STR_D_CREATE_CINFO_FROM_OPC_UA       = "Используя описания узла OPC UA создать класс в Sk server";
  String STR_N_CREATE_OBJS_FROM_OPC_UA        = "Создать новые объекты";
  String STR_D_CREATE_OBJS_FROM_OPC_UA        = "Используя описания узлов OPC UA создать новые объекты в Sk server";
  String STR_N_SHOW_OPC_UA_NODE_2_GWID_ACT_ID = "Check UaNode->Gwid";
  String STR_D_SHOW_OPC_UA_NODE_2_GWID_ACT_ID = "Отобразить наличие связи между UaNode и Gwid";
}
