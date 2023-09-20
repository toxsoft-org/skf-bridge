package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;

/**
 * Opc ua connection implementation.
 *
 * @author max
 */
public class OpcUaServerConnCfg
    extends StridableParameterized
    implements IOpcUaServerConnCfg {

  /**
   * Constructor by id
   *
   * @param aId String - identifier.
   * @param aParams IOptionSet - params of connection.
   */
  public OpcUaServerConnCfg( String aId, IOptionSet aParams ) {
    super( aId, aParams );
  }

}
