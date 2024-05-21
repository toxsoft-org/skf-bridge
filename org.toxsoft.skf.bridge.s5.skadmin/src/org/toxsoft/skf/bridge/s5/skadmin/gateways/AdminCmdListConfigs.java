package org.toxsoft.skf.bridge.s5.skadmin.gateways;

import static org.toxsoft.skf.bridge.s5.skadmin.gateways.IAdminHardConstants.*;
import static org.toxsoft.skf.bridge.s5.skadmin.gateways.IAdminHardResources.*;
import static org.toxsoft.uskat.skadmin.core.EAdminCmdContextNames.*;

import org.toxsoft.core.tslib.bricks.strid.coll.IStridablesList;
import org.toxsoft.core.tslib.coll.IList;
import org.toxsoft.core.tslib.coll.primtypes.IStringList;
import org.toxsoft.core.tslib.coll.primtypes.IStringMap;
import org.toxsoft.skf.bridge.s5.lib.ISkGatewayConfiguration;
import org.toxsoft.skf.bridge.s5.lib.ISkGatewayService;
import org.toxsoft.skf.bridge.s5.lib.impl.SkGatewayGwidConfigs;
import org.toxsoft.uskat.core.ISkCoreApi;
import org.toxsoft.uskat.legacy.plexy.IPlexyType;
import org.toxsoft.uskat.legacy.plexy.IPlexyValue;
import org.toxsoft.uskat.s5.client.remote.connection.IS5ConnectionInfo;
import org.toxsoft.uskat.skadmin.core.IAdminCmdCallback;
import org.toxsoft.uskat.skadmin.core.impl.AbstractAdminCmd;

/**
 * Команда s5admin: вывод текущих конфигураций мостов
 *
 * @author mvk
 */
public class AdminCmdListConfigs
    extends AbstractAdminCmd {

  /**
   * Конструктор
   */
  public AdminCmdListConfigs() {
    // Контекст: API ISkConnection
    addArg( CTX_SK_CORE_API );
  }

  // ------------------------------------------------------------------------------------
  // Реализация абстрактных методов AbstractAdminCmd
  //
  @Override
  public String id() {
    return CMD_LIST_CONFIGS_ID;
  }

  @Override
  public String alias() {
    return CMD_LIST_CONFIGS_ALIAS;
  }

  @Override
  public String nmName() {
    return CMD_LIST_CONFIGS_NAME;
  }

  @Override
  public String description() {
    return CMD_LIST_CONFIGS_DESCR;
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
    try {
      long startTime = System.currentTimeMillis();
      // Список конфигураций
      IStridablesList<ISkGatewayConfiguration> configs = service.gatewayConfigs();
      // Вывод конфигурации мостов
      for( ISkGatewayConfiguration config : configs ) {
        printConfig( config );
      }
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
  private void printConfig( ISkGatewayConfiguration aConfig ) {
    addResultInfo( MSG_CONFIG_LINE );
    String id = aConfig.id();
    String name = aConfig.nmName();
    String descr = aConfig.description();
    String login = aConfig.loginInfo().login();
    IS5ConnectionInfo address = aConfig.connectionInfo();
    String currdata = SkGatewayGwidConfigs.KEEPER.ent2str( aConfig.exportCurrData() );
    String histdata = SkGatewayGwidConfigs.KEEPER.ent2str( aConfig.exportHistData() );
    String events = SkGatewayGwidConfigs.KEEPER.ent2str( aConfig.exportEvents() );
    String executors = SkGatewayGwidConfigs.KEEPER.ent2str( aConfig.exportCmdExecutors() );
    addResultInfo( MSG_CONFIG, id, name, descr, login, address, currdata, histdata, events, executors );
  }
}
