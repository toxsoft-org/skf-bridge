package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

/**
 * Localizable resources.
 *
 * @author dima
 */
public interface ISkResources {

  //
  // {@link TCPAddressM5Model}
  // -----------------------------------------------------------------
  //
  String STR_N_CONNECTION_NAME = Messages.STR_N_CONNECTION_NAME;
  String STR_D_CONNECTION_NAME = Messages.STR_D_CONNECTION_NAME;
  String STR_N_IP_ADDRESS      = Messages.STR_N_IP_ADDRESS;
  String STR_D_IP_ADDRESS      = Messages.STR_D_IP_ADDRESS;
  String STR_N_PORT_NUMBER     = Messages.STR_N_PORT_NUMBER;
  String STR_D_PORT_NUMBER     = Messages.STR_D_PORT_NUMBER;

  //
  // {@link TCPAddressM5LifecycleManager}
  // ------------------------------------------------------------------
  //
  String MSG_ERR_INVALID_IP_ADDRESS = Messages.MSG_ERR_INVALID_IP_ADDRESS;
  String MSG_ERR_NOT_IDPATH         = Messages.MSG_ERR_NOT_IDPATH;

  //
  // {@link ModbusDeviceM5Model}
  // ------------------------------------------------------------------
  //
  String STR_N_IS_TCP_INDEX     = Messages.STR_N_IS_TCP_INDEX;
  String STR_D_IS_TCP_INDEX     = Messages.STR_D_IS_TCP_INDEX;
  String STR_N_DEVICE_CONN_OPTS = Messages.STR_N_DEVICE_CONN_OPTS;
  String STR_D_DEVICE_CONN_OPTS = Messages.STR_D_DEVICE_CONN_OPTS;

  //
  // {@link ModbusToS5CfgDoc}
  // ------------------------------------------------------------------
  //
  String STR_ERR_DOC_READ    = Messages.STR_ERR_DOC_READ;
  String STR_N_PATH_TO_L2    = "директория";
  String STR_D_PATH_TO_L2    = "Директория генерации конфигурационных файлов";
  String STR_N_CFG_FILE_NAME = "шаблон имени";
  String STR_D_CFG_FILE_NAME = "Шаблон имени конфигурационного файла";

  //
  // {@link ModbusNodesForCfgM5Model}
  // ------------------------------------------------------------------
  //
  String STR_N_MODBUS_REGISTER       = Messages.STR_N_MODBUS_REGISTER;
  String STR_D_MODBUS_REGISTER       = Messages.STR_D_MODBUS_REGISTER;
  String STR_N_MODBUS_WORDS_COUNT    = Messages.STR_N_MODBUS_WORDS_COUNT;
  String STR_D_MODBUS_WORDS_COUNT    = Messages.STR_D_MODBUS_WORDS_COUNT;
  String STR_N_MODBUS_REQUEST_TYPE   = Messages.STR_N_MODBUS_REQUEST_TYPE;
  String STR_D_MODBUS_REQUEST_TYPE   = Messages.STR_D_MODBUS_REQUEST_TYPE;
  String STR_N_MODBUS_VALUE_TYPE     = Messages.STR_N_MODBUS_VALUE_TYPE;
  String STR_D_MODBUS_VALUE_TYPE     = Messages.STR_D_MODBUS_VALUE_TYPE;
  String STR_N_MODBUS_PARAMETERS_STR = Messages.STR_N_MODBUS_PARAMETERS_STR;
  String STR_D_MODBUS_PARAMETERS_STR = Messages.STR_D_MODBUS_PARAMETERS_STR;
}
