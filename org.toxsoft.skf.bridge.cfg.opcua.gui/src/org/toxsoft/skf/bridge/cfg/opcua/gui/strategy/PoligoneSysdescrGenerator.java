package org.toxsoft.skf.bridge.cfg.opcua.gui.strategy;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.skide.IGreenWorldRefbooks.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.stack.core.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skf.refbooks.lib.*;
import org.toxsoft.skf.rri.lib.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Class for startegy to generate system description from Poligone OPC UA tree.
 * <p/>
 *
 * @author dima
 */
public class PoligoneSysdescrGenerator
    extends BaseSysdescrGenerator {

  public static String DFLT_RRI_SECTION_ID    = "rri.section.id";          //$NON-NLS-1$
  public static String DFLT_RRI_SECTION_NAME  = "секция НСИ";              //$NON-NLS-1$
  public static String DFLT_RRI_SECTION_DESCR = "Единственная секция НСИ"; //$NON-NLS-1$

  /**
   * Constructor.
   *
   * @param aContext app context
   * @param aClient - OPC UA server
   * @param aOpcUaServerConnCfg - OPC UA server connection settings
   * @param aComponentModown - M5 tree to view OPC UA tree
   */
  public PoligoneSysdescrGenerator( ITsGuiContext aContext, OpcUaClient aClient,
      IOpcUaServerConnCfg aOpcUaServerConnCfg, MultiPaneComponentModown<UaTreeNode> aComponentModown ) {
    super( aContext, aClient, aOpcUaServerConnCfg, aComponentModown );
    // при создании стратегии проверяем и создаем все что нужно для ее (стратегии) работы
    // проверка справочников
    ISkRefbookService rbServ = conn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    // create all essential refbooks: RRI_OPCUA, Cmd_OPCUA, BitMask
    RefbookGenerator rbGenerator = new RefbookGenerator( conn );
    if( rbServ.findRefbook( RefbookGenerator.REFBOOK_CMDS_OPCUA.id() ) == null ) {
      // Cmd_OPCUA
      rbGenerator.createPoligonCommandsRefbook();
    }
    if( rbServ.findRefbook( RefbookGenerator.REFBOOK_RRI_OPCUA.id() ) == null ) {
      // RRI_OPCUA
      rbGenerator.createPoligonRriRefbook();
    }
    if( rbServ.findRefbook( RefbookGenerator.REFBOOK_BITMASK_OPCUA.id() ) == null ) {
      // BitMask
      rbGenerator.createPoligonBitMaskRefbook();
    }
    // проверка раздела НСИ
    ISkRegRefInfoService rriService =
        (ISkRegRefInfoService)conn.coreApi().services().getByKey( ISkRegRefInfoService.SERVICE_ID );
    if( rriService.listSections().isEmpty() ) {
      IOptionSetEdit optSet = new OptionSet();
      IAvMetaConstants.DDEF_IDNAME.setValue( optSet, AvUtils.avStr( DFLT_RRI_SECTION_NAME ) );
      rriService.createSection( DFLT_RRI_SECTION_ID, DFLT_RRI_SECTION_NAME, DFLT_RRI_SECTION_DESCR, optSet );
    }

  }

  @Override
  protected UaTreeNode getClassNode( UaTreeNode aNode ) {
    return aNode;
  }

  @Override
  protected boolean isIgnore4RtData( String aClassId, String aDataId ) {
    // игнорируем узлы для работы с НСИ
    if( aDataId.startsWith( BaseSysdescrGenerator.RRI_PREFIX ) ) {
      return true;
    }
    // игнорируем узлы для работы с командами
    if( aDataId.indexOf( nodeCmdIdBrowseName ) >= 0 ) {
      return true;
    }
    if( aDataId.indexOf( nodeCmdArgFlt ) >= 0 ) {
      return true;
    }
    if( aDataId.indexOf( nodeCmdArgInt ) >= 0 ) {
      return true;
    }
    // последним пунктом проверки на содержание в справочнике НСИ
    return (existInRriRefbook( aClassId, aDataId ) != null);
  }

  @Override
  protected boolean isIgnore4Event( String aClassId, String aEvId ) {
    // игнорируем узлы для работы с командами
    if( aEvId.indexOf( nodeCmdIdBrowseName ) >= 0 ) {
      return true;
    }
    if( aEvId.indexOf( nodeCmdArgFlt ) >= 0 ) {
      return true;
    }
    if( aEvId.indexOf( nodeCmdArgInt ) >= 0 ) {
      return true;
    }
    if( aEvId.indexOf( nodeCmdFeedback ) >= 0 ) {
      return true;
    }

    // последним пунктом проверки на содержание в справочнике НСИ
    return (existInRriRefbook( aClassId, aEvId ) != null);
  }

  @Override
  protected boolean isIgnore4Command( String aClassId, String aCmdId ) {
    // игнорируем узлы которые не могут быть командными
    // TODO
    // последним пунктом проверки на содержание в справочнике НСИ
    return (existInRriRefbook( aClassId, aCmdId ) != null);
  }

  @Override
  protected IList<UaTreeNode> getVariableNodes( UaTreeNode aObjectNode ) {
    IListEdit<UaTreeNode> retVal = new ElemArrayList<>();
    // у Poligone узлы переменных сразу под описанием класса
    retVal.addAll( aObjectNode.getChildren() );
    return retVal;
  }

  @Override
  protected void generateNode2GwidLinks( ITsGuiContext aContext, ISkClassInfo aClassInfo, IList<IDtoObject> aObjList,
      IStridablesList<IDtoRriParamInfo> aRriParamInfoes ) {
    IListEdit<UaNode2Gwid> node2RtdGwidList = new ElemArrayList<>();
    IListEdit<UaNode2Gwid> node2RriGwidList = new ElemArrayList<>();
    IListEdit<UaNode2EventGwid> node2EvtGwidList = new ElemArrayList<>();
    IListEdit<CmdGwid2UaNodes> cmdGwid2UaNodesList = new ElemArrayList<>();
    IListEdit<CmdGwid2UaNodes> rriAttrCmdGwid2UaNodesList = new ElemArrayList<>();
    // в этом месте у нас 100% уже загружено дерево узлов OPC UA
    IList<UaTreeNode> treeNodes = ((OpcUaNodeM5LifecycleManager)componentModown.lifecycleManager()).getCached();
    // идем по списку объектов
    for( IDtoObject obj : aObjList ) {
      // находим родительский UaNode
      Skid parentSkid = new Skid( aClassInfo.id(), obj.id() );

      NodeId parentNodeId = OpcUaUtils.nodeBySkid( aContext, parentSkid, opcUaServerConnCfg );
      // тут отрабатываем ситуацию когда утеряна метаинформация
      if( parentNodeId == null ) {
        @SuppressWarnings( "nls" )
        ETsDialogCode retCode = TsDialogUtils.askYesNoCancel( getShell(),
            "Can't find nodeId for Skid: %s .\n Check section %s in file data-storage.ktor .\n Do you want to continue?",
            parentSkid.toString(), OpcUaUtils
                .getTreeSectionNameByConfig( OpcUaUtils.SECTID_OPC_UA_NODES_2_SKIDS_TEMPLATE, opcUaServerConnCfg ) );
        if( retCode == ETsDialogCode.CANCEL || retCode == ETsDialogCode.CLOSE || retCode == ETsDialogCode.NO ) {
          return;
        }
        continue;
      }
      UaTreeNode parentNode = findParentNode( treeNodes, parentNodeId );
      // привязываем команды
      for( IDtoCmdInfo cmdInfo : aClassInfo.cmds().list() ) {
        CmdGwid2UaNodes cmdGwid2UaNodes = findCmdNodes( obj, cmdInfo, parentNode );
        cmdGwid2UaNodesList.add( cmdGwid2UaNodes );
      }

      // идем по списку его rtdProperties
      for( IDtoRtdataInfo rtdInfo : aClassInfo.rtdata().list() ) {
        // находим свой UaNode
        // сначала ищем в данных битовой маски
        UaTreeNode uaNode = tryBitMaskRtData( aClassInfo, parentNode, rtdInfo );
        if( uaNode == null ) {
          uaNode = findVarNodeByClassGwid( aContext, Gwid.createRtdata( aClassInfo.id(), rtdInfo.id() ),
              parentNode.getChildren() );
        }
        Gwid gwid = Gwid.createRtdata( obj.classId(), obj.id(), rtdInfo.id() );
        if( uaNode != null ) {
          LoggerUtils.defaultLogger().debug( "%s [%s] -> %s", uaNode.getBrowseName(), uaNode.getNodeId(), //$NON-NLS-1$
              gwid.canonicalString() );
          String nodeDescr = parentNode.getBrowseName() + "::" + uaNode.getBrowseName(); //$NON-NLS-1$
          UaNode2Gwid node2Gwid = new UaNode2Gwid( uaNode.getNodeId(), nodeDescr, gwid );
          node2RtdGwidList.add( node2Gwid );
        }
        else {
          LoggerUtils.errorLogger().error( "Can't match rtData: ? -> %s", gwid.canonicalString() ); //$NON-NLS-1$
        }
      }
      // идем по списку его rriProperties
      for( IDtoRriParamInfo aParamInfo : aRriParamInfoes ) {
        if( aParamInfo.isLink() ) {
          continue;
        }
        IDtoAttrInfo attrInfo = aParamInfo.attrInfo();
        // привязываем атрибуты к командным тегам
        CmdGwid2UaNodes rriAttrCmdGwid2UaNodes = findAttrCmdNodes( obj, attrInfo, parentNode );
        rriAttrCmdGwid2UaNodesList.add( rriAttrCmdGwid2UaNodes );

        // находим свой UaNode
        // сначала ищем в данных битовой маски
        UaTreeNode uaNode = tryBitMaskRriAttr( aClassInfo, parentNode, attrInfo );
        if( uaNode == null ) {
          uaNode = findVarNodeByClassGwid( aContext, Gwid.createAttr( aClassInfo.id(), attrInfo.id() ),
              parentNode.getChildren() );
        }
        Gwid gwid = Gwid.createAttr( obj.classId(), obj.id(), attrInfo.id() );
        if( uaNode != null ) {
          LoggerUtils.defaultLogger().debug( "%s [%s] -> RRI attr %s", uaNode.getBrowseName(), uaNode.getNodeId(), //$NON-NLS-1$
              gwid.canonicalString() );
          String nodeDescr = parentNode.getBrowseName() + "::" + uaNode.getBrowseName(); //$NON-NLS-1$
          UaNode2Gwid node2Gwid = new UaNode2Gwid( uaNode.getNodeId(), nodeDescr, gwid );
          // here we are have gwid & nodeId, we should set value and set it in Uskat server
          updateRrriValueUskatServer( uaNode, gwid );
          node2RriGwidList.add( node2Gwid );
        }
        else {
          LoggerUtils.errorLogger().error( "Can't match rtData: ? -> %s", gwid.canonicalString() ); //$NON-NLS-1$
        }
      }
      // идем по списку его events
      for( IDtoEventInfo evtInfo : aClassInfo.events().list() ) {
        // находим свой UaNode
        // сначала ищем в событиях битовой маске
        UaTreeNode uaNode = tryBitMaskEvent( aClassInfo, parentNode, evtInfo );
        if( uaNode == null ) {
          uaNode = findVarNodeByClassGwid( aContext, Gwid.createEvent( aClassInfo.id(), evtInfo.id() ),
              parentNode.getChildren() );
        }
        Gwid gwid = Gwid.createEvent( obj.classId(), obj.id(), evtInfo.id() );
        if( uaNode != null ) {
          LoggerUtils.defaultLogger().debug( "%s [%s] -> %s", uaNode.getBrowseName(), uaNode.getNodeId(), //$NON-NLS-1$
              gwid.canonicalString() );
          String nodeDescr = parentNode.getBrowseName() + "::" + uaNode.getBrowseName(); //$NON-NLS-1$
          IStringListEdit paramIds = new StringArrayList();
          for( String paramId : evtInfo.paramDefs().keys() ) {
            paramIds.add( paramId );
          }
          UaNode2EventGwid node2EventGwid = new UaNode2EventGwid( uaNode.getNodeId(), nodeDescr, gwid, paramIds );
          node2EvtGwidList.add( node2EventGwid );
        }
        else {
          LoggerUtils.errorLogger().error( "Can't match event: ? -> %s", gwid.canonicalString() ); //$NON-NLS-1$
        }
      }
    }
    // заливаем в хранилище. Метод который обновляет только подмножество объектов текущего класса, а
    // остальное сохраняет
    OpcUaUtils.updateNodes2ObjGwidsInStore( aClassInfo.id(), aContext, node2RtdGwidList,
        OpcUaUtils.SECTID_OPC_UA_NODES_2_RTD_GWIDS_TEMPLATE, UaNode2Gwid.KEEPER, opcUaServerConnCfg );
    OpcUaUtils.updateNodes2ObjGwidsInStore( aClassInfo.id(), aContext, node2RriGwidList,
        OpcUaUtils.SECTID_OPC_UA_NODES_2_RRI_GWIDS_TEMPLATE, UaNode2Gwid.KEEPER, opcUaServerConnCfg );
    OpcUaUtils.updateNodes2ObjGwidsInStore( aClassInfo.id(), aContext, node2EvtGwidList,
        OpcUaUtils.SECTID_OPC_UA_NODES_2_EVT_GWIDS_TEMPLATE, UaNode2EventGwid.KEEPER, opcUaServerConnCfg );
    OpcUaUtils.updateCmdGwid2NodesInStore( aContext, cmdGwid2UaNodesList, opcUaServerConnCfg );
    OpcUaUtils.updateRriAttrGwid2NodesInStore( aContext, rriAttrCmdGwid2UaNodesList, opcUaServerConnCfg );
  }

  private void updateRrriValueUskatServer( UaTreeNode aUaNode, Gwid aGwid ) {
    try {
      UaVariableNode dNode = client.getAddressSpace().getVariableNode( aUaNode.getUaNode().getNodeId() );
      DataValue dValue = dNode.readValue();
      Variant value = dValue.getValue();
      IAtomicValue av = convertFromOpc( value );
      // check if Gwid in bitMask refbook, than get bit value from state word
      ISkRefbookItem rbBitMaskItem = existInRriRefbook( aGwid.classId(), aGwid.propId() );
      if( rbBitMaskItem != null ) {
        // get value bit number and extract value
        int bitNumber = rbBitMaskItem.attrs().getInt( RBATRID_BITMASK___BITN );
        int stateWord = av.asInt();
        int bitMask = 0x1 << bitNumber;
        boolean bitValue = false;
        if( (stateWord & bitMask) != 0 ) {
          bitValue = true;
        }
        av = AvUtils.avBool( bitValue );
      }
      ISkRegRefInfoService rriServ = conn.coreApi().getService( ISkRegRefInfoService.SERVICE_ID );
      // TODO get actual section
      ISkRriSection section = rriServ.listSections().first();
      section.setAttrParamValue( aGwid.skid(), aGwid.propId(), av, "auto init on creation" );
    }
    catch( UaException ex ) {
      LoggerUtils.errorLogger().error( ex );
    }
  }

  static IAtomicValue convertFromOpc( Variant aValue ) {
    if( aValue.isNull() ) {
      return IAtomicValue.NULL;
    }
    if( aValue.getValue() instanceof UShort ) {
      UShort ushortVal = (UShort)aValue.getValue();

      return AvUtils.avInt( ushortVal.intValue() );
    }

    if( aValue.getValue() instanceof UByte ) {
      UByte ubytetVal = (UByte)aValue.getValue();

      return AvUtils.avInt( ubytetVal.intValue() );
    }

    IAtomicValue defaultConvertVal = AvUtils.avFromObj( aValue.getValue() );

    if( defaultConvertVal == null ) {
      LoggerUtils.errorLogger().error( "Cant convert from opc '%s' to IAtomicValue",
          aValue.getValue().getClass().getName() );
    }

    return defaultConvertVal;
  }

  /**
   * По описанию команды ищем подходящие UaNodes
   *
   * @param aObj описание объекта
   * @param aCmdInfo описание command
   * @param aObjNode родительский узел описывающий объект
   * @return контейнер с описание узлов или null
   */
  private CmdGwid2UaNodes findCmdNodes( IDtoObject aObj, IDtoCmdInfo aCmdInfo, UaTreeNode aObjNode ) {
    CmdGwid2UaNodes retVal = null;
    Gwid gwid = Gwid.createCmd( aObj.classId(), aObj.id(), aCmdInfo.id() );
    String nodeDescr = aObjNode.getBrowseName();
    String niCmdId = null;
    String niCmdArgInt = null; // может и не быть
    String niCmdArgFlt = null;
    String niCmdFeedback = null;
    // получаем тип аргумента команды
    EAtomicType argType = EAtomicType.NONE;
    if( !aCmdInfo.argDefs().isEmpty() ) {
      argType = aCmdInfo.argDefs().first().atomicType();
    }
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
      case INTEGER, FLOATING, NONE -> new CmdGwid2UaNodes( gwid, nodeDescr, niCmdId, niCmdArgInt, niCmdArgFlt,
          niCmdFeedback, argType );
      case BOOLEAN, STRING, TIMESTAMP, VALOBJ -> throw new TsNotAllEnumsUsedRtException( argType.name() );
    };
    return retVal;
  }

  @Override
  public void ensureBeforeClassCreation() {
    // TODO need refactoring
    ensureCmdDescription();
    ensureBitMaskDescription();
    ensureRriSection( context );
  }

  @Override
  public void ensureBeforeObjsCreation() {
    ensureBitMaskDescription();
    ensureRriSection( context );
  }

  @Override
  protected boolean useRRI() {
    return true;
  }

  /**
   * Читает описание команды и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aDtoClass текущее описание класса
   * @param aVariableNode описание узла типа переменная
   * @param aNode2ClassGwidList список привязок Node -> Class Gwid
   */
  @Override
  protected void readCmdInfo( DtoClassInfo aDtoClass, UaVariableNode aVariableNode,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // nop
    // В этой стратегии команды генератся из только справочника команд
  }

  /**
   * Читает описание события и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aDtoClass текущее описание класса
   * @param aVariableNode описание узла типа переменная
   * @param aNode2ClassGwidList список привязок Node -> Class Gwid
   */
  @Override
  protected void readEventInfo( DtoClassInfo aDtoClass, UaVariableNode aVariableNode,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // nop
    // В этой стратегии события генератся только из справочника событий
  }

  /**
   * Читает описание НСИ атрибута и добавляет его в описание класса {@link IDtoClassInfo}
   *
   * @param aDtoClass текущее описание класса
   * @param aVariableNode описание узла типа переменная
   * @param aNode2ClassGwidList список для хранения привязки node -> Class Gwid
   */
  @Override
  protected void readRriAttrInfo( DtoClassInfo aDtoClass, UaVariableNode aVariableNode,
      IListEdit<UaNode2Gwid> aNode2ClassGwidList ) {
    // id атрибута
    String attrId = aVariableNode.getBrowseName().getName();
    // if( isIgnore4RriAttr( aDtoClass.id(), attrId ) ) {
    // return;
    // }
    // соблюдаем соглашения о наименовании
    if( !attrId.startsWith( RRI_PREFIX ) ) {
      attrId = RRI_PREFIX + attrId;
    }
    // работаем ТОЛЬКО с теми атрибутами которые есть в справочнике команд НСИ
    if( !inRriRefbook( aDtoClass.id(), attrId ) ) {
      return;
    }
    // название
    String name = aVariableNode.getDisplayName().getText();
    // описание
    String descr = aVariableNode.getDescription() == null ? name : aVariableNode.getDescription().getText();
    // описание
    if( (descr == null) || descr.isBlank() ) {
      descr = name;
    }

    // тип данных атрибута
    Class<?> clazz = OpcUaUtils.getNodeDataTypeClass( aVariableNode );
    EAtomicType type = OpcUaUtils.getAtomicType( clazz );
    DataType ddType = DataType.create( type );

    IDtoAttrInfo atrInfo = DtoAttrInfo.create2( attrId, ddType, //
        TSID_NAME, name, //
        TSID_DESCRIPTION, descr );

    aDtoClass.attrInfos().add( atrInfo );

    // TODO boilerplate code сохраним привязку для использования в автоматическом связывании
    NodeId nodeId = aVariableNode.getNodeId();
    Gwid classGwid = Gwid.createAttr( aDtoClass.id(), attrId );
    UaNode2Gwid uaNode2Gwid = new UaNode2Gwid( nodeId.toParseableString(), descr, classGwid );
    aNode2ClassGwidList.add( uaNode2Gwid );
  }

  /**
   * Проверяет наличие в справочнике RBID_RRI_OPCUA элемента с заданным RRI param id
   *
   * @param aClassId - префикс составного strid
   * @param aRriParamId - RRI param id
   * @return true если элемент с таким strid есть в справочнике
   */
  protected boolean inRriRefbook( String aClassId, String aRriParamId ) {
    // читаем справочник НСИ и фильтруем то что предназначено для этого
    ISkRefbookService skRefServ = (ISkRefbookService)conn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    IList<ISkRefbookItem> rbItems = skRefServ.findRefbook( RBID_RRI_OPCUA ).listItems();
    // создаем id элемента справочника
    for( ISkRefbookItem rbItem : rbItems ) {
      String paramId = rbItem.attrs().getValue( RBATRID_RRI_OPCUA___RRIID ).asString();
      if( rbItem.strid().startsWith( aClassId ) && paramId.compareTo( aRriParamId ) == 0 ) {
        return true;
      }
    }
    return false;
  }

}
