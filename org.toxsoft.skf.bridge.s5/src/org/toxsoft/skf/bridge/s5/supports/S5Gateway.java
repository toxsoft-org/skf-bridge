package org.toxsoft.skf.bridge.s5.supports;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.coll.impl.TsCollectionsUtils.*;
import static org.toxsoft.skf.bridge.s5.lib.impl.SkGatewayGwids.*;
import static org.toxsoft.skf.bridge.s5.supports.IS5Resources.*;
import static org.toxsoft.uskat.s5.common.IS5CommonResources.*;

import java.util.*;

import org.toxsoft.core.log4j.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.av.temporal.*;
import org.toxsoft.core.tslib.bricks.ctx.*;
import org.toxsoft.core.tslib.bricks.ctx.impl.*;
import org.toxsoft.core.tslib.bricks.events.change.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.bricks.threadexec.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.bricks.time.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.coll.synch.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.login.*;
import org.toxsoft.core.tslib.utils.logs.*;
import org.toxsoft.skf.alarms.lib.impl.*;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.skf.dq.lib.*;
import org.toxsoft.skf.dq.lib.impl.*;
import org.toxsoft.uskat.classes.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.cmdserv.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.api.gwids.*;
import org.toxsoft.uskat.core.api.hqserv.*;
import org.toxsoft.uskat.core.api.rtdserv.*;
import org.toxsoft.uskat.core.backend.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.impl.*;
import org.toxsoft.uskat.s5.client.*;
import org.toxsoft.uskat.s5.client.remote.connection.*;
import org.toxsoft.uskat.s5.common.*;
import org.toxsoft.uskat.s5.server.*;
import org.toxsoft.uskat.s5.server.backend.*;
import org.toxsoft.uskat.s5.server.backend.supports.events.*;
import org.toxsoft.uskat.s5.server.backend.supports.histdata.*;
import org.toxsoft.uskat.s5.server.interceptors.*;
import org.toxsoft.uskat.s5.server.startup.*;
import org.toxsoft.uskat.s5.utils.*;
import org.toxsoft.uskat.s5.utils.jobs.*;
import org.toxsoft.uskat.s5.utils.progress.*;

/**
 * Однонаправленный(!) шлюз службы {@link IBaGateway}.
 * <p>
 * <b>Причины по которым шлюз определяется только как однонаправленный: </b><br>
 * {@link ISkCoreApi} не позволяет перехватывать в полном объеме вызовы и делегировать их шлюзу. Например, нельзя
 * перехватить поступление хранимых данных в режиме реального времени (как c текущими данными с их слушателем
 * {@link ISkCurrDataChangeListener}). Такая же ситуация может возникнуть с любой службой (core, skf) и требовать от их
 * API методы поддержки шлюза является явно неправильным.
 * <p>
 * По этой причине (чтобы не менять API служб под нужны шлюза), предлагается делать перехват необходимых потоков данных
 * для шлюза в самом бекенде. Например, в бекенде s5-сервера для этого используется механизм интерсепции
 * {@link IS5Interceptor} который реализуется синглетонами поддержки бекенда {@link IS5BackendSupportSingleton}.
 *
 * @author mvk
 */
class S5Gateway
    extends Stridable
    implements IS5Gateway, IS5ServerJob, //
    IS5HistDataInterceptor, IS5EventInterceptor, //
    ISkCommandExecutor, ISkDataQualityChangeListener, ISkConnectionListener {

  /**
   * Тикет качества данных: список идентификаторов (объекты класса {@link ISkNetNode}) пройденных сетевых узлов.
   * <p>
   * Тип тикета: {@link EAtomicType#VALOBJ} {@link IStringList}.
   */
  private static final String TICKET_ROUTE = "transmittedServers"; //$NON-NLS-1$

  /**
   * Служба шлюзов
   */
  private S5BackendGatewaySingleton owner;

  /**
   * Список идентификаторов всех серверов {@link ISkServer} в направлении которых производится передача
   */
  private final IStringList transmittingServers;

  /**
   * Конфигурация шлюза
   */
  private final ISkGatewayInfo configuration;

  /**
   * Соединение с локальным сервером
   */
  private final ISkConnection localConnection;

  /**
   * Соединение с удаленным сервером
   */
  private ISkConnection remoteConnection;

  /**
   * Исполнитель потоков uskat-соединений (один на весь шлюз)
   */
  private final ITsThreadExecutor threadExecutor;

  /**
   * Поставщик удаленного backend
   */
  private final ISkBackendProvider remoteBackendProvider;

  /**
   * Порт передачи текущих данных от локального сервера на удаленный
   */
  private S5GatewayCurrDataPort localToRemoteCurrdataPort;

  /**
   * Служба данных реального времени локального сервера
   */
  private ISkRtdataService localRtDataService;

  /**
   * Служба данных реального времени удаленного сервера
   */
  private ISkRtdataService remoteRtDataService;

  /**
   * Карта значений метки "пройденный маршрут значения данного" по идентификаторам передаваемых через мост данных
   * определенные через локальную службу качества.
   * <p>
   * Ключ: идентификатор данного значения которого могут быть переданы через шлюз; Значение: значение метки "пройденный
   * маршрут значения данного" для данного.
   */
  private IMap<Gwid, IAtomicValue> transmittingGwids;

  /**
   * Карта каналов записи исторических данных
   * <p>
   * Ключ: {@link Gwid}-идентификатор данного;<br>
   * Значение: канал записи.
   */
  private final IMapEdit<Gwid, ISkWriteHistDataChannel> writeHistData = new ElemMap<>();

  /**
   * Служба команд (чтение)
   */
  private ISkCommandService remoteCmdService;

  /**
   * Служба команд (запись)
   */
  private ISkCommandService localCmdService;

  /**
   * Карта выполняемых команд
   * <p>
   * Ключ: идентификатор команды;<br>
   * Значение: выполняемая команда
   */
  private IMapEdit<Gwid, IDtoCommand> executingCommands = new SynchronizedMap<>( new ElemMap<>() );

  /**
   * Идентификаторы событий передаваемых на удаленный сервера
   */
  private HashSet<Gwid> eventGwids = new HashSet<>();

  /**
   * Слушатель исполнения команд
   */
  private InternalCommandStateChangedListener executingCommandsListener = new InternalCommandStateChangedListener();

  /**
   * Список идентификаторов команд поддерживаемых локальными исполнителями и зарегистрированные на удаленном сервере
   */
  private GwidList remoteCmdGwids = new GwidList();

  /**
   * Служба идентификаторов (чтение)
   */
  private ISkGwidService localGwidService;

  /**
   * Служба событий (запись)
   */
  private ISkEventService remoteEventService;

  /**
   * Служба качества данных (чтение)
   */
  private ISkDataQualityService localDataQualityService;

  /**
   * Служба качества данных (запись)
   */
  private ISkDataQualityService remoteDataQualityService;

  /**
   * Служба служба (чтение)
   */
  // private ISkAlarmService localAlarmService;

  /**
   * Признак готовности для синхронизации данных соединений
   */
  private boolean readyForSynchronize;

  /**
   * Признак того, что шлюз завершил своб работу
   */
  private boolean completed;

  /**
   * Признак требование пересоздать соединение с удаленным сервером
   */
  private volatile boolean needRecreate;

  /**
   * /** Признак требование провести синхронизацию с удаленным сервером
   */
  private volatile boolean needSynchronize;

  /**
   * Интервал вывода статистики.
   */
  private static final long TRANSMITTED_INTERVAL = 10 * 1000;

  /**
   * Количество переданных текущих данных.
   */
  private int transmittedCurrdata;

  /**
   * Количество переданных значений текущих данных.
   */
  private int transmittedCurrdataItems;

  /**
   * Количество переданных хранимых данных.
   */
  private int transmittedHistdata;

  /**
   * Количество значений переданных хранимых данных.
   */
  private int transmittedHistdataItems;

  /**
   * Количество передач событий.
   */
  private int transmittedEvents;

  /**
   * Количество переданных событий.
   */
  private int transmittedEventItems;

  /**
   * Таймер статистики
   */
  private final S5IntervalTimer transmittedTimer = new S5IntervalTimer( TRANSMITTED_INTERVAL );

  /**
   * Журнал работы.
   */
  private ILogger logger = LoggerWrapper.getLogger( getClass() );

  /**
   * Конструктор шлюза
   *
   * @param aOwner {@link S5BackendGatewaySingleton} синглетон службы
   * @param aConfiguration {@link ISkGatewayInfo} конфигурация шлюза
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  S5Gateway( S5BackendGatewaySingleton aOwner, ISkGatewayInfo aConfiguration ) {
    super( aConfiguration.id(), aConfiguration.nmName(), aConfiguration.description() );
    owner = aOwner;
    transmittingServers = owner.gatewayConfigs().ids();
    configuration = aConfiguration;
    logger.info( MSG_GW_STARTED, id() );

    // Регистрация расширений API соединения
    SkCoreUtils.registerSkServiceCreator( SkDataQualityService.CREATOR );
    SkCoreUtils.registerSkServiceCreator( SkAlarmService.CREATOR );

    // Создание соединения с локальным сервером
    String programName = getClass().getSimpleName() + '_' + configuration.id();
    // Создание локального соединения
    localConnection = aOwner.localConnection().open( programName );
    // Создание соединения с удаленным сервером
    remoteBackendProvider = aOwner.remoteConnectionProvider();
    remoteConnection = SkCoreUtils.createConnection();
    // Исполнитель потоков
    threadExecutor = SkThreadExecutorService.getExecutor( localConnection.coreApi() );
    // Попытка открыть соединение с удаленном сервером
    // 2021-01-19 mvk tryOpenRemote должна работать под блокировкой
    tryOpenRemote();

    // owner.objectsBackend().addObjectsInterceptor( new
    // S5GatewayObjectsInterceptor( this ), 0 );
    owner.histdataBackend().addHistDataInterceptor( this, 0 );
    owner.eventBackend().addEventInterceptor( this, 0 );
    // owner.lobsBackend().addLobsInterceptor( new S5GatewayLobsInterceptor( this ),
    // 0 );
  }

  // ------------------------------------------------------------------------------------
  // IS5Gateway
  //
  @Override
  public ISkGatewayInfo configuration() {
    return configuration;
  }

  @Override
  public ISkConnection localConnection() {
    return (localConnection.state() != ESkConnState.CLOSED ? localConnection : null);
  }

  @Override
  public ISkConnection remoteConnection() {
    return (remoteConnection.state() != ESkConnState.CLOSED ? remoteConnection : null);
  }

  // ------------------------------------------------------------------------------------
  // IS5ServerJob
  //
  @Override
  public void doJobPrepare() {
    // nop
  }

  @SuppressWarnings( "boxing" )
  @Override
  public void doJob() {
    if( needRecreate ) {
      // Попытка запуска отложенной синхронизации из doJob
      logger.warning( ERR_TRY_RECREATE_FROM_DOJOB, id() );
      // Завершение старого соединения
      threadExecutor.syncExec( () -> remoteConnection.close() );
      // Создание нового соединения
      remoteConnection = SkCoreUtils.createConnection();
      // Сброс требования
      needRecreate = false;
    }
    if( remoteConnection.state() == ESkConnState.CLOSED && !completed ) {
      // Повторная попытка открыть соединение с удаленном сервером
      tryOpenRemote();
    }
    if( remoteConnection.state() != ESkConnState.ACTIVE ) {
      // Нет связи с удаленным сервером
      logger.error( ERR_DOJOB_NOT_CONNECTION, id(), remoteConnection );
    }
    if( remoteConnection.state() == ESkConnState.ACTIVE && needSynchronize ) {
      // Попытка запуска отложенной синхронизации из doJob
      logger.warning( ERR_TRY_SYNCH_FROM_DOJOB, id() );
      // Синхронизация данных с удаленным сервером
      synchronize();
    }
    if( transmittedTimer.update() ) {
      // Вывод в журнал количества переданных данных
      logger.info( MSG_TRANSIMITTED, id(), transmittingGwids.size(), //
          transmittedCurrdata, transmittedCurrdataItems, //
          transmittedHistdata, transmittedHistdataItems, //
          transmittedEvents, transmittedEventItems //
      );
      // Сброс статистики
      transmittedCurrdata = 0;
      transmittedCurrdataItems = 0;
      transmittedHistdata = 0;
      transmittedHistdataItems = 0;
      transmittedEvents = 0;
      transmittedEventItems = 0;
    }
  }

  @Override
  public boolean completed() {
    return completed;
  }

  @Override
  public void close() {
    // Шлюз завершает работу
    logger.info( MSG_GW_CLOSE, id() );
    synchronize();
    // Завершение работы наборов данных
    if( localToRemoteCurrdataPort != null ) {
      localToRemoteCurrdataPort.close();
      localToRemoteCurrdataPort = null;
    }
    if( writeHistData != null ) {
      for( ISkRtdataChannel channel : writeHistData ) {
        try {
          channel.close();
        }
        catch( Throwable e ) {
          logger.error( e );
        }
      }
      writeHistData.clear();
    }
    remoteConnection.close();
    localConnection.close();
    // Дерегистрация интерсепторов
    owner.histdataBackend().removeHistDataInterceptor( this );
    // Шлюз выгружен
    completed = true;
    logger.info( MSG_GW_CLOSED, id() );
  }

  // ------------------------------------------------------------------------------------
  // IS5HistDataInterceptor
  //
  @Override
  public boolean beforeWriteHistData( IMap<Gwid, Pair<ITimeInterval, ITimedList<ITemporalAtomicValue>>> aValues ) {
    Integer count = Integer.valueOf( aValues.size() );
    Gwid first = aValues.keys().first();
    // Передача хранимых данных
    logger.debug( MSG_GW_HISTDATA_TRANSFER, id(), count, first );
    // Передача данных синхронизируется через исполнитель потоков соединения
    threadExecutor.asyncExec( () -> {
      // Текущий канал по которому проводится запись
      ISkWriteHistDataChannel writeChannel = null;
      try {
        for( Gwid gwid : aValues.keys() ) {
          writeChannel = writeHistData.findByKey( gwid );
          if( writeChannel == null ) {
            // Не найден канал записи хранимых данных
            logger.debug( ERR_HISTDATA_WRITE_CHANNEL_NOT_FOUND, id(), gwid );
            continue;
          }
          Pair<ITimeInterval, ITimedList<ITemporalAtomicValue>> sequence = aValues.getByKey( gwid );
          ITimeInterval interval = sequence.left();
          ITimedList<ITemporalAtomicValue> values = sequence.right();
          writeChannel.writeValues( interval, values );
          transmittedHistdata++;
          transmittedHistdataItems += values.size();
        }
        // Завершение передачи хранимых данных
        logger.debug( MSG_GW_HISTDATA_TRANSFER_FINISH, id(), count, first );
      }
      catch( Throwable e ) {
        // Ошибка передачи хранимых данных.
        logger.error( e, ERR_SEND_HISTDATA, id(), writeChannel != null ? writeChannel.gwid() : "N/A", cause( e ) ); //$NON-NLS-1$
        //// Запланирована синхронизация
        //// needSynchronize = true;
        // Требование пересоздания соединения
        needRecreate = true;
      }
    } );
    // Разрешить дальшейшее выполнение операции
    return true;
  }

  @Override
  public void afterWriteHistData( IMap<Gwid, Pair<ITimeInterval, ITimedList<ITemporalAtomicValue>>> aValues ) {
    // nop
  }

  // ------------------------------------------------------------------------------------
  // IS5EventInterceptor
  //
  @Override
  public boolean beforeWriteEvents( ISkEventList aEvents ) {
    // Передача событий синхронизируется через исполнитель потоков соединения
    threadExecutor.asyncExec( () -> {
      // Список событий пересылаемых на удаленный сервер
      SkEventList events = new SkEventList( aEvents.size() );
      for( SkEvent event : aEvents ) {
        if( eventGwids.contains( event.eventGwid() ) ) {
          events.add( event );
        }
      }
      if( events.size() == 0 ) {
        return;
      }
      Integer count = Integer.valueOf( events.size() );
      SkEvent first = events.first();
      // Передача событий
      logger.info( MSG_GW_EVENT_TRANSFER, id(), count, first );
      remoteEventService.fireEvents( events );
      transmittedEvents++;
      transmittedEventItems += events.size();
      // Завершение передачи событий
      logger.info( MSG_GW_EVENT_TRANSFER_FINISH, id(), count, first );
    } );
    // Разрешить дальшейшее выполнение операции
    return true;
  }

  @Override
  public void afterWriteEvents( ISkEventList aEvents ) {
    // nop
  }

  // ------------------------------------------------------------------------------------
  // ISkCommandExecutor
  //
  @Override
  public void executeCommand( IDtoCommand aCmd ) {
    // Передача команды исполнителю
    logger.info( MSG_GW_COMMAND_TRANSFER, id(), aCmd );
    // Время начала запроса
    long traceStartTime = System.currentTimeMillis();
    // Передача команды синхронизируется через исполнитель потоков соединения
    threadExecutor.asyncExec( () -> {
      // Передача запроса
      try {
        ISkCommand cmd = localCmdService.sendCommand( aCmd.cmdGwid(), aCmd.authorSkid(), aCmd.argValues() );
        cmd.stateEventer().addListener( executingCommandsListener );
        executingCommands.put( aCmd.cmdGwid(), aCmd );
        // Время выполнения
        Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
        // Завершение передачи команды исполнителю
        logger.info( MSG_GW_COMMAND_TRANSFER_FINISH, id(), aCmd, traceTime );
      }
      catch( Throwable e ) {
        // Время выполнения
        Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
        // Ошибка передачи команды. Запланирована синхронизация
        logger.error( e, ERR_SEND_CMD, id(), traceTime, cause( e ) );
        needSynchronize = true;
      }
    } );
  }

  // ------------------------------------------------------------------------------------
  // Слушатель изменения состояния выполняемых команд
  //
  private final class InternalCommandStateChangedListener
      implements IGenericChangeListener {

    @Override
    public void onGenericChangeEvent( Object aSource ) {
      if( !(aSource instanceof SkCommand) ) {
        logger.error(
            "S5GateWay.InternalCommandStateChangedListener: incorrect logic (aSource instanceof SkCommand == false)" ); //$NON-NLS-1$
        return;
      }
      // Передача состояния команды синхронизируется через исполнитель потоков соединения
      threadExecutor.asyncExec( () -> {
        ISkCommand aCommand = (SkCommand)aSource;
        // Передача состояния команды отправителю
        logger.info( MSG_GW_COMMAND_STATE_TRANSFER, id(), aCommand );
        // Признак завершения выполнения команды
        boolean complete = aCommand.state().state().isComplete();
        // Команда полученная от удаленного сервера
        Gwid gwid = aCommand.cmdGwid();
        IDtoCommand cmd = (complete ? executingCommands.removeByKey( gwid ) : executingCommands.findByKey( gwid ));
        if( cmd == null ) {
          // Команда не отправлялась шлюзом (отправил другой клиент?)
          logger.warning( MSG_GW_ALIEN_COMMAND, id() );
          return;
        }
        // Время начала запроса
        long traceStartTime = System.currentTimeMillis();
        try {
          remoteCmdService.changeCommandState( new DtoCommandStateChangeInfo( cmd.instanceId(), aCommand.state() ) );
          // Время выполнения
          Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
          // Завершение передачи состояния команды отправителю
          logger.info( mSG_GW_COMMAND_STATE_TRANSFER_FINISH, id(), aCommand, traceTime );
        }
        catch( Throwable e ) {
          // Время выполнения
          Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
          // Ошибка передачи команды. Запланирована синхронизация
          logger.error( e, ERR_SEND_CMD_STATE, id(), traceTime, cause( e ) );
          needSynchronize = true;
        }
      } );
    }
  }

  // ------------------------------------------------------------------------------------
  // Слушатель изменения списка поддерживаемых команд
  //
  private final class InternalGloballyHandledGwidsListener
      implements IGenericChangeListener {

    @Override
    public void onGenericChangeEvent( Object aSource ) {
      if( !(aSource instanceof SkCoreServCommands) ) {
        logger.error(
            "S5GateWay.InternalGloballyHandledGwidsListener: incorrect logic (aSource instanceof SkCommand == false)" ); //$NON-NLS-1$
        return;
      }
      ISkCommandService service = (ISkCommandService)aSource;
      IGwidList listGloballyHandledCommandGwids = service.listGloballyHandledCommandGwids();
      // Передача списка исполнителей команд
      Integer count = Integer.valueOf( listGloballyHandledCommandGwids.size() );
      Gwid firstCmdGwid = listGloballyHandledCommandGwids.first();
      logger.info( MSG_TRANSFER_EXECUTORS_START, id(), count, firstCmdGwid );
      // Данные по которым предоставляется качество
      IGwidList qualityGwids = owner.dataQualityBackend().getConnectedResources( Skid.NONE );
      // Список команд которые могут быть выполнены службой команд
      IGwidList cmdGwids = getExecutableCmdGwids( qualityGwids );
      // Вывод в журнал в отладке
      if( logger.isSeverityOn( ELogSeverity.DEBUG ) ) {
        // Текстовое представление идентификаторов для журнала
        String cmdGwidsString = getGwidsString( cmdGwids );
        // Изменение списка исполнителей команд
        logger.debug( MSG_CHANGE_CMD_EXECUTORS, id(), cmdGwidsString );
      }
      // Исполнители команд синхронизируются через исполнитель потоков соединения
      threadExecutor.asyncExec( () -> {
        // Сихнонизация исполнителей команды
        synchronizeCmdExecutors( cmdGwids );
      } );
    }
  }

  // ------------------------------------------------------------------------------------
  // ISkDataQualityChangeListener
  //
  @Override
  public void onResourcesStateChanged( ISkDataQualityService aSource, String aTicketId ) {
    if( !aTicketId.equals( ISkDataQualityService.TICKET_ID_NO_CONNECTION ) ) {
      // Обработка только качества данных потеря/восстановление связи
      return;
    }
    // Сообщение об изменении списка отслеживаемых ресурсов
    logger.info( MSG_SYNCHONIZE_BY_DATAQUALITY_RESOURCES, id() );
    try {
      try {
        if( aSource.equals( localDataQualityService ) ) {
          // Данные локального сервера которые могут быть переданы через мост
          IMap<Gwid, IAtomicValue> gwids = getTransmittingGwids( transmittingServers, localDataQualityService );
          if( isListsSameContent( transmittingGwids.keys(), gwids.keys() ) ) {
            // Набор не изменился
            return;
          }
          transmittingGwids = gwids;
        }
        // Выставление признака необходимости синхронизации
        needSynchronize = true;
        // Попытка синхронизации данных
        synchronize();
      }
      catch( Throwable e ) {
        // Ошибка синхронизации с удаленным сервером. Соединение будет закрыто
        logger.error( e, ERR_SYNCHONIZE, id(), cause( e ) );
        // 2021-01-24 mvk
        // Завершение текущего соединения
        // remoteConnection.close();
      }
    }
    finally {
      // Завершение синхронизации по отслеживаемым ресурсам ISkDataQualityService
      logger.info( MSG_SYNCHONIZE_BY_DATAQUALITY_RESOURCES_FINISH, id() );
    }
  }

  // ------------------------------------------------------------------------------------
  // ISkConnectionListener
  //
  @Override
  public void onSkConnectionStateChanged( ISkConnection aSource, ESkConnState aOldState ) {
    // 2020-10-12 mvk doJob + mainLock
    // 2021-01-19 mvk onSkConnectionStateChanged уже работает под connLock???
    // lockWrite( connLock );
    // try {
    switch( aSource.state() ) {
      case ACTIVE:
        // Подключение к удаленному серверу
        logger.info( MSG_GW_REMOTE_CONNECTED, id() );
        // Выставление признака необходимости синхронизации
        needSynchronize = true;
        // Попытка синхронизации данных соединений
        synchronize();
        break;
      case INACTIVE:
      case CLOSED:
        // Закрыто соединение с удаленным сервером
        logger.info( MSG_GW_REMOTE_DISCONNECTED, id() );
        // Очистка списка исполняемых команд
        synchronized (remoteCmdGwids) {
          remoteCmdGwids.clear();
        }
        // paused = true
        // 2020-07-04 mvk не очень полезно и даже в некоторых случаях вредно
        // synchronize( true );
        break;
      default:
        throw new TsNotAllEnumsUsedRtException();
    }
    // }
    // finally {
    // // 2020-10-12 mvk doJob + mainLock
    // unlockWrite( connLock );
    // }
  }

  // ------------------------------------------------------------------------------------
  // Object
  //
  @Override
  public String toString() {
    return configuration.toString();
  }

  // ------------------------------------------------------------------------------------
  // Внутренние методы
  //
  /**
   * Проводит инициализацию шлюза после соединения с удаленным сервером
   * <p>
   * Метод должен вызываться под блокировкой connLock
   */
  private void tryOpenRemote() {
    if( remoteConnection.state() != ESkConnState.CLOSED ) {
      // Инициализация уже выполнена
      logger.warning( ERR_GATEWAY_ALREADY_INITED, id() );
      return;
    }
    try {
      // Начало инициализации шлюза
      logger.info( MSG_GW_INIT_START, id(), threadExecutor.thread().getName() );
      // Установка слушателя соединения
      remoteConnection.addConnectionListener( this );
      // Попытка открыть удаленное соединение
      openRemoteConnection( remoteConnection, remoteBackendProvider, threadExecutor, configuration,
          owner.initialImplement() );
      // Локальное API
      ISkCoreApi localApi = localConnection.coreApi();
      // Удаленное API
      ISkCoreApi remoteApi = remoteConnection.coreApi();
      // Служба идентификаторов
      localGwidService = localApi.gwidService();
      // Службы текущих данных: чтение/запись
      localRtDataService = localApi.rtdService();
      remoteRtDataService = remoteApi.rtdService();
      // TODO: хранимые данные
      // Служба команд: получение/передача команд
      remoteCmdService = remoteApi.cmdService();
      localCmdService = localApi.cmdService();
      // Служба событий: чтение/запись
      remoteEventService = remoteApi.eventService();
      // Служба качества данных: чтение/запись
      localDataQualityService = localApi.getService( ISkDataQualityService.SERVICE_ID );
      remoteDataQualityService = remoteApi.getService( ISkDataQualityService.SERVICE_ID );
      // Регистрация обновление тикета "пройденных мостов"
      IDataType dt = new DataType( EAtomicType.VALOBJ );
      localDataQualityService.defineTicket( TICKET_ROUTE, STR_ROUTE, STR_ROUTE_D, dt );
      remoteDataQualityService.defineTicket( TICKET_ROUTE, STR_ROUTE, STR_ROUTE_D, dt );

      // Служба алармов
      // ISkAlarmService localAlarmService = localApi.getService( ISkAlarmService.SERVICE_ID );
      // Создание портов передачи текущих данных
      localToRemoteCurrdataPort = new S5GatewayCurrDataPort( id(), localRtDataService, remoteRtDataService, logger ) {

        @Override
        public void onCurrData( IMap<Gwid, IAtomicValue> aNewValues ) {
          super.onCurrData( aNewValues );
          transmittedCurrdata++;
          transmittedCurrdataItems += aNewValues.size();
        }
      };

      // Регистрация слушателей служб
      threadExecutor.syncExec( () -> {
        localDataQualityService.eventer().addListener( this );
        localCmdService.globallyHandledGwidsEventer().addListener( new InternalGloballyHandledGwidsListener() );
        // Признак готовности для синхронизации
        readyForSynchronize = true;
        // Выставление признака необходимости синхронизации
        needSynchronize = true;
        // Данные локального сервера которые могут быть переданы через мост
        transmittingGwids = getTransmittingGwids( transmittingServers, localDataQualityService );
        // Попытка синхронизации наборов данных, слушателей подключенных соединений
        synchronize();
      } );

      // threadExecutor.syncExec( () -> {
      // // TODO: в разработке события, команды
      // remoteEventService.registerHandler( new GwidList(
      // Gwid.createEvent( ISkAlarmConstants.CLSID_ALARM, Gwid.STR_MULTI_ID, ISkAlarmConstants.EVID_ACKNOWLEDGE ) ),
      // aEvents -> {
      // // Квитирование алармов локального сервера по событиям квитирования удаленного сервера
      // for( SkEvent event : aEvents ) {
      // Skid alarmId = event.eventGwid().skid();
      // Skid authorId = event.paramValues().getValobj( ISkAlarmConstants.EVPRMID_ACK_AUTHOR );
      // String comment = event.paramValues().getValobj( ISkAlarmConstants.EVPRMID_ACK_COMMNET );
      // ISkAlarm alarm = localAlarmService.findAlarm( alarmId.strid() );
      // if( alarm != null ) {
      // alarm.sendAcknowledge( authorId, comment );
      // }
      // }
      // } );

      // long currTime = System.currentTimeMillis();
      // long startTime = currTime - SYNC_INTERVAL.getValue( owner.configuration() ).asInt() * 24 * 60 * 60 * 1000;
      // Определение интервала синхронизации
      // IQueryInterval interval = new QueryInterval( EQueryIntervalType.CSCE, startTime, MAX_FORMATTABLE_TIMESTAMP );
      // Синхронизация истории алармов
      // IStridablesList<ISkAlarm> localAlarms = localAlarmService.listAlarms();
      // // Формирование списка gwid и синхронизируемых событий
      // GwidList eventIds = new GwidList();
      // for( ISkAlarm alarm : localAlarms ) {
      // eventIds.add( Gwid.createEvent( alarm.classId(), alarm.strid(), Gwid.STR_MULTI_ID ) );
      // }
      // ISkEventList localEvents = loadEvents( localApi.hqService(), eventIds, interval );
      // ISkEventList remoteEvents = loadEvents( remoteApi.hqService(), eventIds, interval );
      // // Синхронизация списка событий между серверами
      // if( localEvents.size() > 0
      // && (remoteEvents.size() == 0 || localEvents.last().timestamp() > remoteEvents.last().timestamp()) ) {
      // // События пересылаются из локального сервера в удаленный сервер
      // remoteEventService.fireEvents(
      // remoteEvents.size() > 0 ? localEvents.selectAfter( remoteEvents.last().timestamp() ) : localEvents );
      // }
      // } )
      // Шлюз завершил инициализацию
      logger.info( MSG_GW_INIT_FINISH, id() );
    }
    catch(

    Throwable e ) {
      // Ошибка установки соединения с удаленным сервером
      logger.error( e, ERR_CREATE_CONNECTION, id(), cause( e ) );
      // Требуется повторная инициализация
      remoteConnection.close();
    }
  }

  /**
   * Открывает соединение с удаленным сервером
   *
   * @param aConnection {@link ISkConnection} открываемое удаленное соединение
   * @param aProvider {@link ISkConnection} поставщик соединений с сервером
   * @param aTreadExecutor {@link ITsThreadExecutor} исполнитель потоков
   * @param aConfiguration {@link ISkGatewayInfo} конфигурация шлюза
   * @param aInitialImplementaion {@link IS5InitialImplementation} начальная, неизменяемая конфигурация локального
   *          сервера
   * @return {@link ISkConnection} соединение с сервером
   * @throws TsNullArgumentRtException любой аргумент = null
   * @throws TsIllegalArgumentRtException ошибка подключения к серверу
   */
  public static ISkConnection openRemoteConnection( ISkConnection aConnection, ISkBackendProvider aProvider,
      ITsThreadExecutor aTreadExecutor, ISkGatewayInfo aConfiguration,
      IS5InitialImplementation aInitialImplementaion ) {
    TsNullArgumentRtException.checkNulls( aConnection, aProvider, aTreadExecutor, aConfiguration );

    long connectionTimeout = aConfiguration.connectionInfo().connectTimeout();
    long failureTimeout = aConfiguration.connectionInfo().failureTimeout();
    long currdataTimeout = aConfiguration.connectionInfo().currDataTimeout();
    long histdataTimeout = aConfiguration.connectionInfo().histDataTimeout();

    IS5ConnectionInfo connectionInfo = aConfiguration.connectionInfo();
    ILoginInfo loginInfo = aConfiguration.loginInfo();
    S5Module module = IS5ServerHardConstants.OP_BACKEND_MODULE.getValue( aInitialImplementaion.params() ).asValobj();
    ITsContext ctx = new TsContext();
    ISkCoreConfigConstants.REFDEF_BACKEND_PROVIDER.setRef( ctx, aProvider );
    ISkCoreConfigConstants.REFDEF_THREAD_EXECUTOR.setRef( ctx, aTreadExecutor );

    IS5ConnectionParams.OP_USERNAME.setValue( ctx.params(), avStr( loginInfo.login() ) );
    IS5ConnectionParams.OP_PASSWORD.setValue( ctx.params(), avStr( loginInfo.password() ) );
    // Настройки имеющие значение для подключения к удаленным серверам. Игнорируются
    // для локальных соединений
    IS5ConnectionParams.OP_HOSTS.setValue( ctx.params(), avValobj( connectionInfo.hosts() ) );
    IS5ConnectionParams.REF_CLASSLOADER.setRef( ctx, S5Gateway.class.getClassLoader() );
    IS5ConnectionParams.REF_MONITOR.setRef( ctx, IS5ProgressMonitor.NULL );
    IS5ConnectionParams.OP_CLIENT_PROGRAM.setValue( ctx.params(), avStr( module.id() ) );
    IS5ConnectionParams.OP_CLIENT_VERSION.setValue( ctx.params(), avValobj( module.version() ) );
    IS5ConnectionParams.OP_CONNECT_TIMEOUT.setValue( ctx.params(), avInt( connectionTimeout ) );
    IS5ConnectionParams.OP_FAILURE_TIMEOUT.setValue( ctx.params(), avInt( failureTimeout ) );
    IS5ConnectionParams.OP_CURRDATA_TIMEOUT.setValue( ctx.params(), avInt( currdataTimeout ) );
    IS5ConnectionParams.OP_HISTDATA_TIMEOUT.setValue( ctx.params(), avInt( histdataTimeout ) );

    aConnection.open( ctx );

    return aConnection;
  }

  /**
   * Синхронизация наборов данных, слушателей подключенных соединений
   */
  private void synchronize() {
    // Синхронизация вызова
    threadExecutor.syncExec( this::synchronizeRun );
  }

  /**
   * Запуск задачи синхронизации наборов данных, слушателей подключенных соединений
   */
  private void synchronizeRun() {
    if( !readyForSynchronize ) {
      // Нет готовности к синхронизации соединений
      logger.warning( ERR_NOT_READY_FOR_SYNC, id() );
      return;
    }
    // Время начала синхронизации
    long traceStartTime = System.currentTimeMillis();
    // Дерегистрация исполнителей команд
    remoteCmdService.unregisterExecutor( this );

    // Публикация значений тикетов "маршрутов прохождения значений данных"
    remoteDataQualityService.setConnectedAndMarkValues( TICKET_ROUTE, transmittingGwids );

    IGwidList localGwids = new GwidList( transmittingGwids.keys() );
    // Создание каналов передачи данных
    createRtdChannels( localGwids );

    // Регистрация исполнителей команд
    IGwidList cmdGwids = getExecutableCmdGwids( localGwids );
    synchronizeCmdExecutors( cmdGwids );

    // Подписка на события
    eventGwids.clear();
    for( Gwid g : getConfigGwids( localGwidService, configuration.exportEvents(), localGwids ) ) {
      eventGwids.add( g );
    }

    // Сброс признака необходимости синхронизации
    needSynchronize = false;
    // Время выполнения синхронизации
    Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
    // Завершение синронизации данных между соединениями
    logger.info( MSG_SYNC_COMPLETED, id(), Boolean.FALSE, traceTime );
  }

  /**
   * Синхронизация исполнителей команд между локальным и удаленными серверами
   *
   * @param aCmdGwids {@link IGwidList} список идентификаторов команд поддерживаемые локальными исполнителями
   * @throws TsNullArgumentRtException аргумент = null
   */
  private void synchronizeCmdExecutors( IGwidList aCmdGwids ) {
    TsNullArgumentRtException.checkNull( aCmdGwids );
    // Передача списка исполнителей команд
    Integer count = Integer.valueOf( aCmdGwids.size() );
    Gwid firstCmdGwid = aCmdGwids.first();
    // Время начала запроса
    long traceStartTime = System.currentTimeMillis();
    try {
      synchronized (remoteCmdGwids) {
        if( remoteCmdGwids.equals( aCmdGwids ) ) {
          // Список исполнителей команд не изменился
          logger.warning( ERR_CMD_EXECUTORS_NOT_CHANGED, id(), Integer.valueOf( aCmdGwids.size() ) );
          return;
        }
        remoteCmdGwids.setAll( aCmdGwids );
      }
      // Изменение регистрации исполнителей команд
      remoteCmdService.registerExecutor( this, aCmdGwids );
      // Время выполнения запроса
      Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
      // Завершение передачи списка исполнителей команд
      logger.info( MSG_TRANSFER_EXECUTORS_FINISH, id(), count, firstCmdGwid, traceTime );
    }
    catch( Throwable e ) {
      // Время выполнения запроса
      Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
      // Ошибка регистрации исполнителей команд на удаленном сервере. Соединение будет
      // закрыто
      logger.error( e, ERR_REGISTER_CMD_GWIDS, id(), count, firstCmdGwid, traceTime, cause( e ) );
      // Завершение текущего соединения
      remoteConnection.close();
    }
  }

  /**
   * Возвращает карту значений меток "маршрут прохождения значений данного" по передаваемым через шлюз данным.
   *
   * @param aTransmittingServers IStringList идентификаторы удаленных серверов {@link ISkServer} в направлении которых
   *          проводится передача данных.
   * @param aDataQualityService {@link ISkDataQualityService} служба качества данных
   * @return {@link IMap}&lt;{@link Gwid},{@link IOptionSet}&gt; карта значений меток. <br>
   *         Ключ: идентификатор данного;<br>
   *         Значение: значение метки "маршрут прохождения значений данного".
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  private static IMap<Gwid, IAtomicValue> getTransmittingGwids( IStringList aTransmittingServers,
      ISkDataQualityService aDataQualityService ) {
    TsNullArgumentRtException.checkNulls( aTransmittingServers, aDataQualityService );
    IGwidList gwids = aDataQualityService.getConnectedResources();
    IMap<Gwid, IOptionSet> marksMap = aDataQualityService.getResourcesMarks( gwids );
    IMapEdit<Gwid, IAtomicValue> retValue = new ElemMap<>();
    for( Gwid gwid : gwids ) {
      IOptionSet marks = marksMap.getByKey( gwid );
      IStringList route = marks.getValobj( TICKET_ROUTE, IStringList.EMPTY );
      if( !TsCollectionsUtils.intersects( aTransmittingServers, route ) ) {
        IStringListEdit newRoute = new StringArrayList( route );
        newRoute.addAll( aTransmittingServers );
        retValue.put( gwid, avValobj( newRoute ) );
      }
    }
    return retValue;
  }

  /**
   * Настройка конфигурации каналов передаваемых данных реального времени от локального удаленному севреру и обратно
   *
   * @param aLocalQualityGwids {@link IGwidList} список идентификаторов данных локального сервера по которым
   *          предоставляется качество
   * @throws TsNullArgumentRtException аргумент = null
   */
  private void createRtdChannels( IGwidList aLocalQualityGwids ) {
    TsNullArgumentRtException.checkNull( aLocalQualityGwids );
    // Завершение работы портов
    localToRemoteCurrdataPort.close();
    for( ISkRtdataChannel channel : writeHistData.values() ) {
      channel.close();
    }
    // ЭКСПОРТ (передача с локального на удаленный севрер)
    // Идентификаторы для текущих данных
    IGwidList currdataGwids = getConfigGwids( localGwidService, configuration.exportCurrData(), aLocalQualityGwids );
    // Добавление вновь добавленных каналов
    if( currdataGwids.size() > 0 ) {
      // Регистрация передаваемых текущих данных на удаленном сервере
      logger.info( MSG_REGISTER_CURRDATA_ON_REMOTE, id(), Integer.valueOf( currdataGwids.size() ) );
      // Текущие данные (прием/передача)
      localToRemoteCurrdataPort.setDataIds( currdataGwids );
    }
    // Идентификаторы для хранимых данных
    IGwidList histdataGwids = getConfigGwids( localGwidService, configuration.exportHistData(), aLocalQualityGwids );
    // Добавление вновь добавленных каналов
    if( histdataGwids.size() > 0 ) {
      // Регистрация передаваемых хранимых данных на удаленном сервере
      logger.info( MSG_REGISTER_HISTDATA_ON_REMOTE, id(), Integer.valueOf( currdataGwids.size() ) );
      // Создание новых каналов хранимых данных.
      writeHistData.putAll( remoteRtDataService.createWriteHistDataChannels( histdataGwids ) );
    }
  }

  /**
   * Возвращает список идентификаторов команд которые могут быть выполнены службой команд
   *
   * @param aQualityGwids {@link IGwidList} список идентификаторов предоставляемых службой качества
   * @return {@link IGwidList} список идентификаторов команд
   * @throws TsNullArgumentRtException аргумент = null
   */
  private IGwidList getExecutableCmdGwids( IGwidList aQualityGwids ) {
    TsNullArgumentRtException.checkNull( aQualityGwids );
    // Получение полного списка идентификаторов исключени
    GwidList allExecutorGwids = new GwidList();
    for( Gwid gwid : owner.commandBackend().listGloballyHandledCommandGwids() ) {
      allExecutorGwids.addAll( localGwidService.expandGwid( gwid ) );
    }
    GwidList allConfigGwids = new GwidList();
    for( Gwid gwid : getConfigGwids( localGwidService, configuration.exportCmdExecutors(), aQualityGwids ) ) {
      allConfigGwids.addAll( localGwidService.expandGwid( gwid ) );
    }
    if( allConfigGwids.size() == 0 && configuration.exportCmdExecutors().excludeGwids().size() == 0 ) {
      // Если конфигурация исполнителей не настроена, то по умолчанию регистрируются все локальные исполнители
      return allExecutorGwids;
    }
    GwidList retValue = new GwidList();
    for( Gwid gwid : allExecutorGwids ) {
      if( allConfigGwids.hasElem( gwid ) ) {
        retValue.add( gwid );
      }
    }
    return retValue;
  }

  /**
   * Возвращает текстовое представление списка идентификаторов
   *
   * @param aGwids {@link IGwidList} список идентификаторов
   * @return String текстовое представление списка
   */
  private static String getGwidsString( IGwidList aGwids ) {
    StringBuilder sb = new StringBuilder();
    for( int index = 0, n = aGwids.size(); index < n; index++ ) {
      sb.append( "   " + aGwids.get( index ) + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return sb.toString();
  }

  /**
   * Загружает список указанных событий сервера
   *
   * @param aHqService {@link ISkHistoryQueryService} служба запросов сервера.
   * @param aEventIds {@link GwidList} список идентификаторов запрашиваемых событий
   * @return {@link ITimedList}&lt; {@link SkEvent}&gt; список загруженных событий
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  private static ISkEventList loadEvents( ISkHistoryQueryService aHqService, GwidList aEventIds,
      IQueryInterval aInterval ) {
    TsNullArgumentRtException.checkNulls( aHqService, aEventIds, aInterval );
    // TODO: mvkd ошибка обращения к query service у которой еше не инициализировано sharedConnection
    if( true ) {
      // return new TimedList<SkEvent>();
    }
    // Параметры запроса
    IOptionSetEdit options = new OptionSet( OptionSetUtils.createOpSet( //
    // ISkHistoryQueryServiceConstants.OP_SK_MAX_EXECUTION_TIME, AvUtils.avInt( 10000 ) //
    ) );
    ISkQueryRawHistory query = aHqService.createHistoricQuery( options );
    query.prepare( aEventIds );
    query.exec( aInterval );

    IListEdit<ITimedList<SkEvent>> queryResults = new ElemArrayList<>();
    for( Gwid gwid : query.listGwids() ) {
      ITimedList<SkEvent> cmds = query.get( gwid );
      queryResults.add( cmds );
    }
    SkEventList retValue = TimeUtils.uniteTimeporaLists( queryResults, SkEventList::new );
    return retValue;
  }
}
