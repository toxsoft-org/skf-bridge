package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
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
 * Модельный класс конфигурируемой связи opc ua на данные s5. \\ Типичная единица конфигурации - мапирование один узел
 * на одно данное \\
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

          // Nodes
          // nodes count
          aSw.writeInt( aEntity.dataNodes.size() );
          aSw.writeSeparatorChar();
          aSw.writeEol();

          for( int i = 0; i < aEntity.dataNodes.size(); i++ ) {
            // one node
            aSw.writeQuotedString( aEntity.dataNodes.get( i ).toParseableString() );
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

          // Nodes
          // nodes count
          int nodesCount = aSr.readInt();
          aSr.ensureSeparatorChar();

          IListEdit<NodeId> nodes = new ElemArrayList<>();
          for( int i = 0; i < nodesCount; i++ ) {
            // one node
            String nodeString = aSr.readQuotedString();
            aSr.ensureSeparatorChar();

            NodeId nodeId = NodeId.parse( nodeString );
            nodes.add( nodeId );
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
          result.setDataNodes( nodes );
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
  private IList<NodeId> dataNodes = new ElemArrayList<>();

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

  protected void setDataGwids( IList<Gwid> aGwids ) {
    dataGwids = aGwids;
  }

  public IList<NodeId> getDataNodes() {
    return dataNodes;
  }

  protected void setDataNodes( IList<NodeId> aNodes ) {
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
