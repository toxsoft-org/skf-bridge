package org.toxsoft.skf.bridge.s5.addons;

import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.uskat.s5.server.backend.addons.*;

import jakarta.ejb.*;

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
