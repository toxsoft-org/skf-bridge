package org.toxsoft.skf.bridge.cfg.modbus.gui.utils;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.core.tslib.utils.TsLibUtils.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.skide.IGreenWorldRefbooks.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.km5.ISkResources.*;

import java.io.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.refbooks.lib.*;
import org.toxsoft.skf.refbooks.lib.impl.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Create refbooks to be used in modbus codes
 *
 * @author dima
 */
public class ModbusRefbookGenerator {

  static class RefookItemBuider {

    IOptionSetEdit attrValues = new OptionSet();

    private final Skid   skid;
    private final String name;
    private final String descr;

    RefookItemBuider( String aId, String aName, String aDescr, ISkRefbook aRefbook ) {
      String classId = aRefbook.itemClassId();
      skid = new Skid( classId, aId );
      name = aName;
      descr = aDescr;

      DtoRefbookInfo rbInfo = DtoRefbookInfo.of( aRefbook );
      for( IDtoAttrInfo ai : rbInfo.attrInfos() ) {
        attrValues.put( ai.id(), IAtomicValue.NULL );
      }
    }

    void setValue( String aFieldId, IAtomicValue aValue ) {
      attrValues.put( aFieldId, aValue );
    }

    IDtoFullObject buildItem() {
      IDtoObject dtoObj = new DtoObject( skid, attrValues, IStringMap.EMPTY );
      DtoFullObject retVal = new DtoFullObject( dtoObj, IStringMap.EMPTY, IStringMap.EMPTY );
      retVal.attrs().setStr( TSID_NAME, name );
      retVal.attrs().setStr( TSID_DESCRIPTION, descr );
      return retVal;
    }
  }

  /**
   * server connection
   */
  private final ISkConnection conn;
  private final Shell         shell;

  // reg.translator - Трансляторы регистров
  public static String RBID_REG_TRANSLATOR                  = "reg.translator";
  public static String RBATRID_REG_TRANSLATOR___WORDSCOUNT  = "wordsCount";    // Количество слов
  public static String RBATRID_REG_TRANSLATOR___PARAMS      = "params";        // Параметры транслятора
  public static String RBATRID_REG_TRANSLATOR___REQUESTTYPE = "requestType";   // Тип запроса
  public static String RBATRID_REG_TRANSLATOR___VALUETYPE   = "valueType";     // Тип значения

  public static String ITEMID_REG_TRANSLATOR___VQ2INT        = "VQRegs2Integer"; // org.toxsoft.l2.thd.modbus.common.translators.VordaqIntegerRegistersTranslator
  public static String ITEMID_REG_TRANSLATOR___FLOATCDAB     = "FloatCDAB";      // org.toxsoft.l2.thd.modbus.common.translators.FloatCommonRegistersTranslator
  public static String ITEMID_REG_TRANSLATOR___SEC2HOURFLOAT = "Sec2HourFloat";  // org.toxsoft.l2.thd.modbus.common.translators.Sec2HourFloatRegistersTranslator
  public static String ITEMID_REG_TRANSLATOR___FLOAT2BIT     = "Float2Bit";      // org.toxsoft.l2.thd.modbus.common.translators.Float2BitRegistersTranslator

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_REG_TRANSLATOR___WORDSCOUNT}.
   */
  static IDtoAttrInfo ATRINF_WORDS_COUNT = DtoAttrInfo.create2( RBATRID_REG_TRANSLATOR___WORDSCOUNT, DDEF_INTEGER, //
      TSID_NAME, STR_N_MODBUS_WORDS_COUNT, //
      TSID_DESCRIPTION, STR_D_MODBUS_WORDS_COUNT, //
      TSID_DEFAULT_VALUE, avInt( 1 ) //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_REG_TRANSLATOR___PARAMS}.
   */
  static IDtoAttrInfo ATRINF_TRANSLATOR_PARAMS = DtoAttrInfo.create2( RBATRID_REG_TRANSLATOR___PARAMS, DDEF_STRING, //
      TSID_NAME, STR_N_MODBUS_PARAMETERS_STR, //
      TSID_DESCRIPTION, STR_D_MODBUS_PARAMETERS_STR //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_REG_TRANSLATOR___REQUESTTYPE}.
   */
  static IDtoAttrInfo ATRINF_REQUESTTYPE = DtoAttrInfo.create2( RBATRID_REG_TRANSLATOR___REQUESTTYPE, DDEF_VALOBJ, //
      TSID_NAME, STR_N_MODBUS_REQUEST_TYPE, //
      TSID_DESCRIPTION, STR_D_MODBUS_REQUEST_TYPE, //
      TSID_KEEPER_ID, ERequestType.KEEPER_ID //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_REG_TRANSLATOR___VALUETYPE}.
   */
  static IDtoAttrInfo ATRINF_VALUETYPE = DtoAttrInfo.create2( RBATRID_REG_TRANSLATOR___VALUETYPE, DDEF_VALOBJ, //
      TSID_NAME, STR_N_MODBUS_VALUE_TYPE, //
      TSID_DESCRIPTION, STR_D_MODBUS_VALUE_TYPE, //
      TSID_KEEPER_ID, EAtomicType.KEEPER_ID //
  );

  /**
   * Refbook: Трансляторы регистров.
   * <p>
   */
  public static IDtoRefbookInfo REFBOOK_REG_TRANSLATOR = DtoRefbookInfo.create( RBID_REG_TRANSLATOR, ///
      OptionSetUtils.createOpSet( ///
          TSID_NAME, "Трансляторы", //$NON-NLS-1$
          TSID_DESCRIPTION, "Трансляторы значений регистров Modbus" //$NON-NLS-1$
      ), ///
      new StridablesList<>( ///
          ATRINF_WORDS_COUNT, //
          ATRINF_TRANSLATOR_PARAMS, //
          ATRINF_REQUESTTYPE, //
          ATRINF_VALUETYPE //
      ), //
      new StridablesList<>( ///
      // no CLOBs
      ), ///
      new StridablesList<>( ///
      // no rivets
      ), ///
      new StridablesList<>( ///
      // no links
      ) ///
  );

  /**
   * Constructor.
   *
   * @param aConn - server
   * @param aShell - curren shell
   */
  public ModbusRefbookGenerator( ISkConnection aConn, Shell aShell ) {
    conn = aConn;
    shell = aShell;
  }

  /**
   * Create refbook register translators
   */
  @SuppressWarnings( "nls" )
  public void createTranslatorsRb() {
    ISkRefbookService rbServ = conn.coreApi().getService( ISkRefbookService.SERVICE_ID );

    // create refbook
    ISkRefbook rbTranslators = rbServ.defineRefbook( REFBOOK_REG_TRANSLATOR );
    // fill refbook
    addTraslatorRbItem( rbTranslators, ITEMID_REG_TRANSLATOR___VQ2INT, "Vordaq", "Vordaq registers to integer", 2,
        "org.toxsoft.l2.thd.modbus.common.translators.VordaqIntegerRegistersTranslator", ERequestType.AI,
        EAtomicType.INTEGER );
  }

  private static void addTraslatorRbItem( ISkRefbook aRefbook, String aItemId, String aName, String aDescr,
      int aWordCount, String aParams, ERequestType aRequestType, EAtomicType aAtomicType ) {
    String rbItemId = aItemId;
    RefookItemBuider b = new RefookItemBuider( rbItemId, aName, aDescr, aRefbook );
    b.setValue( RBATRID_REG_TRANSLATOR___WORDSCOUNT, AvUtils.avInt( aWordCount ) );
    b.setValue( RBATRID_REG_TRANSLATOR___PARAMS, AvUtils.avStr( aParams ) );
    b.setValue( RBATRID_REG_TRANSLATOR___REQUESTTYPE, AvUtils.avValobj( aRequestType ) );
    b.setValue( RBATRID_REG_TRANSLATOR___VALUETYPE, AvUtils.avValobj( aAtomicType ) );
    IDtoFullObject rbItem = b.buildItem();
    aRefbook.defineItem( rbItem );
  }

}
