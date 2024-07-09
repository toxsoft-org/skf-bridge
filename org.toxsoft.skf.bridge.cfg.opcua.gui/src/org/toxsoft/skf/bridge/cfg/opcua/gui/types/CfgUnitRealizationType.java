package org.toxsoft.skf.bridge.cfg.opcua.gui.types;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.filegen.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;

/**
 * Type of realization of cfg unit with params defenitions. Implementation.
 *
 * @author max
 */
public class CfgUnitRealizationType
    extends Stridable
    implements ICfgUnitRealizationType {

  private ECfgUnitType              cfgUnitType;
  private IStridablesList<IDataDef> paramDefenitions;
  private IOptionSet                defaultValues;

  /**
   * @param aId String - id of type of realization
   * @param aName String - name of type of realization
   * @param aCfgUnitType ECfgUnitType - type of cfg unit
   * @param aParamDefenitions IList - list of params defenitions
   * @param aDefaultValues IOptionSet
   */
  public CfgUnitRealizationType( String aId, String aName, ECfgUnitType aCfgUnitType, IList<IDataDef> aParamDefenitions,
      IOptionSet aDefaultValues ) {
    super( aId, aName, aName );

    cfgUnitType = aCfgUnitType;
    paramDefenitions = new StridablesList<>( aParamDefenitions );
    defaultValues = aDefaultValues;
  }

  @Override
  public ECfgUnitType cfgUnitType() {
    return cfgUnitType;
  }

  @Override
  public IStridablesList<IDataDef> paramDefenitions() {
    return paramDefenitions;
  }

  @Override
  public IOptionSet getDefaultValues() {
    return defaultValues;
  }

  /**
   * Создаёт начальную конфигурацию узла opc для драйвера
   *
   * @param aNodeId - идентификатор узла
   * @param aNodeIndex - номер узла
   * @param aNodeCount - общее количество узлов
   * @return CfgOpcUaNode - начальная конфигурация, по умолчанию - асинхронный узел Integer на чтение.
   */
  @Override
  public CfgOpcUaNode createInitCfg( ITsGuiContext aContext, String aNodeId, int aNodeIndex, int aNodeCount ) {
    OpcUaServerConnCfg conConf =
        (OpcUaServerConnCfg)aContext.find( OpcToS5DataCfgUnitM5Model.OPCUA_OPC_CONNECTION_CFG );

    EAtomicType type = EAtomicType.NONE;
    if( conConf != null ) {
      type = OpcUaUtils.getValueTypeOfNode( aContext, conConf, aNodeId );
    }
    if( type == null ) {
      type = EAtomicType.NONE;
    }

    return new CfgOpcUaNode( aNodeId, false, true, false, type );
  }
}
