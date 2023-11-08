package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.uskat.core.api.sysdescr.dto.*;

/**
 * Simple container to store link bit index → IDtoEventInfo.
 *
 * @author dima
 */
public class BitIdx2DtoEvent {

  private final String        strid;
  private final int           bitIndex;
  private final IDtoEventInfo eventInfo;

  private final boolean generateUp;
  private final boolean generateDn;

  /**
   * Constructor by parent nodeId and fields values.
   *
   * @param aStrid - string id of bit array word
   * @param aBitIndex - index in word
   * @param aEventInfo - description of event
   * @param aGenUp - generate when 0->1
   * @param aGenDn - generate when 1->0
   */
  public BitIdx2DtoEvent( String aStrid, int aBitIndex, IDtoEventInfo aEventInfo, boolean aGenUp, boolean aGenDn ) {
    super();
    strid = aStrid;
    bitIndex = aBitIndex;
    eventInfo = aEventInfo;
    generateUp = aGenUp;
    generateDn = aGenDn;
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
   * @return {@link IDtoEventInfo }
   */
  public IDtoEventInfo dtoEventInfo() {
    return eventInfo;
  }

  /**
   * @return generate when 0->1
   */
  public boolean isGenerateUp() {
    return generateUp;
  }

  /**
   * @return generate when 1->0
   */
  public boolean isGenerateDn() {
    return generateDn;
  }

}
