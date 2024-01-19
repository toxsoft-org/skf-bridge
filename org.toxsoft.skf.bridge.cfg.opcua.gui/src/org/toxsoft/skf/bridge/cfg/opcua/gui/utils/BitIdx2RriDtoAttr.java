package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.uskat.core.api.sysdescr.dto.*;

/**
 * Simple container to store link bit index → IDtoAttrInfo. TODO отрефакторить с классом BitIdx2DtoEvent
 *
 * @author dima
 */
public class BitIdx2RriDtoAttr {

  private final String       strid;
  private final int          bitIndex;
  private final IDtoAttrInfo attrInfo;

  /**
   * Constructor by parent nodeId and fields values.
   *
   * @param aStrid - string id of bit array word
   * @param aBitIndex - index in word
   * @param aAttrInfo - description of attribute
   */
  public BitIdx2RriDtoAttr( String aStrid, int aBitIndex, IDtoAttrInfo aAttrInfo ) {
    super();
    strid = aStrid;
    bitIndex = aBitIndex;
    attrInfo = aAttrInfo;
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
   * @return {@link IDtoAttrInfo }
   */
  public IDtoAttrInfo dtoAttrInfo() {
    return attrInfo;
  }

}
