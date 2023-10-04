package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.bricks.strio.IStrioHardConstants.*;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.gw.gwid.*;

/**
 * Simple container to store link UaNode → Gwid.
 *
 * @author dima
 */
public class UaNode2RtdGwid {

  private final String nodeId;    // uaNode.getNodeId().toParseableString()
  private final String nodeDescr; // parent.browseName()::this.browseName();
  private final Gwid   rtdGwid;   // пример работы с кипером VtGraphParam::IEntityKeeper

  /**
   * Value-object keeper identifier.
   */
  public static final String KEEPER_ID = "UaNode2rtdGwid"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public final static IEntityKeeper<UaNode2RtdGwid> KEEPER =
      new AbstractEntityKeeper<>( UaNode2RtdGwid.class, EEncloseMode.NOT_IN_PARENTHESES, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, UaNode2RtdGwid aEntity ) {
          // nodeDescr
          aSw.writeQuotedString( aEntity.nodeDescr() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // пишем NodeId
          aSw.writeQuotedString( aEntity.getNodeId().toParseableString() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // пишем Gwid
          Gwid.KEEPER.write( aSw, aEntity.gwid() );
          aSw.writeEol();
        }

        @Override
        protected UaNode2RtdGwid doRead( IStrioReader aSr ) {
          String nodeDescr = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String nodeId = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          Gwid gwid = Gwid.KEEPER.read( aSr );
          return new UaNode2RtdGwid( nodeId, nodeDescr, gwid );
        }
      };

  /**
   * Constructor by parent nodeId and fields values.
   *
   * @param aNodeId - node id
   * @param aNodeDescr - description parent.browseName()::this.browseName()
   * @param aRtdGwid - rtData Gwid
   */
  public UaNode2RtdGwid( String aNodeId, String aNodeDescr, Gwid aRtdGwid ) {
    super();
    nodeId = aNodeId;
    nodeDescr = aNodeDescr;
    rtdGwid = aRtdGwid;
  }

  /**
   * @return {@link NodeId} nodeId
   */
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
   * @return {@link Gwid } rtData Gwid;
   */
  public Gwid gwid() {
    return rtdGwid;
  }

}
