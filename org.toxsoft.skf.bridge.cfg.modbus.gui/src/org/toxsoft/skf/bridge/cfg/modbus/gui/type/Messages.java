package org.toxsoft.skf.bridge.cfg.modbus.gui.type;

import org.eclipse.osgi.util.*;

public class Messages
    extends NLS {

  private static final String BUNDLE_NAME    = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
  public static String        STR_D_TCP_PORT = null;
  public static String        STR_N_TCP_PORT;
  public static String        STR_D_TCP_IP_ADDRESS;
  public static String        STR_N_TCP_IP_ADDRESS;
  public static String        STR_N_RTU_PORT_NAME;
  public static String        STR_D_RTU_PORT_NAME;
  public static String        STR_D_RTU_ADDRESS;
  public static String        STR_N_RTU_ADDRESS;
  static {
    // initialize resource bundle
    NLS.initializeMessages( BUNDLE_NAME, Messages.class );
  }

  private Messages() {
  }
}
