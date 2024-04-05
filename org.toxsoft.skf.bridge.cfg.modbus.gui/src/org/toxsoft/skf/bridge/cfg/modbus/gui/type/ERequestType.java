package org.toxsoft.skf.bridge.cfg.modbus.gui.type;

/**
 * Типы запросов к modbus
 *
 * @author hazard157
 */
public enum ERequestType {

  /**
   * Чтение дискретного выхода
   */
  DO, //

  /**
   * Чтение аналогового выхода
   */
  AO, //

  /**
   * Чтение дискретного входа
   */
  DI, //

  /**
   * Чтение аналогового входа
   */
  AI; //
}
