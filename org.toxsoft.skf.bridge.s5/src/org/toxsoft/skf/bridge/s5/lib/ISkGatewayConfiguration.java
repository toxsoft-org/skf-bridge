package org.toxsoft.skf.bridge.s5.lib;

import org.toxsoft.core.tslib.bricks.strid.IStridable;
import org.toxsoft.core.tslib.gw.gwid.Gwid;
import org.toxsoft.core.tslib.gw.gwid.IGwidList;
import org.toxsoft.core.tslib.utils.errors.TsIllegalArgumentRtException;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.core.tslib.utils.login.ILoginInfo;
import org.toxsoft.uskat.core.api.cmdserv.ISkCommandExecutor;
import org.toxsoft.uskat.core.api.cmdserv.ISkCommandService;
import org.toxsoft.uskat.core.api.evserv.ISkEventHandler;
import org.toxsoft.uskat.core.api.evserv.ISkEventService;
import org.toxsoft.uskat.core.backend.api.IBaRtdata;
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
   * Возвращает {@link Gwid}-идентификаторы сущностей обрабатываемых шлюзом.
   * <ul>
   * <li>Текущие данные: смотри {@link IBaRtdata#configureCurrDataReader(IGwidList)},
   * {@link IBaRtdata#configureCurrDataWriter(IGwidList)};</li>
   * <li>Принимаемые команды: {@link ISkCommandService#registerExecutor(ISkCommandExecutor, IGwidList)};</li>
   * <li>Передаваемые события: {@link ISkEventService#registerHandler(IGwidList, ISkEventHandler)}.</li>
   * </ul>
   *
   * @return {@link IGwidList} список идентификторов.
   */
  IGwidList gwids();

  /**
   * Возвращает признак того, что передача данных через шлюз временно приостановлена клиентом
   *
   * @return boolean <b>true</b> передача данных приостановлена;<b>false</b> шлюз работает в штатном режиме
   * @throws TsNullArgumentRtException любой аргумент = null
   * @throws TsIllegalArgumentRtException шлюз не существует
   */
  boolean isPaused();
}
