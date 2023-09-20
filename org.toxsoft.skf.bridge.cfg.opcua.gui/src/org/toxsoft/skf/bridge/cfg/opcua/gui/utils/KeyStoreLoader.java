/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.cert.*;
import java.util.regex.*;

import org.eclipse.milo.opcua.sdk.server.util.*;
import org.eclipse.milo.opcua.stack.core.util.*;
import org.toxsoft.core.log4j.*;
import org.toxsoft.core.tslib.utils.logs.*;

/**
 * Util class using in opc ua connection establishing.
 *
 * @author max
 */
class KeyStoreLoader {

  private static final String HOSTNAMES_ADDRESS_PARAM = "0.0.0.0";

  private static final String CERTIFICATE_BUILDER_IP_ADDRESS = "127.0.0.1";

  private static final String CERTIFICATE_BUILDER_DNS_NAME = "localhost";

  private static final String CERTIFICATE_BUILDER_APPLICATION_URI = "urn:eclipse:milo:examples:client";

  private static final String CERTIFICATE_BUILDER_COUNTRY_CODE = "US";

  private static final String CERTIFICATE_BUILDER_STATE_NAME = "CA";

  private static final String CERTIFICATE_BUILDER_LOCALITY_NAME = "Folsom";

  private static final String CERTIFICATE_BUILDER_ORGANIZATIONAL_UNIT = "dev";

  private static final String CERTIFICATE_BUILDER_ORGANIZATION = "digitalpetri";

  private static final String CERTIFICATE_BUILDER_COMMON_NAME = "Eclipse Milo Example Client";

  private static final String KEY_STORE_FILE = "example-client.pfx";

  private static final String KEY_STORE_TYPE = "PKCS12";

  private static final String IP_ADDR_PATTERN_STR_FROMAT =
      "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

  private static final String CLIENT_ALIAS = "client-ai";
  private static final char[] PASSWORD     = "password".toCharArray();

  private static final Pattern IP_ADDR_PATTERN = Pattern.compile( IP_ADDR_PATTERN_STR_FROMAT );

  /**
   * Журнал работы
   */
  private static ILogger logger = LoggerWrapper.getLogger( KeyStoreLoader.class.getName() );

  private X509Certificate clientCertificate;
  private KeyPair         clientKeyPair;

  public KeyStoreLoader load( Path baseDir )
      throws Exception {
    KeyStore keyStore = KeyStore.getInstance( KEY_STORE_TYPE );

    Path serverKeyStore = baseDir.resolve( KEY_STORE_FILE );

    logger.info( "Loading KeyStore at %s", serverKeyStore ); //$NON-NLS-1$

    if( !Files.exists( serverKeyStore ) ) {
      keyStore.load( null, PASSWORD );

      KeyPair keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair( 2048 );

      SelfSignedCertificateBuilder builder = new SelfSignedCertificateBuilder( keyPair )
          .setCommonName( CERTIFICATE_BUILDER_COMMON_NAME ).setOrganization( CERTIFICATE_BUILDER_ORGANIZATION )
          .setOrganizationalUnit( CERTIFICATE_BUILDER_ORGANIZATIONAL_UNIT )
          .setLocalityName( CERTIFICATE_BUILDER_LOCALITY_NAME ).setStateName( CERTIFICATE_BUILDER_STATE_NAME )
          .setCountryCode( CERTIFICATE_BUILDER_COUNTRY_CODE ).setApplicationUri( CERTIFICATE_BUILDER_APPLICATION_URI )
          .addDnsName( CERTIFICATE_BUILDER_DNS_NAME ).addIpAddress( CERTIFICATE_BUILDER_IP_ADDRESS );

      // Get as many hostnames and IP addresses as we can listed in the certificate.
      for( String hostname : HostnameUtil.getHostnames( HOSTNAMES_ADDRESS_PARAM ) ) {
        if( IP_ADDR_PATTERN.matcher( hostname ).matches() ) {
          builder.addIpAddress( hostname );
        }
        else {
          builder.addDnsName( hostname );
        }
      }

      X509Certificate certificate = builder.build();

      keyStore.setKeyEntry( CLIENT_ALIAS, keyPair.getPrivate(), PASSWORD, new X509Certificate[] { certificate } );
      try( OutputStream out = Files.newOutputStream( serverKeyStore ) ) {
        keyStore.store( out, PASSWORD );
      }
    }
    else {
      try( InputStream in = Files.newInputStream( serverKeyStore ) ) {
        keyStore.load( in, PASSWORD );
      }
    }

    Key serverPrivateKey = keyStore.getKey( CLIENT_ALIAS, PASSWORD );
    if( serverPrivateKey instanceof PrivateKey ) {
      clientCertificate = (X509Certificate)keyStore.getCertificate( CLIENT_ALIAS );
      PublicKey serverPublicKey = clientCertificate.getPublicKey();
      clientKeyPair = new KeyPair( serverPublicKey, (PrivateKey)serverPrivateKey );
    }

    return this;
  }

  public X509Certificate getClientCertificate() {
    return clientCertificate;
  }

  public KeyPair getClientKeyPair() {
    return clientKeyPair;
  }

}
