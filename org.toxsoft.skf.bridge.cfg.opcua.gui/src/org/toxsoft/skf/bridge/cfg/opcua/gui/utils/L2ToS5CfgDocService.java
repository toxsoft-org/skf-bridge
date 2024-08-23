package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.core.log4j.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.*;
import org.toxsoft.core.txtproj.lib.storage.*;
import org.toxsoft.core.txtproj.lib.workroom.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;

/**
 * Service base class for SkIDE bridge editors
 *
 * @author max
 * @param <T>
 */
public class L2ToS5CfgDocService<T extends IStridable> {

  /**
   * Журнал работы
   */
  private static ILogger logger = LoggerWrapper.getLogger( L2ToS5CfgDocService.class.getName() );

  private String sectIdCfgDocs;

  private IEntityKeeper<T> entityKeeper;

  protected ITsGuiContext context;

  private IStridablesListEdit<T> workCopy = new StridablesList<>();

  /**
   * Constructor
   *
   * @param aContext - app context {@link ITsGuiContext}
   * @param aSectIdCfgDocs - section id
   * @param aEntityKeeper - entity keeper
   */
  public L2ToS5CfgDocService( ITsGuiContext aContext, String aSectIdCfgDocs, IEntityKeeper<T> aEntityKeeper ) {
    context = aContext;
    sectIdCfgDocs = aSectIdCfgDocs;
    entityKeeper = aEntityKeeper;
  }

  public IList<T> getCfgDocs() {
    if( workCopy.size() == 0 ) {
      ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
      TsInternalErrorRtException.checkNull( workroom );
      IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
      try {
        workCopy = new StridablesList<>( storage.readColl( sectIdCfgDocs, entityKeeper ) );
      }
      catch( Exception e ) {
        logger.error( e );
      }
    }
    return workCopy;
  }

  /**
   * Сохранить конфигурацию в файл внутреннего хранилища плагина SkIDE
   *
   * @param aDoc - конфигурация
   */
  public void saveCfgDoc( T aDoc ) {
    ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IStridablesListEdit<T> stored = new StridablesList<>();
    try {
      stored = new StridablesList<>( storage.readColl( sectIdCfgDocs, entityKeeper ) );
    }
    catch( Exception e ) {
      logger.error( e );
    }
    if( stored.hasKey( aDoc.id() ) ) {
      stored.replace( aDoc.id(), aDoc );
    }
    else {
      stored.add( aDoc );
    }

    storage.writeColl( sectIdCfgDocs, stored, entityKeeper );

    if( workCopy.hasKey( aDoc.id() ) ) {
      workCopy.replace( aDoc.id(), aDoc );
    }
    else {
      workCopy.add( aDoc );
    }
  }

  public void removeCfgDoc( T aDoc ) {
    ITsWorkroom workroom = context.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IStridablesListEdit<T> stored = new StridablesList<>( storage.readColl( sectIdCfgDocs, entityKeeper ) );
    if( stored.hasKey( aDoc.id() ) ) {
      stored.removeById( aDoc.id() );
    }

    storage.writeColl( sectIdCfgDocs, stored, entityKeeper );

    if( workCopy.hasKey( aDoc.id() ) ) {
      workCopy.removeById( aDoc.id() );
    }
  }
}
