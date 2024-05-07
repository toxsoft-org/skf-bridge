package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.km5.ISkResources.*;

import java.net.*;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.utils.*;

/**
 * LM for {@link TCPAddress } entity
 *
 * @author dima
 */
public class TCPAddressM5LifecycleManager
    extends M5LifecycleManager<TCPAddress, ModbusToS5CfgDocService> {

  /**
   * @param aModel - m5 model
   * @param aDocService - service of that functional
   */
  public TCPAddressM5LifecycleManager( IM5Model<TCPAddress> aModel, ModbusToS5CfgDocService aDocService ) {
    super( aModel, true, true, true, true, aDocService );
  }

  @Override
  protected ValidationResult doBeforeCreate( IM5Bunch<TCPAddress> aValues ) {
    String id = aValues.getAsAv( FID_ID ).asString();
    if( !StridUtils.isValidIdPath( id ) ) {
      return ValidationResult.error( MSG_ERR_NOT_IDPATH );
    }
    String addrStr = aValues.getAsAv( TCPAddressM5Model.FID_IP_ADDRESS ).asString();
    try {
      InetAddress.getByName( addrStr );
    }
    catch( @SuppressWarnings( "unused" ) UnknownHostException ex ) {
      return ValidationResult.error( MSG_ERR_INVALID_IP_ADDRESS );
    }
    return ValidationResult.SUCCESS;
  }

  @Override
  protected TCPAddress doCreate( IM5Bunch<TCPAddress> aValues ) {
    String id = aValues.getAsAv( FID_ID ).asString();
    String name = aValues.getAsAv( FID_NAME ).asString();
    String addrStr = aValues.getAsAv( TCPAddressM5Model.FID_IP_ADDRESS ).asString();
    InetAddress addr = null;
    try {
      addr = InetAddress.getByName( addrStr );
    }
    catch( UnknownHostException ex ) {
      LoggerUtils.errorLogger().error( ex );
    }
    int portNum = aValues.getAsAv( TCPAddressM5Model.FID_PORT_NUM ).asInt();
    TCPAddress newAddr = new TCPAddress( id, name, addr, portNum );
    master().saveIPAddress( newAddr );

    return newAddr;
  }

  @Override
  protected ValidationResult doBeforeEdit( IM5Bunch<TCPAddress> aValues ) {
    String addrStr = aValues.getAsAv( TCPAddressM5Model.FID_IP_ADDRESS ).asString();
    try {
      InetAddress.getByName( addrStr );
    }
    catch( @SuppressWarnings( "unused" ) UnknownHostException ex ) {
      return ValidationResult.error( MSG_ERR_INVALID_IP_ADDRESS );
    }

    return ValidationResult.SUCCESS;
  }

  @Override
  protected TCPAddress doEdit( IM5Bunch<TCPAddress> aValues ) {
    String name = aValues.getAsAv( FID_NAME ).asString();
    String addrStr = aValues.getAsAv( TCPAddressM5Model.FID_IP_ADDRESS ).asString();
    InetAddress addr = null;
    try {
      addr = InetAddress.getByName( addrStr );
    }
    catch( UnknownHostException ex ) {
      LoggerUtils.errorLogger().error( ex );
    }
    int portNum = aValues.getAsAv( TCPAddressM5Model.FID_PORT_NUM ).asInt();

    TCPAddress originalAddress = aValues.originalEntity();

    originalAddress.setName( name );
    originalAddress.setIP( addr );
    originalAddress.setPort( portNum );

    master().saveIPAddress( originalAddress );

    return originalAddress;
  }

  @Override
  protected void doRemove( TCPAddress aEntity ) {
    master().removeAddress( aEntity );
  }

  @Override
  protected IList<TCPAddress> doListEntities() {
    return master().getIPAddresses();
  }
}
