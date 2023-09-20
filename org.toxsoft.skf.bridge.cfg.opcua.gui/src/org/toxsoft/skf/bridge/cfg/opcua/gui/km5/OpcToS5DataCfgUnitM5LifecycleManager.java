package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import java.io.*;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.core.log4j.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.logs.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;

/**
 * Lifecycle Manager of {@link UaTreeNode} entities.
 *
 * @author max
 */
public class OpcToS5DataCfgUnitM5LifecycleManager
    extends M5LifecycleManager<OpcToS5DataCfgUnit, ITsGuiContext> {

  /**
   * Журнал работы
   */
  private ILogger logger = LoggerWrapper.getLogger( this.getClass().getName() );

  /**
   * тестовый список конфигураций
   */
  private OpcToS5DataCfgDoc cfgDoc = new OpcToS5DataCfgDoc( "id", "name", "descr" );

  /**
   * Constructor by M5 model and context
   *
   * @param aModel IM5Model - model
   * @param aContext ITsGuiContext - context
   */
  public OpcToS5DataCfgUnitM5LifecycleManager( IM5Model<OpcToS5DataCfgUnit> aModel, ITsGuiContext aContext ) {
    super( aModel, true, true, true, true, aContext );

  }

  /**
   * Constructor by M5 model and service
   *
   * @param aModel IM5Model - model
   * @param aContext ITsGuiContext - context
   * @param aCfgDoc OpcToS5DataCfgDoc - cfg doc
   */
  public OpcToS5DataCfgUnitM5LifecycleManager( IM5Model<OpcToS5DataCfgUnit> aModel, ITsGuiContext aContext,
      OpcToS5DataCfgDoc aCfgDoc ) {
    super( aModel, true, true, true, true, aContext );
    cfgDoc = aCfgDoc;
  }

  @Override
  protected IList<OpcToS5DataCfgUnit> doListEntities() {
    return cfgDoc.dataUnits();
  }

  @Override
  protected OpcToS5DataCfgUnit doCreate( IM5Bunch<OpcToS5DataCfgUnit> aValues ) {
    String name = OpcToS5DataCfgUnitM5Model.DISPLAY_NAME.getFieldValue( aValues ).asString();
    String strid = "opctos5.bridge.cfg.unit.id" + System.currentTimeMillis();// OpcToS5DataCfgUnitM5Model.STRID.getFieldValue(
    EDataCfgType type = OpcToS5DataCfgUnitM5Model.TYPE.getFieldValue( aValues ).asValobj();
    // aValues ).asString();
    IList<Gwid> gwids = OpcToS5DataCfgUnitM5Model.GWIDS.getFieldValue( aValues );
    IList<NodeId> nodes = OpcToS5DataCfgUnitM5Model.NODES.getFieldValue( aValues );
    OpcToS5DataCfgUnit result = new OpcToS5DataCfgUnit( strid, name );
    result.setDataNodes( nodes );
    result.setDataGwids( gwids );
    result.setTypeOfDataCfg( type );
    cfgDoc.addDataUnit( result );
    return result;
  }

  @Override
  protected OpcToS5DataCfgUnit doEdit( IM5Bunch<OpcToS5DataCfgUnit> aValues ) {
    String name = OpcToS5DataCfgUnitM5Model.DISPLAY_NAME.getFieldValue( aValues ).asString();
    // String strid = OpcToS5DataCfgUnitM5Model.STRID.getFieldValue( aValues ).asString();
    IList<Gwid> gwids = OpcToS5DataCfgUnitM5Model.GWIDS.getFieldValue( aValues );
    EDataCfgType type = OpcToS5DataCfgUnitM5Model.TYPE.getFieldValue( aValues ).asValobj();
    IList<NodeId> nodes = OpcToS5DataCfgUnitM5Model.NODES.getFieldValue( aValues );

    OpcToS5DataCfgUnit result = aValues.originalEntity();
    result.setDataNodes( nodes );
    result.setName( name );
    result.setDataGwids( gwids );
    result.setTypeOfDataCfg( type );

    return result;
  }

  @Override
  protected void doRemove( OpcToS5DataCfgUnit aEntity ) {
    cfgDoc.removeDataUnit( aEntity );
  }

  void saveCurrState() {
    OpcToS5DataCfgDocService service = master().get( OpcToS5DataCfgDocService.class );
    service.saveCfgDoc( cfgDoc );
  }

  void generateFileFromCurrState() {
    try {

      // FileWriter fw = new FileWriter( "C://tmp//333.txt" );
      // CharOutputStreamWriter chOut = new CharOutputStreamWriter( fw );
      // StrioWriter strioWriter = new StrioWriter( chOut );

      IAvTree avTree = OpcToS5DataCfgConverter.convertToTree( cfgDoc );
      // OpcToS5DataCfgDoc.KEEPER.write( strioWriter, cfgDoc );
      String TMP_DEST_FILE = "destDlmFile.tmp";

      AvTreeKeeper.KEEPER.write( new File( TMP_DEST_FILE ), avTree );

      String DLM_CONFIG_STR = "DlmConfig = ";

      PinsConfigFileFormatter.format( TMP_DEST_FILE, "C://tmp//333.txt", DLM_CONFIG_STR );

      // fw.flush();
      // fw.close();

    }
    catch( Exception e ) {
      e.printStackTrace();
      // Ошибка создания писателя канала
      // throw new TsIllegalArgumentRtException( e );
    }
  }
}
