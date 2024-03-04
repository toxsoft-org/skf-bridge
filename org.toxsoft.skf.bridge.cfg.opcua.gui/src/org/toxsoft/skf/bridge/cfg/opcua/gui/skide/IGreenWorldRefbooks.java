/**
 * SkIDE generated file, 2024-03-04 17:57:08
 */
package org.toxsoft.skf.bridge.cfg.opcua.gui.skide;

/**
 * IGreenWorldRefbooks
 */
@SuppressWarnings( { "javadoc", "nls" } )
public interface IGreenWorldRefbooks {

  // RRI_OPCUA - Команды НСИ
  String RBID_RRI_OPCUA                                  = "RRI_OPCUA";
  String RBATRID_RRI_OPCUA___INDEX                       = "index";                        // Индекс
  String RBATRID_RRI_OPCUA___RRIID                       = "rriID";                        // Идентификатор НСИ
  String RBATRID_RRI_OPCUA___ON                          = "On";                           // 0->1
  String RBATRID_RRI_OPCUA___OFF                         = "Off";                          // 1->0
  String ITEMID_RRI_OPCUA___ANALOGINPUT_IMITATIONVALUE   = "AnalogInput.ImitationValue";   // Значение имитации
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1GENON   = "AnalogInput.SetPoint1genOn";   // Генерация ВА1 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1GENOFF  = "AnalogInput.SetPoint1genOff";  // Генерация ВА1 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1INDON   = "AnalogInput.SetPoint1indOn";   // Индикация ВА1 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1INDOFF  = "AnalogInput.SetPoint1indOff";  // Индикация ВА1 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2GENON   = "AnalogInput.SetPoint2genOn";   // Генерация ВП2 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2GENOFF  = "AnalogInput.SetPoint2genOff";  // Генерация ВП2 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2INDON   = "AnalogInput.SetPoint2indOn";   // Индикация ВП2 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2INDOFF  = "AnalogInput.SetPoint2indOff";  // Индикация ВП2 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3GENON   = "AnalogInput.SetPoint3genOn";   // Генерация НП3 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3GENOFF  = "AnalogInput.SetPoint3genOff";  // Генерация НП3 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4GENON   = "AnalogInput.SetPoint4genOn";   // Генерация НА4 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4GENOFF  = "AnalogInput.SetPoint4genOff";  // Генерация НА4 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3INDON   = "AnalogInput.SetPoint3indOn";   // Индикация НП3 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3INDOFF  = "AnalogInput.SetPoint3indOff";  // Индикация НП3 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4INDON   = "AnalogInput.SetPoint4indOn";   // Индикация НА4 Вкл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4INDOFF  = "AnalogInput.SetPoint4indOff";  // Индикация НА4 Откл.
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1        = "AnalogInput.SetPoint1";        // Уставка ВА1
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2        = "AnalogInput.SetPoint2";        // Уставка ВП2
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3        = "AnalogInput.SetPoint3";        // Уставка НП3
  String ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4        = "AnalogInput.SetPoint4";        // Уставка НА4
  String ITEMID_RRI_OPCUA___ANALOGINPUT_Y0               = "AnalogInput.Y0";               // Нижняя граница параметра
  String ITEMID_RRI_OPCUA___ANALOGINPUT_Y1               = "AnalogInput.Y1";               // Верхняя граница параметра
  String ITEMID_RRI_OPCUA___ANALOGINPUT_HYSTERESIS       = "AnalogInput.Hysteresis";       // Гистерезис
  String ITEMID_RRI_OPCUA___ANALOGINPUT_DELAYALARM       = "AnalogInput.DelayAlarm";       // Задержка аварии
  String ITEMID_RRI_OPCUA___ANALOGINPUT_DELAYWARN        = "AnalogInput.DelayWarn";        // Задержка предупреждения
  String ITEMID_RRI_OPCUA___ANALOGINPUT_DELAYRESET       = "AnalogInput.DelayReset";       // Задержка сброса
  String ITEMID_RRI_OPCUA___CTRLSYSTEM_SETOILTEMP        = "CtrlSystem.SetOilTemp";        // Уставка т-ры масла для
                                                                                           // пуска
  String ITEMID_RRI_OPCUA___CTRLSYSTEM_CASEHEATTARGET    = "CtrlSystem.CaseHeatTarget";    // Уставка т-ры корпуса для
                                                                                           // пуска
  String ITEMID_RRI_OPCUA___CTRLSYSTEM_GEDTIME           = "CtrlSystem.GEDTime";           // Таймер на запуск ГЭД
  String ITEMID_RRI_OPCUA___CTRLSYSTEM_STARTUPHEATTIME   = "CtrlSystem.StartupHeatTime";   // Время прогрева
  String ITEMID_RRI_OPCUA___CTRLSYSTEM_STOPRUNOUTTIME    = "CtrlSystem.StopRunoutTime";    // Время выбега
  String ITEMID_RRI_OPCUA___CTRLSYSTEM_STARTUPOILMIXTIME = "CtrlSystem.StartupOilMixTime"; // Таймер ПМН при запуске
  String ITEMID_RRI_OPCUA___CTRLSYSTEM_BLOWTIME          = "CtrlSystem.BlowTime";          // Время продувки
  String ITEMID_RRI_OPCUA___CTRLSYSTEM_NETENABLEALARM    = "CtrlSystem.NetEnableAlarm";    // Разрешить контроль связи
  String ITEMID_RRI_OPCUA___CTRLSYSTEM_NETDISABLEALARM   = "CtrlSystem.NetDisableAlarm";   // Блокировать контроль связи
  String ITEMID_RRI_OPCUA___IRREVERSIBLEENGINE_AUXTIME   = "IrreversibleEngine.AuxTime";   // Таймаут ДК
  String ITEMID_RRI_OPCUA___REVERSIBLEENGINE_AUXTIME     = "ReversibleEngine.AuxTime";     // Таймаут ДК
  String ITEMID_RRI_OPCUA___REVERSIBLEENGINE_OPENTIME    = "ReversibleEngine.OpenTime";    // Время открытия
  String ITEMID_RRI_OPCUA___REVERSIBLEENGINE_CLOSETIME   = "ReversibleEngine.CloseTime";   // Время закрытия
  String ITEMID_RRI_OPCUA___MAINSWITCH_AUXTIME           = "MainSwitch.AuxTime";           // Таймаут ДК
  String ITEMID_RRI_OPCUA___REGOILPRESSURE_SETPOINTON    = "RegOilPressure.SetPointOn";    // Уставка включения
  String ITEMID_RRI_OPCUA___REGOILPRESSURE_SETPOINTOFF   = "RegOilPressure.SetPointOff";   // Уставка отключения
  String ITEMID_RRI_OPCUA___PIDREGDO_TASK                = "PIDregDO.Task";                // Задание
  String ITEMID_RRI_OPCUA___PIDREGDO_KP                  = "PIDregDO.KP";                  // КП
  String ITEMID_RRI_OPCUA___PIDREGDO_KI                  = "PIDregDO.KI";                  // КИ
  String ITEMID_RRI_OPCUA___PIDREGDO_KD                  = "PIDregDO.KD";                  // КД
  String ITEMID_RRI_OPCUA___PIDREGDO_TI                  = "PIDregDO.TI";                  // ТИ
  String ITEMID_RRI_OPCUA___PIDREGDO_TD                  = "PIDregDO.TD";                  // ТД
  String ITEMID_RRI_OPCUA___PIDREGDO_DEADBAND            = "PIDregDO.DeadBand";            // Зона нечувствительности
  String ITEMID_RRI_OPCUA___PIDREGDO_CTRLT               = "PIDregDO.CtrlT";               // Период вызова
  String ITEMID_RRI_OPCUA___PIDREGDO_MINPULSE            = "PIDregDO.MinPulse";            // Мин.импульс
  String ITEMID_RRI_OPCUA___PIDREGDO_MAXDUTYCYCLE        = "PIDregDO.MaxDutyCycle";        // Макс.скважность

  // Cmd_OPCUA - Команды управления
  String RBID_CMD_OPCUA                                     = "Cmd_OPCUA";
  String RBATRID_CMD_OPCUA___INDEX                          = "index";                           // Индекс
  String RBATRID_CMD_OPCUA___CMDID                          = "cmdID";                           // Идентификатор
                                                                                                 // команды
  String ITEMID_CMD_OPCUA___ANALOGINPUT_ENABLEON            = "AnalogInput.EnableOn";            // Разрешение
  String ITEMID_CMD_OPCUA___ANALOGINPUT_ENABLEOFF           = "AnalogInput.EnableOff";           // Блокировка
  String ITEMID_CMD_OPCUA___ANALOGINPUT_IMITATIONON         = "AnalogInput.ImitationOn";         // Имитация Вкл.
  String ITEMID_CMD_OPCUA___ANALOGINPUT_IMITATIONOFF        = "AnalogInput.ImitationOff";        // Имитация Откл.
  String ITEMID_CMD_OPCUA___ANALOGINPUT_CONFIRMATION        = "AnalogInput.Confirmation";        // Квитирование
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_TESTALARM            = "CtrlSystem.TestAlarm";            // Тест аварийной
                                                                                                 // сигнализации
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_TESTWARN             = "CtrlSystem.TestWarn";             // Тест
                                                                                                 // предупредительной
                                                                                                 // сигнализации
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_START                = "CtrlSystem.Start";                // Запуск в Автомате
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_STOP                 = "CtrlSystem.Stop";                 // Останов в Автомате
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_CONFIRMATION         = "CtrlSystem.Confirmation";         // Квитирование
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_ENABLEALARM          = "CtrlSystem.EnableAlarm";          // Разрешение
                                                                                                 // сигнализации
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_DISABLEALARM         = "CtrlSystem.DisableAlarm";         // Блокировка
                                                                                                 // сигнализации
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_SETAUTOCTRL          = "CtrlSystem.SetAutoCtrl";          // Режим Авто
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_SETMANCTRL           = "CtrlSystem.SetManCtrl";           // Режим Руч
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_SETLOCALCTRL         = "CtrlSystem.SetLocalCtrl";         // Режим Мест
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_LAUNCHGRVPU          = "CtrlSystem.LaunchGRVPU";          // Включить ГР/ВПУ
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_STOPGRVPU            = "CtrlSystem.StopGRVPU";            // Отключить ГР/ВПУ
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_LAUNCHVENT           = "CtrlSystem.LaunchVent";           // Включить вентсистему
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_STOPVENT             = "CtrlSystem.StopVent";             // Отключить
                                                                                                 // вентсистему
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_RESETRRI             = "CtrlSystem.ResetRRI";             // Сбросить флаг НСИ
  String ITEMID_CMD_OPCUA___CTRLSYSTEM_SETRRI               = "CtrlSystem.SetRRI";               // Установить флаг НСИ
  String ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_ENABLEON     = "IrreversibleEngine.EnableOn";     // Разрешение
  String ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_ENABLEOFF    = "IrreversibleEngine.EnableOff";    // Блокировка
  String ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_IMITATIONON  = "IrreversibleEngine.ImitationOn";  // Имитация Вкл.
  String ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_IMITATIONOFF = "IrreversibleEngine.ImitationOff"; // Имитация Откл.
  String ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_CONFIRMATION = "IrreversibleEngine.Confirmation"; // Квитирование
  String ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_RESETHOUR    = "IrreversibleEngine.ResetHour";    // Сброс наработки
  String ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_STOPMAN      = "IrreversibleEngine.StopMan";      // Стоп Руч
  String ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_STARTMAN     = "IrreversibleEngine.StartMan";     // Старт Руч
  String ITEMID_CMD_OPCUA___VALVE_ENABLEON                  = "Valve.EnableOn";                  // Разрешение
  String ITEMID_CMD_OPCUA___VALVE_ENABLEOFF                 = "Valve.EnableOff";                 // Блокировка
  String ITEMID_CMD_OPCUA___VALVE_IMITATIONON               = "Valve.ImitationOn";               // Имитация Вкл.
  String ITEMID_CMD_OPCUA___VALVE_IMITATIONOFF              = "Valve.ImitationOff";              // Имитация Откл.
  String ITEMID_CMD_OPCUA___VALVE_CONFIRMATION              = "Valve.Confirmation";              // Квитирование
  String ITEMID_CMD_OPCUA___VALVE_STOPMAN                   = "Valve.StopMan";                   // Стоп Руч
  String ITEMID_CMD_OPCUA___VALVE_STARTMAN                  = "Valve.StartMan";                  // Старт Руч
  String ITEMID_CMD_OPCUA___REVERSIBLEENGINE_ENABLEON       = "ReversibleEngine.EnableOn";       // Разрешение
  String ITEMID_CMD_OPCUA___REVERSIBLEENGINE_ENABLEOFF      = "ReversibleEngine.EnableOff";      // Блокировка
  String ITEMID_CMD_OPCUA___REVERSIBLEENGINE_IMITATIONON    = "ReversibleEngine.ImitationOn";    // Имитация Вкл.
  String ITEMID_CMD_OPCUA___REVERSIBLEENGINE_IMITATIONOFF   = "ReversibleEngine.ImitationOff";   // Имитация Откл.
  String ITEMID_CMD_OPCUA___REVERSIBLEENGINE_CONFIRMATION   = "ReversibleEngine.Confirmation";   // Квитирование
  String ITEMID_CMD_OPCUA___REVERSIBLEENGINE_RESETHOUR      = "ReversibleEngine.ResetHour";      // Сброс наработки
  String ITEMID_CMD_OPCUA___REVERSIBLEENGINE_STOPMAN        = "ReversibleEngine.StopMan";        // Стоп Руч
  String ITEMID_CMD_OPCUA___REVERSIBLEENGINE_OPENMAN        = "ReversibleEngine.OpenMan";        // Открыть Руч
  String ITEMID_CMD_OPCUA___REVERSIBLEENGINE_CLOSEMAN       = "ReversibleEngine.CloseMan";       // Закрыть Руч
  String ITEMID_CMD_OPCUA___MAINSWITCH_ENABLEON             = "MainSwitch.EnableOn";             // Разрешение
  String ITEMID_CMD_OPCUA___MAINSWITCH_ENABLEOFF            = "MainSwitch.EnableOff";            // Блокировка
  String ITEMID_CMD_OPCUA___MAINSWITCH_IMITATIONON          = "MainSwitch.ImitationOn";          // Имитация Вкл.
  String ITEMID_CMD_OPCUA___MAINSWITCH_IMITATIONOFF         = "MainSwitch.ImitationOff";         // Имитация Откл.
  String ITEMID_CMD_OPCUA___MAINSWITCH_CONFIRMATION         = "MainSwitch.Confirmation";         // Квитирование
  String ITEMID_CMD_OPCUA___MAINSWITCH_RESETHOUR            = "MainSwitch.ResetHour";            // Сброс наработки
  String ITEMID_CMD_OPCUA___MAINSWITCH_STOPMAN              = "MainSwitch.StopMan";              // Стоп Руч
  String ITEMID_CMD_OPCUA___MAINSWITCH_STARTMAN             = "MainSwitch.StartMan";             // Старт Руч
  String ITEMID_CMD_OPCUA___VENTILATION_WORKMAN1            = "Ventilation.WorkMan1";            // Старт Руч 1
  String ITEMID_CMD_OPCUA___VENTILATION_WORKMAN2            = "Ventilation.WorkMan2";            // Старт Руч 2
  String ITEMID_CMD_OPCUA___VENTILATION_WORKMAN3            = "Ventilation.WorkMan3";            // Старт Руч 3
  String ITEMID_CMD_OPCUA___VENTILATION_STOPMAN             = "Ventilation.StopMan";             // Стоп Руч
  String ITEMID_CMD_OPCUA___VENTILATION_CONFIRMATION        = "Ventilation.Confirmation";        // Квитирование
  String ITEMID_CMD_OPCUA___VENTILATION_CTRLMODEAUTO        = "Ventilation.CtrlModeAuto";        // Режим Авто
  String ITEMID_CMD_OPCUA___VENTILATION_CTRLMODEMAN         = "Ventilation.CtrlModeMan";         // Режим Руч
  String ITEMID_CMD_OPCUA___REGOILPRESSURE_ON               = "RegOilPressure.On";               // Включить
  String ITEMID_CMD_OPCUA___REGOILPRESSURE_OFF              = "RegOilPressure.Off";              // Отключить
  String ITEMID_CMD_OPCUA___PIDREGDO_ON                     = "PIDregDO.On";                     // Включить
  String ITEMID_CMD_OPCUA___PIDREGDO_OFF                    = "PIDregDO.Off";                    // Отключить

  // BitMask - Битовые маски
  String RBID_BITMASK                                        = "BitMask";
  String RBATRID_BITMASK___BITN                              = "bitN";                               // Номер бита
  String RBATRID_BITMASK___IDENTIFICATOR                     = "identificator";                      // Идентификатор
  String RBATRID_BITMASK___IDW                               = "idW";                                // Слово
  String RBATRID_BITMASK___ON                                = "On";                                 // 0->1
  String RBATRID_BITMASK___OFF                               = "Off";                                // 1->0
  String ITEMID_BITMASK___ANALOGINPUT_CALIBRATIONWARNING     = "AnalogInput.CalibrationWarning";     // Значение за
                                                                                                     // диапазоном 5%
  String ITEMID_BITMASK___ANALOGINPUT_CALIBRATIONERROR       = "AnalogInput.CalibrationError";       // Ошибка статуса
                                                                                                     // измерения
  String ITEMID_BITMASK___ANALOGINPUT_ENABLED                = "AnalogInput.Enabled";                // Разрешение
                                                                                                     // сигнализации
  String ITEMID_BITMASK___ANALOGINPUT_IMITATION              = "AnalogInput.Imitation";              // Имитация
  String ITEMID_BITMASK___ANALOGINPUT_ALARMMINGENERATION     = "AnalogInput.AlarmMinGeneration";     // Генерация НА 4
  String ITEMID_BITMASK___ANALOGINPUT_ALARMMININDICATION     = "AnalogInput.AlarmMinIndication";     // Индикация НА 4
  String ITEMID_BITMASK___ANALOGINPUT_WARNINGMINGENERATION   = "AnalogInput.WarningMinGeneration";   // Генерация НП 3
  String ITEMID_BITMASK___ANALOGINPUT_WARNINGMININDICATION   = "AnalogInput.WarningMinIndication";   // Индикация НП 3
  String ITEMID_BITMASK___ANALOGINPUT_WARNINGMAXGENERATION   = "AnalogInput.WarningMaxGeneration";   // Генерация ВП 2
  String ITEMID_BITMASK___ANALOGINPUT_WARNINGMAXINDICATION   = "AnalogInput.WarningMaxIndication";   // Индикация ВП 2
  String ITEMID_BITMASK___ANALOGINPUT_ALARMMAXGENERATION     = "AnalogInput.AlarmMaxGeneration";     // Генерация ВА 1
  String ITEMID_BITMASK___ANALOGINPUT_ALARMMAXINDICATION     = "AnalogInput.AlarmMaxIndication";     // Индикация ВА 1
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT4GENERATION    = "AnalogInput.SetPoint4generation";    // Задание
                                                                                                     // Генерация НА 4
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT4INDICATION    = "AnalogInput.SetPoint4indication";    // Задание
                                                                                                     // Индикация НА 4
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT3GENERATION    = "AnalogInput.SetPoint3generation";    // Задание
                                                                                                     // Генерация НП 3
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT3INDICATION    = "AnalogInput.SetPoint3indication";    // Задание
                                                                                                     // Индикация НП 3
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT2GENERATION    = "AnalogInput.SetPoint2generation";    // Задание
                                                                                                     // Генерация ВП 2
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT2INDICATION    = "AnalogInput.SetPoint2indication";    // Задание
                                                                                                     // Индикация ВП 2
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT1GENERATION    = "AnalogInput.SetPoint1generation";    // Задание
                                                                                                     // Генерация ВА 1
  String ITEMID_BITMASK___ANALOGINPUT_SETPOINT1INDICATION    = "AnalogInput.SetPoint1indication";    // Задание
                                                                                                     // Индикация ВА 1
  String ITEMID_BITMASK___CTRLSYSTEM_STARTING                = "CtrlSystem.Starting";                // Запуск в
                                                                                                     // Автомате
  String ITEMID_BITMASK___CTRLSYSTEM_STOPING                 = "CtrlSystem.Stoping";                 // Останов в
                                                                                                     // Автомате
  String ITEMID_BITMASK___CTRLSYSTEM_EMERGENCYSTOP           = "CtrlSystem.EmergencyStop";           // Останов
                                                                                                     // Аварийный
  String ITEMID_BITMASK___CTRLSYSTEM_READYAUTO               = "CtrlSystem.ReadyAuto";               // Готовность Авто
  String ITEMID_BITMASK___CTRLSYSTEM_READYMAN                = "CtrlSystem.ReadyMan";                // Готовность Руч
  String ITEMID_BITMASK___CTRLSYSTEM_FS3                     = "CtrlSystem.FS3";                     // Проток воды ГЭД
  String ITEMID_BITMASK___CTRLSYSTEM_MAINSWITCHONLOC         = "CtrlSystem.MainSwitchOnLoc";         // ВВ включен в
                                                                                                     // Местном
  String ITEMID_BITMASK___CTRLSYSTEM_RRI                     = "CtrlSystem.RRI";                     // Флаг НСИ
  String ITEMID_BITMASK___CTRLSYSTEM_GRVPUSTARTING           = "CtrlSystem.GRVPUstarting";           // ГР/ВПУ
                                                                                                     // включается
  String ITEMID_BITMASK___CTRLSYSTEM_GRVPUSTOPING            = "CtrlSystem.GRVPUstoping";            // ГР/ВПУ
                                                                                                     // отключается
  String ITEMID_BITMASK___CTRLSYSTEM_GRVPUSTARTCOMPLETE      = "CtrlSystem.GRVPUstartComplete";      // ГР/ВПУ включены
  String ITEMID_BITMASK___CTRLSYSTEM_GRVPUSTOPCOMPLETE       = "CtrlSystem.GRVPUstopComplete";       // ГР/ВПУ отключены
  String ITEMID_BITMASK___CTRLSYSTEM_GRSTARTING              = "CtrlSystem.GRstarting";              // Пуск ГР\авто
  String ITEMID_BITMASK___CTRLSYSTEM_GRSTOPING               = "CtrlSystem.GRstoping";               // Стоп ГР
  String ITEMID_BITMASK___CTRLSYSTEM_VPUSTARTING             = "CtrlSystem.VPUstarting";             // Пуск ВПУ\авто
  String ITEMID_BITMASK___CTRLSYSTEM_VPUSTOPING              = "CtrlSystem.VPUstoping";              // Стоп ВПУ
  String ITEMID_BITMASK___CTRLSYSTEM_GEARERROR               = "CtrlSystem.GearError";               // Ошибка
                                                                                                     // зацепления
  String ITEMID_BITMASK___CTRLSYSTEM_OILFAILURE              = "CtrlSystem.OilFailure";              // Масла нет
  String ITEMID_BITMASK___CTRLSYSTEM_PWR_OK                  = "CtrlSystem.Pwr_ok";                  // Питание ШС норма
  String ITEMID_BITMASK___CTRLSYSTEM_FEEDER1ON               = "CtrlSystem.Feeder1on";               // Фидер 1 включен
  String ITEMID_BITMASK___CTRLSYSTEM_FEEDER2ON               = "CtrlSystem.Feeder2on";               // Фидер 2 включен
  String ITEMID_BITMASK___CTRLSYSTEM_FEEDER1_OK              = "CtrlSystem.Feeder1_ok";              // Фидер 1 норма
  String ITEMID_BITMASK___CTRLSYSTEM_FEEDER2_OK              = "CtrlSystem.Feeder2_ok";              // Фидер 2 норма
  String ITEMID_BITMASK___CTRLSYSTEM_L72_OK                  = "CtrlSystem.L72_ok";                  // L72 норма
  String ITEMID_BITMASK___CTRLSYSTEM_G1_OK                   = "CtrlSystem.G1_ok";                   // БП G1 норма
  String ITEMID_BITMASK___CTRLSYSTEM_G2_OK                   = "CtrlSystem.G2_ok";                   // БП G2 норма
  String ITEMID_BITMASK___CTRLSYSTEM_G3_OK                   = "CtrlSystem.G3_ok";                   // БП G3 норма
  String ITEMID_BITMASK___CTRLSYSTEM_G4_OK                   = "CtrlSystem.G4_ok";                   // БП G4 норма
  String ITEMID_BITMASK___CTRLSYSTEM_L73_OK                  = "CtrlSystem.L73_ok";                  // L73 норма
  String ITEMID_BITMASK___CTRLSYSTEM_ENGINEBLOCK             = "CtrlSystem.EngineBlock";             // Есть блокировка
                                                                                                     // привода
  String ITEMID_BITMASK___CTRLSYSTEM_ENGINEALARM             = "CtrlSystem.EngineAlarm";             // Есть авария
                                                                                                     // привода
  String ITEMID_BITMASK___CTRLSYSTEM_EMERGENCYBUTTONSHU      = "CtrlSystem.EmergencyButtonSHU";      // Кнопка Авар.Стоп
                                                                                                     // ШУ
  String ITEMID_BITMASK___CTRLSYSTEM_EMERGENCYBUTTONSHS      = "CtrlSystem.EmergencyButtonSHS";      // Кнопка Авар.Стоп
                                                                                                     // ШС
  String ITEMID_BITMASK___CTRLSYSTEM_EXTEMERGENCYBUTTON      = "CtrlSystem.ExtEmergencyButton";      // Кнопка Авар.Стоп
                                                                                                     // внешняя
  String ITEMID_BITMASK___CTRLSYSTEM_BLOCKSTARTSHU           = "CtrlSystem.BlockStartSHU";           // Блокировка
                                                                                                     // запуска ШУ
  String ITEMID_BITMASK___CTRLSYSTEM_DISABLEALARM            = "CtrlSystem.DisableAlarm";            // Блокировка
                                                                                                     // сигнализации
  String ITEMID_BITMASK___CTRLSYSTEM_POWERFAILURE            = "CtrlSystem.PowerFailure";            // Авария по
                                                                                                     // питанию
  String ITEMID_BITMASK___CTRLSYSTEM_LOC                     = "CtrlSystem.LOC";                     // Режим управления
                                                                                                     // Местный
  String ITEMID_BITMASK___CTRLSYSTEM_VVALARM                 = "CtrlSystem.VValarm";                 // Авария ячейки ВВ
  String ITEMID_BITMASK___CTRLSYSTEM_VVBLOCK                 = "CtrlSystem.VVblock";                 // Нет готовности
                                                                                                     // ячейки ВВ
  String ITEMID_BITMASK___CTRLSYSTEM_LOWOILLEVEL             = "CtrlSystem.LowOilLevel";             // Низкий уровень
                                                                                                     // масла
  String ITEMID_BITMASK___CTRLSYSTEM_WATERINGED              = "CtrlSystem.WaterInGED";              // Вода в ГЭД
  String ITEMID_BITMASK___CTRLSYSTEM_VTEALARM                = "CtrlSystem.VTEalarm";                // Авария ТВУ
  String ITEMID_BITMASK___CTRLSYSTEM_VTEBLOCK                = "CtrlSystem.VTEblock";                // Нет готовности
                                                                                                     // ТВУ
  String ITEMID_BITMASK___CTRLSYSTEM_BLOWFAILURE             = "CtrlSystem.BlowFailure";             // Не выполнена
                                                                                                     // продувка
  String ITEMID_BITMASK___CTRLSYSTEM_AIALARM                 = "CtrlSystem.AIalarm";                 // Есть аларм
                                                                                                     // параметра
  String ITEMID_BITMASK___CTRLSYSTEM_VPUON                   = "CtrlSystem.VPUon";                   // Включен ВПУ
  String ITEMID_BITMASK___CTRLSYSTEM_GRON                    = "CtrlSystem.GRon";                    // Включен ГР
  String ITEMID_BITMASK___CTRLSYSTEM_CLOSE_ZN_ZB             = "CtrlSystem.Close_ZN_ZB";             // ЗН и ЗБ закрыты
  String ITEMID_BITMASK___CTRLSYSTEM_PANELCONNECT            = "CtrlSystem.PanelConnect";            // Связь с Панелью
  String ITEMID_BITMASK___CTRLSYSTEM_ARMCONNECT              = "CtrlSystem.ARMConnect";              // Связь с АРМ
  String ITEMID_BITMASK___CTRLSYSTEM_ENABLEPANELCONNECTALARM = "CtrlSystem.EnablePanelConnectAlarm"; // Резрешение
                                                                                                     // контроля связи с
                                                                                                     // Панелью
  String ITEMID_BITMASK___CTRLSYSTEM_ENABLEARMCONNECTALARM   = "CtrlSystem.EnableARMConnectAlarm";   // Резрешение
                                                                                                     // контроля связи с
                                                                                                     // АРМ
  String ITEMID_BITMASK___IRREVERSIBLEENGINE_PWR             = "IrreversibleEngine.Pwr";             // Питание
  String ITEMID_BITMASK___IRREVERSIBLEENGINE_PWRCTRL         = "IrreversibleEngine.PwrCtrl";         // Контроль
  String ITEMID_BITMASK___IRREVERSIBLEENGINE_ENABLED         = "IrreversibleEngine.Enabled";         // Разрешение
  String ITEMID_BITMASK___IRREVERSIBLEENGINE_IMITATION       = "IrreversibleEngine.Imitation";       // Имитация
  String ITEMID_BITMASK___IRREVERSIBLEENGINE_READY           = "IrreversibleEngine.Ready";           // Готовность
  String ITEMID_BITMASK___IRREVERSIBLEENGINE_ON              = "IrreversibleEngine.On";              // Включен
  String ITEMID_BITMASK___IRREVERSIBLEENGINE_AUXON           = "IrreversibleEngine.AuxOn";           // Контактор
  String ITEMID_BITMASK___IRREVERSIBLEENGINE_STOPAUTOBLOCK   = "IrreversibleEngine.StopAutoBlock";   // СТОП блокировка
  String ITEMID_BITMASK___IRREVERSIBLEENGINE_PWRFAILURE      = "IrreversibleEngine.PwrFailure";      // Питания нет
  String ITEMID_BITMASK___IRREVERSIBLEENGINE_CTRLFAILURE     = "IrreversibleEngine.CtrlFailure";     // Контроля нет
  String ITEMID_BITMASK___IRREVERSIBLEENGINE_ONFAILURE       = "IrreversibleEngine.OnFailure";       // Не включился
  String ITEMID_BITMASK___IRREVERSIBLEENGINE_OFFFAILURE      = "IrreversibleEngine.OffFailure";      // Не отключился
  String ITEMID_BITMASK___IRREVERSIBLEENGINE_EXTALARM        = "IrreversibleEngine.ExtAlarm";        // Внешняя авария
  String ITEMID_BITMASK___VALVE_PWR                          = "Valve.Pwr";                          // Питание
  String ITEMID_BITMASK___VALVE_ENABLED                      = "Valve.Enabled";                      // Разрешение
  String ITEMID_BITMASK___VALVE_IMITATION                    = "Valve.Imitation";                    // Имитация
  String ITEMID_BITMASK___VALVE_READY                        = "Valve.Ready";                        // Готовность
  String ITEMID_BITMASK___VALVE_ON                           = "Valve.On";                           // Включен
  String ITEMID_BITMASK___VALVE_PWRFAILURE                   = "Valve.PwrFailure";                   // Питания нет
  String ITEMID_BITMASK___REVERSIBLEENGINE_PWR               = "ReversibleEngine.Pwr";               // Питание
  String ITEMID_BITMASK___REVERSIBLEENGINE_PWRCTRL           = "ReversibleEngine.PwrCtrl";           // Контроль
  String ITEMID_BITMASK___REVERSIBLEENGINE_ENABLED           = "ReversibleEngine.Enabled";           // Разрешение
  String ITEMID_BITMASK___REVERSIBLEENGINE_IMITATION         = "ReversibleEngine.Imitation";         // Имитация
  String ITEMID_BITMASK___REVERSIBLEENGINE_OPEN              = "ReversibleEngine.Open";              // Открывается
  String ITEMID_BITMASK___REVERSIBLEENGINE_AUXOPEN           = "ReversibleEngine.AuxOpen";           // ДК на открытие
  String ITEMID_BITMASK___REVERSIBLEENGINE_CLOSE             = "ReversibleEngine.Close";             // Закрывается
  String ITEMID_BITMASK___REVERSIBLEENGINE_AUXCLOSE          = "ReversibleEngine.AuxClose";          // ДК на закрытие
  String ITEMID_BITMASK___REVERSIBLEENGINE_OPENED            = "ReversibleEngine.Opened";            // Открыто
  String ITEMID_BITMASK___REVERSIBLEENGINE_LIMITSWITCHOPEN   = "ReversibleEngine.LimitSwitchOpen";   // КВ открытия
  String ITEMID_BITMASK___REVERSIBLEENGINE_CLOSED            = "ReversibleEngine.Closed";            // Закрыто
  String ITEMID_BITMASK___REVERSIBLEENGINE_LIMITSWITCHCLOSE  = "ReversibleEngine.LimitSwitchClose";  // КВ закрытия
  String ITEMID_BITMASK___REVERSIBLEENGINE_STOPAUTOBLOCK     = "ReversibleEngine.StopAutoBlock";     // СТОП блокировка
  String ITEMID_BITMASK___REVERSIBLEENGINE_READYOPEN         = "ReversibleEngine.ReadyOpen";         // Готовность
                                                                                                     // открытия
  String ITEMID_BITMASK___REVERSIBLEENGINE_READYCLOSE        = "ReversibleEngine.ReadyClose";        // Готовность
                                                                                                     // закрытия
  String ITEMID_BITMASK___REVERSIBLEENGINE_PWRFAILURE        = "ReversibleEngine.PwrFailure";        // Питания нет
  String ITEMID_BITMASK___REVERSIBLEENGINE_CTRLFAILURE       = "ReversibleEngine.CtrlFailure";       // Контроля нет
  String ITEMID_BITMASK___REVERSIBLEENGINE_ONFAILURE         = "ReversibleEngine.OnFailure";         // Не открылось
  String ITEMID_BITMASK___REVERSIBLEENGINE_RTDOFFFAILURE     = "ReversibleEngine.rtdOffFailure";     // Не закрылось
  String ITEMID_BITMASK___REVERSIBLEENGINE_EXTALARM          = "ReversibleEngine.ExtAlarm";          // Внешняя авария
  String ITEMID_BITMASK___REVERSIBLEENGINE_OPENONFAILURE     = "ReversibleEngine.OpenOnFailure";     // Не вкл. на
                                                                                                     // открытие
  String ITEMID_BITMASK___REVERSIBLEENGINE_OPENOFFFAILURE    = "ReversibleEngine.OpenOffFailure";    // Не откл. на
                                                                                                     // открытие
  String ITEMID_BITMASK___REVERSIBLEENGINE_CLOSEONFAILURE    = "ReversibleEngine.CloseOnFailure";    // Не вкл. на
                                                                                                     // закрытие
  String ITEMID_BITMASK___REVERSIBLEENGINE_CLOSEOFFFAILURE   = "ReversibleEngine.CloseOffFailure";   // Не откл. на
                                                                                                     // закрытие
  String ITEMID_BITMASK___MAINSWITCH_PWRCTRL                 = "MainSwitch.PwrCtrl";                 // Контроль
  String ITEMID_BITMASK___MAINSWITCH_ENABLED                 = "MainSwitch.Enabled";                 // Разрешение
  String ITEMID_BITMASK___MAINSWITCH_IMITATION               = "MainSwitch.Imitation";               // Имитация
  String ITEMID_BITMASK___MAINSWITCH_ON                      = "MainSwitch.On";                      // Включен
  String ITEMID_BITMASK___MAINSWITCH_AUXON                   = "MainSwitch.AuxOn";                   // Допконтакт Вкл.
  String ITEMID_BITMASK___MAINSWITCH_AUXOFF                  = "MainSwitch.AuxOff";                  // Допконтакт Откл.
  String ITEMID_BITMASK___MAINSWITCH_READYSTART              = "MainSwitch.ReadyStart";              // Готовность
                                                                                                     // старта
  String ITEMID_BITMASK___MAINSWITCH_READYSTOP               = "MainSwitch.ReadyStop";               // Готовность
                                                                                                     // останова
  String ITEMID_BITMASK___MAINSWITCH_CTRLFAILURE             = "MainSwitch.CtrlFailure";             // Контроля нет
  String ITEMID_BITMASK___MAINSWITCH_PWRFAILURE              = "MainSwitch.PwrFailure";              // Невалидное сост.
  String ITEMID_BITMASK___MAINSWITCH_ONFAILURE               = "MainSwitch.OnFailure";               // Не включился
  String ITEMID_BITMASK___MAINSWITCH_OFFFAILURE              = "MainSwitch.OffFailure";              // Не отключился
  String ITEMID_BITMASK___VENTILATION_READY                  = "Ventilation.Ready";                  // Готовность
  String ITEMID_BITMASK___VENTILATION_WORK1                  = "Ventilation.Work1";                  // Работа в режиме
                                                                                                     // 1
  String ITEMID_BITMASK___VENTILATION_WORK2                  = "Ventilation.Work2";                  // Работа в режиме
                                                                                                     // 2
  String ITEMID_BITMASK___VENTILATION_WORK3                  = "Ventilation.Work3";                  // Работа в режиме
                                                                                                     // 3
  String ITEMID_BITMASK___VENTILATION_VENTWORK               = "Ventilation.VentWork";               // Вентиляция
                                                                                                     // запускается
  String ITEMID_BITMASK___VENTILATION_RD1                    = "Ventilation.RD1";                    // Воздух ГЭД РД1
  String ITEMID_BITMASK___VENTILATION_RD2                    = "Ventilation.RD2";                    // Воздух ГЭД РД2
  String ITEMID_BITMASK___VENTILATION_BLOWCMPLT              = "Ventilation.BlowCmplt";              // Продувка
                                                                                                     // выполнена
  String ITEMID_BITMASK___VENTILATION_BLOWTIMER              = "Ventilation.BlowTimer";              // Из таймера
                                                                                                     // продувки
  String ITEMID_BITMASK___VENTILATION_NOTREADY               = "Ventilation.NotReady";               // Нет готовности
  String ITEMID_BITMASK___VENTILATION_FAILUREBLOWING         = "Ventilation.FailureBlowing";         // Не работает при
                                                                                                     // включенном ГЭД
  String ITEMID_BITMASK___VENTILATION_ONFAILUREW1            = "Ventilation.OnFailureW1";            // Не включился
                                                                                                     // Режим 1
  String ITEMID_BITMASK___VENTILATION_ONFAILUREW2            = "Ventilation.OnFailureW2";            // Не включился
                                                                                                     // Режим 2
  String ITEMID_BITMASK___VENTILATION_ONFAILUREW3            = "Ventilation.OnFailureW3";            // Не включился
                                                                                                     // Режим 3
  String ITEMID_BITMASK___VENTILATION_OFFFAILURE             = "Ventilation.OffFailure";             // Не отключился
  String ITEMID_BITMASK___REGOILPRESSURE_ON                  = "RegOilPressure.On";                  // Регулятор
                                                                                                     // включен/отключен
  String ITEMID_BITMASK___REGOILPRESSURE_OUTOFF              = "RegOilPressure.OutOff";              // Условие стоп
  String ITEMID_BITMASK___REGOILPRESSURE_OUTON               = "RegOilPressure.OutOn";               // Условие старт
  String ITEMID_BITMASK___PIDREGDO_ON                        = "PIDregDO.On";                        // Регулятор
                                                                                                     // включен/отключен
  String ITEMID_BITMASK___PIDREGDO_ALARM                     = "PIDregDO.Alarm";                     // Неотработка
  String ITEMID_BITMASK___PIDREGDO_LIMITUP                   = "PIDregDO.LimitUp";                   // Верхний предел
  String ITEMID_BITMASK___PIDREGDO_LIMITDOWN                 = "PIDregDO.LimitDown";                 // Нижний предел

}
