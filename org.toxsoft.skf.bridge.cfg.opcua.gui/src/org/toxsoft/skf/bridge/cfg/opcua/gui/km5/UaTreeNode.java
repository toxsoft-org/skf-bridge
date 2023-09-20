package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * Tree node, aggragating UaNode, and forming tree structure.
 *
 * @author max
 */
public class UaTreeNode {

  private IListEdit<UaTreeNode> children = new ElemArrayList<>();

  private UaTreeNode parent = null;

  private UaNode uaNode = null;

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

    if( aParent != null ) {
      aParent.children.add( this );
    }
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
   * Returns opc ua node.
   *
   * @return UaNode - opc ua node.
   */
  public UaNode getUaNode() {
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

}
