package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import static com.google.common.collect.Lists.*;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.*;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.stack.core.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.slf4j.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * Панель просмотра списка узлов { @link UaVariableNodeM5Model } <br>
 *
 * @author dima
 */
public class UaVariableNodeListPanel
    extends TsPanel {

  private IM5CollectionPanel<UaVariableNode>     uaVariableNodePanel;
  private OpcUaClient                            client;
  private final Logger                           logger       = LoggerFactory.getLogger( getClass() );
  private final UaVariableNodeM5LifecycleManager lm;
  private UInteger                               clientHandle = null;

  /**
   * Конструктор панели.
   * <p>
   * Конструктор просто запоминает ссылку на контекст, без создания копии.
   *
   * @param aParent {@link Composite} - родительская панель
   * @param aContext {@link ITsGuiContext} - контекст панели
   * @param aOpcUaClient {@link OpcUaClient} - клиент для работы с OPC UA
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public UaVariableNodeListPanel( Composite aParent, ITsGuiContext aContext, OpcUaClient aOpcUaClient ) {
    super( aParent, aContext );
    client = aOpcUaClient;
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    ISkConnection conn = connSup.defConn();
    IM5Domain m5Domain = conn.scope().get( IM5Domain.class );
    // тут получаем KM5 модель UaVariableNode
    IM5Model<UaVariableNode> model = m5Domain.getModel( UaVariableNodeM5Model.MODEL_ID, UaVariableNode.class );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    lm = new UaVariableNodeM5LifecycleManager( model, aOpcUaClient );
    uaVariableNodePanel = model.panelCreator().createCollEditPanel( ctx, lm.itemsProvider(), lm );

    uaVariableNodePanel.createControl( this );

    addListener( SWT.Dispose, e -> {
      if( clientHandle != null ) {
        client.getSubscriptionManager().deleteSubscription( clientHandle );
      }
    } );

  }

  /**
   * @param aNode {@link UaVariableNode} добавить узел свойства которого нужно отобразить
   */
  public void addNode( UaVariableNode aNode ) {
    if( !lm.nodesList().hasElem( aNode ) ) {
      lm.nodesList().add( aNode );
      uaVariableNodePanel.refresh();
      try {
        updateSubscription();
      }
      catch( InterruptedException | ExecutionException ex ) {
        LoggerUtils.errorLogger().error( ex );
      }
    }
    else {
      uaVariableNodePanel.setSelectedItem( aNode );
    }
  }

  /**
   * @param aItem {@link UaMonitoredItem} описание узла на котором изменились данные
   * @param aValue {@link UaVariableNode} заменить узел свойства которого изменились
   */
  public void updateNodeValue( UaMonitoredItem aItem, DataValue aValue ) {
    UaVariableNode uaNode = getNode( aItem );
    if( uaNode != null ) {
      uaNode.setValue( aValue );
      uaVariableNodePanel.refresh();
    }
  }

  private UaVariableNode getNode( UaMonitoredItem aNode ) {
    for( UaVariableNode node : lm.nodesList() ) {
      NodeId nodeId = node.getNodeId();
      ExpandedNodeId expNodeId = aNode.getReadValueId().getNodeId().expanded();
      if( nodeId.equals( expNodeId ) ) {
        return node;
      }
    }
    return null;
  }

  private void updateSubscription()
      throws InterruptedException,
      ExecutionException {
    // отписываемся от старой подписки
    if( clientHandle != null ) {
      client.getSubscriptionManager().deleteSubscription( clientHandle );
    }

    // create a subscription @ 1000ms
    UaSubscription subscription = client.getSubscriptionManager().createSubscription( 1000.0 ).get();

    // create list of requests
    List<MonitoredItemCreateRequest> requestList = new ArrayList<>();

    for( UaVariableNode node : lm.nodesList() ) {
      MonitoredItemCreateRequest request = createMonitoringRequest( node, subscription );
      requestList.add( request );
    }

    // when creating items in MonitoringMode.Reporting this callback is where each item needs to have its
    // value/event consumer hooked up. The alternative is to create the item in sampling mode, hook up the
    // consumer after the creation call completes, and then change the mode for all items to reporting.
    BiConsumer<UaMonitoredItem, Integer> onItemCreated =
        ( item, id ) -> item.setValueConsumer( this::onSubscriptionValue );

    List<UaMonitoredItem> items =
        subscription.createMonitoredItems( TimestampsToReturn.Both, newArrayList( requestList ), onItemCreated ).get();

    for( UaMonitoredItem item : items ) {
      if( item.getStatusCode().isGood() ) {
        logger.info( "item created for nodeId={}", item.getReadValueId().getNodeId() );
      }
      else {
        logger.warn( "failed to create item for nodeId={} (status={})", item.getReadValueId().getNodeId(),
            item.getStatusCode() );
      }
    }
  }

  private MonitoredItemCreateRequest createMonitoringRequest( UaVariableNode aNode, UaSubscription subscription ) {
    // subscribe to the Value attribute of the server's CurrentTime node
    ReadValueId readValueId =
        new ReadValueId( aNode.getNodeId(), AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE );

    // IMPORTANT: client handle must be unique per item within the context of a subscription.
    // You are not required to use the UaSubscription's client handle sequence; it is provided as a convenience.
    // Your application is free to assign client handles by whatever means necessary.
    clientHandle = subscription.nextClientHandle();

    MonitoringParameters parameters = new MonitoringParameters( clientHandle, 1000.0, // sampling interval
        null, // filter, null means use default
        uint( 10 ), // queue size
        true // discard oldest
    );

    MonitoredItemCreateRequest request =
        new MonitoredItemCreateRequest( readValueId, MonitoringMode.Reporting, parameters );
    return request;
  }

  private void onSubscriptionValue( UaMonitoredItem item, DataValue value ) {
    // Работаем НЕ в GUI потоке, а потому...
    Display.getDefault().asyncExec( () -> {
      updateNodeValue( item, value );
      logger.info( "subscription value received: item={}, value={}", item.getReadValueId().getNodeId(),
          value.getValue() );
    } );
  }

}
