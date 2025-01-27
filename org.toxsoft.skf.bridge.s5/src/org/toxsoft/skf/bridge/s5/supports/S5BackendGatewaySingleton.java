package org.toxsoft.skf.bridge.s5.supports;

import static org.toxsoft.skf.bridge.s5.supports.IS5Resources.*;
import static org.toxsoft.uskat.s5.server.IS5ImplementConstants.*;
import static org.toxsoft.uskat.s5.utils.threads.impl.S5Lockable.*;

import javax.ejb.*;

import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.skf.bridge.s5.lib.impl.*;
import org.toxsoft.skf.dq.s5.supports.*;
import org.toxsoft.uskat.core.backend.*;
import org.toxsoft.uskat.s5.client.local.*;
import org.toxsoft.uskat.s5.client.remote.*;
import org.toxsoft.uskat.s5.server.backend.impl.*;
import org.toxsoft.uskat.s5.server.backend.supports.clobs.*;
import org.toxsoft.uskat.s5.server.backend.supports.commands.*;
import org.toxsoft.uskat.s5.server.backend.supports.currdata.*;
import org.toxsoft.uskat.s5.server.backend.supports.histdata.*;
import org.toxsoft.uskat.s5.server.backend.supports.objects.*;
import org.toxsoft.uskat.s5.server.sessions.*;
import org.toxsoft.uskat.s5.server.startup.*;
import org.toxsoft.uskat.s5.utils.jobs.*;
import org.toxsoft.uskat.s5.utils.threads.impl.*;

/**
 * Синглетон backend {@link IBaGateway} предоставляемый s5-сервером.
 *
 * @author mvk
 */
@Startup
@Singleton
@LocalBean
@DependsOn( { //
    BACKEND_SKATLET_SINGLETON,//
} )
@TransactionManagement( TransactionManagementType.CONTAINER )
@TransactionAttribute( TransactionAttributeType.SUPPORTS )
@ConcurrencyManagement( ConcurrencyManagementType.BEAN )
@Lock( LockType.READ )
public class S5BackendGatewaySingleton
    extends S5BackendSupportSingleton
    implements IS5BackendGatewaySingleton, IS5ServerJob {

  private static final long serialVersionUID = 157157L;

  /**
   * Имя синглетона в контейнере сервера для организации зависимостей (@DependsOn)
   */
  public static final String BACKEND_GATEWAYS_ID = "S5BackendGatewaySingleton"; //$NON-NLS-1$

  /**
   * Интервал выполнения doJob (мсек)
   */
  private static final long DOJOB_INTERVAL = 1000;

  /**
   * Начальная, неизменяемая, проектно-зависимая конфигурация реализации бекенда сервера
   */
  @EJB
  private IS5InitialImplementSingleton initialImplement;

  /**
   * Менеджер управления сессиями сервера
   */
  @EJB
  private IS5SessionManager sessionManager;

  /**
   * Бекенд поддержки службы объектов
   */
  @EJB
  private IS5BackendObjectsSingleton objectsBackend;

  /**
   * Бекенд поддержки службы текущих данных реального времени
   */
  @EJB
  private IS5BackendCurrDataSingleton currdataBackend;

  /**
   * Бекенд поддержки службы хранимых данных реального времени
   */
  @EJB
  private IS5BackendHistDataSingleton histdataBackend;

  /**
   * Бекенд поддержки службы команд
   */
  @EJB
  private IS5BackendCommandSingleton commandBackend;

  /**
   * Бекенд поддержки службы больших данных
   */
  @EJB
  private IS5BackendClobsSingleton lobsBackend;

  /**
   * Бекенд поддержки службы качества данных
   */
  @EJB
  private IS5BackendDataQualitySingleton dataQualityBackend;

  /**
   * Соединение с локальным сервером
   */
  @EJB
  private IS5LocalConnectionSingleton localConnection;

  /**
   * Поставщик соединений с удаленным сервером
   */
  private ISkBackendProvider remoteConnectionProvider = new S5RemoteBackendProvider();

  /**
   * Запущенные шлюзы
   */
  private IStridablesListEdit<S5Gateway> gateways = new StridablesList<>();

  /**
   * Блокировка доступа к {@link #gateways}
   */
  private S5Lockable gatewaysLock = new S5Lockable();

  /**
   * Тайматут (мсек) ожидания блокировки {@link #gatewaysLock}
   */
  private static final long LOCK_TIMEOUT = 1000;

  static {
    // Регистрация хранителей данных
    SkGatewayValobjUtils.registerS5Keepers();
  }

  /**
   * Пустой конструктор.
   */
  public S5BackendGatewaySingleton() {
    super( BACKEND_GATEWAYS_ID, ISkGatewayHardConstants.BAINF_GATEWAYS.nmName() );
  }

  // ------------------------------------------------------------------------------------
  // Реализация шаблонных методов S5SingletonBase
  //
  @Override
  protected IStringList doConfigurationPaths() {
    IStringListEdit retValue = new StringArrayList();
    retValue.addAll( S5BackendGatewayConfig.ALL_GATEWAYS_OPDEFS.keys() );
    return retValue;
  }

  @Override
  protected IOptionSet doCreateConfiguration() {
    return super.doCreateConfiguration();
  }

  @Override
  protected void onConfigChanged( IOptionSet aPrevConfig, IOptionSet aNewConfig ) {
    SkGatewayConfigurationList prevGateways = S5BackendGatewayConfig.GATEWAYS.getValue( aPrevConfig ).asValobj();
    SkGatewayConfigurationList newGateways = S5BackendGatewayConfig.GATEWAYS.getValue( aNewConfig ).asValobj();
    if( !newGateways.equals( prevGateways ) ) {
      // Чтобы не вызвать блокировку при создании S5Connection делаем сохранение конфигурации через businessApi
      IS5BackendGatewaySingleton businessApi = sessionContext().getBusinessObject( IS5BackendGatewaySingleton.class );
      businessApi.updateGateways();
    }
  }

  @Override
  protected void doInit() {
    super.doInit();
  }

  @Override
  protected void doInitSupport() {
    // Инициализация базового класса
    super.doInitSupport();
    // Запуск doJob
    addOwnDoJob( DOJOB_INTERVAL );
    // TODO: 2020-03-21 mvk
    updateGateways();
  }

  @Override
  protected void doCloseSupport() {
    super.doCloseSupport();
  }

  // ------------------------------------------------------------------------------------
  // IS5BackendGatewaySingleton
  //
  @Override
  public IStridablesList<ISkGatewayConfiguration> gatewayConfigs() {
    IStridablesListEdit<ISkGatewayConfiguration> retValue = new StridablesList<>();
    lockRead( gatewaysLock );
    try {
      for( S5Gateway gateway : gateways ) {
        retValue.add( gateway.configuration() );
      }
    }
    finally {
      unlockRead( gatewaysLock );
    }
    return retValue;
  }

  @Lock( LockType.WRITE )
  @Override
  public void defineGateway( ISkGatewayConfiguration aGatewayConfig ) {
    TsNullArgumentRtException.checkNull( aGatewayConfig );
    // Новая конфигурация службы
    IOptionSetEdit newConfigurations = new OptionSet( configuration() );
    // Список описаний шлюзов в конфигурации службы
    SkGatewayConfigurationList gatewayConfigs =
        S5BackendGatewayConfig.GATEWAYS.getValue( newConfigurations ).asValobj();
    // Добавление описания нового шлюза
    gatewayConfigs.add( aGatewayConfig );
    // Обновление списка описаний в конфигурации
    S5BackendGatewayConfig.GATEWAYS.setValue( newConfigurations, AvUtils.avValobj( gatewayConfigs ) );
    // Сохранение конфигурации может быть только в транзакции
    IS5BackendGatewaySingleton businessApi = sessionContext().getBusinessObject( IS5BackendGatewaySingleton.class );
    // Сохранение настроек в базе данных
    businessApi.saveConfiguration( newConfigurations );
  }

  @Lock( LockType.WRITE )
  @Override
  public void removeGateway( String aGatewayId ) {
    TsNullArgumentRtException.checkNull( aGatewayId );
    // Новая конфигурация службы
    IOptionSetEdit newConfigurations = new OptionSet( configuration() );
    // Список описаний шлюзов в конфигурации службы
    SkGatewayConfigurationList gatewayConfigs =
        S5BackendGatewayConfig.GATEWAYS.getValue( newConfigurations ).asValobj();
    // Удаление описания шлюза
    gatewayConfigs.removeById( aGatewayId );
    // Обновление списка описаний в конфигурации
    S5BackendGatewayConfig.GATEWAYS.setValue( newConfigurations, AvUtils.avValobj( gatewayConfigs ) );
    // Сохранение конфигурации может быть только в транзакции
    IS5BackendGatewaySingleton businessApi = sessionContext().getBusinessObject( IS5BackendGatewaySingleton.class );
    // Сохранение настроек в базе данных
    businessApi.saveConfiguration( newConfigurations );
  }

  @Lock( LockType.READ )
  @Override
  public IS5Gateway findGateway( String aGatewayId ) {
    TsNullArgumentRtException.checkNull( aGatewayId );
    lockRead( gatewaysLock );
    try {
      return gateways.findByKey( aGatewayId );
    }
    finally {
      unlockRead( gatewaysLock );
    }
  }

  @Asynchronous
  @Override
  public void updateGateways() {
    IStridablesList<ISkGatewayConfiguration> config =
        S5BackendGatewayConfig.GATEWAYS.getValue( configuration() ).asValobj();
    // Количество добавленных шлюзов
    int addedCount = 0;
    // Количество обновленных шлюзов
    int updatedCount = 0;
    // Количество удаленных шлюзов
    int removedCount = 0;
    // Проход по всем текущим шлюзам, удаление несуществующих и обновление измененных
    lockWrite( gatewaysLock );
    try {
      for( S5Gateway gateway : new StridablesList<>( gateways ) ) {
        String gatewayId = gateway.id();
        ISkGatewayConfiguration configuration = config.findByKey( gatewayId );
        if( configuration == null ) {
          // Шлюз удален
          gateway.close();
          gateways.removeById( gatewayId );
          removedCount++;
        }
        if( configuration != null && !configuration.equals( gateway.configuration() ) ) {
          // Запись в журнал об пересоздании шлюза (изменение конфигурации)
          logger().info( MSG_RECREATE_GATEWAY_BY_UPDATE_CONFIG, gateway.id(), gateway.configuration(), configuration );
          // Шлюз обновлен
          gateway.close();
          gateways.removeById( gatewayId );
          gateways.add( new S5Gateway( this, configuration ) );
          updatedCount++;
        }
      }
      // Проход по всем конфигурациям, добавление новых
      for( ISkGatewayConfiguration configuration : config ) {
        if( gateways.hasKey( configuration.id() ) ) {
          continue;
        }
        gateways.add( new S5Gateway( this, configuration ) );
        addedCount++;
      }
    }
    finally {
      unlockWrite( gatewaysLock );
    }
    // Запись в журнал количества загруженных конфигураций
    Integer ac = Integer.valueOf( addedCount );
    Integer uc = Integer.valueOf( updatedCount );
    Integer rc = Integer.valueOf( removedCount );
    logger().info( MSG_GW_CONFIGURATIONS_UPDATED, ac, uc, rc );
  }

  // ------------------------------------------------------------------------------------
  // Реализация IS5ServerJob
  //
  @Override
  public void doJob() {
    // 2021-01-19 mvk попытка избавится от странной блокировки
    if( !tryLockWrite( gatewaysLock, LOCK_TIMEOUT ) ) {
      // Ошибка получения блокировки
      logger().warning( ERR_TRY_LOCK, gatewaysLock );
      return;
    }
    try {
      for( S5Gateway gateway : gateways ) {
        try {
          // Фоновая работа шлюза
          gateway.doJob();
        }
        catch( Throwable e ) {
          logger().error( e );
        }
      }
      // Вывод журнала
      logger().debug( MSG_DOJOB );
    }
    finally {
      unlockWrite( gatewaysLock );
    }
  }

  // ------------------------------------------------------------------------------------
  // API пакета
  //
  /**
   * Возвращает начальную, неизменяемую, проектно-зависимая конфигурацию реализации бекенда сервера
   *
   * @return {@link IS5InitialImplementation} неизменяемая конфигурация сервера
   */
  IS5InitialImplementation initialImplement() {
    return initialImplement.impl();
  }

  /**
   * Возвращает поставщика соединения с локальным сервером
   *
   * @return {@link IS5LocalConnectionSingleton} поставщик локального соединения
   */
  IS5LocalConnectionSingleton localConnection() {
    return localConnection;
  }

  /**
   * Возвращает поставщика соединения с удаленным сервером
   *
   * @return {@link ISkBackendProvider} поставщик удаленного соединения
   */
  ISkBackendProvider remoteConnectionProvider() {
    return remoteConnectionProvider;
  }

  /**
   * Возвращает менеджера управления сессиями сервера
   *
   * @return {@link IS5SessionManager} менеджер управления сессиями сервера
   */
  IS5SessionManager sessionManager() {
    return sessionManager;
  }

  /**
   * Возвращает бекенд поддержки объектов
   *
   * @return {@link IS5BackendObjectsSingleton} бекенд поддержки объектов
   */
  IS5BackendObjectsSingleton objectsBackend() {
    return objectsBackend;
  }

  /**
   * Возвращает бекенд поддержки текущих данных реального времени
   *
   * @return {@link IS5BackendCurrDataSingleton} бекенд поддержки текущих данных реального времени
   */
  IS5BackendCurrDataSingleton currdataBackend() {
    return currdataBackend;
  }

  /**
   * Возвращает бекенд поддержки хранимых данных реального времени
   *
   * @return {@link IS5BackendHistDataSingleton} бекенд поддержки хранимых данных реального времени
   */
  IS5BackendHistDataSingleton histdataBackend() {
    return histdataBackend;
  }

  /**
   * Возвращает бекенд поддержки службы команд
   *
   * @return {@link IS5BackendCommandSingleton} бекенд поддержки команд
   */
  IS5BackendCommandSingleton commandBackend() {
    return commandBackend;
  }

  /**
   * Возвращает бекенд поддержки больших данных
   *
   * @return {@link IS5BackendClobsSingleton} бекенд поддержки больших данных
   */
  IS5BackendClobsSingleton lobsBackend() {
    return lobsBackend;
  }

  /**
   * Возвращает бекенд поддержки службы качества данных
   *
   * @return {@link IS5BackendDataQualitySingleton} бекенд поддержки
   */
  IS5BackendDataQualitySingleton dataQualityBackend() {
    return dataQualityBackend;
  }

  // ------------------------------------------------------------------------------------
  // Внутренние методы
  //

}
