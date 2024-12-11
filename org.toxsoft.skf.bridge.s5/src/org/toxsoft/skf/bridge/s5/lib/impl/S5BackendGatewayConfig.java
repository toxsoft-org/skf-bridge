package org.toxsoft.skf.bridge.s5.lib.impl;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.s5.lib.impl.ISkResources.*;

import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.s5.utils.*;

/**
 * Конфигурация службы {@link ISkGatewayService}.
 *
 * @author mvk
 */
@SuppressWarnings( "nls" )
public final class S5BackendGatewayConfig
    extends S5RegisteredConstants {

  /**
   * Префикс идентфикаторов подсистемы
   */
  public static final String SYBSYSTEM_ID_PREFIX = ISkHardConstants.SK_SYSEXT_SERVICE_ID_PREFIX + ".gateways";

  /**
   * Список описаний шлюзов с которыми работает сервер
   * <p>
   * TODO: register SkGatewayConfigurationListKeeper.KEEPER
   */
  public static final IDataDef GATEWAYS = register( ".configurations", VALOBJ, //$NON-NLS-1$
      TSID_NAME, N_GATEWAYS, //
      TSID_DESCRIPTION, D_GATEWAYS, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, avValobj( new SkGatewayConfigurationList() ) );

  /**
   * Все параметры подсистемы.
   */
  public static final IStridablesList<IDataDef> ALL_GATEWAYS_OPDEFS = new StridablesList<>( //
      GATEWAYS //
  );
}
