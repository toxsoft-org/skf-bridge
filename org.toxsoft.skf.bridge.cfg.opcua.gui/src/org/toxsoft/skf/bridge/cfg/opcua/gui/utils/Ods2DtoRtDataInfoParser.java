package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.*;

import java.io.*;

import org.jopendocument.dom.spreadsheet.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Утилитный класс считывает описание битовых данных из ods файла и создает {@link BitIdx2DtoRtData }
 *
 * @author dima
 */
public class Ods2DtoRtDataInfoParser {

  /**
   * Параметр события: включен.
   * <p>
   * Параметр имеет тип {@link EAtomicType#BOOLEAN}.
   */
  static String EVPID_ON = "on"; //$NON-NLS-1$

  /**
   * карта id класса - > его {@link BitIdx2DtoRtData}
   */
  static private final StringMap<StringMap<IList<BitIdx2DtoRtData>>> dtoRtdataInfoesMap = new StringMap<>();

  /**
   * карта id класса - > его {@link BitIdx2DtoEvent}
   */
  static private final StringMap<StringMap<IList<BitIdx2DtoEvent>>> dtoEventInfoesMap = new StringMap<>();

  /**
   * карта id класса - > его {@link BitIdx2RriDtoAttr}
   */
  static private final StringMap<StringMap<IList<BitIdx2RriDtoAttr>>> dtoRriAttrInfoesMap = new StringMap<>();

  static private final String RTD_PREFIX = "rtd"; //$NON-NLS-1$
  private static final String EVT_PREFIX = "evt"; //$NON-NLS-1$
  static private final String RRI_PREFIX = "rri"; //$NON-NLS-1$

  /**
   * Колонка описания материнского rtDataId
   */
  private static final String BIT_ARRAY_DATAID_COL = "A"; //$NON-NLS-1$ ;

  /**
   * Колонка номера бита
   */
  private static final String BIT_INDEX_COL = "B"; //$NON-NLS-1$ ;

  /**
   * Колонка rtdata id
   */
  private static final String RTDATA_ID_COL = "C"; //$NON-NLS-1$ ;

  /**
   * Колонка rtdata name
   */
  private static final String RTDATA_NAME_COL = "D"; //$NON-NLS-1$ ;

  /**
   * Колонка rtdata description
   */
  private static final String RTDATA_DESCR_COL = "E"; //$NON-NLS-1$ ;

  /**
   * Колонка generate 0->1
   */
  private static final String UP_COL = "F"; //$NON-NLS-1$ ;

  /**
   * Колонка generate 1->0
   */
  private static final String DN_COL = "G"; //$NON-NLS-1$ ;

  /**
   * Начальная строка для сканирования
   */
  private static final int START_ROW = 3;

  private static String cellRef( String aColumnLetter, int nRowIndex ) {
    return aColumnLetter + Integer.toString( nRowIndex );
  }

  /**
   * Считывает описание данных
   *
   * @param aOdsFile файл с описанием данных
   * @return {@link StringMap<StringMap<IList<BitIdx2DtoRtData>>>} карта classId -> карта word rtDataId -> список
   *         описаний BitIdx2DtoRtData данных
   * @throws IOException исключение при работе с файлом
   */
  @SuppressWarnings( "javadoc" )
  private static void parse( File aOdsFile )
      throws IOException {

    // Читаем подряд пока не закончатся закладки с описанием данных
    SpreadSheet spreadSheet = SpreadSheet.createFromFile( aOdsFile );
    for( int i = 0; i < spreadSheet.getSheetCount(); i++ ) {
      Sheet sheet = spreadSheet.getSheet( i );
      String classId = sheet.getName();
      StringMap<IList<BitIdx2DtoRtData>> rtDataMap = readRtDataInfoes( sheet );
      dtoRtdataInfoesMap.put( classId, rtDataMap );
      StringMap<IList<BitIdx2DtoEvent>> evtMap = readEventInfoes( sheet );
      dtoEventInfoesMap.put( classId, evtMap );
      StringMap<IList<BitIdx2RriDtoAttr>> rriAttrMap = readRriAttrInfoes( sheet );
      dtoRriAttrInfoesMap.put( classId, rriAttrMap );
    }
  }

  private static StringMap<IList<BitIdx2RriDtoAttr>> readRriAttrInfoes( Sheet aSheet ) {
    StringMap<IList<BitIdx2RriDtoAttr>> retVal = new StringMap<>();
    // сканируем закладку от 3 ряда
    for( int currRow = START_ROW; currRow <= aSheet.getRowCount(); currRow++ ) {
      // читаем материнский rtDataId
      MutableCell<?> cell = aSheet.getCellAt( cellRef( BIT_ARRAY_DATAID_COL, currRow ) );
      // если ячейка пустая, то пропускаем
      if( cell.isEmpty() ) {
        continue;
      }
      String bitArrayRtDataId = cell.getTextValue();
      // проверяем и создаем, если надо, его список bitRtData
      if( !retVal.hasKey( bitArrayRtDataId ) ) {
        retVal.put( bitArrayRtDataId, new ElemArrayList<>() );
      }

      BitIdx2RriDtoAttr attrInfo = readBitIdx2RriDtoAttr( bitArrayRtDataId, aSheet, currRow );
      if( attrInfo != null ) {
        IListEdit<BitIdx2RriDtoAttr> bitList = (IListEdit<BitIdx2RriDtoAttr>)retVal.getByKey( bitArrayRtDataId );
        bitList.add( attrInfo );
      }
    }
    return retVal;
  }

  private static StringMap<IList<BitIdx2DtoRtData>> readRtDataInfoes( Sheet aSheet ) {
    StringMap<IList<BitIdx2DtoRtData>> retVal = new StringMap<>();
    // сканируем закладку от 3 ряда
    for( int currRow = START_ROW; currRow <= aSheet.getRowCount(); currRow++ ) {
      // читаем материнский rtDataId
      MutableCell<?> cell = aSheet.getCellAt( cellRef( BIT_ARRAY_DATAID_COL, currRow ) );
      // если ячейка пустая, то пропускаем
      if( cell.isEmpty() ) {
        continue;
      }
      String bitArrayRtDataId = cell.getTextValue();
      // проверяем и создаем, если надо, его список bitRtData
      if( !retVal.hasKey( bitArrayRtDataId ) ) {
        retVal.put( bitArrayRtDataId, new ElemArrayList<>() );
      }

      BitIdx2DtoRtData dataInfo = readBitIdx2DtoRtData( bitArrayRtDataId, aSheet, currRow );
      if( dataInfo != null ) {
        IListEdit<BitIdx2DtoRtData> bitList = (IListEdit<BitIdx2DtoRtData>)retVal.getByKey( bitArrayRtDataId );
        bitList.add( dataInfo );
      }
    }
    return retVal;
  }

  private static StringMap<IList<BitIdx2DtoEvent>> readEventInfoes( Sheet aSheet ) {
    StringMap<IList<BitIdx2DtoEvent>> retVal = new StringMap<>();
    // сканируем закладку от 3 ряда
    for( int currRow = START_ROW; currRow <= aSheet.getRowCount(); currRow++ ) {
      // читаем материнский rtDataId
      MutableCell<?> cell = aSheet.getCellAt( cellRef( BIT_ARRAY_DATAID_COL, currRow ) );
      // если ячейка пустая, то пропускаем
      if( cell.isEmpty() ) {
        continue;
      }
      String bitArrayRtDataId = cell.getTextValue();
      // проверяем и создаем, если надо, его список bitEvent
      if( !retVal.hasKey( bitArrayRtDataId ) ) {
        retVal.put( bitArrayRtDataId, new ElemArrayList<>() );
      }

      BitIdx2DtoEvent evtInfo = readBitIdx2DtoEvent( bitArrayRtDataId, aSheet, currRow );
      if( evtInfo != null ) {
        IListEdit<BitIdx2DtoEvent> bitList = (IListEdit<BitIdx2DtoEvent>)retVal.getByKey( bitArrayRtDataId );
        bitList.add( evtInfo );
      }
    }
    return retVal;
  }

  /**
   * Читает описание BitIdx2DtoRtData
   *
   * @param aBitArrayRtDataId - id переменной битоваого массива
   * @param aSheet таблица с описанием
   * @param aRowIndex текущий ряд парсинга
   * @return {@link BitIdx2DtoRtData} описание данного
   */
  private static BitIdx2DtoRtData readBitIdx2DtoRtData( String aBitArrayRtDataId, Sheet aSheet, int aRowIndex ) {
    // id данного
    MutableCell<?> cell = aSheet.getCellAt( cellRef( RTDATA_ID_COL, aRowIndex ) );
    String dataId = cell.getTextValue();

    // если нет id - пропустить эту строчку вообще
    boolean empty = cell.isEmpty();

    if( !empty ) {
      // проверяем что это rtData
      if( !dataId.startsWith( RTD_PREFIX ) ) {
        return null;
      }
      // bit index
      cell = aSheet.getCellAt( cellRef( BIT_INDEX_COL, aRowIndex ) );
      String idxStr = cell.getTextValue();
      int bitIndex = Integer.parseInt( idxStr );
      // название
      cell = aSheet.getCellAt( cellRef( RTDATA_NAME_COL, aRowIndex ) );
      String name = cell.getTextValue();
      // описание
      cell = aSheet.getCellAt( cellRef( RTDATA_DESCR_COL, aRowIndex ) );
      String descr = cell.getTextValue();
      // sync
      boolean sync = false; // по умолчанию асинхронное
      int deltaT = sync ? 1000 : 1;

      IDtoRtdataInfo dataInfo = DtoRtdataInfo.create1( dataId, new DataType( EAtomicType.BOOLEAN ), //
          true, // isCurr
          true, // isHist
          sync, // isSync
          deltaT, // deltaT
          OptionSetUtils.createOpSet( //
              TSID_NAME, name, //
              TSID_DESCRIPTION, descr //
          ) );

      return new BitIdx2DtoRtData( aBitArrayRtDataId, bitIndex, dataInfo );
    }
    return null;
  }

  /**
   * Читает описание BitIdx2RriDtoAttr
   *
   * @param aBitArrayRtDataId - id переменной битового массива
   * @param aSheet таблица с описанием
   * @param aRowIndex текущий ряд парсинга
   * @return {@link BitIdx2RriDtoAttr} описание атрибута
   */
  private static BitIdx2RriDtoAttr readBitIdx2RriDtoAttr( String aBitArrayRtDataId, Sheet aSheet, int aRowIndex ) {
    // id атрибута
    MutableCell<?> cell = aSheet.getCellAt( cellRef( RTDATA_ID_COL, aRowIndex ) );

    // если нет id - пропустить эту строчку вообще
    boolean empty = cell.isEmpty();

    if( !empty ) {
      String attrId = cell.getTextValue();
      // проверяем что это НСИ атрибут
      if( !attrId.startsWith( RRI_PREFIX ) ) {
        return null;
      }
      // bit index
      cell = aSheet.getCellAt( cellRef( BIT_INDEX_COL, aRowIndex ) );
      String idxStr = cell.getTextValue();
      int bitIndex = Integer.parseInt( idxStr );
      // название
      cell = aSheet.getCellAt( cellRef( RTDATA_NAME_COL, aRowIndex ) );
      String name = cell.getTextValue();
      // описание
      cell = aSheet.getCellAt( cellRef( RTDATA_DESCR_COL, aRowIndex ) );
      String descr = cell.getTextValue();

      IDtoAttrInfo attrInfo = DtoAttrInfo.create1( attrId, new DataType( EAtomicType.BOOLEAN ), //
          OptionSetUtils.createOpSet( //
              TSID_NAME, name, //
              TSID_DESCRIPTION, descr //
          ) );

      return new BitIdx2RriDtoAttr( aBitArrayRtDataId, bitIndex, attrInfo );
    }
    return null;
  }

  /**
   * Читает описание BitIdx2DtoEvent
   *
   * @param aBitArrayRtDataId - id переменной битоваого массива
   * @param aSheet таблица с описанием
   * @param aRowIndex текущий ряд парсинга
   * @return {@link BitIdx2DtoEvent} описание события
   */
  private static BitIdx2DtoEvent readBitIdx2DtoEvent( String aBitArrayRtDataId, Sheet aSheet, int aRowIndex ) {
    // id события
    MutableCell<?> cell = aSheet.getCellAt( cellRef( RTDATA_ID_COL, aRowIndex ) );
    String evtId = cell.getTextValue();

    // если нет id - пропустить эту строчку вообще
    boolean empty = cell.isEmpty();

    if( !empty ) {
      // соблюдаем соглашения о наименовании
      if( !evtId.startsWith( EVT_PREFIX ) ) {
        // отрезаем префикс rtd or rri
        if( evtId.startsWith( RTD_PREFIX ) ) {
          evtId = evtId.substring( RTD_PREFIX.length() );
        }
        if( evtId.startsWith( RRI_PREFIX ) ) {
          evtId = evtId.substring( RRI_PREFIX.length() );
        }
        evtId = EVT_PREFIX + evtId;
      }
      // bit index
      cell = aSheet.getCellAt( cellRef( BIT_INDEX_COL, aRowIndex ) );
      String idxStr = cell.getTextValue();
      int bitIndex = Integer.parseInt( idxStr );
      // название
      cell = aSheet.getCellAt( cellRef( RTDATA_NAME_COL, aRowIndex ) );
      String name = cell.getTextValue();
      // описание
      cell = aSheet.getCellAt( cellRef( RTDATA_DESCR_COL, aRowIndex ) );
      String descr = cell.getTextValue();
      // 0->1
      cell = aSheet.getCellAt( cellRef( UP_COL, aRowIndex ) );
      boolean up = false;
      String upText = TsLibUtils.EMPTY_STRING;
      if( !cell.isEmpty() ) {
        up = true;
        upText = cell.getTextValue();
      }
      // 1->0
      cell = aSheet.getCellAt( cellRef( DN_COL, aRowIndex ) );
      boolean dn = false;
      String dnText = TsLibUtils.EMPTY_STRING;
      if( !cell.isEmpty() ) {
        dn = true;
        dnText = cell.getTextValue();
      }
      if( up || dn ) {

        // for example FMT_BOOL_CHECK = "%Б[-|✔]"
        String FMT_ON = "%Б[" + dnText + "|" + upText + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // создаем описание параметра
        DataDef EVPDD_ON = DataDef.create( EVPID_ON, EAtomicType.BOOLEAN, TSID_NAME, STR_N_EV_PARAM_ON, //
            TSID_DESCRIPTION, STR_D_EV_PARAM_ON, //
            TSID_IS_NULL_ALLOWED, AV_FALSE, //
            TSID_FORMAT_STRING, FMT_ON, //
            TSID_DEFAULT_VALUE, AV_FALSE );
        StridablesList<IDataDef> evParams = new StridablesList<>( EVPDD_ON );

        IDtoEventInfo evtInfo = DtoEventInfo.create1( evtId, true, //
            evParams, //
            OptionSetUtils.createOpSet( //
                IAvMetaConstants.TSID_NAME, name, //
                IAvMetaConstants.TSID_DESCRIPTION, descr //
            ) ); //

        return new BitIdx2DtoEvent( aBitArrayRtDataId, bitIndex, evtInfo, up, dn );
      }
    }
    return null;
  }

  /**
   * @return карта id класса - > его {@link BitIdx2DtoRtData}
   */
  private static StringMap<StringMap<IList<BitIdx2DtoRtData>>> getRtdataInfoesMap() {
    return dtoRtdataInfoesMap;
  }

  /**
   * @return карта id класса - > его {@link BitIdx2DtoRtData}
   */
  private static StringMap<StringMap<IList<BitIdx2RriDtoAttr>>> getRriAttrInfoesMap() {
    return dtoRriAttrInfoesMap;
  }

  /**
   * @return карта id класса - > его BitIdx2DtoEvent
   */
  private static StringMap<StringMap<IList<BitIdx2DtoEvent>>> getEventInfoesMap() {
    return dtoEventInfoesMap;
  }
}
