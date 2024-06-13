package org.toxsoft.skf.bridge.cfg.modbus.gui.panels;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.panels.ISkResources.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.utils.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * Panel to browser list of Modbus Devices and select one of it .
 *
 * @author dima
 */
public class PanelModbusDeviceSelector
    extends AbstractTsDialogPanel<ModbusDevice, ITsGuiContext> {

  private IM5CollectionPanel<ModbusDevice> tcpAddresesPanel;

  /**
   * Constructor
   *
   * @param aParent Composite - parent component.
   * @param aEnviroment - enviroment of run.
   */
  protected PanelModbusDeviceSelector( Composite aParent, TsDialog<ModbusDevice, ITsGuiContext> aEnviroment ) {
    super( aParent, aEnviroment );
    this.setLayout( new BorderLayout() );

    ISkConnectionSupplier connSup = tsContext().get( ISkConnectionSupplier.class );
    ISkConnection conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    // Model of IP address
    IM5Model<ModbusDevice> ipAddresessModel = m5.getModel( ModbusDeviceM5Model.MODEL_ID, ModbusDevice.class );

    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    ctx.params().addAll( tsContext().params() );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    // не добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_FALSE );

    // обнуляем действие по умолчанию на dbl click
    IMultiPaneComponentConstants.OPDEF_DBLCLICK_ACTION_ID.setValue( ctx.params(), AvUtils.AV_STR_EMPTY );
    ModbusToS5CfgDocService docService = new ModbusToS5CfgDocService( tsContext() );
    ModbusDeviceM5LifecycleManager lm = new ModbusDeviceM5LifecycleManager( ipAddresessModel, docService );

    MultiPaneComponentModown<ModbusDevice> componentModown =
        new MultiPaneComponentModown<>( ctx, ipAddresessModel, lm.itemsProvider(), lm );

    tcpAddresesPanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );
    tcpAddresesPanel.createControl( this );
  }

  // ------------------------------------------------------------------------------------
  // Реализация интерфейса
  //

  @Override
  protected void doSetDataRecord( ModbusDevice aInitAddr ) {
    if( aInitAddr != null ) {
      tcpAddresesPanel.setSelectedItem( aInitAddr );
    }
  }

  @Override
  protected ModbusDevice doGetDataRecord() {
    return tcpAddresesPanel.selectedItem();
  }

  // ------------------------------------------------------------------------------------
  // Статические метод вызова диалога
  //

  /**
   * Вызов диалога для выбора одного произвольного Modbus Device
   *
   * @param aTsContext ITsGuiContext - соответствующий контекст
   * @param aInitDev - init Modbus Device
   * @return TCPAddress - выбранный Modbus Device или <b>null</b> в случае отказа от выбора
   */
  public static ModbusDevice selectModbusDevice( ITsGuiContext aTsContext, ModbusDevice aInitDev ) {
    ITsDialogInfo cdi = new TsDialogInfo( aTsContext, STR_MSG_SELECT_TCP_ADDR, STR_DESCR_SELECT_TCP_ADDR );

    IDialogPanelCreator<ModbusDevice, ITsGuiContext> creator = PanelModbusDeviceSelector::new;
    TsDialog<ModbusDevice, ITsGuiContext> d = new TsDialog<>( cdi, aInitDev, aTsContext, creator );
    return d.execData();
  }

}
