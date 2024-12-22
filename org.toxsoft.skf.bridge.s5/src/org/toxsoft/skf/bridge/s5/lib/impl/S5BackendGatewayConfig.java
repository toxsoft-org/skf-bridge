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
   * Хост удаленного сервера по умолчанию.
   */
  public static final IDataDef DEFAULT_HOST = register( SYBSYSTEM_ID_PREFIX + ".host", STRING, //$NON-NLS-1$
      TSID_NAME, N_HOST, //
      TSID_DESCRIPTION, D_HOST, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, avStr( "localhost" ) );

  /**
   * Порт удаленного сервера по умолчанию.
   */
  public static final IDataDef DEFAULT_PORT = register( SYBSYSTEM_ID_PREFIX + ".port", INTEGER, //$NON-NLS-1$
      TSID_NAME, N_PORT, //
      TSID_DESCRIPTION, D_PORT, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, avInt( 8080 ) );

  /**
   * Логин пользователя для подключения к удаленному серверу по умолчанию.
   */
  public static final IDataDef DEFAULT_LOGIN = register( SYBSYSTEM_ID_PREFIX + ".login", STRING, //$NON-NLS-1$
      TSID_NAME, N_LOGING, //
      TSID_DESCRIPTION, D_LOGING, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, avStr( "root" ) );

  /**
   * Пароль пользователя для подключения к удаленному серверу по умолчанию.
   */
  public static final IDataDef DEFAULT_PASSWORD = register( SYBSYSTEM_ID_PREFIX + ".password", STRING, //$NON-NLS-1$
      TSID_NAME, N_PASSWORD, //
      TSID_DESCRIPTION, D_PASSWORD, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, avStr( "1" ) );

  /**
   * Интервал времени (в сутках) используемый для синхронизации данных между локальным и удаленным сервером при
   * образовании связи.
   * <p>
   */
  public static final IDataDef SYNC_INTERVAL = register( SYBSYSTEM_ID_PREFIX + ".syncInterval", INTEGER, //$NON-NLS-1$
      TSID_NAME, N_SYNC_INTERVAL, //
      TSID_DESCRIPTION, D_SYNC_INTERVAL, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, avInt( 3 ) );

  /**
   * Список описаний шлюзов с которыми работает сервер
   * <p>
   * TODO: register SkGatewayConfigurationListKeeper.KEEPER
   */
  public static final IDataDef GATEWAYS = register( SYBSYSTEM_ID_PREFIX + ".configurations", VALOBJ, //$NON-NLS-1$
      TSID_NAME, N_GATEWAYS, //
      TSID_DESCRIPTION, D_GATEWAYS, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, avValobj( new SkGatewayConfigurationList() ) );

  /**
   * Все параметры подсистемы.
   */
  public static final IStridablesList<IDataDef> ALL_GATEWAYS_OPDEFS = new StridablesList<>( //
      DEFAULT_LOGIN, //
      DEFAULT_PASSWORD, //
      SYNC_INTERVAL, //
      GATEWAYS //
  );
}
