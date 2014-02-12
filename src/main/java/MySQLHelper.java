/**
 * Created by james on 1/27/14.
 * source from http://www.programcreek.com/2012/12/how-to-make-a-web-crawler-using-java/
 */
import java.sql.*;
import java.util.Map;


public class MySQLHelper {

    private Connection connection = null;
    private String createStatement;
    public String TAG = "mysqlhelper";
    private Connector connector;
    // Control for our connection
    private Statement statement = null;
    private PreparedStatement pStatement = null;
    private ResultSet resultSet = null;
    private boolean dbAvailable;

    public MySQLHelper() {
        this.connector = Connector.getInstance();
        this.connection = this.connector.getConnection();
        try {
            this.statement = this.connection.createStatement();
            this.dbAvailable = true;
        } catch(Exception e) {
            Log.d(TAG, "Error creating statements: " + e.getMessage(), 3);
            this.dbAvailable = false;
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
        String query = String.format("INSERT INTO `records` (`URL`, `Domain`, `Args`, `Title`, `Head`, `Body`, " +
                "`Raw`, `UpdateTime`, `LinksTo`, `LinkBacks`, `LoadTime`) VALUES (" +
                "\"%s\", \"%s\", \"%s\", \"%s\", " +
                "\"%s\", \"%s\", \"%s\", %d, " +
                "%d, %d, %d)",
                s.get("url"), s.get("domain"), s.get("args"), s.get("title"),
                s.get("head"), s.get("body"), s.get("raw"), i.get("updatetime"),
                i.get("linksto"), i.get("linkbacks"), i.get("loadtime"));
        this.query(query);
    }

    public int selectRecordIDByURL(String u) {
        int id = 0;
        String query = String.format("SELECT id from records WHERE url = '%s'", u);
        Log.d(TAG, query, 6);
        ResultSet s = this.queryForResult(query);
        try {
            while(s.next()){
                id = s.getInt("id");
            }
            Log.d(TAG, "Id from selectRecordIDBYURL: " + id, 7);
        } catch (SQLException e) {
            Log.d(TAG, "error: " + e.getMessage(), 3);
            return id;
        } catch(NullPointerException e) {
            Log.d(TAG, "error: " + e.getMessage(), 3);
        }
        return id;
    }

    public void incrementLinkBack(int id) {
        int linkBacks = 0;
        String query = String.format("SELECT linkbacks FROM records WHERE id = %d", id);
            ResultSet s = this.queryForResult(query);
        try {
        while(s.next()){
                linkBacks = s.getInt("LinkBacks");
        }
            linkBacks++;
            this.query(String.format("Update records SET linkbacks = %d WHERE id = %d", linkBacks, id));
        } catch(SQLException e) {
            Log.d(TAG, "Error iterating the result: " + e.getMessage(), 3);
        } catch(NullPointerException e) {
            Log.d(TAG, "error: " + e.getMessage(), 3);
        }
    }

    public void query(String query) {
        if(!this.dbAvailable) {return;}
        try {
            this.statement.execute(query);
        } catch (SQLException e) {
            Log.d(TAG, String.format("Error running query: %s\n with query: %s", e.getMessage(), query), 3);
        }
    }

    public ResultSet queryForResult(String query) {
        if(!this.dbAvailable){return null;}
        ResultSet result;
        try {
            result = this.statement.executeQuery(query);
        } catch (SQLException e) {
            Log.d(TAG, String.format("Error running query: %s\n with query: %s", e.getMessage(), query), 3);
            result = null;
        }
        return result;
    }
}