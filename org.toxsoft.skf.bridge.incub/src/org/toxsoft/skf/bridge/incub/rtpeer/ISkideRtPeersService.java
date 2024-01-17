package org.toxsoft.skf.bridge.incub.rtpeer;

import org.toxsoft.skf.bridge.incub.rtpeer.mapping.*;

// TODO ISkideRtPeersService
@SuppressWarnings( "javadoc" )
public interface ISkideRtPeersService {

  ISkideRtPeersProvider peersProvider();

  IRtPeerSource getSource( String aSourceId );

  IRtPeerUskatMappingCfg readMappingConfig( String aSourceId );

  void writeMappingConfig( String aSourceId, IRtPeerUskatMappingCfg aCfg );

}
