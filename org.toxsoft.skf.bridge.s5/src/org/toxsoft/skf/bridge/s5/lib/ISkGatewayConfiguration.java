package org.toxsoft.skf.bridge.s5.lib;

import org.toxsoft.core.tslib.bricks.strid.IStridable;
import org.toxsoft.core.tslib.utils.login.ILoginInfo;
import org.toxsoft.uskat.s5.client.remote.connection.IS5ConnectionInfo;

/**
 * Описание конфигурации шлюза службы {@link IBaGateway}
 * <p>
 * Этот интерфейс реализует {@link IStridable}, поля которого имеют следующий смысл:
 * <ul>
 * <li><b>id</b>() - уникальный идентификатор шлюза (ИД-путь);</li>
 * <li><b>description</b>() - удобочитаемое описание шлюза;</li>
 * <li><b>nmName</b>() - краткое название шлюза.</li>
 * </ul>
 *
 * @author mvk
 */
public interface ISkGatewayConfiguration
    extends IStridable {

  /**
   * Информация о соединении с удаленным сервером
   *
   * @return {@link IS5ConnectionInfo} информация о соединении
   */
  IS5ConnectionInfo connectionInfo();

  /**
   * Информация об учетной записи для подключения к удаленному серверу
   *
   * @return {@link ILoginInfo}
   */
  ILoginInfo loginInfo();

  /**
   * Конфигурация идентификаторов по экспортируемым текущим данным
   *
   * @return {@link ISkGatewayGwidConfigs} конфигурация идентификаторов
   */
  ISkGatewayGwidConfigs exportCurrData();

  /**
   * Конфигурация идентификаторов по экспортируемым хранимым данным
   *
   * @return {@link ISkGatewayGwidConfigs} конфигурация идентификаторов
   */
  ISkGatewayGwidConfigs exportHistData();

  /**
   * Конфигурация идентификаторов по экспортируемым событиям
   *
   * @return {@link ISkGatewayGwidConfigs} конфигурация идентификаторов
   */
  ISkGatewayGwidConfigs exportEvents();

  /**
   * Конфигурация идентификаторов по экспортируемым исполнителям команд
   *
   * @return {@link ISkGatewayGwidConfigs} конфигурация идентификаторов
   */
  ISkGatewayGwidConfigs exportCmdExecutors();

  /**
   * Возвращает признак того, что передача данных через шлюз временно приостановлена клиентом
   *
   * @return boolean <b>true</b> передача данных приостановлена;<b>false</b> шлюз работает в штатном режиме
   */
  boolean isPaused();
}
