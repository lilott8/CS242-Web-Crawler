import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jason on 1/21/14.
 */
public class Crawler extends Spidey implements Runnable {
    // File for where to save things, not implemented
    // private File mFolderPath;
    // Parser to actually parse our documents
    Parser mParser;
    // just for troubleshooting
    public String TAG = "crawler";
    // Our fake queue, this is easier to work with than a queue in java
    private ArrayList<String> mURLQueue = new ArrayList<String>();
    // validates urls so we know to follow them or not
    //UrlValidator mURLValidator;
    private Robots mRobots;
    // empty because of the implicit super() call
    // from the child, parser
    public Crawler() {}

    // Constructor
    public Crawler(String fn, String url) {
        //this.mFolderPath = new File(fn);
        this.mParser = new Parser();
        this.mParser.parseDocument(url);
        this.mRobots = new Robots();
        this.loadUrls();
    }

    public ArrayList<String> getQueue() {
        return this.mURLQueue;
    }

    public void run() {
        Log.d(TAG, "Starting to run", 7);
        String el;
        Log.d(TAG, "Queue size: " + this.getQueueSize(), 7);
        this.printQueue();
        while(!this.mURLQueue.isEmpty()) {
            // pop the first element off the queue
            el = this.mURLQueue.remove(0);
            Log.d(TAG, String.format("Attempting to crawl: %s", el), 6);
            Log.d(TAG, String.format("Queue size is: %s", this.getQueueSize()), 6);
            this.mRobots.getRobots(el);
            // parse the document
            this.mParser.parseDocument(el);
            this.loadUrls();
        }
        Log.d(TAG, "We ran out of urls to parse", 7);
    }

    public int getQueueSize() {
        return this.mURLQueue.size();
    }

    public void printQueue() {
        Log.d(TAG, "Printing queue...", 7);
        for(String e : this.mURLQueue) {
            Log.d(TAG, e, 7);
        }
    }

    public void loadUrls() {
            this.mURLQueue.addAll(this.mParser.getUrls());
    }

    public boolean inQueue(String url) {
        return this.mURLQueue.contains(url);
    }

    public HashMap getRules() {return this.mRobots.getRules();}
    public boolean isInRobots(String url) {
        return super.isInRobots(url);
    }

}
