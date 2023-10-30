package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;

/**
 * Simple container to store link bit index → IDtoEventInfo.
 *
 * @author dima
 */
public class BitIdx2DtoEvent {

  private final int           bitIndex;
  private final IDtoEventInfo eventInfo;
  private final boolean       generateUp;

  private final boolean generateDn;

  /**
   * Constructor by parent nodeId and fields values.
   *
   * @param aBitIndex - index in word
   * @param aEventInfo - description of event
   * @param aGenUp - generate when 0->1
   * @param aGenDn - generate when 1->0
   */
  public BitIdx2DtoEvent( int aBitIndex, IDtoEventInfo aEventInfo, boolean aGenUp, boolean aGenDn ) {
    super();
    bitIndex = aBitIndex;
    eventInfo = aEventInfo;
    generateUp = aGenUp;
    generateDn = aGenDn;
  }

  /**
   * @return {@link NodeId} nodeId
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
