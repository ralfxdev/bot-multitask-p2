package umg.pgm2;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import umg.pgm2.botTelegram.BotMultiTask;
import umg.pgm2.model.User;
import umg.pgm2.service.UserService;

import java.sql.SQLException;

public class Main {

    private static void PruebaInsertaUsuario() {
        //explicación:
        // 1. Servicio
        //Servicio (UserService.java):
        //La clase UserService actúa como intermediario entre el controlador y la capa de acceso a datos (DAO).
        // Se encarga de la lógica de negocio, validaciones y de coordinar las transacciones.
        // 2. DAO
        //Capa de Acceso a Datos (UserDao.java):
        //Esta clase contiene los métodos para interactuar con la base de datos, usando la
        // conexión proporcionada por DatabaseConnection. Aquí es donde se construyen y ejecutan
        // las consultas SQL.
        // 3. Conexión a la Base de Datos
        //Gestión de la Conexión (DatabaseConnection.java):
        //Esta clase es responsable de proporcionar la conexión a la base de datos. Puede leer la configuración
        // desde un archivo de propiedades (application.properties) para obtener los detalles de conexión.
        // 4. Transacciones
        //Gestión de Transacciones (TransactionManager.java):
        //Esta clase se encarga de iniciar, confirmar o revertir transacciones en la base de datos.
        // Se utiliza para agrupar varias operaciones en una sola transacción y garantizar la integridad de los datos.
        // 5. Modelo (User.java):
        //La clase User representa la estructura de los datos que se insertan en la base de datos.
        // Es una clase POJO (Plain Old Java Object) con atributos, getters y setters.

        //invoca el servicio que manejará la lógica de negocio.
        UserService userService=new UserService();
        User user = new User();

        // Crear un nuevo usuarioUseruser=newUser();
//        user.setCarne("0905-20-8464");
        user.setNombre("RALF");
//        user.setCorreo("rlopez@gmail.com");
//        user.setSeccion("B");
//        user.setTelegramid(1234567890L);
//        user.setActivo("Y");

        try {
            userService.createUser(user);
            System.out.println("User created successfully!");
        } catch (SQLException e) {
            System.out.println("hay clavos!!");
            e.printStackTrace();
        }
    }

    private static void PruebaActualizacionUsuario() {
        UserService servicioUsuaio = new UserService();

        User usurioObtenido;
        //obtener información del usuario por correo electrónico
        try {
            usurioObtenido = servicioUsuaio.getUserByCarne("0905-12-12345");
            if (usurioObtenido == null) {
                System.out.println("Usuario no encontrado!");
            } else {
                System.out.println("Retrieved User: " + usurioObtenido.getNombre());
                System.out.println("Retrieved User: " + usurioObtenido.getCorreo());
                System.out.println("Retrieved User: " + usurioObtenido.getId());

                //actualizar información del usuario
                usurioObtenido.setCarne("0905-12-12345");
                usurioObtenido.setNombre("Randy Lopez");
                usurioObtenido.setCorreo("rlopez19@gmail.com");
                usurioObtenido.setSeccion("B");
                usurioObtenido.setTelegramid(1234567890L);
                usurioObtenido.setActivo("Y");

                servicioUsuaio.updateUser(usurioObtenido);
                System.out.println("User updated successfully!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void PruebaEliminarUsuario() {
        UserService servicioUsuaio = new UserService();
        try {
            servicioUsuaio.deleteUserByEmail("anAscoli@gmail.com");
            System.out.println("User deleted successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
//        try{
//            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//            BotMultiTask bot = new BotMultiTask();
//            botsApi.registerBot(bot);
//            System.out.println("/help");
//            System.out.println("Bot registered!");
//        }
//        catch(Exception e){
//            System.out.println("Error: " + e.getMessage());
//        }
        PruebaActualizacionUsuario();
    }
}