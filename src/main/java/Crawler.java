import crawlercommons.robots.RobotUtils;
import sun.awt.image.ImageWatched;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Created by jason on 1/21/14.
 */
public class Crawler implements Runnable {
    // File for where to save things, not implemented
    // private File mFolderPath;
    // Parser to actually parse our documents
    Parser mParser;
    // just for troubleshooting
    public String TAG = "crawler";
    private Robots mRobots;
    private String mDomain;
    private int mTid;
    private String currentURL;
    private MySQLHelper db;
    private boolean isDone;

    // Constructors
    public Crawler() {}
    public Crawler(String fn, String url) {
        //this.mFolderPath = new File(fn);
        this.mParser = new Parser();
        this.mParser.parseDocument(url);
        this.mRobots = new Robots();
        this.loadUrls();
        // Create a new database connection
        this.db = new MySQLHelper();
    }

    public ArrayList<String> getQueue() {return LinkQueue.getQueue(this.mTid);}

    public void run() {
        String el;
        int id = 0;

        Log.d(TAG, "Starting to run", 7);
        Log.d(TAG, "Queue size: " + this.getQueueSize(), 2);
        //this.printQueue();

        while(!LinkQueue.isQueueEmpty(this.mTid)) {
            // pop the first element off the queue
            el = LinkQueue.getUrl(this.mTid);
            this.currentURL = el;
            Log.d(TAG, String.format("Attempting to crawl: %s", el), 7);
            Log.d(TAG, String.format("tid %d's Queue size is: %s", this.mTid, this.getQueueSize()), 7);
            // check for robots first, then add
            // if !super.isInRobots()
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
            // id = this.selectRecordIDByURL();
            if(id > 0) {
                this.incrementLinkBack(id);
            } else {
                this.insertRow();
            }
            this.loadUrls();
            RobotRules.printKeys();
        }
        Log.d(TAG, "We ran out of urls to parse", 7);
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
            //Log.d(TAG, String.format("Right part of url: %s", LinkQueue.getFirstPartOfURL(s)), 1);
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
                    LinkQueue.addLink(this.mTid, s);
                }
                //Log.d(TAG, String.format("We cannot crawl: %s", s), 3);
            } catch(ArrayIndexOutOfBoundsException e) {
                if(RobotRules.isAllowed(this.mDomain, "")) {
                    LinkQueue.addLink(this.mTid, s);
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
        ints.put("linksback", 0); // the number of pages that links back here
        ints.put("loadtime", (int) this.mParser.getPageLoadTime()); // how long did the page take to load

        this.db.insertRecord(strings, ints);
    }

    private void updateRecord() {

    }

    private int selectRecordIDByURL() {
        return this.db.selectRecordIDByURL(String.format(
                "SELECT RecordID from Records WHERE url LIKE \"%%%s%\"",
                this.currentURL)
        );
    }

    private void incrementLinkBack(int id) {
        this.db.incrementLinkBack(id);
    }

    public void setTid(int i) {this.mTid = i;}
}
