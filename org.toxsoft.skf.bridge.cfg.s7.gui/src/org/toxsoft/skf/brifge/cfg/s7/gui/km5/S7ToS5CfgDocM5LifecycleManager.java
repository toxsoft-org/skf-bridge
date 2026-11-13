package org.toxsoft.skf.brifge.cfg.s7.gui.km5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.utils.*;
import org.toxsoft.skf.brifge.cfg.s7.gui.utils.*;

/**
 * Life cycle maneger for {@link S7ToS5CfgDoc }
 *
 * @author dima
 */
public class S7ToS5CfgDocM5LifecycleManager
    extends M5LifecycleManager<S7ToS5CfgDoc, S7ToS5CfgDocService> {

  /**
   * Constructor
   *
   * @param aModel - m5 model {@link IM5Model}
   * @param aDocService - service {@link ModbusToS5CfgDocService}
   */
  public S7ToS5CfgDocM5LifecycleManager( IM5Model<S7ToS5CfgDoc> aModel, S7ToS5CfgDocService aDocService ) {
    super( aModel, true, true, true, true, aDocService );
  }

  @Override
  protected S7ToS5CfgDoc doCreate( IM5Bunch<S7ToS5CfgDoc> aValues ) {
    IAtomicValue nameVal = aValues.get( S7ToS5CfgDocM5Model.NAME );
    IAtomicValue descrVal = aValues.get( S7ToS5CfgDocM5Model.DESCRIPTION );
    String strid = "S7tos5.bridge.cfg.doc.id" + System.currentTimeMillis(); //$NON-NLS-1$
    S7ToS5CfgDoc newDoc = new S7ToS5CfgDoc( strid, nameVal.asString(), descrVal.asString() );

    IAtomicValue cfgFileNameVal = aValues.get( S7ToS5CfgDocM5Model.CFG_FILE_NAME );
    IAtomicValue pathToL2Val = aValues.get( S7ToS5CfgDocM5Model.PATH_TO_L2 );

    newDoc.setCfgFilesPrefix( cfgFileNameVal.asString().trim() );
    newDoc.setL2Path( pathToL2Val.asValobj() );

    IList<IStringList> props = aValues.get( S7ToS5CfgDocM5Model.ADDITIONAL_PROPERTIES );
    newDoc.setProperties( props );

    master().saveCfgDoc( newDoc );

    return newDoc;
  }

  @Override
  protected S7ToS5CfgDoc doEdit( IM5Bunch<S7ToS5CfgDoc> aValues ) {
    IAtomicValue nameVal = aValues.get( S7ToS5CfgDocM5Model.NAME );
    IAtomicValue descrVal = aValues.get( S7ToS5CfgDocM5Model.DESCRIPTION );

    S7ToS5CfgDoc origDoc = aValues.originalEntity();

    origDoc.setName( nameVal.asString() );
    origDoc.setDescription( descrVal.asString() );

    IAtomicValue cfgFileNameVal = aValues.get( S7ToS5CfgDocM5Model.CFG_FILE_NAME );
    IAtomicValue pathToL2Val = aValues.get( S7ToS5CfgDocM5Model.PATH_TO_L2 );

    origDoc.setCfgFilesPrefix( cfgFileNameVal.asString().trim() );
    origDoc.setL2Path( pathToL2Val.asValobj() );

    IList<IStringList> props = aValues.get( S7ToS5CfgDocM5Model.ADDITIONAL_PROPERTIES );
    origDoc.setProperties( props );

    master().saveCfgDoc( origDoc );

    return origDoc;
  }

  @Override
  protected void doRemove( S7ToS5CfgDoc aEntity ) {
    // docs.remove( aEntity );
    // master().writeColl( SECTID_OPC_CFG_DOCS, docs, OpcToS5DataCfgDoc.KEEPER );

    master().removeCfgDoc( aEntity );
  }

  @Override
  protected IList<S7ToS5CfgDoc> doListEntities() {
    // docs = new ElemArrayList<>( master().readColl( SECTID_OPC_CFG_DOCS, OpcToS5DataCfgDoc.KEEPER ) );
    // return docs;

    return master().getCfgDocs();
  }
}
