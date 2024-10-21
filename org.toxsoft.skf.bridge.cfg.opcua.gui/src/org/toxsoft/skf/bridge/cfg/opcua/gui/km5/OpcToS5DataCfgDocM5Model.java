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
 * @author dima
 */
public class OpcToS5DataCfgDocM5Model
    extends M5Model<OpcToS5DataCfgDoc> {

  static String DEFAULT_END_POINT_URL   = "opc.tcp://192.168.12.57:4840"; //$NON-NLS-1$
  static String DEFAULT_USER_OPC_UA     = "user1";                        //$NON-NLS-1$
  static String DEFAULT_PASSWOR_DOPC_UA = "361";                          //$NON-NLS-1$

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
   * End point URL
   */
  public static final String FID_END_POINT_URL = "end.point.url"; //$NON-NLS-1$

  /**
   * OPC UA connection user name
   */
  public static final String FID_USER_OPC_UA = "user.opc_ua"; //$NON-NLS-1$

  /**
   * OPC UA connection password name
   */
  public static final String FID_PASSWORD_OPC_UA = "password.opc_ua"; //$NON-NLS-1$

  /**
   * Attribute {@link OpcToS5DataCfgDoc#getL2Path() } path to l2 dir
   */
  public static final M5AttributeFieldDef<OpcToS5DataCfgDoc> PATH_TO_L2 =
      new M5AttributeFieldDef<>( FID_PATH_TO_L2, IValedFileConstants.DT_DIRECTORY_FILE, TSID_NAME, STR_N_PATH_TO_L2, //
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
          return avValobj( aEntity.getL2Path() );
        }

      };

  /**
   * Attribute {@link OpcToS5DataCfgDoc#getCfgFilesPrefix() } config files name
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
   * Attribute {@link OpcToS5DataCfgDoc#getEndPointURL() } end point URL
   */
  public static final M5AttributeFieldDef<OpcToS5DataCfgDoc> END_POINT_URL =
      new M5AttributeFieldDef<>( FID_END_POINT_URL, EAtomicType.STRING, //
          TSID_NAME, STR_N_END_POINT_URL, //
          TSID_DESCRIPTION, STR_D_END_POINT_URL, //
          TSID_DEFAULT_VALUE, DEFAULT_END_POINT_URL //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( OpcToS5DataCfgDoc aEntity ) {
          return avStr( aEntity.getEndPointURL() );
        }

      };

  /**
   * Attribute {@link OpcToS5DataCfgDoc#getUserOPC_UA() } OPC UA user
   */
  public static final M5AttributeFieldDef<OpcToS5DataCfgDoc> USER_OPC_UA =
      new M5AttributeFieldDef<>( FID_USER_OPC_UA, EAtomicType.STRING, //
          TSID_NAME, STR_N_OPC_UA_USER, //
          TSID_DESCRIPTION, STR_D_OPC_UA_USER, //
          TSID_DEFAULT_VALUE, DEFAULT_USER_OPC_UA //
      ) {

        protected IAtomicValue doGetFieldValue( OpcToS5DataCfgDoc aEntity ) {
          return avStr( aEntity.getUserOPC_UA() );
        }

      };

  /**
   * Attribute {@link OpcToS5DataCfgDoc#getUserOPC_UA() } OPC UA password
   */
  public static final M5AttributeFieldDef<OpcToS5DataCfgDoc> PASSWORD_OPC_UA =
      new M5AttributeFieldDef<>( FID_PASSWORD_OPC_UA, EAtomicType.STRING, //
          TSID_NAME, STR_N_OPC_UA_PASSWORD, //
          TSID_DESCRIPTION, STR_D_OPC_UA_PASSWORD, //
          TSID_DEFAULT_VALUE, DEFAULT_PASSWOR_DOPC_UA //
      ) {

        protected IAtomicValue doGetFieldValue( OpcToS5DataCfgDoc aEntity ) {
          return avStr( aEntity.getPasswordOPC_UA() );
        }

      };

  /**
   * Constructor.
   */
  public OpcToS5DataCfgDocM5Model() {
    super( MODEL_ID, OpcToS5DataCfgDoc.class );
    addFieldDefs( NAME, PATH_TO_L2, CFG_FILE_NAME, END_POINT_URL, USER_OPC_UA, PASSWORD_OPC_UA, DESCRIPTION );

  }
}
