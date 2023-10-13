package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tslib.bricks.strio.IStrioHardConstants.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.stack.core.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;

/**
 * Tree node, aggragating UaNode, and forming tree structure.
 *
 * @author max
 * @author dima // make storable
 */
public class UaTreeNode {

  private IListEdit<UaTreeNode> children = new ElemArrayList<>();

  private UaTreeNode parent = null;

  private UaNode      uaNode       = null;
  private String      parentNodeId = null;
  private String      nodeId       = null;
  private String      browseName   = null;
  private String      displayName  = null;
  private String      description  = null;
  private NodeClass   nodeClass    = null;
  private EAtomicType type         = EAtomicType.NONE;

  private OpcUaClient        client;
  /**
   * Value-object keeper identifier.
   */
  public static final String KEEPER_ID = "UaTreeNode"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public final static IEntityKeeper<UaTreeNode> KEEPER =
      new AbstractEntityKeeper<>( UaTreeNode.class, EEncloseMode.NOT_IN_PARENTHESES, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, UaTreeNode aEntity ) {
          // пишем parent NodeId
          String parentNodeIdStr =
              aEntity.parent == null ? TsLibUtils.EMPTY_STRING : aEntity.parent.uaNode.getNodeId().toParseableString();
          aSw.writeQuotedString( parentNodeIdStr );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // пишем NodeId
          aSw.writeQuotedString( aEntity.uaNode.getNodeId().toParseableString() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // browseName
          String browseName = aEntity.uaNode.getBrowseName() == null ? TsLibUtils.EMPTY_STRING
              : aEntity.uaNode.getBrowseName().getName();
          aSw.writeQuotedString( browseName );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // displayName
          String displayName = aEntity.uaNode.getDisplayName() == null ? TsLibUtils.EMPTY_STRING
              : aEntity.uaNode.getDisplayName().getText();
          aSw.writeQuotedString( displayName );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // description
          String description =
              (aEntity.uaNode.getDescription() != null) && (aEntity.uaNode.getDescription().getText() != null)
                  ? aEntity.uaNode.getDescription().getText()
                  : TsLibUtils.EMPTY_STRING;
          aSw.writeQuotedString( description );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // node class
          int nodeClass = aEntity.uaNode.getNodeClass().getValue();
          aSw.writeInt( nodeClass );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // type
          EAtomicType.KEEPER.write( aSw, aEntity.type );
          aSw.writeEol();
        }

        @Override
        protected UaTreeNode doRead( IStrioReader aSr ) {
          String parentNodeId = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String nodeId = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String browseName = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String displayName = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String description = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          int nodeClassVal = aSr.readInt();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          EAtomicType type = EAtomicType.KEEPER.read( aSr );
          return new UaTreeNode( parentNodeId, nodeId, browseName, displayName, description,
              NodeClass.from( nodeClassVal ), type );
        }
      };

  /**
   * Constructor by parent node and content.
   *
   * @param aParent UaTreeNode - parent node, can be NULL - if this node is root.
   * @param aUaNode UaNode - content (real ua node).
   */
  public UaTreeNode( UaTreeNode aParent, UaNode aUaNode ) {
    super();
    TsNullArgumentRtException.checkNulls( aUaNode );
    parent = aParent;
    uaNode = aUaNode;
    if( uaNode instanceof UaVariableNode variableNode ) {
      Class<?> clazz = OpcUaUtils.getNodeDataTypeClass( variableNode );
      type = OpcUaUtils.getAtomicType( clazz );
    }
    if( aParent != null ) {
      aParent.children.add( this );
    }
  }

  /**
   * Constructor by parent nodeId and fields values.
   *
   * @param aParentNodeId UaTreeNode - parent node id, can be NULL - if this node is root.
   * @param aNodeId - node id
   * @param aBrowseName - node browse name
   * @param aDisplayName - node display name
   * @param aDescription - node description
   * @param aNodeClass - node class
   * @param aType - тип данного для node типа Variable
   */
  public UaTreeNode( String aParentNodeId, String aNodeId, String aBrowseName, String aDisplayName, String aDescription,
      NodeClass aNodeClass, EAtomicType aType ) {
    super();
    parentNodeId = aParentNodeId;
    nodeId = aNodeId;
    browseName = aBrowseName;
    displayName = aDisplayName;
    description = aDescription;
    nodeClass = aNodeClass;
    type = aType;
  }

  /**
   * Returns parent node.
   *
   * @return UaTreeNode - parent node, it can be null - if the node is root.
   */
  public UaTreeNode getParent() {
    return parent;
  }

  /**
   * @param aParent - a parent node
   * @param aClient - OPC UA connection
   */
  public void init( UaTreeNode aParent, OpcUaClient aClient ) {
    parent = aParent;
    client = aClient;
    if( aParent != null ) {
      aParent.children.add( this );
    }
  }

  /**
   * Returns opc ua node.
   *
   * @return UaNode - opc ua node.
   */
  public UaNode getUaNode() {
    if( uaNode == null ) {
      try {
        NodeId ni = NodeId.parse( getNodeId() );
        uaNode = client.getAddressSpace().getNode( ni );
      }
      catch( UaException ex ) {
        LoggerUtils.errorLogger().error( ex );
      }
    }
    return uaNode;
  }

  /**
   * Return children nodes.
   *
   * @return IList - list of children nodes.
   */
  public IList<UaTreeNode> getChildren() {
    return children;
  }

  /**
   * @return OPC UA Node Id
   */
  public String getNodeId() {
    return nodeId == null ? uaNode.getNodeId().toParseableString() : nodeId;
  }

  /**
   * @return parent OPC UA Node Id
   */
  public String getParentNodeId() {
    return parentNodeId;
  }

  /**
   * @return OPC UA browse name
   */
  public String getBrowseName() {
    return browseName == null ? uaNode.getBrowseName().getName() : browseName;
  }

  /**
   * @return OPC UA display name
   */
  public String getDisplayName() {
    return displayName == null ? uaNode.getDisplayName().getText() : displayName;
  }

  /**
   * @return OPC UA node class
   */
  public NodeClass getNodeClass() {
    return nodeClass == null ? uaNode.getNodeClass() : nodeClass;
  }

  /**
   * @return OPC UA node data type
   */
  public EAtomicType getDataType() {
    return type;
  }

  /**
   * @return OPC UA description
   */
  public String getDescription() {
    String retVal = TsLibUtils.EMPTY_STRING;
    if( description != null ) {
      return description;
    }
    if( uaNode.getDescription() != null && uaNode.getDescription().getText() != null ) {
      return uaNode.getDescription().getText();
    }
    return retVal;
  }

  /**
   * make node looks like root
   */
  public void clearParent() {
    parent = null;
  }

}
