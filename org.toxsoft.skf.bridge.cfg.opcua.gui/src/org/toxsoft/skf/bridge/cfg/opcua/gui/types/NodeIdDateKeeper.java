package org.toxsoft.skf.bridge.cfg.opcua.gui.types;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.utils.valobj.*;

/**
 * Хранитель объектов типа {@link NodeId}.
 * <p>
 * Хранит {@link NodeId} в совместимом с формате, в виде "...".
 *
 * @author max
 */
public class NodeIdDateKeeper
    extends AbstractEntityKeeper<NodeId> {

  /**
   * Идентификатор регистрации хранителя {@link #KEEPER} в реестре {@link TsValobjUtils}.
   */
  public static final String KEEPER_ID = "NodeId"; //$NON-NLS-1$

  /**
   * Синглтон хранителя.
   */
  public static final IEntityKeeper<NodeId> KEEPER = new NodeIdDateKeeper();

  private NodeIdDateKeeper() {
    super( NodeId.class, EEncloseMode.NOT_IN_PARENTHESES, null );
  }

  @Override
  protected void doWrite( IStrioWriter aSw, NodeId aEntity ) {
    aSw.writeQuotedString( aEntity.toParseableString() );
  }

  @Override
  protected NodeId doRead( IStrioReader aSr ) {
    return NodeId.parse( aSr.readQuotedString() );
  }

}
