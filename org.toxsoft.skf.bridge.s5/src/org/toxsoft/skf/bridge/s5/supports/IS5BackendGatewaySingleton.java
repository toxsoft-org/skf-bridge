package org.toxsoft.skf.bridge.s5.supports;

import javax.ejb.Local;

import org.toxsoft.core.tslib.bricks.strid.coll.IStridablesList;
import org.toxsoft.core.tslib.utils.errors.TsIllegalArgumentRtException;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.skf.bridge.s5.lib.IBaGateway;
import org.toxsoft.skf.bridge.s5.lib.ISkGatewayConfiguration;
import org.toxsoft.uskat.s5.server.backend.IS5BackendSupportSingleton;

/**
 * Локальный интерфейс синглетона backend {@link IBaGateway} предоставляемый s5-сервером.
 *
 * @author mvk
 */
@Local
public interface IS5BackendGatewaySingleton
    extends IS5BackendSupportSingleton {

  /**
   * Возвращает список конфигураций мостов зарегистрированных службой
   *
   * @return {@link IStridablesList}&lt;{@link ISkGatewayConfiguration}&gt; список конфигураций мостов
   */
  IStridablesList<ISkGatewayConfiguration> gatewayConfigs();

  /**
   * Регистрация шлюза.
   * <p>
   * Если в службе уже зарегистирован шлюз {@link ISkGatewayConfiguration#id()}, то заменяет его конфигурацию
   *
   * @param aGatewayConfig {@link ISkGatewayConfiguration} конфигурация шлюза.
   * @throws TsNullArgumentRtException аргумент = null
   * @throws TsIllegalArgumentRtException ошибка подключения к удаленному серверу
   */
  void defineGateway( ISkGatewayConfiguration aGatewayConfig );

  /**
   * Удаляет шлюз из службы
   * <p>
   * Если в службе уже зарегистирован шлюз aGatewayId, то ничего не делает
   *
   * @param aGatewayId String текстовый идентификатор (ИД-имя) шлюза {@link ISkGatewayConfiguration#id()}.
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  void removeGateway( String aGatewayId );

  /**
   * Возвращает шлюз по его идентификатору
   *
   * @param aGatewayId String текстовый идентификатор (ИД-имя) шлюза {@link ISkGatewayConfiguration#id()}.
   * @return {@link IS5Gateway} шлюз. null: шлюз не найден
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  IS5Gateway findGateway( String aGatewayId );

  /**
   * Проводит синхронизацию шлюзов с их описанием в конфигурации
   * <p>
   * Метод используется при запуске службы
   */
  void updateGateways();
}
