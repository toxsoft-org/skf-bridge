package org.toxsoft.skf.bridge.cfg.opcua.service.impl;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.apprefs.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.bricks.strid.idgen.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.service.*;

/**
 * Implementation of service for managing cfgs of opc ua connections
 *
 * @author max
 */
public class OpcUaServerConnCfgService
    implements IOpcUaServerConnCfgService {

  /**
   * Название раздела {@link IAppPreferences}, в вотором хранятся конфигурации.
   */
  public static final String PBID_STORAGE = "OpcUaServerConnCfgService"; //$NON-NLS-1$

  private final IStridablesListEdit<IOpcUaServerConnCfg> configs = new StridablesList<>();

  private final IStridGenerator idGen = new UuidStridGenerator();

  private final IPrefBundle pb;

  /**
   * Коснтруктор.
   *
   * @param aAprefs {@link IAppPreferences} - настройки, в котором хранятся конфигурации в разделе {@link #PBID_STORAGE}
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public OpcUaServerConnCfgService( IAppPreferences aAprefs ) {
    TsNullArgumentRtException.checkNull( aAprefs );
    pb = aAprefs.getBundle( PBID_STORAGE );
    // load configs
    for( String id : pb.prefs().keys() ) {
      IOptionSet params = pb.prefs().getValobj( id );
      OpcUaServerConnCfg cfg = new OpcUaServerConnCfg( id, params );
      configs.add( cfg );
    }
  }

  // ------------------------------------------------------------------------------------
  // IOpcServerConnCfgManager
  //

  @Override
  public IStridablesList<IOpcUaServerConnCfg> configs() {
    return configs;
  }

  @Override
  public IOpcUaServerConnCfg createCfg( IOptionSet aParams ) {
    TsNullArgumentRtException.checkNull( aParams );
    String id = idGen.nextId();
    IOpcUaServerConnCfg cfg = new OpcUaServerConnCfg( id, aParams );
    configs.add( cfg );
    pb.prefs().setValobj( id, cfg.params() );
    return cfg;
  }

  @Override
  public IOpcUaServerConnCfg editCfg( String aId, IOptionSet aParams ) {
    TsNullArgumentRtException.checkNulls( aId, aParams );
    TsItemNotFoundRtException.checkFalse( configs.hasKey( aId ) );
    IOpcUaServerConnCfg cfg = new OpcUaServerConnCfg( aId, aParams );
    configs.put( cfg );
    pb.prefs().setValobj( aId, cfg.params() );
    return cfg;
  }

  @Override
  public void removeCfg( String aId ) {
    if( configs.removeById( aId ) != null ) {
      pb.prefs().remove( aId );
    }
  }

}
