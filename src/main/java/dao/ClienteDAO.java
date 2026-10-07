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

}