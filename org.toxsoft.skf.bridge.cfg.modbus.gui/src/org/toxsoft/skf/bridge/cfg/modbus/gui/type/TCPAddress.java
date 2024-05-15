package org.toxsoft.skf.bridge.cfg.modbus.gui.type;

import static org.toxsoft.core.tslib.bricks.strio.IStrioHardConstants.*;

import java.net.*;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;

/**
 * Description of one address in TCP Network
 *
 * @author dima
 */
public class TCPAddress
    extends Stridable {

  /**
   * Singleton of the no address.
   */
  public static final TCPAddress NONE = new TCPAddress();

  /**
   * IP address
   */
  private InetAddress ip;

  /**
   * port of process
   */
  private int port;

  /**
   * Value-object keeper identifier.
   */
  public static final String KEEPER_ID = "TCPAddress"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public final static IEntityKeeper<TCPAddress> KEEPER =
      new AbstractEntityKeeper<>( TCPAddress.class, EEncloseMode.NOT_IN_PARENTHESES, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, TCPAddress aEntity ) {
          aSw.incNewLine();

          // id, name, description
          aSw.writeAsIs( aEntity.id() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          aSw.writeQuotedString( aEntity.nmName() );
          aSw.writeSeparatorChar();
          aSw.writeEol();
          // IP address
          aSw.writeQuotedString( aEntity.getIP().getHostAddress() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // port
          aSw.writeInt( aEntity.getPort() );
          aSw.writeEol();
        }

        @Override
        protected TCPAddress doRead( IStrioReader aSr ) {
          // id, name, description
          String id = aSr.readIdPath();
          aSr.ensureSeparatorChar();
          String name = aSr.readQuotedString();
          aSr.ensureSeparatorChar();
          String ipStr = aSr.readQuotedString();
          InetAddress ip;
          try {
            ip = InetAddress.getByName( ipStr );
          }
          catch( UnknownHostException ex ) {
            LoggerUtils.errorLogger().error( ex );
            ip = null;
          }
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          int port = aSr.readInt();
          return new TCPAddress( id, name, ip, port );
        }
      };

  /**
   * Constructor
   *
   * @param aId - id of entity
   * @param aName - human name of address
   * @param aInetAddress - IP address
   * @param aPort - port of process
   */
  public TCPAddress( String aId, String aName, InetAddress aInetAddress, int aPort ) {
    super( aId, aName, TsLibUtils.EMPTY_STRING );
    TsNullArgumentRtException.checkNull( aInetAddress );
    ip = aInetAddress;
    port = aPort;
  }

  private TCPAddress() {
    super( "empty.tcp.address.id", "empty IP address", TsLibUtils.EMPTY_STRING );
    try {
      ip = InetAddress.getByName( "192.168.0.1" );
    }
    catch( UnknownHostException ex ) {
      LoggerUtils.errorLogger().error( ex );
    }
    port = 502;
  }

  /**
   * Sets {@link #nmName()}.
   *
   * @param aName String - short name
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  @Override
  public void setName( String aName ) {
    TsNullArgumentRtException.checkNull( aName );
    super.setName( aName );
  }

  /**
   * @return IP address
   */
  public InetAddress getIP() {
    return ip;
  }

  /**
   * @return port of process
   */
  public int getPort() {
    return port;
  }

  /**
   * @param aAddr - new IP address
   */
  public void setIP( InetAddress aAddr ) {
    ip = aAddr;
  }

  /**
   * @param aPortNum - new port number
   */
  public void setPort( int aPortNum ) {
    port = aPortNum;
  }

  /**
   * @return string
   */
  @SuppressWarnings( "boxing" )
  @Override
  public String toString() {
    return String.format( "%s - %s:%d", nmName(), getIP().getHostAddress(), port ); //$NON-NLS-1$
  }

}
