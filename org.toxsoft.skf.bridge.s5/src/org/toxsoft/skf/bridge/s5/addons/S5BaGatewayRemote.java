package org.toxsoft.skf.bridge.s5.addons;

import org.toxsoft.core.tslib.bricks.events.msg.GtMessage;
import org.toxsoft.core.tslib.bricks.strid.coll.IStridablesList;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.uskat.s5.server.backend.addons.IS5BackendRemote;
import org.toxsoft.uskat.s5.server.backend.addons.S5AbstractBackendAddonRemote;

/**
 * Remote {@link IBaGateway} implementation.
 *
 * @author mvk
 */
public final class S5BaGatewayRemote
    extends S5AbstractBackendAddonRemote<IS5BaGatewaySession>
    implements IBaGateway {

  /**
   * Данные конфигурации фронтенда для {@link IBaGateway}
   */
  private final S5BaGatewayData baData = new S5BaGatewayData();

  /**
   * Constructor.
   *
   * @param aOwner {@link IS5BackendRemote} - the owner backend
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public S5BaGatewayRemote( IS5BackendRemote aOwner ) {
    super( aOwner, ISkGatewayHardConstants.BAINF_GATEWAYS, IS5BaGatewaySession.class );
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
  public IStridablesList<ISkGatewayInfo> gatewayConfigs() {
    return session().gatewayConfigs();
  }

  @Override
  public void defineGateway( ISkGatewayInfo aGatewayConfig ) {
    TsNullArgumentRtException.checkNull( aGatewayConfig );
    session().defineGateway( aGatewayConfig );
  }

  @Override
  public void removeGateway( String aGatewayId ) {
    TsNullArgumentRtException.checkNull( aGatewayId );
    session().removeGateway( aGatewayId );
  }
}
