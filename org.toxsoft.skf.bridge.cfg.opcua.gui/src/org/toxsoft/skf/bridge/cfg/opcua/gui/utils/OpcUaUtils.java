package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.impl.DataDef.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skide.plugin.exconn.ISkidePluginExconnSharedResources.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.function.*;
import java.util.regex.*;

import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.operation.*;
import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.api.config.*;
import org.eclipse.milo.opcua.sdk.client.api.identity.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.stack.core.security.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.eclipse.milo.opcua.stack.core.util.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.log4j.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.OptionSet;
import org.toxsoft.core.tslib.bricks.apprefs.*;
import org.toxsoft.core.tslib.bricks.apprefs.impl.*;
import org.toxsoft.core.tslib.bricks.geometry.impl.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.*;
import org.toxsoft.core.txtproj.lib.storage.*;
import org.toxsoft.core.txtproj.lib.workroom.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.bridge.cfg.opcua.service.impl.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * Utils of OPC UA server connections.
 *
 * @author max
 * @author dima
 */
public class OpcUaUtils {

  private static final String COMMANDS_JAVA_CLASS_VALUE_COMMAND_BY_ONE_TAG_EXEC =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.commands.ValCommandByOneTagWithParamExec";

  public static final String CFG_UNIT_REALIZATION_TYPE_VALUE_COMMAND_BY_ONE_TAG = "val.command.one.tag";

  public static final String CFG_UNIT_REALIZATION_TYPE_VALUE_COMMAND = "value.command";

  private static final String COMMANDS_JAVA_CLASS_VALUE_COMMAND_EXEC =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.commands.ValueCommandExec";

  private static final String DATA_JAVA_CLASS_ONE_TO_ONE_DATA_TRANSMITTER_FACTORY =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.data.OneToOneDataTransmitterFactory";

  private static final String EVENTS_ONE_TAG_CHANGED_PARAM_FORMER =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OneTagToChangedParamFormer";

  private static final String EVENTS_ONE_TAG_TO_ONE_PARAM_FORMER =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OneTagToOneParamFormer";

  private static final String EVENTS_JAVA_CLASS_TAG_SWITCH_CONDITION =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OneTagSwitchEventCondition";

  private static final String EVENTS_JAVA_CLASS_TAG_VALUE_CHANGED_CONDITION =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OneTagChangedEventCondition";

  private static final String EVENTS_JAVA_CLASS_OPC_TAGS_EVENT_SENDER =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OpcTagsEventSender";

  public static final String CFG_UNIT_REALIZATION_TYPE_TAG_VALUE_CHANGED = "opc.tags.event.sender.tag.value.changed";

  public static final String CFG_UNIT_REALIZATION_TYPE_BIT_SWITCH_EVENT = "opc.tags.event.sender.bit.switch";

  public static final String CFG_UNIT_REALIZATION_TYPE_SWITCH_EVENT = "opc.tags.event.sender.switch";

  private static StringMap<IList<UaTreeNode>> section2NodesList = new StringMap<>();

  public static final String CFG_UNIT_REALIZATION_TYPE_ONT_TO_ONE_DATA = "ont.to.one.data";

  public static final String CFG_UNIT_REALIZATION_TYPE_ONE_INT_TO_ONE_BIT_DATA = "int.to.byte.data";

  private static final String DATA_JAVA_CLASS_ONE_INT_TO_ONE_BIT_DATA_TRANSMITTER_FACTORY =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.data.SingleIntToSingleBoolDataTransmitterFactory";

  /**
   * id secton for cached OPC UA nodes
   */
  public static final String SECTID_OPC_UA_NODES_PREFIX = "cached.opc.ua.nodes"; //$NON-NLS-1$

  /**
   * id secton for store links UaNode->Skid meta info
   */
  public static final String SECTID_OPC_UA_NODES_2_SKIDS = "opc.ua.nodes2skids"; //$NON-NLS-1$

  /**
   * id secton for store links UaNode->RtdGwid
   */
  public static final String SECTID_OPC_UA_NODES_2_RTD_GWIDS = "opc.ua.nodes2rtd.gwids"; //$NON-NLS-1$

  /**
   * id secton for store links UaNode->EvtGwid
   */
  public static final String SECTID_OPC_UA_NODES_2_EVT_GWIDS = "opc.ua.nodes2evt.gwids"; //$NON-NLS-1$

  /**
   * id secton for store links CmdGwid->UaNodes
   */
  public static final String SECTID_CMD_GWIDS_2_OPC_UA_NODES = "cmd.gwid2opc.ua.nodes"; //$NON-NLS-1$

  private static final String                       CLIENT_APP_NAME                            =
      "eclipse milo opc-ua client";
  private static final String                       CLIENT_APP_URI                             =
      "urn:eclipse:milo:examples:client";
  private static final String                       ERROR_FORMAT_UNABLE_TO_CREATE_SECURITY_DIR =
      "unable to create security dir: %s";
  private static final String                       SYS_PROP_JAVA_IO_TMPDIR_DEF_VAL            = "security";
  private static final String                       SYS_PROP_JAVA_IO_TMPDIR                    = "java.io.tmpdir";
  private static final Map<String, Gwid>            nodeId2GwidMap                             = new HashMap<>();
  private static final Map<String, NodeId>          gwid2NodeIdMap                             = new HashMap<>();
  private static final Map<Skid, NodeId>            skid2NodeIdMap                             = new HashMap<>();
  private static final Map<String, CmdGwid2UaNodes> cmdGwid2NodeIdsMap                         = new HashMap<>();

  /**
   * Hided constructor.
   */
  private OpcUaUtils() {

  }

  /**
   * Журнал работы
   */
  private static ILogger logger = LoggerWrapper.getLogger( OpcUaUtils.class.getName() );

  /**
   * Creates and returns opc ua client formed according to connection configuration.
   *
   * @param aCfg IOpcUaServerConnCfg - conn configuration.
   * @return OpcUaClient - opc ua client.
   * @throws Exception - exception during connection try.
   */
  public static OpcUaClient createClient( IOpcUaServerConnCfg aCfg )
      throws Exception {
    Path securityTempDir = Paths.get( System.getProperty( SYS_PROP_JAVA_IO_TMPDIR ), SYS_PROP_JAVA_IO_TMPDIR_DEF_VAL );
    Files.createDirectories( securityTempDir );
    if( !Files.exists( securityTempDir ) ) {
      throw new TsIllegalStateRtException( ERROR_FORMAT_UNABLE_TO_CREATE_SECURITY_DIR, securityTempDir );
    }

    logger.info( "security temp dir: %s", securityTempDir.toAbsolutePath().toString() ); //$NON-NLS-1$

    KeyStoreLoader loader = new KeyStoreLoader().load( securityTempDir );

    Function<List<EndpointDescription>, Optional<EndpointDescription>> selectEndpoint = aEendpoints -> {
      Optional<EndpointDescription> result = aEendpoints.stream().filter( endpointFilter() ).findFirst();
      return result;
    };

    Function<OpcUaClientConfigBuilder, OpcUaClientConfig> buildConfig = aConfigBuilder -> {
      OpcUaClientConfig result = aConfigBuilder.setApplicationName( LocalizedText.english( CLIENT_APP_NAME ) )
          .setApplicationUri( CLIENT_APP_URI ).setCertificate( loader.getClientCertificate() )
          .setKeyPair( loader.getClientKeyPair() ).setIdentityProvider( getIdentityProvider( aCfg ) )
          .setRequestTimeout( Unsigned.uint( 5000 ) ).build();
      return result;
    };

    return OpcUaClient.create( getEndpointUrl( aCfg ), selectEndpoint, buildConfig );
  }

  static String getEndpointUrl( IOpcUaServerConnCfg aCfg ) {
    return aCfg.host();
    // return "opc.tcp://192.168.153.1:4850"; //poligon
    // return "opc.tcp://localhost:12686/milo";

  }

  static Predicate<EndpointDescription> endpointFilter() {
    return e -> true;
  }

  static SecurityPolicy getSecurityPolicy() {
    return SecurityPolicy.None;
  }

  static IdentityProvider getIdentityProvider( IOpcUaServerConnCfg aCfg ) {
    if( aCfg.login().length() > 0 ) {
      // return new UsernameProvider("admin","123"); //poligon
      return new UsernameProvider( aCfg.login(), aCfg.passward() );
    }

    return new AnonymousProvider();
  }

  /**
   * @param aEntity узел значения переменной
   * @return класс типа данных значения узла
   */
  public static Class<?> getNodeDataTypeClass( UaVariableNode aEntity ) {
    Class<?> retVal = null;
    // получение значения узла
    DataValue dataValue = aEntity.getValue();
    // тут получаем Variant
    Variant variant = dataValue.getValue();
    Optional<ExpandedNodeId> dataTypeNode = variant.getDataType();
    if( dataTypeNode.isPresent() ) {
      ExpandedNodeId expNodeId = dataTypeNode.get();
      // TODO разобраться с отображением не числовых типов
      if( expNodeId.getType() == IdType.Numeric ) {
        UInteger id = (UInteger)expNodeId.getIdentifier();
        NodeId nodeId = new NodeId( expNodeId.getNamespaceIndex(), id );
        Class<?> clazz = TypeUtil.getBackingClass( nodeId );
        retVal = clazz;
      }
    }
    return retVal;
  }

  /**
   * Получить Variant из текстового значения
   *
   * @param aClazz класс переменной
   * @param aVal текстовое значение
   * @return значение {@link Variant } применяемое в milo для передачи/записи значений тегов
   */
  public static Variant getVariant( Class<?> aClazz, String aVal ) {
    if( aClazz.equals( Boolean.class ) ) {
      return new Variant( Boolean.valueOf( aVal ) );
    }
    if( aClazz.equals( Byte.class ) ) {
      return new Variant( Byte.valueOf( aVal ) );
    }
    if( aClazz.equals( UByte.class ) ) {
      return new Variant( UByte.valueOf( aVal ) );
    }
    if( aClazz.equals( Short.class ) ) {
      return new Variant( Short.valueOf( aVal ) );
    }
    if( aClazz.equals( UShort.class ) ) {
      return new Variant( UShort.valueOf( aVal ) );
    }
    if( aClazz.equals( Integer.class ) ) {
      return new Variant( Integer.valueOf( aVal ) );
    }
    if( aClazz.equals( UInteger.class ) ) {
      return new Variant( UInteger.valueOf( aVal ) );
    }
    if( aClazz.equals( Long.class ) ) {
      return new Variant( Long.valueOf( aVal ) );
    }
    if( aClazz.equals( ULong.class ) ) {
      return new Variant( ULong.valueOf( aVal ) );
    }
    if( aClazz.equals( Float.class ) ) {
      return new Variant( Float.valueOf( aVal ) );
    }
    return Variant.NULL_VALUE;
  }

  /**
   * Получить соответствующий тип для работы в Sk из описания OPC UA типа
   *
   * @param aClazz класс переменной
   * @return значение {@link EAtomicType } тип данных применяемый в Sk
   */
  public static EAtomicType getAtomicType( Class<?> aClazz ) {
    if( aClazz.equals( Boolean.class ) ) {
      return EAtomicType.BOOLEAN;
    }
    if( aClazz.equals( Byte.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( UByte.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( Short.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( UShort.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( Integer.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( UInteger.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( Long.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( ULong.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( Float.class ) ) {
      return EAtomicType.FLOATING;
    }
    return EAtomicType.NONE;
  }

  /**
   * Update list of links UaNode->Skid {@link UaNode2Skid} to store in inner storage
   *
   * @param aContext app context
   * @param aNodes2Skids list of links UaNode->Skid
   * @param aSectId section id to store data
   */
  public static void updateNodes2SkidsInStore( ITsGuiContext aContext, IList<UaNode2Skid> aNodes2Skids,
      String aSectId ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IList<UaNode2Skid> oldList = loadNodes2Skids( aContext, aSectId );
    IListEdit<UaNode2Skid> newList = new ElemArrayList<>();
    // тут приходится перекладывать из одного списка в другой
    IListEdit<IContainNodeId> tmpList = new ElemArrayList<>();
    for( UaNode2Skid node : aNodes2Skids ) {
      tmpList.add( node );
    }
    for( UaNode2Skid oldItem : oldList ) {
      if( !containsNodeIn( tmpList, oldItem ) ) {
        newList.add( oldItem );
      }
    }
    newList.addAll( aNodes2Skids );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    storage.writeColl( aSectId, newList, UaNode2Skid.KEEPER );
    // чистим кеш
    skid2NodeIdMap.clear();
  }

  /**
   * Update list of links UaNode->RtdGwid {@link UaNode2Gwid} to store in inner storage
   *
   * @param aContext app context
   * @param aNodes2Gwids list of links UaNode->Gwid
   * @param aSectId section id to store data
   */
  public static <T extends UaNode2Gwid> void updateNodes2GwidsInStore( ITsGuiContext aContext, IList<T> aNodes2Gwids,
      String aSectId, IEntityKeeper<T> aKeeper ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IList<T> oldList = loadNodes2Gwids( aContext, aSectId, aKeeper );
    IListEdit<T> newList = new ElemArrayList<>();
    // тут приходится перекладывать из одного списка в другой
    IListEdit<IContainNodeId> tmpList = new ElemArrayList<>();
    for( T node : aNodes2Gwids ) {
      tmpList.add( node );
    }
    for( T oldItem : oldList ) {
      if( !containsNodeIn( tmpList, oldItem ) ) {
        newList.add( oldItem );
      }
    }
    newList.addAll( aNodes2Gwids );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    // storage.writeColl( aSectId, newList, UaNode2Gwid.KEEPER ); IEntityKeeper<T> aKeeper
    storage.writeColl( aSectId, newList, aKeeper );
  }

  private static boolean containsNodeIn( IList<IContainNodeId> aNodes2Gwids, IContainNodeId aOldItem ) {
    for( IContainNodeId node : aNodes2Gwids ) {
      if( node.getNodeId().equals( aOldItem.getNodeId() ) ) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param aContext app context
   * @return list of links {@link UaNode2Gwid } UaNode->Gwid
   */
  public static IList<UaNode2Gwid> loadNodes2RtdGwids( ITsGuiContext aContext ) {
    return loadNodes2Gwids( aContext, SECTID_OPC_UA_NODES_2_RTD_GWIDS, UaNode2Gwid.KEEPER );
  }

  /**
   * @param aContext app context
   * @return list of links {@link UaNode2Gwid } UaNode->Gwid
   */
  public static IList<UaNode2EventGwid> loadNodes2EvtGwids( ITsGuiContext aContext ) {
    return loadNodes2Gwids( aContext, SECTID_OPC_UA_NODES_2_EVT_GWIDS, UaNode2EventGwid.KEEPER );
  }

  /**
   * @param <T> расширение класса {@link UaNode2Gwid}
   * @param aContext app context
   * @param aSectId id of section where data stored
   * @param aKeeper хранитель сущностей типа {@link UaNode2Gwid} и его наследников
   * @return list of links {@link UaNode2Gwid } UaNode->Gwid
   */
  static <T extends UaNode2Gwid> IList<T> loadNodes2Gwids( ITsGuiContext aContext, String aSectId,
      IEntityKeeper<T> aKeeper ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    // IList<T> retVal = new ElemArrayList<>( storage.readColl( aSectId, UaNode2Gwid.KEEPER ) ); aKeeper
    IList<T> retVal = new ElemArrayList<>( storage.readColl( aSectId, aKeeper ) );
    return retVal;
  }

  /**
   * @param aContext app context
   * @param aSectId id of section where data stored
   * @return list of links {@link UaNode2Gwid } UaNode->Skid
   */
  static IList<UaNode2Skid> loadNodes2Skids( ITsGuiContext aContext, String aSectId ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IList<UaNode2Skid> retVal = new ElemArrayList<>( storage.readColl( aSectId, UaNode2Skid.KEEPER ) );
    return retVal;
  }

  /**
   * Get NodeId By Skid
   *
   * @param aContext app context
   * @param aSkid OPC UA nodeId
   * @return NodeId linked to Skid or null
   */
  public static NodeId nodeBySkid( ITsGuiContext aContext, Skid aSkid ) {
    NodeId retVal = null;
    if( skid2NodeIdMap.isEmpty() ) {
      // пустая карта кеша, загружаем
      IList<UaNode2Skid> nodes2Skids = loadNodes2Skids( aContext, SECTID_OPC_UA_NODES_2_SKIDS );
      for( UaNode2Skid node2Skid : nodes2Skids ) {
        skid2NodeIdMap.put( node2Skid.skid(), node2Skid.getNodeId() );
      }
    }
    if( skid2NodeIdMap.containsKey( aSkid ) ) {
      retVal = skid2NodeIdMap.get( aSkid );
    }
    return retVal;
  }

  /**
   * Get Gwid by NodeId
   *
   * @param aContext app context
   * @param aNodeId OPC UA nodeId
   * @return Gwid linked to UaNode or null
   */
  public static Gwid uaNode2rtdGwid( ITsGuiContext aContext, NodeId aNodeId ) {
    Gwid retVal = null;
    if( nodeId2GwidMap.isEmpty() ) {
      // пустая карта кеша, загружаем
      IList<UaNode2Gwid> nodes2Gwids = loadNodes2Gwids( aContext, SECTID_OPC_UA_NODES_2_RTD_GWIDS, UaNode2Gwid.KEEPER );
      for( UaNode2Gwid node2Gwid : nodes2Gwids ) {
        String key = node2Gwid.getNodeId().toParseableString();
        nodeId2GwidMap.put( key, node2Gwid.gwid() );
      }
    }
    String nodeKey = aNodeId.toParseableString();
    if( nodeId2GwidMap.containsKey( nodeKey ) ) {
      retVal = nodeId2GwidMap.get( nodeKey );
    }
    return retVal;
  }

  /**
   * Get NodeId by Gwid
   *
   * @param aContext app context
   * @param aGwid rtData Gwid
   * @return NodeId linked to aGwid or null
   */
  public static NodeId rtdGwid2uaNode( ITsGuiContext aContext, Gwid aGwid ) {
    NodeId retVal = null;
    if( gwid2NodeIdMap.isEmpty() ) {
      // пустая карта кеша, загружаем
      IList<UaNode2Gwid> nodes2Gwids = loadNodes2Gwids( aContext, SECTID_OPC_UA_NODES_2_RTD_GWIDS, UaNode2Gwid.KEEPER );
      for( UaNode2Gwid node2Gwid : nodes2Gwids ) {
        String key = node2Gwid.gwid().asString();
        gwid2NodeIdMap.put( key, node2Gwid.getNodeId() );
      }
    }
    String gwidKey = aGwid.asString();
    if( gwid2NodeIdMap.containsKey( gwidKey ) ) {
      retVal = gwid2NodeIdMap.get( gwidKey );
    }
    return retVal;
  }

  /**
   * Add in inner storage list of {@link CmdGwid2UaNodes} links CmdGwid->UaNodes
   *
   * @param aContext app context
   * @param aCmdGwid2UaNodes list of links CmdGwid->UaNodes
   */
  public static void updateCmdGwid2NodesInStore( ITsGuiContext aContext, IList<CmdGwid2UaNodes> aCmdGwid2UaNodes ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IList<CmdGwid2UaNodes> oldList = loadCmdGwid2Nodes( aContext );
    // добавляем в список на запись только те элементы которых нет в новом списке
    IListEdit<CmdGwid2UaNodes> newList = new ElemArrayList<>();
    for( CmdGwid2UaNodes oldItem : oldList ) {
      if( !containsGwidIn( aCmdGwid2UaNodes, oldItem ) ) {
        newList.add( oldItem );
      }
    }
    newList.addAll( aCmdGwid2UaNodes );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    storage.writeColl( SECTID_CMD_GWIDS_2_OPC_UA_NODES, newList, CmdGwid2UaNodes.KEEPER );
  }

  private static boolean containsGwidIn( IList<CmdGwid2UaNodes> aCmdGwid2UaNodes, CmdGwid2UaNodes aOldItem ) {
    for( CmdGwid2UaNodes item : aCmdGwid2UaNodes ) {
      if( item.gwid().equals( aOldItem.gwid() ) ) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param aContext app context
   * @return list of links {@link CmdGwid2UaNodes } CmdGwid->UaNodes
   */
  public static IList<CmdGwid2UaNodes> loadCmdGwid2Nodes( ITsGuiContext aContext ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IList<CmdGwid2UaNodes> retVal =
        new ElemArrayList<>( storage.readColl( SECTID_CMD_GWIDS_2_OPC_UA_NODES, CmdGwid2UaNodes.KEEPER ) );
    return retVal;
  }

  /**
   * Get NodeIds by Gwid
   *
   * @param aContext app context
   * @param aGwid command Gwid
   * @return {@link CmdGwid2UaNodes} NodeIds linked to aGwid or null
   */
  public static CmdGwid2UaNodes cmdGwidu2Nodes( ITsGuiContext aContext, Gwid aGwid ) {
    CmdGwid2UaNodes retVal = null;
    if( cmdGwid2NodeIdsMap.isEmpty() ) {
      // пустая карта кеша, загружаем
      IList<CmdGwid2UaNodes> cmdGwid2NodesList = loadCmdGwid2Nodes( aContext );
      for( CmdGwid2UaNodes cmdGwid2Nodes : cmdGwid2NodesList ) {
        String key = cmdGwid2Nodes.gwid().asString();
        cmdGwid2NodeIdsMap.put( key, cmdGwid2Nodes );
      }
    }
    String gwidKey = aGwid.asString();
    if( cmdGwid2NodeIdsMap.containsKey( gwidKey ) ) {
      retVal = cmdGwid2NodeIdsMap.get( gwidKey );
    }
    return retVal;
  }

  public static final IDataDef OP_CMD_JAVA_CLASS = create( "command.exec.java.class", STRING, //$NON-NLS-1$
      TSID_NAME, "java класс", //
      TSID_DESCRIPTION, "java class of realization", //
      TSID_IS_READ_ONLY, AV_TRUE );

  public static final IDataDef OP_CMD_VALUE_PARAM_ID = create( "value.param.id", STRING, //$NON-NLS-1$
      TSID_NAME, "ИД параметра", //
      TSID_DESCRIPTION, "Идентификатор параметра" ); //

  public static final IDataDef OP_CMD_OPC_ID = create( "cmd.opc.id", INTEGER, //$NON-NLS-1$
      TSID_NAME, "ИД команды в OPC", //
      TSID_DESCRIPTION, "Идентификатор команды в OPC сервера" ); //

  public static final IDataDef OP_DATA_JAVA_CLASS = create( "java.class", STRING, //$NON-NLS-1$
      TSID_NAME, "data java класс", //
      TSID_DESCRIPTION, "data java class of realization", //
      TSID_IS_READ_ONLY, AV_TRUE );

  /**
   * Имя параметра - номер бита (начиная от младшего с нулевого)
   */
  public static final IDataDef OP_BIT_INDEX = create( "bit.index", INTEGER, //$NON-NLS-1$
      TSID_NAME, "номер бита", //
      TSID_DESCRIPTION, "номер бита (начиная от младшего с нулевого)" );

  public static final IDataDef OP_EVENT_SENDER_JAVA_CLASS = create( "event.sender.java.class", STRING, //$NON-NLS-1$
      TSID_NAME, "event sender java класс", //
      TSID_DESCRIPTION, "java class of realization of event sender", //
      TSID_IS_READ_ONLY, AV_TRUE );

  public static final IDataDef OP_CONDITION_JAVA_CLASS = create( "condition.java.class", STRING, //$NON-NLS-1$
      TSID_NAME, "condition java класс", //
      TSID_DESCRIPTION, "java class of realization of condition", //
      TSID_IS_READ_ONLY, AV_TRUE );

  public static final IDataDef OP_PARAM_FORMER_JAVA_CLASS = create( "param.former.java.class", STRING, //$NON-NLS-1$
      TSID_NAME, "param former java класс", //
      TSID_DESCRIPTION, "java class of realization of param former", //
      TSID_IS_READ_ONLY, AV_TRUE );

  public static final IDataDef OP_FORMER_EVENT_PARAM = create( "former.event.params", STRING, //$NON-NLS-1$
      TSID_NAME, "former event params", //
      TSID_DESCRIPTION, "former event params" );

  public static final IDataDef OP_CONDITION_SWITCH_ON = create( "condition.switch.on", BOOLEAN, //$NON-NLS-1$
      TSID_NAME, "Передний фронт", //
      TSID_DESCRIPTION, "Срабатывние события по переднему фронту" );

  public static final IDataDef OP_CONDITION_SWITCH_OFF = create( "condition.switch.off", BOOLEAN, //$NON-NLS-1$
      TSID_NAME, "Задний фронт", //
      TSID_DESCRIPTION, "Срабатывние события по заднему фронту" );

  /**
   * Registers cfg unit realization types in holder and adds it into context.
   *
   * @param aContext ITsGuiContext - context.
   */
  public static void registerCfgUnitRealizationTypes( ITsGuiContext aContext ) {
    if( aContext.hasKey( CfgUnitRealizationTypeRegister.class ) ) {
      return;
    }
    CfgUnitRealizationTypeRegister realizationTypeRegister = new CfgUnitRealizationTypeRegister();
    aContext.put( CfgUnitRealizationTypeRegister.class, realizationTypeRegister );

    // ----------------------------------------------------
    // Определение первой реализации команд

    IListEdit<IDataDef> paramDefenitions = new ElemArrayList<>();

    paramDefenitions.add( OP_CMD_JAVA_CLASS );
    paramDefenitions.add( OP_CMD_VALUE_PARAM_ID );
    paramDefenitions.add( OP_CMD_OPC_ID );

    IOptionSetEdit defaultParams = new OptionSet();
    OP_CMD_JAVA_CLASS.setValue( defaultParams, avStr( COMMANDS_JAVA_CLASS_VALUE_COMMAND_BY_ONE_TAG_EXEC ) );

    ICfgUnitRealizationType cmdRealValCommandByOneTagWithParamExec =
        new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_VALUE_COMMAND_BY_ONE_TAG,
            "Установка значения через командный узел", ECfgUnitType.COMMAND, paramDefenitions, defaultParams ) {

          @Override
          public CfgOpcUaNode createInitCfg( ITsGuiContext aaContext, String aNodeId, int aNodeIndex, int aNodeCount ) {
            OpcUaServerConnCfg conConf =
                (OpcUaServerConnCfg)aaContext.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );

            EAtomicType type = EAtomicType.NONE;
            if( conConf != null ) {
              type = OpcUaUtils.getValueTypeOfNode( aaContext, conConf, aNodeId );
            }
            return new CfgOpcUaNode( aNodeId, false, true, aNodeIndex < aNodeCount - 1, type );
          }

        };

    realizationTypeRegister.registerType( cmdRealValCommandByOneTagWithParamExec );

    // ----------------------------------------------------
    // Определение второй реализации команд

    paramDefenitions = new ElemArrayList<>();

    paramDefenitions.add( OP_CMD_JAVA_CLASS );
    paramDefenitions.add( OP_CMD_VALUE_PARAM_ID );

    defaultParams = new OptionSet();
    OP_CMD_JAVA_CLASS.setValue( defaultParams, avStr( COMMANDS_JAVA_CLASS_VALUE_COMMAND_EXEC ) );
    OP_CMD_VALUE_PARAM_ID.setValue( defaultParams, avStr( "value" ) );

    ICfgUnitRealizationType cmdRealValueCommandExec =
        new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_VALUE_COMMAND, "Установка значения в узел",
            ECfgUnitType.COMMAND, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( cmdRealValueCommandExec );

    // ----------------------------------------------------
    // Определение первой реализации данных (простое - один к одному)

    paramDefenitions = new ElemArrayList<>();

    paramDefenitions.add( OP_DATA_JAVA_CLASS );

    defaultParams = new OptionSet();
    OP_DATA_JAVA_CLASS.setValue( defaultParams, avStr( DATA_JAVA_CLASS_ONE_TO_ONE_DATA_TRANSMITTER_FACTORY ) );

    ICfgUnitRealizationType dataOneToOne = new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_ONT_TO_ONE_DATA,
        "Один к одному", ECfgUnitType.DATA, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( dataOneToOne );

    // ----------------------------------------------------
    // Определение второй реализации данных (битовое данное из интового тега)

    paramDefenitions = new ElemArrayList<>();

    paramDefenitions.add( OP_DATA_JAVA_CLASS );
    paramDefenitions.add( OP_BIT_INDEX );

    defaultParams = new OptionSet();
    OP_DATA_JAVA_CLASS.setValue( defaultParams, avStr( DATA_JAVA_CLASS_ONE_INT_TO_ONE_BIT_DATA_TRANSMITTER_FACTORY ) );
    OP_BIT_INDEX.setValue( defaultParams, avInt( 0 ) );

    ICfgUnitRealizationType dataIntToOne =
        new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_ONE_INT_TO_ONE_BIT_DATA,
            "Битовое данное из интового тега", ECfgUnitType.DATA, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( dataIntToOne );

    // ----------------------------------------------------
    // Определение первой реализации события

    // ----------------------------------------------------
    // Определение второй реализации события

    paramDefenitions = new ElemArrayList<>();

    paramDefenitions.add( OP_EVENT_SENDER_JAVA_CLASS );
    paramDefenitions.add( OP_CONDITION_JAVA_CLASS );
    paramDefenitions.add( OP_PARAM_FORMER_JAVA_CLASS );
    paramDefenitions.add( OP_FORMER_EVENT_PARAM );

    defaultParams = new OptionSet();
    OP_EVENT_SENDER_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_JAVA_CLASS_OPC_TAGS_EVENT_SENDER ) );
    OP_CONDITION_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_JAVA_CLASS_TAG_VALUE_CHANGED_CONDITION ) );
    OP_PARAM_FORMER_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_ONE_TAG_CHANGED_PARAM_FORMER ) );
    OP_FORMER_EVENT_PARAM.setValue( defaultParams, avStr( "oldVal;newVal" ) );

    ICfgUnitRealizationType opcTagsEventSender2 =
        new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_TAG_VALUE_CHANGED, "Событие изменения значения тега",
            ECfgUnitType.EVENT, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( opcTagsEventSender2 );

    // ----------------------------------------------------
    // Определение третьей реализации события

    paramDefenitions = new ElemArrayList<>();

    paramDefenitions.add( OP_EVENT_SENDER_JAVA_CLASS );
    paramDefenitions.add( OP_CONDITION_JAVA_CLASS );
    paramDefenitions.add( OP_PARAM_FORMER_JAVA_CLASS );
    paramDefenitions.add( OP_FORMER_EVENT_PARAM );
    paramDefenitions.add( OP_CONDITION_SWITCH_ON );
    paramDefenitions.add( OP_CONDITION_SWITCH_OFF );

    defaultParams = new OptionSet();
    OP_EVENT_SENDER_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_JAVA_CLASS_OPC_TAGS_EVENT_SENDER ) );
    OP_CONDITION_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_JAVA_CLASS_TAG_SWITCH_CONDITION ) );
    OP_CONDITION_SWITCH_ON.setValue( defaultParams, avBool( true ) );
    OP_CONDITION_SWITCH_OFF.setValue( defaultParams, avBool( false ) );
    OP_PARAM_FORMER_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_ONE_TAG_TO_ONE_PARAM_FORMER ) );
    OP_FORMER_EVENT_PARAM.setValue( defaultParams, avStr( "on" ) );

    ICfgUnitRealizationType opcTagsEventSender3 = new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_SWITCH_EVENT,
        "Событие переключения битового тега", ECfgUnitType.EVENT, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( opcTagsEventSender3 );

    // ----------------------------------------------------
    // Определение четвертой реализации события

    paramDefenitions = new ElemArrayList<>();

    paramDefenitions.add( OP_EVENT_SENDER_JAVA_CLASS );
    paramDefenitions.add( OP_CONDITION_JAVA_CLASS );
    paramDefenitions.add( OP_PARAM_FORMER_JAVA_CLASS );
    paramDefenitions.add( OP_FORMER_EVENT_PARAM );
    paramDefenitions.add( OP_CONDITION_SWITCH_ON );
    paramDefenitions.add( OP_CONDITION_SWITCH_OFF );
    paramDefenitions.add( OP_BIT_INDEX );

    defaultParams = new OptionSet();
    OP_EVENT_SENDER_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_JAVA_CLASS_OPC_TAGS_EVENT_SENDER ) );
    OP_CONDITION_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_JAVA_CLASS_TAG_SWITCH_CONDITION ) );
    OP_CONDITION_SWITCH_ON.setValue( defaultParams, avBool( true ) );
    OP_CONDITION_SWITCH_OFF.setValue( defaultParams, avBool( false ) );
    OP_PARAM_FORMER_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_ONE_TAG_TO_ONE_PARAM_FORMER ) );
    OP_FORMER_EVENT_PARAM.setValue( defaultParams, avStr( "on" ) );
    OP_BIT_INDEX.setValue( defaultParams, avInt( 0 ) );

    ICfgUnitRealizationType opcTagsEventSender4 =
        new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_BIT_SWITCH_EVENT,
            "Событие переключения бита интового тега", ECfgUnitType.EVENT, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( opcTagsEventSender4 );
  }

  /**
   * По описанию конфигурации подключения к OPC UA выдает имя секции для хранения кэша узлов
   *
   * @param aSelConfig - описание конфигурации подключения к OPC UA
   * @return строка с именем секции
   */
  @SuppressWarnings( "nls" )
  public static String getCachedTreeSectionName( IOpcUaServerConnCfg aSelConfig ) {
    // создаем имя секции для хранения дерева узлов
    // выделяем из хоста IP, opc.tcp://192.168.12.61:4840
    Pattern p = Pattern.compile( "[a-z:\\.\\/]+([0-9\\.]+)" );
    String host = aSelConfig.host();
    Matcher n = p.matcher( host );
    String ipAddress = "localhost";
    if( n.find() ) {
      ipAddress = n.group( 1 );
      // заменим точки на символы подчеркивания
      ipAddress = ipAddress.replace( '.', '_' );
    }
    return SECTID_OPC_UA_NODES_PREFIX + ".IP_Address_" + ipAddress + ".UserName_" + aSelConfig.login();
  }

  /**
   * Determines existance of cach for configuration.
   *
   * @param aContext ITsGuiContext - context
   * @param aSelConfig IOpcUaServerConnCfg - opc ua server cfg
   * @return boolean - true - cach exists
   */
  public static boolean hasCachedOpcUaNodesTreeFor( ITsGuiContext aContext, IOpcUaServerConnCfg aSelConfig ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    String sectionName = OpcUaUtils.getCachedTreeSectionName( aSelConfig );
    return !storage.readColl( sectionName, UaTreeNode.KEEPER ).isEmpty();
  }

  private static IList<UaTreeNode> loadUaTreeNodes( ITsGuiContext aContext, IOpcUaServerConnCfg aConnCfg ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    String sectionName = OpcUaUtils.getCachedTreeSectionName( aConnCfg );
    IList<UaTreeNode> retVal = new ElemArrayList<>( storage.readColl( sectionName, UaTreeNode.KEEPER ) );
    return retVal;
  }

  /**
   * По описанию NodeId получить тип узла
   *
   * @param aContext - контекст
   * @param aConnCfg - конфигурация подключения
   * @param aNodeId - адрес узла в OPC UA
   * @return тип данного для узла
   */
  public static EAtomicType getValueTypeOfNode( ITsGuiContext aContext, IOpcUaServerConnCfg aConnCfg, String aNodeId ) {
    IList<UaTreeNode> nodes;
    NodeId nodeId = NodeId.parse( aNodeId );
    // сначала проверяем в кэше
    String sectionName = OpcUaUtils.getCachedTreeSectionName( aConnCfg );
    if( section2NodesList.hasKey( sectionName ) ) {
      nodes = section2NodesList.getByKey( sectionName );
    }
    else {
      nodes = loadUaTreeNodes( aContext, aConnCfg );
    }
    for( UaTreeNode node : nodes ) {
      NodeId candidateNodeId = NodeId.parse( node.getNodeId() );
      if( candidateNodeId.equals( nodeId ) ) {
        return node.getDataType();
      }
    }
    return null;
  }

  /**
   * Lets one to select opc serve config from the list of availables.
   *
   * @param aContext ITsGuiContext - context.
   * @return IOpcUaServerConnCfg - selected config or null.
   */
  public static IOpcUaServerConnCfg selectOpcServerConfig( ITsGuiContext aContext ) {
    TsNullArgumentRtException.checkNull( aContext );

    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    ISkConnection conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    // занесем параметры из файла в контекст
    AbstractAppPreferencesStorage apStorage =
        new AppPreferencesConfigIniStorage( new File( OPC_UA_SERVER_CONN_CFG_STORE_FILE ) );
    IAppPreferences appPreferences = new AppPreferences( apStorage );
    OpcUaServerConnCfgService cfgService = new OpcUaServerConnCfgService( appPreferences );

    IM5Model<IOpcUaServerConnCfg> model = m5.getModel( OpcUaServerConnCfgModel.MODEL_ID, IOpcUaServerConnCfg.class );

    IM5LifecycleManager<IOpcUaServerConnCfg> lm = new OpcUaServerConnCfgM5LifecycleManager( model, cfgService );

    TsDialogInfo di = new TsDialogInfo( aContext, DLG_SELECT_CFG_AND_OPEN, DLG_SELECT_CFG_AND_OPEN_D );
    // установим нормальный размер диалога
    di.setMinSize( new TsPoint( -30, -40 ) );
    // убираем поле фильтра
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( di.tsContext().params(), AvUtils.AV_FALSE );

    return M5GuiUtils.askSelectItem( di, model, null, lm.itemsProvider(), lm );
  }

  /**
   * Выполняет работы в отдельном потоке при открытом диалоге ожидания.
   *
   * @param aContext ITsGuiContext - контекст
   * @param aDialogName String - имя диалога ожидания.
   * @param aRunnable IRunnableWithProgress - реализация потока выполнения работы.
   */
  public static void runInWaitingDialog( final ITsGuiContext aContext, final String aDialogName,
      final IRunnableWithProgress aRunnable ) {

    final ProgressMonitorDialog dialog = new ProgressMonitorDialog( aContext.get( Shell.class ) ) {

      @Override
      protected Control createDialogArea( Composite aParent ) {
        Control c = super.createDialogArea( aParent );
        c.getShell().setText( aDialogName );
        return c;
      }
    };

    try {
      dialog.run( true, true, aRunnable );
    }
    catch( InvocationTargetException | InterruptedException e ) {
      Display.getDefault().asyncExec(
          () -> TsDialogUtils.error( aContext.get( Shell.class ), e.getCause() != null ? e.getCause() : e ) );

    }

  }

}
