package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;

/**
 * Определитель наличия составляющих комплексного тега в юните.
 *
 * @author max
 */
public interface IComplexTagDetector {

  /**
   * Определяет наличие составляющих комплексного тега в юните.
   *
   * @param aUnit OpcToS5DataCfgUnit - конфигурационный юнит
   * @param aContext ITsGuiContext - контекст
   * @return true - если в юните есть составляющие комплексного тега
   */
  boolean hasComplexNode( OpcToS5DataCfgUnit aUnit, ITsGuiContext aContext );
}
