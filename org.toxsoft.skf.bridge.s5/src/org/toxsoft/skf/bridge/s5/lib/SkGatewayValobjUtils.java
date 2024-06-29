package org.toxsoft.skf.bridge.s5.lib;

import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.bridge.s5.lib.impl.*;

/**
 * Регистрация хранителей данных подсистемы
 *
 * @author mvk
 */
public class SkGatewayValobjUtils {

  /**
   * Регистрация известных хранителей.
   */
  public static void registerS5Keepers() {
    TsValobjUtils.registerKeeperIfNone( SkGatewayGwidConfigs.KEEPER_ID, SkGatewayGwidConfigs.KEEPER );
    TsValobjUtils.registerKeeperIfNone( SkGatewayConfigurationKeeper.KEEPER_ID, SkGatewayConfigurationKeeper.KEEPER );
    TsValobjUtils.registerKeeperIfNone( SkGatewayConfigurationListKeeper.KEEPER_ID,
        SkGatewayConfigurationListKeeper.KEEPER );
  }
}
