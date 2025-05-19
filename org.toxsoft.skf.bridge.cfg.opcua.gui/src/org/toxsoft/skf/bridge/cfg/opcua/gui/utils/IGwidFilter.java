package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.core.tslib.gw.gwid.*;

/**
 * Фильтр gwid
 *
 * @author max
 */
public interface IGwidFilter {

  /**
   * Проверяет соответствие gwid критериям фильтрации.
   *
   * @param aDataGwid - проверяемый gwid
   * @return boolean true - если gwid соответствует критериям фильтрации.
   */
  boolean isSuited( Gwid aDataGwid );

  /**
   * Фильтр gwid по умолчанию.
   *
   * @author max
   */
  public static class DeaultByStrGwidFilter
      implements IGwidFilter {

    /**
     * Строка фильтрации.
     */
    private String str;

    /**
     * Constructor.
     *
     * @param aStr String - Строка фильтрации.
     */
    public DeaultByStrGwidFilter( String aStr ) {
      str = aStr;
    }

    @Override
    public boolean isSuited( Gwid aDataGwid ) {
      return aDataGwid.skid().classId().contains( str ) || aDataGwid.propId().contains( str )
          || aDataGwid.skid().strid().contains( str );
    }

  }

  /**
   * Пустой фильтр, которому соответствует любой элемент
   */
  IGwidFilter EMPTY_FILTER = aDataGwid -> true;

  // {
  // private static boolean isSuited( IList<Gwid> aDataGwids ) {
  // for( Gwid gwid : aDataGwids ) {
  // if( gwid.skid().classId().indexOf( "InterfaceConverterBox" ) >= 0
  // && gwid.propId().indexOf( TKA_TEMPLATE ) >= 0 ) {
  // return true;
  // }
  // if( gwid.skid().strid().indexOf( TKA_TEMPLATE ) >= 0 ) {
  // return true;
  // }
  // }
  // return false;
  // }
  // }
}
