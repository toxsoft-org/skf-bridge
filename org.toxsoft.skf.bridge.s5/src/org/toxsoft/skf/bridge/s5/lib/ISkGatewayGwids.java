package org.toxsoft.skf.bridge.s5.lib;

import org.toxsoft.core.tslib.gw.gwid.*;

/**
 * Описание конфигурации набора {@link Gwid} используемой подсистемой шлюза {@link IBaGateway}
 *
 * @author mvk
 */
public interface ISkGatewayGwids {

  /**
   * Возвращает типы {@link Gwid}-идентификаторов хранимых в конфигурации.
   *
   * @return {@link EGwidKind} тип идентификатора {@link Gwid}.
   */
  EGwidKind gwidKind();

  /**
   * Идентификаторы включаемых идентификаторов {@link Gwid}.
   *
   * @return {@link IGwidList} список включаемых идентификаторов {@link Gwid}.
   */
  IGwidList includeGwids();

  /**
   * Идентификаторы исключаемых идентификаторов {@link Gwid}.
   *
   * @return {@link IGwidList} список исключаемых идентификаторов {@link Gwid}.
   */
  IGwidList excludeGwids();

  /**
   * Включать/выключать идентификаторы {@link Gwid} предоставляемые службой качества данных.
   *
   * @return boolean <b>true</b> включать идентификаторы {@link Gwid}; <b>false</b> не включать идентификаторы
   *         {@link Gwid}.
   */
  boolean includeQualityGwids();

}
