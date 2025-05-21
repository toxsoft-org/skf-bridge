package org.toxsoft.skf.bridge.cfg.opcua.gui.strategy;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skf.rri.lib.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;

/**
 * Class for startegy to generate system description from Poligone OPC UA tree.
 * <p/>
 *
 * @author dima
 */
public class PoligoneSysdescrGenerator
    extends BaseSysdescrGenerator {

  /**
   * Constructor.
   *
   * @param aContext app context
   * @param aClient - OPC UA server
   * @param aOpcUaServerConnCfg - OPC UA server connection settings
   */
  public PoligoneSysdescrGenerator( ITsGuiContext aContext, OpcUaClient aClient,
      IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    super( aContext, aClient, aOpcUaServerConnCfg, EOPCUATreeType.POLIGONE );
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
    return existInRriRefbook( aClassId, aDataId );
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
    return existInRriRefbook( aClassId, aEvId );
  }

  @Override
  protected boolean isIgnore4Command( String aClassId, String aCmdId ) {
    // игнорируем узлы которые не могут быть командными
    // TODO
    // последним пунктом проверки на содержание в справочнике НСИ
    return existInRriRefbook( aClassId, aCmdId );
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

}
