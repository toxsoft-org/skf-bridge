package org.toxsoft.skf.bridge.s5.supports;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.bridge.s5.lib.impl.SkGatewayGwidConfigs.*;
import static org.toxsoft.skf.bridge.s5.supports.IS5Resources.*;
import static org.toxsoft.uskat.s5.common.IS5CommonResources.*;

import org.toxsoft.core.log4j.LoggerWrapper;
import org.toxsoft.core.tslib.av.IAtomicValue;
import org.toxsoft.core.tslib.av.temporal.ITemporalAtomicValue;
import org.toxsoft.core.tslib.bricks.ctx.ITsContext;
import org.toxsoft.core.tslib.bricks.ctx.impl.TsContext;
import org.toxsoft.core.tslib.bricks.events.change.IGenericChangeListener;
import org.toxsoft.core.tslib.bricks.strid.impl.Stridable;
import org.toxsoft.core.tslib.bricks.time.ITimeInterval;
import org.toxsoft.core.tslib.bricks.time.ITimedList;
import org.toxsoft.core.tslib.coll.IMap;
import org.toxsoft.core.tslib.coll.IMapEdit;
import org.toxsoft.core.tslib.coll.impl.ElemMap;
import org.toxsoft.core.tslib.coll.synch.SynchronizedMap;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.Skid;
import org.toxsoft.core.tslib.utils.Pair;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.login.ILoginInfo;
import org.toxsoft.core.tslib.utils.logs.ELogSeverity;
import org.toxsoft.core.tslib.utils.logs.ILogger;
import org.toxsoft.skf.bridge.s5.lib.IBaGateway;
import org.toxsoft.skf.bridge.s5.lib.ISkGatewayConfiguration;
import org.toxsoft.skf.dq.lib.ISkDataQualityChangeListener;
import org.toxsoft.skf.dq.lib.ISkDataQualityService;
import org.toxsoft.skf.dq.lib.impl.SkDataQualityService;
import org.toxsoft.uskat.core.ISkCoreApi;
import org.toxsoft.uskat.core.api.cmdserv.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.api.gwids.ISkGwidService;
import org.toxsoft.uskat.core.api.rtdserv.*;
import org.toxsoft.uskat.core.backend.ISkBackendProvider;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.impl.*;
import org.toxsoft.uskat.s5.client.IS5ConnectionParams;
import org.toxsoft.uskat.s5.client.remote.connection.IS5ConnectionInfo;
import org.toxsoft.uskat.s5.common.S5Module;
import org.toxsoft.uskat.s5.server.IS5ServerHardConstants;
import org.toxsoft.uskat.s5.server.backend.supports.currdata.IS5BackendCurrDataSingleton;
import org.toxsoft.uskat.s5.server.backend.supports.currdata.impl.IS5CurrDataInterceptor;
import org.toxsoft.uskat.s5.server.backend.supports.histdata.IS5HistDataInterceptor;
import org.toxsoft.uskat.s5.server.frontend.IS5FrontendRear;
import org.toxsoft.uskat.s5.server.sessions.IS5SessionInterceptor;
import org.toxsoft.uskat.s5.server.startup.IS5InitialImplementation;
import org.toxsoft.uskat.s5.utils.jobs.IS5ServerJob;
import org.toxsoft.uskat.s5.utils.progress.IS5ProgressMonitor;

import core.tslib.bricks.threadexecutor.ITsThreadExecutor;

/**
 * Шлюз службы {@link IBaGateway}
 *
 * @author mvk
 */
class S5Gateway
    extends Stridable
    implements IS5Gateway, IS5ServerJob, //
    IS5SessionInterceptor, //
    IS5CurrDataInterceptor, //
    IS5HistDataInterceptor, //
    ISkCommandExecutor, ISkEventHandler, ISkDataQualityChangeListener, ISkConnectionListener {

  /**
   * Служба шлюзов
   */
  private S5BackendGatewaySingleton owner;

  /**
   * Конфигурация шлюза
   */
  private final ISkGatewayConfiguration configuration;

  /**
   * Соединение с локальным сервером
   */
  private final ISkConnection localConnection;

  /**
   * Соединение с удаленным сервером
   */
  private final ISkConnection remoteConnection;

  /**
   * Исполнитель потоков uskat-соединений (один на весь шлюз)
   */
  private final ITsThreadExecutor threadExecutor;

  /**
   * Поставщик удаленного backend
   */
  private final ISkBackendProvider remoteBackendProvider;

  /**
   * Служба данных реального времени локального сервера
   */
  private ISkRtdataService localRtDataService;

  /**
   * Служба данных реального времени удаленного сервера
   */
  private ISkRtdataService remoteRtDataService;

  /**
   * Порт передачи текущих данных от локального сервера на удаленный
   */
  private S5GatewayCurrDataPort localToRemoteCurrdataPort;

  /**
   * Порт передачи текущих данных от удаленного сервера на локальный
   */
  private S5GatewayCurrDataPort remoteToLocalCurrdataPort;

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
   * Служба событий (чтение)
   */
  private ISkEventService localEventService;

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
   * Признак готовности для синхронизации данных соединений
   */
  private boolean readyForSynchronize;

  /**
   * Признак того, что шлюз завершил своб работу
   */
  private boolean completed;

  /**
   * Признак требование провести синхронизацию с удаленным сервером
   */
  private volatile boolean needSynchronize;

  /**
   * Признак того, что мост приостановил свою работу.
   * <p>
   * В режиме остановки моста, данные через него не пересылаются
   */
  private boolean paused;

  /**
   * Журнал работы
   */
  private ILogger logger = LoggerWrapper.getLogger( getClass() );

  /**
   * Конструктор шлюза
   *
   * @param aOwner {@link S5BackendGatewaySingleton} синглетон службы
   * @param aConfiguration {@link ISkGatewayConfiguration} конфигурация шлюза
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  S5Gateway( S5BackendGatewaySingleton aOwner, ISkGatewayConfiguration aConfiguration ) {
    super( aConfiguration.id(), aConfiguration.nmName(), aConfiguration.description() );
    owner = aOwner;
    configuration = aConfiguration;
    paused = aConfiguration.isPaused();
    logger.info( MSG_GW_STARTED, this, Boolean.valueOf( paused ) );
    // Перехват событий бекенда (интерсепция)
    owner.sessionManager().addSessionInterceptor( this, 0 );
    // owner.objectsBackend().addObjectsInterceptor( new
    // S5GatewayObjectsInterceptor( this ), 0 );
    owner.currdataBackend().addCurrDataInterceptor( this, 0 );
    owner.histdataBackend().addHistDataInterceptor( this, 0 );
    // owner.lobsBackend().addLobsInterceptor( new S5GatewayLobsInterceptor( this ),
    // 0 );

    // Регистрация расширений API соединения
    SkCoreUtils.registerSkServiceCreator( SkDataQualityService.CREATOR );

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
  }

  // ------------------------------------------------------------------------------------
  // IS5Gateway
  //
  @Override
  public ISkGatewayConfiguration configuration() {
    return configuration;
  }

  @Override
  public boolean isPaused() {
    return paused;
  }

  @Override
  public void setPaused( boolean aPause ) {
    if( paused != aPause ) {
      // Запрос клиента приостановить/возобновить передачу данных через шлюз
      logger.info( (aPause ? MSG_GW_PAUSE_QUERY : MSG_GW_START_QUERY), this );
      // Синхронизация наборов данных, слушателей подключенных соединений
      synchronize( aPause );
    }
    paused = aPause;
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

  @Override
  public void doJob() {
    if( remoteConnection.state() == ESkConnState.CLOSED && !completed ) {
      // Повторная попытка открыть соединение с удаленном сервером
      tryOpenRemote();
    }
    if( remoteConnection.state() != ESkConnState.ACTIVE ) {
      // Нет связи с удаленным сервером
      logger.error( ERR_DOJOB_NOT_CONNECTION, remoteConnection );
    }
    if( remoteConnection.state() == ESkConnState.ACTIVE && needSynchronize ) {
      // Попытка запуска отложенной синхронизации из doJob
      logger.warning( ERR_TRY_SYNCH_FROM_DOJOB );
      // Синхронизация данных с удаленным сервером
      synchronize( paused );
    }
  }

  @Override
  public boolean completed() {
    return completed;
  }

  @Override
  public void close() {
    // Шлюз завершает работу
    logger.info( MSG_GW_CLOSE, this );
    // paused = true
    synchronize( true );
    // Завершение работы наборов данных
    if( localToRemoteCurrdataPort != null ) {
      localToRemoteCurrdataPort.close();
      localToRemoteCurrdataPort = null;
    }
    if( remoteToLocalCurrdataPort != null ) {
      remoteToLocalCurrdataPort.close();
      remoteToLocalCurrdataPort = null;
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
    owner.currdataBackend().removeCurrDataInterceptor( this );
    owner.histdataBackend().removeHistDataInterceptor( this );
    // Шлюз выгружен
    completed = true;
    logger.info( MSG_GW_CLOSED, this );
  }

  // ------------------------------------------------------------------------------------
  // IS5SessionInterceptor
  //
  @Override
  public void beforeCreateSession( Skid aSessionID ) {
    // nop
  }

  @Override
  public void afterCreateSession( Skid aSessionID ) {
    // nop
  }

  @Override
  public void beforeCloseSession( Skid aSessionID ) {
    // nop
  }

  @SuppressWarnings( "nls" )
  @Override
  public void afterCloseSession( Skid aSessionID ) {
    // Синхронизация вызова
    threadExecutor.asyncExec( () -> {
      // Данные игнорируемые для чтения. null: настройка не требуется
      IGwidList ignored = null;
      // От локального сервера отключился один клиентов. Требуется перенастроить порт
      // приема текущих данных от сервера,
      // так как совокупный список получаемых данных может быть изменен (нет больше
      // необходимости в некоторых данных)
      logger.debug( "afterCloseSession(...). remoteToLocalCurrdataPort = %s", remoteToLocalCurrdataPort );
      logger.debug( "afterCloseSession(...). get connLock (write). remoteToLocalCurrdataPort = %s",
          remoteToLocalCurrdataPort );
      // Конфигурация порта передачи текущих данных от удаленного сервера на локальный
      if( remoteToLocalCurrdataPort != null ) {
        // Настройка порта текущих данных для импорта значений
        ignored = (localDataQualityService != null ? localToRemoteCurrdataPort.dataIds() : IGwidList.EMPTY);
      }
      logger.debug( "afterCloseSession(...): is completed" );
      // 2021-01-19 mvk настройка импорта делается вне блокировки
      if( ignored != null ) {
        configureCurrdataPortForImport( owner.currdataBackend(), remoteToLocalCurrdataPort, ignored, logger );
      }
    } );
  }

  // ------------------------------------------------------------------------------------
  // IS5CurrDataInterceptor
  //
  @Override
  public boolean beforeReconfigureCurrData( IGwidList aRemovedGwids, IMap<Gwid, IAtomicValue> aAddedGwids ) {
    // nop
    return true;
  }

  @Override
  public void afterReconfigureCurrData( IGwidList aRemovedGwids, IMap<Gwid, IAtomicValue> aAddedGwids ) {
    // nop
  }

  @Override
  public boolean beforeConfigureCurrDataReader( IS5FrontendRear aFrontend, IGwidList aToRemove, IGwidList aToAdd ) {
    return true;
  }

  @Override
  public void afterConfigureCurrDataReader( IS5FrontendRear aFrontend, IGwidList aToRemove, IGwidList aToAdd ) {
    // Синхронизация вызова (вызов должен быть асинхронным, иначе возможны проблемы при запуске сервера)
    threadExecutor.asyncExec( () -> {
      // Данные игнорируемые для чтения. null: настройка не требуется
      IGwidList ignored = null;
      // Конфигурация порта передачи текущих данных от удаленного сервера на локальный
      if( remoteToLocalCurrdataPort != null ) {
        // Настройка порта текущих данных для импорта значений
        ignored = (localDataQualityService != null ? localToRemoteCurrdataPort.dataIds() : IGwidList.EMPTY);
      }
      // 2021-01-19 mvk настройка импорта делается вне блокировки
      if( ignored != null ) {
        configureCurrdataPortForImport( owner.currdataBackend(), remoteToLocalCurrdataPort, ignored, logger );
      }
    } );
  }

  @Override
  public boolean beforeConfigureCurrDataWriter( IS5FrontendRear aFrontend, IGwidList aToRemove, IGwidList aToAdd ) {
    // nop
    return true;
  }

  @Override
  public void afterConfigureCurrDataWriter( IS5FrontendRear aFrontend, IGwidList aToRemove, IGwidList aToAdd ) {
    // nop
  }

  @Override
  public boolean beforeWriteCurrData( IMap<Gwid, IAtomicValue> aValues ) {
    // Требование продолжить запись текущих данных в систему
    return true;
  }

  @Override
  public void afterWriteCurrData( IMap<Gwid, IAtomicValue> aValues ) {
    // nop
  }

  // ------------------------------------------------------------------------------------
  // IS5HistDataInterceptor
  //
  @Override
  public boolean beforeWriteHistData( IMap<Gwid, Pair<ITimeInterval, ITimedList<ITemporalAtomicValue>>> aValues ) {
    if( paused ) {
      // Передача данных через шлюз временно приостановлена
      logger.info( MSG_GW_PAUSED, this );
      // Разрешить дальшейшее выполнение операции
      return true;
    }
    Integer count = Integer.valueOf( aValues.size() );
    Gwid first = aValues.keys().first();
    // Передача текущих данных
    logger.debug( MSG_GW_HISTDATA_TRANSFER, count, first );
    // Передача данных синхронизируется через исполнитель потоков соединения
    threadExecutor.asyncExec( () -> {
      try {
        for( Gwid gwid : aValues.keys() ) {
          ISkWriteHistDataChannel writeChannel = writeHistData.findByKey( gwid );
          if( writeChannel == null ) {
            // Не найден канал записи хранимых данных
            logger.warning( ERR_HISTDATA_WRITE_CHANNEL_NOT_FOUND, gwid );
            continue;
          }
          Pair<ITimeInterval, ITimedList<ITemporalAtomicValue>> sequence = aValues.getByKey( gwid );
          ITimeInterval interval = sequence.left();
          ITimedList<ITemporalAtomicValue> values = sequence.right();
          writeChannel.writeValues( interval, values );
        }
        // Завершение передачи хранимых данных
        logger.debug( MSG_GW_HISTDATA_TRANSFER_FINISH, count, first );
      }
      catch( Throwable e ) {
        // Ошибка передачи хранимых данных. Запланирована синхронизация
        logger.error( e, ERR_SEND_HISTDATA, cause( e ) );
        needSynchronize = true;
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
  // ISkCommandExecutor
  //
  @Override
  public void executeCommand( IDtoCommand aCmd ) {
    // Передача команды исполнителю
    logger.info( MSG_GW_COMMAND_TRANSFER, aCmd );
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
        logger.info( MSG_GW_COMMAND_TRANSFER_FINISH, aCmd, traceTime );
      }
      catch( Throwable e ) {
        // Время выполнения
        Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
        // Ошибка передачи команды. Запланирована синхронизация
        logger.error( e, ERR_SEND_CMD, traceTime, cause( e ) );
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
        logger.info( MSG_GW_COMMAND_STATE_TRANSFER, aCommand );
        // Признак завершения выполнения команды
        boolean complete = aCommand.state().state().isComplete();
        // Команда полученная от удаленного сервера
        Gwid gwid = aCommand.cmdGwid();
        IDtoCommand cmd = (complete ? executingCommands.removeByKey( gwid ) : executingCommands.findByKey( gwid ));
        if( cmd == null ) {
          // Команда не отправлялась шлюзом (отправил другой клиент?)
          logger.warning( MSG_GW_ALIEN_COMMAND, this );
          return;
        }
        // Время начала запроса
        long traceStartTime = System.currentTimeMillis();
        try {
          remoteCmdService.changeCommandState( new DtoCommandStateChangeInfo( cmd.instanceId(), aCommand.state() ) );
          // Время выполнения
          Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
          // Завершение передачи состояния команды отправителю
          logger.info( mSG_GW_COMMAND_STATE_TRANSFER_FINISH, aCommand, traceTime );
        }
        catch( Throwable e ) {
          // Время выполнения
          Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
          // Ошибка передачи команды. Запланирована синхронизация
          logger.error( e, ERR_SEND_CMD_STATE, traceTime, cause( e ) );
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
      logger.info( MSG_TRANSFER_EXECUTORS_START, count, firstCmdGwid );
      // Данные по которым предоставляется качество
      IGwidList qualityGwids = owner.dataQualityBackend().getConnectedResources( Skid.NONE );
      // Список команд которые могут быть выполнены службой команд
      IGwidList cmdGwids = getExecutableCmdGwids( qualityGwids );
      // Вывод в журнал в отладке
      if( logger.isSeverityOn( ELogSeverity.DEBUG ) ) {
        // Текстовое представление идентификаторов для журнала
        String cmdGwidsString = getGwidsString( cmdGwids );
        // Изменение списка исполнителей команд
        logger.debug( MSG_CHANGE_CMD_EXECUTORS, Boolean.valueOf( paused ), cmdGwidsString );
      }
      // Исполнители команд синхронизируются через исполнитель потоков соединения
      threadExecutor.asyncExec( () -> {
        // Сихнонизация исполнителей команды
        synchronizeCmdExecutors( cmdGwids );
      } );
    }
  }

  // ------------------------------------------------------------------------------------
  // ISkEventHandler
  //
  @Override
  public void onEvents( ISkEventList aEvents ) {
    if( paused ) {
      // Передача данных через шлюз временно приостановлена
      logger.info( MSG_GW_PAUSED, this );
      return;
    }
    // Передача событий синхронизируется через исполнитель потоков соединения
    threadExecutor.asyncExec( () -> {
      Integer count = Integer.valueOf( aEvents.size() );
      SkEvent first = aEvents.first();
      // Передача событий
      logger.info( MSG_GW_EVENT_TRANSFER, count, first );
      for( SkEvent event : aEvents ) {
        remoteEventService.fireEvent( event );
      }
      // Завершение передачи событий
      logger.info( MSG_GW_EVENT_TRANSFER_FINISH, count, first );
    } );
  }

  // ------------------------------------------------------------------------------------
  // ISkDataQualityChangeListener
  //
  @Override
  public void onResourcesStateChanged( ISkDataQualityService aSource, String aTicketId ) {
    // Сообщение об изменении списка отслеживаемых ресурсов
    logger.info( MSG_SYNCHONIZE_BY_DATAQUALITY_RESOURCES );
    try {
      try {
        // Выставление признака необходимости синхронизации
        needSynchronize = true;
        // Попытка синхронизации данных
        synchronize( paused );
      }
      catch( Throwable e ) {
        // Ошибка синхронизации с удаленным сервером. Соединение будет закрыто
        logger.error( e, ERR_SYNCHONIZE, this, cause( e ) );
        // 2021-01-24 mvk
        // Завершение текущего соединения
        // remoteConnection.close();
      }
    }
    finally {
      // Завершение синхронизации по отслеживаемым ресурсам ISkDataQualityService
      logger.info( MSG_SYNCHONIZE_BY_DATAQUALITY_RESOURCES_FINISH );
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
        logger.info( MSG_GW_REMOTE_CONNECTED, this );
        // Выставление признака необходимости синхронизации
        needSynchronize = true;
        // Попытка синхронизации данных соединений
        synchronize( paused );
        break;
      case INACTIVE:
      case CLOSED:
        // Закрыто соединение с удаленным сервером
        logger.info( MSG_GW_REMOTE_DISCONNECTED, this );
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
      logger.warning( ERR_GATEWAY_ALREADY_INITED, this );
      return;
    }
    try {
      // Установка слушателя соединения
      remoteConnection.addConnectionListener( this );
      // Попытка открыть удаленное соединение
      openRemoteConnection( remoteConnection, remoteBackendProvider, threadExecutor, configuration,
          owner.initialImplement() );
      // Локальное API
      ISkCoreApi localApi = localConnection.coreApi();
      // Удаленное API
      ISkCoreApi remoteApi = remoteConnection.coreApi();
      // Начало инициализации шлюза
      logger.info( MSG_GW_INIT_START, this );
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
      localEventService = localApi.eventService();
      remoteEventService = remoteApi.eventService();
      // Служба качества данных: чтение/запись
      localDataQualityService = localApi.getService( ISkDataQualityService.SERVICE_ID );
      remoteDataQualityService = remoteApi.getService( ISkDataQualityService.SERVICE_ID );
      // Создание портов передачи текущих данных
      localToRemoteCurrdataPort = new S5GatewayCurrDataPort( "localToRemote", localRtDataService, //$NON-NLS-1$
          remoteRtDataService, logger );
      remoteToLocalCurrdataPort = new S5GatewayCurrDataPort( "remoteToLocal", remoteRtDataService, //$NON-NLS-1$
          localRtDataService, logger );

      // Регистрация слушателей служб
      threadExecutor.syncExec( () -> {
        localCmdService.globallyHandledGwidsEventer().addListener( new InternalGloballyHandledGwidsListener() );
        localDataQualityService.eventer().addListener( this );
        // Признак готовности для синхронизации
        readyForSynchronize = true;
        // Выставление признака необходимости синхронизации
        needSynchronize = true;
        // Попытка синхронизации наборов данных, слушателей подключенных соединений
        synchronize( paused );
      } );
      threadExecutor.syncExec( () -> {
        // Данные игнорируемые для чтения. null: настройка не требуется
        IGwidList ignored = (localDataQualityService != null ? localToRemoteCurrdataPort.dataIds() : IGwidList.EMPTY);
        // Настройка импорта
        if( ignored != null ) {
          configureCurrdataPortForImport( owner.currdataBackend(), remoteToLocalCurrdataPort, ignored, logger );
        }
      } );
      // Шлюз завершил инициализацию
      logger.info( MSG_GW_INIT_FINISH, this );
    }
    catch( Throwable e ) {
      // Ошибка инициализации
      logger.error( e );
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
   * @param aConfiguration {@link ISkGatewayConfiguration} конфигурация шлюза
   * @param aInitialImplementaion {@link IS5InitialImplementation} начальная, неизменяемая конфигурация локального
   *          сервера
   * @return {@link ISkConnection} соединение с сервером
   * @throws TsNullArgumentRtException любой аргумент = null
   * @throws TsIllegalArgumentRtException ошибка подключения к серверу
   */
  public static ISkConnection openRemoteConnection( ISkConnection aConnection, ISkBackendProvider aProvider,
      ITsThreadExecutor aTreadExecutor, ISkGatewayConfiguration aConfiguration,
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
   *
   * @param aPaused boolean <b>true</b> мост приостановил работу, но возможно установлена связь с удаленным
   *          сервером;<b>false</b> мост работает в штатном режиме, но возможна потеря связи с удаленным сервером
   */
  private void synchronize( boolean aPaused ) {
    // Синхронизация вызова
    threadExecutor.syncExec( () -> synchronizeRun( aPaused ) );
  }

  /**
   * Запуск задачи синхронизации наборов данных, слушателей подключенных соединений
   *
   * @param aPaused boolean <b>true</b> мост приостановил работу, но возможно установлена связь с удаленным
   *          сервером;<b>false</b> мост работает в штатном режиме, но возможна потеря связи с удаленным сервером
   */
  private void synchronizeRun( boolean aPaused ) {
    if( !readyForSynchronize ) {
      // Нет готовности к синхронизации соединений
      logger.warning( ERR_NOT_READY_FOR_SYNC, this );
      return;
    }
    // Время начала синхронизации
    long traceStartTime = System.currentTimeMillis();
    // Дерегистрация исполнителей команд
    remoteCmdService.unregisterExecutor( this );
    // Завершение работы предыдущих каналов
    closeRtdataChannels();
    // Отписываемся от всех событий
    localEventService.unregisterHandler( this );
    // Синхронизация данных в зависимости от режима
    if( !aPaused ) {
      // Синхронизация в "штатном режиме"
      // Данные по которым предоставляется качество
      IGwidList qualityGwids = owner.dataQualityBackend().getConnectedResources( Skid.NONE );
      // Количество данных
      Integer dataCount = Integer.valueOf( qualityGwids.size() );
      // Регистрация данных в удаленном сервере
      logger.info( MSG_REGISTER_DATA_ON_REMOTE, dataCount );
      // Передача списка целевой службе качества данных
      remoteDataQualityService.setConnectedResources( qualityGwids );
      // Завершение регистрации в удаленной службе качества данных
      logger.info( MSG_REGISTER_QUALITY_COMPLETED, dataCount );
      // Создание каналов передачи данных
      createRtdataChannels( qualityGwids );
      // Завершение регистрации каналов передачи данных
      logger.info( MSG_REGISTER_CHANNELS_COMPLETED, dataCount );
      // Регистрация исполнителей команд
      IGwidList cmdGwids = getExecutableCmdGwids( qualityGwids );
      synchronizeCmdExecutors( cmdGwids );
      // Подписка на события
      IGwidList eventGwids = getConfigGwids( localGwidService, configuration.exportEvents(), qualityGwids );
      // Запись в протокол
      logger.info( MSG_GW_GWIDS_LIST, this, //
          Integer.valueOf( localToRemoteCurrdataPort.dataIds().size() ), //
          Integer.valueOf( writeHistData.size() ), //
          Integer.valueOf( cmdGwids.size() ), //
          Integer.valueOf( eventGwids.size() ) );
      localEventService.registerHandler( eventGwids, this );
      // Сброс признака необходимости синхронизации
      needSynchronize = false;
      // Время выполнения синхронизации
      Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
      // Завершение синронизации данных между соединениями
      logger.info( MSG_SYNC_COMPLETED, Boolean.FALSE, traceTime );
      return;
    }
    if( remoteConnection.state() == ESkConnState.ACTIVE ) {
      remoteDataQualityService.setConnectedResources( IGwidList.EMPTY );
    }
    // Сброс признака необходимости синхронизации
    needSynchronize = false;
    // Время выполнения синхронизации
    Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
    // Завершение синхронизации данных между соединениями
    logger.info( MSG_SYNC_COMPLETED, Boolean.TRUE, traceTime );
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
          logger.warning( ERR_CMD_EXECUTORS_NOT_CHANGED, Integer.valueOf( aCmdGwids.size() ) );
          return;
        }
        remoteCmdGwids.setAll( aCmdGwids );
      }
      // Изменение регистрации исполнителей команд
      remoteCmdService.registerExecutor( this, aCmdGwids );
      // Время выполнения запроса
      Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
      // Завершение передачи списка исполнителей команд
      logger.info( MSG_TRANSFER_EXECUTORS_FINISH, count, firstCmdGwid, traceTime );
    }
    catch( Throwable e ) {
      // Время выполнения запроса
      Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
      // Ошибка регистрации исполнителей команд на удаленном сервере. Соединение будет
      // закрыто
      logger.error( e, ERR_REGISTER_CMD_GWIDS, this, count, firstCmdGwid, traceTime, cause( e ) );
      // Завершение текущего соединения
      remoteConnection.close();
    }
  }

  /**
   * Настройка конфигурации передаваемых данных
   *
   * @param aQualityGwids {@link IGwidList} список идентификаторов данных по которым предоставляется качество
   * @throws TsNullArgumentRtException аргумент = null
   */
  private void createRtdataChannels( IGwidList aQualityGwids ) {
    TsNullArgumentRtException.checkNull( aQualityGwids );
    // Завершение текущих каналов
    closeRtdataChannels();
    // Идентификаторы для текущих данных
    IGwidList currdataGwids = getConfigGwids( localGwidService, configuration.exportCurrData(), aQualityGwids );
    // Добавление вновь добавленных каналов
    if( currdataGwids.size() > 0 ) {
      // Текущие данные (прием/передача)
      localToRemoteCurrdataPort.setDataIds( currdataGwids );
    }
    // Идентификаторы для хранимых данных
    IGwidList histdataGwids = getConfigGwids( localGwidService, configuration.exportHistData(), aQualityGwids );
    // Добавление вновь добавленных каналов
    if( histdataGwids.size() > 0 ) {
      // Создание новых каналов хранимых данных.
      writeHistData.putAll( remoteRtDataService.createWriteHistDataChannels( histdataGwids ) );
    }
  }

  /**
   * Завершает работу каналов чтения записи
   */
  private void closeRtdataChannels() {
    // Завершение передачи порта
    if( localToRemoteCurrdataPort != null ) {
      localToRemoteCurrdataPort.close();
    }
    for( ISkRtdataChannel channel : writeHistData.values() ) {
      channel.close();
    }
    writeHistData.clear();
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
   * Настроить порт передачи текущих данных для импорта значений с удаленного сервера
   *
   * @param aCurrdataBackend {@link IS5BackendCurrDataSingleton} поддержка службы текущих данных
   * @param aCurrDataPort {@link S5GatewayCurrDataPort} порт передачи текущих данных
   * @param aIgnoredDataIds {@link IGwidList} список данных которые не должны быть импортированы
   * @param aLogger {@link ILogger} журнал работы
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  private static void configureCurrdataPortForImport( IS5BackendCurrDataSingleton aCurrdataBackend,
      S5GatewayCurrDataPort aCurrDataPort, IGwidList aIgnoredDataIds, ILogger aLogger ) {
    TsNullArgumentRtException.checkNulls( aCurrdataBackend, aCurrDataPort, aIgnoredDataIds, aLogger );

    // Завершаем старый список
    aCurrDataPort.setDataIds( IGwidList.EMPTY );
    // Фильтрация данных значения которых не формируются другими (не gateway)
    // писателями
    IGwidList readDataGwids = aCurrdataBackend.readRtdGwids();
    GwidList gwids = new GwidList();
    for( Gwid gwid : readDataGwids ) {
      if( !aIgnoredDataIds.hasElem( gwid ) ) {
        aLogger.debug( "configureCurrdataPortForImport(...): import data gwid = %s", gwid ); //$NON-NLS-1$
        gwids.add( gwid );
      }
    }
    Integer rdg = Integer.valueOf( readDataGwids.size() );
    Integer idg = Integer.valueOf( aIgnoredDataIds.size() );
    Integer g = Integer.valueOf( gwids.size() );
    aLogger.debug( "configureCurrdataPortForImport(...): readed = %d, ignored = %d, imported = %d", rdg, idg, g ); //$NON-NLS-1$
    aCurrDataPort.setDataIds( gwids );
  }
}
