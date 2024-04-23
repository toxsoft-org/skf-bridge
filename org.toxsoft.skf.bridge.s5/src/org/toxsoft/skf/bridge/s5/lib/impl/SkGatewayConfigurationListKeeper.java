package org.toxsoft.skf.bridge.s5.lib.impl;

import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper;
import org.toxsoft.core.tslib.bricks.keeper.IEntityKeeper;
import org.toxsoft.core.tslib.bricks.strio.IStrioReader;
import org.toxsoft.core.tslib.bricks.strio.IStrioWriter;
import org.toxsoft.core.tslib.utils.valobj.TsValobjUtils;

/**
 * Хранитель объекта типа {@link SkGatewayConfigurationList} в текстовое представление.
 *
 * @author mvk
 */
public class SkGatewayConfigurationListKeeper
    extends AbstractEntityKeeper<SkGatewayConfigurationList> {

  /**
   * Идентификатор регистрации хранителя {@link #KEEPER} в реестре {@link TsValobjUtils}.
   */
  public static final String KEEPER_ID = "SkGatewayConfigurationListKeeper"; //$NON-NLS-1$

  /**
   * Экземпляр-синглтон хранителя.
   */
  public static final IEntityKeeper<SkGatewayConfigurationList> KEEPER = new SkGatewayConfigurationListKeeper();

  private SkGatewayConfigurationListKeeper() {
    super( SkGatewayConfigurationList.class, EEncloseMode.ENCLOSES_BASE_CLASS, null );
  }

  // ------------------------------------------------------------------------------------
  // Реализация методов класса AbstractEntityKeeper
  //

  @Override
  protected void doWrite( IStrioWriter aSw, SkGatewayConfigurationList aEntity ) {
    SkGatewayConfigurationKeeper.KEEPER.writeColl( aSw, aEntity, false );
  }

  @Override
  protected SkGatewayConfigurationList doRead( IStrioReader aSr ) {
    SkGatewayConfigurationList retValue = new SkGatewayConfigurationList();
    SkGatewayConfigurationKeeper.KEEPER.readColl( aSr, retValue );
    return retValue;
  }
}
