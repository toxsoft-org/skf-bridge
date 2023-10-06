package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

/**
 * Localizable resources.
 *
 * @author dima
 */
interface ISkResources {

  /**
   * {@link UaVariableNodeM5Model}
   */
  String STR_N_NODE_ID          = "Node Id";
  String STR_D_NODE_ID          = "Node Id";
  String STR_N_VALUE            = "Value";
  String STR_D_VALUE            = "Value of node";
  String STR_N_DATA_TYPE        = "Data type";
  String STR_D_DATA_TYPE        = "Data type of node's value";
  String STR_N_STATUS_CODE      = "Status code";
  String STR_D_STATUS_CODE      = "Node's status code";
  String STR_N_SOURCE_TIMESTAMP = "Source timestamp";
  String STR_D_SOURCE_TIMESTAMP = "Source timestamp";
  String STR_N_SERVER_TIMESTAMP = "Server timestamp";
  String STR_D_SERVER_TIMESTAMP = "Server timestamp";

  /**
   * {@link UaVariableNodeM5LifecycleManager}
   */
  String STR_WRITE_NODE_CONFIRM =
      "В системе Poligon запрещена запись в узлы с ns > 0x8000.\n Вы уверены что хотите записать в узел %s?";
}
