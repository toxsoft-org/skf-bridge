package org.toxsoft.skf.bridge.cfg.opcua.gui.types;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;

/**
 * Type of realization of cfg unit with params defenition. Implementation. Need registration in the holder.
 *
 * @author max
 */
public interface ICfgUnitRealizationType
    extends IStridable {

  //
  ECfgUnitType cfgUnitType();

  // поля реализации
  IStridablesList<IDataDef> paramDefenitions();

  IOptionSet getDefaultValues();

  /**
   * Создаёт начальную конфигурацию узла opc для драйвера
   *
   * @param aNodeId - идентификатор узла
   * @param aNodeIndex - номер узла
   * @param aNodeCount - общее количество узлов
   * @return CfgOpcUaNode - начальная конфигурация, по умолчанию - асинхронный узел Integer на чтение.
   */
  CfgOpcUaNode createInitCfg( ITsGuiContext aContext, String aNodeId, int aNodeIndex, int aNodeCount );
}
