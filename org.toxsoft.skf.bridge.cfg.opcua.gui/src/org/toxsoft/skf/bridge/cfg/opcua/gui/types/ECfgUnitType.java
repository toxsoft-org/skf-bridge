package org.toxsoft.skf.bridge.cfg.opcua.gui.types;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;

/**
 * Type of cfg unit: data, command, event
 *
 * @author max
 */
public enum ECfgUnitType {

  /**
   * Cfg unit type for configuration data transmition.
   */
  DATA,

  /**
   * Cfg unit type for configuration command execution.
   */
  COMMAND,

  /**
   * Cfg unit type for configuration event fire-ing.
   */
  EVENT;

  /**
   * The keeper ID.
   */
  public static final String KEEPER_ID = "ECfgUnitType"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public static final IEntityKeeper<ECfgUnitType> KEEPER =
      new AbstractEntityKeeper<>( ECfgUnitType.class, EEncloseMode.ENCLOSES_BASE_CLASS, ECfgUnitType.DATA ) {

        @Override
        protected void doWrite( IStrioWriter aSw, ECfgUnitType aEntity ) {
          aSw.writeAsIs( aEntity.name() );
        }

        @Override
        protected ECfgUnitType doRead( IStrioReader aSr ) {
          String id = aSr.readIdPath();
          return ECfgUnitType.valueOf( id );
        }

      };

  /**
   * Search enum element with pointed java-name, analog of valueof method with default value
   *
   * @param aEnumName String - java-name
   * @param aDefaultValue ECfgUnitType - default value
   * @return ECfgUnitType - enum element with pointed java-name or default if not found
   */
  public static ECfgUnitType searchByEnumName( String aEnumName, ECfgUnitType aDefaultValue ) {
    ECfgUnitType result = aDefaultValue;
    try {
      result = ECfgUnitType.valueOf( aEnumName );
    }
    catch( Exception e ) {
      System.out.println( e.getMessage() );
    }
    return result;
  }
}
