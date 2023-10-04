package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.txtproj.lib.storage.*;
import org.toxsoft.core.txtproj.lib.workroom.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;

public class OpcToS5DataCfgDocService {

  private static final String SECTID_OPC_CFG_DOCS = "opc.bridge.cfg.doc";

  // private IStridablesListEdit<OpcToS5DataCfgDoc> initial = new StridablesList<>();

  private ITsGuiContext context;

  public OpcToS5DataCfgDocService( ITsGuiContext aContext ) {
    super();
    context = aContext;
  }

  private IStridablesListEdit<OpcToS5DataCfgDoc> workCopy = new StridablesList<>();

  public IList<OpcToS5DataCfgDoc> getCfgDocs() {
    if( workCopy.size() == 0 ) {
      ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
      TsInternalErrorRtException.checkNull( workroom );
      IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
      try {
        workCopy = new StridablesList<>( storage.readColl( SECTID_OPC_CFG_DOCS, OpcToS5DataCfgDoc.KEEPER ) );
      }
      catch( Exception e ) {
        e.printStackTrace();
      }
    }
    return workCopy;
  }

  public void saveCfgDoc( OpcToS5DataCfgDoc aDoc ) {
    ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IStridablesListEdit<OpcToS5DataCfgDoc> stored = new StridablesList<>();
    try {
      stored = new StridablesList<>( storage.readColl( SECTID_OPC_CFG_DOCS, OpcToS5DataCfgDoc.KEEPER ) );
    }
    catch( Exception e ) {
      e.printStackTrace();
    }
    if( stored.hasKey( aDoc.id() ) ) {
      stored.replace( aDoc.id(), aDoc );
    }
    else {
      stored.add( aDoc );
    }

    storage.writeColl( SECTID_OPC_CFG_DOCS, stored, OpcToS5DataCfgDoc.KEEPER );

    if( workCopy.hasKey( aDoc.id() ) ) {
      workCopy.replace( aDoc.id(), aDoc );
    }
    else {
      workCopy.add( aDoc );
    }
  }

  public void removeCfgDoc( OpcToS5DataCfgDoc aDoc ) {
    ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IStridablesListEdit<OpcToS5DataCfgDoc> stored =
        new StridablesList<>( storage.readColl( SECTID_OPC_CFG_DOCS, OpcToS5DataCfgDoc.KEEPER ) );
    if( stored.hasKey( aDoc.id() ) ) {
      stored.removeById( aDoc.id() );
    }

    storage.writeColl( SECTID_OPC_CFG_DOCS, stored, OpcToS5DataCfgDoc.KEEPER );

    if( workCopy.hasKey( aDoc.id() ) ) {
      workCopy.removeById( aDoc.id() );
    }
  }
}
