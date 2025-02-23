package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import org.eclipse.osgi.util.*;

public class Messages
    extends NLS {

  private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
  public static String        DLG_C_CLASS_INFO;
  public static String        DLG_C_NEW_CLASS;
  public static String        DLG_C_NEW_OBJS;
  public static String        DLG_T_CLASS_INFO;
  public static String        DLG_T_NEW_CLASS;
  public static String        DLG_T_NEW_OBJS;
  public static String        ERR_MSG_CACHE_OUTDATED;
  public static String        MSG_CMDS_DESCR_LOADED;
  public static String        SELECT_FILE_4_IMPORT_BIT_RTDATA;
  public static String        SELECT_FILE_4_IMPORT_CMD;
  public static String        STR_BITMASK_FILE_LOADED;
  public static String        STR_CHECK_OPC_UA_CACHE;
  public static String        STR_CLASSES_LIST;
  public static String        STR_D_ARG_VALUE;
  public static String        STR_D_EDIT_CONFIG_SET;
  public static String        STR_D_EDIT_OPC_UA_NODES;
  public static String        STR_D_EV_AUTO;
  public static String        STR_D_EV_ON;
  public static String        STR_D_EV_PARAM_NEW_VAL;
  public static String        STR_D_EV_PARAM_OLD_VAL;
  public static String        STR_D_EV_PARAM_ON;
  public static String        STR_D_EV_SET_POINT_MAX;
  public static String        STR_D_EV_SET_POINT_MIN;
  public static String        STR_D_OPC_UA_MARKER;
  public static String        STR_D_SAVE_CONFIG;
  public static String        STR_D_SELECT_OPC_UA_SERVER;
  public static String        STR_D_SELECT_S5_SERVER;
  public static String        STR_DESCR_SELECT_NODE;
  public static String        STR_ENTER_DESCR;
  public static String        STR_ENTER_NAME;
  public static String        STR_LINKS;
  public static String        STR_MSG_SELECT_NODE;
  public static String        STR_MSG_SELECT_NODE_4_CLASS;
  public static String        STR_MSG_SELECT_NODE_4_OBJS;
  public static String        STR_N_ARG_VALUE;
  public static String        STR_N_EDIT_CONFIG_SET;
  public static String        STR_N_EDIT_OPC_UA_NODES;
  public static String        STR_N_EV_AUTO;
  public static String        STR_N_EV_ON;
  public static String        STR_N_EV_PARAM_NEW_VAL;
  public static String        STR_N_EV_PARAM_OLD_VAL;
  public static String        STR_N_EV_PARAM_ON;
  public static String        STR_N_EV_SET_POINT_MAX;
  public static String        STR_N_EV_SET_POINT_MIN;
  public static String        STR_N_OPC_UA_MARKER;
  public static String        STR_N_SAVE_CONFIG;
  public static String        STR_N_SELECT_OPC_UA_SERVER;
  public static String        STR_N_SELECT_S5_SERVER;
  public static String        STR_NODES;
  public static String        STR_OPC_UA_CACHE;
  public static String        STR_OPC_UA_CLIENT_CONNECTING;
  public static String        STR_OPC_UA_CLIENT_CREATE;
  public static String        STR_OPC_UA_CONNECTED;
  public static String        STR_OPC_UA_CONNECTING_PROCESS;
  public static String        STR_OPC_UA_CONNECTING_PROCESS_FAIL;
  public static String        STR_OPC_UA_DESCR;
  public static String        STR_SK_CONN_DESCR;
  public static String        STR_SUCCESS_CLASS_CREATED;
  public static String        STR_SUCCESS_CLASS_UPDATED;
  public static String        STR_SUCCESS_OBJS_UPDATED;
  public static String        STR_USE_OPC_UA_CACHE;
  public static String        STR_USE_OPC_UA_CACHE_CREATED;
  public static String        STR_USE_OPC_UA_CACHE_CREATING;
  public static String        STR_USE_OPC_UA_CACHE_CREATION_FAIL;
  public static String        STR_WARN_RECREATE_OBJS;
  public static String        STR_DEFAULT_WORKROOM_SK_CONN;
  public static String        STR_N_AUTO_LINK;
  public static String        STR_D_AUTO_LINK;
  public static String        STR_N_GENERATE_DLMCFG;
  public static String        STR_D_GENERATE_DLMCFG;

  static {
    // initialize resource bundle
    NLS.initializeMessages( BUNDLE_NAME, Messages.class );
  }

  private Messages() {
  }
}
