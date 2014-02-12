import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jason on 2/3/14.
 */
public class Parser {

    // Our connection to websites
    protected Document mDocument;
    // Domain portion of the url
    String mDomain;
    // Troubleshooting tag for logging
    String TAG = "parser";
    // URL validator
    UrlValidator mURLValidator;
    long pageLoadTime;

    public Parser() {
        this.mURLValidator = new UrlValidator(new String[]{"http", "https"});
    }

    public void parseDocument(String url) {
        try{
            long begin = System.currentTimeMillis()/1000;
            this.mDocument = Jsoup.connect(url).get(); // get the next page
            this.pageLoadTime = System.currentTimeMillis()/1000 - begin;

        } catch(IOException e) {
            Log.d(TAG,String.format("Error connecting to %s: %s", url, e.toString()), 5);
        }
    }

    public ArrayList<String> getUrls() {
        ArrayList<String> links = new ArrayList<String>();
        // Get all the links on a page
        // Start building our seed
        for(Element e : this.mDocument.getElementsByTag("a")) {
            // make sure the url is valid, if it is push it onto our "queue"
            String url = e.attr("href");
            // make sure this is a valid URL
            if(this.mURLValidator.isValid(url)) {
                Log.d(TAG, String.format("This url: %s is valid", url), 7);
                // Scrape all the urls and worry about checking
                // if we have them in the LinkQueue class
                links.add(url);
            } else {
                Log.d(TAG, String.format("This url: %s is not a valid url", url), 7);
            }
        }
        Log.d(TAG, String.format("Size of links: %s", links.size()), 7);
        return links;
    }

    public String getBody() {
        return this.mDocument.body().toString();
    }

    public String getTitle() {
        return this.mDocument.title();
    }

    public String getWholePage() {
        return this.mDocument.html();
    }

    public long getPageLoadTime(){
        return this.pageLoadTime;
    }

    public String getHead() {
        return this.mDocument.head().html();
    }

}
