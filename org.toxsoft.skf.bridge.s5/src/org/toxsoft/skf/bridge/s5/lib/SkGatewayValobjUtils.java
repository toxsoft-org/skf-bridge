package org.toxsoft.skf.bridge.s5.lib;

import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.bridge.s5.lib.impl.*;

/**
 * Регистрация хранителей данных подсистемы.
 *
 * @author mvk
 */
public class SkGatewayValobjUtils {

  /**
   * Регистрация известных хранителей.
   */
  public static void registerS5Keepers() {
    TsValobjUtils.registerKeeperIfNone( SkGatewayGwids.KEEPER_ID, SkGatewayGwids.KEEPER );
    TsValobjUtils.registerKeeperIfNone( SkGatewayInfoKeeper.KEEPER_ID, SkGatewayInfoKeeper.KEEPER );
    TsValobjUtils.registerKeeperIfNone( SkGatewayInfosKeeper.KEEPER_ID,
        SkGatewayInfosKeeper.KEEPER );
  }
}
