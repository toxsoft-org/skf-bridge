package org.toxsoft.skf.bridge.cfg.modbus.gui.panels;

import org.toxsoft.skf.bridge.cfg.modbus.gui.km5.*;

/**
 * Localizable resources.
 *
 * @author dima
 */
public interface ISkResources {

  /**
   * {@link TCPAddressM5Model}
   */
  String STR_MSG_SELECT_TCP_ADDR   = Messages.STR_MSG_SELECT_TCP_ADDR;
  String STR_DESCR_SELECT_TCP_ADDR = Messages.STR_DESCR_SELECT_TCP_ADDR;

  /**
   * {@link ModbusToS5CfgDocEditorPanel}
   */
  String DLG_T_SKID_SEL          = Messages.DLG_T_SKID_SEL;
  String STR_MSG_SKID_SELECTION  = Messages.STR_MSG_SKID_SELECTION;
  String STR_SEL_IP_ADDRESS      = Messages.STR_SEL_IP_ADDRESS;      // "IP: ";
  String STR_IP_ADDRESES         = Messages.STR_IP_ADDRESES;         // "IP адреса";
  String STR_N_SELECT_IP_ADDRESS = Messages.STR_N_SELECT_IP_ADDRESS; // "IP адрес";
  String STR_D_SELECT_IP_ADDRESS = Messages.STR_D_SELECT_IP_ADDRESS; // "Фильтр на выбранный IP адрес";
  String STR_N_COPY_ALL          = Messages.STR_N_COPY_ALL;          // "скопировать все";
  String STR_D_COPY_ALL          = Messages.STR_D_COPY_ALL;          // "Создать новые элементы сделав копии из всех
                                                                     // отображающихся";
}
