package org.toxsoft.skf.bidge.incub.rtpeer;

import org.toxsoft.skf.bidge.incub.rtpeer.mapping.*;

// TODO ISkideRtPeersService
@SuppressWarnings( "javadoc" )
public interface ISkideRtPeersService {

  ISkideRtPeersProvider peersProvider();

  IRtPeerSource getSource( String aSourceId );

  IRtPeerUskatMappingCfg readMappingConfig( String aSourceId );

  void writeMappingConfig( String aSourceId, IRtPeerUskatMappingCfg aCfg );

}
