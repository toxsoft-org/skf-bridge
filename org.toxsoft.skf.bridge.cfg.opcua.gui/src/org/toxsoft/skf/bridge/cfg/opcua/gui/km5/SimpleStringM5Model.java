package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.models.misc.*;

/**
 * @author max
 */
public class SimpleStringM5Model
    extends StringM5Model {

  /**
   * The model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.opcua.SimpleStringM5Model"; //$NON-NLS-1$

  public SimpleStringM5Model() {
    super( MODEL_ID );
    setPanelCreator( new M5DefaultPanelCreator<>() {

      @Override
      protected IM5CollectionPanel<String> doCreateCollViewerPanel( ITsGuiContext aContext,
          IM5ItemsProvider<String> aItemsProvider ) {
        OPDEF_IS_ACTIONS_CRUD.setValue( aContext.params(), AV_FALSE );
        OPDEF_IS_DETAILS_PANE.setValue( aContext.params(), AV_FALSE );
        OPDEF_IS_DETAILS_PANE_HIDDEN.setValue( aContext.params(), AV_TRUE );
        OPDEF_IS_TOOLBAR.setValue( aContext.params(), AV_FALSE );
        MultiPaneComponentModown<String> mpc = new MultiPaneComponentModown<>( aContext, model(), aItemsProvider );
        return new M5CollectionPanelMpcModownWrapper<>( mpc, true );
      }
    } );
  }
}
