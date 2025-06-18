package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.uskat.core.connection.*;

/**
 * Генератор содержимого файла dlmcfg по массиву конфигурационных юнитов s5-opc. Построен по паттерну - билдер: сначала
 * устанавливаются все необходимые исходные данные - затем вызывается метод generate().
 *
 * @author max
 */
public interface IOpcCommonDlmCfgGenerator {

  IOpcCommonDlmCfgGenerator setUnits( IList<OpcToS5DataCfgUnit> aCfgUnits );

  IOpcCommonDlmCfgGenerator setConnection( ISkConnection aConn );

  IOpcCommonDlmCfgGenerator setNodeIdConvertor( INodeIdConvertor aConvertor );

  IOpcCommonDlmCfgGenerator setGwidFilter( IGwidFilter aGwidFilter );

  IOpcCommonDlmCfgGenerator setAdditionalProperties( IList<IStringList> aProperties );

  IOpcCommonDlmCfgGenerator setComplexTagDetector( IComplexTagDetector aComplexTagDetector );

  IOpcCommonDlmCfgGenerator setParamValueSource( IDevCfgParamValueSource aParamValueSource );

  IAvTree generate();

  /**
   * Имя параметра - идентификатор команды.
   */
  String CMD_ID = "cmd.id";

  /**
   * Имя параметра - массив тегов на входе команды.
   */
  String COMMAND_TAGS_ARRAY = "tags";

  /**
   * Начало блока, отвечающего за конфигурацию данных.
   */
  String DATA_DEFS = "dataDefs";

  /**
   * Начало блока, отвечающего за конфигурацию НСИ данных.
   */
  String RRI_DEFS = "rriDefs";

  /**
   * Начало блока, отвечающего за конфигурацию команд.
   */
  String CMD_DEFS = "cmdDefs";

  /**
   * Начало блока, отвечающего за конфигурацию команд для регистрации в качестве слушателя.
   */
  String CMD_CLASS_DEFS = "cmdClassDefs";

  /**
   * Начало блока, отвечающего за события.
   */
  String EVENT_DEFS = "eventDefs";

  /**
   * Начало блока, отвечающего за комплексные теги.
   */
  String СOMPLEX_TAGS_DEFS = "complexTagsDefs";

  String DESCRIPTION_STR = "description";
  String ID_STR          = "id";

  /**
   * Имя параметра - идентификатор пина.
   */
  String PIN_ID = "pin.id";

  /**
   * Имя параметра - идентификатор класса.
   */
  String CLASS_ID = "class.id";

  /**
   * Имя параметра - имя объекта.
   */
  String OBJ_NAME = "obj.name";

  /**
   * Имя параметра - идентификатор данного.
   */
  String DATA_ID = "data.id";

  /**
   * Имя параметра - идентификатор события.
   */
  String EVENT_ID = "event.id";

  /**
   * Имя параметра - идентификатор тега входного данного
   */
  String TAG_ID = "tag.id";

  /**
   * Имя параметра - идентификатор тега для записи значения в OPC UA
   */
  String COMPLEX_TAG_ID = "complex.tag.id";

  /**
   * Имя параметра - идентификатор специального устройства с тегами
   */
  String TAG_DEVICE_ID = "tag.dev.id";

  String CLASS_DEF_FORMAT = "class.%s.def";

  String DATA_DEF_FORMAT = "data.%s.def";

  /**
   * Разделитель имён объектов в списке объектов, предназначенных для выполнения команд (а также в списке команд)
   */
  String LIST_DELIM = ",";

  /**
   * Имя параметра - список идентификаторов команд.
   */
  String CMD_IDS_LIST = "cmd.ids.list";

  /**
   * Имя параметра - список имён объектов..
   */
  String OBJ_NAMES_LIST = "obj.names.list";

  //
  // ---------------------------------------------------------
  // значения, которые можно поменять в свойствах
  String DLM_ID_TEMPLATE          = "ru.toxsoft.l2.dlm.tags.common";
  String DLM_DESCR_TEMPLATE       = "ru.toxsoft.l2.dlm.tags.common";
  String DLM_CFG_NODE_ID_TEMPLATE = "opc.dlm.cfg";

  String СOMPLEX_TAG_ID          = "complex.tag.id";
  String СOMPLEX_TAG_TYPE        = "complex.tag.type";
  String SIMPLE_СOMPLEX_TAG_TYPE = "node.with.address.params.feedback";

  String CMD_DEF_FORMAT = "cmd.%s.def";

  String СT_WRITE_ID_TAG         = "write.id.tag";
  String СT_WRITE_VAL_TAG_PREFIX = "write.param.";
  String СT_WRITE_VAL_TAG_FORMAT = СT_WRITE_VAL_TAG_PREFIX + "%s.tag";
  String СT_READ_FEEDBACK_TAG    = "read.feedback.tag";

  String EVENT_TRIGGER_DEF_FORMAT = "event.trigger.%s.def";

  /**
   * TODO найти правильное место <br>
   * id device где node статуса НСИ.
   */
  String RRI_STATUS_DEVICE_ID = "status.rri.tag.dev.id";

  /**
   * node id чтения статуса НСИ.
   */
  String RRI_STATUS_READ_NODE_ID = "status.rri.read.tag.id";

  /**
   * аргумент aAddress в IComplexTag::setValue( int aAddress, IAtomicValue aValue ) для установки статуса НСИ.
   */
  String RRI_STATUS_CMD_SET_ID = "status.rri.cmd.set.id";

  /**
   * аргумент aAddress в IComplexTag::setValue( int aAddress, IAtomicValue aValue ) для сброса статуса НСИ.
   */
  String RRI_STATUS_CMD_RESET_ID = "status.rri.cmd.reset.id";

  /**
   * Complex node id записи статуса НСИ.
   */
  String RRI_STATUS_COMPLEX_NODE_ID = "status.rri.complex.tag.id";

  String RRI_ATTR_DEF_FORMAT = "rri.attr.%s.def";

  /**
   * Имя параметра - id НСИ атрибута.
   */
  String RRI_ATTR_ID = "rri.attr.id";

  /**
   * Имя параметра - индекс команды в OPC UA
   */
  String OPC_CMD_INDEX = "opc.cmd.index";

  /**
   * Имя параметра - индекс команды в OPC UA
   */
  String OPC_CMD_INDEX_ON = "opc.cmd.index.on";

  /**
   * Имя параметра - индекс команды в OPC UA
   */
  String OPC_CMD_INDEX_OFF = "opc.cmd.index.off";
}
