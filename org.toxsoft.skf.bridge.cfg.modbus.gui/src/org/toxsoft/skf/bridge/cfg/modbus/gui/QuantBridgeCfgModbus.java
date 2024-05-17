package org.toxsoft.skf.bridge.cfg.modbus.gui;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.valed.impl.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.panels.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.uskat.core.gui.km5.*;

/**
 * The library quant.
 *
 * @author max
 */
public class QuantBridgeCfgModbus
    extends AbstractQuant {

  /**
   * Constructor.
   */
  public QuantBridgeCfgModbus() {
    super( QuantBridgeCfgModbus.class.getSimpleName() );

    TsValobjUtils.registerKeeperIfNone( ECfgUnitType.KEEPER_ID, ECfgUnitType.KEEPER );
    TsValobjUtils.registerKeeperIfNone( ModbusNodeKeeper.KEEPER_ID, ModbusNodeKeeper.KEEPER );
    TsValobjUtils.registerKeeperIfNone( ERequestType.KEEPER_ID, ERequestType.KEEPER );
    // TsValobjUtils.registerKeeperIfNone( ETimeUnit.KEEPER_ID, ETimeUnit.KEEPER );
    // TsValobjUtils.registerKeeperIfNone( VtGraphParamsList.KEEPER_ID, VtGraphParamsList.KEEPER );

    KM5Utils.registerContributorCreator( KM5ModbusContributor.CREATOR );
  }

  @Override
  protected void doInitApp( IEclipseContext aWinContext ) {

    // load configs
    // ITsWorkroom workroom = aWinContext.get( ITsWorkroom.class );
    // TsInternalErrorRtException.checkNull( workroom );
    // IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    // IList<IConnectionConfig> ll = storage.readColl( SECTID_CONNECTION_CONFIGS, ConnectionConfig.KEEPER );
    // storage.writeColl( SECTID_CONNECTION_CONFIGS, ccService.listConfigs(), ConnectionConfig.KEEPER );

    // IVtWsCoreConstants.init( aWinContext );

    // SkCoreUtils.registerSkServiceCreator( VtReportTemplateService.CREATOR );
    // SkCoreUtils.registerSkServiceCreator( VtGraphTemplateService.CREATOR );

    // ISkConnectionSupplier connSup = aWinContext.get( ISkConnectionSupplier.class );
    // ISkConnection conn = connSup.defConn();
    //
    // // регистрируем свои m5 модели
    // IM5Domain m5 = aWinContext.get( IM5Domain.class );
    // m5.addModel( new VtReportParamM5Model() );
    // m5.addModel( new VtReportTemplateM5Model( conn ) );
    // m5.addModel( new VtGraphParamM5Model() );
    // m5.addModel( new VtGraphTemplateM5Model( conn ) );

    // ValedControlFactoriesRegistry vcReg = aWinContext.get( ValedControlFactoriesRegistry.class );
    // vcReg.registerFactory( ValedGwidEditor.FACTORY );
    // vcReg.registerFactory( ValedAvValobjGwidEditor.FACTORY );
    // vcReg.registerFactory( ValedSkidEditor.FACTORY );
    // vcReg.registerFactory( ValedAvValobjSkidEditor.FACTORY );
  }

  @Override
  protected void doInitWin( IEclipseContext aWinContext ) {
    IBridgeCfgModbusConstants.init( aWinContext );

    // SkCoreUtils.registerSkServiceCreator( VtReportTemplateService.CREATOR );
    // SkCoreUtils.registerSkServiceCreator( VtGraphTemplateService.CREATOR );

    // ISkConnectionSupplier connSup = aWinContext.get( ISkConnectionSupplier.class );
    // ISkConnection conn = connSup.defConn();
    //
    // // регистрируем свои m5 модели
    // IM5Domain m5 = aWinContext.get( IM5Domain.class );
    // m5.addModel( new VtReportParamM5Model() );

    ValedControlFactoriesRegistry vcReg = aWinContext.get( ValedControlFactoriesRegistry.class );
    vcReg.registerFactory( ValedTCPAddressEditor.FACTORY );
    vcReg.registerFactory( ValedAvValobjTCPAddressEditor.FACTORY );
  }

}
