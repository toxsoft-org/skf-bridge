package org.toxsoft.skf.bridge.cfg.opcua.gui.panels;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.util.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.stdevents.*;
import org.toxsoft.core.tsgui.bricks.stdevents.impl.*;
import org.toxsoft.core.tsgui.bricks.tsnodes.*;
import org.toxsoft.core.tsgui.bricks.tstree.*;
import org.toxsoft.core.tsgui.bricks.tstree.impl.*;
import org.toxsoft.core.tsgui.bricks.uievents.*;
import org.toxsoft.core.tsgui.graphics.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.graphics.image.*;
import org.toxsoft.core.tsgui.utils.*;
import org.toxsoft.core.tsgui.utils.jface.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.events.change.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.basis.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * Реализация {@link ITsTreeViewer} чек-дерева узлов OPC UA.
 *
 * @author max
 * @author dima // адаптация к функционалу выбора узла/ов OPC UA
 */
public class OpcUANodesTreeViewer
    implements ITsTreeViewer, ICheckStateListener, ControlListener, ITreeViewerListener {

  /**
   * Расширение отображения для дополнительной подсветки серых чекбоксов (из-за того что в некоторых операционках серые
   * чекбоксы некорректно отрабатываются)
   *
   * @author max
   */
  class InternalLabelProvider
      extends TsTreeLabelProvider {

    @Override
    protected String doGetColumnText( ITsNode aNode, int aColumnIndex ) {

      if( getGrayed( aNode ) ) {
        return "*" + aNode.name(); //$NON-NLS-1$
      }
      return aNode.name();
    }

  }

  private final ISelectionChangedListener treeSelectionChangedListener = new ISelectionChangedListener() {

    @Override
    public void selectionChanged( SelectionChangedEvent aEvent ) {

      selectionChangeEventHelper.fireTsSelectionEvent( selectedItem() );
    }
  };

  private final IDoubleClickListener treeDoubleClickListener = new IDoubleClickListener() {

    @Override
    public void doubleClick( DoubleClickEvent aEvent ) {
      doubleClickEventHelper.fireTsDoublcClickEvent( selectedItem() );
    }
  };

  final IOptionSetEdit                        params          = new OptionSet();
  final TsSelectionChangeEventHelper<ITsNode> selectionChangeEventHelper;
  final TsDoubleClickEventHelper<ITsNode>     doubleClickEventHelper;
  private final ITsGuiContext                 context;
  private final TsTreeContentProvider         contentProvider = new TsTreeContentProvider();
  private final InternalLabelProvider         labelProvider   = new InternalLabelProvider();
  private final IListEdit<ITsNode>            rootNodes       = new ElemLinkedBundleList<>();
  private final IListEdit<ITsViewerColumn>    columns         = new ElemLinkedBundleList<>();
  private ViewerPaintHelper<Tree>             paintHelper     = null;
  private EIconSize                           iconSize        = EIconSize.IS_16X16;
  /**
   * Чек-дерева.
   */
  private CheckboxTreeViewer                  treeViewer      = null;

  private ITsTreeViewerConsole console = null;

  /**
   * Модель зачеканных узлов дерева.
   */
  private Map<ITsNode, Boolean> checkedNodes = new HashMap<>();

  /**
   * Модель серых узлов дерева.
   */
  private Map<ITsNode, Boolean> grayedNodes = new HashMap<>();

  /**
   * Слушатели событий изменения состояния выбора узлов дерева.
   */
  private ListenerList<ICheckStateListener> checkStateListeners = new ListenerList<>();

  /**
   * Конструктор дерева с указанием контекста.
   *
   * @param aContext {@link ITsGuiContext} - контекст панели
   * @throws TsNullArgumentRtException аргумент = null
   */
  public OpcUANodesTreeViewer( ITsGuiContext aContext ) {
    TsNullArgumentRtException.checkNull( aContext );
    context = aContext;
    selectionChangeEventHelper = new TsSelectionChangeEventHelper<>( this );
    doubleClickEventHelper = new TsDoubleClickEventHelper<>( this );
  }

  // ------------------------------------------------------------------------------------
  // Реализация интерфейса ILazyControl
  //

  @Override
  public Control createControl( Composite aParent ) {
    treeViewer =
        new CheckboxTreeViewer( aParent, SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION ) {

          @Override
          public boolean getExpandedState( Object aElementOrTreePath ) {
            if( aElementOrTreePath == root() ) {
              return true;
            }
            return super.getExpandedState( aElementOrTreePath );
          }

        };
    // console = new TsTreeViewerConsole( treeViewer );
    treeViewer.setContentProvider( contentProvider );
    treeViewer.setLabelProvider( labelProvider );
    treeViewer.addSelectionChangedListener( treeSelectionChangedListener );
    treeViewer.addDoubleClickListener( treeDoubleClickListener );
    treeViewer.addCheckStateListener( this );

    // treeViewer.getTree().setHeaderVisible( true );
    treeViewer.getTree().setLinesVisible( true );
    treeViewer.setInput( this );

    TreeViewerColumn tvCol = new TreeViewerColumn( treeViewer, SWT.CENTER, 0 );
    treeViewer.setLabelProvider( labelProvider ); // refresh column cells label provider

    tvCol.getColumn().setText( TsLibUtils.EMPTY_STRING );
    tvCol.getColumn().setAlignment( SWT.CENTER );
    tvCol.getColumn().setWidth( 150 );

    treeViewer.getControl().addControlListener( this );

    treeViewer.addTreeListener( this );

    return treeViewer.getControl();
  }

  @Override
  public Control getControl() {
    if( treeViewer == null ) {
      return null;
    }
    return treeViewer.getControl();
  }

  // ------------------------------------------------------------------------------------
  // Реализация интерфейса IParameterizedEdit
  //

  @Override
  public IOptionSetEdit params() {
    return params;
  }

  // ------------------------------------------------------------------------------------
  // Реализация интерфейса ITsSelectionProvider
  //

  @Override
  public ITsNode selectedItem() {
    IStructuredSelection ss = (IStructuredSelection)treeViewer.getSelection();
    if( ss.isEmpty() ) {
      return null;
    }
    return (ITsNode)ss.getFirstElement();
  }

  @Override
  public void setSelectedItem( ITsNode aItem ) {
    IStructuredSelection selection = StructuredSelection.EMPTY;
    if( aItem != null ) {
      selection = new StructuredSelection( aItem );
    }
    treeViewer.setSelection( selection, true );
  }

  // ------------------------------------------------------------------------------------
  // Реализация интерфейса ITsDoubleClickEventProducer
  //

  @Override
  public void addTsDoubleClickListener( ITsDoubleClickListener<ITsNode> aListener ) {
    doubleClickEventHelper.addTsDoubleClickListener( aListener );
  }

  @Override
  public void removeTsDoubleClickListener( ITsDoubleClickListener<ITsNode> aListener ) {
    doubleClickEventHelper.removeTsDoubleClickListener( aListener );
  }

  // ------------------------------------------------------------------------------------
  // Реализация интерфейса ITsNode
  //

  @Override
  public ITsGuiContext context() {
    return context;
  }

  @Override
  public ITsTreeViewer root() {
    return this;
  }

  @Override
  public ITsNodeKind<?> kind() {
    return TREE_KIND;
  }

  @Override
  public String name() {
    return TsLibUtils.EMPTY_STRING;
  }

  @Override
  public Object entity() {
    return null;
  }

  @Override
  public ITsNode parent() {
    return null;
  }

  @Override
  public IList<ITsNode> childs() {
    return rootNodes;
  }

  @Override
  public void rebuildSubtree( boolean aRebuild, boolean aQuerySubtree ) {
    for( ITsNode root : rootNodes ) {
      root.rebuildSubtree( aRebuild, aQuerySubtree );
    }
  }

  // ------------------------------------------------------------------------------------
  // Реализация интерфейса ITsTreeViewer
  //

  @Override
  public void addTsSelectionListener( ITsSelectionChangeListener<ITsNode> aListener ) {
    selectionChangeEventHelper.addTsSelectionListener( aListener );
  }

  @Override
  public void removeTsSelectionListener( ITsSelectionChangeListener<ITsNode> aListener ) {
    selectionChangeEventHelper.removeTsSelectionListener( aListener );
  }

  @Override
  public IList<ITsViewerColumn> columns() {
    return columns;
  }

  @Override
  public ITsViewerColumn addColumn( String aTitle, EHorAlignment aAlignment,
      ITsVisualsProvider<ITsNode> aNameProvider ) {
    // сейчас не реализовано
    return null;
  }

  @Override
  public void removeColumns() {
    //
  }

  @Override
  public void setRootNodes( ITsCollection<ITsNode> aRootNodes ) {

    Object[] expanded = treeViewer.getExpandedElements();

    rootNodes.clear();
    if( aRootNodes != null ) {
      for( ITsNode node : aRootNodes ) {
        TsIllegalArgumentRtException.checkTrue( node.parent() != this );
      }
      rootNodes.setAll( aRootNodes );
      rebuildSubtree( false, false );
    }

    // TODO - проверять новую структуру дерева с сохранёнными экспад узлами и модель чек и грей синхронизировать
    grayedNodes.clear();
    Map<ITsNode, Boolean> newCheckedNodes = new HashMap<>();

    IList<ITsNode> allNodes = getAllNodes();

    for( ITsNode node : allNodes ) {
      Boolean isNodeChecked = checkedNodes.get( node );
      if( isNodeChecked != null ) {
        newCheckedNodes.put( node, isNodeChecked );
      }
    }
    checkedNodes = newCheckedNodes;

    ensureGrayedState();
    treeViewer.refresh();

    treeViewer.setExpandedElements( expanded );

    fireCommonCheckStateChanged();
  }

  @Override
  public void clear() {
    setRootNodes( IList.EMPTY );
  }

  @Override
  public ITsNode findByEntity( Object aEntity, boolean aQuerySubtree ) {
    for( ITsNode root : rootNodes ) {
      ITsNode node = root.findByEntity( aEntity, aQuerySubtree );
      if( node != null ) {
        return node;
      }
    }
    return null;
  }

  @Override
  public ITsTreeViewerConsole console() {
    TsIllegalStateRtException.checkNull( console );
    return console;
  }

  @Override
  public IIntList getSelectedPath() {
    ITsNode sel = selectedItem();
    if( sel == null ) {
      return IIntList.EMPTY;
    }
    ITsNode parent = sel.parent();
    IIntListEdit result = new IntArrayList();
    while( parent != null ) {
      int index = parent.childs().indexOf( sel );
      result.insert( 0, index );
      sel = parent;
      parent = sel.parent();
    }
    // теперь осталось добавить индекс среди корневых узлов
    int index = rootNodes.indexOf( sel );
    result.insert( 0, index );
    return result;
  }

  @Override
  public void setSelectedPath( IIntList aPathIndexes ) {
    TsNullArgumentRtException.checkNull( aPathIndexes );
    if( aPathIndexes.isEmpty() ) {
      setSelectedItem( null );
      return;
    }
    int index = aPathIndexes.getValue( 0 );
    if( index < 0 || index >= rootNodes.size() ) {
      setSelectedItem( null );
      return;
    }
    ITsNode sel = rootNodes.get( index );

    for( int i = 1, count = aPathIndexes.size(); i < count; i++ ) {
      IList<ITsNode> list = sel.childs();
      index = aPathIndexes.getValue( i );
      if( index < 0 || index >= list.size() ) {
        break;
      }
      sel = list.get( index );
    }
    setSelectedItem( sel );
  }

  @Override
  public void setTreePaintHelper( ViewerPaintHelper<Tree> aHelper ) {
    if( aHelper == null ) {
      if( paintHelper != null ) {
        paintHelper.deinstall();
        paintHelper = null;
        treeViewer.refresh();
      }
      return;
    }
    aHelper.install( treeViewer.getTree() );
    paintHelper = aHelper;
    treeViewer.refresh();
  }

  /**
   * Задает поставщика шрифта для ячеек таблицы или null, если используются только шрифты по умолчанию.
   * <p>
   * Внимание: после этого метода следует обновить отрисовщики ячеек вызовом
   * {@link ColumnViewer#setLabelProvider(IBaseLabelProvider) ColumnViewer.setLabelProvider(this)}.
   *
   * @param aFontProvider {@link ITableFontProvider} - поставщик шрифтов ячеек или null
   */
  @Override
  public void setFontProvider( ITableFontProvider aFontProvider ) {
    labelProvider.setFontProvider( aFontProvider );
    treeViewer.setLabelProvider( labelProvider );
  }

  /**
   * Задает поставщика цветов для ячеек таблицы или null, если используются только цвета по умолчанию.
   * <p>
   * Внимание: после этого метода следует обновить отрисовщики ячеек вызовом
   * {@link ColumnViewer#setLabelProvider(IBaseLabelProvider) ColumnViewer.setLabelProvider(this)}.
   *
   * @param aColorProvider {@link ITableColorProvider} - поставщик цветов ячеек или null
   */
  @Override
  public void setColorProvider( ITableColorProvider aColorProvider ) {
    labelProvider.setColorProvider( aColorProvider );
    treeViewer.setLabelProvider( labelProvider );
  }

  @Override
  public EIconSize iconSize() {
    return iconSize;
  }

  @Override
  public void setIconSize( EIconSize aIconSize ) {
    iconSize = aIconSize;
  }

  // ------------------------------------------------------------------------------------
  // API класса по работе с чек и грей
  //

  //
  // -------------------------------------------------------------------------
  // Методы подмены функционала сохранения галочек и грея в дереве (сохранение в модели, отметка в отображаемом дереве -
  // по раскрытию узлов) - эти методы есть в дереве, но в нём напрямую не вызывать.

  /**
   * Возвращает состояние чек-бокса выбранного узла
   *
   * @param aNode ITsNode - узел дерева
   * @return boolean - состояние чек-бокса.
   */
  public boolean getChecked( ITsNode aNode ) {
    Boolean result = checkedNodes.get( aNode );
    return result == null ? false : result.booleanValue();
  }

  /**
   * Устанавливает состояние чек-бокса выбранного узла.
   *
   * @param aNode ITsNode - узел дерева
   * @param aChecked boolean - новое состояние чек-бокса.
   */
  public void setChecked( ITsNode aNode, boolean aChecked ) {
    checkedNodes.put( aNode, Boolean.valueOf( aChecked ) );
    if( aNode.parent() == null || treeViewer.getExpandedState( aNode.parent() ) ) {
      treeViewer.setChecked( aNode, aChecked );
    }
  }

  /**
   * Устанавливает состояние чек-боксов всех дочерних узлов во всех дочерних ветвях дерева выбранного узла.
   *
   * @param aNode ITsNode - узел дерева (корень подветвей)
   * @param aChecked boolean - новое состояние чек-боксов.
   */
  public void setSubtreeChecked( ITsNode aNode, boolean aChecked ) {
    checkedNodes.put( aNode, Boolean.valueOf( aChecked ) );
    if( aNode.parent() == null || treeViewer.getExpandedState( aNode.parent() ) ) {
      treeViewer.setChecked( aNode, aChecked );
    }

    for( ITsNode child : aNode.childs() ) {
      setSubtreeChecked( child, aChecked );
    }
  }

  /**
   * Только на уровне модели (не влезая в дерево) Устанавливает состояние чек-боксов всех дочерних узлов во всех
   * дочерних ветвях дерева выбранного узла.
   *
   * @param aNode ITsNode - узел дерева (корень подветвей)
   * @param aChecked boolean - новое состояние чек-боксов.
   */
  public void setSubtreeCheckedOnlyModel( ITsNode aNode, boolean aChecked ) {
    checkedNodes.put( aNode, Boolean.valueOf( aChecked ) );

    for( ITsNode child : aNode.childs() ) {
      setSubtreeCheckedOnlyModel( child, aChecked );
    }
  }

  /**
   * Возвращает массив узлов, состояние чек-боксов которых = true.
   *
   * @return ITsNode[] - массив узлов, состояние чек-боксов которых = true.
   */
  public ITsNode[] getCheckedElements() {
    IListEdit<ITsNode> checkedElements = new ElemArrayList<>();

    for( ITsNode node : checkedNodes.keySet() ) {
      if( checkedNodes.get( node ) != null && checkedNodes.get( node ).booleanValue() ) {
        checkedElements.add( node );
      }
    }

    return checkedElements.toArray( new ITsNode[0] );
  }

  /**
   * Устанавливает закрашенность серым чек-бокса выбранного узла.
   *
   * @param aNode ITsNode - узел дерева
   * @param aChecked boolean - новое состояние закрашенности серым чек-бокса.
   */
  public void setGrayed( ITsNode aNode, boolean aChecked ) {
    grayedNodes.put( aNode, Boolean.valueOf( aChecked ) );
    if( aNode.parent() == null || treeViewer.getExpandedState( aNode.parent() ) ) {
      treeViewer.setGrayed( aNode, aChecked );
    }
  }

  /**
   * Возвращает состояние закрашенности серым чек-бокса выбранного узла
   *
   * @param aNode ITsNode - узел дерева
   * @return boolean - состояние закрашенности серым чек-бокса.
   */
  public boolean getGrayed( ITsNode aNode ) {
    Boolean result = grayedNodes.get( aNode );
    return result == null ? false : result.booleanValue();
  }

  // Окончание методов подмены
  // -------------------------------------------------------------------------
  //

  /**
   * Поставить галочки в чек-боксах всех узлов дерева на низком уровне.
   */
  public void checkAllByApparat() {
    ITsNode root = root();

    TreeItem[] items = treeViewer.getTree().getItems();

    for( TreeItem item : items ) {
      item.setChecked( true );
    }

    for( ITsNode subRoot : root.childs() ) {
      setSubtreeCheckedOnlyModel( subRoot, true );
      setSubtreeUngrayed( subRoot );
    }
    treeViewer.refresh();

    CheckStateChangedEvent event = new CheckStateChangedEvent( treeViewer, getCheckedElements(), true );
    fireCheckStateChanged( event );
  }

  /**
   * Убрать галочки в чек-боксах всех узлов дерева на низком уровне.
   */
  public void uncheckAllByApparat() {
    ITsNode root = root();

    TreeItem[] items = treeViewer.getTree().getItems();

    for( TreeItem item : items ) {
      item.setChecked( false );
    }

    for( ITsNode subRoot : root.childs() ) {
      setSubtreeCheckedOnlyModel( subRoot, false );
      setSubtreeUngrayed( subRoot );
    }
    treeViewer.refresh();

    CheckStateChangedEvent event = new CheckStateChangedEvent( treeViewer, getCheckedElements(), false );
    fireCheckStateChanged( event );
  }

  /**
   * Поставить галочки в чек-боксах всех узлов дерева.
   */
  public void checkAll() {
    ITsNode root = root();

    for( ITsNode subRoot : root.childs() ) {
      setSubtreeChecked( subRoot, true );
      setSubtreeUngrayed( subRoot );
    }
    treeViewer.refresh();

    CheckStateChangedEvent event = new CheckStateChangedEvent( treeViewer, getCheckedElements(), true );
    fireCheckStateChanged( event );
  }

  /**
   * Убрать галочки в чек-боксах всех узлов дерева.
   */
  public void uncheckAll() {
    ITsNode root = root();

    for( ITsNode subRoot : root.childs() ) {

      setSubtreeChecked( subRoot, false );
      setSubtreeUngrayed( subRoot );
    }
    treeViewer.refresh();

    CheckStateChangedEvent event = new CheckStateChangedEvent( treeViewer, getCheckedElements(), false );
    fireCheckStateChanged( event );
  }

  /**
   * Привести в соответствие с реальным положением дел состояния чек-боксов всех родительских узлов отностиельно
   * указанного узла
   *
   * @param aChecked ITsNode - узел, состояния родительских узлов которого приводится в соответствие.
   */
  public void ensureParentChecked( ITsNode aChecked ) {
    ITsNode parent = aChecked.parent();
    while( parent != null ) {

      boolean hasChecked = false;
      // boolean allChecked = false;

      for( ITsNode child : parent.childs() ) {
        if( getChecked( child ) ) {
          hasChecked = true;
        }
        else {
          // allChecked = false;
        }
      }

      setChecked( parent, hasChecked );

      parent = parent.parent();
    }
  }

  /**
   * Привести в соответствие с реальным положением дел закрашенность серым чек-боксов всех родительских узлов
   * отностиельно указанного узла
   *
   * @param aChecked ITsNode - узел, закрашенность серым родительских узлов которого приводится в соответствие.
   */
  public void ensureParentGrayed( ITsNode aChecked ) {
    ITsNode parent = aChecked.parent();

    while( parent != null ) {

      boolean allChecked = true;
      // boolean allChecked = false;

      for( ITsNode child : parent.childs() ) {
        if( !getChecked( child ) ) {
          allChecked = false;
          break;
        }
        if( getGrayed( child ) ) {
          allChecked = false;
          break;
        }
      }

      setGrayed( parent, !allChecked );

      parent = parent.parent();
    }
    treeViewer.refresh();
  }

  /**
   * Привести в соответствие с реальным положением дел закрашенность серым чек-боксов всех узлов дерева
   */
  public void ensureGrayedState() {
    IList<ITsNode> roots = root().childs();

    for( ITsNode rootNode : roots ) {
      ensureGrayedState( rootNode );
    }

    treeViewer.refresh();
  }

  // ------------------------------------------------------------------------------------
  // Вспомогательные по работе с чек и грей
  //

  private void ensureGrayedState( ITsNode aNode ) {
    if( aNode.childs().size() == 0 ) {
      setGrayed( aNode, false );
      return;
    }
    if( !getChecked( aNode ) ) {
      setGrayed( aNode, false );
      return;
    }

    for( ITsNode child : aNode.childs() ) {
      ensureGrayedState( child );
    }
    boolean toBeGrayed = false;
    for( ITsNode child : aNode.childs() ) {
      if( !getChecked( child ) ) {
        toBeGrayed = true;
        break;
      }
      if( getGrayed( child ) ) {
        toBeGrayed = true;
        break;
      }
    }
    setGrayed( aNode, toBeGrayed );
  }

  private void setSubtreeUngrayed( ITsNode aNode ) {
    setGrayed( aNode, false );
    for( ITsNode child : aNode.childs() ) {
      setSubtreeUngrayed( child );
    }
  }

  private IList<ITsNode> getAllNodes() {
    IListEdit<ITsNode> nodes = new ElemArrayList<>();

    for( ITsNode node : rootNodes ) {
      // addNodeAndChildren( nodes, node );
      addNode( nodes, node );
    }
    return nodes;
  }

  // private void addNodeAndChildren( IListEdit<ITsNode> aNodesCollection, ITsNode aNode ) {
  // aNodesCollection.add( aNode );
  // for( ITsNode child : aNode.childs() ) {
  // addNodeAndChildren( aNodesCollection, child );
  // }
  // }

  private void addNode( IListEdit<ITsNode> aNodesCollection, ITsNode aNode ) {
    aNodesCollection.add( aNode );
  }

  //
  // -----------------------------------------------------------------------
  // API добавления и удаления слушателей событий установки чек-боксов

  /**
   * Добавляет слушателя событий изменения состояния выбора узлов дерева.
   *
   * @param listener ICheckStateListener - слушатель.
   */
  public void addCheckStateListener( ICheckStateListener listener ) {
    checkStateListeners.add( listener );
  }

  /**
   * Удаляет слушателя событий изменения состояния выбора узлов дерева.
   *
   * @param listener ICheckStateListener - слушатель.
   */
  public void removeCheckStateListener( ICheckStateListener listener ) {
    checkStateListeners.remove( listener );
  }

  //
  // -------------------------------------------------------------
  // ICheckStateListener

  @Override
  public void checkStateChanged( CheckStateChangedEvent event ) {
    setSubtreeChecked( (ITsNode)event.getElement(), event.getChecked() );
    ITsNode element = (ITsNode)event.getElement();

    setSubtreeUngrayed( element );

    ensureParentChecked( element );
    ensureParentGrayed( element );

    fireCheckStateChanged( event );
  }

  //
  // --------------------------------------------------------------------------
  // методы отправки событий

  protected void fireCommonCheckStateChanged() {
    CheckStateChangedEvent event = new CheckStateChangedEvent( treeViewer, getCheckedElements(), true );
    fireCheckStateChanged( event );
  }

  protected void fireCheckStateChanged( final CheckStateChangedEvent event ) {
    Object[] array = checkStateListeners.getListeners();
    for( int i = 0; i < array.length; i++ ) {
      final ICheckStateListener l = (ICheckStateListener)array[i];
      SafeRunnable.run( new SafeRunnable() {

        @Override
        public void run() {
          l.checkStateChanged( event );
        }
      } );
    }
  }

  //
  // ----------------------------------------------------------------------------------
  // ControlListener

  @Override
  public void controlMoved( ControlEvent e ) {
    // нет реализации
  }

  /**
   * Попытка конролировать ширину колонки.
   */
  @Override
  public void controlResized( ControlEvent e ) {
    ((Tree)getControl()).getColumn( 0 ).setWidth( getControl().getSize().x );
    // availableDataParamsTree.columns().get( 0 ).setWidth( availableDataParamsTree.getControl().getSize().x );
  }

  //
  // --------------------------------------------------------
  // ITreeViewerListener

  @Override
  public void treeCollapsed( TreeExpansionEvent aArg0 ) {
    // нет реализации
  }

  /**
   * В соответствии с моделью - во время раскрывания ветви дерева - выделить соответствующие подузлы (чек и грей).
   */
  @Override
  public void treeExpanded( TreeExpansionEvent aArg0 ) {
    ITsNode expanded = (ITsNode)aArg0.getElement();

    for( ITsNode child : expanded.childs() ) {
      Boolean checked = checkedNodes.get( child );
      boolean isChecked = checked == null ? false : checked.booleanValue();
      treeViewer.setChecked( child, isChecked );

      Boolean grayed = grayedNodes.get( child );
      boolean isGrayed = grayed == null ? false : grayed.booleanValue();
      treeViewer.setGrayed( child, isGrayed );
    }

  }

  @Override
  public IList<ITsNode> listExistingChilds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String iconId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addTsKeyInputListener( ITsKeyInputListener aListener ) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeTsKeyInputListener( ITsKeyInputListener aListener ) {
    // TODO Auto-generated method stub

  }

  @Override
  public Image getIcon( EIconSize aIconSize ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IGenericChangeEventer iconSizeChangeEventer() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EIconSize defaultIconSize() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IGenericChangeEventer thumbSizeEventer() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EThumbSize thumbSize() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setThumbSize( EThumbSize aThumbSize ) {
    // TODO Auto-generated method stub

  }

  @Override
  public EThumbSize defaultThumbSize() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ITsNode findChildByEntity( Object aEntity, boolean aQueryChildren ) {
    // TODO Auto-generated method stub
    return null;
  }

}
