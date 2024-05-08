package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.km5.ISkResources.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.fields.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.utils.*;

/**
 * M5 model realization for {@link TCPAddress} entities.
 *
 * @author dima
 */
public class TCPAddressM5Model
    extends M5Model<TCPAddress> {

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "org.toxsoft.skf.bridge.cfg.modbus.gui.km5.TCPAddress"; //$NON-NLS-1$

  /**
   * Field {@link ModbusToS5CfgDoc#id()}
   */
  public static final M5AttributeFieldDef<TCPAddress> ID = new M5StdFieldDefId<>();

  /**
   * /** Attribute {@link TCPAddress#nmName() } string name
   */
  public M5AttributeFieldDef<TCPAddress> NAME = new M5AttributeFieldDef<>( FID_NAME, EAtomicType.STRING, //
      TSID_NAME, STR_N_ADDRESS_NAME, //
      TSID_DESCRIPTION, STR_D_ADDRESS_NAME, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( TCPAddress aEntity ) {
      return avStr( aEntity.nmName() );
    }

  };

  /**
   * IP address
   */
  public static final String FID_IP_ADDRESS = "ip.address"; //$NON-NLS-1$

  /**
   * Attribute {@link TCPAddress#getIP() } IP address
   */
  public M5AttributeFieldDef<TCPAddress> IP_ADDRESS = new M5AttributeFieldDef<>( FID_IP_ADDRESS, EAtomicType.STRING, //
      TSID_NAME, STR_N_IP_ADDRESS, //
      TSID_DESCRIPTION, STR_D_IP_ADDRESS, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( TCPAddress aEntity ) {
      return avStr( aEntity.getIP().getHostAddress() );
    }

  };

  /**
   * number of port
   */
  public static final String FID_PORT_NUM = "port.number"; //$NON-NLS-1$

  /**
   * Attribute {@link TCPAddress#getPort() } port number
   */
  public M5AttributeFieldDef<TCPAddress> PORT_NUM = new M5AttributeFieldDef<>( FID_PORT_NUM, EAtomicType.INTEGER, //
      TSID_NAME, STR_N_PORT_NUMBER, //
      TSID_DESCRIPTION, STR_N_PORT_NUMBER, //
      TSID_DEFAULT_VALUE, avInt( 502 ), // TCP modbus default port
      OPID_EDITOR_FACTORY_NAME, ValedAvIntegerText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( TCPAddress aEntity ) {
      return avInt( aEntity.getPort() );
    }

  };

  /**
   * Constructor.
   */
  public TCPAddressM5Model() {
    super( MODEL_ID, TCPAddress.class );
    ID.setFlags( M5FF_HIDDEN | M5FF_INVARIANT );
    addFieldDefs( ID, NAME, IP_ADDRESS, PORT_NUM );
    setPanelCreator( new M5DefaultPanelCreator<>() {

      protected IM5EntityPanel<TCPAddress> doCreateEntityEditorPanel( ITsGuiContext aContext,
          IM5LifecycleManager<TCPAddress> aLifecycleManager ) {
        IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( aContext.params(), AvUtils.AV_FALSE );
        return new M5DefaultEntityControlledPanel<>( aContext, model(), aLifecycleManager, null );

        // MultiPaneComponentModown<TCPAddress> mpc =
        // new MultiPaneComponentModown<>( aContext, model(), aItemsProvider, aLifecycleManager ) {
        //
        // @Override
        // protected TCPAddress doAddItem() {
        // TCPAddress selected = PanelTCPAddressSelector.selectTCPAddress( tsContext(), TCPAddress.NONE );
        // return selected;
        // }
        //
        // @Override
        // protected TCPAddress doEditItem( TCPAddress aItem ) {
        // TCPAddress selected = PanelTCPAddressSelector.selectTCPAddress( tsContext(), aItem );
        // return selected;
        // }
        //
        // protected boolean doRemoveItem( TCPAddress aItem ) {
        // return super.doRemoveItem( aItem );
        // }
        //
        // };
        // return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
      }

    } );

  }

  @Override
  protected IM5LifecycleManager<TCPAddress> doCreateDefaultLifecycleManager() {
    ModbusToS5CfgDocService docService = new ModbusToS5CfgDocService( tsContext() );
    return new TCPAddressM5LifecycleManager( this, docService );
  }

}
