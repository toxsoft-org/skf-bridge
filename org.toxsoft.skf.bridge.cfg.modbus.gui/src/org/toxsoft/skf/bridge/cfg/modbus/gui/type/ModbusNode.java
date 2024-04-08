package org.toxsoft.skf.bridge.cfg.modbus.gui.type;

/**
 * Описание одного участка адресного пространства Modbus (аналог NodeId в opc ua)
 *
 * @author max
 */
public class ModbusNode {

  private int register;

  private int wordsCount;

  private ERequestType requestType;

  public ModbusNode( int aRegister, int aWordsCount, ERequestType aRequestType ) {
    super();
    register = aRegister;
    wordsCount = aWordsCount;
    requestType = aRequestType;
  }

  public String getId() {
    return requestType.name() + "_" + register + "_" + wordsCount;
  }

  public int getRegister() {
    return register;
  }

  public void setRegister( int aRegister ) {
    register = aRegister;
  }

  public int getWordsCount() {
    return wordsCount;
  }

  public void setWordsCount( int aWordsCount ) {
    wordsCount = aWordsCount;
  }

  public ERequestType getRequestType() {
    return requestType;
  }

  public void setRequestType( ERequestType aRequestType ) {
    requestType = aRequestType;
  }

}
