package org.toxsoft.skf.bridge.cfg.opcua.gui.strategy;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.core.tslib.gw.IGwHardConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.skide.IGreenWorldRefbooks.*;
import static org.toxsoft.uskat.core.gui.km5.sded.IKM5SdedConstants.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.stack.core.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.events.change.*;
import org.toxsoft.core.tslib.bricks.geometry.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skf.refbooks.lib.*;
import org.toxsoft.skf.rri.lib.*;
import org.toxsoft.skf.rri.lib.impl.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.km5.sded.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Base class for startegies to generate system description from OPC UA tree. Use as parent for classes that implement
 * specail cases.
 * <p/>
 *
 * @author dima
 */
public abstract class BaseSysdescrGenerator {

  static private String   RTD_PREFIX = "rtd"; //$NON-NLS-1$
  protected static String RRI_PREFIX = "rri"; //$NON-NLS-1$
  static protected String EVT_PREFIX = "evt"; //$NON-NLS-1$
  static protected String CMD_PREFIX = "cmd"; //$NON-NLS-1$

  protected static final String nodeCmdIdBrowseName = "CmdId";       //$NON-NLS-1$
  protected static final String nodeCmdArgInt       = "CmdArgInt";   //$NON-NLS-1$
  protected static final String nodeCmdArgFlt       = "CmdArgFlt";   //$NON-NLS-1$
  protected static final String nodeCmdFeedback     = "CmdFeedback"; //$NON-NLS-1$

  protected final ITsGuiContext       context;
  private final OpcUaClient           client;
  protected final IOpcUaServerConnCfg opcUaServerConnCfg;
  private final EOPCUATreeType        treeType;
  protected final ISkConnection       conn;

  /**
   * Собсно сама панель
   */
  protected final MultiPaneComponentModown<UaTreeNode> componentModown;

  private StringMap<IList<IDtoCmdInfo>> clsId2CmdInfoes = null;

  /**
   * карта id класса - > его BitIdx2DtoRtData
   */
  private StringMap<StringMap<IList<BitIdx2DtoRtData>>>  clsId2RtDataInfoes  = null;
  /**
   * карта id класса - > его BitIdx2DtoRriAttr
   */
  private StringMap<StringMap<IList<BitIdx2RriDtoAttr>>> clsId2RriAttrInfoes = null;
  protected ISkRriSection                                rriSection          = null;

  /**
   * карта id класса - > его BitIdx2DtoEvent
   */
  private StringMap<StringMap<IList<BitIdx2DtoEvent>>> clsId2EventInfoes = null;

  /**
   * Аргумент команды: значение.
   * <p>
   * Аргумент имеет тип {@link EAtomicType#FLOATING}.
   */
  private static String CMDARGID_VALUE = "value"; //$NON-NLS-1$

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
  public static DataDef EVPDD_ON = DataDef.create( EVPID_ON, EAtomicType.BOOLEAN, TSID_NAME, STR_N_EV_PARAM_ON, //
      TSID_DESCRIPTION, STR_D_EV_PARAM_ON, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, AV_FALSE );

  /**
   * Items provider for ISkObject created on OPC UA node.
   *
   * @author dima
   */
  static class OpcUANode2SkObjectItemsProvider
      implements IM5ItemsProvider<IDtoObject> {

    private IListEdit<IDtoObject> items         = new ElemArrayList<>();
    private final ISkCoreApi      coreApi;
    IListEdit<UaNode2Skid>        node2SkidList = new ElemArrayList<>();

    // Создаем IDpuObject и инициализируем его значениями из узла
    private IDtoObject makeObjDto( String aClassId, UaTreeNode aObjNode ) {
      String id = aObjNode.getBrowseName();
      Skid skid = new Skid( aClassId, id );
      DtoObject dtoObj = DtoObject.createDtoObject( skid, coreApi );
      dtoObj.attrs().setValue( FID_NAME, AvUtils.avStr( aObjNode.getDisplayName() ) );
      dtoObj.attrs().setValue( FID_DESCRIPTION, AvUtils.avStr( aObjNode.getDescription() ) );
      // add master-object support
      String[] splitted = id.split( "_" ); //$NON-NLS-1$
      dtoObj.attrs().setStr( AID_MASTER_OBJ_RESOLVER, splitted.length > 1 ? splitted[1] : splitted[0] );
      return dtoObj;
    }

    OpcUANode2SkObjectItemsProvider( IList<UaTreeNode> aSelectedNodes, ISkCoreApi aSkCoreApi,
        ISkClassInfo aSelectedClassInfo ) {
      coreApi = aSkCoreApi;
      for( UaTreeNode objNode : aSelectedNodes ) {
        IDtoObject dtoObj = makeObjDto( aSelectedClassInfo.id(), objNode );
        items.add( dtoObj );
        Skid skid = new Skid( aSelectedClassInfo.id(), dtoObj.id() );
        node2SkidList.add( new UaNode2Skid( objNode.getNodeId(), objNode.getDisplayName(), skid ) );
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
   * Constructor.
   *
   * @param aContext app context
   * @param aClient - OPC UA server
   * @param aOpcUaServerConnCfg - OPC UA server connection settings
   */
  BaseSysdescrGenerator( ITsGuiContext aContext, OpcUaClient aClient, IOpcUaServerConnCfg aOpcUaServerConnCfg,
      MultiPaneComponentModown<UaTreeNode> aComponentModown ) {
    context = aContext;
    client = aClient;
    componentModown = aComponentModown;
    opcUaServerConnCfg = aOpcUaServerConnCfg;
    treeType = opcUaServerConnCfg.treeType();
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();
  }

  protected ITsGuiContext tsContext() {
    return context;
  }

  /**
   * Create class from subtree.
   *
   * @param aSelectedNode - root node of subtree
   */
  @SuppressWarnings( "nls" )
  public void createClassFromNodes( UaTreeNode aSelectedNode ) {
    // создать класс из информации об UaNode
    IList<UaTreeNode> selNodes = OpcUaNodesSelector.selectUaNodes4Class( context, aSelectedNode.getUaNode().getNodeId(),
        client, opcUaServerConnCfg );
    if( selNodes != null ) {
      // создаем описание класса из списка выбранных узлов
      // отредактируем список узлов чтобы в нем была вся необходимая информация для описания класса
      IList<UaTreeNode> nodes4DtoClass = nodes4DtoClass( aSelectedNode, selNodes, treeType );
      // TODO здесь очищаем список от rri нодов и формируем отдельный для rtd
      IListEdit<UaNode2Gwid> node2ClassGwidList = new ElemArrayList<>();
      Pair<IDtoClassInfo, IDtoClassInfo> pairClassInfo = makeDtoClassInfo( nodes4DtoClass, node2ClassGwidList );
      IDtoClassInfo dtoClassInfo = pairClassInfo.left();
      IDtoClassInfo rriDtoClassInfo = pairClassInfo.right();
      IM5Model<IDtoClassInfo> modelDto = conn.scope().get( IM5Domain.class )
          .getModel( IKM5SdedConstants.MID_SDED_DTO_CLASS_INFO, IDtoClassInfo.class );
      TsDialogInfo cdi = new TsDialogInfo( tsContext(), null, DLG_C_NEW_CLASS, DLG_T_NEW_CLASS, 0 );
      // установим нормальный размер диалога
      cdi.setMinSize( new TsPoint( -30, -70 ) );
      // проверяем наличие класса
      ISkClassInfo existClsInfo = conn.coreApi().sysdescr().findClassInfo( dtoClassInfo.id() );
      // если он уже существует, то обновляем все существующие поля
      if( existClsInfo != null ) {
        IDtoClassInfo currDtoClassInfo = DtoClassInfo.createFromSk( existClsInfo, false );
        dtoClassInfo = updateDtoClassInfo( dtoClassInfo, currDtoClassInfo );
        // просим редактировать описание НСИ класса и нажать Ok
        dtoClassInfo =
            M5GuiUtils.askEdit( tsContext(), modelDto, dtoClassInfo, cdi, modelDto.getLifecycleManager( conn ) );
        if( dtoClassInfo == null ) {
          // если пользователь нажал Cancel
          return;
        }
        // тут пользователь нажал Ok,но проверяем есть ли смысл возится с НСИ тенью этого класса
        if( useRRI() && !rriDtoClassInfo.attrInfos().isEmpty() ) {
          // создаем временный IM5LifecycleManager задача которого ничего не делать, а просто отобразить содержимое
          // описания класса
          IM5LifecycleManager<IDtoClassInfo> localLM = localLifeCycleManager4DtoClassInfo( modelDto );
          // фильтруем содержимое НСИ так чтобы атрибуты НСИ не совпадали с rtData базового класса
          rriDtoClassInfo = filterRriClassInfo( dtoClassInfo, rriDtoClassInfo );
          // создаем НСИ атрибуты и связи
          cdi = new TsDialogInfo( tsContext(), null, "Параметры НСИ класса",
              "Отредактируйте НСИ атрибуты и связи класса", 0 );
          // установим нормальный размер диалога
          cdi.setMinSize( new TsPoint( -30, -80 ) );
          rriDtoClassInfo = M5GuiUtils.askEdit( tsContext(), modelDto, rriDtoClassInfo, cdi, localLM );
          if( rriDtoClassInfo != null ) {
            defineRriParams( context, rriDtoClassInfo );
          }
        }
        // чистим список привязок ClassGwid -> NodeId
        node2ClassGwidList = filterNode2ClassGwidList( dtoClassInfo, rriDtoClassInfo, node2ClassGwidList );
        // заливаем в хранилище
        OpcUaUtils.updateNodes2GwidsInStore( context, node2ClassGwidList,
            OpcUaUtils.SECTID_OPC_UA_NODES_2_CLS_GWIDS_TEMPLATE, UaNode2Gwid.KEEPER, opcUaServerConnCfg );

        ISkidList skids2Remove = conn.coreApi().objService().listSkids( currDtoClassInfo.id(), false );
        // перепривязываем объекты
        ISkClassInfo updatedClassInfo = conn.coreApi().sysdescr().getClassInfo( currDtoClassInfo.id() );
        IListEdit<IDtoObject> listObj2Update = new ElemArrayList<>();
        for( Skid skid : skids2Remove ) {
          ISkObject obj2Update = conn.coreApi().objService().find( skid );
          listObj2Update.add( DtoObject.createFromSk( obj2Update, conn.coreApi() ) );
        }
        OpcUaUtils.clearCache( opcUaServerConnCfg );
        generateNode2GwidLinks( context, updatedClassInfo, listObj2Update,
            rriSection == null ? IStridablesList.EMPTY : rriSection.listParamInfoes( updatedClassInfo.id() ) );
        TsDialogUtils.info( getShell(), STR_SUCCESS_CLASS_UPDATED, currDtoClassInfo.id() );
      }
      else {
        // создаем пучок из модели
        M5BunchEdit<IDtoClassInfo> bunchOfFieldVals = new M5BunchEdit<>( modelDto );
        // add attrs, rtData, cmds from OPC UA nodes
        bunchOfFieldVals.fillFrom( dtoClassInfo, false );
        // old version
        // IM5Bunch<IDtoClassInfo> bunchOfFieldVals = modelDto.valuesOf( dtoClassInfo );
        // просим прользователя верифицировать/редактировать описание класса и нажать Ok
        dtoClassInfo =
            M5GuiUtils.askCreate( tsContext(), modelDto, bunchOfFieldVals, cdi, modelDto.getLifecycleManager( conn ) );
        if( dtoClassInfo != null ) {
          // FIXME copy|paste code, see up need refactoring
          // тут кусок кода который имеет смысл если стратегия работает с НСИ
          if( useRRI() ) {
            // создаем временный IM5LifecycleManager задача которого ничего не делать, а просто отобразить содержимое
            // описания класса
            IM5LifecycleManager<IDtoClassInfo> localLM = localLifeCycleManager4DtoClassInfo( modelDto );
            // фильтруем содержимое НСИ так чтобы атрибуты НСИ не совпадали с rtData базового класса
            rriDtoClassInfo = filterRriClassInfo( dtoClassInfo, rriDtoClassInfo );
            // создаем НСИ атрибуты и связи
            cdi = new TsDialogInfo( tsContext(), null, "Параметры НСИ класса",
                "Отредактируйте НСИ атрибуты и связи класса", 0 );
            // установим нормальный размер диалога
            cdi.setMinSize( new TsPoint( -30, -80 ) );
            rriDtoClassInfo = M5GuiUtils.askEdit( tsContext(), modelDto, rriDtoClassInfo, cdi, localLM );
            if( rriDtoClassInfo != null ) {
              defineRriParams( context, rriDtoClassInfo );
            }
          }
          // чистим список привязок ClassGwid -> NodeId
          node2ClassGwidList = filterNode2ClassGwidList( dtoClassInfo, rriDtoClassInfo, node2ClassGwidList );
          // заливаем в хранилище
          OpcUaUtils.updateNodes2GwidsInStore( context, node2ClassGwidList,
              OpcUaUtils.SECTID_OPC_UA_NODES_2_CLS_GWIDS_TEMPLATE, UaNode2Gwid.KEEPER, opcUaServerConnCfg );
          OpcUaUtils.clearCache( opcUaServerConnCfg );
          // подтверждаем успешное создание класса
          TsDialogUtils.info( getShell(), STR_SUCCESS_CLASS_CREATED, dtoClassInfo.id() );
        }
      }
    }
  }

  /**
   * Фильтрует ранее подготовленный список привязок NodeId->ClassGwid удаляее элементы которые пользователь выбросил.
   * Оставляет
   *
   * @param aDtoClassInfo - описание класса отредактированное пользователем
   * @param aRriDtoClassInfo - описание НСИшной части класса отредактированное пользователем
   * @param aNode2ClassGwidList - ранее подготовленный список привязок NodeId->ClassGwid
   * @return отфильрованный список
   */
  private static IListEdit<UaNode2Gwid> filterNode2ClassGwidList( IDtoClassInfo aDtoClassInfo,
      IDtoClassInfo aRriDtoClassInfo, IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    IListEdit<UaNode2Gwid> retVal = new ElemArrayList<>();
    // выкидываем из списка события и rtData которые пользователь удалил
    for( IDtoRtdataInfo rtDataInfo : aDtoClassInfo.rtdataInfos() ) {
      Gwid gwid = Gwid.createRtdata( aDtoClassInfo.id(), rtDataInfo.id() );
      for( UaNode2Gwid uaNode2Gwid : aNode2ClassGwidList ) {
        if( gwid.equals( uaNode2Gwid.gwid() ) ) {
          retVal.add( uaNode2Gwid );
        }
      }
    }
    for( IDtoEventInfo evtInfo : aDtoClassInfo.eventInfos() ) {
      Gwid gwid = Gwid.createEvent( aDtoClassInfo.id(), evtInfo.id() );
      for( UaNode2Gwid uaNode2Gwid : aNode2ClassGwidList ) {
        if( gwid.equals( uaNode2Gwid.gwid() ) ) {
          retVal.add( uaNode2Gwid );
        }
      }
    }
    for( IDtoAttrInfo attrInfo : aRriDtoClassInfo.attrInfos() ) {
      Gwid gwid = Gwid.createAttr( aDtoClassInfo.id(), attrInfo.id() );
      for( UaNode2Gwid uaNode2Gwid : aNode2ClassGwidList ) {
        if( gwid.equals( uaNode2Gwid.gwid() ) ) {
          retVal.add( uaNode2Gwid );
        }
      }
    }
    return retVal;
  }

  /**
   * По описания атрибутов создает НСИ атрибуты
   *
   * @param aContext контекст приложения
   * @param aRriDtoClassInfo - описание НСИ
   */
  public void defineRriParams( ITsGuiContext aContext, IDtoClassInfo aRriDtoClassInfo ) {
    // TODO refactoring создаем/обновляем НСИ параметры
    IStridablesListEdit<IDtoRriParamInfo> rriParamInfoes = new StridablesList<>();
    for( IDtoAttrInfo attrInfo : aRriDtoClassInfo.attrInfos() ) {
      rriParamInfoes.add( new DtoRriParamInfo( attrInfo ) );
    }
    for( IDtoLinkInfo linkInfo : aRriDtoClassInfo.linkInfos() ) {
      rriParamInfoes.add( new DtoRriParamInfo( linkInfo ) );
    }
    rriSection.defineParam( aRriDtoClassInfo.id(), rriParamInfoes );
  }

  /**
   * На основе переданного списка узлов создаем новый список который содержит в себе всю необходимую для создания класса
   * информацию. А именно узел описания класса и список узлов с описаниями RtData. При этом удаляем узел описывающий
   * объект.
   *
   * @param aClassNode узел описывающий класс
   * @param aSelNodes выбранные узлы
   * @param aTreeType тип дерева OPC UA
   * @return список узлов один из которых описание класса, остальные описание RtData этого класса
   */
  private IList<UaTreeNode> nodes4DtoClass( UaTreeNode aClassNode, IList<UaTreeNode> aSelNodes,
      EOPCUATreeType aTreeType ) {
    IListEdit<UaTreeNode> retVal = new ElemArrayList<>();
    // первый элемент - описание класса
    retVal.add( getClassNode( aClassNode ) );
    for( UaTreeNode node : aSelNodes ) {
      if( node.getNodeClass().equals( NodeClass.Variable ) ) {
        retVal.add( node );
      }
    }
    return retVal;
  }

  private Pair<IDtoClassInfo, IDtoClassInfo> makeDtoClassInfo( IList<UaTreeNode> aNodes,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // узел типа Object, его данные используются для создания описания класса
    UaTreeNode classNode = objNode( aNodes );

    String id = extractClassId( classNode.getBrowseName() );

    String name = classNode.getDisplayName();
    String description = classNode.getDescription().trim().length() > 0 ? classNode.getDescription() : name;

    IOptionSetEdit params = new OptionSet();

    params.setStr( FID_NAME, name );
    params.setStr( FID_DESCRIPTION, description );
    DtoClassInfo cinf = new DtoClassInfo( id, IGwHardConstants.GW_ROOT_CLASS_ID, params );
    DtoClassInfo rriCinf = new DtoClassInfo( id, IGwHardConstants.GW_ROOT_CLASS_ID, params );
    for( UaTreeNode node : aNodes ) {
      if( node.getNodeClass().equals( NodeClass.Variable ) ) {
        try {
          UaVariableNode varNode = client.getAddressSpace().getVariableNode( NodeId.parse( node.getNodeId() ) );
          readDataInfo( cinf, varNode, aNode2ClassGwidList );
          readCmdInfo( cinf, varNode, aNode2ClassGwidList );
          readEventInfo( cinf, varNode, aNode2ClassGwidList );
          // FIXME НСИ атрибут
          // readRriAttrInfo( rriCinf, varNode, aNode2ClassGwidList );
          // dima 05.03.24 не генерим автоматически события из узлов. Только из справочника см. код ниже
          // readEventInfo( cinf, varNode, aNode2ClassGwidList );
        }
        catch( UaRuntimeException | UaException ex ) {
          LoggerUtils.errorLogger().error( ex );
          TsDialogUtils.error( getShell(), ERR_MSG_CACHE_OUTDATED );
        }
      }
    }
    // добавим атрибут который сигнализирует что класс из OPC UA node
    markClassOPC_UA( cinf );
    // add support for master obj techology
    addMasterObjResolver( cinf );
    // добавим команды, если они описаны
    if( clsId2CmdInfoes != null ) {
      if( clsId2CmdInfoes.hasKey( id ) ) {
        IList<IDtoCmdInfo> cmdInfioes = clsId2CmdInfoes.getByKey( id );
        cinf.cmdInfos().addAll( cmdInfioes );
      }
    }
    // добавим битовые rtData, если они описаны
    if( clsId2RtDataInfoes != null ) {
      if( clsId2RtDataInfoes.hasKey( id ) ) {
        StringMap<IList<BitIdx2DtoRtData>> rtDataInfioesMap = clsId2RtDataInfoes.getByKey( id );
        for( String key : rtDataInfioesMap.keys() ) {
          IList<BitIdx2DtoRtData> list = rtDataInfioesMap.getByKey( key );
          for( BitIdx2DtoRtData dto : list ) {
            cinf.rtdataInfos().add( dto.dtoRtdataInfo() );
          }
        }
      }
    }
    // добавим битовые RRI Param, если они описаны
    if( clsId2RriAttrInfoes != null ) {
      if( clsId2RriAttrInfoes.hasKey( id ) ) {
        StringMap<IList<BitIdx2RriDtoAttr>> rriAttrInfioesMap = clsId2RriAttrInfoes.getByKey( id );
        for( String key : rriAttrInfioesMap.keys() ) {
          IList<BitIdx2RriDtoAttr> list = rriAttrInfioesMap.getByKey( key );
          for( BitIdx2RriDtoAttr dto : list ) {
            rriCinf.attrInfos().add( dto.dtoAttrInfo() );
          }
        }
      }
    }
    // добавим битовые события, если они описаны
    if( clsId2EventInfoes != null ) {
      if( clsId2EventInfoes.hasKey( id ) ) {
        StringMap<IList<BitIdx2DtoEvent>> evtInfioesMap = clsId2EventInfoes.getByKey( id );
        for( String key : evtInfioesMap.keys() ) {
          IList<BitIdx2DtoEvent> list = evtInfioesMap.getByKey( key );
          for( BitIdx2DtoEvent dte : list ) {
            cinf.eventInfos().add( dte.dtoEventInfo() );
          }
        }
      }
    }

    return new Pair<>( cinf, rriCinf );
  }

  protected Shell getShell() {
    return tsContext().get( Shell.class );
  }

  private static void markClassOPC_UA( DtoClassInfo aCinf ) {
    IDataDef ddType = DDEF_BOOLEAN;
    // название
    String name = STR_N_OPC_UA_MARKER;
    // описание
    String descr = STR_D_OPC_UA_MARKER;

    IDtoAttrInfo atrInfo = DtoAttrInfo.create2( IOpcUaServerConnCfgConstants.AID_OPC_UA_CLASS_MARKER, ddType, //
        TSID_NAME, name, //
        TSID_DESCRIPTION, descr, //
        TSID_DEFAULT_VALUE, AvUtils.AV_TRUE );

    aCinf.attrInfos().add( atrInfo );
  }

  /**
   * Add attribute for support master-obj technology
   *
   * @param aCinf - class description info
   */
  private static void addMasterObjResolver( DtoClassInfo aCinf ) {
    IDataDef ddType = DDEF_NAME;
    // название
    String name = STR_N_MASTER_OBJ_RESOLVER;
    // описание
    String descr = STR_D_MASTER_OBJ_RESOLVER;

    IDtoAttrInfo atrInfo = DtoAttrInfo.create2( IOpcUaServerConnCfgConstants.AID_MASTER_OBJ_RESOLVER, ddType, //
        TSID_NAME, name, //
        TSID_DESCRIPTION, descr, //
        TSID_DEFAULT_VALUE, AvUtils.AV_STR_EMPTY );

    aCinf.attrInfos().add( atrInfo );
  }

  private static UaTreeNode objNode( IList<UaTreeNode> aNodes ) {
    UaTreeNode retVal = null;
    // ищем элемент типа Object
    for( UaTreeNode node : aNodes ) {
      if( node.getNodeClass().equals( NodeClass.Object ) ) {
        retVal = node;
        break;
      }
    }
    return retVal;
  }

  /**
   * Читает описание Rt данного и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aDtoClass текущее описание класса
   * @param aVariableNode описание узла типа переменная
   * @param aNode2ClassGwidList список для хранения привязки node -> Class Gwid
   * @return {@link IDtoRtdataInfo} - созданное описание RtData или null
   */
  private IDtoRtdataInfo readDataInfo( DtoClassInfo aDtoClass, UaVariableNode aVariableNode,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // id данного
    String dataId = aVariableNode.getBrowseName().getName();
    if( isIgnore4RtData( aDtoClass.id(), dataId ) ) {
      return null;
    }
    // соблюдаем соглашения о наименовании
    if( !dataId.startsWith( RTD_PREFIX ) ) {
      dataId = RTD_PREFIX + dataId;
    }
    // название
    String name = aVariableNode.getDisplayName().getText();
    // описание
    String descr = aVariableNode.getDescription() == null ? name : aVariableNode.getDescription().getText();
    // описание
    if( (descr == null) || descr.isBlank() ) {
      descr = name;
    }

    // тип данного
    Class<?> clazz = OpcUaUtils.getNodeDataTypeClass( aVariableNode );
    EAtomicType type = OpcUaUtils.getAtomicType( clazz );
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
    // TODO boilerplate code сохраним привязку для использования в автоматическом связывании
    NodeId nodeId = aVariableNode.getNodeId();
    Gwid classGwid = Gwid.createRtdata( aDtoClass.id(), dataId );
    UaNode2Gwid uaNode2Gwid = new UaNode2Gwid( nodeId.toParseableString(), descr, classGwid );
    aNode2ClassGwidList.add( uaNode2Gwid );
    return dataInfo;
  }

  /**
   * Читает описание команды и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aDtoClass текущее описание класса
   * @param aVariableNode описание узла типа переменная
   * @param aNode2ClassGwidList список привязок Node -> Class Gwid
   */
  private void readCmdInfo( DtoClassInfo aDtoClass, UaVariableNode aVariableNode,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // id команды
    String cmdId = aVariableNode.getBrowseName().getName();
    if( isIgnore4Command( aDtoClass.id(), cmdId ) ) {
      return;
    }
    // соблюдаем соглашения о наименовании
    if( !cmdId.startsWith( CMD_PREFIX ) ) {
      cmdId = CMD_PREFIX + cmdId;
    }
    // название
    String name = aVariableNode.getDisplayName().getText();
    // описание
    String descr = aVariableNode.getDescription() == null ? name : aVariableNode.getDescription().getText();
    // описание
    if( descr == null ) {
      descr = name;
    }

    // тип данного
    Class<?> clazz = OpcUaUtils.getNodeDataTypeClass( aVariableNode );
    EAtomicType type = OpcUaUtils.getAtomicType( clazz );

    IDtoCmdInfo cmdInfo = DtoCmdInfo.create1( cmdId, //
        new StridablesList<>( //
            DataDef.create( CMDARGID_VALUE, type, TSID_NAME, STR_N_CMD_ARG_VALUE, //
                TSID_DESCRIPTION, STR_D_CMD_ARG_VALUE, //
                TSID_IS_NULL_ALLOWED, AV_FALSE ) //
        ), //
        OptionSetUtils.createOpSet( //
            IAvMetaConstants.TSID_NAME, name, //
            IAvMetaConstants.TSID_DESCRIPTION, descr //
        ) ); //

    aDtoClass.cmdInfos().add( cmdInfo );
    // TODO boilerplate code сохраним привязку для использования в автоматическом связывании
    NodeId nodeId = aVariableNode.getNodeId();
    Gwid classGwid = Gwid.createCmd( aDtoClass.id(), cmdId );
    UaNode2Gwid uaNode2Gwid = new UaNode2Gwid( nodeId.toParseableString(), descr, classGwid );
    aNode2ClassGwidList.add( uaNode2Gwid );
  }

  /**
   * Проверяет наличие в справочнике RBID_RRI_OPCUA элемента с составным strid
   *
   * @param aClassId - префикс составного strid
   * @param aDataId - суффикс составного strid
   * @return true если элемент с таким strid есть в справочнике
   */
  protected boolean existInRriRefbook( String aClassId, String aDataId ) {
    // читаем справочник НСИ и фильтруем то что предназначено для этого
    ISkRefbookService skRefServ = (ISkRefbookService)conn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    IList<ISkRefbookItem> rbItems = skRefServ.findRefbook( RBID_RRI_OPCUA ).listItems();
    // создаем id элемента справочника
    String rbItemStrid = new StringBuffer( aClassId ).append( "." ).append( aDataId ).toString(); //$NON-NLS-1$
    for( ISkRefbookItem rbItem : rbItems ) {
      if( rbItem.strid().equals( rbItemStrid ) ) {
        return true;
      }
    }
    return false;
  }

  /**
   * Читает описание события и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aDtoClass текущее описание класса
   * @param aVariableNode описание узла типа переменная
   * @param aNode2ClassGwidList список привязок Node -> Class Gwid
   */
  private void readEventInfo( DtoClassInfo aDtoClass, UaVariableNode aVariableNode,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // id события
    String evtId = aVariableNode.getBrowseName().getName();
    if( isIgnore4Event( aDtoClass.id(), evtId ) ) {
      return;
    }
    // соблюдаем соглашения о наименовании
    if( !evtId.startsWith( EVT_PREFIX ) ) {
      evtId = EVT_PREFIX + evtId;
    }
    // название
    String name = aVariableNode.getDisplayName().getText();
    // описание
    String descr = aVariableNode.getDescription() == null ? name : aVariableNode.getDescription().getText();
    // описание
    if( descr == null ) {
      descr = name;
    }

    // тип данного
    Class<?> clazz = OpcUaUtils.getNodeDataTypeClass( aVariableNode );
    EAtomicType type = OpcUaUtils.getAtomicType( clazz );

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
    // TODO boilerplate code сохраним привязку для использования в автоматическом связывании
    NodeId nodeId = aVariableNode.getNodeId();
    Gwid classGwid = Gwid.createEvent( aDtoClass.id(), evtId );
    UaNode2Gwid uaNode2Gwid = new UaNode2Gwid( nodeId.toParseableString(), descr, classGwid );
    aNode2ClassGwidList.add( uaNode2Gwid );
  }

  private static TsIllegalArgumentRtException invalidParamTypeExcpt( EAtomicType type ) {
    return new TsIllegalArgumentRtException( "Can't create event parameters with type %s", //$NON-NLS-1$
        type.id() );
  }

  private static DtoClassInfo updateDtoClassInfo( IDtoClassInfo aDtoClassInfo, IDtoClassInfo aCurrDtoClassInfo ) {
    DtoClassInfo dtoClass;
    if( aDtoClassInfo.id().equals( IGwHardConstants.GW_ROOT_CLASS_ID ) ) {
      dtoClass = new DtoClassInfo( aDtoClassInfo.params() );
    }
    else {
      dtoClass = new DtoClassInfo( aDtoClassInfo.id(), aDtoClassInfo.parentId(), aDtoClassInfo.params() );
    }
    // копируем свойства нового
    dtoClass.attrInfos().setAll( aDtoClassInfo.attrInfos() );
    dtoClass.rtdataInfos().setAll( aDtoClassInfo.rtdataInfos() );
    dtoClass.cmdInfos().setAll( aDtoClassInfo.cmdInfos() );
    dtoClass.eventInfos().setAll( aDtoClassInfo.eventInfos() );

    // обновляем атрибуты
    for( IDtoAttrInfo attrInfo : aCurrDtoClassInfo.attrInfos() ) {
      if( !dtoClass.attrInfos().hasKey( attrInfo.id() ) ) {
        dtoClass.attrInfos().add( attrInfo );
      }
    }
    // обновляем rtData
    for( IDtoRtdataInfo rtDataInfo : aCurrDtoClassInfo.rtdataInfos() ) {
      if( !dtoClass.rtdataInfos().hasKey( rtDataInfo.id() ) ) {
        dtoClass.rtdataInfos().add( rtDataInfo );
      }
    }
    // обновляем cmds
    for( IDtoCmdInfo cmdInfo : aCurrDtoClassInfo.cmdInfos() ) {
      if( !dtoClass.cmdInfos().hasKey( cmdInfo.id() ) ) {
        dtoClass.cmdInfos().add( cmdInfo );
      }
    }
    // обновляем events
    for( IDtoEventInfo evtInfo : aCurrDtoClassInfo.eventInfos() ) {
      if( !dtoClass.eventInfos().hasKey( evtInfo.id() ) ) {
        dtoClass.eventInfos().add( evtInfo );
      }
    }
    return dtoClass;
  }

  /**
   * @param aModel M5 модель {@link IDtoClassInfo}
   * @return lm заточенный под редактирование списка параметров НСИ без реального обновления на сервере
   */
  private static IM5LifecycleManager<IDtoClassInfo> localLifeCycleManager4DtoClassInfo(
      IM5Model<IDtoClassInfo> aModel ) {
    IM5LifecycleManager<IDtoClassInfo> retVal = new M5LifecycleManager<>( aModel, false, true, true, true, null ) {

      private IDtoClassInfo makeDtoClassInfo( IM5Bunch<IDtoClassInfo> aValues ) {
        String id = aValues.getAsAv( FID_CLASS_ID ).asString();
        String parentId = aValues.getAsAv( FID_PARENT_ID ).asString();
        IOptionSetEdit params = new OptionSet();
        if( aValues.originalEntity() != null ) {
          params.setAll( aValues.originalEntity().params() );
        }
        params.setStr( FID_NAME, aValues.getAsAv( FID_NAME ).asString() );
        params.setStr( FID_DESCRIPTION, aValues.getAsAv( FID_DESCRIPTION ).asString() );
        DtoClassInfo cinf;
        if( id.equals( IGwHardConstants.GW_ROOT_CLASS_ID ) ) {
          cinf = new DtoClassInfo( params );
        }
        else {
          cinf = new DtoClassInfo( id, parentId, params );
        }
        IList<IDtoAttrInfo> attrList = aValues.get( IKM5SdedConstants.FID_SELF_ATTR_INFOS );
        cinf.attrInfos().setAll( attrList );
        IList<IDtoLinkInfo> linkList = aValues.get( IKM5SdedConstants.FID_SELF_LINK_INFOS );
        cinf.linkInfos().setAll( linkList );
        return cinf;
      }

      @Override
      protected IDtoClassInfo doEdit( IM5Bunch<IDtoClassInfo> aValues ) {
        return makeDtoClassInfo( aValues );
      }

    };
    return retVal;
  }

  private static IDtoClassInfo filterRriClassInfo( IDtoClassInfo aDtoClassInfo, IDtoClassInfo aRriDtoClassInfo ) {
    // копируем свойства исходного
    DtoClassInfo retVal =
        new DtoClassInfo( aRriDtoClassInfo.id(), aRriDtoClassInfo.parentId(), aRriDtoClassInfo.params() );
    retVal.attrInfos().addAll( aRriDtoClassInfo.attrInfos() );
    retVal.linkInfos().addAll( aRriDtoClassInfo.linkInfos() );
    // удаляем из класса описания НСИ все атрибуты которые совпадают с rtData базового класса
    for( IDtoRtdataInfo rtData : aDtoClassInfo.rtdataInfos() ) {
      String nodeName = rtData.id().substring( 3 );
      for( IDtoAttrInfo attr : aRriDtoClassInfo.attrInfos() ) {
        String rriAttrId = attr.id().substring( 3 );
        if( rriAttrId.compareTo( nodeName ) == 0 ) {
          retVal.attrInfos().remove( attr );
        }
      }
    }
    return retVal;
  }

  protected UaTreeNode findParentNode( IList<UaTreeNode> aListItems, NodeId aParentNodeId ) {
    for( UaTreeNode node : aListItems ) {
      if( node.getNodeId().equals( aParentNodeId.toParseableString() ) ) {
        return node;
      }
    }
    return null;
  }

  /**
   * По значению Gwid класса ищем его UaNode
   *
   * @param aContext ITsGuiContext - context.
   * @param aClassGwid {@link Gwid} Gwid класса
   * @param aVarNodes список узлов типа Variable
   * @return подходящий узел или null
   */
  protected UaTreeNode findVarNodeByClassGwid( ITsGuiContext aContext, Gwid aClassGwid, IList<UaTreeNode> aVarNodes ) {
    UaTreeNode retVal = null;
    NodeId classNodeId = OpcUaUtils.classGwid2uaNode( aContext, aClassGwid, opcUaServerConnCfg );
    if( classNodeId != null ) {
      // отрабатываем вариант когда 1 класс - 1 объект, в таком случае ищем полное совпадение nodeId
      for( UaTreeNode varNode : aVarNodes ) {
        NodeId nodeId = NodeId.parse( varNode.getNodeId() );
        if( nodeId.equals( classNodeId ) ) {
          return varNode;
        }
      }
      // здесь отрабатываем вариант когда класс отдельно, объекты отдельно. В этом варианте опираемся на правило
      // равенства
      // namespace у узлов описания класса и объекта
      int classNamespace = classNodeId.getNamespaceIndex().intValue();
      for( UaTreeNode varNode : aVarNodes ) {
        NodeId nodeId = NodeId.parse( varNode.getNodeId() );
        int ns = nodeId.getNamespaceIndex().intValue();

        if( ns == classNamespace ) {
          retVal = varNode;
          break;
        }
      }
    }
    return retVal;
  }

  protected UaTreeNode tryBitMaskRtData( ISkClassInfo aClassInfo, UaTreeNode aParentNode, IDtoRtdataInfo aRtDataInfo ) {
    if( clsId2RtDataInfoes != null && clsId2RtDataInfoes.hasKey( aClassInfo.id() ) ) {
      StringMap<IList<BitIdx2DtoRtData>> strid2Bits = clsId2RtDataInfoes.getByKey( aClassInfo.id() );
      for( String strid : strid2Bits.keys() ) {
        IList<BitIdx2DtoRtData> rtDataBits = strid2Bits.getByKey( strid );
        for( BitIdx2DtoRtData bit2rtData : rtDataBits ) {
          if( bit2rtData.dtoRtdataInfo().id().equals( aRtDataInfo.id() ) ) {
            // нашли свое данное, значит у него 100пудово должен быть его узел
            String bitArrayNode = bit2rtData.bitArrayWordStrid().substring( RTD_PREFIX.length() );
            // получаем список узлов в котором описаны переменные класса
            IList<UaTreeNode> variableNodes = getVariableNodes( aParentNode );
            UaTreeNode retVal = findNodeByBrowseName( bitArrayNode, variableNodes );
            TsIllegalStateRtException.checkNull( retVal,
                "Subtree %s, can't find node %s for class: %s, bit mask rtData: %s", bitArrayNode, //$NON-NLS-1$
                aParentNode.getBrowseName(), aClassInfo.id(), aRtDataInfo.id() );
            return retVal;
          }
        }
      }
    }
    // данное не из битовой маски, возвращаем null
    return null;
  }

  /**
   * По названию узла ищем UaNode
   *
   * @param aBrowseName - browseName узла
   * @param aVarNodes список узлов типа Variable
   * @return подходящий узел или null
   */
  private static UaTreeNode findNodeByBrowseName( String aBrowseName, IList<UaTreeNode> aVarNodes ) {
    UaTreeNode retVal = null;
    for( UaTreeNode varNode : aVarNodes ) {
      if( varNode.getNodeClass().equals( NodeClass.Variable ) ) {
        String name4Search = varNode.getBrowseName();
        if( aBrowseName.compareTo( name4Search ) == 0 ) {
          retVal = varNode;
          break;
        }
      }
    }
    return retVal;
  }

  protected UaTreeNode tryBitMaskRriAttr( ISkClassInfo aClassInfo, UaTreeNode aParentNode, IDtoAttrInfo aRriAttrInfo ) {
    if( clsId2RriAttrInfoes != null && clsId2RriAttrInfoes.hasKey( aClassInfo.id() ) ) {
      StringMap<IList<BitIdx2RriDtoAttr>> strid2RriBits = clsId2RriAttrInfoes.getByKey( aClassInfo.id() );
      for( String strid : strid2RriBits.keys() ) {
        IList<BitIdx2RriDtoAttr> rriAttrBits = strid2RriBits.getByKey( strid );
        for( BitIdx2RriDtoAttr bit2rriAttr : rriAttrBits ) {
          if( bit2rriAttr.dtoAttrInfo().id().equals( aRriAttrInfo.id() ) ) {
            // нашли свой НСИ атрибут, значит у него 100пудово должен быть его узел
            String bitArrayNode = bit2rriAttr.bitArrayWordStrid().substring( RTD_PREFIX.length() );
            // получаем список узлов в котором описаны переменные класса
            IList<UaTreeNode> variableNodes = getVariableNodes( aParentNode );
            UaTreeNode retVal = findNodeByBrowseName( bitArrayNode, variableNodes );
            TsIllegalStateRtException.checkNull( retVal,
                "Subtree %s, can't find node %s for class: %s, bit mask rriAttr: %s", bitArrayNode, //$NON-NLS-1$
                aParentNode.getBrowseName(), aClassInfo.id(), aRriAttrInfo.id() );
            return retVal;
          }
        }
      }
    }
    // НСИ атрибут не из битовой маски, возвращаем null
    return null;
  }

  /**
   * По описанию атрибута ищем подходящие командные UaNodes
   *
   * @param aObj описание объекта
   * @param aAttrInfo описание атрибута
   * @param aObjNode родительский узел описывающий объект
   * @return контейнер с описание узлов или null
   */
  protected CmdGwid2UaNodes findAttrCmdNodes( IDtoObject aObj, IDtoAttrInfo aAttrInfo, UaTreeNode aObjNode ) {
    CmdGwid2UaNodes retVal = null;
    Gwid gwid = Gwid.createCmd( aObj.classId(), aObj.id(), aAttrInfo.id() );
    String nodeDescr = aObjNode.getBrowseName();
    String niCmdId = null;
    String niCmdArgInt = null; // может и не быть
    String niCmdArgFlt = null;
    String niCmdFeedback = null;
    // получаем тип
    EAtomicType argType = aAttrInfo.dataType().atomicType();
    // получаем список узлов в котором описаны переменные класса
    IList<UaTreeNode> variableNodes = getVariableNodes( aObjNode );
    // перебираем все узлы и заполняем нужные нам для описания связи
    for( UaTreeNode varNode : variableNodes ) {
      if( varNode.getNodeClass().equals( NodeClass.Variable ) ) {
        String candidateBrowseName = varNode.getBrowseName();
        if( nodeCmdIdBrowseName.compareTo( candidateBrowseName ) == 0 ) {
          niCmdId = varNode.getNodeId();
          continue;
        }
        if( nodeCmdArgInt.compareTo( candidateBrowseName ) == 0 ) {
          niCmdArgInt = varNode.getNodeId();
          continue;
        }
        if( nodeCmdArgFlt.compareTo( candidateBrowseName ) == 0 ) {
          niCmdArgFlt = varNode.getNodeId();
          continue;
        }
        if( nodeCmdFeedback.compareTo( candidateBrowseName ) == 0 ) {
          niCmdFeedback = varNode.getNodeId();
          continue;
        }
      }
    }
    retVal = switch( argType ) {
      case BOOLEAN, INTEGER, FLOATING, NONE -> new CmdGwid2UaNodes( gwid, nodeDescr, niCmdId, niCmdArgInt, niCmdArgFlt,
          niCmdFeedback, argType );
      case STRING, TIMESTAMP, VALOBJ -> throw new TsNotAllEnumsUsedRtException( argType.name() );
    };
    return retVal;
  }

  protected UaTreeNode tryBitMaskEvent( ISkClassInfo aClassInfo, UaTreeNode aParentNode, IDtoEventInfo aEvtInfo ) {
    if( clsId2EventInfoes != null && clsId2EventInfoes.hasKey( aClassInfo.id() ) ) {
      StringMap<IList<BitIdx2DtoEvent>> strid2Bits = clsId2EventInfoes.getByKey( aClassInfo.id() );
      for( String strid : strid2Bits.keys() ) {
        IList<BitIdx2DtoEvent> eventBits = strid2Bits.getByKey( strid );
        for( BitIdx2DtoEvent bit2Event : eventBits ) {
          if( bit2Event.dtoEventInfo().id().equals( aEvtInfo.id() ) ) {
            // нашли свое событие, значит у него 100пудово должно быть его узел
            // получаем список узлов в котором описаны переменные класса
            IList<UaTreeNode> variableNodes = getVariableNodes( aParentNode );
            UaTreeNode retVal =
                findNodeByBrowseName( bit2Event.bitArrayWordStrid().substring( RTD_PREFIX.length() ), variableNodes );
            TsIllegalStateRtException.checkNull( retVal,
                "Subtree %s, can't find node for class: %s, bit mask event: %s", aParentNode.getBrowseName(), //$NON-NLS-1$
                aClassInfo.id(), aEvtInfo.id() );
            return retVal;
          }
        }
      }
    }
    // событие не из битовой маски, возвращаем null
    return null;
  }

  /**
   * Create objects from selected node as root of subtree
   *
   * @param aSelectedNode - root node
   */
  public void createObjsFromNodes( UaTreeNode aSelectedNode ) {
    // создать объекты по списку UaNode
    IList<UaTreeNode> selNodes = OpcUaNodesSelector.selectUaNodes4Objects( context,
        aSelectedNode.getUaNode().getNodeId(), client, opcUaServerConnCfg );
    if( selNodes != null ) {
      // получаем М5 модель IDtoObject
      IM5Model<IDtoObject> modelSk =
          conn.scope().get( IM5Domain.class ).getModel( DtoObjectM5Model.MODEL_ID, IDtoObject.class );

      // выбор класса
      ISkClassInfo baseClassInfo = conn.coreApi().sysdescr().findClassInfo( GW_ROOT_CLASS_ID );
      ISkClassInfo selectedClassInfo = PanelClassInfoSelector.selectChildClass( baseClassInfo, tsContext() );
      if( selectedClassInfo != null ) {

        // подсунем свой item provider
        // IM5ItemsProvider<ISkObject> ip = lmSk.itemsProvider();
        OpcUANode2SkObjectItemsProvider itemProvider =
            new OpcUANode2SkObjectItemsProvider( selNodes, conn.coreApi(), selectedClassInfo );

        IListEdit<IDtoObject> llObjs = new ElemArrayList<>();
        for( IDtoObject dtoObj : itemProvider.listItems() ) {
          llObjs.add( dtoObj );
        }

        IM5LifecycleManager<IDtoObject> localLM = localLifeCycleManager4DtoObject( modelSk, llObjs, itemProvider );

        // ITsGuiContext ctxSk = new TsGuiContext( tsContext() );

        IM5CollectionPanel<IDtoObject> skObjsPanel =
            modelSk.panelCreator().createCollEditPanel( context, localLM.itemsProvider(), localLM );
        // show dialog
        TsDialogInfo di = new TsDialogInfo( context, getShell(), DLG_C_NEW_OBJS, DLG_T_NEW_OBJS, 0 );
        di.setMinSizeShellRelative( 10, 50 );
        if( M5GuiUtils.showCollPanel( di, skObjsPanel ) != null ) {
          // сохраним привязки node -> Skid
          OpcUaUtils.updateNodes2SkidsInStore( context, itemProvider.node2SkidList, opcUaServerConnCfg );
          String userMsg = createSelObjs( localLM );
          // делаем автоматическую привязку NodeId -> Gwid
          generateNode2GwidLinks( context, selectedClassInfo, localLM.itemsProvider().listItems(),
              rriSection == null ? IStridablesList.EMPTY : rriSection.listParamInfoes( selectedClassInfo.id() ) );
          // подтверждаем успешное создание объектов
          TsDialogUtils.info( getShell(), STR_SUCCESS_OBJS_UPDATED, userMsg );
        }
      }
    }
  }

  private String createSelObjs( IM5LifecycleManager<IDtoObject> aLocalLM ) {
    StringBuilder sb = new StringBuilder();
    // создаем выбранные объекты
    for( IDtoObject obj : aLocalLM.itemsProvider().listItems() ) {
      conn.coreApi().objService().defineObject( obj );
      sb.append( "\n" + obj.skid() + " - " + obj.nmName() ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return sb.toString();
  }

  /**
   * @param aModel M5 модель {@link IDtoObject}
   * @param aListObjs список объектов для верификации пользователем перед реальным созданием в БД сервера
   * @param aItemProvider локальный поставщик списка сущностей
   * @return lm заточенный под редактирование списка сущностей без реального обновления на сервере
   */
  private IM5LifecycleManager<IDtoObject> localLifeCycleManager4DtoObject( IM5Model<IDtoObject> aModel,
      IListEdit<IDtoObject> aListObjs, OpcUANode2SkObjectItemsProvider aItemProvider ) {
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
        // обновим кеш провайдера FIXME
        // UaTreeNode treeNode = aItemProvider.id2Node.remove( aValues.originalEntity().id() );
        aListObjs.set( index, edited );
        // aItemProvider.id2Node.put( edited.id(), treeNode );
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
   * По описанию параметра ищем подходящий UaNode
   *
   * @param aPropInfo описание свойства класса
   * @param aObjectNode узел дерева объекта
   * @return подходящий узел или null
   */
  protected UaTreeNode findVarNodeByPropName( IDtoClassPropInfoBase aPropInfo, UaTreeNode aObjectNode ) {
    UaTreeNode retVal = null;
    IList<UaTreeNode> varNodes = getVariableNodes( aObjectNode );
    for( UaTreeNode varNode : varNodes ) {
      if( varNode.getNodeClass().equals( NodeClass.Variable ) ) {
        // create full id
        String name4Search = aPropInfo.id().substring( 0, 3 ) + varNode.getBrowseName();
        if( aPropInfo.id().compareTo( name4Search ) == 0 ) {
          retVal = varNode;
          break;
        }
      }
    }
    return retVal;
  }

  /**
   * Извлекает id класса из имени узла. Обычно это просто имя, но в проекте Байконур более сложная обработка.
   *
   * @param aBrowseName - имя узла
   * @return id класса
   */
  protected String extractClassId( String aBrowseName ) {
    return aBrowseName;
  }

  /**
   * Проверяет и если надо подкачивает нужные данные из справочников битовых масок
   */
  protected void ensureBitMaskDescription() {
    if( clsId2RtDataInfoes == null || clsId2EventInfoes == null || clsId2RriAttrInfoes == null ) {
      clsId2RtDataInfoes = OpcUaUtils.readRtDataInfoes( conn );
      clsId2RriAttrInfoes = OpcUaUtils.readRriAttrInfoes( conn );
      clsId2EventInfoes = OpcUaUtils.readEventInfoes( conn );
    }
  }

  /**
   * Проверяет и если надо подкачивает нужные данные из справочника команд
   */
  protected void ensureCmdDescription() {
    if( clsId2CmdInfoes == null ) {
      clsId2CmdInfoes = OpcUaUtils.readClass2CmdInfoes( context, conn );
    }
  }

  protected boolean ensureRriSection( ITsGuiContext aContext ) {
    boolean retVal = false;
    if( rriSection == null ) {
      // если секция одна, то выбирать не нужно
      ISkRegRefInfoService rriService =
          (ISkRegRefInfoService)conn.coreApi().services().getByKey( ISkRegRefInfoService.SERVICE_ID );
      if( rriService.listSections().size() == 1 ) {
        rriSection = rriService.listSections().first();
      }
      else {
        rriSection = PanelRriSectionSelector.selectRriSection( null, aContext );
      }
    }
    if( rriSection == null ) {
      TsDialogUtils.error( getShell(), "Не выбрана секция НСИ" );
    }
    else {
      retVal = true;
    }
    return retVal;
  }

  abstract protected UaTreeNode getClassNode( UaTreeNode aNode );

  abstract protected boolean isIgnore4RtData( String aClassId, String aDataId );

  abstract protected boolean isIgnore4Event( String aClassId, String aEventId );

  abstract protected boolean isIgnore4Command( String aClassId, String aCmdId );

  abstract protected void generateNode2GwidLinks( ITsGuiContext aContext, ISkClassInfo aClassInfo,
      IList<IDtoObject> aObjList, IStridablesList<IDtoRriParamInfo> aRriParamInfoes );

  abstract protected IList<UaTreeNode> getVariableNodes( UaTreeNode aObjectNode );

  /**
   * Выполнение всех предварительных проверок и подкачек перед созданием класса
   */
  public abstract void ensureBeforeClassCreation();

  /**
   * Выполнение всех предварительных проверок и подкачек перед созданием объектов
   */
  public abstract void ensureBeforeObjsCreation();

  /**
   * true - cтратегия использует НСИ, иначе false
   */
  protected abstract boolean useRRI();

}
