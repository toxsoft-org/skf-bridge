package org.toxsoft.skf.brifge.cfg.s7.gui.type;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.valobj.*;

/**
 * Описание одного блока данных S7
 *
 * @author dima
 */
public class GuiS7DataBlock {

  private GuiS7Device guiS7Device = GuiS7Device.DEFAULT_DEVICE;

  private int dbNumber;

  private String params = TsLibUtils.EMPTY_STRING;

  private IListEdit<GuiS7Tag> tags = new ElemArrayList<>();

  /**
   * Идентификатор регистрации хранителя {@link #KEEPER} в реестре {@link TsValobjUtils}.
   */
  public static final String KEEPER_ID = "GuiS7DataBlock"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public final static IEntityKeeper<GuiS7DataBlock> KEEPER =
      new AbstractEntityKeeper<>( GuiS7DataBlock.class, EEncloseMode.NOT_IN_PARENTHESES, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, GuiS7DataBlock aEntity ) {
          GuiS7Device.KEEPER.write( aSw, aEntity.getS7Device() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          aSw.writeInt( aEntity.getDBNumber() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
        }

        @Override
        protected GuiS7DataBlock doRead( IStrioReader aSr ) {
          GuiS7Device s7Device = GuiS7Device.KEEPER.read( aSr );
          aSr.ensureSeparatorChar();
          int dbNumber = aSr.readInt();
          aSr.ensureSeparatorChar();
          return new GuiS7DataBlock( s7Device, dbNumber );

        }
      };

  /**
   * @param aGuiS7Device - S7 Device {@link GuiS7Device}
   * @param aDBNumber - number of DB
   */
  public GuiS7DataBlock( GuiS7Device aGuiS7Device, int aDBNumber ) {
    guiS7Device = aGuiS7Device;
    dbNumber = aDBNumber;
  }

  /**
   * @return id
   */
  @SuppressWarnings( "nls" )
  public String getId() {
    String ID_FMT_STR = "%s.dataBlock%d";
    String retVal = String.format( ID_FMT_STR, guiS7Device.id(), Integer.valueOf( dbNumber ) );
    return retVal;
  }

  /**
   * @return S7 Device {@link GuiS7Device}
   */
  public GuiS7Device getS7Device() {
    return guiS7Device;
  }

  /**
   * Set new S7 Device
   *
   * @param aGuiS7Device {@link GuiS7Device}
   */
  public void setS7Device( GuiS7Device aGuiS7Device ) {
    guiS7Device = aGuiS7Device;
  }

  /**
   * @return DB number
   */
  public int getDBNumber() {
    return dbNumber;
  }

  /**
   * Set new DB number
   *
   * @param aDBNumber - DB number
   */
  public void setDBNumber( int aDBNumber ) {
    dbNumber = aDBNumber;
  }

  /**
   * Returns parameters of node as string
   *
   * @return String - parameters of node as string
   */
  public String getParams() {
    return params;
  }

  /**
   * Sets params of node as string
   *
   * @param aParams - params of node as strin
   */
  public void setParams( String aParams ) {
    params = aParams;
  }

  /**
   * @return string
   */
  @SuppressWarnings( "boxing" )
  @Override
  public String toString() {
    return String.format( "%s : %d", guiS7Device.id(), dbNumber );
  }

  /**
   * @return list of lags {@link IList}
   */
  public IList<GuiS7Tag> getTags() {
    return tags;
  }

  public void addTag( GuiS7Tag aTag ) {
    tags.add( aTag );
  }

}
