package org.toxsoft.skf.bridge.s5.lib;

import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.api.*;

/**
 * Cлужба импорта-экспорта данных/событий/команд локального s5-сервера в удаленный s5-сервер (шлюзы).
 *
 * @author mvk
 */
public interface ISkGatewayService
    extends ISkService {

  /**
   * Идентификатор службы.
   */
  String SERVICE_ID = SK_SYSEXT_SERVICE_ID_PREFIX + "GatewayService"; //$NON-NLS-1$

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
   * Удаляет шлюз из службы
   * <p>
   * Если в службе уже зарегистирован шлюз aGatewayId, то ничего не делает
   *
   * @param aGatewayId String текстовый идентификатор (ИД-имя) шлюза {@link ISkGatewayInfo#id()}.
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  void removeGateway( String aGatewayId );
}
