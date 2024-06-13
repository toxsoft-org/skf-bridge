package org.toxsoft.skf.bridge.cfg.modbus.gui.panels;

import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tsgui.valed.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;

/**
 * {@link EAtomicType#VALOBJ} of type {@link ModbusDevice} editor.
 * <p>
 * Wraps over {@link ValedModbusDeviceEditor}.
 *
 * @author dima
 */
public class ValedAvValobjModbusDeviceEditor
    extends AbstractAvValobjWrapperValedControl<ModbusDevice> {

  /**
   * The factory name.
   */
  public static final String FACTORY_NAME = VALED_EDNAME_PREFIX + ".ValedAvValobjModbusDeviceEditor"; //$NON-NLS-1$

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
    protected IValedControl<IAtomicValue> doCreateEditor( ITsGuiContext aContext ) {
      return new ValedAvValobjModbusDeviceEditor( aContext );
    }

  }

  /**
   * The factory singleton.
   */
  public static final AbstractValedControlFactory FACTORY = new Factory();

  /**
   * Constructor.
   *
   * @param aTsContext {@link ITsGuiContext} - the editor context
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public ValedAvValobjModbusDeviceEditor( ITsGuiContext aTsContext ) {
    super( aTsContext, ValedModbusDeviceEditor.FACTORY );
  }

}
