package org.toxsoft.skf.bridge.s5.lib.impl;

import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.s5.lib.*;

/**
 * Список описаний шлюзов {@link ISkGatewayInfo}
 *
 * @author mvk
 */
public class SkGatewayInfos
    extends StridablesList<ISkGatewayInfo> {

  private static final long serialVersionUID = 157157L;

  /**
   * Создает пустой список.
   */
  public SkGatewayInfos() {
  }

  /**
   * Конструктор копирования.
   *
   * @param aSource {@link IStridablesList}&lt;{@link ISkGatewayInfo}&gt; исходный список
   * @throws TsNullArgumentRtException аргумент = null
   */
  public SkGatewayInfos( IStridablesList<ISkGatewayInfo> aSource ) {
    super( aSource );
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
