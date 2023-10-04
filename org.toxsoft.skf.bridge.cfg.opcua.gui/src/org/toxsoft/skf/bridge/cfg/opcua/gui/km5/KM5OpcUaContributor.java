package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;

/**
 * Contributes M5-models for opc ua entities.
 *
 * @author max
 * @author dima
 */
public class KM5OpcUaContributor
    extends KM5AbstractContributor {

  /**
   * Creator singleton.
   */
  public static final IKM5ContributorCreator CREATOR = KM5OpcUaContributor::new;

  private static final IStringList CONRTIBUTED_MODEL_IDS = new StringArrayList( //
      OpcUaServerConnCfgModel.MODEL_ID, //
      OpcUaNodeModel.MODEL_ID, //
      DtoObjectM5Model.MODEL_ID, //
      UaVariableNodeM5Model.MODEL_ID, //
      GwidsForCfgM5Model.MODEL_ID, //
      OpcToS5DataCfgUnitM5Model.MODEL_ID, //
      OpcToS5DataCfgDocM5Model.MODEL_ID, //
      NodesForCfgM5Model.MODEL_ID, //
      CfgUnitRealizationTypeM5Model.MODEL_ID, //
      CfgOpcUaNodeM5Model.MODEL_ID );

  private final IStringListEdit myModels = new StringArrayList();

  /**
   * Constructor.
   *
   * @param aConn {@link ISkConnection} - the connection
   * @param aDomain {@link IM5Domain} - connection domain
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public KM5OpcUaContributor( ISkConnection aConn, IM5Domain aDomain ) {
    super( aConn, aDomain );
  }

  @Override
  protected IStringList papiCreateModels() {
    OpcUaUtils.registerCfgUnitRealizationTypes( m5().tsContext() );
    OpcUaServerConnCfgModel opcUaServerConnCfgModel = new OpcUaServerConnCfgModel();
    myModels.add( opcUaServerConnCfgModel.id() );
    m5().addModel( opcUaServerConnCfgModel );

    OpcUaNodeModel opcUaModel = new OpcUaNodeModel();
    myModels.add( opcUaModel.id() );
    m5().addModel( opcUaModel );

    UaVariableNodeM5Model uaVariableNodeM5Model = new UaVariableNodeM5Model();
    myModels.add( uaVariableNodeM5Model.id() );
    m5().addModel( uaVariableNodeM5Model );

    DtoObjectM5Model dtoObjectM5Model = new DtoObjectM5Model( skConn() );
    myModels.add( dtoObjectM5Model.id() );
    m5().addModel( dtoObjectM5Model );

    GwidsForCfgM5Model gwidsForCfgM5Model = new GwidsForCfgM5Model();
    myModels.add( gwidsForCfgM5Model.id() );
    m5().addModel( gwidsForCfgM5Model );

    OpcToS5DataCfgUnitM5Model opcToS5DataCfgUnitM5Model = new OpcToS5DataCfgUnitM5Model();
    myModels.add( opcToS5DataCfgUnitM5Model.id() );
    m5().addModel( opcToS5DataCfgUnitM5Model );

    OpcToS5DataCfgDocM5Model opcToS5DataCfgDocM5Model = new OpcToS5DataCfgDocM5Model();
    myModels.add( opcToS5DataCfgDocM5Model.id() );
    m5().addModel( opcToS5DataCfgDocM5Model );

    NodesForCfgM5Model nodesForCfgM5Model = new NodesForCfgM5Model();
    myModels.add( nodesForCfgM5Model.id() );
    m5().addModel( nodesForCfgM5Model );

    CfgUnitRealizationTypeM5Model cfgUnitRealizationTypeM5Model = new CfgUnitRealizationTypeM5Model();
    myModels.add( cfgUnitRealizationTypeM5Model.id() );
    m5().addModel( cfgUnitRealizationTypeM5Model );

    CfgOpcUaNodeM5Model cfgOpcUaNodeM5Model = new CfgOpcUaNodeM5Model();
    myModels.add( cfgOpcUaNodeM5Model.id() );
    m5().addModel( cfgOpcUaNodeM5Model );

    return CONRTIBUTED_MODEL_IDS;
  }

}
