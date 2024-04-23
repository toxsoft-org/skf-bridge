package org.toxsoft.skf.bridge.s5.addons;

import javax.ejb.Remote;

import org.toxsoft.skf.bridge.s5.lib.IBaGateway;
import org.toxsoft.uskat.s5.server.backend.addons.IS5BackendAddonSession;

/**
 * Сессия службы {@link IBaGateway}.
 *
 * @author mvk
 */
@Remote
public interface IS5BaGatewaySession
    extends IBaGateway, IS5BackendAddonSession {
  // nop
}
