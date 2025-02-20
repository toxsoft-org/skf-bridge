package org.toxsoft.skf.bridge.s5.lib.impl;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.login.*;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.uskat.s5.client.remote.connection.*;

/**
 * Реализация {@link ISkGatewayInfo}
 *
 * @author mvk
 */
public class SkGatewayInfo
    extends StridableParameterizedSer
    implements ISkGatewayInfo {

  private static final long serialVersionUID = 157157L;

  /**
   * Формат текстового представления {@link SkGatewayInfo}
   */
  private static final String TO_STRING_FORMAT =
      "%s [%s@%s]. import[currdata{%s}, histdata{%s}, events{%s}, executors{%s}], export[currdata{%s}, histdata{%s}, events{%s}, executors{%s}]"; //$NON-NLS-1$

  private IS5ConnectionInfo connectionInfo;
  private ILoginInfo        loginInfo;
  private ISkGatewayGwids   exportCurrData     = new SkGatewayGwids( EGwidKind.GW_RTDATA );
  private ISkGatewayGwids   exportHistData     = new SkGatewayGwids( EGwidKind.GW_RTDATA );
  private ISkGatewayGwids   exportEvents       = new SkGatewayGwids( EGwidKind.GW_EVENT );
  private ISkGatewayGwids   exportCmdExecutors = new SkGatewayGwids( EGwidKind.GW_CMD );
  private ISkGatewayGwids   importCurrData     = new SkGatewayGwids( EGwidKind.GW_RTDATA );
  private ISkGatewayGwids   importHistData     = new SkGatewayGwids( EGwidKind.GW_RTDATA );
  private ISkGatewayGwids   importEvents       = new SkGatewayGwids( EGwidKind.GW_EVENT );
  private ISkGatewayGwids   importCmdExecutors = new SkGatewayGwids( EGwidKind.GW_CMD );

  /**
   * Конструктор
   *
   * @param aId String идентификатор шлюза (ИД-путь)
   * @param aDescription String описание шлюза
   * @param aName String удобочитаемое имя шлюза
   * @param aConnectionInfo {@link IS5ConnectionInfo} информация для подключения к удаленному серверу
   * @param aLoginInfo {@link ILoginInfo} информация об учетной записи для подключения к удаленному серверу
   * @throws TsNullArgumentRtException любой аргумент = null
   * @throws TsIllegalArgumentRtException aId не ИД-путь или не ИД-имя
   */
  public SkGatewayInfo( String aId, String aDescription, String aName, IS5ConnectionInfo aConnectionInfo,
      ILoginInfo aLoginInfo ) {
    // true: ИД-путь разрешен
    super( aId, IOptionSet.NULL );
    setNameAndDescription( aName, aDescription );
    setConnectionInfo( aConnectionInfo );
    setLoginInfo( aLoginInfo );
  }

  // ------------------------------------------------------------------------------------
  // Открытые методы
  //
  /**
   * Установить информацию для подключения к удаленному серверу
   *
   * @param aConnectionInfo {@link IS5ConnectionInfo} информация для подключения удаленному серверу
   * @throws TsNullArgumentRtException аргумент = null
   */
  public void setConnectionInfo( IS5ConnectionInfo aConnectionInfo ) {
    TsNullArgumentRtException.checkNull( aConnectionInfo );
    connectionInfo = aConnectionInfo;
  }

  /**
   * Установить информацию об учетной записи пользователя для подключения к удаленному серверу
   *
   * @param aLoginInfo {@link ILoginInfo} информация об учетной записи пользователя для подключения удаленному серверу
   * @throws TsNullArgumentRtException аргумент = null
   */
  public void setLoginInfo( ILoginInfo aLoginInfo ) {
    TsNullArgumentRtException.checkNull( aLoginInfo );
    loginInfo = aLoginInfo;
  }

  /**
   * Установить идентификаторы текущих данных передаваемых через шлюз
   *
   * @param aGwidConfigs {@link ISkGatewayGwids} конфигурация идентификаторов
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public void setExportCurrData( ISkGatewayGwids aGwidConfigs ) {
    TsNullArgumentRtException.checkNull( aGwidConfigs );
    TsIllegalArgumentRtException.checkFalse( aGwidConfigs.gwidKind() == EGwidKind.GW_RTDATA );
    exportCurrData = aGwidConfigs;
  }

  /**
   * Установить идентификаторы хранимых данных передаваемых через шлюз
   *
   * @param aGwidConfigs {@link ISkGatewayGwids} конфигурация идентификаторов
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public void setExportHistData( ISkGatewayGwids aGwidConfigs ) {
    TsNullArgumentRtException.checkNull( aGwidConfigs );
    TsIllegalArgumentRtException.checkFalse( aGwidConfigs.gwidKind() == EGwidKind.GW_RTDATA );
    exportHistData = aGwidConfigs;
  }

  /**
   * Установить идентификаторы событий передаваемых через шлюз
   *
   * @param aGwidConfigs {@link ISkGatewayGwids} конфигурация идентификаторов
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public void setExportEvents( ISkGatewayGwids aGwidConfigs ) {
    TsNullArgumentRtException.checkNull( aGwidConfigs );
    TsIllegalArgumentRtException.checkFalse( aGwidConfigs.gwidKind() == EGwidKind.GW_EVENT );
    exportEvents = aGwidConfigs;
  }

  /**
   * Установить идентификаторы исполняемых команд выполняемых локальным сервером
   *
   * @param aGwidConfigs {@link ISkGatewayGwids} конфигурация идентификаторов
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public void setExportCmdExecutors( ISkGatewayGwids aGwidConfigs ) {
    TsNullArgumentRtException.checkNull( aGwidConfigs );
    TsIllegalArgumentRtException.checkFalse( aGwidConfigs.gwidKind() == EGwidKind.GW_CMD );
    exportCmdExecutors = aGwidConfigs;
  }

  /**
   * Установить идентификаторы текущих данных принимаемых через шлюз
   *
   * @param aGwidConfigs {@link ISkGatewayGwids} конфигурация идентификаторов
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public void setImportCurrData( ISkGatewayGwids aGwidConfigs ) {
    TsNullArgumentRtException.checkNull( aGwidConfigs );
    TsIllegalArgumentRtException.checkFalse( aGwidConfigs.gwidKind() == EGwidKind.GW_RTDATA );
    importCurrData = aGwidConfigs;
  }

  /**
   * Установить идентификаторы хранимых данных принимаемых через шлюз
   *
   * @param aGwidConfigs {@link ISkGatewayGwids} конфигурация идентификаторов
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public void setImportHistData( ISkGatewayGwids aGwidConfigs ) {
    TsNullArgumentRtException.checkNull( aGwidConfigs );
    TsIllegalArgumentRtException.checkFalse( aGwidConfigs.gwidKind() == EGwidKind.GW_RTDATA );
    importHistData = aGwidConfigs;
  }

  /**
   * Установить идентификаторы событий принимаемых через шлюз
   *
   * @param aGwidConfigs {@link ISkGatewayGwids} конфигурация идентификаторов
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public void setImportEvents( ISkGatewayGwids aGwidConfigs ) {
    TsNullArgumentRtException.checkNull( aGwidConfigs );
    TsIllegalArgumentRtException.checkFalse( aGwidConfigs.gwidKind() == EGwidKind.GW_EVENT );
    importEvents = aGwidConfigs;
  }

  /**
   * Установить идентификаторы команд обрабатываемых удаленным сервером
   *
   * @param aGwidConfigs {@link ISkGatewayGwids} конфигурация идентификаторов
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public void setImportCmdExecutors( ISkGatewayGwids aGwidConfigs ) {
    TsNullArgumentRtException.checkNull( aGwidConfigs );
    TsIllegalArgumentRtException.checkFalse( aGwidConfigs.gwidKind() == EGwidKind.GW_CMD );
    importCmdExecutors = aGwidConfigs;
  }

  // ------------------------------------------------------------------------------------
  // Реализация ISkGatewayInfo
  //
  @Override
  public IS5ConnectionInfo connectionInfo() {
    return connectionInfo;
  }

  @Override
  public ILoginInfo loginInfo() {
    return loginInfo;
  }

  @Override
  public ISkGatewayGwids exportCurrData() {
    return exportCurrData;
  }

  @Override
  public ISkGatewayGwids exportHistData() {
    return exportHistData;
  }

  @Override
  public ISkGatewayGwids exportEvents() {
    return exportEvents;
  }

  @Override
  public ISkGatewayGwids exportCmdExecutors() {
    return exportCmdExecutors;
  }

  @Override
  public ISkGatewayGwids importCurrData() {
    return importCurrData;
  }

  @Override
  public ISkGatewayGwids importHistData() {
    return importHistData;
  }

  @Override
  public ISkGatewayGwids importEvents() {
    return importEvents;
  }

  @Override
  public ISkGatewayGwids importCmdExecutors() {
    return importCmdExecutors;
  }

  // ------------------------------------------------------------------------------------
  // Реализация Object
  //
  @Override
  public String toString() {
    return String.format( TO_STRING_FORMAT, id(), loginInfo.login(), connectionInfo, //
        exportCurrData, exportHistData, exportEvents, exportCmdExecutors, //
        importCurrData, importHistData, importEvents, importCmdExecutors );
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = TsLibUtils.PRIME * result + connectionInfo.hashCode();
    // TODO: LoginInfo не имеет hashCode и equals
    result = TsLibUtils.PRIME * result + loginInfo.login().hashCode();
    result = TsLibUtils.PRIME * result + loginInfo.password().hashCode();
    result = TsLibUtils.PRIME * result + loginInfo.role().hashCode();
    result = TsLibUtils.PRIME * result + exportCurrData.hashCode();
    result = TsLibUtils.PRIME * result + exportHistData.hashCode();
    result = TsLibUtils.PRIME * result + exportEvents.hashCode();
    result = TsLibUtils.PRIME * result + exportCmdExecutors.hashCode();
    result = TsLibUtils.PRIME * result + importCurrData.hashCode();
    result = TsLibUtils.PRIME * result + importHistData.hashCode();
    result = TsLibUtils.PRIME * result + importEvents.hashCode();
    result = TsLibUtils.PRIME * result + importCmdExecutors.hashCode();
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
    if( !super.equals( aObject ) ) {
      return false;
    }
    ISkGatewayInfo other = (ISkGatewayInfo)aObject;
    if( !connectionInfo.equals( other.connectionInfo() ) ) {
      return false;
    }
    // TODO: LoginInfo не имеет hashCode и equals
    if( !loginInfo.login().equals( other.loginInfo().login() ) ) {
      return false;
    }
    if( !loginInfo.password().equals( other.loginInfo().password() ) ) {
      return false;
    }
    if( !loginInfo.role().equals( other.loginInfo().role() ) ) {
      return false;
    }
    if( !exportCurrData.equals( other.exportCurrData() ) ) {
      return false;
    }
    if( !exportHistData.equals( other.exportHistData() ) ) {
      return false;
    }
    if( !exportEvents.equals( other.exportEvents() ) ) {
      return false;
    }
    if( !exportCmdExecutors.equals( other.exportCmdExecutors() ) ) {
      return false;
    }
    if( !importCurrData.equals( other.exportCurrData() ) ) {
      return false;
    }
    if( !importHistData.equals( other.exportHistData() ) ) {
      return false;
    }
    if( !importEvents.equals( other.exportEvents() ) ) {
      return false;
    }
    if( !importCmdExecutors.equals( other.exportCmdExecutors() ) ) {
      return false;
    }
    return true;
  }
}
