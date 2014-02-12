/**
 * Created by james on 1/27/14.
 * source from http://www.programcreek.com/2012/12/how-to-make-a-web-crawler-using-java/
 */
import java.sql.*;
import java.util.Map;

import com.mysql.jdbc.Driver;


public class MySQLHelper {

    //String server = "localhost";
    String server = "192.168.1.128";
    int port = 3306;
    String database = "Spidey";
    String user = "root";
    String password = "root";

    private Connection conn = null;
    private String createStatement;
    public String TAG = "mysqlhelper";
    private Connector connector;
    // Control for our connection
    private Statement statement = null;
    private PreparedStatement pStatement = null;
    private ResultSet resultSet = null;

    public MySQLHelper() {
        this.connector = Connector.getInstance();
        this.conn = this.connector.getConnection();

        /*try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = String.format("jdbc:mysql://%s:%d/%s", this.server, this.port, this.database);
            conn = DriverManager.getConnection(url, this.user, this.password);

            this.pStatement = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS ?");
            this.pStatement.setString(1, this.database);
            this.pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        */

    }

    public Connection getConnection() {return conn;}

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