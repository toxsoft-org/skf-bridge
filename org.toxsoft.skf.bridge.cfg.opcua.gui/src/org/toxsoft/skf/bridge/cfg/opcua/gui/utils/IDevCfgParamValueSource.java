package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.*;

/**
 * Источник значения параметра (пример простейшего источника - просто найти в контексте) по его названию
 *
 * @author max
 */
public interface IDevCfgParamValueSource {

  /**
   * Возвращает значение параметра по имени
   *
   * @param aParamName String - имя параметра
   * @param aContext ITsGuiContext - контекст
   * @return IAtomicValue - значение параметра или null
   */
  IAtomicValue getDevCfgParamValue( String aParamName, ITsGuiContext aContext );
}
