package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import org.toxsoft.core.tslib.bricks.filter.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;

/**
 * Filter list of {@link OpcToS5DataCfgUnit} by Modbus Device connection options.
 *
 * @author dima
 */
public class FilterByModbusDevice
    implements ITsFilter<OpcToS5DataCfgUnit> {

  private final String modbusConnectionId;

  /**
   * Construct filter by Modbus Device
   *
   * @param aModbusDevice - Modbus Device {@link ModbusDevice}
   */
  public FilterByModbusDevice( ModbusDevice aModbusDevice ) {
    modbusConnectionId = aModbusDevice.getDeviceConnectionId();
  }

  @Override
  public boolean accept( OpcToS5DataCfgUnit aUnit ) {
    // get modbus device
    IList<ModbusNode> nodes = OpcUaUtils.convertToNodesList( aUnit.getDataNodes2() );
    if( nodes.isEmpty() ) {
      return false;
    }
    ModbusNode firstNode = nodes.first();
    ModbusDevice modbusDevice = firstNode.getModbusDevice();
    String connectionId = modbusDevice.getDeviceConnectionId();
    return modbusConnectionId.equals( connectionId );
  }

}
