package org.toxsoft.skf.bridge.cfg.modbus.gui.utils;

import java.util.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.validator.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * Реализация генератора содержимого файла devcfg по массиву конфигурационных юнитов sk-modbus.
 *
 * @author max
 */
public class BaseModbusCommonDevCfgGenerator
    implements IModbusCommonDevCfgGenerator {

  //
  // -----------------------------------------
  // preset entities

  private static final String ERR_STR_RESULT_HAS_BEEN_ALREADY_GENERATED = "Result has been already generated"; //$NON-NLS-1$

  protected ITsGuiContext context;

  protected IList<ModbusNode> cfgNodes = new ElemArrayList<>();

  protected IList<ModbusDevice> modbusDevices = new ElemArrayList<>();

  protected ISkConnection connection;

  protected INodeIdConvertor idConvertor = aNodeEntity -> new Pair<>( aNodeEntity.asString(), aNodeEntity.asString() );

  protected IListEdit<IStringList> properties = new ElemArrayList<>();

  protected IDevCfgParamValueSource paramValueSource =
      ( aParamName, aContext ) -> aContext.params().findByKey( aParamName );

  //
  // -----------------------------------------
  // result of generation

  protected AvTree result;

  /**
   * Constructor by context.
   *
   * @param aContext ITsGuiContext - context.
   */
  public BaseModbusCommonDevCfgGenerator( ITsGuiContext aContext ) {
    context = aContext;

    connection = context.get( ISkConnectionSupplier.class ).defConn();
  }

  @Override
  public IModbusCommonDevCfgGenerator setUnits( IList<ModbusNode> aCfgUnits ) {
    TsIllegalStateRtException.checkFalse( result == null, ERR_STR_RESULT_HAS_BEEN_ALREADY_GENERATED );
    cfgNodes = new ElemArrayList<>( aCfgUnits );
    return this;
  }

  @Override
  public IModbusCommonDevCfgGenerator setDevices( IList<ModbusDevice> aModbusDevices ) {
    TsIllegalStateRtException.checkFalse( result == null, ERR_STR_RESULT_HAS_BEEN_ALREADY_GENERATED );
    modbusDevices = new ElemArrayList<>( aModbusDevices );
    return this;
  }

  @Override
  public IModbusCommonDevCfgGenerator setConnection( ISkConnection aConn ) {
    TsIllegalStateRtException.checkFalse( result == null, ERR_STR_RESULT_HAS_BEEN_ALREADY_GENERATED );
    connection = aConn;
    return this;
  }

  @Override
  public IModbusCommonDevCfgGenerator setNodeIdConvertor( INodeIdConvertor aIdConvertor ) {
    TsIllegalStateRtException.checkFalse( result == null, ERR_STR_RESULT_HAS_BEEN_ALREADY_GENERATED );
    idConvertor = aIdConvertor;
    return this;
  }

  @Override
  public IModbusCommonDevCfgGenerator setAdditionalProperties( IList<IStringList> aProperties ) {
    TsIllegalStateRtException.checkFalse( result == null, ERR_STR_RESULT_HAS_BEEN_ALREADY_GENERATED );
    properties = new ElemArrayList<>( aProperties );
    return this;
  }

  @Override
  public IModbusCommonDevCfgGenerator setParamValueSource( IDevCfgParamValueSource aParamValueSource ) {
    TsIllegalStateRtException.checkFalse( result == null, ERR_STR_RESULT_HAS_BEEN_ALREADY_GENERATED );
    paramValueSource = aParamValueSource;
    return this;
  }

  @Override
  public IAvTree generate() {
    IStringMapEdit<IListEdit<ModbusDevice>> modbusDevicesTree = new StringMap<>();

    for( ModbusDevice device : modbusDevices ) {

      if( device.isTcp() ) {
        modbusDevicesTree.put( device.getDeviceModbusConnectionId(), new ElemArrayList<>( device ) );
      }
      else {
        String deviceModbusConnectionId = device.getDeviceModbusConnectionId();
        IListEdit<ModbusDevice> devicesOnPort = modbusDevicesTree.findByKey( deviceModbusConnectionId );
        if( devicesOnPort == null ) {
          devicesOnPort = new ElemArrayList<>();
          modbusDevicesTree.put( deviceModbusConnectionId, devicesOnPort );
        }
        devicesOnPort.add( device );
      }
    }

    // массив соединений
    AvTree connectionsMassivTree = AvTree.createArrayAvTree();

    for( String deviceModbusConnectionId : modbusDevicesTree.keys() ) {
      IListEdit<ModbusDevice> devicesOnConnect = modbusDevicesTree.getByKey( deviceModbusConnectionId );
      // массив устройств
      AvTree devicesMassivTree = AvTree.createArrayAvTree();
      for( ModbusDevice device : devicesOnConnect ) {

        // String.format( DEVICE_DEF_ID_FORMAT, String.valueOf( devieNumber++ ) ) );
        AvTree modbusDeviceTree = createDevice( device, device.getDeviceId() + ".def" ); //$NON-NLS-1$

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
      // String connNodeId = String.format( CONNECTION_TEMP_FMT_STR, Integer.valueOf( connNumber++ ) );

      IAvTree singleConnection = AvTree.createSingleAvTree( deviceModbusConnectionId, connOps, connectionNodes );
      connectionsMassivTree.addElement( singleConnection );
    }

    StringMap<IAvTree> nodes = new StringMap<>();
    nodes.put( CONNECTIONS_ARRAY_NAME, connectionsMassivTree );

    IOptionSetEdit opSet = new OptionSet();

    opSet.setStr( JAVA_CLASS_PARAM_NAME, JAVA_CLASS_PARAM_VAL_TEMPLATE );
    opSet.setStr( ID_PARAM_NAME, MODBUS_DEVICE_ID );
    opSet.setStr( DESCRIPTION_PARAM_NAME, DESCRIPTION_PARAM_VAL_TEMPLATE );
    opSet.setInt( SYNCH_PERIOD_PARAM_NAME, 1000 );

    IAvTree tree = AvTree.createSingleAvTree( MODBUS_CFG_NODE_ID, opSet, nodes );

    BaseOpcCommonDlmCfgGenerator.insertProperties( tree, properties, paramValueSource, context );

    return tree;
  }

  private AvTree createDevice( ModbusDevice device, String aDeviceNodeId ) {

    // массив тегов группы
    AvTree tagsMassivTree = AvTree.createArrayAvTree();

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
      pinOpSet1.setStr( PIN_TRANSLATOR_PARAMS_PARAM_ID, getParams( aData ) );
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

  private static String getParams( ModbusNode aData ) {
    if( aData.getParams() != null && aData.getParams().length() > 0 ) {
      StringBuilder params = new StringBuilder();
      StringTokenizer st = new StringTokenizer( aData.getParams() );
      String add = TsLibUtils.EMPTY_STRING;
      while( st.hasMoreTokens() ) {
        String token = st.nextToken();
        if( !token.startsWith( TRANSLATOR_DETECTOR_STR ) ) {
          params.append( add ).append( token );
          add = " "; //$NON-NLS-1$
        }
      }
      return params.toString();
    }
    return TsLibUtils.EMPTY_STRING;
  }

  private static String getTranslator( ModbusNode aData ) {
    if( aData.getParams() != null && aData.getParams().length() > 0 ) {
      StringTokenizer st = new StringTokenizer( aData.getParams() );
      while( st.hasMoreTokens() ) {
        String token = st.nextToken();
        if( token.startsWith( TRANSLATOR_DETECTOR_STR ) ) {
          return token.substring( TRANSLATOR_DETECTOR_STR.length() );
        }
      }
    }

    if( aData.getRequestType().isDiscret() ) {
      return PIN_DEFAULT_DISCRET_TRANSLATOR_VAL;

    }

    return switch( aData.getValueType() ) {
      case BOOLEAN -> PIN_DEFAULT_BOOLEAN_TRANSLATOR_VAL;
      case FLOATING -> PIN_DEFAULT_FLOAT_TRANSLATOR_VAL;
      case INTEGER, NONE, STRING, TIMESTAMP, VALOBJ -> PIN_DEFAULT_INTEGER_TRANSLATOR_VAL;
      default -> PIN_DEFAULT_INTEGER_TRANSLATOR_VAL;
    };

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
