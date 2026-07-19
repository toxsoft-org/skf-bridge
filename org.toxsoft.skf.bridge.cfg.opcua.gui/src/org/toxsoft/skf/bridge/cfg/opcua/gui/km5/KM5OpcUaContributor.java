package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
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
      OpcToS5DataCfgUnitM5Model.MODEL_ID_TEMPLATE + ".opcua", //
      OpcToS5DataCfgDocM5Model.MODEL_ID, //
      NodesForCfgM5Model.MODEL_ID, //
      CfgUnitRealizationTypeM5Model.MODEL_ID, //
      CfgOpcUaNodeM5Model.MODEL_ID, //
      StringPropertiesM5Model.MODEL_ID, //
      SimpleStringM5Model.MODEL_ID );

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

    OpcToS5DataCfgDocService docService = new OpcToS5DataCfgDocService( m5().tsContext() );
    m5().tsContext().put( OpcToS5DataCfgDocService.class, docService );
    OpcUaUtils.registerCfgUnitRealizationTypes( m5().tsContext() );

    addIfNotAlreadyAdded( new OpcUaServerConnCfgModel() );
    addIfNotAlreadyAdded( new OpcUaNodeModel() );
    addIfNotAlreadyAdded( new UaVariableNodeM5Model() );
    addIfNotAlreadyAdded( new DtoObjectM5Model( skConn() ) );
    addIfNotAlreadyAdded( new GwidsForCfgM5Model() );
    addIfNotAlreadyAdded( new OpcToS5DataCfgUnitM5Model( OpcToS5DataCfgUnitM5Model.MODEL_ID_TEMPLATE + ".opcua",
        NodesForCfgM5Model.MODEL_ID ) );
    addIfNotAlreadyAdded( new OpcToS5DataCfgDocM5Model() );
    addIfNotAlreadyAdded( new NodesForCfgM5Model() );
    addIfNotAlreadyAdded( new CfgUnitRealizationTypeM5Model() );
    addIfNotAlreadyAdded( new CfgOpcUaNodeM5Model() );
    addIfNotAlreadyAdded( new StringPropertiesM5Model() );
    addIfNotAlreadyAdded( new SimpleStringM5Model() );

    return CONRTIBUTED_MODEL_IDS;
  }

  private <T> void addIfNotAlreadyAdded( M5Model<T> aModel ) {
    if( m5().findModel( aModel.id() ) == null ) {
      myModels.add( aModel.id() );
      m5().addModel( aModel );
    }
  }

}
