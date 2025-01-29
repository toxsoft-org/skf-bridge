package org.toxsoft.skf.bridge.s5.lib.impl;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.impl.DataDef.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.s5.lib.impl.ISkResources.*;

import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.uskat.core.*;

/**
 * Конфигурация службы {@link ISkGatewayService}.
 *
 * @author mvk
 */
@SuppressWarnings( "nls" )
public final class S5BackendGatewayConfig {

  /**
   * Префикс идентфикаторов подсистемы
   */
  public static final String SYBSYSTEM_ID_PREFIX = ISkHardConstants.SK_SYSEXT_SERVICE_ID_PREFIX + ".gateways";

  /**
   * Интервал времени (в сутках) используемый для синхронизации данных между локальным и удаленным сервером при
   * образовании связи.
   * <p>
   */
  public static final IDataDef SYNC_INTERVAL = create( SYBSYSTEM_ID_PREFIX + ".syncInterval", INTEGER, //$NON-NLS-1$
      TSID_NAME, N_SYNC_INTERVAL, //
      TSID_DESCRIPTION, D_SYNC_INTERVAL, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, avInt( 3 ) );

  /**
   * Список описаний шлюзов с которыми работает сервер
   * <p>
   * TODO: register SkGatewayInfosKeeper.KEEPER
   */
  public static final IDataDef GATEWAY_INFOS = create( SYBSYSTEM_ID_PREFIX + ".infos", VALOBJ, //$NON-NLS-1$
      TSID_NAME, N_GATEWAYS, //
      TSID_DESCRIPTION, D_GATEWAYS, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, avValobj( new SkGatewayInfos() ) );
}
