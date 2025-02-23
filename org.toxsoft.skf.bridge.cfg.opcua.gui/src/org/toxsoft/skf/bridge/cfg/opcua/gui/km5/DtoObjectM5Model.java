package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;

import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.connection.*;

/**
 * M5 model realization for {@link IDtoObject} entities.
 *
 * @author dima
 */
public class DtoObjectM5Model
    extends M5Model<IDtoObject> {

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.opcua.m5.DtoObject"; //$NON-NLS-1$

  /**
   * Class id of object
   */
  public static final String FID_CLASSID = "classId"; //$NON-NLS-1$

  /**
   * string id of object
   */
  public static final String FID_STRID = "strid"; //$NON-NLS-1$

  /**
   * display name of object
   */
  public static final String FID_NAME = "ts.Name"; //$NON-NLS-1$

  /**
   * description of object
   */
  public static final String FID_DESCRIPTION = "ts.Description"; //$NON-NLS-1$

  /**
   * Field {@link IDtoObject#classId() } class id
   */
  public M5AttributeFieldDef<IDtoObject> CLASS_ID = new M5AttributeFieldDef<>( FID_CLASSID, EAtomicType.STRING, //
      TSID_NAME, STR_N_CLASS_ID, //
      TSID_DESCRIPTION, STR_D_CLASS_ID, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IDtoObject aEntity ) {
      return avStr( aEntity.classId() );
    }

  };

  /**
   * Field {@link IDtoObject#strid() } string id
   */
  public M5AttributeFieldDef<IDtoObject> STRID = new M5AttributeFieldDef<>( FID_STRID, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_STRID, //
      TSID_DESCRIPTION, STR_D_PARAM_STRID, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IDtoObject aEntity ) {
      return avStr( aEntity.strid() );
    }

  };

  /**
   * Field {@link IDtoObject#nmName() } name
   */
  public M5AttributeFieldDef<IDtoObject> NAME = new M5AttributeFieldDef<>( FID_NAME, EAtomicType.STRING, //
      TSID_NAME, STR_N_NAME, //
      TSID_DESCRIPTION, STR_D_NAME, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IDtoObject aEntity ) {
      return avStr( aEntity.nmName() );
    }

  };

  /**
   * Field {@link IDtoObject#description() } description
   */
  public M5AttributeFieldDef<IDtoObject> DESCRIPTION = new M5AttributeFieldDef<>( FID_DESCRIPTION, EAtomicType.STRING, //
      TSID_NAME, STR_N_DESCR, //
      TSID_DESCRIPTION, STR_D_DESCR, //
      OPID_EDITOR_FACTORY_NAME, //
      ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IDtoObject aEntity ) {
      return avStr( aEntity.description() );
    }

  };

  /**
   * Constructor.
   *
   * @param aConn Sk connection
   */
  public DtoObjectM5Model( ISkConnection aConn ) {
    super( MODEL_ID, IDtoObject.class );

    addFieldDefs( CLASS_ID, STRID, NAME, DESCRIPTION );
  }

  @Override
  protected IM5LifecycleManager<IDtoObject> doCreateDefaultLifecycleManager() {
    ISkConnection master = domain().tsContext().get( ISkConnection.class );
    return new DtoObjectM5LifecycleManager( this, master );
  }

  @Override
  protected IM5LifecycleManager<IDtoObject> doCreateLifecycleManager( Object aMaster ) {
    ISkConnection master = ISkConnection.class.cast( aMaster );
    return new DtoObjectM5LifecycleManager( this, master );
  }

}
