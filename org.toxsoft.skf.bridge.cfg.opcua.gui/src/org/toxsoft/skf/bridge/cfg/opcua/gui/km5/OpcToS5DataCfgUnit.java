package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.list.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.types.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;

/**
 * Модельный класс конфигурируемой связи opc ua на данные s5.<br>
 * Типичная единица конфигурации - мапирование один узел на одно данное
 *
 * @author max
 */
public class OpcToS5DataCfgUnit
    extends Stridable {

  private static final String CHECK_UNIT = "CheckUnit"; //$NON-NLS-1$

  /**
   * The keeper singleton.
   */

  public static final IEntityKeeper<OpcToS5DataCfgUnit> KEEPER =
      new AbstractEntityKeeper<>( OpcToS5DataCfgUnit.class, EEncloseMode.ENCLOSES_BASE_CLASS, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, OpcToS5DataCfgUnit aEntity ) {
          aSw.incNewLine();

          // id, name
          aSw.writeAsIs( aEntity.id() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          aSw.writeQuotedString( aEntity.nmName() );
          aSw.writeSeparatorChar();
          aSw.writeEol();

          // type
          aSw.writeAsIs( aEntity.typeOfCfgUnit.name() );
          aSw.writeSeparatorChar();
          aSw.writeEol();

          // Gwids
          // gwids count
          aSw.writeInt( aEntity.dataGwids.size() );
          aSw.writeSeparatorChar();
          aSw.writeEol();

          for( int i = 0; i < aEntity.dataGwids.size(); i++ ) {
            // one gwid
            Gwid.KEEPER.write( aSw, aEntity.dataGwids.get( i ) );
            aSw.writeSeparatorChar();
            aSw.writeEol();
          }

          aSw.writeInt( aEntity.dataNodes.size() );
          aSw.writeSeparatorChar();
          aSw.writeEol();

          for( int i = 0; i < aEntity.dataNodes.size(); i++ ) {
            // one node
            AtomicValueKeeper.KEEPER.write( aSw, aEntity.dataNodes.get( i ) );
            aSw.writeSeparatorChar();
            aSw.writeEol();
          }

          // realization type id
          aSw.writeAsIs( aEntity.getRelizationTypeId() );
          aSw.writeSeparatorChar();
          aSw.writeEol();

          // realization options
          OptionSetKeeper.KEEPER.write( aSw, aEntity.getRealizationOpts() );

          aSw.writeQuotedString( CHECK_UNIT );

          aSw.decNewLine();
        }

        @Override
        protected OpcToS5DataCfgUnit doRead( IStrioReader aSr ) {
          // id, name
          String id = aSr.readIdPath();
          aSr.ensureSeparatorChar();
          String name = aSr.readQuotedString();
          aSr.ensureSeparatorChar();

          // type
          String typeName = aSr.readIdName();
          ECfgUnitType type = ECfgUnitType.searchByEnumName( typeName, ECfgUnitType.DATA );
          aSr.ensureSeparatorChar();

          // Gwids
          // gwids count
          int gwidsCount = aSr.readInt();
          aSr.ensureSeparatorChar();

          IListEdit<Gwid> gwids = new ElemArrayList<>();
          for( int i = 0; i < gwidsCount; i++ ) {
            // one gwid
            Gwid gwid = Gwid.KEEPER.read( aSr );
            aSr.ensureSeparatorChar();
            gwids.add( gwid );
          }

          // nodes count
          int nodesCount = aSr.readInt();
          aSr.ensureSeparatorChar();

          IAvListEdit nodes = new AvList( new ElemArrayList<>() );
          for( int i = 0; i < nodesCount; i++ ) {
            // one node
            nodes.add( AtomicValueKeeper.KEEPER.read( aSr ) );
            aSr.ensureSeparatorChar();

          }

          String relizationTypeId = aSr.readIdPath();
          aSr.ensureSeparatorChar();

          IOptionSet realizationOpts = OptionSetKeeper.KEEPER.read( aSr );

          if( !aSr.readQuotedString().equals( CHECK_UNIT ) ) {
            System.out.println( "Error Unit Read" );
          }

          OpcToS5DataCfgUnit result = new OpcToS5DataCfgUnit( id, name );
          result.typeOfCfgUnit = type;
          result.setDataGwids( gwids );
          result.setDataNodes2( nodes );
          result.setRelizationTypeId( relizationTypeId );
          result.setRealizationOpts( realizationOpts );
          return result;
        }
      };

  /**
   * Constructor by id and name
   *
   * @param aId String - id
   * @param aName String - name
   */
  public OpcToS5DataCfgUnit( String aId, String aName ) {
    super( aId, aName, aName );

    // realizationOpts.setStr( "param.str", "string" );
    // realizationOpts.setInt( "param.int", 5 );
  }

  /**
   * Constructor by id and name
   *
   * @param aId String - id
   * @param aName String - name
   * @param aGwids IList - список данных сервера S5, используемых в этой единице конфигурации
   * @param aNodes IList - список узлов opc ua, используемых в этой единице конфигурации
   */
  public OpcToS5DataCfgUnit( String aId, String aName, IList<Gwid> aGwids, IAvList aNodes ) {
    super( aId, aName, aName );
    dataGwids = aGwids;
    dataNodes = aNodes;
  }

  /**
   * Тип конфигурационной единицы
   */
  private ECfgUnitType typeOfCfgUnit = ECfgUnitType.DATA;

  /**
   * Список данных сервера S5, используемых в этой единице конфигурации
   */
  private IList<Gwid> dataGwids = new ElemArrayList<>();

  /**
   * Список узлов opc ua, используемых в этой единице конфигурации
   */
  private IAvList dataNodes = new AvList( new ElemArrayList<>() );

  /**
   * Ид Типа реализации
   */
  private String relizationTypeId = OpcUaUtils.CFG_UNIT_REALIZATION_TYPE_ONT_TO_ONE_DATA;

  /**
   * Параметры реализации
   */
  private IOptionSetEdit realizationOpts = new OptionSet();

  @Override
  public void setName( String aName ) {
    super.setName( aName );

  }

  public ECfgUnitType getTypeOfCfgUnit() {
    return typeOfCfgUnit;
  }

  public void setTypeOfCfgUnit( ECfgUnitType aType ) {
    typeOfCfgUnit = aType;
  }

  public IList<Gwid> getDataGwids() {
    return dataGwids;
  }

  public void setDataGwids( IList<Gwid> aGwids ) {
    dataGwids = aGwids;
  }

  public IAvList getDataNodes2() {
    return dataNodes;
  }

  public void setDataNodes2( IAvList aNodes ) {
    dataNodes = aNodes;
  }

  public IOptionSet getRealizationOpts() {
    return realizationOpts;
  }

  public void setRealizationOpts( IOptionSet aRealizationOpts ) {
    realizationOpts = new OptionSet( aRealizationOpts );
  }

  public String getRelizationTypeId() {
    return relizationTypeId;
  }

  public void setRelizationTypeId( String aRelizationTypeId ) {
    relizationTypeId = aRelizationTypeId;
  }

}
