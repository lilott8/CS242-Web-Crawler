import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jason on 2/6/14.
 */
public class RobotRules {

    public static HashMap<String, HashMap<String, ArrayList<String>>> mRules =
            new HashMap<String, HashMap<String, ArrayList<String>>>();
    public static final String TAG = "robotrules";

    public static boolean addRule(String domain, final String action, final String url) {
        try {
            // map.put(.0F,new HashMap(){{put(.0F,0);}});
            mRules.put(domain, new HashMap(){{put(action, new ArrayList().add(url));}});
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Error adding a new rule: " + e.getMessage(), 2);
            return false;
        }
    }


    public static boolean isInRobots(String url) {
        Log.d(TAG, url, 1);
        Log.d(TAG, Boolean.toString(mRules.containsKey(url)), 1);
        return mRules.containsKey(url);
    }

    public static boolean isAllowed(String domain, String url) {
        // check for deny_all
        if (mRules.get(domain).get("deny").contains("DENY_ALL")) {
            return false;
        }
        // check for allow_all
        else if(mRules.get(domain).get("allow").contains("ALLOW_ALL")) {
            return true;
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

    public static void clearRuleSet(String domain, String action) {
        mRules.get(domain).get(action).clear();
    }
}
