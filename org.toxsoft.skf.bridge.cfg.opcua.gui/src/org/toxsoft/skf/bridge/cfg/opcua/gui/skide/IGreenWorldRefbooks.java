/**
 * SkIDE generated file, 2024-02-07 12:53:28
 */
package org.toxsoft.skf.bridge.cfg.opcua.gui.skide;

/**
 * IGreenWorldRefbooks
 */
@SuppressWarnings( { "javadoc", "nls" } )
public interface IGreenWorldRefbooks {

  // RRI_OPCUA - Команды НСИ
  String RBID_RRI_OPCUA                                 = "RRI_OPCUA";
  String RBATRID_RRI_OPCUA___INDEX                      = "index";                       // Индекс
  String RBATRID_RRI_OPCUA___RRIID                      = "rriID";                       // Идентификатор НСИ
  String RBATRID_RRI_OPCUA___ON                         = "On";                          // 0->1
  String RBATRID_RRI_OPCUA___OFF                        = "Off";                         // 1->0
  String ITEMID_RRI_OPCUA___ANALOGINPUT_IMITATIONVALUE  = "AnalogInput.ImitationValue";  // Значение имитации
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1GENON  = "AnalogInput.SetPoint1genOn";  // Генерация ВА1 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1GENOFF = "AnalogInput.SetPoint1genOff"; // Генерация ВА1 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1INDON  = "AnalogInput.SetPoint1indOn";  // Индикация ВА1 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1INDOFF = "AnalogInput.SetPoint1indOff"; // Индикация ВА1 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2GENON  = "AnalogInput.SetPoint2genOn";  // Генерация ВП2 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2GENOFF = "AnalogInput.SetPoint2genOff"; // Генерация ВП2 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2INDON  = "AnalogInput.SetPoint2indOn";  // Индикация ВП2 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2INDOFF = "AnalogInput.SetPoint2indOff"; // Индикация ВП2 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3GENON  = "AnalogInput.SetPoint3genOn";  // Генерация НП3 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3GENOFF = "AnalogInput.SetPoint3genOff"; // Генерация НП3 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4GENON  = "AnalogInput.SetPoint4genOn";  // Генерация НА4 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4GENOFF = "AnalogInput.SetPoint4genOff"; // Генерация НА4 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3INDON  = "AnalogInput.SetPoint3indOn";  // Индикация НП3 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3INDOFF = "AnalogInput.SetPoint3indOff"; // Индикация НП3 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4INDON  = "AnalogInput.SetPoint4indOn";  // Индикация НА4 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4INDOFF = "AnalogInput.SetPoint4indOff"; // Индикация НА4 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1       = "AnalogInput.SetPoint1";       // Уставка ВА1
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2       = "AnalogInput.SetPoint2";       // Уставка ВП2
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3       = "AnalogInput.SetPoint3";       // Уставка НП3
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4       = "AnalogInput.SetPoint4";       // Уставка НА4
  String ITEMID_RRI_OPCUA___ANALOGINPUT_Y0              = "AnalogInput.Y0";              // Нижняя граница параметра
  String ITEMID_RRI_OPCUA___ANALOGINPUT_Y1              = "AnalogInput.Y1";              // Верхняя граница параметра
  String ITEMID_RRI_OPCUA___ANALOGINPUT_HYSTERESIS      = "AnalogInput.Hysteresis";      // Гистерезис
  String ITEMID_RRI_OPCUA___ANALOGINPUT_DELAYALARM      = "AnalogInput.DelayAlarm";      // Задержка аварии
  String ITEMID_RRI_OPCUA___ANALOGINPUT_DELAYWARN       = "AnalogInput.DelayWarn";       // Задержка предупреждения
  String ITEMID_RRI_OPCUA___ANALOGINPUT_DELAYRESET      = "AnalogInput.DelayReset";      // Задержка сброса

  // Cmd_OPCUA - Команды управления
  String RBID_CMD_OPCUA                              = "Cmd_OPCUA";
  String RBATRID_CMD_OPCUA___INDEX                   = "index";                    // Индекс
  String RBATRID_CMD_OPCUA___CMDID                   = "cmdID";                    // Идентификатор команды
  String ITEMID_CMD_OPCUA___ANALOGINPUT_ENABLEON     = "AnalogInput.EnableOn";     // Разрешение
  String ITEMID_CMD_OPCUA___ANALOGINPUT_ENABLEOFF    = "AnalogInput.EnableOff";    // Блокировка
  String ITEMID_CMD_OPCUA___ANALOGINPUT_IMITATIONON  = "AnalogInput.ImitationOn";  // Имитация Вкл.
  String ITEMID_CMD_OPCUA___ANALOGINPUT_IMITATIONOFF = "AnalogInput.ImitationOff"; // Имитация Откл.
  String ITEMID_CMD_OPCUA___ANALOGINPUT_CONFIRMATION = "AnalogInput.Confirmation"; // Квитирование

  // BitMask - Битовые маски
  String RBID_BITMASK                                      = "BitMask";
  String RBATRID_BITMASK___BITN                            = "bitN";                             // Номер бита
  String RBATRID_BITMASK___IDENTIFICATOR                   = "identificator";                    // Идентификатор
  String RBATRID_BITMASK___IDW                             = "idW";                              // Слово
  String ITEMID_BITMASK___ANALOGINPUT_CALIBRATIONWARNING   = "AnalogInput.CalibrationWarning";   // Значение за
                                                                                                 // диапазоном 5%
  String ITEMID_BITMASK___ANALOGINPUT_CALIBRATIONERROR     = "AnalogInput.CalibrationError";     // Ошибка статуса
                                                                                                 // измерения
  String ITEMID_BITMASK___ANALOGINPUT_ENABLED              = "AnalogInput.Enabled";              // Разрешение
                                                                                                 // сигнализации
  String ITEMID_BITMASK___ANALOGINPUT_IMITATION            = "AnalogInput.Imitation";            // Имитация
  String ITEMID_BITMASK___ANALOGINPUT_ALARMMINGENERATION   = "AnalogInput.AlarmMinGeneration";   // Генерация НА 4
  String ITEMID_BITMASK___ANALOGINPUT_ALARMMININDICATION   = "AnalogInput.AlarmMinIndication";   // Индикация НА 4
  String ITEMID_BITMASK___ANALOGINPUT_WARNINGMINGENERATION = "AnalogInput.WarningMinGeneration"; // Генерация НП 3
  String ITEMID_BITMASK___ANALOGINPUT_WARNINGMININDICATION = "AnalogInput.WarningMinIndication"; // Индикация НП 3
  String ITEMID_BITMASK___ANALOGINPUT_WARNINGMAXGENERATION = "AnalogInput.WarningMaxGeneration"; // Генерация ВП 2
  String ITEMID_BITMASK___ANALOGINPUT_WARNINGMAXINDICATION = "AnalogInput.WarningMaxIndication"; // Индикация ВП 2
  String ITEMID_BITMASK___ANALOGINPUT_ALARMMAXGENERATION   = "AnalogInput.AlarmMaxGeneration";   // Генерация ВА 1
  String ITEMID_BITMASK___ANALOGINPUT_ALARMMAXINDICATION   = "AnalogInput.AlarmMaxIndication";   // Индикация ВА 1
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT4GENERATION  = "AnalogInput.SetPoint4generation";  // Задание Генерация НА
                                                                                                 // 4
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT4INDICATION  = "AnalogInput.SetPoint4indication";  // Задание Индикация НА
                                                                                                 // 4
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT3GENERATION  = "AnalogInput.SetPoint3generation";  // Задание Генерация НП
                                                                                                 // 3
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT3INDICATION  = "AnalogInput.SetPoint3indication";  // Задание Индикация НП
                                                                                                 // 3
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT2GENERATION  = "AnalogInput.SetPoint2generation";  // Задание Генерация ВП
                                                                                                 // 2
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT2INDICATION  = "AnalogInput.SetPoint2indication";  // Задание Индикация ВП
                                                                                                 // 2
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT1GENERATION  = "AnalogInput.SetPoint1generation";  // Задание Генерация ВА
                                                                                                 // 1
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT1INDICATION  = "AnalogInput.SetPoint1indication";  // Задание Индикация ВА
                                                                                                 // 1

}
