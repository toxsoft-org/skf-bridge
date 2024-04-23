package org.toxsoft.skf.bridge.s5.lib;

import static org.toxsoft.skf.bridge.s5.lib.ISkResources.*;

import org.toxsoft.core.tslib.bricks.strid.IStridable;
import org.toxsoft.core.tslib.bricks.strid.impl.Stridable;
import org.toxsoft.uskat.core.backend.ISkBackendHardConstant;

/**
 * Unchangeable constants of the gateway service.
 *
 * @author mvk
 */
@SuppressWarnings( { "nls", "javadoc" } )
public interface ISkGatewayHardConstants {

  // ------------------------------------------------------------------------------------
  // IBaGateway
  //
  String BAID_GATEWAYS = ISkBackendHardConstant.SKB_ID + ".Gateways";

  IStridable BAINF_GATEWAYS = new Stridable( BAID_GATEWAYS, STR_N_BA_GATEWAY, STR_D_BA_GATEWAY );

}
