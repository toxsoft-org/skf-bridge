package org.toxsoft.skf.bridge.s5.skadmin.gateways;

import static org.toxsoft.skf.bridge.s5.skadmin.gateways.IAdminHardConstants.*;
import static org.toxsoft.skf.bridge.s5.skadmin.gateways.IAdminHardResources.*;
import static org.toxsoft.uskat.skadmin.core.EAdminCmdContextNames.*;

import org.toxsoft.core.tslib.av.IAtomicValue;
import org.toxsoft.core.tslib.bricks.strid.coll.IStridablesListEdit;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.StridablesList;
import org.toxsoft.core.tslib.coll.IList;
import org.toxsoft.core.tslib.coll.primtypes.IStringList;
import org.toxsoft.core.tslib.coll.primtypes.IStringMap;
import org.toxsoft.core.tslib.gw.gwid.EGwidKind;
import org.toxsoft.core.tslib.utils.errors.TsIllegalArgumentRtException;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.core.tslib.utils.login.LoginInfo;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.skf.bridge.s5.lib.impl.SkGatewayConfiguration;
import org.toxsoft.skf.bridge.s5.lib.impl.SkGatewayGwidConfigs;
import org.toxsoft.uskat.core.ISkCoreApi;
import org.toxsoft.uskat.legacy.plexy.IPlexyType;
import org.toxsoft.uskat.legacy.plexy.IPlexyValue;
import org.toxsoft.uskat.s5.client.remote.connection.S5ConnectionInfo;
import org.toxsoft.uskat.s5.common.S5Host;
import org.toxsoft.uskat.s5.common.S5HostList;
import org.toxsoft.uskat.skadmin.core.IAdminCmdArgDef;
import org.toxsoft.uskat.skadmin.core.IAdminCmdCallback;
import org.toxsoft.uskat.skadmin.core.impl.AbstractAdminCmd;

/**
 * Команда s5admin: добавить конфигурацию моста
 *
 * @author mvk
 */
public class AdminCmdAddConfig
    extends AbstractAdminCmd {

  /**
   * Конструктор
   */
  public AdminCmdAddConfig() {
    // Контекст: API ISkConnection
    addArg( CTX_SK_CORE_API );
    // Идентификатор моста
    addArg( ARG_ID );
    // Имя моста
    addArg( ARG_NAME );
    // Описание моста
    addArg( ARG_DESCR );
    // Хост главного сервера
    addArg( ARG_HOST );
    // Порт главного сервера
    addArg( ARG_PORT );
    // Логин для подключения к главному серверу
    addArg( ARG_LOGIN );
    // Пароль для подключения к главному серверу
    addArg( ARG_PASSW );
    // Конфигурация передачи текущих данных
    addArg( ARG_CURRDATA );
    // Конфигурация передачи хранимых данных
    addArg( ARG_HISTDATA );
    // Конфигурация передачи событий
    addArg( ARG_EVENTS );
    // Конфигурация регистрации исполнителей команд
    addArg( ARG_EXECUTORS );
  }

  // ------------------------------------------------------------------------------------
  // Реализация абстрактных методов AbstractAdminCmd
  //
  @Override
  public String id() {
    return CMD_ADD_CONFIG_ID;
  }

  @Override
  public String alias() {
    return CMD_ADD_CONFIG_ALIAS;
  }

  @Override
  public String nmName() {
    return CMD_ADD_CONFIG_NAME;
  }

  @Override
  public String description() {
    return CMD_ADD_CONFIG_DESCR;
  }

  @Override
  public IPlexyType resultType() {
    return IPlexyType.NONE;
  }

  @Override
  public IStringList roles() {
    return IStringList.EMPTY;
  }

  @Override
  public void doExec( IStringMap<IPlexyValue> aArgValues, IAdminCmdCallback aCallback ) {
    // API сервера
    ISkCoreApi coreApi = argSingleRef( CTX_SK_CORE_API );
    ISkGatewayService service = (ISkGatewayService)coreApi.services().getByKey( ISkGatewayService.SERVICE_ID );

    String id = argSingleValue( ARG_ID ).asString();
    String name = argSingleValue( ARG_NAME ).asString();
    String descr = argSingleValue( ARG_DESCR ).asString();
    String host = argSingleValue( ARG_HOST ).asString();
    int port = argSingleValue( ARG_PORT ).asInt();
    String login = argSingleValue( ARG_LOGIN ).asString();
    String passw = argSingleValue( ARG_PASSW ).asString();
    try {
      long startTime = System.currentTimeMillis();
      // Список конфигураций
      IStridablesListEdit<ISkGatewayConfiguration> configs = new StridablesList<>( service.gatewayConfigs() );
      boolean needUpdate = (configs.findByKey( id ) != null);

      // Формирование конфигурации моста
      S5HostList hosts = new S5HostList();
      hosts.add( new S5Host( host, port ) );
      SkGatewayConfiguration config =
          new SkGatewayConfiguration( id, descr, name, new S5ConnectionInfo( hosts ), new LoginInfo( login, passw ) );
      config.setExportCurrData( readGwidConfigArg( EGwidKind.GW_RTDATA, ARG_CURRDATA ) );
      config.setExportHistData( readGwidConfigArg( EGwidKind.GW_RTDATA, ARG_HISTDATA ) );
      config.setExportEvents( readGwidConfigArg( EGwidKind.GW_EVENT, ARG_EVENTS ) );
      config.setExportCmdExecutors( readGwidConfigArg( EGwidKind.GW_CMD, ARG_EXECUTORS ) );
      // Передача конфигурации на сервер
      service.defineGateway( config );
      // Вывод сообщения
      addResultInfo( needUpdate ? MSG_CMD_UPDATED : MSG_CMD_ADDED );

      long delta = (System.currentTimeMillis() - startTime) / 1000;
      addResultInfo( MSG_CMD_TIME, Long.valueOf( delta ) );
      resultOk();
    }
    finally {
      // nop
    }
  }

  @Override
  protected IList<IPlexyValue> doPossibleValues( String aArgId, IStringMap<IPlexyValue> aArgValues ) {
    return IList.EMPTY;
  }

  // ------------------------------------------------------------------------------------
  // Внутренняя реализация
  //
  /**
   * Читает значение аргумента представляющего {@link ISkGatewayGwidConfigs} в текстовом виде
   *
   * @param aGwidKind {@link EGwidKind} тип конфигурации
   * @param aArg {@link IAdminCmdArgDef} аргумент
   * @return {@link ISkGatewayGwidConfigs} конфигурация
   * @throws TsNullArgumentRtException аргумент = null
   */
  private ISkGatewayGwidConfigs readGwidConfigArg( EGwidKind aGwidKind, IAdminCmdArgDef aArg ) {
    TsNullArgumentRtException.checkNulls( aGwidKind, aArg );
    IAtomicValue av = argSingleValue( aArg );
    if( !av.isAssigned() ) {
      // Значение не указано, установка значения по умолчанию
      return new SkGatewayGwidConfigs( aGwidKind );
    }
    try {
      ISkGatewayGwidConfigs retValue = SkGatewayGwidConfigs.KEEPER.str2ent( av.asString() );
      return retValue;
    }
    catch( Throwable e ) {
      // Ошибка чтения аргумента
      throw new TsIllegalArgumentRtException( ERR_READ_GWID_CONFIG, aArg.id(), e.getLocalizedMessage() );
    }
  }
}
