package org.toxsoft.skf.bridge.s5.supports;

import org.toxsoft.uskat.s5.common.IS5CommonResources;

/**
 * Локализуемые ресурсы.
 *
 * @author mvk
 */
@SuppressWarnings( "nls" )
interface IS5Resources
    extends IS5CommonResources {

  // ------------------------------------------------------------------------------------
  // Константы, идентификаторы, имена, описания
  //

  // ------------------------------------------------------------------------------------
  // Строки сообщений
  //
  String MSG_DOJOB                                      = "Gateway backend doJob";
  String MSG_GW_CONFIGURATIONS_UPDATED                  =
      "Обновление конфигурации шлюзов. Новых: %d. Обновленных: %d. Удаленных: %d";
  String MSG_GW_STARTED                                 = "%s. Запуск шлюза. paused = %b";
  String MSG_GW_INIT_START                              = "%s. Начало инициализации шлюза";
  String MSG_GW_INIT_FINISH                             = "%s. Завершение инициализации шлюза";
  String MSG_GW_CLOSE                                   = "%s. Шлюз завершает работу";
  String MSG_GW_CLOSED                                  = "%s. Шлюз выгружен";
  String MSG_GW_REMOTE_CONNECTED                        = "%s. Установлено соединение с удаленным сервером";
  String MSG_GW_REMOTE_DISCONNECTED                     = "%s. Закрыто соединение с удаленным сервером";
  String MSG_GW_CREATE_OJBS_MAPPING                     =
      "%s. Формирование карт идентификаторов объектов чтения в идентификаторы объектов записи и обратно. Количество объектов: %d";
  String MSG_GW_QUERY_LOCAL_OJBS                        =
      "%s. Запрос локальных объектов шлюза. Количество объектов: %d";
  String MSG_GW_QUERY_CLASSES                           = "%s. Запрос классов объектов шлюза. Количество классов: %d";
  String MSG_GW_GWIDS_LIST                              = "%s. Сформированы списки данных шлюза. events = %d.";
  String MSG_GW_SYNC_HISTDATA                           =
      "%s. Синхронизация хранимых данных. Метка времени последних значений приемника: %s";
  String MSG_GW_SYNC_EVENTS                             =
      "%s. Синхронизация событий. Метка времени последних событий приемника: %s";
  String MSG_GW_PREPARE_EVENT_CONSUMER                  = "%s. Подготовка событий формируемых объектами шлюза";
  String MSG_GW_REGISTER_EVENT_CONSUMER                 =
      "%s. Регистрация событий формируемых объектами шлюза. Количество событий: %d";
  String MSG_GW_PREPARE_COMMAND_EXECUTOR                = "%s. Подготовка команд исполняемых объектами шлюза";
  String MSG_GW_REGISTER_COMMAND_EXECUTOR               =
      "%s. Регистрация команд исполняемых объектами шлюза. Количество команд: %d";
  String MSG_GW_PREPARE_COMMAND_TRACER                  =
      "%s. Подготовка наблюдателя за исполнением команд объектами шлюза";
  String MSG_GW_REGISTER_COMMAND_TRACER                 =
      "%s. Регистрация наблюдателя за исполнением команд объектами шлюза. Количество команд: %d";
  String MSG_GW_CURRDATA_TRANSFER                       =
      "%s. Передача текущих данных. Количество данных: %d. first: %s";
  String MSG_GW_CURRDATA_TRANSFER_FINISH                =
      "%s. Завершение передачи текущих данных. Количество данных: %d. first: %s. Время запроса: %d (msec)";
  String MSG_GW_HISTDATA_TRANSFER                       = "Передача хранимых данных. Количество данных: %d. first: %s";
  String MSG_GW_HISTDATA_TRANSFER_FINISH                =
      "Завершение передачи хранимых данных. Количество данных: %d. first: %s.";
  String MSG_GW_EVENT_TRANSFER                          = "Передача событий. Количество событий: %d. first: %s";
  String MSG_GW_EVENT_TRANSFER_FINISH                   =
      "Завершение передачи событий. Количество событий: %d. first: %s";
  String MSG_GW_COMMAND_TRANSFER                        = "Передача команды исполнителю. cmd: %s";
  String MSG_GW_COMMAND_TRANSFER_FINISH                 =
      "Завершение передачи команды исполнителю. cmd: %s. Время запроса: %d (msec)";
  String MSG_GW_COMMAND_STATE_TRANSFER                  = "Передача состояния команды отправителю: %s";
  String mSG_GW_COMMAND_STATE_TRANSFER_FINISH           =
      "Завершение передача состояния команды отправителю: %s. Время запроса: %d (msec)";
  String MSG_TRANSFER_EXECUTORS_START                   =
      "onExecutableCommandGwidsChanged(...). Cообщение об изменении списка исполнителей команд: %d, first: %s";
  String MSG_CHANGE_CMD_EXECUTORS                       =
      "Изменение списка исполнителей команд. aPaused = %b, cmdGwids:\n%s";
  String MSG_TRANSFER_EXECUTORS_FINISH                  =
      "Завершение передачи списка исполнителей команд. . Количество исполнителей: %d, first: %s. Время запроса: %d (msec)";
  String MSG_SYNCHONIZE_BY_DATAQUALITY_RESOURCES        =
      "onResourcesStateChanged(...): Сообщение об изменении списка отслеживаемых ресурсов ISkDataQualityService.";
  String MSG_SYNCHONIZE_BY_DATAQUALITY_RESOURCES_FINISH =
      "Завершение синхронизации по отслеживаемым ресурсам ISkDataQualityService.";
  String MSG_GW_CURRDATA_SYNC                           = "%s. Синхронизация текущих данных. Количество каналов: %d";
  String MSG_GW_HISTDATA_SYNC                           = "%s. Синхронизация хранимых данных. Количество каналов: %d";
  String MSG_GW_EVENT_SYNC                              =
      "%s. Синхронизация событий объектов класса %s. Количество событий: %d";
  String MSG_GW_PASSTHROUGH_COMMAND                     = "Передача команды через шлюз: %s";
  String MSG_GW_CMD_AUTHOR_NOT_FOUND                    = "В описании шлюза %s не найден объект-автор команды: %d.";
  String MSG_GW_CMD_EXECUTOR_NOT_FOUND                  =
      "В описании шлюза %s не найден объект-исполнитель команды: %d.";
  String MSG_GW_ALIEN_COMMAND                           =
      "%s. Команда не отправлялась шлюзом (отправил другой клиент?)";
  String MSG_GW_PAUSE_QUERY                             = "%s. Запрос клиента приостановить передачу данных через шлюз";
  String MSG_GW_START_QUERY                             = "%s. Запрос клиента возобновить передачу данных через шлюз";
  String MSG_GW_PAUSED                                  = "%s. Передача данных через шлюз временно приостановлена";
  String MSG_SYNC_COMPLETED                             =
      "Завершение синхронизации данных между соединениями. aPaused = %b. Время: %d (msec)";
  String MSG_DATA_CHANNELS_CLOSE                        =
      "%s. Завершение работы каналов передачи данных. rcd: %d, wcd: %d, rhd: %d, whd: %d";
  String MSG_REGISTER_DATA_ON_REMOTE                    =
      "Регистрация передаваемых данных на удаленном сервере. Количество данных: %d";
  String MSG_REGISTER_QUALITY_COMPLETED                 =
      "Завершение регистрации в удаленной службе качества данных. Количество: %d";
  String MSG_REGISTER_CHANNELS_COMPLETED                =
      "Завершение регистрации каналов передачи данных. Количество: %d";
  String MSG_RECREATE_GATEWAY_BY_UPDATE_CONFIG          =
      "reconfigure gateway %s. \n   old config = %s \n   new config = %s";

  // ------------------------------------------------------------------------------------
  // Тексты ошибок
  //
  String ERR_CREATE_CONNECTION                 = "Ошибка установки соединения с удаленным сервером. Причина: %s";
  String ERR_DOJOB_NOT_CONNECTION              = "doJob(...): нет соединения с удаленным сервером %s";
  String ERR_COMMAND_NOT_FOUND                 =
      "%s. Не найдена команда отправителя (executingCommands). Команда исполнителя: %s";
  String ERR_COMMAND_REMOVE_BY_TIMEOUT         = "%s. Команда исполнителя: %s (отправителя: %s) удалена по таймауту";
  String ERR_TRACE_DEREG                       = "%s. Ошибка дерегистрации наблюдателя команд. Причина: %s";
  String ERR_EXECUTOR_DEREG                    = "%s. Ошибка дерегистрации исполнителя команд. Причина: %s";
  String ERR_EVENT_CONSUMER                    = "%s. Ошибка дерегистрации получателя событий. Причина: %s";
  String ERR_READ_CURRDATA_DEREG               = "%s. Ошибка завершения набора чтения текущих данных. Причина: %s";
  String ERR_READ_HISTDATA_DEREG               = "%s. Ошибка завершения набора чтения хранимых данных. Причина: %s";
  String ERR_WRITE_CURRDATA_DEREG              = "%s. Ошибка завершения набора записи текущих данных. Причина: %s";
  String ERR_WRITE_HISTDATA_DEREG              = "%s. Ошибка завершения набора записи хранимых данных. Причина: %s";
  String ERR_EVENT_AUTHOR_NOT_FOUND            = "%s. В описании шлюза не найден объект-автор события: %d. Событие: %s";
  String ERR_CMD_AUTHOR_NOT_FOUND              = "%s. В описании шлюза не найден объект-автор команды: %d. Команда: %s";
  String ERR_CMD_EXECUTOR_NOT_FOUND            =
      "%s. В описании шлюза не найден объект-исполнитель команды: %d. Команда: %s";
  String ERR_GATEWAY_ALREADY_INITED            = "%s. Инициализация уже выполнена";
  String ERR_NOT_READY_FOR_SYNC                = "%s. Нет готовности к синхронизации соединений";
  String ERR_SEND_CURRDATA                     =
      "%s. Ошибка передачи текущих данных. Запланирована синхронизация. Время запроса: %d (msec). Причина: %s";
  String ERR_SEND_HISTDATA                     =
      "Ошибка передачи хранимых данных. Запланирована синхронизация. Причина: %s";
  String ERR_SEND_CMD                          =
      "Ошибка передачи команды. Запланирована синхронизация. Время запроса: %d (msec). Причина: %s";
  String ERR_SEND_CMD_STATE                    =
      "Ошибка передачи состояния команды. Запланирована синхронизация. Время запроса: %d (msec). Причина: %s";
  String ERR_READ_WRITE_CD_NOT_EQUALS          = "Неодинаковые наборы чтения и записи текущих данных";
  String ERR_REGISTER_CMD_GWIDS                =
      "%s. Ошибка регистрации исполнителей команд на удаленном сервере. Соединение будет закрыто. Количество исполнителей: %d, first: %s. Время запроса: %d (msec). Причина: %s";
  String ERR_SYNCHONIZE                        =
      "%s. Ошибка синхронизации с удаленным сервером. Соединение будет закрыто. Причина: %s";
  String ERR_CMD_EXECUTORS_NOT_CHANGED         =
      "synchronizeCmdExecutors(): список исполнителей команд не изменился. aCmdGwids.size() = %d";
  String ERR_TRY_SYNCH_FROM_DOJOB              = "Попытка запуска отложенной синхронизации из doJob";
  String ERR_HISTDATA_WRITE_CHANNEL_NOT_FOUND  =
      "beforeWriteHistData(...): не найден канал записи хранимых данных для %s";
  String ERR_HISTDATA_WRITE_CHANNEL_NOT_FOUND2 = "onCurrData(...): %s. Не найден канал записи хранимых данных для %s";
  String ERR_TRY_LOCK                          = "doJob(...): ошибка получения блокировки %s.";
  String ERR_CURRDATA_DOUBLE                   =
      "Попытка повторной регистрации канала записи текущего данного %s - игнорировано";
}
