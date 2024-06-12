package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import org.toxsoft.core.tslib.bricks.filter.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;

/**
 * Filter list of {@link OpcToS5DataCfgUnit} by IP address
 *
 * @author dima
 */
public class FilterByIPAddress
    implements ITsFilter<OpcToS5DataCfgUnit> {

  private final String selIP;

  /**
   * Construct filter by IP address
   *
   * @param aTCPAddress - IP address {@link TCPAddress}
   */
  public FilterByIPAddress( ModbusDevice aTCPAddress ) {
    selIP = aTCPAddress.getDeviceConnectionId();
  }

  @Override
  public boolean accept( OpcToS5DataCfgUnit aUnit ) {
    // get IP address
    IList<ModbusNode> nodes = OpcUaUtils.convertToNodesList( aUnit.getDataNodes2() );
    if( nodes.isEmpty() ) {
      return false;
    }
    ModbusNode firstNode = nodes.first();
    ModbusDevice address = firstNode.getAddress();
    String ip = address.getDeviceConnectionId();
    return selIP.equals( ip );
  }

}
