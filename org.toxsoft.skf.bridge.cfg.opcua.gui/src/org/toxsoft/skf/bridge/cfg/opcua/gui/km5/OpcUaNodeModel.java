package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;

import org.eclipse.milo.opcua.sdk.core.nodes.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;

/**
 * M5 model realization for {@link Node} entities.
 *
 * @author max
 */
public class OpcUaNodeModel
    extends M5Model<UaTreeNode> {

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.opcua.m5.OpcUaNodeModel"; //$NON-NLS-1$

  /**
   * string id of node
   */
  public static final String FID_STRID = "strid"; //$NON-NLS-1$

  /**
   * browse name of node
   */
  public static final String FID_BROWSE_NAME = "browse.name"; //$NON-NLS-1$

  /**
   * display name of node
   */
  public static final String FID_DISPLAY_NAME = "display.name"; //$NON-NLS-1$

  /**
   * comment of node
   */
  public static final String FID_COMMENT = "comment"; //$NON-NLS-1$

  /**
   * Attribute {@link Node#getNodeId() } string id
   */
  public M5AttributeFieldDef<UaTreeNode> STRID = new M5AttributeFieldDef<>( FID_STRID, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_STRID, //
      TSID_DESCRIPTION, STR_D_PARAM_STRID, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( UaTreeNode aEntity ) {
      return avStr( aEntity.getNodeId() );
    }

  };

  /**
   * Attribute {@link Node#getBrowseName() } browse name
   */
  public M5AttributeFieldDef<UaTreeNode> BROWSE_NAME = new M5AttributeFieldDef<>( FID_BROWSE_NAME, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_BROWSE_NAME, //
      TSID_DESCRIPTION, STR_D_PARAM_BROWSE_NAME, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( UaTreeNode aEntity ) {
      return avStr( aEntity.getBrowseName() );
    }

  };

  /**
   * Attribute {@link Node#getDescription() } description
   */
  public M5AttributeFieldDef<UaTreeNode> DISPLAY_NAME = new M5AttributeFieldDef<>( FID_DISPLAY_NAME, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_DISPLAY_NAME, //
      TSID_DESCRIPTION, STR_D_PARAM_DISPLAY_NAME, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( UaTreeNode aEntity ) {
      return avStr( aEntity.getDisplayName() );
    }

  };

  /**
   * Attribute {@link Node#getDisplayName() } display name
   */
  public M5AttributeFieldDef<UaTreeNode> DESCRIPTION = new M5AttributeFieldDef<>( FID_DESCRIPTION, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_DESCRIPTION, //
      TSID_DESCRIPTION, STR_D_PARAM_DESCRIPTION, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( UaTreeNode aEntity ) {
      return avStr( aEntity.getDescription() );
    }

  };

  /**
   * Constructor.
   */
  public OpcUaNodeModel() {
    super( MODEL_ID, UaTreeNode.class );

    addFieldDefs( STRID, BROWSE_NAME, DISPLAY_NAME, DESCRIPTION );
  }

}
