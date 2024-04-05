package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;

public class OpcToS5DataCfgDocService
    extends L2ToS5CfgDocService<OpcToS5DataCfgDoc> {

  private final static String SECT_ID_CFG_DOCS = "opc.bridge.cfg.doc";

  public OpcToS5DataCfgDocService( ITsGuiContext aContext ) {
    super( aContext, SECT_ID_CFG_DOCS, OpcToS5DataCfgDoc.KEEPER );

  }

}
