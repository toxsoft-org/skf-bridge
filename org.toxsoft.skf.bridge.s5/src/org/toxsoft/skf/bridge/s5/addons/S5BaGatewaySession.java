package org.toxsoft.skf.bridge.s5.addons;

import static org.toxsoft.uskat.s5.server.IS5ImplementConstants.*;

import java.util.concurrent.TimeUnit;

import javax.ejb.*;

import org.toxsoft.core.tslib.bricks.strid.coll.IStridablesList;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.skf.bridge.s5.supports.IS5BackendGatewaySingleton;
import org.toxsoft.uskat.s5.server.backend.addons.IS5BackendAddonSessionControl;
import org.toxsoft.uskat.s5.server.backend.addons.S5AbstractBackendAddonSession;
import org.toxsoft.uskat.s5.server.sessions.init.IS5SessionInitData;
import org.toxsoft.uskat.s5.server.sessions.init.S5SessionInitResult;
import org.toxsoft.uskat.s5.server.sessions.pas.S5SessionMessenger;

/**
 * Сессия реализации службы {@link IBaGateway}.
 *
 * @author mvk
 */
@Stateful
@StatefulTimeout( value = STATEFULL_TIMEOUT, unit = TimeUnit.MILLISECONDS )
@AccessTimeout( value = ACCESS_TIMEOUT_DEFAULT, unit = TimeUnit.MILLISECONDS )
@TransactionManagement( TransactionManagementType.CONTAINER )
@TransactionAttribute( TransactionAttributeType.SUPPORTS )
@SuppressWarnings( "unused" )
public class S5BaGatewaySession
    extends S5AbstractBackendAddonSession
    implements IS5BaGatewaySession, IS5BackendAddonSessionControl {

  private static final long serialVersionUID = 157157L;

  /**
   * Поддержка бекенда службы управления шлюзами
   */
  @EJB
  private IS5BackendGatewaySingleton gatewaysSupport;

  /**
   * Пустой конструктор.
   */
  public S5BaGatewaySession() {
    super( ISkGatewayHardConstants.BAINF_GATEWAYS );
  }

  // ------------------------------------------------------------------------------------
  // Реализация шаблонных методов S5BackendAddonSession
  //
  @Override
  protected Class<? extends IS5BaGatewaySession> doGetSessionView() {
    return IS5BaGatewaySession.class;
  }

  @Override
  protected void doAfterInit( S5SessionMessenger aMessenger, IS5SessionInitData aInitData,
      S5SessionInitResult aInitResult ) {
    S5BaGatewayData baData = new S5BaGatewayData();
    frontend().frontendData().setBackendAddonData( IBaGateway.ADDON_ID, baData );
  }

  @Override
  protected void doBeforeClose() {
    // nop
  }

  // ------------------------------------------------------------------------------------
  // Реализация IBaGateway
  //
  @Override
  public IStridablesList<ISkGatewayConfiguration> gatewayConfigs() {
    return gatewaysSupport.gatewayConfigs();
  }

  @Override
  public void defineGateway( ISkGatewayConfiguration aGatewayConfig ) {
    TsNullArgumentRtException.checkNull( aGatewayConfig );
    gatewaysSupport.defineGateway( aGatewayConfig );
  }

  @Override
  public void removeGateway( String aGatewayId ) {
    TsNullArgumentRtException.checkNull( aGatewayId );
    gatewaysSupport.removeGateway( aGatewayId );
  }

  // ------------------------------------------------------------------------------------
  // Реализация интерфейса IBaGateway
  //
}
