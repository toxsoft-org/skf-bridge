package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * A hole configuration of opc ua <-> s5 bridge, contains several devisions (data, commands, events) each contains units
 * of configuration A unit - is a simple peace of configuration (for example one node to one gwid data transmitter). A
 * {@link OpcToS5DataCfgDoc} corresponds to one cfg file (.dlmcfg) and is source for auto generating of this file.
 *
 * @author max
 */
public class OpcToS5DataCfgDoc
    extends Stridable {

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

          aSw.writeQuotedString( "CheckDoc" );
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

          if( !aSr.readQuotedString().equals( "CheckDoc" ) ) {
            System.out.println( "Error Doc Read" );
          }

          OpcToS5DataCfgDoc result = new OpcToS5DataCfgDoc( id, name, descr );
          result.dataCfgUnits = units;
          return result;
        }
      };

  /**
   * List of data cfg units.
   */
  private IListEdit<OpcToS5DataCfgUnit> dataCfgUnits = new ElemArrayList<>();

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

  public void removeDataUnit( OpcToS5DataCfgUnit aDataUnit ) {
    dataCfgUnits.remove( aDataUnit );
  }
}
