import crawlercommons.robots.RobotUtils;
import sun.awt.image.ImageWatched;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

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
    // Our fake queue, this is easier to work with than a queue in java
    //private ArrayList<String> mURLQueue = new ArrayList<String>();
    // validates urls so we know to follow them or not
    //UrlValidator mURLValidator;
    private Robots mRobots;
    private String mDomain;
    private int mTid;

    // Constructors
    public Crawler() {}
    public Crawler(String fn, String url) {
        //this.mFolderPath = new File(fn);
        this.mParser = new Parser();
        this.mParser.parseDocument(url);
        this.mRobots = new Robots();
        this.loadUrls();
    }

    public ArrayList<String> getQueue() {return LinkQueue.getQueue(this.mTid);}

    public void run() {
        String el;

        Log.d(TAG, "Starting to run", 7);
        Log.d(TAG, "Queue size: " + this.getQueueSize(), 7);
        this.printQueue();

        while(!LinkQueue.isQueueEmpty(this.mTid)) {
            // pop the first element off the queue
            el = LinkQueue.getUrl(this.mTid);
            Log.d(TAG, String.format("Attempting to crawl: %s", el), 7);
            Log.d(TAG, String.format("tid %d's Queue size is: %s", this.mTid, this.getQueueSize()), 7);
            // check for robots first, then add
            // if !super.isInRobots()
            try {
                this.mDomain = Robots.getDomain(el);
            } catch(URISyntaxException e) {/* do nothing */}
            // Check first here, not in robots
            if(!RobotRules.isInRobots(this.mDomain)) {
                Log.d(TAG, this.mDomain + " is not in robots", 4);
                this.mRobots.getRobots(el);
            }
            /**
             * We can do this without checking, because we wouldn't
             * be here in the first place.
             */
            this.mParser.parseDocument(el);
            this.loadUrls();
            RobotRules.printKeys();
        }
        Log.d(TAG, "We ran out of urls to parse", 7);
    }

    public int getQueueSize() {
        return LinkQueue.getQueueSize(this.mTid);
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
        for(String s : this.mParser.getUrls()) {
            /*
                TODO: parse the url to get just the right side of the url
             */
            // Log.d(TAG, s, 1);
            if(RobotRules.isAllowed(this.mDomain, s)) {
                LinkQueue.addLink(this.mTid, s);
            }
        }
    }

    public void setTid(int i) {this.mTid = i;}
}
