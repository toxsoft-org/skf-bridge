package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.helpers.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.panels.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * M5 model realization for {@link ModbusNode} entities using for cfg of map uanodes-gwids
 *
 * @author max
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
   * address
   */
  public static final String FID_ADDRESS = "address"; //$NON-NLS-1$

  /**
   * register
   */
  public static final String FID_REGISTER = "register"; //$NON-NLS-1$

  /**
   * words count
   */
  public static final String FID_WORDS_COUNT = "words.count"; //$NON-NLS-1$

  /**
   * request type
   */
  public static final String FID_REQUEST_TYPE = "request.type"; //$NON-NLS-1$

  /**
   * Структура для описания IP адреса которые хранятся ВМЕСТЕ с сущностью. Ключевое отличие от связи с объектам в том
   * что по связи объекты хранятся отдельно от сущности.
   */
  public final IM5SingleModownFieldDef<IAtomicValue, TCPAddress> TCP_ADDRESS =
      new M5SingleModownFieldDef<>( FID_ADDRESS, TCPAddressM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( "адрес", " TCP/IP адрес" );
          params().setStr( IValedControlConstants.OPID_EDITOR_FACTORY_NAME,
              ValedAvValobjTCPAddressEditor.FACTORY_NAME );
          setFlags( M5FF_DETAIL );
        }

        protected TCPAddress doGetFieldValue( IAtomicValue aEntity ) {
          return ((ModbusNode)aEntity.asValobj()).getAddress();
        }

      };

  public final M5AttributeFieldDef<IAtomicValue> REGISTER =
      new M5AttributeFieldDef<>( FID_REGISTER, EAtomicType.INTEGER, //
          TSID_NAME, "Регистр modbus", //
          TSID_DESCRIPTION, "Регистр modbus" //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
          // setFlags( M5FF_COLUMN | M5FF_READ_ONLY | M5FF_HIDDEN );
          // setFlags( M5FF_COLUMN | M5FF_HIDDEN );
        }

        protected IAtomicValue doGetFieldValue( IAtomicValue aEntity ) {
          return avInt( ((ModbusNode)aEntity.asValobj()).getRegister() );
        }

      };

  public final M5AttributeFieldDef<IAtomicValue> WORDS_COUNT =
      new M5AttributeFieldDef<>( FID_WORDS_COUNT, EAtomicType.INTEGER, //
          TSID_NAME, "Количество слов modbus", //
          TSID_DESCRIPTION, "Количество слов modbus" //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
          // setFlags( M5FF_COLUMN | M5FF_READ_ONLY | M5FF_HIDDEN );
          // setFlags( M5FF_COLUMN | M5FF_HIDDEN );
        }

        protected IAtomicValue doGetFieldValue( IAtomicValue aEntity ) {
          return avInt( ((ModbusNode)aEntity.asValobj()).getWordsCount() );
        }

      };

  public final M5AttributeFieldDef<IAtomicValue> REQUEST_TYPE =
      new M5AttributeFieldDef<>( FID_REQUEST_TYPE, EAtomicType.VALOBJ, //
          TSID_NAME, "Тип запроса modbus", //
          TSID_DESCRIPTION, "Тип запроса modbus", //
          TSID_KEEPER_ID, ERequestType.KEEPER_ID //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
          setDefaultValue( avValobj( ERequestType.DI ) );
          // setFlags( M5FF_COLUMN | M5FF_READ_ONLY | M5FF_HIDDEN );
          // setFlags( M5FF_COLUMN | M5FF_HIDDEN );
        }

        protected IAtomicValue doGetFieldValue( IAtomicValue aEntity ) {
          return avValobj( ((ModbusNode)aEntity.asValobj()).getRequestType() );
        }

      };

  /**
   * Constructor
   */
  public ModbusNodesForCfgM5Model() {
    super( MODEL_ID, IAtomicValue.class );
    addFieldDefs( TCP_ADDRESS, REGISTER, WORDS_COUNT, REQUEST_TYPE );
  }

  @Override
  protected IM5LifecycleManager<IAtomicValue> doCreateDefaultLifecycleManager() {
    ISkConnectionSupplier cs = tsContext().get( ISkConnectionSupplier.class );
    // TODO which connection to use?
    ISkConnection conn = cs.defConn();
    return new ModbusNodesForCfgM5LifecycleManager( this, conn );
  }

  @Override
  protected IM5LifecycleManager<IAtomicValue> doCreateLifecycleManager( Object aMaster ) {
    return new ModbusNodesForCfgM5LifecycleManager( this, ISkConnection.class.cast( aMaster ) );
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

      TCPAddress address = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_ADDRESS )).asValobj();
      int reg = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_REGISTER )).asInt();
      int count = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_WORDS_COUNT )).asInt();
      ERequestType type = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_REQUEST_TYPE )).asValobj();

      // return AvUtils.avValobj( new ModbusNode( reg, count, type ) );
      return AvUtils.avValobj( new ModbusNode( address, reg, count, type ) );
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

      TCPAddress address = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_ADDRESS )).asValobj();
      int reg = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_REGISTER )).asInt();
      int count = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_WORDS_COUNT )).asInt();
      ERequestType type = ((IAtomicValue)aValues.get( ModbusNodesForCfgM5Model.FID_REQUEST_TYPE )).asValobj();

      // return AvUtils.avValobj( new ModbusNode( reg, count, type ) );
      return AvUtils.avValobj( new ModbusNode( address, reg, count, type ) );
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
