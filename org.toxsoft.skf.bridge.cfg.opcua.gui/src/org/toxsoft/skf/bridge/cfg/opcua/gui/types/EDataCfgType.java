package org.toxsoft.skf.bridge.cfg.opcua.gui.types;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;

/**
 * Типы единиц конфигураии
 *
 * @author max
 */
public enum EDataCfgType {

  /**
   * Передатчик - сквозной - один тег на один gwid
   */
  ONE_TO_ONE( null, 1, ECountConstraintType.EXACT, 1, ECountConstraintType.EXACT ),

  /**
   * Самый общий передатчик - многие ко многим - логика передачи описывается скриптом
   */
  MANY_TO_MANY_SCRIPT( null, -1, ECountConstraintType.EXACT, -1, ECountConstraintType.EXACT );

  /**
   * The keeper ID.
   */
  public static final String KEEPER_ID = "EDataCfgType"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public static final IEntityKeeper<EDataCfgType> KEEPER =
      new AbstractEntityKeeper<>( EDataCfgType.class, EEncloseMode.ENCLOSES_BASE_CLASS, EDataCfgType.ONE_TO_ONE ) {

        @Override
        protected void doWrite( IStrioWriter aSw, EDataCfgType aEntity ) {
          aSw.writeAsIs( aEntity.name() );
        }

        @Override
        protected EDataCfgType doRead( IStrioReader aSr ) {
          String id = aSr.readIdPath();
          return EDataCfgType.valueOf( id );
        }

      };

  /**
   * Класс, реализующий передачу данных из тегов (нодов) в сущности s5
   */
  private String javaClass;

  /**
   * Количество тегов (нодов)
   */
  private int tagCount;

  /**
   * Тип ограничения числа тегов (нодов)
   */
  private ECountConstraintType tagCountType;

  /**
   * Количество gwid-ов
   */
  private int gwidCount;

  /**
   * Тип ограничения числа gwid-ов
   */
  private ECountConstraintType gwidCountType;

  /**
   * Конструктор по всем полям перечисления, может быть null
   *
   * @param aJavaClass - класс, реализующий данный тип передатчика данных
   * @param aTagCount - количество тегов, если -1 - то любое
   * @param aTagCountType - ограничение, описывающее количество тегов
   * @param aGwidCount - количество gwid-ов, если -1 - то любое
   * @param aGwidCountType - ограничение, описывающее количество gwid-ов
   */
  EDataCfgType( String aJavaClass, int aTagCount, ECountConstraintType aTagCountType, int aGwidCount,
      ECountConstraintType aGwidCountType ) {
    javaClass = aJavaClass;
    tagCount = aTagCount;
    tagCountType = aTagCountType;
    gwidCount = aGwidCount;
    gwidCountType = aGwidCountType;
  }

  /**
   * Класс, реализующий данный тип передатчика данных
   *
   * @return String - имя java-класса
   */
  public String getJavaClass() {
    return javaClass;
  }

  /**
   * Количество тегов, если -1 - то любое
   *
   * @return int - Количество тегов
   */
  public int getTagCount() {
    return tagCount;
  }

  /**
   * Ограничение, описывающее количество тегов
   *
   * @return ECountConstraintType - Ограничение, описывающее количество тегов
   */
  public ECountConstraintType getTagCountType() {
    return tagCountType;
  }

  /**
   * Количество gwid-ов, если -1 - то любое
   *
   * @return int - количество gwid-ов, если -1 - то любое
   */
  public int getGwidCount() {
    return gwidCount;
  }

  /**
   * Ограничение, описывающее количество gwid-ов
   *
   * @return ECountConstraintType - Ограничение, описывающее количество gwid-ов
   */
  public ECountConstraintType getGwidCountType() {
    return gwidCountType;
  }

}
