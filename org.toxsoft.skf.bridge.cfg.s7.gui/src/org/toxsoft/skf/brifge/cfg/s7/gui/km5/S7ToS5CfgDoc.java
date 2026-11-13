package org.toxsoft.skf.brifge.cfg.s7.gui.km5;

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
import org.toxsoft.skf.bridge.cfg.modbus.gui.type.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;
import org.toxsoft.skf.brifge.cfg.s7.gui.type.*;

/**
 * Single SIMATIC S7-USkat bridge configuration.
 * <p>
 * A whole configuration of SIMATIC S7 <-> s5 bridge, contains several devisions (data, commands, events) each contains
 * units of configuration A unit - is a simple piece of configuration (for example one node to one gwid data
 * transmitter). A {@link S7ToS5CfgDoc} corresponds to cfg files (.dlmcfg and .devcfg) and is source for auto generating
 * of this files.
 *
 * @author dima
 */
public class S7ToS5CfgDoc
    extends Stridable {

  private static final String CHECK_DOC = "CheckDoc"; //$NON-NLS-1$

  /**
   * The keeper singleton.
   */
  public static final IEntityKeeper<S7ToS5CfgDoc> KEEPER =
      new AbstractEntityKeeper<>( S7ToS5CfgDoc.class, EEncloseMode.ENCLOSES_BASE_CLASS, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, S7ToS5CfgDoc aEntity ) {
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

          IList<IStringList> properties = aEntity.getProperties();

          // Properties
          if( properties.size() > 0 ) {
            // props count
            aSw.writeInt( properties.size() );
            aSw.writeSeparatorChar();
            aSw.writeEol();

            for( int i = 0; i < properties.size(); i++ ) {
              // one prop
              StringListKeeper.KEEPER.write( aSw, properties.get( i ) );
              aSw.writeSeparatorChar();
              aSw.writeEol();
            }
          }

          aSw.writeQuotedString( CHECK_DOC );
          aSw.decNewLine();
        }

        @Override
        protected S7ToS5CfgDoc doRead( IStrioReader aSr ) {
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

          // if( !aSr.readQuotedString().equals( CHECK_DOC ) ) {
          // LoggerUtils.errorLogger().error( LOG_ERR_READING_CONFIG );
          // }

          IListEdit<IStringList> properties = new ElemArrayList<>();

          // для совместимости с предыдущей версией
          try {
            String line = aSr.readUntilDelimiter();
            if( line.equals( "\"" + CHECK_DOC + "\"" ) ) {
              System.out.println( "For debug:  empty properties" );
            }
            else {
              try {
                // свойства
                int propsCount = Integer.parseInt( line.trim() );// aSr.readInt();
                aSr.ensureSeparatorChar();

                for( int i = 0; i < propsCount; i++ ) {
                  // one prop
                  IStringList prop = StringListKeeper.KEEPER.read( aSr );
                  aSr.ensureSeparatorChar();
                  properties.add( prop );
                }

                System.out.println( "For debug: props count = " + propsCount );

                aSr.ensureString( "\"" + CHECK_DOC + "\"" );
              }
              catch( Exception ee ) {
                // для совместимости с предыдущей версией

                System.out.println( "Error Doc Read" );
              }
            }
            // aSr.ensureString( "\"" + CHECK_DOC + "\"" );
            // aSr.readQuotedString();

          }
          catch( Exception e ) {
            System.out.println( "Error Doc Read" );
          }

          S7ToS5CfgDoc result = new S7ToS5CfgDoc( id, name, descr );
          result.dataCfgUnits = units;
          result.setL2Path( l2Path );
          result.setCfgFilesPrefix( cfgFileName );
          result.setProperties( properties );
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
  private IStringMapEdit<GuiS7DataBlock> nodesCfgs = new StringMap<>();

  /**
   * Доп свойства
   */
  private IListEdit<IStringList> properties = new ElemArrayList<>();

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
  public S7ToS5CfgDoc( String aId, String aName, String aDescription ) {
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

  public IList<IStringList> getProperties() {
    return properties;
  }

  public void setProperties( IList<IStringList> aProperties ) {
    properties = new ElemArrayList<>( aProperties );
  }

  /**
   * Returns list of configurations for all node existed in document (and may be more - configs are not deleted)
   *
   * @return IList - list of configurations for all node existed in document
   */
  public IList<GuiS7DataBlock> getNodesCfgs() {
    // ensureNodesCfgs( aContext );
    return nodesCfgs.values();
  }

  /**
   * Update map of nodes
   *
   * @param aNodesCfgs - added nodes {@link GuiS7DataBlock}
   */
  public void setNodesCfgs( IList<GuiS7DataBlock> aNodesCfgs ) {
    nodesCfgs.clear();
    for( GuiS7DataBlock cfg : aNodesCfgs ) {
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
      IList<GuiS7DataBlock> nodes = OpcUaUtils.convertToNodesList( unit.getDataNodes2() );
      ECfgUnitType type = unit.getTypeOfCfgUnit();

      for( int i = 0; i < nodes.size(); i++ ) {
        GuiS7DataBlock node = nodes.get( i );
        // FIXME
        // node.setOutput( type == ECfgUnitType.COMMAND );

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
