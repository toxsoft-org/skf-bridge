package org.toxsoft.skf.bridge.cfg.modbus.gui.type;

import static org.toxsoft.skf.bridge.cfg.modbus.gui.l10n.ISkBridgeCfgModbusGuiSharedResources.*;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.std.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * The MODBUS request ype for data I/O.
 *
 * @author AUTHOR_NAME
 */
public enum ERequestType
    implements IStridable {

  /**
   * Read discrete output.
   */
  DO( "do", STR_ERT_DO, STR_ERT_DO_D ), // //$NON-NLS-1$

  /**
   * Read analog output.
   */
  AO( "ao", STR_ERT_AO, STR_ERT_AO_D ), // //$NON-NLS-1$

  /**
   * Read discrete input.
   */
  DI( "di", STR_ERT_DI_D, STR_ERT_DI_D ), // //$NON-NLS-1$

  /**
   * Read analog input.
   */
  AI( "ai", STR_ERT_AI_D, STR_ERT_AI_D ); // //$NON-NLS-1$

  /**
   * The registered keeper ID.
   */
  public static final String KEEPER_ID = "ERequestType"; //$NON-NLS-1$

  /**
   * The keeper singleton.
   */
  public static final IEntityKeeper<ERequestType> KEEPER = new StridableEnumKeeper<>( ERequestType.class );

  private static IStridablesListEdit<ERequestType> list = null;

  private final String id;
  private final String name;
  private final String description;

  ERequestType( String aId, String aName, String aDescription ) {
    id = aId;
    name = aName;
    description = aDescription;
  }

  // --------------------------------------------------------------------------
  // IStridable
  //

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
    return description;
  }

  // ------------------------------------------------------------------------------------
  // API
  //

  /**
   * Determines if the item presents discrete type.
   *
   * @return boolean - <code>true</code> - item presents discrete I/O, <code>false</code> - analog I/O
   */
  public boolean isDiscret() {
    return this == DO || this == DI;
  }

  // ----------------------------------------------------------------------------------
  // Stridable enum common API
  //

  /**
   * Returns all constants in single list.
   *
   * @return {@link IStridablesList}&lt; {@link ERequestType} &gt; - list of constants in order of declaraion
   */
  public static IStridablesList<ERequestType> asList() {
    if( list == null ) {
      list = new StridablesList<>( values() );
    }
    return list;
  }

  /**
   * Returns the constant by the ID.
   *
   * @param aId String - the ID
   * @return {@link ERequestType} - found constant
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsItemNotFoundRtException no constant found by specified ID
   */
  public static ERequestType getById( String aId ) {
    return asList().getByKey( aId );
  }

  /**
   * Finds the constant by the name.
   *
   * @param aName String - the name
   * @return {@link ERequestType} - found constant or <code>null</code>
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public static ERequestType findByName( String aName ) {
    TsNullArgumentRtException.checkNull( aName );
    for( ERequestType item : values() ) {
      if( item.name.equals( aName ) ) {
        return item;
      }
    }
    return null;
  }

  /**
   * Returns the constant by the name.
   *
   * @param aName String - the name
   * @return {@link ERequestType} - found constant
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsItemNotFoundRtException no constant found by specified name
   */
  public static ERequestType getByName( String aName ) {
    return TsItemNotFoundRtException.checkNull( findByName( aName ) );
  }

}
