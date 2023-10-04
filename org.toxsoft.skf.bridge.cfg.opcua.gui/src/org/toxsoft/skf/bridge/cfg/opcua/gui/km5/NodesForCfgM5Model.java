package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.api.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
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
    extends M5Model<NodeId> {

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.opcua.m5.NodesForCfgM5Model"; //$NON-NLS-1$

  final static String ACTID_ADD_AS_STR = SK_ID + ".opcua.to.s5.add.nodeid.as.str"; //$NON-NLS-1$

  final static String ACTID_EDIT_AS_STR = SK_ID + ".opcua.to.s5.edit.nodeid.as.str"; //$NON-NLS-1$

  final static TsActionDef ACDEF_ADD_AS_STR =
      TsActionDef.ofPush2( ACTID_ADD_AS_STR, "Добавить как строку", "Добавить как строку", ICONID_LIST_ADD );

  final static TsActionDef ACDEF_EDIT_AS_STR = TsActionDef.ofPush2( ACTID_EDIT_AS_STR, "Редактировать как строку",
      "Редактировать как строку", ICONID_DOCUMENT_EDIT );

  /**
   * Canonical str node
   */
  public static final String FID_NODE_STR = "node.str"; //$NON-NLS-1$

  public M5AttributeFieldDef<NodeId> NODE_STR = new M5AttributeFieldDef<>( FID_NODE_STR, EAtomicType.STRING, //
      TSID_NAME, "Node Str", //
      TSID_DESCRIPTION, "Node Str" //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
      // setFlags( M5FF_COLUMN | M5FF_READ_ONLY | M5FF_HIDDEN );
      // setFlags( M5FF_COLUMN | M5FF_HIDDEN );
    }

    protected IAtomicValue doGetFieldValue( NodeId aEntity ) {
      return avStr( aEntity.toParseableString() );
    }

  };

  /**
   * Constructor
   */
  public NodesForCfgM5Model() {
    super( MODEL_ID, NodeId.class );
    addFieldDefs( NODE_STR );

    setPanelCreator( new M5DefaultPanelCreator<>() {

      protected IM5CollectionPanel<NodeId> doCreateCollEditPanel( ITsGuiContext aContext,
          IM5ItemsProvider<NodeId> aItemsProvider, IM5LifecycleManager<NodeId> aLifecycleManager ) {
        MultiPaneComponentModown<NodeId> mpc =
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
              protected NodeId doAddItem() {

                UaClient uaClient =
                    (UaClient)tsContext().find( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_OPC_CONNECTION );
                if( uaClient != null ) {
                  System.out.println( "Selecetd opc conn: " + uaClient.toString() );
                }
                else {
                  System.out.println( "Selecetd opc conn: " + "null" );
                }

                IList<UaTreeNode> selNodes = OpcUaNodesSelector.selectUaNode( aContext, (OpcUaClient)uaClient );

                if( selNodes == null || selNodes.size() == 0 ) {
                  return null;
                }

                IM5BunchEdit<NodeId> bunch = new M5BunchEdit<>( model() );
                bunch.set( FID_NODE_STR, avStr( selNodes.first().getNodeId() ) );
                aLifecycleManager.create( bunch );
                return NodeId.parse( selNodes.first().getNodeId() );
              }

              @Override
              protected NodeId doEditItem( NodeId aItem ) {

                UaClient uaClient =
                    (UaClient)tsContext().find( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_OPC_CONNECTION );
                if( uaClient != null ) {
                  System.out.println( "Selecetd opc conn: " + uaClient.toString() );
                }
                else {
                  System.out.println( "Selecetd opc conn: " + "null" );
                }

                IList<UaTreeNode> selNodes = OpcUaNodesSelector.selectUaNode( aContext, (OpcUaClient)uaClient );

                if( selNodes == null || selNodes.size() == 0 ) {
                  return aItem;
                }

                IM5BunchEdit<NodeId> bunch = new M5BunchEdit<>( model() );
                bunch.fillFrom( aItem, true );
                bunch.set( FID_NODE_STR, avStr( selNodes.first().getNodeId() ) );
                aLifecycleManager.edit( bunch );
                return NodeId.parse( selNodes.first().getNodeId() );
              }

              @Override
              protected boolean doRemoveItem( NodeId aItem ) {
                return super.doRemoveItem( aItem );
              }

            };
        return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
      }
    } );

  }

  @Override
  protected IM5LifecycleManager<NodeId> doCreateDefaultLifecycleManager() {
    ISkConnectionSupplier cs = tsContext().get( ISkConnectionSupplier.class );
    // TODO which connection to use?
    ISkConnection conn = cs.defConn();
    return new NodesForCfgM5LifecycleManager( this, conn );
  }

  @Override
  protected IM5LifecycleManager<NodeId> doCreateLifecycleManager( Object aMaster ) {
    return new NodesForCfgM5LifecycleManager( this, ISkConnection.class.cast( aMaster ) );
  }

  static class NodesForCfgM5LifecycleManager
      extends M5LifecycleManager<NodeId, ISkConnection> {

    /**
     * Constructor by m5 model and sk-connection as master-object.
     *
     * @param aModel IM5Model - model
     * @param aMaster ISkConnection - sk-connection
     */
    public NodesForCfgM5LifecycleManager( IM5Model<NodeId> aModel, ISkConnection aMaster ) {
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
    protected ValidationResult doBeforeCreate( IM5Bunch<NodeId> aValues ) {
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
    protected NodeId doCreate( IM5Bunch<NodeId> aValues ) {
      IAtomicValue nodeStr = aValues.get( NodesForCfgM5Model.FID_NODE_STR );
      return NodeId.parse( nodeStr.asString() );
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
    protected ValidationResult doBeforeEdit( IM5Bunch<NodeId> aValues ) {
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
    protected NodeId doEdit( IM5Bunch<NodeId> aValues ) {
      IAtomicValue nodeStr = aValues.get( NodesForCfgM5Model.FID_NODE_STR );
      return NodeId.parse( nodeStr.asString() );
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
    protected ValidationResult doBeforeRemove( NodeId aEntity ) {
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
    protected void doRemove( NodeId aEntity ) {
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
    protected IList<NodeId> doListEntities() {
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
    protected IListReorderer<NodeId> doGetItemsReorderer() {
      return null;
    }
  }

}
