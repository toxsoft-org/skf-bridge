package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;

import org.toxsoft.core.tslib.bricks.strid.*;

/**
 * Opc ua connection params
 *
 * @author max
 */
public interface IOpcUaServerConnCfg
    extends IStridableParameterized {

  /**
   * Returns server host.
   *
   * @return String - server host.
   */
  default String host() {
    return OPDEF_HOST.getValue( params() ).asString();
  }

  /**
   * Returns user login.
   *
   * @return String - user login.
   */
  default String login() {
    return OPDEF_LOGIN.getValue( params() ).asString();
  }

  /**
   * Returns user passward.
   *
   * @return String - user passward.
   */
  default String passward() {
    return OPDEF_PASSWORD.getValue( params() ).asString();
  }

}
