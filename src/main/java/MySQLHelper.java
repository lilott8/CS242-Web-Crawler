/**
 * Created by james on 1/27/14.
 * source from http://www.programcreek.com/2012/12/how-to-make-a-web-crawler-using-java/
 */
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import com.mysql.jdbc.Driver;


public class MySQLHelper {

    //String server = "localhost";
    String server = "192.168.1.128";
    int port = 3306;
    String database = "Spidey";
    String user = "root";
    String password = "root";

    private Connection connection = null;
    private String createStatement;
    public String TAG = "mysqlhelper";
    private Connector connector;
    // Control for our connection
    private Statement statement = null;
    private PreparedStatement pStatement = null;
    private ResultSet resultSet = null;

    public MySQLHelper() {
        this.connector = Connector.getInstance();
        this.connection = this.connector.getConnection();
        try {
            this.statement = this.connection.createStatement();
        } catch(SQLException e) {
            Log.d(TAG, "Error creating statements: " + e.getMessage(), 2);
        }
    }

    //public Connection getConnection() {return connection;}

    public ResultSet runSql(String sql) throws SQLException {
        Statement sta = connection.createStatement();
        return sta.executeQuery(sql);
    }

    public boolean runSql2(String sql) throws SQLException {
        Statement sta = connection.createStatement();
        return sta.execute(sql);
    }

    public void insertRecord(Map s, Map i) {
        StringBuilder sb = new StringBuilder();

        String query = String.format("INSERT INTO `Records` (`URL`, `Domain`, `Args`, `Title`, `Head`, `Body`, " +
                "`Raw`, `UpdateTime`, `LinksTo`, `LinksBack`, `LoadTime`) VALUES (" +
                "\"%s\", \"%s\", \"%s\", \"%s\", " +
                "\"%s\", \"%s\", \"%s\", %d, " +
                "%d, %d, %d)",
                s.get("url"), s.get("domain"), s.get("args"), s.get("title"),
                s.get("head"), s.get("body"), s.get("raw"), i.get("updatetime"),
                i.get("linksto"), i.get("linksback"), i.get("loadtime"));
        try {
            this.statement.execute(query);
        } catch(SQLException e) {
            Log.d(TAG, "Error inserting record: " + e.getMessage(), 1);
        }
    }

    public int selectRecordIDByURL(String u) {
        int id = 0;
        Log.d(TAG, u, 1);
        try{
            ResultSet s = this.statement.executeQuery(u);
            while(s.next()){
                id = s.getInt(0);
            }
        } catch (SQLException e) {
            return id;
        }
        return id;
    }

    public void incrementLinkBack(int id) {
        int linkBacks = 0;
        try {
            ResultSet s = this.statement.executeQuery("SELECT linkback FROM Records WHERE RecordID = " + id);
            linkBacks = s.getInt(0);
            linkBacks++;
            this.statement.execute(String.format("Update Records SET linkbacks = %d WHERE RecordID = %d", linkBacks, id));
        } catch(SQLException e) {
            Log.d(TAG, "ERROR doing something: "+e.getMessage(), 1);
        }
    }
}