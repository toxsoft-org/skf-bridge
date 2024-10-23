package org.toxsoft.skf.bridge.cfg.modbus.gui.type;

import static org.toxsoft.skf.bridge.cfg.modbus.gui.type.ISkResources.*;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strio.*;

/**
 * Типы запросов к modbus
 *
 * @author max
 */
public enum ERequestType
    implements IStridable {

  /**
   * Чтение дискретного выхода
   */
  DO( "do", STR_N_REQ_TYPE_DO ), // //$NON-NLS-1$

  /**
   * Чтение аналогового выхода
   */
  AO( "ao", STR_N_REQ_TYPE_AO ), // //$NON-NLS-1$

  /**
   * Чтение дискретного входа
   */
  DI( "di", STR_N_REQ_TYPE_DI ), // //$NON-NLS-1$

  /**
   * Чтение аналогового входа
   */
  AI( "ai", STR_N_REQ_TYPE_AI ); // //$NON-NLS-1$

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

  /**
   * Determines if the item presents discret type.
   *
   * @return true - item presents discret type, false - overwise.
   */
  public boolean isDiscret() {
    return this == DO || this == DI;
  }

  private String id;
  private String name;

  ERequestType( String aId, String aName ) {
    id = aId;
    name = aName;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public String nmName() {
    return name;
  }

  @Override
  public String description() {
    return name;
  }
}
