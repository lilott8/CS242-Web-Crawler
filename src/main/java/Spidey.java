import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by jason on 1/21/14.
 */
public class Spidey {

    public static void main(String[] args) {
        // Number of crawlers/threads to create
        int totalCrawlers;
        // Arraylist holding our threads
        ArrayList<Thread> threads = new ArrayList<Thread>();
        // Arraylist holding our crawler
        ArrayList<Crawler> crawlers = new ArrayList<Crawler>();
        // temp path, not implemented
        String filePath = "/Volumes/Virtual Machines/spidey/";
        // just a list of sites to start for our seed
        String[] eduSites = {"http://www.ucr.edu", "http://www.mit.edu", "http://www.siu.edu",
                "http://www.niu.edu", "http://www.harvard.edu"};

        if(!args[0].isEmpty()) {
            totalCrawlers = Integer.parseInt(args[0]);
            if(totalCrawlers > 5) {
                totalCrawlers = 5;
            }
        } else {
            totalCrawlers = 2;
        }

        // This primes our seed for each crawler
        for(int i=0; i<totalCrawlers;i++) {
            // This allows us to save our crawler in our arraylist
            Crawler c = new Crawler(filePath, eduSites[i]);
            crawlers.add(c);
            // let it be threadable
            Thread worker = new Thread(c);
            // add our thread to our arraylist
            threads.add(worker);
        }

        // Using this we can start to crawl sites
        for(Thread t : threads) {
            t.start();
        }

        /**
         // easy way to cycle through them all
         for(Crawler c : crawlers) {
         }
         **/

        System.out.println("There are " + crawlers.size() + " crawlers ready to sling!");
    }
}
