package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.*;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.std.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * Тип дерева OPC UA.
 *
 * @author dima
 */
@SuppressWarnings( "nls" )
public enum EOPCUATreeType
    implements IStridable {

  /**
   * Дерево построенное из среды Poligone
   */
  POLIGONE( "POLIGONE", STR_N_POLIGONE, STR_D_POLIGONE ),
  /**
   * Дерево построенное из среды Siemens
   */
  SIEMENS( "SIEMENS", STR_N_SIEMENS, STR_D_SIEMENS ),
  /**
   * Дерево построенное из среды Siemens для Байконура
   */
  SIEMENS_BAIKONUR( "SIEMENS", STR_N_SIEMENS_BAIKONUR, STR_D_SIEMENS_BAIKONUR ),
  /**
   * Дерево построенное из остальных инструментариев
   */
  OTHER( "OTHER", STR_N_OTHER, STR_D_OTHER );

  /**
   * The keeper ID.
   */
  public static final String KEEPER_ID = "EOPCUATreeType"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public static final IEntityKeeper<EOPCUATreeType> KEEPER = new StridableEnumKeeper<>( EOPCUATreeType.class );

  private static IStridablesListEdit<EOPCUATreeType> list = null;

  private final String id;
  private final String name;
  private final String description;

  EOPCUATreeType( String aId, String aName, String aDescription ) {
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

  // ----------------------------------------------------------------------------------
  // API
  //

  /**
   * Returns all constants in single list.
   *
   * @return {@link IStridablesList}&lt; {@link EOPCUATreeType} &gt; - list of constants in order of declaraion
   */
  public static IStridablesList<EOPCUATreeType> asList() {
    if( list == null ) {
      list = new StridablesList<>( values() );
    }
    return list;
  }

  /**
   * Returns the constant by the ID.
   *
   * @param aId String - the ID
   * @return {@link EOPCUATreeType} - found constant
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsItemNotFoundRtException no constant found by specified ID
   */
  public static EOPCUATreeType getById( String aId ) {
    return asList().getByKey( aId );
  }

  /**
   * Finds the constant by the name.
   *
   * @param aName String - the name
   * @return {@link EOPCUATreeType} - found constant or <code>null</code>
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public static EOPCUATreeType findByName( String aName ) {
    TsNullArgumentRtException.checkNull( aName );
    for( EOPCUATreeType item : values() ) {
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
   * @return {@link EOPCUATreeType} - found constant
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsItemNotFoundRtException no constant found by specified name
   */
  public static EOPCUATreeType getByName( String aName ) {
    return TsItemNotFoundRtException.checkNull( findByName( aName ) );
  }

}
