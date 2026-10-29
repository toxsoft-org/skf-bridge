package org.toxsoft.skf.brifge.cfg.s7.gui.utils;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.utils.ISkResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.utils.OpcUaUtils.*;

import java.io.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.av.list.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skf.brifge.cfg.s7.gui.type.*;

/**
 * Реализация генератора содержимого файла конфигурации верхней половины драйвера S7 (s7.dlmcfg)
 *
 * @author dima
 */
public class S7DlmCfgGenerator
    implements IS7DlmCfgGenerator {

  private static final String DATA_JAVA_CLASS_ONE_TO_ONE_DATA_TRANSMITTER_FACTORY =
      "ru.toxsoft.l2.dlm.opc_bridge.submodules.data.OneToOneDataTransmitterFactory"; //$NON-NLS-1$

  private final static String CFG_CMD_UNIT_ID_FORMAT = "opctos5.bridge.cfg.cmd.unit.id%d.%s"; //$NON-NLS-1$

  /**
   * все S7 устройства
   */
  protected IListEdit<GuiS7Device> s7Devices = new ElemArrayList<>();

  /**
   * Added device info to driver configuration
   *
   * @param aDevice S7 device configuration info
   */
  public void addS7Device( GuiS7Device aDevice ) {
    s7Devices.add( aDevice );
  }

  @Override
  public IAvTree generate() {
    // TODO
    return IAvTree.NULL;
  }

  static IAvList convertToAtomicList( IList<GuiS7Tag> aNodeList ) {
    IAvListEdit result = new AvList( new ElemArrayList<>() );
    for( GuiS7Tag node : aNodeList ) {
      result.add( avValobj( node, GuiS7TagKeeper.KEEPER, GuiS7TagKeeper.KEEPER_ID ) );
    }
    return result;
  }

  /**
   * Just for testing
   *
   * @param aArgs program arguments
   */
  @SuppressWarnings( "nls" )
  public static void main( String[] aArgs ) {
    TsValobjUtils.registerKeeperIfNone( ECfgUnitType.KEEPER_ID, ECfgUnitType.KEEPER );
    TsValobjUtils.registerKeeperIfNone( GuiS7Device.KEEPER_ID, GuiS7Device.KEEPER );
    TsValobjUtils.registerKeeperIfNone( GuiS7DataBlock.KEEPER_ID, GuiS7DataBlock.KEEPER );
    TsValobjUtils.registerKeeperIfNone( GuiS7TagKeeper.KEEPER_ID, GuiS7TagKeeper.KEEPER );
    S7DlmCfgGenerator dlmCfgGenerator = new S7DlmCfgGenerator();

    OpcToS5DataCfgDoc opcToS5DataCfgDoc =
        new OpcToS5DataCfgDoc( "S7ToS5DataCfgDocId", "S7ToS5DataCfgDocName", "S7ToS5DataCfgDocDescription" );
    IListEdit<OpcToS5DataCfgUnit> units = new ElemArrayList<>();
    IList<GuiS7Device> devList = S7DevCfgGenerator.getTestConfig();
    for( GuiS7Device device : devList ) {
      for( GuiS7DataBlock block : device.getDataBlocks() ) {
        for( GuiS7Tag tag : block.getTags() ) {
          IGwidList gwidList = new GwidList( Gwid.createRtdata( "bi.AnalogInput", "biObj", "currVal" ) );
          IAvList tagList = convertToAtomicList( new SingleItemList<>( tag ) );
          OpcToS5DataCfgUnit opcToS5DataCfgUnit = new OpcToS5DataCfgUnit( "id", "name", gwidList, tagList );
          opcToS5DataCfgUnit.setTypeOfCfgUnit( ECfgUnitType.DATA );

          // ----------------------------------------------------
          // Определение первой реализации данных (простое - один к одному)

          IListEdit<IDataDef> paramDefinitions = new ElemArrayList<>( false );

          paramDefinitions.add( OpcUaUtils.OP_DATA_JAVA_CLASS );
          paramDefinitions.add( OpcUaUtils.OP_SYNCH_PERIOD );
          paramDefinitions.add( OpcUaUtils.OP_IS_CURR );
          paramDefinitions.add( OpcUaUtils.OP_IS_HIST );

          OptionSet defaultParams = new OptionSet();
          OpcUaUtils.OP_DATA_JAVA_CLASS.setValue( defaultParams,
              avStr( DATA_JAVA_CLASS_ONE_TO_ONE_DATA_TRANSMITTER_FACTORY ) );
          OpcUaUtils.OP_SYNCH_PERIOD.setValue( defaultParams, avInt( 1000 ) );
          OpcUaUtils.OP_IS_CURR.setValue( defaultParams, avBool( true ) );
          OpcUaUtils.OP_IS_HIST.setValue( defaultParams, avBool( true ) );

          ICfgUnitRealizationType dataOneToOne =
              new CfgUnitRealizationType( OpcUaUtils.CFG_UNIT_REALIZATION_TYPE_ONT_TO_ONE_DATA, STR_ONE_2_ONE,
                  ECfgUnitType.DATA, paramDefinitions, defaultParams );

          opcToS5DataCfgUnit.setRelizationTypeId( dataOneToOne.id() );
          opcToS5DataCfgUnit.setRealizationOpts( defaultParams );

          opcToS5DataCfgDoc.addDataUnit( opcToS5DataCfgUnit );
          units.add( opcToS5DataCfgUnit );
          units.add( getCmdCfgUnit( Gwid.createCmd( "bi.AnalogInput", "biObj", "cmdVal" ), tag ) );
        }
      }
    }
    IAvTree dlmconfigTree = OpcToS5DataCfgConverter.convertToDlmCfgTree( units, null, aNodeEntity -> {
      GuiS7Tag nodeid = aNodeEntity.asValobj();
      return new Pair<>( nodeid.getDataBlock().getS7Device().id(), nodeid.getId() );
    } );
    String TMP_DEST_FILE = "destDlmFile.tmp"; //$NON-NLS-1$
    File tmpFile = new File( TMP_DEST_FILE );
    AvTreeKeeper.KEEPER.write( new File( TMP_DEST_FILE ), dlmconfigTree );

    String DLM_CONFIG_STR = "DlmConfig = "; //$NON-NLS-1$
    try {
      PinsConfigFileFormatter.format( TMP_DEST_FILE, "s7bench.dlmcfg", DLM_CONFIG_STR, true );
    }
    catch( IOException ex ) {
      // TODO Auto-generated catch block
      LoggerUtils.error( ex );
    }
  }

  // описание одной команды
  private static OpcToS5DataCfgUnit getCmdCfgUnit( Gwid aCmdGwid, GuiS7Tag aTag ) {
    String strid =
        String.format( CFG_CMD_UNIT_ID_FORMAT, Long.valueOf( System.currentTimeMillis() ), aCmdGwid.strid() );
    ECfgUnitType type = ECfgUnitType.COMMAND;

    IList<Gwid> gwids = new ElemArrayList<>( aCmdGwid );
    IAvList tagList = convertToAtomicList( new SingleItemList<>( aTag ) );

    String name = STR_LINK_PREFIX + aCmdGwid.canonicalString();

    OpcToS5DataCfgUnit unit = new OpcToS5DataCfgUnit( strid, name, gwids, tagList );

    IListEdit<IDataDef> paramDefenitions = new ElemArrayList<>( false );

    paramDefenitions.add( OP_CMD_JAVA_CLASS );
    paramDefenitions.add( OP_CMD_VALUE_PARAM_ID );
    paramDefenitions.add( OP_CMD_OPC_ID );

    IOptionSetEdit defaultParams = new OptionSet();
    OP_CMD_JAVA_CLASS.setValue( defaultParams, avStr( COMMANDS_JAVA_CLASS_VALUE_COMMAND_BY_ONE_TAG_EXEC ) );

    ICfgUnitRealizationType realType = new CfgUnitRealizationType( CFG_UNIT_REALIZATION_TYPE_VALUE_COMMAND,
        STR_SET_NODE_VALUE, ECfgUnitType.COMMAND, paramDefenitions, defaultParams ) {

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

    unit.setTypeOfCfgUnit( type );
    unit.setRelizationTypeId( realType.id() );

    OptionSet realization = new OptionSet();
    OpcUaUtils.OP_CMD_JAVA_CLASS.setValue( realization, avStr( COMMANDS_JAVA_CLASS_VALUE_COMMAND_EXEC ) );

    // if( cmd2Nodes.paramIds() != null && cmd2Nodes.paramIds().size() == 1 ) {
    // OpcUaUtils.OP_CMD_VALUE_PARAM_ID.setValue( realization, avStr( cmd2Nodes.paramIds().first() ) );
    // }

    unit.setRealizationOpts( realization );
    return unit;
  }

}
