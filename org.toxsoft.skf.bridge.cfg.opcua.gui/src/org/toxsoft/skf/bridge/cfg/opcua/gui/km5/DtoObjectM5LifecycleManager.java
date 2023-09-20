package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Lifecylce manager for {@link DtoObjectM5Model}.
 *
 * @author dima
 */
public class DtoObjectM5LifecycleManager
    extends M5LifecycleManager<IDtoObject, ISkConnection> {

  /**
   * Constructor.
   *
   * @param aModel {@link IM5Model}&lt;T&gt; - the model
   * @param aMaster &lt;M&gt; - master object, may be <code>null</code>
   * @throws TsNullArgumentRtException model is <code>null</code>
   */
  public DtoObjectM5LifecycleManager( IM5Model<IDtoObject> aModel, ISkConnection aMaster ) {
    super( aModel, true, true, true, true, aMaster );
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  private IDtoObject makeDtoObject( IM5Bunch<IDtoObject> aValues ) {
    // Создаем IDpuObject и инициализируем его значениями из пучка
    String classId = aValues.getAsAv( DtoObjectM5Model.FID_CLASSID ).asString();
    String id = aValues.getAsAv( DtoObjectM5Model.FID_STRID ).asString();
    Skid skid = new Skid( classId, id );
    DtoObject dtoObject = DtoObject.createDtoObject( skid, master().coreApi() );
    dtoObject.attrs().setValue( DtoObjectM5Model.FID_NAME, aValues.getAsAv( DtoObjectM5Model.FID_NAME ) );
    dtoObject.attrs().setValue( DtoObjectM5Model.FID_DESCRIPTION, aValues.getAsAv( DtoObjectM5Model.FID_DESCRIPTION ) );
    return dtoObject;
  }

  // ------------------------------------------------------------------------------------
  // M5LifecycleManager
  //

  @Override
  protected ValidationResult doBeforeCreate( IM5Bunch<IDtoObject> aValues ) {
    IDtoObject dtoObject = makeDtoObject( aValues );
    return master().coreApi().objService().svs().validator().canCreateObject( dtoObject );
  }

  @Override
  protected IDtoObject doCreate( IM5Bunch<IDtoObject> aValues ) {
    IDtoObject dtoObject = makeDtoObject( aValues );
    ISkObject created = master().coreApi().objService().defineObject( dtoObject );
    return DtoFullObject.createDtoFullObject( created.skid(), master().coreApi() );
  }

  // @Override
  // protected ValidationResult doBeforeEdit( IM5Bunch<IDtoObject> aValues ) {
  // IDtoObject dtoObject = makeDtoObject( aValues );
  // return master().coreApi().objService().svs().validator().canEditObject( dtoObject, aValues.originalEntity() );
  // }

  @Override
  protected IDtoObject doEdit( IM5Bunch<IDtoObject> aValues ) {
    IDtoObject dtoObject = makeDtoObject( aValues );
    ISkObject edited = master().coreApi().objService().defineObject( dtoObject );
    return DtoFullObject.createDtoFullObject( edited.skid(), master().coreApi() );
  }

  @Override
  protected ValidationResult doBeforeRemove( IDtoObject aEntity ) {
    return master().coreApi().objService().svs().validator().canRemoveObject( aEntity.skid() );
  }

  @Override
  protected void doRemove( IDtoObject aEntity ) {
    master().coreApi().objService().removeObject( aEntity.skid() );
  }

}
