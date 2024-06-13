package org.toxsoft.skf.bridge.cfg.modbus.gui.panels;

import org.eclipse.osgi.util.*;

public class Messages
    extends NLS {

  private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
  public static String        DLG_T_SKID_SEL;
  public static String        STR_DESCR_SELECT_TCP_ADDR;
  public static String        STR_MSG_SELECT_TCP_ADDR;
  public static String        STR_MSG_SKID_SELECTION;
  public static String        STR_SEL_IP_ADDRESS;
  public static String        STR_IP_ADDRESES;
  public static String        STR_N_SELECT_IP_ADDRESS;
  public static String        STR_D_SELECT_IP_ADDRESS;
  public static String        STR_N_COPY_ALL;
  public static String        STR_D_COPY_ALL;

  static {
    // initialize resource bundle
    NLS.initializeMessages( BUNDLE_NAME, Messages.class );
  }

  private Messages() {
  }
}
