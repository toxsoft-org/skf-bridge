package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import org.eclipse.milo.opcua.sdk.client.*;
import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.panels.OpcUaNodesLazySelector.*;

/**
 * Panel to lazy browse sub tree of opc ua server nodes and select list of UaVariableNode .
 *
 * @author dima
 */
public class OpcUaNodesLazySelector
    extends AbstractTsDialogPanel<IList<UaNode>, OpcUaNodesSelectorContext> {

  static class OpcUaNodesSelectorContext {

    private final NodeId      topNode;
    private final OpcUaClient client;
    private final boolean     hideVariableNodes;

    public OpcUaNodesSelectorContext( NodeId aTopNode, OpcUaClient aClient, boolean isHideVariableNodes ) {
      topNode = aTopNode;
      client = aClient;
      hideVariableNodes = isHideVariableNodes;
    }

  }

  private OpcUANodesTreeComposite opcUaNodePanel;

  /**
   * Constructor
   *
   * @param aParent Composite - parent component.
   * @param aEnviroment - enviroment of run.
   */
  protected OpcUaNodesLazySelector( Composite aParent,
      TsDialog<IList<UaNode>, OpcUaNodesSelectorContext> aEnviroment ) {
    super( aParent, aEnviroment );
    this.setLayout( new BorderLayout() );
    createOpcUaNodesTreeViewer( this );

  }

  private void createOpcUaNodesTreeViewer( Composite aParent ) {

    opcUaNodePanel = new OpcUANodesTreeComposite( aParent, tsContext(), environ().client );
    opcUaNodePanel.setRoot( environ().topNode );
  }

  // ------------------------------------------------------------------------------------
  // Реализация интерфейса
  //

  @Override
  protected void doSetDataRecord( IList<UaNode> aData ) {
    // nop
  }

  @Override
  protected IList<UaNode> doGetDataRecord() {
    return opcUaNodePanel.getCheckedNodes();
  }

  // ------------------------------------------------------------------------------------
  // Статический метод вызова диалога
  //

  /**
   * Вызов диалога для выбора узлов OPC UA для создания описания класса
   *
   * @param aTsContext ITsGuiContext - соответствующий контекст
   * @param aTopNode - верхний узел поддерева
   * @param aClient - OPC UA
   * @return IList<UaNode> - список выбранных узлов или <b>null</b> в случае отказа от выбора
   */
  public static IList<UaNode> selectUaNodes4Class( ITsGuiContext aTsContext, NodeId aTopNode, OpcUaClient aClient ) {
    ITsDialogInfo cdi =
        new TsDialogInfo( aTsContext, "Создание класса из дерева узлов OPC UA", "Пометьте нужные узлы и нажмите Ok" );
    OpcUaNodesSelectorContext ctx = new OpcUaNodesSelectorContext( aTopNode, aClient, false );

    IDialogPanelCreator<IList<UaNode>, OpcUaNodesSelectorContext> creator = OpcUaNodesLazySelector::new;
    TsDialog<IList<UaNode>, OpcUaNodesSelectorContext> d = new TsDialog<>( cdi, null, ctx, creator );
    return d.execData();
  }

  /**
   * Вызов диалога для выбора узлов OPC UA для создания объектов
   *
   * @param aTsContext ITsGuiContext - соответствующий контекст
   * @param aTopNode - верхний узел поддерева
   * @param aClient - OPC UA
   * @return IList<UaNode> - список выбранных узлов или <b>null</b> в случае отказа от выбора
   */
  public static IList<UaNode> selectUaNodes4Objects( ITsGuiContext aTsContext, NodeId aTopNode, OpcUaClient aClient ) {
    ITsDialogInfo cdi =
        new TsDialogInfo( aTsContext, "Создание объектов из дерева узлов OPC UA", "Пометьте нужные узлы и нажмите Ok" );
    OpcUaNodesSelectorContext ctx = new OpcUaNodesSelectorContext( aTopNode, aClient, true );

    IDialogPanelCreator<IList<UaNode>, OpcUaNodesSelectorContext> creator = OpcUaNodesLazySelector::new;
    TsDialog<IList<UaNode>, OpcUaNodesSelectorContext> d = new TsDialog<>( cdi, null, ctx, creator );
    return d.execData();
  }

}
