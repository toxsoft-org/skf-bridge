package org.toxsoft.skf.bridge.cfg.modbus.gui.utils;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.validator.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;

/**
 * Utility class for devcfg tree creation
 *
 * @author max
 * @author dima
 */
@SuppressWarnings( "nls" )
public class ModbusCfgDocConverter {

  private static final String PIN_NODE_ID_FORMAT = "pin.%s.def";

  private static final String SYNC_TAGS_ARRAY_ID = "tags";
  // private static final String DEVICE_DEF_ID_FORMAT = "device%s.def";
  private static final String SYNCH_PERIOD_PARAM_NAME = "period";

  private static final String CONNECTIONS_ARRAY_NAME = "connections";
  private static final String DEVICES_ARRAY_NAME     = "devices";

  private static final String MODBUS_DEVICE_ID = "opc2s5.bridge.collection.id";// "modbus.bridge.common";

  private static final String DESCRIPTION_PARAM_NAME         = "description";
  private static final String DESCRIPTION_PARAM_VAL_TEMPLATE = "modbus common apparat producer";

  private static final String ID_PARAM_NAME = "id";

  private static final String JAVA_CLASS_PARAM_NAME         = "javaClassName";
  private static final String JAVA_CLASS_PARAM_VAL_TEMPLATE =
      "org.toxsoft.l2.thd.modbus.common.CommonModbusDeviceProducer";

  private static final String TYPE_PARAM_NAME    = "type";
  private static final String TYPE_PARAM_VAL_TCP = "tcp";
  private static final String IP_PARAM_NAME      = "ip";
  private static final String PORT_PARAM_NAME    = "port";

  private static final String TYPE_PARAM_VAL_RTU          = "rtu";
  private static final String PORT_NAME_PARAM_NAME        = "port.name";
  private static final String BAUD_RATE_PARAM_NAME        = "baud.rate";
  private static final String DEV_ADDRESS_RATE_PARAM_NAME = "dev.address";

  private static final String CONNECTION_TEMP_FMT_STR = "connection.number%d.def";

  private static final String MODBUS_CFG_NODE_ID = "modbus.common.cfg";

  private static final String PIN_ID_PARAM_NAME              = "id";
  private static final String PIN_DESCR_PARAM_NAME           = "descr";
  private static final String PIN_REQUEST_TYPE_PARAM_NAME    = "request.type";
  private static final String PIN_REGISTER_PARAM_NAME        = "register";
  private static final String PIN_WORD_COUNT_PARAM_NAME      = "words.count";
  private static final String PIN_IS_OUTPUT_PARAM_NAME       = "is.output";
  private static final String PIN_TRANSLATOR_PARAM_NAME      = "translator";
  private static final String PIN_TRANSLATOR_PARAMS_PARAM_ID = "translator.params";

  private static final String PIN_DEFAULT_DISCRET_TRANSLATOR_VAL = "BOOLEAN_DISCRETS";
  private static final String PIN_DEFAULT_INTEGER_TRANSLATOR_VAL = "INTEGER_REGISTERS";
  private static final String PIN_DEFAULT_FLOAT_TRANSLATOR_VAL   = "FLOAT_REGISTERS";
  private static final String PIN_DEFAULT_BOOLEAN_TRANSLATOR_VAL = "BOOLEAN_REGISTERS";

  /**
   * Create config file *.devcfg
   *
   * @param aContext - app context
   * @param aDoc - config data for editor
   * @return tree {@link IAvTree} to store in file
   */
  public static IAvTree convertToDevCfgTree( ITsGuiContext aContext, ModbusToS5CfgDoc aDoc ) {
    // new version
    // get used IP addresses
    ModbusToS5CfgDocService docService = new ModbusToS5CfgDocService( aContext );
    IListEdit<ModbusDevice> modbusDevices = new ElemArrayList<>( docService.getModbusDevices() );

    IStringMapEdit<IListEdit<ModbusDevice>> modbusDevicesTree = new StringMap<>();

    for( ModbusDevice device : modbusDevices ) {

      if( device.isTcp() ) {
        modbusDevicesTree.put( device.id(), new ElemArrayList<>( device ) );
      }
      else {
        String portName = ModbusDeviceOptionsUtils.OP_RTU_PORT_NAME.getValue( device.getDeviceOptValues() ).asString();
        IListEdit<ModbusDevice> devicesOnPort = modbusDevicesTree.findByKey( portName );
        if( devicesOnPort == null ) {
          devicesOnPort = new ElemArrayList<>();
          modbusDevicesTree.put( portName, devicesOnPort );
        }
        devicesOnPort.add( device );
      }
    }

    // массив соединений
    AvTree connectionsMassivTree = AvTree.createArrayAvTree();
    int connNumber = 1;

    for( IListEdit<ModbusDevice> devicesOnConnect : modbusDevicesTree.values() ) {
      // массив устройств
      AvTree devicesMassivTree = AvTree.createArrayAvTree();
      for( ModbusDevice device : devicesOnConnect ) {

        // String.format( DEVICE_DEF_ID_FORMAT, String.valueOf( devieNumber++ ) ) );
        AvTree modbusDeviceTree = createDevice( aDoc, device, device.getDeviceConnectionId() + ".def" );

        devicesMassivTree.addElement( modbusDeviceTree );

      }

      StringMap<IAvTree> connectionNodes = new StringMap<>();
      connectionNodes.put( DEVICES_ARRAY_NAME, devicesMassivTree );

      IOptionSetEdit connOps = new OptionSet();

      ModbusDevice deviceToDefineConnection = devicesOnConnect.first();

      String type = deviceToDefineConnection.isTcp() ? TYPE_PARAM_VAL_TCP : TYPE_PARAM_VAL_RTU;
      connOps.setStr( TYPE_PARAM_NAME, type );
      if( deviceToDefineConnection.isTcp() ) {
        String ipAddressStr = ModbusDeviceOptionsUtils.OP_TCP_IP_ADDRESS
            .getValue( deviceToDefineConnection.getDeviceOptValues() ).asString();
        int port =
            ModbusDeviceOptionsUtils.OP_TCP_PORT.getValue( deviceToDefineConnection.getDeviceOptValues() ).asInt();

        connOps.setStr( IP_PARAM_NAME, ipAddressStr );
        connOps.setInt( PORT_PARAM_NAME, port );
      }
      else {

        String portName = ModbusDeviceOptionsUtils.OP_RTU_PORT_NAME
            .getValue( deviceToDefineConnection.getDeviceOptValues() ).asString();
        connOps.setInt( BAUD_RATE_PARAM_NAME, 9600 );
        connOps.setStr( PORT_NAME_PARAM_NAME, portName );
      }

      // id next section
      String connNodeId = String.format( CONNECTION_TEMP_FMT_STR, Integer.valueOf( connNumber++ ) );

      IAvTree singleConnection = AvTree.createSingleAvTree( connNodeId, connOps, connectionNodes );
      connectionsMassivTree.addElement( singleConnection );
    }

    // old version
    // String type = TYPE_PARAM_VAL_TEMPLATE;
    // String ipAddress = IP_PARAM_VAL_TEMPLATE;
    // String port = PORT_PARAM_VAL_TEMPLATE;
    //
    // AvTree deviceRegistersGroup = createGroup( aDoc, SYNC_TAGS_ARRAY_ID, SYNC_GROUP_NODE_ID );
    //
    // // массив устройств
    // AvTree devicesMassivTree = AvTree.createArrayAvTree();
    //
    // devicesMassivTree.addElement( deviceRegistersGroup );
    //
    // StringMap<IAvTree> devicesNodes = new StringMap<>();
    // devicesNodes.put( DEVICES_ARRAY_NAME, devicesMassivTree );
    //
    // IOptionSetEdit connection1Ops = new OptionSet();
    //
    // connection1Ops.setStr( TYPE_PARAM_NAME, type );
    // connection1Ops.setStr( IP_PARAM_NAME, ipAddress );
    // connection1Ops.setStr( PORT_PARAM_NAME, port );

    // массив соединений
    // AvTree connectionsMassivTree = AvTree.createArrayAvTree();

    // IAvTree singleConnection = AvTree.createSingleAvTree( CONNECTION_1_NODE_ID, connection1Ops, devicesNodes );
    // connectionsMassivTree.addElement( singleConnection );

    StringMap<IAvTree> nodes = new StringMap<>();
    nodes.put( CONNECTIONS_ARRAY_NAME, connectionsMassivTree );

    IOptionSetEdit opSet = new OptionSet();

    opSet.setStr( JAVA_CLASS_PARAM_NAME, JAVA_CLASS_PARAM_VAL_TEMPLATE );
    opSet.setStr( ID_PARAM_NAME, MODBUS_DEVICE_ID );
    opSet.setStr( DESCRIPTION_PARAM_NAME, DESCRIPTION_PARAM_VAL_TEMPLATE );
    opSet.setInt( SYNCH_PERIOD_PARAM_NAME, 1000 );

    IAvTree tree = AvTree.createSingleAvTree( MODBUS_CFG_NODE_ID, opSet, nodes );

    return tree;
  }

  private static AvTree createDevice( ModbusToS5CfgDoc aDoc, ModbusDevice device, String aDeviceNodeId ) {

    // массив тегов группы
    AvTree tagsMassivTree = AvTree.createArrayAvTree();

    aDoc.ensureNodesCfgs();
    IList<ModbusNode> cfgNodes = aDoc.getNodesCfgs();

    for( ModbusNode tagData : cfgNodes ) {
      if( isSameConnection( tagData.getModbusDevice(), device ) ) {
        IAvTree tag = createTag( tagData );
        tagsMassivTree.addElement( tag );
      }
    }

    StringMap<IAvTree> nodes = new StringMap<>();
    nodes.put( SYNC_TAGS_ARRAY_ID, tagsMassivTree );

    int devAddress =
        device.isTcp() ? 1 : ModbusDeviceOptionsUtils.OP_RTU_ADDRESS.getValue( device.getDeviceOptValues() ).asInt();

    IOptionSetEdit pinOpSet1 = new OptionSet();
    pinOpSet1.setInt( DEV_ADDRESS_RATE_PARAM_NAME, devAddress );

    AvTree groupTree = AvTree.createSingleAvTree( aDeviceNodeId, pinOpSet1, nodes );
    return groupTree;
  }

  private static IAvTree createTag( ModbusNode aData ) {

    IOptionSetEdit pinOpSet1 = new OptionSet();

    // id="do1", #может генерироваться автоматически, используется в dlm
    // descr="do1", #вспомогательное описание (чтобы знать откуда ноги растут)
    // request.type = "DO", #тип запроса
    // register = 0, #номер регистра
    // words.count = 1, #количество слов
    // is.output = true #транслятор

    pinOpSet1.setStr( PIN_ID_PARAM_NAME, aData.getId() );
    pinOpSet1.setStr( PIN_DESCR_PARAM_NAME, aData.getId() );
    pinOpSet1.setStr( PIN_REQUEST_TYPE_PARAM_NAME, aData.getRequestType().name() );
    pinOpSet1.setInt( PIN_REGISTER_PARAM_NAME, aData.getRegister() );
    pinOpSet1.setInt( PIN_WORD_COUNT_PARAM_NAME, aData.getWordsCount() );
    if( aData.isOutput() ) {
      pinOpSet1.setBool( PIN_IS_OUTPUT_PARAM_NAME, aData.isOutput() );
    }
    else {
      pinOpSet1.setStr( PIN_TRANSLATOR_PARAM_NAME, getTranslator( aData ) );
    }
    if( aData.getParams() != null && aData.getParams().length() > 0 ) {
      pinOpSet1.setStr( PIN_TRANSLATOR_PARAMS_PARAM_ID, aData.getParams() );
    }

    IAvTree pinTree1 = null;
    try {
      pinTree1 =
          AvTree.createSingleAvTree( String.format( PIN_NODE_ID_FORMAT, aData.getId() ), pinOpSet1, IStringMap.EMPTY );
    }
    catch( TsValidationFailedRtException e ) {
      System.out.println( aData.getId() );
      throw e;
    }
    return pinTree1;
  }

  private static String getTranslator( ModbusNode aData ) {
    if( aData.getRequestType().isDiscret() ) {
      return PIN_DEFAULT_DISCRET_TRANSLATOR_VAL;

    }

    switch( aData.getValueType() ) {
      case BOOLEAN:
        return PIN_DEFAULT_BOOLEAN_TRANSLATOR_VAL;
      case FLOATING:
        return PIN_DEFAULT_FLOAT_TRANSLATOR_VAL;
      case INTEGER:
      case NONE:
      case STRING:
      case TIMESTAMP:
      case VALOBJ:
      default:
        return PIN_DEFAULT_INTEGER_TRANSLATOR_VAL;

    }

  }

  private static boolean isSameConnection( ModbusDevice aDevice1, ModbusDevice aDevice2 ) {
    if( aDevice1.isTcp() && aDevice2.isTcp() ) {
      String ipAddress1 =
          ModbusDeviceOptionsUtils.OP_TCP_IP_ADDRESS.getValue( aDevice1.getDeviceOptValues() ).asString();
      int port1 = ModbusDeviceOptionsUtils.OP_TCP_PORT.getValue( aDevice1.getDeviceOptValues() ).asInt();

      String ipAddress2 =
          ModbusDeviceOptionsUtils.OP_TCP_IP_ADDRESS.getValue( aDevice2.getDeviceOptValues() ).asString();
      int port2 = ModbusDeviceOptionsUtils.OP_TCP_PORT.getValue( aDevice2.getDeviceOptValues() ).asInt();

      return ipAddress1.equals( ipAddress2 ) && port1 == port2;
    }

    if( !aDevice1.isTcp() && !aDevice2.isTcp() ) {
      String portName1 = ModbusDeviceOptionsUtils.OP_RTU_PORT_NAME.getValue( aDevice1.getDeviceOptValues() ).asString();
      String portName2 = ModbusDeviceOptionsUtils.OP_RTU_PORT_NAME.getValue( aDevice2.getDeviceOptValues() ).asString();
      return portName1.equals( portName2 );
    }
    return false;
  }
}
