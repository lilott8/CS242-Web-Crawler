import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by jason on 2/11/14.
 */
public class Connector {

    public static Connector connector;

    //  private String server = "localhost";
    private static String server = "localhost";
    private static int port = 3306;
    private static String database = "Spidey";
    private static String user = "root";
    private static String password = "root";

    private static Connection connection;
    private static final String TAG = "connector";

    private Connector() {
        Log.d(TAG, String.format("User: %s, Password: %s, Server: %s", user, password, server), 7);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = String.format("jdbc:mysql://%s:%d/%s",
                    server, port, database);
            // Connect to our database
            connection = DriverManager.getConnection(url, user, password);
            // Check to see if our database exists
            checkForDatabase();
            // check for our table(s)
            checkForTables();
        } catch (SQLException e) {
            Log.d(TAG, "Error with: " + e.getMessage(), 1);
        } catch(ClassNotFoundException e) {
            Log.d(TAG, "Class not found: " + e.getMessage(), 2);
        }
    }

    public static synchronized Connector getInstance() {
        if(connector == null) {
            connector = new Connector();
        }
        return connector;
    }

    public Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {

        }
    }

    public static void checkForTables() {
        String createStatement = "CREATE TABLE IF NOT EXISTS `records`(\n" +
                "  `id` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `url` text NOT NULL,\n" +
                "  `domain` text NOT NULL,\n" +
                "  `args` text NOT NULL,\n" +
                "  `title` text NOT NULL,\n" +
                "  `head` longtext NOT NULL,\n" +
                "  `body` longtext NOT NULL,\n" +
                "  `raw` longtext NOT NULL,\n" +
                "  `timestamp` TIMESTAMP default now(),\n" +
                "  `updatetime` TIMESTAMP null,\n" +
                "  `linksto` INT(11) NOT NULL,\n" +
                "  `linkbacks` INT(11) NOT NULL,\n" +
                "  `loadtime` INT(11) NOT NULL,\n" +
                "   PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
        try {
            Statement create = connection.createStatement();
            create.execute(createStatement);
            Log.d(TAG, "Attempting to create the table", 7);
        } catch(SQLException e) {
            Log.d(TAG, "Error creating table." + e.getMessage(), 1);
        }
    }

    public static void checkForDatabase() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + database);
        } catch (SQLException e) {
            Log.d(TAG, "Error executing query " + e.toString(), 3);
        }
    }

    public static void setDatabaseAddress(String s) {server = s;}
    public static void setDatabaseUser(String s) {user = s;}
    public static void setDatabasePassword(String s) {password = s;}
}
