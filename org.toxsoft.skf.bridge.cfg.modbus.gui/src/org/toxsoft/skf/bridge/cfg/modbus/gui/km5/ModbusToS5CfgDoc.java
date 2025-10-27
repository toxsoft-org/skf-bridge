package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import static org.toxsoft.skf.bridge.cfg.modbus.gui.l10n.ISkBridgeCfgModbusGuiSharedResources.*;

import java.io.*;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.keeper.std.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;

/**
 * Single MODBU-USkat bridge configuration.
 * <p>
 * A whole configuration of modbus <-> s5 bridge, contains several devisions (data, commands, events) each contains
 * units of configuration A unit - is a simple piece of configuration (for example one node to one gwid data
 * transmitter). A {@link ModbusToS5CfgDoc} corresponds to cfg files (.dlmcfg and .devcfg) and is source for auto
 * generating of this files.
 *
 * @author max
 */
public class ModbusToS5CfgDoc
    extends Stridable {

  private static final String CHECK_DOC = "CheckDoc"; //$NON-NLS-1$

  // private static String MAP_KEY_FORMAT_STR = "%s:%d %s_%d"; //$NON-NLS-1$

  /**
   * The keeper singleton.
   */
  public static final IEntityKeeper<ModbusToS5CfgDoc> KEEPER =
      new AbstractEntityKeeper<>( ModbusToS5CfgDoc.class, EEncloseMode.ENCLOSES_BASE_CLASS, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, ModbusToS5CfgDoc aEntity ) {
          aSw.incNewLine();

          // id, name, description
          aSw.writeAsIs( aEntity.id() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          aSw.writeQuotedString( aEntity.nmName() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          aSw.writeQuotedString( aEntity.description() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          FileKeeper.KEEPER.write( aSw, aEntity.getL2Path() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          aSw.writeQuotedString( aEntity.getCfgFilesPrefix() );
          aSw.writeSeparatorChar();

          aSw.writeEol();

          // Units
          // units count
          aSw.writeInt( aEntity.dataCfgUnits.size() );
          aSw.writeSeparatorChar();
          aSw.writeEol();

          for( int i = 0; i < aEntity.dataCfgUnits.size(); i++ ) {
            // one unit
            OpcToS5DataCfgUnit.KEEPER.write( aSw, aEntity.dataCfgUnits.get( i ) );
            aSw.writeSeparatorChar();
            aSw.writeEol();
          }

          aSw.writeQuotedString( CHECK_DOC );
          aSw.decNewLine();
        }

        @Override
        protected ModbusToS5CfgDoc doRead( IStrioReader aSr ) {
          // id, name, description
          String id = aSr.readIdPath();
          aSr.ensureSeparatorChar();
          String name = aSr.readQuotedString();
          aSr.ensureSeparatorChar();
          String descr = aSr.readQuotedString();
          aSr.ensureSeparatorChar();
          File l2Path = FileKeeper.KEEPER.read( aSr );
          aSr.ensureSeparatorChar();
          String cfgFileName = aSr.readQuotedString();
          aSr.ensureSeparatorChar();

          // Units
          // units count
          int unitsCount = aSr.readInt();
          aSr.ensureSeparatorChar();

          IListEdit<OpcToS5DataCfgUnit> units = new ElemArrayList<>();
          for( int i = 0; i < unitsCount; i++ ) {
            // one unit
            OpcToS5DataCfgUnit unit = OpcToS5DataCfgUnit.KEEPER.read( aSr );
            aSr.ensureSeparatorChar();
            units.add( unit );
          }

          if( !aSr.readQuotedString().equals( CHECK_DOC ) ) {
            LoggerUtils.errorLogger().error( LOG_ERR_READING_CONFIG );
          }

          ModbusToS5CfgDoc result = new ModbusToS5CfgDoc( id, name, descr );
          result.dataCfgUnits = units;
          result.setL2Path( l2Path );
          result.setCfgFilesPrefix( cfgFileName );
          return result;
        }
      };

  /**
   * List of data cfg units.
   */
  private IListEdit<OpcToS5DataCfgUnit> dataCfgUnits = new ElemArrayList<>();

  /**
   * List of nodes cfgs.
   */
  private IStringMapEdit<ModbusNode> nodesCfgs = new StringMap<>();

  /**
   * Path to l2 bridge
   */
  private File l2Path;

  /**
   * Prefix of cfg files ("prefix".dlmcfg, "prefix".devcfg)
   */
  private String cfgFilesPrefix;

  /**
   * Constructor by id, name and description/
   *
   * @param aId String - id
   * @param aName String - name
   * @param aDescription String - description
   */
  public ModbusToS5CfgDoc( String aId, String aName, String aDescription ) {
    super( aId, aName, aDescription );
  }

  /**
   * Sets {@link #nmName()}.
   *
   * @param aName String - short name
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  @Override
  public void setName( String aName ) {
    super.setName( aName );
  }

  /**
   * Sets {@link #description()}.
   *
   * @param aDescription String - description
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  @Override
  public void setDescription( String aDescription ) {
    super.setDescription( aDescription );
  }

  /**
   * @return list of {@link OpcToS5DataCfgUnit}
   */
  public IListEdit<OpcToS5DataCfgUnit> dataUnits() {
    return dataCfgUnits;
  }

  /**
   * @param aDataUnit - add data unit {@link OpcToS5DataCfgUnit}
   */
  public void addDataUnit( OpcToS5DataCfgUnit aDataUnit ) {
    dataCfgUnits.add( aDataUnit );
  }

  /**
   * @param aDataUnits - add list of {@link OpcToS5DataCfgUnit}
   */
  public void addDataUnits( IList<OpcToS5DataCfgUnit> aDataUnits ) {
    dataCfgUnits.addAll( aDataUnits );
  }

  /**
   * @param aDataUnit -remove data unit {@link OpcToS5DataCfgUnit}
   */
  public void removeDataUnit( OpcToS5DataCfgUnit aDataUnit ) {
    dataCfgUnits.remove( aDataUnit );
  }

  /**
   * Returns list of configurations for all node existed in document (and may be more - configs are not deleted)
   *
   * @return IList - list of configurations for all node existed in document
   */
  public IList<ModbusNode> getNodesCfgs() {
    // ensureNodesCfgs( aContext );
    return nodesCfgs.values();
  }

  /**
   * Update map of nodes
   *
   * @param aNodesCfgs - added nodes {@link ModbusNode}
   */
  public void setNodesCfgs( IList<ModbusNode> aNodesCfgs ) {
    nodesCfgs.clear();
    for( ModbusNode cfg : aNodesCfgs ) {
      // String mapKey = String.format( MAP_KEY_FORMAT_STR, cfg.getAddress().getIP().getHostAddress(),
      // Integer.valueOf( cfg.getAddress().getPort() ), cfg.getRequestType().name(),
      // Integer.valueOf( cfg.getRegister() ) );
      nodesCfgs.put( cfg.getId(), cfg );
      // old version
      // nodesCfgs.put( cfg.getRequestType().name() + "_" + cfg.getRegister(), cfg );
    }
  }

  /**
   * Ensured node map
   */
  public void ensureNodesCfgs() {
    nodesCfgs.clear();
    for( OpcToS5DataCfgUnit unit : dataCfgUnits ) {
      IList<ModbusNode> nodes = OpcUaUtils.convertToNodesList( unit.getDataNodes2() );
      ECfgUnitType type = unit.getTypeOfCfgUnit();

      for( int i = 0; i < nodes.size(); i++ ) {
        ModbusNode node = nodes.get( i );
        node.setOutput( type == ECfgUnitType.COMMAND );

        nodesCfgs.put( node.getId(), node );
      }
    }
  }

  /**
   * @return path to default directory generate to
   */
  public File getL2Path() {
    return l2Path;
  }

  /**
   * @param aL2Path - path to default directory generate to
   */
  public void setL2Path( File aL2Path ) {
    l2Path = aL2Path;
  }

  /**
   * @return template name for config files
   */
  public String getCfgFilesPrefix() {
    return cfgFilesPrefix;
  }

  /**
   * @param aCfgFilesPrefix - template name for config files
   */
  public void setCfgFilesPrefix( String aCfgFilesPrefix ) {
    cfgFilesPrefix = aCfgFilesPrefix;
  }

}
