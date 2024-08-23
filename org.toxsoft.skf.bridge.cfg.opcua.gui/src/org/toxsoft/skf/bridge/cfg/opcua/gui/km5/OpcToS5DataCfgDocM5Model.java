package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;

import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.fields.*;
import org.toxsoft.core.tsgui.rcp.valed.*;
import org.toxsoft.core.tslib.av.*;

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
   * Field {@link OpcToS5DataCfgDoc#nmName()}
   */
  public static final M5AttributeFieldDef<OpcToS5DataCfgDoc> NAME = new M5StdFieldDefName<>();

  /**
   * Field {@link OpcToS5DataCfgDoc#description()}
   */
  public static final M5AttributeFieldDef<OpcToS5DataCfgDoc> DESCRIPTION = new M5StdFieldDefDescription<>();

  /**
   * Path to l2 dir
   */
  public static final String FID_PATH_TO_L2 = "path.to.l2"; //$NON-NLS-1$

  /**
   * Cfg file name (without extension)
   */
  public static final String FID_CFG_FILE_NAME = "cfg.file.name"; //$NON-NLS-1$

  /**
   * Attribute {@link OpcToS5DataCfgDoc#getL2Path() } path to l2 dir
   */
  public static final M5AttributeFieldDef<OpcToS5DataCfgDoc> PATH_TO_L2 =
      new M5AttributeFieldDef<>( FID_PATH_TO_L2, IValedFileConstants.DT_DIRECTORY_FILE,
          // EAtomicType.STRING, //
          TSID_NAME, STR_N_PATH_TO_L2, //
          TSID_DESCRIPTION, STR_D_PATH_TO_L2 // , //
      // OPID_EDITOR_FACTORY_NAME, ValedFile.FACTORY_NAME // suitable valed
      // OPID_EDITOR_FACTORY_NAME, ValedAvStringFile.FACTORY_NAME // suitable valed
      // OPID_EDITOR_FACTORY_NAME, ValedAvValobjFile.FACTORY_NAME // suitable valed
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( OpcToS5DataCfgDoc aEntity ) {
          // return avStr( aEntity.getL2Path() );
          return avValobj( aEntity.getL2Path() );
        }

      };

  /**
   * Attribute {@link OpcToS5DataCfgDoc#getCfgFilesPrefix() } display name
   */
  public static final M5AttributeFieldDef<OpcToS5DataCfgDoc> CFG_FILE_NAME =
      new M5AttributeFieldDef<>( FID_CFG_FILE_NAME, EAtomicType.STRING, //
          TSID_NAME, STR_N_CFG_FILE_NAME, //
          TSID_DESCRIPTION, STR_D_CFG_FILE_NAME //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( OpcToS5DataCfgDoc aEntity ) {
          return avStr( aEntity.getCfgFilesPrefix() );
        }

      };

  /**
   * Constructor.
   */
  public OpcToS5DataCfgDocM5Model() {
    super( MODEL_ID, OpcToS5DataCfgDoc.class );
    addFieldDefs( NAME, PATH_TO_L2, CFG_FILE_NAME, DESCRIPTION );

  }
}
