package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.km5.ISkResources.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.fields.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tsgui.valed.controls.basic.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.utils.*;

/**
 * M5 model realization for {@link ModbusDevice} entities.
 *
 * @author max
 */
public class ModbusDeviceM5Model
    extends M5Model<ModbusDevice> {

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "org.toxsoft.skf.bridge.cfg.modbus.gui.km5.ModbusDevice"; //$NON-NLS-1$

  /**
   * Field {@link ModbusDevice#id()}
   */
  public static final M5AttributeFieldDef<ModbusDevice> ID = new M5StdFieldDefId<>();

  /**
   * Attribute {@link ModbusDevice#nmName() } string name
   */
  public M5AttributeFieldDef<ModbusDevice> NAME = new M5AttributeFieldDef<>( FID_NAME, EAtomicType.STRING, //
      TSID_NAME, STR_N_ADDRESS_NAME, //
      TSID_DESCRIPTION, STR_D_ADDRESS_NAME, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( ModbusDevice aEntity ) {
      return avStr( aEntity.nmName() );
    }

  };

  /**
   * TCP (or RTU) index
   */
  public static final String FID_IS_TCP_INDEX = "is.tcp.index"; //$NON-NLS-1$

  /**
   * Attribute {@link ModbusDevice#isTcp() } is tcp index
   */
  public M5AttributeFieldDef<ModbusDevice> IS_TCP_INDEX =
      new M5AttributeFieldDef<>( FID_IS_TCP_INDEX, EAtomicType.BOOLEAN, //
          TSID_NAME, "Устройство TCP", //
          TSID_DESCRIPTION, "Устройство TCP (или RTU)" //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );

          setDefaultValue( avBool( true ) );
        }

        protected IAtomicValue doGetFieldValue( ModbusDevice aEntity ) {
          return avBool( aEntity.isTcp() );
        }

      };

  /**
   * TCP (or RTU) index
   */
  public static final String FID_DEVICE_CONN_OPTS = "device.conn.opts"; //$NON-NLS-1$

  /**
   * Attribute {@link ModbusDevice#getDeviceOptValues() } device connection options
   */
  final IM5FieldDef<ModbusDevice, IOptionSet> DEVICE_CONN_OPTS =
      new M5FieldDef<>( FID_DEVICE_CONN_OPTS, IOptionSet.class ) {

        @Override
        protected void doInit() {
          setNameAndDescription( "Параметры", "Параметры устройства" );
          setFlags( M5FF_COLUMN );
          setValedEditor( ValedOptionSet.FACTORY_NAME );
          // default
          setDefaultValue( ModbusDeviceOptionsUtils.getParamDefaultValuess( true ) );
          params().setBool( IValedControlConstants.OPDEF_IS_WIDTH_FIXED, false );
        }

        protected IOptionSet doGetFieldValue( ModbusDevice aEntity ) {
          return aEntity.getDeviceOptValues();
        }

        protected String doGetFieldValueName( ModbusDevice aEntity ) {
          IList<IDataDef> paramDefenitions = ModbusDeviceOptionsUtils.getParamDefenitions( aEntity.isTcp() );
          IOptionSet paramValues = aEntity.getDeviceOptValues();
          StringBuilder result = new StringBuilder();
          String add = TsLibUtils.EMPTY_STRING;
          for( IDataDef dDef : paramDefenitions ) {
            result.append( add ).append( dDef.nmName() ).append( ": " )
                .append( dDef.getValue( paramValues ).toString() );
            add = ", ";
          }
          return result.toString();
        }

      };

  /**
   * Constructor.
   */
  public ModbusDeviceM5Model() {
    super( MODEL_ID, ModbusDevice.class );
    ID.setFlags( M5FF_HIDDEN | M5FF_INVARIANT );
    addFieldDefs( ID, NAME, IS_TCP_INDEX, DEVICE_CONN_OPTS );

    setPanelCreator( new M5DefaultPanelCreator<>() {

      @Override
      protected IM5EntityPanel<ModbusDevice> doCreateEntityEditorPanel( ITsGuiContext aContext,
          IM5LifecycleManager<ModbusDevice> aLifecycleManager ) {
        IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( aContext.params(), AvUtils.AV_FALSE );
        return new M5DefaultEntityControlledPanel<>( aContext, model(), aLifecycleManager, new Controller() );
      }

      class Controller
          extends M5EntityPanelWithValedsController<ModbusDevice> {

        @Override
        public void beforeSetValues( IM5Bunch<ModbusDevice> aValues ) {
          IAtomicValue isTcpVal = (IAtomicValue)aValues.get( FID_IS_TCP_INDEX );
          prepareOptValusEditor( isTcpVal.asBool() );
        }

        @Override
        public boolean doProcessEditorValueChange( IValedControl<?> aEditor, IM5FieldDef<ModbusDevice, ?> aFieldDef,
            boolean aEditFinished ) {
          switch( aFieldDef.id() ) {
            case FID_IS_TCP_INDEX:
              // when changing the provider, change the value editor
              IAtomicValue isTcpVal = (IAtomicValue)editors().getByKey( FID_IS_TCP_INDEX ).getValue();
              prepareOptValusEditor( isTcpVal.asBool() );
              // we will try to use the available value as much as possible
              // ValedOptionSet vops = getEditor( FID_REALIZATION, ValedOptionSet.class );
              // vops.setValue( lastValues().getAs( FID_REALIZATION, IOptionSet.class ) );
              break;

            default:
              break;
          }
          return true;
        }

        private void prepareOptValusEditor( boolean aIsTcp ) {
          ValedOptionSet vops = getEditor( FID_DEVICE_CONN_OPTS, ValedOptionSet.class );

          vops.setOptionDefs( ModbusDeviceOptionsUtils.getParamDefenitions( aIsTcp ) );
          vops.setValue( ModbusDeviceOptionsUtils.getParamDefaultValuess( aIsTcp ) );
        }

      }

    } );

  }

  @Override
  protected IM5LifecycleManager<ModbusDevice> doCreateDefaultLifecycleManager() {
    ModbusToS5CfgDocService docService = new ModbusToS5CfgDocService( tsContext() );
    return new ModbusDeviceM5LifecycleManager( this, docService );
  }

}
