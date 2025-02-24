package org.toxsoft.skf.bridge.s5.supports;

import static org.toxsoft.skf.bridge.s5.supports.IS5Resources.*;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.synch.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.*;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.uskat.core.api.rtdserv.*;

/**
 * Порт передачи текущих данных через шлюз службы {@link IBaGateway}
 *
 * @author mvk
 */
class S5GatewayCurrDataPort
    extends Stridable
    implements ISkCurrDataChangeListener, ICloseable {

  /**
   * Карта каналов чтения текущих данных
   * <p>
   * Ключ: {@link Gwid}-идентификатор данного;<br>
   * Значение: канал чтения.
   */
  private final IMapEdit<Gwid, ISkReadCurrDataChannel> readCurrData = new SynchronizedMap<>( new ElemMap<>() );

  /**
   * Карта каналов записи текущих данных
   * <p>
   * Ключ: {@link Gwid}-идентификатор данного;<br>
   * Значение: канал записи.
   */
  private final IMapEdit<Gwid, ISkWriteCurrDataChannel> writeCurrData = new SynchronizedMap<>( new ElemMap<>() );

  /**
   * Служба чтения данных реального времени
   */
  private final ISkRtdataService readRtDataService;

  /**
   * Служба записи данных реального времени
   */
  private final ISkRtdataService writeRtDataService;

  /**
   * Журнал работы
   */
  private final ILogger logger;

  /**
   * Конструктор шлюза
   *
   * @param aId {@link String} идентификатор слушателя
   * @param aReadRtService {@link ISkRtdataService} служба чтения текущих данных
   * @param aWriteRtService {@link ISkRtdataService} служба записи текущих данных
   * @param aLogger {@link ILogger} журнал работы
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  S5GatewayCurrDataPort( String aId, ISkRtdataService aReadRtService, ISkRtdataService aWriteRtService,
      ILogger aLogger ) {
    // true: разрешение ИД-пути
    super( aId );
    readRtDataService = TsNullArgumentRtException.checkNull( aReadRtService );
    writeRtDataService = TsNullArgumentRtException.checkNull( aWriteRtService );
    logger = TsNullArgumentRtException.checkNull( aLogger );
  }

  // ------------------------------------------------------------------------------------
  // API пакета
  //

  /**
   * Возвращает список данных передаваемых шлюзом
   *
   * @return {@link IGwidList} список данных передаваемых шлюзом
   */
  IGwidList dataIds() {
    return new GwidList( writeCurrData.keys() );
  }

  /**
   * Устанавливает список передаваемых текущих данных
   *
   * @param aGwids {@link IGwidList} список идентификаторов
   * @throws TsNullArgumentRtException аргумент = null
   */
  void setDataIds( IGwidList aGwids ) {
    TsNullArgumentRtException.checkNull( aGwids );
    // 2020-11-31 mvk
    // Удаление возможных дублей в запросе
    GwidList gwids = new GwidList();
    for( Gwid gwid : aGwids ) {
      if( gwids.hasElem( gwid ) ) {
        logger.warning( ERR_CURRDATA_DOUBLE, id(), gwid );
        continue;
      }
      gwids.add( gwid );
    }
    closeImpl( false );
    if( gwids.size() == 0 ) {
      return;
    }
    writeCurrData.setAll( writeRtDataService.createWriteCurrDataChannels( gwids ) );
    readRtDataService.eventer().addListener( this );
    readCurrData.setAll( readRtDataService.createReadCurrDataChannels( gwids ) );
  }

  // ------------------------------------------------------------------------------------
  // ISkCurrDataChangeListener
  //
  @Override
  public void onCurrData( IMap<Gwid, IAtomicValue> aNewValues ) {
    // Время начала запроса
    long traceStartTime = System.currentTimeMillis();
    Integer count = Integer.valueOf( aNewValues.size() );
    Gwid first = aNewValues.keys().first();
    // Передача текущих данных
    logger.debug( MSG_GW_CURRDATA_TRANSFER, id(), count, first );
    for( Gwid gwid : aNewValues.keys() ) {
      IAtomicValue value = aNewValues.getByKey( gwid );
      ISkWriteCurrDataChannel writeChannel = writeCurrData.findByKey( gwid );
      if( writeChannel == null ) {
        // Не найден канал записи хранимых данных
        logger.error( ERR_CURRDATA_WRITE_CHANNEL_NOT_FOUND, id(), gwid );
        continue;
      }
      writeChannel.setValue( value );
    }
    // Время выполнения
    Long traceTime = Long.valueOf( System.currentTimeMillis() - traceStartTime );
    // Завершение передачи текущих данных
    logger.debug( MSG_GW_CURRDATA_TRANSFER_FINISH, id(), count, first, traceTime );
  }

  // ------------------------------------------------------------------------------------
  // ICloseable
  //
  @Override
  public void close() {
    // verbose = true
    closeImpl( true );
  }

  // ------------------------------------------------------------------------------------
  // Внутренние методы
  //
  /**
   * Реализация завершения работы порта
   *
   * @param aVerbose boolean <b>true</b> выводить в лог сообщение о завершении;<b>false</b> не выводить в лог сообщение
   */
  private void closeImpl( boolean aVerbose ) {
    IMap<Gwid, ISkReadCurrDataChannel> rd = readCurrData.copyTo( new ElemMap<>() );
    IMap<Gwid, ISkWriteCurrDataChannel> wd = writeCurrData.copyTo( new ElemMap<>() );

    if( aVerbose ) {
      Integer rcd = Integer.valueOf( rd.values().size() );
      Integer wcd = Integer.valueOf( wd.values().size() );
      // TODO: histdata
      Integer rhd = Integer.valueOf( 0 );
      Integer whd = Integer.valueOf( 0 );
      // Завершение работы каналов передачи данных
      logger.info( MSG_DATA_CHANNELS_CLOSE, id(), rcd, wcd, rhd, whd );
    }
    // Дерегистрация слушателя данных
    readRtDataService.eventer().removeListener( this );
    // Завершение работы каналов
    for( ISkReadCurrDataChannel channel : rd.values() ) {
      channel.close();
    }
    readCurrData.clear();
    for( ISkWriteCurrDataChannel channel : wd.values() ) {
      channel.close();
    }
    writeCurrData.clear();
  }
}
