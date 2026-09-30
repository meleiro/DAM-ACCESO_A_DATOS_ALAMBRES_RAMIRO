# UD2 JDBC con interfaz gráfica

Proyecto Maven para Java 21 e IntelliJ IDEA.

## Tecnologías

- Java 21
- Swing
- JDBC
- PostgreSQL
- Maven

## Qué permite hacer la interfaz

La ventana principal contiene dos pestañas:

- Clientes
- Productos

En ambas se puede:

- listar datos;
- insertar;
- modificar;
- eliminar.

Además existe un botón **Probar conexión** para comprobar la conexión JDBC.

## Estructura

src/main/java
├── app
│   └── Main.java
├── conexion
│   └── ConexionBD.java
├── dao
│   ├── ClienteDAO.java
│   └── ProductoDAO.java
├── modelo
│   ├── Cliente.java
│   └── Producto.java
└── vista
    └── VentanaPrincipal.java

sql
├── 00_crear_base_datos.sql
└── 01_crear_esquema.sql

## Antes de ejecutar

1. Instalar PostgreSQL.
2. Ejecutar `sql/00_crear_base_datos.sql` como `postgres`.
3. Abrir/conectarse a `tienda_ud2`.
4. Ejecutar `sql/01_crear_esquema.sql`.
5. Abrir el proyecto en IntelliJ como proyecto Maven.
6. Seleccionar JDK 21.
7. Ejecutar `app.Main`.

## Conexión configurada

ConexionBD.java utiliza:

- host: localhost
- puerto: 5432
- base: tienda_ud2
- usuario: tienda_app
- contraseña: tienda1234

## Recorrido didáctico sugerido

1. Ejecutar la interfaz sin PostgreSQL y observar el error de conexión.
2. Estudiar el `pom.xml` y el driver JDBC.
3. Revisar `ConexionBD`.
4. Probar `Connection`.
5. Estudiar `ClienteDAO.listar()`:
   Connection → PreparedStatement → ResultSet → Cliente.
6. Estudiar INSERT y las claves generadas.
7. Estudiar UPDATE.
8. Estudiar DELETE.
9. Repetir el recorrido con Producto.
10. Más adelante incorporar transacciones usando movimiento_stock.

La interfaz gráfica es deliberadamente sencilla: el objetivo principal
de esta unidad es entender JDBC y la persistencia, no el diseño visual.
