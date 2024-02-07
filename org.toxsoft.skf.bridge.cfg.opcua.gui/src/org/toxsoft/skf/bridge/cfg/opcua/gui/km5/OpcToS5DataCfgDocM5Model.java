package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;

import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
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
   * string id of cfg doc
   */
  public static final String FID_STRID = "strid"; //$NON-NLS-1$

  /**
   * name of cfg doc
   */
  public static final String FID_NAME = "name"; //$NON-NLS-1$

  /**
   * descr of cfg doc
   */
  public static final String FID_DESCR = "descr"; //$NON-NLS-1$

  /**
   * Attribute {@link OpcToS5DataCfgDoc#id() } string id
   */
  final M5AttributeFieldDef<OpcToS5DataCfgDoc> STRID = new M5AttributeFieldDef<>( FID_STRID, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_STRID, //
      TSID_DESCRIPTION, STR_D_PARAM_STRID, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_HIDDEN | M5FF_READ_ONLY );
    }

    protected IAtomicValue doGetFieldValue( OpcToS5DataCfgDoc aEntity ) {
      return avStr( aEntity.id() );
    }

  };

  /**
   * Attribute {@link OpcToS5DataCfgDoc#nmName() } name
   */
  final M5AttributeFieldDef<OpcToS5DataCfgDoc> NAME = new M5AttributeFieldDef<>( FID_NAME, EAtomicType.STRING, //
      TSID_NAME, STR_N_CONFIG_NAME, //
      TSID_DESCRIPTION, STR_D_CONFIG_NAME, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( OpcToS5DataCfgDoc aEntity ) {
      return avStr( aEntity.nmName() );
    }

  };

  /**
   * Attribute {@link OpcToS5DataCfgDoc#description() } description
   */
  final M5AttributeFieldDef<OpcToS5DataCfgDoc> DESCR = new M5AttributeFieldDef<>( FID_DESCR, EAtomicType.STRING, //
      TSID_NAME, STR_N_CONFIG_DESCR, //
      TSID_DESCRIPTION, STR_D_CONFIG_DESCR, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( OpcToS5DataCfgDoc aEntity ) {
      return avStr( aEntity.description() );
    }

  };

  /**
   * Constructor.
   */
  public OpcToS5DataCfgDocM5Model() {
    super( MODEL_ID, OpcToS5DataCfgDoc.class );

    addFieldDefs( STRID, NAME, DESCR );

  }
}
