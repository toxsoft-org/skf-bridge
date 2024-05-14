package org.toxsoft.skf.bridge.cfg.modbus.gui.panels;

import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tsgui.valed.controls.helpers.*;
import org.toxsoft.core.tsgui.valed.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;

/**
 * Allows to select {@link TCPAddress}.
 *
 * @author dima
 */
public class ValedTCPAddressEditor
    extends AbstractValedTextAndButton<TCPAddress> {

  /**
   * The factory name.
   */
  public static final String FACTORY_NAME = VALED_EDNAME_PREFIX + ".TCPAddressEditor"; //$NON-NLS-1$

  /**
   * The factory class.
   *
   * @author dima
   */
  static class Factory
      extends AbstractValedControlFactory {

    protected Factory() {
      super( FACTORY_NAME );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    protected IValedControl<TCPAddress> doCreateEditor( ITsGuiContext aContext ) {
      AbstractValedControl<TCPAddress, ?> e = new ValedTCPAddressEditor( aContext );
      return e;
    }

  }

  /**
   * The factory singleton.
   */
  public static final AbstractValedControlFactory FACTORY = new Factory();

  /**
   * Constructor for subclasses.
   *
   * @param aContext {@link ITsGuiContext} - the valed context
   * @throws TsNullArgumentRtException аргумент = null
   */
  public ValedTCPAddressEditor( ITsGuiContext aContext ) {
    super( aContext );
    setParamIfNull( OPDEF_IS_WIDTH_FIXED, AV_FALSE );
    setParamIfNull( OPDEF_IS_HEIGHT_FIXED, AV_TRUE );
    setParamIfNull( OPDEF_VERTICAL_SPAN, AV_1 );
  }

  @Override
  protected boolean doProcessButtonPress() {
    // create and dispaly TCPAddress selector
    TCPAddress address =
        PanelTCPAddressSelector.selectTCPAddress( tsContext(), canGetValue().isOk() ? getValue() : null );

    if( address != null ) {
      doSetUnvalidatedValue( address );
      return true;
    }
    return false;
  }

  @Override
  public ValidationResult canGetValue() {
    return ValidationResult.SUCCESS;
  }

  @Override
  protected void doUpdateTextControl() {
    // nop
  }

  @Override
  protected TCPAddress doGetUnvalidatedValue() {
    // TODO
    // return TCPAddress.of( getTextControl().getText() );
    return TCPAddress.NONE;
  }

  @Override
  protected void doDoSetUnvalidatedValue( TCPAddress aValue ) {
    String txt = TsLibUtils.EMPTY_STRING;
    if( aValue != null ) {
      txt = aValue.toString();
    }
    getTextControl().setText( txt );
  }

}
