package org.toxsoft.skf.bridge.cfg.modbus.gui.type;

/**
 * Localizable resources.
 *
 * @author dima
 */
@SuppressWarnings( "javadoc" )
public interface ISkResources {

  /**
   * {@link ERequestType}
   */
  String STR_N_REQ_TYPE_AO = "AO (FC3)"; //$NON-NLS-1$
  String STR_N_REQ_TYPE_AI = "AI (FC4)"; //$NON-NLS-1$
  String STR_N_REQ_TYPE_DO = "DO (FC1)"; //$NON-NLS-1$
  String STR_N_REQ_TYPE_DI = "DI (FC2)"; //$NON-NLS-1$

  /**
   * {@link ModbusDeviceOptionsUtils}
   */
  String STR_N_TCP_IP_ADDRESS = Messages.STR_N_TCP_IP_ADDRESS;
  String STR_D_TCP_IP_ADDRESS = Messages.STR_D_TCP_IP_ADDRESS;
  String STR_N_TCP_PORT       = Messages.STR_N_TCP_PORT;
  String STR_D_TCP_PORT       = Messages.STR_D_TCP_PORT;
  String STR_N_RTU_PORT_NAME  = Messages.STR_N_RTU_PORT_NAME;
  String STR_D_RTU_PORT_NAME  = Messages.STR_D_RTU_PORT_NAME;
  String STR_N_RTU_ADDRESS    = Messages.STR_N_RTU_ADDRESS;
  String STR_D_RTU_ADDRESS    = Messages.STR_D_RTU_ADDRESS;
}
