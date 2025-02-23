package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.uskat.core.connection.*;

/**
 * Former of configuration units in auto mode
 *
 * @author max
 */
public interface IAutoLinkConfigurationProcess {

  /**
   * Forms configuration units of mapping opc ua nodes and s5 entities.
   *
   * @param aContext ITsGuiContext - context.
   * @param currConn ISkConnection - current s5 connection which entities are to be mapped.
   * @param conConf OpcUaServerConnCfg - current opc ua connection cfg which nodes are to be mapped.
   * @return IList - configuration units list
   */
  IList<OpcToS5DataCfgUnit> formCfgUnitsFromAutoElements( ITsGuiContext aContext, ISkConnection currConn,
      OpcUaServerConnCfg conConf );
}
