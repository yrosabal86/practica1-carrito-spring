# Práctica 1 – Tienda con carrito (Spring Boot)

Aplicación web de una pequeña tienda online: catálogo de productos público, carrito de la compra sin necesidad de registrarse y un panel de administración (solo para el rol ADMIN) donde se gestionan los productos.

## Tecnologías

- Java 17
- Spring Boot 3.5.9 (Web, Data JPA, Security)
- Thymeleaf + Thymeleaf Extras Spring Security 6
- Base de datos H2 en memoria
- Lombok
- Maven (incluye Maven Wrapper: `mvnw` / `mvnw.cmd`)

## Cómo ejecutarla

### Desde Eclipse / STS

1. `File > Import > Maven > Existing Maven Projects` y seleccionar la carpeta `Pract1`.
2. Esperar a que Maven descargue las dependencias.
3. Clic derecho sobre `Pr1Application.java` > `Run As > Java Application` (o `Spring Boot App`).

### Desde la terminal

En la carpeta del proyecto:

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Cuando arranque, abrir en el navegador: <http://localhost:8080>

> No hace falta instalar ninguna base de datos: H2 se crea en memoria al arrancar.

## Usuarios de prueba

| Usuario      | Contraseña | Rol   | Qué puede hacer                                        |
|--------------|------------|-------|--------------------------------------------------------|
| `admin`      | `admin`    | ADMIN | Todo: gestionar productos, exportar BD, consola H2     |
| `estudiante` | `1234`     | USER  | Ver catálogo y usar el carrito                         |

Los usuarios están definidos en memoria en `SecurityConfig.java` y las contraseñas se guardan cifradas con BCrypt.

## Funcionalidades

### Público (sin iniciar sesión)
- **Página de inicio** (`/`): accesos al catálogo, al carrito y a la administración.
- **Catálogo** (`/products`): listado de todos los productos con nombre, precio e imagen.
- **Carrito** (`/cart`):
  - Añadir un producto desde el catálogo.
  - Eliminar un producto del carrito.
  - El carrito es propio de cada sesión del navegador.
  - Si un administrador borra un producto del catálogo, se retira automáticamente del carrito y se avisa al usuario.

### Autenticación
- Formulario de login personalizado (`/login`) y cierre de sesión (`/logout`).
- Tras iniciar sesión, el administrador va a `/admin` y el resto de usuarios al catálogo. Si el usuario venía de una página protegida, vuelve a ella.
- Protección CSRF activada en los formularios.

### Administración (solo ADMIN)
- **Panel de administración** (`/admin`): lista de productos con las acciones disponibles.
- **Añadir producto**: nombre, precio e imagen opcional.
- **Editar producto** (`/products/edit/{id}`): si no se sube una imagen nueva, se conserva la anterior.
- **Eliminar producto**.
- **Subida de imágenes**: se guardan en la carpeta `uploads/` con un nombre único. Formatos permitidos: `.jpg`, `.jpeg`, `.png`, `.gif`, `.webp` (máximo 5 MB).
- **Exportar la base de datos** (`/admin/export`): descarga un archivo `products.sql` con la creación de la tabla y los `INSERT` de todos los productos.
- **Consola H2** (`/h2-console`), solo accesible para el administrador:
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Usuario: `sa`
  - Contraseña: `password`

## Rutas principales

| Ruta                       | Método | Acceso  | Descripción                         |
|----------------------------|--------|---------|-------------------------------------|
| `/`                        | GET    | Público | Página de inicio                    |
| `/products`                | GET    | Público | Catálogo                            |
| `/cart`                    | GET    | Público | Ver carrito                         |
| `/cart/add/{id}`           | POST   | Público | Añadir producto al carrito          |
| `/cart/remove/{id}`        | POST   | Público | Quitar producto del carrito         |
| `/login`                   | GET    | Público | Formulario de login                 |
| `/admin`                   | GET    | ADMIN   | Panel de administración             |
| `/products/add`            | POST   | ADMIN   | Crear producto                      |
| `/products/edit/{id}`      | GET/POST | ADMIN | Editar producto                     |
| `/products/delete/{id}`    | POST   | ADMIN   | Eliminar producto                   |
| `/admin/export`            | GET    | ADMIN   | Descargar `products.sql`            |
| `/h2-console`              | GET    | ADMIN   | Consola de la base de datos         |

## Estructura del proyecto

```
src/main/java/dss/example/pr1/
├── Pr1Application.java
├── config/        SecurityConfig, WebConfig (sirve /uploads)
├── controller/    Home, Login, Product, Cart, Admin
├── model/         Product (entidad JPA)
├── repository/    ProductRepo
└── service/       ProductService, CartService, DatabaseExportService

src/main/resources/
├── application.properties
├── static/css/styles.css
└── templates/     index, login, products, product_form, cart, admin (.html)
```

## Notas importantes

- La base de datos es **en memoria**: al parar la aplicación se pierden los productos. Al arrancar está vacía; hay que entrar como `admin` y crear los productos desde `/admin`.
- Las imágenes subidas se guardan en la carpeta `uploads/` (en la raíz del proyecto), que se crea sola si no existe.
- El puerto por defecto es el 8080.
