package org.toxsoft.skf.bridge.cfg.opcua.gui.types;

import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;

/**
 * Holder of cfg unit realization types
 *
 * @author max
 */
public class CfgUnitRealizationTypeRegister {

  private IStringMapEdit<IStridablesListEdit<ICfgUnitRealizationType>> allTypes = new StringMap<>();

  /**
   * Registers a type of cfg unit realization
   *
   * @param aType - a type of cfg unit realization
   */
  public void registerType( ICfgUnitRealizationType aType ) {
    String cfgUnitType = aType.cfgUnitType().name();
    IStridablesListEdit<ICfgUnitRealizationType> cfgUnitList;
    if( allTypes.hasKey( cfgUnitType ) ) {
      cfgUnitList = allTypes.getByKey( cfgUnitType );
    }
    else {
      cfgUnitList = new StridablesList<>();
      allTypes.put( cfgUnitType, cfgUnitList );
    }
    TsItemAlreadyExistsRtException.checkTrue( cfgUnitList.hasKey( aType.id() ) );
    cfgUnitList.add( aType );
  }

  /**
   * Returns list of cfg unit realization types for pointed cfg unit type.
   *
   * @param aCfgUnitType ECfgUnitType - pointed cfg unit type.
   * @return IList - cfg unit realization types
   */
  public IList<ICfgUnitRealizationType> getTypesOfRealizationForCfgUnitType( ECfgUnitType aCfgUnitType ) {
    if( allTypes.hasKey( aCfgUnitType.name() ) ) {
      return allTypes.getByKey( aCfgUnitType.name() );
    }

    return new StridablesList<>();
  }

  /**
   * Returns list of cfg unit realization types for pointed cfg unit type.
   *
   * @param aTypeOfRealizationId String - identifier of type
   * @param aCfgUnitType ECfgUnitType - pointed cfg unit type.
   * @return IList - cfg unit realization types
   */
  public ICfgUnitRealizationType getTypeOfRealizationById( ECfgUnitType aCfgUnitType, String aTypeOfRealizationId ) {
    IStridablesListEdit<ICfgUnitRealizationType> types = allTypes.getByKey( aCfgUnitType.name() );

    return types.getByKey( aTypeOfRealizationId );
  }
}
