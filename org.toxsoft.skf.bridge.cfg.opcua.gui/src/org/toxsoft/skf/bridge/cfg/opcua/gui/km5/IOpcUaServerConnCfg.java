package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;

import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.*;

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
   * Returns user password.
   *
   * @return String - user password.
   */
  default String password() {
    return OPDEF_PASSWORD.getValue( params() ).asString();
  }

  /**
   * @return EOPCUATreeType { @link EOPCUATreeType} type of OPC UA tree
   */
  default EOPCUATreeType treeType() {
    return params().getValobj( OpcUaServerConnCfgModel.FID_TREE_TYPE, EOPCUATreeType.SIEMENS_BAIKONUR );
  }

}
