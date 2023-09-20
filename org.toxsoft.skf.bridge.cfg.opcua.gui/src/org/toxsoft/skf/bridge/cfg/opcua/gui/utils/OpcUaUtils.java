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
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;

/**
 * Utils of OPC UA server connections.
 *
 * @author max
 */
public class OpcUaUtils {

  private static final String CLIENT_APP_NAME                            = "eclipse milo opc-ua client";
  private static final String CLIENT_APP_URI                             = "urn:eclipse:milo:examples:client";
  private static final String ERROR_FORMAT_UNABLE_TO_CREATE_SECURITY_DIR = "unable to create security dir: %s";
  private static final String SYS_PROP_JAVA_IO_TMPDIR_DEF_VAL            = "security";
  private static final String SYS_PROP_JAVA_IO_TMPDIR                    = "java.io.tmpdir";

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

}
