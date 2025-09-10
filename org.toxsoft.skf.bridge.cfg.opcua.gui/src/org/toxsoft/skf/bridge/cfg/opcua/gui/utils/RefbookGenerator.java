package org.toxsoft.skf.bridge.cfg.opcua.gui.utils;

import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.bridge.cfg.opcua.gui.skide.IGreenWorldRefbooks.*;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.skf.refbooks.lib.*;
import org.toxsoft.skf.refbooks.lib.impl.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Create refbooks to be used in project
 *
 * @author dima
 */
public class RefbookGenerator {

  static class RefookItemBuider {

    IOptionSetEdit attrValues = new OptionSet();

    private final Skid   skid;
    private final String name;
    private final String descr;

    RefookItemBuider( String aId, String aName, String aDescr, ISkRefbook aRefbook ) {
      String classId = aRefbook.itemClassId();
      skid = new Skid( classId, aId );
      name = aName;
      descr = aDescr;

      DtoRefbookInfo rbInfo = DtoRefbookInfo.of( aRefbook );
      for( IDtoAttrInfo ai : rbInfo.attrInfos() ) {
        attrValues.put( ai.id(), IAtomicValue.NULL );
      }
    }

    void setValue( String aFieldId, IAtomicValue aValue ) {
      attrValues.put( aFieldId, aValue );
    }

    IDtoFullObject buildItem() {
      IDtoObject dtoObj = new DtoObject( skid, attrValues, IStringMap.EMPTY );
      DtoFullObject retVal = new DtoFullObject( dtoObj, IStringMap.EMPTY, IStringMap.EMPTY );
      retVal.attrs().setStr( TSID_NAME, name );
      retVal.attrs().setStr( TSID_DESCRIPTION, descr );
      return retVal;
    }
  }

  /**
   * server connection
   */
  private final ISkConnection conn;

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_RRI_OPCUA___INDEX}.
   */
  static IDtoAttrInfo ATRINF_RRI_INDEX = DtoAttrInfo.create2( RBATRID_RRI_OPCUA___INDEX, DDEF_INTEGER, //
      TSID_NAME, "Индекс", TSID_DESCRIPTION, "Индекс команды НСИ", //
      TSID_DEFAULT_VALUE, AvUtils.AV_0 //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_RRI_OPCUA___RRIID}.
   */
  static IDtoAttrInfo ATRINF_RRI_ID = DtoAttrInfo.create2( RBATRID_RRI_OPCUA___RRIID, DDEF_STRING, //
      TSID_NAME, "Id НСИ", TSID_DESCRIPTION, "Id команды НСИ", //
      TSID_DEFAULT_VALUE, AvUtils.AV_0 //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_RRI_OPCUA___ON}.
   */
  static IDtoAttrInfo ATRINF_RRI_ON = DtoAttrInfo.create2( RBATRID_RRI_OPCUA___ON, DDEF_BOOLEAN, //
      TSID_NAME, "0->1", TSID_DESCRIPTION, "срабатывание при 0->1", //
      TSID_DEFAULT_VALUE, AvUtils.AV_FALSE //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_RRI_OPCUA___OFF}.
   */
  static IDtoAttrInfo ATRINF_RRI_OFF = DtoAttrInfo.create2( RBATRID_RRI_OPCUA___OFF, DDEF_BOOLEAN, //
      TSID_NAME, "1->0", TSID_DESCRIPTION, "срабатывание при 1->0", //
      TSID_DEFAULT_VALUE, AvUtils.AV_FALSE //
  );

  /**
   * Refbook: RRI_OPCUA - Команды НСИ.
   * <p>
   */
  public static IDtoRefbookInfo REFBOOK_RRI_OPCUA = DtoRefbookInfo.create( RBID_RRI_OPCUA, ///
      OptionSetUtils.createOpSet( ///
          TSID_NAME, "Команды НСИ", //$NON-NLS-1$
          TSID_DESCRIPTION, "Команды НСИ" //$NON-NLS-1$
      ), ///
      new StridablesList<>( ///
          ATRINF_RRI_INDEX, //
          ATRINF_RRI_ID, //
          ATRINF_RRI_ON, //
          ATRINF_RRI_OFF //
      ), //
      new StridablesList<>( ///
      // no CLOBs
      ), ///
      new StridablesList<>( ///
      // no rivets
      ), ///
      new StridablesList<>( ///
      // no links
      ) ///
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #ATRID_INDEX}.
   */
  static IDtoAttrInfo ATRINF_INDEX = DtoAttrInfo.create2( RBATRID_CMD_OPCUA___INDEX, DDEF_INTEGER, //
      TSID_NAME, "Индекс", TSID_DESCRIPTION, "Индекс команды управления", //
      TSID_DEFAULT_VALUE, AvUtils.AV_0 //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #ATRID_CMD_ID }.
   */
  static IDtoAttrInfo ATRINF_CMD_ID = DtoAttrInfo.create2( RBATRID_CMD_OPCUA___CMDID, DDEF_STRING, //
      TSID_NAME, "Идентификатор команды", //
      TSID_DESCRIPTION, "Идентификатор команды Uskat", //
      TSID_DEFAULT_VALUE, AvUtils.AV_STR_EMPTY //
  );

  /**
   * Refbook: commands for OPC.
   * <p>
   */
  public static IDtoRefbookInfo REFBOOK_CMDS_OPCUA = DtoRefbookInfo.create( RBID_CMD_OPCUA, ///
      OptionSetUtils.createOpSet( ///
          TSID_NAME, "Команды управления", //$NON-NLS-1$
          TSID_DESCRIPTION, "Команды управления" //$NON-NLS-1$
      ), ///
      new StridablesList<>( ///
          ATRINF_INDEX, //
          ATRINF_CMD_ID ), //
      new StridablesList<>( ///
      // no CLOBs
      ), ///
      new StridablesList<>( ///
      // no rivets
      ), ///
      new StridablesList<>( ///
      // no links
      ) ///
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_BITMASK___BITN}.
   */
  static IDtoAttrInfo ATRINF_BITMASK_BITN = DtoAttrInfo.create2( RBATRID_BITMASK___BITN, DDEF_INTEGER, //
      TSID_NAME, "Номер бита", TSID_DESCRIPTION, "Номер бита в слове/регистре", //
      TSID_DEFAULT_VALUE, AvUtils.AV_0 //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_BITMASK___IDENTIFICATOR}.
   */
  static IDtoAttrInfo ATRINF_BITMASK_IDENTIFICATOR = DtoAttrInfo.create2( RBATRID_BITMASK___IDENTIFICATOR, DDEF_STRING, //
      TSID_NAME, "Идентификатор", TSID_DESCRIPTION, "Идентификатор данного", //
      TSID_DEFAULT_VALUE, AvUtils.AV_STR_EMPTY //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_BITMASK___IDW}.
   */
  static IDtoAttrInfo ATRINF_BITMASK_IDW = DtoAttrInfo.create2( RBATRID_BITMASK___IDW, DDEF_STRING, //
      TSID_NAME, "Слово", TSID_DESCRIPTION, "Идентификатор слова/регистра", //
      TSID_DEFAULT_VALUE, AvUtils.AV_STR_EMPTY //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_BITMASK___ON}.
   */
  static IDtoAttrInfo ATRINF_BITMASK_ON = DtoAttrInfo.create2( RBATRID_BITMASK___ON, DDEF_STRING, //
      TSID_NAME, "0->1", TSID_DESCRIPTION, "срабатывание при 0->1", //
      TSID_DEFAULT_VALUE, AvUtils.AV_STR_EMPTY //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_RRI_OPCUA___OFF}.
   */
  static IDtoAttrInfo ATRINF_BITMASK_OFF = DtoAttrInfo.create2( RBATRID_BITMASK___OFF, DDEF_STRING, //
      TSID_NAME, "1->0", TSID_DESCRIPTION, "срабатывание при 1->0", //
      TSID_DEFAULT_VALUE, AvUtils.AV_STR_EMPTY //
  );

  /**
   * Refbook: BitMask - Битовые маски.
   * <p>
   */
  public static IDtoRefbookInfo REFBOOK_BITMASK_OPCUA = DtoRefbookInfo.create( RBID_BITMASK, ///
      OptionSetUtils.createOpSet( ///
          TSID_NAME, "Битовые маски", //$NON-NLS-1$
          TSID_DESCRIPTION, " Битовые маски" //$NON-NLS-1$
      ), ///
      new StridablesList<>( ///
          ATRINF_BITMASK_BITN, //
          ATRINF_BITMASK_IDENTIFICATOR, //
          ATRINF_BITMASK_IDW, //
          ATRINF_BITMASK_ON, //
          ATRINF_BITMASK_OFF //
      ), //
      new StridablesList<>( ///
      // no CLOBs
      ), ///
      new StridablesList<>( ///
      // no rivets
      ), ///
      new StridablesList<>( ///
      // no links
      ) ///
  );

  /**
   * Constructor.
   *
   * @param aConn - server
   */
  public RefbookGenerator( ISkConnection aConn ) {
    conn = aConn;
  }

  /**
   * Create refbook Poligon commands
   */
  public void createPoligonCommandsRefbook() {
    ISkRefbookService rbServ = conn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    // create refbook of commands
    ISkRefbook rbCommnads = rbServ.defineRefbook( REFBOOK_CMDS_OPCUA );
    // fill refbook
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___ANALOGINPUT_ENABLEON, "Разрешение",
        "Разрешение формирования сигнализации", 1 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___ANALOGINPUT_ENABLEOFF, "Блокировка",
        "Блокировка формирования сигнализации", 2 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___ANALOGINPUT_IMITATIONON, "Имитация Вкл.",
        "Включить режим имитации", 3 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___ANALOGINPUT_IMITATIONOFF, "Имитация Откл",
        "Отключить режим имитации", 4 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___ANALOGINPUT_CONFIRMATION, "Квитирование", "Квитирование", 5 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_TESTALARM, "Тест аварийной сигнализации",
        "Тест аварийной сигнализации", 1 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_TESTWARN, "Тест предупредительной сигнализации",
        "Тест предупредительной сигнализации", 2 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_START, "Запуск в Автомате",
        "Включение режима автоматического старта агрегата", 3 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_STOP, "Останов в Автомате",
        "Включение режима автоматического останова агрегата", 4 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_CONFIRMATION, "Квитирование", "Квитирование", 5 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_ENABLEALARM, "Разрешение сигнализации",
        "Разрешение сигнализации", 6 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_DISABLEALARM, "Блокировка сигнализации",
        "Блокировка сигнализации", 7 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_SETAUTOCTRL, "Режим Авто",
        "Режим управления Автоматический", 8 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_SETMANCTRL, "Режим Руч", "Режим управления ручной",
        9 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_SETLOCALCTRL, "Режим Мест", "Режим управления Местный",
        10 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_LAUNCHGRVPU, "Включить ГР/ВПУ", "Включить ГР/ВПУ",
        19 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_STOPGRVPU, "Отключить ГР/ВПУ", "Отключить ГР/ВПУ",
        20 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_LAUNCHVENT, "Включить вентсистему",
        "Включить вентсистему", 21 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_STOPVENT, "Отключить вентсистему",
        "Отключить вентсистему", 22 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_RESETRRI, "Сбросить флаг НСИ",
        "Сбросить флаг рабочего НСИ для заливки с сервера", 23 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___CTRLSYSTEM_SETRRI, "Установить флаг НСИ",
        "Установить флаг рабочего НСИ для заливки с сервера", 24 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_ENABLEON, "Разрешение",
        "Разрешение работы устройства", 1 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_ENABLEOFF, "Блокировка",
        "Блокировка работы устройства", 2 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_IMITATIONON, "Имитация Вкл.",
        "Включить режим имитации", 3 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_IMITATIONOFF, "Имитация Откл.",
        "Отключить режим имитации", 4 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_CONFIRMATION, "Квитирование",
        "Квитирование ошибок", 5 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_RESETHOUR, "Сброс наработки",
        "Сброс наработки", 6 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_STOPMAN, "Стоп Руч", "Стоп в Ручном режиме",
        8 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___IRREVERSIBLEENGINE_STARTMAN, "Старт Руч", "Старт в Ручном режиме",
        10 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VALVE_ENABLEON, "Разрешение", "Разрешение работы устройства", 1 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VALVE_ENABLEOFF, "Блокировка", "Блокировка работы устройства",
        2 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VALVE_IMITATIONON, "Имитация Вкл.", "Включить режим имитации",
        3 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VALVE_IMITATIONOFF, "Имитация Откл.", "Отключить режим имитации",
        4 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VALVE_CONFIRMATION, "Квитирование", "Квитирование", 5 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VALVE_STOPMAN, "Стоп Руч", "Стоп в Ручном режиме", 8 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VALVE_STARTMAN, "Старт Руч", "Старт в Ручном режиме", 10 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___REVERSIBLEENGINE_ENABLEON, "Разрешение",
        "Разрешение работы устройства", 1 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___REVERSIBLEENGINE_ENABLEOFF, "Блокировка",
        "Блокировка работы устройства", 2 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___REVERSIBLEENGINE_IMITATIONON, "Имитация Вкл.",
        "Включить режим имитации", 3 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___REVERSIBLEENGINE_IMITATIONOFF, "Имитация Откл.",
        "Отключить режим имитации", 4 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___REVERSIBLEENGINE_CONFIRMATION, "Квитирование", "Квитирование",
        5 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___REVERSIBLEENGINE_RESETHOUR, "Сброс наработки", "Сброс наработки",
        6 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___REVERSIBLEENGINE_STOPMAN, "Стоп Руч", "Стоп в Ручном режиме", 8 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___REVERSIBLEENGINE_OPENMAN, "Открыть Руч",
        "Открыть в Ручном режиме", 10 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___REVERSIBLEENGINE_CLOSEMAN, "Закрыть Руч",
        "Закрыть в Ручном режиме", 12 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___MAINSWITCH_ENABLEON, "Разрешение", "Разрешение работы устройства",
        1 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___MAINSWITCH_ENABLEOFF, "Блокировка",
        "Блокировка работы устройства", 2 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___MAINSWITCH_IMITATIONON, "Имитация Вкл.",
        "Включить режим имитации", 3 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___MAINSWITCH_IMITATIONOFF, "Имитация Откл.",
        "Отключить режим имитации", 4 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___MAINSWITCH_CONFIRMATION, "Квитирование", "Квитирование", 5 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___MAINSWITCH_RESETHOUR, "Сброс наработки", "Сброс наработки", 6 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___MAINSWITCH_STOPMAN, "Стоп Руч", "Стоп в Ручном режиме", 8 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___MAINSWITCH_STARTMAN, "Старт Руч", "Старт в Ручном режиме", 10 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VENTILATION_WORKMAN1, "Старт Руч 1", "Старт в ручном режиме 1",
        1 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VENTILATION_WORKMAN2, "Старт Руч 2", "Старт в ручном режиме 2",
        2 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VENTILATION_WORKMAN3, "Старт Руч 3", "Старт в ручном режиме 3",
        3 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VENTILATION_STOPMAN, "Стоп Руч", "Стоп в ручном режиме", 4 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VENTILATION_CONFIRMATION, "Квитирование", "Квитирование", 5 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VENTILATION_CTRLMODEAUTO, "Режим Авто",
        "Режим управления Автоматический", 10 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___VENTILATION_CTRLMODEMAN, "Режим Руч", "Режим управления Ручной",
        11 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___REGOILPRESSURE_ON, "Включить", "Включить", 1 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___REGOILPRESSURE_OFF, "Отключить", "Отключить", 2 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___PIDREGDO_ON, "Включить", "Включить", 1 );
    addCommandsRbItem( rbCommnads, ITEMID_CMD_OPCUA___PIDREGDO_OFF, "Отключить", "Отключить", 2 );

  }

  private void addCommandsRbItem( ISkRefbook aRefbook, String aItemId, String aName, String aDescr, int aCmdIndex ) {
    RefookItemBuider b = new RefookItemBuider( aItemId, aName, aDescr, aRefbook );
    b.setValue( RBATRID_CMD_OPCUA___INDEX, AvUtils.avInt( aCmdIndex ) );
    // get cmdId from item id
    String[] parts = aItemId.split( "\\." ); //$NON-NLS-1$
    b.setValue( RBATRID_CMD_OPCUA___CMDID, AvUtils.avStr( "cmd" + parts[1] ) );
    IDtoFullObject rbItem = b.buildItem();
    aRefbook.defineItem( rbItem );
  }

  public void createPoligonRriRefbook() {
    ISkRefbookService rbServ = conn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    // create refbook of RRI
    ISkRefbook rbRRIs = rbServ.defineRefbook( REFBOOK_RRI_OPCUA );
    // fill refbook
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_IMITATIONVALUE, "Значение имитации",
        "Задать значение имитации", 6, false, false );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT1GENERATION, "Генерация ВА1 Вкл.",
        "Включить режим Генерации по Верхней Аварии", 7, false, true );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT1GENERATION, "Генерация ВА1 Откл.",
        "Отключить режим Генерации по Верхней Аварии", 8, true, false );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT1INDICATION, "Индикация ВА1 Вкл.",
        "Включить режим Индикации по Верхней Аварии", 9, false, true );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT1INDICATION, "Индикация ВА1 Откл.",
        "Отключить режим Индикации по Верхней Аварии", 10, true, false );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT2GENERATION, "Генерация ВП2 Вкл.",
        "Включить режим Генерации по Верхнему Предупреждению", 11, false, true );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT2GENERATION, "Генерация ВП2 Откл.",
        "Отключить режим Генерации по Верхнему Предупреждению", 12, true, false );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT2INDICATION, "Индикация ВП2 Вкл.",
        "Включить режим Индикации по Верхнему Предупреждению", 13, false, true );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT2INDICATION, "Индикация ВП2 Откл.",
        "Отключить режим Индикации по Верхнему Предупреждению", 14, true, false );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT3GENERATION, "Генерация НП3 Вкл.",
        "Включить режим Генерации по Нижнему Предупреждению", 15, false, true );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT3GENERATION, "Генерация НП3 Откл.",
        "Отключить режим Генерации по Нижнему Предупреждению", 16, true, false );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT3INDICATION, "Индикация НП3 Вкл.",
        "Включить режим Индикации по Нижнему Предупреждению", 17, false, true );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT3INDICATION, "Индикация НП3 Откл.",
        "Отключить режим Индикации по Нижнему Предупреждению", 18, true, false );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT4GENERATION, "Генерация НА4 Вкл.",
        "Включить режим Генерации по Нижней Аварии", 19, false, true );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT4GENERATION, "Генерация НА4 Откл.",
        "Отключить режим Генерации по Нижней Аварии", 20, true, false );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT4INDICATION, "Индикация НА4 Вкл.",
        "Включить режим Индикации по Нижней Аварии", 21, false, true );
    addRriRbItem( rbRRIs, ITEMID_BITMASK___ANALOGINPUT_SETPOINT4INDICATION, "Индикация НА4 Откл.",
        "Отключить режим Индикации по Нижней Аварии", 22, true, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1, "Уставка ВА1", "Задать Верхнюю Аварию", 23, false,
        false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2, "Уставка ВП2", "Задать Верхнее Предупреждение", 24,
        false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3, "Уставка НП3", "Задать Нижнее Предупреждение", 25,
        false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4, "Уставка НА4", "Задать Нижнюю Аварию", 26, false,
        false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_Y0, "Нижняя граница параметра",
        "Нижняя граница диапазона измерения параметра", 29, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_Y1, "Верхняя граница параметра",
        "Верхняя граница диапазона измерения параметра", 30, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_HYSTERESIS, "Гистерезис",
        "Гистерезис в целых долях от диапазона. 100=1%, 200=0,5% и т.д.", 31, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_DELAYALARM, "Задержка аварии",
        "Задержка генерации аварии 0,1с", 32, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_DELAYWARN, "Задержка предупреждения",
        "Задержка генерации предупреждения 0,1с", 33, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_DELAYRESET, "Задержка сброса",
        "Задержка сброса аварии/предупреждения 0,1с", 34, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___CTRLSYSTEM_SETOILTEMP, "Уставка т-ры масла для пуска",
        "Уставка температуры масла для пуска агрегата", 11, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___CTRLSYSTEM_CASEHEATTARGET, "Уставка т-ры корпуса для пуска",
        "Уставка температуры корпуса для пуска агрегата", 12, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___CTRLSYSTEM_GEDTIME, "Таймер на запуск ГЭД",
        "Таймер на запуск ГЭД после продувки газом 0,1с", 13, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___CTRLSYSTEM_STARTUPHEATTIME, "Время прогрева",
        "Время прогрева агрегата после пуска ГЭД 0,1с", 14, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___CTRLSYSTEM_STOPRUNOUTTIME, "Время выбега",
        "Время выбега агрегата после останова ГЭД 0,1с", 15, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___CTRLSYSTEM_STARTUPOILMIXTIME, "Таймер ПМН при запуске",
        "Таймер ПМН при запуске для перемешивания масла 0,1с", 16, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___CTRLSYSTEM_BLOWTIME, "Время продувки",
        "Время продувки ГЭД перед пуском нагнетателя 0,1c", 17, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___CTRLSYSTEM_NETENABLEALARM, "Разрешить контроль связи",
        "Разрешить сигнализацию контроля связи заданного узла", 25, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___CTRLSYSTEM_NETDISABLEALARM, "Блокировать контроль связи",
        "Блокировать сигнализацию контроля связи заданного узла", 26, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___IRREVERSIBLEENGINE_AUXTIME, "Таймаут ДК",
        "Задержка срабатывания допконтакта 0,1с", 13, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___REVERSIBLEENGINE_AUXTIME, "Таймаут ДК",
        "Задержка срабатывания допконтакта 0,1с", 13, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___REVERSIBLEENGINE_OPENTIME, "Время открытия", "Время открытия 0,1с", 14,
        false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___REVERSIBLEENGINE_CLOSETIME, "Время закрытия", "Время закрытия 0,1с", 15,
        false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___MAINSWITCH_AUXTIME, "Таймаут ДК", "Задержка срабатывания допконтакта 0,1с",
        13, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___REGOILPRESSURE_SETPOINTON, "Уставка включения", "Уставка включения", 3,
        false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___REGOILPRESSURE_SETPOINTOFF, "Уставка отключения", "Уставка отключения", 4,
        false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___PIDREGDO_TASK, "Задание", "Задание ПИД-регулятору", 3, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___PIDREGDO_KP, "КП", "Коэффициент пропорциональности", 4, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___PIDREGDO_KI, "КИ", "Коэффициент интегральный", 5, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___PIDREGDO_KD, "КД", "Коэффициент дифференциальный", 6, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___PIDREGDO_TI, "ТИ", "Время интегрирования, сек", 7, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___PIDREGDO_TD, "ТД", "Время дифференцирования, сек", 8, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___PIDREGDO_DEADBAND, "Зона нечувствительности",
        "Зона нечувствительности в единицах параметра", 9, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___PIDREGDO_CTRLT, "Период вызова", "Период импульсов управления, сек", 10,
        false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___PIDREGDO_MINPULSE, "Мин.импульс", "Минимальный импульс управления, сек",
        11, false, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___PIDREGDO_MAXDUTYCYCLE, "Макс.скважность",
        "Максимальная скважность импульсов управления 0-1", 12, false, false );
  }

  private void addRriRbItem( ISkRefbook aRefbook, String aItemId, String aName, String aDescr, int aRriIndex,
      boolean aFall, boolean aFront ) {
    String rbItemId = aItemId;
    if( aFall ) {
      rbItemId = rbItemId + "Off";
    }
    if( aFront ) {
      rbItemId = rbItemId + "On";
    }
    RefookItemBuider b = new RefookItemBuider( rbItemId, aName, aDescr, aRefbook );
    b.setValue( RBATRID_RRI_OPCUA___INDEX, AvUtils.avInt( aRriIndex ) );
    // get rriId from item id
    String[] parts = aItemId.split( "\\." ); //$NON-NLS-1$
    b.setValue( RBATRID_RRI_OPCUA___RRIID, AvUtils.avStr( "rri" + parts[1] ) );
    b.setValue( RBATRID_RRI_OPCUA___OFF, AvUtils.avBool( aFall ) );
    b.setValue( RBATRID_RRI_OPCUA___ON, AvUtils.avBool( aFront ) );
    IDtoFullObject rbItem = b.buildItem();
    aRefbook.defineItem( rbItem );

  }

  public void createPoligonBitMaskRefbook() {
    ISkRefbookService rbServ = conn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    // create refbook of BITMASK
    ISkRefbook rbBitMasks = rbServ.defineRefbook( REFBOOK_BITMASK_OPCUA );
    // fill refbook
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_CALIBRATIONWARNING, "Значение за диапазоном 5%",
        "Отклонение в пределах 5% за диапазоном", "rtdStateWord", 0, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_CALIBRATIONERROR, "Ошибка статуса измерения",
        "Ошибка статуса измерения / неисправность датчика", "rtdStateWord", 1, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_ENABLED, "Разрешение сигнализации",
        "Разрешение сигнализации индикации генерации", "rtdCtrlWord", 2, "заблокировано", "разрешено" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_IMITATION, "Имитация", "Включен режим имитации",
        "rtdCtrlWord", 3, "Отключена", "Включена" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_ALARMMINGENERATION, "Генерация НА 4",
        "Генерация нижний аварийный уровень", "rtdStateWord", 8, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_ALARMMININDICATION, "Индикация НА 4",
        "Индикация нижний аварийный уровень", "rtdStateWord", 9, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_WARNINGMINGENERATION, "Генерация НП 3",
        "Генерация нижний предупредительный уровень", "rtdStateWord", 10, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_WARNINGMININDICATION, "Индикация НП 3",
        "Индикация нижний предупредительный уровень", "rtdStateWord", 11, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_WARNINGMAXGENERATION, "Генерация ВП 2",
        "Генерация верхний предупредительный уровень", "rtdStateWord", 12, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_WARNINGMAXINDICATION, "Индикация ВП 2",
        "Индикация верхний предупредительный уровень", "rtdStateWord", 13, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_ALARMMAXGENERATION, "Генерация ВА 1",
        "Генерация верхний аварийный уровень", "rtdStateWord", 14, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_ALARMMAXINDICATION, "Индикация ВА 1",
        "Индикация верхний аварийный уровень", "rtdStateWord", 15, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_SETPOINT4GENERATION, "Задание Генерация НА 4",
        "Задание режима Генерация нижний аварийный уровень", "rtdCtrlWord", 8, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING, true );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_SETPOINT4INDICATION, "Задание Индикация НА 4",
        "Задание режима Индикация нижний аварийный уровень", "rtdCtrlWord", 9, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING, true );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_SETPOINT3GENERATION, "Задание Генерация НП 3",
        "Задание режима Генерация нижний предупредительный уровень", "rtdCtrlWord", 10, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING, true );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_SETPOINT3INDICATION, "Задание Индикация НП 3",
        "Задание режима Индикация нижний предупредительный уровень", "rtdCtrlWord", 11, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING, true );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_SETPOINT2GENERATION, "Задание Генерация ВП 2",
        "Задание режима Генерация верхний предупредительный уровень", "rtdCtrlWord", 12, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING, true );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_SETPOINT2INDICATION, "Задание Индикация ВП 2",
        "Задание режима Индикация верхний предупредительный уровень", "rtdCtrlWord", 13, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING, true );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_SETPOINT1GENERATION, "Задание Генерация ВА 1",
        "Задание режима Генерация верхний аварийный уровень", "rtdCtrlWord", 14, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING, true );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___ANALOGINPUT_SETPOINT1INDICATION, "Задание Индикация ВА 1",
        "Задание режима Индикация верхний аварийный уровень", "rtdCtrlWord", 15, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING, true );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_STARTING, "Запуск в Автомате",
        "Выполняется алгоритм автоматического запуска агрегата", "rtdStateWord", 0, "Закончился", "Начался" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_STOPING, "Останов в Автомате",
        "Выполняется алгоритм автоматического останова агрегата", "rtdStateWord", 1, "Закончился", "Начался" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_EMERGENCYSTOP, "Останов в Аварийный",
        "Выполняется алгоритм аварийного останова агрегата", "rtdStateWord", 2, "Закончился", "Начался" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_READYAUTO, "Готовность Авто",
        "Готовность к автостарту агрегата", "rtdStateWord", 3, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_READYMAN, "Готовность Руч", "Готовность к пуску ГЭД",
        "rtdStateWord", 4, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_FS3, "Проток воды ГЭД", "Проток воды ГЭД есть/нет",
        "rtdStateWord", 5, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_MAINSWITCHONLOC, "ВВ включен в Местном",
        "ВВ включен в Местном режиме", "rtdStateWord", 6, TsLibUtils.EMPTY_STRING, "-" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_STATUSRRI, "Флаг статуса НСИ",
        "Флаг НСИ 0-надо залить с сервера 1-уже залито", "rtdStateWord", 12, "требуется заливка", "залито с сервера" );
    // not in use in project kz
    // addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_GRVPUSTARTING, "ГР/ВПУ включается",
    // "ГР/ВПУ включается в автомате", "rtdStateWordGrVpu", 0, "окончание автовключения группы",
    // "начало автовключения группы" );
    // addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_GRVPUSTOPING, "ГР/ВПУ отключается",
    // "ГР/ВПУ отключается в автомате", "rtdStateWordGrVpu", 1, "окончание автоостанова группы",
    // "начало автоостанова группы" );
    // addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_GRVPUSTARTCOMPLETE, "ГР/ВПУ включены", "ГР/ВПУ
    // включены",
    // "rtdStateWordGrVpu", 2, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    // addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_GRVPUSTOPCOMPLETE, "ГР/ВПУ отключены", "ГР/ВПУ
    // отключены",
    // "rtdStateWordGrVpu", 3, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    // addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_GRSTARTING, "Пуск ГР\\авто",
    // "Пуск гидрораспределителя в группе ГР/ВПУ", "rtdStateWordGrVpu", 4, TsLibUtils.EMPTY_STRING,
    // TsLibUtils.EMPTY_STRING );
    // addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_GRSTOPING, "Стоп ГР",
    // "Отключение гидрораспределителя в группе ГР/ВПУ", "rtdStateWordGrVpu", 5, TsLibUtils.EMPTY_STRING,
    // TsLibUtils.EMPTY_STRING );
    // addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_VPUSTARTING, "Пуск ВПУ\\авто",
    // "Пуск двигателя ВПУ в группе ГР/ВПУ", "rtdStateWordGrVpu", 6, TsLibUtils.EMPTY_STRING,
    // TsLibUtils.EMPTY_STRING );
    // addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_VPUSTOPING, "Стоп ВПУ",
    // "Отключение двигателя ВПУ в группе ГР/ВПУ", "rtdStateWordGrVpu", 7, TsLibUtils.EMPTY_STRING,
    // TsLibUtils.EMPTY_STRING );
    // addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_GEARERROR, "Ошибка зацепления",
    // "Ошибка определения положения муфты зацепления", "rtdStateWordGrVpu", 8, TsLibUtils.EMPTY_STRING,
    // TsLibUtils.EMPTY_STRING );
    // addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_OILFAILURE, "Масла нет",
    // "Отсутствует валидное давление масла при включенном ПМН", "rtdStateWordGrVpu", 9, TsLibUtils.EMPTY_STRING,
    // TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_PWR_OK, "Питание ШС норма", "Питание ШС норма",
        "rtdPwrWord", 0, TsLibUtils.EMPTY_STRING, "включено" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_FEEDER1ON, "Фидер 1 включен", "Фидер 1 включен",
        "rtdPwrWord", 1, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_FEEDER2ON, "Фидер 2 включен", "Фидер 2 включен",
        "rtdPwrWord", 2, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_FEEDER1_OK, "Фидер 1 норма", "Фидер 1 норма",
        "rtdPwrWord", 3, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_FEEDER2_OK, "Фидер 2 норма", "Фидер 2 норма",
        "rtdPwrWord", 4, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_L72_OK, "L72 норма", "L72 норма", "rtdPwrWord", 9,
        TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_G1_OK, "БП G1 норма", "Блок питания G1 в норме",
        "rtdPwrWord", 5, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_G2_OK, "БП G2 норма", "Блок питания G2 в норме",
        "rtdPwrWord", 6, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_G3_OK, "БП G3 норма", "Блок питания G3 в норме",
        "rtdPwrWord", 7, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_G4_OK, "БП G4 норма", "Блок питания G4 в норме",
        "rtdPwrWord", 8, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_L73_OK, "L73 норма",
        "L73 питание гидрораспределителя в норме", "rtdPwrWord", 10, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_ENGINEBLOCK, "Есть блокировка", "Есть блокировка привода",
        "rtdWB_AUTO", 0, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_ENGINEALARM, "Есть авария", "Есть авария привода",
        "rtdWB_AUTO", 1, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_EMERGENCYBUTTONSHU, "Кнопка Авар.Стоп ШУ",
        "Кнопка Авар.Стоп ШУ нажата", "rtdWB_AUTO", 2, TsLibUtils.EMPTY_STRING, "нажата" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_EMERGENCYBUTTONSHS, "Кнопка Авар.Стоп ШС",
        "Кнопка Авар.Стоп ШС нажата", "rtdWB_AUTO", 3, TsLibUtils.EMPTY_STRING, "нажата" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_EXTEMERGENCYBUTTON, "Кнопка Авар.Стоп внешняя",
        "Кнопка Авар.Стоп внешняя нажата", "rtdWB_AUTO", 4, TsLibUtils.EMPTY_STRING, "нажата" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_BLOCKSTARTSHU, "Блокировка запуска ШУ",
        "Блокировка запуска агрегата на двери ШУ", "rtdWB_AUTO", 5, "разблокировано", "заблокировано" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_DISABLEALARM, "Блокировка сигнализации",
        "Блокировка запуска агрегата на двери ШУ", "rtdWB_AUTO", 6, "разблокировано", "заблокировано" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_POWERFAILURE, "Авария по питанию", "Авария по питанию",
        "rtdWB_AUTO", 7, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_LOC, "Режим управления Местный",
        "Режим управления Местный", "rtdWB_AUTO", 8, TsLibUtils.EMPTY_STRING, "-" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_VVALARM, "Авария ячейки ВВ",
        "Авария ячейки ВВ — сигнал из ячейки", "rtdWB_AUTO", 10, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_VVBLOCK, "Нет готовности ячейки ВВ",
        "Нет готовности ячейки ВВ — сигнал из ячейки", "rtdWB_AUTO", 11, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_LOWOILLEVEL, "Нет готовности ячейки ВВ",
        "Низкий уровень масла в маслобаке", "rtdWB_AUTO", 12, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_WATERINGED, "Вода в ГЭД", "Вода в ГЭД", "rtdWB_AUTO", 13,
        TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_VTEALARM, "Авария ТВУ", "Авария ТВУ", "rtdWB_AUTO", 14,
        TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_VTEBLOCK, "Нет готовности ТВУ", "Нет готовности ТВУ",
        "rtdWB_AUTO", 15, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_BLOWFAILURE, "Не выполнена продувка",
        "Не выполнена продувка воздухом ГЭД перед пуском", "rtdWB_MAN", 0, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_AIALARM, "Есть аларм параметра",
        "Есть аварийный уровень технологической блокировки", "rtdWB_MAN", 1, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_VPUON, "Включен ВПУ", "Включен двигатель ВПУ",
        "rtdWB_MAN", 23, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_GRON, "Включен ГР", "Включен гидрораспределитель",
        "rtdWB_MAN", 22, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_CLOSE_ZN_ZB, "ЗН и ЗБ закрыты",
        "ЗН не открыта полностью, ЗБ закрыта — нельзя включать ГЭД", "rtdWB_MAN", 25, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_PANELCONNECT, "Связь с Панелью", "Связь с Панелью",
        "rtdNetWord", 1, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_ARMCONNECT, "Связь с АРМ", "Связь с АРМ", "rtdNetWord", 7,
        TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_ENABLEPANELCONNECTALARM,
        "Разрешение контроля связи с Панелью", "Разрешение контроля связи с Панелью", "rtdEnableNetWord", 1,
        "контроль связи отключен", "контроль связи включен" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_ENABLEARMCONNECTALARM, "Разрешение контроля связи с АРМ",
        "Разрешение контроля связи с АРМ", "rtdEnableNetWord", 7, "контроль связи отключен", "контроль связи включен" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___CTRLSYSTEM_ENABLEARMCONNECTALARM, "Разрешение контроля связи с АРМ",
        "Разрешение контроля связи с АРМ", "rtdEnableNetWord", 7, "контроль связи отключен", "контроль связи включен" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___IRREVERSIBLEENGINE_PWR, "Питание", "Питание в норме/отсутствует",
        "rtdStateWord", 0, TsLibUtils.EMPTY_STRING, "включено" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___IRREVERSIBLEENGINE_PWRCTRL, "Контроль",
        "Напряжение управления в норме / отсутствует", "rtdStateWord", 1, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___IRREVERSIBLEENGINE_ENABLED, "Разрешение", "Разрешение работы",
        "rtdStateWord", 2, "заблокировано", "разрешено" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___IRREVERSIBLEENGINE_IMITATION, "Имитация", "Включен режим имитации",
        "rtdStateWord", 3, "Отключена", "Включена" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___IRREVERSIBLEENGINE_READY, "Готовность", "Готовность привода",
        "rtdStateWord", 4, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___IRREVERSIBLEENGINE_ON, "Включен", "Электропривод включен логически",
        "rtdStateWord", 5, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___IRREVERSIBLEENGINE_AUXON, "Контактор", "Контактор включен",
        "rtdStateWord", 6, "отключено", "включено" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___IRREVERSIBLEENGINE_STOPAUTOBLOCK, "СТОП блокировка",
        "СТОП блокировка контроллером", "rtdStateWord", 13, TsLibUtils.EMPTY_STRING, "-" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___IRREVERSIBLEENGINE_PWRFAILURE, "Питания нет",
        "Питание силовое отсутствует", "rtdErrorWord", 0, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___IRREVERSIBLEENGINE_CTRLFAILURE, "Контроля нет",
        "Нет напряжения управления", "rtdErrorWord", 1, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___IRREVERSIBLEENGINE_ONFAILURE, "Не включился", "Не включился",
        "rtdErrorWord", 2, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___IRREVERSIBLEENGINE_OFFFAILURE, "Не отключился", "Не отключился",
        "rtdErrorWord", 3, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___IRREVERSIBLEENGINE_EXTALARM, "Внешняя авария",
        "Внешняя авария. Силовое питание ШС", "rtdErrorWord", 4, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VALVE_PWR, "Питание", "Питание в норме/отсутствует", "rtdStateWord",
        0, TsLibUtils.EMPTY_STRING, "включено" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VALVE_ENABLED, "Разрешение работы", "Разрешение работы",
        "rtdStateWord", 2, "заблокировано", "разрешено" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VALVE_IMITATION, "Имитация", "Включен режим имитации",
        "rtdStateWord", 3, "Отключена", "Включена" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VALVE_READY, "Готовность", "Готовность привода", "rtdStateWord", 4,
        TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VALVE_ON, "Включен", "Электропривод включен логически",
        "rtdStateWord", 5, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VALVE_PWRFAILURE, "Питания нет", "Питание силовое отсутствует",
        "rtdErrorWord", 0, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_PWR, "Питание", "Питание силовое отсутствует",
        "rtdStateWord", 0, TsLibUtils.EMPTY_STRING, "включено" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_PWRCTRL, "Контроль",
        "Напряжение управления в норме / отсутствует", "rtdStateWord", 1, TsLibUtils.EMPTY_STRING, "появился" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_ENABLED, "Разрешение", "Разрешение работы",
        "rtdStateWord", 2, "заблокировано", "разрешено" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_IMITATION, "Имитация", "Включен режим имитации",
        "rtdStateWord", 3, "Отключена", "Включена" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_OPEN, "Открывается", "Открывается логически",
        "rtdStateWord", 5, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_AUXOPEN, "ДК на открытие",
        "Контактор включен на открытие", "rtdStateWord", 6, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_CLOSE, "Закрывается", "Закрывается логически",
        "rtdStateWord", 7, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_AUXCLOSE, "ДК на закрытие",
        "Контактор включен на закрытие", "rtdStateWord", 8, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_OPENED, "Открыто", "Открыто логически",
        "rtdStateWord", 9, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_LIMITSWITCHOPEN, "КВ открытия",
        "Концевик открытия сработал", "rtdStateWord", 10, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_CLOSED, "Закрыто", "Закрыто логически",
        "rtdStateWord", 11, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_LIMITSWITCHCLOSE, "КВ закрытия",
        "Концевик закрытия", "rtdStateWord", 12, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_STOPAUTOBLOCK, "СТОП блокировка",
        "СТОП блокировка контроллером", "rtdStateWord", 13, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_READYOPEN, "Готовность открытия",
        "Готовность технологическая к открытию", "rtdStateWord", 14, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_READYCLOSE, "Готовность закрытия",
        "Готовность технологическая к закрытию", "rtdStateWord", 15, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_PWRFAILURE, "Питания нет",
        "Питание силовое отсутствует", "rtdErrorWord", 0, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_CTRLFAILURE, "Контроля нет",
        "Нет напряжения управления", "rtdErrorWord", 1, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_ONFAILURE, "Не открылось",
        "Не открылось за время открытия", "rtdErrorWord", 2, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_OFFFAILURE, "Не закрылось",
        "Не закрылось за время закрытия", "rtdErrorWord", 3, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_EXTALARM, "Внешняя авария",
        "Внешняя авария. Силовое питание ШС", "rtdErrorWord", 4, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_OPENONFAILURE, "Не откл. на открытие",
        "Не включился на открытие", "rtdErrorWord", 5, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_OPENOFFFAILURE, "Не вкл. на открытие",
        "Не отключился на открытие", "rtdErrorWord", 6, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_CLOSEONFAILURE, "Не вкл. на закрытие",
        "Не включился на закрытие", "rtdErrorWord", 7, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REVERSIBLEENGINE_CLOSEOFFFAILURE, "Не откл. на закрытие",
        "Не отключился на закрытие", "rtdErrorWord", 8, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___MAINSWITCH_PWRCTRL, "Контроль",
        "Напряжение управления в норме / отсутствует", "rtdStateWord", 1, TsLibUtils.EMPTY_STRING, "появился" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___MAINSWITCH_ENABLED, "Разрешение", "Разрешение работы",
        "rtdStateWord", 2, "заблокировано", "разрешено" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___MAINSWITCH_IMITATION, "Имитация", "Включен режим имитации",
        "rtdStateWord", 3, "Отключена", "Включена" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___MAINSWITCH_ON, "Включен", "Выключатель включен логически",
        "rtdStateWord", 5, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___MAINSWITCH_AUXON, "Допконтакт Вкл.",
        "Допконтакт включенного состояния", "rtdStateWord", 6, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___MAINSWITCH_AUXOFF, "Допконтакт Откл.",
        "Допконтакт отключенного состояния", "rtdStateWord", 7, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___MAINSWITCH_READYSTART, "Готовность старта",
        "Готовность включения технологическая", "rtdStateWord", 8, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___MAINSWITCH_READYSTOP, "Готовность останова",
        "Готовность отключения технологическая", "rtdStateWord", 9, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___MAINSWITCH_CTRLFAILURE, "Контроля нет", "Нет напряжения управления",
        "rtdErrorWord", 1, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___MAINSWITCH_PWRFAILURE, "Невалидное сост.",
        "Невалидное состояние по сигналам допконтактов", "rtdErrorWord", 0, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___MAINSWITCH_ONFAILURE, "Не включился", "Не включился", "rtdErrorWord",
        2, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___MAINSWITCH_OFFFAILURE, "Не отключился", "Не отключился",
        "rtdErrorWord", 3, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_READY, "Готовность привода", "Готовность",
        "rtdStateWord", 4, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_WORK1, "Работа в режиме 1", "Работа в режиме 1",
        "rtdStateWord", 1, TsLibUtils.EMPTY_STRING, "-" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_WORK2, "Работа в режиме 2", "Работа в режиме 2",
        "rtdStateWord", 2, TsLibUtils.EMPTY_STRING, "-" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_WORK3, "Работа в режиме 3", "Работа в режиме 3",
        "rtdStateWord", 3, TsLibUtils.EMPTY_STRING, "-" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_VENTWORK, "Вентиляция запускается",
        "Вентиляция в процессе автоматического алгоритма запуска", "rtdStateWord", 5, "Окончание", "Начало" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_RD1, "Воздух ГЭД РД1",
        "Сработало реле РД1 воздуха под кожухом ГЭД", "rtdStateWord", 6, "отключено", "включено" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_RD2, "Воздух ГЭД РД2",
        "Сработало реле РД2 воздуха под кожухом ГЭД", "rtdStateWord", 7, "отключено", "включено" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_BLOWCMPLT, "Продувка выполнена", "Продувка выполнена",
        "rtdStateWord", 9, TsLibUtils.EMPTY_STRING, "-" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_BLOWTIMER, "Из таймера продувки",
        "Таймер продувки отработал заданное время", "rtdStateWord", 10, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_NOTREADY, "Нет готовности", "Нет готовности",
        "rtdErrorWord", 0, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_FAILUREBLOWING, "Не работает при включенном ГЭД",
        "Не работает при включенном ГЭД", "rtdErrorWord", 1, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_ONFAILUREW1, "Не включился Режим 1",
        "Не включился Режим 1", "rtdErrorWord", 2, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_ONFAILUREW2, "Не включился Режим 2",
        "Не включился Режим 2", "rtdErrorWord", 3, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_ONFAILUREW3, "Не включился Режим 3",
        "Не включился Режим 3", "rtdErrorWord", 4, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___VENTILATION_OFFFAILURE, "Не отключился", "Не отключился",
        "rtdErrorWord", 5, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REGOILPRESSURE_ON, "Регулятор включен/отключен",
        "Регулятор функционирует =1, отключен =0", "rtdStateWord", 1, "отключено", "включено" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REGOILPRESSURE_OUTOFF, "Условие стоп",
        "Флаг отключения выхода регулятора", "rtdStateWord", 2, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___REGOILPRESSURE_OUTON, "Условие старт",
        "Флаг включения выхода регулятора", "rtdStateWord", 3, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___PIDREGDO_ON, "Регулятор включен/отключен",
        "Регулятор функционирует =1, отключен =0", "rtdStateWord", 1, "включен", "отключен" );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___PIDREGDO_ALARM, "Неотработка", "Ошибка регулятора Неотработка",
        "rtdStateWord", 2, TsLibUtils.EMPTY_STRING, TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___PIDREGDO_LIMITUP, "Верхний предел",
        "Достигнут верхний предел параметра регулирования", "rtdStateWord", 3, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
    addBitMaskRbItem( rbBitMasks, ITEMID_BITMASK___PIDREGDO_LIMITDOWN, "Нижний предел",
        "Достигнут Нижний предел параметра регулирования", "rtdStateWord", 4, TsLibUtils.EMPTY_STRING,
        TsLibUtils.EMPTY_STRING );
  }

  private void addBitMaskRbItem( ISkRefbook aRefbook, String aItemId, String aName, String aDescr, String aWordId,
      int aBitNumber, String aFallStr, String aFrontStr ) {
    addBitMaskRbItem( aRefbook, aItemId, aName, aDescr, aWordId, aBitNumber, aFallStr, aFrontStr, false );
  }

  private void addBitMaskRbItem( ISkRefbook aRefbook, String aItemId, String aName, String aDescr, String aWordId,
      int aBitNumber, String aFallStr, String aFrontStr, boolean isRriParam ) {
    RefookItemBuider b = new RefookItemBuider( aItemId, aName, aDescr, aRefbook );
    b.setValue( RBATRID_BITMASK___BITN, AvUtils.avInt( aBitNumber ) );
    // get rriId from item id
    String[] parts = aItemId.split( "\\." ); //$NON-NLS-1$
    if( isRriParam ) {
      b.setValue( RBATRID_BITMASK___IDENTIFICATOR, AvUtils.avStr( "rri" + parts[1] ) );
    }
    else {
      b.setValue( RBATRID_BITMASK___IDENTIFICATOR, AvUtils.avStr( "rtd" + parts[1] ) );
    }
    b.setValue( RBATRID_BITMASK___IDW, AvUtils.avStr( aWordId ) );
    b.setValue( RBATRID_BITMASK___OFF, AvUtils.avStr( aFallStr ) );
    b.setValue( RBATRID_BITMASK___ON, AvUtils.avStr( aFrontStr ) );
    IDtoFullObject rbItem = b.buildItem();
    aRefbook.defineItem( rbItem );

  }

}
