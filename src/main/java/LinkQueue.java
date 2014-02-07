import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jason on 2/7/14.
 */
public class LinkQueue {

    private static Map<Integer, ArrayList<String>> mLinkQueue = new HashMap<Integer, ArrayList<String>>();
    public static final String TAG = "linkqueue";

    // hash the url %5 determines the bucket it belongs in
    public static synchronized void addLink(int tid, String url) {
        // which bucket does the url belong in
        int bucket = url.hashCode() % Spidey.getThreadCount();
        // List of urls for a thread to crawl
        ArrayList<String> items = mLinkQueue.get(bucket);

        // if list does not exist create it
        if(items == null) {
            items = new ArrayList<String>();
            items.add(url);
            mLinkQueue.put(bucket, items);
        } else {
            // add if item is not already in list
            if(!items.contains(url)) items.add(url);
        }
    }

    public static ArrayList<String> getQueue(int tid) {
        return mLinkQueue.get(tid);
    }

    public synchronized static String getUrl(int tid) {
        return mLinkQueue.get(tid).remove(0);
    }

    public static boolean isQueueEmpty(int tid) {
        return mLinkQueue.get(tid).isEmpty();
    }

    public static int getQueueSize(int tid) {
        return mLinkQueue.get(tid).size();
    }

    public static boolean inQueue(int tid, String u) {
        return mLinkQueue.get(tid).contains(u);
    }
}
