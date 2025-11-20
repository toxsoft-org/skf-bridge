package org.toxsoft.skf.bridge.cfg.modbus.gui.panels;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.utils.TsLibUtils.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.l10n.ISkBridgeCfgModbusGuiSharedResources.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.panels.IPackageConstants.*;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.gui.viewers.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tsgui.widgets.*;
import org.toxsoft.core.tsgui.widgets.contrib.*;
import org.toxsoft.core.tslib.av.list.*;
import org.toxsoft.core.tslib.bricks.filter.*;
import org.toxsoft.core.tslib.bricks.strid.more.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.utils.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.km5.sded.*;

/**
 * Editor panel for creating, editing, deleting MODBUS bridges configurations.
 * <p>
 * MODBUS bridge configurations are provided by {@link ModbusToS5CfgDocService#getCfgDocs()}. Instance of the service
 * {@link ModbusToS5CfgDocService} is in the application context.
 * <p>
 * FIXME {@link ModbusToS5CfgDocService} instance is created and put in context here, by this class. It is a <b>bad</b>
 * idea making this panel unusable twice (or more0 in a single application. All E4 service initializations must be
 * placed in plugin initialization code (somewhere in <code>AddonXxx</code> or <code>QuantXxx</code>).
 * <p>
 * Contains:
 * <ul>
 * <li>left panel - editable list of bridge configurations provided by
 * {@link ModbusToS5CfgDocService#getCfgDocs()};</li>
 * <li>right tab folder - opens editor of the bridge configuration selected in left panel in separate TabItem.</li>
 * </ul>
 *
 * @author max
 * @author dima
 */
public class ModbusToS5CfgDocEditorPanel
    extends TsPanel {

  final ISkConnection conn;

  IM5CollectionPanel<ModbusToS5CfgDoc> opcToS5DataCfgDocPanel;

  private CTabFolder tabFolder;

  private IMapEdit<ModbusToS5CfgDoc, CTabItem> tabItemsMap = new ElemMap<>();

  /**
   * Current selected IP address
   */
  private ModbusDevice      selAddress = ModbusDevice.DEFAULT_DEVICE;
  private LabelContribution filterAddressLabel;

  /**
   * Constructor.
   * <p>
   * Constructor stores reference to the context, does not creates copy.
   *
   * @param aParent {@link Composite} - parent component
   * @param aContext {@link ITsGuiContext} - the context
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public ModbusToS5CfgDocEditorPanel( Composite aParent, ITsGuiContext aContext ) {
    super( aParent, aContext );
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<ModbusToS5CfgDoc> model = m5.getModel( ModbusToS5CfgDocM5Model.MODEL_ID, ModbusToS5CfgDoc.class );

    ModbusToS5CfgDocService docService = new ModbusToS5CfgDocService( aContext );
    aContext.put( ModbusToS5CfgDocService.class, docService );

    IM5LifecycleManager<ModbusToS5CfgDoc> lm = new ModbusToS5CfgDocM5LifecycleManager( model, docService );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );

    // IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AV_TRUE );
    // IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
    // avValobj( EBorderLayoutPlacement.SOUTH ) );
    // IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AV_TRUE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_DBLCLICK_ACTION_ID.setValue( ctx.params(), avStr( ACTID_EDIT_UNITS ) );

    MultiPaneComponentModown<ModbusToS5CfgDoc> componentModown =
        new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm ) {

          @Override
          protected ITsToolbar doCreateToolbar( ITsGuiContext aContext2, String aName, EIconSize aIconSize,
              IListEdit<ITsActionDef> aActs ) {
            aActs.add( ACDEF_SEPARATOR );
            aActs.add( ACDEF_EDIT_UNITS );
            ITsToolbar toolbar = super.doCreateToolbar( aContext2, aName, aIconSize, aActs );
            return toolbar;
          }

          @Override
          protected void doProcessAction( String aActionId ) {
            ModbusToS5CfgDoc selDoc = selectedItem();
            switch( aActionId ) {
              case ACTID_EDIT_UNITS:
                editOpcCfgDoc( selDoc );
                break;
              default:
                break;
            }
          }
        };

    opcToS5DataCfgDocPanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );

    SashForm sf = new SashForm( this, SWT.HORIZONTAL );
    opcToS5DataCfgDocPanel.createControl( sf );

    tabFolder = new CTabFolder( sf, SWT.BORDER );
    tabFolder.setLayout( new BorderLayout() );

    sf.setWeights( 300, 500 );

  }

  protected void editOpcCfgDoc( ModbusToS5CfgDoc aSelDoc ) {
    // if configuration is already edited, activate item and return
    CTabItem tabItem = tabItemsMap.findByKey( aSelDoc );
    if( tabItem != null ) {
      tabFolder.setSelection( tabItem );
      return;
    }

    // create new tab item to edit configuration
    tabItem = new CTabItem( tabFolder, SWT.CLOSE );
    tabItemsMap.put( aSelDoc, tabItem );
    tabItem.addDisposeListener( e -> tabItemsMap.removeByKey( aSelDoc ) );

    tabItem.setText( aSelDoc.nmName() );
    tabItem.setToolTipText( aSelDoc.description() );

    TsComposite frame = new TsComposite( tabFolder );
    frame.setLayout( new BorderLayout() );

    tabItem.setControl( frame );
    tabFolder.setSelection( tabItem );

    CTabFolder tabSubFolder = new CTabFolder( frame, SWT.BORDER );
    tabSubFolder.setLayoutData( BorderLayout.CENTER );

    // Создаём закладку для конфигурации связей opc-sk
    CTabItem tabCfgUnitsItem = new CTabItem( tabSubFolder, SWT.NONE );
    tabCfgUnitsItem.setText( STR_TAB_DATA_BINDINGS );
    tabCfgUnitsItem.setToolTipText( STR_TAB_DATA_BINDINGS_D );

    // Создаём закладку IP адресов
    CTabItem tabIPAddrsItem = new CTabItem( tabSubFolder, SWT.NONE );
    tabIPAddrsItem.setText( STR_TAB_MODBUS_DEVICES );
    tabIPAddrsItem.setToolTipText( STR_TAB_MODBUS_DEVICES_D );

    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    ctx.params().addAll( tsContext().params() );

    TsToolbar toolBar = new TsToolbar( ctx );
    toolBar.setIconSize( EIconSize.IS_24X24 );
    toolBar.addActionDef( ACDEF_SAVE_DOC );

    Control toolbarCtrl = toolBar.createControl( frame );
    toolbarCtrl.setLayoutData( BorderLayout.NORTH );

    IM5Domain m5 = conn.scope().get( IM5Domain.class );

    toolBar.addListener( aActionId -> {
      switch( aActionId ) {
        case ACTID_SAVE_DOC: {
          ModbusToS5CfgDocService service = ctx.get( ModbusToS5CfgDocService.class );
          service.saveCfgDoc( aSelDoc );
          break;
        }
        default:
          break;
      }

    } );

    // ISkConnectionSupplier connSup = ctx.get( ISkConnectionSupplier.class );
    // установить по умолчанию s5 соединение рабочего пространства
    // IdChain defaultConnIdChain = connSup.getDefaultConnectionKey();
    IdChain defaultConnIdChain = ISkConnectionSupplier.DEF_CONN_ID;
    ctx.put( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_S5_CONNECTION, defaultConnIdChain );

    // Модель cвязи Gwid -> Modbus register
    IM5Model<OpcToS5DataCfgUnit> linksModel =
        m5.getModel( OpcToS5DataCfgUnitM5Model.MODEL_ID_TEMPLATE + ".modbus", OpcToS5DataCfgUnit.class ); //$NON-NLS-1$

    // IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AV_TRUE );
    // IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
    // avValobj( EBorderLayoutPlacement.SOUTH ) );
    // IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AV_TRUE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_REORDER.setValue( ctx.params(), AV_TRUE );
    IM5LifecycleManager<OpcToS5DataCfgUnit> linksLm = new ModbusToS5CfgUnitM5LifecycleManager( linksModel, aSelDoc );

    MultiPaneComponentModown<OpcToS5DataCfgUnit> linksMpc =
        new MultiPaneComponentModown<>( ctx, linksModel, linksLm.itemsProvider(), linksLm ) {

          @Override
          protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
              IListEdit<ITsActionDef> aActs ) {
            // add func create copy
            int index = 1 + aActs.indexOf( ACDEF_ADD );
            aActs.insert( index, ACDEF_ADD_COPY );
            aActs.add( ACDEF_SEPARATOR );
            aActs.add( OpcToS5DataCfgDocEditorPanel.ACDEF_GENERATE_FILE );
            aActs.add( ACDEF_SEPARATOR );
            aActs.add( ACDEF_COPY_ALL );
            aActs.add( ACDEF_FILTER_BY_ADDRESS );
            aActs.add( ACDEF_SEPARATOR );
            // aActs.add( ACDEF_LIST_ELEM_MOVE_UP );
            // aActs.add( ACDEF_LIST_ELEM_MOVE_DOWN );

            ITsToolbar toolbar = super.doCreateToolbar( aContext, aName, aIconSize, aActs );

            toolbar.addListener( aActionId -> {
              // nop
            } );

            toolbar.setIconSize( EIconSize.IS_24X24 );
            return toolbar;
          }

          @Override
          protected void doProcessAction( String aActionId ) {

            switch( aActionId ) {
              case OpcToS5DataCfgDocEditorPanel.ACTID_GENERATE_FILE:
                ((ModbusToS5CfgUnitM5LifecycleManager)lifecycleManager()).generateDlmFileFromCurrState( ctx );
                ((ModbusToS5CfgUnitM5LifecycleManager)lifecycleManager()).generateDevFileFromCurrState( ctx );
                break;
              case ACTID_FILTER_BY_ADDRESS:
                boolean checked = toolbar().getAction( ACTID_FILTER_BY_ADDRESS ).isChecked();
                if( checked ) {
                  // select IP
                  ModbusDevice address = PanelModbusDeviceSelector.selectModbusDevice( tsContext(), selAddress );
                  if( address == null ) {
                    toolbar().getAction( ACTID_FILTER_BY_ADDRESS ).setChecked( false );
                  }
                  else {
                    setNewIPAddress( tree(), address );
                  }
                }
                else {
                  clearFilter( tree() );
                }
                break;
              case ACTID_COPY_ALL: {
                // получим отфильтрованные элементы
                IList<OpcToS5DataCfgUnit> list2Copy = tree().filterManager().items();
                IListEdit<OpcToS5DataCfgUnit> newItems = new ElemArrayList<>();
                if( list2Copy.size() == 0 ) {
                  break;
                }
                // popup dialogs to select new IP
                ModbusDevice newAddress = PanelModbusDeviceSelector.selectModbusDevice( ctx, selAddress );
                if( newAddress == null ) {
                  break;
                }
                Skid newSkid = selectSkid( list2Copy.first().getDataGwids().first().skid(), ctx );
                if( newSkid == null ) {
                  break;
                }
                for( OpcToS5DataCfgUnit sel : list2Copy ) {
                  IM5BunchEdit<OpcToS5DataCfgUnit> initVals = new M5BunchEdit<>( model() );
                  initVals.fillFrom( sel, false );
                  // новый strid
                  initVals.set( OpcToS5DataCfgUnitM5Model.FID_STRID,
                      avStr( ModbusToS5CfgUnitM5LifecycleManager.generateStrid() ) );
                  IList<Gwid> gwids = initVals.get( OpcToS5DataCfgUnitM5Model.FID_GWIDS );
                  IListEdit<Gwid> newGwids = new ElemArrayList<>();
                  for( Gwid gwid4Copy : gwids ) {
                    Gwid newGwid = Gwid.createRtdata( newSkid.classId(), newSkid.strid(), gwid4Copy.propId() );
                    newGwids.add( newGwid );
                  }
                  initVals.set( OpcToS5DataCfgUnitM5Model.FID_GWIDS, newGwids );
                  // обновляем IP
                  IList<ModbusNode> nodes = OpcUaUtils.convertToNodesList( sel.getDataNodes2() );
                  IListEdit<ModbusNode> newNodes = new ElemArrayList<>();
                  for( ModbusNode node : nodes ) {
                    ModbusNode newNode = new ModbusNode( newAddress, node.getRegister(), node.getWordsCount(),
                        node.getValueType(), node.getRequestType() );
                    newNodes.add( newNode );
                  }
                  initVals.set( OpcToS5DataCfgUnitM5Model.FID_NODES, convertToAtomicList( newNodes ) );
                  // create new item
                  OpcToS5DataCfgUnit item = lifecycleManager().create( initVals );
                  newItems.add( item );
                }
                // добавляем в список и переключаем в новый фильтр
                fillViewer( newItems.first() );
                setNewIPAddress( tree(), newAddress );
                break;
              }
              case ACTID_ADD_COPY: {
                OpcToS5DataCfgUnit selected = tree().selectedItem();
                ITsDialogInfo cdi = doCreateDialogInfoToAddItem();
                IM5BunchEdit<OpcToS5DataCfgUnit> initVals = new M5BunchEdit<>( model() );
                initVals.fillFrom( selected, false );
                // новый strid
                initVals.set( OpcToS5DataCfgUnitM5Model.FID_STRID,
                    avStr( ModbusToS5CfgUnitM5LifecycleManager.generateStrid() ) );

                OpcToS5DataCfgUnit item =
                    M5GuiUtils.askCreate( tsContext(), model(), initVals, cdi, lifecycleManager() );
                if( item != null ) {
                  fillViewer( item );
                }
                break;
              }

              default:
                break;
            }
          }

        };

    IM5CollectionPanel<OpcToS5DataCfgUnit> opcToS5DataCfgUnitPanel =
        new M5CollectionPanelMpcModownWrapper<>( linksMpc, false );

    // Model of IP address
    IM5Model<ModbusDevice> ipAddresessModel = m5.getModel( ModbusDeviceM5Model.MODEL_ID, ModbusDevice.class );
    ModbusToS5CfgDocService service = ctx.get( ModbusToS5CfgDocService.class );
    IM5LifecycleManager<ModbusDevice> ipAddressLm = new ModbusDeviceM5LifecycleManager( ipAddresessModel, service );

    tabCfgUnitsItem.setControl( opcToS5DataCfgUnitPanel.createControl( tabSubFolder ) );
    // add label to display selected IP
    filterAddressLabel = new LabelContribution( "filterAddressLabelId", 200, EMPTY_STRING, SWT.NONE ); //$NON-NLS-1$
    linksMpc.toolbar().addContributionItem( filterAddressLabel );
    clearFilter( linksMpc.tree() );

    // create IP adddreses panel
    MultiPaneComponentModown<ModbusDevice> ipAddrsMpc =
        new MultiPaneComponentModown<>( ctx, ipAddresessModel, ipAddressLm.itemsProvider(), ipAddressLm ) {

          @Override
          protected ITsToolbar doCreateToolbar( ITsGuiContext aaContext, String aName, EIconSize aIconSize,
              IListEdit<ITsActionDef> aActs ) {
            int index = 1 + aActs.indexOf( ACDEF_ADD );
            aActs.insert( index, ACDEF_ADD_COPY );

            aActs.add( ACDEF_SEPARATOR );
            ITsToolbar toolbar = super.doCreateToolbar( aaContext, aName, aIconSize, aActs );

            toolbar.addListener( aActionId -> {
              // nop
            } );

            toolbar.setIconSize( EIconSize.IS_24X24 );
            return toolbar;
          }

          @Override
          protected void doProcessAction( String aActionId ) {

            switch( aActionId ) {
              case ACTID_ADD_COPY: {
                ModbusDevice selected = tree().selectedItem();
                ITsDialogInfo cdi = doCreateDialogInfoToAddItem();
                IM5BunchEdit<ModbusDevice> initVals = new M5BunchEdit<>( model() );
                initVals.fillFrom( selected, false );
                // новый strid
                initVals.set( ModbusDeviceM5Model.ID.id(), avStr( ModbusDeviceM5LifecycleManager.generateStrid() ) );

                ModbusDevice item = M5GuiUtils.askCreate( tsContext(), model(), initVals, cdi, lifecycleManager() );
                if( item != null ) {
                  fillViewer( item );
                }
                break;
              }

              default:
                throw new TsNotAllEnumsUsedRtException( aActionId );
            }
          }
        };

    IM5CollectionPanel<ModbusDevice> ipAddrsPanel = new M5CollectionPanelMpcModownWrapper<>( ipAddrsMpc, false );
    tabIPAddrsItem.setControl( ipAddrsPanel.createControl( tabSubFolder ) );

    // select links tab
    tabSubFolder.setSelection( tabCfgUnitsItem );
  }

  static IAvList convertToAtomicList( IList<ModbusNode> aNodeList ) {
    IAvListEdit result = new AvList( new ElemArrayList<>() );
    for( ModbusNode node : aNodeList ) {
      result.add( avValobj( node ) );
    }
    return result;
  }

  /**
   * FIXME copy paste из класса Выводит диалог выбора Skid.
   * <p>
   *
   * @param aInitSkid {@link Skid} для инициализации
   * @param aContext {@link ITsGuiContext} - контекст
   * @return Skid - выбранный объект или <b>null</b> в случает отказа от выбора
   */
  Skid selectSkid( Skid aInitSkid, ITsGuiContext aContext ) {
    TsNullArgumentRtException.checkNull( aContext );
    IM5Domain m5 = conn.scope().get( IM5Domain.class );

    IM5Model<ISkObject> modelSk = m5.getModel( IKM5SdedConstants.MID_SDED_SK_OBJECT, ISkObject.class );
    IM5LifecycleManager<ISkObject> lmSk = modelSk.getLifecycleManager( conn );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    TsDialogInfo di = new TsDialogInfo( ctx, STR_SELECT_MULTI_COPY_DEST_SKID, STR_SELECT_MULTI_COPY_DEST_SKID_D );
    ISkObject initObj = aInitSkid == null ? null : conn.coreApi().objService().get( aInitSkid );
    ISkObject selObj = M5GuiUtils.askSelectItem( di, modelSk, initObj, lmSk.itemsProvider(), lmSk );
    if( selObj != null ) {
      return selObj.skid();
    }
    return Skid.NONE;
  }

  void setNewIPAddress( IM5TreeViewer<OpcToS5DataCfgUnit> aIm5TreeViewer, ModbusDevice address ) {
    if( address != null ) {
      // create new filter
      ITsFilter<OpcToS5DataCfgUnit> filter = new FilterByModbusDevice( address );
      aIm5TreeViewer.filterManager().setFilter( filter );
      selAddress = address;
      filterAddressLabel.label().setText( String.format( FMT_FILTERED_DEVICE_ADDRESS, address.nmName() ) );
      filterAddressLabel.label().setToolTipText( String.format( FMT_FILTERED_DEVICE_ADDRESS_D, address.nmName() ) );
    }

  }

  void clearFilter( IM5TreeViewer<OpcToS5DataCfgUnit> aTreeViewer ) {
    aTreeViewer.filterManager().setFilter( ITsFilter.ALL );
    filterAddressLabel.label().setText( EMPTY_STRING );
    filterAddressLabel.label().setToolTipText( EMPTY_STRING );
  }

}
