package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.utils.*;

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
   * @return Pair<String, String> - идентификатор, состоящий из идентификатора устройства (слева) и идентификатора сущности (справа).
   */
  Pair<String, String> converToNodeId( IAtomicValue aNodeEntity );
}
