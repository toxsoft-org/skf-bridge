package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.uskat.core.api.sysdescr.dto.*;

/**
 * Simple container to store link bit index → IDtoRtdataInfo. TODO отрефакторить с колассом BitIdx2DtoEvent
 *
 * @author dima
 */
public class BitIdx2DtoRtData {

  private final String         strid;
  private final int            bitIndex;
  private final IDtoRtdataInfo rtdataInfo;

  /**
   * Constructor by parent nodeId and fields values.
   *
   * @param aStrid - string id of bit array word
   * @param aBitIndex - index in word
   * @param aRtdataInfo - description of data
   */
  public BitIdx2DtoRtData( String aStrid, int aBitIndex, IDtoRtdataInfo aRtdataInfo ) {
    super();
    strid = aStrid;
    bitIndex = aBitIndex;
    rtdataInfo = aRtdataInfo;
  }

  /**
   * @return string id of bit array word
   */
  public String bitArrayWordStrid() {
    return strid;
  }

  /**
   * @return index of bit in bit array word
   */
  public int bitIndex() {
    return bitIndex;
  }

  /**
   * @return {@link IDtoRtdataInfo }
   */
  public IDtoRtdataInfo dtoRtdataInfo() {
    return rtdataInfo;
  }

}
