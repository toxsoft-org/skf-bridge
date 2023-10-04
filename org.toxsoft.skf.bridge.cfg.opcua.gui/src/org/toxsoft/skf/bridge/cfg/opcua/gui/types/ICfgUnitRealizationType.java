package org.toxsoft.skf.bridge.cfg.opcua.gui.types;

import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;

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
}
