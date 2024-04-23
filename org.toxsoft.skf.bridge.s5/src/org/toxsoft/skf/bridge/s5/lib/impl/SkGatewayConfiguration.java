package org.toxsoft.skf.bridge.s5.lib.impl;

import org.toxsoft.core.tslib.av.opset.IOptionSet;
import org.toxsoft.core.tslib.bricks.strid.impl.StridableParameterizedSer;
import org.toxsoft.core.tslib.gw.gwid.GwidList;
import org.toxsoft.core.tslib.gw.gwid.IGwidList;
import org.toxsoft.core.tslib.utils.TsLibUtils;
import org.toxsoft.core.tslib.utils.errors.TsIllegalArgumentRtException;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.core.tslib.utils.login.ILoginInfo;
import org.toxsoft.skf.bridge.s5.lib.ISkGatewayConfiguration;
import org.toxsoft.skf.dq.lib.ISkDataQualityService;
import org.toxsoft.uskat.core.api.cmdserv.ISkCommandExecutor;
import org.toxsoft.uskat.core.api.cmdserv.ISkCommandService;
import org.toxsoft.uskat.core.api.evserv.ISkEventService;
import org.toxsoft.uskat.core.backend.api.IBaRtdata;
import org.toxsoft.uskat.s5.client.remote.connection.IS5ConnectionInfo;

/**
 * Реализация {@link ISkGatewayConfiguration}
 *
 * @author mvk
 */
public class SkGatewayConfiguration
    extends StridableParameterizedSer
    implements ISkGatewayConfiguration {

  private static final long   serialVersionUID = 157157L;
  /**
   * Формат текстового представления {@link SkGatewayConfiguration}
   */
  private static final String TO_STRING_FORMAT = "%s [%s@%s]. gwids = %d"; //$NON-NLS-1$

  private IS5ConnectionInfo connectionInfo;
  private ILoginInfo        loginInfo;
  private GwidList          gwids  = new GwidList();
  private boolean           paused = false;

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
  public SkGatewayConfiguration( String aId, String aDescription, String aName, IS5ConnectionInfo aConnectionInfo,
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
   * Установить идентификаторы данных передаваемых через шлюз
   * <ul>
   * <li>Текущие данные: смотри {@link IBaRtdata#configureCurrDataReader(IGwidList)},
   * {@link IBaRtdata#configureCurrDataWriter(IGwidList)};</li>
   * <li>Команды: смотри {@link ISkCommandService#registerExecutor(ISkCommandExecutor, IGwidList)};</li>
   * <li>События: смотри {@link ISkEventService};</li>
   * <li>Качество данных: смотри {@link ISkDataQualityService}.</li>
   * </ul>
   *
   * @param aGwids {@link IGwidList} список идентификаторов данных
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public void setGwids( IGwidList aGwids ) {
    TsNullArgumentRtException.checkNull( aGwids );
    gwids.setAll( aGwids );
  }

  /**
   * Устанавливает признак того, что передача данных через шлюз временно приостановлена клиентом
   *
   * @param aPaused boolean <b>true</b> передача данных приостановлена;<b>false</b> шлюз работает в штатном режиме
   * @throws TsNullArgumentRtException любой аргумент = null
   * @throws TsIllegalArgumentRtException шлюз не существует
   */
  public void setPaused( boolean aPaused ) {
    paused = aPaused;
  }

  // ------------------------------------------------------------------------------------
  // Реализация ISkGatewayConfiguration
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
  public IGwidList gwids() {
    return gwids;
  }

  @Override
  public boolean isPaused() {
    return paused;
  }

  // ------------------------------------------------------------------------------------
  // Реализация Object
  //
  @Override
  public String toString() {
    return String.format( TO_STRING_FORMAT, id(), loginInfo.login(), connectionInfo, Integer.valueOf( gwids.size() ) );
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = TsLibUtils.PRIME * result + connectionInfo.hashCode();
    // TODO: LoginInfo не имеет hashCode и equals
    result = TsLibUtils.PRIME * result + loginInfo.login().hashCode();
    result = TsLibUtils.PRIME * result + loginInfo.password().hashCode();
    result = TsLibUtils.PRIME * result + loginInfo.role().hashCode();
    result = TsLibUtils.PRIME * result + gwids.hashCode();
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
    ISkGatewayConfiguration other = (ISkGatewayConfiguration)aObject;
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
    if( !gwids.equals( other.gwids() ) ) {
      return false;
    }
    return true;
  }
}
