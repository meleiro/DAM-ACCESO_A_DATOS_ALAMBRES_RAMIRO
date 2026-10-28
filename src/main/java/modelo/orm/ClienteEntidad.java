
package modelo.orm;

/*
 * ================================================================
 * CLASE: ClienteEntidad
 * PAQUETE: modelo.orm
 * ================================================================
 *
 * OBJETIVO:
 *
 * Representar mediante un objeto Java los datos almacenados
 * en la tabla "cliente" de nuestra base de datos PostgreSQL.
 *
 * En el proyecto anterior, con JDBC, teníamos una clase:
 *
 *     modelo.Cliente
 *
 * Esa clase era un modelo Java normal. El DAO JDBC se encargaba
 * de ejecutar sentencias SQL y convertir manualmente las filas
 * del ResultSet en objetos Cliente.
 *
 * Ahora utilizaremos:
 *
 *     modelo.orm.ClienteEntidad
 *
 * Esta clase incorpora anotaciones JPA que permiten a Hibernate
 * conocer la relación entre sus atributos y las columnas SQL.
 *
 * Por ejemplo:
 *
 *     Atributo Java          Columna PostgreSQL
 *     -----------------------------------------
 *     id                     id
 *     nombre                 nombre
 *     email                  email
 *     activo                 activo
 *
 * IMPORTANTE:
 *
 * No estamos creando otra tabla de clientes.
 *
 * ClienteEntidad representa la misma tabla "cliente"
 * que utilizábamos anteriormente mediante JDBC.
 *
 * Las dos implementaciones pueden coexistir.
 */

// Importamos las anotaciones de Jakarta Persistence.
//
// El asterisco (*) importa los tipos accesibles directamente
// dentro del paquete jakarta.persistence.
//
// Incluye, entre otros:
//     Entity
//     Table
//     Id
//     GeneratedValue
//     GenerationType
//     Column
//
// Estas anotaciones pertenecen a la API JPA.
// Hibernate interpreta su significado durante la ejecución.
import jakarta.persistence.*;

/*
 * ================================================================
 * ANOTACIÓN @Entity
 * ================================================================
 *
 * Indica que esta clase Java es una entidad persistente JPA.
 *
 * Una entidad es un objeto cuyo estado puede almacenarse
 * y recuperarse desde una base de datos.
 *
 * Gracias a @Entity, Hibernate puede gestionar operaciones como:
 *
 *     em.persist(cliente);               INSERT
 *
 *     em.find(ClienteEntidad.class, id); SELECT por ID
 *
 *     em.remove(cliente);                DELETE
 *
 * También puede detectar modificaciones en los atributos
 * de una entidad gestionada y generar un UPDATE.
 *
 * IMPORTANTE:
 *
 * @Entity no crea automáticamente una tabla.
 *
 * La creación o validación del esquema depende de la
 * configuración de Hibernate.
 *
 * En nuestro persistence.xml tenemos:
 *
 *     hibernate.hbm2ddl.auto = validate
 *
 * Por tanto, Hibernate comprobará la estructura existente,
 * pero no creará ni modificará nuestras tablas.
 */
@Entity

/*
 * ================================================================
 * ANOTACIÓN @Table
 * ================================================================
 *
 * Relaciona esta entidad Java con una tabla de la base de datos.
 *
 * name = "cliente":
 *
 * Indica que la tabla correspondiente se llama "cliente".
 *
 * Esto es importante porque:
 *
 *     Nombre de la clase: ClienteEntidad
 *     Nombre de la tabla: cliente
 *
 * Son nombres diferentes.
 *
 * Hibernate utilizará este mapeo para generar las consultas SQL.
 *
 * Por ejemplo, cuando escribamos una consulta JPQL:
 *
 *     SELECT c FROM ClienteEntidad c
 *
 * Hibernate sabrá que debe consultar la tabla "cliente".
 *
 * Si la tabla estuviera en otro esquema, también podríamos
 * indicar el atributo schema de @Table.
 *
 * En nuestro caso utilizamos el esquema habitual "public".
 */
@Table(name = "cliente")

/*
 * ================================================================
 * DECLARACIÓN DE LA CLASE
 * ================================================================
 *
 * public:
 *     Permite utilizar esta clase desde otros paquetes,
 *     como dao.orm o app.
 *
 * class:
 *     Indica que estamos declarando una clase Java.
 *
 * ClienteEntidad:
 *     Es el nombre que hemos elegido para representar
 *     la entidad de clientes.
 *
 * La clase no necesita extender ninguna clase de Hibernate.
 *
 * Tampoco necesita implementar una interfaz especial.
 *
 * Esto es una característica importante de JPA:
 * podemos trabajar con clases Java normales que incorporan
 * anotaciones de persistencia.
 */
public class ClienteEntidad {

    /*
     * ============================================================
     * ATRIBUTO id: CLAVE PRIMARIA
     * ============================================================
     *
     * @Id:
     *
     * Identifica el atributo que representa la clave primaria
     * de la entidad.
     *
     * En PostgreSQL tenemos una columna similar a:
     *
     *     id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
     *
     * La clave primaria identifica de manera única cada cliente.
     *
     * Por ejemplo:
     *
     *     id = 1 -> Ana
     *     id = 2 -> Pedro
     *     id = 3 -> Marta
     *
     * Hibernate utiliza este identificador para distinguir
     * las entidades dentro del contexto de persistencia.
     *
     * También permite realizar búsquedas como:
     *
     *     em.find(ClienteEntidad.class, 3);
     *
     * En este caso buscaríamos el cliente cuyo ID es 3.
     */
    @Id

    /*
     * ============================================================
     * ANOTACIÓN @GeneratedValue
     * ============================================================
     *
     * Indica cómo se genera el valor de la clave primaria.
     *
     * strategy = GenerationType.IDENTITY:
     *
     * Significa que el identificador lo genera la base de datos
     * utilizando una columna de identidad.
     *
     * Esto coincide con nuestra definición en PostgreSQL:
     *
     *     GENERATED ALWAYS AS IDENTITY
     *
     * Cuando creamos un cliente nuevo:
     *
     *     ClienteEntidad cliente = new ClienteEntidad(
     *         null,
     *         true,
     *         "ana@ejemplo.com",
     *         "Ana"
     *     );
     *
     * No indicamos manualmente su ID.
     *
     * Al persistirlo:
     *
     *     em.persist(cliente);
     *
     * Hibernate solicita la inserción y obtiene el
     * identificador generado por PostgreSQL.
     *
     * IMPORTANTE:
     *
     * IDENTITY no es la única estrategia disponible.
     *
     * Otras estrategias son:
     *
     *     AUTO:
     *         El proveedor selecciona una estrategia.
     *
     *     SEQUENCE:
     *         Utiliza una secuencia de la base de datos.
     *
     *     TABLE:
     *         Utiliza una tabla para gestionar identificadores.
     *
     * En nuestro proyecto utilizamos IDENTITY porque la
     * estructura SQL existente ya utiliza columnas IDENTITY.
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    /*
     * ¿Por qué Integer y no int?
     *
     * Integer permite almacenar null.
     *
     * Antes de insertar un cliente, su ID todavía no existe:
     *
     *     id = null
     *
     * Después de persistirlo, PostgreSQL genera un valor:
     *
     *     id = 7
     *
     * Con int no podríamos representar null.
     *
     * private:
     *     Impide acceder directamente al atributo desde
     *     otras clases, respetando la encapsulación.
     */
    private Integer id;

    /*
     * ============================================================
     * ATRIBUTO nombre
     * ============================================================
     *
     * @Column:
     *     Configura el mapeo de un atributo con una columna SQL.
     *
     * name = "nombre":
     *     Indica el nombre de la columna en PostgreSQL.
     *
     * nullable = false:
     *     Declara que la columna no debe admitir valores NULL.
     *
     *     Se corresponde con:
     *
     *         nombre VARCHAR(100) NOT NULL
     *
     * length = 100:
     *     Indica la longitud declarada para la columna de texto.
     *
     * IMPORTANTE:
     *
     * Estas anotaciones describen el mapeo.
     *
     * No debemos confundirlas con una validación completa
     * de los datos introducidos por el usuario.
     *
     * Por ejemplo, nullable = false no impide necesariamente
     * que el usuario introduzca una cadena vacía:
     *
     *     ""
     *
     * Para validar ese caso necesitaremos lógica adicional
     * o anotaciones de Bean Validation, como @NotBlank.
     *
     * Como utilizamos validate, la columna correspondiente
     * debe existir previamente en PostgreSQL.
     */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /*
     * ============================================================
     * ATRIBUTO email
     * ============================================================
     *
     * name = "email":
     *     Se corresponde con la columna email.
     *
     * nullable = false:
     *     Indica que no debe aceptar NULL.
     *
     * length = 150:
     *     Longitud declarada de la columna.
     *
     * unique = true:
     *     Expresa que los valores de la columna deben ser únicos.
     *
     * En PostgreSQL tenemos una definición equivalente a:
     *
     *     email VARCHAR(150) NOT NULL UNIQUE
     *
     * Esto significa que no pueden existir dos clientes
     * con el mismo correo electrónico, según la restricción
     * de unicidad y la comparación aplicada por PostgreSQL.
     *
     * EJEMPLO:
     *
     * Cliente 1:
     *     ana@ejemplo.com
     *
     * Cliente 2:
     *     ana@ejemplo.com
     *
     * Si intentamos insertar ambos, PostgreSQL rechazará
     * el segundo registro por la restricción UNIQUE.
     *
     * IMPORTANTE:
     *
     * unique = true no comprueba que el texto tenga
     * un formato de correo electrónico válido.
     *
     * Por ejemplo:
     *
     *     "hola"
     *
     * Podría almacenarse si no existen otras validaciones.
     *
     * Para validar el formato podríamos utilizar @Email
     * de Jakarta Validation, con su dependencia correspondiente.
     *
     * La restricción UNIQUE debe existir realmente en la
     * base de datos para garantizar la unicidad.
     */
    @Column(name = "email", nullable = false,
            length = 150, unique = true)
    private String email;

    /*
     * ============================================================
     * ATRIBUTO activo
     * ============================================================
     *
     * Representa si un cliente está activo o inactivo.
     *
     * En PostgreSQL:
     *
     *     activo BOOLEAN NOT NULL DEFAULT TRUE
     *
     * En Java:
     *
     *     boolean activo
     *
     * Correspondencia:
     *
     *     Java true  -> PostgreSQL TRUE
     *     Java false -> PostgreSQL FALSE
     *
     * nullable = false:
     *     Indica que el campo no debe ser NULL.
     *
     * Hemos utilizado el tipo primitivo boolean porque
     * solamente necesitamos dos estados:
     *
     *     true
     *     false
     *
     * Si necesitásemos representar también la ausencia
     * de valor, podríamos utilizar Boolean.
     *
     * La asignación:
     *
     *     = true
     *
     * establece un valor inicial en Java.
     *
     * Esto es diferente de DEFAULT TRUE en PostgreSQL.
     *
     * El valor inicial Java se aplica al construir el objeto.
     *
     * El valor DEFAULT de PostgreSQL se aplica cuando
     * una inserción omite esa columna.
     */
    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    /*
     * ============================================================
     * CONSTRUCTOR SIN PARÁMETROS
     * ============================================================
     *
     * JPA necesita que las entidades tengan un constructor
     * sin argumentos public o protected.
     *
     * ¿POR QUÉ?
     *
     * Porque Hibernate debe poder crear instancias de esta
     * clase cuando recupera registros de PostgreSQL.
     *
     * Por ejemplo:
     *
     *     em.find(ClienteEntidad.class, 5);
     *
     * Hibernate necesita crear un objeto ClienteEntidad
     * y cargar los valores correspondientes al registro.
     *
     * protected:
     *     Permite el acceso necesario para JPA y las subclases,
     *     sin exponer un constructor público sin argumentos.
     *
     * No necesitamos escribir código dentro del constructor.
     *
     * La inicialización activo = true se aplica igualmente.
     *
     * IMPORTANTE:
     *
     * Si definimos un constructor con parámetros, Java
     * deja de generar automáticamente el constructor vacío.
     *
     * Por eso debemos declararlo explícitamente.
     */
    protected ClienteEntidad() {

    }

    /*
     * ============================================================
     * CONSTRUCTOR CON PARÁMETROS
     * ============================================================
     *
     * Este constructor lo utilizaremos nosotros para crear
     * objetos ClienteEntidad desde la aplicación.
     *
     * Recibe:
     *
     *     id:
     *         Identificador del cliente.
     *
     *     activo:
     *         Estado del cliente.
     *
     *     email:
     *         Correo electrónico.
     *
     *     nombre:
     *         Nombre del cliente.
     *
     * El orden de los parámetros es el de nuestro código:
     *
     *     Integer, boolean, String, String
     *
     * Por ejemplo:
     *
     *     ClienteEntidad nuevo = new ClienteEntidad(
     *         null,
     *         true,
     *         "ana@ejemplo.com",
     *         "Ana"
     *     );
     *
     * IMPORTANTE:
     *
     * Este constructor no inserta nada en PostgreSQL.
     *
     * Solamente crea un objeto Java en memoria.
     *
     * Para registrar el objeto en la base de datos,
     * posteriormente utilizaremos:
     *
     *     em.persist(nuevo);
     *
     * dentro de una transacción.
     *
     * La palabra this permite distinguir entre:
     *
     *     this.nombre -> atributo del objeto
     *
     *     nombre      -> parámetro del constructor
     */
    public ClienteEntidad(
            Integer id,
            boolean activo,
            String email,
            String nombre) {

        this.id = id;
        this.activo = activo;
        this.email = email;
        this.nombre = nombre;
    }

    /*
     * ============================================================
     * GETTER DEL IDENTIFICADOR
     * ============================================================
     *
     * Permite consultar el ID del cliente.
     *
     * Por ejemplo:
     *
     *     Integer id = cliente.getId();
     *
     * Después de persistir un cliente con IDENTITY,
     * Hibernate puede asignar el identificador generado
     * a este atributo.
     *
     * El getter no ejecuta por sí mismo una consulta SQL
     * para este atributo básico ya cargado.
     */
    public Integer getId() {
        return id;
    }

    /*
     * ============================================================
     * SETTER DEL IDENTIFICADOR
     * ============================================================
     *
     * Permite modificar el valor del atributo id en Java.
     *
     * Sin embargo, debemos tener cuidado:
     *
     * El identificador es la identidad persistente
     * del objeto y normalmente no debe modificarse
     * una vez que la entidad está gestionada.
     *
     * Con GenerationType.IDENTITY, lo habitual es dejar
     * que PostgreSQL genere el ID.
     *
     * Este setter no es obligatorio para JPA cuando
     * utilizamos acceso por campos, como en esta clase.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /*
     * ============================================================
     * GETTER DEL ESTADO ACTIVO
     * ============================================================
     *
     * En Java, para los atributos boolean es habitual
     * utilizar el prefijo is en lugar de get.
     *
     * Por eso tenemos:
     *
     *     isActivo()
     *
     * Devuelve:
     *
     *     true  -> cliente activo
     *     false -> cliente inactivo
     *
     * Por ejemplo:
     *
     *     if (cliente.isActivo()) {
     *         System.out.println("Cliente activo");
     *     }
     */
    public boolean isActivo() {
        return activo;
    }

    /*
     * ============================================================
     * SETTER DEL ESTADO ACTIVO
     * ============================================================
     *
     * Permite modificar el estado del cliente.
     *
     * Por ejemplo:
     *
     *     cliente.setActivo(false);
     *
     * Esto modifica el objeto Java.
     *
     * Si la entidad está gestionada por un EntityManager
     * dentro de una transacción, Hibernate puede detectar
     * ese cambio y generar un UPDATE al sincronizar.
     *
     * Este mecanismo se llama dirty checking.
     *
     * Si el objeto está separado del contexto de persistencia,
     * modificarlo no actualiza automáticamente PostgreSQL.
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /*
     * ============================================================
     * GETTER DEL EMAIL
     * ============================================================
     *
     * Devuelve el correo electrónico almacenado en el objeto.
     *
     * Ejemplo:
     *
     *     String correo = cliente.getEmail();
     *
     * Hibernate puede utilizar directamente el campo email
     * para persistirlo, porque hemos colocado @Id y @Column
     * sobre los atributos y no sobre los getters.
     */
    public String getEmail() {
        return email;
    }

    /*
     * ============================================================
     * SETTER DEL EMAIL
     * ============================================================
     *
     * Permite cambiar el correo electrónico del objeto.
     *
     * Ejemplo:
     *
     *     cliente.setEmail("nuevo@ejemplo.com");
     *
     * Si el cliente está gestionado por Hibernate,
     * el cambio podrá persistirse al sincronizar
     * el contexto de persistencia.
     *
     * PostgreSQL seguirá comprobando que el nuevo email
     * no incumpla la restricción UNIQUE.
     *
     * Este setter no comprueba por sí mismo si el texto
     * tiene formato de correo electrónico válido.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /*
     * ============================================================
     * GETTER DEL NOMBRE
     * ============================================================
     *
     * Permite recuperar el nombre del cliente.
     *
     * Ejemplo:
     *
     *     System.out.println(cliente.getNombre());
     *
     * No contiene ninguna lógica de acceso a PostgreSQL.
     *
     * Simplemente devuelve el atributo del objeto Java.
     */
    public String getNombre() {
        return nombre;
    }

    /*
     * ============================================================
     * SETTER DEL NOMBRE
     * ============================================================
     *
     * Permite modificar el nombre del cliente.
     *
     * Ejemplo:
     *
     *     cliente.setNombre("María López");
     *
     * Si el objeto está gestionado por un EntityManager,
     * Hibernate podrá detectar la modificación.
     *
     * No necesitamos escribir manualmente:
     *
     *     UPDATE cliente SET nombre = ... WHERE id = ...
     *
     * Hibernate generará la operación correspondiente
     * cuando sea necesario sincronizar los cambios.
     *
     * IMPORTANTE:
     *
     * El setter solamente cambia el estado del objeto.
     * No garantiza que exista una transacción activa
     * ni que el cambio vaya a guardarse en PostgreSQL.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
