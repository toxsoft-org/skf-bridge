package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import java.util.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.avtree.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.validator.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

public class BaseOpcCommonDevCfgGenerator
    implements IOpcCommonDevCfgGenerator {

  //
  // -----------------------------------------
  // preset entities

  protected ITsGuiContext context;

  protected IList<CfgOpcUaNode> cfgNodes = new ElemArrayList<>();

  protected ISkConnection connection;

  protected INodeIdConvertor idConvertor = aNodeEntity -> new Pair<>( aNodeEntity.asString(), aNodeEntity.asString() );

  protected IOpcUaNodeFilter ocUaNodeFilter = IOpcUaNodeFilter.EMPTY_FILTER;

  protected IListEdit<IStringList> properties = new ElemArrayList<>();

  protected IComplexTagDetector complexTagDetector = ( aUnit, aContext ) -> false;

  protected IDevCfgParamValueSource paramValueSource =
      ( aParamName, aContext ) -> aContext.params().findByKey( aParamName );

  //
  // -----------------------------------------
  // result of generation

  protected AvTree result;

  /**
   * Constructor by context.
   *
   * @param aContext ITsGuiContext - context.
   */
  public BaseOpcCommonDevCfgGenerator( ITsGuiContext aContext ) {
    context = aContext;

    connection = context.get( ISkConnectionSupplier.class ).defConn();
  }

  @Override
  public IOpcCommonDevCfgGenerator setUnits( IList<CfgOpcUaNode> aCfgUnits ) {
    TsIllegalStateRtException.checkFalse( result == null, "Result has been already generated" );
    cfgNodes = new ElemArrayList<>( aCfgUnits );
    return this;
  }

  @Override
  public IOpcCommonDevCfgGenerator setConnection( ISkConnection aConn ) {
    TsIllegalStateRtException.checkFalse( result == null, "Result has been already generated" );
    connection = aConn;
    return this;
  }

  @Override
  public IOpcCommonDevCfgGenerator setNodeIdConvertor( INodeIdConvertor aIdConvertor ) {
    TsIllegalStateRtException.checkFalse( result == null, "Result has been already generated" );
    idConvertor = aIdConvertor;
    return this;
  }

  @Override
  public IOpcCommonDevCfgGenerator setNodeFilter( IOpcUaNodeFilter aNodeFilter ) {
    TsIllegalStateRtException.checkFalse( result == null, "Result has been already generated" );
    ocUaNodeFilter = aNodeFilter;
    return this;
  }

  @Override
  public IOpcCommonDevCfgGenerator setAdditionalProperties( IList<IStringList> aProperties ) {
    TsIllegalStateRtException.checkFalse( result == null, "Result has been already generated" );
    properties = new ElemArrayList<>( aProperties );
    return this;
  }

  @Override
  public IOpcCommonDevCfgGenerator setParamValueSource( IDevCfgParamValueSource aParamValueSource ) {
    TsIllegalStateRtException.checkFalse( result == null, "Result has been already generated" );
    paramValueSource = aParamValueSource;
    return this;
  }

  @Override
  public IAvTree generate() {
    String endPointURL = HOST_PARAM_VAL_TEMPLATE;
    String user = USER_PARAM_VAL_TEMPLATE;
    String pass = PASSWORD_PARAM_VAL_TEMPLATE;
    OpcUaServerConnCfg conConf = (OpcUaServerConnCfg)context.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );
    if( conConf != null ) {
      endPointURL = conConf.host();
      user = conConf.login();
      pass = conConf.password();
    }
    // else {
    // endPointURL = aDoc.getEndPointURL();
    // user = aDoc.getUserOPC_UA();
    // pass = aDoc.getPasswordOPC_UA();
    // }

    IOptionSetEdit opSet = new OptionSet();

    opSet.setStr( JAVA_CLASS_PARAM_NAME, JAVA_CLASS_PARAM_VAL_TEMPLATE );
    opSet.setStr( ID_PARAM_NAME, OPC_TAG_DEVICE_UA );
    opSet.setStr( DESCRIPTION_PARAM_NAME, DESCRIPTION_PARAM_VAL_TEMPLATE );

    IOptionSetEdit bridgeOps = new OptionSet();

    bridgeOps.setStr( ID_PARAM_NAME, OPC_TAG_DEVICE_UA );
    bridgeOps.setStr( DESCRIPTION_PARAM_NAME, DESCRIPTION_PARAM_VAL_TEMPLATE );
    bridgeOps.setStr( HOST_PARAM_NAME, endPointURL );// host
    bridgeOps.setStr( USER_PARAM_NAME, user );
    bridgeOps.setStr( PASSWORD_PARAM_NAME, pass );

    AvTree synchGroup =
        createGroup( cfgNodes, aCfgNode -> (aCfgNode.isRead() && aCfgNode.isSynch() && !aCfgNode.isNodeIdNull()),
            SYNC_TAGS_ARRAY_ID, SYNC_GROUP_NODE_ID );

    synchGroup.fieldsEdit().setInt( SYNCH_PERIOD_PARAM_NAME, 500 );

    IAvTree asynchGroup =
        createGroup( cfgNodes, aCfgNode -> (aCfgNode.isRead() && !aCfgNode.isSynch()) && !aCfgNode.isNodeIdNull(),
            ASYNC_TAGS_ARRAY_ID, ASYNC_GROUP_NODE_ID );

    IAvTree outputGroup = createGroup( cfgNodes, aCfgNode -> (aCfgNode.isWrite() && !aCfgNode.isNodeIdNull()),
        OUTPUT_TAGS_ARRAY_ID, OUTPUT_GROUP_NODE_ID );

    // массив групп
    AvTree groupsMassivTree = AvTree.createArrayAvTree();

    groupsMassivTree.addElement( synchGroup );
    groupsMassivTree.addElement( asynchGroup );
    groupsMassivTree.addElement( outputGroup );

    // массив групп
    AvTree bridgesMassivTree = AvTree.createArrayAvTree();

    StringMap<IAvTree> groupsNodes = new StringMap<>();
    groupsNodes.put( GROUPS_ARRAY_NAME, groupsMassivTree );

    IAvTree siemensBridge = AvTree.createSingleAvTree( SIEMENS_BRIDGE_NODE_ID, bridgeOps, groupsNodes );
    bridgesMassivTree.addElement( siemensBridge );

    StringMap<IAvTree> nodes = new StringMap<>();
    nodes.put( BRIDGES_ARRAY_NAME, bridgesMassivTree );

    IAvTree tree = AvTree.createSingleAvTree( OPC2S5_CFG_NODE_ID, opSet, nodes );

    return tree;
  }

  private AvTree createGroup( IList<CfgOpcUaNode> aCfgNodes, IGroupFilter aGroupFilter, String aArrayId,
      String aGroupNodeId ) {

    // массив тегов группы
    AvTree tagsMassivTree = AvTree.createArrayAvTree();

    Set<String> alreadyAddedTags = new HashSet<>();

    for( CfgOpcUaNode tagData : aCfgNodes ) {
      if( !aGroupFilter.isValid( tagData ) ) {
        continue;
      }

      if( !isSuited( tagData ) ) {
        continue;
      }

      IAvTree tag = createTag( tagData );

      tagsMassivTree.addElement( tag );
    }

    StringMap<IAvTree> nodes = new StringMap<>();
    nodes.put( aArrayId, tagsMassivTree );

    IOptionSetEdit pinOpSet1 = new OptionSet();

    AvTree groupTree = AvTree.createSingleAvTree( aGroupNodeId, pinOpSet1, nodes );
    return groupTree;
  }

  private boolean isSuited( CfgOpcUaNode aTagData ) {
    return ocUaNodeFilter.isSuited( aTagData );
    // if( aTagData.getNodeId().indexOf( TKA_TEMPLATE ) < 0 ) {
    // return false;
    // }
    // return true;
  }

  private static IAvTree createTag( CfgOpcUaNode aData ) {

    IOptionSetEdit pinOpSet1 = new OptionSet();

    pinOpSet1.setStr( OPC_TAG_PARAM_NAME, aData.getNodeId() );
    pinOpSet1.setStr( PIN_ID_PARAM_NAME, getPinId( aData.getNodeId() ) );
    pinOpSet1.setStr( PIN_TYPE_PARAM_NAME, aData.getType().id() );

    // ЗАПЛАТКА TODO - переделать драйвер!!!!!!!!! TODO
    if( aData.getType() == EAtomicType.INTEGER ) {
      pinOpSet1.setStr( PIN_TYPE_EXTRA_PARAM_NAME, "INT" );
    }

    // if( aData.getCmdWordBitIndex() > -1 ) {
    // pinOpSet1.setBool( PIN_CONTROL_WORD_PARAM_NAME, true );
    // }

    IAvTree pinTree1 = null;
    try {
      pinTree1 = AvTree.createSingleAvTree( String.format( PIN_TAG_NODE_ID_FORMAT, getPinId( aData.getNodeId() ) ),
          pinOpSet1, IStringMap.EMPTY );
    }
    catch( TsValidationFailedRtException e ) {
      System.out.println( aData.getNodeId() );
      throw e;
    }
    return pinTree1;
  }

  private static String getPinId( String aTagName ) {
    String result = aTagName;
    result = result.replace( " ", "_" );
    result = result.replace( "-", "_" );
    result = result.replace( ".", "_" );
    result = result.replace( "(", "_" );
    result = result.replace( ")", "_" );
    result = result.replace( "=", "_" );
    result = result.replace( ";", "_" );
    result = result.replace( "\\\"", "" );
    result = result.replace( "\"", "" );
    return result;
  }

  private interface IGroupFilter {

    boolean isValid( CfgOpcUaNode aCfgOpcUaNode );
  }
}
