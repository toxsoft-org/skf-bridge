package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;

import java.io.*;

import org.jopendocument.dom.spreadsheet.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Утилитный класс считывает описание битовых данных из ods файла и создает {@link BitIdx2DtoRtData }
 *
 * @author dima
 */
public class Ods2DtoRtDataInfoParser {

  /**
   * карта id класса - > его DtoCmdInfo
   */
  static private final StringMap<StringMap<IList<BitIdx2DtoRtData>>> dtoRtdataInfoesMap = new StringMap<>();

  static private String RTD_PREFIX = "rtd"; //$NON-NLS-1$

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
  public static StringMap<StringMap<IList<BitIdx2DtoRtData>>> parse( File aOdsFile )
      throws IOException {

    // Читаем подряд пока не закончатся закладки с описанием данных
    SpreadSheet spreadSheet = SpreadSheet.createFromFile( aOdsFile );
    for( int i = 0; i < spreadSheet.getSheetCount(); i++ ) {
      Sheet sheet = spreadSheet.getSheet( i );
      String classId = sheet.getName();
      StringMap<IList<BitIdx2DtoRtData>> rtDataMap = readRtDataInfoes( sheet );
      dtoRtdataInfoesMap.put( classId, rtDataMap );
    }
    return dtoRtdataInfoesMap;
  }

  private static StringMap<IList<BitIdx2DtoRtData>> readRtDataInfoes( Sheet aSheet ) {
    StringMap<IList<BitIdx2DtoRtData>> retVal = new StringMap<>();
    // сканируем закладку от 3 ряда
    for( int currRow = START_ROW; currRow <= aSheet.getRowCount(); currRow++ ) {
      MutableCell<?> cell = aSheet.getCellAt( cellRef( BIT_ARRAY_DATAID_COL, currRow ) );
      // если ячейка пустая, то пропускаем
      if( cell.isEmpty() ) {
        continue;
      }
      // читаем материнский rtDataId
      cell = aSheet.getCellAt( cellRef( BIT_ARRAY_DATAID_COL, currRow ) );
      String bitArrayRtDataId = cell.getTextValue();
      // проверяем и создаем, если надо, его список bitRtData
      if( !retVal.hasKey( bitArrayRtDataId ) ) {
        retVal.put( bitArrayRtDataId, new ElemArrayList<>() );
      }

      BitIdx2DtoRtData dataInfo = readBitIdx2DtoRtData( aSheet, currRow );
      if( dataInfo != null ) {
        IListEdit<BitIdx2DtoRtData> bitList = (IListEdit<BitIdx2DtoRtData>)retVal.getByKey( bitArrayRtDataId );
        bitList.add( dataInfo );
      }
    }
    return retVal;
  }

  private static boolean isNumber( MutableCell<?> aCell ) {
    if( aCell.isEmpty() ) {
      return false;
    }
    String cellVal = aCell.getTextValue();
    try {
      Integer.parseInt( cellVal );
    }
    catch( Exception ex ) {
      LoggerUtils.errorLogger().error( ex );
      return false;
    }
    return true;
  }

  /**
   * Читает описание BitIdx2DtoRtData
   *
   * @param aSheet таблица с описанием
   * @param aRowIndex текущий ряд парсинга
   * @return {@link BitIdx2DtoRtData} описание данного
   */
  private static BitIdx2DtoRtData readBitIdx2DtoRtData( Sheet aSheet, int aRowIndex ) {
    // id данного
    MutableCell<?> cell = aSheet.getCellAt( cellRef( RTDATA_ID_COL, aRowIndex ) );
    String dataId = cell.getTextValue();

    // если нет id - пропустить эту строчку вообще
    boolean empty = cell.isEmpty();

    if( !empty ) {
      String rtdId = cell.getTextValue();
      // проверяем что это rtData
      if( !rtdId.startsWith( RTD_PREFIX ) ) {
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

      return new BitIdx2DtoRtData( bitIndex, dataInfo );
    }
    return null;
  }
}
