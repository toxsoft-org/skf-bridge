package org.toxsoft.skf.bridge.s5.lib;

import org.toxsoft.core.tslib.utils.valobj.TsValobjUtils;
import org.toxsoft.skf.bridge.s5.lib.impl.SkGatewayConfigurationKeeper;
import org.toxsoft.skf.bridge.s5.lib.impl.SkGatewayConfigurationListKeeper;

/**
 * Регистрация хранителей данных подсистемы
 *
 * @author mvk
 */
public class SkGatewayValobjUtils {

  /**
   * Регистрация известных хранителей
   */
  public static void registerS5Keepers() {
    TsValobjUtils.registerKeeperIfNone( SkGatewayConfigurationKeeper.KEEPER_ID, SkGatewayConfigurationKeeper.KEEPER );
    TsValobjUtils.registerKeeperIfNone( SkGatewayConfigurationListKeeper.KEEPER_ID,
        SkGatewayConfigurationListKeeper.KEEPER );
  }
}
