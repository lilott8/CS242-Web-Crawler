import com.sun.tools.corba.se.idl.TypedefEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by jason on 2/6/14.
 */

public class RobotRules {

    public static Map<String, Map<String, ArrayList<String>>> mRules =
            new HashMap<String, Map<String, ArrayList<String>>>();
    public static final String TAG = "robotrules";
    public static final Map<String, Integer> mDirectives = new HashMap<String, Integer>() {{
        put("disallow", 1);
        put("allow", 2);
        put("user-agent", 3);
        put("crawl-delay", 4);
        put("site-map", 5);
        put("#", 6);
    }};

    public static synchronized void addRuleSets(String domain, final ArrayList<String> allow, final ArrayList<String> deny) {
        Map<String, ArrayList<String>> temp = new HashMap<String, ArrayList<String>>();
        temp.put("allow", allow);
        temp.put("deny", deny);
        mRules.put(domain, temp);
        temp = null;
    }

    /**
     * add an allow rule to our robots
     * @param domain string of the domain
     * @param rule string of the rule to add
     */
    public static synchronized void addAllowRule(String domain, final String rule) {
        try {
            // mRules.get(domain).get("allow");
            mRules.get(domain).get("allow").add(rule);
        } catch (NullPointerException e) {
            // Temporary vars to add our new values to our hashmap
            ArrayList<String> alTemp = new ArrayList<String>();
            HashMap<String, ArrayList<String>> hmTemp = new HashMap<String, ArrayList<String>>();
            // add the items to our structures
            alTemp.add(rule);
            hmTemp.put("allow", alTemp);
            mRules.put(domain, hmTemp);
        }
    }

    /**
     * add a deny rule to our robots
     * @param domain string of the domain
     * @param rule string of the rule to add
     */
    public static synchronized void addDenyRule(String domain, final String rule) {
        try {
            // mRules.get(domain).get("deny");
            mRules.get(domain).get("deny").add(rule);
        } catch (NullPointerException e) {
            ArrayList<String> alTemp = new ArrayList<String>();
            HashMap<String, ArrayList<String>> hmTemp = new HashMap<String, ArrayList<String>>();
            alTemp.add(rule);
            hmTemp.put("deny", alTemp);
            mRules.put(domain, hmTemp);
            // mRules.get(domain).put("deny", new ArrayList<String>());
        }
    }

    /**
     * Clear the allow/deny array list
     * @param domain string of the domain
     * @param action string of what action to be removed
     */
    public static void clearRuleSet(String domain, String action) {
        try {
            mRules.get(domain).get(action).clear();
        } catch(NullPointerException e){}
    }

    /**
     * does this url exist in the ruleset
     * @param domain what to test against
     * @return boolean of if the rule is in the mRules
     */
    public static boolean isInRobots(String domain) {
        RobotRules.printRules(domain);
        return mRules.containsKey(domain);
    }

    /**
     *
     * @param domain string of the domain that is to be crawled
     * @param url string of the url portion of the page to be crawled
     * @return boolean if we are allowed to crawl the site
     *
     * There are only a few cases to account for:
     *  - deny = "DENY_ALL"
     *  - allow = "ALLOW_ALL"
     *  - deny from the robots
     *  - else scan the site
     */
    public static boolean isAllowed(String domain, String url) {
        int size_of_allow = sizeOfHashMap(domain, "allow");
        int size_of_deny = sizeOfHashMap(domain, "deny");

        // if nothing in deny return true
        if(size_of_deny < 1) {
            return true;
        }
        // check for deny_all
        if (mRules.get(domain).get("deny").contains("DENY_ALL")) {
            return false;
        }
        // check for specific deny url
        else if(mRules.get(domain).get("deny").contains(url)) {
            return false;
        }
        // allow
        else {
            return true;
        }
    }

    public static int sizeOfHashMap(String domain, String action) {
            try {
                return mRules.get(domain).get(action).size();
            } catch (NullPointerException e) {
                return 0;
            }
    }

    public static void printKeys() {
        Set<String> s = mRules.keySet();
        for(String q : s) {
            Log.d(TAG, "Key: " + s, 7);
        }
    }

    public static int translateRule(String key) {
        try {
            return mDirectives.get(key.toLowerCase());
        } catch(NullPointerException e) {
            return 1000;
        }
    }

    public static void printRules(String domain) {
        Map<String, ArrayList<String>> hmTemp = new HashMap<String, ArrayList<String>>();
        hmTemp = mRules.get(domain);
        StringBuilder dsb = new StringBuilder();
        try {
            for(String s : hmTemp.get("deny")) {
                dsb.append(s+"\t");
            }
            StringBuilder asb = new StringBuilder();
            for(String s : hmTemp.get("allow")) {
                asb.append(s + "\t");
            }
            Log.d(TAG, String.format("%s\n %s", dsb.toString(), asb.toString()), 3);
        } catch (NullPointerException e) {
            Log.d(TAG, "Error printingRules", 3);
        }
    }
}
