package org.toxsoft.skf.bridge.s5.lib;

import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.utils.login.*;
import org.toxsoft.uskat.s5.client.remote.connection.*;

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
public interface ISkGatewayInfo
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
   * @return {@link ISkGatewayGwids} конфигурация идентификаторов
   */
  ISkGatewayGwids exportCurrData();

  /**
   * Конфигурация идентификаторов по экспортируемым хранимым данным
   *
   * @return {@link ISkGatewayGwids} конфигурация идентификаторов
   */
  ISkGatewayGwids exportHistData();

  /**
   * Конфигурация идентификаторов по экспортируемым событиям
   *
   * @return {@link ISkGatewayGwids} конфигурация идентификаторов
   */
  ISkGatewayGwids exportEvents();

  /**
   * Конфигурация идентификаторов по экспортируемым исполнителям команд
   *
   * @return {@link ISkGatewayGwids} конфигурация идентификаторов
   */
  ISkGatewayGwids exportCmdExecutors();

  /**
   * Конфигурация идентификаторов по импортируемым текущим данным
   *
   * @return {@link ISkGatewayGwids} конфигурация идентификаторов
   */
  ISkGatewayGwids importCurrData();

  /**
   * Конфигурация идентификаторов по импортируемым хранимым данным
   *
   * @return {@link ISkGatewayGwids} конфигурация идентификаторов
   */
  ISkGatewayGwids importHistData();

  /**
   * Конфигурация идентификаторов по импортируемым событиям
   *
   * @return {@link ISkGatewayGwids} конфигурация идентификаторов
   */
  ISkGatewayGwids importEvents();

  /**
   * Конфигурация идентификаторов по импортируемым исполнителям команд
   *
   * @return {@link ISkGatewayGwids} конфигурация идентификаторов
   */
  ISkGatewayGwids importCmdExecutors();
}
