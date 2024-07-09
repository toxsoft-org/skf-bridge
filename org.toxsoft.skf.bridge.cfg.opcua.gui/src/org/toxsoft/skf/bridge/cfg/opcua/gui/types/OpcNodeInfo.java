package org.toxsoft.skf.bridge.cfg.opcua.gui.types;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;

/**
 * Extended info opc node
 *
 * @author max
 */
public class OpcNodeInfo {

  private NodeId nodeId;

  private String displayName;

  private String browseName;

  private String description;

  private String parentDisplay;

  /**
   * Constructor by id
   *
   * @param aNodeId NodeId - id
   */
  public OpcNodeInfo( NodeId aNodeId ) {
    super();
    nodeId = aNodeId;
  }

  /**
   * Update all avqilable info from tree node
   *
   * @param aTreeNode UaTreeNode - tree node
   */
  public void updateFromUaTreeNode( UaTreeNode aTreeNode, UaTreeNode aParentTreeNode ) {
    browseName = aTreeNode.getBrowseName();
    displayName = aTreeNode.getDisplayName();
    description = aTreeNode.getDescription();

    parentDisplay =
        aParentTreeNode != null ? (aParentTreeNode.getBrowseName() + " [" + aParentTreeNode.getParentNodeId() + "]")
            : null;
  }

  /**
   * Returns Node id
   *
   * @return NodeId - node id, cant be null;
   */
  public NodeId getNodeId() {
    return nodeId;
  }

  /**
   * Returns displey name (may be null).
   *
   * @return displey name (may be null).
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Returns Browse Name (may be null).
   *
   * @return Browse Name (may be null).
   */
  public String getBrowseName() {
    return browseName;
  }

  /**
   * Returns Parent Node Id (may be null).
   *
   * @return Parent Node Id (may be null).
   */
  public String getParentDisplay() {
    return parentDisplay;
  }

  public void setDisplayName( String aDisplayName ) {
    displayName = aDisplayName;
  }

  public void setBrowseName( String aBrowseName ) {
    browseName = aBrowseName;
  }

  public void setParentDisplay( String aParentDisplay ) {
    parentDisplay = aParentDisplay;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( String aDescription ) {
    description = aDescription;
  }

  public synchronized void setNodeId( NodeId aNodeId ) {
    nodeId = aNodeId;
  }

}
