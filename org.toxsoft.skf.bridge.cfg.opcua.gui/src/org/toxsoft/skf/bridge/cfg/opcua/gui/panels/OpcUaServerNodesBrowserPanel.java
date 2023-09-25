package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.core.tslib.gw.IGwHardConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.sdk.core.nodes.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.tsnodes.*;
import org.toxsoft.core.tsgui.bricks.tstree.tmm.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.events.change.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.gw.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.km5.sded.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Browser of opc ua server nodes.
 *
 * @author max
 */
public class OpcUaServerNodesBrowserPanel
    extends TsPanel {

  /**
   * id параметра события: старое значение.
   * <p>
   * Параметр имеет тип {@link EAtomicType#FLOATING}.
   */
  static String EVPID_OLD_VAL = "oldVal"; //$NON-NLS-1$

  /**
   * Параметр события: старое значение.
   * <p>
   * Параметр имеет тип {@link EAtomicType#FLOATING}.
   */
  static DataDef EVPDD_OLD_VAL_FLOAT =
      DataDef.create( EVPID_OLD_VAL, EAtomicType.FLOATING, TSID_NAME, STR_N_EV_PARAM_OLD_VAL, //
          TSID_DESCRIPTION, STR_D_EV_PARAM_OLD_VAL, //
          TSID_IS_NULL_ALLOWED, AV_TRUE );

  /**
   * /** Параметр события: старое значение.
   * <p>
   * Параметр имеет тип {@link EAtomicType#INTEGER}.
   */
  static DataDef EVPDD_OLD_VAL_INT =
      DataDef.create( EVPID_OLD_VAL, EAtomicType.INTEGER, TSID_NAME, STR_N_EV_PARAM_OLD_VAL, //
          TSID_DESCRIPTION, STR_D_EV_PARAM_OLD_VAL, //
          TSID_IS_NULL_ALLOWED, AV_TRUE );

  /**
   * id параметра события: новое значение.
   */

  static String EVPID_NEW_VAL = "newVal"; //$NON-NLS-1$

  /**
   * Параметр события: новое значение.
   * <p>
   * Параметр имеет тип {@link EAtomicType#FLOATING}.
   */
  static DataDef EVPDD_NEW_VAL_FLOAT =
      DataDef.create( EVPID_NEW_VAL, EAtomicType.FLOATING, TSID_NAME, STR_N_EV_PARAM_NEW_VAL, //
          TSID_DESCRIPTION, STR_D_EV_PARAM_NEW_VAL, //
          TSID_IS_NULL_ALLOWED, AV_FALSE, //
          TSID_DEFAULT_VALUE, AV_STR_EMPTY );

  /**
   * Параметр события: новое значение.
   * <p>
   * Параметр имеет тип {@link EAtomicType#INTEGER}.
   */
  static DataDef EVPDD_NEW_VAL_INT =
      DataDef.create( EVPID_NEW_VAL, EAtomicType.INTEGER, TSID_NAME, STR_N_EV_PARAM_NEW_VAL, //
          TSID_DESCRIPTION, STR_D_EV_PARAM_NEW_VAL, //
          TSID_IS_NULL_ALLOWED, AV_FALSE, //
          TSID_DEFAULT_VALUE, AV_STR_EMPTY );

  /**
   * Параметр события: включен.
   * <p>
   * Параметр имеет тип {@link EAtomicType#BOOLEAN}.
   */
  static String EVPID_ON = "on"; //$NON-NLS-1$

  /**
   * Параметр события: on.
   * <p>
   * Параметр имеет тип {@link EAtomicType#BOOLEAN}.
   */
  static DataDef EVPDD_ON = DataDef.create( EVPID_ON, EAtomicType.BOOLEAN, TSID_NAME, STR_N_EV_PARAM_ON, //
      TSID_DESCRIPTION, STR_D_EV_PARAM_ON, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, AV_FALSE );

  private final ISkConnection conn;
  static private String       RTD_PREFIX = "rtd"; //$NON-NLS-1$
  static private String       EVT_PREFIX = "evt"; //$NON-NLS-1$

  private IM5CollectionPanel<UaTreeNode> opcUaNodePanel;
  /**
   * Панель подробного просмотра
   */
  private UaVariableNodeListPanel        inspectorPanel;

  /**
   * Собственно клиент OPC UA сервера
   */
  private OpcUaClient client;
  private UaTreeNode  selectedNode = null;

  /**
   * Items provider for ISkObject created on OPC UA node.
   *
   * @author dima
   */
  static class OpcUANode2SkObjectItemsProvider
      implements IM5ItemsProvider<IDtoObject> {

    private IListEdit<IDtoObject> items = new ElemArrayList<>();
    private final ISkCoreApi      coreApi;

    // Создаем IDpuObject и инициализируем его значениями из узла
    private IDtoObject makeObjDto( String aClassId, UaNode aObjNode ) {
      String id = aObjNode.getDisplayName().getText();
      Skid skid = new Skid( aClassId, id );
      DtoObject dtoObj = DtoObject.createDtoObject( skid, coreApi );
      dtoObj.attrs().setValue( FID_NAME, AvUtils.avStr( aObjNode.getDescription().getText() ) );
      dtoObj.attrs().setValue( FID_DESCRIPTION, AvUtils.avStr( aObjNode.getDescription().getText() ) );
      return dtoObj;
    }

    OpcUANode2SkObjectItemsProvider( IList<UaNode> aSelectedNodes, ISkCoreApi aSkCoreApi,
        ISkClassInfo aSelectedClassInfo ) {
      coreApi = aSkCoreApi;
      for( UaNode objNode : aSelectedNodes ) {
        items.add( makeObjDto( aSelectedClassInfo.id(), objNode ) );
      }
    }

    @Override
    public IGenericChangeEventer genericChangeEventer() {
      return NoneGenericChangeEventer.INSTANCE;
    }

    @Override
    public IList<IDtoObject> listItems() {
      return items;
    }

  }

  /**
   * Constructor
   *
   * @param aParent Composite - parent component.
   * @param aContext ITsGuiContext - context.
   * @param aOpcUaServerConnCfg IOpcUaServerConnCfg - opc ua server connection configuration.
   */
  public OpcUaServerNodesBrowserPanel( Composite aParent, ITsGuiContext aContext,
      IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    super( aParent, aContext );
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<UaTreeNode> model = m5.getModel( OpcUaNodeModel.MODEL_ID, UaTreeNode.class );

    try {
      client = OpcUaUtils.createClient( aOpcUaServerConnCfg );
      client.connect().get();
    }
    catch( Exception ex ) {
      LoggerUtils.errorLogger().error( ex );
      return;
    }

    IM5LifecycleManager<UaTreeNode> lm = new OpcUaNodeM5LifecycleManager( model, client );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_FALSE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // обнуляем действие по умолчанию на dbl click
    IMultiPaneComponentConstants.OPDEF_DBLCLICK_ACTION_ID.setValue( ctx.params(), AvUtils.AV_STR_EMPTY );

    MultiPaneComponentModown<UaTreeNode> componentModown =
        new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm ) {

          // добавляем tool bar
          @Override
          protected ITsToolbar doCreateToolbar( @SuppressWarnings( "hiding" ) ITsGuiContext aContext, String aName,
              EIconSize aIconSize, IListEdit<ITsActionDef> aActs ) {
            aActs.add( ITsStdActionDefs.ACDEF_SEPARATOR );
            aActs.add( IOpcUaServerConnCfgConstants.createClass_OPC_UA_Item );
            aActs.add( IOpcUaServerConnCfgConstants.createObjs_OPC_UA_Item );

            ITsToolbar toolBar = super.doCreateToolbar( aContext, aName, aIconSize, aActs );

            toolBar.addListener( aActionId -> {
              if( aActionId == CREATE_CINFO_FROM_OPC_UA_ACT_ID ) {
                createClassFromNodes( aContext );
              }

              if( aActionId == CREATE_OBJS_FROM_OPC_UA_ACT_ID ) {
                createObjsFromNodes( aContext );
              }

            } );
            toolBar.setIconSize( EIconSize.IS_16X16 );
            return toolBar;
          }

        };
    UaNodesTreeMaker treeMaker = new UaNodesTreeMaker();

    componentModown.tree().setTreeMaker( treeMaker );

    componentModown.treeModeManager().addTreeMode( new TreeModeInfo<>( TMID_GROUP_BY_OPC_UA_ORIGIN,
        STR_N_BY_OPC_NODES_STRUCT, STR_D_BY_OPC_NODES_STRUCT, null, treeMaker ) );
    componentModown.treeModeManager().setCurrentMode( TMID_GROUP_BY_OPC_UA_ORIGIN );

    componentModown.addTsDoubleClickListener( ( aSource, aSelectedItem ) -> {

      if( aSelectedItem != null ) {
        UaNode selNode = aSelectedItem.getUaNode();
        // реагируем только на тип UaVariableNode
        if( selNode instanceof VariableNode ) {
          UaVariableNode variableNode = (UaVariableNode)selNode;
          inspectorPanel.addNode( variableNode );
        }
      }
    } );

    opcUaNodePanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );
    opcUaNodePanel.createControl( this );
    // пока не выбран ни один узел, отключаем
    componentModown.toolbar().getAction( CREATE_CINFO_FROM_OPC_UA_ACT_ID ).setEnabled( false );
    componentModown.toolbar().getAction( CREATE_OBJS_FROM_OPC_UA_ACT_ID ).setEnabled( false );
    componentModown.addTsSelectionListener( ( aSource, aSelectedItem ) -> {
      // просто активируем кнопки создания/обнолвения классов/объектов
      boolean enableCreateBttns = (aSelectedItem != null);
      componentModown.toolbar().getAction( CREATE_CINFO_FROM_OPC_UA_ACT_ID ).setEnabled( enableCreateBttns );
      componentModown.toolbar().getAction( CREATE_OBJS_FROM_OPC_UA_ACT_ID ).setEnabled( enableCreateBttns );
      if( aSelectedItem != null ) {
        selectedNode = aSelectedItem;
      }

    } );

  }

  private static class UaNodesTreeMaker
      implements ITsTreeMaker<UaTreeNode> {

    private final ITsNodeKind<UaTreeNode> kind =
        new TsNodeKind<>( "UaTreeNode", UaTreeNode.class, true, IOpcUaServerConnCfgConstants.ICONID_APP_ICON ); //$NON-NLS-1$

    @Override
    public IList<ITsNode> makeRoots( ITsNode aRootNode, IList<UaTreeNode> aItems ) {

      IListEdit<ITsNode> result = new ElemArrayList<>();
      IListEdit<UaTreeNode> roots = new ElemArrayList<>();

      for( UaTreeNode uaTreeNode : aItems ) {
        UaTreeNode parent = uaTreeNode;
        while( parent.getParent() != null ) {
          parent = parent.getParent();
        }

        if( roots.hasElem( parent ) ) {
          continue;
        }

        roots.add( parent );

        DefaultTsNode<UaTreeNode> rootNode = new DefaultTsNode<>( kind, aRootNode, parent );

        formTree( rootNode );
        result.add( rootNode );
      }

      return result;
    }

    private void formTree( DefaultTsNode<UaTreeNode> aParentNode ) {
      for( UaTreeNode child : aParentNode.entity().getChildren() ) {
        DefaultTsNode<UaTreeNode> childNode = new DefaultTsNode<>( kind, aParentNode, child );
        if( childNode.entity().getUaNode() instanceof VariableNode ) {
          // FIXME иконки не отображаются, выяснить у Гоги пачему
          childNode.setIconId( ICONID_VARIABLE_NODE );
        }
        if( childNode.entity().getUaNode() instanceof ObjectNode ) {
          childNode.setIconId( ICONID_OBJECT_NODE );
        }
        aParentNode.addNode( childNode );
        formTree( childNode );
      }
    }

    @Override
    public boolean isItemNode( ITsNode aNode ) {
      return true;
    }

  }

  /**
   * Установить панель подробного просмотра
   *
   * @param aNodesInspector панель инспектора
   */
  public void setInspector( UaVariableNodeListPanel aNodesInspector ) {
    inspectorPanel = aNodesInspector;
  }

  /**
   * Получить клиента для работы с OPC UA сервером
   *
   * @return {@link OpcUaClient} клиент для работы с OPC UA сервером
   */
  public OpcUaClient getOpcUaClient() {
    return client;
  }

  private void createClassFromNodes( ITsGuiContext aContext ) {
    // создать класс из информации об UaNode
    IList<UaNode> selNodes =
        OpcUaNodesSelector.selectUaNodes4Class( aContext, selectedNode.getUaNode().getNodeId(), client );
    if( selNodes != null ) {
      // создаем описание класса из списка выбранных узлов
      // отредактируем список узлов чтобы в нем была вся необходимая информация для описания класса
      IList<UaNode> nodes4DtoClass = nodes4DtoClass( selectedNode.getUaNode(), selNodes );
      IDtoClassInfo dtoClassInfo = makeDtoClassInfo( nodes4DtoClass );
      IM5Model<IDtoClassInfo> modelDto = conn.scope().get( IM5Domain.class )
          .getModel( IKM5SdedConstants.MID_SDED_DTO_CLASS_INFO, IDtoClassInfo.class );
      // TODO тут нужно вызвать код выбора соединения куда писать
      // nop
      ITsDialogInfo cdi = new TsDialogInfo( tsContext(), null, DLG_C_NEW_CLASS, DLG_T_NEW_CLASS, 0 );
      // создаем пучок из модели
      IM5Bunch<IDtoClassInfo> bunchOfFieldVals = modelDto.valuesOf( dtoClassInfo );
      // просим прользователя верифицировать/редактировать описание класса и нажать Ok
      dtoClassInfo =
          M5GuiUtils.askCreate( tsContext(), modelDto, bunchOfFieldVals, cdi, modelDto.getLifecycleManager( conn ) );
      // подтверждаем успешное создание класса
      TsDialogUtils.info( getShell(), "Операция завершена успешно, создан/обновлен class: %s", dtoClassInfo.id() );
    }
  }

  /**
   * На основе переданного списка узлов создаем новый список который содержит в себе всю необходимую для создания класса
   * информацию. А именно узел описания класса и список узлов с описаниями RtData. При этом удаляем узел описывающий
   * объект.
   *
   * @param aClassNode узел описывающий класс
   * @param aSelNodes выбранные узлы
   * @return список узлов один из которых описание класса, остальные описание RtData этого класса
   */
  private static IList<UaNode> nodes4DtoClass( UaNode aClassNode, IList<UaNode> aSelNodes ) {
    IListEdit<UaNode> retVal = new ElemArrayList<>();
    // первый элемент - описание класса
    retVal.add( aClassNode );
    for( UaNode node : aSelNodes ) {
      if( node instanceof UaVariableNode objNode ) {
        retVal.add( node );
      }
    }
    return retVal;
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  private static IDtoClassInfo makeDtoClassInfo( IList<UaNode> aNodes ) {
    // узел типа Object, его данные используются для создания описания класса
    UaObjectNode classNode = objNode( aNodes );

    String id = classNode.getBrowseName().getName();
    String name = classNode.getDisplayName().getText();
    String description =
        classNode.getDescription().getText().trim().length() > 0 ? classNode.getDescription().getText() : name;

    IOptionSetEdit params = new OptionSet();

    params.setStr( FID_NAME, name );
    params.setStr( FID_DESCRIPTION, description );
    DtoClassInfo cinf = new DtoClassInfo( id, IGwHardConstants.GW_ROOT_CLASS_ID, params );
    for( UaNode node : aNodes ) {
      if( node instanceof UaVariableNode varNode ) {
        readDataInfo( cinf, varNode );
        readEventInfo( cinf, varNode );
      }
    }
    // добавим атрибут который сигнализирует что класс из OPC UA node
    markClassOPC_UA( cinf );
    return cinf;
  }

  private static UaObjectNode objNode( IList<UaNode> aNodes ) {
    UaObjectNode retVal = null;
    // ищем элемент типа Object
    for( UaNode node : aNodes ) {
      if( node instanceof UaObjectNode ) {
        retVal = (UaObjectNode)node;
        break;
      }
    }
    return retVal;
  }

  /**
   * Читает описание данного и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aDtoClass текущее описание класса
   * @param aVariableNode описание узла типа переменная
   */
  private static void readDataInfo( DtoClassInfo aDtoClass, UaVariableNode aVariableNode ) {
    // id данного
    String dataId = aVariableNode.getBrowseName().getName();
    // соблюдаем соглашения о наименовании
    if( !dataId.startsWith( RTD_PREFIX ) ) {
      dataId = RTD_PREFIX + dataId;
    }
    String paramDescrStr = aVariableNode.getDisplayName().getText();
    String[] result = paramDescrStr.split( "\\|" ); //$NON-NLS-1$

    // тип данного
    Class<?> clazz = OpcUaUtils.getNodeDataTypeClass( aVariableNode );
    EAtomicType type = OpcUaUtils.getAtomicType( clazz );
    // название
    String name = result.length > 1 ? result[0] : paramDescrStr;
    // описание
    String descr = result.length > 2 ? result[1] : paramDescrStr;
    // sync
    boolean sync = false; // по умолчанию асинхронное
    int deltaT = sync ? 1000 : 1;

    IDtoRtdataInfo dataInfo = DtoRtdataInfo.create1( dataId, new DataType( type ), //
        true, // isCurr
        true, // isHist
        sync, // isSync
        deltaT, // deltaT
        OptionSetUtils.createOpSet( //
            TSID_NAME, name, //
            TSID_DESCRIPTION, descr //
        ) );

    aDtoClass.rtdataInfos().add( dataInfo );
  }

  /**
   * Читает описание данного и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aDtoClass текущее описание класса
   * @param aVariableNode описание узла типа переменная
   */
  private static void readEventInfo( DtoClassInfo aDtoClass, UaVariableNode aVariableNode ) {
    // id события
    String evtId = aVariableNode.getBrowseName().getName();
    // соблюдаем соглашения о наименовании
    if( !evtId.startsWith( EVT_PREFIX ) ) {
      evtId = EVT_PREFIX + evtId;
    }
    String evtDescrStr = aVariableNode.getDisplayName().getText();
    String[] result = evtDescrStr.split( "\\|" ); //$NON-NLS-1$

    // тип данного
    Class<?> clazz = OpcUaUtils.getNodeDataTypeClass( aVariableNode );
    EAtomicType type = OpcUaUtils.getAtomicType( clazz );
    // название
    String name = result.length > 1 ? result[0] : evtDescrStr;
    // описание
    String descr = result.length > 2 ? result[1] : evtDescrStr;
    StridablesList<IDataDef> evParams;
    evParams = switch( type ) {
      case INTEGER -> new StridablesList<>( EVPDD_OLD_VAL_INT, EVPDD_NEW_VAL_INT );
      case BOOLEAN -> new StridablesList<>( EVPDD_ON );
      case FLOATING -> new StridablesList<>( EVPDD_OLD_VAL_FLOAT, EVPDD_NEW_VAL_FLOAT );
      case NONE -> throw invalidParamTypeExcpt( type );
      case STRING -> throw invalidParamTypeExcpt( type );
      case TIMESTAMP -> throw invalidParamTypeExcpt( type );
      case VALOBJ -> throw invalidParamTypeExcpt( type );
    };

    IDtoEventInfo evtInfo = DtoEventInfo.create1( evtId, true, //
        evParams, //
        OptionSetUtils.createOpSet( //
            IAvMetaConstants.TSID_NAME, name, //
            IAvMetaConstants.TSID_DESCRIPTION, descr //
        ) ); //

    aDtoClass.eventInfos().add( evtInfo );
  }

  private static TsIllegalArgumentRtException invalidParamTypeExcpt( EAtomicType type ) {
    return new TsIllegalArgumentRtException( "Can't create event parameters with type %s", //$NON-NLS-1$
        type.id() );
  }

  private static void markClassOPC_UA( DtoClassInfo aCinf ) {
    IDataDef ddType = DDEF_BOOLEAN;
    // название
    String name = "маркер OPC UA";
    // описание
    String descr = "маркер класса сгенерированного из OPC UA";

    IDtoAttrInfo atrInfo = DtoAttrInfo.create2( IOpcUaServerConnCfgConstants.OPC_AU_CLASS_MARKER, ddType, //
        TSID_NAME, name, //
        TSID_DESCRIPTION, descr );

    aCinf.attrInfos().add( atrInfo );
  }

  private void createObjsFromNodes( ITsGuiContext aContext ) {
    // создать объекты по списку UaNode
    IList<UaNode> tmpListNodes =
        OpcUaNodesSelector.selectUaNodes4Objects( aContext, selectedNode.getUaNode().getNodeId(), client );
    if( tmpListNodes != null ) {
      IListEdit<UaNode> selNodes = new ElemArrayList<>( tmpListNodes );
      // отрезаем первый, это узловой нод он лишний
      selNodes.removeByIndex( 0 );
      // получаем М5 модель IDtoObject
      IM5Model<IDtoObject> modelSk =
          conn.scope().get( IM5Domain.class ).getModel( DtoObjectM5Model.MODEL_ID, IDtoObject.class );

      // выбор класса
      ISkClassInfo baseClassInfo = conn.coreApi().sysdescr().findClassInfo( GW_ROOT_CLASS_ID );
      ISkClassInfo selectedClassInfo = PanelClassInfoSelector.selectChildClass( baseClassInfo, tsContext() );
      if( selectedClassInfo != null ) {

        // подсунем свой item provider
        // IM5ItemsProvider<ISkObject> ip = lmSk.itemsProvider();
        IM5ItemsProvider<IDtoObject> itemProvider =
            new OpcUANode2SkObjectItemsProvider( selNodes, conn.coreApi(), selectedClassInfo );

        IListEdit<IDtoObject> llObjs = new ElemArrayList<>();
        for( IDtoObject dtoObj : itemProvider.listItems() ) {
          llObjs.add( dtoObj );
        }

        IM5LifecycleManager<IDtoObject> localLM = localLifeCycleManager( modelSk, llObjs );

        // ITsGuiContext ctxSk = new TsGuiContext( tsContext() );

        IM5CollectionPanel<IDtoObject> skObjsPanel =
            modelSk.panelCreator().createCollEditPanel( aContext, localLM.itemsProvider(), localLM );
        // show dialog
        TsDialogInfo di = new TsDialogInfo( aContext, getShell(), DLG_C_NEW_OBJS, DLG_T_NEW_OBJS, 0 );
        di.setMinSizeShellRelative( 10, 50 );
        if( M5GuiUtils.showCollPanel( di, skObjsPanel ) != null ) {
          StringBuilder sb = new StringBuilder();
          // создаем выбранные объекты
          for( IDtoObject obj : localLM.itemsProvider().listItems() ) {
            conn.coreApi().objService().defineObject( obj );
            sb.append( "\n" + obj.skid() + " - " + obj.nmName() );
          }
          // подтверждаем успешное создание объектов
          TsDialogUtils.info( getShell(), "Операция завершена успешно, созданы/обновлены объекты: %s", sb.toString() );
        }
      }
    }
  }

  /**
   * @param aModel M5 модель {@link IDtoObject}
   * @param aListObjs список объектов для верификации пользователем перед реальным созданием в БД сервера
   * @return lm заточенный под редактирование списка сущностей без реального обновления на сервере
   */
  private IM5LifecycleManager<IDtoObject> localLifeCycleManager( IM5Model<IDtoObject> aModel,
      IListEdit<IDtoObject> aListObjs ) {
    IM5LifecycleManager<IDtoObject> retVal = new M5LifecycleManager<>( aModel, false, true, true, true, null ) {

      private IDtoObject makeDtoObject( IM5Bunch<IDtoObject> aValues ) {
        // Создаем IDpuObject и инициализируем его значениями из пучка
        String classId = aValues.getAsAv( DtoObjectM5Model.FID_CLASSID ).asString();
        String id = aValues.getAsAv( DtoObjectM5Model.FID_STRID ).asString();
        Skid skid = new Skid( classId, id );
        DtoObject dtoObject = DtoObject.createDtoObject( skid, conn.coreApi() );
        dtoObject.attrs().setValue( DtoObjectM5Model.FID_NAME, aValues.getAsAv( DtoObjectM5Model.FID_NAME ) );
        dtoObject.attrs().setValue( DtoObjectM5Model.FID_DESCRIPTION,
            aValues.getAsAv( DtoObjectM5Model.FID_DESCRIPTION ) );
        return dtoObject;
      }

      @Override
      protected IDtoObject doEdit( IM5Bunch<IDtoObject> aValues ) {
        IDtoObject edited = makeDtoObject( aValues );
        int index = aListObjs.indexOf( aValues.originalEntity() );
        aListObjs.set( index, edited );
        return edited;
      }

      @Override
      protected IList<IDtoObject> doListEntities() {
        return aListObjs;
      }
    };
    return retVal;
  }

  /**
   * Создает, если нужно базовый класс для всех классов сгенерированных из дерева OPC UA
   *
   * @param aConn соединение с сервером
   */
  private static void ensureOPC_UABaseClass( ISkConnection aConn ) {
    IOptionSetEdit params = new OptionSet();
    params.setStr( FID_NAME, "Базовый класс для всех OPC UA сущностей" );
    params.setStr( FID_DESCRIPTION, "Базовый класс для всех OPC UA сущностей" );

    DtoClassInfo dtoClassInfo = new DtoClassInfo( "OPC_UAObject", GW_ROOT_CLASS_ID, params );
    OPDEF_SK_IS_SOURCE_CODE_DEFINED_CLASS.setValue( dtoClassInfo.params(), AV_FALSE );
    OPDEF_SK_IS_SOURCE_USKAT_CORE_CLASS.setValue( dtoClassInfo.params(), AV_FALSE );
    aConn.coreApi().sysdescr().defineClass( dtoClassInfo );
  }
}
