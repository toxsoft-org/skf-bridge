package org.toxsoft.skf.brifge.cfg.s7.gui.utils;

/**
 * Генератор содержимого файла s7.devcfg по массиву конфигурационных юнитов sk-s7. Построен по паттерну - билдер:
 * сначала устанавливаются все необходимые исходные данные - затем вызывается метод generate().
 *
 * @author dima
 */
@SuppressWarnings( { "javadoc", "nls" } )
public interface BiL2CfgGeneratorConstants {

  String DATA_JAVA_CLASS_ONE_TO_ONE_DATA_TRANSMITTER_FACTORY =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.data.OneToOneDataTransmitterFactory"; //$NON-NLS-1$

  String DATA_JAVA_CLASS_ONE_INT_TO_ONE_BIT_DATA_TRANSMITTER_FACTORY =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.data.SingleIntToSingleBoolDataTransmitterFactory"; //$NON-NLS-1$

  String CFG_DATA_UNIT_ID_FORMAT_ = "s7tos5.bridge.cfg.data.unit.id.%s"; //$NON-NLS-1$

  String CFG_CMD_UNIT_ID_FORMAT_ = "s7tos5.bridge.cfg.cmd.unit.id.%s"; //$NON-NLS-1$

  String CFG_EVENT_UNIT_ID_FORMAT_ = "s7tos5.bridge.cfg.event.unit.id.%s.%s"; //$NON-NLS-1$

  String EVENTS_JAVA_CLASS_OPC_TAGS_EVENT_SENDER       =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OpcTagsEventSender";          //$NON-NLS-1$
  String EVENTS_JAVA_CLASS_TAG_VALUE_CHANGED_CONDITION =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OneTagChangedEventCondition"; //$NON-NLS-1$
  String EVENTS_ONE_TAG_CHANGED_PARAM_FORMER           =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OneTagToChangedParamFormer";  //$NON-NLS-1$

  String EVENTS_JAVA_CLASS_TAG_SWITCH_CONDITION =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OneTagSwitchEventCondition"; //$NON-NLS-1$

  String EVENTS_ONE_TAG_TO_ONE_PARAM_FORMER = "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OneTagToOneParamFormer"; //$NON-NLS-1$

  String DEV_CONFIG_STR = "DeviceConfig = ";

  String JAVA_CLASS_PARAM_NAME         = "javaClassName";
  String JAVA_CLASS_PARAM_VAL_TEMPLATE = "org.toxsoft.l2.thd.s7.S7DeviceProducer";

  String S7_CFG_NODE_ID        = "s7.common.cfg";
  String S7_DEVICES_ARRAY_NAME = "s7.devices";

  String ID_PARAM_NAME                  = "id";
  String S7_DEVICE_ID                   = "s7.bridge.collection.id";
  String DESCRIPTION_PARAM_NAME         = "description";
  String DESCRIPTION_PARAM_VAL_TEMPLATE = "Siemens S7 apparat producer";
  String SYNCH_PERIOD_PARAM_NAME        = "period";

}
