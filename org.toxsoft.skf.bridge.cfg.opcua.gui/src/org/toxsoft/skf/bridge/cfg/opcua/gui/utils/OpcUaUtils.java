package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import java.nio.file.*;
import java.util.*;
import java.util.function.*;

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
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.*;
import org.toxsoft.core.txtproj.lib.storage.*;
import org.toxsoft.core.txtproj.lib.workroom.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;

/**
 * Utils of OPC UA server connections.
 *
 * @author max
 * @author dima
 */
public class OpcUaUtils {

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
   * Store in inner storage list of links UaNode->Gwid
   *
   * @param aContext app context
   * @param aNodes2Gwids list of links UaNode->Gwid
   */
  public static void saveNodes2Gwids( ITsGuiContext aContext, IList<UaNode2RtdGwid> aNodes2Gwids ) {
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
   * Store in inner storage list of links CmdGwid->UaNodes
   *
   * @param aContext app context
   * @param aCmdGwid2UaNodes list of links CmdGwid->UaNodes
   */
  public static void saveCmdGwid2Nodes( ITsGuiContext aContext, IList<CmdGwid2UaNodes> aCmdGwid2UaNodes ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    storage.writeColl( SECTID_CMD_GWIDS_2_OPC_UA_NODES, aCmdGwid2UaNodes, CmdGwid2UaNodes.KEEPER );
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

}
