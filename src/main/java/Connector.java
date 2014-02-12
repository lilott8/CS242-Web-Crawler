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
    private String server = "10.211.55.4";
    private int port = 3306;
    private static String database = "Spidey";
    private String user = "root";
    private String password = "root";

    private static Connection connection;
    private static final String TAG = "connector";

    private Connector() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = String.format("jdbc:mysql://%s:%d/%s",
                    this.server, this.port, database);
            // Connect to our database
            connection = DriverManager.getConnection(url, this.user, this.password);
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
        String createStatement = "CREATE TABLE IF NOT EXISTS `Records`(\n" +
                "  `RecordID` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `URL` text NOT NULL,\n" +
                "  `Domain` text NOT NULL,\n" +
                "  `Args` text NOT NULL,\n" +
                "  `Title` text NOT NULL,\n" +
                "  `Head` text NOT NULL,\n" +
                "  `Body` text NOT NULL,\n" +
                "  `TimeStamp` TIMESTAMP NOT NULL,\n" +
                "  `UpdateTime` INT(11) NOT NULL,\n" +
                "  `Raw` text NOT NULL,\n" +
                "  `LinksTo` INT(11) NOT NULL,\n" +
                "  `LinksBack` INT(11) NOT NULL,\n" +
                "  `LoadTime` INT(11) NOT NULL,\n" +
                "  `LastUpdate` TIMESTAMP NOT NULL,\n" +
                "   PRIMARY KEY (`RecordID`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
        try {
            Statement create = connection.createStatement();
            create.execute(createStatement);
            Log.d(TAG, "Attempting to create the table", 5);
        } catch(SQLException e) {
            Log.d(TAG, "Error creating table." + e.getMessage(), 5);
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
}
