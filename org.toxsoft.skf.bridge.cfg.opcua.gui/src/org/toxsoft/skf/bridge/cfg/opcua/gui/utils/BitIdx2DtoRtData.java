package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.bricks.strio.IStrioHardConstants.*;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Simple container to store link bit index → IDtoRtdataInfo.
 *
 * @author dima
 */
public class BitIdx2DtoRtData {

  private final int            bitIndex;
  private final IDtoRtdataInfo rtdataInfo;

  /**
   * Value-object keeper identifier.
   */
  public static final String KEEPER_ID = "BitIdx2DtoRtData"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public final static IEntityKeeper<BitIdx2DtoRtData> KEEPER =
      new AbstractEntityKeeper<>( BitIdx2DtoRtData.class, EEncloseMode.NOT_IN_PARENTHESES, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, BitIdx2DtoRtData aEntity ) {
          // bit idx
          aSw.writeInt( aEntity.bitIndex() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // пишем IDtoRtdataInfo
          DtoRtdataInfo.KEEPER.write( aSw, aEntity.dtoRtdataInfo() );
          aSw.writeEol();
        }

        @Override
        protected BitIdx2DtoRtData doRead( IStrioReader aSr ) {
          int bitIdx = aSr.readInt();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          IDtoRtdataInfo rtData = DtoRtdataInfo.KEEPER.read( aSr );
          return new BitIdx2DtoRtData( bitIdx, rtData );
        }
      };

  /**
   * Constructor by parent nodeId and fields values.
   *
   * @param aBitIndex - index in word
   * @param aRtdataInfo - description of data
   */
  public BitIdx2DtoRtData( int aBitIndex, IDtoRtdataInfo aRtdataInfo ) {
    super();
    bitIndex = aBitIndex;
    rtdataInfo = aRtdataInfo;
  }

  /**
   * @return {@link NodeId} nodeId
   */
  public int bitIndex() {
    return bitIndex;
  }

  /**
   * @return {@link IDtoRtdataInfo } parent.browseName()::this.browseName();
   */
  public IDtoRtdataInfo dtoRtdataInfo() {
    return rtdataInfo;
  }

}
