import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by jason on 2/3/14.
 */
public class Robots {

    // Domain of the robots.txt
    private String mDomain;
    private ArrayList<String> allowed = new ArrayList<String>();
    private ArrayList<String> denied = new ArrayList<String>();
    private int crawlSpeed;
    private String TAG = "robots";

    public Robots() {}

    public Robots(String url) {
        this.mDomain = url;
    }

    public void getRobots(String u) {
        // get the file
        try {
            URL url = new URL(u + "/robots.txt");;
            URLConnection con = url.openConnection();
            con.setConnectTimeout(1000);
            con.setReadTimeout(1000);
            InputStream in = con.getInputStream();
            this.parseRobots(in);
        } catch(Exception e) {
            Log.d(TAG, String.format("Error parsing url: %s, with %s", this.mDomain, e.getMessage()), 4);
        }
    }

    public void parseRobots(InputStream in) {
        String line;
        /*try {

        } catch (IOException e) {

        }*/
        this.crawlSpeed = 10;
    }

    public boolean isAllowed(String url) {
        return false;
    }

    public boolean isDenied(String url) {
        return false;
    }

    public void setCrawlSpeed(int i) {
        this.crawlSpeed = i;
    }

}
