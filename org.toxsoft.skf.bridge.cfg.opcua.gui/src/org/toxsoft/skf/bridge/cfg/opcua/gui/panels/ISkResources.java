package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

/**
 * Localizable resources.
 *
 * @author dima
 */
public interface ISkResources {

  /**
   * {@link OpcUaServerNodesBrowserPanel}
   */
  String DLG_C_NEW_CLASS                 = "Создание описания класса";
  String DLG_T_NEW_CLASS                 = "Проверьте и отредактируйте описание и нажмите Ok";
  String DLG_C_NEW_OBJS                  = "Создание объектов OPC UA";
  String DLG_T_NEW_OBJS                  = "Отредактируйте список объектов и нажмите Ok";
  String STR_ENTER_DESCR                 = "<введите описание>";
  String STR_N_EV_SET_POINT_MIN          = "Уставка включения";
  String STR_D_EV_SET_POINT_MIN          = "Уставка включения";
  String STR_N_EV_PARAM_OLD_VAL          = "старое значение";
  String STR_D_EV_PARAM_OLD_VAL          = "Старое значение";
  String STR_N_EV_PARAM_NEW_VAL          = "новое значение";
  String STR_D_EV_PARAM_NEW_VAL          = "Новое значение";
  String STR_N_EV_SET_POINT_MAX          = "Уставка отключения";
  String STR_D_EV_SET_POINT_MAX          = "Уставка отключения";
  String STR_N_EV_PARAM_ON               = "вкл:";
  String STR_D_EV_PARAM_ON               = "включено";
  String STR_N_EV_AUTO                   = "Регулятор в автомате";
  String STR_D_EV_AUTO                   = "Регулятор в автомате";
  String STR_N_EV_ON                     = "Команда включения от регулятора";
  String STR_D_EV_ON                     = "Команда включения от регулятора";
  String STR_N_ARG_VALUE                 = "значение";
  String STR_D_ARG_VALUE                 = "значение аргумента команды";
  String STR_SUCCESS_CLASS_UPDATED       = "Операция завершена успешно, создан/обновлен class: %s";
  String STR_WARN_RECREATE_OBJS          = "Внимание class: %s обновлен, желательно удалить его объекты.\n Удалить?";
  String STR_ENTER_NAME                  = "<введите название>";
  String STR_N_OPC_UA_MARKER             = "маркер OPC UA";
  String STR_D_OPC_UA_MARKER             = "маркер класса сгенерированного из OPC UA";
  String STR_SUCCESS_OBJS_UPDATED        = "Операция завершена успешно, создан/обновлены объекты: %s";
  String SELECT_FILE_4_IMPORT_CMD        = "Выберите файл с описанием команд";
  String SELECT_FILE_4_IMPORT_BIT_RTDATA = "Выберите файл с описанием битовых RtData";

  /**
   * {@link PanelClassInfoSelector}
   */
  String DLG_C_CLASS_INFO            = "Выбор класса";
  String DLG_T_CLASS_INFO            = "Выберите класс и нажмите Ok";
  String STR_CLASSES_LIST            = "id класса";
  String STR_MSG_SELECT_NODE         = "Выбор узла из дерева OPC UA";
  String STR_DESCR_SELECT_NODE       = "Выделите нужный и нажмите Ok";
  String STR_MSG_SELECT_NODE_4_OBJS  = "Создание объектов из дерева узлов OPC UA";
  String STR_MSG_SELECT_NODE_4_CLASS = "Создание класса из дерева узлов OPC UA";
}
