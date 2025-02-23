package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.bricks.strio.IStrioHardConstants.*;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.gw.skid.*;

/**
 * Simple container to store link UaNode → Skid.
 *
 * @author dima
 */
public class UaNode2Skid
    implements IContainNodeId {

  private final String nodeId;    // uaNode.getNodeId().toParseableString()
  private final String nodeDescr; // parent.browseName()::this.browseName();
  private final Skid   skid;      // пример работы с кипером VtGraphParam::IEntityKeeper

  /**
   * Value-object keeper identifier.
   */
  public static final String KEEPER_ID = "UaNode2Gwid"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public final static IEntityKeeper<UaNode2Skid> KEEPER =
      new AbstractEntityKeeper<>( UaNode2Skid.class, EEncloseMode.NOT_IN_PARENTHESES, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, UaNode2Skid aEntity ) {
          // nodeDescr
          aSw.writeQuotedString( aEntity.nodeDescr() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // пишем NodeId
          aSw.writeQuotedString( aEntity.getNodeId().toParseableString() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // пишем Skid
          Skid.KEEPER.write( aSw, aEntity.skid() );
          aSw.writeEol();
        }

        @Override
        protected UaNode2Skid doRead( IStrioReader aSr ) {
          String nodeDescr = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String nodeId = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          Skid skid = Skid.KEEPER.read( aSr );
          return new UaNode2Skid( nodeId, nodeDescr, skid );
        }
      };

  /**
   * Constructor by parent nodeId and fields values.
   *
   * @param aNodeId - node id
   * @param aNodeDescr - description parent.browseName()::this.browseName()
   * @param aSkid - object {@link Skid }
   */
  public UaNode2Skid( String aNodeId, String aNodeDescr, Skid aSkid ) {
    super();
    nodeId = aNodeId;
    nodeDescr = aNodeDescr;
    skid = aSkid;
  }

  /**
   * @return {@link NodeId} nodeId
   */
  @Override
  public NodeId getNodeId() {
    return NodeId.parse( nodeId );
  }

  /**
   * @return {@link String } parent.browseName()::this.browseName();
   */
  public String nodeDescr() {
    return nodeDescr;
  }

  /**
   * @return {@link Skid } Skid;
   */
  public Skid skid() {
    return skid;
  }

}
