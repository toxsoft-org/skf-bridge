package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.skide.IGreenWorldRefbooks.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.utils.OpcUaUtils.*;

import java.util.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.av.list.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.validator.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.refbooks.lib.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

public class BaseOpcCommonDlmCfgGenerator
    implements IOpcCommonDlmCfgGenerator {

  //
  // -----------------------------------------
  // preset entities

  protected ITsGuiContext context;

  protected IListEdit<OpcToS5DataCfgUnit> cfgUnits = new ElemArrayList<>();

  protected ISkConnection connection;

  protected INodeIdConvertor idConvertor = aNodeEntity -> new Pair<>( aNodeEntity.asString(), aNodeEntity.asString() );

  protected IGwidFilter gwidFilter = IGwidFilter.EMPTY_FILTER;

  protected IListEdit<IStringList> properties = new ElemArrayList<>();

  protected IComplexTagDetector complexTagDetector = ( aUnit, aContext ) -> false;

  protected IDevCfgParamValueSource paramValueSource =
      ( aParamName, aContext ) -> aContext.params().findByKey( aParamName );

  //
  // -----------------------------------------
  // result of generation

  /**
   * Хранилище комплексных тегов (идентификаторов и составляющих тегов)
   */
  protected IStringMapEdit<IStringMapEdit<Pair<String, String>>> complexTagsContetnt = new StringMap<>();

  protected AvTree result;

  /**
   * Constructor by context.
   *
   * @param aContext ITsGuiContext - context.
   */
  public BaseOpcCommonDlmCfgGenerator( ITsGuiContext aContext ) {
    context = aContext;

    connection = context.get( ISkConnectionSupplier.class ).defConn();
  }

  @Override
  public IOpcCommonDlmCfgGenerator setUnits( IList<OpcToS5DataCfgUnit> aCfgUnits ) {
    TsIllegalStateRtException.checkFalse( result == null, "Result has been already generated" );
    cfgUnits = new ElemArrayList<>( aCfgUnits );
    return this;
  }

  @Override
  public IOpcCommonDlmCfgGenerator setConnection( ISkConnection aConn ) {
    TsIllegalStateRtException.checkFalse( result == null, "Result has been already generated" );
    connection = aConn;
    return this;
  }

  @Override
  public IOpcCommonDlmCfgGenerator setNodeIdConvertor( INodeIdConvertor aIdConvertor ) {
    TsIllegalStateRtException.checkFalse( result == null, "Result has been already generated" );
    idConvertor = aIdConvertor;
    return this;
  }

  @Override
  public IOpcCommonDlmCfgGenerator setGwidFilter( IGwidFilter aGwidFilter ) {
    TsIllegalStateRtException.checkFalse( result == null, "Result has been already generated" );
    gwidFilter = aGwidFilter;
    return this;
  }

  @Override
  public IOpcCommonDlmCfgGenerator setAdditionalProperties( IList<IStringList> aProperties ) {
    TsIllegalStateRtException.checkFalse( result == null, "Result has been already generated" );
    properties = new ElemArrayList<>( aProperties );
    return this;
  }

  @Override
  public IOpcCommonDlmCfgGenerator setComplexTagDetector( IComplexTagDetector aComplexTagDetector ) {
    TsIllegalStateRtException.checkFalse( result == null, "Result has been already generated" );
    complexTagDetector = aComplexTagDetector;
    return this;
  }

  @Override
  public IOpcCommonDlmCfgGenerator setParamValueSource( IDevCfgParamValueSource aParamValueSource ) {
    TsIllegalStateRtException.checkFalse( result == null, "Result has been already generated" );
    paramValueSource = aParamValueSource;
    return this;
  }

  @Override
  public IAvTree generate() {
    StringMap<IAvTree> nodes = new StringMap<>();

    // сложные теги
    IAvTree complexTagsTree = createComplexTags( cfgUnits );
    nodes.put( СOMPLEX_TAGS_DEFS, complexTagsTree );

    // перечисление возможных команд по классам
    IAvTree commandInfoesMassivTree = createCommandInfoes( cfgUnits );
    nodes.put( CMD_CLASS_DEFS, commandInfoesMassivTree );

    // команды
    IAvTree commandsMassivTree = createCommands( cfgUnits );
    nodes.put( CMD_DEFS, commandsMassivTree );

    // данные
    IAvTree datasMassivTree = createDatas( cfgUnits );
    nodes.put( DATA_DEFS, datasMassivTree );

    // атрибуты НСИ
    IAvTree rriAttrsArrayTree = createRriAttrs( cfgUnits, connection );
    nodes.put( RRI_DEFS, rriAttrsArrayTree );

    // события
    IAvTree eventsMassivTree = createEvents( cfgUnits );
    nodes.put( EVENT_DEFS, eventsMassivTree );

    IOptionSetEdit opSet = new OptionSet();

    opSet.setStr( ID_STR, DLM_ID_TEMPLATE );
    opSet.setStr( DESCRIPTION_STR, DLM_DESCR_TEMPLATE );

    result = AvTree.createSingleAvTree( DLM_CFG_NODE_ID_TEMPLATE, opSet, nodes );

    insertProperties( result, properties, context );

    return result;
  }

  private IAvTree createRriAttrs( IList<OpcToS5DataCfgUnit> aCfgUnits, ISkConnection aConnection ) {
    AvTree rriDefsTree = AvTree.createArrayAvTree();

    for( int i = 0; i < aCfgUnits.size(); i++ ) {
      OpcToS5DataCfgUnit unit = aCfgUnits.get( i );

      if( unit.getTypeOfCfgUnit() == ECfgUnitType.RRI ) {
        rriDefsTree.addElement( createRriAttrPin( unit, aConnection ) );
      }
    }

    if( rriDefsTree.arrayLength() == 0 ) {
      // возвращаем пустое дерево
      return rriDefsTree;
    }

    // создаем корневое дерево НСИ и вносим в него общие нстройки модуля
    StringMap<IAvTree> rriDefNodes = new StringMap<>();

    rriDefNodes.put( "rriNodes", rriDefsTree );

    // String refbookName = RBID_CMD_OPCUA;
    // ISkRefbookService skRefServ = (ISkRefbookService)aConnection.coreApi().getService( ISkRefbookService.SERVICE_ID
    // );
    // IStridablesList<ISkRefbookItem> rbItems = skRefServ.findRefbook( refbookName ).listItems();

    // ISkRefbookItem itemSetRRI = rbItems.findByKey( itemStridSetRRI );
    // ISkRefbookItem itemResetRRI = rbItems.findByKey( itemStridResetRRI );

    // int cmdIndexSetRRI = 600;// itemSetRRI != null ? itemSetRRI.attrs().getValue( RBATRID_CMD_OPCUA___INDEX ).asInt()
    // :
    // -1;
    // int cmdIndexResetRRI = 606;// itemSetRRI != null ? itemResetRRI.attrs().getValue( RBATRID_CMD_OPCUA___INDEX
    // ).asInt() : -1;

    IOptionSetEdit opSet = new OptionSet();

    // заполним описание настройки для модуля вцелом

    // device где node статуса НСИ
    opSet.setStr( RRI_STATUS_DEVICE_ID, "Set in doc properties" );

    // String nodeIdStatusRRI = tagsIdsByGwidContetnt.findByKey( gwidStatusRRI );
    // String strNodeIdStatusRRI = nodeIdStatusRRI != null ? nodeIdStatusRRI : defaultNodeIdStatusRRI;

    // node статуса НСИ Gwid CtrlSystem[CtrlSystem]rtd(rtdStatusRRI)
    opSet.setStr( RRI_STATUS_READ_NODE_ID, "Set in doc properties" );

    // справочника Cmd_OPCUA, strid CtrlSystem.SetRRI
    opSet.setInt( RRI_STATUS_CMD_SET_ID, -1 );

    // справочника Cmd_OPCUA, strid CtrlSystem.ResetRRI
    opSet.setInt( RRI_STATUS_CMD_RESET_ID, -1 );

    // для всех команд CtrlSystem[CtrlSystem]
    // String complexTagForSystem = complexTagsIdsBySkidsContetnt.findByKey( ctrSystemSkid );
    // if( complexTagForSystem == null ) {
    // complexTagForSystem = defaultComplexTagForSystem;
    // }

    opSet.setStr( RRI_STATUS_COMPLEX_NODE_ID, "Set in doc properties" );

    IAvTree retVal = AvTree.createSingleAvTree( DLM_CFG_NODE_ID_TEMPLATE, opSet, rriDefNodes );

    return retVal;
  }

  /**
   * TODO журнал описания команд прошит! И причесать все имена полей журнала.<br>
   * Создаёт конфигурацию одного НСИ атрибута (пина-тега) для подмодуля данных базового DLM.
   *
   * @param aUnit OpcToS5DataCfgUnit - описание данного (пина-тега). Предполагается что все параметры заполнены.
   * @param aConnection - соединение с сервером
   * @return IAvTree - конфигурация в стандартном виде.
   */

  private IAvTree createRriAttrPin( OpcToS5DataCfgUnit aUnit, ISkConnection aConnection ) {
    String pinId = aUnit.id();

    IOptionSetEdit rriAttrOpSet = new OptionSet();
    rriAttrOpSet.setStr( PIN_ID, pinId );

    IOptionSet opts = aUnit.getRealizationOpts();
    rriAttrOpSet.addAll( opts );

    IList<Gwid> gwids = aUnit.getDataGwids();

    for( int i = 0; i < gwids.size(); i++ ) {
      Gwid gwid = gwids.get( i );

      // класс
      String classId = gwid.classId();
      String objName = gwid.strid();
      String dataId = gwid.propId();

      // класс-объект-данное - остаётся как есть
      rriAttrOpSet.setStr( i == 0 ? CLASS_ID : CLASS_ID + i, classId );
      rriAttrOpSet.setStr( i == 0 ? OBJ_NAME : OBJ_NAME + i, objName );
      rriAttrOpSet.setStr( i == 0 ? RRI_ATTR_ID : RRI_ATTR_ID + i, dataId );
    }

    IList<Pair<String, String>> nodes = convertToNodesList2( aUnit.getDataNodes2(), idConvertor );

    // вместо пина - данные о теге
    // идентификатор OPC-устройства (драйвера)
    rriAttrOpSet.setStr( TAG_DEVICE_ID, nodes.first().left() );

    for( int i = 0; i < nodes.size(); i++ ) {
      String node = nodes.get( i ).right();

      // сам идентфикатор тега
      rriAttrOpSet.setStr( i == 0 ? TAG_ID : TAG_ID + i, node );
    }
    // комплексный тег и индексы команд OPC
    // Skid attrSkid = gwids.first().skid();

    // if( !complexTagsIdsBySkidsContetnt.hasKey( attrSkid ) ) {
    // // создаем комплексный тег, его узлы описаны после 0 элемента
    // IList<Pair<String, String>> cmdNodes = nodes.fetch( 1, nodes.size() - 1 );
    // String complexNodeId = createIfNeedAndGetComplexNodeId( cmdNodes, null );
    // complexTagsIdsBySkidsContetnt.put( attrSkid, complexNodeId );
    // }
    // String complexTagId = complexTagsIdsBySkidsContetnt.findByKey( attrSkid );

    if( complexTagDetector.hasComplexNode( aUnit, context ) ) {
      String complexNodeId = getComplexNodeId( aUnit );
      rriAttrOpSet.setStr( COMPLEX_TAG_ID, complexNodeId );
    }

    // индексы команды OPC получаем через справочник
    ISkRefbookService skRefServ = (ISkRefbookService)aConnection.coreApi().getService( ISkRefbookService.SERVICE_ID );
    if( skRefServ.findRefbook( RBID_RRI_OPCUA ) != null ) {
      IList<ISkRefbookItem> rbItems = skRefServ.findRefbook( RBID_RRI_OPCUA ).listItems();
      String rriAttrId = gwids.first().propId();
      String rriClassId = gwids.first().classId();

      // ищем все элементы справочника у которых есть такой rriAttrId
      IList<ISkRefbookItem> myRbItems = getMyRbItems( rbItems, rriClassId, rriAttrId );
      TsIllegalStateRtException.checkTrue( myRbItems.isEmpty(),
          "Can't find command index'es for attrId: %s in refbook %s", rriAttrId, RBID_RRI_OPCUA );
      TsIllegalStateRtException.checkTrue( myRbItems.size() > 2,
          "Find more than 2 commands for attrId: %s in refbook %s", rriAttrId, RBID_RRI_OPCUA );
      fillCmdIndex( rriAttrOpSet, myRbItems );
    }
    try {
      IAvTree retVal =
          AvTree.createSingleAvTree( String.format( RRI_ATTR_DEF_FORMAT, pinId ), rriAttrOpSet, IStringMap.EMPTY );
      return retVal;
    }
    catch( TsValidationFailedRtException ex ) {
      LoggerUtils.errorLogger().error( ex );
      throw ex;
    }

  }

  private static IList<ISkRefbookItem> getMyRbItems( IList<ISkRefbookItem> aRbItems, String aClassId,
      String aRriAttrId ) {
    IListEdit<ISkRefbookItem> retVal = new ElemArrayList<>();
    for( ISkRefbookItem rbItem : aRbItems ) {
      // Получаем класс aтрибута
      String classId = rbItem.strid().split( "\\." )[0];
      // Получаем значение aтрибута
      String paramId = rbItem.attrs().getValue( RBATRID_RRI_OPCUA___RRIID ).asString();
      if( classId.compareTo( aClassId ) == 0 && aRriAttrId.compareTo( paramId ) == 0 ) {
        retVal.add( rbItem );
      }
    }
    return retVal;
  }

  /**
   * Заполняет дерево описания атрибута НСИ командами на его изменение
   *
   * @param aAttrOpSet - дерево описания
   * @param aRbItems - элементы справочника описывающие команды изменения этого НСИ атрибуа
   */
  void fillCmdIndex( IOptionSetEdit aAttrOpSet, IList<ISkRefbookItem> aRbItems ) {
    if( aRbItems.size() == 1 ) {
      // команда с аргументом
      int cmdIndex = aRbItems.first().attrs().getValue( RBATRID_RRI_OPCUA___INDEX ).asInt();
      // проверяем переходы 0->1 & 1->0
      boolean flagUp = aRbItems.first().attrs().getValue( RBATRID_RRI_OPCUA___ON ).asBool();
      boolean flagDn = aRbItems.first().attrs().getValue( RBATRID_RRI_OPCUA___OFF ).asBool();
      if( !flagUp && !flagDn ) {
        aAttrOpSet.setInt( OPC_CMD_INDEX, cmdIndex );
      }
      else {
        if( flagUp ) {
          aAttrOpSet.setInt( OPC_CMD_INDEX_ON, cmdIndex );
        }
        else {
          aAttrOpSet.setInt( OPC_CMD_INDEX_OFF, cmdIndex );
        }
      }
    }
    else {
      // команды для установки и сброса булевых флагов
      for( ISkRefbookItem myRbItem : aRbItems ) {
        int cmdIndex = myRbItem.attrs().getValue( RBATRID_RRI_OPCUA___INDEX ).asInt();
        boolean flagUp = myRbItem.attrs().getValue( RBATRID_RRI_OPCUA___ON ).asBool();
        if( flagUp ) {
          aAttrOpSet.setInt( OPC_CMD_INDEX_ON, cmdIndex );
        }
        else {
          aAttrOpSet.setInt( OPC_CMD_INDEX_OFF, cmdIndex );
        }
      }
    }
  }

  private IAvTree createComplexTags( IList<OpcToS5DataCfgUnit> aCfgUnits ) {

    for( int i = 0; i < aCfgUnits.size(); i++ ) {
      OpcToS5DataCfgUnit unit = aCfgUnits.get( i );
      createComplexTagContent( unit );
    }

    AvTree pinsMassivTree = AvTree.createArrayAvTree();

    IStringList tagsIds = complexTagsContetnt.keys();

    for( String tagId : tagsIds ) {
      IOptionSetEdit pinOpSet1 = new OptionSet();

      pinOpSet1.setStr( СOMPLEX_TAG_ID, tagId );
      pinOpSet1.setStr( СOMPLEX_TAG_TYPE, SIMPLE_СOMPLEX_TAG_TYPE );

      // ссылки на составляющие теги
      IStringMapEdit<Pair<String, String>> tags = complexTagsContetnt.getByKey( tagId );

      for( String tagKey : tags.keys() ) {
        // dima 12.02.24 пустые теги игнорируем
        String nodeId = tags.getByKey( tagKey ).right();
        if( nodeId.isBlank() ) {
          continue;
        }
        pinOpSet1.setStr( tagKey, tags.getByKey( tagKey ).right() );// .toParseableString() );
      }
      pinOpSet1.setStr( TAG_DEVICE_ID, tags.values().first().left() );

      AvTree complexTagTree = AvTree.createSingleAvTree( tagId + ".def", pinOpSet1, IStringMap.EMPTY );
      pinsMassivTree.addElement( complexTagTree );

    }

    return pinsMassivTree;
  }

  private void createComplexTagContent( OpcToS5DataCfgUnit aUnit ) {
    if( !complexTagDetector.hasComplexNode( aUnit, context ) ) {
      return;
    }

    String complexNodeId = getComplexNodeId( aUnit );

    boolean isRRI = aUnit.getTypeOfCfgUnit() == ECfgUnitType.RRI;

    IList<Pair<String, String>> dataNodes = convertToNodesList2( aUnit.getDataNodes2(), idConvertor );

    if( isRRI ) {
      dataNodes = dataNodes.fetch( 1, dataNodes.size() - 1 );
    }

    // последний - фидбак
    String feedBackNode = dataNodes.last().right();

    // проверка наличия этого сложного тега
    IStringMapEdit<Pair<String, String>> cTagContent = complexTagsContetnt.findByKey( complexNodeId );

    // если ещё не встречался - создать
    if( cTagContent == null ) {
      cTagContent = new StringMap<>();

      // первый node - командный (по нему же - мапирование)
      String cmdIdNode = dataNodes.first().right();

      String deviceId = dataNodes.first().left();

      // командный
      cTagContent.put( СT_WRITE_ID_TAG, new Pair<>( deviceId, cmdIdNode ) );
      // фидбак
      cTagContent.put( СT_READ_FEEDBACK_TAG, new Pair<>( deviceId, feedBackNode ) );
      // dima 08.02.24 теперь всегда приходят теги с параметрами
      // первым идет int
      String argIntTagId = String.format( СT_WRITE_VAL_TAG_FORMAT, EAtomicType.INTEGER.id().toLowerCase() );
      cTagContent.put( argIntTagId, new Pair<>( deviceId, dataNodes.get( 1 ).right() ) );
      // вторым float
      String argFloatTagId = String.format( СT_WRITE_VAL_TAG_FORMAT, EAtomicType.FLOATING.id().toLowerCase() );
      cTagContent.put( argFloatTagId, new Pair<>( deviceId, dataNodes.get( 2 ).right() ) );

      complexTagsContetnt.put( complexNodeId, cTagContent );
    }
    else {
      IOptionSet unitOpts = aUnit.getRealizationOpts();
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

      // проверить актуальность данного тега
      // проверка содержания тега
      // сначала фидбак
      TsIllegalArgumentRtException.checkFalse( cTagContent.hasKey( СT_READ_FEEDBACK_TAG )
          && cTagContent.getByKey( СT_READ_FEEDBACK_TAG ).right().equals( feedBackNode ) );

      // далее проверяем и добавляем теги параметров
      if( dataNodes.size() > 2 && cmdArgType != null ) {
        String paramTagId = String.format( СT_WRITE_VAL_TAG_FORMAT, cmdArgType.id().toLowerCase() );
        if( cTagContent.hasKey( paramTagId ) ) {
          // проверка
          TsIllegalArgumentRtException
              .checkFalse( cTagContent.getByKey( paramTagId ).right().equals( dataNodes.get( 1 ).right() ) );
        }
        else {
          // добавление
          cTagContent.put( paramTagId, dataNodes.get( 1 ) );
        }
      }
    }
  }

  private IAvTree createEvents( IList<OpcToS5DataCfgUnit> aCfgUnits ) {
    AvTree eventsMassivTree = AvTree.createArrayAvTree();

    for( int i = 0; i < aCfgUnits.size(); i++ ) {
      OpcToS5DataCfgUnit unit = aCfgUnits.get( i );

      if( unit.getTypeOfCfgUnit() == ECfgUnitType.EVENT ) {
        if( !isSuited( unit.getDataGwids() ) ) {
          continue;
        }
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
  private IAvTree createEvent( OpcToS5DataCfgUnit aUnit ) {
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

    IList<Pair<String, String>> nodes = convertToNodesList2( aUnit.getDataNodes2(), idConvertor );
    String node = nodes.first().right();
    String deviceId = nodes.first().left();

    pinOpSet1.setStr( TAG_DEVICE_ID, deviceId );
    pinOpSet1.setStr( TAG_ID, node );

    IOptionSet opts = aUnit.getRealizationOpts();
    pinOpSet1.addAll( opts );

    IAvTree triggerTree =
        AvTree.createSingleAvTree( String.format( EVENT_TRIGGER_DEF_FORMAT, aUnit.id() ), pinOpSet1, IStringMap.EMPTY );

    return triggerTree;
  }

  private IAvTree createDatas( IList<OpcToS5DataCfgUnit> aCfgUnits ) {
    AvTree pinsMassivTree = AvTree.createArrayAvTree();
    int suited = 1;
    for( int i = 0; i < aCfgUnits.size(); i++ ) {
      OpcToS5DataCfgUnit unit = aCfgUnits.get( i );
      // dima 29.12.24 ignore empty unit
      if( unit.getDataGwids().isEmpty() ) {
        continue;
      }
      // dima 16.05.25 filter only my TKA
      if( !isSuited( unit.getDataGwids() ) ) {
        continue;
      }

      if( unit.getTypeOfCfgUnit() == ECfgUnitType.DATA ) {
        pinsMassivTree.addElement( createDataPin( unit ) );
      }
    }

    return pinsMassivTree;
  }

  /**
   * Создаёт конфигурацию одного данного (пина-тега) для подмодуля данных базового DLM.
   *
   * @param aUnit OpcToS5DataCfgUnit - описание данного (пина-тега). Предполагается что все параметры заполнены.
   * @return IAvTree - конфигурация в стандартном виде.
   */

  private IAvTree createDataPin( OpcToS5DataCfgUnit aUnit ) {
    String pinId = aUnit.id();

    IOptionSetEdit pinOpSet1 = new OptionSet();
    pinOpSet1.setStr( PIN_ID, pinId );

    IOptionSet opts = aUnit.getRealizationOpts();

    // Вот это место покрывает 90% потребностей в доп параметрах
    pinOpSet1.addAll( opts );

    // координаты в s5
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

    IList<Pair<String, String>> nodes = convertToNodesList2( aUnit.getDataNodes2(), idConvertor );

    for( int i = 0; i < nodes.size(); i++ ) {
      String node = nodes.get( i ).right();

      // сам идентфикатор тега
      pinOpSet1.setStr( i == 0 ? TAG_ID : TAG_ID + i, node );

      // идентификатор OPC-устройства (драйвера) - один для всех тегов юнита
      pinOpSet1.setStr( TAG_DEVICE_ID, nodes.first().left() );
    }

    // сохраняем соответствие для потомков (вдруг пригодится) - для каждого gwid
    if( nodes.size() == 1 ) {
      for( int i = 0; i < gwids.size(); i++ ) {
        Gwid gwid = gwids.get( i );

        // tagsIdsByGwidContetnt.put( gwid, nodes.first().right() );
      }
    }

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

  private IAvTree createCommands( IList<OpcToS5DataCfgUnit> aCfgUnits ) {
    AvTree commandsMassivTree = AvTree.createArrayAvTree();

    for( int i = 0; i < aCfgUnits.size(); i++ ) {
      OpcToS5DataCfgUnit unit = aCfgUnits.get( i );

      if( unit.getTypeOfCfgUnit() == ECfgUnitType.COMMAND ) {
        if( !isSuited( unit.getDataGwids() ) ) {
          continue;
        }
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

  private IAvTree createCommand( OpcToS5DataCfgUnit aUnit ) {
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

    // параметры реализации
    pinOpSet1.addAll( aUnit.getRealizationOpts() );

    // проверка на сложную команду с заменой на сложный тег
    // IOptionSet unitOpts = aUnit.getRealizationOpts();
    // if( unitOpts.hasKey( OP_CMD_JAVA_CLASS.id() )
    // && unitOpts.getStr( OP_CMD_JAVA_CLASS ).equals( COMMANDS_JAVA_CLASS_VALUE_COMMAND_BY_ONE_TAG_EXEC ) ) {

    // поментяь класс , оставить один сложный тег, создать его, если надо, проверить правильность состава сложного
    // тега
    // добавить теги параметров, если не совпадает фидбак - выкинуть ошибку
    // IOptionSetEdit replacedRealizationOpts = new OptionSet( unitOpts );
    // OP_CMD_JAVA_CLASS.setValue( pinOpSet1, avStr( COMMANDS_JAVA_CLASS_VALUE_COMMAND_BY_COMPLEX_TAG_EXEC ) );

    if( complexTagDetector.hasComplexNode( aUnit, context ) ) {
      String complexNodeId = getComplexNodeId( aUnit );
      OP_COMPLEX_TAG_ID.setValue( pinOpSet1, avStr( complexNodeId ) );

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

    IList<Pair<String, String>> nodes = convertToNodesList2( aUnit.getDataNodes2(), idConvertor );

    for( int i = 0; i < nodes.size(); i++ ) {
      String node = nodes.get( i ).right();
      String device = nodes.get( i ).left();

      IOptionSetEdit nodeOpSet = new OptionSet();

      nodeOpSet.setStr( TAG_DEVICE_ID, device );
      nodeOpSet.setStr( TAG_ID, node );
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

  private String getComplexNodeId( OpcToS5DataCfgUnit aUnit ) {
    // старое
    // String complexNodeId =
    // createIfNeedAndGetComplexNodeId( convertToNodesList2( aUnit.getDataNodes2(), idConvertor ), cmdArgType );

    boolean isRRI = aUnit.getTypeOfCfgUnit() == ECfgUnitType.RRI;

    IAvList nodes = aUnit.getDataNodes2();
    if( nodes.isEmpty() || (isRRI && nodes.size() < 2) ) {
      return null;
    }

    IAtomicValue nodeId = isRRI ? nodes.get( 1 ) : nodes.get( 0 );

    // первый node - командный (по нему же - мапирование)
    String cmdIdNode = idConvertor.converToNodeId( nodeId ).right();

    // генерим идентификатор
    String complexNodeId = "synthetic_" + cmdIdNode;
    complexNodeId = complexNodeId.replaceAll( ";", "_" ).replaceAll( "=", "_" );
    return complexNodeId;
  }

  private IAvTree createCommandInfoes( IList<OpcToS5DataCfgUnit> aCfgUnits ) {
    AvTree commandInfoesMassivTree = AvTree.createArrayAvTree();

    IMapEdit<String, IListEdit<String>> objsByClass = new ElemMap<>();
    IMapEdit<String, IListEdit<String>> cmdsByClass = new ElemMap<>();

    // IList<OpcToS5DataCfgUnit> cfgUnits = aDoc.dataUnits();

    for( int i = 0; i < aCfgUnits.size(); i++ ) {
      OpcToS5DataCfgUnit unit = aCfgUnits.get( i );

      if( unit.getTypeOfCfgUnit() == ECfgUnitType.COMMAND ) {
        IList<Gwid> gwids = unit.getDataGwids();
        for( Gwid gwid : gwids ) {
          if( !isSuited( new SingleItemList( gwid ) ) ) {
            continue;
          }
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

  private static void insertProperties( IAvTree avTree, IList<IStringList> aProperties, ITsGuiContext aContext ) {
    for( IStringList aProp : aProperties ) {
      Iterator<String> iterator = aProp.iterator();
      String name = iterator.next();
      String value = iterator.next();
      String path = iterator.next();

      IList<AvTree> destinations = getPropertyDestination( avTree, path );

      for( AvTree dest : destinations ) {
        dest.fieldsEdit().setStr( name, parseAndConvertValue( value, aContext ) );
      }
    }
  }

  private static String parseAndConvertValue( String aValue, ITsGuiContext aContext ) {
    String value = aValue;

    while( value.contains( "{$" ) ) {
      int sIndex = value.indexOf( "{$" );
      int eIndex = value.indexOf( "}", sIndex );
      if( eIndex > 0 ) {
        String paramName = value.substring( sIndex + 2, eIndex );
        IAtomicValue paramVal = aContext.params().findByKey( paramName );

        if( paramVal != null && paramVal.isAssigned() ) {
          value = value.replace( "{$" + paramName + "}", paramVal.asString() );
        }
        else {
          value = value.replace( "{$" + paramName + "}", "!NotFoundParamValue!" );
        }
      }
      else {
        value = value.replace( "{$", "!NotCompleteParamDef!" );
      }
    }

    return value;
  }

  private static IList<AvTree> getPropertyDestination( IAvTree avTree, String aPath ) {
    StringTokenizer st = new StringTokenizer( aPath, "#" );

    AvTree result = (AvTree)avTree;
    while( st.hasMoreTokens() ) {
      String token = st.nextToken();

      // массив
      if( token.startsWith( "[" ) && token.endsWith( "]" ) ) {
        String arrayIndexStr = token.substring( 1, token.length() - 1 );
        if( arrayIndexStr.equals( "*" ) ) {
          // any
          // здесь нужно ветвить и лепить массив
          // слепить оставшиеся токены
          String newPath = new String();
          String add = new String();

          while( st.hasMoreTokens() ) {
            String t = st.nextToken();
            newPath += add + t;
            add = "#";
          }

          IListEdit<AvTree> resultAll = new ElemArrayList<>();
          for( int i = 0; i < result.arrayLength(); i++ ) {
            IList<AvTree> res = getPropertyDestination( result.arrayElement( i ), newPath );
            resultAll.addAll( res );
          }

          return resultAll;
        }
        else
          if( arrayIndexStr.equals( "+" ) ) {
            // first
            result = (AvTree)result.arrayElement( 0 );
          }
          else {
            // index
            result = (AvTree)result.arrayElement( Integer.parseInt( arrayIndexStr ) );
          }
      }
      else {
        result = (AvTree)result.nodes().getByKey( token );
      }
    }

    return new ElemArrayList<>( result );
  }

  private boolean isSuited( IList<Gwid> aDataGwids ) {
    for( Gwid gwid : aDataGwids ) {
      if( gwidFilter.isSuited( gwid ) ) {
        return true;
      }
      // if( gwid.skid().classId().indexOf( "InterfaceConverterBox" ) >= 0
      // && gwid.propId().indexOf( TKA_TEMPLATE ) >= 0 ) {
      // return true;
      // }
      // if( gwid.skid().strid().indexOf( TKA_TEMPLATE ) >= 0 ) {
      // return true;
      // }
    }
    return false;
  }

}
