package dao;


/*
 * ================================================================
 * IMPORTACIONES DE NUESTRO PROYECTO
 * ================================================================
 */


/*
 * Importamos ConexionBD porque será la clase encargada de
 * proporcionarnos una conexión con PostgreSQL.
 *
 * Es decir:
 *
 *      ClienteDAO
 *          |
 *          | solicita una conexión
 *          v
 *      ConexionBD
 *          |
 *          v
 *      PostgreSQL
 */
import conexion.ConexionBD;


/*
 * Importamos nuestra clase Cliente.
 *
 * La necesitamos porque este DAO va a transformar:
 *
 *      FILAS de la tabla cliente
 *
 * en:
 *
 *      OBJETOS Cliente
 *
 * y, más adelante, también realizará el proceso contrario:
 *
 *      OBJETOS Cliente
 *
 *          ↓
 *
 *      FILAS de la tabla cliente
 */
import modelo.Cliente;



/*
 * ================================================================
 * IMPORTACIONES DE JDBC
 * ================================================================
 *
 * java.sql contiene las principales clases e interfaces
 * que utilizaremos para trabajar con bases de datos mediante JDBC.
 *
 * El * significa:
 *
 *      importar todas las clases/interfaces públicas
 *      del paquete java.sql que necesitemos.
 *
 * Entre ellas utilizaremos:
 *
 *      Connection
 *      PreparedStatement
 *      ResultSet
 *      SQLException
 *
 *
 * IMPORTANTE:
 *
 * JDBC significa:
 *
 *      Java Database Connectivity
 *
 * y es la API estándar de Java para trabajar con
 * bases de datos relacionales.
 */
import java.sql.*;


/*
 * ArrayList será la implementación concreta de List
 * que utilizaremos para ir almacenando los clientes
 * recuperados de PostgreSQL.
 */
import java.util.ArrayList;


/*
 * List es la interfaz que utilizaremos como tipo
 * para representar una colección de clientes.
 */
import java.util.List;



/**
 * ================================================================
 * DAO DE CLIENTE
 * ================================================================
 *
 * DAO significa:
 *
 *      Data Access Object
 *
 *      Objeto de Acceso a Datos
 *
 *
 * Esta clase concentra las operaciones relacionadas con
 * el acceso a los datos de Cliente.
 *
 *
 * Por ejemplo, aquí tendremos métodos para:
 *
 *      listar clientes
 *      insertar clientes
 *      actualizar clientes
 *      eliminar clientes
 *
 *
 * Es decir, las operaciones CRUD:
 *
 *      CREATE  -> INSERT
 *      READ    -> SELECT
 *      UPDATE  -> UPDATE
 *      DELETE  -> DELETE
 *
 *
 * ---------------------------------------------------------------
 * ¿POR QUÉ CREAMOS UN DAO?
 * ---------------------------------------------------------------
 *
 * Porque NO queremos escribir SQL directamente dentro de
 * nuestra interfaz gráfica.
 *
 * No queremos algo como:
 *
 *      VentanaPrincipal
 *          |
 *          |-- JTable
 *          |-- JButton
 *          |-- JOptionPane
 *          |-- SELECT ...
 *          |-- INSERT ...
 *          |-- UPDATE ...
 *          |-- DELETE ...
 *          |-- Connection
 *          |-- ResultSet
 *
 *
 * Queremos separar responsabilidades:
 *
 *
 *      INTERFAZ GRÁFICA
 *      VentanaPrincipal
 *              |
 *              | utiliza
 *              v
 *         ClienteDAO
 *              |
 *              | utiliza JDBC
 *              v
 *         ConexionBD
 *              |
 *              v
 *          PostgreSQL
 *
 *
 * De esta manera:
 *
 *      VentanaPrincipal
 *
 * se preocupa principalmente de la interfaz.
 *
 * Mientras:
 *
 *      ClienteDAO
 *
 * se preocupa del acceso a los datos de Cliente.
 *
 *
 * ---------------------------------------------------------------
 * RESPONSABILIDAD DE ESTE DAO
 * ---------------------------------------------------------------
 *
 * Contiene el SQL y transforma filas de la tabla cliente
 * en objetos Cliente, y viceversa.
 *
 *
 * Por ejemplo:
 *
 * TABLA CLIENTE
 *
 *      id | nombre | email             | activo
 *      ---+--------+-------------------+-------
 *       1 | Ana    | ana@email.com     | true
 *       2 | Luis   | luis@email.com    | false
 *
 *
 * se transformará en:
 *
 *      List<Cliente>
 *           |
 *           +-- Cliente(1, "Ana",  "ana@email.com",  true)
 *           |
 *           +-- Cliente(2, "Luis", "luis@email.com", false)
 *
 */
public class ClienteDAO {



    /**
     * ============================================================
     * MÉTODO listar()
     * ============================================================
     *
     * Este método recupera TODOS los clientes almacenados
     * en la tabla:
     *
     *      cliente
     *
     * de PostgreSQL.
     *
     *
     * El proceso completo será:
     *
     *
     *      PostgreSQL
     *          |
     *          | SELECT
     *          v
     *      ResultSet
     *          |
     *          | recorremos las filas
     *          v
     *      objetos Cliente
     *          |
     *          v
     *      List<Cliente>
     *
     *
     * ------------------------------------------------------------
     * VALOR DEVUELTO
     * ------------------------------------------------------------
     *
     * El método devuelve:
     *
     *      List<Cliente>
     *
     * porque una consulta SELECT puede devolver varios clientes.
     *
     *
     * ------------------------------------------------------------
     * throws SQLException
     * ------------------------------------------------------------
     *
     * SQLException es una excepción relacionada con operaciones
     * de bases de datos.
     *
     * Puede producirse, por ejemplo, si:
     *
     *      PostgreSQL no está arrancado
     *
     *      la URL de conexión es incorrecta
     *
     *      el usuario o contraseña son incorrectos
     *
     *      la tabla cliente no existe
     *
     *      nuestro SQL contiene un error
     *
     *      se pierde la conexión
     *
     *
     * SQLException es una CHECKED EXCEPTION.
     *
     * Por eso Java nos obliga a:
     *
     *      capturarla con try-catch
     *
     * o:
     *
     *      declararla mediante throws
     *
     *
     * En este método hemos decidido PROPAGARLA:
     *
     *      throws SQLException
     *
     * Es decir:
     *
     * "Si ocurre un problema de base de datos, este método
     *  no lo resuelve aquí. Se lo comunica a quien lo llamó."
     */
    public List<Cliente> listar() throws SQLException {


        /*
         * ========================================================
         * LISTA DE RESULTADOS
         * ========================================================
         *
         * Creamos una lista inicialmente vacía.
         *
         * Aquí iremos almacenando cada objeto Cliente
         * que reconstruyamos a partir de las filas obtenidas
         * desde PostgreSQL.
         *
         *
         * Inicialmente:
         *
         *      resultado
         *          |
         *          v
         *         [ ]
         *
         *
         * Después de leer la primera fila:
         *
         *      resultado
         *          |
         *          v
         *      [ Cliente Ana ]
         *
         *
         * Después de leer más filas:
         *
         *      resultado
         *          |
         *          v
         *      [ Cliente Ana,
         *        Cliente Luis,
         *        Cliente Marta ]
         *
         *
         * Utilizamos:
         *
         *      List<Cliente>
         *
         * como tipo de la variable.
         *
         * Y:
         *
         *      new ArrayList<>()
         *
         * como implementación concreta.
         */
        List<Cliente> resultado = new ArrayList<>();



        /*
         * ========================================================
         * SENTENCIA SQL
         * ========================================================
         *
         * Guardamos nuestra consulta SQL dentro de un String.
         *
         * Utilizamos un TEXT BLOCK de Java:
         *
         *      """
         *
         * Esto permite escribir Strings de varias líneas
         * de una forma mucho más cómoda y legible.
         *
         *
         * La consulta que enviaremos a PostgreSQL es:
         *
         *      SELECT id, nombre, email, activo
         *      FROM cliente
         *      ORDER BY id
         *
         *
         * --------------------------------------------------------
         * SELECT
         * --------------------------------------------------------
         *
         * Indica qué columnas queremos recuperar.
         *
         * Queremos:
         *
         *      id
         *      nombre
         *      email
         *      activo
         *
         *
         * Podríamos escribir:
         *
         *      SELECT *
         *
         * pero normalmente es preferible indicar explícitamente
         * las columnas que necesitamos.
         *
         *
         * --------------------------------------------------------
         * FROM cliente
         * --------------------------------------------------------
         *
         * Indica de qué tabla queremos obtener los datos.
         *
         *
         * --------------------------------------------------------
         * ORDER BY id
         * --------------------------------------------------------
         *
         * Ordena los resultados por el campo id.
         *
         * De esta manera obtendremos normalmente:
         *
         *      1
         *      2
         *      3
         *      4
         *      ...
         */
        String sql = """
                SELECT id, nombre, email, activo
                FROM cliente
                ORDER BY id
                """;



        /*
         * ========================================================
         * TRY-WITH-RESOURCES
         * ========================================================
         *
         * Aquí vamos a utilizar tres recursos JDBC:
         *
         *      Connection
         *      PreparedStatement
         *      ResultSet
         *
         *
         * Los tres deben cerrarse correctamente cuando
         * terminemos de utilizarlos.
         *
         * Por eso usamos:
         *
         *      try ( ... ) {
         *
         *      }
         *
         *
         * Java cerrará automáticamente los recursos cuando
         * salgamos del bloque try.
         *
         *
         * Esto ya lo conocemos de la unidad de ficheros.
         *
         * Antes hacíamos:
         *
         *      try (BufferedReader br = ...) {
         *
         *      }
         *
         *
         * Ahora hacemos:
         *
         *      try (
         *          Connection con = ...;
         *          PreparedStatement ps = ...;
         *          ResultSet rs = ...;
         *      ) {
         *
         *      }
         *
         *
         * La filosofía es exactamente la misma:
         *
         *      ABRIR RECURSO
         *           ↓
         *      UTILIZARLO
         *           ↓
         *      CERRARLO AUTOMÁTICAMENTE
         */
        try (


                /*
                 * =================================================
                 * 1. CONNECTION
                 * =================================================
                 *
                 * Solicitamos una conexión a nuestra clase:
                 *
                 *      ConexionBD
                 *
                 *
                 * obtenerConexion() devolverá un objeto:
                 *
                 *      Connection
                 *
                 *
                 * Connection representa una conexión activa
                 * entre nuestra aplicación Java y PostgreSQL.
                 *
                 *
                 * Conceptualmente:
                 *
                 *      ClienteDAO
                 *          |
                 *          | obtenerConexion()
                 *          v
                 *      ConexionBD
                 *          |
                 *          v
                 *      Connection
                 *          |
                 *          v
                 *      PostgreSQL
                 *
                 *
                 * La variable:
                 *
                 *      con
                 *
                 * contendrá esa conexión.
                 */
                Connection con =
                        ConexionBD.obtenerConexion();


                /*
                 * =================================================
                 * 2. PREPAREDSTATEMENT
                 * =================================================
                 *
                 * Ahora que tenemos una conexión, preparamos
                 * la sentencia SQL.
                 *
                 *
                 *      con.prepareStatement(sql)
                 *
                 * significa aproximadamente:
                 *
                 * "Prepara esta sentencia SQL para ejecutarla
                 *  utilizando esta conexión."
                 *
                 *
                 * El resultado es:
                 *
                 *      PreparedStatement
                 *
                 *
                 * PreparedStatement representa una sentencia SQL
                 * preparada para ser ejecutada.
                 *
                 *
                 * En este SELECT todavía no tenemos parámetros:
                 *
                 *      SELECT id, nombre, email, activo
                 *      FROM cliente
                 *      ORDER BY id
                 *
                 *
                 * Pero más adelante veremos sentencias como:
                 *
                 *      DELETE FROM cliente
                 *      WHERE id = ?
                 *
                 *
                 * donde PreparedStatement nos permitirá sustituir
                 * el ? de manera segura.
                 */
                PreparedStatement ps =
                        con.prepareStatement(sql);


                /*
                 * =================================================
                 * 3. RESULTSET
                 * =================================================
                 *
                 * Ejecutamos nuestra consulta:
                 *
                 *      ps.executeQuery()
                 *
                 *
                 * Utilizamos:
                 *
                 *      executeQuery()
                 *
                 * porque estamos ejecutando un SELECT.
                 *
                 *
                 * REGLA DIDÁCTICA IMPORTANTE:
                 *
                 *      SELECT
                 *          ↓
                 *      executeQuery()
                 *          ↓
                 *      ResultSet
                 *
                 *
                 * Mientras que posteriormente veremos:
                 *
                 *      INSERT
                 *      UPDATE
                 *      DELETE
                 *          ↓
                 *      executeUpdate()
                 *
                 *
                 * executeQuery() devuelve un:
                 *
                 *      ResultSet
                 *
                 *
                 * ResultSet contiene las filas devueltas
                 * por PostgreSQL.
                 *
                 *
                 * Podemos imaginar:
                 *
                 *      id | nombre | email          | activo
                 *      ---+--------+----------------+-------
                 *       1 | Ana    | ana@email.com  | true
                 *       2 | Luis   | luis@email.com | false
                 *
                 *
                 * Todo ese resultado será accesible mediante:
                 *
                 *      rs
                 */
                ResultSet rs =
                        ps.executeQuery()

        ) {


            /*
             * ====================================================
             * RECORRER EL RESULTSET
             * ====================================================
             *
             * ResultSet funciona mediante un CURSOR.
             *
             * Podemos imaginar el resultado:
             *
             *
             *      id | nombre | email          | activo
             *      ---+--------+----------------+-------
             *       1 | Ana    | ana@email.com  | true
             *       2 | Luis   | luis@email.com | false
             *       3 | Marta  | marta@mail.com | true
             *
             *
             * Inicialmente el cursor está ANTES de la
             * primera fila:
             *
             *      cursor
             *        ↓
             *
             *      ---- antes de la primera fila ----
             *       1 | Ana   | ...
             *       2 | Luis  | ...
             *       3 | Marta | ...
             *
             *
             * Por eso necesitamos:
             *
             *      rs.next()
             *
             *
             * next() intenta avanzar a la siguiente fila.
             *
             *
             * Si existe una fila:
             *
             *      devuelve true
             *
             *
             * Si ya no existen más filas:
             *
             *      devuelve false
             *
             *
             * Por eso encaja perfectamente en un while:
             *
             *      while (rs.next()) {
             *
             *      }
             */
            while (rs.next()) {


                /*
                 * =================================================
                 * FILA DE POSTGRESQL -> OBJETO CLIENTE
                 * =================================================
                 *
                 * En este momento rs está situado sobre UNA fila.
                 *
                 * Por ejemplo:
                 *
                 *      id       = 1
                 *      nombre   = Ana
                 *      email    = ana@email.com
                 *      activo   = true
                 *
                 *
                 * Tenemos que transformar esa fila en:
                 *
                 *      new Cliente(...)
                 *
                 *
                 * Este proceso es MUY importante.
                 *
                 * Estamos haciendo manualmente el mapeo:
                 *
                 *      BASE DE DATOS
                 *           ↓
                 *      OBJETO JAVA
                 *
                 *
                 * Más adelante, cuando estudiemos JPA/Hibernate,
                 * veremos que el ORM podrá automatizar gran parte
                 * de este trabajo.
                 */


                /*
                 * Creamos un nuevo Cliente y lo añadimos
                 * directamente a la lista resultado.
                 */
                resultado.add(

                        new Cliente(


                                /*
                                 * ---------------------------------
                                 * COLUMNA id
                                 * ---------------------------------
                                 *
                                 * En PostgreSQL id es un INTEGER.
                                 *
                                 * En Java nuestro Cliente utiliza
                                 * un int.
                                 *
                                 * Por eso usamos:
                                 *
                                 *      getInt(...)
                                 *
                                 *
                                 * Podemos indicar el nombre
                                 * de la columna:
                                 *
                                 *      "id"
                                 */
                                rs.getInt("id"),


                                /*
                                 * ---------------------------------
                                 * COLUMNA nombre
                                 * ---------------------------------
                                 *
                                 * nombre es texto.
                                 *
                                 * Por eso utilizamos:
                                 *
                                 *      getString(...)
                                 */
                                rs.getString("nombre"),


                                /*
                                 * ---------------------------------
                                 * COLUMNA email
                                 * ---------------------------------
                                 *
                                 * También es texto, por lo que
                                 * utilizamos getString().
                                 */
                                rs.getString("email"),


                                /*
                                 * ---------------------------------
                                 * COLUMNA activo
                                 * ---------------------------------
                                 *
                                 * En PostgreSQL:
                                 *
                                 *      activo BOOLEAN
                                 *
                                 * En Java:
                                 *
                                 *      boolean activo
                                 *
                                 *
                                 * Por eso utilizamos:
                                 *
                                 *      getBoolean(...)
                                 */
                                rs.getBoolean("activo")
                        )
                );


                /*
                 * Después de esta instrucción:
                 *
                 *      resultado.add(new Cliente(...))
                 *
                 * la fila actual de PostgreSQL ya tiene
                 * su correspondiente objeto Java dentro
                 * de nuestra lista.
                 *
                 *
                 * Por ejemplo:
                 *
                 *      FILA POSTGRESQL
                 *
                 *      1 | Ana | ana@email.com | true
                 *
                 *              ↓
                 *
                 *      rs.getInt(...)
                 *      rs.getString(...)
                 *      rs.getBoolean(...)
                 *
                 *              ↓
                 *
                 *      new Cliente(
                 *          1,
                 *          "Ana",
                 *          "ana@email.com",
                 *          true
                 *      )
                 *
                 *              ↓
                 *
                 *      resultado.add(...)
                 */
            }


            /*
             * Cuando rs.next() finalmente devuelve false,
             * significa que ya hemos recorrido todas las filas.
             *
             * Entonces abandonamos el while.
             */
        }


        /*
         * ========================================================
         * FIN DEL TRY-WITH-RESOURCES
         * ========================================================
         *
         * Al llegar aquí Java ya habrá cerrado automáticamente:
         *
         *      ResultSet
         *      PreparedStatement
         *      Connection
         *
         *
         * El cierre se realiza en orden inverso al de creación:
         *
         *      ResultSet
         *          ↓
         *      PreparedStatement
         *          ↓
         *      Connection
         *
         *
         * Pero los objetos Cliente que hemos creado siguen
         * existiendo en nuestra lista resultado.
         *
         *
         * Es decir:
         *
         *      PostgreSQL
         *          X
         *          | conexión ya cerrada
         *
         *
         *      resultado
         *          |
         *          +-- Cliente
         *          +-- Cliente
         *          +-- Cliente
         *
         *
         * La lista está en memoria y podemos devolverla.
         */


        /*
         * ========================================================
         * DEVOLVEMOS EL RESULTADO
         * ========================================================
         *
         * Si PostgreSQL devolvió tres filas:
         *
         *      resultado
         *
         * contendrá tres objetos Cliente.
         *
         *
         * Si PostgreSQL no devolvió ninguna fila:
         *
         *      resultado
         *
         * será simplemente:
         *
         *      []
         *
         * una lista vacía.
         *
         *
         * Esto es preferible a devolver null.
         */
        return resultado;
    }

    /**
     * ================================================================
     * MÉTODO insertar(Cliente cliente)
     * ================================================================
     *
     * OBJETIVO:
     *
     * Insertar un nuevo cliente en la tabla "cliente"
     * de nuestra base de datos PostgreSQL.
     *
     *
     * ---------------------------------------------------------------
     * ¿QUÉ RECIBE ESTE MÉTODO?
     * ---------------------------------------------------------------
     *
     * Recibe un objeto de nuestra clase Cliente:
     *
     *      Cliente cliente
     *
     * Por ejemplo:
     *
     *      Cliente cliente = new Cliente(
     *              0,
     *              "Ana",
     *              "ana@email.com",
     *              true
     *      );
     *
     *
     * Este objeto contiene los datos que queremos guardar
     * en PostgreSQL.
     *
     *
     * ---------------------------------------------------------------
     * ¿QUÉ QUEREMOS CONSEGUIR?
     * ---------------------------------------------------------------
     *
     * Convertir:
     *
     *      OBJETO JAVA
     *
     *      Cliente
     *          id     = 0
     *          nombre = "Ana"
     *          email  = "ana@email.com"
     *          activo = true
     *
     * en:
     *
     *      FILA DE POSTGRESQL
     *
     *      id | nombre | email          | activo
     *      ---+--------+----------------+-------
     *       1 | Ana    | ana@email.com  | true
     *
     *
     * IMPORTANTE:
     *
     * El ID lo genera automáticamente PostgreSQL.
     *
     * Por eso no lo incluiremos en la sentencia INSERT.
     *
     * Después recuperaremos el ID generado y lo guardaremos
     * en el mismo objeto Cliente que hemos recibido.
     *
     *
     * ---------------------------------------------------------------
     * ¿QUÉ DEVUELVE?
     * ---------------------------------------------------------------
     *
     * El método está declarado:
     *
     *      void
     *
     * Por tanto, no devuelve ningún valor.
     *
     * Sin embargo, sí puede MODIFICAR el objeto Cliente
     * recibido como argumento, asignándole el ID generado.
     *
     *
     * ---------------------------------------------------------------
     * throws SQLException
     * ---------------------------------------------------------------
     *
     * Indica que este método puede producir una excepción
     * relacionada con JDBC o con PostgreSQL.
     *
     * Algunos ejemplos:
     *
     *      - No se puede establecer la conexión.
     *      - La tabla cliente no existe.
     *      - El SQL es incorrecto.
     *      - El email ya existe y tiene una restricción UNIQUE.
     *      - El usuario no tiene permiso para insertar.
     *
     * No capturamos aquí la excepción.
     *
     * La propagamos al código que llamó al método.
     */
    public void insertar(Cliente cliente) throws SQLException {


        /*
         * ============================================================
         * PASO 1: PREPARAR LA SENTENCIA SQL
         * ============================================================
         *
         * Vamos a insertar una fila en la tabla:
         *
         *      cliente
         *
         *
         * Nuestra tabla tiene estas columnas:
         *
         *      id
         *      nombre
         *      email
         *      activo
         *
         *
         * Pero en el INSERT solamente indicamos:
         *
         *      nombre
         *      email
         *      activo
         *
         *
         * ¿POR QUÉ NO INCLUIMOS id?
         *
         * Porque la columna id fue definida en PostgreSQL
         * para generar automáticamente sus valores.
         *
         * En nuestro proyecto utilizamos:
         *
         *      GENERATED ALWAYS AS IDENTITY
         *
         *
         * ------------------------------------------------------------
         * LOS SIGNOS DE INTERROGACIÓN ?
         * ------------------------------------------------------------
         *
         * Observad:
         *
         *      VALUES (?, ?, ?)
         *
         *
         * Cada ? representa un parámetro cuyo valor
         * estableceremos posteriormente desde Java.
         *
         *
         *      INSERT INTO cliente(nombre, email, activo)
         *      VALUES (     ?  ,    ? ,    ?  )
         *                   |       |      |
         *                   |       |      |
         *                   1       2      3
         *
         *
         *      Parámetro 1 -> nombre
         *      Parámetro 2 -> email
         *      Parámetro 3 -> activo
         *
         *
         * ATENCIÓN:
         *
         * Los parámetros de PreparedStatement empiezan
         * en la posición 1, NO en la posición 0.
         *
         *
         * ------------------------------------------------------------
         * ¿POR QUÉ UTILIZAMOS ? EN VEZ DE CONCATENAR STRINGS?
         * ------------------------------------------------------------
         *
         * Porque PreparedStatement permite enviar los valores
         * como parámetros de manera segura.
         *
         * Además, evita que tengamos que construir manualmente
         * las comillas y los escapes de los valores SQL.
         *
         * Esto es especialmente importante para prevenir
         * problemas de inyección SQL.
         */
        String sql = """
            INSERT INTO cliente(nombre, email, activo)
            VALUES (?, ?, ?)
            """;


        /*
         * ============================================================
         * PASO 2: ABRIR LOS RECURSOS JDBC
         * ============================================================
         *
         * Utilizamos try-with-resources.
         *
         * Ya conocemos esta estructura de la unidad de ficheros:
         *
         *      try (BufferedReader br = ...) {
         *
         *      }
         *
         *
         * Ahora trabajamos con:
         *
         *      Connection
         *      PreparedStatement
         *
         *
         * Java cerrará automáticamente estos recursos
         * al finalizar el bloque try, incluso si ocurre
         * una excepción durante su utilización.
         */
        try (

                /*
                 * ====================================================
                 * 2.1. CONNECTION
                 * ====================================================
                 *
                 * Solicitamos una conexión con PostgreSQL.
                 *
                 * Utilizamos nuestra clase:
                 *
                 *      ConexionBD
                 *
                 * y su método estático:
                 *
                 *      obtenerConexion()
                 *
                 *
                 * Este método devuelve un objeto Connection.
                 *
                 *
                 *      ClienteDAO
                 *          |
                 *          v
                 *      ConexionBD.obtenerConexion()
                 *          |
                 *          v
                 *      Connection
                 *          |
                 *          v
                 *      PostgreSQL
                 */
                Connection con = ConexionBD.obtenerConexion();


                /*
                 * ====================================================
                 * 2.2. PREPAREDSTATEMENT
                 * ====================================================
                 *
                 * Preparamos la sentencia SQL utilizando
                 * la conexión que acabamos de obtener.
                 *
                 *
                 * En este caso utilizamos una sobrecarga especial
                 * del método prepareStatement():
                 *
                 *      con.prepareStatement(
                 *              sql,
                 *              Statement.RETURN_GENERATED_KEYS
                 *      )
                 *
                 *
                 * ----------------------------------------------------
                 * PRIMER ARGUMENTO: sql
                 * ----------------------------------------------------
                 *
                 * Contiene la sentencia INSERT que hemos definido.
                 *
                 *
                 * ----------------------------------------------------
                 * SEGUNDO ARGUMENTO: RETURN_GENERATED_KEYS
                 * ----------------------------------------------------
                 *
                 * Indica que queremos poder recuperar
                 * las claves generadas por la base de datos
                 * durante la inserción.
                 *
                 *
                 * ¿PARA QUÉ NECESITAMOS ESTO?
                 *
                 * Porque PostgreSQL asignará automáticamente
                 * un nuevo ID al cliente.
                 *
                 *
                 * Por ejemplo:
                 *
                 *      INSERT INTO cliente(...)
                 *
                 *              |
                 *              v
                 *
                 *          PostgreSQL
                 *
                 *              |
                 *              v
                 *
                 *      genera automáticamente id = 15
                 *
                 *
                 * Queremos conocer ese 15 desde Java.
                 *
                 * Por eso solicitamos las claves generadas.
                 *
                 *
                 * IMPORTANTE:
                 *
                 * RETURN_GENERATED_KEYS es una constante
                 * de la interfaz Statement.
                 *
                 * No significa que aquí estemos creando
                 * un objeto Statement.
                 *
                 * Seguimos utilizando un PreparedStatement.
                 */
                PreparedStatement ps = con.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )

        ) {


            /*
             * ========================================================
             * PASO 3: ASIGNAR VALORES A LOS PARÁMETROS
             * ========================================================
             *
             * Ahora tenemos preparada esta sentencia:
             *
             *      INSERT INTO cliente(nombre, email, activo)
             *      VALUES (?, ?, ?)
             *
             *
             * Pero todavía necesitamos proporcionar
             * los valores de los tres interrogantes.
             *
             *
             * Utilizaremos los métodos:
             *
             *      setString()
             *      setBoolean()
             *
             *
             * Cada uno recibe:
             *
             *      posición del parámetro
             *
             * y:
             *
             *      valor que queremos asignarle
             */


            /*
             * --------------------------------------------------------
             * PRIMER PARÁMETRO: nombre
             * --------------------------------------------------------
             *
             * ps.setString(1, ...)
             *
             * significa:
             *
             *      "Asigna un String al primer interrogante".
             *
             *
             * El valor procede del objeto Cliente:
             *
             *      cliente.getNombre()
             *
             *
             * Si el cliente se llama Ana:
             *
             *      parámetro 1 -> "Ana"
             */
            ps.setString(1, cliente.getNombre());


            /*
             * --------------------------------------------------------
             * SEGUNDO PARÁMETRO: email
             * --------------------------------------------------------
             *
             * Asignamos al segundo interrogante
             * el email del cliente.
             *
             *
             *      parámetro 2 -> "ana@email.com"
             */
            ps.setString(2, cliente.getEmail());


            /*
             * --------------------------------------------------------
             * TERCER PARÁMETRO: activo
             * --------------------------------------------------------
             *
             * La columna activo es de tipo BOOLEAN
             * en PostgreSQL.
             *
             * Por eso utilizamos:
             *
             *      setBoolean()
             *
             *
             * El valor puede ser:
             *
             *      true
             *
             * o:
             *
             *      false
             *
             *
             *      parámetro 3 -> true
             */
            ps.setBoolean(3, cliente.isActivo());


            /*
             * ========================================================
             * PASO 4: EJECUTAR EL INSERT
             * ========================================================
             *
             * Una vez asignados los parámetros,
             * ya podemos ejecutar la sentencia SQL.
             *
             *
             * Utilizamos:
             *
             *      executeUpdate()
             *
             *
             * RECORDATORIO:
             *
             *      SELECT
             *          |
             *          v
             *      executeQuery()
             *
             *
             *      INSERT / UPDATE / DELETE
             *          |
             *          v
             *      executeUpdate()
             *
             *
             * executeUpdate() devuelve un int con el número
             * de filas afectadas por la operación.
             *
             *
             * En nuestro caso, si insertamos correctamente
             * un cliente, normalmente será:
             *
             *      1
             *
             *
             * En este método no guardamos ese valor
             * porque solamente queremos ejecutar el INSERT
             * y recuperar posteriormente el ID generado.
             *
             *
             * IMPORTANTE:
             *
             * Los valores se envían a PostgreSQL
             * al ejecutar esta instrucción.
             *
             * Las llamadas setString() y setBoolean()
             * anteriores únicamente preparaban los parámetros.
             */
            ps.executeUpdate();


            /*
             * ========================================================
             * PASO 5: RECUPERAR EL ID GENERADO
             * ========================================================
             *
             * PostgreSQL ya ha ejecutado el INSERT.
             *
             * Ahora queremos conocer el ID que ha asignado
             * automáticamente al nuevo cliente.
             *
             *
             * Para ello utilizamos:
             *
             *      ps.getGeneratedKeys()
             *
             *
             * Este método devuelve un ResultSet.
             *
             *
             * ¡ATENCIÓN!
             *
             * Aunque estamos realizando un INSERT,
             * aquí también aparece un ResultSet.
             *
             * Pero NO contiene el resultado de un SELECT
             * que hayamos escrito nosotros.
             *
             * Contiene información sobre las claves
             * generadas durante la inserción.
             *
             *
             * Lo llamamos:
             *
             *      claves
             *
             *
             * Utilizamos otro try-with-resources
             * para que este ResultSet se cierre
             * automáticamente cuando terminemos.
             */
            try (ResultSet claves = ps.getGeneratedKeys()) {


                /*
                 * ====================================================
                 * PASO 6: AVANZAR EL CURSOR DEL RESULTSET
                 * ====================================================
                 *
                 * Recordad el error que habíamos visto:
                 *
                 *      ResultSet not positioned properly,
                 *      perhaps you need to call next
                 *
                 *
                 * Un ResultSet comienza con el cursor
                 * situado ANTES de la primera fila.
                 *
                 * Por eso no podemos leer directamente:
                 *
                 *      claves.getInt(1)
                 *
                 *
                 * Primero debemos llamar a:
                 *
                 *      claves.next()
                 *
                 *
                 * Si devuelve true:
                 *
                 *      existe una fila con información
                 *      sobre la clave generada.
                 *
                 *
                 * Si devuelve false:
                 *
                 *      no tenemos ninguna fila que leer.
                 *
                 *
                 * Utilizamos if porque solamente necesitamos
                 * recuperar la clave del cliente insertado.
                 */
                if (claves.next()) {


                    /*
                     * ================================================
                     * PASO 7: ASIGNAR EL ID AL OBJETO CLIENTE
                     * ================================================
                     *
                     * Recuperamos el valor entero situado
                     * en la primera columna del ResultSet.
                     *
                     *
                     *      claves.getInt(1)
                     *
                     *
                     * ATENCIÓN:
                     *
                     * Aquí el 1 indica la PRIMERA COLUMNA
                     * del ResultSet.
                     *
                     * No indica el ID del cliente.
                     *
                     *
                     * Por ejemplo, si PostgreSQL ha generado:
                     *
                     *      id = 15
                     *
                     * obtendremos:
                     *
                     *      claves.getInt(1) -> 15
                     *
                     *
                     * Después llamamos a:
                     *
                     *      cliente.setId(...)
                     *
                     *
                     * para actualizar el objeto Cliente
                     * que recibimos como parámetro.
                     *
                     *
                     * ANTES DEL INSERT:
                     *
                     *      cliente.id = 0
                     *
                     *
                     * DESPUÉS DEL INSERT:
                     *
                     *      cliente.id = 15
                     *
                     *
                     * No estamos creando un objeto Cliente nuevo.
                     *
                     * Estamos modificando el mismo objeto
                     * que recibimos al entrar en el método.
                     *
                     *
                     * Esto es posible porque los objetos
                     * en Java se pasan mediante una copia
                     * del valor de su referencia.
                     *
                     * Por tanto, el método que llamó a insertar()
                     * también podrá observar el nuevo ID
                     * en ese mismo objeto.
                     */
                    cliente.setId(claves.getInt(1));
                }
            }


            /*
             * ========================================================
             * PASO 8: CIERRE DE RECURSOS
             * ========================================================
             *
             * Al salir del try interno se cierra:
             *
             *      ResultSet claves
             *
             *
             * Al salir del try externo se cierran:
             *
             *      PreparedStatement ps
             *
             *      Connection con
             *
             *
             * Todo ello automáticamente.
             */
        }
    }

    /**
     * ================================================================
     * MÉTODO actualizar(Cliente cliente)
     * ================================================================
     *
     * OBJETIVO:
     *
     * Actualizar los datos de un cliente que ya existe
     * en la tabla cliente de PostgreSQL.
     *
     *
     * ---------------------------------------------------------------
     * DIFERENCIA ENTRE INSERT Y UPDATE
     * ---------------------------------------------------------------
     *
     * INSERT:
     *
     *      Crea una fila NUEVA.
     *
     *
     * UPDATE:
     *
     *      Modifica una fila EXISTENTE.
     *
     *
     * Por ejemplo:
     *
     * ANTES:
     *
     *      id | nombre | email          | activo
     *      ---+--------+----------------+-------
     *       5 | Ana    | ana@email.com  | true
     *
     *
     * DESPUÉS:
     *
     *      id | nombre | email             | activo
     *      ---+--------+-------------------+-------
     *       5 | Ana    | ana.nuevo@mail.es | false
     *
     *
     * Fijaos en que el ID sigue siendo 5.
     *
     * No estamos creando otro cliente.
     *
     * Estamos modificando los datos del cliente
     * que ya tenía ese identificador.
     *
     *
     * ---------------------------------------------------------------
     * PARÁMETRO
     * ---------------------------------------------------------------
     *
     * Recibimos:
     *
     *      Cliente cliente
     *
     * Este objeto debe contener:
     *
     *      id
     *      nombre
     *      email
     *      activo
     *
     *
     * El ID es especialmente importante porque lo utilizaremos
     * para localizar la fila que queremos modificar.
     *
     *
     * ---------------------------------------------------------------
     * VALOR DEVUELTO: boolean
     * ---------------------------------------------------------------
     *
     * Este método devuelve:
     *
     *      true
     *
     * si la sentencia UPDATE ha afectado exactamente
     * a una fila.
     *
     *
     * Devuelve:
     *
     *      false
     *
     * si la sentencia UPDATE no ha afectado exactamente
     * a una fila.
     *
     *
     * En nuestra tabla, donde id es PRIMARY KEY,
     * el UPDATE puede afectar normalmente:
     *
     *      0 filas -> no existe ese ID.
     *
     *      1 fila  -> existe ese ID y se ha actualizado.
     *
     *
     * IMPORTANTE:
     *
     * Este boolean NO indica si los valores nuevos
     * son distintos de los antiguos.
     *
     * Indica si el número de filas afectadas es 1.
     *
     *
     * ---------------------------------------------------------------
     * throws SQLException
     * ---------------------------------------------------------------
     *
     * Propagamos cualquier error JDBC o SQL al código
     * que haya llamado a este método.
     */
    public boolean actualizar(Cliente cliente) throws SQLException {


        /*
         * ============================================================
         * PASO 1: PREPARAR LA SENTENCIA UPDATE
         * ============================================================
         *
         * La sentencia SQL es:
         *
         *      UPDATE cliente
         *      SET nombre = ?, email = ?, activo = ?
         *      WHERE id = ?
         *
         *
         * Vamos a analizarla.
         *
         *
         * ------------------------------------------------------------
         * UPDATE cliente
         * ------------------------------------------------------------
         *
         * Indica qué tabla queremos modificar.
         *
         * En nuestro caso:
         *
         *      cliente
         *
         *
         * ------------------------------------------------------------
         * SET
         * ------------------------------------------------------------
         *
         * Indica qué columnas queremos actualizar.
         *
         *
         *      nombre = ?
         *
         *      email = ?
         *
         *      activo = ?
         *
         *
         * Los nuevos valores procederán del objeto Cliente
         * recibido como parámetro.
         *
         *
         * ------------------------------------------------------------
         * WHERE id = ?
         * ------------------------------------------------------------
         *
         * Esta condición es FUNDAMENTAL.
         *
         * Indica qué cliente queremos modificar.
         *
         *
         * Por ejemplo:
         *
         *      WHERE id = 5
         *
         * significa:
         *
         *      "Actualiza solamente el cliente cuyo ID sea 5".
         *
         *
         * ¡MUCHO CUIDADO!
         *
         * Si escribiéramos:
         *
         *      UPDATE cliente
         *      SET activo = false
         *
         * SIN WHERE, estaríamos modificando TODAS
         * las filas de la tabla.
         *
         *
         * ------------------------------------------------------------
         * ORDEN DE LOS PARÁMETROS
         * ------------------------------------------------------------
         *
         * Nuestra sentencia contiene CUATRO interrogantes:
         *
         *
         *      UPDATE cliente
         *
         *      SET nombre = ?, email = ?, activo = ?
         *                   1          2           3
         *
         *      WHERE id = ?
         *                 4
         *
         *
         * Por tanto:
         *
         *      1 -> nombre
         *      2 -> email
         *      3 -> activo
         *      4 -> id
         *
         *
         * El orden es muy importante.
         *
         * No depende del orden de las columnas
         * en la tabla de PostgreSQL.
         *
         * Depende del orden en que aparecen
         * los signos ? en nuestra sentencia SQL.
         */
        String sql = """
            UPDATE cliente
            SET nombre = ?, email = ?, activo = ?
            WHERE id = ?
            """;


        /*
         * ============================================================
         * PASO 2: ABRIR CONEXIÓN Y PREPARAR LA SENTENCIA
         * ============================================================
         *
         * Volvemos a utilizar try-with-resources.
         *
         *
         * Necesitamos:
         *
         *      Connection
         *
         * y:
         *
         *      PreparedStatement
         *
         *
         * No necesitamos solicitar claves generadas
         * porque NO estamos insertando un cliente nuevo.
         *
         * Estamos modificando uno que ya tiene ID.
         *
         *
         * Por eso aquí basta con:
         *
         *      con.prepareStatement(sql)
         *
         *
         * y NO utilizamos:
         *
         *      Statement.RETURN_GENERATED_KEYS
         */
        try (

                /*
                 * Obtenemos una conexión con PostgreSQL.
                 */
                Connection con = ConexionBD.obtenerConexion();


                /*
                 * Preparamos nuestra sentencia UPDATE.
                 */
                PreparedStatement ps = con.prepareStatement(sql)

        ) {


            /*
             * ========================================================
             * PASO 3: ASIGNAR LOS VALORES A LOS INTERROGANTES
             * ========================================================
             *
             * Tenemos cuatro parámetros.
             *
             * Debemos proporcionar los cuatro valores
             * antes de ejecutar la sentencia.
             */


            /*
             * --------------------------------------------------------
             * PARÁMETRO 1: nombre
             * --------------------------------------------------------
             *
             * Asignamos al primer interrogante
             * el nombre del cliente.
             */
            ps.setString(1, cliente.getNombre());


            /*
             * --------------------------------------------------------
             * PARÁMETRO 2: email
             * --------------------------------------------------------
             *
             * Asignamos al segundo interrogante
             * el email del cliente.
             */
            ps.setString(2, cliente.getEmail());


            /*
             * --------------------------------------------------------
             * PARÁMETRO 3: activo
             * --------------------------------------------------------
             *
             * Asignamos al tercer interrogante
             * el estado activo del cliente.
             *
             * Utilizamos setBoolean() porque la columna
             * es de tipo BOOLEAN en PostgreSQL.
             */
            ps.setBoolean(3, cliente.isActivo());


            /*
             * --------------------------------------------------------
             * PARÁMETRO 4: id
             * --------------------------------------------------------
             *
             * Este parámetro se utilizará en:
             *
             *      WHERE id = ?
             *
             *
             * Es el dato que permite identificar
             * qué fila queremos actualizar.
             *
             *
             * Por ejemplo:
             *
             *      cliente.getId() -> 5
             *
             *
             * hará que la condición sea equivalente a:
             *
             *      WHERE id = 5
             *
             *
             * IMPORTANTE:
             *
             * Utilizamos setInt() porque id es
             * de tipo INTEGER en PostgreSQL.
             */
            ps.setInt(4, cliente.getId());


            /*
             * ========================================================
             * PASO 4: EJECUTAR EL UPDATE
             * ========================================================
             *
             * Ahora que hemos asignado los cuatro parámetros,
             * ejecutamos la sentencia.
             *
             *
             * Utilizamos:
             *
             *      executeUpdate()
             *
             *
             * Este método devuelve un int:
             *
             *      número de filas afectadas
             *
             *
             * Por ejemplo:
             *
             *      0 -> no se ha encontrado ninguna fila
             *           que cumpla WHERE id = ?
             *
             *      1 -> se ha actualizado una fila
             *
             *
             * Como id es PRIMARY KEY, no puede haber
             * dos clientes con el mismo ID.
             *
             * Por tanto, en nuestra tabla el resultado
             * normal será 0 o 1.
             *
             *
             * --------------------------------------------------------
             * ¿QUÉ SIGNIFICA == 1?
             * --------------------------------------------------------
             *
             * Observad:
             *
             *      ps.executeUpdate() == 1
             *
             *
             * Primero se ejecuta:
             *
             *      ps.executeUpdate()
             *
             *
             * Supongamos que devuelve:
             *
             *      1
             *
             *
             * Entonces Java evalúa:
             *
             *      1 == 1
             *
             *
             * Resultado:
             *
             *      true
             *
             *
             * Si devuelve:
             *
             *      0
             *
             *
             * Java evalúa:
             *
             *      0 == 1
             *
             *
             * Resultado:
             *
             *      false
             *
             *
             * Por tanto, podemos devolver directamente
             * el resultado de esa comparación.
             *
             *
             * IMPORTANTE:
             *
             *      ==  -> operador de comparación
             *
             *      =   -> operador de asignación
             *
             *
             * Aquí necesitamos == porque queremos comparar
             * el número de filas afectadas con el valor 1.
             */
            return ps.executeUpdate() == 1;
        }


        /*
         * ============================================================
         * PASO 5: CIERRE AUTOMÁTICO DE RECURSOS
         * ============================================================
         *
         * Aunque el return está dentro del try,
         * Java cerrará automáticamente los recursos
         * antes de completar la salida del método.
         *
         *
         * Se cerrarán:
         *
         *      PreparedStatement
         *
         *      Connection
         *
         *
         * Si durante el cierre ocurre una SQLException,
         * también podrá propagarse.
         */
    }

    public boolean eliminar(int id) throws SQLException{
        String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
         PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, id);

            return ps.executeUpdate() == 1;

        }
    }

}