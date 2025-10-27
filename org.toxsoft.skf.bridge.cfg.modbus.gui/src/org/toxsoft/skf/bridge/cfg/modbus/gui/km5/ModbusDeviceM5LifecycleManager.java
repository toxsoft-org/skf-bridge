package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.l10n.ISkBridgeCfgModbusGuiSharedResources.*;

import java.net.*;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.utils.*;

/**
 * LM for {@link ModbusDevice } entity
 *
 * @author max
 */
public class ModbusDeviceM5LifecycleManager
    extends M5LifecycleManager<ModbusDevice, ModbusToS5CfgDocService> {

  private static String DEVICE_ID_TEMPL = "bridge.cfg.modbus.device.id"; //$NON-NLS-1$

  /**
   * @param aModel - m5 model
   * @param aDocService - service of that functional
   */
  public ModbusDeviceM5LifecycleManager( IM5Model<ModbusDevice> aModel, ModbusToS5CfgDocService aDocService ) {
    super( aModel, true, true, true, true, aDocService );
  }

  @Override
  protected ValidationResult doBeforeCreate( IM5Bunch<ModbusDevice> aValues ) {

    boolean isTcp = aValues.getAsAv( ModbusDeviceM5Model.FID_IS_TCP_INDEX ).asBool();
    if( isTcp ) {
      IOptionSet deviceConnParams = aValues.get( ModbusDeviceM5Model.FID_DEVICE_CONN_OPTS );
      IAtomicValue ipAddressVal = ModbusDeviceOptionsUtils.OP_TCP_IP_ADDRESS.getValue( deviceConnParams );
      if( ipAddressVal.isAssigned() ) {
        try {
          InetAddress.getByName( ipAddressVal.asString() );
        }
        catch( @SuppressWarnings( "unused" ) UnknownHostException ex ) {
          return ValidationResult.error( MSG_ERR_INVALID_IP_ADDRESS );
        }
      }
    }

    return ValidationResult.SUCCESS;
  }

  @Override
  protected ModbusDevice doCreate( IM5Bunch<ModbusDevice> aValues ) {
    String strid = generateStrid();

    String name = aValues.getAsAv( FID_NAME ).asString();
    boolean isTcp = aValues.getAsAv( ModbusDeviceM5Model.FID_IS_TCP_INDEX ).asBool();
    IOptionSet deviceConnParams = aValues.get( ModbusDeviceM5Model.FID_DEVICE_CONN_OPTS );
    ModbusDevice device = new ModbusDevice( strid, name, isTcp, deviceConnParams );

    master().saveModbusDevice( device );

    return device;
  }

  /**
   * @return generated strid for {@link ModbusDevice}
   */
  public static String generateStrid() {
    return DEVICE_ID_TEMPL + System.currentTimeMillis();
  }

  @Override
  protected ValidationResult doBeforeEdit( IM5Bunch<ModbusDevice> aValues ) {

    boolean isTcp = aValues.getAsAv( ModbusDeviceM5Model.FID_IS_TCP_INDEX ).asBool();
    if( isTcp ) {
      IOptionSet deviceConnParams = aValues.get( ModbusDeviceM5Model.FID_DEVICE_CONN_OPTS );
      IAtomicValue ipAddressVal = ModbusDeviceOptionsUtils.OP_TCP_IP_ADDRESS.getValue( deviceConnParams );
      if( ipAddressVal.isAssigned() ) {
        try {
          InetAddress.getByName( ipAddressVal.asString() );
        }
        catch( @SuppressWarnings( "unused" ) UnknownHostException ex ) {
          return ValidationResult.error( MSG_ERR_INVALID_IP_ADDRESS );
        }
      }
    }

    return ValidationResult.SUCCESS;
  }

  @Override
  protected ModbusDevice doEdit( IM5Bunch<ModbusDevice> aValues ) {
    String name = aValues.getAsAv( FID_NAME ).asString();
    boolean isTcp = aValues.getAsAv( ModbusDeviceM5Model.FID_IS_TCP_INDEX ).asBool();
    IOptionSet deviceConnParams = aValues.get( ModbusDeviceM5Model.FID_DEVICE_CONN_OPTS );

    ModbusDevice originalDevice = aValues.originalEntity();

    originalDevice.setName( name );
    originalDevice.setTcp( isTcp );
    originalDevice.setDeviceOptValues( deviceConnParams );

    master().saveModbusDevice( originalDevice );

    return originalDevice;
  }

  @Override
  protected void doRemove( ModbusDevice aEntity ) {
    master().removeModbusDevice( aEntity );
  }

  @Override
  protected IList<ModbusDevice> doListEntities() {
    return master().getModbusDevices();
  }
}
