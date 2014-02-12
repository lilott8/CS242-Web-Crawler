import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jason on 1/21/14.
 */
public class Crawler implements Runnable {
    // Parser to actually parse our documents
    Parser mParser;
    // just for troubleshooting
    public String TAG = "crawler";
    private Robots mRobots;
    private String mDomain;
    private int mTid;
    private String currentURL;
    private MySQLHelper db;
    private boolean isDone = false;
    private boolean usDB = true;

    // Constructors
    public Crawler() {}
    public Crawler(String[] url) {
        //this.mFolderPath = new File(fn);
        this.mParser = new Parser();
        this.mRobots = new Robots();
        // Create a new database connection
        if(this.usDB) {
            this.db = new MySQLHelper();
        }
        for(String s : url) {
            LinkQueue.addLink(s);
        }
        //this.mParser.parseDocument(url);
        //this.loadUrls();
    }

    public ArrayList<String> getQueue() {return LinkQueue.getQueue(this.mTid);}

    public void run() {
        String el;
        int id = 0;

        Log.d(TAG, "Starting to run", 7);
        Log.d(TAG, this.mTid + "'s Queue size: " + this.getQueueSize(), 6);
        //this.printQueue();

        while(!LinkQueue.isQueueEmpty(this.mTid)) {
            // pop the first element off the queue
            el = LinkQueue.getUrl(this.mTid);
            this.currentURL = el;
            Log.d(TAG, String.format("Attempting to crawl: %s", el), 7);
            Log.d(TAG, String.format("tid %d's Queue size is: %s", this.mTid, this.getQueueSize()), 7);
            // check for robots first, then add
            try {
                this.mDomain = Robots.getDomain(el);
            } catch(URISyntaxException e) {/* do nothing */}
            // Check first here, not in robots
            if(!RobotRules.isInRobots(this.mDomain)) {
                Log.d(TAG, this.mDomain + " is not in robots", 7);
                this.mRobots.getRobots(el);
            }
            /**
             * We can do this without checking, because we wouldn't
             * be here in the first place.
             */
            this.mParser.parseDocument(el);
            // Comment this out if we need to
            id = this.selectRecordIDByURL();
            if(id > 0) {
                this.incrementLinkBack(id);
            } else {
                this.insertRow();
            }
            this.loadUrls();
            RobotRules.printKeys();
        }
        Log.d(TAG, "============THREAD "+ this.mTid +" RAN OUT OF URLS============", 1);
        this.isDone = true;
    }

    public int getQueueSize() {
        try {
            return LinkQueue.getQueueSize(this.mTid);
        } catch(NullPointerException e) {
            return 0;
        }
    }

    public void printQueue() {
        Log.d(TAG, "Printing queue...", 7);
        for(String e : LinkQueue.getQueue(this.mTid)) {
            Log.d(TAG, e, 7);
        }
    }

    /**
     * This loads only the urls that are not in the deny portion of
     * a robots.txt and not already in the queue
     */
    public void loadUrls() {
        String path;
        String[] args;
        for(String s : this.mParser.getUrls()) {
            /*
                TODO: parse the url to get just the right side of the url
             */
            // Log.d(TAG, s, 1);
            Log.d(TAG, String.format("Right part of url: %s", LinkQueue.getFirstPartOfURL(s)), 6);
            try {
                URL url = new URL(s);
                path = url.getPath();
            } catch (MalformedURLException e) {
                path = s;
            }
            // Log.d(TAG, path, 3);
            args = path.split("/");
            try {
                if(RobotRules.isAllowed(this.mDomain, args[1])) {
                    LinkQueue.addLink(s);
                }
                //Log.d(TAG, String.format("We cannot crawl: %s", s), 3);
            } catch(ArrayIndexOutOfBoundsException e) {
                if(RobotRules.isAllowed(this.mDomain, "")) {
                    LinkQueue.addLink(s);
                }
                break;
            }
        }
    }

    private void insertRow() {
        Map<String, String> strings = new HashMap<String, String>();
        Map<String, Integer> ints = new HashMap<String, Integer>();
        java.util.Date date= new java.util.Date();
        new Timestamp(date.getTime());
        // our string based values
        strings.put("url", this.currentURL); // full url
        strings.put("domain", this.mDomain); // domain of the site
        strings.put("args", "");// arguments of url after domain
        strings.put("title", this.mParser.getTitle()); // title of the page
        strings.put("head", this.mParser.getHead()); // the head of the document
        strings.put("body", this.mParser.getBody()); // page body
        strings.put("raw", this.mParser.getWholePage()); // get the whole document
        // Our integer based values
        ints.put("updatetime", 0); // when was the page updated
        ints.put("linksto", 0); // the number of pages this links to
        ints.put("linkbacks", 0); // the number of pages that links back here
        ints.put("loadtime", (int) this.mParser.getPageLoadTime()); // how long did the page take to load

        if(this.usDB) {
            this.db.insertRecord(strings, ints);
        }
    }

    private void updateRecord() {

    }

    private int selectRecordIDByURL() {
        if(this.usDB) {
            return this.db.selectRecordIDByURL(this.currentURL);
        } else {
            return -1;
        }
    }

    private void incrementLinkBack(int id) {
        if(this.usDB) {
            this.db.incrementLinkBack(id);
        }
    }

    public void setTid(int i) {this.mTid = i;}
    public boolean getIsDone() {return this.isDone;}
    public void setIsDone(boolean b){this.isDone = b;}
}
