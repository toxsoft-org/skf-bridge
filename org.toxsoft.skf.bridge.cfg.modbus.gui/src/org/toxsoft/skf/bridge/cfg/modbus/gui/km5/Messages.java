package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import org.eclipse.osgi.util.*;

public class Messages
    extends NLS {

  private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
  public static String        MSG_ERR_INVALID_IP_ADDRESS;
  public static String        MSG_ERR_NOT_IDPATH;
  public static String        STR_D_CONNECTION_NAME;
  public static String        STR_D_IP_ADDRESS;
  public static String        STR_D_MODBUS_REGISTER;
  public static String        STR_D_MODBUS_REQUEST_TYPE;
  public static String        STR_D_MODBUS_VALUE_TYPE;
  public static String        STR_D_MODBUS_PARAMETERS_STR;
  public static String        STR_D_MODBUS_WORDS_COUNT;
  public static String        STR_D_PORT_NUMBER;
  public static String        STR_D_MODBUS_DEVICE;
  public static String        STR_ERR_DOC_READ;
  public static String        STR_N_CONNECTION_NAME;
  public static String        STR_N_IP_ADDRESS;
  public static String        STR_N_MODBUS_REGISTER;
  public static String        STR_N_MODBUS_REQUEST_TYPE;
  public static String        STR_N_MODBUS_VALUE_TYPE;
  public static String        STR_N_MODBUS_PARAMETERS_STR;
  public static String        STR_N_MODBUS_WORDS_COUNT;
  public static String        STR_N_PORT_NUMBER;
  public static String        STR_N_MODBUS_DEVICE;
  public static String        STR_N_IS_TCP_INDEX;
  public static String        STR_D_IS_TCP_INDEX;
  public static String        STR_N_DEVICE_CONN_OPTS;
  public static String        STR_D_DEVICE_CONN_OPTS;

  static {
    // initialize resource bundle
    NLS.initializeMessages( BUNDLE_NAME, Messages.class );
  }

  private Messages() {
  }
}
