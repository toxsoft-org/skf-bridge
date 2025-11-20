package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;
import org.toxsoft.uskat.core.connection.*;

/**
 * Генератор содержимого файла devcfg по массиву конфигурационных юнитов sk-opc. Построен по паттерну - билдер: сначала
 * устанавливаются все необходимые исходные данные - затем вызывается метод generate().
 *
 * @author max
 */
public interface IOpcCommonDevCfgGenerator {

  /**
   * Устанавливается массив единиц мапирования - каждая единица описывает тег OPC UA
   *
   * @param aCfgUnits IList - массив единиц мапирования
   * @return IOpcCommonDlmCfgGenerator - ссылка на генератор
   */
  IOpcCommonDevCfgGenerator setUnits( IList<CfgOpcUaNode> aCfgUnits );

  /**
   * Устанавливает соединение с сервером
   *
   * @param aConn ISkConnection - соединение с сервером
   * @return IOpcCommonDlmCfgGenerator - ссылка на генератор
   */
  IOpcCommonDevCfgGenerator setConnection( ISkConnection aConn );

  /**
   * Устанавливает преобразователь из сущности узла (тега, регистра) в идентификатор
   *
   * @param aConvertor INodeIdConvertor - преобразователь из сущности узла (тега, регистра) в идентификатор
   * @return IOpcCommonDlmCfgGenerator - ссылка на генератор
   */
  IOpcCommonDevCfgGenerator setNodeIdConvertor( INodeIdConvertor aConvertor );

  /**
   * Устанавливает фильтр тегов
   *
   * @param aNodeFilter IOpcUaNodeFilter - фильтр тегов
   * @return IOpcCommonDlmCfgGenerator - ссылка на генератор
   */
  IOpcCommonDevCfgGenerator setNodeFilter( IOpcUaNodeFilter aNodeFilter );

  /**
   * Устанавливает дополнительные свойства, расставляемые по файлу после его предварительной генерации
   *
   * @param aProperties дополнительные свойства: список, каждый элемент которого массив трёх строк: название свойства,
   *          путь в файле, значение (может быть форматом)
   * @return IOpcCommonDlmCfgGenerator - ссылка на генератор
   */
  IOpcCommonDevCfgGenerator setAdditionalProperties( IList<IStringList> aProperties );

  /**
   * Устанавливает источник значений параметров (например, по умолчанию - из контекста) в частности для доп свойств
   * {@link #setAdditionalProperties(IList)}
   *
   * @param aParamValueSource IDevCfgParamValueSource - источник значений параметров в частности для доп свойств
   * @return IOpcCommonDlmCfgGenerator - ссылка на генератор
   */
  IOpcCommonDevCfgGenerator setParamValueSource( IDevCfgParamValueSource aParamValueSource );

  /**
   * Формирует содержимое файла конфигурации драйвера (.devcfg)
   *
   * @return IAvTree - содержимое файла конфигурации драйвера (.devcfg)
   */
  IAvTree generate();

  String JAVA_CLASS_PARAM_NAME  = "javaClassName";
  String ID_PARAM_NAME          = "id";
  String DESCRIPTION_PARAM_NAME = "description";

  String OUTPUT_TAGS_ARRAY_ID    = "output.tags";
  String ASYNC_TAGS_ARRAY_ID     = "async.tags";
  String SYNC_TAGS_ARRAY_ID      = "sync.tags";
  String OUTPUT_GROUP_NODE_ID    = "opc.output.group.def";
  String ASYNC_GROUP_NODE_ID     = "opc.async.group.def";
  String SYNC_GROUP_NODE_ID      = "opc.sync.group.def";
  String SYNCH_PERIOD_PARAM_NAME = "period";

  String GROUPS_ARRAY_NAME  = "groups";
  String BRIDGES_ARRAY_NAME = "bridges";

  String OPC_TAG_PARAM_NAME        = "opc.tag";
  String PIN_ID_PARAM_NAME         = "pin.id";
  String PIN_TYPE_PARAM_NAME       = "pin.type";
  String PIN_TYPE_EXTRA_PARAM_NAME = "pin.type.extra";
  String PIN_TAG_NODE_ID_FORMAT    = "pin.tag.%s.def";

  String SIEMENS_BRIDGE_NODE_ID = "device1.opc.def";

  String OPC2S5_CFG_NODE_ID = "opc2s5.cfg";

  String HOST_PARAM_NAME         = "host";
  String HOST_PARAM_VAL_TEMPLATE = "Set param in doc";

  String USER_PARAM_NAME         = "user";
  String USER_PARAM_VAL_TEMPLATE = "Set param in doc";

  String PASSWORD_PARAM_NAME         = "password";
  String PASSWORD_PARAM_VAL_TEMPLATE = "Set param in doc";

  String OPC_TAG_DEVICE_UA = "opc2s5.bridge.collection.id";

  String DESCRIPTION_PARAM_VAL_TEMPLATE = "opc 2 s5 pins UA apparat producer";
  String JAVA_CLASS_PARAM_VAL_TEMPLATE  = "org.toxsoft.l2.thd.opc.ua.milo.OpcUaMiloDriverProducer";
}
