package org.toxsoft.skf.bridge.incub.rtpeer.helpers;

import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.skf.bridge.incub.rtpeer.*;

/**
 * The kind of RT peer data source, mainly determines protocol to use for communication.
 * <p>
 * Examples of kinds are ({@link #id()} of kind is also returned by {@link IRtPeerNodeId#proto()}:
 * <ul>
 * <li>OPC UA protocol - {@link #id()} = "<code>opcua</code>";</li>
 * <li>Modbus, either IP or RTU protocol - {@link #id()} = "<code>modbus</code>";</li>
 * <li>OPC protocol - {@link #id()} = "<code>opc</code>";</li>
 * <li>some other protocols like IEC defined protocols, IoT protocols, etc.</li>
 * </ul>
 *
 * @author hazard157
 */
public interface IRtPeerKind
    extends IStridableParameterized {

  @Override
  String id(); // the same is returned by IRtNeerNodeId.proto()

  // ???

  IList<Object> browseSources( /* dicovery options? */ ); // ??? return value

}
