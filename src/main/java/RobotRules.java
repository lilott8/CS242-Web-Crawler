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


    public static synchronized void addRule(String domain, final ArrayList<String> allow, final ArrayList<String> deny) {
        Map<String, ArrayList<String>> temp = new HashMap<String, ArrayList<String>>();
        temp.put("allow", allow);
        temp.put("deny", deny);
        mRules.put(domain, temp);
        temp = null;
    }

    public static boolean isInRobots(String url) {
        return mRules.containsKey(url);
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

    public static void clearRuleSet(String domain, String action) {
        mRules.get(domain).get(action).clear();
    }

    public static void printKeys() {
        Set<String> s = mRules.keySet();
        for(String q : s) {
            Log.d(TAG, "Key: " + s, 7);
        }
    }
}
