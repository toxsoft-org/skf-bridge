package org.toxsoft.skf.bridge.s5.lib;

import static org.toxsoft.skf.bridge.s5.lib.ISkResources.*;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.uskat.classes.*;
import org.toxsoft.uskat.core.backend.*;

/**
 * Unchangeable constants of the gateway service.
 *
 * @author mvk
 */
@SuppressWarnings( { "nls", "javadoc" } )
public interface ISkGatewayHardConstants {

  /**
   * Тикет качества данных: список идентификаторов (объекты класса {@link ISkNetNode}) пройденных сетевых узлов.
   * <p>
   * Тип тикета: {@link EAtomicType#VALOBJ} {@link IStringList}.
   */
  String TICKET_ROUTE = "transmittedServers";

  // ------------------------------------------------------------------------------------
  // IBaGateway
  //
  String BAID_GATEWAYS = ISkBackendHardConstant.SKB_ID + ".Gateways";

  IStridable BAINF_GATEWAYS = new Stridable( BAID_GATEWAYS, STR_N_BA_GATEWAY, STR_D_BA_GATEWAY );

}
