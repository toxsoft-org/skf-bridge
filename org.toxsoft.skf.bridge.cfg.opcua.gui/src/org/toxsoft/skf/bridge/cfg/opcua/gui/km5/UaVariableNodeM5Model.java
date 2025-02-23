package org.toxsoft.skf.bridge.cfg.opcua.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.IBridgeCfgOpcUaResources.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.km5.ISkResources.*;

import java.text.*;
import java.util.*;

import org.eclipse.milo.opcua.sdk.client.nodes.*;
import org.eclipse.milo.opcua.sdk.core.nodes.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.skf.bridge.cfg.opcua.gui.utils.*;

/**
 * M5 model realization for {@link UaVariableNode} entities.
 *
 * @author dima
 */
public class UaVariableNodeM5Model
    extends M5Model<UaVariableNode> {

  /**
   * Model ID.
   */
  public static final String MODEL_ID = "bridge.cfg.opcua.m5.UaVariableNode"; //$NON-NLS-1$

  /**
   * формат для отображения метки времени
   */
  private static final String timestampFormatString = "HH:mm:ss.SSS"; //$NON-NLS-1$

  private static final DateFormat timestampFormat = new SimpleDateFormat( timestampFormatString );

  /**
   * string of OPC UA node id {@link UaVariableNode#getNodeId() }
   */
  public static final String FID_NODE_ID = "nodeId"; //$NON-NLS-1$

  /**
   * Attribute {@link UaVariableNode#getNodeId() } string id
   */
  public M5AttributeFieldDef<UaVariableNode> NODE_ID = new M5AttributeFieldDef<>( FID_NODE_ID, EAtomicType.STRING, //
      TSID_NAME, STR_N_NODE_ID, //
      TSID_DESCRIPTION, STR_D_NODE_ID, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_READ_ONLY | M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( UaVariableNode aEntity ) {
      return avStr( aEntity.getNodeId().toParseableString() );
    }

  };

  /**
   * browse name of node
   */
  public static final String FID_BROWSE_NAME = "browse.name"; //$NON-NLS-1$

  /**
   * Attribute {@link Node#getBrowseName() } browse name
   */
  public M5AttributeFieldDef<UaVariableNode> BROWSE_NAME =
      new M5AttributeFieldDef<>( FID_BROWSE_NAME, EAtomicType.STRING, //
          TSID_NAME, STR_N_PARAM_BROWSE_NAME, //
          TSID_DESCRIPTION, STR_D_PARAM_BROWSE_NAME, //
          OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( UaVariableNode aEntity ) {
          return avStr( aEntity.getBrowseName().getName() );
        }

      };

  /**
   * display name of node
   */
  public static final String FID_DISPLAY_NAME = "display.name"; //$NON-NLS-1$

  /**
   * Attribute {@link Node#getDisplayName() } display name
   */
  public M5AttributeFieldDef<UaVariableNode> DISPLAY_NAME =
      new M5AttributeFieldDef<>( FID_DISPLAY_NAME, EAtomicType.STRING, //
          TSID_NAME, STR_N_PARAM_DISPLAY_NAME, //
          TSID_DESCRIPTION, STR_D_PARAM_DISPLAY_NAME, //
          OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN | M5FF_INVARIANT );
        }

        protected IAtomicValue doGetFieldValue( UaVariableNode aEntity ) {
          return avStr( aEntity.getDisplayName().getText() );
        }

      };

  /**
   * value of node
   */
  public static final String FID_VALUE = "value"; //$NON-NLS-1$

  /**
   * Attribute {@link UaVariableNode#getValue() } value of node
   */
  public M5AttributeFieldDef<UaVariableNode> VALUE = new M5AttributeFieldDef<>( FID_VALUE, EAtomicType.STRING, //
      TSID_NAME, STR_N_VALUE, //
      TSID_DESCRIPTION, STR_D_VALUE, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( UaVariableNode aEntity ) {
      // получение значения узла
      DataValue dataValue = aEntity.getValue();
      // тут получаем Variant
      Variant variant = dataValue.getValue();
      Object rawValue = variant.getValue();
      return avStr( rawValue.toString() );
    }

  };

  /**
   * data type of node value
   */
  public static final String FID_DATA_TYPE = "dataType"; //$NON-NLS-1$

  /**
   * Attribute {@link UaVariableNode#getDataType() } value of node
   */
  public M5AttributeFieldDef<UaVariableNode> DATA_TYPE = new M5AttributeFieldDef<>( FID_DATA_TYPE, EAtomicType.STRING, //
      TSID_NAME, STR_N_DATA_TYPE, //
      TSID_DESCRIPTION, STR_D_DATA_TYPE, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN | M5FF_READ_ONLY );
    }

    protected IAtomicValue doGetFieldValue( UaVariableNode aEntity ) {
      String retVal = "unknown"; //$NON-NLS-1$
      Class<?> clazz = OpcUaUtils.getNodeDataTypeClass( aEntity );
      if( clazz != null ) {
        retVal = clazz.getSimpleName();
      }
      return avStr( retVal );
    }

  };

  /**
   * nodes status code
   */
  public static final String FID_STATUS_CODE = "statusCode"; //$NON-NLS-1$

  /**
   * Attribute nodes status code
   */
  @SuppressWarnings( "nls" )
  public M5AttributeFieldDef<UaVariableNode> STATUS_CODE =
      new M5AttributeFieldDef<>( FID_STATUS_CODE, EAtomicType.STRING, //
          TSID_NAME, STR_N_STATUS_CODE, //
          TSID_DESCRIPTION, STR_D_STATUS_CODE, //
          OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN | M5FF_READ_ONLY );
        }

        protected IAtomicValue doGetFieldValue( UaVariableNode aEntity ) {
          // получение значения узла
          DataValue dataValue = aEntity.getValue();
          // тут получаем StatusCode
          StatusCode statusCode = dataValue.getStatusCode();
          return avStr( quality( statusCode ) );
        }

        private static String quality( StatusCode statusCode ) {
          if( statusCode.isGood() ) {
            return "good";
          }
          else
            if( statusCode.isBad() ) {
              return "bad";
            }
            else
              if( statusCode.isUncertain() ) {
                return "uncertain";
              }
              else {
                return "unknown";
              }
        }

      };

  /**
   * Node's attribute server timestamp
   */
  public static final String FID_SOURCE_TIMESTAMP = "source.timestamp"; //$NON-NLS-1$

  /**
   * Node's attribute server timestamp
   */
  public M5AttributeFieldDef<UaVariableNode> SOURCE_TIMESTAMP =
      new M5AttributeFieldDef<>( FID_SOURCE_TIMESTAMP, EAtomicType.STRING, //
          TSID_NAME, STR_N_SOURCE_TIMESTAMP, //
          TSID_DESCRIPTION, STR_D_SOURCE_TIMESTAMP, //
          OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN | M5FF_READ_ONLY );
        }

        protected IAtomicValue doGetFieldValue( UaVariableNode aEntity ) {
          DataValue dataValue = aEntity.getValue();
          DateTime retVal = dataValue.getSourceTime();
          return javaDate( retVal );
        }

      };

  private static IAtomicValue javaDate( DateTime retVal ) {
    Date date = retVal.getJavaDate();
    return avStr( timestampFormat.format( date ) );
  }

  /**
   * Node's attribute server timestamp
   */
  public static final String FID_SERVER_TIMESTAMP = "server.timestamp"; //$NON-NLS-1$

  /**
   * Node's attribute server timestamp
   */
  public M5AttributeFieldDef<UaVariableNode> SERVER_TIMESTAMP =
      new M5AttributeFieldDef<>( FID_SERVER_TIMESTAMP, EAtomicType.STRING, //
          TSID_NAME, STR_N_SERVER_TIMESTAMP, //
          TSID_DESCRIPTION, STR_D_SERVER_TIMESTAMP, //
          OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN | M5FF_READ_ONLY );
        }

        protected IAtomicValue doGetFieldValue( UaVariableNode aEntity ) {
          DataValue dataValue = aEntity.getValue();
          DateTime retVal = dataValue.getServerTime();

          return javaDate( retVal );
        }

      };

  /**
   * Constructor.
   */
  public UaVariableNodeM5Model() {
    super( MODEL_ID, UaVariableNode.class );
    addFieldDefs( NODE_ID, BROWSE_NAME, DISPLAY_NAME, VALUE, DATA_TYPE, STATUS_CODE, SOURCE_TIMESTAMP,
        SERVER_TIMESTAMP );
  }

}
