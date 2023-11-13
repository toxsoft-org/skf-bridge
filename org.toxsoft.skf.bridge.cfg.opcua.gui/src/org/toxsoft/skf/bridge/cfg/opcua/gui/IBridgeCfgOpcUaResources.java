package org.toxsoft.skf.bridge.cfg.opcua.gui;

import org.toxsoft.skf.bridge.cfg.opcua.gui.Messages;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;

/**
 * Localizable resources.
 *
 * @author max
 */
@SuppressWarnings( value = { "javadoc" } ) // "nls",
public interface IBridgeCfgOpcUaResources {

  String MSG_ERR_INV_GWID_FORMAT = Messages.getString( "MSG_ERR_INV_GWID_FORMAT" ); //$NON-NLS-1$
  String MSG_ERR_INV_SKID_FORMAT = Messages.getString( "MSG_ERR_INV_SkID_FORMAT" ); //$NON-NLS-1$

  String STR_N_PARAM_NODEID        = Messages.getString("STR_N_PARAM_NODEID"); //$NON-NLS-1$
  String STR_N_PARAM_STRID         = Messages.getString("STR_N_PARAM_STRID"); //$NON-NLS-1$
  String STR_D_PARAM_STRID         = Messages.getString("STR_D_PARAM_STRID"); //$NON-NLS-1$
  String STR_D_BY_OPC_NODES_STRUCT = Messages.getString("STR_D_BY_OPC_NODES_STRUCT"); //$NON-NLS-1$
  String STR_N_BY_OPC_NODES_STRUCT = Messages.getString("STR_N_BY_OPC_NODES_STRUCT"); //$NON-NLS-1$

  String STR_N_BROWSE_CONN                = Messages.getString("STR_N_BROWSE_CONN"); //$NON-NLS-1$
  String STR_D_BROWSE_CONN                = Messages.getString("STR_D_BROWSE_CONN"); //$NON-NLS-1$
  String STR_N_REMOVE_CACHED_NODES_OPC_UA = Messages.getString("STR_N_REMOVE_CACHED_NODES_OPC_UA"); //$NON-NLS-1$
  String STR_D_REMOVE_CACHED_NODES_OPC_UA = Messages.getString("STR_D_REMOVE_CACHED_NODES_OPC_UA"); //$NON-NLS-1$
  String MSG_ASK_REMOVE_CACHE_CONFIRM     = Messages.getString("MSG_ASK_REMOVE_CACHE_CONFIRM"); //$NON-NLS-1$

  String STR_LOADING_OPC_UA_NODES_STRUCT = Messages.getString("STR_LOADING_OPC_UA_NODES_STRUCT"); //$NON-NLS-1$

  String STR_N_PARAM_BROWSE_NAME = Messages.getString("STR_N_PARAM_BROWSE_NAME"); //$NON-NLS-1$
  String STR_D_PARAM_BROWSE_NAME = Messages.getString("STR_D_PARAM_BROWSE_NAME"); //$NON-NLS-1$

  String STR_N_PARAM_DISPLAY_NAME = Messages.getString("STR_N_PARAM_DISPLAY_NAME"); //$NON-NLS-1$
  String STR_D_PARAM_DISPLAY_NAME = Messages.getString("STR_D_PARAM_DISPLAY_NAME"); //$NON-NLS-1$

  String STR_N_PARAM_DESCRIPTION = Messages.getString("STR_N_PARAM_DESCRIPTION"); //$NON-NLS-1$
  String STR_D_PARAM_DESCRIPTION = Messages.getString("STR_D_PARAM_DESCRIPTION"); //$NON-NLS-1$

  /**
   * {@link DtoObjectM5Model}
   */
  String STR_N_CLASS_ID = Messages.getString("STR_N_CLASS_ID"); //$NON-NLS-1$
  String STR_D_CLASS_ID = Messages.getString("STR_D_CLASS_ID"); //$NON-NLS-1$
  String STR_N_NAME     = Messages.getString("STR_N_NAME"); //$NON-NLS-1$
  String STR_D_NAME     = Messages.getString("STR_D_NAME"); //$NON-NLS-1$
  String STR_N_DESCR    = Messages.getString("STR_N_DESCR"); //$NON-NLS-1$
  String STR_D_DESCR    = Messages.getString("STR_D_DESCR"); //$NON-NLS-1$
}
