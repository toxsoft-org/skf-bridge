package org.toxsoft.skf.brifge.cfg.s7.gui.utils;

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
import org.toxsoft.skf.bridge.cfg.modbus.gui.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skf.brifge.cfg.s7.gui.km5.*;
import org.toxsoft.skf.brifge.cfg.s7.gui.type.*;

/**
 * Service for SkIDE S7 bridge configuration editor
 *
 * @author dima
 */
public class S7ToS5CfgDocService
    extends L2ToS5CfgDocService<S7ToS5CfgDoc> {

  private final static String SECT_ID_CFG_DOCS = "S7.bridge.cfg.doc"; //$NON-NLS-1$

  private final static String SECT_ID_ADDRESSES = "S7.bridge.ip.addresses"; //$NON-NLS-1$

  private final static String SECT_ID_S7_DEVICES = "S7.bridge.devices"; //$NON-NLS-1$

  /**
   * cached list of ip addresses
   */
  private IStridablesListEdit<TCPAddress> ipAddrsCopy = new StridablesList<>();

  /**
   * cached list of S7 Devices
   */
  private IStridablesListEdit<GuiS7Device> devicesCopy = new StridablesList<>();

  /**
   * @param aContext - app context {@link ITsGuiContext}
   */
  public S7ToS5CfgDocService( ITsGuiContext aContext ) {
    super( aContext, SECT_ID_CFG_DOCS, S7ToS5CfgDoc.KEEPER );

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
        LoggerUtils.error( e );
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
      LoggerUtils.error( e );
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

  /**
   * @param aS7Device сохранить устройство {@link GuiS7Device}
   */
  public void saveS7Device( GuiS7Device aS7Device ) {
    ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IStridablesListEdit<GuiS7Device> stored = new StridablesList<>();
    try {
      stored = new StridablesList<>( storage.readColl( SECT_ID_S7_DEVICES, GuiS7Device.KEEPER ) );
    }
    catch( Exception e ) {
      LoggerUtils.error( e );
      Shell shell = context.find( Shell.class );
      TsDialogUtils.error( shell, e );
    }
    // FIXME разобраться зачем тут два телодвижения ( stored & ipAddrsCopy )
    if( stored.hasKey( aS7Device.id() ) ) {
      stored.replace( aS7Device.id(), aS7Device );
    }
    else {
      stored.add( aS7Device );
    }

    storage.writeColl( SECT_ID_S7_DEVICES, stored, GuiS7Device.KEEPER );

    if( devicesCopy.hasKey( aS7Device.id() ) ) {
      devicesCopy.replace( aS7Device.id(), aS7Device );
    }
    else {
      devicesCopy.add( aS7Device );
    }
  }

  /**
   * @param aS7Device удалить устройство {@link GuiS7Device}
   */
  public void removeS7Device( GuiS7Device aS7Device ) {
    ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IStridablesListEdit<GuiS7Device> stored =
        new StridablesList<>( storage.readColl( SECT_ID_S7_DEVICES, GuiS7Device.KEEPER ) );
    if( stored.hasKey( aS7Device.id() ) ) {
      stored.removeById( aS7Device.id() );
    }

    storage.writeColl( SECT_ID_S7_DEVICES, stored, GuiS7Device.KEEPER );

    if( devicesCopy.hasKey( aS7Device.id() ) ) {
      devicesCopy.removeById( aS7Device.id() );
    }
  }

  /**
   * @return получить список {@link GuiS7Device}
   */
  public IList<GuiS7Device> getS7Devices() {
    if( devicesCopy.size() == 0 ) {
      ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
      TsInternalErrorRtException.checkNull( workroom );
      IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
      try {
        devicesCopy = new StridablesList<>( storage.readColl( SECT_ID_S7_DEVICES, GuiS7Device.KEEPER ) );
      }
      catch( Exception e ) {
        LoggerUtils.error( e );
        Shell shell = context.find( Shell.class );
        TsDialogUtils.error( shell, e );
      }
    }
    return devicesCopy;
  }

}
