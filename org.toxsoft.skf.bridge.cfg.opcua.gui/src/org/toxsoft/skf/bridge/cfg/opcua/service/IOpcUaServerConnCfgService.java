package org.toxsoft.skf.bridge.cfg.opcua.service;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;

/**
 * Service for managing configurations of OPC UA server connection .
 *
 * @author max
 */
public interface IOpcUaServerConnCfgService {

  /**
   * Возвращает запомненные конфигурации.
   *
   * @return {@link IStridablesList}&lt;{@link IOpcUaServerConnCfg}&gt; - список конфигурации
   */
  IStridablesList<IOpcUaServerConnCfg> configs();

  /**
   * Создает конфигурацию с автоматическим назначением идентификатора.
   *
   * @param aParams {@link IOptionSet} - параметры, составляющие конфигурацию
   * @return {@link IOpcUaServerConnCfg} - созданная конфигурация
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  IOpcUaServerConnCfg createCfg( IOptionSet aParams );

  /**
   * Редактирует существующую конфигурацию, заменяя переданные параметры.
   *
   * @param aId String - идентификатор редактируемой конфигурации
   * @param aParams {@link IOptionSet} - параметры, составляющие конфигурацию
   * @return {@link IOpcUaServerConnCfg} - отредатированны экземпляр конфигурация
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  IOpcUaServerConnCfg editCfg( String aId, IOptionSet aParams );

  /**
   * Удаляет конфигурацию.
   * <p>
   * Если такой конфигурации не существует, метод ничего не делает.
   *
   * @param aId String - ИД удаляемой конфигурации
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  void removeCfg( String aId );
}
