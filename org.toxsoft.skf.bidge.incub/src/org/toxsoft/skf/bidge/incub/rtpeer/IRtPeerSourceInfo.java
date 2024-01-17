package org.toxsoft.skf.bidge.incub.rtpeer;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.coll.*;

/**
 * The single source of the RT peer meta-data.
 * <p>
 * Corresponds to the concepts like single OPC (UA) server, one Modbus IP/RTU bus, etc.
 * <p>
 * Provides the tree of the nodes {@link IRtPeerNodeInfo}. Nodes my be either a grouping node or the RT data I/O gate,
 * event both kind of a same time. For example, <i>int16</i> OPC tag is a Data I/O gate but may contain (may be
 * interpreted as) 16 bits (<i>boolean</i>) child nodes.
 * <p>
 * THis is a {@link IStridableParameterized} entity:
 * <ul>
 * <li>{@link #id()} - the ID, name and description is assigned by the user to distinguish different sources;</li>
 * <li>{@link #params()} - may contain other SkIDE.user specific options like access protocol, etc.;</li>
 * <li>{@link #connectionConfig()} - is the configuration information needed to establish communication with the
 * source</li>
 * </ul>
 *
 * @author hazard157
 */
@SuppressWarnings( "javadoc" )
public interface IRtPeerSourceInfo
    extends IStridableParameterized {

  IOptionSet connectionConfig();

  // ------------------------------------------------------------------------------------
  // Nodes tree representation

  IRtPeerNodeId rootId();

  IRtPeerNodeId getParentId( IRtPeerNodeId aNodeId );

  IList<IRtPeerNodeId> getChildIds( IRtPeerNodeId aNodeId );

  IMap<IRtPeerNodeId, IRtPeerNodeInfo> getChildNodes( IRtPeerNodeId aNodeId );

  IRtPeerNodeInfo getNode( IRtPeerNodeId aNodeId );

}
