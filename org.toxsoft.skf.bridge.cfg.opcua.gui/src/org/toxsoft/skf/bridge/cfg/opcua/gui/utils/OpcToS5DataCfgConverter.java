package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.validator.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;

/**
 * Converter of cfg to avtree and back
 *
 * @author max
 */
public class OpcToS5DataCfgConverter {

  private static final String DLM_CFG_NODE_ID_TEMPLATE                          = "dispatch.dlm.cfg";
  private static final String DESCRIPTION_STR                                   = "description";
  private static final String ID_STR                                            = "id";
  private static final String DATA_DEF_FORMAT                                   = "data.%s.def";
  private static final String DLM_ID_TEMPLATE                                   =
      "ru.toxsoft.l2.dlm.tags.common.OpcBridgeDlm";
  private static final String DLM_DESCR_TEMPLATE                                =
      "ru.toxsoft.l2.dlm.tags.common.OpcBridgeDlm";
  private static final String ONE_TO_ONE_DATA_TRANSMITTER_FACTORY_CLASS         =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.data.OneToOneDataTransmitterFactory";
  private static final String ONE_INT_TO_ONE_BIT_DATA_TRANSMITTER_FACTORY_CLASS =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.data.SingleIntToSingleBoolDataTransmitterFactory";
  private static final String OPC_TAG_DEVICE                                    = "opc2s5.bridge.collection.id";

  /**
   * Начало блока, отвечающего за конфигурацию данных.
   */
  private static final String DATA_DEFS = "dataDefs";

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

  private OpcToS5DataCfgConverter() {

  }

  public static IAvTree convertToTree( OpcToS5DataCfgDoc aDoc ) {
    StringMap<IAvTree> nodes = new StringMap<>();

    // данные
    IAvTree datasMassivTree = createDatas( aDoc );
    nodes.put( DATA_DEFS, datasMassivTree );

    IOptionSetEdit opSet = new OptionSet();

    opSet.setStr( ID_STR, DLM_ID_TEMPLATE );
    opSet.setStr( DESCRIPTION_STR, DLM_DESCR_TEMPLATE );

    IAvTree dstParams = AvTree.createSingleAvTree( DLM_CFG_NODE_ID_TEMPLATE, opSet, nodes );

    return dstParams;
  }

  /**
   * Создаёт конфигурацию всех данных для подмодуля данных базового DLM.
   *
   * @return IAvTree - конфигурация в стандартном виде.
   */
  private static IAvTree createDatas( OpcToS5DataCfgDoc aDoc ) {
    AvTree pinsMassivTree = AvTree.createArrayAvTree();

    // Set<String> alreadyAddedTags = new HashSet<>();

    IList<OpcToS5DataCfgUnit> cfgUnits = aDoc.dataUnits();

    for( int i = 0; i < cfgUnits.size(); i++ ) {
      OpcToS5DataCfgUnit unit = cfgUnits.get( i );

      pinsMassivTree.addElement( createDataPin( unit ) );
    }

    return pinsMassivTree;
  }

  /**
   * Создаёт конфигурацию одного данного (пина-тега) для подмодуля данных базового DLM.
   *
   * @param aUnit OpcToS5DataCfgUnit - описание данного (пина-тега). Предполагается что все параметры заполнены.
   * @return IAvTree - конфигурация в стандартном виде.
   */
  @SuppressWarnings( "unchecked" )
  private static IAvTree createDataPin( OpcToS5DataCfgUnit aUnit ) {
    String pinId = aUnit.id();

    IOptionSetEdit pinOpSet1 = new OptionSet();
    pinOpSet1.setStr( PIN_ID, pinId );
    pinOpSet1.setStr( JAVA_CLASS, ONE_TO_ONE_DATA_TRANSMITTER_FACTORY_CLASS );

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

    // признак текущности, историчности
    pinOpSet1.setBool( IS_HIST, true );
    pinOpSet1.setBool( IS_CURR, true );

    // признак синхронности заменён на конкретное значение периода синхронизации
    if( isSynch ) {
      pinOpSet1.setLong( SYNCH_PERIOD, 1000L );
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
}
