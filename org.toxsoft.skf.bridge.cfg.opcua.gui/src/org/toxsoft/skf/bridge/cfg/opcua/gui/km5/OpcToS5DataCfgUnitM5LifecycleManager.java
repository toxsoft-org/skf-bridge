package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.list.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;

/**
 * Lifecycle Manager of {@link OpcToS5DataCfgUnit} entities.
 *
 * @author max
 */
public class OpcToS5DataCfgUnitM5LifecycleManager
    extends M5LifecycleManager<OpcToS5DataCfgUnit, OpcToS5DataCfgDoc> {

  /**
   * тестовый список конфигураций
   */
  // private OpcToS5DataCfgDoc cfgDoc = new OpcToS5DataCfgDoc( "id", "name", "descr" );

  /**
   * Constructor by M5 model and context
   *
   * @param aModel IM5Model - model
   * @param aCfgDoc OpcToS5DataCfgDoc - config document
   */
  public OpcToS5DataCfgUnitM5LifecycleManager( IM5Model<OpcToS5DataCfgUnit> aModel, OpcToS5DataCfgDoc aCfgDoc ) {
    super( aModel, true, true, true, true, aCfgDoc );
  }

  private OpcToS5DataCfgUnitM5Model m() {
    return (OpcToS5DataCfgUnitM5Model)model();
  }

  @Override
  protected IList<OpcToS5DataCfgUnit> doListEntities() {
    return master().dataUnits();
  }

  @Override
  protected OpcToS5DataCfgUnit doCreate( IM5Bunch<OpcToS5DataCfgUnit> aValues ) {
    String name = m().DISPLAY_NAME.getFieldValue( aValues ).asString();
    String strid = generateStrid();
    ECfgUnitType type = m().TYPE.getFieldValue( aValues ).asValobj();

    IList<Gwid> gwids = m().GWIDS.getFieldValue( aValues );
    IList<IAtomicValue> nodes = m().NODES.getFieldValue( aValues );

    ICfgUnitRealizationType realType = m().REALIZATION_TYPE.getFieldValue( aValues );
    IOptionSet realization = m().REALIZATION.getFieldValue( aValues );

    OpcToS5DataCfgUnit result = new OpcToS5DataCfgUnit( strid, name );
    result.setDataNodes2( new AvList( new ElemArrayList<>( nodes ) ) );
    result.setDataGwids( gwids );
    result.setTypeOfCfgUnit( type );
    result.setRelizationTypeId( realType.id() );
    result.setRealizationOpts( realization );
    master().addDataUnit( result );

    itemsProvider().informOnItemsListChange();

    return result;
  }

  /**
   * @return generate strid
   */
  public static String generateStrid() {
    return "opctos5.bridge.cfg.unit.id" + System.currentTimeMillis(); //$NON-NLS-1$
  }

  @Override
  protected OpcToS5DataCfgUnit doEdit( IM5Bunch<OpcToS5DataCfgUnit> aValues ) {
    String name = m().DISPLAY_NAME.getFieldValue( aValues ).asString();
    // String strid= m().STRID.getFieldValue( aValues ).asString();
    IList<Gwid> gwids = m().GWIDS.getFieldValue( aValues );
    ECfgUnitType type = m().TYPE.getFieldValue( aValues ).asValobj();
    IList<IAtomicValue> nodes = m().NODES.getFieldValue( aValues );

    ICfgUnitRealizationType realType = m().REALIZATION_TYPE.getFieldValue( aValues );
    IOptionSet realization = m().REALIZATION.getFieldValue( aValues );

    OpcToS5DataCfgUnit result = aValues.originalEntity();
    result.setDataNodes2( new AvList( new ElemArrayList<>( nodes ) ) );
    result.setName( name );
    result.setDataGwids( gwids );
    result.setTypeOfCfgUnit( type );
    result.setRelizationTypeId( realType.id() );
    result.setRealizationOpts( realization );

    itemsProvider().informOnItemsListChange();

    return result;
  }

  @Override
  protected void doRemove( OpcToS5DataCfgUnit aEntity ) {
    master().removeDataUnit( aEntity );

    itemsProvider().informOnItemsListChange();
  }

  /**
   * Adds cfg unit
   *
   * @param aCfgUnit OpcToS5DataCfgUnit - config unit
   */
  public void addCfgUnit( OpcToS5DataCfgUnit aCfgUnit ) {
    master().addDataUnit( aCfgUnit );
  }

  /**
   * Adds cfg units
   *
   * @param aCfgUnits IList - config unit
   */
  public void addCfgUnits( IList<OpcToS5DataCfgUnit> aCfgUnits ) {
    master().addDataUnits( aCfgUnits );
  }
}
