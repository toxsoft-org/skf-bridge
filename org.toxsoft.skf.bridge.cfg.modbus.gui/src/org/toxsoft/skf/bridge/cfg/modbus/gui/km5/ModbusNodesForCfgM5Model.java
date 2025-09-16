package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.km5.ISkResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.helpers.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.panels.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.utils.*;
import org.toxsoft.skf.refbooks.lib.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * M5 model realization for {@link ModbusNode} entities using for cfg of map OPC UA Nodes <-> Gwids
 *
 * @author max
 * @author dima
 */
public class ModbusNodesForCfgM5Model
    extends M5Model<IAtomicValue> {

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.modbus.m5.ModbusNodesForCfgM5Model"; //$NON-NLS-1$

  final static String ACTID_ADD_AS_STR = SK_ID + ".opcua.to.s5.add.nodeid.as.str"; //$NON-NLS-1$

  final static String ACTID_EDIT_AS_STR = SK_ID + ".opcua.to.s5.edit.nodeid.as.str"; //$NON-NLS-1$

  final static TsActionDef ACDEF_ADD_AS_STR =
      TsActionDef.ofPush2( ACTID_ADD_AS_STR, STR_N_ADD_AS_STRING, STR_D_ADD_AS_STRING, ICONID_LIST_ADD );

  final static TsActionDef ACDEF_EDIT_AS_STR =
      TsActionDef.ofPush2( ACTID_EDIT_AS_STR, STR_N_EDIT_AS_STRING, STR_D_EDIT_AS_STRING, ICONID_DOCUMENT_EDIT );

  /**
   * Id of translator refbook.
   */
  final static String REG_TRANSLATOR_REFBOOK = "reg.translator"; //$NON-NLS-1$

  /**
   * Id of parametor words count in translator refbook.
   */
  final static String REG_TRANS_REF_ATTR_WORDS_COUNT = "wordsCount"; //$NON-NLS-1$

  /**
   * Id of parametor request type in translator refbook.
   */
  final static String REG_TRANS_REF_ATTR_REQ_TYPE = "requestType"; //$NON-NLS-1$

  /**
   * Id of parametor value type in translator refbook.
   */
  final static String REG_TRANS_REF_ATTR_VAL_TYPE = "valueType"; //$NON-NLS-1$

  /**
   * Id of parametor params in translator refbook.
   */
  final static String REG_TRANS_REF_ATTR_PARAMS = "params"; //$NON-NLS-1$

  /**
   * address
   */
  public static final String FID_MODBUS_DEVICE = "modbus.device"; //$NON-NLS-1$

  /**
   * register
   */
  public static final String FID_REGISTER = "register"; //$NON-NLS-1$

  /**
   * register
   */
  public static final String FID_REG_TRANSLATOR = "reg.translator"; //$NON-NLS-1$

  /**
   * words count
   */
  public static final String FID_WORDS_COUNT = "words.count"; //$NON-NLS-1$

  /**
   * value type
   */
  public static final String FID_VALUE_TYPE = "value.type"; //$NON-NLS-1$

  /**
   * request type
   */
  public static final String FID_REQUEST_TYPE = "request.type"; //$NON-NLS-1$

  /**
   * parameters string
   */
  public static final String FID_PARAMETERS_STR = "params.str"; //$NON-NLS-1$

  /**
   * Attribute {@link ModbusNode#getModbusDevice()}.
   */
  public final M5AttributeFieldDef<IAtomicValue> MODBUS_DEVICE = new M5AttributeFieldDef<>( FID_MODBUS_DEVICE, VALOBJ, //
      TSID_NAME, STR_N_MODBUS_DEVICE, //
      TSID_DESCRIPTION, STR_D_MODBUS_DEVICE, //
      TSID_KEEPER_ID, ModbusDevice.KEEPER_ID, //
      OPID_EDITOR_FACTORY_NAME, ValedAvValobjModbusDeviceEditor.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IAtomicValue aEntity ) {
      return AvUtils.avValobj( ((ModbusNode)aEntity.asValobj()).getModbusDevice() );
    }

  };

  /**
   * Attribute {@link ModbusNode#getRegister()}.
   */
  public final M5AttributeFieldDef<IAtomicValue> REGISTER =
      new M5AttributeFieldDef<>( FID_REGISTER, EAtomicType.INTEGER, //
          TSID_NAME, STR_N_MODBUS_REGISTER, //
          TSID_DESCRIPTION, STR_N_MODBUS_REGISTER, //
          TSID_DEFAULT_VALUE, avInt( 100 ) //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( IAtomicValue aEntity ) {
          return avInt( ((ModbusNode)aEntity.asValobj()).getRegister() );
        }

      };

  /**
   * Attribute {@link ModbusNode#getRegTranslator() } jr param from design form
   */
  public M5SingleLookupFieldDef<IAtomicValue, ISkRefbookItem> REG_TRANSLATOR =
      new M5SingleLookupFieldDef<>( FID_REG_TRANSLATOR,
          StridUtils.makeIdPath( ISkRefbookServiceHardConstants.CLSID_PREFIX_REFBOOK_ITEM, REG_TRANSLATOR_REFBOOK ) ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_TRANSLATOR, STR_D_TRANSLATOR );
          setFlags( M5FF_COLUMN );
          params().setBool( TSID_IS_NULL_ALLOWED, AV_TRUE.asBool() );
          setLookupProvider( () -> {
            ISkConnectionSupplier connSupplier = tsContext().get( ISkConnectionSupplier.class );
            ISkConnection conn = connSupplier.defConn();

            ISkRefbookService skRefServ = (ISkRefbookService)conn.coreApi().getService( ISkRefbookService.SERVICE_ID );
            ISkRefbook intervalsRefbook = skRefServ.findRefbook( REG_TRANSLATOR_REFBOOK );
            if( intervalsRefbook == null ) {
              return new ElemArrayList<>();
            }
            return intervalsRefbook.listItems();
          } );
        }

        protected ISkRefbookItem doGetFieldValue( IAtomicValue aEntity ) {
          String regTranslator = ((ModbusNode)aEntity.asValobj()).getRegTranslator();
          if( regTranslator == null || regTranslator.length() == 0 ) {
            return null;
          }

          ISkConnectionSupplier connSupplier = tsContext().get( ISkConnectionSupplier.class );
          ISkConnection conn = connSupplier.defConn();

          ISkRefbookService skRefServ = (ISkRefbookService)conn.coreApi().getService( ISkRefbookService.SERVICE_ID );
          ISkRefbook intervalsRefbook = skRefServ.findRefbook( REG_TRANSLATOR_REFBOOK );
          if( intervalsRefbook == null ) {
            return null;
          }
          return intervalsRefbook.findItem( regTranslator );
        }

        protected String doGetFieldValueName( IAtomicValue aEntity ) {
          ISkRefbookItem value = doGetFieldValue( aEntity );
          return value != null ? value.nmName() : " - "; //$NON-NLS-1$
        }

      };

  /**
   * Attribute {@link ModbusNode#getWordsCount()}.
   */
  public final M5AttributeFieldDef<IAtomicValue> WORDS_COUNT =
      new M5AttributeFieldDef<>( FID_WORDS_COUNT, EAtomicType.INTEGER, //
          TSID_NAME, STR_N_MODBUS_WORDS_COUNT, //
          TSID_DESCRIPTION, STR_D_MODBUS_WORDS_COUNT, //
          TSID_DEFAULT_VALUE, avInt( 1 ) //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( IAtomicValue aEntity ) {
          return avInt( ((ModbusNode)aEntity.asValobj()).getWordsCount() );
        }

      };

  /**
   * Attribute {@link ModbusNode#getValueType()}.
   */
  public final M5AttributeFieldDef<IAtomicValue> VALUE_TYPE =
      new M5AttributeFieldDef<>( FID_VALUE_TYPE, EAtomicType.VALOBJ, //
          TSID_NAME, STR_N_MODBUS_VALUE_TYPE, //
          TSID_DESCRIPTION, STR_D_MODBUS_VALUE_TYPE, //
          TSID_KEEPER_ID, EAtomicType.KEEPER_ID //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
          setDefaultValue( avValobj( EAtomicType.INTEGER ) );
        }

        protected IAtomicValue doGetFieldValue( IAtomicValue aEntity ) {
          return avValobj( ((ModbusNode)aEntity.asValobj()).getValueType() );
        }

      };

  /**
   * Attribute {@link ModbusNode#getRequestType()}.
   */
  public final M5AttributeFieldDef<IAtomicValue> REQUEST_TYPE =
      new M5AttributeFieldDef<>( FID_REQUEST_TYPE, EAtomicType.VALOBJ, //
          TSID_NAME, STR_N_MODBUS_REQUEST_TYPE, //
          TSID_DESCRIPTION, STR_D_MODBUS_REQUEST_TYPE, //
          TSID_KEEPER_ID, ERequestType.KEEPER_ID //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
          setDefaultValue( avValobj( ERequestType.DI ) );
        }

        protected IAtomicValue doGetFieldValue( IAtomicValue aEntity ) {
          return avValobj( ((ModbusNode)aEntity.asValobj()).getRequestType() );
        }

      };

  /**
   * Attribute {@link ModbusNode#getParams()}.
   */
  public final M5AttributeFieldDef<IAtomicValue> PARAMETERS_STR =
      new M5AttributeFieldDef<>( FID_PARAMETERS_STR, EAtomicType.STRING, //
          TSID_NAME, STR_N_MODBUS_PARAMETERS_STR, //
          TSID_DESCRIPTION, STR_D_MODBUS_PARAMETERS_STR //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
          setDefaultValue( avStr( TsLibUtils.EMPTY_STRING ) );
        }

        protected IAtomicValue doGetFieldValue( IAtomicValue aEntity ) {
          return avStr( ((ModbusNode)aEntity.asValobj()).getParams() );
        }

      };

  /**
   * Constructor
   *
   * @param aaContext ITsGuiContext - context
   */
  public ModbusNodesForCfgM5Model( ITsGuiContext aaContext ) {
    super( MODEL_ID, IAtomicValue.class );

    addFieldDefs( MODBUS_DEVICE, REGISTER );
    if( ensureTranslatorRefbook( aaContext ) ) {
      addFieldDefs( REG_TRANSLATOR );
    }

    addFieldDefs( WORDS_COUNT, VALUE_TYPE, REQUEST_TYPE, PARAMETERS_STR );
    // переопределяю только для того чтобы отключить панель фильтра
    setPanelCreator( new M5DefaultPanelCreator<>() {

      protected IM5EntityPanel<IAtomicValue> doCreateEntityEditorPanel( ITsGuiContext aContext,
          IM5LifecycleManager<IAtomicValue> aLifecycleManager ) {
        IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( aContext.params(), AvUtils.AV_FALSE );

        return new M5DefaultEntityControlledPanel<>( aContext, model(), aLifecycleManager, new Controller() );
      }

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
            new MultiPaneComponentModown<>( aContext, model(), aItemsProvider, aLifecycleManager );
        return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
      }

    } );

  }

  private static boolean ensureTranslatorRefbook( ITsGuiContext aContext ) {
    ISkConnectionSupplier connSupplier = aContext.get( ISkConnectionSupplier.class );
    ISkConnection conn = connSupplier.defConn();

    ISkRefbookService skRefServ = (ISkRefbookService)conn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    ISkRefbook intervalsRefbook = skRefServ.findRefbook( REG_TRANSLATOR_REFBOOK );
    // create all essential refbooks
    ModbusRefbookGenerator rbGenerator = new ModbusRefbookGenerator( conn, aContext.get( Shell.class ) );
    if( intervalsRefbook == null ) {
      rbGenerator.createTranslatorsRb();
    }
    return true;
  }

  @Override
  protected IM5LifecycleManager<IAtomicValue> doCreateDefaultLifecycleManager() {
    ISkConnectionSupplier cs = tsContext().get( ISkConnectionSupplier.class );
    ISkConnection conn = cs.defConn();
    return new ModbusNodesForCfgM5LifecycleManager( this, conn );
  }

  @Override
  protected IM5LifecycleManager<IAtomicValue> doCreateLifecycleManager( Object aMaster ) {
    ISkConnectionSupplier cs = tsContext().get( ISkConnectionSupplier.class );
    ISkConnection conn = cs.defConn();
    return new ModbusNodesForCfgM5LifecycleManager( this, conn );
  }

  class Controller
      extends M5EntityPanelWithValedsController<IAtomicValue> {

    public Controller() {
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public boolean doProcessEditorValueChange( IValedControl<?> aEditor, IM5FieldDef<IAtomicValue, ?> aFieldDef,
        boolean aEditFinished ) {
      switch( aFieldDef.id() ) {
        case FID_REG_TRANSLATOR:

          // when changing the Gwid then autocomplete name and description
          ISkRefbookItem item = (ISkRefbookItem)editors().getByKey( FID_REG_TRANSLATOR ).getValue();

          if( item == null ) {
            break;
          }

          IAtomicValue wordsCountVal = item.attrs().findValue( REG_TRANS_REF_ATTR_WORDS_COUNT );

          if( wordsCountVal != null && wordsCountVal.isAssigned() ) {

            IValedControl<IAtomicValue> wordsCountValed =
                (IValedControl<IAtomicValue>)editors().getByKey( FID_WORDS_COUNT );

            wordsCountValed.setValue( wordsCountVal );
          }

          IAtomicValue requestTypeVal = item.attrs().findValue( REG_TRANS_REF_ATTR_REQ_TYPE );

          if( requestTypeVal != null && requestTypeVal.isAssigned() ) {
            try {
              ERequestType rType = requestTypeVal.asValobj();

              IValedControl<IAtomicValue> requestTypeValed =
                  (IValedControl<IAtomicValue>)editors().getByKey( FID_REQUEST_TYPE );

              requestTypeValed.setValue( AvUtils.avFromObj( rType ) );
            }
            catch( TsItemNotFoundRtException e ) {
              LoggerUtils.errorLogger().error( e, "Couldnot find ERequestType with id: %s", requestTypeVal.asString() ); //$NON-NLS-1$
              // nop
            }
          }

          IAtomicValue valueTypeVal = item.attrs().findValue( REG_TRANS_REF_ATTR_VAL_TYPE );

          if( valueTypeVal != null && valueTypeVal.isAssigned() ) {

            try {
              EAtomicType vType = valueTypeVal.asValobj();

              IValedControl<IAtomicValue> valueTypeValed =
                  (IValedControl<IAtomicValue>)editors().getByKey( FID_VALUE_TYPE );

              valueTypeValed.setValue( AvUtils.avFromObj( vType ) );
            }
            catch( TsItemNotFoundRtException e ) {
              LoggerUtils.errorLogger().error( e, "Couldnot find EAtomicType with id: &s", valueTypeVal.asString() ); //$NON-NLS-1$
              // nop
            }
          }

          IAtomicValue paramsVal = item.attrs().findValue( REG_TRANS_REF_ATTR_PARAMS );

          if( paramsVal != null && paramsVal.isAssigned() ) {

            IValedControl<IAtomicValue> paramsValed =
                (IValedControl<IAtomicValue>)editors().getByKey( FID_PARAMETERS_STR );

            paramsValed.setValue( paramsVal );
          }

          break;
        default:
          break;
      }
      return true;
    }

  }

  static class ModbusNodesForCfgM5LifecycleManager
      extends M5LifecycleManager<IAtomicValue, ISkConnection> {

    /**
     * Constructor by m5 model and sk-connection as master-object.
     *
     * @param aModel IM5Model - model
     * @param aMaster ISkConnection - sk-connection
     */
    public ModbusNodesForCfgM5LifecycleManager( IM5Model<IAtomicValue> aModel, ISkConnection aMaster ) {
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

      ModbusDevice address = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_MODBUS_DEVICE )).asValobj();
      int reg = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_REGISTER )).asInt();
      int count = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_WORDS_COUNT )).asInt();
      EAtomicType valueType = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_VALUE_TYPE )).asValobj();
      ERequestType type = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_REQUEST_TYPE )).asValobj();
      String params = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_PARAMETERS_STR )).asString();

      ModbusNode modbusNode = new ModbusNode( address, reg, count, valueType, type, params );
      if( aValues.model().fieldDefs().hasKey( ModbusNodesForCfgM5Model.FID_REG_TRANSLATOR ) ) {
        ISkRefbookItem regTranslator = aValues.get( ModbusNodesForCfgM5Model.FID_REG_TRANSLATOR );
        if( regTranslator != null ) {
          modbusNode.setRegTranslator( regTranslator.id() );
        }
      }

      return AvUtils.avValobj( modbusNode );
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
      ModbusDevice address = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_MODBUS_DEVICE )).asValobj();
      int reg = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_REGISTER )).asInt();
      int count = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_WORDS_COUNT )).asInt();
      EAtomicType valueType = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_VALUE_TYPE )).asValobj();
      ERequestType type = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_REQUEST_TYPE )).asValobj();
      String params = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_PARAMETERS_STR )).asString();

      ModbusNode modbusNode = new ModbusNode( address, reg, count, valueType, type, params );
      if( aValues.model().fieldDefs().hasKey( ModbusNodesForCfgM5Model.FID_REG_TRANSLATOR ) ) {
        ISkRefbookItem regTranslator = aValues.get( ModbusNodesForCfgM5Model.FID_REG_TRANSLATOR );
        if( regTranslator != null ) {
          modbusNode.setRegTranslator( regTranslator.id() );
        }
      }

      return AvUtils.avValobj( modbusNode );
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
