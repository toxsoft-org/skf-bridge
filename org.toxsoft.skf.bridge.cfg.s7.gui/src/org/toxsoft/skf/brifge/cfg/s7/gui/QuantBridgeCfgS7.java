package org.toxsoft.skf.brifge.cfg.s7.gui;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.valed.impl.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.panels.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.brifge.cfg.s7.gui.type.*;

/**
 * The library quant.
 *
 * @author max
 */
public class QuantBridgeCfgS7
    extends AbstractQuant {

  /**
   * Constructor.
   */
  public QuantBridgeCfgS7() {
    super( QuantBridgeCfgS7.class.getSimpleName() );

    TsValobjUtils.registerKeeperIfNone( ECfgUnitType.KEEPER_ID, ECfgUnitType.KEEPER );
    TsValobjUtils.registerKeeperIfNone( GuiS7Device.KEEPER_ID, GuiS7Device.KEEPER );
    TsValobjUtils.registerKeeperIfNone( GuiS7DataBlock.KEEPER_ID, GuiS7DataBlock.KEEPER );
    TsValobjUtils.registerKeeperIfNone( GuiS7TagKeeper.KEEPER_ID, GuiS7TagKeeper.KEEPER );

    // KM5Utils.registerContributorCreator( KM5ModbusContributor.CREATOR );
  }

  @Override
  protected void doInitApp( IEclipseContext aWinContext ) {
    // vcReg.registerFactory( ValedAvValobjSkidEditor.FACTORY );
  }

  @Override
  protected void doInitWin( IEclipseContext aWinContext ) {

    // leave as sample of code
    // ISkConnectionSupplier connSup = aWinContext.get( ISkConnectionSupplier.class );
    // ISkConnection conn = connSup.defConn();
    //
    // // регистрируем свои m5 модели
    // IM5Domain m5 = aWinContext.get( IM5Domain.class );
    // m5.addModel( new VtReportParamM5Model() );

    ValedControlFactoriesRegistry vcReg = aWinContext.get( ValedControlFactoriesRegistry.class );
    vcReg.registerFactory( ValedModbusDeviceEditor.FACTORY );
    vcReg.registerFactory( ValedAvValobjModbusDeviceEditor.FACTORY );
  }

}
