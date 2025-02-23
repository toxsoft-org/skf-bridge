package org.toxsoft.skf.bridge.incub.rtpeer.mapping;

import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.coll.*;

/**
 * TODO automatization rules/scripts sections "POLIGON", "SIEMENS", "L2 way1", "L2 way 2", etc
 *
 * @author hazard157
 */
public interface IRtPeerUskatMappingProfile
    extends IStridableParameterized {

  // TODO following API is to be developed!!!

  // ???

  IList<Object> listRules();

  IList<Object> listScripts();

}
