package org.toxsoft.skf.bridge.s5.lib.impl;

import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper;
import org.toxsoft.core.tslib.bricks.keeper.IEntityKeeper;
import org.toxsoft.core.tslib.bricks.strio.IStrioReader;
import org.toxsoft.core.tslib.bricks.strio.IStrioWriter;
import org.toxsoft.core.tslib.utils.valobj.TsValobjUtils;

/**
 * Хранитель объекта типа {@link SkGatewayInfos} в текстовое представление.
 *
 * @author mvk
 */
public class SkGatewayInfosKeeper
    extends AbstractEntityKeeper<SkGatewayInfos> {

  /**
   * Идентификатор регистрации хранителя {@link #KEEPER} в реестре {@link TsValobjUtils}.
   */
  public static final String KEEPER_ID = "SkGatewayInfosKeeper"; //$NON-NLS-1$

  /**
   * Экземпляр-синглтон хранителя.
   */
  public static final IEntityKeeper<SkGatewayInfos> KEEPER = new SkGatewayInfosKeeper();

  private SkGatewayInfosKeeper() {
    super( SkGatewayInfos.class, EEncloseMode.ENCLOSES_BASE_CLASS, null );
  }

  // ------------------------------------------------------------------------------------
  // Реализация методов класса AbstractEntityKeeper
  //

  @Override
  protected void doWrite( IStrioWriter aSw, SkGatewayInfos aEntity ) {
    SkGatewayInfoKeeper.KEEPER.writeColl( aSw, aEntity, false );
  }

  @Override
  protected SkGatewayInfos doRead( IStrioReader aSr ) {
    SkGatewayInfos retValue = new SkGatewayInfos();
    SkGatewayInfoKeeper.KEEPER.readColl( aSr, retValue );
    return retValue;
  }
}
