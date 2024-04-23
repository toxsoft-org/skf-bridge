package org.toxsoft.skf.bridge.s5.lib.impl;

import static org.toxsoft.skf.bridge.s5.lib.impl.ISkResources.*;

import org.toxsoft.core.tslib.bricks.ctx.ITsContextRo;
import org.toxsoft.core.tslib.utils.errors.TsIllegalStateRtException;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.uskat.core.ISkServiceCreator;
import org.toxsoft.uskat.core.backend.ISkBackend;
import org.toxsoft.uskat.core.backend.api.IBaQueries;
import org.toxsoft.uskat.core.devapi.IDevCoreApi;
import org.toxsoft.uskat.core.impl.AbstractSkService;
import org.toxsoft.uskat.s5.client.remote.connection.IS5ConnectionListener;

/**
 * Клиентская реализация службы {@link IBaGateway}.
 *
 * @author mvk
 */
public class SkGatewayService
    extends AbstractSkService
    implements ISkGatewayService, IS5ConnectionListener {

  /**
   * Service creator singleton.
   */
  public static final ISkServiceCreator<AbstractSkService> CREATOR = SkGatewayService::new;

  /**
   * Конструктор службы.
   *
   * @param aCoreApi {@link IDevCoreApi} API ядра uskat
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public SkGatewayService( IDevCoreApi aCoreApi ) {
    super( ISkGatewayService.SERVICE_ID, aCoreApi );
  }

  // ------------------------------------------------------------------------------------
  // Реализация шаблонных методов класса AbstractSkService
  //
  @Override
  protected void doInit( ITsContextRo aArgs ) {
    // nop
  }

  @Override
  protected void doClose() {
    // nop
  }

  // ------------------------------------------------------------------------------------
  // Реализация методов класса IBaGateway
  //
  @Override
  public void defineGateway( ISkGatewayConfiguration aGatewayConfig ) {
    TsNullArgumentRtException.checkNull( aGatewayConfig );
    remote().defineGateway( aGatewayConfig );
  }

  @Override
  public void removeGateway( String aGatewayId ) {
    TsNullArgumentRtException.checkNull( aGatewayId );
    remote().removeGateway( aGatewayId );
  }

  // ------------------------------------------------------------------------------------
  // Внутренние методы
  //
  /**
   * Return query service backend.
   *
   * @return {@link IBaQueries} query service backend.
   */
  private IBaGateway remote() {
    ISkBackend backend = coreApi().backend();
    if( !backend.isActive() ) {
      // Нет соединения с s5-сервером
      throw new TsIllegalStateRtException( MSG_ERR_NOT_CONNECTION );
    }
    return backend.findBackendAddon( IBaGateway.ADDON_ID, IBaGateway.class );
  }
}
