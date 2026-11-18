# Plan de push — Incremento 1 (5 integrantes)

Reglas que este plan cumple:

- Una rama `feature/*` por módulo y **un Pull Request por rama**.
- Cada PR tiene un **revisor distinto al autor**.
- Los PR se **fusionan en el orden de la tabla** (las dependencias van de arriba hacia abajo).
- Cada integrante hace commit **con su propia cuenta de Git** en su propio módulo.
- **7 commits por integrante** (35 en total) para que la auditoría de contribución quede equilibrada.
- No se reescriben fechas ni se hace `--amend` de commits ajenos.

> Antes de empezar cada integrante configura su identidad **solo en su copia**:
>
> ```bash
> git config user.name  "Nombre Apellido"
> git config user.email "correo@unal.edu.co"
> ```
>
> Verificación para la auditoría: `git log --format="%h %an <%ae> %ad %s" --date=short`.

## 0. Equipo

| Alias | Integrante | Rol en el Entregable 1 | Módulos asignados | Commits |
| --- | --- | --- | --- | --- |
| M1 | Keller Joseph Guzmán Ariza | Administrador de Base de Datos | Base de arquitectura + módulo de administración | 7 |
| M2 | Diego Armando Pérez Solano | Desarrollador Frontend | Dominio + vistas del cliente | 7 |
| M3 | Jheyrer Andrés Castillo Ripoll | Desarrollador Full Stack | Puertos + seguridad | 7 |
| M4 | Juan David Gil Ortíz | Desarrollador Full Stack | Servicios de aplicación + web del cliente | 7 |
| M5 | Thomas Enrique Rincón Parra | QA Engineer | Persistencia + adaptadores + pruebas y documentación | 7 |

> Los alias M2–M5 se pueden intercambiar entre ustedes mientras **cada PR lo haga su dueño**.
> M1 ya tiene el PR 1 commiteado; no se debe rehacer.

## 1. Orden de ramas, autores, revisores y archivos

| # | Rama | Autor | Revisor | Archivos exactos | Commits (en orden) |
| --- | --- | --- | --- | --- | --- |
| 1 | `feature/00-base-arquitectura` | M1 | M3 | `tuencargo/pom.xml`, `tuencargo/src/main/resources/application.properties`, `tuencargo/src/main/java/com/tuencargo/TuencargoApplication.java`, `tuencargo/src/main/java/com/tuencargo/infrastructure/config/AppConfig.java`, `tuencargo/.gitignore` y **eliminación** de `tuencargo/src/main/java/com/tuencargo/tuencargo/TuencargoApplication.java` + `tuencargo/src/test/java/com/tuencargo/tuencargo/TuencargoApplicationTests.java` | 1. `chore(build): dependencias de seguridad, thymeleaf y validacion`<br>2. `chore(config): H2 en archivo y TRM simulada`<br>3. `refactor: mover la aplicacion al paquete raiz com.tuencargo` |
| 2 | `feature/01-dominio` | M2 | M5 | `tuencargo/src/main/java/com/tuencargo/domain/model/{Role,PaymentMethod,QuotationStatus,OrderStatus,StoreOffer,MoneyBreakdown,PriceOption,WarehouseRate,Notification,User,Quotation,OrderEvent,Order}.java`, `domain/service/QuotationCalculator.java`, `domain/exception/{DomainException,NotFoundException}.java` | 1. `feat(domain): enums y value objects del modelo`<br>2. `feat(domain): usuario con rol y casillero`<br>3. `feat(domain): cotizacion con vigencia de 24 horas`<br>4. `feat(domain): pedido y maquina de estados con historial` |
| 3 | `feature/02-puertos` | M3 | M4 | `tuencargo/src/main/java/com/tuencargo/application/ports/in/*.java` (6), `tuencargo/src/main/java/com/tuencargo/application/ports/out/*.java` (10) | 1. `feat(ports): casos de uso del sprint 1`<br>2. `feat(ports): puertos de persistencia`<br>3. `feat(ports): puertos de tiendas, pago, correo y TRM` |
| 4 | `feature/03-persistencia` | M5 | M2 | `tuencargo/src/main/java/com/tuencargo/infrastructure/persistence/entity/*.java` (6), `.../repository/*.java` (5), `.../adapter/*.java` (5) | 1. `feat(persistence): entidades JPA del modelo`<br>2. `feat(persistence): repositorios Spring Data`<br>3. `feat(persistence): adaptadores de repositorio y mapeo` |
| 5 | `feature/04-servicios` | M4 | M1 | `tuencargo/src/main/java/com/tuencargo/application/services/*.java` (6) | 1. `feat(service): registro con BCrypt y casillero`<br>2. `feat(service): cotizacion y comparacion`<br>3. `feat(service): pedidos y maquina de estados`<br>4. `feat(service): pagos, notificaciones y tarifas` |
| 6 | `feature/05-seguridad` | M3 | M5 | `tuencargo/src/main/java/com/tuencargo/infrastructure/security/{AuthenticatedUser,JpaUserDetailsService}.java`, `.../config/SecurityConfig.java`, `.../adapter/security/BCryptPasswordHasherAdapter.java` | 1. `feat(security): principal autenticado`<br>2. `feat(security): UserDetailsService sobre el puerto de usuarios`<br>3. `feat(security): RBAC ADMIN/USER y CSRF`<br>4. `feat(security): adaptador BCrypt del puerto de hashing` |
| 7 | `feature/06-adaptadores` | M5 | M4 | `tuencargo/src/main/java/com/tuencargo/infrastructure/adapter/store/MockStoreCatalogAdapter.java`, `.../adapter/fx/MockExchangeRateAdapter.java`, `.../adapter/payment/MockPsePaymentAdapter.java`, `.../adapter/email/MockEmailAdapter.java`, `.../config/DataSeeder.java` | 1. `feat(adapter): catalogo de tiendas y TRM simulados`<br>2. `feat(adapter): pasarela PSE, correo y seeder` |
| 8 | `feature/07-web-cliente` | M4 | M2 | `tuencargo/src/main/java/com/tuencargo/infrastructure/web/{HomeController,AuthController,QuotationController,OrderController,GlobalModelAdvice,Formats}.java` | 1. `feat(web): registro, login y panel`<br>2. `feat(web): cotizacion, comparacion y checkout`<br>3. `feat(web): pago, ciclo del pedido y advice global` |
| 9 | `feature/08-web-admin` | M1 | M4 | `tuencargo/src/main/java/com/tuencargo/infrastructure/web/AdminController.java`, `.../web/NotificationController.java` | 1. `feat(web): tarifas y avance logistico (ADMIN)`<br>2. `feat(web): buzon de notificaciones` |
| 10 | `feature/09-vistas-cliente` | M2 | M1 | `tuencargo/src/main/resources/templates/fragments/layout.html`, `index.html`, `login.html`, `register.html`, `error.html`, `error/message.html`, `quotations/*.html` (4), `orders/*.html` (2), `notifications/list.html`, `tuencargo/src/main/resources/static/css/app.css` | 1. `feat(ui): layout responsive y pantalla publica`<br>2. `feat(ui): registro, login y cotizacion`<br>3. `feat(ui): pedidos, pago y buzon` |
| 11 | `feature/10-vistas-admin` | M1 | M3 | `tuencargo/src/main/resources/templates/admin/{home,rates,orders}.html` | 1. `feat(ui): panel administrativo`<br>2. `feat(ui): tarifas y avance de pedidos` |
| 12 | `feature/11-pruebas-docs` | M5 | M2 | `tuencargo/src/test/java/com/tuencargo/{DomainRulesTest,TuencargoApplicationTests}.java`, `README.md`, `docs/PLAN_PUSH.md` | 1. `test: reglas de dominio y contexto Spring`<br>2. `docs: README y plan de push` |

## 2. Reparto de commits y revisiones

| Integrante | PRs | Commits | Revisiones asignadas |
| --- | --- | --- | --- |
| M1 | 1, 9, 11 | 3 + 2 + 2 = **7** | PR 5, PR 10 |
| M2 | 2, 10 | 4 + 3 = **7** | PR 4, PR 8, PR 12 |
| M3 | 3, 6 | 3 + 4 = **7** | PR 1, PR 11 |
| M4 | 5, 8 | 4 + 3 = **7** | PR 3, PR 7, PR 9 |
| M5 | 4, 7, 12 | 3 + 2 + 2 = **7** | PR 2, PR 6 |

Ningún revisor coincide con el autor de su PR y todos revisan al menos dos PR.

## 3. Procedimiento por PR

```bash
# 1. Actualizar main
git checkout main && git pull origin main

# 2. Crear la rama del módulo
git switch -c feature/01-dominio

# 3. Agregar SOLO los archivos del módulo y commitear en orden (nunca git add .)
git add tuencargo/src/main/java/com/tuencargo/domain/model
git commit -m "feat(domain): enums y value objects del modelo"

# 4. Publicar y abrir el PR con el revisor asignado
git push -u origin feature/01-dominio
```

`gh` no está instalado en el equipo; el PR se crea desde el navegador:

```
https://github.com/Diego-armando-perez/TUENCARGO/compare/main...<rama>?expand=1
```

Política de merge: **Merge commit** (no squash), para que se conserven los commits de cada
integrante. Se fusiona únicamente con la aprobación del revisor asignado.

## 4. Calendario (16–18 de noviembre de 2026)

| Fecha | Hora | Actividad | Responsable |
| --- | --- | --- | --- |
| Lun 16 | 18:00–19:30 | Push PR 1, revisión y merge | M1 / M3 |
| Lun 16 | 19:30–22:00 | PR 2 (`dominio`) y PR 3 (`puertos`) en ramas separadas | M2 / M3 |
| Mar 17 | 08:00–10:00 | Revisión y merge PR 2 (M5) y PR 3 (M4) | M2 / M3 |
| Mar 17 | 10:00–12:00 | PR 4 (`persistencia`) y PR 5 (`servicios`) | M5 / M4 |
| Mar 17 | 12:00–14:00 | Revisión y merge PR 4 (M2); PR 6 (`seguridad`) | M5 / M3 |
| Mar 17 | 14:00–16:00 | PR 7 (`adaptadores` + seeder) y PR 8 (`web cliente`) | M5 / M4 |
| Mar 17 | 16:00–18:00 | Merge PR 5 a PR 8 en orden; PR 9 (`web admin`) y PR 11 (`vistas admin`) | M4 / M1 |
| Mar 17 | 18:00–20:00 | PR 10 (`vistas cliente`) y revisión de PR 9 | M2 / M4 |
| Mar 17 | 20:00–22:00 | Merge PR 9–PR 11; PR 12 (`pruebas y docs`) | M1 / M5 |
| Mié 18 | 08:00–11:00 | Prueba end-to-end de los 7 flujos en `main` | Todos |
| Mié 18 | 11:00–13:00 | Correcciones, documento v2.0 y matriz de trazabilidad | Todos |
| Mié 18 | 14:00–16:00 | Grabar demo de 3–5 minutos | M2 / M5 |
| Mié 18 | 16:00–18:00 | Ensayo de Q&A (cada uno defiende su módulo) | Todos |
| Mié 18 | 18:00–20:00 | `git tag v1.0-incremento1`, verificación final y **entrega** | M1 |

## 5. Checklist de auditoría antes de entregar

- [ ] `git log --format="%an" | sort | uniq -c` muestra ~7 commits por integrante (5 autores).
- [ ] Cada PR tiene un revisor distinto al autor y quedó aprobado.
- [ ] No hay commits con fecha futura ni reescrituras (`git log --date=iso`).
- [ ] `./mvnw test` en verde en `main`.
- [ ] `./mvnw spring-boot:run` levanta la app y el seeder crea ADMIN y USER.
- [ ] README con prerrequisitos, comando único y credenciales de prueba.
- [ ] Cada integrante puede explicar su módulo (mínimo: una clase y una decisión de diseño).
