package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.utils.OpcUaUtils.*;

import java.util.*;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.validator.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;

/**
 * Converter of cfg to avtree and back
 *
 * @author max
 */
public class OpcToS5DataCfgConverter {

  public static final String СT_WRITE_ID_TAG         = "write.id.tag";
  public static final String СT_WRITE_VAL_TAG_PREFIX = "write.param.";
  public static final String СT_WRITE_VAL_TAG_FORMAT = СT_WRITE_VAL_TAG_PREFIX + "%s.tag";
  public static final String СT_READ_FEEDBACK_TAG    = "read.feedback.tag";

  private static final String СOMPLEX_TAGS_DEFS = "complexTagsDefs";

  public static final String СOMPLEX_TAG_ID   = "complex.tag.id";
  public static final String СOMPLEX_TAG_TYPE = "complex.tag.type";

  public static final String SIMPLE_СOMPLEX_TAG_TYPE = "node.with.address.params.feedback";

  private static final String DLM_CFG_NODE_ID_TEMPLATE                          = "opc.dlm.cfg";
  private static final String DESCRIPTION_STR                                   = "description";
  private static final String ID_STR                                            = "id";
  private static final String DATA_DEF_FORMAT                                   = "data.%s.def";
  private static final String RRI_ATTR_DEF_FORMAT                               = "rri.attr.%s.def";
  private static final String CMD_DEF_FORMAT                                    = "cmd.%s.def";
  private static final String CLASS_DEF_FORMAT                                  = "class.%s.def";
  private static final String DLM_ID_TEMPLATE                                   = "ru.toxsoft.l2.dlm.tags.common";
  private static final String DLM_DESCR_TEMPLATE                                = "ru.toxsoft.l2.dlm.tags.common";
  private static final String ONE_TO_ONE_DATA_TRANSMITTER_FACTORY_CLASS         =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.data.OneToOneDataTransmitterFactory";
  private static final String ONE_INT_TO_ONE_BIT_DATA_TRANSMITTER_FACTORY_CLASS =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.data.SingleIntToSingleBoolDataTransmitterFactory";
  private static final String OPC_TAG_DEVICE                                    = "opc2s5.bridge.collection.id";
  private static final String EVENT_TRIGGER_DEF_FORMAT                          = "event.trigger.%s.def";

  private static final String OUTPUT_TAGS_ARRAY_ID    = "output.tags";
  private static final String ASYNC_TAGS_ARRAY_ID     = "async.tags";
  private static final String SYNC_TAGS_ARRAY_ID      = "sync.tags";
  private static final String OUTPUT_GROUP_NODE_ID    = "opc.output.group.def";
  private static final String ASYNC_GROUP_NODE_ID     = "opc.async.group.def";
  private static final String SYNC_GROUP_NODE_ID      = "opc.sync.group.def";
  private static final String SYNCH_PERIOD_PARAM_NAME = "period";

  private static final String GROUPS_ARRAY_NAME  = "groups";
  private static final String BRIDGES_ARRAY_NAME = "bridges";

  private static final String OPC_TAG_DEVICE_UA = "opc2s5.bridge.collection.id";

  private static final String DESCRIPTION_PARAM_NAME         = "description";
  private static final String DESCRIPTION_PARAM_VAL_TEMPLATE = "opc 2 s5 pins UA apparat producer";

  private static final String ID_PARAM_NAME = "id";

  private static final String JAVA_CLASS_PARAM_NAME         = "javaClassName";
  private static final String JAVA_CLASS_PARAM_VAL_TEMPLATE = "org.toxsoft.l2.thd.opc.ua.milo.OpcUaMiloDriverProducer";

  private static final String HOST_PARAM_NAME         = "host";
  private static final String HOST_PARAM_VAL_TEMPLATE = "opc.tcp://192.168.0.???:4840";

  private static final String USER_PARAM_NAME         = "user";
  private static final String USER_PARAM_VAL_TEMPLATE = "";

  private static final String PASSWORD_PARAM_NAME         = "password";
  private static final String PASSWORD_PARAM_VAL_TEMPLATE = "";

  private static final String SIEMENS_BRIDGE_NODE_ID = "device1.opc.def";

  private static final String OPC2S5_CFG_NODE_ID = "opc2s5.cfg";

  private static final String OPC_TAG_PARAM_NAME        = "opc.tag";
  private static final String PIN_ID_PARAM_NAME         = "pin.id";
  private static final String PIN_TYPE_PARAM_NAME       = "pin.type";
  private static final String PIN_TYPE_EXTRA_PARAM_NAME = "pin.type.extra";
  private static final String PIN_TAG_NODE_ID_FORMAT    = "pin.tag.%s.def";

  /**
   * Начало блока, отвечающего за конфигурацию данных.
   */
  private static final String DATA_DEFS = "dataDefs";

  /**
   * Начало блока, отвечающего за конфигурацию НСИ данных.
   */
  private static final String RRI_DEFS = "rriDefs";

  /**
   * Начало блока, отвечающего за конфигурацию команд.
   */
  private static final String CMD_DEFS = "cmdDefs";

  /**
   * Начало блока, отвечающего за конфигурацию команд для регистрации в качестве слушателя.
   */
  private static final String CMD_CLASS_DEFS = "cmdClassDefs";

  /**
   * Начало блока, отвечающего за события.
   */
  private static final String EVENT_DEFS = "eventDefs";

  /**
   * Имя параметра - идентификатор пина.
   */
  private static final String PIN_ID = "pin.id";

  /**
   * Имя параметра - идентификатор класса.
   */
  private static final String CLASS_ID = "class.id";

  /**
   * Имя параметра - имя объекта.
   */
  private static final String OBJ_NAME = "obj.name";

  /**
   * Имя параметра - идентификатор данного.
   */
  private static final String DATA_ID = "data.id";

  /**
   * Имя параметра - id НСИ атрибута.
   */
  private static final String RRI_ATTR_ID = "rri.attr.id";

  /**
   * Имя параметра - идентификатор команды.
   */
  private static final String CMD_ID = "cmd.id";

  /**
   * Имя параметра - массив тегов на входе команды.
   */
  private static final String COMMAND_TAGS_ARRAY = "tags";

  /**
   * Имя параметра - java-класс.
   */
  private static final String JAVA_CLASS = "java.class";

  /**
   * Имя параметра - является ли данное текущим.
   */
  private static final String IS_CURR = "is.curr";

  /**
   * Имя параметра - является ли данное синхронным.
   */
  private static final String IS_SYNCH = "is.synch";

  /**
   * Имя параметра - является ли данное историческим.
   */
  private static final String IS_HIST = "is.hist";

  /**
   * Имя параметра - идентификатор события.
   */
  private static final String EVENT_ID = "event.id";

  /**
   * Имя параметра - идентификатор тега входного данного
   */
  private static final String TAG_ID = "tag.id";

  /**
   * Имя параметра - идентификатор специального устройства с тегами
   */
  private static final String TAG_DEVICE_ID = "tag.dev.id";

  /**
   * Имя параметра - интервал обновления синхронного данного (для сервера) в мс.
   */
  private static final String SYNCH_PERIOD = "synch.period";

  /**
   * Разделитель имён объектов в списке объектов, предназначенных для выполнения команд (а также в списке команд)
   */
  private static final String LIST_DELIM = ",";

  /**
   * Имя параметра - список идентификаторов команд.
   */
  private static final String CMD_IDS_LIST = "cmd.ids.list";

  /**
   * Имя параметра - список имён объектов..
   */
  private static final String OBJ_NAMES_LIST = "obj.names.list";

  /**
   * Хранилище комплексных тегов (идентификаторов и составляющих тегов) - очищать перед новой генерацией.
   */
  private static IStringMapEdit<IStringMapEdit<NodeId>> complexTagsContetnt = new StringMap<>();

  /**
   * Хранилище идов комплексных тегов (мапированных по skid объектов) - очищать перед новой генерацией.
   */
  private static IMapEdit<Skid, String> complexTagsIdsBySkidsContetnt = new ElemMap<>();

  private OpcToS5DataCfgConverter() {

  }

  /**
   * Из описания {@link OpcToS5DataCfgDoc} создает дерево {@link IAvTree} конфигурации DLM
   *
   * @param aDoc описание конфигурации
   * @return дерево для конфигурирования модуля DLM
   */
  public static IAvTree convertToDlmCfgTree( OpcToS5DataCfgDoc aDoc ) {
    complexTagsContetnt.clear();
    complexTagsIdsBySkidsContetnt.clear();

    StringMap<IAvTree> nodes = new StringMap<>();

    // перечисление возможных команд по классам
    IAvTree commandInfoesMassivTree = createCommandInfoes( aDoc );
    nodes.put( CMD_CLASS_DEFS, commandInfoesMassivTree );

    // команды
    IAvTree commandsMassivTree = createCommands( aDoc );
    nodes.put( CMD_DEFS, commandsMassivTree );

    // данные
    IAvTree datasMassivTree = createDatas( aDoc );
    nodes.put( DATA_DEFS, datasMassivTree );

    // атрибуты НСИ
    IAvTree rriAttrsArrayTree = createRriAttrs( aDoc );
    nodes.put( RRI_DEFS, rriAttrsArrayTree );

    // события
    IAvTree eventsMassivTree = createEvents( aDoc );
    nodes.put( EVENT_DEFS, eventsMassivTree );

    // сложные теги
    IAvTree complexTagsTree = createComplexTags();
    nodes.put( СOMPLEX_TAGS_DEFS, complexTagsTree );

    IOptionSetEdit opSet = new OptionSet();

    opSet.setStr( ID_STR, DLM_ID_TEMPLATE );
    opSet.setStr( DESCRIPTION_STR, DLM_DESCR_TEMPLATE );

    IAvTree dstParams = AvTree.createSingleAvTree( DLM_CFG_NODE_ID_TEMPLATE, opSet, nodes );

    return dstParams;
  }

  private static IAvTree createComplexTags() {
    AvTree pinsMassivTree = AvTree.createArrayAvTree();

    IStringList tagsIds = complexTagsContetnt.keys();

    for( String tagId : tagsIds ) {
      IOptionSetEdit pinOpSet1 = new OptionSet();

      pinOpSet1.setStr( СOMPLEX_TAG_ID, tagId );
      pinOpSet1.setStr( СOMPLEX_TAG_TYPE, SIMPLE_СOMPLEX_TAG_TYPE );
      pinOpSet1.setStr( TAG_DEVICE_ID, OPC_TAG_DEVICE );

      // ссылки на составляющие теги
      IStringMapEdit<NodeId> tags = complexTagsContetnt.getByKey( tagId );

      for( String tagKey : tags.keys() ) {
        pinOpSet1.setStr( tagKey, tags.getByKey( tagKey ).toParseableString() );
      }

      AvTree complexTagTree = AvTree.createSingleAvTree( tagId + ".def", pinOpSet1, IStringMap.EMPTY );
      pinsMassivTree.addElement( complexTagTree );

    }

    return pinsMassivTree;
  }

  /**
   * Создаёт конфигурацию всех данных для подмодуля данных базового DLM.
   *
   * @param aDoc OpcToS5DataCfgDoc - набор единиц конфигурации
   * @return IAvTree - конфигурация в стандартном виде.
   */
  private static IAvTree createDatas( OpcToS5DataCfgDoc aDoc ) {
    AvTree pinsMassivTree = AvTree.createArrayAvTree();

    IList<OpcToS5DataCfgUnit> cfgUnits = aDoc.dataUnits();

    for( int i = 0; i < cfgUnits.size(); i++ ) {
      OpcToS5DataCfgUnit unit = cfgUnits.get( i );

      if( unit.getTypeOfCfgUnit() == ECfgUnitType.DATA ) {
        pinsMassivTree.addElement( createDataPin( unit ) );
      }
    }

    return pinsMassivTree;
  }

  /**
   * Создаёт конфигурацию всех НСИ атрибутов для подмодуля данных базового DLM.
   *
   * @param aDoc OpcToS5DataCfgDoc - набор единиц конфигурации
   * @return IAvTree - конфигурация в стандартном виде.
   */
  private static IAvTree createRriAttrs( OpcToS5DataCfgDoc aDoc ) {

    AvTree rriDefsTree = AvTree.createArrayAvTree();

    IList<OpcToS5DataCfgUnit> cfgUnits = aDoc.dataUnits();

    for( int i = 0; i < cfgUnits.size(); i++ ) {
      OpcToS5DataCfgUnit unit = cfgUnits.get( i );

      if( unit.getTypeOfCfgUnit() == ECfgUnitType.RRI ) {
        rriDefsTree.addElement( createRriAttrPin( unit ) );
      }
    }
    // создаем корневое дерево НСИ и вносим в него общие нстройки модуля
    StringMap<IAvTree> rriDefNodes = new StringMap<>();

    rriDefNodes.put( "rriNodes", rriDefsTree );

    IOptionSetEdit opSet = new OptionSet();
    // TODO заполним описание настройки для модуля вцелом
    opSet.setStr( "status.rri.tag.dev.id", "opc2s5.bridge.collection.id" ); // device где node статуса НСИ
    opSet.setStr( "status.rri.read.tag.id", "ns=32769;i=4955" ); // сам node статуса НСИ
    opSet.setInt( "status.rri.cmd.opc.id", 13 ); // аргумент aAddress в IComplexTag::setValue( int aAddress,
                                                 // IAtomicValue aValue ) для смены статуса НСИ
    opSet.setStr( "status.rri.write.tag.id", "ns=32769;i=4955" );

    IAvTree retVal = AvTree.createSingleAvTree( DLM_CFG_NODE_ID_TEMPLATE, opSet, rriDefNodes );

    return retVal;
  }

  /**
   * Создаёт конфигурацию одного данного (пина-тега) для подмодуля данных базового DLM.
   *
   * @param aUnit OpcToS5DataCfgUnit - описание данного (пина-тега). Предполагается что все параметры заполнены.
   * @return IAvTree - конфигурация в стандартном виде.
   */

  private static IAvTree createDataPin( OpcToS5DataCfgUnit aUnit ) {
    String pinId = aUnit.id();

    IOptionSetEdit pinOpSet1 = new OptionSet();
    pinOpSet1.setStr( PIN_ID, pinId );
    // .setStr( JAVA_CLASS, ONE_TO_ONE_DATA_TRANSMITTER_FACTORY_CLASS );

    IOptionSet opts = aUnit.getRealizationOpts();
    pinOpSet1.addAll( opts );

    // синхронный
    boolean isSynch = true; // TODO - завести поле

    IList<Gwid> gwids = aUnit.getDataGwids();

    for( int i = 0; i < gwids.size(); i++ ) {
      Gwid gwid = gwids.get( i );

      // класс
      String classId = gwid.classId();
      String objName = gwid.strid();
      String dataId = gwid.propId();

      // класс-объект-данное - остаётся как есть
      pinOpSet1.setStr( i == 0 ? CLASS_ID : CLASS_ID + i, classId );
      pinOpSet1.setStr( i == 0 ? OBJ_NAME : OBJ_NAME + i, objName );
      pinOpSet1.setStr( i == 0 ? DATA_ID : DATA_ID + i, dataId );
    }

    // вместо пина - данные о теге
    // идентификатор OPC-устройства (драйвера)
    pinOpSet1.setStr( TAG_DEVICE_ID, OPC_TAG_DEVICE );

    IList<NodeId> nodes = aUnit.getDataNodes();

    for( int i = 0; i < nodes.size(); i++ ) {
      NodeId node = nodes.get( i );

      // сам идентфикатор тега
      pinOpSet1.setStr( i == 0 ? TAG_ID : TAG_ID + i, node.toParseableString() );

    }

    // if( aTagData.getCmdWordBitIndex() >= 0 ) {
    // pinOpSet1.setInt( BIT_INDEX, aTagData.getCmdWordBitIndex() );
    // }

    // признак текущности, историчности TODO
    // pinOpSet1.setBool( IS_HIST, true );
    // pinOpSet1.setBool( IS_CURR, true );

    // признак синхронности заменён на конкретное значение периода синхронизации
    // if( isSynch ) {
    // pinOpSet1.setLong( SYNCH_PERIOD, 1000L );
    // }

    try {
      IAvTree pinTree1 =
          AvTree.createSingleAvTree( String.format( DATA_DEF_FORMAT, pinId ), pinOpSet1, IStringMap.EMPTY );
      return pinTree1;
    }
    catch( TsValidationFailedRtException e ) {
      System.out.println( "Validation Exception" ); //$NON-NLS-1$
      throw e;
    }

  }

  /**
   * Создаёт конфигурацию одного НСИ атрибута (пина-тега) для подмодуля данных базового DLM.
   *
   * @param aUnit OpcToS5DataCfgUnit - описание данного (пина-тега). Предполагается что все параметры заполнены.
   * @return IAvTree - конфигурация в стандартном виде.
   */

  private static IAvTree createRriAttrPin( OpcToS5DataCfgUnit aUnit ) {
    String pinId = aUnit.id();

    IOptionSetEdit pinOpSet1 = new OptionSet();
    pinOpSet1.setStr( PIN_ID, pinId );

    IOptionSet opts = aUnit.getRealizationOpts();
    pinOpSet1.addAll( opts );

    IList<Gwid> gwids = aUnit.getDataGwids();

    for( int i = 0; i < gwids.size(); i++ ) {
      Gwid gwid = gwids.get( i );

      // класс
      String classId = gwid.classId();
      String objName = gwid.strid();
      String dataId = gwid.propId();

      // класс-объект-данное - остаётся как есть
      pinOpSet1.setStr( i == 0 ? CLASS_ID : CLASS_ID + i, classId );
      pinOpSet1.setStr( i == 0 ? OBJ_NAME : OBJ_NAME + i, objName );
      pinOpSet1.setStr( i == 0 ? RRI_ATTR_ID : RRI_ATTR_ID + i, dataId );
    }

    // вместо пина - данные о теге
    // идентификатор OPC-устройства (драйвера)
    pinOpSet1.setStr( TAG_DEVICE_ID, OPC_TAG_DEVICE );

    IList<NodeId> nodes = aUnit.getDataNodes();

    for( int i = 0; i < nodes.size(); i++ ) {
      NodeId node = nodes.get( i );

      // сам идентфикатор тега
      pinOpSet1.setStr( i == 0 ? TAG_ID : TAG_ID + i, node.toParseableString() );

    }

    try {
      IAvTree pinTree1 =
          AvTree.createSingleAvTree( String.format( RRI_ATTR_DEF_FORMAT, pinId ), pinOpSet1, IStringMap.EMPTY );
      return pinTree1;
    }
    catch( TsValidationFailedRtException ex ) {
      LoggerUtils.errorLogger().error( ex );
      throw ex;
    }

  }

  /**
   * Создаёт и возвращает конфигурацию всех событий.
   *
   * @param aDoc OpcToS5DataCfgDoc - набор единиц конфигурации
   * @return IAvTree - конфигурация в стандартном виде.
   */
  private static IAvTree createCommands( OpcToS5DataCfgDoc aDoc ) {
    AvTree commandsMassivTree = AvTree.createArrayAvTree();

    IList<OpcToS5DataCfgUnit> cfgUnits = aDoc.dataUnits();

    for( int i = 0; i < cfgUnits.size(); i++ ) {
      OpcToS5DataCfgUnit unit = cfgUnits.get( i );

      if( unit.getTypeOfCfgUnit() == ECfgUnitType.COMMAND ) {
        commandsMassivTree.addElement( createCommand( unit ) );
      }
    }

    return commandsMassivTree;
  }

  /**
   * Создаёт и возвращает конфигурацию команды.
   *
   * @param aUnit OpcToS5DataCfgUnit - данные для конфигурации команды
   * @return IAvTree - конфигурация в стандартном виде.
   */

  private static IAvTree createCommand( OpcToS5DataCfgUnit aUnit ) {
    String pinId = aUnit.id();

    IOptionSetEdit pinOpSet1 = new OptionSet();

    IList<Gwid> gwids = aUnit.getDataGwids();
    Gwid gwid = gwids.first();

    // класс
    String classId = gwid.classId();
    String objName = gwid.strid();
    String cmdId = gwid.propId();

    // класс-объект-данное - остаётся как есть
    pinOpSet1.setStr( CLASS_ID, classId );
    pinOpSet1.setStr( OBJ_NAME, objName );
    pinOpSet1.setStr( CMD_ID, cmdId );

    IOptionSet unitOpts = aUnit.getRealizationOpts();

    // параметры реализации
    pinOpSet1.addAll( aUnit.getRealizationOpts() );

    // проверка на сложную команду с заменой на сложный тег
    if( unitOpts.hasKey( OP_CMD_JAVA_CLASS.id() )
        && unitOpts.getStr( OP_CMD_JAVA_CLASS ).equals( COMMANDS_JAVA_CLASS_VALUE_COMMAND_BY_ONE_TAG_EXEC ) ) {

      // поментяь класс , оставить один сложный тег, создать его, если надо, проверить правильность состава сложного
      // тега
      // добавить теги параметров, если не совпадает фидбак - выкинуть ошибку
      // IOptionSetEdit replacedRealizationOpts = new OptionSet( unitOpts );
      OP_CMD_JAVA_CLASS.setValue( pinOpSet1, avStr( COMMANDS_JAVA_CLASS_VALUE_COMMAND_BY_COMPLEX_TAG_EXEC ) );

      try {
        EAtomicType cmdArgType = null;
        if( unitOpts.hasKey( OP_CMD_VALUE_PARAM_ID.id() ) ) {
          String cmdArgTypeStr = unitOpts.getStr( OP_CMD_VALUE_PARAM_ID.id() );
          if( cmdArgTypeStr.equals( Ods2DtoCmdInfoParser.CMD_ARG_INT_ID ) ) {
            cmdArgType = EAtomicType.INTEGER;
          }
          else
            if( cmdArgTypeStr.equals( Ods2DtoCmdInfoParser.CMD_ARG_FLT_ID ) ) {
              cmdArgType = EAtomicType.FLOATING;
            }
        }
        String complexNodeId = createIfNeedAndGetComplexNodeId( aUnit.getDataNodes(), cmdArgType );
        OP_COMPLEX_TAG_ID.setValue( pinOpSet1, avStr( complexNodeId ) );

        // специально для ДИМЫ:
        complexTagsIdsBySkidsContetnt.put( new Skid( classId, objName ), complexNodeId );
      }
      catch( TsIllegalArgumentRtException e ) {
        System.out.println( "Complex tag Exception" ); //$NON-NLS-1$
        throw e;
      }

      try {
        return AvTree.createSingleAvTree( String.format( CMD_DEF_FORMAT, pinId ), pinOpSet1, IStringMap.EMPTY );
      }
      catch( TsValidationFailedRtException e ) {
        System.out.println( "Validation Exception" ); //$NON-NLS-1$
        throw e;
      }

    }

    // дерево тегов
    IStringMapEdit<IAvTree> cmdTreeNodes = new StringMap<>();

    AvTree tagsTree = AvTree.createArrayAvTree();
    cmdTreeNodes.put( COMMAND_TAGS_ARRAY, tagsTree );

    IList<NodeId> nodes = aUnit.getDataNodes();

    for( int i = 0; i < nodes.size(); i++ ) {
      NodeId node = nodes.get( i );

      IOptionSetEdit nodeOpSet = new OptionSet();

      nodeOpSet.setStr( TAG_DEVICE_ID, OPC_TAG_DEVICE );
      nodeOpSet.setStr( TAG_ID, node.toParseableString() );
      IAvTree nodeTree = AvTree.createSingleAvTree( "tag" + (i + 1), nodeOpSet, IStringMap.EMPTY );
      tagsTree.addElement( nodeTree );
    }

    try {
      IAvTree pinTree1 = AvTree.createSingleAvTree( String.format( CMD_DEF_FORMAT, pinId ), pinOpSet1, cmdTreeNodes );
      return pinTree1;
    }
    catch( TsValidationFailedRtException e ) {
      System.out.println( "Validation Exception" ); //$NON-NLS-1$
      throw e;
    }
  }

  /**
   * Создаёт сложный тег, если надо, проверяет правильность состава сложного тега, добавляет теги параметров, если не
   * совпадает фидбак - выкидывает ошибку.
   *
   * @param aDataNodes IList - список тегов, из которых состоит сложный тег (может отличаться тегами параметров),
   *          сложный тег мапируется по тегу с идентификатором команд - должен быть первым в списке, тег с фидбаком
   *          должен быть последним.
   * @param aParamNodeType EAtomicType - тип передваемого в команде, использующей данный сложный тег, параметра, может
   *          быть null
   * @return String - идентификатор сложного тега
   * @throws TsIllegalArgumentRtException - в случае несовпадения фидбаков при одинаковых командных тегов.
   */
  private static String createIfNeedAndGetComplexNodeId( IList<NodeId> aDataNodes, EAtomicType aParamNodeType ) {
    // миниммум 2 тега - командный и фидбак
    TsIllegalArgumentRtException.checkFalse( aDataNodes.size() > 1 );

    // первый node - командный (по нему же - мапирование
    NodeId cmdIdNode = aDataNodes.first();

    // последний - фидбак
    NodeId feedBackNode = aDataNodes.last();

    // генерим идентификатор
    String compexTagId = "synthetic_" + cmdIdNode.toParseableString();
    compexTagId = compexTagId.replaceAll( ";", "_" ).replaceAll( "=", "_" );

    // проверка наличия этого сложного тега
    IStringMapEdit<NodeId> cTagContent = complexTagsContetnt.findByKey( compexTagId );
    if( cTagContent == null ) {
      cTagContent = new StringMap<>();
      // командный
      cTagContent.put( СT_WRITE_ID_TAG, cmdIdNode );
      // фидбак
      cTagContent.put( СT_READ_FEEDBACK_TAG, feedBackNode );

      // между ними - если есть - тег параметров
      if( aDataNodes.size() > 2 && aParamNodeType != null ) {
        String paramTagId = String.format( СT_WRITE_VAL_TAG_FORMAT, aParamNodeType.id().toLowerCase() );
        cTagContent.put( paramTagId, aDataNodes.get( 1 ) );
      }

      complexTagsContetnt.put( compexTagId, cTagContent );
      return compexTagId;
    }

    // проверка содержания тега
    // сначала фидбак
    TsIllegalArgumentRtException.checkFalse( cTagContent.hasKey( СT_READ_FEEDBACK_TAG ) && cTagContent
        .getByKey( СT_READ_FEEDBACK_TAG ).toParseableString().equals( feedBackNode.toParseableString() ) );

    // далее проверяем и добавляем теги параметров
    if( aDataNodes.size() > 2 && aParamNodeType != null ) {
      String paramTagId = String.format( СT_WRITE_VAL_TAG_FORMAT, aParamNodeType.id().toLowerCase() );
      if( cTagContent.hasKey( paramTagId ) ) {
        // проверка
        TsIllegalArgumentRtException.checkFalse(
            cTagContent.getByKey( paramTagId ).toParseableString().equals( aDataNodes.get( 1 ).toParseableString() ) );
      }
      else {
        // добавление
        cTagContent.put( paramTagId, aDataNodes.get( 1 ) );
      }
    }

    return compexTagId;
  }

  /**
   * Создаёт и возвращает перечисление возможных команд по классам.
   *
   * @param aDoc OpcToS5DataCfgDoc - набор единиц конфигурации
   * @return IAvTree - конфигурация в стандартном виде.
   */
  private static IAvTree createCommandInfoes( OpcToS5DataCfgDoc aDoc ) {
    AvTree commandInfoesMassivTree = AvTree.createArrayAvTree();

    IMapEdit<String, IListEdit<String>> objsByClass = new ElemMap<>();
    IMapEdit<String, IListEdit<String>> cmdsByClass = new ElemMap<>();

    IList<OpcToS5DataCfgUnit> cfgUnits = aDoc.dataUnits();

    for( int i = 0; i < cfgUnits.size(); i++ ) {
      OpcToS5DataCfgUnit unit = cfgUnits.get( i );

      if( unit.getTypeOfCfgUnit() == ECfgUnitType.COMMAND ) {
        IList<Gwid> gwids = unit.getDataGwids();
        for( Gwid gwid : gwids ) {

          // класс
          String classId = gwid.classId();
          String objName = gwid.strid();
          String cmdId = gwid.propId();

          IListEdit<String> objs;
          IListEdit<String> cmds;

          if( objsByClass.hasKey( classId ) ) {
            objs = objsByClass.getByKey( classId );
            cmds = cmdsByClass.getByKey( classId );
          }
          else {
            objs = new ElemArrayList<>( false );
            cmds = new ElemArrayList<>( false );

            objsByClass.put( classId, objs );
            cmdsByClass.put( classId, cmds );
          }

          objs.add( objName );
          cmds.add( cmdId );
        }
      }
    }

    Iterator<String> classIterator = objsByClass.keys().iterator();

    while( classIterator.hasNext() ) {
      String classId = classIterator.next();

      IAvTree classDef =
          createClassCommandsInfo( classId, objsByClass.getByKey( classId ), cmdsByClass.getByKey( classId ) );
      commandInfoesMassivTree.addElement( classDef );
    }

    return commandInfoesMassivTree;

  }

  /**
   * Создаёт и возвращает перечисление возможных команд одного класса.
   *
   * @param aClassId - идентификатор класса
   * @param aObjNames - имена объектов
   * @param aCmdIds - идентификаторы команд класса.
   * @return IAvTree - конфигурация в стандартном виде.
   */

  private static IAvTree createClassCommandsInfo( String aClassId, IList<String> aObjNames, IList<String> aCmdIds ) {
    IOptionSetEdit pinOpSet1 = new OptionSet();
    pinOpSet1.setStr( CLASS_ID, aClassId );

    StringBuilder objList = new StringBuilder();
    String add = new String();
    for( String str : aObjNames ) {
      objList.append( add );
      objList.append( str );
      add = LIST_DELIM;
    }
    pinOpSet1.setStr( OBJ_NAMES_LIST, objList.toString() );

    StringBuilder cmdList = new StringBuilder();
    add = new String();
    for( String str : aCmdIds ) {
      cmdList.append( add );
      cmdList.append( str );
      add = LIST_DELIM;
    }
    pinOpSet1.setStr( CMD_IDS_LIST, cmdList.toString() );

    IAvTree pinTree1 =
        AvTree.createSingleAvTree( String.format( CLASS_DEF_FORMAT, aClassId ), pinOpSet1, IStringMap.EMPTY );
    return pinTree1;
  }

  /**
   * Создаёт и возвращает конфигурацию всех событий.
   *
   * @param aDoc OpcToS5DataCfgDoc - набор единиц конфигурации
   * @return IAvTree - конфигурация в стандартном виде.
   */
  private static IAvTree createEvents( OpcToS5DataCfgDoc aDoc ) {
    AvTree eventsMassivTree = AvTree.createArrayAvTree();

    IList<OpcToS5DataCfgUnit> cfgUnits = aDoc.dataUnits();

    for( int i = 0; i < cfgUnits.size(); i++ ) {
      OpcToS5DataCfgUnit unit = cfgUnits.get( i );

      if( unit.getTypeOfCfgUnit() == ECfgUnitType.EVENT ) {
        eventsMassivTree.addElement( createEvent( unit ) );
      }
    }

    return eventsMassivTree;
  }

  /**
   * Создаёт конфигурацию события и возвращает её.
   *
   * @return IAvTree - конфигурация события.
   */
  @SuppressWarnings( "unchecked" )
  private static IAvTree createEvent( OpcToS5DataCfgUnit aUnit ) {
    IOptionSetEdit pinOpSet1 = new OptionSet();

    IList<Gwid> gwids = aUnit.getDataGwids();
    Gwid gwid = gwids.first();

    // класс
    String classId = gwid.classId();
    String objName = gwid.strid();
    String evntId = gwid.propId();

    // класс-объект-данное - остаётся как есть
    pinOpSet1.setStr( CLASS_ID, classId );
    pinOpSet1.setStr( OBJ_NAME, objName );
    pinOpSet1.setStr( EVENT_ID, evntId );

    IList<NodeId> nodes = aUnit.getDataNodes();
    NodeId node = nodes.first();

    pinOpSet1.setStr( TAG_DEVICE_ID, OPC_TAG_DEVICE );
    pinOpSet1.setStr( TAG_ID, node.toParseableString() );

    IOptionSet opts = aUnit.getRealizationOpts();
    pinOpSet1.addAll( opts );

    IAvTree triggerTree =
        AvTree.createSingleAvTree( String.format( EVENT_TRIGGER_DEF_FORMAT, aUnit.id() ), pinOpSet1, IStringMap.EMPTY );

    return triggerTree;
  }

  public static IAvTree convertToDevCfgTree( OpcToS5DataCfgDoc aDoc ) {

    IOptionSetEdit opSet = new OptionSet();

    opSet.setStr( JAVA_CLASS_PARAM_NAME, JAVA_CLASS_PARAM_VAL_TEMPLATE );
    opSet.setStr( ID_PARAM_NAME, OPC_TAG_DEVICE_UA );
    opSet.setStr( DESCRIPTION_PARAM_NAME, DESCRIPTION_PARAM_VAL_TEMPLATE );

    IOptionSetEdit bridgeOps = new OptionSet();

    bridgeOps.setStr( ID_PARAM_NAME, OPC_TAG_DEVICE_UA );
    bridgeOps.setStr( DESCRIPTION_PARAM_NAME, DESCRIPTION_PARAM_VAL_TEMPLATE );
    bridgeOps.setStr( HOST_PARAM_NAME, HOST_PARAM_VAL_TEMPLATE );
    bridgeOps.setStr( USER_PARAM_NAME, USER_PARAM_VAL_TEMPLATE );
    bridgeOps.setStr( PASSWORD_PARAM_NAME, PASSWORD_PARAM_VAL_TEMPLATE );

    AvTree synchGroup = createGroup( aDoc, aCfgNode -> (aCfgNode.isRead() && aCfgNode.isSynch()), SYNC_TAGS_ARRAY_ID,
        SYNC_GROUP_NODE_ID );

    synchGroup.fieldsEdit().setInt( SYNCH_PERIOD_PARAM_NAME, 500 );

    IAvTree asynchGroup = createGroup( aDoc, aCfgNode -> (aCfgNode.isRead() && !aCfgNode.isSynch()),
        ASYNC_TAGS_ARRAY_ID, ASYNC_GROUP_NODE_ID );

    IAvTree outputGroup = createGroup( aDoc, CfgOpcUaNode::isWrite, OUTPUT_TAGS_ARRAY_ID, OUTPUT_GROUP_NODE_ID );

    // массив групп
    AvTree groupsMassivTree = AvTree.createArrayAvTree();

    groupsMassivTree.addElement( synchGroup );
    groupsMassivTree.addElement( asynchGroup );
    groupsMassivTree.addElement( outputGroup );

    // массив групп
    AvTree bridgesMassivTree = AvTree.createArrayAvTree();

    StringMap<IAvTree> groupsNodes = new StringMap<>();
    groupsNodes.put( GROUPS_ARRAY_NAME, groupsMassivTree );

    IAvTree siemensBridge = AvTree.createSingleAvTree( SIEMENS_BRIDGE_NODE_ID, bridgeOps, groupsNodes );
    bridgesMassivTree.addElement( siemensBridge );

    StringMap<IAvTree> nodes = new StringMap<>();
    nodes.put( BRIDGES_ARRAY_NAME, bridgesMassivTree );

    IAvTree tree = AvTree.createSingleAvTree( OPC2S5_CFG_NODE_ID, opSet, nodes );

    return tree;
  }

  private static AvTree createGroup( OpcToS5DataCfgDoc aDoc, IGroupFilter aGroupFilter, String aArrayId,
      String aGroupNodeId ) {

    // массив тегов группы
    AvTree tagsMassivTree = AvTree.createArrayAvTree();

    Set<String> alreadyAddedTags = new HashSet<>();

    IList<CfgOpcUaNode> cfgNodes = aDoc.getNodesCfgs();

    for( CfgOpcUaNode tagData : cfgNodes ) {
      if( !aGroupFilter.isValid( tagData ) ) {
        continue;
      }

      IAvTree tag = createTag( tagData );

      tagsMassivTree.addElement( tag );
    }

    StringMap<IAvTree> nodes = new StringMap<>();
    nodes.put( aArrayId, tagsMassivTree );

    IOptionSetEdit pinOpSet1 = new OptionSet();

    AvTree groupTree = AvTree.createSingleAvTree( aGroupNodeId, pinOpSet1, nodes );
    return groupTree;
  }

  private static IAvTree createTag( CfgOpcUaNode aData ) {

    IOptionSetEdit pinOpSet1 = new OptionSet();

    pinOpSet1.setStr( OPC_TAG_PARAM_NAME, aData.getNodeId() );
    pinOpSet1.setStr( PIN_ID_PARAM_NAME, getPinId( aData.getNodeId() ) );
    pinOpSet1.setStr( PIN_TYPE_PARAM_NAME, aData.getType().id() );

    // ЗАПЛАТКА TODO - переделать драйвер!!!!!!!!! TODO
    if( aData.getType() == EAtomicType.INTEGER ) {
      pinOpSet1.setStr( PIN_TYPE_EXTRA_PARAM_NAME, "INT" );
    }

    // if( aData.getCmdWordBitIndex() > -1 ) {
    // pinOpSet1.setBool( PIN_CONTROL_WORD_PARAM_NAME, true );
    // }

    IAvTree pinTree1 = null;
    try {
      pinTree1 = AvTree.createSingleAvTree( String.format( PIN_TAG_NODE_ID_FORMAT, getPinId( aData.getNodeId() ) ),
          pinOpSet1, IStringMap.EMPTY );
    }
    catch( TsValidationFailedRtException e ) {
      System.out.println( aData.getNodeId() );
      throw e;
    }
    return pinTree1;
  }

  private static String getPinId( String aTagName ) {
    String result = aTagName;
    result = result.replace( " ", "_" );
    result = result.replace( "-", "_" );
    result = result.replace( ".", "_" );
    result = result.replace( "(", "_" );
    result = result.replace( ")", "_" );
    result = result.replace( "=", "_" );
    result = result.replace( ";", "_" );
    result = result.replace( "\\\"", "" );
    result = result.replace( "\"", "" );
    return result;
  }

  interface IGroupFilter {

    boolean isValid( CfgOpcUaNode aCfgOpcUaNode );
  }
}
