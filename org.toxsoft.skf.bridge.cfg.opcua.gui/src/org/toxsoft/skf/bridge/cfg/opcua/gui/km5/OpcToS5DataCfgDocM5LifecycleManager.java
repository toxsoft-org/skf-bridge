package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;

public class OpcToS5DataCfgDocM5LifecycleManager
    extends M5LifecycleManager<OpcToS5DataCfgDoc, OpcToS5DataCfgDocService> {

  public OpcToS5DataCfgDocM5LifecycleManager( IM5Model<OpcToS5DataCfgDoc> aModel, OpcToS5DataCfgDocService aMaster ) {
    super( aModel, true, true, true, true, aMaster );

  }

  @Override
  protected OpcToS5DataCfgDoc doCreate( IM5Bunch<OpcToS5DataCfgDoc> aValues ) {
    IAtomicValue nameVal = aValues.get( OpcToS5DataCfgDocM5Model.NAME );
    IAtomicValue descrVal = aValues.get( OpcToS5DataCfgDocM5Model.DESCRIPTION );
    String strid = "opctos5.bridge.cfg.doc.id" + System.currentTimeMillis(); //$NON-NLS-1$
    OpcToS5DataCfgDoc newDoc = new OpcToS5DataCfgDoc( strid, nameVal.asString(), descrVal.asString() );

    // docs.add( newDoc );
    // master().writeColl( SECTID_OPC_CFG_DOCS, docs, OpcToS5DataCfgDoc.KEEPER );

    master().saveCfgDoc( newDoc );

    return newDoc;
  }

  @Override
  protected OpcToS5DataCfgDoc doEdit( IM5Bunch<OpcToS5DataCfgDoc> aValues ) {
    IAtomicValue nameVal = aValues.get( OpcToS5DataCfgDocM5Model.NAME );
    IAtomicValue descrVal = aValues.get( OpcToS5DataCfgDocM5Model.DESCRIPTION );

    OpcToS5DataCfgDoc origDoc = aValues.originalEntity();

    origDoc.setName( nameVal.asString() );
    origDoc.setDescription( descrVal.asString() );

    // master().writeColl( SECTID_OPC_CFG_DOCS, docs, OpcToS5DataCfgDoc.KEEPER );

    master().saveCfgDoc( origDoc );

    return origDoc;
  }

  @Override
  protected void doRemove( OpcToS5DataCfgDoc aEntity ) {
    // docs.remove( aEntity );
    // master().writeColl( SECTID_OPC_CFG_DOCS, docs, OpcToS5DataCfgDoc.KEEPER );

    master().removeCfgDoc( aEntity );
  }

  @Override
  protected IList<OpcToS5DataCfgDoc> doListEntities() {
    // docs = new ElemArrayList<>( master().readColl( SECTID_OPC_CFG_DOCS, OpcToS5DataCfgDoc.KEEPER ) );
    // return docs;

    return master().getCfgDocs();
  }

}
