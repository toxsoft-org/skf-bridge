package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.utils.*;

/**
 * Life cycle maneger for {@link ModbusToS5CfgDoc }
 *
 * @author max
 */
public class ModbusToS5CfgDocM5LifecycleManager
    extends M5LifecycleManager<ModbusToS5CfgDoc, ModbusToS5CfgDocService> {

  /**
   * Constructor
   *
   * @param aModel - m5 model {@link IM5Model}
   * @param aDocService - service {@link ModbusToS5CfgDocService}
   */
  public ModbusToS5CfgDocM5LifecycleManager( IM5Model<ModbusToS5CfgDoc> aModel, ModbusToS5CfgDocService aDocService ) {
    super( aModel, true, true, true, true, aDocService );
  }

  @Override
  protected ModbusToS5CfgDoc doCreate( IM5Bunch<ModbusToS5CfgDoc> aValues ) {
    IAtomicValue nameVal = aValues.get( ModbusToS5CfgDocM5Model.NAME );
    IAtomicValue descrVal = aValues.get( ModbusToS5CfgDocM5Model.DESCRIPTION );
    String strid = "modbustos5.bridge.cfg.doc.id" + System.currentTimeMillis(); //$NON-NLS-1$
    ModbusToS5CfgDoc newDoc = new ModbusToS5CfgDoc( strid, nameVal.asString(), descrVal.asString() );

    IAtomicValue cfgFileNameVal = aValues.get( ModbusToS5CfgDocM5Model.CFG_FILE_NAME );
    IAtomicValue pathToL2Val = aValues.get( ModbusToS5CfgDocM5Model.PATH_TO_L2 );

    newDoc.setCfgFilesPrefix( cfgFileNameVal.asString().trim() );
    newDoc.setL2Path( pathToL2Val.asValobj() );

    master().saveCfgDoc( newDoc );

    return newDoc;
  }

  @Override
  protected ModbusToS5CfgDoc doEdit( IM5Bunch<ModbusToS5CfgDoc> aValues ) {
    IAtomicValue nameVal = aValues.get( ModbusToS5CfgDocM5Model.NAME );
    IAtomicValue descrVal = aValues.get( ModbusToS5CfgDocM5Model.DESCRIPTION );

    ModbusToS5CfgDoc origDoc = aValues.originalEntity();

    origDoc.setName( nameVal.asString() );
    origDoc.setDescription( descrVal.asString() );

    IAtomicValue cfgFileNameVal = aValues.get( ModbusToS5CfgDocM5Model.CFG_FILE_NAME );
    IAtomicValue pathToL2Val = aValues.get( ModbusToS5CfgDocM5Model.PATH_TO_L2 );

    origDoc.setCfgFilesPrefix( cfgFileNameVal.asString().trim() );
    origDoc.setL2Path( pathToL2Val.asValobj() );

    master().saveCfgDoc( origDoc );

    return origDoc;
  }

  @Override
  protected void doRemove( ModbusToS5CfgDoc aEntity ) {
    // docs.remove( aEntity );
    // master().writeColl( SECTID_OPC_CFG_DOCS, docs, OpcToS5DataCfgDoc.KEEPER );

    master().removeCfgDoc( aEntity );
  }

  @Override
  protected IList<ModbusToS5CfgDoc> doListEntities() {
    // docs = new ElemArrayList<>( master().readColl( SECTID_OPC_CFG_DOCS, OpcToS5DataCfgDoc.KEEPER ) );
    // return docs;

    return master().getCfgDocs();
  }
}
