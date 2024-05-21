package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;

import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.fields.*;

/**
 * M5 model realization for {@link ModbusToS5CfgDoc} entities.
 *
 * @author max
 */
public class ModbusToS5CfgDocM5Model
    extends M5Model<ModbusToS5CfgDoc> {

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.modbus.m5.ModbusToS5CfgDocM5Model"; //$NON-NLS-1$

  /**
   * Field {@link ModbusToS5CfgDoc#id()}
   */
  public static final M5AttributeFieldDef<ModbusToS5CfgDoc> ID = new M5StdFieldDefId<>();

  /**
   * Field {@link ModbusToS5CfgDoc#nmName()}
   */
  public static final M5AttributeFieldDef<ModbusToS5CfgDoc> NAME = new M5StdFieldDefName<>();

  /**
   * Field {@link ModbusToS5CfgDoc#description()}
   */
  public static final M5AttributeFieldDef<ModbusToS5CfgDoc> DESCRIPTION = new M5StdFieldDefDescription<>();

  /**
   * Constructor.
   */
  public ModbusToS5CfgDocM5Model() {
    super( MODEL_ID, ModbusToS5CfgDoc.class );
    // прячем никому не нужный ID
    ID.setFlags( M5FF_HIDDEN | M5FF_INVARIANT );
    addFieldDefs( ID, NAME, DESCRIPTION );

  }
}
