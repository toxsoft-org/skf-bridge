package org.toxsoft.skf.bridge.s5.lib.impl;

import static org.toxsoft.uskat.core.utils.SkHelperUtils.*;

import java.io.*;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.uskat.core.*;

/**
 * Реализация {@link ISkGatewayGwids}
 *
 * @author mvk
 */
public class SkGatewayGwids
    implements ISkGatewayGwids, Serializable {

  private static final long serialVersionUID = 157157L;

  /**
   * Value-object registration identifier for {@link TsValobjUtils}.
   */
  public static final String KEEPER_ID = "SkGatewayGwids"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public static final IEntityKeeper<ISkGatewayGwids> KEEPER =
      new AbstractEntityKeeper<>( ISkGatewayGwids.class, EEncloseMode.ENCLOSES_BASE_CLASS, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, ISkGatewayGwids aEntity ) {
          aSw.writeAsIs( aEntity.gwidKind().id() );
          aSw.writeSeparatorChar();
          GwidList.KEEPER.write( aSw, aEntity.includeGwids() );
          aSw.writeSeparatorChar();
          GwidList.KEEPER.write( aSw, aEntity.excludeGwids() );
          aSw.writeSeparatorChar();
          aSw.writeInt( aEntity.includeQualityGwids() ? 1 : 0 );
        }

        @Override
        protected ISkGatewayGwids doRead( IStrioReader aSr ) {
          SkGatewayGwids configs = new SkGatewayGwids( EGwidKind.findById( aSr.readIdName() ) );
          aSr.ensureSeparatorChar();
          configs.setIncludeGwids( GwidList.KEEPER.read( aSr ) );
          aSr.ensureSeparatorChar();
          configs.setExcludeGwids( GwidList.KEEPER.read( aSr ) );
          aSr.ensureSeparatorChar();
          configs.setIncludeQualityGwids( (aSr.readInt() != 0) );
          return configs;
        }
      };

  /**
   * Формат текстового представления {@link SkGatewayGwids}
   */
  private static final String TO_STRING_FORMAT = "%s, includes = %s, excludes = %s, dq = %b"; //$NON-NLS-1$

  private final EGwidKind gwidKind;
  private GwidList        includeGwids        = new GwidList();
  private GwidList        excludeGwids        = new GwidList();
  private boolean         includeQualityGwids = false;

  /**
   * Конструктор
   *
   * @param aGwidKind {@link EGwidKind} тип идентификаторов хранимых в конфигурации
   * @throws TsNullArgumentRtException аргумент = null
   */
  public SkGatewayGwids( EGwidKind aGwidKind ) {
    TsNullArgumentRtException.checkNull( aGwidKind );
    gwidKind = aGwidKind;
  }

  // ------------------------------------------------------------------------------------
  // Открытые методы
  //
  /**
   * Установить идентификаторы включаемых идентификаторов {@link Gwid}.
   *
   * @param aIncludeGwids {@link IGwidList} список включаемых идентификаторов {@link Gwid}.
   * @throws TsNullArgumentRtException аргумент = null
   */
  public void setIncludeGwids( IGwidList aIncludeGwids ) {
    TsNullArgumentRtException.checkNull( aIncludeGwids );
    includeGwids.clear();
    for( Gwid gwid : aIncludeGwids ) {
      if( gwid.kind() == gwidKind ) {
        includeGwids.add( gwid );
      }
    }
  }

  /**
   * Установить идентификаторы исключаемых идентификаторов {@link Gwid}.
   *
   * @param aExcludeGwids {@link IGwidList} список исключаемых идентификаторов {@link Gwid}.
   * @throws TsNullArgumentRtException аргумент = null
   */
  public void setExcludeGwids( IGwidList aExcludeGwids ) {
    TsNullArgumentRtException.checkNull( aExcludeGwids );
    excludeGwids.clear();
    for( Gwid gwid : aExcludeGwids ) {
      if( gwid.kind() == gwidKind ) {
        excludeGwids.add( gwid );
      }
    }
  }

  /**
   * Установить требование включать/выключать идентификаторы {@link Gwid} предоставляемые службой качества данных.
   *
   * @param aIncludeQualityGwids boolean <b>true</b> включать идентификаторы {@link Gwid}; <b>false</b> не включать
   *          идентификаторы {@link Gwid}.
   * @throws TsNullArgumentRtException аргумент = null
   */
  public void setIncludeQualityGwids( boolean aIncludeQualityGwids ) {
    includeQualityGwids = aIncludeQualityGwids;
  }

  // ------------------------------------------------------------------------------------
  // Реализация ISkGatewayGwids
  //
  @Override
  public EGwidKind gwidKind() {
    return gwidKind;
  }

  @Override
  public boolean includeQualityGwids() {
    return includeQualityGwids;
  }

  @Override
  public IGwidList includeGwids() {
    return includeGwids;
  }

  @Override
  public IGwidList excludeGwids() {
    return excludeGwids;
  }

  // ------------------------------------------------------------------------------------
  // Реализация Object
  //
  @Override
  public String toString() {
    return String.format( TO_STRING_FORMAT, gwidKind, includeGwids, excludeGwids,
        Boolean.valueOf( includeQualityGwids ) );
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = TsLibUtils.PRIME * result + gwidKind.hashCode();
    result = TsLibUtils.PRIME * result + includeGwids.hashCode();
    result = TsLibUtils.PRIME * result + excludeGwids.hashCode();
    result = TsLibUtils.PRIME * result + Boolean.hashCode( includeQualityGwids );
    return result;
  }

  @Override
  public boolean equals( Object aObject ) {
    if( this == aObject ) {
      return true;
    }
    if( aObject == null ) {
      return false;
    }
    if( getClass() != aObject.getClass() ) {
      return false;
    }
    ISkGatewayGwids other = (ISkGatewayGwids)aObject;
    if( !gwidKind.equals( other.gwidKind() ) ) {
      return false;
    }
    if( !includeGwids.equals( other.includeGwids() ) ) {
      return false;
    }
    if( !excludeGwids.equals( other.excludeGwids() ) ) {
      return false;
    }
    if( includeQualityGwids != other.includeQualityGwids() ) {
      return false;
    }
    return true;
  }

  // ------------------------------------------------------------------------------------
  // Вспомогательные методы
  //
  /**
   * Возвращает список идентификаторов конфигурации
   *
   * @param aCoreApi {@link ISkCoreApi} API сервера
   * @param aConfig {@link ISkGatewayGwids} идентификаторы {@link Gwid} для подсистемы
   * @param aQualityGwids {@link IGwidList} список идентификаторов предоставляемых службой качества
   * @param aOutputKind {@link EGwidKind} тип выходных идентификаторов
   * @return {@link IGwidList} список идентификаторов
   */
  public static IGwidList getConfigGwids( ISkCoreApi aCoreApi, ISkGatewayGwids aConfig, IGwidList aQualityGwids,
      EGwidKind aOutputKind ) {
    TsNullArgumentRtException.checkNulls( aCoreApi, aConfig, aQualityGwids );
    // Добавление идентификаторов в результат
    GwidList retValue = new GwidList();

    // Добавление идентификаторов конфигурации
    retValue.addAll( expandGwids( aCoreApi, aConfig.includeGwids(), aOutputKind ) );

    // Добавление идентификаторов качества данных
    if( aConfig.includeQualityGwids() ) {
      retValue.addAll( expandGwids( aCoreApi, aQualityGwids, aOutputKind ) );
    }

    // Исключение идентификаторов из результата
    if( aConfig.excludeGwids().size() > 0 ) {
      // Получение полного списка идентификаторов исключений
      IGwidList excludeList = expandGwids( aCoreApi, aConfig.excludeGwids(), aOutputKind );
      // Получение полного списка идентификаторов результата
      GwidList tmp = new GwidList();
      for( Gwid gwid : retValue ) {
        if( !excludeList.hasElem( gwid ) ) {
          tmp.add( gwid );
        }
      }
      retValue = tmp;
    }
    return retValue;
  }
}
