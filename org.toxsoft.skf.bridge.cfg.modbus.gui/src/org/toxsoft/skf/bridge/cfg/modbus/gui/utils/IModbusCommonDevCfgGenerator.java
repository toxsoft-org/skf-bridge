package org.toxsoft.skf.bridge.cfg.modbus.gui.utils;

import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.uskat.core.connection.*;

/**
 * Генератор содержимого файла devcfg по массиву конфигурационных юнитов sk-modbus. Построен по паттерну - билдер:
 * сначала устанавливаются все необходимые исходные данные - затем вызывается метод generate().
 *
 * @author max
 */
public interface IModbusCommonDevCfgGenerator {

  /**
   * Устанавливается массив единиц мапирования - каждая единица описывает тег OPC UA
   *
   * @param aCfgUnits IList - массив единиц мапирования
   * @return IModbusCommonDevCfgGenerator - ссылка на генератор
   */
  IModbusCommonDevCfgGenerator setUnits( IList<ModbusNode> aCfgUnits );

  /**
   * Устанавливает массив модбас-устройств
   *
   * @param aModbusDevices IList - массив модбас-устройств
   * @return IModbusCommonDevCfgGenerator - ссылка на генератор
   */
  IModbusCommonDevCfgGenerator setDevices( IList<ModbusDevice> aModbusDevices );

  /**
   * Устанавливает соединение с сервером
   *
   * @param aConn ISkConnection - соединение с сервером
   * @return IModbusCommonDevCfgGenerator - ссылка на генератор
   */
  IModbusCommonDevCfgGenerator setConnection( ISkConnection aConn );

  /**
   * Устанавливает преобразователь из сущности узла (тега, регистра) в идентификатор
   *
   * @param aConvertor INodeIdConvertor - преобразователь из сущности узла (тега, регистра) в идентификатор
   * @return IModbusCommonDevCfgGenerator - ссылка на генератор
   */
  IModbusCommonDevCfgGenerator setNodeIdConvertor( INodeIdConvertor aConvertor );

  /**
   * Устанавливает дополнительные свойства, расставляемые по файлу после его предварительной генерации
   *
   * @param aProperties дополнительные свойства: список, каждый элемент которого массив трёх строк: название свойства,
   *          путь в файле, значение (может быть форматом)
   * @return IModbusCommonDevCfgGenerator - ссылка на генератор
   */
  IModbusCommonDevCfgGenerator setAdditionalProperties( IList<IStringList> aProperties );

  /**
   * Устанавливает источник значений параметров (например, по умолчанию - из контекста) в частности для доп свойств
   * {@link #setAdditionalProperties(IList)}
   *
   * @param aParamValueSource IDevCfgParamValueSource - источник значений параметров в частности для доп свойств
   * @return IModbusCommonDevCfgGenerator - ссылка на генератор
   */
  IModbusCommonDevCfgGenerator setParamValueSource( IDevCfgParamValueSource aParamValueSource );

  /**
   * Формирует содержимое файла конфигурации драйвера (.devcfg)
   *
   * @return IAvTree - содержимое файла конфигурации драйвера (.devcfg)
   */
  IAvTree generate();

  String TRANSLATOR_DETECTOR_STR = "translator=";

  String PIN_NODE_ID_FORMAT = "pin.%s.def";

  String SYNC_TAGS_ARRAY_ID = "tags";
  // String DEVICE_DEF_ID_FORMAT = "device%s.def";
  String SYNCH_PERIOD_PARAM_NAME = "period";

  String CONNECTIONS_ARRAY_NAME = "connections";
  String DEVICES_ARRAY_NAME     = "devices";

  String MODBUS_DEVICE_ID = "modbus.bridge.collection.id";// "modbus.bridge.common";

  String DESCRIPTION_PARAM_NAME         = "description";
  String DESCRIPTION_PARAM_VAL_TEMPLATE = "modbus common apparat producer";

  String ID_PARAM_NAME = "id";

  String JAVA_CLASS_PARAM_NAME         = "javaClassName";
  String JAVA_CLASS_PARAM_VAL_TEMPLATE = "org.toxsoft.l2.thd.modbus.common.CommonModbusDeviceProducer";

  String TYPE_PARAM_NAME    = "type";
  String TYPE_PARAM_VAL_TCP = "tcp";
  String IP_PARAM_NAME      = "ip";
  String PORT_PARAM_NAME    = "port";

  String TYPE_PARAM_VAL_RTU          = "rtu";
  String PORT_NAME_PARAM_NAME        = "port.name";
  String BAUD_RATE_PARAM_NAME        = "baud.rate";
  String DEV_ADDRESS_RATE_PARAM_NAME = "dev.address";

  // String CONNECTION_TEMP_FMT_STR = "connection.number%d.def";

  String MODBUS_CFG_NODE_ID = "modbus.common.cfg";

  String PIN_ID_PARAM_NAME              = "id";
  String PIN_DESCR_PARAM_NAME           = "descr";
  String PIN_REQUEST_TYPE_PARAM_NAME    = "request.type";
  String PIN_REGISTER_PARAM_NAME        = "register";
  String PIN_WORD_COUNT_PARAM_NAME      = "words.count";
  String PIN_IS_OUTPUT_PARAM_NAME       = "is.output";
  String PIN_TRANSLATOR_PARAM_NAME      = "translator";
  String PIN_TRANSLATOR_PARAMS_PARAM_ID = "translator.params";

  String PIN_DEFAULT_DISCRET_TRANSLATOR_VAL = "BOOLEAN_DISCRETS";
  String PIN_DEFAULT_INTEGER_TRANSLATOR_VAL = "INTEGER_REGISTERS";
  String PIN_DEFAULT_FLOAT_TRANSLATOR_VAL   = "FLOAT_REGISTERS";
  String PIN_DEFAULT_BOOLEAN_TRANSLATOR_VAL = "BOOLEAN_REGISTERS";
}
