package org.toxsoft.skf.bridge.s5.supports;

import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.uskat.core.connection.*;

/**
 * Шлюз передачи данных между серверами skat-s5
 *
 * @author mvk
 */
public interface IS5Gateway {

  /**
   * Возвращает конфигурацию шлюза
   *
   * @return {@link ISkGatewayInfo} конфигурация шлюза
   */
  ISkGatewayInfo configuration();

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
