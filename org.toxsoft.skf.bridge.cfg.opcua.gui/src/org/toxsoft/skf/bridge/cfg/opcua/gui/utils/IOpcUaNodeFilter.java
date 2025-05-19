package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;

/**
 * Фильтр opc ua узлов
 *
 * @author max
 */
public interface IOpcUaNodeFilter {

  /**
   * Проверяет соответствие узла opc ua критериям фильтрации.
   *
   * @param aOpcUaNode - проверяемый узел
   * @return boolean true - если eptk соответствует критериям фильтрации.
   */
  boolean isSuited( CfgOpcUaNode aOpcUaNode );

  /**
   * Фильтр opc ua узлов по умолчанию.
   *
   * @author max
   */
  public static class DeaultByStrOpcUaNodeFilter
      implements IOpcUaNodeFilter {

    /**
     * Строка фильтрации.
     */
    private String str;

    /**
     * Constructor.
     *
     * @param aStr String - Строка фильтрации.
     */
    public DeaultByStrOpcUaNodeFilter( String aStr ) {
      str = aStr;
    }

    @Override
    public boolean isSuited( CfgOpcUaNode aCfgOpcUaNode ) {
      return aCfgOpcUaNode.getNodeId().contains( str );
    }

  }

  /**
   * Пустой фильтр, которому соответствует любой элемент
   */
  IOpcUaNodeFilter EMPTY_FILTER = aDataGwid -> true;

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
