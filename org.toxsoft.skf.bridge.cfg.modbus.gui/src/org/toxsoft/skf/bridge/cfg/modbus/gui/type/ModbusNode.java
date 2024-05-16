package org.toxsoft.skf.bridge.cfg.modbus.gui.type;

import org.toxsoft.core.tslib.utils.*;

/**
 * Описание одного участка адресного пространства Modbus (аналог NodeId в OPC UA)
 *
 * @author max
 * @author dima
 */
public class ModbusNode {

  private TCPAddress address = TCPAddress.NONE;

  private int register;

  private int wordsCount;

  private ERequestType requestType;

  private boolean isOutput = false;

  /**
   * @param aRegister - modbus register
   * @param aWordsCount - count of words
   * @param aRequestType - request type {@link ERequestType}
   */
  public ModbusNode( int aRegister, int aWordsCount, ERequestType aRequestType ) {
    super();
    register = aRegister;
    wordsCount = aWordsCount;
    requestType = aRequestType;
  }

  /**
   * @param aAddress - TCP/IP address {@link TCPAddress}
   * @param aRegister - modbus register
   * @param aWordsCount - count of words
   * @param aRequestType - request type {@link ERequestType}
   */
  public ModbusNode( TCPAddress aAddress, int aRegister, int aWordsCount, ERequestType aRequestType ) {
    super();
    address = aAddress;
    register = aRegister;
    wordsCount = aWordsCount;
    requestType = aRequestType;
  }

  /**
   * @return id
   */
  @SuppressWarnings( "nls" )
  public String getId() {
    // old version
    // return requestType.name() + "_" + register + "_" + wordsCount + (isOutput ? "_output" : TsLibUtils.EMPTY_STRING);
    String flattenIP = address.getIP().getHostAddress().replace( '.', '_' );
    String ID_FMT_STR = "modbus_node_id_%s_%d_%s_%d_%d";
    String retVal = String.format( ID_FMT_STR, flattenIP, Integer.valueOf( address.getPort() ), requestType.name(),
        Integer.valueOf( register ), Integer.valueOf( wordsCount ) );
    return retVal + (isOutput ? "_output" : TsLibUtils.EMPTY_STRING);
  }

  /**
   * @return address {@link TCPAddress}
   */
  public TCPAddress getAddress() {
    return address;
  }

  /**
   * Set new address
   *
   * @param aAddress - address {@link TCPAddress}
   */
  public void setAddress( TCPAddress aAddress ) {
    address = aAddress;
  }

  /**
   * @return modbus register
   */
  public int getRegister() {
    return register;
  }

  /**
   * Set new register
   *
   * @param aRegister - modbus register
   */
  public void setRegister( int aRegister ) {
    register = aRegister;
  }

  /**
   * @return words count
   */
  public int getWordsCount() {
    return wordsCount;
  }

  /**
   * Set word count
   *
   * @param aWordsCount - word count
   */
  public void setWordsCount( int aWordsCount ) {
    wordsCount = aWordsCount;
  }

  /**
   * @return request type {@link ERequestType}
   */
  public ERequestType getRequestType() {
    return requestType;
  }

  /**
   * Set request type
   *
   * @param aRequestType - request type {@link ERequestType}
   */
  public void setRequestType( ERequestType aRequestType ) {
    requestType = aRequestType;
  }

  /**
   * @return true - node for output
   */
  public boolean isOutput() {
    return isOutput;
  }

  /**
   * @param aIsOutput - set output flag
   */
  public void setOutput( boolean aIsOutput ) {
    isOutput = aIsOutput;
  }

  /**
   * @return string
   */
  @SuppressWarnings( "boxing" )
  @Override
  public String toString() {
    return String.format( "%s : %d : %d : %s", address.getIP().getHostAddress(), register, wordsCount, //$NON-NLS-1$
        requestType.name() );
  }

}
