package org.toxsoft.skf.bridge.cfg.modbus.gui.utils;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.validator.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;

public class ModbusCfgDocConverter {

  private static final String PIN_NODE_ID_FORMAT = "pin.%s.def";

  private static final String SYNC_TAGS_ARRAY_ID      = "tags";
  private static final String SYNC_GROUP_NODE_ID      = "device.def";
  private static final String SYNCH_PERIOD_PARAM_NAME = "period";

  private static final String CONNECTIONS_ARRAY_NAME = "connections";
  private static final String DEVICES_ARRAY_NAME     = "devices";

  private static final String OPC_TAG_DEVICE_UA = "modbus.bridge.common";

  private static final String DESCRIPTION_PARAM_NAME         = "description";
  private static final String DESCRIPTION_PARAM_VAL_TEMPLATE = "modbus common apparat producer";

  private static final String ID_PARAM_NAME = "id";

  private static final String JAVA_CLASS_PARAM_NAME         = "javaClassName";
  private static final String JAVA_CLASS_PARAM_VAL_TEMPLATE =
      "org.toxsoft.l2.thd.modbus.common.CommonModbusDeviceProducer";

  private static final String TYPE_PARAM_NAME = "type";
  private static final String IP_PARAM_NAME   = "ip";
  private static final String PORT_PARAM_NAME = "port";

  private static final String IP_PARAM_VAL_TEMPLATE = "localhost";

  private static final String TYPE_PARAM_VAL_TEMPLATE = "tcp";

  private static final String PORT_PARAM_VAL_TEMPLATE = "502";

  private static final String CONNECTION_1_NODE_ID = "connection1.def";

  private static final String MODBUS_CFG_NODE_ID = "modbus.common.cfg";

  private static final String PIN_ID_PARAM_NAME           = "id";
  private static final String PIN_DESCR_PARAM_NAME        = "descr";
  private static final String PIN_REQUEST_TYPE_PARAM_NAME = "request.type";
  private static final String PIN_REGISTER_PARAM_NAME     = "register";
  private static final String PIN_WORD_COUNT_PARAM_NAME   = "register";
  private static final String PIN_IS_OUTPUT_PARAM_NAME    = "is.output";
  private static final String PIN_TRANSLATOR_PARAM_NAME   = "translator";

  private static final String PIN_DEFAULT_DISCRET_TRANSLATOR_VAL =
      "org.toxsoft.l2.thd.modbus.common.translators.OneToOneDiscretTranslator";
  private static final String PIN_DEFAULT_ANALOG_TRANSLATOR_VAL  =
      "org.toxsoft.l2.thd.modbus.common.translators.AnalogTowBytesTranslator";

  public static IAvTree convertToDevCfgTree( ITsGuiContext aContext, ModbusToS5CfgDoc aDoc ) {
    String type = TYPE_PARAM_VAL_TEMPLATE;
    String ipAddress = IP_PARAM_VAL_TEMPLATE;
    String port = PORT_PARAM_VAL_TEMPLATE;

    AvTree deviceRegistersGroup = createGroup( aDoc, SYNC_TAGS_ARRAY_ID, SYNC_GROUP_NODE_ID );

    // массив устройств
    AvTree devicesMassivTree = AvTree.createArrayAvTree();

    devicesMassivTree.addElement( deviceRegistersGroup );

    StringMap<IAvTree> devicesNodes = new StringMap<>();
    devicesNodes.put( DEVICES_ARRAY_NAME, devicesMassivTree );

    IOptionSetEdit connection1Ops = new OptionSet();

    connection1Ops.setStr( TYPE_PARAM_NAME, type );
    connection1Ops.setStr( IP_PARAM_NAME, ipAddress );
    connection1Ops.setStr( PORT_PARAM_NAME, port );

    // массив соединений
    AvTree connectionsMassivTree = AvTree.createArrayAvTree();

    IAvTree singleConnection = AvTree.createSingleAvTree( CONNECTION_1_NODE_ID, connection1Ops, devicesNodes );
    connectionsMassivTree.addElement( singleConnection );

    StringMap<IAvTree> nodes = new StringMap<>();
    nodes.put( CONNECTIONS_ARRAY_NAME, connectionsMassivTree );

    IOptionSetEdit opSet = new OptionSet();

    opSet.setStr( JAVA_CLASS_PARAM_NAME, JAVA_CLASS_PARAM_VAL_TEMPLATE );
    opSet.setStr( ID_PARAM_NAME, OPC_TAG_DEVICE_UA );
    opSet.setStr( DESCRIPTION_PARAM_NAME, DESCRIPTION_PARAM_VAL_TEMPLATE );
    opSet.setInt( SYNCH_PERIOD_PARAM_NAME, 1000 );

    IAvTree tree = AvTree.createSingleAvTree( MODBUS_CFG_NODE_ID, opSet, nodes );

    return tree;
  }

  private static AvTree createGroup( ModbusToS5CfgDoc aDoc, String aArrayId, String aGroupNodeId ) {

    // массив тегов группы
    AvTree tagsMassivTree = AvTree.createArrayAvTree();

    aDoc.ensureNodesCfgs();
    IList<ModbusNode> cfgNodes = aDoc.getNodesCfgs();

    for( ModbusNode tagData : cfgNodes ) {

      IAvTree tag = createTag( tagData );

      tagsMassivTree.addElement( tag );
    }

    StringMap<IAvTree> nodes = new StringMap<>();
    nodes.put( aArrayId, tagsMassivTree );

    IOptionSetEdit pinOpSet1 = new OptionSet();

    AvTree groupTree = AvTree.createSingleAvTree( aGroupNodeId, pinOpSet1, nodes );
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

    return PIN_DEFAULT_ANALOG_TRANSLATOR_VAL;
  }
}
