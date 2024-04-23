package org.toxsoft.skf.bridge.s5.addons;

import org.toxsoft.core.tslib.coll.primtypes.IStringList;
import org.toxsoft.core.tslib.coll.primtypes.impl.StringArrayList;
import org.toxsoft.core.tslib.utils.Pair;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.skf.bridge.s5.lib.impl.SkGatewayService;
import org.toxsoft.skf.bridge.s5.supports.S5BackendGatewaySingleton;
import org.toxsoft.uskat.core.ISkServiceCreator;
import org.toxsoft.uskat.core.impl.AbstractSkService;
import org.toxsoft.uskat.s5.server.backend.addons.*;

/**
 * Построитель расширения бекенда {@link IBaGateway} для s5
 *
 * @author mvk
 */
public class S5BaGatewayCreator
    extends S5AbstractBackendAddonCreator {

  static {
    // Регистрация хранителей данных
    SkGatewayValobjUtils.registerS5Keepers();
  }

  /**
   * Конструктор
   */
  public S5BaGatewayCreator() {
    super( ISkGatewayHardConstants.BAINF_GATEWAYS );
  }

  // ------------------------------------------------------------------------------------
  // IS5BackendAddonCreator
  //
  @Override
  protected ISkServiceCreator<? extends AbstractSkService> doGetServiceCreator() {
    return SkGatewayService.CREATOR;
  }

  @Override
  protected Pair<Class<? extends IS5BackendAddonSession>, Class<? extends IS5BackendAddonSession>> doGetSessionClasses() {
    return new Pair<>( IS5BaGatewaySession.class, S5BaGatewaySession.class );
  }

  @Override
  protected IS5BackendAddonLocal doCreateLocal( IS5BackendLocal aOwner ) {
    return new S5BaGatewayLocal( aOwner );
  }

  @Override
  protected IS5BackendAddonRemote doCreateRemote( IS5BackendRemote aOwner ) {
    return new S5BaGatewayRemote( aOwner );
  }

  @Override
  protected IStringList doSupportSingletonIds() {
    return new StringArrayList( //
        S5BackendGatewaySingleton.BACKEND_GATEWAYS_ID//
    );
  }

}
