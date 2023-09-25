package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.eclipse.milo.opcua.sdk.core.nodes.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.gui.panels.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * M5 model realization for {@link Gwid} entities using for cfg of map uanodes-gwids
 *
 * @author max
 */
public class GwidsForCfgM5Model
    extends M5Model<Gwid> {

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.opcua.m5.GwidsForCfgM5Model"; //$NON-NLS-1$

  final static String ACTID_ADD_AS_STR = SK_ID + ".opcua.to.s5.add.gwid.as.str"; //$NON-NLS-1$

  final static String ACTID_EDIT_AS_STR = SK_ID + ".opcua.to.s5.edit.gwid.as.str"; //$NON-NLS-1$

  final static TsActionDef ACDEF_ADD_AS_STR =
      TsActionDef.ofPush2( ACTID_ADD_AS_STR, "Добавить как строку", "Добавить как строку", ICONID_LIST_ADD );

  final static TsActionDef ACDEF_EDIT_AS_STR = TsActionDef.ofPush2( ACTID_EDIT_AS_STR, "Редактировать как строку",
      "Редактировать как строку", ICONID_DOCUMENT_EDIT );

  /**
   * string id of cfg unit
   */
  // public static final String FID_STRID = "strid"; //$NON-NLS-1$

  /**
   * display name of cfg unit
   */
  public static final String FID_DISPLAY_NAME = "display.name"; //$NON-NLS-1$

  /**
   * Canonical str gwid
   */
  public static final String FID_GWID_STR = "gwid.str"; //$NON-NLS-1$

  /**
   * Attribute {@link Node#getNodeId() } string id
   */
  public M5AttributeFieldDef<Gwid> DISPLAY_NAME = new M5AttributeFieldDef<>( FID_DISPLAY_NAME, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_DISPLAY_NAME, //
      TSID_DESCRIPTION, STR_D_PARAM_DISPLAY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( Gwid aEntity ) {
      // aContext.put( OPCUA_BRIDGE_CFG_S5_CONNECTION, selectedConnection );
      return avStr( aEntity.asString() );
    }

  };

  /**
   * Invisible attribute for sending selected entity into lifecyclemanager string id
   */
  public M5AttributeFieldDef<Gwid> GWID_STR = new M5AttributeFieldDef<>( FID_GWID_STR, EAtomicType.STRING, //
      TSID_NAME, FID_GWID_STR, //
      TSID_DESCRIPTION, FID_GWID_STR //
  ) {

    @Override
    protected void doInit() {
      // setFlags( M5FF_COLUMN );
      // setFlags( M5FF_COLUMN | M5FF_READ_ONLY | M5FF_HIDDEN );
      // setFlags( M5FF_COLUMN | M5FF_HIDDEN );
    }

    protected IAtomicValue doGetFieldValue( Gwid aEntity ) {
      return avStr( aEntity.asString() );
    }

  };

  /**
   * Constructor
   */
  public GwidsForCfgM5Model() {
    super( MODEL_ID, Gwid.class );
    addFieldDefs( DISPLAY_NAME );
    addFieldDefs( GWID_STR );

    setPanelCreator( new M5DefaultPanelCreator<>() {

      protected IM5CollectionPanel<Gwid> doCreateCollEditPanel( ITsGuiContext aContext,
          IM5ItemsProvider<Gwid> aItemsProvider, IM5LifecycleManager<Gwid> aLifecycleManager ) {
        MultiPaneComponentModown<Gwid> mpc =
            new MultiPaneComponentModown<>( aContext, model(), aItemsProvider, aLifecycleManager ) {

              @Override
              protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
                  IListEdit<ITsActionDef> aActs ) {
                aActs.add( ACDEF_SEPARATOR );
                aActs.add( ACDEF_ADD_AS_STR );

                aActs.add( ACDEF_SEPARATOR );
                aActs.add( ACDEF_EDIT_AS_STR );

                ITsToolbar toolbar =

                    super.doCreateToolbar( aContext, aName, aIconSize, aActs );

                toolbar.addListener( aActionId -> {
                  // nop

                } );

                return toolbar;
              }

              @Override
              protected void doProcessAction( String aActionId ) {

                switch( aActionId ) {

                  case ACTID_ADD_AS_STR:
                    // Gwid gwid =
                    super.doAddItem();

                    break;

                  case ACTID_EDIT_AS_STR:
                    // Gwid gwid =
                    super.doEditItem( selectedItem() );

                    break;

                  default:
                    throw new TsNotAllEnumsUsedRtException( aActionId );
                }
              }

              @Override
              protected Gwid doAddItem() {

                ISkConnection conn =
                    (ISkConnection)tsContext().find( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_S5_CONNECTION );
                if( conn != null ) {
                  System.out.println( "Selecetd conn: " + conn.toString() );
                }
                else {
                  System.out.println( "Selecetd conn: " + "null" );
                }

                Gwid gwid = PanelGwidSelector.selectGwid( null, tsContext() );

                if( gwid == null ) {
                  return null;
                }

                IM5BunchEdit<Gwid> bunch = new M5BunchEdit<>( model() );
                bunch.set( FID_GWID_STR, avStr( gwid.asString() ) );
                aLifecycleManager.create( bunch );
                return gwid;
              }

              @Override
              protected Gwid doEditItem( Gwid aItem ) {

                ISkConnection conn =
                    (ISkConnection)tsContext().find( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_S5_CONNECTION );
                if( conn != null ) {
                  System.out.println( "Selecetd conn: " + conn.toString() );
                }
                else {
                  System.out.println( "Selecetd conn: " + "null" );
                }

                Gwid gwid = PanelGwidSelector.selectGwid( aItem, tsContext() );
                if( gwid == null ) {
                  return aItem;
                }
                IM5BunchEdit<Gwid> bunch = new M5BunchEdit<>( model() );
                bunch.fillFrom( aItem, true );
                bunch.set( FID_GWID_STR, avStr( gwid.asString() ) );
                aLifecycleManager.edit( bunch );
                return gwid;
              }

              @Override
              protected boolean doRemoveItem( Gwid aItem ) {
                return super.doRemoveItem( aItem );
              }

            };
        return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
      }
    } );
  }

  @Override
  protected IM5LifecycleManager<Gwid> doCreateDefaultLifecycleManager() {
    ISkConnectionSupplier cs = tsContext().get( ISkConnectionSupplier.class );
    // TODO which connection to use?
    ISkConnection conn = cs.defConn();
    return new GwidsForCfgM5LifecycleManager( this, conn );
  }

  @Override
  protected IM5LifecycleManager<Gwid> doCreateLifecycleManager( Object aMaster ) {
    return new GwidsForCfgM5LifecycleManager( this, ISkConnection.class.cast( aMaster ) );
  }
}