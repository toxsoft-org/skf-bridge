package org.toxsoft.skf.bridge.incub.rtpeer.mapping;

/**
 * Configuration of the RT peer source to USkat entities mapping.
 * <p>
 * This is a keepable data.
 *
 * @author hazard157
 */
@SuppressWarnings( "javadoc" )
public interface IRtPeerUskatMappingCfg {

  // TODO following API is to be developed!!!

  // ???

  // TODO RT peer node ID <-> GWID mapping

  // TODO configuration info to generate DLM and DEV configs for L2

  String mappingProfileId(); // ID in the list ISkideRtPeersProvider.listMappingProfiles()

  // TODO if not stored in SkIDE sysdescr/RRI etc, then here we need SkClass infos, RRI section/param infos to store

}
