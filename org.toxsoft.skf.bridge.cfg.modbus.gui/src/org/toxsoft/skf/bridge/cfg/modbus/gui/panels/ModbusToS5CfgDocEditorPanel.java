package org.toxsoft.skf.bridge.cfg.modbus.gui.panels;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.panels.ISkResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

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
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.list.*;
import org.toxsoft.core.tslib.bricks.filter.*;
import org.toxsoft.core.tslib.bricks.strid.more.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.utils.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skide.plugin.exconn.service.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.km5.sded.*;

/**
 * Editor panel for creating, editing, deleting modbus to s5 cfg docs.
 *
 * @author max
 * @author dima
 */
public class ModbusToS5CfgDocEditorPanel
    extends TsPanel {

  final static String ACTID_EDIT_UNITS = SK_ID + ".users.gui.EditUnits"; //$NON-NLS-1$

  final static String ACTID_EDIT_NODES = SK_ID + ".users.gui.EditNodes"; //$NON-NLS-1$

  final static TsActionDef ACDEF_EDIT_UNITS =
      TsActionDef.ofPush2( ACTID_EDIT_UNITS, STR_N_EDIT_CONFIG_SET, STR_D_EDIT_CONFIG_SET, ICONID_EDIT_UNITS );

  final static TsActionDef ACDEF_EDIT_NODES =
      TsActionDef.ofPush2( ACTID_EDIT_NODES, STR_N_EDIT_OPC_UA_NODES, STR_D_EDIT_OPC_UA_NODES, ICONID_EDIT_NODES );

  final static String ACTID_SAVE_DOC = SK_ID + "bridge.cfg.opcua.to.s5.save.doc"; //$NON-NLS-1$

  final static TsActionDef ACDEF_SAVE_DOC =
      TsActionDef.ofPush2( ACTID_SAVE_DOC, STR_N_SAVE_CONFIG, STR_D_SAVE_CONFIG, ICONID_SAVE_DOC );

  final static String ACTID_S5_SERVER_SELECT = SK_ID + "bridge.cfg.opcua.to.s5.server.select"; //$NON-NLS-1$

  final static String ACTID_OPC_SERVER_SELECT = SK_ID + "bridge.cfg.opcua.to.s5.opc.server.select"; //$NON-NLS-1$

  final static String ACTID_IP_ADDRESS_SELECT = SK_ID + "bridge.cfg.modbus.ip.address.select"; //$NON-NLS-1$

  final static String ACTID_COPY_ALL = SK_ID + "bridge.cfg.modbus.copy.all"; //$NON-NLS-1$

  final static String ACTID_UP_SEL_ITEM   = SK_ID + "bridge.cfg.modbus.up.selected.item";   //$NON-NLS-1$
  final static String ACTID_DOWN_SEL_ITEM = SK_ID + "bridge.cfg.modbus.down.selected.item"; //$NON-NLS-1$

  final static TsActionDef ACDEF_S5_SERVER_SELECT = TsActionDef.ofPush2( ACTID_S5_SERVER_SELECT, STR_N_SELECT_S5_SERVER,
      STR_D_SELECT_S5_SERVER, ICONID_S5_SERVER_SELECT );

  final static TsActionDef ACDEF_IP_ADDRESS_SELECT = TsActionDef.ofCheck2( ACTID_IP_ADDRESS_SELECT,
      ISkResources.STR_N_SELECT_IP_ADDRESS, ISkResources.STR_D_SELECT_IP_ADDRESS, ICONID_FILTER );

  final static TsActionDef ACDEF_OPC_SERVER_SELECT = TsActionDef.ofPush2( ACTID_OPC_SERVER_SELECT,
      STR_N_SELECT_OPC_UA_SERVER, STR_D_SELECT_OPC_UA_SERVER, ICONID_OPC_SERVER_SELECT );

  final static TsActionDef ACDEF_COPY_ALL = TsActionDef.ofPush2( ACTID_COPY_ALL, ISkResources.STR_N_COPY_ALL,
      ISkResources.STR_D_COPY_ALL, ITsStdIconIds.ICONID_LIST_ADD_ALL );

  final static TsActionDef ACDEF_UP_SEL_ITEM = TsActionDef.ofPush2( ACTID_UP_SEL_ITEM, STR_N_UP_SELECTED_ITEM,
      STR_D_UP_SELECTED_ITEM, ITsStdIconIds.ICONID_ARROW_UP );

  final static TsActionDef ACDEF_DOWN_SEL_ITEM = TsActionDef.ofPush2( ACTID_DOWN_SEL_ITEM, STR_N_DOWN_SELECTED_ITEM,
      STR_D_DOWN_SELECTED_ITEM, ITsStdIconIds.ICONID_ARROW_DOWN );

  final ISkConnection conn;

  IM5CollectionPanel<ModbusToS5CfgDoc> opcToS5DataCfgDocPanel;

  private CTabFolder tabFolder;

  private TextControlContribution textContr1;

  /**
   * Current selected IP address
   */
  private ModbusDevice            selAddress = ModbusDevice.DEFAULT_DEVICE;
  private TextControlContribution selAddressTextContr;

  /**
   * Конструктор панели.
   * <p>
   * Конструктор просто запоминает ссылку на контекст, без создания копии.
   *
   * @param aParent {@link Composite} - родительская панель
   * @param aContext {@link ITsGuiContext} - контекст панели
   * @throws TsNullArgumentRtException любой аргумент = null
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

    // IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
    // avValobj( EBorderLayoutPlacement.SOUTH ) );
    // IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    // IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );

    MultiPaneComponentModown<ModbusToS5CfgDoc> componentModown =
        new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm ) {

          @Override
          protected ITsToolbar doCreateToolbar( ITsGuiContext aContext2, String aName, EIconSize aIconSize,
              IListEdit<ITsActionDef> aActs ) {
            aActs.add( ACDEF_SEPARATOR );
            aActs.add( ACDEF_EDIT_UNITS );
            // aActs.add( ACDEF_EDIT_NODES );

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
            ModbusToS5CfgDoc selDoc = selectedItem();

            switch( aActionId ) {

              case ACTID_EDIT_UNITS:
                editOpcCfgDoc( selDoc );
                break;

              case ACTID_EDIT_NODES:
                // editOpcCfgNodes( selDoc );
                break;

              default:
                throw new TsNotAllEnumsUsedRtException( aActionId );
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

    // создаем новую общую закладку закладку
    CTabItem tabItem = new CTabItem( tabFolder, SWT.CLOSE );
    tabItem.setText( aSelDoc.nmName() );

    TsComposite frame = new TsComposite( tabFolder );
    frame.setLayout( new BorderLayout() );

    tabItem.setControl( frame );
    tabFolder.setSelection( tabItem );

    CTabFolder tabSubFolder = new CTabFolder( frame, SWT.BORDER );
    tabSubFolder.setLayoutData( BorderLayout.CENTER );

    // Создаём закладку для конфигурации связей opc-sk
    CTabItem tabCfgUnitsItem = new CTabItem( tabSubFolder, SWT.NONE );
    tabCfgUnitsItem.setText( STR_LINKS );

    // Создаём закладку IP адресов
    CTabItem tabIPAddrsItem = new CTabItem( tabSubFolder, SWT.NONE );
    tabIPAddrsItem.setText( ISkResources.STR_IP_ADDRESES );

    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    ctx.params().addAll( tsContext().params() );

    TsToolbar toolBar = new TsToolbar( ctx );
    toolBar.setIconSize( EIconSize.IS_24X24 );
    toolBar.addActionDef( ACDEF_SAVE_DOC );
    toolBar.addActionDef( ACDEF_S5_SERVER_SELECT );

    toolBar.addSeparator();

    Control toolbarCtrl = toolBar.createControl( frame );
    toolbarCtrl.setLayoutData( BorderLayout.NORTH );

    textContr1 = new TextControlContribution( "Label", 200, STR_SK_CONN_DESCR, SWT.NONE ); //$NON-NLS-1$
    toolBar.addContributionItem( textContr1 );

    IM5Domain m5 = conn.scope().get( IM5Domain.class );

    toolBar.addListener( aActionId -> {
      if( aActionId.equals( ACDEF_SAVE_DOC.id() ) ) {
        ModbusToS5CfgDocService service = ctx.get( ModbusToS5CfgDocService.class );
        service.saveCfgDoc( aSelDoc );
        return;
      }
      if( aActionId.equals( ACDEF_S5_SERVER_SELECT.id() ) ) {
        ISkideExternalConnectionsService connService =
            ctx.eclipseContext().get( ISkideExternalConnectionsService.class );
        IdChain idChain = connService.selectConfigAndOpenConnection( ctx );
        if( idChain != null ) {
          ctx.put( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_S5_CONNECTION, idChain );
          textContr1.setText( STR_SK_CONN_DESCR + idChain.first() );
        }

        return;
      }

    } );

    // ISkConnectionSupplier connSup = ctx.get( ISkConnectionSupplier.class );
    // установить по умолчанию s5 соединение рабочего пространства
    // IdChain defaultConnIdChain = connSup.getDefaultConnectionKey();
    IdChain defaultConnIdChain = ISkConnectionSupplier.DEF_CONN_ID;
    ctx.put( OpcToS5DataCfgUnitM5Model.OPCUA_BRIDGE_CFG_S5_CONNECTION, defaultConnIdChain );
    String defConnName = defaultConnIdChain.first() != null ? defaultConnIdChain.first() : STR_DEFAULT_WORKROOM_SK_CONN;
    textContr1.setText( STR_SK_CONN_DESCR + defConnName );

    // Модель cвязи Gwid -> Modbus register
    IM5Model<OpcToS5DataCfgUnit> linksModel =
        m5.getModel( OpcToS5DataCfgUnitM5Model.MODEL_ID_TEMPLATE + ".modbus", OpcToS5DataCfgUnit.class ); //$NON-NLS-1$

    // IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
    // avValobj( EBorderLayoutPlacement.SOUTH ) );
    // IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
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
            aActs.add( ACDEF_IP_ADDRESS_SELECT );
            aActs.add( ACDEF_SEPARATOR );
            aActs.add( ACDEF_UP_SEL_ITEM );
            aActs.add( ACDEF_DOWN_SEL_ITEM );

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
              case ACTID_IP_ADDRESS_SELECT:
                boolean checked = toolbar().getAction( ACTID_IP_ADDRESS_SELECT ).isChecked();
                if( checked ) {
                  // select IP
                  ModbusDevice address = PanelModbusDeviceSelector.selectModbusDevice( tsContext(), selAddress );
                  if( address == null ) {
                    toolbar().getAction( ACTID_IP_ADDRESS_SELECT ).setChecked( false );
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
              case ACTID_UP_SEL_ITEM: {
                OpcToS5DataCfgUnit selected = tree().selectedItem();
                ModbusToS5CfgUnitM5LifecycleManager lm = (ModbusToS5CfgUnitM5LifecycleManager)lifecycleManager();
                int currIndex = lm.getListEditEntities().indexOf( selected );
                if( currIndex - 1 >= 0 ) {
                  lm.getListEditEntities().removeByIndex( currIndex );
                  lm.getListEditEntities().insert( --currIndex, selected );
                  fillViewer( selected );
                }
                break;
              }

              case ACTID_DOWN_SEL_ITEM: {
                OpcToS5DataCfgUnit selected = tree().selectedItem();
                ModbusToS5CfgUnitM5LifecycleManager lm = (ModbusToS5CfgUnitM5LifecycleManager)lifecycleManager();
                int currIndex = lm.getListEditEntities().indexOf( selected );
                if( currIndex + 1 < lm.getListEditEntities().size() ) {
                  lm.getListEditEntities().removeByIndex( currIndex );
                  lm.getListEditEntities().insert( ++currIndex, selected );
                  fillViewer( selected );
                }
                break;
              }

              default:
                throw new TsNotAllEnumsUsedRtException( aActionId );
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
    // add label to dispale selected IP
    selAddressTextContr =
        new TextControlContribution( "selAddressTextContrId", 200, ISkResources.STR_SEL_IP_ADDRESS, SWT.NONE ); //$NON-NLS-1$
    linksMpc.toolbar().addContributionItem( selAddressTextContr );
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
    TsDialogInfo di = new TsDialogInfo( ctx, DLG_T_SKID_SEL, STR_MSG_SKID_SELECTION );
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
      selAddressTextContr.setText( ISkResources.STR_SEL_IP_ADDRESS + address.nmName() );
    }

  }

  void clearFilter( IM5TreeViewer<OpcToS5DataCfgUnit> aTreeViewer ) {
    aTreeViewer.filterManager().setFilter( ITsFilter.ALL );
    selAddressTextContr.setText( TsLibUtils.EMPTY_STRING );
  }

}
