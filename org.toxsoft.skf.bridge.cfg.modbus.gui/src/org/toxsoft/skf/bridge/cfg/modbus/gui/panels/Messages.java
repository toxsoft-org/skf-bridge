package org.toxsoft.skf.bridge.cfg.modbus.gui.panels;

import org.eclipse.osgi.util.*;

public class Messages
    extends NLS {

  private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
  public static String        DLG_T_SKID_SEL;
  public static String        STR_DESCR_SELECT_TCP_ADDR;
  public static String        STR_MSG_SELECT_TCP_ADDR;
  public static String        STR_MSG_SKID_SELECTION;
  static {
    // initialize resource bundle
    NLS.initializeMessages( BUNDLE_NAME, Messages.class );
  }

  private Messages() {
  }
}
