package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.km5.ISkResources.*;

import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.fields.*;
import org.toxsoft.core.tsgui.rcp.valed.*;
import org.toxsoft.core.tslib.av.*;

/**
 * M5 model realization for {@link ModbusToS5CfgDoc} entities.
 *
 * @author max
 * @author dima
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
   * Path to l2 dir
   */
  public static final String FID_PATH_TO_L2 = "path.to.l2"; //$NON-NLS-1$

  /**
   * Cfg file name (without extension)
   */
  public static final String FID_CFG_FILE_NAME = "cfg.file.name"; //$NON-NLS-1$

  /**
   * Attribute {@link ModbusToS5CfgDoc#getL2Path() } path to l2 dir
   */
  public static final M5AttributeFieldDef<ModbusToS5CfgDoc> PATH_TO_L2 =
      new M5AttributeFieldDef<>( FID_PATH_TO_L2, IValedFileConstants.DT_DIRECTORY_FILE, TSID_NAME, STR_N_PATH_TO_L2, //
          TSID_DESCRIPTION, STR_D_PATH_TO_L2 ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( ModbusToS5CfgDoc aEntity ) {
          return avValobj( aEntity.getL2Path() );
        }

      };

  /**
   * Attribute {@link ModbusToS5CfgDoc#getCfgFilesPrefix() } display name
   */
  public static final M5AttributeFieldDef<ModbusToS5CfgDoc> CFG_FILE_NAME =
      new M5AttributeFieldDef<>( FID_CFG_FILE_NAME, EAtomicType.STRING, //
          TSID_NAME, STR_N_CFG_FILE_NAME, //
          TSID_DESCRIPTION, STR_D_CFG_FILE_NAME //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( ModbusToS5CfgDoc aEntity ) {
          return avStr( aEntity.getCfgFilesPrefix() );
        }

      };

  /**
   * Constructor.
   */
  public ModbusToS5CfgDocM5Model() {
    super( MODEL_ID, ModbusToS5CfgDoc.class );
    // прячем никому не нужный ID
    ID.setFlags( M5FF_HIDDEN | M5FF_INVARIANT );
    addFieldDefs( ID, NAME, PATH_TO_L2, CFG_FILE_NAME, DESCRIPTION );

  }
}
