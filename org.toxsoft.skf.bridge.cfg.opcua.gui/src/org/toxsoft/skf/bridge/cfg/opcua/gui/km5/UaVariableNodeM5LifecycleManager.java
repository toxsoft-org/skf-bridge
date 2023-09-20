package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import java.util.*;
import java.util.concurrent.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.core.log4j.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.utils.logs.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;

//import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList;

/**
 * Lifecycle Manager of {@link UaVariableNode} entities.
 *
 * @author dima
 */
public class UaVariableNodeM5LifecycleManager
    extends M5LifecycleManager<UaVariableNode, OpcUaClient> {

  private IListEdit<UaVariableNode> nodesList = new ElemArrayList<>();

  /**
   * Журнал работы
   */
  private ILogger logger = LoggerWrapper.getLogger( this.getClass().getName() );

  /**
   * Constructor by M5 model and service
   *
   * @param aModel IM5Model - model
   * @param aClient OpcUaClient - opc ua client
   */
  public UaVariableNodeM5LifecycleManager( IM5Model<UaVariableNode> aModel, OpcUaClient aClient ) {
    super( aModel, false, true, true, true, aClient );
  }

  @Override
  protected UaVariableNode doEdit( IM5Bunch<UaVariableNode> aValues ) {
    UaVariableNode original = aValues.originalEntity();
    // получаем новое значение
    String newVal = aValues.getAsAv( UaVariableNodeM5Model.FID_VALUE ).asString();

    // устанавливаем его
    List<NodeId> nodeIds = ImmutableList.of( aValues.originalEntity().getNodeId() );
    Class<?> clazz = OpcUaUtils.getNodeDataTypeClass( original );
    Variant v = OpcUaUtils.getVariant( clazz, newVal );

    // don't write status or timestamps
    DataValue dv = new DataValue( v, null, null );

    // write asynchronously....
    CompletableFuture<List<StatusCode>> f = master().writeValues( nodeIds, ImmutableList.of( dv ) );

    // ...but block for the results so we write in order
    List<StatusCode> statusCodes;
    try {
      statusCodes = f.get();
      StatusCode status = statusCodes.get( 0 );

      if( status.isGood() ) {
        logger.info( "Wrote '{}' to nodeId={}", v, nodeIds.get( 0 ) ); //$NON-NLS-1$
        dv = new DataValue( v, status, new DateTime() );
        original.setValue( dv );
      }
    }
    catch( InterruptedException | ExecutionException ex ) {
      LoggerUtils.errorLogger().error( ex );
    }
    return original;
  }

  @Override
  protected void doRemove( UaVariableNode aEntity ) {
    nodesList.remove( aEntity );
    // TODO dima 04.08.23 по хорошему надо еще обновить подписку чтобы не слушать те узлы которые удалены
  }

  @Override
  protected IList<UaVariableNode> doListEntities() {
    if( !nodesList.isEmpty() ) {
      return nodesList;
    }
    return IList.EMPTY;
  }

  /**
   * @return список инспектируемых узлов
   */
  public IListEdit<UaVariableNode> nodesList() {
    return nodesList;
  }
}
