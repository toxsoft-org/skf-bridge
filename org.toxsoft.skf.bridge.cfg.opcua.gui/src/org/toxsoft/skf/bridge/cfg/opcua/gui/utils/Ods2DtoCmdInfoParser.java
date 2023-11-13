package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.utils.ISkResources.*;

import java.io.*;

import org.jopendocument.dom.spreadsheet.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Утилитный класс считывает описание команд из ods файла и создает IDtoCmdInfo
 *
 * @author dima
 */
public class Ods2DtoCmdInfoParser {

  /**
   * карта id класса - > его DtoCmdInfo
   */
  static private final StringMap<IList<IDtoCmdInfo>> dtoCmdInfoesMap = new StringMap<>();

  /**
   * карта id класса - > карта cmdId -> cmdCod opc
   */
  static private final StringMap<IStringMap<Integer>> dtoCmdOpcCodesMap = new StringMap<>();

  static private String CMD_PREFIX = "cmd"; //$NON-NLS-1$

  /**
   * id аргумента.
   * <p>
   * аргумент имеет тип {@link EAtomicType#INTEGER}.
   */
  public static String CMD_ARG_INT_ID = "argInt"; //$NON-NLS-1$

  /**
   * /** Аргумент команды.
   * <p>
   * Параметр имеет тип {@link EAtomicType#INTEGER}.
   */
  static DataDef CMD_ARG_INT = DataDef.create( CMD_ARG_INT_ID, EAtomicType.INTEGER, TSID_NAME, STR_N_CMD_ARG_INT, //
      TSID_DESCRIPTION, STR_D_CMD_ARG_INT, //
      TSID_IS_NULL_ALLOWED, AV_TRUE );

  /**
   * id аргумента.
   * <p>
   * аргумент имеет тип {@link EAtomicType#FLOATING}.
   */
  public static String CMD_ARG_FLT_ID = "argFlt"; //$NON-NLS-1$

  /**
   * /** Аргумент команды.
   * <p>
   * Параметр имеет тип {@link EAtomicType#FLOATING}.
   */
  static DataDef CMD_ARG_FLT = DataDef.create( CMD_ARG_FLT_ID, EAtomicType.FLOATING, TSID_NAME, STR_N_CMD_ARG_FLT, //
      TSID_DESCRIPTION, STR_D_CMD_ARG_FLT, //
      TSID_IS_NULL_ALLOWED, AV_TRUE );

  /**
   * Колонка описания кода команды
   */
  private static final String CMD_CODE_COL = "A"; //$NON-NLS-1$ ;

  /**
   * Колонка cmdId
   */
  private static final String CMD_ID_COL = "B"; //$NON-NLS-1$ ;

  /**
   * Колонка cmdName
   */
  private static final String CMD_NAME_COL = "C"; //$NON-NLS-1$ ;

  /**
   * Колонка cmd description
   */
  private static final String CMD_DESCR_COL = "D"; //$NON-NLS-1$ ;

  /**
   * Колонка cmd description
   */
  private static final String CMD_ARG_TYPE_COL = "E"; //$NON-NLS-1$ ;

  private static String cellRef( String aColumnLetter, int nRowIndex ) {
    return aColumnLetter + Integer.toString( nRowIndex );
  }

  /**
   * Считывает описание команд
   *
   * @param aOdsFile файл с описанием классов
   * @return {@link StringMap<IList<IDtoCmdInfo>>} карта classId -> список описаний команд
   * @throws IOException исключение при работе с файлом
   */
  @SuppressWarnings( "javadoc" )
  public static StringMap<IList<IDtoCmdInfo>> parse( File aOdsFile )
      throws IOException {

    // Читаем подряд пока не закончатся закладки с описанием команд
    SpreadSheet spreadSheet = SpreadSheet.createFromFile( aOdsFile );
    for( int i = 0; i < spreadSheet.getSheetCount(); i++ ) {
      Sheet sheet = spreadSheet.getSheet( i );
      String classId = sheet.getName();
      IList<IDtoCmdInfo> cmdInfoes = readCmdInfoes( sheet );
      dtoCmdInfoesMap.put( classId, cmdInfoes );
    }
    return dtoCmdInfoesMap;
  }

  private static IList<IDtoCmdInfo> readCmdInfoes( Sheet aSheet ) {
    IListEdit<IDtoCmdInfo> retVal = new ElemArrayList<>();
    // сканируем закладку от 3 ряда
    for( int currRow = 3; currRow <= aSheet.getRowCount(); currRow++ ) {
      MutableCell<?> cell = aSheet.getCellAt( cellRef( CMD_CODE_COL, currRow ) );
      // если нет номера, то выходим
      if( !isNumber( cell ) ) {
        return retVal;
      }
      IDtoCmdInfo cmdInfo = readCmdInfo( aSheet, currRow );
      if( cmdInfo != null ) {
        retVal.add( cmdInfo );
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
   * Читает описание команды и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aSheet таблица с описанием
   * @param aRowIndex текущий ряд парсинга
   * @return {@link IDtoCmdInfo} описание команды
   */
  private static IDtoCmdInfo readCmdInfo( Sheet aSheet, int aRowIndex ) {
    IDtoCmdInfo retVal = null;
    // id команды
    MutableCell<?> cell = aSheet.getCellAt( cellRef( CMD_ID_COL, aRowIndex ) );

    // если нет id - пропустить эту строчку вообще
    boolean empty = cell.isEmpty();

    if( !empty ) {
      String cmdId = cell.getTextValue();
      // проверяем что это команда
      if( !cmdId.startsWith( CMD_PREFIX ) ) {
        return null;
      }

      EAtomicType type = dataType( aSheet, aRowIndex );
      // название
      cell = aSheet.getCellAt( cellRef( CMD_NAME_COL, aRowIndex ) );
      String name = cell.getTextValue();
      // описание
      cell = aSheet.getCellAt( cellRef( CMD_DESCR_COL, aRowIndex ) );
      String descr = cell.getTextValue();
      StridablesList<IDataDef> cmdArgs;
      cmdArgs = switch( type ) {
        case INTEGER -> new StridablesList<>( CMD_ARG_INT );
        case BOOLEAN -> throw invalidParamTypeExcpt( aRowIndex, type );
        case FLOATING -> new StridablesList<>( CMD_ARG_FLT );
        case NONE -> new StridablesList<>();
        case STRING -> throw invalidParamTypeExcpt( aRowIndex, type );
        case TIMESTAMP -> throw invalidParamTypeExcpt( aRowIndex, type );
        case VALOBJ -> throw invalidParamTypeExcpt( aRowIndex, type );
      };

      retVal = DtoCmdInfo.create1( cmdId, //
          cmdArgs, //
          OptionSetUtils.createOpSet( //
              IAvMetaConstants.TSID_NAME, name, //
              IAvMetaConstants.TSID_DESCRIPTION, descr //
          ) ); //

    }
    return retVal;
  }

  private static EAtomicType dataType( Sheet aSheet, int aRowIndex ) {
    // тип
    MutableCell<?> cell = aSheet.getCellAt( cellRef( CMD_ARG_TYPE_COL, aRowIndex ) );
    if( cell.isEmpty() ) {
      // для пустых ячеек подразумевается что команда без аргументв
      return EAtomicType.NONE;
    }
    String argTypeStr = cell.getTextValue();
    EAtomicType type = getDataType( argTypeStr );
    return type;
  }

  private static TsIllegalArgumentRtException invalidParamTypeExcpt( int aRowIndex, EAtomicType type ) {
    return new TsIllegalArgumentRtException( "Row %d\n Can't create event parameters with type %s", //$NON-NLS-1$
        Integer.valueOf( aRowIndex ), type.id() );
  }

  private static EAtomicType getDataType( String aDataTypeStr ) {
    if( aDataTypeStr.indexOf( "argInt" ) >= 0 ) { //$NON-NLS-1$
      return EAtomicType.INTEGER;
    }
    if( aDataTypeStr.indexOf( "argFlt" ) >= 0 ) { //$NON-NLS-1$
      return EAtomicType.FLOATING;
    }

    throw new TsIllegalArgumentRtException( "Unknown type: %s", aDataTypeStr ); //$NON-NLS-1$
  }

  /**
   * Считывает коды команд на opc сервере
   *
   * @param aOdsFile файл с описанием классов
   * @return {@link StringMap<IStringMap<Integer>>} карта classId -> карта cmdId -> cmdCod opc
   * @throws IOException исключение при работе с файлом
   */
  @SuppressWarnings( "javadoc" )
  public static IStringMap<IStringMap<Integer>> parseOpcCmdCodes( File aOdsFile )
      throws IOException {

    // Читаем подряд пока не закончатся закладки с описанием команд
    SpreadSheet spreadSheet = SpreadSheet.createFromFile( aOdsFile );
    for( int i = 0; i < spreadSheet.getSheetCount(); i++ ) {
      Sheet sheet = spreadSheet.getSheet( i );
      String classId = sheet.getName();
      IStringMap<Integer> cmdCodes = readCmdOpcCodes( sheet );
      dtoCmdOpcCodesMap.put( classId, cmdCodes );
    }
    return dtoCmdOpcCodesMap;
  }

  private static IStringMap<Integer> readCmdOpcCodes( Sheet aSheet ) {
    IStringMapEdit<Integer> retVal = new StringMap<>();
    // сканируем закладку от 3 ряда
    for( int currRow = 3; currRow <= aSheet.getRowCount(); currRow++ ) {
      MutableCell<?> cell = aSheet.getCellAt( cellRef( CMD_CODE_COL, currRow ) );
      // если нет номера, то выходим
      if( !isNumber( cell ) ) {
        return retVal;
      }

      int code = Integer.parseInt( cell.getTextValue() );
      IDtoCmdInfo cmdInfo = readCmdInfo( aSheet, currRow );
      if( cmdInfo != null ) {
        retVal.put( cmdInfo.id(), Integer.valueOf( code ) );
      }
    }
    return retVal;
  }
}
