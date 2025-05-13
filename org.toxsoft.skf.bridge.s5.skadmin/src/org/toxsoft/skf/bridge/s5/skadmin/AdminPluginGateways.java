package org.toxsoft.skf.bridge.s5.skadmin;

import org.toxsoft.skf.bridge.s5.skadmin.gateways.*;
import org.toxsoft.uskat.skadmin.core.plugins.*;

/**
 * Плагин s5admin: команды управления конфигурациями s5-шлюзов
 *
 * @author mvk
 */
public class AdminPluginGateways
    extends AbstractPluginCmdLibrary {

  /**
   * ИД-путь команд которые находятся в плагине
   */
  public static final String CMD_PATH = "sk.ext.gateways."; //$NON-NLS-1$

  // ------------------------------------------------------------------------------------
  // Реализация абстрактных методов AbstractPluginCmdLibrary
  //
  @Override
  public String getName() {
    return getClass().getName();
  }

  @Override
  protected void doInit() {
    // Шлюзы
    addCmd( new AdminCmdListConfigs() );
    addCmd( new AdminCmdRemoveConfig() );
    addCmd( new AdminCmdAddConfig() );
  }

  @Override
  protected void doClose() {
    // nop
  }
}
