package org.toxsoft.skf.bridge.cfg.modbus.gui.l10n;

import org.toxsoft.skf.bridge.cfg.modbus.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.panels.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;

/**
 * Localizable resources.
 *
 * @author hazard157
 */
@SuppressWarnings( "javadoc" )
public interface ISkBridgeCfgModbusGuiSharedResources {

  /**
   * {@link PanelModbusDeviceSelector}
   */
  String STR_SELECT_MODBUS_DEVICE   = Messages.getString( "STR_SELECT_MODBUS_DEVICE" );   //$NON-NLS-1$
  String STR_SELECT_MODBUS_DEVICE_D = Messages.getString( "STR_SELECT_MODBUS_DEVICE_D" ); //$NON-NLS-1$

  /**
   * {@link ModbusToS5CfgDocEditorPanel}
   */
  String STR_SELECT_MULTI_COPY_DEST_SKID   = Messages.getString( "STR_SELECT_MULTI_COPY_DEST_SKID" );   //$NON-NLS-1$
  String STR_SELECT_MULTI_COPY_DEST_SKID_D = Messages.getString( "STR_SELECT_MULTI_COPY_DEST_SKID_D" ); //$NON-NLS-1$
  String STR_TAB_DATA_BINDINGS             = Messages.getString( "STR_TAB_DATA_BINDINGS" );             //$NON-NLS-1$
  String STR_TAB_DATA_BINDINGS_D           = Messages.getString( "STR_TAB_DATA_BINDINGS_D" );           //$NON-NLS-1$
  String STR_TAB_MODBUS_DEVICES            = Messages.getString( "STR_TAB_MODBUS_DEVICES" );            //$NON-NLS-1$
  String STR_TAB_MODBUS_DEVICES_D          = Messages.getString( "STR_TAB_MODBUS_DEVICES_D" );          //$NON-NLS-1$
  String FMT_FILTERED_DEVICE_ADDRESS       = Messages.getString( "FMT_FILTERED_DEVICE_ADDRESS" );       //$NON-NLS-1$
  String FMT_FILTERED_DEVICE_ADDRESS_D     = Messages.getString( "FMT_FILTERED_DEVICE_ADDRESS_D" );     //$NON-NLS-1$
  String STR_ACT_FILTER_BY_ADDRESS         = Messages.getString( "STR_ACT_FILTER_BY_ADDRESS" );         //$NON-NLS-1$
  String STR_ACT_FILTER_BY_ADDRESS_D       = Messages.getString( "STR_ACT_FILTER_BY_ADDRESS_D" );       //$NON-NLS-1$
  String STR_ACT_MULTI_COPY                = Messages.getString( "STR_ACT_MULTI_COPY" );                //$NON-NLS-1$
  String STR_ACT_MULTI_COPY_D              = Messages.getString( "STR_ACT_MULTI_COPY_D" );              //$NON-NLS-1$

  /**
   * {@link ERequestType}
   */
  String STR_ERT_DO   = Messages.getString( "ERequestType.STR_ERT_DO" );   //$NON-NLS-1$
  String STR_ERT_DO_D = Messages.getString( "ERequestType.STR_ERT_DO_D" ); //$NON-NLS-1$
  String STR_ERT_AO   = Messages.getString( "ERequestType.STR_ERT_AO" );   //$NON-NLS-1$
  String STR_ERT_AO_D = Messages.getString( "ERequestType.STR_ERT_AO_D" ); //$NON-NLS-1$
  String STR_ERT_DI   = Messages.getString( "ERequestType.STR_ERT_DI" );   //$NON-NLS-1$
  String STR_ERT_DI_D = Messages.getString( "ERequestType.STR_ERT_DI_D" ); //$NON-NLS-1$
  String STR_ERT_AI   = Messages.getString( "ERequestType.STR_ERT_AI" );   //$NON-NLS-1$
  String STR_ERT_AI_D = Messages.getString( "ERequestType.STR_ERT_AI_D" ); //$NON-NLS-1$

  /**
   * {@link ModbusDeviceM5Model}
   */
  String STR_MDEV_NAME     = Messages.getString( "STR_MDEV_NAME" );     //$NON-NLS-1$
  String STR_MDEV_NAME_D   = Messages.getString( "STR_MDEV_NAME_D" );   //$NON-NLS-1$
  String STR_MDEV_IS_TCP   = Messages.getString( "STR_MDEV_IS_TCP" );   //$NON-NLS-1$
  String STR_MDEV_IS_TCP_D = Messages.getString( "STR_MDEV_IS_TCP_D" ); //$NON-NLS-1$
  String STR_MDEV_PARAMS   = Messages.getString( "STR_MDEV_PARAMS" );   //$NON-NLS-1$
  String STR_MDEV_PARAMS_D = Messages.getString( "STR_MDEV_PARAMS_D" ); //$NON-NLS-1$

  /**
   * {@link ModbusDeviceM5LifecycleManager}
   */
  String MSG_ERR_INVALID_IP_ADDRESS = Messages.getString( "MSG_ERR_INVALID_IP_ADDRESS" ); //$NON-NLS-1$

  /**
   * {@link ModbusToS5CfgDoc}
   */
  String LOG_ERR_READING_CONFIG = "Error reading configuration from Strio stream"; //$NON-NLS-1$

  /**
   * {@link ModbusToS5CfgDocM5Model}
   */
  String STR_BRIDGE_CFG_FILE_DIR         = Messages.getString( "STR_BRIDGE_CFG_FILE_DIR" );         //$NON-NLS-1$
  String STR_BRIDGE_CFG_FILE_DIR_D       = Messages.getString( "STR_BRIDGE_CFG_FILE_DIR_D" );       //$NON-NLS-1$
  String STR_BRIDGE_CFG_FILE_BARE_NAME   = Messages.getString( "STR_BRIDGE_CFG_FILE_BARE_NAME" );   //$NON-NLS-1$
  String STR_BRIDGE_CFG_FILE_BARE_NAME_D = Messages.getString( "STR_BRIDGE_CFG_FILE_BARE_NAME_D" ); //$NON-NLS-1$

  /**
   * {@link ModbusDeviceOptionsUtils}
   */
  String STR_MDEV_TCP_IP_ADDRESS   = Messages.getString( "STR_MDEV_TCP_IP_ADDRESS" );   //$NON-NLS-1$
  String STR_MDEV_TCP_IP_ADDRESS_D = Messages.getString( "STR_MDEV_TCP_IP_ADDRESS_D" ); //$NON-NLS-1$
  String STR_MDEV_TCP_PORT_NO      = Messages.getString( "STR_MDEV_TCP_PORT_NO" );      //$NON-NLS-1$
  String STR_MDEV_TCP_PORT_NO_D    = Messages.getString( "STR_MDEV_TCP_PORT_NO_D" );    //$NON-NLS-1$
  String STR_MDEV_RTU_PORT_NAME    = Messages.getString( "STR_MDEV_RTU_PORT_NAME" );    //$NON-NLS-1$
  String STR_MDEV_RTU_PORT_NAME_D  = Messages.getString( "STR_MDEV_RTU_PORT_NAME_D" );  //$NON-NLS-1$
  String STR_MDEV_RTU_DEV_ADDR     = Messages.getString( "STR_MDEV_RTU_DEV_ADDR" );     //$NON-NLS-1$
  String STR_MDEV_RTU_DEV_ADDR_D   = Messages.getString( "STR_MDEV_RTU_DEV_ADDR_D" );   //$NON-NLS-1$

  /**
   * org.toxsoft.skf.bridge.cfg.modbus.gui.km5.IPackageConstants
   */
  String STR_ACT_MANUAL_ADD    = Messages.getString( "STR_ACT_MANUAL_ADD" );    //$NON-NLS-1$
  String STR_ACT_MANUAL_ADD_D  = Messages.getString( "STR_ACT_MANUAL_ADD_D" );  //$NON-NLS-1$
  String STR_ACT_MANUAL_EDIT   = Messages.getString( "STR_ACT_MANUAL_EDIT" );   //$NON-NLS-1$
  String STR_ACT_MANUAL_EDIT_D = Messages.getString( "STR_ACT_MANUAL_EDIT_D" ); //$NON-NLS-1$

  /**
   * {@link ModbusNodesForCfgM5Model}
   */
  String STR_MBNODE_DEVICE         = Messages.getString( "STR_MBNODE_DEVICE" );         //$NON-NLS-1$
  String STR_MBNODE_DEVICE_D       = Messages.getString( "STR_MBNODE_DEVICE_D" );       //$NON-NLS-1$
  String STR_MBNODE_REGISTER       = Messages.getString( "STR_MBNODE_REGISTER" );       //$NON-NLS-1$
  String STR_MBNODE_REGISTER_D     = Messages.getString( "STR_MBNODE_REGISTER_D" );     //$NON-NLS-1$
  String STR_MBNODE_TRANSLATOR     = Messages.getString( "STR_MBNODE_TRANSLATOR" );     //$NON-NLS-1$
  String STR_MBNODE_TRANSLATOR_D   = Messages.getString( "STR_MBNODE_TRANSLATOR_D" );   //$NON-NLS-1$
  String STR_MBNODE_WORDS_COUNT    = Messages.getString( "STR_MBNODE_WORDS_COUNT" );    //$NON-NLS-1$
  String STR_MBNODE_WORDS_COUNT_D  = Messages.getString( "STR_MBNODE_WORDS_COUNT_D" );  //$NON-NLS-1$
  String STR_MBNODE_VALUE_TYPE     = Messages.getString( "STR_MBNODE_VALUE_TYPE" );     //$NON-NLS-1$
  String STR_MBNODE_VALUE_TYPE_D   = Messages.getString( "STR_MBNODE_VALUE_TYPE_D" );   //$NON-NLS-1$
  String STR_MBNODE_REQUEST_TYPE   = Messages.getString( "STR_MBNODE_REQUEST_TYPE" );   //$NON-NLS-1$
  String STR_MBNODE_REQUEST_TYPE_D = Messages.getString( "STR_MBNODE_REQUEST_TYPE_D" ); //$NON-NLS-1$
  String STR_MBNODE_PARAM_STR      = Messages.getString( "STR_MBNODE_PARAM_STR" );      //$NON-NLS-1$
  String STR_MBNODE_PARAM_STR_D    = Messages.getString( "STR_MBNODE_PARAM_STR_D" );    //$NON-NLS-1$

}
