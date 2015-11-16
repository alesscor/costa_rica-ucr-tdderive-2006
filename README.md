# `tdderive` - Sistema distribuido débilmente acoplado para el descubrimiento de conocimiento

Este es el código fuente del desarrollo inicial de un sistema  distribuido débilmente acoplado  llamado  `tdderive`,  el  cual  se  encarga  de  planificar  el  procesamiento paralelo  de  uno  o  varios  trabajos  de  minería  de  datos.  Aunque  el  proyecto inicialmente  propicia  la  planificación  distribuida  de  una  aplicación  de  minería  de datos particular, llamada `dderive`, ofrece la capacidad de favorecer el procesamiento de cualquier aplicación o servicio cuyos trabajos se puedan dividir en subtrabajos de ejecución paralela.

Palabras clave: *middleware*, concurrencia, planificación, sistemas distribuidos, arquitectura de software, lenguaje de programación *java*


## Detalles técnicos

Objetivos:

* Facilitar  el  procesamiento  distribuido  y  paralelo  a  aplicaciones  de comportamiento  incremental  sobre  un  ambiente  débilmente  acoplado  y heterogéneo, dentro de una red de área local o de área extensa.
* Ocultar  al usuario final el hecho de que la aplicación se  realiza en forma distribuida  y  paralela,  excluyendo  de  tal  encubrimiento  el  tiempo  de respuesta y la disponibilidad de la aplicación.
* Dar prioridad a la distribución de la aplicación `dderive`.

Características del sistema ligadas a su ambiente:

* Remotidad en sus componentes.
* Concurrencia con asincronía global.
* Ausencia de estado global.
* Presencia de fallas parciales por ocultar ante el usuario final.
* Heterogeneidad de los sistemas anfitriones.
* Autonomía de cada sistema anfitrión.
* Evolución y movilidad en el ambiente que rodea al sistema distribuido.
* Amenazas externas permanentemente presentes.

Propiedades:

* Apertura para proveer más servicios y para ser integrado a otros servicios.
* Flexibilidad en los sistemas anfitriones a residir.
* Modularidad en sus componentes dispuestos en múltiples capas.
* Transparencia  ante  el  usuario  final  en  las  fallas  (parcialmente)  y  en  la distribución (completamente).
* Confiabilidad  y  disponibilidad  basadas  en  el  aprovechamiento  de  la replicación de recursos.
* Desempeño para favorecer programas de algoritmos incrementales.
* Escalabilidad (falta comprobar) para apoyar procesamientos grandes.
* Disponibilidad a la federación y al cumplimiento de políticas externas de sistemas más complejos (no implementada pero aplicable en el futuro).
* Administrabilidad  implementada  para  manejar  a  `dderive`. No  se  ha implementado para una administración automática de más alto nivel.
* Disponibilidad a la provisión de la calidad de servicios (no implementada pero realizable).
* Seguridad (no implementada pero realizable).

Modelos de colaboración utilizados:

* Modelo  "cliente-servidor",  en  el  contacto  con  clientes  que  solicitan aplicaciones.
* Modelo "par-a-par", en la administración distribuida y el aprovechamiento de los recursos distribuidos.
* Patrones de colaboración, concurrencia y trabajo en red utilizados:
    * "Aceptador-Conector".
    * "Objeto Activo", basándose en "aceptador-conector".

Herramientas CASE utilizadas en el desarrollo y tecnologías de implementación:

* Ambiente de trabajo: *Eclipse 3.1*, para la comunidad abierta.
* Lenguaje de programación: *Java*, segunda edición empresarial (*J2EE*).
* Sistema subyacente de comunicación: Biblioteca estándar de *sockets*.
* Mecanismos  de  concurrencia  brindados  por  el  lenguaje  de  programación: Sincronización utilizando la estructura `synchronized`, creación de hilos y control de éstos empleando las clases  `Thread`,  `Runnable` y la capacidad de crear clases anónimas.
* Tecnología  para  la  provisión  de  objetos  persistentes:  *Java  Database  Conectivity* (JDBC).
* Sistema administrador de bases de datos: `HSQLDB` (*JDBC* del “IV tipo”), para la comunidad abierta.
* Sistema para la generación de documentos: *OpenOffice 2.0.3*.

Modelo para la confiabilidad de los protocolos de intercambio:

* Se  utilizan  protocolos  de  consumación  de  dos  fases  y  el  protocolo  de consumación  de  tres  fases,  *2PC* y  *3PC* respectivamente,  con  sus correspondientes  protocolos  de  terminación  y  recuperación.  El  *2PC* se utilizó para el protocolo de balance de carga dinámico y el *3PC* se aplicó al desarrollo  completo  de  cada  aplicación  (desde  su  inicio  hasta  su consumación).

Administración de recursos distribuidos:

* Tipos  principales  de  recursos:  Las  aplicaciones  son  el  principal  recurso,  en este  caso,  el  de  la  minería  de  datos  de  `dderive`.  Cada  aplicación  es  un recurso compuesto por otros varios recursos como: los datos del usuario, el procesamiento  de  tales  datos,  los  archivos  que  componen  los  datos,  los archivos que componen la respuesta del sistema.
* Movilidad de recursos: Los recursos de procesamiento no tienen movilidad: el  nombre  de  alto  nivel  de  un  recurso  está  asignado  sin  posibilidad  de cambio a la ubicación de bajo nivel asignada a él. Lo que si tiene movilidad es el trabajo que se procesa.
* Tipo de consistencia de los recursos: Los recursos siempre son consistentes.
* Tipo  de  ubicación  de  los  recursos:  Los  recursos  son  autoubicables  por  su nombre.
* Ubicación  del  control  de  los  recursos:  Totalmente  distribuida,  basada  en agentes (modelo *Par-a-Par*).

Características de la administración de recursos:

- Administración:  totalmente  distribuida  y basada  en  agentes, cada  uno  con información parcial de su entorno (dominios de balance).
- Administración  de  microprocesador:  distribuición  uniforme  de  trabajos (implementada)  y   política  de  balance  de  carga  hidrodinámico  de  Hui  y Chanson (no implementada)
- Estrategia de balance de carga: uniforme por demanda. En el balance de carga
hidrodinámico  (no  implementado),  difusión  iniciada  por  el  receptor  de trabajos (i.e. por parte del sistema menos cargado).
- Tipos de procesos administrados: procesos pesados y livianos.
- Planificación  de  procesos:  Unifome  por  demanda.  Regida  por  políticas  de balance hidrodinámico (Hui y Chanson), de retardo de la ejecución (Hui y Chanson)  y de discriminación  de la calidad  brindada por nodos externos (las tres sin implementar).
- Ejecución  remota:  Sin  migración  de  procesos  y  realizada  en  ambientes heterogéneos,  está  basada  en  la  creación  anticipada  del  ambiente  que pertenece a una aplicación para su posterior ejecución remota, teniendo el control sobre su terminación remota.
- Modelos de  ejecución remota base:  `NEST`,  `UTOPIA` y  `BALANCE`. De  `NEST` se aprovechó  la  estructura  de  la  identificación  de  procesos  para  ser independientes de la ubicación; de  `UTOPIA` y  `BALANCE` se aprovechó la especificación de su arquitectura para poder estructurar la arquitectura del sistema.

### Tareas por hacer:

- Probar al servidor `tdderive` con una aplicación que le introduzca ráfagas controladas de tareas de tiempo de procesamiento bien pronosticable.
- Activar  la  comunicación  entre  los  componentes  del  servidor `balanceador` y  `planificador`, para llevar a cabo  el balance de carga hidrodinámico.
- Corregir `printdt` para  que  pueda  interpretar  un  árbol  de  decisiones descrito por subárboles.
- Probar a `tdderive` con más aplicaciones similares a `dderive`.
- Especificar con  más detalle las clases que componen  el código  fuente  de `tdderive`.
- Brindar una interfaz para configurar los servidores de `tdderive` antes de iniciarse  y  después  de  iniciados.  Así  estando  los  servidores  en  marcha, podrán recibir órdenes de ésta aplicación.
- Brindarle  al  servidor  `tdderive` una  máquina  de  inferencias  clara  y flexible que sea usada por el componente  `planificador` en su toma de decisiones.
- Proveer a `tdderive` de un lenguaje que permita definir de manera fácil y flexible cómo debe hacerse la división de trabajos de cualquier aplicación y cómo debe hacerse la unión de las respuestas.
- Implementar  en  `tdderive` la  devolución  asincrónica  de  resultados,  por ejemplo mediante correo electrónico, notificación de  RSS o mediante una simple consulta al servidor, con una clave de acceso.


## El programa `dderive`

El  principal  objetivo  del  programa `dderive`, cuyo autor es Ronald Argüello Venegas, es  demostrar  cómo  se  puede utilizar  la  teoría  matemática  de  la  determinación  para  generar,  de  manera incremental,  árboles  de  decisión  a  partir  de  grandes  colecciones  de  datos dispuestos tabularmente, favoreciéndose  del múltiple procesamiento.  Desde una perspectiva más práctica, `dderive` se encarga de descubrir patrones que guíen  la  toma  de  decisiones  a  partir  de  grandes  cantidades  de  datos;  esta responsabilidad  es  parte  de  todo  un  proceso  de  descubrimiento  de conocimiento.

El usuario final encuentra en `dderive` las siguientes funciones:
- Generación  de  un  árbol  de  inducción  al  emplear  una  de  varias estrategias  para  la  selección  del  mejor  atributo:  la  determinación  de primer y segundo orden, la entropía y el índice gini.
- Información sobre el uso de `dderive`.
- Generación  de  un  árbol  a  partir  de  un  proceso  que  organice  el multiprocesamiento.
- Agregación  de  procesos  de  generación  de  árboles  para  el multiprocesamiento local.

Las  dos  últimas  opciones,  de  bajo  nivel,  se  necesitan  para  que  un  usuario logre  el  múltiple  procesamiento.  Si  el  usuario  requiere  que  el  múltiple procesamiento se realice en más de una computadora, éste necesita verificar si  los  datos  están  al  alcance  de  las  computadoras  participantes,  además  de agregar  los  procesos  computacionales  de  generación  que  desee  en  cada computadora.
