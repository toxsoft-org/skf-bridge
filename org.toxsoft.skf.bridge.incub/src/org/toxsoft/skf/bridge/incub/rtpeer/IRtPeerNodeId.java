package org.toxsoft.skf.bridge.incub.rtpeer;

import org.toxsoft.core.tslib.coll.primtypes.*;

/**
 * The ID of the {@link IRtPeerNodeInfo}.
 * <p>
 * TODO full (with protocol/data source) and short IDs<br>
 * TODO IDs are keepable and persistent<br>
 * TODO examples
 * <ul>
 * <li>"<code>opcua://Server Name/path1/path2/to the node</code>";</li>
 * <li>"<code>modbus://Bus identification (Name)/path1/path2/to the node</code>";</li>
 * <li>"<code>opc://Server Name/tag name</code>".</li>
 * </ul>
 *
 * @author hazard157
 */
@SuppressWarnings( "javadoc" )
public interface IRtPeerNodeId {

  String proto(); // "opcua", "modbus", "opc", "iecNNN", etc

  String serverName(); // "GBH OPC UA server", "GWP site #157 Modbus", etc

  IStringList path(); // "path1/path2/to the node", "tag name", etc

  String canonicalString(); // like noted above - "opcua://Server Name/path1/path2/to the node"

  // TODO maybe something else?

}
