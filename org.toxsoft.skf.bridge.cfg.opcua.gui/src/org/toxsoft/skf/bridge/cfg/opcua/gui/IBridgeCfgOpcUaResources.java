package org.toxsoft.skf.bridge.cfg.opcua.gui;

import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;

/**
 * Localizable resources.
 *
 * @author max
 */
@SuppressWarnings( value = { "javadoc" } ) // "nls",
public interface IBridgeCfgOpcUaResources {

  String MSG_ERR_INV_GWID_FORMAT = Messages.getString( "MSG_ERR_INV_GWID_FORMAT" );
  String MSG_ERR_INV_SKID_FORMAT = Messages.getString( "MSG_ERR_INV_SkID_FORMAT" );

  String STR_N_PARAM_STRID         = "Строковый id";
  String STR_D_PARAM_STRID         = "Строковый идентификатор";
  String STR_D_BY_OPC_NODES_STRUCT = "В соответствии со структурой узлов OPC UA";
  String STR_N_BY_OPC_NODES_STRUCT = "По OPC структуре";

  String STR_N_BROWSE_CONN = "Просмотр структуры узлов OPC UA сервера";
  String STR_D_BROWSE_CONN = "Подключение к серверу OPC UF и просмотр структуры узлов этого сервера";

  String STR_LOADING_OPC_UA_NODES_STRUCT = "Загрузка структуры узлов OPC UA";

  String STR_N_PARAM_BROWSE_NAME = "Имя просмотра";
  String STR_D_PARAM_BROWSE_NAME = "Имя для просмотра в дерева";

  String STR_N_PARAM_DISPLAY_NAME = "Название";
  String STR_D_PARAM_DISPLAY_NAME = "Отображаемое название";

  String STR_N_PARAM_DESCRIPTION = "Описание";
  String STR_D_PARAM_DESCRIPTION = "Описание узла";

  /**
   * {@link DtoObjectM5Model}
   */
  String STR_N_CLASS_ID = "Class id";
  String STR_D_CLASS_ID = "Идентификатор класса";
  String STR_N_NAME     = "Название";
  String STR_D_NAME     = "Название объекта";
  String STR_N_DESCR    = "Описание";
  String STR_D_DESCR    = "Описание объекта";
}
