package umg.pgm2.db;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    static Dotenv dotenv = Dotenv.load();

    private static String DB_CONECTION = dotenv.get("DB_CONECTION");
    private static String USER_DB = dotenv.get("USER_DB");
    private static String PASS_DB = dotenv.get("PASS_DB");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_CONECTION, USER_DB, PASS_DB);
    }
}
