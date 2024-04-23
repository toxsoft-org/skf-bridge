package org.toxsoft.skf.bridge.cfg.modbus.gui.utils;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.skf.bridge.cfg.modbus.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;

public class ModbusToS5CfgDocService
    extends L2ToS5CfgDocService<ModbusToS5CfgDoc> {

  private final static String SECT_ID_CFG_DOCS = "modbus.bridge.cfg.doc";

  public ModbusToS5CfgDocService( ITsGuiContext aContext ) {
    super( aContext, SECT_ID_CFG_DOCS, ModbusToS5CfgDoc.KEEPER );

  }

}
