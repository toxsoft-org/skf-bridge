package org.toxsoft.skf.bridge.cfg.opcua.gui.types;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.utils.valobj.*;

/**
 * Хранитель объектов типа {@link OpcNodeInfo}.
 * <p>
 * Для {@link OpcNodeInfo} хранит только NodeId - остальное подгружается с сервера OPC UA или кэша
 *
 * @author max
 */
public class OpcNodeInfoKeeper
    extends AbstractEntityKeeper<OpcNodeInfo> {

  /**
   * Идентификатор регистрации хранителя {@link #KEEPER} в реестре {@link TsValobjUtils}.
   */
  public static final String KEEPER_ID = "OpcNodeInfo"; //$NON-NLS-1$

  /**
   * Синглтон хранителя.
   */
  public static final IEntityKeeper<OpcNodeInfo> KEEPER = new OpcNodeInfoKeeper();

  private OpcNodeInfoKeeper() {
    super( OpcNodeInfo.class, EEncloseMode.NOT_IN_PARENTHESES, null );
  }

  @Override
  protected void doWrite( IStrioWriter aSw, OpcNodeInfo aEntity ) {
    aSw.writeQuotedString( aEntity.getNodeId().toParseableString() );
  }

  @Override
  protected OpcNodeInfo doRead( IStrioReader aSr ) {
    return new OpcNodeInfo( NodeId.parse( aSr.readQuotedString() ) );
  }

}
