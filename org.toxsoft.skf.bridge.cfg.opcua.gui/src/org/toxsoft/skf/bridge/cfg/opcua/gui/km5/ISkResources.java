package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

/**
 * Localizable resources.
 *
 * @author dima
 */
@SuppressWarnings( "javadoc" )
public interface ISkResources {

  /**
   * {@link UaVariableNodeM5Model}
   */
  String STR_N_NODE_ID          = Messages.STR_N_NODE_ID;
  String STR_D_NODE_ID          = Messages.STR_D_NODE_ID;
  String STR_N_VALUE            = Messages.STR_N_VALUE;
  String STR_D_VALUE            = Messages.STR_D_VALUE;
  String STR_N_DATA_TYPE        = Messages.STR_N_DATA_TYPE;
  String STR_D_DATA_TYPE        = Messages.STR_D_DATA_TYPE;
  String STR_N_STATUS_CODE      = Messages.STR_N_STATUS_CODE;
  String STR_D_STATUS_CODE      = Messages.STR_D_STATUS_CODE;
  String STR_N_SOURCE_TIMESTAMP = Messages.STR_N_SOURCE_TIMESTAMP;
  String STR_D_SOURCE_TIMESTAMP = Messages.STR_D_SOURCE_TIMESTAMP;
  String STR_N_SERVER_TIMESTAMP = Messages.STR_N_SERVER_TIMESTAMP;
  String STR_D_SERVER_TIMESTAMP = Messages.STR_D_SERVER_TIMESTAMP;

  /**
   * {@link UaVariableNodeM5LifecycleManager}
   */
  String STR_WRITE_NODE_CONFIRM = Messages.STR_WRITE_NODE_CONFIRM;

  /**
   * {@link CfgOpcUaNodeM5Model}
   */
  String STR_N_ADD_MISSED_NODES         = Messages.STR_N_ADD_MISSED_NODES;
  String STR_D_ADD_MISSED_NODES         = Messages.STR_D_ADD_MISSED_NODES;
  String STR_N_REMOVE_MISSED_NODES      = Messages.STR_N_REMOVE_MISSED_NODES;
  String STR_D_REMOVE_MISSED_NODES      = Messages.STR_D_REMOVE_MISSED_NODES;
  String STR_N_REMOVE_ALL_NODES         = Messages.STR_N_REMOVE_ALL_NODES;
  String STR_D_REMOVE_ALL_NODES         = Messages.STR_D_REMOVE_ALL_NODES;
  String STR_N_GENERATE_DEVCFG          = Messages.STR_N_GENERATE_DEVCFG;
  String STR_D_GENERATE_DEVCFG          = Messages.STR_D_GENERATE_DEVCFG;
  String STR_N_NODE_VALUE_TYPE          = Messages.STR_N_NODE_VALUE_TYPE;
  String STR_D_NODE_VALUE_TYPE          = Messages.STR_D_NODE_VALUE_TYPE;
  String STR_N_NODE_2READ               = Messages.STR_N_NODE_2READ;
  String STR_D_NODE_2READ               = Messages.STR_D_NODE_2READ;
  String STR_N_NODE_2WRITE              = Messages.STR_N_NODE_2WRITE;
  String STR_D_NODE_2WRITE              = Messages.STR_D_NODE_2WRITE;
  String STR_N_NODE_SYNC                = Messages.STR_N_NODE_SYNC;
  String STR_D_NODE_SYNC                = Messages.STR_D_NODE_SYNC;
  String STR_SELECT_FILE_SAVE_DEVCFG    = Messages.STR_SELECT_FILE_SAVE_DEVCFG;
  String MSG_CONFIG_FILE_DEVCFG_CREATED = Messages.MSG_CONFIG_FILE_DEVCFG_CREATED;
  String MSG_SELECT_OPC_UA_SERVER       = Messages.MSG_SELECT_OPC_UA_SERVER;

  /**
   * {@link GwidsForCfgM5Model}
   */
  String STR_N_ADD_AS_STRING  = Messages.STR_N_ADD_AS_STRING;
  String STR_D_ADD_AS_STRING  = Messages.STR_D_ADD_AS_STRING;
  String STR_N_EDIT_AS_STRING = Messages.STR_N_EDIT_AS_STRING;
  String STR_D_EDIT_AS_STRING = Messages.STR_D_EDIT_AS_STRING;

  /**
   * {@link NodesForCfgM5Model}
   */
  String STR_N_NODE_STRING       = Messages.STR_N_NODE_STRING;
  String STR_D_NODE_STRING       = Messages.STR_D_NODE_STRING;
  String STR_D_NODE_DISPLAY_NAME = Messages.STR_D_NODE_DISPLAY_NAME;
  String STR_N_NODE_DISPLAY_NAME = Messages.STR_N_NODE_DISPLAY_NAME;
  String STR_D_NODE_BROWSE_NAME  = Messages.STR_D_NODE_BROWSE_NAME;
  String STR_N_NODE_BROWSE_NAME  = Messages.STR_N_NODE_BROWSE_NAME;
  String STR_D_NODE_DESCRIPTION  = Messages.STR_D_NODE_DESCRIPTION;
  String STR_N_NODE_DESCRIPTION  = Messages.STR_N_NODE_DESCRIPTION;
  String STR_D_NODE_PARENT       = Messages.STR_D_NODE_PARENT;
  String STR_N_NODE_PARENT       = Messages.STR_N_NODE_PARENT;

  /**
   * {@link OpcToS5DataCfgDocM5Model}
   */
  String STR_N_PATH_TO_L2      = Messages.STR_N_PATH_TO_L2;
  String STR_D_PATH_TO_L2      = Messages.STR_D_PATH_TO_L2;
  String STR_N_CFG_FILE_NAME   = Messages.STR_N_CFG_FILE_NAME;
  String STR_D_CFG_FILE_NAME   = Messages.STR_D_CFG_FILE_NAME;
  String STR_N_END_POINT_URL   = "End point URL";
  String STR_D_END_POINT_URL   = "End point URL (IP:port)";
  String STR_N_OPC_UA_USER     = "user";
  String STR_D_OPC_UA_USER     = "OPC UA user";
  String STR_N_OPC_UA_PASSWORD = "password";
  String STR_D_OPC_UA_PASSWORD = "OPC UA password";

  /**
   * {@link OpcToS5DataCfgUnitM5LifecycleManager}
   */
  String STR_SELECT_FILE_SAVE_DLMCFG    = Messages.STR_SELECT_FILE_SAVE_DLMCFG;
  String MSG_CONFIG_FILE_DLMCFG_CREATED = Messages.MSG_CONFIG_FILE_DLMCFG_CREATED;

  /**
   * {@link OpcToS5DataCfgUnitM5Model}
   */
  String STR_N_SAVE_CONFIG          = Messages.STR_N_SAVE_CONFIG;
  String STR_D_SAVE_CONFIG          = Messages.STR_D_SAVE_CONFIG;
  String STR_N_SELECT_S5_SERVER     = Messages.STR_N_SELECT_S5_SERVER;
  String STR_D_SELECT_S5_SERVER     = Messages.STR_D_SELECT_S5_SERVER;
  String STR_N_SELECT_OPC_UA_SERVER = Messages.STR_N_SELECT_OPC_UA_SERVER;
  String STR_D_SELECT_OPC_UA_SERVER = Messages.STR_D_SELECT_OPC_UA_SERVER;
  String STR_N_VALIDATE_CONFIG      = Messages.STR_N_VALIDATE_CONFIG;
  String STR_D_VALIDATE_CONFIG      = Messages.STR_D_VALIDATE_CONFIG;
  String STR_N_SHOW_UNMATCHED_UNITS = Messages.STR_N_SHOW_UNMATCHED_UNITS;
  String STR_D_SHOW_UNMATCHED_UNITS = Messages.STR_D_SHOW_UNMATCHED_UNITS;
  String STR_N_READ_CONFIG_FILES    = Messages.STR_N_READ_CONFIG_FILES;
  String STR_D_READ_CONFIG_FILES    = Messages.STR_D_READ_CONFIG_FILES;
  String STR_N_TYPE_REALIZ          = Messages.STR_N_TYPE_REALIZ;
  String STR_D_TYPE_REALIZ          = Messages.STR_D_TYPE_REALIZ;
  String STR_N_REALIZ               = Messages.STR_N_REALIZ;
  String STR_D_REALIZ               = Messages.STR_D_REALIZ;
  String MSG_LOADED_CMDS_DESCR      = Messages.MSG_LOADED_CMDS_DESCR;
  String MSG_LOADED_BIT_MASKS_DESCR = Messages.MSG_LOADED_BIT_MASKS_DESCR;

  String STR_LINK_PREFIX = Messages.STR_LINK_PREFIX;

  /**
   * {@link OpcUaServerConnCfgModel}
   */
  String STR_N_PARAM_TREE_TYPE = "тип дерева";
  String STR_D_PARAM_TREE_TYPE = "Тип OPC UA tree";

}
