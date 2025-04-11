package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.*;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.utils.*;
import org.toxsoft.core.tsgui.valed.controls.basic.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;
import org.toxsoft.uskat.core.api.sysdescr.*;

/**
 * Панель для выбора класса.
 * <p>
 *
 * @author dima
 */
public class PanelClassInfoSelector
    extends AbstractTsDialogPanel<ISkClassInfo, ITsGuiContext> {

  ValedComboSelector<ISkClassInfo> classInfosCombo;

  protected PanelClassInfoSelector( Composite aParent, TsDialog<ISkClassInfo, ITsGuiContext> aOwnerDialog ) {
    super( aParent, aOwnerDialog );
    this.setLayout( new GridLayout( 2, false ) );
    CLabel l = new CLabel( this, SWT.LEFT );
    l.setText( STR_CLASSES_LIST );

    IList<ISkClassInfo> values = new ElemArrayList<>();
    ITsVisualsProvider<ISkClassInfo> visualsProvider = ISkClassInfo::id;
    classInfosCombo = new ValedComboSelector<>( tsContext(), values, visualsProvider );
    classInfosCombo.createControl( this ).setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );
    // оставим пока для примера, может пригодится еще
    classInfosCombo.eventer().addListener( ( aSource, aEditFinished ) -> {
      ISkClassInfo ci = classInfosCombo.selectedItem();
      if( ci != null ) {
        // TODO
      }
    } );
  }

  // ------------------------------------------------------------------------------------
  // AbstractTsDialogPanel
  //

  @Override
  protected void doSetDataRecord( ISkClassInfo aData ) {
    IListEdit<ISkClassInfo> cls = new ElemArrayList<>();
    for( ISkClassInfo ci : aData.listSubclasses( true, false ) ) {
      if( ci.attrs().list().hasKey( IOpcUaServerConnCfgConstants.AID_OPC_UA_CLASS_MARKER ) ) {
        cls.add( ci );
      }
    }
    classInfosCombo.setItems( cls );
  }

  @Override
  protected ISkClassInfo doGetDataRecord() {
    return classInfosCombo.getValue();
  }

  // ------------------------------------------------------------------------------------
  //
  //

  /**
   * Выбор класса наследника из выпадающего списка.
   * <p>
   *
   * @param aParent ISkClassInfo - класс родитель
   * @param aContext - контекст
   * @return ISkClassInfo - выбранный класс или <b>null</b> в случает отказа от редактирования
   */
  public static final ISkClassInfo selectChildClass( ISkClassInfo aParent, ITsGuiContext aContext ) {
    TsNullArgumentRtException.checkNull( aContext );
    IDialogPanelCreator<ISkClassInfo, ITsGuiContext> creator = PanelClassInfoSelector::new;
    TsDialogInfo dlgInfo = new TsDialogInfo( aContext, DLG_C_CLASS_INFO, DLG_T_CLASS_INFO );
    dlgInfo.setMinSizeShellRelative( 28, 26 );
    dlgInfo.setMaxSizeShellRelative( 28, 26 );
    TsDialog<ISkClassInfo, ITsGuiContext> d = new TsDialog<>( dlgInfo, aParent, aContext, creator );
    return d.execData();
  }

}
