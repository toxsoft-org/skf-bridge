package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;

/**
 * Contributes M5-models for opc ua entities.
 *
 * @author max
 * @author dima
 */
public class KM5ModbusContributor
    extends KM5AbstractContributor {

  /**
   * Creator singleton.
   */
  public static final IKM5ContributorCreator CREATOR = KM5ModbusContributor::new;

  private final IStringListEdit myModels = new StringArrayList();

  /**
   * Constructor.
   *
   * @param aConn {@link ISkConnection} - the connection
   * @param aDomain {@link IM5Domain} - connection domain
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public KM5ModbusContributor( ISkConnection aConn, IM5Domain aDomain ) {
    super( aConn, aDomain );
  }

  @Override
  protected IStringList papiCreateModels() {
    OpcUaUtils.registerCfgUnitRealizationTypes( m5().tsContext() );

    addIfNotAlreadyAdded( new ModbusToS5CfgDocM5Model() );
    addIfNotAlreadyAdded( new GwidsForCfgM5Model() );
    addIfNotAlreadyAdded( new OpcToS5DataCfgUnitM5Model( OpcToS5DataCfgUnitM5Model.MODEL_ID_TEMPLATE + ".modbus", //$NON-NLS-1$
        ModbusNodesForCfgM5Model.MODEL_ID ) );
    addIfNotAlreadyAdded( new ModbusNodesForCfgM5Model() );
    addIfNotAlreadyAdded( new CfgUnitRealizationTypeM5Model() );
    addIfNotAlreadyAdded( new TCPAddressM5Model() );

    return myModels;
  }

  private <T> void addIfNotAlreadyAdded( M5Model<T> aModel ) {
    if( m5().findModel( aModel.id() ) == null ) {
      myModels.add( aModel.id() );
      m5().addModel( aModel );
    }
  }

}
