package org.toxsoft.skf.bridge.s5.lib;

import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.utils.login.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.rtdserv.*;
import org.toxsoft.uskat.s5.client.remote.connection.*;
import org.toxsoft.uskat.s5.server.backend.*;
import org.toxsoft.uskat.s5.server.interceptors.*;

/**
 * Описание конфигурации однонаправленного(!) шлюза службы {@link IBaGateway}
 * <p>
 * Этот интерфейс реализует {@link IStridable}, поля которого имеют следующий смысл:
 * <ul>
 * <li><b>id</b>() - уникальный идентификатор шлюза (ИД-путь);</li>
 * <li><b>description</b>() - удобочитаемое описание шлюза;</li>
 * <li><b>nmName</b>() - краткое название шлюза.</li>
 * </ul>
 * <p>
 * <b>Причины по которым шлюз определяется только как однонаправленный: </b><br>
 * {@link ISkCoreApi} не позволяет перехватывать в полном объеме вызовы и делегировать их шлюзу. Например, нельзя
 * перехватить поступление хранимых данных в режиме реального времени (как c текущими данными с их слушателем
 * {@link ISkCurrDataChangeListener}). Такая же ситуация может возникнуть с любой службой (core, skf) и требовать от их
 * API методы поддержки шлюза является явно неправильным.
 * <p>
 * По этой причине (чтобы не менять API служб под нужны шлюза), предлагается делать перехват необходимых потоков данных
 * для шлюза в самом бекенде. Например, в бекенде s5-сервера для этого используется механизм интерсепции
 * {@link IS5Interceptor} который реализуется синглетонами поддержки бекенда {@link IS5BackendSupportSingleton}.
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
}
