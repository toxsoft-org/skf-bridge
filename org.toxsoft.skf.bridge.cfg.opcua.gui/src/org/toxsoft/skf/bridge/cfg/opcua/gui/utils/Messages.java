package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import org.eclipse.osgi.util.*;

public class Messages
    extends NLS {

  private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
  public static String        STR_BIT_FROM_WORD;
  public static String        STR_BIT_TAG_VAL_CHANGED_EVENT;
  public static String        STR_BIT_WORD_TAG_VAL_CHANGED_EVENT;
  public static String        STR_D_BIT_NUMBER;
  public static String        STR_D_CMD_ARG_FLT;
  public static String        STR_D_CMD_ARG_INT;
  public static String        STR_D_CONDITION_JAVA_CLASS;
  public static String        STR_D_DATA_JAVA_CLASS;
  public static String        STR_D_DOWN;
  public static String        STR_D_EV_PARAM_ON;
  public static String        STR_D_EVENT_SENDER_JAVA_CLASS;
  public static String        STR_D_FORMER_EVENT_PARAM;
  public static String        STR_D_FRONT;
  public static String        STR_D_JAVA_CLASS;
  public static String        STR_D_OPC_CMD_ID;
  public static String        STR_D_PARAM_FORMER_JAVA_CLASS;
  public static String        STR_D_PARAM_ID;
  public static String        STR_N_BIT_NUMBER;
  public static String        STR_N_CMD_ARG_FLT;
  public static String        STR_N_CMD_ARG_INT;
  public static String        STR_N_CONDITION_JAVA_CLASS;
  public static String        STR_N_DATA_JAVA_CLASS;
  public static String        STR_N_DOWN;
  public static String        STR_N_EV_PARAM_ON;
  public static String        STR_N_EVENT_SENDER_JAVA_CLASS;
  public static String        STR_N_FORMER_EVENT_PARAM;
  public static String        STR_N_FRONT;
  public static String        STR_N_JAVA_CLASS;
  public static String        STR_N_OPC_CMD_ID;
  public static String        STR_N_PARAM_FORMER_JAVA_CLASS;
  public static String        STR_N_PARAM_ID;
  public static String        STR_ON;
  public static String        STR_ONE_2_ONE;
  public static String        STR_SET_NODE_VALUE;
  public static String        STR_SET_VALUE_CMD;
  public static String        STR_TAG_VAL_CHANGED_EVENT;
  public static String        STR_VALUE;
  public static String STR_D_IS_CURR;//      = "Текущие данные";\\
  public static String STR_N_IS_CURR  ;//    = "Текущие";\\
  public static String STR_D_IS_HIST ;//     = "Исторические данные";\\
  public static String STR_N_IS_HIST ;//     = "Исторические";\\
  public static String STR_D_SYNCH_PERIOD;// = "Период синхронизации данных, мс";\\
  public static String STR_N_SYNCH_PERIOD;// = "Период синхронизации, мс";\\
  public static String STR_D_SYNTH_TAG ;//   = "Идентификатор синтетического тега";\\
  public static String STR_N_SYNTH_TAG ;//   = "Синтетический тег";\\

  static {
    // initialize resource bundle
    NLS.initializeMessages( BUNDLE_NAME, Messages.class );
  }

  private Messages() {
  }
}
