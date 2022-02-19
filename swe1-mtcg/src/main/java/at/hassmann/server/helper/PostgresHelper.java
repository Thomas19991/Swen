package at.hassmann.server.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * help functions for Postgres DB
 */
public class PostgresHelper {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/swe1user";
    private static final String USER = "swe1user";
    private static final String PASS = "swe1pw";

    /**
     * connect to db
     * @return Connection Objekt
     */
    public static Connection con() {
        Connection c = null;
        try {
            c = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return c;
    }

    /**
     * executes Sql statement without return, but with message
     * @param sql Sql command
     * @param message Mesasage, which is shown before execution
     * @return True if sucess, else false
     */
    public static boolean executeUpdateMessage(String sql, String message){
        System.out.println(message);
        return executeUpdate(sql);
    }

    /**
     * executes Sql statement without return
     * @param sql Sql command
     * @return True if success, else false
     */
    public static boolean executeUpdate(String sql){
        Connection c = con();
        Statement stmt;
        try {
            stmt = c.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
        return true;
    }
}
