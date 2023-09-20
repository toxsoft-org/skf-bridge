package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;

import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.fields.*;

/**
 * M5 model realization for {@link IOpcUaServerConnCfg} entities.
 *
 * @author max
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
   * Атрибут {@link IOpcUaServerConnCfg#passward()}.
   */
  public final M5AttributeFieldDef<IOpcUaServerConnCfg> PASSWORD = new M5StdFieldDefParamAttr<>( OPDEF_PASSWORD );

  /**
   * Конструктор.
   */
  public OpcUaServerConnCfgModel() {
    super( MODEL_ID, IOpcUaServerConnCfg.class );
    ID.setFlags( M5FF_HIDDEN | M5FF_INVARIANT );
    addFieldDefs( ID, NAME, HOST, LOGIN, PASSWORD, DESCRIPTION );
    // setLifecycleManagerCreator( new M5AbstractLifecycleManagerCreator<IOpcServerConnCfg, IOpcServerConnCfgManager>()
    // {
    //
    // @Override
    // protected IM5LifecycleManager<IOpcServerConnCfg> doCreateDefault() {
    // return null;
    // }
    //
    // @Override
    // protected IM5LifecycleManager<IOpcServerConnCfg> doCreate( IOpcServerConnCfgManager aMaster ) {
    // return new OpcServerConnCfgM5LifecycleManager( model(), aMaster );
    // }
    // } );
  }

}
