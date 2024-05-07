package org.toxsoft.skf.bridge.s5.lib.impl;

import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper;
import org.toxsoft.core.tslib.bricks.keeper.IEntityKeeper;
import org.toxsoft.core.tslib.bricks.strio.IStrioReader;
import org.toxsoft.core.tslib.bricks.strio.IStrioWriter;
import org.toxsoft.core.tslib.utils.login.ILoginInfo;
import org.toxsoft.core.tslib.utils.login.LoginInfo;
import org.toxsoft.core.tslib.utils.valobj.TsValobjUtils;
import org.toxsoft.skf.bridge.s5.lib.ISkGatewayConfiguration;
import org.toxsoft.uskat.s5.client.remote.connection.IS5ConnectionInfo;
import org.toxsoft.uskat.s5.client.remote.connection.S5ConnectionInfo;

/**
 * Хранитель объектов типа {@link ISkGatewayConfiguration}.
 *
 * @author mvk
 */
public class SkGatewayConfigurationKeeper
    extends AbstractEntityKeeper<ISkGatewayConfiguration> {

  /**
   * Идентификатор регистрации хранителя {@link #KEEPER} в реестре {@link TsValobjUtils}.
   */
  public static final String KEEPER_ID = "SkGatewayConfigurationKeeper"; //$NON-NLS-1$

  /**
   * Экземпляр-синглтон хранителя.
   */
  public static IEntityKeeper<ISkGatewayConfiguration> KEEPER = new SkGatewayConfigurationKeeper();

  private SkGatewayConfigurationKeeper() {
    // Авт.обрамление скобками, null объект не используется
    super( ISkGatewayConfiguration.class, EEncloseMode.ENCLOSES_BASE_CLASS, null );
  }

  // ------------------------------------------------------------------------------------
  // Реализация методов класса AbstractEntityKeeper
  //

  @Override
  protected void doWrite( IStrioWriter aSw, ISkGatewayConfiguration aEntity ) {
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
    SkGatewayGwidConfigs.KEEPER.write( aSw, aEntity.exportCurrData() );
    aSw.writeSeparatorChar();
    SkGatewayGwidConfigs.KEEPER.write( aSw, aEntity.exportHistData() );
    aSw.writeSeparatorChar();
    SkGatewayGwidConfigs.KEEPER.write( aSw, aEntity.exportEvents() );
    aSw.writeSeparatorChar();
    SkGatewayGwidConfigs.KEEPER.write( aSw, aEntity.exportCmdExecutors() );
    aSw.writeSeparatorChar();
    aSw.writeInt( aEntity.isPaused() ? 1 : 0 );
  }

  @Override
  protected ISkGatewayConfiguration doRead( IStrioReader aSr ) {
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
    SkGatewayConfiguration retValue = new SkGatewayConfiguration( id, descr, name, connectionInfo, loginInfo );
    retValue.setExportCurrData( SkGatewayGwidConfigs.KEEPER.read( aSr ) );
    aSr.ensureSeparatorChar();
    retValue.setExportHistData( SkGatewayGwidConfigs.KEEPER.read( aSr ) );
    aSr.ensureSeparatorChar();
    retValue.setExportEvents( SkGatewayGwidConfigs.KEEPER.read( aSr ) );
    aSr.ensureSeparatorChar();
    retValue.setExportCmdExecutors( SkGatewayGwidConfigs.KEEPER.read( aSr ) );
    aSr.ensureSeparatorChar();
    retValue.setPaused( aSr.readInt() != 0 );

    return retValue;
  }
}
