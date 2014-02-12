import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jason on 2/3/14.
 */
public class Robots {

    // Domain of the robots.txt
    private String mDomain;
    // crawl speed for a website
    private int crawlSpeed;
    private String TAG = "robots";
    private String[] accessMethods = new String[]{"http://www.", "http://", "https://www.", "https://"};
    private boolean isGlobalCrawler = false;
    private Map<String, Integer> mDirectives = new HashMap<String, Integer>();

    public Robots() {
        this.mDirectives.put("disallow", 1);
        this.mDirectives.put("allow", 2);
        this.mDirectives.put("user-agent", 3);
        this.mDirectives.put("crawl-delay", 4);
        this.mDirectives.put("site-map", 5);
        this.mDirectives.put("#", 6);
    }

    public Robots(String url) {
        this.mDomain = url;
    }

    /**
     * We will need to tweak this a little bit
     * If the http doesn't resolve, try the
     * https, if not that, minus the www, and so on
     * @param u
     */
    public void getRobots(String u) {
        // Create the new url
        URL url;
        try {
            // get the root domain of the url
            u = Robots.getDomain(u);
            this.mDomain = u;
        } catch(URISyntaxException e) {
            Log.d(TAG, String.format("Malformed domain: %s", e.getMessage()), 4);
            return;
        }
        try {
            // iterate our available cases
            for(String s : this.accessMethods) {
                // form the URI for the robots
                url = new URL(s + u + "/robots.txt");
                Log.d(TAG, String.format("Attempting to grab: %s", s + u + "/robots.txt"), 6);
                try {
                    // attempt to get the robots file
                    this.getInputStream(url);
                    // if we succeed, leave the function
                    return;
                } catch (IOException e) {
                    Log.d(TAG, String.format("Error reading file: %s", e.getMessage()), 4);
                }
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, String.format("Malformed URL: %s", e.getMessage()), 4);
        }
    }

    public void parseRobots(InputStream in) {
        String line;
        String[] rule;

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            while((line = br.readLine()) != null) {
                if(this.isValidDirectiveLine(line)) {
                    // split our line on the colon
                    rule = line.split(":");
                    // strip the space
                    rule[0] = rule[0].replaceAll("\\s","").toLowerCase();
                    try {
                        rule[1] = rule[1].replaceAll("\\s","").toLowerCase();
                    } catch(ArrayIndexOutOfBoundsException e) {
                        // skip this rule, it's not correct
                        continue;
                    }
                    // parse the directives
                    switch(RobotRules.translateRule(rule[0])) {
                        case 1:
                            this.parseDisallow(rule[1]);
                            break;
                        case 2:
                            this.parseAllow(rule[1]);
                            break;
                        case 3:
                            this.parseUserAgent(rule[1]);
                            break;
                        case 4:
                            this.crawlSpeed = Integer.parseInt(rule[1]);
                            break;
                        case 5:
                            break;
                    }
                }
            }
        } catch(IOException e) {
            Log.d(TAG, String.format("IOException: %s", e.getMessage()), 3);
        }
    }

    public void setCrawlSpeed(int i) {
        this.crawlSpeed = i;
    }

    public static String getDomain(String u) throws URISyntaxException {
        try {
            URL urls = new URL(u);
            return urls.getAuthority();
        } catch(MalformedURLException e) {
            Log.d("robots", "MalformedURL: " + e.getMessage(), 4);
            return null;
        }
    }

    private void getInputStream(URL url) throws IOException{
        URLConnection con = url.openConnection();
        InputStream in = url.openConnection().getInputStream();
        con.setConnectTimeout(1000);
        con.setReadTimeout(1000);
        this.parseRobots(in);
        in.close();
    }

    private void parseDisallow(String s) {
        if(this.isGlobalCrawler) {
            if(s.equals("/")) {
                RobotRules.clearRuleSet(this.mDomain, "allow");
                RobotRules.addDenyRule(this.mDomain, "1");
            } else {
                RobotRules.addDenyRule(this.mDomain, s);
            }
        }
    }

    private void parseAllow(String s) {
        if(this.isGlobalCrawler) {
            if(s.equals("/")) {
                RobotRules.clearRuleSet(this.mDomain, "deny");
                RobotRules.addAllowRule(this.mDomain, "1");
            } else {
                RobotRules.addAllowRule(this.mDomain, s);
            }
        }
    }

    private void parseUserAgent(String s) {
        this.isGlobalCrawler = s.contains("*");
    }

    /**
     *
     * @param s
     * @return
     *
     * Cases to account for:
     *  if linebreak
     *  if comment
     *  if actual line
     *
     */
    private boolean isValidDirectiveLine(String s) {
        if(s.indexOf("#") == 0){ return false; }
        else if(s.length() < 2){ return false; }
        else if(s.isEmpty()) { return false; }
        else { return s.contains(":"); }
    }
}
