package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;

import java.io.*;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
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
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

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
    String strid = "opctos5.bridge.cfg.unit.id" + System.currentTimeMillis();// OpcToS5DataCfgUnitM5Model.STRID.getFieldValue( //$NON-NLS-1$
    ECfgUnitType type = m().TYPE.getFieldValue( aValues ).asValobj();
    // aValues ).asString();
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
    return result;
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

    return result;
  }

  @Override
  protected void doRemove( OpcToS5DataCfgUnit aEntity ) {
    master().removeDataUnit( aEntity );
  }

  void saveCurrState( ITsGuiContext aContext ) {
    OpcToS5DataCfgDocService service = aContext.get( OpcToS5DataCfgDocService.class );
    service.saveCfgDoc( master() );
  }

  public void generateFileFromCurrState( ITsGuiContext aContext ) {
    Shell shell = aContext.find( Shell.class );
    FileDialog fd = new FileDialog( shell, SWT.SAVE );
    fd.setText( STR_SELECT_FILE_SAVE_DLMCFG );
    fd.setFilterPath( TsLibUtils.EMPTY_STRING );
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
        NodeId nodeid = aNodeEntity.asValobj();
        return nodeid.toParseableString();
      } );
      String TMP_DEST_FILE = "destDlmFile.tmp"; //$NON-NLS-1$
      AvTreeKeeper.KEEPER.write( new File( TMP_DEST_FILE ), avTree );

      String DLM_CONFIG_STR = "DlmConfig = "; //$NON-NLS-1$
      PinsConfigFileFormatter.format( TMP_DEST_FILE, selected, DLM_CONFIG_STR );

      TsDialogUtils.info( shell, MSG_CONFIG_FILE_DLMCFG_CREATED, selected );
    }
    catch( Exception e ) {
      LoggerUtils.errorLogger().error( e );
      TsDialogUtils.error( shell, e );
    }
  }

  /**
   * @param aResult config unit
   */
  public void addCfgUnit( OpcToS5DataCfgUnit aCfgUnit ) {
    master().addDataUnit( aCfgUnit );
  }

  /**
   * @param aResult config unit
   */
  public void addCfgUnits( IList<OpcToS5DataCfgUnit> aCfgUnits ) {
    master().addDataUnits( aCfgUnits );
  }
}
