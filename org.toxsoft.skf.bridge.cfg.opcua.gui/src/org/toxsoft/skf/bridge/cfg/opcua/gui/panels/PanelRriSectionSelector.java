package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

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
import org.toxsoft.skf.rri.lib.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * Панель для выбора секции НСИ.
 * <p>
 *
 * @author dima
 */
public class PanelRriSectionSelector
    extends AbstractTsDialogPanel<ISkRriSection, ITsGuiContext> {

  ValedComboSelector<ISkRriSection> rriSectionsCombo;

  protected PanelRriSectionSelector( Composite aParent, TsDialog<ISkRriSection, ITsGuiContext> aOwnerDialog ) {
    super( aParent, aOwnerDialog );
    this.setLayout( new GridLayout( 2, false ) );
    CLabel l = new CLabel( this, SWT.LEFT );
    l.setText( "НСИ секции" );

    IList<ISkRriSection> values = new ElemArrayList<>();
    ITsVisualsProvider<ISkRriSection> visualsProvider = ISkRriSection::id;
    rriSectionsCombo = new ValedComboSelector<>( tsContext(), values, visualsProvider );
    rriSectionsCombo.createControl( this ).setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );
    // оставим пока для примера, может пригодится еще
    rriSectionsCombo.eventer().addListener( ( aSource, aEditFinished ) -> {
      ISkRriSection ci = rriSectionsCombo.selectedItem();
      if( ci != null ) {
        // TODO
      }
    } );
  }

  // ------------------------------------------------------------------------------------
  // AbstractTsDialogPanel
  //

  @Override
  protected void doSetDataRecord( ISkRriSection aData ) {
    IListEdit<ISkRriSection> sections = new ElemArrayList<>();
    ISkConnectionSupplier connSup = tsContext().get( ISkConnectionSupplier.class );
    ISkConnection conn = connSup.defConn();
    ISkRegRefInfoService rriService =
        (ISkRegRefInfoService)conn.coreApi().services().getByKey( ISkRegRefInfoService.SERVICE_ID );
    for( ISkRriSection sect : rriService.listSections() ) {
      sections.add( sect );
    }
    rriSectionsCombo.setItems( sections );
  }

  @Override
  protected ISkRriSection doGetDataRecord() {
    return rriSectionsCombo.getValue();
  }

  // ------------------------------------------------------------------------------------
  //
  //

  /**
   * Выбор секции НСИ из выпадающего списка.
   * <p>
   *
   * @param aDfltSection {@link ISkRriSection} - секция по умолчанию
   * @param aContext - контекст
   * @return ISkRriSection - выбранная секция или <b>null</b> в случает отказа от редактирования
   */
  public static final ISkRriSection selectRriSection( ISkRriSection aDfltSection, ITsGuiContext aContext ) {
    TsNullArgumentRtException.checkNull( aContext );
    IDialogPanelCreator<ISkRriSection, ITsGuiContext> creator = PanelRriSectionSelector::new;
    TsDialogInfo dlgInfo = new TsDialogInfo( aContext, "Секция НСИ", "Выберите секцию для размещения атрибутов." );
    dlgInfo.setMinSizeShellRelative( 28, 26 );
    dlgInfo.setMaxSizeShellRelative( 28, 26 );
    TsDialog<ISkRriSection, ITsGuiContext> d = new TsDialog<>( dlgInfo, aDfltSection, aContext, creator );
    return d.execData();
  }

}
