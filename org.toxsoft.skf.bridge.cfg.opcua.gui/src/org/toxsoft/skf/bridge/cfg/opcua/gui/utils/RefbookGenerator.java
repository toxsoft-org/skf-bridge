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
  IDtoAttrInfo ATRINF_RRI_INDEX = DtoAttrInfo.create2( RBATRID_RRI_OPCUA___INDEX, DDEF_INTEGER, //
      TSID_NAME, "Индекс", TSID_DESCRIPTION, "Индекс команды НСИ", //
      TSID_DEFAULT_VALUE, AvUtils.AV_0 //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_RRI_OPCUA___RRIID}.
   */
  IDtoAttrInfo ATRINF_RRI_ID = DtoAttrInfo.create2( RBATRID_RRI_OPCUA___RRIID, DDEF_STRING, //
      TSID_NAME, "Id НСИ", TSID_DESCRIPTION, "Id команды НСИ", //
      TSID_DEFAULT_VALUE, AvUtils.AV_0 //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_RRI_OPCUA___ON}.
   */
  IDtoAttrInfo ATRINF_RRI_ON = DtoAttrInfo.create2( RBATRID_RRI_OPCUA___ON, DDEF_BOOLEAN, //
      TSID_NAME, "0->1", TSID_DESCRIPTION, "срабатывание при 0->1", //
      TSID_DEFAULT_VALUE, AvUtils.AV_FALSE //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_RRI_OPCUA___OFF}.
   */
  IDtoAttrInfo ATRINF_RRI_OFF = DtoAttrInfo.create2( RBATRID_RRI_OPCUA___OFF, DDEF_BOOLEAN, //
      TSID_NAME, "1->0", TSID_DESCRIPTION, "срабатывание при 1->0", //
      TSID_DEFAULT_VALUE, AvUtils.AV_FALSE //
  );

  /**
   * Refbook: RRI_OPCUA - Команды НСИ.
   * <p>
   */
  IDtoRefbookInfo REFBOOK_RRI_OPCUA = DtoRefbookInfo.create( RBID_RRI_OPCUA, ///
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
  IDtoAttrInfo ATRINF_INDEX = DtoAttrInfo.create2( RBATRID_CMD_OPCUA___INDEX, DDEF_INTEGER, //
      TSID_NAME, "Индекс", TSID_DESCRIPTION, "Индекс команды управления", //
      TSID_DEFAULT_VALUE, AvUtils.AV_0 //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #ATRID_CMD_ID }.
   */
  IDtoAttrInfo ATRINF_CMD_ID = DtoAttrInfo.create2( RBATRID_CMD_OPCUA___CMDID, DDEF_STRING, //
      TSID_NAME, "Идентификатор команды", //
      TSID_DESCRIPTION, "Идентификатор команды Uskat", //
      TSID_DEFAULT_VALUE, AvUtils.AV_STR_EMPTY //
  );

  /**
   * Refbook: commands for OPC.
   * <p>
   */
  IDtoRefbookInfo REFBOOK_CMDS_OPCUA = DtoRefbookInfo.create( RBID_CMD_OPCUA, ///
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
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1GENON, "Генерация ВА1 Вкл.",
        "Включить режим Генерации по Верхней Аварии", 7, false, true );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1GENOFF, "Генерация ВА1 Откл.",
        "Отключить режим Генерации по Верхней Аварии", 8, true, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1INDON, "Индикация ВА1 Вкл.",
        "Включить режим Индикации по Верхней Аварии", 9, false, true );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT1INDOFF, "Индикация ВА1 Откл.",
        "Отключить режим Индикации по Верхней Аварии", 10, true, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2GENON, "Генерация ВП2 Вкл.",
        "Включить режим Генерации по Верхнему Предупреждению", 11, false, true );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2GENOFF, "Генерация ВП2 Откл.",
        "Отключить режим Генерации по Верхнему Предупреждению", 12, true, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2INDON, "Индикация ВП2 Вкл.",
        "Включить режим Индикации по Верхнему Предупреждению", 13, false, true );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT2INDOFF, "Индикация ВП2 Откл.",
        "Отключить режим Индикации по Верхнему Предупреждению", 14, true, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3GENON, "Генерация НП3 Вкл.",
        "Включить режим Генерации по Нижнему Предупреждению", 15, false, true );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3GENOFF, "Генерация НП3 Откл.",
        "Отключить режим Генерации по Нижнему Предупреждению", 16, true, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4GENON, "Генерация НА4 Вкл.",
        "Включить режим Генерации по Нижней Аварии", 19, false, true );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4GENOFF, "Генерация НА4 Откл.",
        "Отключить режим Генерации по Нижней Аварии", 20, true, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3INDON, "Индикация НП3 Вкл.",
        "Включить режим Индикации по Нижнему Предупреждению", 17, false, true );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT3INDOFF, "Индикация НП3 Откл.",
        "Отключить режим Индикации по Нижнему Предупреждению", 18, true, false );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4INDON, "Индикация НА4 Вкл.",
        "Включить режим Индикации по Нижней Аварии", 21, false, true );
    addRriRbItem( rbRRIs, ITEMID_RRI_OPCUA___ANALOGINPUT_SETPOINT4INDOFF, "Индикация НА4 Откл.",
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
    RefookItemBuider b = new RefookItemBuider( aItemId, aName, aDescr, aRefbook );
    b.setValue( RBATRID_RRI_OPCUA___INDEX, AvUtils.avInt( aRriIndex ) );
    // get rriId from item id
    String[] parts = aItemId.split( "\\." ); //$NON-NLS-1$
    b.setValue( RBATRID_RRI_OPCUA___RRIID, AvUtils.avStr( "rri" + parts[1] ) );
    b.setValue( RBATRID_RRI_OPCUA___OFF, AvUtils.avBool( aFall ) );
    b.setValue( RBATRID_RRI_OPCUA___ON, AvUtils.avBool( aFront ) );
    IDtoFullObject rbItem = b.buildItem();
    aRefbook.defineItem( rbItem );

  }

  /**
   * Constructor.
   *
   * @param aConn - server
   */
  public RefbookGenerator( ISkConnection aConn ) {
    conn = aConn;
  }

}
