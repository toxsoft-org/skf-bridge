package org.toxsoft.skf.bridge.s5.lib.impl;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.utils.login.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.bridge.s5.lib.*;
import org.toxsoft.uskat.s5.client.remote.connection.*;

/**
 * Хранитель объектов типа {@link ISkGatewayInfo}.
 *
 * @author mvk
 */
public class SkGatewayInfoKeeper
    extends AbstractEntityKeeper<ISkGatewayInfo> {

  /**
   * Идентификатор регистрации хранителя {@link #KEEPER} в реестре {@link TsValobjUtils}.
   */
  public static final String KEEPER_ID = "SkGatewayInfoKeeper"; //$NON-NLS-1$

  /**
   * Экземпляр-синглтон хранителя.
   */
  public static IEntityKeeper<ISkGatewayInfo> KEEPER = new SkGatewayInfoKeeper();

  private SkGatewayInfoKeeper() {
    // Авт.обрамление скобками, null объект не используется
    super( ISkGatewayInfo.class, EEncloseMode.ENCLOSES_BASE_CLASS, null );
  }

  // ------------------------------------------------------------------------------------
  // Реализация методов класса AbstractEntityKeeper
  //

  @Override
  protected void doWrite( IStrioWriter aSw, ISkGatewayInfo aEntity ) {
    aSw.writeQuotedString( aEntity.id() );
    aSw.writeSeparatorChar();
    aSw.writeQuotedString( aEntity.description() );
    aSw.writeSeparatorChar();
    aSw.writeQuotedString( aEntity.nmName() );
    aSw.writeSeparatorChar();
    S5ConnectionInfo.KEEPER.write( aSw, aEntity.connectionInfo() );
    aSw.writeSeparatorChar();
    aSw.writeQuotedString( aEntity.loginInfo().login() );
    aSw.writeSeparatorChar();
    aSw.writeQuotedString( aEntity.loginInfo().password() );
    aSw.writeSeparatorChar();
    SkGatewayGwids.KEEPER.write( aSw, aEntity.exportCurrData() );
    aSw.writeSeparatorChar();
    SkGatewayGwids.KEEPER.write( aSw, aEntity.exportHistData() );
    aSw.writeSeparatorChar();
    SkGatewayGwids.KEEPER.write( aSw, aEntity.exportEvents() );
    aSw.writeSeparatorChar();
    SkGatewayGwids.KEEPER.write( aSw, aEntity.exportCmdExecutors() );
    aSw.writeSeparatorChar();
  }

  @Override
  protected ISkGatewayInfo doRead( IStrioReader aSr ) {
    String id = aSr.readQuotedString();
    aSr.ensureSeparatorChar();
    String descr = aSr.readQuotedString();
    aSr.ensureSeparatorChar();
    String name = aSr.readQuotedString();
    aSr.ensureSeparatorChar();
    IS5ConnectionInfo connectionInfo = S5ConnectionInfo.KEEPER.read( aSr );
    aSr.ensureSeparatorChar();
    String login = aSr.readQuotedString();
    aSr.ensureSeparatorChar();
    String passwd = aSr.readQuotedString();
    aSr.ensureSeparatorChar();
    ILoginInfo loginInfo = new LoginInfo( login, passwd );
    SkGatewayInfo retValue = new SkGatewayInfo( id, descr, name, connectionInfo, loginInfo );
    retValue.setExportCurrData( SkGatewayGwids.KEEPER.read( aSr ) );
    aSr.ensureSeparatorChar();
    retValue.setExportHistData( SkGatewayGwids.KEEPER.read( aSr ) );
    aSr.ensureSeparatorChar();
    retValue.setExportEvents( SkGatewayGwids.KEEPER.read( aSr ) );
    aSr.ensureSeparatorChar();
    retValue.setExportCmdExecutors( SkGatewayGwids.KEEPER.read( aSr ) );

    return retValue;
  }
}
