package org.toxsoft.skf.bridge.lib.halfs.outer;

import org.toxsoft.core.tslib.bricks.ctx.*;
import org.toxsoft.skf.bridge.lib.halfs.*;
import org.toxsoft.skf.bridge.lib.halfs.inner.*;

/**
 * Base class to implement outer half of the bridge.
 *
 * @author hazard157
 */
public class AbstractBridgeOuterHalf
    extends AbstractBridgeHalf {

  private final BridgeInnerHalf innerHalf;

  protected AbstractBridgeOuterHalf( ITsContext aArgs, BridgeInnerHalf aInnerHalf ) {
    super( aArgs );
    innerHalf = aInnerHalf;
  }

}
