package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.eclipse.milo.opcua.sdk.core.nodes.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.bricks.strid.more.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.ugwi.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.reports.gui.panels.*;
import org.toxsoft.skf.rri.lib.ugwi.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.valed.ugwi.*;

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
      TsActionDef.ofPush2( ACTID_ADD_AS_STR, STR_N_ADD_AS_STRING, STR_D_ADD_AS_STRING, ICONID_LIST_ADD );

  final static TsActionDef ACDEF_EDIT_AS_STR =
      TsActionDef.ofPush2( ACTID_EDIT_AS_STR, STR_N_EDIT_AS_STRING, STR_N_EDIT_AS_STRING, ICONID_DOCUMENT_EDIT );

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
  public final M5AttributeFieldDef<Gwid> DISPLAY_NAME = new M5AttributeFieldDef<>( FID_DISPLAY_NAME, EAtomicType.STRING, //
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
  public final M5AttributeFieldDef<Gwid> GWID_STR = new M5AttributeFieldDef<>( FID_GWID_STR, EAtomicType.STRING, //
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

      protected IM5CollectionPanel<Gwid> doCreateCollViewerPanel( ITsGuiContext aContext,
          IM5ItemsProvider<Gwid> aItemsProvider ) {
        OPDEF_IS_ACTIONS_CRUD.setValue( aContext.params(), AV_FALSE );
        IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( aContext.params(), AvUtils.AV_FALSE );
        MultiPaneComponentModown<Gwid> mpc = new MultiPaneComponentModown<>( aContext, model(), aItemsProvider );
        return new M5CollectionPanelMpcModownWrapper<>( mpc, true );
      }

      protected IM5CollectionPanel<Gwid> doCreateCollEditPanel( ITsGuiContext aContext,
          IM5ItemsProvider<Gwid> aItemsProvider, IM5LifecycleManager<Gwid> aLifecycleManager ) {
        IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( aContext.params(), AvUtils.AV_FALSE );
        MultiPaneComponentModown<Gwid> mpc =
            new MultiPaneComponentModown<>( aContext, model(), aItemsProvider, aLifecycleManager ) {

              @Override
              protected ITsToolbar doCreateToolbar( @SuppressWarnings( "hiding" ) ITsGuiContext aContext, String aName,
                  EIconSize aIconSize, IListEdit<ITsActionDef> aActs ) {
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

                ECfgUnitType type = ((GwidsForCfgM5Model)model()).getCfgUnitType();

                Gwid selGwid = selectGwid( type, null );
                if( selGwid == null ) {
                  return null;
                }

                IM5BunchEdit<Gwid> bunch = new M5BunchEdit<>( model() );
                bunch.set( FID_GWID_STR, avStr( selGwid.canonicalString() ) );
                aLifecycleManager.create( bunch );
                return selGwid;
              }

              @Override
              protected Gwid doEditItem( Gwid aItem ) {

                ECfgUnitType type = ((GwidsForCfgM5Model)model()).getCfgUnitType();

                Gwid selGwid = selectGwid( type, null );
                if( selGwid == null ) {
                  return null;
                }
                IM5BunchEdit<Gwid> bunch = new M5BunchEdit<>( model() );
                bunch.fillFrom( aItem, true );
                bunch.set( FID_GWID_STR, avStr( selGwid.canonicalString() ) );
                aLifecycleManager.edit( bunch );
                return selGwid;
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

  private ECfgUnitType unitType = ECfgUnitType.DATA;

  public void setCfgUnitType( ECfgUnitType aUnitType ) {
    unitType = aUnitType;
  }

  public ECfgUnitType getCfgUnitType() {
    return unitType;
  }

  private Gwid selectGwid( ECfgUnitType aType, Gwid aItem ) {
    Gwid retVal = null;
    IdChain connIdChain = (IdChain)tsContext().find( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_S5_CONNECTION );

    switch( aType ) {
      case COMMAND:
        retVal = PanelGwidSelector.selectGwid( aItem, tsContext(), ESkClassPropKind.CMD, connIdChain );
        break;
      case DATA:
        retVal = PanelGwidSelector.selectGwid( aItem, tsContext(), ESkClassPropKind.RTDATA, connIdChain );
        break;
      case EVENT:
        retVal = PanelGwidSelector.selectGwid( aItem, tsContext(), ESkClassPropKind.CMD, connIdChain );
        break;
      case RRI:
        Ugwi currItem = aItem == null ? null : Ugwi.of( UgwiKindRriAttr.KIND_ID, aItem.canonicalString() );
        Ugwi selUgwi = PanelUgwiSelector.selectUgwiSingleKind( tsContext(), currItem, UgwiKindRriAttr.KIND_ID );
        if( selUgwi == null ) {
          return null;
        }
        retVal = Gwid.createAttr( UgwiKindRriAttr.getClassId( selUgwi ), UgwiKindRriAttr.getObjStrid( selUgwi ),
            UgwiKindRriAttr.getAttrId( selUgwi ) );
        break;
      default:
        break;
    }
    return retVal;
  }
}
