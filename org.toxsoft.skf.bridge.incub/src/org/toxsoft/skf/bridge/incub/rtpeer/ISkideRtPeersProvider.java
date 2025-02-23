package org.toxsoft.skf.bridge.incub.rtpeer;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.skf.bridge.incub.rtpeer.mapping.*;

public interface ISkideRtPeersProvider {

  IStridablesListEdit<IRtPeerSourceInfo> listSources();

  IStridablesListEdit<IRtPeerUskatMappingProfile> listMappingProfiles();

  IRtPeerSourceInfo addSource( IStridableParameterized aId, IOptionSet aConnectionConfig );

  IRtPeerSourceInfo removeSource( String aSourceId );

}
