package org.toxsoft.skf.bridge.incub.rtpeer;

import org.toxsoft.core.tslib.utils.*;

/**
 * The single source of the RT peer data.
 * <p>
 * TODO works together with {@link IRtPeerSourceInfo} ???
 *
 * @author hazard157
 */
@SuppressWarnings( "javadoc" )
public interface IRtPeerSource
    extends ICloseable {

  void openConnection();

  void closeConnection(); // may be opened again

  @Override
  void close(); // makes instance unusable

  /**
   * FIXME hot to work with source info?
   *
   * @return
   */
  // TODO void refreshSourceInfo(IRtPeerSourceInfo aSourceInfo ); // TODO do we need nodes caching API here?

  boolean isOpen();

  // FIXME where and how must be the actual data I/O? or it can not have general API, oply protocol specific?

  Object getValue( IRtPeerNodeId aNodeId );

  void setValue( IRtPeerNodeId aNodeId, Object aValue );

  // FIXME other data I/O API like batch get/set, change listeners, etc

}
