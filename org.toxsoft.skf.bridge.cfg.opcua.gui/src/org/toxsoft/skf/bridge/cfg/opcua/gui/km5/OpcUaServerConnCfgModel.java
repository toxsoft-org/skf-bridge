package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;

import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.fields.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.*;

/**
 * M5 model realization for {@link IOpcUaServerConnCfg} entities.
 *
 * @author max
 * @author dima
 */
public class OpcUaServerConnCfgModel
    extends M5Model<IOpcUaServerConnCfg> {

  /**
   * Идентификатор модели.
   */
  public static final String MODEL_ID = "bridge.cfg.opcua.m5.OpcUaServerConnCfg"; //$NON-NLS-1$

  /**
   * Атрибут {@link IOpcUaServerConnCfg#id()}.
   */
  public final M5AttributeFieldDef<IOpcUaServerConnCfg> ID = new M5StdFieldDefId<>();

  /**
   * Атрибут {@link IOpcUaServerConnCfg#nmName()}.
   */
  public final M5AttributeFieldDef<IOpcUaServerConnCfg> NAME = new M5StdFieldDefName<>();

  /**
   * Атрибут {@link IOpcUaServerConnCfg#description()}.
   */
  public final M5AttributeFieldDef<IOpcUaServerConnCfg> DESCRIPTION = new M5StdFieldDefDescription<>();

  /**
   * Атрибут {@link IOpcUaServerConnCfg#host()}.
   */
  public final M5AttributeFieldDef<IOpcUaServerConnCfg> HOST = new M5StdFieldDefParamAttr<>( OPDEF_HOST );

  /**
   * Атрибут {@link IOpcUaServerConnCfg#login()}.
   */
  public final M5AttributeFieldDef<IOpcUaServerConnCfg> LOGIN = new M5StdFieldDefParamAttr<>( OPDEF_LOGIN );

  /**
   * Атрибут {@link IOpcUaServerConnCfg#password()}.
   */
  public final M5AttributeFieldDef<IOpcUaServerConnCfg> PASSWORD = new M5StdFieldDefParamAttr<>( OPDEF_PASSWORD );

  /**
   * id field of tree type
   */
  public static final String FID_TREE_TYPE = "treeType"; //$NON-NLS-1$

  /**
   * Attribute {@link IOpcUaServerConnCfg#treeType() } type of OPC UA tree
   */
  public M5AttributeFieldDef<IOpcUaServerConnCfg> TREE_TYPE = new M5AttributeFieldDef<>( FID_TREE_TYPE, VALOBJ, //
      TSID_NAME, STR_N_PARAM_TREE_TYPE, //
      TSID_DESCRIPTION, STR_D_PARAM_TREE_TYPE, //
      TSID_KEEPER_ID, EOPCUATreeType.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( EOPCUATreeType.POLIGONE ) ) {

    protected IAtomicValue doGetFieldValue( IOpcUaServerConnCfg aEntity ) {
      return avValobj( aEntity.treeType() );
    }
  };

  /**
   * Конструктор.
   */
  public OpcUaServerConnCfgModel() {
    super( MODEL_ID, IOpcUaServerConnCfg.class );
    ID.setFlags( M5FF_HIDDEN | M5FF_INVARIANT );
    addFieldDefs( ID, NAME, TREE_TYPE, HOST, LOGIN, PASSWORD, DESCRIPTION );
  }

}
