package org.toxsoft.skf.brifge.cfg.s7.gui.type;

import org.toxsoft.core.tslib.av.*;

import ru.toxsoft.l2.thd.opc.*;

/**
 * Описание одного тега S7
 *
 * @author dima
 */
public class GuiS7Tag {

  private GuiS7DataBlock dataBlock;

  private int bufferOffset = 0;

  private EAtomicType valueType = EAtomicType.FLOATING;

  private int bitNumber = -1; // только для типа Boolean

  private EKind tagKind = EKind.R;

  /**
   * @param aGuiS7DataBlock parent DB
   * @param aTagKind - R/W/RW
   * @param aBufferOffset - offset in DB buffer
   * @param aValueType - type of value
   */
  public GuiS7Tag( GuiS7DataBlock aGuiS7DataBlock, EKind aTagKind, int aBufferOffset, EAtomicType aValueType ) {
    dataBlock = aGuiS7DataBlock;
    tagKind = aTagKind;
    bufferOffset = aBufferOffset;
    valueType = aValueType;
  }

  /**
   * /**
   *
   * @param aGuiS7DataBlock parent DB
   * @param aTagKind - R/W/RW
   * @param aBufferOffset - offset in DB buffer
   * @param aBitNumber - bit number in refister of flags
   */
  public GuiS7Tag( GuiS7DataBlock aGuiS7DataBlock, EKind aTagKind, int aBufferOffset, int aBitNumber ) {
    dataBlock = aGuiS7DataBlock;
    tagKind = aTagKind;
    bufferOffset = aBufferOffset;
    valueType = EAtomicType.BOOLEAN;
    bitNumber = aBitNumber;
  }

  /**
   * @return id of S7 tag
   */
  @SuppressWarnings( "nls" )
  public String getId() {
    if( !valueType.equals( EAtomicType.BOOLEAN ) ) {
      String ID_FMT_STR = "%s.tagKind_%s.tagOffset_%d.valType_%s";
      String retVal = String.format( ID_FMT_STR, dataBlock.getId(), tagKind.getName(), Integer.valueOf( bufferOffset ),
          valueType.id() );
      return retVal;
    }
    String ID_FMT_STR = "%s.tagKind_%s.tagOffset_%d.valType_%s.bitNumber_%d";
    String retVal = String.format( ID_FMT_STR, dataBlock.getId(), tagKind.getName(), Integer.valueOf( bufferOffset ),
        valueType.id(), Integer.valueOf( bitNumber ) );
    return retVal;
  }

  /**
   * @return tag's DB
   */
  public GuiS7DataBlock getDataBlock() {
    return dataBlock;
  }

  /**
   * @return offset of valiue in DB buffer
   */
  public int getBufferOffset() {
    return bufferOffset;
  }

  /**
   * @return value type {@link EAtomicType}
   */
  public EAtomicType getValueType() {
    return valueType;
  }

  /**
   * @return bit postion in bit register
   */
  public int getBitNumber() {
    return bitNumber;
  }

  /**
   * @return kind of tag {@link EKind}
   */
  public EKind getTagKind() {
    return tagKind;
  }

}
