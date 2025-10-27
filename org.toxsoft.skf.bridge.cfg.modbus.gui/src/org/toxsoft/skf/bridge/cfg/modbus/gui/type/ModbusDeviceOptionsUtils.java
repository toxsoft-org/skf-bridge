package org.toxsoft.skf.bridge.cfg.modbus.gui.type;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.impl.DataDef.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.l10n.ISkBridgeCfgModbusGuiSharedResources.*;

import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;

/**
 * Utils for modbus devices editor
 *
 * @author max
 */
public interface ModbusDeviceOptionsUtils {

  IDataDef OP_TCP_IP_ADDRESS = create( "ip.address", STRING, //$NON-NLS-1$
      TSID_NAME, STR_MDEV_TCP_IP_ADDRESS, //
      TSID_DESCRIPTION, STR_MDEV_TCP_IP_ADDRESS_D, //
      TSID_DEFAULT_VALUE, avStr( "127.0.0.1" ) //
  ); //

  IDataDef OP_TCP_PORT = create( "ip.port", INTEGER, //$NON-NLS-1$
      TSID_NAME, STR_MDEV_TCP_PORT_NO, //
      TSID_DESCRIPTION, STR_MDEV_TCP_PORT_NO_D, //
      TSID_DEFAULT_VALUE, avInt( 502 ) //
  ); //

  IDataDef OP_RTU_PORT_NAME = create( "port.name", STRING, //$NON-NLS-1$
      TSID_NAME, STR_MDEV_RTU_PORT_NAME, //
      TSID_DESCRIPTION, STR_MDEV_RTU_PORT_NAME_D, //
      TSID_DEFAULT_VALUE, avStr( "COM1" ) //
  ); //

  IDataDef OP_RTU_ADDRESS = create( "bus.address", INTEGER, //$NON-NLS-1$
      TSID_NAME, STR_MDEV_RTU_DEV_ADDR, //
      TSID_DESCRIPTION, STR_MDEV_RTU_DEV_ADDR_D, //
      TSID_DEFAULT_VALUE, avInt( 1 ) //
  ); //

  static IStridablesList<IDataDef> getParamDefenitions( boolean aIsTcp ) {
    return aIsTcp ? new StridablesList<>( OP_TCP_IP_ADDRESS, OP_TCP_PORT )
        : new StridablesList<>( OP_RTU_PORT_NAME, OP_RTU_ADDRESS );
  }

  static IOptionSet getParamDefaultValuess( boolean aIsTcp ) {
    OptionSet defaultVals = new OptionSet();

    IList<IDataDef> paramDefenitions = ModbusDeviceOptionsUtils.getParamDefenitions( aIsTcp );
    for( IDataDef dDef : paramDefenitions ) {
      dDef.setValue( defaultVals, dDef.defaultValue() );
    }
    return defaultVals;
  }
}
