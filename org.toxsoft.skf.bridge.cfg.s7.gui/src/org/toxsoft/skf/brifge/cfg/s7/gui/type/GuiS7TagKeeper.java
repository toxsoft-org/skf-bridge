package org.toxsoft.skf.brifge.cfg.s7.gui.type;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.utils.valobj.*;

import ru.toxsoft.l2.thd.opc.*;

/**
 * Хранитель объектов типа {@link GuiS7Tag}.
 * <p>
 * Хранит {@link GuiS7Tag} в совместимом с формате, в виде "...".
 *
 * @author dima
 */
public class GuiS7TagKeeper
    extends AbstractEntityKeeper<GuiS7Tag> {

  /**
   * Идентификатор регистрации хранителя {@link #KEEPER} в реестре {@link TsValobjUtils}.
   */
  public static final String KEEPER_ID = "GuiS7Tag"; //$NON-NLS-1$

  /**
   * Синглтон хранителя.
   */
  public static final IEntityKeeper<GuiS7Tag> KEEPER = new GuiS7TagKeeper();

  private GuiS7TagKeeper() {
    super( GuiS7Tag.class, EEncloseMode.NOT_IN_PARENTHESES, null );
  }

  @Override
  protected void doWrite( IStrioWriter aSw, GuiS7Tag aEntity ) {
    GuiS7DataBlock.KEEPER.write( aSw, aEntity.getDataBlock() );
    aSw.writeSeparatorChar();
    aSw.writeEol();
    aSw.writeQuotedString( aEntity.getTagKind().name() );
    aSw.writeSeparatorChar();
    aSw.writeEol();
    aSw.writeInt( aEntity.getBufferOffset() );
    aSw.writeSeparatorChar();
    aSw.writeEol();
    aSw.writeQuotedString( aEntity.getValueType().name() );
    aSw.writeSeparatorChar();
    aSw.writeEol();
    if( aEntity.getValueType().equals( EAtomicType.BOOLEAN ) ) {
      aSw.writeInt( aEntity.getBitNumber() );
      aSw.writeSeparatorChar();
      aSw.writeEol();
    }
    aSw.decNewLine();
  }

  @Override
  protected GuiS7Tag doRead( IStrioReader aSr ) {
    GuiS7DataBlock dataBlock = GuiS7DataBlock.KEEPER.read( aSr );
    aSr.ensureSeparatorChar();
    EKind tagKid = EKind.findByName( aSr.readQuotedString() );
    aSr.ensureSeparatorChar();
    int bufferOffset = aSr.readInt();
    aSr.ensureSeparatorChar();
    EAtomicType valueType = EAtomicType.valueOf( aSr.readQuotedString() );
    aSr.ensureSeparatorChar();
    if( !valueType.equals( EAtomicType.BOOLEAN ) ) {
      return new GuiS7Tag( dataBlock, tagKid, bufferOffset, valueType );
    }
    int bitNumber = aSr.readInt();
    aSr.ensureSeparatorChar();
    return new GuiS7Tag( dataBlock, tagKid, bufferOffset, bitNumber );
  }

}
