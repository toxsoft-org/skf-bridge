package org.toxsoft.skf.bridge.cfg.opcua.gui;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.impl.DataDef.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.ISkResources.*;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tslib.av.metainfo.*;

/**
 * Константы пакета.
 *
 * @author goga
 */
@SuppressWarnings( { "javadoc" } )
public interface IOpcUaServerConnCfgConstants {

  String OPC_AU_CLASS_MARKER = "atrGeneratedFromOPC_UA"; //$NON-NLS-1$
  //
  // ------------------------------------------------------------------------------------
  // Icons

  String PREFIX_OF_ICON_FIELD_NAME = "ICONID_";  //$NON-NLS-1$
  String ICONID_APP_ICON           = "app-icon"; //$NON-NLS-1$

  String ICONID_VARIABLE_NODE = "s5-datatype";   //$NON-NLS-1$
  String ICONID_OBJECT_NODE   = "s5-obj";        //$NON-NLS-1$
  String ICONID_NEW_CLASS     = "s5-class-new";  //$NON-NLS-1$
  String ICONID_NEW_OBJECTS   = "s5-objs-class"; //$NON-NLS-1$
  //
  // -------------------------------------------------------------------------------------
  // Params of conn to opc ua server (constants)

  String OPID_PREFIX = "bridge.cfg.opcua"; //$NON-NLS-1$

  String OPID_HOST     = OPID_PREFIX + ".Host";     //$NON-NLS-1$
  String OPID_LOGIN    = OPID_PREFIX + ".Login";    //$NON-NLS-1$
  String OPID_PASSWORD = OPID_PREFIX + ".Password"; //$NON-NLS-1$

  IDataDef OPDEF_HOST = create( OPID_HOST, STRING, //
      TSID_NAME, STR_N_OPC_UA_SERVER_URI, //
      TSID_DESCRIPTION, STR_D_OPC_UA_SERVER_URI, //
      TSID_IS_MANDATORY, AV_FALSE, //
      TSID_DEFAULT_VALUE, avStr( "opc.tcp://127.0.0.1:4840" ) // по умолчанию Poligon'овкие //$NON-NLS-1$
                                                              // настройки
  );

  IDataDef OPDEF_LOGIN = create( OPID_LOGIN, STRING, //
      TSID_NAME, STR_N_LOGIN, //
      TSID_DESCRIPTION, STR_D_LOGIN, //
      TSID_IS_MANDATORY, AV_FALSE, //
      TSID_DEFAULT_VALUE, avStr( "user1" ) // по умолчанию Poligon'овкие настройки //$NON-NLS-1$
  );

  IDataDef OPDEF_PASSWORD = create( OPID_PASSWORD, STRING, //
      TSID_NAME, STR_N_PASSWORD, //
      TSID_DESCRIPTION, STR_D_PASSWORD, //
      TSID_IS_MANDATORY, AV_FALSE, //
      TSID_DEFAULT_VALUE, avStr( "361" ) // по умолчанию //$NON-NLS-1$
                                         // Poligon'овкие
                                         // настройки
  );

  /**
   * id действия "создать описание класса из OPC UA"
   */
  String CREATE_CINFO_FROM_OPC_UA_ACT_ID = "create_cinfo_from_opc_ua_act_id"; //$NON-NLS-1$

  /**
   * id действия "создать объекты из узлов OPC UA"
   */
  String CREATE_OBJS_FROM_OPC_UA_ACT_ID = "create_objs_from_opc_ua_act_id"; //$NON-NLS-1$

  /**
   * id действия "отобразить UaNode -> Gwid"
   */
  String SHOW_OPC_UA_NODE_2_GWID_ACT_ID = "show_opc_ua_node_2_gwid_act_id"; //$NON-NLS-1$

  TsActionDef ACTDEF_CREATE_CLASS_OPC_UA_ITEM = TsActionDef.ofPush2( CREATE_CINFO_FROM_OPC_UA_ACT_ID,
      STR_N_CREATE_CINFO_FROM_OPC_UA, STR_D_CREATE_CINFO_FROM_OPC_UA, ICONID_NEW_CLASS );

  TsActionDef ACTDEF_CREATE_OBJS_OPC_UA_ITEM = TsActionDef.ofPush2( CREATE_OBJS_FROM_OPC_UA_ACT_ID,
      STR_N_CREATE_OBJS_FROM_OPC_UA, STR_D_CREATE_OBJS_FROM_OPC_UA, ICONID_NEW_OBJECTS );

  TsActionDef ACTDEF_SHOW_OPC_UA_NODE_2_GWID =
      TsActionDef.ofPush2( SHOW_OPC_UA_NODE_2_GWID_ACT_ID, STR_N_SHOW_OPC_UA_NODE_2_GWID_ACT_ID,
          STR_D_SHOW_OPC_UA_NODE_2_GWID_ACT_ID, ITsStdIconIds.ICONID_EDIT_FIND_REPLACE );

  /**
   * Id of tree mode using origin nodes structure.
   */
  String TMID_GROUP_BY_OPC_UA_ORIGIN = "ByOpcUaOriginStructure"; //$NON-NLS-1$

  /**
   * File to store opc ua server configurations.
   */
  String OPC_UA_SERVER_CONN_CFG_STORE_FILE = "opcUaConnCfgStoreFile.skide"; //$NON-NLS-1$

  /**
   * Constants registration.
   *
   * @param aWinContext {@link IEclipseContext} - windows level context
   */
  static void init( IEclipseContext aWinContext ) {
    ITsIconManager iconManager = aWinContext.get( ITsIconManager.class );
    iconManager.registerStdIconByIds( Activator.PLUGIN_ID, IOpcUaServerConnCfgConstants.class,
        PREFIX_OF_ICON_FIELD_NAME );
    //
  }
}
