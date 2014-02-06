import com.google.common.net.InternetDomainName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private ArrayList<String> mAllow = new ArrayList<String>();
    private ArrayList<String> mDeny = new ArrayList<String>();
    private int crawlSpeed;
    private String TAG = "robots";

    public Robots() {}

    public Robots(String url) {
        this.mDomain = url;
    }

    public void getRobots(String u) {
        // get the file
        try {
            u = this.getDomain(u);
        } catch(URISyntaxException e) {
            Log.d(TAG, String.format("Error getting domain: %s", e.getMessage()), 3);
        }
        try {
            URL url = new URL("http://www." + u + "/robots.txt");
            URLConnection con = url.openConnection();
            con.setConnectTimeout(1000);
            con.setReadTimeout(1000);
            InputStream in = con.getInputStream();
            this.parseRobots(in);
            in.close();
        } catch(Exception e) {
            Log.d(TAG, String.format("Error parsing url: %s, with %s", u, e.getMessage()), 4);
        }
    }

    public void parseRobots(InputStream in) {
        String line;
        String[] rule;
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            while((line = br.readLine()) != null) {
                // Split the robots directives
                rule = line.split(":");
                // Strip all the white space
                if(rule.length < 2) {
                    rule[0] = rule[0].replaceAll("\\s","");
                    rule[1] = rule[1].replaceAll("\\s","");
                }
                // Put the rule where it belongs
                if(rule[0].toLowerCase().equals("disallow")) {
                    // Log.d(TAG, "Disallow: " + rule[1], 1);
                    // If we get a deny with the /, deny everything
                    if(rule[1].equals("/")) {
                        this.mAllow.clear();
                        this.mAllow.add("DENY_ALL");
                    }
                    this.mDeny.add(rule[1]);
                // Check for allows
                } else if(rule[0].toLowerCase().equals("allow")){
                    if(rule[1].equals("/")) {
                        this.mDeny.clear();
                        this.mDeny.add("ALLOW_ALL");
                    }
                    this.mAllow.add(rule[1]);
                    // Log.d(TAG, "Allow: " + rule[1], 1);
                } else if(rule[0].toLowerCase().equals("crawl-delay")){
                    this.crawlSpeed = Integer.parseInt(rule[1]);
                } else {
                    this.crawlSpeed = 10;
                }
            }
        } catch (IOException e) {
            Log.d(TAG, String.format("Error parsing: %s", e.getMessage()), 2);
        }
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

    public String getDomain(String u) throws URISyntaxException {
        try {
            URL urls = new URL(u);
            String[] domain = urls.getHost().split("\\.");
            return domain[domain.length-2] + "." + domain[domain.length-1];
        } catch(MalformedURLException e) {
            Log.d(TAG, "MalformedURL: " + e.getMessage(), 2);
            return null;
        }
    }

    public ArrayList<String> getDeny() { return this.mDeny;}
    public ArrayList<String> getAllow() { return this.mAllow;}
}
