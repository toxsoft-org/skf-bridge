package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

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
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;

/**
 * M5 model of node cfg {@link CfgOpcUaNode}
 *
 * @author max
 */
public class CfgOpcUaNodeM5Model
    extends M5Model<CfgOpcUaNode> {

  /**
   * Идентификатор модели.
   */
  public static final String MODEL_ID = "bridge.cfg.opcua.m5.CfgOpcUaNode"; //$NON-NLS-1$

  final static String ACTID_SYNCHRONIZE = SK_ID + "bridge.cfg.opcua.to.s5.synchronize.nodes"; //$NON-NLS-1$

  final static String ACTID_REMOVE_ALL = SK_ID + "bridge.cfg.opcua.to.s5.remove.all"; //$NON-NLS-1$

  final static String ACTID_REMOVE_UNNECESSARY = SK_ID + "bridge.cfg.opcua.to.s5.remove.unnecessary"; //$NON-NLS-1$

  final static String ACTID_GENERATE_DEVCFG_FILE = SK_ID + "bridge.cfg.opcua.to.s5.generate.devcfg.file"; //$NON-NLS-1$

  final static TsActionDef ACDEF_SYNCHRONIZE =
      TsActionDef.ofPush2( ACTID_SYNCHRONIZE, STR_N_ADD_MISSED_NODES, STR_D_ADD_MISSED_NODES, ICONID_ADD_NODE );

  final static TsActionDef ACDEF_REMOVE_UNNECESSARY = TsActionDef.ofPush2( ACTID_REMOVE_UNNECESSARY,
      STR_N_REMOVE_MISSED_NODES, STR_D_REMOVE_MISSED_NODES, ICONID_REMOVE_NODE );

  final static TsActionDef ACDEF_REMOVE_ALL =
      TsActionDef.ofPush2( ACTID_REMOVE_ALL, STR_N_REMOVE_ALL_NODES, STR_D_REMOVE_ALL_NODES, ICONID_CLEAR_ALL );

  final static TsActionDef ACDEF_GENERATE_DEVCFG_FILE = TsActionDef.ofPush2( ACTID_GENERATE_DEVCFG_FILE,
      STR_N_GENERATE_DEVCFG, STR_D_GENERATE_DEVCFG, ICONID_SHOW_GENERATE_DEVCFG );

  /**
   * string id of cfg node
   */
  public static final String FID_STRID = "strid"; //$NON-NLS-1$

  /**
   * {@link EAtomicType} type of node
   */
  public static final String FID_TYPE = "type"; //$NON-NLS-1$

  /**
   * is read index of node
   */
  public static final String FID_IS_READ = "is.read"; //$NON-NLS-1$

  /**
   * is write index of node
   */
  public static final String FID_IS_WRITE = "is.write"; //$NON-NLS-1$

  /**
   * is synch index of node
   */
  public static final String FID_IS_SYNCH = "is.synch"; //$NON-NLS-1$

  /**
   * Attribute {@link CfgOpcUaNode#getNodeId() } string id
   */
  final M5AttributeFieldDef<CfgOpcUaNode> STRID = new M5AttributeFieldDef<>( FID_STRID, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_STRID, //
      TSID_DESCRIPTION, STR_D_PARAM_STRID, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_READ_ONLY | M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( CfgOpcUaNode aEntity ) {
      return avStr( aEntity.getNodeId() );
    }

  };

  /**
   * Attribute {@link CfgOpcUaNode#getType() } type
   */
  final M5AttributeFieldDef<CfgOpcUaNode> TYPE = new M5AttributeFieldDef<>( FID_TYPE, EAtomicType.VALOBJ, //
      TSID_NAME, STR_N_NODE_VALUE_TYPE, //
      TSID_DESCRIPTION, STR_D_NODE_VALUE_TYPE, //
      TSID_KEEPER_ID, EAtomicType.KEEPER_ID //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
      setDefaultValue( avValobj( EAtomicType.INTEGER ) );
    }

    protected IAtomicValue doGetFieldValue( CfgOpcUaNode aEntity ) {
      return avValobj( aEntity.getType() );
    }
  };

  /**
   * Attribute {@link CfgOpcUaNode#isRead() } boolean
   */
  final M5AttributeFieldDef<CfgOpcUaNode> IS_READ = new M5AttributeFieldDef<>( FID_IS_READ, EAtomicType.BOOLEAN, //
      TSID_NAME, STR_N_NODE_2READ, //
      TSID_DESCRIPTION, STR_D_NODE_2READ //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( CfgOpcUaNode aEntity ) {
      return avBool( aEntity.isRead() );
    }

  };

  /**
   * Attribute {@link CfgOpcUaNode#isWrite() } boolean
   */
  final M5AttributeFieldDef<CfgOpcUaNode> IS_WRITE = new M5AttributeFieldDef<>( FID_IS_WRITE, EAtomicType.BOOLEAN, //
      TSID_NAME, STR_N_NODE_2WRITE, //
      TSID_DESCRIPTION, STR_D_NODE_2WRITE //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( CfgOpcUaNode aEntity ) {
      return avBool( aEntity.isWrite() );
    }

  };

  /**
   * Attribute {@link CfgOpcUaNode#isSynch() } boolean
   */
  final M5AttributeFieldDef<CfgOpcUaNode> IS_SYNCH = new M5AttributeFieldDef<>( FID_IS_SYNCH, EAtomicType.BOOLEAN, //
      TSID_NAME, STR_N_NODE_SYNC, //
      TSID_DESCRIPTION, STR_D_NODE_SYNC//
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( CfgOpcUaNode aEntity ) {
      return avBool( aEntity.isSynch() );
    }

  };

  /**
   * Constructor.
   */
  public CfgOpcUaNodeM5Model() {
    super( MODEL_ID, CfgOpcUaNode.class );
    addFieldDefs( STRID, TYPE, IS_READ, IS_WRITE, IS_SYNCH );

    setPanelCreator( new M5DefaultPanelCreator<>() {

      protected IM5CollectionPanel<CfgOpcUaNode> doCreateCollEditPanel( ITsGuiContext aContext,
          IM5ItemsProvider<CfgOpcUaNode> aItemsProvider, IM5LifecycleManager<CfgOpcUaNode> aLifecycleManager ) {
        MultiPaneComponentModown<CfgOpcUaNode> mpc =
            new MultiPaneComponentModown<>( aContext, model(), aItemsProvider, aLifecycleManager ) {

              @Override
              protected ITsToolbar doCreateToolbar( ITsGuiContext aContext2, String aName, EIconSize aIconSize,
                  IListEdit<ITsActionDef> aActs ) {

                // удалить всё
                // aActs.add( ACDEF_REMOVE_ALL );
                // aActs.add( ACDEF_SEPARATOR );

                // добавить недостающие
                aActs.add( ACDEF_SYNCHRONIZE );
                // aActs.add( ACDEF_REMOVE_UNNECESSARY );

                // aActs.add( ACDEF_SEPARATOR );
                // aActs.add( OpcToS5DataCfgUnitM5Model.ACDEF_SAVE_DOC );
                // aActs.add( ACDEF_GENERATE_DEVCFG_FILE );

                ITsToolbar toolbar =

                    super.doCreateToolbar( aContext2, aName, aIconSize, aActs );

                toolbar.addListener( aActionId -> {
                  // nop

                } );

                toolbar.setIconSize( EIconSize.IS_24X24 );
                return toolbar;
              }

              @Override
              protected void doProcessAction( String aActionId ) {

                switch( aActionId ) {

                  case ACTID_REMOVE_ALL:
                    ((CfgOpcUaNodeLifecycleManager)lifecycleManager()).removeAll( aContext );
                    doFillTree();
                    break;

                  case ACTID_SYNCHRONIZE:
                    ((CfgOpcUaNodeLifecycleManager)lifecycleManager()).ensureNodesCfgs( aContext );
                    doFillTree();
                    break;

                  case OpcToS5DataCfgUnitM5Model.ACTID_SAVE_DOC:
                    // ((CfgOpcUaNodeLifecycleManager)lifecycleManager()).saveCurrState( tsContext() );

                    break;

                  case ACTID_GENERATE_DEVCFG_FILE:
                    // ((CfgOpcUaNodeLifecycleManager)lifecycleManager()).generateFileFromCurrState( tsContext() );
                    break;

                  case ACTID_REMOVE_UNNECESSARY:
                    ((CfgOpcUaNodeLifecycleManager)lifecycleManager()).removeUnnecessary( aContext );
                    doFillTree();
                    break;

                  default:
                    throw new TsNotAllEnumsUsedRtException( aActionId );
                }
              }

            };
        return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
      }
    } );
  }

  @Override
  protected IM5LifecycleManager<CfgOpcUaNode> doCreateDefaultLifecycleManager() {

    return new CfgOpcUaNodeLifecycleManager( this,
        new OpcToS5DataCfgDoc( TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING ) );
  }

  @Override
  protected IM5LifecycleManager<CfgOpcUaNode> doCreateLifecycleManager( Object aMaster ) {
    return new CfgOpcUaNodeLifecycleManager( this, OpcToS5DataCfgDoc.class.cast( aMaster ) );
  }

  static class CfgOpcUaNodeLifecycleManager
      extends M5LifecycleManager<CfgOpcUaNode, OpcToS5DataCfgDoc> {

    /**
     * Constructor by m5 model and sk-connection as master-object.
     *
     * @param aModel IM5Model - model
     * @param aMaster ISkConnection - sk-connection
     */
    public CfgOpcUaNodeLifecycleManager( IM5Model<CfgOpcUaNode> aModel, OpcToS5DataCfgDoc aMaster ) {
      super( aModel, false, true, false, true, aMaster );
    }

    public void removeUnnecessary( @SuppressWarnings( "unused" ) ITsGuiContext aContext ) {
      IList<OpcToS5DataCfgUnit> dataCfgUnits = master().dataUnits();

      IList<CfgOpcUaNode> nodesCfgsList = master().getNodesCfgs();
      IStringMapEdit<CfgOpcUaNode> nodesCfgs = new StringMap<>();
      for( CfgOpcUaNode node : nodesCfgsList ) {
        nodesCfgs.put( node.getNodeId(), node );
      }

      IStringMapEdit<CfgOpcUaNode> actualNodesCfgs = new StringMap<>();

      for( OpcToS5DataCfgUnit unit : dataCfgUnits ) {
        IList<NodeId> nodes = OpcUaUtils.convertToNodesList( unit.getDataNodes2() );

        for( int i = 0; i < nodes.size(); i++ ) {
          NodeId node = nodes.get( i );
          if( nodesCfgs.hasKey( node.toParseableString() ) ) {
            actualNodesCfgs.put( node.toParseableString(), nodesCfgs.getByKey( node.toParseableString() ) );
          }
        }

        master().setNodesCfgs( actualNodesCfgs.values() );
      }

      itemsProvider().informOnItemsListChange();
    }

    public void removeAll( @SuppressWarnings( "unused" ) ITsGuiContext aContext ) {
      master().setNodesCfgs( new ElemArrayList<>() );

      itemsProvider().informOnItemsListChange();
    }

    @Override
    protected CfgOpcUaNode doEdit( IM5Bunch<CfgOpcUaNode> aValues ) {
      CfgOpcUaNode origin = aValues.originalEntity();
      EAtomicType type = aValues.getAsAv( FID_TYPE ).asValobj();
      boolean isRead = aValues.getAsAv( FID_IS_READ ).asBool();
      boolean isWrite = aValues.getAsAv( FID_IS_WRITE ).asBool();
      boolean isSynch = aValues.getAsAv( FID_IS_SYNCH ).asBool();

      origin.setType( type );
      origin.setRead( isRead );
      origin.setWrite( isWrite );
      origin.setSynch( isSynch );

      itemsProvider().informOnItemsListChange();

      return origin;
    }

    @Override
    protected void doRemove( CfgOpcUaNode aEntity ) {
      IListEdit<CfgOpcUaNode> nodesCfgsList = new ElemArrayList<>( master().getNodesCfgs() );
      nodesCfgsList.remove( aEntity );
      master().setNodesCfgs( nodesCfgsList );

      itemsProvider().informOnItemsListChange();
    }

    @Override
    protected IList<CfgOpcUaNode> doListEntities() {
      return master().getNodesCfgs();
    }

    void ensureNodesCfgs( ITsGuiContext aContext ) {
      OpcUaUtils.synchronizeNodesCfgs( master(), aContext, false );
    }
  }

}
