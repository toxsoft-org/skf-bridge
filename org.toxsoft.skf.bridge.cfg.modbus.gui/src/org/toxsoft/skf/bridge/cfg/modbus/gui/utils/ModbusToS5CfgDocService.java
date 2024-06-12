package org.toxsoft.skf.bridge.cfg.modbus.gui.utils;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.core.txtproj.lib.storage.*;
import org.toxsoft.core.txtproj.lib.workroom.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;

/**
 * Service for SkIDE modbus bridge configuration editor
 *
 * @author max
 * @author dima
 */
public class ModbusToS5CfgDocService
    extends L2ToS5CfgDocService<ModbusToS5CfgDoc> {

  private final static String SECT_ID_CFG_DOCS = "modbus.bridge.cfg.doc"; //$NON-NLS-1$

  private final static String SECT_ID_ADDRESSES = "modbus.bridge.ip.addresses"; //$NON-NLS-1$

  private final static String SECT_ID_MODBUS_DEVICES = "modbus.bridge.modbus.devices"; //$NON-NLS-1$

  /**
   * cached list of ip addresses
   */
  private IStridablesListEdit<TCPAddress> ipAddrsCopy = new StridablesList<>();

  /**
   * cached list of Modbus Devices
   */
  private IStridablesListEdit<ModbusDevice> devicesCopy = new StridablesList<>();

  /**
   * @param aContext - app context {@link ITsGuiContext}
   */
  public ModbusToS5CfgDocService( ITsGuiContext aContext ) {
    super( aContext, SECT_ID_CFG_DOCS, ModbusToS5CfgDoc.KEEPER );

  }

  /**
   * @return list of IP addresses
   */
  public IList<TCPAddress> getIPAddresses() {
    if( ipAddrsCopy.size() == 0 ) {
      ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
      TsInternalErrorRtException.checkNull( workroom );
      IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
      try {
        ipAddrsCopy = new StridablesList<>( storage.readColl( SECT_ID_ADDRESSES, TCPAddress.KEEPER ) );
      }
      catch( Exception e ) {
        LoggerUtils.errorLogger().error( e );
        Shell shell = context.find( Shell.class );
        TsDialogUtils.error( shell, e );
      }
    }
    return ipAddrsCopy;
  }

  /**
   * Save new IP address
   *
   * @param aAddress - new address {@link TCPAddress}
   */
  public void saveIPAddress( TCPAddress aAddress ) {
    ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IStridablesListEdit<TCPAddress> stored = new StridablesList<>();
    try {
      stored = new StridablesList<>( storage.readColl( SECT_ID_ADDRESSES, TCPAddress.KEEPER ) );
    }
    catch( Exception e ) {
      LoggerUtils.errorLogger().error( e );
      Shell shell = context.find( Shell.class );
      TsDialogUtils.error( shell, e );
    }
    // FIXME разобраться зачем тут два телодвижения ( stored & ipAddrsCopy )
    if( stored.hasKey( aAddress.id() ) ) {
      stored.replace( aAddress.id(), aAddress );
    }
    else {
      stored.add( aAddress );
    }

    storage.writeColl( SECT_ID_ADDRESSES, stored, TCPAddress.KEEPER );

    if( ipAddrsCopy.hasKey( aAddress.id() ) ) {
      ipAddrsCopy.replace( aAddress.id(), aAddress );
    }
    else {
      ipAddrsCopy.add( aAddress );
    }
  }

  /**
   * Remove IP address
   *
   * @param aAddress - address {@link TCPAddress} to remove
   */
  public void removeAddress( TCPAddress aAddress ) {
    ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IStridablesListEdit<TCPAddress> stored =
        new StridablesList<>( storage.readColl( SECT_ID_ADDRESSES, TCPAddress.KEEPER ) );
    if( stored.hasKey( aAddress.id() ) ) {
      stored.removeById( aAddress.id() );
    }

    storage.writeColl( SECT_ID_ADDRESSES, stored, TCPAddress.KEEPER );

    if( ipAddrsCopy.hasKey( aAddress.id() ) ) {
      ipAddrsCopy.removeById( aAddress.id() );
    }
  }

  public void saveModbusDevice( ModbusDevice aModbusDevice ) {
    ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IStridablesListEdit<ModbusDevice> stored = new StridablesList<>();
    try {
      stored = new StridablesList<>( storage.readColl( SECT_ID_MODBUS_DEVICES, ModbusDevice.KEEPER ) );
    }
    catch( Exception e ) {
      LoggerUtils.errorLogger().error( e );
      Shell shell = context.find( Shell.class );
      TsDialogUtils.error( shell, e );
    }
    // FIXME разобраться зачем тут два телодвижения ( stored & ipAddrsCopy )
    if( stored.hasKey( aModbusDevice.id() ) ) {
      stored.replace( aModbusDevice.id(), aModbusDevice );
    }
    else {
      stored.add( aModbusDevice );
    }

    storage.writeColl( SECT_ID_MODBUS_DEVICES, stored, ModbusDevice.KEEPER );

    if( devicesCopy.hasKey( aModbusDevice.id() ) ) {
      devicesCopy.replace( aModbusDevice.id(), aModbusDevice );
    }
    else {
      devicesCopy.add( aModbusDevice );
    }
  }

  public void removeModbusDevice( ModbusDevice aModbusDevice ) {
    ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IStridablesListEdit<ModbusDevice> stored =
        new StridablesList<>( storage.readColl( SECT_ID_MODBUS_DEVICES, ModbusDevice.KEEPER ) );
    if( stored.hasKey( aModbusDevice.id() ) ) {
      stored.removeById( aModbusDevice.id() );
    }

    storage.writeColl( SECT_ID_MODBUS_DEVICES, stored, ModbusDevice.KEEPER );

    if( devicesCopy.hasKey( aModbusDevice.id() ) ) {
      devicesCopy.removeById( aModbusDevice.id() );
    }
  }

  public IList<ModbusDevice> getModbusDevices() {
    if( devicesCopy.size() == 0 ) {
      ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
      TsInternalErrorRtException.checkNull( workroom );
      IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
      try {
        devicesCopy = new StridablesList<>( storage.readColl( SECT_ID_MODBUS_DEVICES, ModbusDevice.KEEPER ) );
      }
      catch( Exception e ) {
        LoggerUtils.errorLogger().error( e );
        Shell shell = context.find( Shell.class );
        TsDialogUtils.error( shell, e );
      }
    }
    return devicesCopy;
  }

}
