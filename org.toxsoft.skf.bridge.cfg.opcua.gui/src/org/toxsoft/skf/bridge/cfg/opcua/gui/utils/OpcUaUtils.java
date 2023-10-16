package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.impl.DataDef.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;

import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;

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
import org.toxsoft.core.log4j.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.OptionSet;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.*;
import org.toxsoft.core.txtproj.lib.storage.*;
import org.toxsoft.core.txtproj.lib.workroom.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;

/**
 * Utils of OPC UA server connections.
 *
 * @author max
 * @author dima
 */
public class OpcUaUtils {

  private static StringMap<IList<UaTreeNode>> section2NodesList = new StringMap<>();

  public static final String CFG_UNIT_REALIZATION_TYPE_ONT_TO_ONE_DATA = "ont.to.one.data";
  /**
   * id secton for cached OPC UA nodes
   */
  public static final String SECTID_OPC_UA_NODES_PREFIX                = "cached.opc.ua.nodes"; //$NON-NLS-1$

  /**
   * id secton for store links UaNode->Gwid
   */
  public static final String SECTID_OPC_UA_NODES_2_RTD_GWIDS = "opc.ua.nodes2rtd.gwids"; //$NON-NLS-1$

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
   * Add list of links UaNode->Gwid {@link UaNode2RtdGwid} to store in inner storage
   *
   * @param aContext app context
   * @param aNodes2Gwids list of links UaNode->Gwid
   */
  public static void addNodes2GwidsInStore( ITsGuiContext aContext, IList<UaNode2RtdGwid> aNodes2Gwids ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IList<UaNode2RtdGwid> oldList = loadNodes2Gwids( aContext );
    IListEdit<UaNode2RtdGwid> newList = new ElemArrayList<>();
    newList.addAll( oldList );
    newList.addAll( aNodes2Gwids );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    storage.writeColl( SECTID_OPC_UA_NODES_2_RTD_GWIDS, newList, UaNode2RtdGwid.KEEPER );
  }

  /**
   * Store in inner storage list of links UaNode->Gwid
   *
   * @param aContext app context
   * @param aNodes2Gwids list of links UaNode->Gwid
   */
  private static void storeNodes2Gwids( ITsGuiContext aContext, IList<UaNode2RtdGwid> aNodes2Gwids ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    storage.writeColl( SECTID_OPC_UA_NODES_2_RTD_GWIDS, aNodes2Gwids, UaNode2RtdGwid.KEEPER );
  }

  /**
   * @param aContext app context
   * @return list of links {@link UaNode2RtdGwid } UaNode->Gwid
   */
  public static IList<UaNode2RtdGwid> loadNodes2Gwids( ITsGuiContext aContext ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IList<UaNode2RtdGwid> retVal =
        new ElemArrayList<>( storage.readColl( SECTID_OPC_UA_NODES_2_RTD_GWIDS, UaNode2RtdGwid.KEEPER ) );
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
      IList<UaNode2RtdGwid> nodes2Gwids = loadNodes2Gwids( aContext );
      for( UaNode2RtdGwid node2Gwid : nodes2Gwids ) {
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
      IList<UaNode2RtdGwid> nodes2Gwids = loadNodes2Gwids( aContext );
      for( UaNode2RtdGwid node2Gwid : nodes2Gwids ) {
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
  public static void addCmdGwid2NodesInStore( ITsGuiContext aContext, IList<CmdGwid2UaNodes> aCmdGwid2UaNodes ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IList<CmdGwid2UaNodes> oldList = loadCmdGwid2Nodes( aContext );
    IListEdit<CmdGwid2UaNodes> newList = new ElemArrayList<>();
    newList.addAll( oldList );
    newList.addAll( aCmdGwid2UaNodes );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    storage.writeColl( SECTID_CMD_GWIDS_2_OPC_UA_NODES, newList, CmdGwid2UaNodes.KEEPER );
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
    OP_CMD_JAVA_CLASS.setValue( defaultParams,
        avStr( "ru.toxsoft.l2.dlm.opc_bridge.submodules.commands.ValCommandByOneTagWithParamExec" ) );

    ICfgUnitRealizationType cmdRealValCommandByOneTagWithParamExec = new CfgUnitRealizationType( "val.command.one.tag",
        "Установка значения через командный узел", ECfgUnitType.COMMAND, paramDefenitions, defaultParams ) {

      @Override
      public CfgOpcUaNode createInitCfg( ITsGuiContext aaContext, String aNodeId, int aNodeIndex, int aNodeCount ) {
        OpcUaServerConnCfg config =
            (OpcUaServerConnCfg)aaContext.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );

        EAtomicType type = OpcUaUtils.getValueTypeOfNode( aaContext, config, aNodeId );
        return new CfgOpcUaNode( aNodeId, false, true, aNodeIndex < aNodeCount - 1,
            type != null ? type : EAtomicType.INTEGER );
      }

    };

    realizationTypeRegister.registerType( cmdRealValCommandByOneTagWithParamExec );

    // ----------------------------------------------------
    // Определение второй реализации команд

    paramDefenitions = new ElemArrayList<>();

    paramDefenitions.add( OP_CMD_JAVA_CLASS );
    paramDefenitions.add( OP_CMD_VALUE_PARAM_ID );

    defaultParams = new OptionSet();
    OP_CMD_JAVA_CLASS.setValue( defaultParams,
        avStr( "ru.toxsoft.l2.dlm.opc_bridge.submodules.commands.ValueCommandExec" ) );
    OP_CMD_VALUE_PARAM_ID.setValue( defaultParams, avStr( "value" ) );

    ICfgUnitRealizationType cmdRealValueCommandExec = new CfgUnitRealizationType( "value.command",
        "Установка значения в узел", ECfgUnitType.COMMAND, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( cmdRealValueCommandExec );

    // ----------------------------------------------------
    // Определение первой реализации данных (простое - один к одному)

    paramDefenitions = new ElemArrayList<>();

    paramDefenitions.add( OP_CMD_JAVA_CLASS );

    defaultParams = new OptionSet();
    OP_CMD_JAVA_CLASS.setValue( defaultParams,
        avStr( "ru.toxsoft.l2.dlm.opc_bridge.submodules.data.OneToOneDataTransmitterFactory" ) );

    ICfgUnitRealizationType dataOneToOne = new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_ONT_TO_ONE_DATA,
        "Один к одному", ECfgUnitType.DATA, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( dataOneToOne );

    // ----------------------------------------------------
    // Определение первой реализации события

    paramDefenitions = new ElemArrayList<>();

    paramDefenitions.add( OP_CMD_JAVA_CLASS );

    defaultParams = new OptionSet();
    OP_CMD_JAVA_CLASS.setValue( defaultParams,
        avStr( "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OpcTagsEventSender" ) );

    ICfgUnitRealizationType opcTagsEventSender = new CfgUnitRealizationType( "opc.tags.event.sender", "Простое событие",
        ECfgUnitType.EVENT, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( opcTagsEventSender );
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
}
