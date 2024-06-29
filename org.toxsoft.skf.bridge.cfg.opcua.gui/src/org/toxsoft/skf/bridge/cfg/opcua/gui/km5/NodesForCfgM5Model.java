package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.api.*;
import org.eclipse.milo.opcua.stack.core.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
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
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.helpers.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * M5 model realization for {@link NodeId} entities using for cfg of map uanodes-gwids
 *
 * @author max
 */
public class NodesForCfgM5Model
    extends M5Model<IAtomicValue> {

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.opcua.m5.NodesForCfgM5Model"; //$NON-NLS-1$

  final static String ACTID_ADD_AS_STR = SK_ID + ".opcua.to.s5.add.nodeid.as.str"; //$NON-NLS-1$

  final static String ACTID_EDIT_AS_STR = SK_ID + ".opcua.to.s5.edit.nodeid.as.str"; //$NON-NLS-1$

  final static TsActionDef ACDEF_ADD_AS_STR =
      TsActionDef.ofPush2( ACTID_ADD_AS_STR, STR_N_ADD_AS_STRING, STR_D_ADD_AS_STRING, ICONID_LIST_ADD );

  final static TsActionDef ACDEF_EDIT_AS_STR =
      TsActionDef.ofPush2( ACTID_EDIT_AS_STR, STR_N_EDIT_AS_STRING, STR_D_EDIT_AS_STRING, ICONID_DOCUMENT_EDIT );

  /**
   * Canonical str node
   */
  public static final String FID_NODE_STR = "node.str"; //$NON-NLS-1$

  public final M5AttributeFieldDef<IAtomicValue> NODE_STR = new M5AttributeFieldDef<>( FID_NODE_STR, EAtomicType.STRING, //
      TSID_NAME, STR_N_NODE_STRING, //
      TSID_DESCRIPTION, STR_D_NODE_STRING //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
      // setFlags( M5FF_COLUMN | M5FF_READ_ONLY | M5FF_HIDDEN );
      // setFlags( M5FF_COLUMN | M5FF_HIDDEN );
    }

    protected IAtomicValue doGetFieldValue( IAtomicValue aEntity ) {
      return avStr( ((NodeId)aEntity.asValobj()).toParseableString() );
    }

  };

  /**
   * Constructor
   */
  public NodesForCfgM5Model() {
    super( MODEL_ID, IAtomicValue.class );
    addFieldDefs( NODE_STR );

    setPanelCreator( new M5DefaultPanelCreator<>() {

      protected IM5CollectionPanel<IAtomicValue> doCreateCollViewerPanel( ITsGuiContext aContext,
          IM5ItemsProvider<IAtomicValue> aItemsProvider ) {
        OPDEF_IS_ACTIONS_CRUD.setValue( aContext.params(), AV_FALSE );
        IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( aContext.params(), AvUtils.AV_FALSE );
        MultiPaneComponentModown<IAtomicValue> mpc =
            new MultiPaneComponentModown<>( aContext, model(), aItemsProvider );
        return new M5CollectionPanelMpcModownWrapper<>( mpc, true );
      }

      protected IM5CollectionPanel<IAtomicValue> doCreateCollEditPanel( ITsGuiContext aContext,
          IM5ItemsProvider<IAtomicValue> aItemsProvider, IM5LifecycleManager<IAtomicValue> aLifecycleManager ) {
        IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( aContext.params(), AvUtils.AV_FALSE );
        MultiPaneComponentModown<IAtomicValue> mpc =
            new MultiPaneComponentModown<>( aContext, model(), aItemsProvider, aLifecycleManager ) {

              @Override
              protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
                  IListEdit<ITsActionDef> aActs ) {
                aActs.add( ACDEF_SEPARATOR );
                aActs.add( ACDEF_ADD_AS_STR );

                aActs.add( ACDEF_SEPARATOR );
                aActs.add( ACDEF_EDIT_AS_STR );

                ITsToolbar toolbar = super.doCreateToolbar( aContext, aName, aIconSize, aActs );

                toolbar.addListener( aActionId -> {
                  // nop

                } );

                return toolbar;
              }

              @Override
              protected boolean doGetIsAddAllowed( IAtomicValue aSel ) {
                OpcUaServerConnCfg config =
                    (OpcUaServerConnCfg)tsContext().find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );

                return config != null;
              }

              @Override
              protected boolean doGetIsEditAllowed( IAtomicValue aSel ) {
                OpcUaServerConnCfg config =
                    (OpcUaServerConnCfg)tsContext().find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );

                return config != null;
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
              protected IAtomicValue doAddItem() {

                UaClient uaClient =
                    (UaClient)tsContext().find( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_OPC_CONNECTION );
                OpcUaServerConnCfg config =
                    (OpcUaServerConnCfg)tsContext().find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );
                if( uaClient != null ) {
                  System.out.println( "Selected opc conn: " + uaClient.toString() );
                }
                else {
                  System.out.println( "Selected opc conn: " + "null" );
                }

                IList<UaTreeNode> selNodes =
                    OpcUaNodesSelector.selectUaNode( aContext, (OpcUaClient)uaClient, Identifiers.RootFolder, config );

                if( selNodes == null || selNodes.size() == 0 ) {
                  return null;
                }

                IM5BunchEdit<IAtomicValue> bunch = new M5BunchEdit<>( model() );
                bunch.set( FID_NODE_STR, avStr( selNodes.first().getNodeId() ) );
                aLifecycleManager.create( bunch );
                return AvUtils.avValobj( NodeId.parse( selNodes.first().getNodeId() ) );
              }

              @Override
              protected IAtomicValue doEditItem( IAtomicValue aItem ) {

                UaClient uaClient =
                    (UaClient)tsContext().find( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_OPC_CONNECTION );
                OpcUaServerConnCfg config =
                    (OpcUaServerConnCfg)tsContext().find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );

                if( uaClient != null ) {
                  System.out.println( "Selected opc conn: " + uaClient.toString() );
                }
                else {
                  System.out.println( "Selected opc conn: " + "null" );
                }
                NodeId nodeId = aItem.asValobj();
                IList<UaTreeNode> selNodes =
                    OpcUaNodesSelector.selectUaNode( aContext, (OpcUaClient)uaClient, nodeId, config );

                if( selNodes == null || selNodes.size() == 0 ) {
                  return aItem;
                }

                IM5BunchEdit<IAtomicValue> bunch = new M5BunchEdit<>( model() );
                bunch.fillFrom( aItem, true );
                bunch.set( FID_NODE_STR, avStr( selNodes.first().getNodeId() ) );
                aLifecycleManager.edit( bunch );
                return AvUtils.avValobj( NodeId.parse( selNodes.first().getNodeId() ) );
              }

              @Override
              protected boolean doRemoveItem( IAtomicValue aItem ) {
                return super.doRemoveItem( aItem );
              }

            };
        return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
      }
    } );

  }

  @Override
  protected IM5LifecycleManager<IAtomicValue> doCreateDefaultLifecycleManager() {
    ISkConnectionSupplier cs = tsContext().get( ISkConnectionSupplier.class );
    // TODO which connection to use?
    ISkConnection conn = cs.defConn();
    return new NodesForCfgM5LifecycleManager( this, conn );
  }

  @Override
  protected IM5LifecycleManager<IAtomicValue> doCreateLifecycleManager( Object aMaster ) {
    return new NodesForCfgM5LifecycleManager( this, ISkConnection.class.cast( aMaster ) );
  }

  static class NodesForCfgM5LifecycleManager
      extends M5LifecycleManager<IAtomicValue, ISkConnection> {

    /**
     * Constructor by m5 model and sk-connection as master-object.
     *
     * @param aModel IM5Model - model
     * @param aMaster ISkConnection - sk-connection
     */
    public NodesForCfgM5LifecycleManager( IM5Model<IAtomicValue> aModel, ISkConnection aMaster ) {
      super( aModel, true, true, true, false, aMaster );
    }

    /**
     * Subclass may perform validation before instance creation.
     * <p>
     * In base class returns {@link ValidationResult#SUCCESS}, there is no need to call superclass method when
     * overriding.
     *
     * @param aValues {@link IM5Bunch} - field values, never is <code>null</code>
     * @return {@link ValidationResult} - the validation result
     */
    @Override
    protected ValidationResult doBeforeCreate( IM5Bunch<IAtomicValue> aValues ) {
      return ValidationResult.SUCCESS;
    }

    /**
     * If creation is supported subclass must create the entity instance.
     * <p>
     * In base class throws an exception, never call superclass method when overriding.
     *
     * @param aValues {@link IM5Bunch} - field values, never is <code>null</code>
     * @return &lt;IVtReportParam&gt; - created instance
     */
    @Override
    protected IAtomicValue doCreate( IM5Bunch<IAtomicValue> aValues ) {
      IAtomicValue nodeStr = aValues.get( NodesForCfgM5Model.FID_NODE_STR );
      return AvUtils.avValobj( NodeId.parse( nodeStr.asString() ) );
    }

    /**
     * Subclass may perform validation before existing editing.
     * <p>
     * In base class returns {@link ValidationResult#SUCCESS}, there is no need to call superclass method when
     * overriding.
     *
     * @param aValues {@link IM5Bunch} - field values, never is <code>null</code>
     * @return {@link ValidationResult} - the validation result
     */
    @Override
    protected ValidationResult doBeforeEdit( IM5Bunch<IAtomicValue> aValues ) {
      return ValidationResult.SUCCESS;
    }

    /**
     * If editing is supported subclass must edit the existing entity.
     * <p>
     * In base class throws an exception, never call superclass method when overriding.
     * <p>
     * The old values may be found in the {@link IM5Bunch#originalEntity()} which obviously is not <code>null</code>.
     *
     * @param aValues {@link IM5Bunch} - field values, never is <code>null</code>
     * @return &lt;IVtReportParam&gt; - created instance
     */
    @Override
    protected IAtomicValue doEdit( IM5Bunch<IAtomicValue> aValues ) {
      IAtomicValue nodeStr = aValues.get( NodesForCfgM5Model.FID_NODE_STR );
      return AvUtils.avValobj( NodeId.parse( nodeStr.asString() ) );
    }

    /**
     * Subclass may perform validation before remove existing entity.
     * <p>
     * In base class returns {@link ValidationResult#SUCCESS}, there is no need to call superclass method when
     * overriding.
     *
     * @param aEntity &lt;IVtReportParam&gt; - the entity to be removed, never is <code>null</code>
     * @return {@link ValidationResult} - the validation result
     */
    @Override
    protected ValidationResult doBeforeRemove( IAtomicValue aEntity ) {
      return ValidationResult.SUCCESS;
    }

    /**
     * If removing is supported subclass must remove the existing entity.
     * <p>
     * In base class throws an exception, never call superclass method when overriding.
     *
     * @param aEntity &lt;IVtReportParam&gt; - the entity to be removed, never is <code>null</code>
     */
    @Override
    protected void doRemove( IAtomicValue aEntity ) {
      // nop
    }

    /**
     * If enumeration is supported subclass must return list of entities.
     * <p>
     * In base class returns {@link IList#EMPTY}, there is no need to call superclass method when overriding.
     *
     * @return {@link IList}&lt;IVtReportParam&gt; - list of entities in the scope of maetr object
     */
    @Override
    protected IList<IAtomicValue> doListEntities() {
      return IList.EMPTY;
    }

    /**
     * If enumeration is supported subclass may allow items reordering.
     * <p>
     * In base class returns <code>null</code>, there is no need to call superclass method when overriding.
     * <p>
     * This method is called every time when user asks for {@link IM5ItemsProvider#reorderer()}.
     *
     * @return {@link IListReorderer}&lt;IVtReportParam&gt; - optional {@link IM5ItemsProvider#listItems()} reordering
     *         means
     */
    @Override
    protected IListReorderer<IAtomicValue> doGetItemsReorderer() {
      return null;
    }
  }

}
