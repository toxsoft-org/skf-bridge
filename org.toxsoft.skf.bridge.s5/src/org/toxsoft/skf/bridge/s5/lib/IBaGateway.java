package org.toxsoft.skf.bridge.s5.lib;

import org.toxsoft.core.tslib.bricks.strid.coll.IStridablesList;
import org.toxsoft.core.tslib.utils.errors.TsIllegalArgumentRtException;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.uskat.core.backend.api.IBackendAddon;

/**
 * backend службы импорта-экспорта данных/событий/команд локального s5-сервера в удаленный s5-сервер (шлюзы).
 *
 * @author mvk
 */
public interface IBaGateway
    extends IBackendAddon {

  /**
   * ID of this backend addon.
   */
  String ADDON_ID = ISkGatewayHardConstants.BAID_GATEWAYS;

  /**
   * Возвращает список конфигураций мостов зарегистрированных службой.
   *
   * @return {@link IStridablesList}&lt;{@link ISkGatewayInfo}&gt; список конфигураций мостов
   */
  IStridablesList<ISkGatewayInfo> gatewayConfigs();

  /**
   * Регистрация шлюза.
   * <p>
   * Если в службе уже зарегистирован шлюз {@link ISkGatewayInfo#id()}, то заменяет его конфигурацию
   *
   * @param aGatewayConfig {@link ISkGatewayInfo} конфигурация шлюза.
   * @throws TsNullArgumentRtException аргумент = null
   * @throws TsIllegalArgumentRtException ошибка подключения к удаленному серверу
   */
  void defineGateway( ISkGatewayInfo aGatewayConfig );

  /**
   * Удаляет шлюз из службы.
   * <p>
   * Если в службе уже зарегистирован шлюз aGatewayId, то ничего не делает
   *
   * @param aGatewayId String текстовый идентификатор (ИД-имя) шлюза {@link ISkGatewayInfo#id()}.
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  void removeGateway( String aGatewayId );

}
