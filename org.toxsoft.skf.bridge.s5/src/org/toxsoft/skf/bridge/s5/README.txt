Правила подключения s5.sysaddons.gateways к конечному серверу проекта.

1. В файле jboss-deployment-structure.xml:
    <jboss-deployment-structure>  
        <deployment>  
             <dependencies>  
                ...  
               <!-- required for S5Connection inside wildfy: -->
                  <module name="org.jboss.xnio" export="true" />
                  <module name="org.jboss.xnio.nio" export="true" />
                  <module name="org.hornetq" export="true" />
              </dependencies>  
        </deployment>  
    </jboss-deployment-structure>               
    
    Если в будущем дистрибутивы wildfly не будут иметь данных модулей (о чем есть предупреждения при загрузке wildfly), 
    то следует предусмотреть другие способы включения данных библиотек в classpath приложения использующего gateways

2. При подключении к удаленному серверу локальный сервер должен быть быть обеспечен через classpath всеми классами и интерфейсами 
   входящими в remote API удаленного сервера. Это можно сделать или через механизм wildfly-modules или непосредственным 
   включением в jar приложения всех необходимых классов remote API.
    
3. Чтобы правильно проводилась синхронизация событий, при выборе учетной записи для подключения к удаленному серверу 
   необходимо использовать учетную запись (объект класса s5.class.User) которой нет на локальном сервере. 

4. Более общее правило (по отношению к п.3): удаленный сервер НЕ ДОЛЖЕН формировать данные или события по объектам ШЛЮЗА
   локального сервера используя для этого другие(не локальный сервер) источники!

5. Определить уровень журналирования для разделов:
      <logger category="org.toxsoft.skf.bridge.s5.supports.S5BackendGatewaySingleton">
        <level name="INFO" />
      </logger>
      <logger category="org.toxsoft.skf.bridge.s5.supports.S5Gateway">
        <level name="INFO" />
      </logger>
    

2017-07-08 mvk    