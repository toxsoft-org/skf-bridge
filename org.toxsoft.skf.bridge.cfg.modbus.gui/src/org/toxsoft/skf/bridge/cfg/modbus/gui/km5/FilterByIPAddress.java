package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import java.net.*;

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

  private final InetAddress selIP;

  /**
   * Construct filter by IP address
   *
   * @param aTCPAddress - IP address {@link TCPAddress}
   */
  public FilterByIPAddress( TCPAddress aTCPAddress ) {
    selIP = aTCPAddress.getIP();
  }

  @Override
  public boolean accept( OpcToS5DataCfgUnit aUnit ) {
    // get IP address
    IList<ModbusNode> nodes = OpcUaUtils.convertToNodesList( aUnit.getDataNodes2() );
    if( nodes.isEmpty() ) {
      return false;
    }
    ModbusNode firstNode = nodes.first();
    TCPAddress address = firstNode.getAddress();
    InetAddress ip = address.getIP();
    return selIP.equals( ip );
  }

}
