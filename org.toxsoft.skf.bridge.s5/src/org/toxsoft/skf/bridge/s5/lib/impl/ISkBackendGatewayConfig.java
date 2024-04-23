package org.toxsoft.skf.bridge.s5.lib.impl;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.impl.DataDef.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.s5.lib.impl.ISkResources.*;

import org.toxsoft.core.tslib.av.metainfo.IDataDef;
import org.toxsoft.skf.bridge.s5.lib.ISkGatewayService;
import org.toxsoft.uskat.s5.server.backend.supports.IS5BackendAddonConfig;

/**
 * Конфигурация службы {@link ISkGatewayService}
 *
 * @author mvk
 */
public interface ISkBackendGatewayConfig
    extends IS5BackendAddonConfig {

  /**
   * Список описаний шлюзов с которыми работает сервер
   * <p>
   * TODO: register SkGatewayConfigurationListKeeper.KEEPER
   */
  IDataDef GATEWAYS = create( "s5.gateways.configurations", VALOBJ, //$NON-NLS-1$
      TSID_NAME, N_GATEWAYS, //
      TSID_DESCRIPTION, D_GATEWAYS, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, avValobj( new SkGatewayConfigurationList() ) );

}
