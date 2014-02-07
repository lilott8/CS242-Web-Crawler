import com.google.common.net.InternetDomainName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
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

    public Robots() {}

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
                Log.d(TAG, String.format("Attempting to grab: %s", s+u+"/robots.txt"), 2);
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

        ArrayList<String> deny = new ArrayList<String>();
        ArrayList<String> allow = new ArrayList<String>();

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            while((line = br.readLine()) != null) {
                // Split the robots directives
                // we need to ignore this
                if(line.contains("#")) {
                    // do nothing
                    Log.d(TAG, String.format("We found a comment: %s", line), 3);
                } else if(line.contains(":")){
                    rule = line.split(":");
                    // Strip all the white space
                    if(rule.length < 2) {
                        rule[0] = rule[0].replaceAll("\\s","");
                        rule[1] = rule[1].replaceAll("\\s","");
                    }
                    if(rule[0].toLowerCase().equals("user-agent") && rule[1].equals("*")) {
                        // Put the rule where it belongs
                        if(rule[0].toLowerCase().equals("disallow")) {
                            // If we get a deny with the /, deny everything
                            if(rule[1].equals("/")) {
                                RobotRules.clearRuleSet(this.mDomain, "allow");
                                deny.add("DENY_ALL");
                                break;
                            }
                            deny.add(rule[1]);
                            // Check for allows
                        } else if(rule[0].toLowerCase().equals("allow")){
                            if(rule[1].equals("/")) {
                                RobotRules.clearRuleSet(this.mDomain, "deny");
                                allow.add("ALLOW_ALL");
                                break;
                            }
                            allow.add(rule[1]);
                        } else if(rule[0].toLowerCase().equals("crawl-delay")){
                            this.crawlSpeed = Integer.parseInt(rule[1]);
                        } else {/** Empty else **/}
                    }
                }
            }// while
            // set crawl speed if it isnt set yet
            if(this.crawlSpeed <0) {this.crawlSpeed = 10;}
        } catch (IOException e) {
            Log.d(TAG, String.format("Error parsing: %s", e.toString()), 3);
            return;
        }
        RobotRules.addRule(this.mDomain, allow, deny);
    }

    public void setCrawlSpeed(int i) {
        this.crawlSpeed = i;
    }

    public static String getDomain(String u) throws URISyntaxException {
        try {
            URL urls = new URL(u);
            String[] domain = urls.getHost().split("\\.");
            return domain[domain.length-2] + "." + domain[domain.length-1];
        } catch(MalformedURLException e) {
            Log.d("robots", "MalformedURL: " + e.getMessage(), 4);
            return null;
        }
    }

    private void getInputStream(URL url) throws IOException{
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        con.setConnectTimeout(1000);
        con.setReadTimeout(1000);
        this.parseRobots(in);
        in.close();
    }
}
