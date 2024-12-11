package org.toxsoft.skf.bridge.cfg.modbus.gui.type;

import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * Modbus device connection options (tcp device: ip, port ; rtu device: port name, rate, adress)
 *
 * @author max
 */
public class ModbusDevice
    extends Stridable {

  /**
   * Index of tcp device (value false shows that device is rtu)
   */
  private boolean isTcp = true;

  /**
   * Values of device connection options.
   */
  private IOptionSetEdit deviceOptValues;

  /**
   * Value-object keeper identifier.
   */
  public static final String KEEPER_ID = "ModbusDevice"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public final static IEntityKeeper<ModbusDevice> KEEPER =
      new AbstractEntityKeeper<>( ModbusDevice.class, EEncloseMode.NOT_IN_PARENTHESES, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, ModbusDevice aEntity ) {
          aSw.incNewLine();

          // id, name, description
          aSw.writeAsIs( aEntity.id() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          aSw.writeQuotedString( aEntity.nmName() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          // index
          aSw.writeBoolean( aEntity.isTcp() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          // options
          OptionSetKeeper.KEEPER.write( aSw, aEntity.getDeviceOptValues() );
          aSw.writeEol();
        }

        @Override
        protected ModbusDevice doRead( IStrioReader aSr ) {
          // id, name, description
          String id = aSr.readIdPath();
          aSr.ensureSeparatorChar();
          String name = aSr.readQuotedString();
          aSr.ensureSeparatorChar();
          boolean ipTcp = aSr.readBoolean();
          aSr.ensureSeparatorChar();
          IOptionSet deviceOptValues = OptionSetKeeper.KEEPER.read( aSr );
          return new ModbusDevice( id, name, ipTcp, deviceOptValues );
        }
      };

  /**
   * Default modbus device
   */
  public static final ModbusDevice DEFAULT_DEVICE = new ModbusDevice();

  /**
   * Constructor
   *
   * @param aId - id of entity
   * @param aName - human name of address
   * @param aIsTcp - index of tcp device (or rtu)
   * @param aDeviceOptValues - values of connection options
   */
  public ModbusDevice( String aId, String aName, boolean aIsTcp, IOptionSet aDeviceOptValues ) {
    super( aId, aName, TsLibUtils.EMPTY_STRING );
    TsNullArgumentRtException.checkNull( aDeviceOptValues );
    isTcp = aIsTcp;
    deviceOptValues = new OptionSet( aDeviceOptValues );
  }

  @SuppressWarnings( "nls" )
  private ModbusDevice() {
    super( "default.modbus.device.id", "default modbus device", TsLibUtils.EMPTY_STRING );
    isTcp = true;
    setDeviceOptValues( ModbusDeviceOptionsUtils.getParamDefaultValuess( isTcp ) );
  }

  /**
   * Sets {@link #nmName()}.
   *
   * @param aName String - short name
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  @Override
  public void setName( String aName ) {
    TsNullArgumentRtException.checkNull( aName );
    super.setName( aName );
  }

  public boolean isTcp() {
    return isTcp;
  }

  public IOptionSet getDeviceOptValues() {
    return deviceOptValues;
  }

  public void setDeviceOptValues( IOptionSet aDeviceOptValues ) {
    deviceOptValues = new OptionSet( aDeviceOptValues );
  }

  public void setTcp( boolean aIsTcp ) {
    isTcp = aIsTcp;
  }

  /**
   * Returns identifier of modbus connection - can be the same for several devices
   *
   * @return String - identifier of modbus connection - can be the same for several devices (tcp-ipAddress-port,
   *         rtu-portName)
   */
  public String getDeviceModbusConnectionId() {
    StringBuilder result = new StringBuilder();
    result.append( isTcp ? "tcp" : "rtu" );

    if( isTcp ) {
      String ipAddress = ModbusDeviceOptionsUtils.OP_TCP_IP_ADDRESS.getValue( getDeviceOptValues() ).asString();
      int port = ModbusDeviceOptionsUtils.OP_TCP_PORT.getValue( getDeviceOptValues() ).asInt();

      result.append( "_" );
      result.append( ipAddress );
      result.append( "_" );
      result.append( port );
    }
    else

    {
      String portName = ModbusDeviceOptionsUtils.OP_RTU_PORT_NAME.getValue( getDeviceOptValues() ).asString();
      result.append( "_" );
      result.append( portName );
    }
    return result.toString().replace( ".", "_" );
  }

  /**
   * Returns device identifier forming from its connection options
   *
   * @return String - device identifier forming from its connection options (tcp-ipAddress-port, rtu-portName-address)
   */
  public String getDeviceId() {
    StringBuilder result = new StringBuilder();
    result.append( isTcp ? "tcp" : "rtu" );

    IList<IDataDef> paramDefenitions = ModbusDeviceOptionsUtils.getParamDefenitions( isTcp );

    for( IDataDef dDef : paramDefenitions ) {
      result.append( "_" );
      result.append( dDef.id() );
      result.append( "_" );
      result.append( dDef.getValue( deviceOptValues ).toString() );
    }
    return result.toString().replace( ".", "_" );
  }

  @Override
  public String toString() {
    return nmName(); // just to dispalay without id
  }

}
