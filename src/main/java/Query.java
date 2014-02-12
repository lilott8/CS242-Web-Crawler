import java.sql.Connection;

/**
 * Created by jason on 2/11/14.
 */
public class Query extends MySQLHelper {

    private Connection connection;
    public static Query instance = null;

    protected Query() {


    }

}
