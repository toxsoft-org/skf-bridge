package org.toxsoft.skf.bridge.s5.supports;

import org.toxsoft.uskat.s5.common.*;

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
  String STR_ROUTE   = "Маршрут";
  String STR_ROUTE_D = "Список идентификаторов сетевых узлов (sk.NetNode) пройденных при передачи значения данного";

  // ------------------------------------------------------------------------------------
  // Строки сообщений
  //
  String MSG_DOJOB                                      = "Gateway backend doJob.";
  String MSG_GW_CONFIGURATIONS_UPDATED                  =
      "Обновление конфигурации шлюзов. Новых: %d. Обновленных: %d. Удаленных: %d.";
  String MSG_GW_STARTED                                 = "[%s] Запуск шлюза.";
  String MSG_GW_INIT_START                              = "[%s] Начало инициализации шлюза.";
  String MSG_GW_INIT_FINISH                             = "[%s] Завершение инициализации шлюза,";
  String MSG_GW_CLOSE                                   = "[%s] Шлюз завершает работу.";
  String MSG_GW_CLOSED                                  = "[%s] Шлюз выгружен.";
  String MSG_GW_REMOTE_CONNECTED                        = "[%s] Установлено соединение с удаленным сервером.";
  String MSG_GW_REMOTE_DISCONNECTED                     = "[%s] Закрыто соединение с удаленным сервером.";
  String MSG_GW_CREATE_OJBS_MAPPING                     =
      "[%s] Формирование карт идентификаторов объектов чтения в идентификаторы объектов записи и обратно. Количество объектов: %d.";
  String MSG_GW_QUERY_LOCAL_OJBS                        =
      "[%s] Запрос локальных объектов шлюза. Количество объектов: %d.";
  String MSG_GW_QUERY_CLASSES                           = "[%s] Запрос классов объектов шлюза. Количество классов: %d.";
  String MSG_GW_GWIDS_LIST                              =
      "[%s] Сформированы списки данных шлюза. currdata = %d, histdata = %d, executors = %d, events = %d.";
  String MSG_GW_SYNC_HISTDATA                           =
      "[%s] Синхронизация хранимых данных. Метка времени последних значений приемника: %s.";
  String MSG_GW_SYNC_EVENTS                             =
      "[%s] Синхронизация событий. Метка времени последних событий приемника: %s.";
  String MSG_GW_PREPARE_EVENT_CONSUMER                  = "[%s] Подготовка событий формируемых объектами шлюза.";
  String MSG_GW_REGISTER_EVENT_CONSUMER                 =
      "[%s] Регистрация событий формируемых объектами шлюза. Количество событий: %d.";
  String MSG_GW_PREPARE_COMMAND_EXECUTOR                = "[%s] Подготовка команд исполняемых объектами шлюза.";
  String MSG_GW_REGISTER_COMMAND_EXECUTOR               =
      "[%s] Регистрация команд исполняемых объектами шлюза. Количество команд: %d.";
  String MSG_GW_PREPARE_COMMAND_TRACER                  =
      "[%s] Подготовка наблюдателя за исполнением команд объектами шлюза.";
  String MSG_GW_REGISTER_COMMAND_TRACER                 =
      "[%s] Регистрация наблюдателя за исполнением команд объектами шлюза. Количество команд: %d.";
  String MSG_GW_CURRDATA_TRANSFER                       =
      "[%s] Передача текущих данных. Количество данных: %d. first: %s.";
  String MSG_GW_CURRDATA_TRANSFER_FINISH                =
      "[%s] Завершение передачи текущих данных. Количество данных: %d. first: %s. Время запроса: %d (msec).";
  String MSG_GW_HISTDATA_TRANSFER                       =
      "[%s] Передача хранимых данных. Количество данных: %d. first: %s.";
  String MSG_GW_HISTDATA_TRANSFER_FINISH                =
      "[%s] Завершение передачи хранимых данных. Количество данных: %d. first: %s.";
  String MSG_GW_EVENT_TRANSFER                          = "[%s] Передача событий. Количество событий: %d. first: %s.";
  String MSG_GW_EVENT_TRANSFER_FINISH                   =
      "[%s] Завершение передачи событий. Количество событий: %d. first: %s.";
  String MSG_GW_COMMAND_TRANSFER                        = "[%s] Передача команды исполнителю. cmd: %s.";
  String MSG_GW_COMMAND_TRANSFER_FINISH                 =
      "[%s] Завершение передачи команды исполнителю. cmd: %s. Время запроса: %d (msec).";
  String MSG_GW_COMMAND_STATE_TRANSFER                  = "[%s] Передача состояния команды отправителю: %s.";
  String mSG_GW_COMMAND_STATE_TRANSFER_FINISH           =
      "[%s] Завершение передача состояния команды отправителю: %s. Время запроса: %d (msec).";
  String MSG_TRANSFER_EXECUTORS_START                   =
      "[%s] onExecutableCommandGwidsChanged(...). Cообщение об изменении списка исполнителей команд: %d, first: %s.";
  String MSG_CHANGE_CMD_EXECUTORS                       = "[%s] Изменение списка исполнителей команд. cmdGwids:\n%s.";
  String MSG_TRANSFER_EXECUTORS_FINISH                  =
      "[%s] Завершение передачи списка исполнителей команд. . Количество исполнителей: %d, first: %s. Время запроса: %d (msec).";
  String MSG_SYNCHONIZE_BY_DATAQUALITY_RESOURCES        =
      "[%s] onResourcesStateChanged(...): Сообщение об изменении списка отслеживаемых ресурсов ISkDataQualityService.";
  String MSG_SYNCHONIZE_BY_DATAQUALITY_RESOURCES_FINISH =
      "[%s] Завершение синхронизации по отслеживаемым ресурсам ISkDataQualityService.";
  String MSG_GW_ALIEN_COMMAND                           =
      "[%s] Команда не отправлялась шлюзом (отправил другой клиент?).";
  String MSG_SYNC_COMPLETED                             =
      "[%s] Завершение синхронизации данных между соединениями. aPaused = %b. Время: %d (msec).";
  String MSG_DATA_CHANNELS_CLOSE                        =
      "[%s] Завершение работы каналов передачи данных. rcd: %d, wcd: %d, rhd: %d, whd: %d.";
  String MSG_REGISTER_CURRDATA_ON_REMOTE                =
      "[%s] Регистрация передаваемых текущих данных на удаленном сервере (%d).";
  String MSG_REGISTER_HISTDATA_ON_REMOTE                =
      "[%s] Регистрация передаваемых хранимых данных на удаленном сервере (%d).";
  String MSG_RECREATE_GATEWAY_BY_UPDATE_CONFIG          =
      "[%s] reconfigure gateway.\n   old config = %s \n   new config = %s.";
  String MSG_TRANSIMITTED                               =
      "[%s] transmittingGwids = %d. transmitted: currdata = %d, histdata = %d.";

  // ------------------------------------------------------------------------------------
  // Тексты ошибок
  //
  String ERR_CREATE_CONNECTION                = "[%s] Ошибка установки соединения с удаленным сервером. Причина: %s.";
  String ERR_DOJOB_NOT_CONNECTION             = "[%s] doJob(...): нет соединения с удаленным сервером %s.";
  String ERR_GATEWAY_ALREADY_INITED           = "[%s] Инициализация уже выполнена.";
  String ERR_NOT_READY_FOR_SYNC               = "[%s] Нет готовности к синхронизации соединений.";
  String ERR_SEND_HISTDATA                    =
      "[%s] Ошибка передачи хранимых данных %s. ВНИМАНИЕ: Запланировано пересоздание соединения с удаленным сервером. Причина: %s.";
  String ERR_SEND_CMD                         =
      "[%s] Ошибка передачи команды. Запланирована синхронизация. Время запроса: %d (msec). Причина: %s.";
  String ERR_SEND_CMD_STATE                   =
      "[%s] Ошибка передачи состояния команды. Запланирована синхронизация. Время запроса: %d (msec). Причина: %s.";
  String ERR_READ_WRITE_CD_NOT_EQUALS         = "Неодинаковые наборы чтения и записи текущих данных.";
  String ERR_REGISTER_CMD_GWIDS               =
      "[%s] Ошибка регистрации исполнителей команд на удаленном сервере. Соединение будет закрыто. Количество исполнителей: %d, first: %s. Время запроса: %d (msec). Причина: %s.";
  String ERR_SYNCHONIZE                       =
      "[%s] Ошибка синхронизации с удаленным сервером. Соединение будет закрыто. Причина: %s.";
  String ERR_CMD_EXECUTORS_NOT_CHANGED        =
      "[%s] synchronizeCmdExecutors(): список исполнителей команд не изменился. aCmdGwids.size() = %d.";
  String ERR_TRY_RECREATE_FROM_DOJOB          =
      "[%s] Попытка запуска отложенного пересоздания соединения с удаленным сервером из doJob.";
  String ERR_TRY_SYNCH_FROM_DOJOB             = "[%s] Попытка запуска отложенной синхронизации из doJob.";
  String ERR_CURRDATA_WRITE_CHANNEL_NOT_FOUND = "[%s] onCurrData(...): Не найден канал записи текущих данных для %s.";
  String ERR_HISTDATA_WRITE_CHANNEL_NOT_FOUND =
      "[%s] beforeWriteHistData(...): не найден канал записи хранимых данных для %s.";
  String ERR_TRY_LOCK                         = "[%s] doJob(...): ошибка получения блокировки %s.";
  String ERR_CURRDATA_DOUBLE                  =
      "[%s] Попытка повторной регистрации канала записи текущего данного %s - игнорировано.";
}
