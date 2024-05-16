package org.toxsoft.skf.bridge.cfg.modbus.gui.km5;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
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

/**
 * A hole configuration of modbus <-> s5 bridge, contains several devisions (data, commands, events) each contains units
 * of configuration A unit - is a simple peace of configuration (for example one node to one gwid data transmitter). A
 * {@link ModbusToS5CfgDoc} corresponds to cfg files (.dlmcfg and .devcfg) and is source for auto generating of this
 * files.
 *
 * @author max
 */
public class ModbusToS5CfgDoc
    extends Stridable {

  private static final String CHECK_DOC = "CheckDoc"; //$NON-NLS-1$

  private static String MAP_KEY_FORMAT_STR = "%s:%d %s_%d"; //$NON-NLS-1$

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

          // IList<CfgOpcUaNode> nodes =
          // aEntity.getNodesCfgs();

          // Nodes
          // nodes count
          // aSw.writeInt( nodes.size() );
          // aSw.writeSeparatorChar();
          // aSw.writeEol();
          //
          // for( int i = 0; i < nodes.size();
          // i++ ) {
          // // one node
          // CfgOpcUaNode.KEEPER.write( aSw,
          // nodes.get( i ) );
          // aSw.writeSeparatorChar();
          // aSw.writeEol();
          // }

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
          // int nodesCount = aSr.readInt();
          // aSr.ensureSeparatorChar();
          //
          // IStringMapEdit<CfgOpcUaNode> nodes =
          // new StringMap<>();
          // for( int i = 0; i < nodesCount; i++
          // ) {
          // // one node
          // CfgOpcUaNode node =
          // CfgOpcUaNode.KEEPER.read( aSr );
          // aSr.ensureSeparatorChar();
          // nodes.put( node.getNodeId(), node );
          // }
          //
          // System.out.println( "Loaded nodes
          // count = " + nodes.size() );

          if( !aSr.readQuotedString().equals( CHECK_DOC ) ) {
            System.out.println( "Error Doc Read" );
          }

          ModbusToS5CfgDoc result = new ModbusToS5CfgDoc( id, name, descr );
          result.dataCfgUnits = units;
          // result.nodesCfgs = nodes;
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
      String mapKey = String.format( MAP_KEY_FORMAT_STR, cfg.getAddress().getIP().getHostAddress(),
          Integer.valueOf( cfg.getAddress().getPort() ), cfg.getRequestType().name(),
          Integer.valueOf( cfg.getRegister() ) );
      nodesCfgs.put( mapKey, cfg );
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
}
