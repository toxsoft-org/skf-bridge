package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;

import java.util.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tsgui.valed.controls.basic.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;

/**
 * Модель свойств, представленных в виде списка строк
 *
 * @author max
 */
public class StringPropertiesM5Model
    extends M5Model<IStringList> {

  /**
   * The model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.opcua.StringPropertiesM5Model"; //$NON-NLS-1$

  /**
   * ID of field {@link #STR}.
   */
  public static final String FID_PROP_NAME = "prop.name"; //$NON-NLS-1$

  /**
   * ID of field {@link #STR}.
   */
  public static final String FID_PROP_VALUE = "prop.value"; //$NON-NLS-1$

  /**
   * ID of field {@link #STR}.
   */
  public static final String FID_PROP_PATH = "prop.path"; //$NON-NLS-1$

  /**
   * Field {@link String#toString()}.
   */
  public final M5AttributeFieldDef<IStringList> PROP_NAME = new M5AttributeFieldDef<>( FID_PROP_NAME, DDEF_STRING ) {

    @Override
    protected void doInit() {
      setNameAndDescription( "Наименование", "Наименование" );
      setDefaultValue( AV_STR_EMPTY );
      setValedEditor( ValedAvStringText.FACTORY_NAME );
      ValedStringText.OPDEF_IS_MULTI_LINE.setValue( params(), AV_TRUE );
      setFlags( M5FF_COLUMN ); // | M5FF_DETAIL );
    }

    @Override
    protected IAtomicValue doGetFieldValue( IStringList aEntity ) {
      return avStr( aEntity.first() );
    }
  };

  /**
   * Field {@link String#toString()}.
   */
  public final M5AttributeFieldDef<IStringList> PROP_VALUE = new M5AttributeFieldDef<>( FID_PROP_VALUE, DDEF_STRING ) {

    @Override
    protected void doInit() {
      setNameAndDescription( "Значение", "Значение" );
      setDefaultValue( AV_STR_EMPTY );
      setValedEditor( ValedAvStringText.FACTORY_NAME );
      ValedStringText.OPDEF_IS_MULTI_LINE.setValue( params(), AV_TRUE );
      setFlags( M5FF_COLUMN ); // | M5FF_DETAIL );
    }

    @Override
    protected IAtomicValue doGetFieldValue( IStringList aEntity ) {
      Iterator<String> iter = aEntity.iterator();
      iter.next();
      return avStr( iter.next() );
    }
  };

  /**
   * Field {@link String#toString()}.
   */
  public final M5AttributeFieldDef<IStringList> PROP_PATH = new M5AttributeFieldDef<>( FID_PROP_PATH, DDEF_STRING ) {

    @Override
    protected void doInit() {
      setNameAndDescription( "Путь", "Путь" );
      setDefaultValue( AV_STR_EMPTY );
      setValedEditor( ValedAvStringText.FACTORY_NAME );
      ValedStringText.OPDEF_IS_MULTI_LINE.setValue( params(), AV_TRUE );
      setFlags( M5FF_COLUMN ); // | M5FF_DETAIL );
    }

    @Override
    protected IAtomicValue doGetFieldValue( IStringList aEntity ) {
      return avStr( aEntity.last() );
    }
  };

  /**
   * LM for this model.
   *
   * @author hazard157
   */
  private static class DefaultLifecyleManager
      extends M5LifecycleManager<IStringList, Object> {

    public DefaultLifecyleManager( IM5Model<IStringList> aModel ) {
      super( aModel, true, true, true, false, null );
    }

    @Override
    protected IStringList doCreate( IM5Bunch<IStringList> aValues ) {
      IStringListEdit result = new StringArrayList();
      result.add( aValues.getAsAv( FID_PROP_NAME ).asString() );
      result.add( aValues.getAsAv( FID_PROP_VALUE ).asString() );
      result.add( aValues.getAsAv( FID_PROP_PATH ).asString() );
      return result;
    }

    @Override
    protected IStringList doEdit( IM5Bunch<IStringList> aValues ) {
      return doCreate( aValues );
    }

    @Override
    protected void doRemove( IStringList aEntity ) {
      // nop
    }

  }

  /**
   * Constructor for builtin model.
   */
  public StringPropertiesM5Model() {
    super( MODEL_ID, IStringList.class );
    setNameAndDescription( "Свойства", "Свойства документа" );
    addFieldDefs( PROP_NAME, PROP_VALUE, PROP_PATH );
    setPanelCreator( new M5DefaultPanelCreator<>() {

      @Override
      protected IM5CollectionPanel<IStringList> doCreateCollViewerPanel( ITsGuiContext aContext,
          IM5ItemsProvider<IStringList> aItemsProvider ) {
        // OPDEF_IS_ACTIONS_CRUD.setValue( aContext.params(), AV_FALSE );
        OPDEF_IS_DETAILS_PANE.setValue( aContext.params(), AV_TRUE );
        OPDEF_IS_DETAILS_PANE_HIDDEN.setValue( aContext.params(), AV_FALSE );
        // OPDEF_IS_TOOLBAR.setValue( aContext.params(), AV_FALSE );
        MultiPaneComponentModown<IStringList> mpc = new MultiPaneComponentModown<>( aContext, model(), aItemsProvider );
        return new M5CollectionPanelMpcModownWrapper<>( mpc, true );
      }
    } );
  }

  @Override
  protected IM5LifecycleManager<IStringList> doCreateDefaultLifecycleManager() {
    return new DefaultLifecyleManager( this );
  }

  @Override
  protected IM5LifecycleManager<IStringList> doCreateLifecycleManager( Object aMaster ) {
    /**
     * If not overridden, use default LM for String.
     */
    return new DefaultLifecyleManager( this );
  }

}
