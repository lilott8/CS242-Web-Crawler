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
    public static int mNumThreads;

    // hash the url %5 determines the bucket it belongs in
    public static synchronized void addLink(String url) {
        // which bucket does the url belong in
        int bucket = Math.abs(url.hashCode() % mNumThreads);
        // Log.d(TAG, String.format("Putting: %s in bucket: %d", url, bucket), 3);
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
        Log.d(TAG, String.format("Bucket: %d\t Queue Size: %d", bucket, mLinkQueue.get(bucket).size()), 6);
    }

    public static ArrayList<String> getQueue(int tid) {
        return mLinkQueue.get(tid);
    }

    public static synchronized String getUrl(int tid) {
        return mLinkQueue.get(tid).remove(0);
    }

    public static synchronized boolean isQueueEmpty(int tid) {
        try {
            return mLinkQueue.get(tid).isEmpty();
        } catch (NullPointerException e) {
            return true;
        }
    }

    public static int getQueueSize(int tid) {
        return mLinkQueue.get(tid).size();
    }

    public static boolean inQueue(int tid, String u) {
        return mLinkQueue.get(tid).contains(u);
    }

    public static String getFirstPartOfURL(String s) {
        return s.split("/")[2];
    }

    public static void setNumThreads(int i) {mNumThreads = i;}
}