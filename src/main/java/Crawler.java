import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.validator.routines.UrlValidator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jason on 1/21/14.
 */
public class Crawler implements Runnable {
    // File for where to save things, not implemented
    private File mFolderPath;
    // Jsoup document parser
    Document mDocument;
    // just for troubleshooting
    public String TAG = "crawler: ";
    // flag for printing to screen
    public boolean mPrintMessages = true;
    // Our fake queue, this is easier to work with than a queue in java
    private ArrayList<String> mURLQueue = new ArrayList<String>();
    // a flag, not implemented
    private String mCurDomain;
    // validates urls so we know to follow them or not
    UrlValidator mURLValidator;
    private RobotstxtConfig mRobotsConfig = new RobotstxtConfig();
    private RobotstxtServer mRobotsServer;
    private edu.uci.ics.crawler4j.fetcher.PageFetcher mPageFetcher;

    public Crawler() {
        this.mFolderPath = new File("/Volumes/Virtual Machines/spidey/");
        this.mURLValidator = new UrlValidator(new String[]{"http", "https"});
        String url = "http://www.ucr.edu";
        try {
            this.mDocument = Jsoup.connect(url).get();
            this.mRobotsServer = new RobotstxtServer(this.mRobotsConfig, this.mPageFetcher);
            this.loadUrls();
        } catch (IOException e) {
            Log.d(TAG, String.format("Could not open our document: %s", e.toString()), Log.getLevel(3));
        }
    }

    public Crawler(String fn, String url) {
        this.mFolderPath = new File(fn);
        this.mURLValidator = new UrlValidator(new String[]{"http", "https"});
        try {
            this.mDocument = Jsoup.connect(url).get();
            this.mRobotsServer = new RobotstxtServer(this.mRobotsConfig, this.mPageFetcher);
            this.loadUrls();
        } catch (IOException e) {
            Log.d(TAG, String.format("Could not open our document: %s", e.toString()), Log.getLevel(3));
        }
    }

    public void printPath() {
        Log.d(TAG, this.mFolderPath.toString(), Log.getLevel(7));
    }

    public ArrayList<String> getQueue() {
        return this.mURLQueue;
    }

    public void run() {
        Log.d(TAG, "Starting to run", Log.getLevel(7));
        String el;
        Log.d(TAG, "Queue size: " + this.getQueueSize(), Log.getLevel(7));
        this.printQueue();
        while(!this.mURLQueue.isEmpty()) {
            el = this.mURLQueue.remove(0);
            Log.d(TAG, String.format("Attempting to crawl: %s", el), Log.getLevel(6));
            Log.d(TAG, String.format("Queue size is: %s", this.getQueueSize()), Log.getLevel(6));
            try{
                WebURL url = new WebURL();
                url.setURL(el);
                if(this.mRobotsServer.allows(url)) {
                this.mDocument = Jsoup.connect(el).get(); // get the next page
                this.loadUrls();
                } else {
                    Log.d(TAG, String.format("We cannot crawl: %s", el), Log.getLevel(4));
                }
            } catch(IOException e) {
                Log.d(TAG, String.format("We couldn't connect to: %s", el), Log.getLevel(5));
            }
        }
            //this.mDocument = Jsoup.connect(this.mURLQueue.);
        Log.d(TAG, "We ran out of urls to parse", Log.getLevel(7));
    }

    public int getQueueSize() {
        return this.mURLQueue.size();
    }

    public void printQueue() {
        Log.d(TAG, "Printing queue...", Log.getLevel(7));
        for(String e : this.mURLQueue) {
            Log.d(TAG, e, Log.getLevel(7));
        }
    }

    public void loadUrls() {
        // Get all the links on a page
        Elements links = this.mDocument.getElementsByTag("a");
        // Start building our seed
        for(Element e : links) {
            // make sure the url is valid, if it is push it onto our "queue"
            String url = e.attr("href");
            if(this.mURLValidator.isValid(url)) {
                Log.d(TAG, String.format("This url: %s is valid", url), Log.getLevel(6));
                if(!this.mURLQueue.contains(url)) {
                    this.mURLQueue.add(url);
                } else {
                    Log.d(TAG, String.format("We already have: %s in our queue", e.attr("href")), Log.getLevel(6));
                }
            } else {
                Log.d(TAG, String.format("This url: %s is not a valid url", url),Log.getLevel(6));
            }
        }
    }

    public boolean isAllowedToCrawl(String host) {



        return false;
    }
}
