package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.fields.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;

/**
 * M5 model of Type of realization of cfg unit with params defenitions.
 *
 * @author max
 */
public class CfgUnitRealizationTypeM5Model
    extends M5Model<ICfgUnitRealizationType> {

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.opcua.m5.CfgUnitRealizationTypeM5Model"; //$NON-NLS-1$

  /**
   * Attribute {@link IStridable#nmName()}.
   */
  public final M5AttributeFieldDef<ICfgUnitRealizationType> NAME = new M5StdFieldDefName<>();

  /**
   * Constructor
   */
  public CfgUnitRealizationTypeM5Model() {
    super( MODEL_ID, ICfgUnitRealizationType.class );
    addFieldDefs( NAME );
  }

}
