package org.toxsoft.skf.bridge.s5.addons;

import org.toxsoft.core.tslib.bricks.events.msg.GtMessage;
import org.toxsoft.core.tslib.bricks.strid.coll.IStridablesList;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.skf.bridge.s5.supports.IS5BackendGatewaySingleton;
import org.toxsoft.skf.bridge.s5.supports.S5BackendGatewaySingleton;
import org.toxsoft.uskat.s5.server.backend.addons.IS5BackendLocal;
import org.toxsoft.uskat.s5.server.backend.addons.S5AbstractBackendAddonLocal;

/**
 * Local {@link IBaGateway} implementation.
 *
 * @author mvk
 */
public final class S5BaGatewayLocal
    extends S5AbstractBackendAddonLocal
    implements IBaGateway {

  /**
   * Поддержка бекенда службы шлюзов
   */
  private final IS5BackendGatewaySingleton gatewaySupport;

  /**
   * Данные конфигурации фронтенда для {@link IBaGateway}
   */
  private final S5BaGatewayData baData = new S5BaGatewayData();

  /**
   * Constructor.
   *
   * @param aOwner {@link IS5BackendLocal} - the owner backend
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public S5BaGatewayLocal( IS5BackendLocal aOwner ) {
    super( aOwner, ISkGatewayHardConstants.BAINF_GATEWAYS );
    gatewaySupport = aOwner.backendSingleton().get( S5BackendGatewaySingleton.BACKEND_GATEWAYS_ID,
        IS5BackendGatewaySingleton.class );
    // Установка конфигурации фронтенда
    frontend().frontendData().setBackendAddonData( IBaGateway.ADDON_ID, baData );
  }

  // ------------------------------------------------------------------------------------
  // BackendAddonBase
  //
  @Override
  public void onBackendMessage( GtMessage aMessage ) {
    // nop
  }

  @Override
  public void close() {
    // nop
  }

  // ------------------------------------------------------------------------------------
  // Реализация IBaGateway
  //
  @Override
  public IStridablesList<ISkGatewayConfiguration> gatewayConfigs() {
    return gatewaySupport.gatewayConfigs();
  }

  @Override
  public void defineGateway( ISkGatewayConfiguration aGatewayConfig ) {
    TsNullArgumentRtException.checkNull( aGatewayConfig );
    gatewaySupport.defineGateway( aGatewayConfig );
  }

  @Override
  public void removeGateway( String aGatewayId ) {
    TsNullArgumentRtException.checkNull( aGatewayId );
    gatewaySupport.removeGateway( aGatewayId );
  }
}
