package org.toxsoft.skf.brifge.cfg.s7.gui.utils;

import java.util.regex.*;

import org.jopendocument.dom.spreadsheet.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;

import ru.toxsoft.l2.thd.opc.*;

/**
 * Set of utils methods
 *
 * @author dima
 */
public class S7Utils {

  private static final String  ROWS_START        = "#";                                       //$NON-NLS-1$
  private static final String  ROWS_END          = "##";                                      //$NON-NLS-1$
  private static final int     CLASSID_COL_NUM   = 0;
  private static final int     OBJID_COL_NUM     = 1;
  private static final int     DATAID_COL_NUM    = 2;
  private static final int     ADDRESS_COL_NUM   = 4;
  private static final int     TYPE_COL_NUM      = 5;
  private static final int     GWID_KIND_COL_NUM = 6;
  private static final Pattern objS7Pattern      = Pattern.compile( "([A-Z_\\d]+).([A-Z])" ); //$NON-NLS-1$

  private static class S7AddressParser {

    /**
     * Паттерн для распознавания полей описания адреса S7<br>
     * Примеры адресов:
     * <ul>
     * <li>DB4.DBD48.0 - буфер 4 байта, одно значение (float | long ) по смещению 0</li>
     * <li>DB4.DBX56.0 - буфер 1 байт, битовое значение со смещением 0</li>
     * <li>DB4.DBX56.1 - буфер 1 байт, битовое значение со смещением 1</li>
     * <li>DB4.DBB57.0 - буфер 1 байт, byte значение (uint8) со смещением 0</li>
     * <li>DB4.DBW58.0 - буфер 2 байт, byte значение (uint16) со смещением 0</li>
     * </ul>
     */
    Pattern p     = Pattern.compile( "DB([\\d]+).DB([XBWD])([\\d]+).([\\d])" ); //$NON-NLS-1$
    int     dbNum;
    int     bufSize;
    int     bufOffset;
    int     bitOffset;
    boolean isBit = false;

    @SuppressWarnings( { "nls", "boxing" } )
    void parseS7Adress( String aAddressStr ) {
      String dbNumStr;
      String bufSizeStr;
      String bufOffsetStr;
      String bitOffsetStr;
      Matcher n = p.matcher( aAddressStr );
      LoggerUtils.debug( "parsed string: %s\n", aAddressStr ); //
      if( n.find() ) {
        dbNumStr = n.group( 1 );
        dbNum = Integer.parseInt( dbNumStr );
        LoggerUtils.debug( "dbNum = %s\n", dbNum ); //
        bufSizeStr = n.group( 2 );
        isBit = isBitTag( bufSizeStr );
        bufSize = s7BufferSize( bufSizeStr );
        LoggerUtils.debug( "bufSize = %s\n", s7BufferSize( bufSizeStr ) ); //
        bufOffsetStr = n.group( 3 );
        bufOffset = Integer.parseInt( bufOffsetStr );
        LoggerUtils.debug( "bufOffset = %s\n", bufOffsetStr ); //
        bitOffsetStr = n.group( 4 );
        bitOffset = Integer.parseInt( bitOffsetStr );
        LoggerUtils.debug( "bitOffset = %s\n", bitOffsetStr ); //
      }
      else {
        LoggerUtils.error( "Unknown S7 address description: %s\n", aAddressStr ); //
      }
    }
  }

  @SuppressWarnings( "nls" )
  static int s7BufferSize( String aBufSize ) {
    if( aBufSize.compareTo( "D" ) == 0 ) {
      return 4;
    }
    if( aBufSize.compareTo( "W" ) == 0 ) {
      return 2;
    }
    // case of "X" or "B"
    return 1;
  }

  @SuppressWarnings( "nls" )
  static boolean isBitTag( String aTagType ) {
    return (aTagType.compareTo( "X" ) == 0);
  }

  static String getStringValue( Sheet aSheet, int aColNo, int aRowNo ) {
    MutableCell<SpreadSheet> cell = aSheet.getCellAt( aColNo, aRowNo );
    return cell.getTextValue();
  }

  /**
   * По строке описания типа S7 возвращает {@link EAtomicType}<br>
   * Типы S7:
   * <ul>
   * <li>float32</li>
   * <li>Real</li>
   * <li>DInt</li>
   * <li>Int</li>
   * <li>StateWord</li>
   * <li>ControlWord</li>
   * <li>uint8</li>
   * <li>Bool</li>
   * </ul>
   *
   * @param aValTypeStr строка описания типа S7
   * @return {@link EAtomicType}
   */
  @SuppressWarnings( "nls" )
  public static EAtomicType s7AtomicType( String aValTypeStr ) {
    String trimmed = aValTypeStr.trim();
    if( trimmed.compareTo( "float32" ) == 0 || trimmed.compareTo( "Real" ) == 0 ) {
      return EAtomicType.FLOATING;
    }
    if( trimmed.compareTo( "\"StateWord\"" ) == 0 || trimmed.compareTo( "\"ControlWord\"" ) == 0
        || trimmed.compareTo( "uint8" ) == 0 || trimmed.compareTo( "Int" ) == 0 || trimmed.compareTo( "DInt" ) == 0
        || trimmed.compareTo( "\"VLV_WS\"" ) == 0 || trimmed.compareTo( "\"VLV_WE\"" ) == 0 ) {
      return EAtomicType.INTEGER;
    }
    if( trimmed.compareTo( "Bool" ) == 0 || trimmed.compareTo( "Boolean" ) == 0 ) {
      return EAtomicType.BOOLEAN;
    }
    throw new TsIllegalArgumentRtException( aValTypeStr );
  }

  /**
   * Parse one sheet and add tag <-> Gwid descriptions to generator
   *
   * @param aBiL2CfgGenerator {@link BiL2CfgGenerator} - l2 configuration generator
   * @param aPage {@link Sheet} - sheet of one class objects
   * @param aS7ServerId - S7 server ID
   */
  @SuppressWarnings( "boxing" )
  public static void parseL2Cfg( BiL2CfgGenerator aBiL2CfgGenerator, Sheet aPage, String aS7ServerId ) {

    int firstRow = firstRowNumber( aPage ) + 2;
    int lastRow = lastRowNumber( aPage );

    for( int row = firstRow; row < lastRow; row++ ) {
      // читаем описание S7 тега
      String address = getStringValue( aPage, ADDRESS_COL_NUM, row );
      if( address == null || address.trim().isBlank() ) {
        continue;
      }
      String gwidKind = getStringValue( aPage, GWID_KIND_COL_NUM, row );
      S7AddressParser parser = new S7AddressParser();
      parser.parseS7Adress( address );
      String tagId;
      if( parser.isBit ) {
        tagId = aBiL2CfgGenerator.addBitTag( aS7ServerId, parser.dbNum, getTagKind( gwidKind ), parser.bufOffset,
            parser.bitOffset );
      }
      else {
        String type = getStringValue( aPage, TYPE_COL_NUM, row );
        tagId = aBiL2CfgGenerator.addDataTag( aS7ServerId, parser.dbNum, getTagKind( gwidKind ), parser.bufOffset,
            parser.bufSize, s7AtomicType( type ) );
      }
      // читаем его Gwid
      String classId = getStringValue( aPage, CLASSID_COL_NUM, row );
      String objId = getStringValue( aPage, OBJID_COL_NUM, row );
      Matcher n = objS7Pattern.matcher( objId );
      if( n.find() ) {
        objId = n.group( 1 );
      }
      else {
        throw new TsIllegalStateRtException( "Can't parse object id from cell value: %s in row = %d", objId, row ); //$NON-NLS-1$
      }
      String dataId = getStringValue( aPage, DATAID_COL_NUM, row );
      switch( EGwidKind.findById( gwidKind ) ) {
        case GW_RTDATA:
          // добавляем в конфигурацию
          Gwid tagRtdGwid = Gwid.createRtdata( classId, objId, dataId );
          aBiL2CfgGenerator.linkS7Tag2Gwid( tagId, tagRtdGwid );
          break;
        case GW_CMD:
          Gwid tagCmdGwid = Gwid.createCmd( classId, objId, dataId );
          aBiL2CfgGenerator.linkS7Tag2Gwid( tagId, tagCmdGwid );
          break;
        case GW_ATTR:
        case GW_CLASS:
        case GW_CLOB:
        case GW_CMD_ARG:
        case GW_EVENT:
        case GW_EVENT_PARAM:
        case GW_LINK:
        case GW_RIVET:
        default:
          throw new TsIllegalStateRtException();
      }
    }
  }

  private static EKind getTagKind( String aGwidKind ) {
    return switch( EGwidKind.findById( aGwidKind ) ) {
      case GW_RTDATA -> EKind.R;
      case GW_CMD -> EKind.W;
      case GW_ATTR, GW_CLASS, GW_CLOB, GW_CMD_ARG, GW_EVENT, GW_EVENT_PARAM, GW_LINK, GW_RIVET -> throw new TsIllegalStateRtException();
      default -> throw new TsIllegalStateRtException();
    };
  }

  private static int firstRowNumber( Sheet aSheet ) {
    for( int i = 0; i < 10000; i++ ) {
      if( ROWS_START.equals( aSheet.getCellAt( 0, i ).getTextValue() ) ) {
        return i;
      }
    }
    return 0;
  }

  private static int lastRowNumber( Sheet aSheet ) {
    for( int i = 0; i < 10000; i++ ) {
      try {
        String val = aSheet.getCellAt( 0, i ).getTextValue();
        if( ROWS_END.equals( val ) ) {
          return i;
        }
      }
      catch( Throwable e ) {
        e.printStackTrace();
        return i;
      }
    }
    return -1;
  }

  private S7Utils() {
    // nop
  }

  @SuppressWarnings( { "javadoc", "nls" } )
  public static void main( String[] args ) {
    S7AddressParser parser = new S7AddressParser();
    parser.parseS7Adress( "DB4.DBD48.0" );
    parser.parseS7Adress( "DB4.DBX56.0" );
    parser.parseS7Adress( "DB4.DBX56.1" );
    parser.parseS7Adress( "DB4.DBB57.0" );
    parser.parseS7Adress( "DB4.DBW58.0" );
    parser.parseS7Adress( "DB4.DBS58.0" );
    String s7Types[] = { "float32", "Real", "DInt", "StateWord", "ControlWord", "uint8", "Bool" };
    for( String s7Type : s7Types ) {
      LoggerUtils.debug( "s7Type:%s -> EAtomicType:%s\n", s7Type, s7AtomicType( s7Type ) ); //
    }
  }

}
