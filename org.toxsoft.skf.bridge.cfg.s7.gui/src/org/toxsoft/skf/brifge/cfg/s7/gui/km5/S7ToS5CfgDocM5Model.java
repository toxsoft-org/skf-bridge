package org.toxsoft.skf.brifge.cfg.s7.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.modbus.gui.l10n.ISkBridgeCfgModbusGuiSharedResources.*;

import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.fields.*;
import org.toxsoft.core.tsgui.rcp.valed.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.km5.*;

/**
 * M5 model realization for {@link S7ToS5CfgDoc} entities.
 *
 * @author dima
 */
public class S7ToS5CfgDocM5Model
    extends M5Model<S7ToS5CfgDoc> {

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.modbus.m5.ModbusToS5CfgDocM5Model"; //$NON-NLS-1$

  /**
   * Field {@link S7ToS5CfgDoc#id()}
   */
  public static final M5AttributeFieldDef<S7ToS5CfgDoc> ID = new M5StdFieldDefId<>();

  /**
   * Field {@link S7ToS5CfgDoc#nmName()}
   */
  public static final M5AttributeFieldDef<S7ToS5CfgDoc> NAME = new M5StdFieldDefName<>();

  /**
   * Field {@link S7ToS5CfgDoc#description()}
   */
  public static final M5AttributeFieldDef<S7ToS5CfgDoc> DESCRIPTION = new M5StdFieldDefDescription<>();

  /**
   * Path to l2 dir
   */
  public static final String FID_PATH_TO_L2 = "path.to.l2"; //$NON-NLS-1$

  /**
   * Cfg file name (without extension)
   */
  public static final String FID_CFG_FILE_NAME = "cfg.file.name"; //$NON-NLS-1$

  /**
   * Attr id for additional properties to modify generated cfg files
   */
  public static final String FID_ADDITIONAL_PROPERTIES = "additional.properties"; //$NON-NLS-1$

  /**
   * Attribute {@link S7ToS5CfgDoc#getL2Path() } path to l2 dir
   */
  public static final M5AttributeFieldDef<S7ToS5CfgDoc> PATH_TO_L2 =
      new M5AttributeFieldDef<>( FID_PATH_TO_L2, IValedFileConstants.DT_DIRECTORY_FILE, //
          TSID_NAME, STR_BRIDGE_CFG_FILE_DIR, //
          TSID_DESCRIPTION, STR_BRIDGE_CFG_FILE_DIR_D //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( S7ToS5CfgDoc aEntity ) {
          return avValobj( aEntity.getL2Path() );
        }

      };

  /**
   * Attribute {@link S7ToS5CfgDoc#getCfgFilesPrefix() } display name
   */
  public static final M5AttributeFieldDef<S7ToS5CfgDoc> CFG_FILE_NAME =
      new M5AttributeFieldDef<>( FID_CFG_FILE_NAME, EAtomicType.STRING, //
          TSID_NAME, STR_BRIDGE_CFG_FILE_BARE_NAME, //
          TSID_DESCRIPTION, STR_BRIDGE_CFG_FILE_BARE_NAME_D //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( S7ToS5CfgDoc aEntity ) {
          return avStr( aEntity.getCfgFilesPrefix() );
        }

      };

  /**
   * Attribute {@link S7ToS5CfgDoc#getProperties() } additional properties to modify generated cfg files
   */
  public static final IM5MultiModownFieldDef<S7ToS5CfgDoc, IStringList> ADDITIONAL_PROPERTIES =
      new M5MultiModownFieldDef<>( FID_ADDITIONAL_PROPERTIES, StringPropertiesM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_BRIDGE_ADD_PROPERTIES, STR_BRIDGE_ADD_PROPERTIES_D );
          setFlags( M5FF_COLUMN | M5FF_DETAIL );
          // пропишем сразу стандартный продюсер
          // IStringListEdit dfltProducer = new StringArrayList();
          // dfltProducer.add( "javaClassName" );
          // dfltProducer.add( "org.toxsoft.l2.thd.opc.ua.milo.OpcUaMiloDriverProducer" );
          // dfltProducer.add( "dev" );
          // IListEdit<IStringList> dfltProps = new ElemArrayList<IStringList>();
          // dfltProps.add( dfltProducer );
          // setDefaultValue( dfltProps );
          // задаем нормальный размер!
          tsContext().params().setInt( IValedControlConstants.OPDEF_VERTICAL_SPAN, 3 );
        }

        protected IList<IStringList> doGetFieldValue( S7ToS5CfgDoc aEntity ) {
          return aEntity.getProperties();
        }

        protected String doGetFieldValueName( S7ToS5CfgDoc aEntity ) {
          IList<IStringList> props = aEntity.getProperties();
          StringBuilder result = new StringBuilder();

          for( IStringList prop : props ) {
            result.append( prop.first() );
            result.append( ", " ); //$NON-NLS-1$
          }

          return result.toString();
        }
      };

  /**
   * Constructor.
   */
  public S7ToS5CfgDocM5Model() {
    super( MODEL_ID, S7ToS5CfgDoc.class );
    // прячем никому не нужный ID
    ID.setFlags( M5FF_HIDDEN | M5FF_INVARIANT );
    addFieldDefs( ID, NAME, PATH_TO_L2, CFG_FILE_NAME, DESCRIPTION, ADDITIONAL_PROPERTIES );

  }
}
