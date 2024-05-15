package org.toxsoft.skf.bridge.cfg.modbus.gui.type;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.utils.valobj.*;

/**
 * Хранитель объектов типа {@link ModbusNode}.
 * <p>
 * Хранит {@link ModbusNode} в совместимом с формате, в виде "...".
 *
 * @author max
 */
public class ModbusNodeKeeper
    extends AbstractEntityKeeper<ModbusNode> {

  /**
   * Идентификатор регистрации хранителя {@link #KEEPER} в реестре {@link TsValobjUtils}.
   */
  public static final String KEEPER_ID = "ModbusNode"; //$NON-NLS-1$

  /**
   * Синглтон хранителя.
   */
  public static final IEntityKeeper<ModbusNode> KEEPER = new ModbusNodeKeeper();

  private ModbusNodeKeeper() {
    super( ModbusNode.class, EEncloseMode.NOT_IN_PARENTHESES, null );
  }

  @Override
  protected void doWrite( IStrioWriter aSw, ModbusNode aEntity ) {
    TCPAddress.KEEPER.write( aSw, aEntity.getAddress() );
    aSw.writeSeparatorChar();
    aSw.writeEol();
    aSw.writeInt( aEntity.getRegister() );
    aSw.writeSeparatorChar();
    aSw.writeInt( aEntity.getRegister() );
    aSw.writeSeparatorChar();
    aSw.writeEol();
    aSw.writeInt( aEntity.getWordsCount() );
    aSw.writeSeparatorChar();
    aSw.writeEol();
    aSw.writeQuotedString( aEntity.getRequestType().name() );
    aSw.decNewLine();
  }

  @Override
  protected ModbusNode doRead( IStrioReader aSr ) {
    TCPAddress address = TCPAddress.KEEPER.read( aSr );
    aSr.ensureSeparatorChar();
    int register = aSr.readInt();
    aSr.ensureSeparatorChar();
    int wordsCount = aSr.readInt();
    aSr.ensureSeparatorChar();
    ERequestType type = ERequestType.valueOf( aSr.readQuotedString() );

    return new ModbusNode( address, register, wordsCount, type );
  }

}
