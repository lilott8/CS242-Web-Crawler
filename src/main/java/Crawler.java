//import com.sun.tools.javac.util.Paths;
import org.apache.commons.validator.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.logging.Logger;

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
    // Our fake queue, this is easier to work with than a queue in java
    private ArrayList<String> mURLQueue = new ArrayList<String>();
    // a flag, not implemented
    private String mCurDomain;
    // validates urls so we know to follow them or not
    UrlValidator mURLValidator;
    // Our user agent
    private String USER_AGENT = "Spideybot/.01 http://www.ucr.edu";

    public Crawler() {
        this.mFolderPath = new File("/Volumes/Virtual Machines/spidey/");
        this.mURLValidator = new UrlValidator(new String[]{"http", "https"});
        try {
            this.mDocument = Jsoup.connect("http://www.ucr.edu").get();
            this.loadUrls();
        } catch (IOException e) {
            System.out.println(TAG + "Could not open our Document.  "+e.toString());
        }
    }

    public Crawler(String fn, String url) {
        this.mFolderPath = new File(fn);
        this.mURLValidator = new UrlValidator(new String[]{"http", "https"});
        try {
            this.mDocument = Jsoup.connect(url).get();
            this.loadUrls();
        } catch (IOException e) {
            System.out.println(TAG + "Could not open our Document.  "+ e.toString());
        }
    }

    public void printPath() {
        System.out.println(this.mFolderPath.toString());
    }

    public ArrayList<String> getQueue() {
        return this.mURLQueue;
    }

    public void run() {
        System.out.println("starting to run");
        String el;
        System.out.println("queue size: " + this.getQueueSize());
        this.printQueue();
        while(!this.mURLQueue.isEmpty()) {
            el = this.mURLQueue.remove(0);
            System.out.println("Attempting to crawl: " + el);
            System.out.println("Queue size is: " + this.getQueueSize());
            try{
                this.mDocument = Jsoup.connect(el).get(); //
                this.loadUrls();
            } catch(IOException e) {
                System.out.println("We couldn't connect to: " + el);
            }
        }
            //this.mDocument = Jsoup.connect(this.mURLQueue.);
        System.out.println("We ran out or urls to parse");
    }

    public int getQueueSize() {
        return this.mURLQueue.size();
    }

    public void printQueue() {
        System.out.println("Printing queue...");
        for(String e : this.mURLQueue) {
            System.out.println(e);
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
                System.out.println("This url: " + url + " is valid");
                if(!this.mURLQueue.contains(url)) {
                    this.mURLQueue.add(url);
                } else {
                    //System.out.println("We already have: " + e.attr("href") + " in our queue");
                }
            } else {
                //System.out.println("This url: " + url + " is not a valid url");
            }
        }
    }
}
