package org.toxsoft.skf.bridge.incub.rtpeer;

/**
 * The RT peer node.
 *
 * @author hazard157
 */
@SuppressWarnings( "javadoc" )
public interface IRtPeerNodeInfo {

  IRtPeerNodeId rtpNodeId();

  // TODO following API is to be developed!!!

  // ???

  boolean isDataNode();

  boolean isRead();

  boolean isWrite();

}
