package org.toxsoft.skf.bridge.s5.supports;

import org.toxsoft.skf.bridge.s5.lib.ISkGatewayConfiguration;
import org.toxsoft.uskat.core.connection.ISkConnection;

/**
 * Шлюз передачи данных между серверами skat-s5
 *
 * @author mvk
 */
public interface IS5Gateway {

  /**
   * Возвращает конфигурацию шлюза
   *
   * @return {@link ISkGatewayConfiguration} конфигурация шлюза
   */
  ISkGatewayConfiguration configuration();

  /**
   * Возвращает признак того, что мост приостановил свою работу и не передает данные
   *
   * @return <b>true</b> мост приостановил работу, но возможно установлена связь с удаленным сервером;<b>false</b> мост
   *         работает в штатном режиме, но возможна потеря связи с удаленным сервером
   */
  boolean isPaused();

  /**
   * Устанавливает признак того, что мост приостановил свою работу и не передает данные
   *
   * @param aPause boolean <b>true</b> мост приостановил работу, но возможно установлена связь с удаленным
   *          сервером;<b>false</b> мост работает в штатном режиме, но возможна потеря связи с удаленным сервером
   */
  void setPaused( boolean aPause );

  /**
   * Возвращает соединение с локальным сервером
   *
   * @return {@link ISkConnection} соединение с локальным сервером. null: нет связи
   */
  ISkConnection localConnection();

  /**
   * Возвращает соединение с удаленным сервером
   *
   * @return {@link ISkConnection} соединение с удаленным сервером. null: нет связи
   */
  ISkConnection remoteConnection();
}
