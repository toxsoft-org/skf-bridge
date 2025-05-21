package org.toxsoft.skf.bridge.cfg.opcua.gui;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.valed.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.core.txtproj.lib.storage.*;
import org.toxsoft.core.txtproj.lib.workroom.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.uskat.core.gui.km5.*;

/**
 * The library quant.
 *
 * @author max
 */
public class QuantBridgeCfgOpcUa
    extends AbstractQuant {

  /**
   * Constructor.
   */
  public QuantBridgeCfgOpcUa() {
    super( QuantBridgeCfgOpcUa.class.getSimpleName() );
    TsValobjUtils.registerKeeperIfNone( ECfgUnitType.KEEPER_ID, ECfgUnitType.KEEPER );
    TsValobjUtils.registerKeeperIfNone( NodeIdDateKeeper.KEEPER_ID, NodeIdDateKeeper.KEEPER );
    TsValobjUtils.registerKeeperIfNone( OpcNodeInfoKeeper.KEEPER_ID, OpcNodeInfoKeeper.KEEPER );
    TsValobjUtils.registerKeeperIfNone( EOPCUATreeType.KEEPER_ID, EOPCUATreeType.KEEPER );

    KM5Utils.registerContributorCreator( KM5OpcUaContributor.CREATOR );
  }

  @Override
  protected void doInitApp( IEclipseContext aWinContext ) {

    // load configs
    ITsWorkroom workroom = aWinContext.get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
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

    ValedControlFactoriesRegistry vcReg = aWinContext.get( ValedControlFactoriesRegistry.class );
    // vcReg.registerFactory( ValedGwidEditor.FACTORY );
    // vcReg.registerFactory( ValedAvValobjGwidEditor.FACTORY );
    // vcReg.registerFactory( ValedSkidEditor.FACTORY );
    // vcReg.registerFactory( ValedAvValobjSkidEditor.FACTORY );
  }

  @Override
  protected void doInitWin( IEclipseContext aWinContext ) {
    IOpcUaServerConnCfgConstants.init( aWinContext );

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

}
