package org.toxsoft.skf.bridge.s5.lib.impl;

import org.toxsoft.core.tslib.bricks.strid.coll.impl.StridablesList;
import org.toxsoft.core.tslib.bricks.strio.IStrioHardConstants;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.skf.bridge.s5.lib.ISkGatewayConfiguration;

/**
 * Список описаний шлюзов {@link ISkGatewayConfiguration}
 *
 * @author mvk
 */
public class SkGatewayConfigurationList
    extends StridablesList<ISkGatewayConfiguration> {

  private static final long serialVersionUID = 157157L;

  /**
   * Создает пустой список.
   */
  public SkGatewayConfigurationList() {
    super();
  }

  /**
   * Создает список с начальным содержимым набора или массива aElems.
   *
   * @param aElems E... - элементы списка (набор или массив)
   * @throws TsNullArgumentRtException любой элемент = null
   */
  public SkGatewayConfigurationList( ISkGatewayConfiguration... aElems ) {
    super( aElems );
  }

  // ------------------------------------------------------------------------------------
  // Реализация методов класса Object
  //
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if( size() > 1 ) {
      sb.append( IStrioHardConstants.CHAR_EOL );
      sb.append( IStrioHardConstants.CHAR_SPACE );
    }
    for( int index = 0, n = size(); index < n; index++ ) {
      sb.append( get( index ).toString() );
      if( index + 1 < n ) {
        sb.append( IStrioHardConstants.CHAR_EOL );
        sb.append( IStrioHardConstants.CHAR_SPACE );
      }
    }
    return sb.toString();
  }

}
