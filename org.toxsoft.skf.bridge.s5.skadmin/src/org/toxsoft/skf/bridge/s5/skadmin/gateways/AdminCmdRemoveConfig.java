package org.toxsoft.skf.bridge.s5.skadmin.gateways;

import static org.toxsoft.skf.bridge.s5.skadmin.gateways.IAdminHardConstants.*;
import static org.toxsoft.skf.bridge.s5.skadmin.gateways.IAdminHardResources.*;
import static org.toxsoft.uskat.skadmin.core.EAdminCmdContextNames.*;

import org.toxsoft.core.tslib.bricks.strid.coll.IStridablesListEdit;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.StridablesList;
import org.toxsoft.core.tslib.bricks.validator.ValidationResult;
import org.toxsoft.core.tslib.coll.IList;
import org.toxsoft.core.tslib.coll.primtypes.IStringList;
import org.toxsoft.core.tslib.coll.primtypes.IStringMap;
import org.toxsoft.skf.bridge.s5.lib.ISkGatewayInfo;
import org.toxsoft.skf.bridge.s5.lib.ISkGatewayService;
import org.toxsoft.uskat.core.ISkCoreApi;
import org.toxsoft.uskat.legacy.plexy.IPlexyType;
import org.toxsoft.uskat.legacy.plexy.IPlexyValue;
import org.toxsoft.uskat.skadmin.core.IAdminCmdCallback;
import org.toxsoft.uskat.skadmin.core.impl.AbstractAdminCmd;

/**
 * Команда s5admin: удаление конфигурации моста
 *
 * @author mvk
 */
public class AdminCmdRemoveConfig
    extends AbstractAdminCmd {

  /**
   * Конструктор
   */
  public AdminCmdRemoveConfig() {
    // Контекст: API ISkConnection
    addArg( CTX_SK_CORE_API );
    // Идентификатор моста
    addArg( ARG_ID );
    // Режим обработки запросов системы
    addArg( ARG_YES_ID );
  }

  // ------------------------------------------------------------------------------------
  // Реализация абстрактных методов AbstractAdminCmd
  //
  @Override
  public String id() {
    return CMD_REMOVE_CONFIG_ID;
  }

  @Override
  public String alias() {
    return CMD_REMOVE_CONFIG_ALIAS;
  }

  @Override
  public String nmName() {
    return CMD_REMOVE_CONFIG_NAME;
  }

  @Override
  public String description() {
    return CMD_REMOVE_CONFIG_DESCR;
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
    boolean yes = argSingleValue( ARG_YES_ID ).asBool();

    // Все готово для импорта объектов. Последнее "китайское" предупреждение
    if( !yes && !queryClientConfirm( ValidationResult.warn( MSG_CMD_CONFIRM_REMOVE ), false ) ) {
      // Клиент отказался от продолжения
      addResultInfo( MSG_CMD_REJECT, id() );
      resultFail();
      return;
    }
    try {
      long startTime = System.currentTimeMillis();
      // Список конфигураций
      IStridablesListEdit<ISkGatewayInfo> configs = new StridablesList<>( service.gatewayConfigs() );
      service.removeGateway( id );
      boolean success = (configs.removeById( id ) != null);
      addResultInfo( success ? MSG_CMD_REMOVED : MSG_CMD_NOT_FOUND );

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
}
