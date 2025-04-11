package org.toxsoft.skf.bridge.cfg.opcua.gui.strategy;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skf.rri.lib.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;

/**
 * Class for startegy to generate system description from Siemens-Baikonгr OPC UA tree.
 * <p/>
 *
 * @author dima
 */
public class BaikonurSiemensSysdescrGenerator
    extends BaseSysdescrGenerator {

  /**
   * Constructor.
   *
   * @param aContext app context
   * @param aClient - OPC UA server
   * @param aOpcUaServerConnCfg - OPC UA server connection settings
   */
  public BaikonurSiemensSysdescrGenerator( ITsGuiContext aContext, OpcUaClient aClient,
      IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    super( aContext, aClient, aOpcUaServerConnCfg, EOPCUATreeType.SIEMENS_BAIKONUR );
  }

  @Override
  protected UaTreeNode getClassNode( UaTreeNode aNode ) {
    return aNode;
  }

  @Override
  protected boolean isIgnore4RtData( String aClassId, String aDataId ) {
    return false;
  }

  @Override
  protected boolean isIgnore4Event( String aClassId, String aEvId ) {
    return false;
  }

  @Override
  protected boolean isIgnore4Command( String aClassId, String aCmdId ) {
    return false;
  }

  @Override
  protected void generateNode2GwidLinks( ITsGuiContext aContext, ISkClassInfo aClassInfo, IList<IDtoObject> aObjList,
      IStridablesList<IDtoRriParamInfo> aRriParamInfoes ) {
    IListEdit<UaNode2Gwid> node2RtdGwidList = new ElemArrayList<>();
    IListEdit<UaNode2EventGwid> node2EvtGwidList = new ElemArrayList<>();
    IListEdit<UaNode2EventGwid> node2CmdGwidList = new ElemArrayList<>();
    // в этом месте у нас 100% уже загружено дерево узлов OPC UA
    IList<UaTreeNode> treeNodes = ((OpcUaNodeM5LifecycleManager)componentModown.lifecycleManager()).getCached();
    // идем по списку объектов
    for( IDtoObject obj : aObjList ) {
      // находим родительский UaNode
      // UaTreeNode parentNode = aItemProvider.nodeById( obj.id() );
      Skid parentSkid = new Skid( aClassInfo.id(), obj.id() );
      // OpcUaUtils.getTreeSectionNameByConfig( OpcUaUtils.SECTID_OPC_UA_NODES_2_SKIDS_TEMPLATE, opcUaServerConnCfg );

      NodeId parentNodeId = OpcUaUtils.nodeBySkid( aContext, parentSkid, opcUaServerConnCfg );
      // тут отрабатываем ситуацию когда утеряна метаинформация
      if( parentNodeId == null ) {
        ETsDialogCode retCode = TsDialogUtils.askYesNoCancel( getShell(),
            "Can't find nodeId for Skid: %s .\n Check section %s in file data-storage.ktor .\n Do you want to continue?", //$NON-NLS-1$
            parentSkid.toString(), OpcUaUtils
                .getTreeSectionNameByConfig( OpcUaUtils.SECTID_OPC_UA_NODES_2_SKIDS_TEMPLATE, opcUaServerConnCfg ) );
        if( retCode == ETsDialogCode.CANCEL || retCode == ETsDialogCode.CLOSE || retCode == ETsDialogCode.NO ) {
          return;
        }
        continue;
      }
      // old version
      // TsIllegalStateRtException.checkNull( parentNodeId,
      // "Can't find nodeId for Skid: %s .\n Check section %s in file data-storage.ktor", parentSkid.toString(),
      // OpcUaUtils.getTreeSectionNameByConfig( OpcUaUtils.SECTID_OPC_UA_NODES_2_SKIDS_TEMPLATE,
      // opcUaServerConnCfg ) );
      UaTreeNode parentNode = findParentNode( treeNodes, parentNodeId );
      // привязываем команды
      for( IDtoCmdInfo cmdInfo : aClassInfo.cmds().list() ) {
        // находим свой UaNode
        UaTreeNode uaNode = findVarNodeByClassGwid( aContext, Gwid.createCmd( aClassInfo.id(), cmdInfo.id() ),
            parentNode.getChildren() );

        Gwid gwid = Gwid.createCmd( obj.classId(), obj.id(), cmdInfo.id() );
        if( uaNode != null ) {
          LoggerUtils.defaultLogger().debug( "%s [%s] -> %s", uaNode.getBrowseName(), uaNode.getNodeId(), //$NON-NLS-1$
              gwid.canonicalString() );
          String nodeDescr = parentNode.getBrowseName() + "::" + uaNode.getBrowseName(); //$NON-NLS-1$
          IStringListEdit argIds = new StringArrayList();
          for( String argId : cmdInfo.argDefs().keys() ) {
            argIds.add( argId );
          }
          UaNode2EventGwid node2BknCmdGwid = new UaNode2EventGwid( uaNode.getNodeId(), nodeDescr, gwid, argIds );
          node2CmdGwidList.add( node2BknCmdGwid );
        }
        else {
          LoggerUtils.errorLogger().error( "Can't match Baikonur cmd: ? -> %s", gwid.canonicalString() ); //$NON-NLS-1$
        }
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
    // заливаем в хранилище
    OpcUaUtils.updateNodes2GwidsInStore( aContext, node2RtdGwidList,
        OpcUaUtils.SECTID_OPC_UA_NODES_2_RTD_GWIDS_TEMPLATE, UaNode2Gwid.KEEPER, opcUaServerConnCfg );
    OpcUaUtils.updateNodes2GwidsInStore( aContext, node2EvtGwidList,
        OpcUaUtils.SECTID_OPC_UA_NODES_2_EVT_GWIDS_TEMPLATE, UaNode2EventGwid.KEEPER, opcUaServerConnCfg );
    // for Baikonur
    OpcUaUtils.updateNodes2GwidsInStore( aContext, node2CmdGwidList,
        OpcUaUtils.SECTID_OPC_UA_NODES_2_BKN_CMD_GWIDS_TEMPLATE, UaNode2EventGwid.KEEPER, opcUaServerConnCfg );
  }

  @Override
  protected IList<UaTreeNode> getVariableNodes( UaTreeNode aObjectNode ) {
    IListEdit<UaTreeNode> retVal = new ElemArrayList<>();
    // у Siemens для Байконура узлы переменных находятся ниже подузлов Inputs/Outputs
    for( UaTreeNode parentNode : aObjectNode.getChildren() ) {
      for( UaTreeNode childNode : parentNode.getChildren() ) {
        if( childNode.getNodeClass().equals( NodeClass.Variable ) ) {
          retVal.add( childNode );
        }
      }
    }
    return retVal;
  }

}
