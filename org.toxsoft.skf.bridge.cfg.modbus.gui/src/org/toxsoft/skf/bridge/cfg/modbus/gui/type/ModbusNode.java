package org.toxsoft.skf.bridge.cfg.modbus.gui.type;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.utils.*;

/**
 * Описание одного участка адресного пространства Modbus (аналог NodeId в OPC UA)
 *
 * @author max
 * @author dima
 */
public class ModbusNode {

  private ModbusDevice modbusDevice = ModbusDevice.DEFAULT_DEVICE;

  private int register;

  private int wordsCount;

  private EAtomicType valueType;

  private ERequestType requestType;

  private String params;

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
   * @param aModbusDevice - Modbus Device {@link ModbusDevice}
   * @param aRegister - modbus register
   * @param aWordsCount - count of words
   * @param aRequestType - request type {@link ERequestType}
   */
  public ModbusNode( ModbusDevice aModbusDevice, int aRegister, int aWordsCount, ERequestType aRequestType ) {
    this( aRegister, aWordsCount, aRequestType );
    modbusDevice = aModbusDevice;
  }

  /**
   * @param aModbusDevice - Modbus Device {@link ModbusDevice}
   * @param aRegister - modbus register
   * @param aWordsCount - count of words
   * @param aValueType - type of node value
   * @param aRequestType - request type {@link ERequestType}
   */
  public ModbusNode( ModbusDevice aModbusDevice, int aRegister, int aWordsCount, EAtomicType aValueType,
      ERequestType aRequestType ) {
    this( aModbusDevice, aRegister, aWordsCount, aRequestType );
    valueType = aValueType;
  }

  /**
   * @param aModbusDevice - Modbus Device {@link ModbusDevice}
   * @param aRegister - modbus register
   * @param aWordsCount - count of words
   * @param aValueType - type of node value
   * @param aRequestType - request type {@link ERequestType}
   * @param aParameters - параметры в строковом представлении
   */
  public ModbusNode( ModbusDevice aModbusDevice, int aRegister, int aWordsCount, EAtomicType aValueType,
      ERequestType aRequestType, String aParameters ) {
    this( aModbusDevice, aRegister, aWordsCount, aValueType, aRequestType );
    params = aParameters;
  }

  /**
   * @return id
   */
  @SuppressWarnings( "nls" )
  public String getId() {
    // old version
    // return requestType.name() + "_" + register + "_" + wordsCount + (isOutput ? "_output" : TsLibUtils.EMPTY_STRING);

    // String flattenIP = address.getIP().getHostAddress().replace( '.', '_' );
    String flattenID = modbusDevice.getDeviceConnectionId();
    String ID_FMT_STR = "modbus_node_id_%s_%s_%d_%d";
    String retVal = String.format( ID_FMT_STR, flattenID, requestType.name(), Integer.valueOf( register ),
        Integer.valueOf( wordsCount ) );
    return retVal + (isOutput ? "_output" : TsLibUtils.EMPTY_STRING);
  }

  /**
   * @return ModbusDevice - Modbus Device {@link ModbusDevice}
   */
  public ModbusDevice getModbusDevice() {
    return modbusDevice;
  }

  /**
   * Set new Modbus Device
   *
   * @param aModbusDevice - Modbus Device {@link ModbusDevice}
   */
  public void setModbusDevice( ModbusDevice aModbusDevice ) {
    modbusDevice = aModbusDevice;
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
   * Returns type of node value
   *
   * @return EAtomicType - type of node value
   */
  public EAtomicType getValueType() {
    return valueType;
  }

  /**
   * Sets type of node value
   *
   * @param aValueType - type of node value
   */
  public void setValueType( EAtomicType aValueType ) {
    valueType = aValueType;
  }

  /**
   * Returns params of node as string
   *
   * @return String - params of node as string
   */
  public String getParams() {
    return params;
  }

  /**
   * Sets params of node as string
   *
   * @param aParams - params of node as strin
   */
  public void setParams( String aParams ) {
    params = aParams;
  }

  /**
   * @return string
   */
  @SuppressWarnings( "boxing" )
  @Override
  public String toString() {
    return String.format( "%s : %d : %d : %s", modbusDevice.getDeviceConnectionId(), register, wordsCount, //$NON-NLS-1$
        requestType.name() );
  }

}
