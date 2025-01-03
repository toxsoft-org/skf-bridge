package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;

import java.io.*;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.av.list.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.utils.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * Lifecycle Manager of {@link OpcToS5DataCfgUnit} entities.
 *
 * @author max
 */
public class ModbusToS5CfgUnitM5LifecycleManager
    extends M5LifecycleManager<OpcToS5DataCfgUnit, ModbusToS5CfgDoc> {

  /**
   * Constructor by M5 model and context
   *
   * @param aModel IM5Model - model
   * @param aCfgDoc OpcToS5DataCfgDoc - config document
   */
  public ModbusToS5CfgUnitM5LifecycleManager( IM5Model<OpcToS5DataCfgUnit> aModel, ModbusToS5CfgDoc aCfgDoc ) {
    super( aModel, true, true, true, true, aCfgDoc );
  }

  @Override
  protected IList<OpcToS5DataCfgUnit> doListEntities() {
    return master().dataUnits();
  }

  /**
   * dima 27.12.24 open access to guts to reoder items
   *
   * @return {@link IListEdit} list of data units
   */
  public IListEdit<OpcToS5DataCfgUnit> getListEditEntities() {
    return master().dataUnits();
  }

  @Override
  protected OpcToS5DataCfgUnit doCreate( IM5Bunch<OpcToS5DataCfgUnit> aValues ) {
    String name = aValues.getAsAv( OpcToS5DataCfgUnitM5Model.FID_DISPLAY_NAME ).asString();

    String strid = generateStrid();
    ECfgUnitType type = aValues.getAsAv( OpcToS5DataCfgUnitM5Model.FID_TYPE ).asValobj();
    // aValues ).asString();
    IList<Gwid> gwids = aValues.get( OpcToS5DataCfgUnitM5Model.FID_GWIDS );
    IList<IAtomicValue> nodes = aValues.get( OpcToS5DataCfgUnitM5Model.FID_NODES );

    ICfgUnitRealizationType realType = aValues.get( OpcToS5DataCfgUnitM5Model.FID_REALIZATION_TYPE );
    IOptionSet realization = aValues.get( OpcToS5DataCfgUnitM5Model.FID_REALIZATION );

    OpcToS5DataCfgUnit result = new OpcToS5DataCfgUnit( strid, name );
    result.setDataNodes2( new AvList( new ElemArrayList<>( nodes ) ) );
    result.setDataGwids( gwids );
    result.setTypeOfCfgUnit( type );
    result.setRelizationTypeId( realType.id() );
    result.setRealizationOpts( realization );
    master().addDataUnit( result );
    return result;
  }

  /**
   * @return generate strid
   */
  public static String generateStrid() {
    return "modbus.bridge.cfg.unit.id" + System.currentTimeMillis(); //$NON-NLS-1$
  }

  @Override
  protected OpcToS5DataCfgUnit doEdit( IM5Bunch<OpcToS5DataCfgUnit> aValues ) {
    String name = aValues.getAsAv( OpcToS5DataCfgUnitM5Model.FID_DISPLAY_NAME ).asString();
    ECfgUnitType type = aValues.getAsAv( OpcToS5DataCfgUnitM5Model.FID_TYPE ).asValobj();
    // String strid= m().STRID.getFieldValue( aValues ).asString();
    IList<Gwid> gwids = aValues.get( OpcToS5DataCfgUnitM5Model.FID_GWIDS );
    IList<IAtomicValue> nodes = aValues.get( OpcToS5DataCfgUnitM5Model.FID_NODES );

    ICfgUnitRealizationType realType = aValues.get( OpcToS5DataCfgUnitM5Model.FID_REALIZATION_TYPE );
    IOptionSet realization = aValues.get( OpcToS5DataCfgUnitM5Model.FID_REALIZATION );

    OpcToS5DataCfgUnit result = aValues.originalEntity();
    result.setDataNodes2( new AvList( new ElemArrayList<>( nodes ) ) );
    result.setName( name );
    result.setDataGwids( gwids );
    result.setTypeOfCfgUnit( type );
    result.setRelizationTypeId( realType.id() );
    result.setRealizationOpts( realization );

    return result;
  }

  @Override
  protected void doRemove( OpcToS5DataCfgUnit aEntity ) {
    master().removeDataUnit( aEntity );
  }

  void saveCurrState( ITsGuiContext aContext ) {
    ModbusToS5CfgDocService service = aContext.get( ModbusToS5CfgDocService.class );
    service.saveCfgDoc( master() );
  }

  /**
   * Generate l2 config file *.dlmcfg
   *
   * @param aContext - app context
   */
  public void generateDlmFileFromCurrState( ITsGuiContext aContext ) {
    File pathToL2 = master().getL2Path();
    String cfgFilename = master().getCfgFilesPrefix();
    Shell shell = aContext.find( Shell.class );
    FileDialog fd = new FileDialog( shell, SWT.SAVE );
    fd.setText( STR_SELECT_FILE_SAVE_DLMCFG );
    fd.setFilterPath( pathToL2.getAbsolutePath() );
    fd.setFileName( cfgFilename );
    String[] filterExt = { ".dlmcfg" }; //$NON-NLS-1$
    fd.setFilterExtensions( filterExt );
    String selected = fd.open();
    if( selected == null ) {
      return;
    }
    try {
      ISkConnectionSupplier cs = aContext.get( ISkConnectionSupplier.class );
      TsInternalErrorRtException.checkNull( cs );
      ISkConnection conn = cs.defConn();
      TsInternalErrorRtException.checkNull( conn );
      IAvTree avTree = OpcToS5DataCfgConverter.convertToDlmCfgTree( master().dataUnits(), conn, aNodeEntity -> {
        ModbusNode nodeid = aNodeEntity.asValobj();
        return new Pair<>( nodeid.getModbusDevice().getDeviceModbusConnectionId(), nodeid.getId() );
      } );
      String TMP_DEST_FILE = "destDlmFile.tmp"; //$NON-NLS-1$
      File tmpFile = new File( TMP_DEST_FILE );
      AvTreeKeeper.KEEPER.write( new File( TMP_DEST_FILE ), avTree );

      String DLM_CONFIG_STR = "DlmConfig = "; //$NON-NLS-1$
      PinsConfigFileFormatter.format( TMP_DEST_FILE, selected, DLM_CONFIG_STR, true );

      // remove tmp file
      tmpFile.delete();
      TsDialogUtils.info( shell, MSG_CONFIG_FILE_DLMCFG_CREATED, selected );
    }
    catch( Exception e ) {
      LoggerUtils.errorLogger().error( e );
      TsDialogUtils.error( shell, e );
    }
  }

  /**
   * Generate config file *.devcfg
   *
   * @param aContext - app context {@link ITsGuiContext}
   */
  public void generateDevFileFromCurrState( ITsGuiContext aContext ) {
    File pathToL2 = master().getL2Path();
    String cfgFilename = master().getCfgFilesPrefix();
    Shell shell = aContext.find( Shell.class );
    FileDialog fd = new FileDialog( shell, SWT.SAVE );
    fd.setText( STR_SELECT_FILE_SAVE_DEVCFG );
    fd.setFilterPath( pathToL2.getAbsolutePath() );
    fd.setFileName( cfgFilename );
    String[] filterExt = { ".devcfg" }; //$NON-NLS-1$
    fd.setFilterExtensions( filterExt );
    String selected = fd.open();
    if( selected == null ) {
      return;
    }
    try {
      ISkConnectionSupplier cs = aContext.get( ISkConnectionSupplier.class );
      TsInternalErrorRtException.checkNull( cs );
      ISkConnection conn = cs.defConn();
      TsInternalErrorRtException.checkNull( conn );
      IAvTree avTree = ModbusCfgDocConverter.convertToDevCfgTree( aContext, master() );
      String TMP_DEST_FILE = "destDevFile.tmp"; //$NON-NLS-1$
      File tmpFile = new File( TMP_DEST_FILE );
      AvTreeKeeper.KEEPER.write( tmpFile, avTree );

      String DEV_CONFIG_STR = "DeviceConfig = "; //$NON-NLS-1$
      PinsConfigFileFormatter.format( TMP_DEST_FILE, selected, DEV_CONFIG_STR, true );
      // remove tmp file
      tmpFile.delete();
      TsDialogUtils.info( shell, MSG_CONFIG_FILE_DEVCFG_CREATED, selected );
    }
    catch( Exception e ) {
      LoggerUtils.errorLogger().error( e );
      TsDialogUtils.error( shell, e );
    }
  }

  /**
   * @param aCfgUnit config unit
   */
  public void addCfgUnit( OpcToS5DataCfgUnit aCfgUnit ) {
    master().addDataUnit( aCfgUnit );
  }

  /**
   * @param aCfgUnits config units
   */
  public void addCfgUnits( IList<OpcToS5DataCfgUnit> aCfgUnits ) {
    master().addDataUnits( aCfgUnits );
  }
}
