package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.fields.*;

/**
 * M5 model realization for {@link OpcToS5DataCfgDoc} entities.
 *
 * @author max
 */
public class OpcToS5DataCfgDocM5Model
    extends M5Model<OpcToS5DataCfgDoc> {

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.opcua.m5.OpcToS5DataCfgDocM5Model"; //$NON-NLS-1$

  /**
   * Field {@link OpcToS5DataCfgDoc#id()}
   */
  public static final M5AttributeFieldDef<OpcToS5DataCfgDoc> ID = new M5StdFieldDefId<>();

  /**
   * Field {@link OpcToS5DataCfgDoc#nmName()}
   */
  public static final M5AttributeFieldDef<OpcToS5DataCfgDoc> NAME = new M5StdFieldDefName<>();

  /**
   * Field {@link OpcToS5DataCfgDoc#description()}
   */
  public static final M5AttributeFieldDef<OpcToS5DataCfgDoc> DESCRIPTION = new M5StdFieldDefDescription<>();

  /**
   * Constructor.
   */
  public OpcToS5DataCfgDocM5Model() {
    super( MODEL_ID, OpcToS5DataCfgDoc.class );

    addFieldDefs( ID, NAME, DESCRIPTION );

  }
}
