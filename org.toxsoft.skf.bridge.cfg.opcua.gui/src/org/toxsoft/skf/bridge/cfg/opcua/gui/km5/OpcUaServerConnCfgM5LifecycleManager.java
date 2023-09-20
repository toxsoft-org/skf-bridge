package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.service.*;

/**
 * Lifecycle Manager of {@link IOpcUaServerConnCfg} entities.
 *
 * @author max
 */
public class OpcUaServerConnCfgM5LifecycleManager
    extends M5LifecycleManager<IOpcUaServerConnCfg, IOpcUaServerConnCfgService> {

  /**
   * Constructor by M5 model and service
   *
   * @param aModel IM5Model - model
   * @param aService IOpcUaServerConnCfgService - service
   */
  public OpcUaServerConnCfgM5LifecycleManager( IM5Model<IOpcUaServerConnCfg> aModel,
      IOpcUaServerConnCfgService aService ) {
    super( aModel, true, true, true, true, aService );
    TsNullArgumentRtException.checkNull( aService );
  }

  private IOptionSet makeCfgParams( IM5Bunch<IOpcUaServerConnCfg> aValues ) {
    IOptionSetEdit params = new OptionSet();
    for( IM5FieldDef<IOpcUaServerConnCfg, ?> fdef : model().fieldDefs() ) {
      IAtomicValue av = aValues.getAsAv( fdef.id() );
      params.setValue( fdef.id(), av );
    }
    return params;
  }

  @Override
  protected IOpcUaServerConnCfg doCreate( IM5Bunch<IOpcUaServerConnCfg> aValues ) {
    IOptionSet p = makeCfgParams( aValues );
    return master().createCfg( p );
  }

  @Override
  protected IOpcUaServerConnCfg doEdit( IM5Bunch<IOpcUaServerConnCfg> aValues ) {
    IOptionSet p = makeCfgParams( aValues );
    return master().editCfg( aValues.originalEntity().id(), p );
  }

  @Override
  protected void doRemove( IOpcUaServerConnCfg aEntity ) {
    master().removeCfg( aEntity.id() );
  }

  @Override
  protected IList<IOpcUaServerConnCfg> doListEntities() {
    return master().configs();
  }

}
