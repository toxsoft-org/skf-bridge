package org.toxsoft.skf.bridge.cfg.modbus.gui.type;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;

/**
 * Типы запросов к modbus
 *
 * @author max
 */
public enum ERequestType {

  /**
   * Чтение дискретного выхода
   */
  DO, //

  /**
   * Чтение аналогового выхода
   */
  AO, //

  /**
   * Чтение дискретного входа
   */
  DI, //

  /**
   * Чтение аналогового входа
   */
  AI; //

  /**
   * The keeper ID.
   */
  public static final String KEEPER_ID = "ERequestType"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public static final IEntityKeeper<ERequestType> KEEPER =
      new AbstractEntityKeeper<>( ERequestType.class, EEncloseMode.ENCLOSES_BASE_CLASS, ERequestType.DI ) {

        @Override
        protected void doWrite( IStrioWriter aSw, ERequestType aEntity ) {
          aSw.writeAsIs( aEntity.name() );
        }

        @Override
        protected ERequestType doRead( IStrioReader aSr ) {
          String id = aSr.readIdPath();
          return ERequestType.valueOf( id );
        }

      };
}
