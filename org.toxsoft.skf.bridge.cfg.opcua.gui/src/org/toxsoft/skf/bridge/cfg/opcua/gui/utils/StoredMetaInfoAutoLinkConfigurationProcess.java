package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.utils.OpcUaUtils.*;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.threadexec.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.impl.*;

/**
 * Implementation of auto link process using meta info, stored in workroom
 *
 * @author max
 */
public class StoredMetaInfoAutoLinkConfigurationProcess
    implements IAutoLinkConfigurationProcess {

  private final static String CFG_CMD_UNIT_ID_FORMAT = "opctos5.bridge.cfg.cmd.unit.id%d.%s";

  private final static String CFG_DATA_UNIT_ID_FORMAT = "opctos5.bridge.cfg.data.unit.id%d.%s.%s";

  private final static String CFG_RRI_UNIT_ID_FORMAT = "opctos5.bridge.cfg.rri.unit.id%d.%s.%s";

  private final static String CFG_EVENT_UNIT_ID_FORMAT = "opctos5.bridge.cfg.event.unit.id%d.%s.%s";

  @Override
  public IList<OpcToS5DataCfgUnit> formCfgUnitsFromAutoElements( ITsGuiContext aContext, ISkConnection currConn,
      OpcUaServerConnCfg conConf ) {
    IListEdit<OpcToS5DataCfgUnit> result = new ElemArrayList<>();
    ITsThreadExecutor threadExecutor = SkThreadExecutorService.getExecutor( currConn.coreApi() );
    threadExecutor.syncExec( () -> {
      IStringMap<IStringMap<Integer>> cmdOpcCodes = OpcUaUtils.readClass2CmdIdx( currConn );
      // IStringMap<IStringMap<Integer>> cmdOpcCodes = new StringMap<>();
      // String cmdFileDescr = getDescrFile(
      // org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.SELECT_FILE_4_IMPORT_CMD );
      // if( cmdFileDescr != null ) {
      // File file = new File( cmdFileDescr );
      // try {
      // cmdOpcCodes = Ods2DtoCmdInfoParser.parseOpcCmdCodes( file );
      // // TODO ensure cmdIndex refbook
      //
      // TsDialogUtils.info( getShell(), MSG_LOADED_CMDS_DESCR, cmdFileDescr );
      // }
      // catch( IOException ex ) {
      // LoggerUtils.errorLogger().error( ex );
      // }
      // }

      // dima 07.02.24 работаем теперь через справочники
      StringMap<StringMap<IList<BitIdx2DtoRtData>>> clsId2RtDataInfoes = OpcUaUtils.readRtDataInfoes( currConn );
      StringMap<StringMap<IList<BitIdx2RriDtoAttr>>> clsId2RriAttrInfoes = OpcUaUtils.readRriAttrInfoes( currConn );
      StringMap<StringMap<IList<BitIdx2DtoEvent>>> clsId2EventInfoes = OpcUaUtils.readEventInfoes( currConn );
      // StringMap<StringMap<IList<BitIdx2DtoRtData>>> clsId2RtDataInfoes = new StringMap<>();
      // StringMap<StringMap<IList<BitIdx2RriDtoAttr>>> clsId2RriAttrInfoes = new StringMap<>();
      //
      // String bitRtdataFileDescr = getDescrFile(
      // org.toxsoft.skf.bridge.cfg.opcua.gui.panels.ISkResources.SELECT_FILE_4_IMPORT_BIT_RTDATA );
      // if( bitRtdataFileDescr != null ) {
      // File file = new File( bitRtdataFileDescr );
      // try {
      // Ods2DtoRtDataInfoParser.parse( file );
      // clsId2RtDataInfoes = Ods2DtoRtDataInfoParser.getRtdataInfoesMap();
      // clsId2EventInfoes = Ods2DtoRtDataInfoParser.getEventInfoesMap();
      // clsId2RriAttrInfoes = Ods2DtoRtDataInfoParser.getRriAttrInfoesMap();
      // TsDialogUtils.info( getShell(), MSG_LOADED_BIT_MASKS_DESCR, bitRtdataFileDescr );
      // }
      // catch( IOException ex ) {
      // LoggerUtils.errorLogger().error( ex );
      // }
      // }

      CfgUnitRealizationTypeRegister typeReg2 = aContext.get( CfgUnitRealizationTypeRegister.class );
      // m5().tsContext().get( CfgUnitRealizationTypeRegister.class );

      // simple cmds (as events) try
      // Max for bkn

      // String cmdSectId = getTreeSectionNameByConfig( SECTID_OPC_UA_NODES_2_BKN_CMD_GWIDS_TEMPLATE, conConf );
      // IList<UaNode2EventGwid> autoCmdEvents = loadNodes2Gwids( aContext, cmdSectId, UaNode2EventGwid.KEEPER );
      //
      // System.out.println( "Auto simple cmd elements size = " + autoCmdEvents.size() ); //$NON-NLS-1$
      // for( UaNode2EventGwid cmd2Nodes : autoCmdEvents ) {
      // String strid = String.format( CFG_CMD_UNIT_ID_FORMAT, Long.valueOf( System.currentTimeMillis() ),
      // cmd2Nodes.gwid().strid() );
      // ECfgUnitType type = ECfgUnitType.COMMAND;
      //
      // IList<Gwid> gwids = new ElemArrayList<>( cmd2Nodes.gwid() );
      // String cmdArgParam = null;
      // IListEdit<NodeId> nodes = new ElemArrayList<>();
      // nodes.add( cmd2Nodes.getNodeId() );
      //
      // String name = STR_LINK_PREFIX + cmd2Nodes.gwid().canonicalString();
      //
      // OpcToS5DataCfgUnit unit = new OpcToS5DataCfgUnit( strid, name, gwids, convertNodeListToAtomicList( nodes ) );
      //
      // ICfgUnitRealizationType realType =
      // typeReg2.getTypeOfRealizationById( type, CFG_UNIT_REALIZATION_TYPE_VALUE_COMMAND );
      //
      // unit.setTypeOfCfgUnit( type );
      // unit.setRelizationTypeId( realType.id() );
      //
      // OptionSet realization = new OptionSet();
      // OpcUaUtils.OP_CMD_JAVA_CLASS.setValue( realization, avStr( COMMANDS_JAVA_CLASS_VALUE_COMMAND_EXEC ) );
      //
      // if( cmd2Nodes.paramIds() != null && cmd2Nodes.paramIds().size() == 1 ) {
      // OpcUaUtils.OP_CMD_VALUE_PARAM_ID.setValue( realization, avStr( cmd2Nodes.paramIds().first() ) );
      // }
      //
      // unit.setRealizationOpts( realization );
      //
      // result.add( unit );
      // }

      // Commands
      // dima

      IList<CmdGwid2UaNodes> autoElements = OpcUaUtils.loadCmdGwid2Nodes( aContext, conConf );
      System.out.println( "Auto cmd elements size = " + autoElements.size() ); //$NON-NLS-1$
      for( CmdGwid2UaNodes cmd2Nodes : autoElements ) {

        IList<Gwid> gwids = new ElemArrayList<>( cmd2Nodes.gwid() );
        String cmdArgParam = null;
        IListEdit<NodeId> nodes = new ElemArrayList<>();
        nodes.add( cmd2Nodes.getNodeCmdId() );
        // dima 08.02.24 сразу заносим все ноды аргументов
        nodes.add( cmd2Nodes.getNodeCmdArgInt() == null ? NodeId.NULL_VALUE : cmd2Nodes.getNodeCmdArgInt() );
        nodes.add( cmd2Nodes.getNodeCmdArgFlt() == null ? NodeId.NULL_VALUE : cmd2Nodes.getNodeCmdArgFlt() );
        switch( cmd2Nodes.argType() ) {
          case INTEGER:
            cmdArgParam = Ods2DtoCmdInfoParser.CMD_ARG_INT_ID;
            break;
          case FLOATING:
            cmdArgParam = Ods2DtoCmdInfoParser.CMD_ARG_FLT_ID;
            break;
          case BOOLEAN:
          case NONE:
          case STRING:
          case TIMESTAMP:
          case VALOBJ:
          default:
            break;
        }
        // old version
        // if( cmd2Nodes.getNodeCmdArgInt() != null ) {
        // nodes.add( cmd2Nodes.getNodeCmdArgInt() );
        // cmdArgParam = Ods2DtoCmdInfoParser.CMD_ARG_INT_ID;
        // }
        // else
        // if( cmd2Nodes.getNodeCmdArgFlt() != null ) {
        // nodes.add( cmd2Nodes.getNodeCmdArgFlt() );
        // cmdArgParam = Ods2DtoCmdInfoParser.CMD_ARG_FLT_ID;
        // }
        nodes.add( cmd2Nodes.getNodeCmdFeedback() );

        String strid = String.format( CFG_CMD_UNIT_ID_FORMAT, Long.valueOf( System.currentTimeMillis() ),
            cmd2Nodes.gwid().strid() );
        ECfgUnitType type = ECfgUnitType.COMMAND;

        ICfgUnitRealizationType realType =
            typeReg2.getTypeOfRealizationById( type, CFG_UNIT_REALIZATION_TYPE_VALUE_COMMAND_BY_ONE_TAG );
        OptionSet realization = new OptionSet();
        OpcUaUtils.OP_CMD_JAVA_CLASS.setValue( realization,
            avStr( COMMANDS_JAVA_CLASS_VALUE_COMMAND_BY_ONE_TAG_EXEC ) );
        if( cmdArgParam != null ) {
          OpcUaUtils.OP_CMD_VALUE_PARAM_ID.setValue( realization, avStr( cmdArgParam ) );
        }
        int cmdOpcCode = 1;

        if( cmdOpcCodes.hasKey( cmd2Nodes.gwid().classId() ) ) {
          IStringMap<Integer> classCodes = cmdOpcCodes.getByKey( cmd2Nodes.gwid().classId() );

          if( classCodes.hasKey( cmd2Nodes.gwid().propId() ) ) {
            cmdOpcCode = classCodes.getByKey( cmd2Nodes.gwid().propId() ).intValue();
          }
        }

        OpcUaUtils.OP_CMD_OPC_ID.setValue( realization, avInt( cmdOpcCode ) );

        String name = STR_LINK_PREFIX + cmd2Nodes.gwid().asString();

        OpcToS5DataCfgUnit unit = new OpcToS5DataCfgUnit( strid, name, gwids, convertNodeListToAtomicList( nodes ) );

        unit.setTypeOfCfgUnit( type );
        unit.setRelizationTypeId( realType.id() );
        unit.setRealizationOpts( realization );

        result.add( unit );
        // ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).addCfgUnit( result );
      }
      // Data
      IList<UaNode2Gwid> nodes2Gwids = OpcUaUtils.loadNodes2RtdGwids( aContext, conConf );
      for( UaNode2Gwid dataNode : nodes2Gwids ) {

        // битовый индекс для данного
        BitIdx2DtoRtData bitIndex = OpcUaUtils.getDataBitIndexForRtDataGwid( dataNode.gwid(), clsId2RtDataInfoes );

        Gwid gwid = dataNode.gwid();
        IList<Gwid> gwids = new ElemArrayList<>( gwid );

        IListEdit<NodeId> nodes = new ElemArrayList<>( dataNode.getNodeId() );

        String strid = String.format( CFG_DATA_UNIT_ID_FORMAT, Long.valueOf( System.currentTimeMillis() ), gwid.strid(),
            gwid.propId() );

        ECfgUnitType type = ECfgUnitType.DATA;

        ICfgUnitRealizationType realType =
            typeReg2.getTypeOfRealizationById( type, bitIndex == null ? CFG_UNIT_REALIZATION_TYPE_ONT_TO_ONE_DATA
                : CFG_UNIT_REALIZATION_TYPE_ONE_INT_TO_ONE_BIT_DATA );

        OptionSet realization = new OptionSet( realType.getDefaultValues() );
        if( bitIndex != null ) {
          OpcUaUtils.OP_BIT_INDEX.setValue( realization, avInt( bitIndex.bitIndex() ) );
        }

        ISkCoreApi api = currConn.coreApi();
        ISkSysdescr sysDescr = api.sysdescr();
        ISkClassInfo classInfo = sysDescr.getClassInfo( gwid.classId() );
        ISkClassProps<IDtoRtdataInfo> dataInfoes = classInfo.rtdata();
        IDtoRtdataInfo dataInfo = dataInfoes.list().getByKey( gwid.propId() );
        // changed by dima 04.07.25
        long syncPeriod = dataInfo.syncDataDeltaT();
        // fool proof
        if( dataInfo.isSync() && syncPeriod == 0 ) {
          syncPeriod = 1000;
        }
        OpcUaUtils.OP_SYNCH_PERIOD.setValue( realization, avInt( syncPeriod ) );
        // OpcUaUtils.OP_SYNCH_PERIOD.setValue( realization, avInt( dataInfo.isSync() ? 1000 : 0 ) );

        OpcUaUtils.OP_IS_CURR.setValue( realization, avBool( dataInfo.isCurr() ) );
        OpcUaUtils.OP_IS_HIST.setValue( realization, avBool( dataInfo.isHist() ) );

        String name = STR_LINK_PREFIX + gwid.asString();

        OpcToS5DataCfgUnit unit = new OpcToS5DataCfgUnit( strid, name, gwids, convertNodeListToAtomicList( nodes ) );

        unit.setTypeOfCfgUnit( type );
        unit.setRelizationTypeId( realType.id() );
        unit.setRealizationOpts( realization );

        result.add( unit );
        // ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).addCfgUnit( result );
      }

      // dima 18.01.24 RRI attrs
      nodes2Gwids = OpcUaUtils.loadNodes2RriGwids( aContext, conConf );
      for( UaNode2Gwid rriAttrNode : nodes2Gwids ) {

        // битовый индекс для rriAttr
        BitIdx2RriDtoAttr bitIndex =
            OpcUaUtils.getDataBitIndexForRriAttrGwid( rriAttrNode.gwid(), clsId2RriAttrInfoes );

        Gwid gwid = rriAttrNode.gwid();
        IList<Gwid> gwids = new ElemArrayList<>( gwid );

        IListEdit<NodeId> nodes = new ElemArrayList<>( rriAttrNode.getNodeId() );

        // добиваем тут командные ноды
        CmdGwid2UaNodes rriAttrCmdNode = getRriAttrCmdNode( aContext, gwid, conConf );
        // FIXME dima просто заплатка на текущий момент
        if( rriAttrCmdNode != null ) {
          nodes.add( rriAttrCmdNode.getNodeCmdId() );
          nodes
              .add( rriAttrCmdNode.getNodeCmdArgInt() == null ? NodeId.NULL_VALUE : rriAttrCmdNode.getNodeCmdArgInt() );
          nodes
              .add( rriAttrCmdNode.getNodeCmdArgFlt() == null ? NodeId.NULL_VALUE : rriAttrCmdNode.getNodeCmdArgFlt() );
          nodes.add( rriAttrCmdNode.getNodeCmdFeedback() );
        }

        String strid = String.format( CFG_RRI_UNIT_ID_FORMAT, Long.valueOf( System.currentTimeMillis() ), gwid.strid(),
            gwid.propId() );

        ECfgUnitType type = ECfgUnitType.RRI;

        ICfgUnitRealizationType realType =
            typeReg2.getTypeOfRealizationById( type, bitIndex == null ? CFG_UNIT_REALIZATION_TYPE_ONE_TO_ONE_RRI
                : CFG_UNIT_REALIZATION_TYPE_ONE_INT_TO_ONE_BIT_RRI );

        OptionSet realization = new OptionSet( realType.getDefaultValues() );
        if( bitIndex != null ) {
          OpcUaUtils.OP_BIT_INDEX.setValue( realization, avInt( bitIndex.bitIndex() ) );
        }

        String name = STR_LINK_PREFIX + gwid.asString();

        OpcToS5DataCfgUnit unit = new OpcToS5DataCfgUnit( strid, name, gwids, convertNodeListToAtomicList( nodes ) );
        unit.setTypeOfCfgUnit( type );
        unit.setRelizationTypeId( realType.id() );
        unit.setRealizationOpts( realization );

        result.add( unit );
        // ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).addCfgUnit( result );
      }

      // events
      IList<UaNode2EventGwid> autoEvents = OpcUaUtils.loadNodes2EvtGwids( aContext, conConf );

      for( UaNode2Gwid evtNode : autoEvents ) {
        Gwid gwid = evtNode.gwid();
        BitIdx2DtoEvent bitIndex = OpcUaUtils.getBitIndexForEvtGwid( gwid, clsId2EventInfoes );

        IList<Gwid> gwids = new ElemArrayList<>( gwid );

        IListEdit<NodeId> nodes = new ElemArrayList<>( evtNode.getNodeId() );

        String strid = String.format( CFG_EVENT_UNIT_ID_FORMAT, Long.valueOf( System.currentTimeMillis() ),
            gwid.strid(), gwid.propId() );

        ECfgUnitType type = ECfgUnitType.EVENT;

        ICfgUnitRealizationType realType = typeReg2.getTypeOfRealizationById( type,
            bitIndex == null ? OpcUaUtils.CFG_UNIT_REALIZATION_TYPE_TAG_VALUE_CHANGED
                : OpcUaUtils.CFG_UNIT_REALIZATION_TYPE_BIT_SWITCH_EVENT );

        OptionSet realization = new OptionSet( realType.getDefaultValues() );

        if( bitIndex != null ) {
          int index = bitIndex.bitIndex();
          boolean genUp = bitIndex.isGenerateUp();
          boolean genDn = bitIndex.isGenerateDn();

          OpcUaUtils.OP_BIT_INDEX.setValue( realization, avInt( index ) );
          OpcUaUtils.OP_CONDITION_SWITCH_ON.setValue( realization, avBool( genUp ) );
          OpcUaUtils.OP_CONDITION_SWITCH_OFF.setValue( realization, avBool( genDn ) );
        }

        String name = STR_LINK_PREFIX + gwid.asString();

        OpcToS5DataCfgUnit unit = new OpcToS5DataCfgUnit( strid, name, gwids, convertNodeListToAtomicList( nodes ) );

        unit.setTypeOfCfgUnit( type );
        unit.setRelizationTypeId( realType.id() );
        unit.setRealizationOpts( realization );

        result.add( unit );
        // ((OpcToS5DataCfgUnitM5LifecycleManager)lifecycleManager()).addCfgUnit( result );
      }
    } );
    return result;
  }

  private static CmdGwid2UaNodes getRriAttrCmdNode( ITsGuiContext aContext, Gwid aGwid, OpcUaServerConnCfg aConConf ) {
    CmdGwid2UaNodes retVal = null;
    IList<CmdGwid2UaNodes> rriAttrCmdList = loadRriAttrGwid2Nodes( aContext, aConConf );
    for( CmdGwid2UaNodes rriAttrCmd : rriAttrCmdList ) {
      if( rriAttrCmd.gwid().skid().equals( aGwid.skid() ) ) {
        retVal = rriAttrCmd;
        break;
      }
    }
    return retVal;
  }
}
