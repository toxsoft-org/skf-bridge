package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.core.tslib.av.*;

/**
 * Конвертор идентификатора
 *
 * @author max
 */
public interface INodeIdConvertor {

  /**
   * Выделяет идентификатор из сущности узла (тега, регистра), хранящейся в виде VALOBJ в AtomicValue.
   *
   * @param aNodeEntity IAtomicValue - сущность, содержащая узел или аналог.
   * @return String - идентификатор.
   */
  String converToNodeId( IAtomicValue aNodeEntity );
}
