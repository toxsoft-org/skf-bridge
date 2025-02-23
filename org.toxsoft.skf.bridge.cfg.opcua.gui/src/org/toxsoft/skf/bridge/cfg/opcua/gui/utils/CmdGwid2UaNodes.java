package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.bricks.strio.IStrioHardConstants.*;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * Simple container to store link command Gwid → three UaNode.
 *
 * @author dima
 */
public class CmdGwid2UaNodes {

  private final Gwid        cmdGwid;       // command Gwid
  private final String      niCmdId;       // node for set command code (id)
  private final String      niCmdArgInt;   // node for set command arg of integer type
  private final String      niCmdArgFlt;   // node for set command arg of float type
  private final String      niCmdFeedback; // node for read command feedback
  private final String      nodeDescr;     // parent.browseName()
  private final EAtomicType argType;       // type of argument

  /**
   * Value-object keeper identifier.
   */
  public static final String KEEPER_ID = "CmdGwid2UaNodes"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public final static IEntityKeeper<CmdGwid2UaNodes> KEEPER =
      new AbstractEntityKeeper<>( CmdGwid2UaNodes.class, EEncloseMode.NOT_IN_PARENTHESES, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, CmdGwid2UaNodes aEntity ) {
          // пишем Gwid
          Gwid.KEEPER.write( aSw, aEntity.gwid() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // nodeDescr
          aSw.writeQuotedString( aEntity.nodeDescr() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // пишем niCmdId
          aSw.writeQuotedString( aEntity.getNodeCmdId().toParseableString() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // пишем niCmdArgInt
          aSw.writeQuotedString( aEntity.getNodeCmdArgInt() == null ? TsLibUtils.EMPTY_STRING
              : aEntity.getNodeCmdArgInt().toParseableString() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // пишем niCmdArgFlt
          aSw.writeQuotedString( aEntity.getNodeCmdArgFlt() == null ? TsLibUtils.EMPTY_STRING
              : aEntity.getNodeCmdArgFlt().toParseableString() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // пишем niCmdFeedback
          aSw.writeQuotedString( aEntity.getNodeCmdFeedback().toParseableString() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // пишем argType
          EAtomicType.KEEPER.write( aSw, aEntity.argType() );
          aSw.writeEol();
        }

        @Override
        protected CmdGwid2UaNodes doRead( IStrioReader aSr ) {
          Gwid gwid = Gwid.KEEPER.read( aSr );
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String nodeDescr = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String niCmdId = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String niCmdArgInt = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String niCmdArgFlt = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String niCmdFeedback = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          EAtomicType argType = EAtomicType.KEEPER.read( aSr );
          return new CmdGwid2UaNodes( gwid, nodeDescr, niCmdId, niCmdArgInt.isBlank() ? null : niCmdArgInt,
              niCmdArgFlt.isBlank() ? null : niCmdArgFlt, niCmdFeedback, argType );
        }
      };

  /**
   * Constructor by parent nodeId and fields values.
   *
   * @param aCmdGwid - command Gwid
   * @param aNodeDescr - description parent.browseName()::this.browseName()
   * @param aNiCmdId - node for set command code (id)
   * @param aNiCmdArgInt - node for set command arg of integer type
   * @param aNiCmdArgFlt - node for set command arg of float type
   * @param aNiCmdFeedback - node for read command feedback
   * @param aArgType - type of argument
   */
  public CmdGwid2UaNodes( Gwid aCmdGwid, String aNodeDescr, String aNiCmdId, String aNiCmdArgInt, String aNiCmdArgFlt,
      String aNiCmdFeedback, EAtomicType aArgType ) {
    super();
    TsNullArgumentRtException.checkNull( aNiCmdId );
    TsNullArgumentRtException.checkNull( aNiCmdFeedback );
    cmdGwid = aCmdGwid;
    nodeDescr = aNodeDescr;
    niCmdId = aNiCmdId;
    niCmdArgInt = aNiCmdArgInt;
    niCmdArgFlt = aNiCmdArgFlt;
    niCmdFeedback = aNiCmdFeedback;
    argType = aArgType;
  }

  /**
   * @return {@link EAtomicType} type of argument
   */
  public EAtomicType argType() {
    return argType;
  }

  /**
   * @return {@link NodeId} niCmdFeedback
   */
  public NodeId getNodeCmdFeedback() {
    return NodeId.parse( niCmdFeedback );
  }

  /**
   * @return {@link NodeId} niCmdArgFlt
   */
  public NodeId getNodeCmdArgFlt() {
    return niCmdArgFlt == null ? null : NodeId.parse( niCmdArgFlt );
  }

  /**
   * @return {@link NodeId} niCmdArgInt
   */
  public NodeId getNodeCmdArgInt() {
    return niCmdArgInt == null ? null : NodeId.parse( niCmdArgInt );
  }

  /**
   * @return {@link NodeId} niCmdId
   */
  public NodeId getNodeCmdId() {
    return NodeId.parse( niCmdId );
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
    return cmdGwid;
  }

}
