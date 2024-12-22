package org.toxsoft.skf.bridge.s5.lib.impl;

/**
 * Локализуемые ресурсы.
 *
 * @author mvk
 */
@SuppressWarnings( "nls" )
interface ISkResources {

  // ------------------------------------------------------------------------------------
  // Строковые константы S5BackendGatewayConfig
  //
  String D_LOGING = "Логин пользователя для подключения к удаленному серверу по умолчанию";
  String N_LOGING = "Пользователь";

  String D_PASSWORD = "Пароль пользователя для подключения к удаленному серверу по умолчанию";
  String N_PASSWORD = "Пароль";

  String D_GATEWAYS = "Список описаний шлюзов с которыми работает сервер";
  String N_GATEWAYS = "Список шлюзов";

  String D_SYNC_INTERVAL =
      "Интервал времени (в сутках) используемый для синхронизации данных между локальным и удаленным сервером при образовании связи.";
  String N_SYNC_INTERVAL = "Интервал синхронизации";

  // ------------------------------------------------------------------------------------
  // Строки сообщений
  //
  // ------------------------------------------------------------------------------------
  // Тексты ошибок
  //
  String MSG_ERR_NOT_CONNECTION = "Нет связи с сервером";
}
