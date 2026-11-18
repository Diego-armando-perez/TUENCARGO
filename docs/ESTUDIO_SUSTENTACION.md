# Estudio para la sustentación oral — TuEncargo, Incremento 1

Documento de estudio del equipo. Curso: Ingeniería de Software 1, Universidad Nacional de Colombia.
Entrega: Incremento 1 del 18 de noviembre de 2026. Repositorio: `https://github.com/Diego-armando-perez/TUENCARGO`.
Todo dato de este archivo se leyó del código en `tuencargo/`. Las afirmaciones que no se pudieron comprobar se marcan con NO VERIFICADO.

---

## 1. Resumen del proyecto

### 1.1 Qué problema resuelve

TuEncargo es un intermediario de compras internacionales. El cliente compra en una tienda de Estados Unidos y envía el paquete a un casillero en Miami. TuEncargo consolida la carga y la envía a Colombia.

El sistema resuelve tres problemas concretos:

1. Cotizar el costo total puesto en Colombia antes de comprar.
2. Comparar el mismo producto entre varias tiendas.
3. Seguir el pedido por etapas, desde el pago hasta la entrega.

### 1.2 Usuarios y roles

| Rol | Quién es | Qué puede hacer | Dónde está definido |
| --- | --- | --- | --- |
| Visitante | Persona sin sesión | Ver la página pública, registrarse, iniciar sesión | `SecurityConfig`, rutas públicas |
| USER | Cliente registrado | Cotizar, comparar, crear pedido, pagar, cancelar, ver su buzón y su casillero | `Role.USER` |
| ADMIN | Administrador de bodega | Todo lo anterior y además administrar tarifas y avanzar pedidos | `Role.ADMIN` |

El rol vive en el enum `Role`, en `tuencargo/src/main/java/com/tuencargo/domain/model/Role.java`.
`Role.authority()` devuelve `ROLE_ADMIN` o `ROLE_USER`.

### 1.3 Qué está implementado, qué está simulado y qué está diferido

| Estado | Elementos | Evidencia |
| --- | --- | --- |
| Implementado real | Registro, login, sesión, casillero, cotización, comparación, pedido, máquina de estados, pago, tarifas, buzón, RBAC, CSRF | Controladores en `infrastructure/web`, servicios en `application/services` |
| Simulado detrás de puertos | Catálogo de tiendas, TRM, pasarela de pago, envío de correo | `MockStoreCatalogAdapter`, `MockExchangeRateAdapter`, `MockPsePaymentAdapter`, `MockEmailAdapter` |
| Diferido a Sprint 2 | Pre-alerta de paquete (FR-07), verificación de cuenta por enlace, peso real en bodega, migraciones Flyway, JaCoCo | No existe la clase `PreAlert`. `AuthService` no crea token. `AdminController` no recibe peso |

La interfaz, el backend y la base de datos son reales. Solo los cuatro sistemas externos están simulados.

---

## 2. Cómo ejecutar el proyecto

### 2.1 Prerrequisitos

| Requisito | Valor real | Fuente |
| --- | --- | --- |
| Java | JDK 21 | `pom.xml`, propiedad `java.version` = 21 |
| Spring Boot | 4.1.2 | `pom.xml`, `spring-boot-starter-parent` |
| Maven | No se instala. Se usa el wrapper `mvnw` | `mvnw`, `mvnw.cmd` en `tuencargo/` |
| Base de datos | H2 en archivo. No se instala nada | `application.properties` |
| Docker | No se usa. No existe `Dockerfile` ni `docker-compose.yml` | Búsqueda en el repositorio |
| Puerto | 8080 | `application.properties`, `server.port=8080` |

### 2.2 Comando único

```bash
git clone https://github.com/Diego-armando-perez/TUENCARGO.git
cd TUENCARGO/tuencargo
./mvnw spring-boot:run
```

En Windows PowerShell el comando es `.\mvnw.cmd spring-boot:run`.
La aplicación queda en `http://localhost:8080`.
La base se crea sola en `tuencargo/data/tuencargo.mv.db`.

### 2.3 Credenciales de prueba

| Rol | Correo | Contraseña | Origen |
| --- | --- | --- | --- |
| ADMIN | `admin@tuencargo.com` | `Admin123*` | `DataSeeder.run` |
| USER | `cliente@tuencargo.com` | `Cliente123*` | `DataSeeder.run` |

`DataSeeder` crea estas cuentas solo si no existen. Por eso el seeder es idempotente.
El seeder escribe las dos contraseñas en el log al arrancar. Eso es una debilidad de seguridad documentada en la sección 14.

### 2.4 Consola H2

| Dato | Valor |
| --- | --- |
| URL | `http://localhost:8080/h2-console` |
| JDBC URL | `jdbc:h2:file:./data/tuencargo;AUTO_SERVER=TRUE` |
| Usuario | `sa` |
| Contraseña | vacía |

`SecurityConfig` deja `/h2-console/**` con acceso público y desactiva CSRF para esa ruta.
Eso es aceptable para la demo, pero es una deuda técnica para producción.

### 2.5 Pruebas automatizadas

Comando: `./mvnw test`.

Resultado real de la ejecución: `Tests run: 3, Failures: 0, Errors: 0, Skipped: 0`. `BUILD SUCCESS`.
Detalle: `DomainRulesTest` ejecuta 2 pruebas. `TuencargoApplicationTests` ejecuta 1 prueba.

---

## 3. Mapa del repositorio

```text
TUENCARGO/
├── README.md                      Instrucciones, credenciales, flujos y arquitectura
├── docs/                          Plan de push y este documento de estudio
└── tuencargo/                     Proyecto Maven
    ├── pom.xml                    Dependencias y versión de Java
    ├── mvnw, mvnw.cmd             Maven Wrapper
    ├── data/                      Base H2 en archivo (se crea al arrancar)
    └── src/
        ├── main/java/com/tuencargo/
        │   ├── TuencargoApplication.java        Arranque de Spring Boot
        │   ├── domain/model/                    13 clases del modelo de negocio
        │   ├── domain/service/                  QuotationCalculator (regla de cálculo)
        │   ├── domain/exception/                DomainException, NotFoundException
        │   ├── application/ports/in/            6 puertos de entrada (casos de uso)
        │   ├── application/ports/out/           10 puertos de salida
        │   ├── application/services/            6 servicios de aplicación
        │   ├── infrastructure/web/              6 controladores, GlobalModelAdvice, Formats
        │   ├── infrastructure/persistence/entity/      6 entidades JPA
        │   ├── infrastructure/persistence/repository/  5 repositorios Spring Data
        │   ├── infrastructure/persistence/adapter/     5 adaptadores de persistencia
        │   ├── infrastructure/adapter/store/    MockStoreCatalogAdapter
        │   ├── infrastructure/adapter/fx/       MockExchangeRateAdapter
        │   ├── infrastructure/adapter/payment/  MockPsePaymentAdapter
        │   ├── infrastructure/adapter/email/    MockEmailAdapter
        │   ├── infrastructure/adapter/security/ BCryptPasswordHasherAdapter
        │   ├── infrastructure/security/         AuthenticatedUser, JpaUserDetailsService
        │   └── infrastructure/config/           AppConfig, SecurityConfig, DataSeeder
        ├── main/resources/
        │   ├── application.properties           Puerto, H2, JPA, TRM
        │   ├── static/css/app.css               Hoja de estilos única, 673 líneas
        │   └── templates/                       16 plantillas Thymeleaf
        └── test/java/com/tuencargo/
            ├── DomainRulesTest.java             2 pruebas de reglas de dominio
            └── TuencargoApplicationTests.java   1 prueba de contexto
```

Conteo real: 73 archivos Java en `src/main`, 2 en `src/test`, 16 plantillas HTML y 1 hoja de estilos.

`GlobalModelAdvice` no es un controlador. Es una clase con `@ControllerAdvice`.
`Formats` no es un controlador. Es un componente de presentación con `@Component("fmt")`.
Los seis controladores son: `HomeController`, `AuthController`, `QuotationController`, `OrderController`, `AdminController` y `NotificationController`.

---

## 4. Arquitectura hexagonal explicada

### 4.1 Qué es un puerto

Un puerto es una interfaz que declara un contrato. El dominio y la aplicación definen el contrato. La infraestructura lo implementa.
En TuEncargo los puertos viven en `application/ports/in` y `application/ports/out`.
Un puerto no tiene anotaciones de Spring ni de JPA. Es Java puro.

### 4.2 Qué es un adaptador

Un adaptador es una clase que implementa un puerto. Vive en `infrastructure`.
Hay dos familias de adaptadores:

1. Adaptadores de persistencia. Traducen objetos de dominio a entidades JPA.
2. Adaptadores de sistemas externos. Simulan tiendas, TRM, pasarela y correo.

Ejemplo: `StoreCatalogPort` es el puerto. `MockStoreCatalogAdapter` es el adaptador.

### 4.3 La regla de dependencias

La regla es `infrastructure → application → domain`.
El dominio no conoce Spring, ni JPA, ni HTTP. Se comprobó con una búsqueda de imports en `com.tuencargo.domain`: no hay ningún import de `org.springframework`, `jakarta.*` ni `javax.*`.
La infraestructura depende del dominio porque lo importa. El dominio nunca importa la infraestructura.
Esta inversión permite cambiar H2 por PostgreSQL, o el simulador PSE por la pasarela real, sin tocar el dominio.

### 4.4 Los 6 puertos de entrada

| Puerto | Métodos | Implementación |
| --- | --- | --- |
| `AuthUseCase` | `register`, `getByEmail` | `AuthService` |
| `QuotationUseCase` | `create`, `get`, `listByUser`, `compare` | `QuotationService` |
| `OrderUseCase` | `createFromQuotation`, `get`, `listByUser`, `listAll`, `advanceLogisticStatus`, `cancel` | `OrderService` |
| `PaymentUseCase` | `pay`; record `PaymentOutcome` | `PaymentService` |
| `NotificationUseCase` | `notifyUser`, `listByUser`, `listAll` | `NotificationService` |
| `WarehouseRateUseCase` | `listAll`, `create`, `toggle`, `delete`, `activeRate` | `WarehouseRateService` |

### 4.5 Los 10 puertos de salida

| Puerto | Métodos | Adaptador |
| --- | --- | --- |
| `UserRepositoryPort` | `save`, `findById`, `findByEmail`, `findAll` | `UserPersistenceAdapter` |
| `QuotationRepositoryPort` | `save`, `findById`, `findByUserId` | `QuotationPersistenceAdapter` |
| `OrderRepositoryPort` | `save`, `findById`, `findByUserId`, `findAll` | `OrderPersistenceAdapter` |
| `NotificationRepositoryPort` | `save`, `findByUserId`, `findAll` | `NotificationPersistenceAdapter` |
| `WarehouseRateRepositoryPort` | `save`, `findById`, `findAll`, `findActive`, `deleteById` | `WarehouseRatePersistenceAdapter` |
| `StoreCatalogPort` | `findOffers(String productUrl)` | `MockStoreCatalogAdapter` |
| `ExchangeRatePort` | `usdToCop()` | `MockExchangeRateAdapter` |
| `PaymentGatewayPort` | `charge(PaymentRequest)`; records `PaymentRequest` y `PaymentResult` | `MockPsePaymentAdapter` |
| `EmailPort` | `send(recipient, subject, body)` | `MockEmailAdapter` |
| `PasswordHasherPort` | `hash`, `matches` | `BCryptPasswordHasherAdapter` |

### 4.6 Flujo de una petición

```text
Navegador
  → GET /quotations/5
  → SecurityConfig (filtro de sesión y RBAC)
  → QuotationController#detail
  → QuotationUseCase (puerto de entrada)
  → QuotationService#get
  → QuotationRepositoryPort (puerto de salida)
  → QuotationPersistenceAdapter
  → JpaQuotationRepository (Spring Data)
  → H2: tabla quotations
  → Quotation (objeto de dominio)
  → plantilla quotations/detail.html
  → HTML al navegador
```

La petición nunca toca una entidad JPA desde el controlador. El controlador recibe objetos de dominio.

---

## 5. Dominio

### 5.1 Clases de `domain/model`

| Clase | Tipo | Propósito |
| --- | --- | --- |
| `Role` | enum | Roles `ADMIN` y `USER`. `authority()` devuelve `ROLE_` + nombre |
| `PaymentMethod` | enum | Medios de pago `PSE`, `CARD`, `TRANSFER`. `label()` da el texto visible |
| `QuotationStatus` | enum | Estados de cotización `VIGENTE`, `CONSUMIDA`, `VENCIDA` |
| `OrderStatus` | enum | Estados del pedido y transiciones permitidas |
| `User` | clase final | Usuario con nombre, correo, hash de contraseña, rol y casillero |
| `Quotation` | clase final | Cotización con vigencia de 24 horas y desglose de costos |
| `Order` | clase final | Pedido con número, total, estado e historial de eventos |
| `OrderEvent` | record | Un cambio de estado con fecha y nota |
| `MoneyBreakdown` | record | Desglose en COP: producto, flete, manejo, servicio, impuesto, total |
| `StoreOffer` | record | Oferta de una tienda: tienda, producto, URL, imagen, precio USD, peso en libras |
| `PriceOption` | record | Una oferta con su desglose calculado, usada en la comparación |
| `WarehouseRate` | record | Tarifa de bodega: precio por libra, manejo, % servicio, % impuestos, activa |
| `Notification` | record | Notificación guardada: destinatario, asunto, cuerpo, categoría, fecha |

### 5.2 La fórmula de `QuotationCalculator`

Archivo: `tuencargo/src/main/java/com/tuencargo/domain/service/QuotationCalculator.java`.
La clase es `final`. El constructor es privado. El método `calculate` es estático.
Todos los valores usan `BigDecimal` con `RoundingMode.HALF_UP` y escala 0 para pesos.

```text
productoCOP     = precioUSD × TRM
fleteCOP        = pesoLibras × precioPorLibraCOP
manejoCOP       = tarifaDeManejoCOP
servicioCOP     = productoCOP × (porcentajeServicio / 100)
baseGravable    = productoCOP + fleteCOP + manejoCOP
impuestoCOP     = baseGravable × (porcentajeImpuesto / 100)
totalCOP        = productoCOP + fleteCOP + manejoCOP + servicioCOP + impuestoCOP
```

Reglas aplicadas en el método `calculate`:

1. Si la oferta, la tarifa o la TRM son nulas, lanza `DomainException`.
2. Si la TRM es cero o negativa, lanza `DomainException`.
3. El servicio se calcula solo sobre el producto. No sobre el flete.
4. El impuesto se calcula sobre producto, flete y manejo. No sobre el servicio.

### 5.3 Ejemplo numérico resuelto

Datos: precio 100.00 USD, peso 2.0 libras, TRM 4100, tarifa de 28000 por libra, manejo 15000, servicio 10 %, impuesto 19 %.
Estos valores son los mismos de la prueba `DomainRulesTest.calculaElCostoTotalPuestoEnColombia`.

| Paso | Operación | Resultado COP |
| --- | --- | --- |
| 1 | producto = 100.00 × 4100 | 410 000 |
| 2 | flete = 2.0 × 28 000 | 56 000 |
| 3 | manejo | 15 000 |
| 4 | servicio = 410 000 × 10 / 100 | 41 000 |
| 5 | base gravable = 410 000 + 56 000 + 15 000 | 481 000 |
| 6 | impuesto = 481 000 × 19 / 100 | 91 390 |
| 7 | total = 410 000 + 56 000 + 15 000 + 41 000 + 91 390 | 613 390 |

La prueba unitaria verifica los seis valores del `MoneyBreakdown` con `assertEquals`.

### 5.4 La máquina de estados de `OrderStatus`

`OrderStatus` tiene exactamente seis valores. No existe el estado "En aduana".

| Valor del enum | Texto de `label()` |
| --- | --- |
| `PENDIENTE_PAGO` | Pendiente de pago |
| `COMPRADO` | Comprado |
| `EN_BODEGA` | Recibido en bodega |
| `EN_TRANSITO` | En tránsito |
| `ENTREGADO` | Entregado |
| `CANCELADO` | Cancelado |

Transiciones permitidas, según `allowedTransitions()`:

| Estado actual | Puede pasar a |
| --- | --- |
| `PENDIENTE_PAGO` | `COMPRADO`, `CANCELADO` |
| `COMPRADO` | `EN_BODEGA`, `CANCELADO` |
| `EN_BODEGA` | `EN_TRANSITO` |
| `EN_TRANSITO` | `ENTREGADO` |
| `ENTREGADO` | Ninguno |
| `CANCELADO` | Ninguno |

`nextLogisticStatus()` devuelve el siguiente estado logístico. `COMPRADO` → `EN_BODEGA`. `EN_BODEGA` → `EN_TRANSITO`. `EN_TRANSITO` → `ENTREGADO`. Cualquier otro devuelve `null`.

`Order.changeStatus` valida la transición con `canTransitionTo`. Si la transición no es válida, lanza `DomainException`. Si es válida, crea un `OrderEvent` y devuelve una copia nueva del pedido. La clase `Order` es inmutable.

```java
// domain/model/Order.java
public Order changeStatus(OrderStatus target, String note, Instant now) {
    if (!status.canTransitionTo(target)) {
        throw new DomainException("Transición no permitida: " + status.label() + " -> " + target.label());
    }
    List<OrderEvent> history = new ArrayList<>(events);
    history.add(new OrderEvent(target, now, note));
    return new Order(id, orderNumber, userId, customerEmail, quotationId, productName, store,
            totalCop, target, createdAt, now, history);
}
```

---

## 6. Casos de uso y servicios

| Servicio | Qué hace | Puertos que usa | Reglas que aplica |
| --- | --- | --- | --- |
| `AuthService` | Registra usuarios y busca por correo | `UserRepositoryPort`, `PasswordHasherPort` | Nombre de mínimo 3 caracteres. Correo válido por expresión regular. Contraseña de mínimo 8 caracteres. Correo único. Rol `USER` por defecto. Casillero generado con código de 6 caracteres |
| `QuotationService` | Crea cotizaciones y compara precios | `StoreCatalogPort`, `ExchangeRatePort`, `QuotationRepositoryPort`, `WarehouseRateRepositoryPort` | URL con `http://` o `https://`. Tiene que existir oferta. Tiene que existir tarifa activa. Vigencia de 24 horas. La comparación ordena de menor a mayor total |
| `OrderService` | Crea pedidos, avanza estados y cancela | `OrderRepositoryPort`, `QuotationRepositoryPort`, `UserRepositoryPort`, `NotificationUseCase` | La cotización debe ser del usuario. La cotización debe estar `VIGENTE` y no vencida. Al crear el pedido la cotización pasa a `CONSUMIDA`. La cancelación exige pertenencia |
| `PaymentService` | Cobra el pedido y aplica el resultado | `OrderRepositoryPort`, `PaymentGatewayPort`, `NotificationUseCase` | El pedido debe ser del usuario. El pedido debe estar `PENDIENTE_PAGO`. El documento debe tener mínimo 6 caracteres. Aprobado → `COMPRADO`. Rechazado → sigue `PENDIENTE_PAGO` |
| `NotificationService` | Envía el correo y guarda copia en el buzón | `EmailPort`, `NotificationRepositoryPort` | Si el correo falla, solo registra un `warn`. La notificación se guarda siempre |
| `WarehouseRateService` | Administra tarifas de bodega | `WarehouseRateRepositoryPort` | Nombre obligatorio. Precio por libra y manejo mayores que cero. Porcentajes entre 0 y 100. Al crear una tarifa, desactiva las demás. No se puede desactivar la única activa. No se puede eliminar la activa |

Ninguno de los seis servicios tiene `@Transactional`. La transacción la abre el adaptador de persistencia.

---

## 7. Flujos de extremo a extremo

Rutas reales tomadas de las anotaciones `@GetMapping` y `@PostMapping`.

| Flujo | Ruta HTTP | Controlador y método | Servicio | Puerto | Adaptador | Tabla |
| --- | --- | --- | --- | --- | --- | --- |
| Registro | `POST /register` | `AuthController#register` | `AuthService#register` | `UserRepositoryPort`, `PasswordHasherPort` | `UserPersistenceAdapter`, `BCryptPasswordHasherAdapter` | `users` |
| Inicio de sesión | `POST /login` | Filtro de Spring Security, sin controlador | `JpaUserDetailsService#loadUserByUsername` | `UserRepositoryPort` | `UserPersistenceAdapter` | `users` |
| Cotización | `POST /quotations` | `QuotationController#create` | `QuotationService#create` | `StoreCatalogPort`, `ExchangeRatePort`, `QuotationRepositoryPort`, `WarehouseRateRepositoryPort` | `MockStoreCatalogAdapter`, `MockExchangeRateAdapter`, `QuotationPersistenceAdapter`, `WarehouseRatePersistenceAdapter` | `quotations` |
| Comparación | `GET /quotations/{id}/compare` | `QuotationController#compare` | `QuotationService#compare` | `StoreCatalogPort`, `WarehouseRateRepositoryPort` | `MockStoreCatalogAdapter`, `WarehouseRatePersistenceAdapter` | Lee `quotations` y `warehouse_rates` |
| Creación de pedido | `POST /quotations/{id}/orders` | `QuotationController#checkout` | `OrderService#createFromQuotation` | `OrderRepositoryPort`, `QuotationRepositoryPort`, `NotificationUseCase` | `OrderPersistenceAdapter`, `QuotationPersistenceAdapter`, `NotificationService` | `purchase_orders`, `quotations`, `notifications` |
| Pago aprobado | `POST /orders/{id}/pay` | `OrderController#pay` | `PaymentService#pay` | `PaymentGatewayPort`, `OrderRepositoryPort` | `MockPsePaymentAdapter`, `OrderPersistenceAdapter` | `purchase_orders`, `order_events`, `notifications` |
| Pago rechazado | `POST /orders/{id}/pay` | `OrderController#pay` | `PaymentService#pay` | `PaymentGatewayPort` | `MockPsePaymentAdapter` | `notifications` |
| Avance logístico | `POST /admin/orders/{id}/advance` | `AdminController#advance` | `OrderService#advanceLogisticStatus` | `OrderRepositoryPort`, `NotificationUseCase` | `OrderPersistenceAdapter` | `purchase_orders`, `order_events`, `notifications` |
| Gestión de tarifas | `GET/POST /admin/rates`, `POST /admin/rates/{id}/toggle`, `POST /admin/rates/{id}/delete` | `AdminController` | `WarehouseRateService` | `WarehouseRateRepositoryPort` | `WarehouseRatePersistenceAdapter` | `warehouse_rates` |
| Notificaciones | `GET /notifications` | `NotificationController#list` | `NotificationService#listByUser` | `NotificationRepositoryPort` | `NotificationPersistenceAdapter` | `notifications` |

### 7.1 Registro e inicio de sesión

1. El visitante abre `GET /register`. `AuthController#form` devuelve `register.html`.
2. Envía el formulario a `POST /register`. El controlador compara `password` y `confirmPassword`.
3. `AuthService#register` valida nombre, correo y contraseña. Genera el casillero con `buildLockerAddress`.
4. `BCryptPasswordHasherAdapter#hash` convierte la contraseña en hash.
5. `UserPersistenceAdapter#save` guarda en la tabla `users`.
6. El sistema redirige a `GET /login?registered`.
7. En el siguiente `POST /login`, Spring Security llama a `JpaUserDetailsService#loadUserByUsername`.
8. `AuthenticatedUser` expone `id`, `fullName`, `email` y `role` a las vistas.

### 7.2 Cotización

1. El cliente entra a `GET /quotations/new`. Ve `quotations/form.html`.
2. Envía el enlace a `POST /quotations`.
3. `QuotationService#create` valida el esquema de la URL.
4. `MockStoreCatalogAdapter#findOffers` devuelve tres ofertas.
5. `WarehouseRateRepositoryPort#findActive` entrega la tarifa activa.
6. `ExchangeRatePort#usdToCop` entrega la TRM de `tuencargo.fx.usd-cop`.
7. `QuotationCalculator#calculate` calcula el desglose.
8. `Quotation.create` fija `expiresAt` en 24 horas.
9. `QuotationPersistenceAdapter#save` guarda la fila en `quotations`.
10. El sistema redirige a `GET /quotations/{id}`.

### 7.3 Comparación de precios

1. El cliente pide `GET /quotations/{id}/compare`.
2. `QuotationController#compare` verifica que la cotización sea del usuario.
3. `QuotationService#compare` busca la tarifa original por `rateId`.
4. Recalcula cada oferta con la TRM guardada en la cotización.
5. Ordena con `Comparator.comparing(option -> option.breakdown().totalCop())`.
6. La vista `quotations/compare.html` resalta la primera opción.
7. El controlador agrega `hasAlternatives` cuando hay más de una oferta.

### 7.4 Creación de pedido

1. El cliente envía `POST /quotations/{id}/orders`.
2. `OrderService#createFromQuotation` verifica la pertenencia de la cotización.
3. Si la cotización está vencida o consumida, la marca `VENCIDA` y lanza `DomainException`.
4. Genera el número con el patrón `TC-yyyyMMdd-XXXX`. Los cuatro dígitos son aleatorios.
5. `Order.fromQuotation` crea el pedido en `PENDIENTE_PAGO` con un evento inicial.
6. Guarda el pedido. Luego guarda la cotización en `CONSUMIDA`.
7. Envía una notificación al cliente. La categoría es `PEDIDO`.
8. Redirige a `GET /orders/{id}`.

### 7.5 Pago aprobado y pago rechazado

1. El cliente envía `POST /orders/{id}/pay` con `method` y `document`.
2. `OrderController#pay` convierte `method` a `PaymentMethod` con `valueOf`.
3. `PaymentService#pay` verifica pertenencia y estado `PENDIENTE_PAGO`.
4. Exige documento de mínimo 6 caracteres.
5. `MockPsePaymentAdapter#charge` rechaza si el documento termina en `9`.
6. Pago aprobado: `changeStatus(COMPRADO)` guarda el evento y notifica con la referencia `PSE-XXXXXXXX`.
7. Pago rechazado: notifica el motivo y el pedido sigue en `PENDIENTE_PAGO`.
8. El controlador muestra un mensaje flash verde o rojo.

### 7.6 Avance logístico por el administrador

1. El administrador abre `GET /admin/orders`.
2. `AdminController#orders` llama a `OrderUseCase#listAll`.
3. Envía `POST /admin/orders/{id}/advance` con una nota opcional.
4. `OrderService#advanceLogisticStatus` calcula el siguiente estado con `nextLogisticStatus`.
5. Si no hay siguiente estado, lanza `DomainException`.
6. `Order.changeStatus` valida y agrega el evento.
7. `OrderPersistenceAdapter#save` guarda el pedido y el evento nuevo.
8. `NotificationService` avisa al cliente.

### 7.7 Gestión de tarifas

1. El administrador abre `GET /admin/rates`.
2. `AdminController#rates` convierte los textos a `BigDecimal` con el método privado `decimal`.
3. El método `decimal` quita los puntos de miles y cambia la coma decimal por punto.
4. `WarehouseRateService#create` valida los cuatro valores.
5. Desactiva todas las tarifas activas con `toggled()`.
6. Guarda la tarifa nueva como activa.
7. Las cotizaciones siguientes usan la tarifa nueva.

### 7.8 Notificaciones

1. Cualquier cambio de estado llama a `NotificationUseCase#notifyUser`.
2. `NotificationService` envía el correo por `EmailPort`. Si falla, solo registra un `warn`.
3. `MockEmailAdapter#send` escribe el correo en el log.
4. `NotificationPersistenceAdapter#save` guarda la fila en `notifications`.
5. El cliente abre `GET /notifications` y ve su buzón.
6. `NotificationController#list` filtra por `userId` del usuario autenticado.
7. El layout muestra el enlace "Buzón" solo con sesión activa.

---

## 8. Persistencia

### 8.1 Tablas y columnas

| Tabla | Columnas y restricciones | Entidad |
| --- | --- | --- |
| `users` | `id` PK autoincremental, `fullName` 120 NOT NULL, `email` 160 NOT NULL UNIQUE, `passwordHash` 100 NOT NULL, `role` 20 NOT NULL, `lockerAddress` 400, `createdAt` NOT NULL | `UserEntity` |
| `quotations` | `id` PK, `userId` NOT NULL, `productUrl` 1000 NOT NULL, `store` 80 NOT NULL, `productName` 300 NOT NULL, `imageUrl` 1000, `priceUsd` 14.2 NOT NULL, `weightLb` 10.3 NOT NULL, `trm` 14.2 NOT NULL, `rateId` NOT NULL, `productCop`, `freightCop`, `handlingCop`, `serviceFeeCop`, `taxCop`, `totalCop` 16.2 NOT NULL, `createdAt`, `expiresAt`, `status` 20 NOT NULL | `QuotationEntity` |
| `purchase_orders` | `id` PK, `orderNumber` 40 NOT NULL UNIQUE, `userId` NOT NULL, `customerEmail` 160 NOT NULL, `quotationId` NOT NULL, `productName` 300 NOT NULL, `store` 80 NOT NULL, `totalCop` 16.2 NOT NULL, `status` 30 NOT NULL, `createdAt`, `updatedAt` | `OrderEntity` |
| `order_events` | `id` PK, `order_id` FK NOT NULL hacia `purchase_orders.id`, `status` 30 NOT NULL, `occurredAt` NOT NULL, `note` 300 | `OrderEventEntity` |
| `warehouse_rates` | `id` PK, `name` 80 NOT NULL, `pricePerLbCop` 16.2 NOT NULL, `handlingFeeCop` 16.2 NOT NULL, `serviceFeePercent` 6.2 NOT NULL, `taxPercent` 6.2 NOT NULL, `active` NOT NULL | `WarehouseRateEntity` |
| `notifications` | `id` PK, `userId` NOT NULL, `recipientEmail` 160 NOT NULL, `subject` 200 NOT NULL, `body` 4000 NOT NULL, `category` 30 NOT NULL, `createdAt` NOT NULL | `NotificationEntity` |

Solo `order_events` tiene clave foránea declarada hacia `purchase_orders`.
Las columnas `quotations.userId`, `quotations.rateId`, `purchase_orders.userId` y `purchase_orders.quotationId` son `Long` simples. No tienen clave foránea.
Las tres columnas `Long` se validan en la capa de aplicación con comparaciones de identificadores.

### 8.2 Estrategia de esquema y seeder

`application.properties` fija `spring.jpa.hibernate.ddl-auto=update`.
No hay Flyway ni Liquibase. No existe la carpeta `src/main/resources/db/migration`.
Hibernate crea las tablas a partir de las entidades en cada arranque.
`DataSeeder` implementa `ApplicationRunner`. Se ejecuta después del arranque del contexto.
`DataSeeder.run` crea el ADMIN, el USER y la tarifa "Tarifa Estandar Miami" solo si no existen.
Los valores de la tarifa sembrada son: 28000 por libra, 15000 de manejo, 10 % de servicio y 19 % de impuestos.
`DataSeeder` escribe las contraseñas de demostración en el log con `log.info`.

### 8.3 Qué garantiza ACID hoy

| Propiedad | Estado | Explicación |
| --- | --- | --- |
| Atomicidad | Parcial | Cada método de adaptador abre su propia transacción con `@Transactional`. Una operación que toca dos agregados usa dos transacciones |
| Consistencia | Sí, a nivel de fila | Restricciones `NOT NULL`, `UNIQUE` en `email` y `orderNumber`, y precisión decimal fija |
| Aislamiento | Sí, por transacción | H2 aplica el nivel de aislamiento por defecto de la base |
| Durabilidad | Sí | H2 en modo archivo escribe en `tuencargo/data/tuencargo.mv.db` |

Lo que no está garantizado: la creación de pedido ejecuta `orders.save` y después `quotations.save(con CONSUMIDA)`. Si el segundo guardado falla, el pedido queda creado y la cotización sigue `VIGENTE`. No hay rollback conjunto porque `OrderService` no tiene `@Transactional`.

---

## 9. Seguridad

### 9.1 Contraseñas con BCrypt

`AppConfig` declara el bean `PasswordEncoder` con `new BCryptPasswordEncoder()`.
`BCryptPasswordHasherAdapter` implementa `PasswordHasherPort` sobre ese bean.
`AuthService#register` guarda solo el valor de `hasher.hash(rawPassword)`.
`JpaUserDetailsService` entrega el hash a Spring Security. Spring compara con `matches`.
La fuerza de BCrypt no se configura en el código. `new BCryptPasswordEncoder()` usa el valor por defecto de la librería.
No hay ninguna contraseña en texto plano en la tabla `users`. Solo el seeder escribe contraseñas en el log.

### 9.2 RBAC por ruta

Reglas tomadas de `SecurityConfig#securityFilterChain`.

| Ruta | Acceso | Regla |
| --- | --- | --- |
| `/` | Público | `permitAll` |
| `/login`, `/register` | Público | `permitAll` |
| `/css/**`, `/error` | Público | `permitAll` |
| `/h2-console/**` | Público | `permitAll`, y CSRF ignorado en esa ruta |
| `/admin/**` | Solo ADMIN | `hasRole("ADMIN")` |
| `/quotations/**` | Autenticado | `anyRequest().authenticated()` |
| `/orders/**` | Autenticado | `anyRequest().authenticated()` |
| `/notifications` | Autenticado | `anyRequest().authenticated()` |
| `POST /logout` | Autenticado | Configurado en `logoutUrl("/logout")` |

Un usuario con rol `USER` que pide `/admin` recibe HTTP 403.
El inicio de sesión por formulario usa `loginPage("/login")` y `defaultSuccessUrl("/", true)`.

### 9.3 CSRF

`SecurityConfig` mantiene CSRF activo por defecto. Solo lo desactiva para `/h2-console/**`.
La plantilla `fragments/layout.html` envía el token en el formulario de salida.
Los formularios con `th:action` reciben el campo oculto del token automáticamente.
Sin el token, el servidor rechaza el `POST` con HTTP 403.

### 9.4 Aislamiento por usuario

| Capa | Mecanismo | Archivo |
| --- | --- | --- |
| Controlador de cotizaciones | `requireAccess` compara `quotation.userId()` con el usuario. Si no coincide, lanza `NotFoundException` | `QuotationController` |
| Controlador de pedidos | `requireAccess` compara `order.userId()`. Lanza `NotFoundException` | `OrderController` |
| Servicio de pedidos | `createFromQuotation` verifica la pertenencia de la cotización | `OrderService` |
| Servicio de pagos | `pay` verifica `order.userId().equals(userId)` | `PaymentService` |
| Servicio de cancelación | `cancel` verifica la pertenencia del pedido | `OrderService` |
| Buzón | `listByUser` filtra por `userId` | `NotificationService` |

`GlobalModelAdvice#handleNotFound` responde HTTP 404 con `error/message.html`. El sistema no revela si el recurso existe.
El ADMIN puede leer cualquier pedido o cotización. El cliente solo lee los propios.

---

## 10. Interfaz y diseño responsive

### 10.1 Plantillas y controladores

| Plantilla | Controlador que la devuelve | Ruta |
| --- | --- | --- |
| `index.html` | `HomeController#index` | `GET /` |
| `login.html` | `AuthController#login` | `GET /login` |
| `register.html` | `AuthController#form` y `AuthController#register` | `GET /register`, `POST /register` |
| `quotations/list.html` | `QuotationController#list` | `GET /quotations` |
| `quotations/form.html` | `QuotationController#form` y `#create` | `GET /quotations/new`, `POST /quotations` |
| `quotations/detail.html` | `QuotationController#detail` | `GET /quotations/{id}` |
| `quotations/compare.html` | `QuotationController#compare` | `GET /quotations/{id}/compare` |
| `orders/list.html` | `OrderController#list` | `GET /orders` |
| `orders/detail.html` | `OrderController#detail` | `GET /orders/{id}` |
| `notifications/list.html` | `NotificationController#list` | `GET /notifications` |
| `admin/home.html` | `AdminController#home` | `GET /admin` |
| `admin/rates.html` | `AdminController#rates` | `GET /admin/rates` |
| `admin/orders.html` | `AdminController#orders` | `GET /admin/orders` |
| `error.html` | Página de error de Spring Boot | Errores no controlados |
| `error/message.html` | `GlobalModelAdvice#handleNotFound` y `#handleDomain` | 404 y 400 |
| `fragments/layout.html` | Fragmentos `head`, `nav`, `flash`, `footer` | Se incluye en todas las vistas |

El casillero se muestra en `index.html`, dentro del bloque con la clase `card locker`.
El botón "Copiar dirección completa" usa un `<script>` al final de `index.html` y `navigator.clipboard.writeText`.
No hay carpeta `static/js`. El único JavaScript es ese bloque en línea.
`Formats` es el bean `fmt`. Las plantillas lo usan para formato de dinero y de fechas en zona `America/Bogota`.

### 10.2 Puntos de quiebre del CSS

`app.css` no usa ningún framework. Es una hoja propia de 673 líneas.

| Punto de quiebre | Qué cambia |
| --- | --- |
| Base, sin media query | Una columna. Contenedor con `min-width: 320px`. Ancho máximo de 1080 px |
| `@media (min-width: 640px)` | `.grid-2` pasa a dos columnas. `.grid-3` pasa a tres columnas. Aumentan tamaños de `h1`, `.hero`, `.card` y `.container` |
| `@media (min-width: 960px)` | Ajusta el relleno de `.nav-inner` y el tamaño de `.nav-links` |

El documento declara `<meta name="viewport" content="width=device-width, initial-scale=1">` en `fragments/layout.html`.
La revisión de CSS es estática. La verificación en navegadores reales a 360 px es NO VERIFICADO.

---

## 11. Adaptadores simulados

### 11.1 Qué simula cada adaptador

| Adaptador | Puerto | Comportamiento real en el código |
| --- | --- | --- |
| `MockStoreCatalogAdapter` | `StoreCatalogPort` | Genera tres ofertas deterministas: Amazon US, Walmart US y eBay US. Usa el `hashCode` de la URL como semilla. El precio base está entre 19.99 y 199.98 USD. El peso base está entre 0.5 y 4.9 libras. Los factores son 1.00/1.00, 0.88/1.10 y 1.12/0.90. No usa red |
| `MockExchangeRateAdapter` | `ExchangeRatePort` | Devuelve el valor de `tuencargo.fx.usd-cop`. El valor por defecto es 4100.00 |
| `MockPsePaymentAdapter` | `PaymentGatewayPort` | Aprueba el pago salvo que el documento termine en `9`. Genera la referencia `PSE-` con 8 caracteres. Registra el resultado en el log |
| `MockEmailAdapter` | `EmailPort` | Escribe `[CORREO SIMULADO]` en el log con destinatario, asunto y cuerpo. No envía correo real |

### 11.2 Por qué la simulación no viola la rúbrica

La rúbrica exige "No UI Mocking for Core Flows": los flujos del Sprint 1 deben funcionar desde la interfaz real, con backend real y base de datos real.

En TuEncargo eso se cumple. El formulario de cotización está en `quotations/form.html`. El controlador es `QuotationController`. El servicio es `QuotationService`. La fila se guarda en la tabla `quotations`. El cliente ve el total real calculado por `QuotationCalculator`.

Lo simulado son los cuatro sistemas de terceros: scraping de tiendas, TRM oficial, pasarela PSE y servidor SMTP. Están detrás de puertos. Cambiar un adaptador no cambia el dominio, ni el controlador, ni la base de datos.
La prueba de que el flujo es real: los datos persisten en H2 y sobreviven al reinicio de la aplicación.

---

## 12. Pruebas

### 12.1 Qué existe hoy

| Archivo | Prueba | Qué verifica |
| --- | --- | --- |
| `src/test/java/com/tuencargo/DomainRulesTest.java` | `calculaElCostoTotalPuestoEnColombia` | Los seis valores de `MoneyBreakdown` para 100 USD, 2 libras, TRM 4100 y la tarifa de 28000/15000/10/19. Total esperado: 613390 |
| `src/test/java/com/tuencargo/DomainRulesTest.java` | `respetaLaMaquinaDeEstadosDelPedido` | `PENDIENTE_PAGO` es pagable. La transición a `COMPRADO` funciona y agrega un evento. La transición directa a `ENTREGADO` lanza `DomainException` |
| `src/test/java/com/tuencargo/TuencargoApplicationTests.java` | `contextLoads` | El contexto de Spring arranca con H2 en memoria `jdbc:h2:mem:tuencargo-test` y `ddl-auto=create-drop` |

No hay pruebas de controladores. No hay pruebas de persistencia con base real. No hay pruebas de seguridad.

### 12.2 Resultado real de `./mvnw test`

Salida observada:

```text
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- in com.tuencargo.DomainRulesTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 -- in com.tuencargo.TuencargoApplicationTests
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

El proyecto no tiene el plugin JaCoCo en `pom.xml`. Por eso no existe un porcentaje de cobertura medido. La cifra de cobertura es NO VERIFICADO.

### 12.3 Plan de pruebas para el Sprint 2

1. Agregar JaCoCo al `pom.xml` y publicar el reporte en cada Pull Request.
2. Escribir pruebas de servicio con JUnit 5 y Mockito para `AuthService`, `QuotationService`, `OrderService`, `PaymentService` y `WarehouseRateService`.
3. Escribir pruebas de controlador con MockMvc para verificar HTTP 403 en `/admin/**`.
4. Escribir pruebas de integración con H2 en memoria para `OrderPersistenceAdapter` y `QuotationPersistenceAdapter`.
5. Agregar pruebas de regresión para los casos borde: TRM cero, tarifa inactiva, cotización vencida, documento corto.
6. Medir latencia con una herramienta de carga para verificar NFR-01 y NFR-02.

---

## 13. Requisitos y trazabilidad

Estados usados: Implementado, Parcial, En progreso, Diferido.

### 13.1 Requisitos funcionales

| ID | Requisito | Estado | Clases que lo cubren |
| --- | --- | --- | --- |
| FR-01 | Registro con correo y contraseña, más activación por enlace | Parcial | `AuthService#register`, `AuthController`, `User`, `UserEntity`, `BCryptPasswordHasherAdapter`. No existe el enlace de verificación |
| FR-02 | Casillero único en Miami | Implementado | `AuthService#buildLockerAddress`, `User`, `index.html`, `DataSeeder` |
| FR-03 | Cotización desde la URL del producto | Implementado con catálogo simulado | `QuotationService#create`, `StoreCatalogPort`, `MockStoreCatalogAdapter`, `Quotation`, `QuotationEntity` |
| FR-04 | Costo total en COP con desglose | Implementado | `QuotationCalculator#calculate`, `MoneyBreakdown`, `WarehouseRate`, `quotations/detail.html` |
| FR-05 | Comparación de ofertas ordenada por total | Implementado | `QuotationService#compare`, `PriceOption`, `quotations/compare.html` |
| FR-06 | Pago con PSE o tarjeta y registro del resultado | Implementado con pasarela simulada | `PaymentService#pay`, `PaymentGatewayPort`, `MockPsePaymentAdapter`, `PaymentMethod` |
| FR-07 | Pre-alerta de paquete al casillero | Diferido | No existe ninguna clase. No hay entidad ni ruta |
| FR-08 | Seguimiento por etapas con fecha y hora | Implementado, con cinco estados | `Order`, `OrderStatus`, `OrderEvent`, `OrderService#advanceLogisticStatus`, `orders/detail.html`. No existe el estado "En aduana" |
| FR-09 | Administración de tarifas y avance logístico | Parcial | `AdminController`, `WarehouseRateService`, `OrderService#advanceLogisticStatus`. No existe el registro del peso real en bodega |
| FR-10 | Notificación al usuario en cada cambio de estado | Implementado con correo simulado | `NotificationService`, `EmailPort`, `MockEmailAdapter`, `Notification`, `NotificationEntity`, `NotificationController` |

### 13.2 Requisitos no funcionales

| ID | Requisito | Estado | Evidencia en el código |
| --- | --- | --- | --- |
| NFR-01 | Lecturas menores a 1,5 s en p95 con 100 usuarios | En progreso | No hay prueba de carga. La medición es NO VERIFICADO |
| NFR-02 | Cotización menor o igual a 8 s en p95 | En progreso | El catálogo es local y no usa red. La medición es NO VERIFICADO |
| NFR-03 | Contraseñas con BCrypt, TLS, sin datos de tarjeta | Parcial | `BCryptPasswordHasherAdapter` y ausencia de campos de tarjeta. TLS no está configurado en desarrollo |
| NFR-04 | Solo ADMIN accede a funciones administrativas | Implementado | `SecurityConfig` con `hasRole("ADMIN")`, `AuthenticatedUser#isAdmin` |
| NFR-05 | Cotizar con la última TRM válida si falla la API | En progreso | `MockExchangeRateAdapter` devuelve un valor fijo. No hay caché con antigüedad ni ruta de fallo |
| NFR-06 | Flujo cotizar → pagar sin ayuda externa | En progreso | Los formularios conservan datos y muestran mensajes. No hay prueba con usuarios. La medición es NO VERIFICADO |
| NFR-07 | Diseño adaptable desde 360 px | Implementado por revisión de CSS | `app.css` con `min-width: 320px` y media queries en 640 px y 960 px. La prueba en navegadores es NO VERIFICADO |
| NFR-08 | Pruebas unitarias y cobertura mayor o igual a 70 % | En progreso | Existen 3 pruebas. No hay JaCoCo. La cobertura es NO VERIFICADO |

---

## 14. Limitaciones conocidas y deuda técnica

| # | Limitación | Riesgo | Corrección propuesta |
| --- | --- | --- | --- |
| 1 | El esquema usa `ddl-auto=update`. No hay Flyway | Un cambio de entidad puede alterar la tabla sin historial ni reversión | Agregar Flyway con migraciones versionadas y cambiar a `ddl-auto=validate` |
| 2 | `userId`, `quotationId` y `rateId` no son claves foráneas | Una fila puede apuntar a un usuario o a una cotización inexistente | Agregar `@ManyToOne` o migraciones con `FOREIGN KEY` |
| 3 | `OrderService` y `PaymentService` no tienen `@Transactional` | Un fallo entre dos guardados deja datos inconsistentes | Anotar los métodos de servicio que tocan dos agregados |
| 4 | Falta el estado "En aduana" del FR-08 original | El seguimiento no refleja la etapa aduanera | Agregar `EN_ADUANA` al enum y ajustar las transiciones y las vistas |
| 5 | No existe el registro del peso real en bodega (FR-09) | El flete se cobra con el peso estimado | Agregar peso real a `Order` y recalcular el total antes del despacho |
| 6 | FR-07 (pre-alerta) no existe | El cliente no puede avisar un paquete comprado por su cuenta | Crear el agregado `PreAlert` con su tabla, su ruta y su vista |
| 7 | No hay verificación de cuenta por enlace | Cualquier correo inventado puede registrarse | Agregar token de un solo uso y estado `PENDING_VERIFICATION` |
| 8 | `/h2-console` es pública y sin CSRF | Cualquier persona con acceso al puerto lee toda la base | Restringir por perfil y deshabilitar en producción |
| 9 | `DataSeeder` escribe las contraseñas en el log | Las credenciales quedan en archivos de log | Registrar solo el correo y leer la contraseña de una variable de entorno |
| 10 | No hay JaCoCo ni meta de cobertura medida | No se sabe qué porcentaje del código está probado | Agregar el plugin y un umbral mínimo en el `pom.xml` |
| 11 | NFR-01 y NFR-02 no están medidos | No hay evidencia de rendimiento para la sustentación | Ejecutar una prueba de carga y registrar percentiles |
| 12 | NFR-05 no tiene respaldo real de TRM | Si la TRM real falla, no hay valor anterior | Guardar la última TRM válida con su fecha y usarla como respaldo |
| 13 | No hay HTTPS en desarrollo | El tráfico viaja sin cifrar | Configurar TLS en el despliegue y en el proxy |
| 14 | El número de pedido usa cuatro dígitos aleatorios por día | Puede existir una colisión y fallar el guardado por `UNIQUE` | Usar una secuencia o el identificador de la cotización |
| 15 | No hay límite de intentos de inicio de sesión | Un atacante puede probar contraseñas sin freno | Agregar bloqueo temporal o limitación de tasa |
| 16 | `navigator.clipboard` exige contexto seguro | El botón de copiar no funciona en HTTP remoto | Documentar que se usa en `localhost`, o agregar un respaldo |
| 17 | Las vistas reciben objetos de dominio, no DTO | Un cambio en el modelo de dominio cambia la plantilla | Evaluar DTO de vista si el modelo crece |
| 18 | El correo se escribe en el log | No hay envío real ni plantilla de correo | Implementar SMTP real con plantillas por categoría |

---

## 15. Hoja de ruta del Sprint 2

1. Lógica de negocio avanzada: pre-alerta (FR-07), verificación de cuenta, peso real en bodega, estado "En aduana", consolidación de paquetes y reembolsos por cancelación.
2. Integraciones externas: scraping real detrás de `StoreCatalogPort`, API de TRM con caché y respaldo, pasarela PSE en sandbox y SMTP real.
3. Pruebas automatizadas: JUnit 5, Mockito, AssertJ, MockMvc, pruebas de persistencia con H2 y JaCoCo con umbral del 70 % en servicios.
4. Persistencia: Flyway, claves foráneas declaradas, índices por `userId` y `status`, y `@Transactional` en los servicios.
5. Operación: perfiles `dev` y `prod`, PostgreSQL para producción, consola H2 deshabilitada, HTTPS y tubería de integración continua en GitHub Actions.
6. Seguridad: limitación de intentos, rotación de sesión y política de contraseñas.

---

## 16. Rúbrica y cómo responder

| Criterio | Puntos | Evidencia del proyecto | Cómo defenderlo |
| --- | --- | --- | --- |
| 1. Cobertura funcional del Sprint 1 | 25 | 8 de 10 requisitos funcionales de punta a punta. Flujos de registro, cotización, comparación, pedido, pago, seguimiento, tarifas y buzón | Mostrar el recorrido en vivo y la tabla de la sección 13 |
| 2. Adherencia arquitectónica y patrones | 20 | Paquetes `domain`, `application`, `infrastructure`. Regla de dependencia comprobada. Puertos y adaptadores. `QuotationCalculator` sin Spring | Mostrar que `domain` no importa Spring ni JPA. Explicar un puerto y su adaptador |
| 3. Persistencia e integración de datos | 15 | Seis tablas, restricciones `NOT NULL` y `UNIQUE`, `order_events` con clave foránea, seeder idempotente, `BigDecimal` | Abrir `/h2-console` y mostrar las tablas y las filas |
| 4. Ejecución de NFR | 15 | BCrypt, RBAC con 403, CSRF, CSS adaptable, aislamiento por usuario | Probar `/admin` con el usuario cliente y mostrar el 403 |
| 5. Calidad de código, Git y documentación | 5 | Ramas `feature/*`, un Pull Request por módulo, revisor distinto al autor, siete commits por integrante, README completo | Mostrar el historial y la lista de Pull Requests |
| 6. Demostración y defensa oral | 20 | Recorrido de siete pasos y este documento de estudio | Repartir los pasos del guion de la sección 19 |

Regla de oro para la defensa: cada afirmación se respalda con una clase y una decisión de diseño. Ejemplo: "El cálculo vive en `QuotationCalculator` porque es una regla de negocio pura y se prueba sin Spring".

---

## 17. Preguntas y respuestas para la sustentación

### 17.1 Keller Joseph Guzmán Ariza — Administrador de Base de Datos

#### Pregunta 1

**¿Qué motor de base de datos usan y por qué?**
Usamos H2 en modo archivo, configurado en `application.properties` con `jdbc:h2:file:./data/tuencargo;AUTO_SERVER=TRUE`.
Elegimos archivo y no memoria porque los datos deben sobrevivir al reinicio y a la demostración.
H2 no requiere instalación y el proyecto arranca con un solo comando.
`AUTO_SERVER=TRUE` permite abrir la consola web mientras la aplicación corre.
La ruta del archivo es `tuencargo/data/tuencargo.mv.db`.

#### Pregunta 2

**¿Cómo se crea el esquema de la base de datos?**
El esquema se genera con `spring.jpa.hibernate.ddl-auto=update`.
Hibernate lee las seis entidades de `infrastructure/persistence/entity` y crea o ajusta las tablas.
No usamos Flyway ni Liquibase en este incremento.
La limitación es que no hay historial de cambios ni reversión automática.
Para el Sprint 2 vamos a migrar a Flyway con `ddl-auto=validate`.

#### Pregunta 3

**¿Qué garantiza ACID hoy y qué no?**
La consistencia de fila está garantizada por `NOT NULL`, `UNIQUE` en `email` y `orderNumber`, y por los tipos decimales fijos.
La durabilidad está garantizada porque H2 escribe en archivo.
La atomicidad es parcial: cada método de adaptador tiene `@Transactional`, pero `OrderService` no lo tiene.
Si `orders.save` funciona y `quotations.save` falla, el pedido existe y la cotización sigue `VIGENTE`.
La corrección es anotar los métodos de servicio que tocan dos agregados.

#### Pregunta 4

**¿Por qué `user_id` no es una clave foránea?**
Porque en `OrderEntity` y en `QuotationEntity` la columna se declaró como `Long` simple, sin `@ManyToOne`.
La integridad se valida en la aplicación: `PaymentService#pay` compara `order.userId()` con el usuario autenticado.
La única clave foránea real está en `order_events`, con `@JoinColumn(name = "order_id")`.
El riesgo es que una fila apunte a un usuario inexistente si alguien escribe directo en la base.
La corrección es agregar `@ManyToOne` o una migración con `FOREIGN KEY`.

#### Pregunta 5

**¿Por qué usan `BigDecimal` y no `double`?**
Porque el dinero no admite errores de redondeo binario. `double` no representa 0.10 de forma exacta.
`QuotationCalculator` usa `BigDecimal` con `setScale(0, RoundingMode.HALF_UP)` para los pesos.
Las columnas usan precisión fija: `16.2` para pesos y `14.2` para dólares.
El total de la prueba unitaria es exacto: 613390.
`double` habría producido diferencias de centavos entre la cotización y el pago.

#### Pregunta 6

**¿Cómo funciona el seeder y por qué es idempotente?**
`DataSeeder` implementa `ApplicationRunner` y corre después de levantar el contexto.
Antes de crear un usuario consulta `users.findByEmail(email)`. Si existe, no lo crea.
Antes de crear la tarifa consulta `rates.findActive()`. Si hay una activa, no crea otra.
Por eso reiniciar la aplicación no duplica datos ni resetea contraseñas.
La debilidad es que escribe las contraseñas de demostración en el log.

#### Pregunta 7

**¿Qué índices existen y cuáles faltan?**
Existen los índices implícitos de la clave primaria y los de las restricciones `UNIQUE` de `users.email` y `purchase_orders.order_number`.
No hay índices explícitos por `user_id` ni por `status`.
Las consultas `findByUserIdOrderByCreatedAtDesc` recorren la tabla completa mientras crece.
Con pocos datos no se nota. Con miles de pedidos sí.
En el Sprint 2 agregamos índices en `purchase_orders.user_id`, `quotations.user_id` y en las columnas de estado.

#### Pregunta 8

**¿Cómo respaldan y recuperan la base?**
La base es un solo archivo `tuencargo/data/tuencargo.mv.db`.
El respaldo consiste en copiar ese archivo con la aplicación detenida.
No hay respaldo automático ni punto de recuperación en el tiempo.
La recuperación consiste en reemplazar el archivo y volver a arrancar.
Para producción el plan es PostgreSQL con respaldos programados y migraciones Flyway.

### 17.2 Thomas Enrique Rincón Parra — QA Engineer

#### Pregunta 1

**¿Qué pruebas automatizadas existen y qué verifican?**
Existen tres pruebas en dos archivos.
`DomainRulesTest.calculaElCostoTotalPuestoEnColombia` verifica los seis valores del desglose y el total de 613390.
`DomainRulesTest.respetaLaMaquinaDeEstadosDelPedido` verifica una transición válida y una inválida.
`TuencargoApplicationTests.contextLoads` verifica que el contexto de Spring arranca con H2 en memoria.
El resultado real es `Tests run: 3, Failures: 0, Errors: 0`.

#### Pregunta 2

**¿Cuál es la diferencia entre una prueba unitaria y una de integración en este proyecto?**
La prueba unitaria no levanta Spring. Llama a `QuotationCalculator.calculate` directamente.
La prueba de integración levanta el contexto con `@SpringBootTest` y una base H2 en memoria.
`TuencargoApplicationTests` es de integración porque carga todos los beans y la configuración de seguridad.
La prueba de dominio es rápida: 0.123 segundos. La de contexto tarda 10.70 segundos.
Esa diferencia de tiempo es una razón práctica para probar la lógica de negocio sin Spring.

#### Pregunta 3

**¿Qué falta para medir la cobertura al 70 %?**
Falta agregar el plugin JaCoCo al `pom.xml`. Hoy el archivo no lo declara.
Sin ese plugin no hay archivo de reporte y no hay porcentaje medido.
El plan es ejecutar `mvn verify` y publicar el reporte por cada Pull Request.
El umbral se configura con una regla de mínimo en el plugin.
Hasta entonces, la cifra de cobertura es NO VERIFICADO.

#### Pregunta 4

**¿Cómo probarían el RBAC de forma automática?**
Con una prueba de controlador con `MockMvc` y un usuario autenticado con rol `USER`.
La prueba pide `GET /admin` y espera el estado HTTP 403.
Otra prueba autentica un ADMIN y espera el estado HTTP 200.
La regla está en `SecurityConfig`, en `requestMatchers("/admin/**").hasRole("ADMIN")`.
Hoy esa verificación se hizo de forma manual, no automática.

#### Pregunta 5

**¿Cómo reproducen un pago rechazado en la demo?**
El adaptador `MockPsePaymentAdapter#charge` rechaza cuando el documento termina en `9`.
La regla está en una sola línea: `request.payerDocument().trim().endsWith("9")`.
Para reproducirlo se paga con el documento `1020304059`.
El pedido sigue en `PENDIENTE_PAGO` y se muestra el motivo del rechazo.
Con `1020304050` el pago se aprueba y el pedido pasa a `COMPRADO`.

#### Pregunta 6

**¿Qué casos borde no están cubiertos hoy?**
No hay prueba para la TRM en cero, aunque `QuotationCalculator` la rechaza con `DomainException`.
No hay prueba para el documento de pago con menos de 6 caracteres.
No hay prueba para una cotización vencida ni para una tarifa inactiva.
No hay prueba de persistencia ni de controladores.
El plan de la sección 12.3 los incluye como prioridad del Sprint 2.

#### Pregunta 7

**¿Cómo verifican que un cliente no lee el pedido de otro?**
Se crea un pedido con el usuario A y se inicia sesión con el usuario B.
El usuario B pide `GET /orders/{id}` con el identificador del pedido de A.
`OrderController#requireAccess` lanza `NotFoundException`.
`GlobalModelAdvice#handleNotFound` responde HTTP 404 con la plantilla `error/message.html`.
La prueba automática de ese caso todavía no existe.

#### Pregunta 8

**¿Qué evidencia de calidad entregan al instructor?**
El historial de Git con siete commits por integrante. Ese dato sale de `git log --format="%an"`.
Los Pull Requests con revisor distinto al autor, según `docs/PLAN_PUSH.md`.
El resultado de `./mvnw test` en verde: 3 pruebas, 0 fallos.
El README con prerrequisitos, comando único y credenciales.
La matriz de trazabilidad de la sección 13 de este documento.

### 17.3 Diego Armando Pérez Solano — Desarrollador Frontend

#### Pregunta 1

**¿Qué motor de plantillas usan y cómo se integra con los controladores?**
Usamos Thymeleaf con renderizado en el servidor.
Cada método de controlador devuelve el nombre de la plantilla, por ejemplo `"orders/detail"`.
`spring-boot-starter-thymeleaf` resuelve esa cadena a `templates/orders/detail.html`.
El controlador agrega datos al `Model` y la plantilla los lee con `th:text` y `th:each`.
No hay una API REST intermedia: el HTML sale completo del servidor.

#### Pregunta 2

**¿Cómo evitan duplicar el menú y el pie en las dieciséis plantillas?**
Usamos fragmentos en `templates/fragments/layout.html`.
Ese archivo define `head`, `nav`, `flash` y `footer`.
Las demás plantillas los insertan con `th:replace` y `th:insert`.
El menú se construye con condiciones de rol: `th:if="${isAdmin}"` muestra "Administración".
Los datos comunes los aporta `GlobalModelAdvice`, que agrega `cu` e `isAdmin` a todos los modelos.

#### Pregunta 3

**¿Cómo logran el diseño adaptable desde 360 px?**
La hoja `static/css/app.css` es propia y no usa frameworks.
La base es de una columna, con `min-width: 320px` en el contenedor y ancho máximo de 1080 px.
En `min-width: 640px`, las clases `.grid-2` y `.grid-3` pasan a dos y tres columnas.
En `min-width: 960px` se ajustan el relleno de la barra y el tamaño de los enlaces.
La verificación en navegadores reales a 360 px es NO VERIFICADO.

#### Pregunta 4

**¿Cómo se protegen los formularios contra CSRF?**
Spring Security activa CSRF por defecto en `SecurityConfig`.
Thymeleaf agrega el campo oculto con el token en cada formulario con `th:action`.
El formulario de salida de `fragments/layout.html` envía el token explícito con `_csrf.parameterName` y `_csrf.token`.
Sin el token, el servidor responde 403.
Solo la ruta `/h2-console/**` está excluida.

#### Pregunta 5

**¿Dónde se muestra el casillero del cliente?**
Se muestra en `templates/index.html`, en el bloque con la clase `card locker`.
La condición es `th:if="${account.lockerAddress != null}"`.
El texto sale de `account`, que `HomeController#index` obtiene con `auth.getByEmail`.
El botón "Copiar dirección completa" usa `navigator.clipboard.writeText` con un `<script>` al final de la plantilla.
No existe una pantalla de perfil separada.

#### Pregunta 6

**¿Solo hay CSS o también JavaScript?**
Solo hay una hoja de estilos: `static/css/app.css`.
No existe la carpeta `static/js`.
El único JavaScript es un bloque de once líneas en `index.html` para copiar la dirección del casillero.
Esa decisión reduce dependencias y funciona sin conexión.
La desventaja es que el copiado exige `localhost` o HTTPS.

#### Pregunta 7

**¿Cómo se conservan los datos cuando un formulario falla?**
`AuthController#register` vuelve a agregar `fullName` y `email` al modelo antes de regresar a la vista.
`QuotationController#create` agrega `url` cuando la URL es inválida.
Las plantillas leen esos atributos con `th:value`.
Los mensajes viajan por atributos `error` y `ok`, que el fragmento `flash` muestra.
Así el usuario no vuelve a escribir todo.

#### Pregunta 8

**¿Qué mostraría en pantalla en la demo del frontend?**
Primero la página pública en `http://localhost:8080/` sin sesión.
Después el panel con el casillero y el botón de copiar.
Luego el formulario de cotización y la vista de comparación con la oferta más económica resaltada.
Al final el detalle del pedido con la línea de tiempo de estados.
El ancho se puede reducir en el navegador para mostrar el diseño de una columna.

### 17.4 Jheyrer Andrés Castillo Ripoll — Desarrollador Full Stack

#### Pregunta 1

**¿Por qué usaron arquitectura hexagonal?**
Porque el problema tiene cuatro sistemas externos que cambian con frecuencia: tiendas, TRM, pasarela y correo.
Con puertos y adaptadores el dominio no depende de ninguno de esos sistemas.
Hoy los cuatro son simuladores. Mañana son servicios reales con el mismo contrato.
La regla `infrastructure → application → domain` se comprobó: `domain` no importa Spring ni JPA.
El costo es más interfaces, pero el cambio queda aislado en un solo archivo.

#### Pregunta 2

**¿Qué es un puerto y qué es un adaptador en su código?**
Un puerto es una interfaz. Por ejemplo, `StoreCatalogPort` declara `findOffers(String productUrl)`.
Un adaptador es la clase que la implementa. Por ejemplo, `MockStoreCatalogAdapter`.
Los puertos de entrada están en `application/ports/in` y los de salida en `application/ports/out`.
Los adaptadores viven en `infrastructure`.
El servicio depende de la interfaz, nunca de la clase concreta.

#### Pregunta 3

**¿Cómo funciona la inyección de dependencias?**
Todos los servicios y controladores reciben sus dependencias por constructor.
Spring resuelve qué bean implementa cada interfaz al arrancar.
Por eso `QuotationService` pide un `StoreCatalogPort` y recibe el adaptador simulado sin saber qué es.
No usamos `@Autowired` en campos.
La ventaja es que una prueba puede pasar un doble sin levantar el contexto.

#### Pregunta 4

**¿Dónde están las validaciones y por qué ahí?**
Las validaciones de negocio están en el dominio y en los servicios, no en las plantillas.
`WarehouseRateService#create` exige valores positivos y porcentajes entre 0 y 100.
`QuotationCalculator#calculate` rechaza datos nulos y TRM no positiva.
`AuthService#register` valida nombre, correo y longitud de contraseña.
Las plantillas solo muestran el mensaje que devuelve `DomainException`.

#### Pregunta 5

**¿Cómo manejan los errores de dominio en la interfaz?**
`GlobalModelAdvice` captura `DomainException` y `NotFoundException`.
`DomainException` responde HTTP 400 con `error/message.html`.
`NotFoundException` responde HTTP 404 con la misma plantilla.
Los controladores que atienden formularios capturan la excepción y usan `RedirectAttributes` para mostrar un mensaje flash.
Así el usuario ve un texto claro y no una traza de error.

#### Pregunta 6

**¿Cómo aplicaron el flujo de Git del curso?**
Cada integrante trabajó en una rama `feature/*` por módulo.
Cada rama tiene un Pull Request con revisor distinto al autor.
El plan de ramas, revisores y commits está en `docs/PLAN_PUSH.md`.
La política de fusión es merge commit, no squash, para conservar los commits de cada autor.
Cada integrante tiene siete commits con su propia identidad.

#### Pregunta 7

**¿Por qué hay un servicio de dominio y no solo servicios de aplicación?**
Porque el cálculo es una regla de negocio pura y no necesita infraestructura.
`QuotationCalculator` es `final`, con constructor privado y método estático.
No tiene anotaciones de Spring, así que se prueba en milisegundos.
Los servicios de aplicación orquestan: leen puertos, llaman al dominio y guardan.
Esa separación es la que permite la prueba `DomainRulesTest` sin contexto.

#### Pregunta 8

**¿Qué cambiarían si mañana migran a microservicios?**
Los puertos ya separan el núcleo de los sistemas externos, así que el dominio no cambia.
Habría que separar `StoreCatalogPort`, `PaymentGatewayPort` y `EmailPort` en servicios remotos.
Los adaptadores pasarían de simulación a clientes HTTP.
La base de datos dejaría de ser un archivo H2 y cada servicio tendría la suya.
No es el alcance del curso, pero la arquitectura lo permite sin reescribir las reglas.

### 17.5 Juan David Gil Ortíz — Desarrollador Full Stack

#### Pregunta 1

**¿Cómo se calcula el costo total puesto en Colombia?**
`QuotationCalculator#calculate` multiplica el precio en dólares por la TRM.
Suma el flete, que es peso en libras por precio por libra.
Suma el manejo fijo y el servicio, que es un porcentaje solo del producto.
Calcula el impuesto sobre producto más flete más manejo.
El total es la suma de las cinco líneas.

#### Pregunta 2

**¿Por qué el servicio no se incluye en la base del impuesto?**
Porque la tarifa de bodega define el servicio como comisión de la plataforma.
El impuesto se aplica sobre el valor de la mercancía y su transporte, no sobre la comisión.
La fórmula está en una sola línea de `QuotationCalculator`: `taxableBase = producto + flete + manejo`.
La decisión está documentada en el código y es verificable con la prueba unitaria.
Si la regla cambiara, se cambia un solo método.

#### Pregunta 3

**¿Cómo se ordena la comparación de precios?**
`QuotationService#compare` recalcula cada oferta con la TRM guardada en la cotización.
Luego ordena con `Comparator.comparing(option -> option.breakdown().totalCop())`.
El resultado es una lista de `PriceOption` de menor a mayor total.
La plantilla `quotations/compare.html` resalta la primera.
Con tres tiendas siempre hay alternativas, así que `hasAlternatives` es verdadero.

#### Pregunta 4

**¿Cómo se genera el número de pedido?**
`OrderService#createFromQuotation` arma el texto `TC-` más la fecha `yyyyMMdd`.
Agrega un guion y cuatro dígitos aleatorios de `ThreadLocalRandom`.
Ejemplo: `TC-20261118-0427`.
La columna `orderNumber` es `UNIQUE`, así que una colisión falla al guardar.
El riesgo de colisión es bajo por día, pero existe. La corrección es usar una secuencia.

#### Pregunta 5

**¿Qué pasa si el pago se rechaza?**
`MockPsePaymentAdapter` devuelve `PaymentResult` con `approved` en falso.
`PaymentService#pay` no cambia el estado del pedido.
Se envía una notificación de rechazo con el motivo y la categoría `PAGO`.
`OrderController#pay` muestra el mensaje en rojo con `RedirectAttributes`.
El cliente puede intentar con otro medio o con otro documento.

#### Pregunta 6

**¿Cómo se garantiza que una cotización solo se use una vez?**
`OrderService#createFromQuotation` verifica `quotation.status() != QuotationStatus.VIGENTE`.
Si la cotización ya fue usada, lanza `DomainException` con el mensaje de vencida o usada.
Después de crear el pedido, guarda la cotización con `QuotationStatus.CONSUMIDA`.
También verifica la vigencia de 24 horas con `Quotation.isExpired`.
Falta atomicidad entre los dos guardados, y eso está en la sección 14.

#### Pregunta 7

**¿Qué cambiarían para usar PSE real?**
Solo el adaptador. `PaymentGatewayPort#charge` ya define el contrato con `PaymentRequest` y `PaymentResult`.
Crearíamos `PsePaymentAdapter` con la llamada HTTP a ACH Colombia y las credenciales del comercio.
`PaymentService` no cambia porque depende del puerto.
El dominio tampoco cambia porque no conoce la pasarela.
Habría que agregar manejo de tiempos de espera y de estados pendientes de confirmación.

#### Pregunta 8

**¿Cómo funciona la notificación de cada cambio de estado?**
Cada transición llama a `NotificationUseCase#notifyUser` con usuario, correo, asunto, cuerpo y categoría.
`NotificationService` intenta enviar por `EmailPort` y captura `RuntimeException` si falla.
Siempre guarda la notificación en la tabla `notifications`.
El cliente la lee en `GET /notifications`, que filtra por su `userId`.
Las categorías usadas son `PEDIDO` y `PAGO`.

### 17.6 Preguntas generales para todo el equipo

#### Pregunta 1

**¿Qué pasa si falla el guardado después de marcar la cotización como consumida?**
Ese orden no ocurre. Primero se guarda el pedido y después la cotización.
Si falla el guardado de la cotización, el pedido ya existe y la cotización sigue `VIGENTE`.
El cliente podría crear un segundo pedido con la misma cotización.
La causa es que `OrderService` no tiene `@Transactional`.
La corrección es anotar `createFromQuotation` para que ambos guardados sean una sola transacción.

#### Pregunta 2

**¿Por qué usaron arquitectura hexagonal y no MVC en capas simple?**
Porque hay cuatro sistemas externos y queremos poder sustituirlos sin tocar reglas de negocio.
En MVC simple el controlador suele llamar directo al repositorio y la regla se dispersa.
Aquí el controlador llama a un puerto de entrada y el servicio usa puertos de salida.
La consecuencia práctica: `QuotationCalculator` se prueba sin Spring y sin base de datos.
También se cumple la regla de dependencias que exige la rúbrica.

#### Pregunta 3

**¿Por qué usan `BigDecimal` y no `double`?**
Porque `double` no representa de forma exacta los decimales y acumula error.
`QuotationCalculator` redondea con `RoundingMode.HALF_UP` a escala 0 para pesos.
Las columnas declaran precisión fija, por ejemplo `16.2` en `totalCop`.
La prueba unitaria confirma un total exacto de 613390.
Con `double`, el total podría diferir en centavos y romper la conciliación del pago.

#### Pregunta 4

**¿Cómo evita el sistema que un cliente vea el pedido de otro?**
Con tres controles. `OrderController#requireAccess` compara `order.userId()` con el usuario.
`PaymentService#pay` valida lo mismo antes de cobrar.
`OrderService#cancel` también valida la pertenencia.
Si no coincide, se lanza `NotFoundException` y el cliente recibe 404, no 403.
Así no se revela si el pedido existe.

#### Pregunta 5

**¿Qué está simulado y por qué eso no viola la regla de "sin mocks en flujos core"?**
La regla prohíbe simular la interfaz o el flujo principal.
Aquí la interfaz es Thymeleaf real, el backend son controladores y servicios reales, y la base es H2 real.
Solo están simulados los cuatro sistemas de terceros, detrás de puertos.
Los datos que produce la simulación se guardan en tablas reales y sobreviven al reinicio.
Cambiar un adaptador no cambia el flujo ni la base.

#### Pregunta 6

**¿Dónde está la vigencia de 24 horas de la cotización?**
En `QuotationService`, la constante `VALIDITY` vale `Duration.ofHours(24)`.
`Quotation.create` guarda `expiresAt` como la fecha de creación más esa duración.
`Quotation.isExpired(now)` compara y devuelve verdadero si ya pasó.
`OrderService#createFromQuotation` la consulta antes de crear el pedido.
Si está vencida, la marca `VENCIDA` y rechaza la operación.

#### Pregunta 7

**¿Qué pasa si no hay tarifa de bodega activa?**
`QuotationService#create` busca la tarifa con `rates.findActive()`.
Si la lista está vacía, lanza `DomainException` con el mensaje "No hay una tarifa de bodega activa".
El controlador devuelve el formulario con el mensaje y conserva la URL.
`WarehouseRateService#toggle` impide desactivar la única tarifa activa.
Esa regla protege el flujo de cotización.

#### Pregunta 8

**¿Cómo se audita el avance de un pedido?**
Cada cambio crea un `OrderEvent` con estado, fecha y nota.
`Order.changeStatus` agrega el evento a la lista y devuelve un objeto nuevo.
`OrderPersistenceAdapter#appendNewEvents` inserta solo los eventos que no existen, usando la clave estado más fecha.
La fila queda en `order_events`, con clave foránea hacia `purchase_orders`.
La vista `orders/detail.html` muestra la línea de tiempo completa.

#### Pregunta 9

**¿Qué pasa si dos clientes compran el mismo producto al mismo tiempo?**
Cada cotización es una fila independiente con su `userId` y su `expiresAt`.
No hay inventario compartido ni contador global, así que no hay contención entre clientes.
El único punto sensible es H2 en archivo con `AUTO_SERVER=TRUE`.
Si dos escrituras coinciden, H2 serializa por transacción y puede aparecer un bloqueo breve.
La mitigación real es PostgreSQL en el Sprint 2.

#### Pregunta 10

**¿Qué cambiarían si el profesor pide PostgreSQL mañana?**
Cambiar el `driver`, la URL, el usuario y la contraseña en `application.properties`.
Agregar la dependencia del controlador de PostgreSQL en `pom.xml`.
Reemplazar `ddl-auto=update` por migraciones Flyway.
El dominio, los servicios y los controladores no cambian porque no conocen el motor.
Los adaptadores de persistencia solo cambian si hay dialecto específico.

---

## 18. Glosario

| Término | Definición |
| --- | --- |
| ACID | Propiedades de una transacción: atomicidad, consistencia, aislamiento y durabilidad |
| Adaptador | Clase de infraestructura que implementa un puerto |
| Arancel | Impuesto que se cobra por importar mercancía |
| BCrypt | Algoritmo de hash de contraseñas con sal aleatoria y costo configurable |
| Bean | Objeto que Spring crea y administra en su contenedor |
| BigDecimal | Clase de Java para aritmética decimal exacta |
| Casillero | Dirección física en Miami que el cliente usa para recibir sus compras |
| Controlador | Clase que recibe una petición HTTP y devuelve una vista o una redirección |
| CSRF | Ataque que obliga a un usuario autenticado a enviar una petición sin saberlo |
| `ddl-auto` | Propiedad de JPA que define cómo se genera el esquema desde las entidades |
| DTO | Objeto simple para transportar datos entre capas |
| Entidad JPA | Clase anotada con `@Entity` que representa una tabla |
| Seeder | Componente que inserta datos iniciales al arrancar la aplicación |
| Flyway | Herramienta de migraciones versionadas de base de datos |
| H2 | Motor de base de datos relacional embebido en Java |
| Hash | Transformación irreversible de un valor, usada para guardar contraseñas |
| JaCoCo | Herramienta que mide la cobertura de las pruebas en Java |
| JPA | Especificación de Java para mapear objetos a tablas relacionales |
| MVC | Patrón de separación entre modelo, vista y controlador |
| NFR | Requisito no funcional, como rendimiento o seguridad |
| PSE | Medio de pago colombiano de débito bancario en línea |
| Puerto | Interfaz que define un contrato entre el núcleo y el exterior |
| RBAC | Control de acceso basado en roles |
| Record | Tipo inmutable de Java que genera constructor y accesores |
| Repositorio | Interfaz que oculta el acceso a datos |
| Sesión | Estado de un usuario autenticado entre peticiones |
| SSR | Renderizado del HTML en el servidor antes de enviarlo |
| Thymeleaf | Motor de plantillas HTML para aplicaciones Java |
| Transacción | Unidad de trabajo que se confirma o se revierte completa |
| TRM | Tasa Representativa del Mercado, tasa oficial de USD a COP |
| Value object | Objeto sin identidad propia, definido solo por sus atributos |
| Vista | Plantilla que genera el HTML que ve el usuario |

---

## 19. Guion de demostración

Duración objetivo: cinco minutos. Orden de intervención: Keller abre, Diego muestra la interfaz, Jheyrer muestra la cotización, Juan David muestra el pago, Thomas muestra el panel administrativo y cierra.

| Paso | Quién habla | Entrada exacta | Resultado esperado |
| --- | --- | --- | --- |
| 0 | Keller | `.\mvnw.cmd spring-boot:run` en `tuencargo/` | Log con `Tomcat started on port 8080` y `Started TuencargoApplication` |
| 1 | Diego | Abrir `http://localhost:8080/` sin sesión | Página pública con enlaces "Ingresar" y "Crear cuenta" |
| 2 | Diego | Clic en "Crear cuenta" y enviar `cliente2@tuencargo.com` con `Prueba123*` | Redirección a `/login?registered` y mensaje de cuenta creada |
| 3 | Jheyrer | Iniciar sesión con `cliente@tuencargo.com` y `Cliente123*`, luego "Cotizar" y URL `https://www.amazon.com/dp/B0C123ABC` | Vista con desglose de producto, flete, manejo, servicio, impuestos y total en COP. Vigencia de 24 horas |
| 4 | Jheyrer | En la cotización, clic en "Comparar precios" | Tres ofertas ordenadas de menor a mayor, con la más económica resaltada |
| 5 | Juan David | Clic en continuar al pedido, luego pago con PSE y documento `1020304050` | Pedido en `Comprado`, mensaje con referencia `PSE-XXXXXXXX` y notificación en el buzón |
| 6 | Juan David | Repetir el pago en un pedido nuevo con documento `1020304059` | Pago rechazado, pedido en `Pendiente de pago`, motivo visible |
| 7 | Thomas | Cerrar sesión, entrar como `admin@tuencargo.com` con `Admin123*`, ir a Administración, avanzar el pedido y crear una tarifa | Estado nuevo en la línea de tiempo, notificación al cliente y tarifa anterior desactivada |
| 8 | Thomas | Cerrar sesión, entrar como cliente y abrir el buzón | Notificaciones de pedido creado, pago y avance logístico |
| 9 | Keller | Abrir `http://localhost:8080/h2-console` con `jdbc:h2:file:./data/tuencargo;AUTO_SERVER=TRUE` y usuario `sa` | Tablas `users`, `quotations`, `purchase_orders`, `order_events`, `warehouse_rates`, `notifications` con filas reales |

Consejo para los pasos 7 y 8: hacerlos en dos ventanas del navegador para no perder el estado de la sesión.
