package org.toxsoft.skf.brifge.cfg.s7.gui.utils;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.brifge.cfg.s7.gui.utils.BiL2CfgGeneratorConstants.*;
import static org.toxsoft.l2.thd.s7.IS7ThdConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.utils.ISkResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.utils.OpcUaUtils.*;

import java.io.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.list.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skf.brifge.cfg.s7.gui.type.*;

import ru.toxsoft.l2.thd.opc.*;

/**
 * Реализация генератора содержимого файлов конфигурации драйвера S7 ( biS7.devcfg & biS7.dlmcfg). <br>
 * Работает по паттерну builder:
 * <ul>
 * <li>создаем S7 устройство методом {@link #createS7Device(String, int, int, String) }</li>
 * <li>описываем его теги {@link #addDataTag(String, int, EKind, int, EAtomicType)} или
 * {@link #addBitTag(String, int, EKind, int, int)}</li>
 * <li>привязываем к тегу {@link GuiS7Tag } его USkat Id {@link Gwid} методом
 * {@link #linkS7Tag2Gwid(String, Gwid) }</li>
 * <li>генерируем нужные конфигурационные файлы методом {@link #generateDevCfgFile() } & {@link #generateDlmCfgFile() }
 * <li>для удобства я написал метод методом {@link #createBiL2Config() } - создает целиком конфигурацию</li></li>
 * </ul>
 *
 * @author dima
 */
public class BiL2CfgGenerator {

  /**
   * Все S7 устройства для данной конфигурации
   */
  private static IStringMapEdit<GuiS7Device> devicesMap = new StringMap<>();

  /**
   * Список все описаний {@link OpcToS5DataCfgUnit} текущей конфигурации
   */
  private static IListEdit<OpcToS5DataCfgUnit> units = new ElemArrayList<>();

  /**
   * Создает один S7 server, возвращает его ID
   *
   * @param aIP - IP адрес
   * @param aRack - rack
   * @param aSlot - slot
   * @param aName - название устройства
   * @return id устройства
   */
  public static String createS7Device( String aIP, int aRack, int aSlot, String aName ) {
    IOptionSetEdit deviceOptValues = new OptionSet();
    S7DeviceOptionsUtils.OP_TCP_IP_ADDRESS.setValue( deviceOptValues, AvUtils.avStr( aIP ) );
    S7DeviceOptionsUtils.OP_RACK.setValue( deviceOptValues, AvUtils.avInt( aRack ) );
    S7DeviceOptionsUtils.OP_SLOT.setValue( deviceOptValues, AvUtils.avInt( aSlot ) );
    String deviceId = GuiS7Device.generateId( deviceOptValues );
    GuiS7Device device = new GuiS7Device( deviceId, aName, deviceOptValues );
    devicesMap.put( deviceId, device );
    return deviceId;
  }

  /**
   * Создает новый тег и привязывает его к блоку данных. Если блока с таким номером нет, то создает новый, привязывает
   * его к серверу и далее «открытый перелом».
   *
   * @param aServerId - id S7 сервера
   * @param aDBNumber - номер блока
   * @param aTagKind - тип тега {@link EKind}
   * @param aBufOffset - смещение внутри блока
   * @param aBufSize - размер буфера значения
   * @param aValueType - тип значения {@link EAtomicType}
   * @return id нового тега
   */
  @SuppressWarnings( "nls" )
  public static String addDataTag( String aServerId, int aDBNumber, EKind aTagKind, int aBufOffset, int aBufSize,
      EAtomicType aValueType ) {
    TsIllegalArgumentRtException.checkTrue( aValueType.equals( EAtomicType.BOOLEAN ),
        "for bit tag use method 'addBitTag(...)'" );
    // получаем S7 server
    GuiS7Device device = devicesMap.getByKey( aServerId );
    // ищем в его блоках
    for( GuiS7DataBlock db : device.getDataBlocks() ) {
      if( db.getDBNumber() == aDBNumber ) {
        GuiS7Tag tag = new GuiS7Tag( db, aTagKind, aBufOffset, aBufSize, aValueType );
        db.addTag( tag );
        return tag.getId();
      }
    }
    // нет такого блока
    GuiS7DataBlock newDataBlock = new GuiS7DataBlock( device, aDBNumber );
    device.addDataBlock( newDataBlock );
    GuiS7Tag tag = new GuiS7Tag( newDataBlock, aTagKind, aBufOffset, aBufSize, aValueType );
    newDataBlock.addTag( tag );
    return tag.getId();
  }

  /**
   * Создает новый тег и привязывает его к блоку данных. Если блока с таким номером нет, то создает новый, привязывает
   * его к серверу и далее «открытый перелом».
   *
   * @param aServerId - id S7 сервера
   * @param aDBNumber - номер блока
   * @param aTagKind - тип тега {@link EKind}
   * @param aOffset - смещение внутри блока
   * @param aBitNumber - номер бита в слове состояния тега
   * @return id нового тега
   */
  public static String addBitTag( String aServerId, int aDBNumber, EKind aTagKind, int aOffset, int aBitNumber ) {
    // получаем S7 server
    GuiS7Device device = devicesMap.getByKey( aServerId );
    // ищем в его блоках
    for( GuiS7DataBlock db : device.getDataBlocks() ) {
      if( db.getDBNumber() == aDBNumber ) {
        GuiS7Tag tag = new GuiS7Tag( db, aTagKind, aOffset, aBitNumber );
        db.addTag( tag );
        return tag.getId();
      }
    }
    // нет такого блока
    GuiS7DataBlock newDataBlock = new GuiS7DataBlock( device, aDBNumber );
    device.addDataBlock( newDataBlock );
    GuiS7Tag tag = new GuiS7Tag( newDataBlock, aTagKind, aOffset, aBitNumber );
    newDataBlock.addTag( tag );
    return tag.getId();
  }

  /**
   * Cоздает новое описание единицы конфигурации {@link OpcToS5DataCfgUnit } и добавляет его в список единиц
   * конфигурации. <br>
   * Тип единицы (данное, команда, событие) определяется по типу aGwid {@link Gwid }
   *
   * @param aTagId - id S7 tag
   * @param aGwid - {@link Gwid}
   */
  public static void linkS7Tag2Gwid( String aTagId, Gwid aGwid ) {
    GuiS7Tag tag = getTag( aTagId );
    TsIllegalStateRtException.checkNull( tag, "Unknown tag: %s", aTagId ); //$NON-NLS-1$
    switch( aGwid.kind() ) {
      case GW_CMD:
        units.add( getCmdCfgUnit( aGwid, tag ) );
        break;
      case GW_RTDATA:
        units.add( getDataCfgUnit( aGwid, tag ) );
        break;
      case GW_EVENT:
        // TODO
        break;
      case GW_ATTR:
      case GW_CLASS:
      case GW_CLOB:
      case GW_CMD_ARG:
      case GW_EVENT_PARAM:
      case GW_LINK:
      case GW_RIVET:
      default:
        break;
    }
  }

  private static GuiS7Tag getTag( String aTagId ) {
    for( GuiS7Device device : devicesMap.values() ) {
      for( GuiS7DataBlock block : device.getDataBlocks() ) {
        for( GuiS7Tag tag : block.getTags() ) {
          if( tag.getId().equals( aTagId ) ) {
            return tag;
          }
        }
      }
    }
    return null;
  }

  private static IAvTree createS7TagTree( GuiS7Tag aGuiS7Tag ) {
    IOptionSetEdit tagOpSet = new OptionSet();
    tagOpSet.setStr( TAG_ID, aGuiS7Tag.getId() );
    tagOpSet.setInt( TAG_BUFFER_OFFSET, aGuiS7Tag.getBufferOffset() );
    tagOpSet.setStr( TAG_KIND_ID, aGuiS7Tag.getTagKind().getName() );
    tagOpSet.setStr( TAG_VALUE_TYPE_ID, aGuiS7Tag.getValueType().id() );
    if( aGuiS7Tag.getValueType().equals( EAtomicType.BOOLEAN ) ) {
      tagOpSet.setInt( BIT_NUMBER, aGuiS7Tag.getBitNumber() );
    }

    IAvTree tagTree =
        AvTree.createSingleAvTree( String.format( TAG_DEF_FORMAT, aGuiS7Tag.getId() ), tagOpSet, IStringMap.EMPTY );
    return tagTree;
  }

  private static IAvTree createS7DataBlockTree( GuiS7DataBlock aGuiS7DataBlock ) {
    IOptionSetEdit dbOpSet = new OptionSet();
    dbOpSet.setStr( DB_ID, aGuiS7DataBlock.getId() );
    dbOpSet.setInt( DB_NUM, aGuiS7DataBlock.getDBNumber() );

    // массив тегов
    AvTree tagArrayTree = AvTree.createArrayAvTree();
    for( GuiS7Tag tag : aGuiS7DataBlock.getTags() ) {
      IAvTree s7TagTree = createS7TagTree( tag );
      tagArrayTree.addElement( s7TagTree );
    }

    StringMap<IAvTree> tags = new StringMap<>();
    tags.put( DB_TAGS_ARRAY_NAME, tagArrayTree );

    IAvTree dbTree =
        AvTree.createSingleAvTree( String.format( DB_DEF_FORMAT, aGuiS7DataBlock.getId() ), dbOpSet, tags );
    return dbTree;
  }

  private static AvTree createS7DeviceTree( GuiS7Device aDevice ) {

    IOptionSetEdit deviceOpSet = new OptionSet();
    deviceOpSet.setStr( DEVICE_ID, aDevice.id() );
    deviceOpSet.setStr( DEVICE_IP_ADDRESS,
        S7DeviceOptionsUtils.OP_TCP_IP_ADDRESS.getValue( aDevice.getDeviceOptValues() ).asString() );
    deviceOpSet.setInt( DEVICE_RACK, S7DeviceOptionsUtils.OP_RACK.getValue( aDevice.getDeviceOptValues() ).asInt() );
    deviceOpSet.setInt( DEVICE_SLOT, S7DeviceOptionsUtils.OP_SLOT.getValue( aDevice.getDeviceOptValues() ).asInt() );

    // массив блоков данных
    AvTree dbArrayTree = AvTree.createArrayAvTree();

    for( GuiS7DataBlock dataBlock : aDevice.getDataBlocks() ) {
      IAvTree db = createS7DataBlockTree( dataBlock );
      dbArrayTree.addElement( db );
    }

    StringMap<IAvTree> dataBlocks = new StringMap<>();
    dataBlocks.put( DATABLOCKS_ARRAY_NAME, dbArrayTree );

    AvTree deviceTree = AvTree.createSingleAvTree( aDevice.id(), deviceOpSet, dataBlocks );
    return deviceTree;
  }

  private static IAvTree devcfgTree() {
    // массив S7 уcтройств
    AvTree devicesArrayTree = AvTree.createArrayAvTree();

    for( GuiS7Device device : devicesMap.values() ) {
      IAvTree db = createS7DeviceTree( device );
      devicesArrayTree.addElement( db );
    }

    // создаем самое верхнее описание, раздел драйвера целиком
    StringMap<IAvTree> devices = new StringMap<>();
    devices.put( S7_DEVICES_ARRAY_NAME, devicesArrayTree );

    IOptionSetEdit opSet = new OptionSet();
    // тут настройки самого драйвера
    opSet.setStr( JAVA_CLASS_PARAM_NAME, JAVA_CLASS_PARAM_VAL_TEMPLATE );
    opSet.setStr( ID_PARAM_NAME, S7_DEVICE_ID );
    opSet.setStr( DESCRIPTION_PARAM_NAME, DESCRIPTION_PARAM_VAL_TEMPLATE );
    opSet.setInt( SYNCH_PERIOD_PARAM_NAME, 1000 );

    IAvTree tree = AvTree.createSingleAvTree( S7_CFG_NODE_ID, opSet, devices );
    return tree;
  }

  /**
   * По ранее введенным метаданным создает конфигурационный файл "biS7.devcfg
   */
  public static void generateDevCfgFile() {

    String TMP_DEST_FILE = "destDevFile.tmp"; //$NON-NLS-1$
    File tmpFile = new File( TMP_DEST_FILE );
    AvTreeKeeper.KEEPER.write( tmpFile, devcfgTree() );
    try {
      PinsConfigFileFormatter.format( TMP_DEST_FILE, "biS7.devcfg", DEV_CONFIG_STR, true );
    }
    catch( IOException ex ) {
      LoggerUtils.error( ex );
    }
  }

  /**
   * По ранее введенным метаданным создает конфигурационный файл "biS7.devcfg
   */
  public static void generateDlmCfgFile() {
    IAvTree dlmconfigTree = OpcToS5DataCfgConverter.convertToDlmCfgTree( units, null, aNodeEntity -> {
      GuiS7Tag nodeid = aNodeEntity.asValobj();
      return new Pair<>( nodeid.getDataBlock().getS7Device().id(), nodeid.getId() );
    } );
    String TMP_DEST_FILE = "destDlmFile.tmp"; //$NON-NLS-1$
    AvTreeKeeper.KEEPER.write( new File( TMP_DEST_FILE ), dlmconfigTree );

    String DLM_CONFIG_STR = "DlmConfig = "; //$NON-NLS-1$
    try {
      PinsConfigFileFormatter.format( TMP_DEST_FILE, "biS7.dlmcfg", DLM_CONFIG_STR, true );
    }
    catch( IOException ex ) {
      LoggerUtils.error( ex );
    }
  }

  /**
   * Just for testing
   *
   * @param aArgs program arguments
   */
  @SuppressWarnings( "nls" )
  public static void main( String[] aArgs ) {
    createBiL2Config();
  }

  /**
   * Create L2 configuration for project BI
   */
  @SuppressWarnings( "nls" )
  public static void createBiL2Config() {
    TsValobjUtils.registerKeeperIfNone( ECfgUnitType.KEEPER_ID, ECfgUnitType.KEEPER );
    TsValobjUtils.registerKeeperIfNone( GuiS7Device.KEEPER_ID, GuiS7Device.KEEPER );
    TsValobjUtils.registerKeeperIfNone( GuiS7DataBlock.KEEPER_ID, GuiS7DataBlock.KEEPER );
    TsValobjUtils.registerKeeperIfNone( GuiS7TagKeeper.KEEPER_ID, GuiS7TagKeeper.KEEPER );

    // BI L2 configuration
    // create S7 device
    String deviceId = createS7Device( "192.168.12.39", 0, 1, "IrkAZ bench of inhectors S7 server" );
    // DB1.DBD48 -> "bi.AnalogInput", "SF_AIV_FT1", "CV"
    String tagId = addDataTag( deviceId, 1, EKind.RW, 48, 4, EAtomicType.FLOATING );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.AnalogInput", "SF_AIV_FT1", "CV" ) );

    // DB2.DBD48 -> "bi.AnalogInput", "SP_AIV_PT1", "CV"
    tagId = addDataTag( deviceId, 2, EKind.RW, 4, 48, EAtomicType.FLOATING );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.AnalogInput", "SP_AIV_PT1", "CV" ) );
    // DB3.DBD48 -> "bi.AnalogInput", "SP_AIV_PT2", "CV"
    tagId = addDataTag( deviceId, 3, EKind.RW, 48, 4, EAtomicType.FLOATING );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.AnalogInput", "SP_AIV_PT2", "CV" ) );
    // DB4.DBD48 -> "bi.AnalogInput", "SG_AIV_GS1", "CV"
    tagId = addDataTag( deviceId, 4, EKind.RW, 48, 4, EAtomicType.FLOATING );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.AnalogInput", "SG_AIV_GS1", "CV" ) );
    // DB5.DBD48 -> "bi.AnalogInput", "SG_AIV_GS2", "CV"
    tagId = addDataTag( deviceId, 4, EKind.RW, 48, 4, EAtomicType.FLOATING );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.AnalogInput", "SG_AIV_GS2", "CV" ) );

    // DB30.DBX23.0 -> "bi.Valve", "VLV1_DB", "Kvf"
    tagId = addBitTag( deviceId, 30, EKind.RW, 23, 0 );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.Valve", "VLV1_DB", "Kvf" ) );
    // DB30.DBX23.1 -> "bi.Valve", "VLV1_DB", "Kvb"
    tagId = addBitTag( deviceId, 30, EKind.RW, 23, 1 );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.Valve", "VLV1_DB", "Kvb" ) );
    // DB30.DBX23.7 -> "bi.Valve", "VLV1_DB", "UPwr"
    tagId = addBitTag( deviceId, 30, EKind.RW, 23, 7 );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.Valve", "VLV1_DB", "UPwr" ) );
    // DB30.DBX22.4 -> "bi.Valve", "VLV1_DB", "MMF"
    tagId = addBitTag( deviceId, 30, EKind.RW, 22, 4 );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.Valve", "VLV1_DB", "MMF" ) );
    // DB30.DBX22.4 -> "bi.Valve", "VLV1_DB", "MMB"
    tagId = addBitTag( deviceId, 30, EKind.RW, 22, 5 );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.Valve", "VLV1_DB", "MMB" ) );

    // DB31.DBX23.0 -> "bi.Valve", "VLV2_DB", "Kvf" Сработал КВ на открытие, краска DB31.DBX23.0
    tagId = addBitTag( deviceId, 31, EKind.RW, 23, 0 );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.Valve", "VLV2_DB", "Kvf" ) );
    // DB31.DBX23.0 -> "bi.Valve", "VLV2_DB", "Kvb" Сработал КВ на открытие, краска DB31.DBX23.0
    tagId = addBitTag( deviceId, 31, EKind.RW, 23, 1 );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.Valve", "VLV2_DB", "Kvb" ) );
    // DB31.DBX23.7 -> "bi.Valve", "VLV2_DB", "UPwr"
    tagId = addBitTag( deviceId, 31, EKind.RW, 23, 7 );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.Valve", "VLV2_DB", "UPwr" ) );
    // DB31.DBX22.4 -> "bi.Valve", "VLV2_DB", "MMF"
    tagId = addBitTag( deviceId, 31, EKind.RW, 22, 4 );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.Valve", "VLV2_DB", "MMF" ) );
    // DB31.DBX22.4 -> "bi.Valve", "VLV2_DB", "MMB"
    tagId = addBitTag( deviceId, 31, EKind.RW, 22, 5 );
    linkS7Tag2Gwid( tagId, Gwid.createRtdata( "bi.Valve", "VLV2_DB", "MMB" ) );

    generateDevCfgFile();
    generateDlmCfgFile();
  }

  static IAvList convertToAtomicList( IList<GuiS7Tag> aNodeList ) {
    IAvListEdit result = new AvList( new ElemArrayList<>() );
    for( GuiS7Tag node : aNodeList ) {
      result.add( avValobj( node, GuiS7TagKeeper.KEEPER, GuiS7TagKeeper.KEEPER_ID ) );
    }
    return result;
  }

  // описание одного данного
  private static OpcToS5DataCfgUnit getDataCfgUnit( Gwid aDataGwid, GuiS7Tag aTag ) {
    IList<Gwid> gwidList = new ElemArrayList<>( aDataGwid );
    IAvList tagList = convertToAtomicList( new SingleItemList<>( aTag ) );

    OpcToS5DataCfgUnit opcToS5DataCfgUnit = new OpcToS5DataCfgUnit( "id", "name", gwidList, tagList );
    opcToS5DataCfgUnit.setTypeOfCfgUnit( ECfgUnitType.DATA );

    IListEdit<IDataDef> paramDefinitions = new ElemArrayList<>( false );

    paramDefinitions.add( OpcUaUtils.OP_DATA_JAVA_CLASS );
    paramDefinitions.add( OpcUaUtils.OP_SYNCH_PERIOD );
    paramDefinitions.add( OpcUaUtils.OP_IS_CURR );
    paramDefinitions.add( OpcUaUtils.OP_IS_HIST );

    OptionSet defaultParams = new OptionSet();
    if( !aTag.getValueType().equals( EAtomicType.BOOLEAN ) ) {
      // ----------------------------------------------------
      // Определение первой реализации данных (простое - один к одному)
      OpcUaUtils.OP_DATA_JAVA_CLASS.setValue( defaultParams,
          avStr( DATA_JAVA_CLASS_ONE_TO_ONE_DATA_TRANSMITTER_FACTORY ) );
      OpcUaUtils.OP_SYNCH_PERIOD.setValue( defaultParams, avInt( 1000 ) );
      OpcUaUtils.OP_IS_CURR.setValue( defaultParams, avBool( true ) );
      OpcUaUtils.OP_IS_HIST.setValue( defaultParams, avBool( true ) );

      ICfgUnitRealizationType dataOneToOne = new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_ONT_TO_ONE_DATA,
          STR_ONE_2_ONE, ECfgUnitType.DATA, paramDefinitions, defaultParams );

      opcToS5DataCfgUnit.setRelizationTypeId( dataOneToOne.id() );
    }
    else {
      // ----------------------------------------------------
      // Определение второй реализации данных (битовое данное из интового тега)

      paramDefinitions.add( OP_BIT_INDEX );

      defaultParams = new OptionSet();
      OP_DATA_JAVA_CLASS.setValue( defaultParams,
          avStr( DATA_JAVA_CLASS_ONE_INT_TO_ONE_BIT_DATA_TRANSMITTER_FACTORY ) );
      OP_BIT_INDEX.setValue( defaultParams, avInt( aTag.getBitNumber() ) );

      OP_SYNCH_PERIOD.setValue( defaultParams, avInt( 0 ) );
      OP_IS_CURR.setValue( defaultParams, avBool( true ) );
      OP_IS_HIST.setValue( defaultParams, avBool( true ) );

      ICfgUnitRealizationType dataIntToOne =
          new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_ONE_INT_TO_ONE_BIT_DATA, STR_BIT_FROM_WORD,
              ECfgUnitType.DATA, paramDefinitions, defaultParams );

      opcToS5DataCfgUnit.setRelizationTypeId( dataIntToOne.id() );
    }
    opcToS5DataCfgUnit.setRealizationOpts( defaultParams );
    return opcToS5DataCfgUnit;
  }

  // описание одной команды
  private static OpcToS5DataCfgUnit getCmdCfgUnit( Gwid aCmdGwid, GuiS7Tag aTag ) {
    String strid =
        String.format( CFG_CMD_UNIT_ID_FORMAT, Long.valueOf( System.currentTimeMillis() ), aCmdGwid.strid() );
    ECfgUnitType type = ECfgUnitType.COMMAND;

    IList<Gwid> gwids = new ElemArrayList<>( aCmdGwid );
    IAvList tagList = convertToAtomicList( new SingleItemList<>( aTag ) );

    String name = STR_LINK_PREFIX + aCmdGwid.canonicalString();

    OpcToS5DataCfgUnit unit = new OpcToS5DataCfgUnit( strid, name, gwids, tagList );

    IListEdit<IDataDef> paramDefenitions = new ElemArrayList<>( false );

    paramDefenitions.add( OP_CMD_JAVA_CLASS );
    paramDefenitions.add( OP_CMD_VALUE_PARAM_ID );
    paramDefenitions.add( OP_CMD_OPC_ID );

    IOptionSetEdit defaultParams = new OptionSet();
    OP_CMD_JAVA_CLASS.setValue( defaultParams, avStr( COMMANDS_JAVA_CLASS_VALUE_COMMAND_BY_ONE_TAG_EXEC ) );

    ICfgUnitRealizationType realType = new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_VALUE_COMMAND,
        STR_SET_NODE_VALUE, ECfgUnitType.COMMAND, paramDefenitions, defaultParams ) {

      @Override
      public CfgOpcUaNode createInitCfg( ITsGuiContext aaContext, String aNodeId, int aNodeIndex, int aNodeCount ) {
        OpcUaServerConnCfg conConf =
            (OpcUaServerConnCfg)aaContext.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );

        EAtomicType type = EAtomicType.NONE;
        if( conConf != null ) {
          type = OpcUaUtils.getValueTypeOfNode( aaContext, conConf, aNodeId );
        }
        // dima 22.04.25 помечаем что тег на запись
        return new CfgOpcUaNode( aNodeId, false, true, true, type );
      }

    };

    unit.setTypeOfCfgUnit( type );
    unit.setRelizationTypeId( realType.id() );

    OptionSet realization = new OptionSet();
    OpcUaUtils.OP_CMD_JAVA_CLASS.setValue( realization, avStr( COMMANDS_JAVA_CLASS_VALUE_COMMAND_EXEC ) );

    // if( cmd2Nodes.paramIds() != null && cmd2Nodes.paramIds().size() == 1 ) {
    // OpcUaUtils.OP_CMD_VALUE_PARAM_ID.setValue( realization, avStr( cmd2Nodes.paramIds().first() ) );
    // }

    unit.setRealizationOpts( realization );
    return unit;
  }

}
