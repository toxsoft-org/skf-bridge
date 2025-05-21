package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

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
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;

/**
 * A whole configuration of opc ua <-> s5 bridge, contains several devisions (data, commands, events) each contains
 * units of configuration A unit - is a simple peace of configuration (for example one node to one gwid data
 * transmitter). A {@link OpcToS5DataCfgDoc} corresponds to one cfg file (.dlmcfg) and is source for auto generating of
 * this file.
 *
 * @author max
 */
public class OpcToS5DataCfgDoc
    extends Stridable {

  private static final String CHECK_DOC = "CheckDoc"; //$NON-NLS-1$

  /**
   * The keeper singleton.
   */
  public static final IEntityKeeper<OpcToS5DataCfgDoc> KEEPER =
      new AbstractEntityKeeper<>( OpcToS5DataCfgDoc.class, EEncloseMode.ENCLOSES_BASE_CLASS, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, OpcToS5DataCfgDoc aEntity ) {
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
          aSw.writeQuotedString( aEntity.getEndPointURL() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          aSw.writeQuotedString( aEntity.getUserOPC_UA() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          aSw.writeQuotedString( aEntity.getPasswordOPC_UA() );
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

          IList<CfgOpcUaNode> nodes = aEntity.getNodesCfgs();

          // Nodes
          // nodes count
          aSw.writeInt( nodes.size() );
          aSw.writeSeparatorChar();
          aSw.writeEol();

          for( int i = 0; i < nodes.size(); i++ ) {
            // one node
            CfgOpcUaNode.KEEPER.write( aSw, nodes.get( i ) );
            aSw.writeSeparatorChar();
            aSw.writeEol();
          }

          IStringList groupIds = aEntity.getGroupIds();
          IList<IStringList> properties = aEntity.getProperties();

          // Groups
          if( groupIds.size() > 0 || properties.size() > 0 ) {

            // groups count
            aSw.writeInt( groupIds.size() );
            aSw.writeSeparatorChar();
            aSw.writeEol();

            for( int i = 0; i < groupIds.size(); i++ ) {
              // one group
              aSw.writeQuotedString( groupIds.get( i ) );
              aSw.writeSeparatorChar();
              aSw.writeEol();
            }

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
        protected OpcToS5DataCfgDoc doRead( IStrioReader aSr ) {
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
          String url = aSr.readQuotedString();
          aSr.ensureSeparatorChar();
          String userOPC_UA = aSr.readQuotedString();
          aSr.ensureSeparatorChar();
          String passwordOPC_UA = aSr.readQuotedString();
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

          // Nodes
          // nodes count
          int nodesCount = aSr.readInt();
          aSr.ensureSeparatorChar();

          IStringMapEdit<CfgOpcUaNode> nodes = new StringMap<>();
          for( int i = 0; i < nodesCount; i++ ) {
            // one node
            CfgOpcUaNode node = CfgOpcUaNode.KEEPER.read( aSr );
            aSr.ensureSeparatorChar();
            nodes.put( node.getNodeId(), node );
          }

          System.out.println( "Loaded nodes count = " + nodes.size() );

          IStringListEdit groupFilters = new StringArrayList();
          IListEdit<IStringList> properties = new ElemArrayList<>();

          // для совместимости с предыдущей версией
          try {
            String line = aSr.readUntilDelimiter();
            if( line.equals( "\"" + CHECK_DOC + "\"" ) ) {
              System.out.println( "For debug:  empty groups and properties" );
            }
            else {
              try {
                // группы
                int groupsCount = Integer.parseInt( line.trim() );// aSr.readInt();
                aSr.ensureSeparatorChar();

                for( int i = 0; i < groupsCount; i++ ) {
                  // one group filter
                  String groupFilter = aSr.readQuotedString();
                  aSr.ensureSeparatorChar();
                  groupFilters.add( groupFilter );
                }

                System.out.println( "For debug:  groups count = " + groupsCount );

                // свойства
                int propsCount = aSr.readInt();
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

          OpcToS5DataCfgDoc result = new OpcToS5DataCfgDoc( id, name, descr );
          result.dataCfgUnits = units;
          result.nodesCfgs = nodes;
          result.setL2Path( l2Path );
          result.setCfgFilesPrefix( cfgFileName );
          result.setEndPointURL( url );
          result.setUserOPC_UA( userOPC_UA );
          result.setPasswordOPC_UA( passwordOPC_UA );
          result.setGroupIds( groupFilters );
          result.setProperties( properties );
          return result;
        }
      };

  /**
   * Path to l2 bridge
   */
  private File l2Path;

  /**
   * Prefix of cfg files ("prefix".dlmcfg, "prefix".devcfg)
   */
  private String cfgFilesPrefix;

  /**
   * IP address and port
   */
  private String endPointURL;

  /**
   * OPC UA user
   */
  private String userOPC_UA;

  /**
   * OPC UA password
   */
  private String passwordOPC_UA;

  /**
   * List of data cfg units.
   */
  private IListEdit<OpcToS5DataCfgUnit> dataCfgUnits = new ElemArrayList<>();

  /**
   * List of nodes cfgs.
   */
  private IStringMapEdit<CfgOpcUaNode> nodesCfgs = new StringMap<>();

  /**
   * Идентификаторы групп, разделяющих выходные файлы на группы
   */
  private IStringList groupIds = new StringArrayList();

  /**
   * Доп свойства
   */
  private IListEdit<IStringList> properties = new ElemArrayList<>();

  /**
   * Constructor by id, name and description/
   *
   * @param aId String - id
   * @param aName String - name
   * @param aDescription String - description
   */
  public OpcToS5DataCfgDoc( String aId, String aName, String aDescription ) {
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

  public IList<OpcToS5DataCfgUnit> dataUnits() {
    return dataCfgUnits;
  }

  public void addDataUnit( OpcToS5DataCfgUnit aDataUnit ) {
    dataCfgUnits.add( aDataUnit );
  }

  public void addDataUnits( IList<OpcToS5DataCfgUnit> aDataUnits ) {
    dataCfgUnits.addAll( aDataUnits );
  }

  public void removeDataUnit( OpcToS5DataCfgUnit aDataUnit ) {
    dataCfgUnits.remove( aDataUnit );
  }

  public IList<IStringList> getProperties() {
    return properties;
  }

  public void setProperties( IList<IStringList> aProperties ) {
    properties = new ElemArrayList<>( aProperties );
  }

  public IStringList getGroupIds() {
    return groupIds;
  }

  public void setGroupIds( IStringList aGroupIds ) {
    groupIds = aGroupIds;
  }

  /**
   * Returns list of configurations for all node existed in document (and may be more - configs are not deleted)
   *
   * @return IList - list of configurations for all node existed in document
   */
  public IList<CfgOpcUaNode> getNodesCfgs() {
    // ensureNodesCfgs( aContext );
    return nodesCfgs.values();
  }

  public void setNodesCfgs( IList<CfgOpcUaNode> aNodesCfgs ) {
    nodesCfgs.clear();
    for( CfgOpcUaNode cfg : aNodesCfgs ) {
      nodesCfgs.put( cfg.getNodeId(), cfg );
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

  /**
   * @return OPC UA server end point URL
   */
  public String getEndPointURL() {
    return endPointURL;
  }

  /**
   * @param aEndPointURL - OPC UA server end point URL
   */
  public void setEndPointURL( String aEndPointURL ) {
    endPointURL = aEndPointURL;
  }

  /**
   * @return OPC UA user
   */
  public String getUserOPC_UA() {
    return userOPC_UA;
  }

  /**
   * @param aUserOPC_UA - OPC UA user
   */
  public void setUserOPC_UA( String aUserOPC_UA ) {
    userOPC_UA = aUserOPC_UA;
  }

  /**
   * @return OPC UA password
   */
  public String getPasswordOPC_UA() {
    return passwordOPC_UA;
  }

  /**
   * @param aPasswordOPC_UA - OPC UA password
   */
  public void setPasswordOPC_UA( String aPasswordOPC_UA ) {
    passwordOPC_UA = aPasswordOPC_UA;
  }

}
