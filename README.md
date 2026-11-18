# TuEncargo — MVP Incremento 1

TuEncargo es un intermediario de compras internacionales: cotiza el costo total puesto en
Colombia desde el link de un producto, compara precios entre tiendas, cobra con medios de pago
locales (PSE simulado), administra el casillero de Miami y hace seguimiento del pedido por etapas.

Proyecto del curso **Ingeniería de Software 1** — Universidad Nacional de Colombia.

## 1. Stack

| Elemento | Tecnología |
| --- | --- |
| Lenguaje / runtime | Java 21 |
| Framework | Spring Boot 4 (MVC + Security + Data JPA) |
| Vistas | Thymeleaf (SSR) + CSS responsive propio |
| Base de datos | H2 en archivo (`./data/tuencargo.mv.db`) |
| Contraseñas | BCrypt |
| Build | Maven Wrapper (`mvnw`) |
| Arquitectura | Hexagonal (puertos y adaptadores) |

## 2. Prerrequisitos

- **JDK 21** instalado y `JAVA_HOME` apuntando a esa instalación.
- No se necesita Maven ni base de datos externa: el wrapper y H2 se resuelven solos.
- Puertos libres: `8080` (aplicación).

## 3. Ejecución en un solo comando

```bash
git clone https://github.com/Diego-armando-perez/TUENCARGO.git
cd TUENCARGO/tuencargo
./mvnw spring-boot:run
```

En Windows (PowerShell):

```powershell
cd TUENCARGO\tuencargo
.\mvnw.cmd spring-boot:run
```

Luego abre <http://localhost:8080>.

> La base H2 se crea automáticamente en `tuencargo/data/` y el **seeder** deja listas las cuentas
> de prueba y la tarifa de bodega activa, por lo que no hay pasos manuales de configuración.

### Pruebas automatizadas

```bash
./mvnw test
```

### Consola H2 (opcional para la demo)

- URL: <http://localhost:8080/h2-console>
- JDBC URL: `jdbc:h2:file:./data/tuencargo;AUTO_SERVER=TRUE`
- Usuario: `sa` · Contraseña: *(vacía)*

## 4. Cuentas preconfiguradas

| Rol | Correo | Contraseña |
| --- | --- | --- |
| ADMIN | `admin@tuencargo.com` | `Admin123*` |
| USER | `cliente@tuencargo.com` | `Cliente123*` |

Cualquier persona puede crear una cuenta nueva desde `/register`; se le asigna automáticamente una
dirección de casillero en Miami.

## 5. Flujo de demostración (5 minutos)

1. **Registro / login** con la cuenta USER.
2. **Cotizar**: pega un link, por ejemplo `https://www.amazon.com/dp/B0C123ABC`, y observa el
   desglose (producto, flete por libra, manejo, servicio, impuestos, total COP).
3. **Comparar precios**: el sistema muestra la misma referencia en tres tiendas ordenada de menor a
   mayor costo total y resalta la más económica.
4. **Confirmar pedido**: el pedido queda en `Pendiente de pago` y notifica al buzón.
5. **Pagar** con PSE usando el documento `1020304050` → pago aprobado, estado `Comprado`.
   Repite con un documento terminado en `9` (por ejemplo `1020304059`) para ver el rechazo.
6. **ADMIN**: inicia sesión como administrador, entra a *Administración → Pedidos* y avanza el
   estado a `Recibido en bodega`, `En tránsito` y `Entregado`. Cada cambio dispara una notificación.
7. **ADMIN → Tarifas de bodega**: crea una tarifa nueva y verifica que la anterior se desactiva y
   que las siguientes cotizaciones usan la nueva tarifa.

## 6. Reglas simuladas (adaptadores de sistemas externos)

Los sistemas externos están detrás de puertos y se implementan con adaptadores simulados, tal como
exige el alcance del incremento:

| Puerto | Adaptador | Comportamiento |
| --- | --- | --- |
| `StoreCatalogPort` | `MockStoreCatalogAdapter` | Genera ofertas deterministas en Amazon US, Walmart US y eBay US a partir del link. |
| `ExchangeRatePort` | `MockExchangeRateAdapter` | TRM fija configurable en `tuencargo.fx.usd-cop` (4100.00 por defecto). |
| `PaymentGatewayPort` | `MockPsePaymentAdapter` | Aprueba el pago salvo que el documento termine en `9`. |
| `EmailPort` | `MockEmailAdapter` | Escribe el correo en el log y en el buzón del usuario (tabla `notifications`). |

## 7. Arquitectura

```
com.tuencargo
├── domain
│   ├── model          Entidades y value objects (User, Quotation, Order, WarehouseRate…)
│   ├── service        Reglas de negocio puras (QuotationCalculator)
│   └── exception      DomainException / NotFoundException
├── application
│   ├── ports.in       Casos de uso (AuthUseCase, QuotationUseCase, PaymentUseCase…)
│   ├── ports.out      Puertos hacia persistencia y sistemas externos
│   └── services       Implementación de los casos de uso
└── infrastructure
    ├── web            Controladores MVC, advice global y utilidades de formato
    ├── persistence    Entidades JPA, repositorios Spring Data y adaptadores
    ├── adapter        Adaptadores simulados (tiendas, TRM, pago, correo)
    ├── security       Principal autenticado y UserDetailsService
    └── config         Seguridad (RBAC), beans y seeder
```

Regla de dependencias: `infrastructure → application → domain`. El dominio no conoce Spring, JPA ni
HTTP; las reglas de negocio (cálculo de la cotización y máquina de estados del pedido) se prueban
sin levantar el contexto.

## 8. Seguridad (NFR)

- Autenticación por formulario con `UserDetailsService` propio.
- Contraseñas almacenadas con **BCrypt** (nunca en texto plano).
- **RBAC**: `/admin/**` requiere `ROLE_ADMIN`; el resto exige autenticación.
- **CSRF** activo en todos los formularios (token oculto en cada `th:action`).
- Aislamiento por usuario: un cliente no puede abrir cotizaciones ni pedidos de otro (404).

## 9. Workflow Git

- Una rama `feature/*` por módulo, un Pull Request por rama y un revisor distinto al autor.
- Commits con la identidad de cada integrante; sin `--amend` de commits ajenos ni cambios de fecha.
- El plan de ramas, PRs y calendario está en [`docs/PLAN_PUSH.md`](docs/PLAN_PUSH.md).
