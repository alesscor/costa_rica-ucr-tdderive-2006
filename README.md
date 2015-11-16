# `tdderive` - Sistema distribuido d�bilmente acoplado para el descubrimiento de conocimiento

Este es el c�digo fuente del desarrollo inicial de un sistema  distribuido d�bilmente acoplado  llamado  `tdderive`,  el  cual  se  encarga  de  planificar  el  procesamiento paralelo  de  uno  o  varios  trabajos  de  miner�a  de  datos.  Aunque  el  proyecto inicialmente  propicia  la  planificaci�n  distribuida  de  una  aplicaci�n  de  miner�a  de datos particular, llamada `dderive`, ofrece la capacidad de favorecer el procesamiento de cualquier aplicaci�n o servicio cuyos trabajos se puedan dividir en subtrabajos de ejecuci�n paralela.

Palabras clave: *middleware*, concurrencia, planificaci�n, sistemas distribuidos, arquitectura de software, lenguaje de programaci�n *java*


## Detalles t�cnicos

Objetivos:

* Facilitar  el  procesamiento  distribuido  y  paralelo  a  aplicaciones  de comportamiento  incremental  sobre  un  ambiente  d�bilmente  acoplado  y heterog�neo, dentro de una red de �rea local o de �rea extensa.
* Ocultar  al usuario final el hecho de que la aplicaci�n se  realiza en forma distribuida  y  paralela,  excluyendo  de  tal  encubrimiento  el  tiempo  de respuesta y la disponibilidad de la aplicaci�n.
* Dar prioridad a la distribuci�n de la aplicaci�n `dderive`.

Caracter�sticas del sistema ligadas a su ambiente:

* Remotidad en sus componentes.
* Concurrencia con asincron�a global.
* Ausencia de estado global.
* Presencia de fallas parciales por ocultar ante el usuario final.
* Heterogeneidad de los sistemas anfitriones.
* Autonom�a de cada sistema anfitri�n.
* Evoluci�n y movilidad en el ambiente que rodea al sistema distribuido.
* Amenazas externas permanentemente presentes.

Propiedades:

* Apertura para proveer m�s servicios y para ser integrado a otros servicios.
* Flexibilidad en los sistemas anfitriones a residir.
* Modularidad en sus componentes dispuestos en m�ltiples capas.
* Transparencia  ante  el  usuario  final  en  las  fallas  (parcialmente)  y  en  la distribuci�n (completamente).
* Confiabilidad  y  disponibilidad  basadas  en  el  aprovechamiento  de  la replicaci�n de recursos.
* Desempe�o para favorecer programas de algoritmos incrementales.
* Escalabilidad (falta comprobar) para apoyar procesamientos grandes.
* Disponibilidad a la federaci�n y al cumplimiento de pol�ticas externas de sistemas m�s complejos (no implementada pero aplicable en el futuro).
* Administrabilidad  implementada  para  manejar  a  `dderive`. No  se  ha implementado para una administraci�n autom�tica de m�s alto nivel.
* Disponibilidad a la provisi�n de la calidad de servicios (no implementada pero realizable).
* Seguridad (no implementada pero realizable).

Modelos de colaboraci�n utilizados:

* Modelo  "cliente-servidor",  en  el  contacto  con  clientes  que  solicitan aplicaciones.
* Modelo "par-a-par", en la administraci�n distribuida y el aprovechamiento de los recursos distribuidos.
* Patrones de colaboraci�n, concurrencia y trabajo en red utilizados:
    * "Aceptador-Conector".
    * "Objeto Activo", bas�ndose en "aceptador-conector".

Herramientas CASE utilizadas en el desarrollo y tecnolog�as de implementaci�n:

* Ambiente de trabajo: *Eclipse 3.1*, para la comunidad abierta.
* Lenguaje de programaci�n: *Java*, segunda edici�n empresarial (*J2EE*).
* Sistema subyacente de comunicaci�n: Biblioteca est�ndar de *sockets*.
* Mecanismos  de  concurrencia  brindados  por  el  lenguaje  de  programaci�n: Sincronizaci�n utilizando la estructura `synchronized`, creaci�n de hilos y control de �stos empleando las clases  `Thread`,  `Runnable` y la capacidad de crear clases an�nimas.
* Tecnolog�a  para  la  provisi�n  de  objetos  persistentes:  *Java  Database  Conectivity* (JDBC).
* Sistema administrador de bases de datos: `HSQLDB` (*JDBC* del �IV tipo�), para la comunidad abierta.
* Sistema para la generaci�n de documentos: *OpenOffice 2.0.3*.

Modelo para la confiabilidad de los protocolos de intercambio:

* Se  utilizan  protocolos  de  consumaci�n  de  dos  fases  y  el  protocolo  de consumaci�n  de  tres  fases,  *2PC* y  *3PC* respectivamente,  con  sus correspondientes  protocolos  de  terminaci�n  y  recuperaci�n.  El  *2PC* se utiliz� para el protocolo de balance de carga din�mico y el *3PC* se aplic� al desarrollo  completo  de  cada  aplicaci�n  (desde  su  inicio  hasta  su consumaci�n).

Administraci�n de recursos distribuidos:

* Tipos  principales  de  recursos:  Las  aplicaciones  son  el  principal  recurso,  en este  caso,  el  de  la  miner�a  de  datos  de  `dderive`.  Cada  aplicaci�n  es  un recurso compuesto por otros varios recursos como: los datos del usuario, el procesamiento  de  tales  datos,  los  archivos  que  componen  los  datos,  los archivos que componen la respuesta del sistema.
* Movilidad de recursos: Los recursos de procesamiento no tienen movilidad: el  nombre  de  alto  nivel  de  un  recurso  est�  asignado  sin  posibilidad  de cambio a la ubicaci�n de bajo nivel asignada a �l. Lo que si tiene movilidad es el trabajo que se procesa.
* Tipo de consistencia de los recursos: Los recursos siempre son consistentes.
* Tipo  de  ubicaci�n  de  los  recursos:  Los  recursos  son  autoubicables  por  su nombre.
* Ubicaci�n  del  control  de  los  recursos:  Totalmente  distribuida,  basada  en agentes (modelo *Par-a-Par*).

Caracter�sticas de la administraci�n de recursos:

- Administraci�n:  totalmente  distribuida  y basada  en  agentes, cada  uno  con informaci�n parcial de su entorno (dominios de balance).
- Administraci�n  de  microprocesador:  distribuici�n  uniforme  de  trabajos (implementada)  y   pol�tica  de  balance  de  carga  hidrodin�mico  de  Hui  y Chanson (no implementada)
- Estrategia de balance de carga: uniforme por demanda. En el balance de carga
hidrodin�mico  (no  implementado),  difusi�n  iniciada  por  el  receptor  de trabajos (i.e. por parte del sistema menos cargado).
- Tipos de procesos administrados: procesos pesados y livianos.
- Planificaci�n  de  procesos:  Unifome  por  demanda.  Regida  por  pol�ticas  de balance hidrodin�mico (Hui y Chanson), de retardo de la ejecuci�n (Hui y Chanson)  y de discriminaci�n  de la calidad  brindada por nodos externos (las tres sin implementar).
- Ejecuci�n  remota:  Sin  migraci�n  de  procesos  y  realizada  en  ambientes heterog�neos,  est�  basada  en  la  creaci�n  anticipada  del  ambiente  que pertenece a una aplicaci�n para su posterior ejecuci�n remota, teniendo el control sobre su terminaci�n remota.
- Modelos de  ejecuci�n remota base:  `NEST`,  `UTOPIA` y  `BALANCE`. De  `NEST` se aprovech�  la  estructura  de  la  identificaci�n  de  procesos  para  ser independientes de la ubicaci�n; de  `UTOPIA` y  `BALANCE` se aprovech� la especificaci�n de su arquitectura para poder estructurar la arquitectura del sistema.

### Tareas por hacer:

- Probar al servidor `tdderive` con una aplicaci�n que le introduzca r�fagas controladas de tareas de tiempo de procesamiento bien pronosticable.
- Activar  la  comunicaci�n  entre  los  componentes  del  servidor `balanceador` y  `planificador`, para llevar a cabo  el balance de carga hidrodin�mico.
- Corregir `printdt` para  que  pueda  interpretar  un  �rbol  de  decisiones descrito por sub�rboles.
- Probar a `tdderive` con m�s aplicaciones similares a `dderive`.
- Especificar con  m�s detalle las clases que componen  el c�digo  fuente  de `tdderive`.
- Brindar una interfaz para configurar los servidores de `tdderive` antes de iniciarse  y  despu�s  de  iniciados.  As�  estando  los  servidores  en  marcha, podr�n recibir �rdenes de �sta aplicaci�n.
- Brindarle  al  servidor  `tdderive` una  m�quina  de  inferencias  clara  y flexible que sea usada por el componente  `planificador` en su toma de decisiones.
- Proveer a `tdderive` de un lenguaje que permita definir de manera f�cil y flexible c�mo debe hacerse la divisi�n de trabajos de cualquier aplicaci�n y c�mo debe hacerse la uni�n de las respuestas.
- Implementar  en  `tdderive` la  devoluci�n  asincr�nica  de  resultados,  por ejemplo mediante correo electr�nico, notificaci�n de  RSS o mediante una simple consulta al servidor, con una clave de acceso.


## El programa `dderive`

El  principal  objetivo  del  programa `dderive`, cuyo autor es Ronald Arg�ello Venegas, es  demostrar  c�mo  se  puede utilizar  la  teor�a  matem�tica  de  la  determinaci�n  para  generar,  de  manera incremental,  �rboles  de  decisi�n  a  partir  de  grandes  colecciones  de  datos dispuestos tabularmente, favoreci�ndose  del m�ltiple procesamiento.  Desde una perspectiva m�s pr�ctica, `dderive` se encarga de descubrir patrones que gu�en  la  toma  de  decisiones  a  partir  de  grandes  cantidades  de  datos;  esta responsabilidad  es  parte  de  todo  un  proceso  de  descubrimiento  de conocimiento.

El usuario final encuentra en `dderive` las siguientes funciones:
- Generaci�n  de  un  �rbol  de  inducci�n  al  emplear  una  de  varias estrategias  para  la  selecci�n  del  mejor  atributo:  la  determinaci�n  de primer y segundo orden, la entrop�a y el �ndice gini.
- Informaci�n sobre el uso de `dderive`.
- Generaci�n  de  un  �rbol  a  partir  de  un  proceso  que  organice  el multiprocesamiento.
- Agregaci�n  de  procesos  de  generaci�n  de  �rboles  para  el multiprocesamiento local.

Las  dos  �ltimas  opciones,  de  bajo  nivel,  se  necesitan  para  que  un  usuario logre  el  m�ltiple  procesamiento.  Si  el  usuario  requiere  que  el  m�ltiple procesamiento se realice en m�s de una computadora, �ste necesita verificar si  los  datos  est�n  al  alcance  de  las  computadoras  participantes,  adem�s  de agregar  los  procesos  computacionales  de  generaci�n  que  desee  en  cada computadora.
