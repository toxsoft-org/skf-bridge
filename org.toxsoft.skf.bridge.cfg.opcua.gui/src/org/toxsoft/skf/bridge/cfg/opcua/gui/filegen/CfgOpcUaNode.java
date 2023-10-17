package org.toxsoft.skf.bridge.cfg.opcua.gui.filegen;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;

/**
 * Configurable node properties for opc ua bridge (driver end)
 *
 * @author max
 */
public class CfgOpcUaNode {

  /**
   * The keeper singleton.
   */

  public static final IEntityKeeper<CfgOpcUaNode> KEEPER =
      new AbstractEntityKeeper<>( CfgOpcUaNode.class, EEncloseMode.ENCLOSES_BASE_CLASS, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, CfgOpcUaNode aEntity ) {
          aSw.incNewLine();

          // id
          aSw.writeQuotedString( aEntity.getNodeId() );
          aSw.writeSeparatorChar();
          aSw.writeEol();

          // boolean indexes
          aSw.writeBoolean( aEntity.isRead() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          aSw.writeBoolean( aEntity.isWrite() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          aSw.writeBoolean( aEntity.isSynch() );
          aSw.writeSeparatorChar();
          aSw.writeEol();

          // type
          aSw.writeQuotedString( aEntity.getType().id() );
          aSw.writeSeparatorChar();
          aSw.writeEol();

          // realization options
          OptionSetKeeper.KEEPER.write( aSw, aEntity.getExtraParams() );

          aSw.writeQuotedString( "CheckNode" );

          aSw.decNewLine();
        }

        @Override
        protected CfgOpcUaNode doRead( IStrioReader aSr ) {
          // id
          String id = aSr.readQuotedString();
          aSr.ensureSeparatorChar();

          // boolean indexes
          boolean read = aSr.readBoolean();
          aSr.ensureSeparatorChar();

          boolean write = aSr.readBoolean();
          aSr.ensureSeparatorChar();

          boolean synch = aSr.readBoolean();
          aSr.ensureSeparatorChar();

          // type
          String typeId = aSr.readQuotedString();
          EAtomicType type = EAtomicType.getById( typeId );
          aSr.ensureSeparatorChar();

          IOptionSet extraParams = OptionSetKeeper.KEEPER.read( aSr );

          if( !aSr.readQuotedString().equals( "CheckNode" ) ) {
            System.out.println( "Error Node Read" );
          }

          CfgOpcUaNode result = new CfgOpcUaNode( id, read, write, synch, type, extraParams );
          return result;
        }
      };

  /**
   * Identifier
   */
  private String nodeId;

  /**
   * Output node
   */
  private boolean write;

  /**
   * Input node
   */
  private boolean read;

  /**
   * Synch node
   */
  private boolean synch;

  /**
   * Type
   */
  private EAtomicType type;

  /**
   * Extra type
   */
  private IOptionSet extraParams;

  /**
   * Constructor
   *
   * @param aNodeId String - opc ua node id ({@link NodeId#toParseableString()}
   * @param aSynch boolean - synch index
   * @param aRead boolean - read index
   * @param aWrite boolean - write index
   * @param aType {@link EAtomicType} - type of values of node
   * @param aExtraParams IOptionSet - extra params
   */
  public CfgOpcUaNode( String aNodeId, boolean aSynch, boolean aRead, boolean aWrite, EAtomicType aType,
      IOptionSet aExtraParams ) {

    nodeId = aNodeId;
    synch = aSynch;
    read = aRead;
    write = aWrite;
    type = aType;
    extraParams = aExtraParams;
  }

  /**
   * Constructor
   *
   * @param aNodeId String - opc ua node id ({@link NodeId#toParseableString()}
   * @param aSynch boolean - synch index
   * @param aRead boolean - read index
   * @param aWrite boolean - write index
   * @param aType {@link EAtomicType} - type of values of node
   */
  public CfgOpcUaNode( String aNodeId, boolean aSynch, boolean aRead, boolean aWrite, EAtomicType aType ) {
    this( aNodeId, aSynch, aRead, aWrite, aType, new OptionSet() );
  }

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId( String aNodeId ) {
    nodeId = aNodeId;
  }

  public boolean isWrite() {
    return write;
  }

  public void setWrite( boolean aWrite ) {
    write = aWrite;
  }

  public boolean isRead() {
    return read;
  }

  public void setRead( boolean aRead ) {
    read = aRead;
  }

  public boolean isSynch() {
    return synch;
  }

  public void setSynch( boolean aSynch ) {
    synch = aSynch;
  }

  public EAtomicType getType() {
    return type;
  }

  public void setType( EAtomicType aType ) {
    type = aType;
  }

  public IOptionSet getExtraParams() {
    return extraParams;
  }

  public void setExtraParams( IOptionSet aExtraParams ) {
    extraParams = aExtraParams;
  }

}
