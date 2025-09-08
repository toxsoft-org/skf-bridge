package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.impl.DataDef.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IOpcUaServerConnCfgConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.skide.IGreenWorldRefbooks.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.utils.ISkResources.*;
import static org.toxsoft.skide.plugin.exconn.ISkidePluginExconnSharedResources.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.function.*;
import java.util.regex.*;

import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.operation.*;
import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.api.config.*;
import org.eclipse.milo.opcua.sdk.client.api.identity.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.stack.core.security.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.eclipse.milo.opcua.stack.core.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.log4j.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.list.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.av.opset.impl.OptionSet;
import org.toxsoft.core.tslib.bricks.apprefs.*;
import org.toxsoft.core.tslib.bricks.apprefs.impl.*;
import org.toxsoft.core.tslib.bricks.geometry.impl.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.core.txtproj.lib.storage.*;
import org.toxsoft.core.txtproj.lib.workroom.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.bridge.cfg.opcua.service.impl.*;
import org.toxsoft.skf.refbooks.lib.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Utils of OPC UA server connections.
 *
 * @author max
 * @author dima
 */
public class OpcUaUtils {

  /**
   * Журнал работы
   */
  private static ILogger logger = LoggerWrapper.getLogger( OpcUaUtils.class.getName() );

  private static final String DLMCFG_FILE_EXTENTION = ".dlmcfg"; //$NON-NLS-1$

  private static final String DEVCFG_FILE_EXTENTION = ".devcfg"; //$NON-NLS-1$

  private static final String DEV_CFG_UNIX_FILE_RELETIVE_PATH_AND_NAME_FORMAT = "/cfg/hal/thds/%s%s";     //$NON-NLS-1$
  private static final String DEV_CFG_WIN_FILE_RELETIVE_PATH_AND_NAME_FORMAT  = "\\cfg\\hal\\thds\\%s%s"; //$NON-NLS-1$

  private static final String DLM_CFG_UNIX_FILE_RELETIVE_PATH_AND_NAME_FORMAT = "/cfg/dlms/%s%s";    //$NON-NLS-1$
  private static final String DLM_CFG_WIN_FILE_RELETIVE_PATH_AND_NAME_FORMAT  = "\\cfg\\dlms\\%s%s"; //$NON-NLS-1$

  /**
   * Параметр события: включен.
   * <p>
   * Параметр имеет тип {@link EAtomicType#BOOLEAN}.
   */
  private static final String EVPID_ON = "on"; //$NON-NLS-1$

  public static final String COMMANDS_JAVA_CLASS_VALUE_COMMAND_BY_COMPLEX_TAG_EXEC =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.commands.ValCommandByComplexTagExec"; //$NON-NLS-1$

  public static final String COMMANDS_JAVA_CLASS_VALUE_COMMAND_BY_ONE_TAG_EXEC =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.commands.ValCommandByOneTagWithParamExec"; //$NON-NLS-1$

  public static final String CFG_UNIT_REALIZATION_TYPE_VALUE_COMMAND_BY_ONE_TAG = "val.command.one.tag"; //$NON-NLS-1$

  public static final String CFG_UNIT_REALIZATION_TYPE_VALUE_COMMAND = "value.command"; //$NON-NLS-1$

  public static final String COMMANDS_JAVA_CLASS_VALUE_COMMAND_EXEC =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.commands.ValueCommandExec"; //$NON-NLS-1$

  private static final String DATA_JAVA_CLASS_ONE_TO_ONE_DATA_TRANSMITTER_FACTORY =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.data.OneToOneDataTransmitterFactory"; //$NON-NLS-1$

  private static final String JAVA_CLASS_ONE_TO_ONE_RRI_ATTR_TRANSMITTER_FACTORY =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.rri.OneToOneRriDataTransmitterFactory"; //$NON-NLS-1$

  private static final String EVENTS_ONE_TAG_CHANGED_PARAM_FORMER =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OneTagToChangedParamFormer"; //$NON-NLS-1$

  private static final String EVENTS_ONE_TAG_TO_ONE_PARAM_FORMER =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OneTagToOneParamFormer"; //$NON-NLS-1$

  private static final String EVENTS_JAVA_CLASS_TAG_SWITCH_CONDITION =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OneTagSwitchEventCondition"; //$NON-NLS-1$

  private static final String EVENTS_JAVA_CLASS_TAG_VALUE_CHANGED_CONDITION =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OneTagChangedEventCondition"; //$NON-NLS-1$

  private static final String EVENTS_JAVA_CLASS_OPC_TAGS_EVENT_SENDER =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.events.OpcTagsEventSender"; //$NON-NLS-1$

  public static final String CFG_UNIT_REALIZATION_TYPE_TAG_VALUE_CHANGED = "opc.tags.event.sender.tag.value.changed"; //$NON-NLS-1$

  public static final String CFG_UNIT_REALIZATION_TYPE_BIT_SWITCH_EVENT = "opc.tags.event.sender.bit.switch"; //$NON-NLS-1$

  public static final String CFG_UNIT_REALIZATION_TYPE_SWITCH_EVENT = "opc.tags.event.sender.switch"; //$NON-NLS-1$

  public static final String CFG_UNIT_REALIZATION_TYPE_ONT_TO_ONE_DATA = "ont.to.one.data"; //$NON-NLS-1$

  public static final String CFG_UNIT_REALIZATION_TYPE_ONE_TO_ONE_RRI = "one.to.one.rri"; //$NON-NLS-1$

  public static final String CFG_UNIT_REALIZATION_TYPE_ONE_INT_TO_ONE_BIT_DATA = "int.to.byte.data"; //$NON-NLS-1$

  public static final String CFG_UNIT_REALIZATION_TYPE_ONE_INT_TO_ONE_BIT_RRI = "int.to.bit.rri"; //$NON-NLS-1$

  private static final String DATA_JAVA_CLASS_ONE_INT_TO_ONE_BIT_DATA_TRANSMITTER_FACTORY =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.data.SingleIntToSingleBoolDataTransmitterFactory"; //$NON-NLS-1$

  private static final String JAVA_CLASS_ONE_INT_TO_ONE_BIT_RRI_ATTR_TRANSMITTER_FACTORY =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.rri.SingleIntToSingleBoolRriDataTransmitterFactory"; //$NON-NLS-1$

  /**
   * template for id secton for cached OPC UA nodes
   */
  private static final String SECTID_OPC_UA_NODES_TEMPLATE = "cached.opc.ua.nodes"; //$NON-NLS-1$

  /**
   * template for id secton for store links UaNode->Skid meta info
   */
  public static final String SECTID_OPC_UA_NODES_2_SKIDS_TEMPLATE = "opc.ua.nodes2skids"; //$NON-NLS-1$

  /**
   * template for id secton for store links UaNode->RtdGwid
   */
  public static final String SECTID_OPC_UA_NODES_2_RTD_GWIDS_TEMPLATE = "opc.ua.nodes2rtd.gwids"; //$NON-NLS-1$

  /**
   * template for id secton for store links UaNode->RriGwid
   */
  public static final String SECTID_OPC_UA_NODES_2_RRI_GWIDS_TEMPLATE = "opc.ua.nodes2rri.gwids"; //$NON-NLS-1$

  /**
   * template for id secton for store links UaNode->ClassGwid
   */
  public static final String SECTID_OPC_UA_NODES_2_CLS_GWIDS_TEMPLATE = "opc.ua.nodes2class.gwids"; //$NON-NLS-1$

  /**
   * template for id secton for store links UaNode->EvtGwid
   */
  public static final String SECTID_OPC_UA_NODES_2_EVT_GWIDS_TEMPLATE = "opc.ua.nodes2evt.gwids"; //$NON-NLS-1$

  /**
   * template for id secton for store links UaNode->BknCmdGwid
   */
  public static final String SECTID_OPC_UA_NODES_2_BKN_CMD_GWIDS_TEMPLATE = "opc.ua.nodes2bkncmd.gwids"; //$NON-NLS-1$

  /**
   * template for id secton for store links CmdGwid->UaNodes
   */
  private static final String SECTID_CMD_GWIDS_2_OPC_UA_NODES_TEMPLATE = "cmd.gwid2opc.ua.nodes"; //$NON-NLS-1$

  /**
   * template for id secton for store links RriAttrCmdGwid->UaNodes
   */
  private static final String SECTID_RRI_ATTR_GWIDS_2_OPC_UA_NODES_TEMPLATE = "rri.attr.gwid2opc.ua.nodes"; //$NON-NLS-1$

  private static final String CLIENT_APP_NAME                            = "eclipse milo opc-ua client";        //$NON-NLS-1$
  private static final String CLIENT_APP_URI                             = "urn:eclipse:milo:examples:client";  //$NON-NLS-1$
  private static final String ERROR_FORMAT_UNABLE_TO_CREATE_SECURITY_DIR = "unable to create security dir: %s"; //$NON-NLS-1$
  private static final String SYS_PROP_JAVA_IO_TMPDIR_DEF_VAL            = "security";                          //$NON-NLS-1$
  private static final String SYS_PROP_JAVA_IO_TMPDIR                    = "java.io.tmpdir";                    //$NON-NLS-1$

  private static final Map<String, Map<String, Gwid>>   sect2nodeId2GwidMap      = new HashMap<>();
  private static final Map<String, Map<String, NodeId>> sect2classGwid2NodeIdMap = new HashMap<>();
  private static final Map<String, Map<Skid, NodeId>>   sect2skid2NodeIdMap      = new HashMap<>();
  private static final StringMap<IList<UaTreeNode>>     section2NodesList        = new StringMap<>();

  static private final String RTD_PREFIX = "rtd"; //$NON-NLS-1$
  private static final String EVT_PREFIX = "evt"; //$NON-NLS-1$
  static private final String RRI_PREFIX = "rri"; //$NON-NLS-1$

  /**
   * Hided constructor.
   */
  private OpcUaUtils() {

  }

  /**
   * Generates devcfg file from configuration Doc.
   *
   * @param aDoc OpcToS5DataCfgDoc - configuration Doc.
   * @param aContext ITsGuiContext - context.
   */
  public static void generateDevCfgFileFromCurrState( OpcToS5DataCfgDoc aDoc, ITsGuiContext aContext ) {
    String selected = formCfgFileFullName( aDoc, aContext, getDevCfgRelativeSysPath(), DEVCFG_FILE_EXTENTION );
    if( selected == null ) {
      return;
    }
    if( selected.endsWith( DEVCFG_FILE_EXTENTION ) ) {
      selected = selected.substring( 0, selected.indexOf( DEVCFG_FILE_EXTENTION ) );
    }

    Shell shell = aContext.find( Shell.class );
    IList<String> filterStrs = aDoc.getGroupIds();
    // new ElemArrayList<>( "TKA1", "TKA2", "TKA3" );

    IList<IStringList> properties = aDoc.getProperties();

    if( filterStrs.size() == 0 ) {
      filterStrs = new ElemArrayList<>( "" );
    }

    for( String fStr : filterStrs ) {
      try {
        IOpcUaNodeFilter filter =
            fStr.length() > 0 ? new IOpcUaNodeFilter.DeaultByStrOpcUaNodeFilter( fStr ) : IOpcUaNodeFilter.EMPTY_FILTER;
        IAvTree avTree = OpcToS5DataCfgConverter.convertToDevCfgTree( aContext, aDoc, filter );

        // TODO - сделать по уму (написать скриптовый язык или использовать существующий)
        if( properties.size() > 0 ) {
          insertProperties( avTree, properties, fStr );
        }
        // .setStr( "health_tag", "ns=3;s=\"noLink_" + fStr + "\"" );

        String TMP_DEST_FILE = "destDlmFile.tmp"; //$NON-NLS-1$
        AvTreeKeeper.KEEPER.write( new File( TMP_DEST_FILE ), avTree );

        String DLM_CONFIG_STR = "DeviceConfig = "; //$NON-NLS-1$

        String selectedFileName = selected + (fStr.length() > 0 ? ("_" + fStr) : "") + DEVCFG_FILE_EXTENTION;
        File dstFile = new File( selectedFileName );
        if( !dstFile.exists() ) {
          dstFile.createNewFile();
        }

        PinsConfigFileFormatter.format( TMP_DEST_FILE, selectedFileName, DLM_CONFIG_STR, true );

        TsDialogUtils.info( shell, MSG_CONFIG_FILE_DEVCFG_CREATED, selectedFileName );
      }
      catch( Exception e ) {
        LoggerUtils.errorLogger().error( e );
        TsDialogUtils.error( shell, e );
      }
    }
  }

  private static void insertProperties( IAvTree avTree, IList<IStringList> aProperties, String aGroupId ) {
    for( IStringList aProp : aProperties ) {
      Iterator<String> iterator = aProp.iterator();
      String name = iterator.next();
      String value = iterator.next();
      String path = iterator.next();

      AvTree dest = getPropertyDestination( avTree, path, aGroupId );
      if( dest != null ) {
        if( value.contains( "{$group}" ) ) {
          value = value.replace( "{$group}", "%s" );
          dest.fieldsEdit().setStr( name, String.format( value, aGroupId ) );
        }
        else {
          dest.fieldsEdit().setStr( name, value );
        }
      }
    }
  }

  private static AvTree getPropertyDestination( IAvTree avTree, String aPath, String aGroupId ) {
    if( aPath.trim().equals( "dev" ) ) {
      return (AvTree)avTree;
    }
    String path;
    if( aPath.startsWith( "dev#" ) ) {
      path = aPath.substring( 4 );
    }
    else {
      return null;
    }

    StringTokenizer st = new StringTokenizer( path, "#" );

    AvTree result = (AvTree)avTree;
    while( st.hasMoreTokens() ) {
      String token = st.nextToken();

      // массив
      if( token.startsWith( "[" ) && token.endsWith( "]" ) ) {
        String arrayIndexStr = token.substring( 1, token.length() - 1 );
        if( arrayIndexStr.equals( "*" ) ) {
          // any
          // здесь нужно ветвить и лепить массив
        }
        else
          if( arrayIndexStr.equals( "+" ) ) {
            // first
            result = (AvTree)result.arrayElement( 0 );
          }
          else {
            // index
            result = (AvTree)result.arrayElement( Integer.parseInt( arrayIndexStr ) );
          }
      }
      else {
        result = (AvTree)result.nodes().getByKey( token );
      }
    }

    return result;
  }

  @SuppressWarnings( "nls" )
  private static String getDevCfgRelativeSysPath() {
    String retVal = DEV_CFG_UNIX_FILE_RELETIVE_PATH_AND_NAME_FORMAT;
    String osName = System.getProperty( "os.name" );
    if( osName.toLowerCase().startsWith( "win" ) ) {
      retVal = DEV_CFG_WIN_FILE_RELETIVE_PATH_AND_NAME_FORMAT;
    }
    return retVal;
  }

  @SuppressWarnings( "nls" )
  private static String getDlmCfgRelativeSysPath() {
    String retVal = DLM_CFG_UNIX_FILE_RELETIVE_PATH_AND_NAME_FORMAT;
    String osName = System.getProperty( "os.name" );
    if( osName.toLowerCase().startsWith( "win" ) ) {
      retVal = DLM_CFG_WIN_FILE_RELETIVE_PATH_AND_NAME_FORMAT;
    }
    return retVal;
  }

  /**
   * Generates dlmcfg file from configuration Doc.
   *
   * @param aDoc OpcToS5DataCfgDoc - configuration Doc.
   * @param aContext ITsGuiContext - context.
   */
  public static void generateDlmCfgFileFromCurrStateNew( OpcToS5DataCfgDoc aDoc, ITsGuiContext aContext ) {
    String selected = formCfgFileFullName( aDoc, aContext, getDlmCfgRelativeSysPath(), DLMCFG_FILE_EXTENTION );
    if( selected == null ) {
      return;
    }
    if( selected.endsWith( DLMCFG_FILE_EXTENTION ) ) {
      selected = selected.substring( 0, selected.indexOf( DLMCFG_FILE_EXTENTION ) );
    }

    Shell shell = aContext.find( Shell.class );

    IList<String> filterStrs = aDoc.getGroupIds();
    // new ElemArrayList<>( "TKA1", "TKA2", "TKA3" );

    if( filterStrs.size() == 0 ) {
      filterStrs = new ElemArrayList<>( "" );
    }

    for( String fStr : filterStrs ) {
      try {
        ITsGuiContext convCtx = new TsGuiContext( aContext );
        convCtx.params().setStr( "group", fStr );

        IOpcCommonDlmCfgGenerator generator = new BaseOpcCommonDlmCfgGenerator( convCtx );
        generator.setUnits( aDoc.dataUnits() );

        generator.setGwidFilter(
            fStr.length() > 0 ? new IGwidFilter.DeaultByStrGwidFilter( fStr ) : IGwidFilter.EMPTY_FILTER );

        IComplexTagDetector complexTagDetector =
            Optional.ofNullable( convCtx.eclipseContext().get( IComplexTagDetector.class ) )
                .orElse( ( aaUnit, aaContext ) -> false );
        generator.setComplexTagDetector( complexTagDetector );

        IDevCfgParamValueSource paramValueSource =
            Optional.ofNullable( convCtx.eclipseContext().get( IDevCfgParamValueSource.class ) )
                .orElse( ( aaParamName, aaContext ) -> aaContext.params().findByKey( aaParamName ) );
        generator.setParamValueSource( paramValueSource );

        generator.setAdditionalProperties( aDoc.getProperties() );

        INodeIdConvertor nodeIdConvertor =
            Optional.ofNullable( convCtx.eclipseContext().get( INodeIdConvertor.class ) ).orElse( aNodeEntity -> {
              OpcNodeInfo nodeid = aNodeEntity.asValobj();
              return new Pair<>( OpcToS5DataCfgConverter.OPC_TAG_DEVICE, nodeid.getNodeId().toParseableString() );
            } );

        generator.setNodeIdConvertor( nodeIdConvertor );

        IAvTree avTree = generator.generate();
        String TMP_DEST_FILE = "destDlmFile.tmp"; //$NON-NLS-1$
        AvTreeKeeper.KEEPER.write( new File( TMP_DEST_FILE ), avTree );

        String DLM_CONFIG_STR = "DlmConfig = "; //$NON-NLS-1$

        String selectedFileName = selected + (fStr.length() > 0 ? ("_" + fStr) : "") + DLMCFG_FILE_EXTENTION;
        File dstFile = new File( selectedFileName );
        if( !dstFile.exists() ) {
          dstFile.createNewFile();
        }

        PinsConfigFileFormatter.format( TMP_DEST_FILE, selectedFileName, DLM_CONFIG_STR, true );

        TsDialogUtils.info( shell, MSG_CONFIG_FILE_DLMCFG_CREATED, selectedFileName );
      }
      catch( Exception e ) {
        LoggerUtils.errorLogger().error( e );
        TsDialogUtils.error( shell, e );
      }
    }
  }

  /**
   * Generates dlmcfg file from configuration Doc.
   *
   * @param aDoc OpcToS5DataCfgDoc - configuration Doc.
   * @param aContext ITsGuiContext - context.
   */
  public static void generateDlmCfgFileFromCurrState( OpcToS5DataCfgDoc aDoc, ITsGuiContext aContext ) {
    String selected = formCfgFileFullName( aDoc, aContext, getDlmCfgRelativeSysPath(), DLMCFG_FILE_EXTENTION );
    if( selected == null ) {
      return;
    }
    if( selected.endsWith( DLMCFG_FILE_EXTENTION ) ) {
      selected = selected.substring( 0, selected.indexOf( DLMCFG_FILE_EXTENTION ) );
    }

    Shell shell = aContext.find( Shell.class );

    IList<String> filterStrs = aDoc.getGroupIds();
    // new ElemArrayList<>( "TKA1", "TKA2", "TKA3" );

    if( filterStrs.size() == 0 ) {
      filterStrs = new ElemArrayList<>( "" );
    }

    for( String fStr : filterStrs ) {
      try {
        ISkConnectionSupplier cs = aContext.get( ISkConnectionSupplier.class );
        TsInternalErrorRtException.checkNull( cs );
        ISkConnection conn = cs.defConn();
        TsInternalErrorRtException.checkNull( conn );
        IGwidFilter filter =
            fStr.length() > 0 ? new IGwidFilter.DeaultByStrGwidFilter( fStr ) : IGwidFilter.EMPTY_FILTER;
        IAvTree avTree = OpcToS5DataCfgConverter.convertToDlmCfgTree( aDoc.dataUnits(), conn, aNodeEntity -> {
          OpcNodeInfo nodeid = aNodeEntity.asValobj();
          return new Pair<>( OpcToS5DataCfgConverter.OPC_TAG_DEVICE, nodeid.getNodeId().toParseableString() );
        }, filter );
        String TMP_DEST_FILE = "destDlmFile.tmp"; //$NON-NLS-1$
        AvTreeKeeper.KEEPER.write( new File( TMP_DEST_FILE ), avTree );

        String DLM_CONFIG_STR = "DlmConfig = "; //$NON-NLS-1$

        String selectedFileName = selected + (fStr.length() > 0 ? ("_" + fStr) : "") + DLMCFG_FILE_EXTENTION;
        File dstFile = new File( selectedFileName );
        if( !dstFile.exists() ) {
          dstFile.createNewFile();
        }

        PinsConfigFileFormatter.format( TMP_DEST_FILE, selectedFileName, DLM_CONFIG_STR, true );

        TsDialogUtils.info( shell, MSG_CONFIG_FILE_DLMCFG_CREATED, selectedFileName );
      }
      catch( Exception e ) {
        LoggerUtils.errorLogger().error( e );
        TsDialogUtils.error( shell, e );
      }
    }
  }

  @SuppressWarnings( "null" )
  private static String formCfgFileFullName( OpcToS5DataCfgDoc aDoc, ITsGuiContext aContext, String aReletivePathFormat,
      String aFileExtention ) {
    File pathToL2 = aDoc.getL2Path();
    String cfgFilename = aDoc.getCfgFilesPrefix();
    String initPath = pathToL2.getAbsolutePath();
    if( pathToL2 != null && pathToL2.length() > 0 && cfgFilename != null && cfgFilename.length() > 0 ) {
      initPath = pathToL2 + String.format( aReletivePathFormat, cfgFilename, aFileExtention );
    }
    Shell shell = aContext.find( Shell.class );
    FileDialog fd = new FileDialog( shell, SWT.SAVE );
    fd.setText( STR_SELECT_FILE_SAVE_DLMCFG );
    fd.setFilterPath( pathToL2.getAbsolutePath() );
    fd.setFileName( initPath );
    String[] filterExt = { aFileExtention };
    fd.setFilterExtensions( filterExt );
    String selected = fd.open();

    return selected;
  }

  /**
   * Выбор и подключение к OPC UA серверу
   *
   * @param aContext контекст приложения
   * @return подключение
   */
  public static OpcUaClient selectOpcConfigAndOpenConnection( ITsGuiContext aContext ) {
    IOpcUaServerConnCfg conConf = OpcUaUtils.selectOpcServerConfig( aContext );
    // dima 13.10.23 сохраним в контекст
    aContext.put( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG, conConf );
    if( conConf == null ) {
      return null;
    }

    try {
      OpcUaClient client = OpcUaUtils.createClient( conConf );
      client.connect().get();
      return client;
    }
    catch( Exception ex ) {
      LoggerUtils.errorLogger().error( ex );
      return null;
    }

  }

  /**
   * Creates and returns opc ua client formed according to connection configuration.
   *
   * @param aCfg IOpcUaServerConnCfg - conn configuration.
   * @return OpcUaClient - opc ua client.
   * @throws Exception - exception during connection try.
   */
  public static OpcUaClient createClient( IOpcUaServerConnCfg aCfg )
      throws Exception {
    Path securityTempDir = Paths.get( System.getProperty( SYS_PROP_JAVA_IO_TMPDIR ), SYS_PROP_JAVA_IO_TMPDIR_DEF_VAL );
    Files.createDirectories( securityTempDir );
    if( !Files.exists( securityTempDir ) ) {
      throw new TsIllegalStateRtException( ERROR_FORMAT_UNABLE_TO_CREATE_SECURITY_DIR, securityTempDir );
    }

    logger.info( "security temp dir: %s", securityTempDir.toAbsolutePath().toString() ); //$NON-NLS-1$

    KeyStoreLoader loader = new KeyStoreLoader().load( securityTempDir );

    Function<List<EndpointDescription>, Optional<EndpointDescription>> selectEndpoint = aEendpoints -> {
      Optional<EndpointDescription> result = aEendpoints.stream().filter( endpointFilter() ).findFirst();
      return result;
    };

    Function<OpcUaClientConfigBuilder, OpcUaClientConfig> buildConfig = aConfigBuilder -> {
      OpcUaClientConfig result = aConfigBuilder.setApplicationName( LocalizedText.english( CLIENT_APP_NAME ) )
          .setApplicationUri( CLIENT_APP_URI ).setCertificate( loader.getClientCertificate() )
          .setKeyPair( loader.getClientKeyPair() ).setIdentityProvider( getIdentityProvider( aCfg ) )
          .setRequestTimeout( Unsigned.uint( 5000 ) ).build();
      return result;
    };

    return OpcUaClient.create( getEndpointUrl( aCfg ), selectEndpoint, buildConfig );
  }

  static String getEndpointUrl( IOpcUaServerConnCfg aCfg ) {
    return aCfg.host();
    // return "opc.tcp://192.168.153.1:4850"; //poligon
    // return "opc.tcp://localhost:12686/milo";

  }

  static Predicate<EndpointDescription> endpointFilter() {
    return e -> true;
  }

  static SecurityPolicy getSecurityPolicy() {
    return SecurityPolicy.None;
  }

  static IdentityProvider getIdentityProvider( IOpcUaServerConnCfg aCfg ) {
    if( aCfg.login().length() > 0 ) {
      // return new UsernameProvider("admin","123"); //poligon
      return new UsernameProvider( aCfg.login(), aCfg.password() );
    }

    return new AnonymousProvider();// Siemens
  }

  /**
   * Переводит список AV объектов в список идентификаторов узлов (идентификатор - пара: ид устроства - ид узла в
   * устройстве)
   *
   * @param aAtomicList список AV объектов
   * @param aConvertor конвертор из AV объекта в полный идентификатор (идентификатор - пара: ид устроства - ид узла в
   *          устройстве)
   * @return список идентификаторов узлов
   */
  public static IList<Pair<String, String>> convertToNodesList2( IAvList aAtomicList, INodeIdConvertor aConvertor ) {
    IListEdit<Pair<String, String>> result = new ElemArrayList<>();
    for( IAtomicValue val : aAtomicList ) {
      result.add( aConvertor.converToNodeId( val ) );
    }
    return result;
  }

  /**
   * Переводит список AV объектов в список узлов (содержащихся в объектах AV)
   *
   * @param <T> - класс узла
   * @param aAtomicList - список AV объектов
   * @return список узлов
   */
  public static <T> IList<T> convertToNodesList( IAvList aAtomicList ) {
    IListEdit<T> result = new ElemArrayList<>();
    for( IAtomicValue val : aAtomicList ) {
      result.add( val.asValobj() );
    }
    return result;
  }

  /**
   * Переводит список узлов в список AV объектов (созданных их этих узлов)
   *
   * @param aNodeList IList - список узлов.
   * @return IAvList - список AV объектов
   */
  public static IAvList convertNodeListToAtomicList( IList<NodeId> aNodeList ) {
    IAvListEdit result = new AvList( new ElemArrayList<>() );
    for( NodeId node : aNodeList ) {
      // dima 19/08/24 fix
      result.add( avValobj( new OpcNodeInfo( node ) ) );
      // result.add( avValobj( node ) );
    }
    return result;
  }

  /**
   * @param aEntity узел значения переменной
   * @return класс типа данных значения узла
   */
  public static Class<?> getNodeDataTypeClass( UaVariableNode aEntity ) {
    // new verion of max
    Class<?> retVal = null;
    try {
      retVal = TypeUtil.getBackingClass( aEntity.getDataType() );
    }
    catch( Exception ex ) {
      LoggerUtils.errorLogger().error( ex );
    }
    // dima 29.02.24 на Siemens верхний метод срабатывает не всегда, а нижний работает
    if( retVal == null ) {
      // old version of dima
      // получение значения узла
      DataValue dataValue = aEntity.getValue();
      // тут получаем Variant
      Variant variant = dataValue.getValue();
      Optional<ExpandedNodeId> dataTypeNode = variant.getDataType();
      if( dataTypeNode.isPresent() ) {
        ExpandedNodeId expNodeId = dataTypeNode.get();
        // TODO разобраться с отображением не числовых типов
        if( expNodeId.getType() == IdType.Numeric ) {
          UInteger id = (UInteger)expNodeId.getIdentifier();
          NodeId nodeId = new NodeId( expNodeId.getNamespaceIndex(), id );
          Class<?> clazz = TypeUtil.getBackingClass( nodeId );
          retVal = clazz;
        }
      }
    }
    return retVal;
  }

  /**
   * Получить Variant из текстового значения
   *
   * @param aClazz класс переменной
   * @param aVal текстовое значение
   * @return значение {@link Variant } применяемое в milo для передачи/записи значений тегов
   */
  public static Variant getVariant( Class<?> aClazz, String aVal ) {
    if( aClazz.equals( Boolean.class ) ) {
      return new Variant( Boolean.valueOf( aVal ) );
    }
    if( aClazz.equals( Byte.class ) ) {
      return new Variant( Byte.valueOf( aVal ) );
    }
    if( aClazz.equals( UByte.class ) ) {
      return new Variant( UByte.valueOf( aVal ) );
    }
    if( aClazz.equals( Short.class ) ) {
      return new Variant( Short.valueOf( aVal ) );
    }
    if( aClazz.equals( UShort.class ) ) {
      return new Variant( UShort.valueOf( aVal ) );
    }
    if( aClazz.equals( Integer.class ) ) {
      return new Variant( Integer.valueOf( aVal ) );
    }
    if( aClazz.equals( UInteger.class ) ) {
      return new Variant( UInteger.valueOf( aVal ) );
    }
    if( aClazz.equals( Long.class ) ) {
      return new Variant( Long.valueOf( aVal ) );
    }
    if( aClazz.equals( ULong.class ) ) {
      return new Variant( ULong.valueOf( aVal ) );
    }
    if( aClazz.equals( Float.class ) ) {
      return new Variant( Float.valueOf( aVal ) );
    }
    return Variant.NULL_VALUE;
  }

  /**
   * Получить соответствующий тип для работы в Sk из описания OPC UA типа
   *
   * @param aClazz класс переменной
   * @return значение {@link EAtomicType } тип данных применяемый в Sk
   */
  public static EAtomicType getAtomicType( Class<?> aClazz ) {
    if( aClazz.equals( Boolean.class ) ) {
      return EAtomicType.BOOLEAN;
    }
    if( aClazz.equals( Byte.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( UByte.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( Short.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( UShort.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( Integer.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( UInteger.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( Long.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( ULong.class ) ) {
      return EAtomicType.INTEGER;
    }
    if( aClazz.equals( Float.class ) ) {
      return EAtomicType.FLOATING;
    }
    return EAtomicType.NONE;
  }

  /**
   * Update list of links UaNode->Skid {@link UaNode2Skid} to store in inner storage
   *
   * @param aContext app context
   * @param aNodes2Skids list of links UaNode->Skid
   * @param aOpcUaServerConnCfg параметры подключения к OPC UA
   */
  public static void updateNodes2SkidsInStore( ITsGuiContext aContext, IList<UaNode2Skid> aNodes2Skids,
      IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    String sectId = getTreeSectionNameByConfig( SECTID_OPC_UA_NODES_2_SKIDS_TEMPLATE, aOpcUaServerConnCfg );
    IList<UaNode2Skid> oldList = loadNodes2Skids( aContext, sectId );
    IListEdit<UaNode2Skid> newList = new ElemArrayList<>();
    // для ускорения переложим в карту
    IStringMapEdit<UaNode2Skid> tmpCach = new StringMap<>();
    for( UaNode2Skid node : aNodes2Skids ) {
      tmpCach.put( node.getNodeId().toParseableString(), node );
    }
    for( UaNode2Skid oldItem : oldList ) {
      if( !tmpCach.hasKey( oldItem.getNodeId().toParseableString() ) ) {
        newList.add( oldItem );
      }
    }
    newList.addAll( aNodes2Skids );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    storage.writeColl( sectId, newList, UaNode2Skid.KEEPER );
    // чистим кеш
    sect2skid2NodeIdMap.remove( sectId );
  }

  /**
   * Update list of links UaNode->RtdGwid {@link UaNode2Gwid} to store in inner storage
   *
   * @param <T> - шаблон для описания привязки {@link UaNode2Gwid}
   * @param aContext app context
   * @param aNodes2Gwids list of links UaNode->Gwid
   * @param aSectIdTempate section id template to store data
   * @param aKeeper хранитель шаблона
   * @param aOpcUaServerConnCfg параметры подключения к OPC UA
   */
  public static <T extends UaNode2Gwid> void updateNodes2GwidsInStore( ITsGuiContext aContext, IList<T> aNodes2Gwids,
      String aSectIdTempate, IEntityKeeper<T> aKeeper, IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    String sectId = getTreeSectionNameByConfig( aSectIdTempate, aOpcUaServerConnCfg );
    IList<T> oldList = loadNodes2Gwids( aContext, sectId, aKeeper );
    IListEdit<T> newList = new ElemArrayList<>( false );
    // для ускорения переложим в карту
    IStringMapEdit<IContainNodeId> tmpCach = new StringMap<>();
    for( T node : aNodes2Gwids ) {
      String key = node.getNodeId().toParseableString();
      tmpCach.put( key, node );
    }
    // перекладываем все что в старом совпадает с новым
    for( T oldItem : oldList ) {
      String key = oldItem.getNodeId().toParseableString();
      if( !tmpCach.hasKey( key ) ) {
        newList.add( oldItem );
      }
    }
    newList.addAll( aNodes2Gwids );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    storage.writeColl( sectId, newList, aKeeper );
  }

  /**
   * Update list of links UaNode->RtdGwid {@link UaNode2Gwid} to store in inner storage Алгорит обновления: <br>
   * <ul>
   * <li><b>переносим все данные НЕ касающиеся обновляемого класса</li>
   * <li><b>добавляем новые данные обновляемого класса</li>
   * </ul>
   *
   * @param <T> - шаблон для описания привязки {@link UaNode2Gwid}
   * @param aClassId класс объекты которого обновляются в метаинформации
   * @param aContext app context
   * @param aNodes2Gwids list of links UaNode->Gwid
   * @param aSectIdTempate section id template to store data
   * @param aKeeper хранитель шаблона
   * @param aOpcUaServerConnCfg параметры подключения к OPC UA
   */
  public static <T extends UaNode2Gwid> void updateNodes2ObjGwidsInStore( String aClassId, ITsGuiContext aContext,
      IList<T> aNodes2Gwids, String aSectIdTempate, IEntityKeeper<T> aKeeper,
      IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    String sectId = getTreeSectionNameByConfig( aSectIdTempate, aOpcUaServerConnCfg );
    IList<T> oldList = loadNodes2Gwids( aContext, sectId, aKeeper );
    IListEdit<T> newList = new ElemArrayList<>( false );
    // в метаинфу переносим все старые данные по другим классам
    for( T oldItem : oldList ) {
      if( oldItem.gwid().classId().compareTo( aClassId ) != 0 ) {
        newList.add( oldItem );
      }
    }
    // и новые данное по обновляемому классу
    newList.addAll( aNodes2Gwids );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    storage.writeColl( sectId, newList, aKeeper );
  }

  /**
   * Загрузить карту привязки узлов OPC -> RtData Gwid
   *
   * @param aContext app context
   * @param aOpcUaServerConnCfg параметры подключения к OPC UA
   * @return list of links {@link UaNode2Gwid } UaNode->Gwid
   */
  public static IList<UaNode2Gwid> loadNodes2RtdGwids( ITsGuiContext aContext,
      IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    String sectId = getTreeSectionNameByConfig( SECTID_OPC_UA_NODES_2_RTD_GWIDS_TEMPLATE, aOpcUaServerConnCfg );
    return loadNodes2Gwids( aContext, sectId, UaNode2Gwid.KEEPER );
  }

  /**
   * Загрузить карту привязки узлов OPC -> RRI Gwid
   *
   * @param aContext app context
   * @param aOpcUaServerConnCfg параметры подключения к OPC UA
   * @return list of links {@link UaNode2Gwid } UaNode->Gwid
   */
  public static IList<UaNode2Gwid> loadNodes2RriGwids( ITsGuiContext aContext,
      IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    String sectId = getTreeSectionNameByConfig( SECTID_OPC_UA_NODES_2_RRI_GWIDS_TEMPLATE, aOpcUaServerConnCfg );
    return loadNodes2Gwids( aContext, sectId, UaNode2Gwid.KEEPER );
  }

  /**
   * Загрузить карту привязки узлов OPC -> Event Gwid
   *
   * @param aContext app context
   * @param aOpcUaServerConnCfg параметры подключения к OPC UA
   * @return list of links {@link UaNode2Gwid } UaNode->Gwid
   */
  public static IList<UaNode2EventGwid> loadNodes2EvtGwids( ITsGuiContext aContext,
      IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    String sectId = getTreeSectionNameByConfig( SECTID_OPC_UA_NODES_2_EVT_GWIDS_TEMPLATE, aOpcUaServerConnCfg );
    return loadNodes2Gwids( aContext, sectId, UaNode2EventGwid.KEEPER );
  }

  /**
   * @param <T> расширение класса {@link UaNode2Gwid}
   * @param aContext app context
   * @param aSectId id of section where data stored
   * @param aKeeper хранитель сущностей типа {@link UaNode2Gwid} и его наследников
   * @return list of links {@link UaNode2Gwid } UaNode->Gwid
   */
  static <T extends UaNode2Gwid> IList<T> loadNodes2Gwids( ITsGuiContext aContext, String aSectId,
      IEntityKeeper<T> aKeeper ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    // IList<T> retVal = new ElemArrayList<>( storage.readColl( aSectId, UaNode2Gwid.KEEPER ) ); aKeeper
    IList<T> retVal = new ElemArrayList<>( storage.readColl( aSectId, aKeeper ) );
    return retVal;
  }

  /**
   * @param aContext app context
   * @param aSectId id of section where data stored
   * @return list of links {@link UaNode2Gwid } UaNode->Skid
   */
  static IList<UaNode2Skid> loadNodes2Skids( ITsGuiContext aContext, String aSectId ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    IList<UaNode2Skid> retVal = new ElemArrayList<>( storage.readColl( aSectId, UaNode2Skid.KEEPER ) );
    return retVal;
  }

  /**
   * Get NodeId By Skid
   *
   * @param aContext app context
   * @param aSkid OPC UA nodeId
   * @param aOpcUaServerConnCfg параметры подключения к OPC UA
   * @return NodeId linked to Skid or null
   */
  public static NodeId nodeBySkid( ITsGuiContext aContext, Skid aSkid, IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    NodeId retVal = null;
    // пустая карта кеша, загружаем
    String sectId = getTreeSectionNameByConfig( SECTID_OPC_UA_NODES_2_SKIDS_TEMPLATE, aOpcUaServerConnCfg );
    if( !sect2skid2NodeIdMap.containsKey( sectId ) ) {
      IList<UaNode2Skid> nodes2Skids = loadNodes2Skids( aContext, sectId );
      Map<Skid, NodeId> skid2NodeIdMap = new HashMap<>();
      sect2skid2NodeIdMap.put( sectId, skid2NodeIdMap );
      for( UaNode2Skid node2Skid : nodes2Skids ) {
        skid2NodeIdMap.put( node2Skid.skid(), node2Skid.getNodeId() );
      }
    }
    Map<Skid, NodeId> skid2NodeIdMap = sect2skid2NodeIdMap.get( sectId );
    if( skid2NodeIdMap.containsKey( aSkid ) ) {
      retVal = skid2NodeIdMap.get( aSkid );
    }
    return retVal;
  }

  /**
   * Get Gwid by NodeId
   *
   * @param aContext app context
   * @param aNodeId OPC UA nodeId
   * @param aOpcUaServerConnCfg параметры подключения к OPC UA
   * @return Gwid linked to UaNode or null
   */
  public static Gwid uaNode2rtdGwid( ITsGuiContext aContext, NodeId aNodeId, IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    Gwid retVal = null;
    String sectId = getTreeSectionNameByConfig( SECTID_OPC_UA_NODES_2_RTD_GWIDS_TEMPLATE, aOpcUaServerConnCfg );
    if( !sect2nodeId2GwidMap.containsKey( sectId ) ) {
      // пустая карта кеша, загружаем
      IList<UaNode2Gwid> nodes2Gwids = loadNodes2Gwids( aContext, sectId, UaNode2Gwid.KEEPER );
      Map<String, Gwid> nodeId2GwidMap = new HashMap<>();
      sect2nodeId2GwidMap.put( sectId, nodeId2GwidMap );
      for( UaNode2Gwid node2Gwid : nodes2Gwids ) {
        String key = node2Gwid.getNodeId().toParseableString();
        nodeId2GwidMap.put( key, node2Gwid.gwid() );
      }
    }
    Map<String, Gwid> nodeId2GwidMap = sect2nodeId2GwidMap.get( sectId );
    String nodeKey = aNodeId.toParseableString();
    if( nodeId2GwidMap.containsKey( nodeKey ) ) {
      retVal = nodeId2GwidMap.get( nodeKey );
    }
    return retVal;
  }

  /**
   * Get NodeId by Class Gwid
   *
   * @param aContext app context
   * @param aGwid Class Gwid
   * @param aOpcUaServerConnCfg параметры подключения к OPC UA
   * @return NodeId linked to aGwid or null
   */
  public static NodeId classGwid2uaNode( ITsGuiContext aContext, Gwid aGwid, IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    NodeId retVal = null;
    String sectId = getTreeSectionNameByConfig( SECTID_OPC_UA_NODES_2_CLS_GWIDS_TEMPLATE, aOpcUaServerConnCfg );
    if( !sect2classGwid2NodeIdMap.containsKey( sectId ) ) {
      // пустая карта кеша, загружаем
      IList<UaNode2Gwid> nodes2Gwids = loadNodes2Gwids( aContext, sectId, UaNode2Gwid.KEEPER );
      Map<String, NodeId> classGwid2NodeIdMap = new HashMap<>();
      sect2classGwid2NodeIdMap.put( sectId, classGwid2NodeIdMap );
      for( UaNode2Gwid node2Gwid : nodes2Gwids ) {
        String key = node2Gwid.gwid().canonicalString();
        classGwid2NodeIdMap.put( key, node2Gwid.getNodeId() );
      }
    }
    String gwidKey = aGwid.canonicalString();
    Map<String, NodeId> classGwid2NodeIdMap = sect2classGwid2NodeIdMap.get( sectId );
    if( classGwid2NodeIdMap.containsKey( gwidKey ) ) {
      retVal = classGwid2NodeIdMap.get( gwidKey );
    }
    return retVal;
  }

  /**
   * Add in inner storage list of {@link CmdGwid2UaNodes} links CmdGwid->UaNodes
   *
   * @param aContext app context
   * @param aCmdGwid2UaNodes list of links CmdGwid->UaNodes
   * @param aOpcUaServerConnCfg OPC UA server connection params
   */
  public static void updateCmdGwid2NodesInStore( ITsGuiContext aContext, IList<CmdGwid2UaNodes> aCmdGwid2UaNodes,
      IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IList<CmdGwid2UaNodes> oldList = loadCmdGwid2Nodes( aContext, aOpcUaServerConnCfg );
    // добавляем в список на запись только те элементы которых нет в новом списке
    IListEdit<CmdGwid2UaNodes> newList = new ElemArrayList<>( false );
    for( CmdGwid2UaNodes oldItem : oldList ) {
      if( !containsGwidIn( aCmdGwid2UaNodes, oldItem ) ) {
        newList.add( oldItem );
      }
    }
    newList.addAll( aCmdGwid2UaNodes );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    String sectId = getTreeSectionNameByConfig( SECTID_CMD_GWIDS_2_OPC_UA_NODES_TEMPLATE, aOpcUaServerConnCfg );
    storage.writeColl( sectId, newList, CmdGwid2UaNodes.KEEPER );
  }

  /**
   * Add in inner storage list of {@link CmdGwid2UaNodes} links CmdGwid->UaNodes
   *
   * @param aContext app context
   * @param aRriAttrGwid2UaNodes list of links RriAttrGwid->UaNodes
   * @param aOpcUaServerConnCfg OPC UA server connection params
   */
  public static void updateRriAttrGwid2NodesInStore( ITsGuiContext aContext,
      IList<CmdGwid2UaNodes> aRriAttrGwid2UaNodes, IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IList<CmdGwid2UaNodes> oldList = loadRriAttrGwid2Nodes( aContext, aOpcUaServerConnCfg );
    // добавляем в список на запись только те элементы которых нет в новом списке
    IListEdit<CmdGwid2UaNodes> newList = new ElemArrayList<>( false );
    for( CmdGwid2UaNodes oldItem : oldList ) {
      if( !containsGwidIn( aRriAttrGwid2UaNodes, oldItem ) ) {
        newList.add( oldItem );
      }
    }
    newList.addAll( aRriAttrGwid2UaNodes );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    String sectId = getTreeSectionNameByConfig( SECTID_RRI_ATTR_GWIDS_2_OPC_UA_NODES_TEMPLATE, aOpcUaServerConnCfg );
    storage.writeColl( sectId, newList, CmdGwid2UaNodes.KEEPER );
  }

  private static boolean containsGwidIn( IList<CmdGwid2UaNodes> aCmdGwid2UaNodes, CmdGwid2UaNodes aOldItem ) {
    for( CmdGwid2UaNodes item : aCmdGwid2UaNodes ) {
      if( item.gwid().equals( aOldItem.gwid() ) ) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param aContext app context
   * @param aOpcUaServerConnCfg OPC UA server connection config
   * @return list of links {@link CmdGwid2UaNodes } CmdGwid->UaNodes
   */
  public static IList<CmdGwid2UaNodes> loadCmdGwid2Nodes( ITsGuiContext aContext,
      IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    String sectId = getTreeSectionNameByConfig( SECTID_CMD_GWIDS_2_OPC_UA_NODES_TEMPLATE, aOpcUaServerConnCfg );
    IList<CmdGwid2UaNodes> retVal = new ElemArrayList<>( storage.readColl( sectId, CmdGwid2UaNodes.KEEPER ) );
    return retVal;
  }

  /**
   * @param aContext app context
   * @param aOpcUaServerConnCfg OPC UA server connection config
   * @return list of links {@link CmdGwid2UaNodes } CmdGwid->UaNodes
   */
  public static IList<CmdGwid2UaNodes> loadRriAttrGwid2Nodes( ITsGuiContext aContext,
      IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    String sectId = getTreeSectionNameByConfig( SECTID_RRI_ATTR_GWIDS_2_OPC_UA_NODES_TEMPLATE, aOpcUaServerConnCfg );
    IList<CmdGwid2UaNodes> retVal = new ElemArrayList<>( storage.readColl( sectId, CmdGwid2UaNodes.KEEPER ) );
    return retVal;
  }

  /**
   * Имя параметра - Идентификатор синтетического тега
   */
  public static final IDataDef OP_COMPLEX_TAG_ID = create( "complex.tag.id", STRING, //$NON-NLS-1$
      TSID_NAME, STR_N_SYNTH_TAG, //
      TSID_DESCRIPTION, STR_D_SYNTH_TAG ); //

  /**
   * Имя параметра - java класс, реализующий оработку команды в dlm
   */
  public static final IDataDef OP_CMD_JAVA_CLASS = create( "command.exec.java.class", STRING, //$NON-NLS-1$
      TSID_NAME, STR_N_JAVA_CLASS, //
      TSID_DESCRIPTION, STR_D_JAVA_CLASS, //
      TSID_IS_READ_ONLY, AV_TRUE );

  /**
   * Имя параметра - идентификатор параметра команды
   */
  public static final IDataDef OP_CMD_VALUE_PARAM_ID = create( "value.param.id", STRING, //$NON-NLS-1$
      TSID_NAME, STR_N_PARAM_ID, //
      TSID_DESCRIPTION, STR_D_PARAM_ID ); //

  /**
   * Имя параметра - идентификатор (номер) команды opc
   */
  public static final IDataDef OP_CMD_OPC_ID = create( "cmd.opc.id", INTEGER, //$NON-NLS-1$
      TSID_NAME, STR_N_OPC_CMD_ID, //
      TSID_DESCRIPTION, STR_D_OPC_CMD_ID ); //

  /**
   * Имя параметра - java класс, реализующий оработку данных в dlm
   */
  public static final IDataDef OP_DATA_JAVA_CLASS = create( "java.class", STRING, //$NON-NLS-1$
      TSID_NAME, STR_N_DATA_JAVA_CLASS, //
      TSID_DESCRIPTION, STR_D_DATA_JAVA_CLASS, //
      TSID_IS_READ_ONLY, AV_TRUE );

  /**
   * Имя параметра - номер бита (начиная от младшего с нулевого)
   */
  public static final IDataDef OP_BIT_INDEX = create( "bit.index", INTEGER, //$NON-NLS-1$
      TSID_NAME, STR_N_BIT_NUMBER, //
      TSID_DESCRIPTION, STR_D_BIT_NUMBER );

  /**
   * Имя параметра - период синхронизации
   */
  public static final IDataDef OP_SYNCH_PERIOD = create( "synch.period", INTEGER, //$NON-NLS-1$
      TSID_NAME, STR_N_SYNCH_PERIOD, //
      TSID_DESCRIPTION, STR_D_SYNCH_PERIOD, //
      TSID_DEFAULT_VALUE, avInt( 1000 ) );

  /**
   * Имя параметра - признак исторических данных
   */
  public static final IDataDef OP_IS_HIST = create( "is.hist", BOOLEAN, //$NON-NLS-1$
      TSID_NAME, STR_N_IS_HIST, //
      TSID_DESCRIPTION, STR_D_IS_HIST, //
      TSID_DEFAULT_VALUE, avBool( true ) );

  /**
   * Имя параметра - признак исторических данных
   */
  public static final IDataDef OP_IS_CURR = create( "is.curr", BOOLEAN, //$NON-NLS-1$
      TSID_NAME, STR_N_IS_CURR, //
      TSID_DESCRIPTION, STR_D_IS_CURR, //
      TSID_DEFAULT_VALUE, avBool( true ), //
      TSID_IS_READ_ONLY, AV_TRUE );

  /**
   * Имя параметра - java класс, реализующий оработку событий в dlm
   */
  public static final IDataDef OP_EVENT_SENDER_JAVA_CLASS = create( "event.sender.java.class", STRING, //$NON-NLS-1$
      TSID_NAME, STR_N_EVENT_SENDER_JAVA_CLASS, //
      TSID_DESCRIPTION, STR_D_EVENT_SENDER_JAVA_CLASS, //
      TSID_IS_READ_ONLY, AV_TRUE );

  /**
   * Имя параметра - java класс, реализующий условие наступления событий в dlm
   */
  public static final IDataDef OP_CONDITION_JAVA_CLASS = create( "condition.java.class", STRING, //$NON-NLS-1$
      TSID_NAME, STR_N_CONDITION_JAVA_CLASS, //
      TSID_DESCRIPTION, STR_D_CONDITION_JAVA_CLASS, //
      TSID_IS_READ_ONLY, AV_TRUE );

  /**
   * Имя параметра - java класс, реализующий формирование параметров событий в dlm
   */
  public static final IDataDef OP_PARAM_FORMER_JAVA_CLASS = create( "param.former.java.class", STRING, //$NON-NLS-1$
      TSID_NAME, STR_N_PARAM_FORMER_JAVA_CLASS, //
      TSID_DESCRIPTION, STR_D_PARAM_FORMER_JAVA_CLASS, //
      TSID_IS_READ_ONLY, AV_TRUE );

  /**
   * Имя параметра - параметры события
   */
  public static final IDataDef OP_FORMER_EVENT_PARAM = create( "former.event.params", STRING, //$NON-NLS-1$
      TSID_NAME, STR_N_FORMER_EVENT_PARAM, //
      TSID_DESCRIPTION, STR_D_FORMER_EVENT_PARAM );

  /**
   * Имя параметра - признак наступления события по фронту
   */
  public static final IDataDef OP_CONDITION_SWITCH_ON = create( "condition.switch.on", BOOLEAN, //$NON-NLS-1$
      TSID_NAME, STR_N_FRONT, //
      TSID_DESCRIPTION, STR_D_FRONT, //
      TSID_DEFAULT_VALUE, avBool( true ) );

  /**
   * Имя параметра - признак наступления события по обратному фронту
   */
  public static final IDataDef OP_CONDITION_SWITCH_OFF = create( "condition.switch.off", BOOLEAN, //$NON-NLS-1$
      TSID_NAME, STR_N_DOWN, //
      TSID_DESCRIPTION, STR_D_DOWN, //
      TSID_DEFAULT_VALUE, avBool( false ) );

  /**
   * Registers cfg unit realization types in holder and adds it into context.
   *
   * @param aContext ITsGuiContext - context.
   */
  public static void registerCfgUnitRealizationTypes( ITsGuiContext aContext ) {
    if( aContext.hasKey( CfgUnitRealizationTypeRegister.class ) ) {
      return;
    }
    CfgUnitRealizationTypeRegister realizationTypeRegister = new CfgUnitRealizationTypeRegister();
    aContext.put( CfgUnitRealizationTypeRegister.class, realizationTypeRegister );

    // ----------------------------------------------------
    // Определение первой реализации команд

    IListEdit<IDataDef> paramDefenitions = new ElemArrayList<>( false );

    paramDefenitions.add( OP_CMD_JAVA_CLASS );
    paramDefenitions.add( OP_CMD_VALUE_PARAM_ID );
    paramDefenitions.add( OP_CMD_OPC_ID );

    IOptionSetEdit defaultParams = new OptionSet();
    OP_CMD_JAVA_CLASS.setValue( defaultParams, avStr( COMMANDS_JAVA_CLASS_VALUE_COMMAND_BY_ONE_TAG_EXEC ) );

    ICfgUnitRealizationType cmdRealValCommandByOneTagWithParamExec =
        new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_VALUE_COMMAND_BY_ONE_TAG, STR_SET_VALUE_CMD,
            ECfgUnitType.COMMAND, paramDefenitions, defaultParams ) {

          @Override
          public CfgOpcUaNode createInitCfg( ITsGuiContext aaContext, String aNodeId, int aNodeIndex, int aNodeCount ) {
            OpcUaServerConnCfg conConf =
                (OpcUaServerConnCfg)aaContext.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );

            EAtomicType type = EAtomicType.NONE;
            if( conConf != null ) {
              type = OpcUaUtils.getValueTypeOfNode( aaContext, conConf, aNodeId );
            }
            return new CfgOpcUaNode( aNodeId, false, true, aNodeIndex < aNodeCount - 1, type );
          }

        };

    realizationTypeRegister.registerType( cmdRealValCommandByOneTagWithParamExec );

    // ----------------------------------------------------
    // Определение второй реализации команд

    paramDefenitions = new ElemArrayList<>( false );

    paramDefenitions.add( OP_CMD_JAVA_CLASS );
    paramDefenitions.add( OP_CMD_VALUE_PARAM_ID );

    defaultParams = new OptionSet();
    OP_CMD_JAVA_CLASS.setValue( defaultParams, avStr( COMMANDS_JAVA_CLASS_VALUE_COMMAND_EXEC ) );
    OP_CMD_VALUE_PARAM_ID.setValue( defaultParams, avStr( STR_VALUE ) );

    ICfgUnitRealizationType cmdRealValueCommandExec =
        new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_VALUE_COMMAND, STR_SET_NODE_VALUE, ECfgUnitType.COMMAND,
            paramDefenitions, defaultParams ) {

          @Override
          public CfgOpcUaNode createInitCfg( ITsGuiContext aaContext, String aNodeId, int aNodeIndex, int aNodeCount ) {
            OpcUaServerConnCfg conConf =
                (OpcUaServerConnCfg)aaContext.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );

            EAtomicType type = EAtomicType.NONE;
            if( conConf != null ) {
              type = OpcUaUtils.getValueTypeOfNode( aaContext, conConf, aNodeId );
            }
            // dima 22.04.25 помечаем что тег на запись
            return new CfgOpcUaNode( aNodeId, false, true, true, type );
          }

        };

    realizationTypeRegister.registerType( cmdRealValueCommandExec );

    // ----------------------------------------------------
    // Определение первой реализации данных (простое - один к одному)

    paramDefenitions = new ElemArrayList<>( false );

    paramDefenitions.add( OP_DATA_JAVA_CLASS );
    paramDefenitions.add( OP_SYNCH_PERIOD );
    paramDefenitions.add( OP_IS_CURR );
    paramDefenitions.add( OP_IS_HIST );

    defaultParams = new OptionSet();
    OP_DATA_JAVA_CLASS.setValue( defaultParams, avStr( DATA_JAVA_CLASS_ONE_TO_ONE_DATA_TRANSMITTER_FACTORY ) );
    // old version
    // OP_SYNCH_PERIOD.setValue( defaultParams, avInt( -1 ) );
    // OP_IS_CURR.setValue( defaultParams, avBool( false ) );
    // OP_IS_HIST.setValue( defaultParams, avBool( false ) );
    // new version
    OP_SYNCH_PERIOD.setValue( defaultParams, avInt( 1000 ) );
    OP_IS_CURR.setValue( defaultParams, avBool( true ) );
    OP_IS_HIST.setValue( defaultParams, avBool( true ) );

    ICfgUnitRealizationType dataOneToOne = new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_ONT_TO_ONE_DATA,
        STR_ONE_2_ONE, ECfgUnitType.DATA, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( dataOneToOne );

    // ----------------------------------------------------
    // Определение второй реализации данных (битовое данное из интового тега)

    paramDefenitions = new ElemArrayList<>( false );

    paramDefenitions.add( OP_DATA_JAVA_CLASS );
    paramDefenitions.add( OP_BIT_INDEX );
    paramDefenitions.add( OP_SYNCH_PERIOD );
    paramDefenitions.add( OP_IS_CURR );
    paramDefenitions.add( OP_IS_HIST );

    defaultParams = new OptionSet();
    OP_DATA_JAVA_CLASS.setValue( defaultParams, avStr( DATA_JAVA_CLASS_ONE_INT_TO_ONE_BIT_DATA_TRANSMITTER_FACTORY ) );
    OP_BIT_INDEX.setValue( defaultParams, avInt( 0 ) );

    OP_SYNCH_PERIOD.setValue( defaultParams, avInt( 0 ) );
    OP_IS_CURR.setValue( defaultParams, avBool( true ) );
    OP_IS_HIST.setValue( defaultParams, avBool( true ) );

    ICfgUnitRealizationType dataIntToOne =
        new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_ONE_INT_TO_ONE_BIT_DATA, STR_BIT_FROM_WORD,
            ECfgUnitType.DATA, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( dataIntToOne );

    // ----------------------------------------------------
    // Определение первой реализации НСИ атрибута (простое - один к одному)

    paramDefenitions = new ElemArrayList<>( false );

    paramDefenitions.add( OP_DATA_JAVA_CLASS );

    defaultParams = new OptionSet();
    OP_DATA_JAVA_CLASS.setValue( defaultParams, avStr( JAVA_CLASS_ONE_TO_ONE_RRI_ATTR_TRANSMITTER_FACTORY ) );

    ICfgUnitRealizationType oneNodeToOneRriAttr = new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_ONE_TO_ONE_RRI,
        STR_ONE_2_ONE, ECfgUnitType.RRI, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( oneNodeToOneRriAttr );

    // ----------------------------------------------------
    // Определение второй реализации данных (битовое данное из интового тега)

    paramDefenitions = new ElemArrayList<>( false );

    paramDefenitions.add( OP_DATA_JAVA_CLASS );
    paramDefenitions.add( OP_BIT_INDEX );

    defaultParams = new OptionSet();
    OP_DATA_JAVA_CLASS.setValue( defaultParams, avStr( JAVA_CLASS_ONE_INT_TO_ONE_BIT_RRI_ATTR_TRANSMITTER_FACTORY ) );
    OP_BIT_INDEX.setValue( defaultParams, avInt( 0 ) );

    ICfgUnitRealizationType intNodeToOneRriBit =
        new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_ONE_INT_TO_ONE_BIT_RRI, STR_BIT_FROM_WORD,
            ECfgUnitType.RRI, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( intNodeToOneRriBit );

    // ----------------------------------------------------
    // Определение первой реализации события

    // ----------------------------------------------------
    // Определение второй реализации события

    paramDefenitions = new ElemArrayList<>( false );

    paramDefenitions.add( OP_EVENT_SENDER_JAVA_CLASS );
    paramDefenitions.add( OP_CONDITION_JAVA_CLASS );
    paramDefenitions.add( OP_PARAM_FORMER_JAVA_CLASS );
    paramDefenitions.add( OP_FORMER_EVENT_PARAM );

    defaultParams = new OptionSet();
    OP_EVENT_SENDER_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_JAVA_CLASS_OPC_TAGS_EVENT_SENDER ) );
    OP_CONDITION_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_JAVA_CLASS_TAG_VALUE_CHANGED_CONDITION ) );
    OP_PARAM_FORMER_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_ONE_TAG_CHANGED_PARAM_FORMER ) );
    OP_FORMER_EVENT_PARAM.setValue( defaultParams, avStr( "oldVal;newVal" ) ); //$NON-NLS-1$

    ICfgUnitRealizationType opcTagsEventSender2 =
        new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_TAG_VALUE_CHANGED, STR_TAG_VAL_CHANGED_EVENT,
            ECfgUnitType.EVENT, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( opcTagsEventSender2 );

    // ----------------------------------------------------
    // Определение третьей реализации события

    paramDefenitions = new ElemArrayList<>( false );

    paramDefenitions.add( OP_EVENT_SENDER_JAVA_CLASS );
    paramDefenitions.add( OP_CONDITION_JAVA_CLASS );
    paramDefenitions.add( OP_PARAM_FORMER_JAVA_CLASS );
    paramDefenitions.add( OP_FORMER_EVENT_PARAM );
    paramDefenitions.add( OP_CONDITION_SWITCH_ON );
    paramDefenitions.add( OP_CONDITION_SWITCH_OFF );

    defaultParams = new OptionSet();
    OP_EVENT_SENDER_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_JAVA_CLASS_OPC_TAGS_EVENT_SENDER ) );
    OP_CONDITION_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_JAVA_CLASS_TAG_SWITCH_CONDITION ) );
    OP_CONDITION_SWITCH_ON.setValue( defaultParams, avBool( true ) );
    OP_CONDITION_SWITCH_OFF.setValue( defaultParams, avBool( false ) );
    OP_PARAM_FORMER_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_ONE_TAG_TO_ONE_PARAM_FORMER ) );
    OP_FORMER_EVENT_PARAM.setValue( defaultParams, avStr( STR_ON ) );

    ICfgUnitRealizationType opcTagsEventSender3 = new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_SWITCH_EVENT,
        STR_BIT_TAG_VAL_CHANGED_EVENT, ECfgUnitType.EVENT, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( opcTagsEventSender3 );

    // ----------------------------------------------------
    // Определение четвертой реализации события

    paramDefenitions = new ElemArrayList<>( false );

    paramDefenitions.add( OP_EVENT_SENDER_JAVA_CLASS );
    paramDefenitions.add( OP_CONDITION_JAVA_CLASS );
    paramDefenitions.add( OP_PARAM_FORMER_JAVA_CLASS );
    paramDefenitions.add( OP_FORMER_EVENT_PARAM );
    paramDefenitions.add( OP_CONDITION_SWITCH_ON );
    paramDefenitions.add( OP_CONDITION_SWITCH_OFF );
    paramDefenitions.add( OP_BIT_INDEX );

    defaultParams = new OptionSet();
    OP_EVENT_SENDER_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_JAVA_CLASS_OPC_TAGS_EVENT_SENDER ) );
    OP_CONDITION_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_JAVA_CLASS_TAG_SWITCH_CONDITION ) );
    OP_CONDITION_SWITCH_ON.setValue( defaultParams, avBool( true ) );
    OP_CONDITION_SWITCH_OFF.setValue( defaultParams, avBool( false ) );
    OP_PARAM_FORMER_JAVA_CLASS.setValue( defaultParams, avStr( EVENTS_ONE_TAG_TO_ONE_PARAM_FORMER ) );
    OP_FORMER_EVENT_PARAM.setValue( defaultParams, avStr( STR_ON ) );
    OP_BIT_INDEX.setValue( defaultParams, avInt( 0 ) );

    ICfgUnitRealizationType opcTagsEventSender4 =
        new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_BIT_SWITCH_EVENT, STR_BIT_WORD_TAG_VAL_CHANGED_EVENT,
            ECfgUnitType.EVENT, paramDefenitions, defaultParams );

    realizationTypeRegister.registerType( opcTagsEventSender4 );
  }

  /**
   * По описанию конфигурации подключения к OPC UA выдает имя секции для хранения кэша узлов
   *
   * @param aSelConfig - описание конфигурации подключения к OPC UA
   * @return строка с именем секции
   */
  @SuppressWarnings( "nls" )
  public static String getCachedTreeSectionName( IOpcUaServerConnCfg aSelConfig ) {
    String ipAddress = extractIP( aSelConfig );
    return SECTID_OPC_UA_NODES_TEMPLATE + ".IP_Address_" + ipAddress + ".UserName_" + aSelConfig.login();
  }

  /**
   * По описанию конфигурации подключения к OPC UA выдает имя соотв. секции для хранения метаинформации
   *
   * @param aSectNameTemplate - шаблон для генерации названия секции
   * @param aSelConfig - описание конфигурации подключения к OPC UA
   * @return строка с именем секции
   */
  @SuppressWarnings( "nls" )
  public static String getTreeSectionNameByConfig( String aSectNameTemplate, IOpcUaServerConnCfg aSelConfig ) {
    // создаем имя секции
    String ipAddress = extractIP( aSelConfig );
    return aSectNameTemplate + ".IP_Address_" + ipAddress;
    // for debug
    // return aSectNameTemplate;
  }

  /**
   * Получение в строковом виде IP адреса из конфигурации подключения к opc серверу с заменой точек на подчёркивание
   *
   * @param aSelConfig IOpcUaServerConnCfg - конфигурация подключения к opc серверу
   * @return String - строковое представление IP адреса с заменой точек на подчёркивание
   */
  @SuppressWarnings( "nls" )
  private static String extractIP( IOpcUaServerConnCfg aSelConfig ) {
    // выделяем из хоста IP, opc.tcp://192.168.12.61:4840
    Pattern p = Pattern.compile( "[a-z:\\.\\/]+([0-9\\.]+)" );
    String host = aSelConfig.host();
    Matcher n = p.matcher( host );
    String ipAddress = "localhost";
    if( n.find() ) {
      ipAddress = n.group( 1 );
      // заменим точки на символы подчеркивания
      ipAddress = ipAddress.replace( '.', '_' );
    }
    return ipAddress;
  }

  /**
   * Determines existance of cach for configuration.
   *
   * @param aContext ITsGuiContext - context
   * @param aSelConfig IOpcUaServerConnCfg - opc ua server cfg
   * @return boolean - true - cach exists
   */
  public static boolean hasCachedOpcUaNodesTreeFor( ITsGuiContext aContext, IOpcUaServerConnCfg aSelConfig ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    String sectionName = OpcUaUtils.getCachedTreeSectionName( aSelConfig );
    return !storage.readColl( sectionName, UaTreeNode.KEEPER ).isEmpty();
  }

  private static IList<UaTreeNode> loadUaTreeNodes( ITsGuiContext aContext, IOpcUaServerConnCfg aConnCfg ) {
    ITsWorkroom workroom = aContext.eclipseContext().get( ITsWorkroom.class );
    TsInternalErrorRtException.checkNull( workroom );
    IKeepablesStorage storage = workroom.getStorage( Activator.PLUGIN_ID ).ktorStorage();
    String sectionName = OpcUaUtils.getCachedTreeSectionName( aConnCfg );
    IList<UaTreeNode> retVal = new ElemArrayList<>( storage.readColl( sectionName, UaTreeNode.KEEPER ) );
    return retVal;
  }

  /**
   * По описанию NodeId получить тип узла
   *
   * @param aContext - контекст
   * @param aConnCfg - конфигурация подключения
   * @param aNodeId - адрес узла в OPC UA
   * @return тип данного для узла
   */
  public static EAtomicType getValueTypeOfNode( ITsGuiContext aContext, IOpcUaServerConnCfg aConnCfg, String aNodeId ) {
    IList<UaTreeNode> nodes;
    NodeId nodeId = NodeId.parse( aNodeId );
    // сначала проверяем в кэше
    String sectionName = OpcUaUtils.getCachedTreeSectionName( aConnCfg );
    if( section2NodesList.hasKey( sectionName ) ) {
      nodes = section2NodesList.getByKey( sectionName );
    }
    else {
      nodes = loadUaTreeNodes( aContext, aConnCfg );
    }
    for( UaTreeNode node : nodes ) {
      NodeId candidateNodeId = NodeId.parse( node.getNodeId() );
      if( candidateNodeId.equals( nodeId ) ) {
        return node.getDataType();
      }
    }
    return EAtomicType.NONE;
  }

  /**
   * По описанию NodeId получить полное описание узла UaTreeNode
   *
   * @param aContext - контекст
   * @param aConnCfg - конфигурация подключения
   * @param aNodeId - адрес узла в OPC UA
   * @return UaTreeNode - полное описание узла
   */
  public static UaTreeNode getUaTreeNodeOfNodeId( ITsGuiContext aContext, IOpcUaServerConnCfg aConnCfg,
      String aNodeId ) {
    IList<UaTreeNode> nodes;
    NodeId nodeId = NodeId.parse( aNodeId );
    // сначала проверяем в кэше
    String sectionName = OpcUaUtils.getCachedTreeSectionName( aConnCfg );
    if( section2NodesList.hasKey( sectionName ) ) {
      nodes = section2NodesList.getByKey( sectionName );
    }
    else {
      nodes = loadUaTreeNodes( aContext, aConnCfg );
    }
    for( UaTreeNode node : nodes ) {
      NodeId candidateNodeId = NodeId.parse( node.getNodeId() );
      if( candidateNodeId.equals( nodeId ) ) {
        return node;
      }
    }
    return null;
  }

  /**
   * Обновляет информацию nodes OPC UA
   *
   * @param aContext - контекст
   * @param aConnCfg - конфигурация подключения к серверу OPC UA
   * @param aDoc - whole configuration of opc ua <-> s5 bridge
   */
  public static void updateNodesInfoesFromCache( ITsGuiContext aContext, IOpcUaServerConnCfg aConnCfg,
      OpcToS5DataCfgDoc aDoc ) {
    IList<UaTreeNode> nodes;

    // сначала проверяем в кэше
    String sectionName = OpcUaUtils.getCachedTreeSectionName( aConnCfg );
    if( section2NodesList.hasKey( sectionName ) ) {
      nodes = section2NodesList.getByKey( sectionName );
    }
    else {
      nodes = loadUaTreeNodes( aContext, aConnCfg );
    }
    IStringMapEdit<UaTreeNode> mappedNodes = new StringMap<>();
    for( UaTreeNode node : nodes ) {
      mappedNodes.put( node.getNodeId(), node );
    }

    IList<OpcToS5DataCfgUnit> units = aDoc.dataUnits();

    for( OpcToS5DataCfgUnit unit : units ) {
      IAvList unitNodes = unit.getDataNodes2();
      for( IAtomicValue unitNode : unitNodes ) {
        OpcNodeInfo nodeInfo = unitNode.asValobj();
        UaTreeNode uaTreeNode = mappedNodes.findByKey( nodeInfo.getNodeId().toParseableString() );
        if( uaTreeNode != null ) {
          UaTreeNode parentTreeNode =
              uaTreeNode.getParentNodeId() != null ? mappedNodes.findByKey( uaTreeNode.getParentNodeId() ) : null;
          nodeInfo.updateFromUaTreeNode( uaTreeNode, parentTreeNode );
        }
      }
    }
  }

  /**
   * Lets one to select opc serve config from the list of availables.
   *
   * @param aContext ITsGuiContext - context.
   * @return IOpcUaServerConnCfg - selected config or null.
   */
  public static IOpcUaServerConnCfg selectOpcServerConfig( ITsGuiContext aContext ) {
    TsNullArgumentRtException.checkNull( aContext );

    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    ISkConnection conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    // занесем параметры из файла в контекст
    AbstractAppPreferencesStorage apStorage =
        new AppPreferencesConfigIniStorage( new File( OPC_UA_SERVER_CONN_CFG_STORE_FILE ) );
    IAppPreferences appPreferences = new AppPreferences( apStorage );
    OpcUaServerConnCfgService cfgService = new OpcUaServerConnCfgService( appPreferences );

    IM5Model<IOpcUaServerConnCfg> model = m5.getModel( OpcUaServerConnCfgModel.MODEL_ID, IOpcUaServerConnCfg.class );

    IM5LifecycleManager<IOpcUaServerConnCfg> lm = new OpcUaServerConnCfgM5LifecycleManager( model, cfgService );

    TsDialogInfo di = new TsDialogInfo( aContext, DLG_SELECT_CFG_AND_OPEN, DLG_SELECT_CFG_AND_OPEN_D );
    // установим нормальный размер диалога
    di.setMinSize( new TsPoint( -30, -40 ) );
    // убираем поле фильтра
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( di.tsContext().params(), AvUtils.AV_FALSE );

    return M5GuiUtils.askSelectItem( di, model, null, lm.itemsProvider(), lm );
  }

  /**
   * Выполняет работы в отдельном потоке при открытом диалоге ожидания.
   *
   * @param aContext ITsGuiContext - контекст
   * @param aDialogName String - имя диалога ожидания.
   * @param aRunnable IRunnableWithProgress - реализация потока выполнения работы.
   */
  public static void runInWaitingDialog( final ITsGuiContext aContext, final String aDialogName,
      final IRunnableWithProgress aRunnable ) {

    final ProgressMonitorDialog dialog = new ProgressMonitorDialog( aContext.get( Shell.class ) ) {

      @Override
      protected Control createDialogArea( Composite aParent ) {
        Control c = super.createDialogArea( aParent );
        c.getShell().setText( aDialogName );
        return c;
      }
    };

    try {
      dialog.run( true, true, aRunnable );
    }
    catch( InvocationTargetException | InterruptedException e ) {
      Display.getDefault().asyncExec(
          () -> TsDialogUtils.error( aContext.get( Shell.class ), e.getCause() != null ? e.getCause() : e ) );

    }

  }

  /**
   * Возвращает битовый индекс в интовом теге, с помощью которого следует формировать булевое данное, заданное в gwid.
   *
   * @param aGwid - булевое данное, для которого идёёт поиск индекса.
   * @param aClsId2RriAttrInfoes - карта classId-wordId-boolDataId-index
   * @return BitIdx2RriDtoAttr - битовый индекс или null;
   */
  public static BitIdx2RriDtoAttr getDataBitIndexForRriAttrGwid( Gwid aGwid,
      IStringMap<StringMap<IList<BitIdx2RriDtoAttr>>> aClsId2RriAttrInfoes ) {
    if( !aClsId2RriAttrInfoes.hasKey( aGwid.classId() ) ) {
      return null;
    }

    StringMap<IList<BitIdx2RriDtoAttr>> classIndexes = aClsId2RriAttrInfoes.getByKey( aGwid.classId() );

    // перебор всех наборов для данного класса

    IStringList keys = classIndexes.keys();

    for( String key : keys ) {
      IList<BitIdx2RriDtoAttr> indexes = classIndexes.getByKey( key );

      for( BitIdx2RriDtoAttr indexData : indexes ) {
        if( indexData.dtoAttrInfo().id().equals( aGwid.propId() ) ) {
          return indexData;
        }
      }
    }

    return null;
  }

  /**
   * Возвращает битовый индекс в интовом теге, с помощью которого следует формировать булевое данное, заданное в gwid.
   *
   * @param aGwid - булевое данное, для которого идёёт поиск индекса.
   * @param aClsId2RtDataInfoes - карта classId-wordId-boolDataId-index
   * @return BitIdx2DtoEvent - битовый индекс или null;
   */
  public static BitIdx2DtoRtData getDataBitIndexForRtDataGwid( Gwid aGwid,
      IStringMap<StringMap<IList<BitIdx2DtoRtData>>> aClsId2RtDataInfoes ) {
    if( !aClsId2RtDataInfoes.hasKey( aGwid.classId() ) ) {
      return null;
    }

    StringMap<IList<BitIdx2DtoRtData>> classIndexes = aClsId2RtDataInfoes.getByKey( aGwid.classId() );

    // перебор всех наборов для данного класса

    IStringList keys = classIndexes.keys();

    for( String key : keys ) {
      IList<BitIdx2DtoRtData> indexes = classIndexes.getByKey( key );

      for( BitIdx2DtoRtData indexData : indexes ) {
        if( indexData.dtoRtdataInfo().id().equals( aGwid.propId() ) ) {
          return indexData;
        }
      }
    }

    return null;
  }

  /**
   * Возвращает битовый индекс в интовом теге, с помощью которого следует формировать булевое данное, заданное в gwid.
   *
   * @param aGwid - булевое данное, для которого идёёт поиск индекса.
   * @param aClsId2EvtInfoes - карта classId-wordId-boolDataId-index
   * @return BitIdx2DtoEvent - битовый индекс или null;
   */
  public static BitIdx2DtoEvent getBitIndexForEvtGwid( Gwid aGwid,
      IStringMap<StringMap<IList<BitIdx2DtoEvent>>> aClsId2EvtInfoes ) {
    if( !aClsId2EvtInfoes.hasKey( aGwid.classId() ) ) {
      return null;
    }

    StringMap<IList<BitIdx2DtoEvent>> classIndexes = aClsId2EvtInfoes.getByKey( aGwid.classId() );

    // перебор всех наборов для данного класса

    IStringList keys = classIndexes.keys();

    for( String key : keys ) {
      IList<BitIdx2DtoEvent> indexes = classIndexes.getByKey( key );

      for( BitIdx2DtoEvent indexData : indexes ) {
        if( indexData.dtoEventInfo().id().equals( aGwid.propId() ) ) {
          return indexData;
        }
      }
    }
    return null;
  }

  /**
   * Clear all cached meta info
   *
   * @param aOpcUaServerConnCfg OPC UA server config
   */
  public static void clearCache( IOpcUaServerConnCfg aOpcUaServerConnCfg ) {
    String sectId = getTreeSectionNameByConfig( SECTID_OPC_UA_NODES_2_RTD_GWIDS_TEMPLATE, aOpcUaServerConnCfg );
    if( sect2skid2NodeIdMap.containsKey( sectId ) ) {
      sect2nodeId2GwidMap.remove( sectId );
    }

    sectId = getTreeSectionNameByConfig( SECTID_OPC_UA_NODES_2_CLS_GWIDS_TEMPLATE, aOpcUaServerConnCfg );
    if( sect2classGwid2NodeIdMap.containsKey( sectId ) ) {
      sect2classGwid2NodeIdMap.remove( sectId );
    }
    sectId = getTreeSectionNameByConfig( SECTID_OPC_UA_NODES_2_SKIDS_TEMPLATE, aOpcUaServerConnCfg );
    if( sect2skid2NodeIdMap.containsKey( sectId ) ) {
      sect2skid2NodeIdMap.remove( sectId );
    }
  }

  /**
   * Читает из справочника метаинформацию о командах
   *
   * @param aContext контекст приложения
   * @param aConn соединение с сервером
   * @return структура описывающая все команды всех классов OPC UA
   */
  public static StringMap<IList<IDtoCmdInfo>> readClass2CmdInfoes( ITsGuiContext aContext, ISkConnection aConn ) {
    StringMap<IList<IDtoCmdInfo>> dtoCmdInfoesMap = new StringMap<>();
    // открываем справочник команд
    String refbookName = RBID_CMD_OPCUA;
    ISkRefbookService skRefServ = (ISkRefbookService)aConn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    // FIXME warninig: "There is no refbook :%s", RBID_CMD_OPCUA
    if( skRefServ.findRefbook( refbookName ) == null ) {
      TsDialogUtils.error( aContext.get( Shell.class ), "There is no refbook :%s", refbookName );
      return null;
    }
    IList<ISkRefbookItem> rbItems = skRefServ.findRefbook( refbookName ).listItems();
    for( ISkRefbookItem myRbItem : rbItems ) {
      String strid = myRbItem.strid();
      // выделяем id класса
      String classId = extractClassId( strid );
      String cmdId = myRbItem.attrs().getValue( RBATRID_CMD_OPCUA___CMDID ).asString();// ID
      // название
      String name = myRbItem.nmName();
      // описание
      String descr = myRbItem.description();
      // аргументов пока НЕТ
      StridablesList<IDataDef> cmdArgs = new StridablesList<>();
      // String argType = myRbItem.attrs().getValue( "ArgType" ).asString();
      //
      // EAtomicType type = getDataType( argType );
      // StridablesList<IDataDef> cmdArgs;
      // cmdArgs = switch( type ) {
      // case INTEGER -> new StridablesList<>( Ods2DtoCmdInfoParser.CMD_ARG_INT );
      // case BOOLEAN -> throw invalidArgTypeExcpt( cmdId, type );
      // case FLOATING -> new StridablesList<>( Ods2DtoCmdInfoParser.CMD_ARG_FLT );
      // case NONE -> new StridablesList<>();
      // case STRING -> throw invalidArgTypeExcpt( cmdId, type );
      // case TIMESTAMP -> throw invalidArgTypeExcpt( cmdId, type );
      // case VALOBJ -> throw invalidArgTypeExcpt( cmdId, type );
      // };

      DtoCmdInfo dtoCmdInfo = DtoCmdInfo.create1( cmdId, //
          cmdArgs, //
          OptionSetUtils.createOpSet( //
              IAvMetaConstants.TSID_NAME, name, //
              IAvMetaConstants.TSID_DESCRIPTION, descr //
          ) ); //
      // считали, ищем список этого класса
      if( !dtoCmdInfoesMap.hasKey( classId ) ) {
        IListEdit<IDtoCmdInfo> classCmds = new ElemArrayList<>( false );
        dtoCmdInfoesMap.put( classId, classCmds );
      }
      IListEdit<IDtoCmdInfo> classCmds = (IListEdit<IDtoCmdInfo>)dtoCmdInfoesMap.getByKey( classId );
      classCmds.add( dtoCmdInfo );
    }
    return dtoCmdInfoesMap;
  }

  static String extractClassId( String strid ) {
    return strid.split( "\\." )[0]; //$NON-NLS-1$
  }

  /**
   * Читает из справочника метаинформацию о парах USkat cmdId -> код OPC команды
   *
   * @param aConn соединение с сервером
   * @return структура описывающая все команды всех классов OPC UA
   */
  public static IStringMap<IStringMap<Integer>> readClass2CmdIdx( ISkConnection aConn ) {
    IStringMapEdit<IStringMap<Integer>> retVal = new StringMap<>();
    // открываем справочник команд
    String refbookName = RBID_CMD_OPCUA;
    ISkRefbookService skRefServ = (ISkRefbookService)aConn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    ISkRefbook refBook = skRefServ.findRefbook( refbookName );
    if( refBook == null ) {
      return retVal;
    }
    IList<ISkRefbookItem> rbItems = refBook.listItems();
    for( ISkRefbookItem myRbItem : rbItems ) {
      String strid = myRbItem.strid();
      // выделяем id класса
      String classId = extractClassId( strid );
      int cmdIndex = myRbItem.attrs().getValue( RBATRID_CMD_OPCUA___INDEX ).asInt();// Код
      String cmdId = myRbItem.attrs().getValue( RBATRID_CMD_OPCUA___CMDID ).asString();// ID
      // считали, ищем список этого класса
      if( !retVal.hasKey( classId ) ) {
        IStringMapEdit<Integer> classCmds = new StringMap<>();
        retVal.put( classId, classCmds );
      }
      IStringMapEdit<Integer> classCmds = (IStringMapEdit<Integer>)retVal.getByKey( classId );
      classCmds.put( cmdId, Integer.valueOf( cmdIndex ) );
    }
    return retVal;
  }

  /**
   * Считывает из справочника масочные rtData
   *
   * @param aConn - соединение с сервером
   * @return карта класс->id битового массива -> список его rtData типа булевое
   */
  public static StringMap<StringMap<IList<BitIdx2DtoRtData>>> readRtDataInfoes( ISkConnection aConn ) {
    StringMap<StringMap<IList<BitIdx2DtoRtData>>> retVal = new StringMap<>();
    // открываем справочник битовых масок
    String refbookName = RBID_BITMASK;
    ISkRefbookService skRefServ = (ISkRefbookService)aConn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    IList<ISkRefbookItem> rbItems = skRefServ.findRefbook( refbookName ).listItems();
    for( ISkRefbookItem rbItem : rbItems ) {
      String strid = rbItem.strid();
      // выделяем id класса
      String classId = extractClassId( strid );
      if( !retVal.hasKey( classId ) ) {
        StringMap<IList<BitIdx2DtoRtData>> rtDataMap = new StringMap<>();
        retVal.put( classId, rtDataMap );
      }
      StringMap<IList<BitIdx2DtoRtData>> rtDataMap = retVal.getByKey( classId );
      String bitArrayRtDataId = rbItem.attrs().getValue( RBATRID_BITMASK___IDW ).asString();
      if( !rtDataMap.hasKey( bitArrayRtDataId ) ) {
        rtDataMap.put( bitArrayRtDataId, new ElemArrayList<>( false ) );
      }

      BitIdx2DtoRtData dataInfo = readBitIdx2DtoRtData( bitArrayRtDataId, rbItem );
      if( dataInfo != null ) {
        IListEdit<BitIdx2DtoRtData> bitList = (IListEdit<BitIdx2DtoRtData>)rtDataMap.getByKey( bitArrayRtDataId );
        bitList.add( dataInfo );
      }
    }
    return retVal;
  }

  /**
   * FIXME проектно-зависмый код вынести отсюда <br>
   * Считывает из справочника команды CtrlSystem (проект Байконур)
   *
   * @param aConn - соединение с сервером
   * @param aCandidateCmd - кандидат на команду
   * @return {@link Boolean} true
   */
  public static boolean isCtrlSystenCommand( ISkConnection aConn, String aCandidateCmd ) {
    boolean retVal = false;
    // открываем справочник команд CtrlSystem
    String refbookName = "CtrlSystemCommands";
    ISkRefbookService skRefServ = (ISkRefbookService)aConn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    IList<ISkRefbookItem> rbItems = skRefServ.findRefbook( refbookName ).listItems();
    for( ISkRefbookItem rbItem : rbItems ) {
      String cmdId = rbItem.attrs().getValue( "identificator" ).asString();

      if( aCandidateCmd.compareTo( cmdId ) == 0 ) {
        retVal = true;
        break;
      }
    }
    return retVal;
  }

  /**
   * FIXME проектно-зависмый код вынести отсюда <br>
   * Считывает из справочника описание команды CtrlSystem (проект Байконур)
   *
   * @param aConn - соединение с сервером
   * @param aCandidateCmd - кандидат на команду
   * @return {@link ISkRefbookItem} описание команды
   */
  public static ISkRefbookItem getCtrlSystenCommand( ISkConnection aConn, String aCandidateCmd ) {
    ISkRefbookItem retVal = null;
    // открываем справочник команд CtrlSystem
    String refbookName = "CtrlSystemCommands";
    ISkRefbookService skRefServ = (ISkRefbookService)aConn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    IList<ISkRefbookItem> rbItems = skRefServ.findRefbook( refbookName ).listItems();
    for( ISkRefbookItem rbItem : rbItems ) {
      String cmdId = rbItem.attrs().getValue( "identificator" ).asString();
      if( aCandidateCmd.compareTo( cmdId ) == 0 ) {
        retVal = rbItem;
        break;
      }
    }
    return retVal;
  }

  /**
   * Считывает из справочника масочные events
   *
   * @param aConn - соединение с сервером
   * @return карта класс->id битового массива -> список его events типа булевое
   */
  public static StringMap<StringMap<IList<BitIdx2DtoEvent>>> readEventInfoes( ISkConnection aConn ) {
    StringMap<StringMap<IList<BitIdx2DtoEvent>>> retVal = new StringMap<>();
    // открываем справочник битовых масок
    String refbookName = RBID_BITMASK;
    ISkRefbookService skRefServ = (ISkRefbookService)aConn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    IList<ISkRefbookItem> rbItems = skRefServ.findRefbook( refbookName ).listItems();
    for( ISkRefbookItem rbItem : rbItems ) {
      String strid = rbItem.strid();
      // выделяем id класса
      String classId = extractClassId( strid );
      if( !retVal.hasKey( classId ) ) {
        StringMap<IList<BitIdx2DtoEvent>> rtDataMap = new StringMap<>();
        retVal.put( classId, rtDataMap );
      }
      StringMap<IList<BitIdx2DtoEvent>> rtDataMap = retVal.getByKey( classId );
      String bitArrayRtDataId = rbItem.attrs().getValue( RBATRID_BITMASK___IDW ).asString();
      if( !rtDataMap.hasKey( bitArrayRtDataId ) ) {
        rtDataMap.put( bitArrayRtDataId, new ElemArrayList<>( false ) );
      }

      BitIdx2DtoEvent eventInfo = readBitIdx2DtoEvent( bitArrayRtDataId, rbItem );
      if( eventInfo != null ) {
        IListEdit<BitIdx2DtoEvent> bitList = (IListEdit<BitIdx2DtoEvent>)rtDataMap.getByKey( bitArrayRtDataId );
        bitList.add( eventInfo );
      }
    }
    return retVal;
  }

  /**
   * Считывает из справочника масочные НСИ атрибуты
   *
   * @param aConn - соединение с сервером
   * @return карта класс->id битового массива -> список его rtData типа булевое
   */
  public static StringMap<StringMap<IList<BitIdx2RriDtoAttr>>> readRriAttrInfoes( ISkConnection aConn ) {
    StringMap<StringMap<IList<BitIdx2RriDtoAttr>>> retVal = new StringMap<>();
    // открываем справочник битовых масок
    String refbookName = RBID_BITMASK;
    ISkRefbookService skRefServ = (ISkRefbookService)aConn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    IList<ISkRefbookItem> rbItems = skRefServ.findRefbook( refbookName ).listItems();
    for( ISkRefbookItem rbItem : rbItems ) {
      String strid = rbItem.strid();
      // выделяем id класса
      String classId = extractClassId( strid );
      if( !retVal.hasKey( classId ) ) {
        StringMap<IList<BitIdx2RriDtoAttr>> rriAttrMap = new StringMap<>();
        retVal.put( classId, rriAttrMap );
      }
      StringMap<IList<BitIdx2RriDtoAttr>> rriAttrMap = retVal.getByKey( classId );
      String bitArrayRtDataId = rbItem.attrs().getValue( RBATRID_BITMASK___IDW ).asString();
      if( !rriAttrMap.hasKey( bitArrayRtDataId ) ) {
        rriAttrMap.put( bitArrayRtDataId, new ElemArrayList<>( false ) );
      }

      BitIdx2RriDtoAttr attrInfo = readBitIdx2RriDtoAttr( bitArrayRtDataId, rbItem );
      if( attrInfo != null ) {
        IListEdit<BitIdx2RriDtoAttr> bitList = (IListEdit<BitIdx2RriDtoAttr>)rriAttrMap.getByKey( bitArrayRtDataId );
        bitList.add( attrInfo );
      }
    }
    return retVal;
  }

  /**
   * Читает описание BitIdx2DtoRtData
   *
   * @param aBitArrayRtDataId - id переменной битового массива
   * @param aRefbookItem - элемент справочника масок
   * @return {@link BitIdx2DtoRtData} описание данного
   */
  private static BitIdx2DtoRtData readBitIdx2DtoRtData( String aBitArrayRtDataId, ISkRefbookItem aRefbookItem ) {
    // id данного
    String dataId = aRefbookItem.attrs().getValue( RBATRID_BITMASK___IDENTIFICATOR ).asString(); // ID;

    // проверяем что это rtData
    if( !dataId.startsWith( RTD_PREFIX ) ) {
      return null;
    }
    // bit index
    int bitIndex = aRefbookItem.attrs().getValue( RBATRID_BITMASK___BITN ).asInt();
    // название
    String name = aRefbookItem.nmName();
    // описание
    String descr = aRefbookItem.description();
    // sync
    boolean sync = false; // по умолчанию асинхронное
    int deltaT = sync ? 1000 : 1;

    IDtoRtdataInfo dataInfo = DtoRtdataInfo.create1( dataId, new DataType( EAtomicType.BOOLEAN ), //
        true, // isCurr
        true, // isHist
        sync, // isSync
        deltaT, // deltaT
        OptionSetUtils.createOpSet( //
            TSID_NAME, name, //
            TSID_DESCRIPTION, descr //
        ) );

    return new BitIdx2DtoRtData( aBitArrayRtDataId, bitIndex, dataInfo );

  }

  /**
   * Читает описание BitIdx2DtoEvent
   *
   * @param aBitArrayRtDataId - id переменной битового массива
   * @param aRefbookItem - элемент справочника масок
   * @return {@link BitIdx2DtoEvent} описание события
   */
  private static BitIdx2DtoEvent readBitIdx2DtoEvent( String aBitArrayRtDataId, ISkRefbookItem aRefbookItem ) {
    // id rt данного
    String dataId = aRefbookItem.attrs().getValue( RBATRID_BITMASK___IDENTIFICATOR ).asString(); // ID;

    // сочиняем evId
    String evtId = EVT_PREFIX + dataId.substring( RTD_PREFIX.length() );

    // bit index
    int bitIndex = aRefbookItem.attrs().getValue( RBATRID_BITMASK___BITN ).asInt();
    // название
    String name = aRefbookItem.nmName();
    // описание
    String descr = aRefbookItem.description();
    // 0->1
    String upText = aRefbookItem.attrs().getValue( RBATRID_BITMASK___ON ).asString();
    boolean up = false;
    if( !upText.isBlank() ) {
      up = true;
    }
    // 1->0
    boolean dn = false;
    String dnText = aRefbookItem.attrs().getValue( RBATRID_BITMASK___OFF ).asString();
    if( !dnText.isBlank() ) {
      dn = true;
    }
    if( up || dn ) {

      // for example FMT_BOOL_CHECK = "%Б[-|✔]"
      String FMT_ON = "%Б[" + dnText + "|" + upText + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      // создаем описание параметра
      DataDef EVPDD_ON = DataDef.create( EVPID_ON, EAtomicType.BOOLEAN, TSID_NAME, STR_N_EV_PARAM_ON, //
          TSID_DESCRIPTION, STR_D_EV_PARAM_ON, //
          TSID_IS_NULL_ALLOWED, AV_FALSE, //
          TSID_FORMAT_STRING, FMT_ON, //
          TSID_DEFAULT_VALUE, AV_FALSE );
      StridablesList<IDataDef> evParams = new StridablesList<>( EVPDD_ON );

      IDtoEventInfo evtInfo = DtoEventInfo.create1( evtId, true, //
          evParams, //
          OptionSetUtils.createOpSet( //
              IAvMetaConstants.TSID_NAME, name, //
              IAvMetaConstants.TSID_DESCRIPTION, descr //
          ) ); //

      return new BitIdx2DtoEvent( aBitArrayRtDataId, bitIndex, evtInfo, up, dn );
    }
    return null;
  }

  /**
   * Читает описание BitIdx2RriDtoAttr
   *
   * @param aBitArrayRtDataId - id переменной битового массива
   * @param aRefbookItem - элемент справочника масок
   * @return {@link BitIdx2RriDtoAttr} описание атрибута
   */
  private static BitIdx2RriDtoAttr readBitIdx2RriDtoAttr( String aBitArrayRtDataId, ISkRefbookItem aRefbookItem ) {
    // id атрибута
    String attrId = aRefbookItem.attrs().getValue( RBATRID_BITMASK___IDENTIFICATOR ).asString(); // ID;

    // проверяем что это НСИ атрибут
    if( !attrId.startsWith( RRI_PREFIX ) ) {
      return null;
    }
    // bit index
    int bitIndex = aRefbookItem.attrs().getValue( RBATRID_BITMASK___BITN ).asInt();
    // название
    String name = aRefbookItem.nmName();
    // описание
    String descr = aRefbookItem.description();

    IDtoAttrInfo attrInfo = DtoAttrInfo.create1( attrId, new DataType( EAtomicType.BOOLEAN ), //
        OptionSetUtils.createOpSet( //
            TSID_NAME, name, //
            TSID_DESCRIPTION, descr //
        ) );

    return new BitIdx2RriDtoAttr( aBitArrayRtDataId, bitIndex, attrInfo );
  }

  /**
   * Does synchronization of Opc Ua Nodes due to cfg units from Doc.
   *
   * @param aDoc OpcToS5DataCfgDoc - Doc, containing cfg units opc-s5.
   * @param aContext ITsGuiContext - context.
   * @param aDeleteUnnecessary true - delet Unnecessary Opc Ua Nodes
   */
  public static void synchronizeNodesCfgs( OpcToS5DataCfgDoc aDoc, ITsGuiContext aContext,
      boolean aDeleteUnnecessary ) {

    // OpcUaServerConnCfg conConf =
    // (OpcUaServerConnCfg)aContext.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );

    // if( conConf == null ) {
    // ETsDialogCode retCode = TsDialogUtils.askYesNoCancel( aContext.get( Shell.class ), MSG_SELECT_OPC_UA_SERVER );
    //
    // if( retCode == ETsDialogCode.CANCEL || retCode == ETsDialogCode.CLOSE || retCode == ETsDialogCode.NO ) {
    // return;
    // }
    // }

    IListEdit<CfgOpcUaNode> nodesCfgsList = new ElemArrayList<>( aDoc.getNodesCfgs() );
    IStringMapEdit<CfgOpcUaNode> nodesCfgs = new StringMap<>();
    for( CfgOpcUaNode node : nodesCfgsList ) {
      nodesCfgs.put( node.getNodeId(), node );
    }

    if( aDeleteUnnecessary ) {
      nodesCfgsList.clear();
    }

    IList<OpcToS5DataCfgUnit> dataCfgUnits = aDoc.dataUnits();
    for( OpcToS5DataCfgUnit unit : dataCfgUnits ) {
      IList<OpcNodeInfo> nodes = OpcUaUtils.convertToNodesList( unit.getDataNodes2() );

      String relizationTypeId = unit.getRelizationTypeId();
      CfgUnitRealizationTypeRegister typeReg2 = aContext.get( CfgUnitRealizationTypeRegister.class );

      ICfgUnitRealizationType realType = typeReg2.getTypeOfRealizationById( unit.getTypeOfCfgUnit(), relizationTypeId );

      for( int i = 0; i < nodes.size(); i++ ) {
        NodeId node = nodes.get( i ).getNodeId();
        if( !nodesCfgs.hasKey( node.toParseableString() ) ) {
          CfgOpcUaNode uaNode = realType.createInitCfg( aContext, node.toParseableString(), i, nodes.size() );
          nodesCfgs.put( node.toParseableString(), uaNode );
          nodesCfgsList.add( uaNode );

          if( unit.getRealizationOpts().hasKey( OpcUaUtils.OP_SYNCH_PERIOD.id() ) ) {
            uaNode.setSynch( OpcUaUtils.OP_SYNCH_PERIOD.getValue( unit.getRealizationOpts() ).asInt() > 0 );
          }
          // new CfgOpcUaNode( node.toParseableString(), false, true, false, EAtomicType.INTEGER ) );
        }
        else {
          if( !nodesCfgsList.hasElem( nodesCfgs.getByKey( node.toParseableString() ) ) ) {
            nodesCfgsList.add( nodesCfgs.getByKey( node.toParseableString() ) );
          }
        }
      }

      aDoc.setNodesCfgs( nodesCfgsList );
    }
  }

}
