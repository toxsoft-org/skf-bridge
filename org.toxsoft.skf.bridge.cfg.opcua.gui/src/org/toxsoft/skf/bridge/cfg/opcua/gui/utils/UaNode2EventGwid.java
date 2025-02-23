package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.bricks.strio.IStrioHardConstants.*;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.keeper.std.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.gw.gwid.*;

/**
 * Simple container to store link UaNode → Gwid of Event with event param ids.
 *
 * @author dima
 */
public class UaNode2EventGwid
    extends UaNode2Gwid {

  /**
   * list of event param ids
   */
  private IStringList paramIds;

  /**
   * Value-object keeper identifier.
   */
  public static final String KEEPER_ID = "UaNode2EventGwid"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public final static IEntityKeeper<UaNode2EventGwid> KEEPER =
      new AbstractEntityKeeper<>( UaNode2EventGwid.class, EEncloseMode.NOT_IN_PARENTHESES, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, UaNode2EventGwid aEntity ) {
          // пишем UaNode2Gwid
          UaNode2Gwid.KEEPER.write( aSw, aEntity );
          // дописываем param ids
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          StringListKeeper.KEEPER.write( aSw, aEntity.paramIds );
          aSw.writeEol();
        }

        @Override
        protected UaNode2EventGwid doRead( IStrioReader aSr ) {
          UaNode2Gwid uaNode2Gwid = UaNode2Gwid.KEEPER.read( aSr );
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          IStringList paramIds = StringListKeeper.KEEPER.read( aSr );
          return new UaNode2EventGwid( uaNode2Gwid.getNodeId().toParseableString(), uaNode2Gwid.nodeDescr(),
              uaNode2Gwid.gwid(), paramIds );
        }
      };

  /**
   * Constructor by parent nodeId and fields values.
   *
   * @param aNodeId - node id
   * @param aNodeDescr - description parent.browseName()::this.browseName()
   * @param aGwid - rtData Gwid {@link Gwid}
   * @param aParamIds - event param ids {@link IStringList}
   */
  public UaNode2EventGwid( String aNodeId, String aNodeDescr, Gwid aGwid, IStringList aParamIds ) {
    super( aNodeId, aNodeDescr, aGwid );
    paramIds = aParamIds;
  }

  /**
   * @return list of event param ids
   */
  public IStringList paramIds() {
    return paramIds;
  }
}
