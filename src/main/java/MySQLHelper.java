/**
 * Created by james on 1/27/14.
 * source from http://www.programcreek.com/2012/12/how-to-make-a-web-crawler-using-java/
 */
import java.sql.*;
import java.util.Map;

import com.mysql.jdbc.Driver;


public class MySQLHelper {

    String server = "localhost";
    int port = 3306;
    String database = "Crawler";
    String user = "root";
    String password = "root";

    // Static for the singleton that extends this
    private Connection conn = null;
    private String createStatement;
    public String TAG = "mysqlhelper";
    // Control for our connection
    private Statement statement = null;
    private PreparedStatement pStatement = null;
    private ResultSet resultSet = null;

    public MySQLHelper() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = String.format("jdbc:mysql://%s:%d/%s", this.server, this.port, this.database);
            conn = DriverManager.getConnection(url, this.user, this.password);
            /**
             * Make sure the database exists, if not, create it
             */
            this.pStatement = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS ?");
            this.pStatement.setString(1, this.database);
            this.pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.checkForTables();
    }

    public Connection getConnection() {return conn;}

    public void checkForTables() {
        String createStatement = "USE Spidey\n" +
                "CREATE TABLE IF NOT EXISTS `Records`(\n" +
                "  `RecordID` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `URL` text NOT NULL,\n" +
                "  `Domain` text NOT NULL,\n" +
                "  `Args` text NOT NULL,\n" +
                "  `Title` text NOT NULL,\n" +
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
                Statement create = conn.createStatement();
                create.execute(createStatement);
                Log.d(TAG, "Attempting to create the table", 5);
        } catch(SQLException e) {
            Log.d(TAG, "Error creating table.", 5);
        }
    }

    public ResultSet runSql(String sql) throws SQLException {
        Statement sta = conn.createStatement();
        return sta.executeQuery(sql);
    }

    public boolean runSql2(String sql) throws SQLException {
        Statement sta = conn.createStatement();
        return sta.execute(sql);
    }

    public void runPreparedStatement(Map m) {

    }

    @Override
    protected void finalize() throws Throwable {
        if (conn != null || !conn.isClosed()) {
            conn.close();
        }
    }
}