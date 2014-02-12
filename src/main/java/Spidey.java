
import java.util.ArrayList;

/**
 * Created by jason on 1/21/14.
 */
public class Spidey {

    public String TAG = "spidey";
    private static int mThreadCount;

    public static void main(String[] args) {
        // Number of crawlers/threads to create
        int totalCrawlers = 2;
        // Arraylist holding our threads
        ArrayList<Thread> threads = new ArrayList<Thread>();
        // Arraylist holding our crawler
        ArrayList<Crawler> crawlers = new ArrayList<Crawler>();
        // just a list of sites to start for our seed
        String[][] eduSites = {
                {"http://cnn.com", "http://www.niu.edu", "http://www.siu.edu","http://www.addictinggames.com"},
                {"http://www.mit.edu", "http://www.harvard.edu", "http://www.ucr.edu", "http://www.yahoo.com"},
                {"http://bbc.co.uk", "http://www.stackoverflow.com", "http://www.serverfault.com", "http://www.utah.gov"},
                {"http://www.digg.com", "http://www.bps101.net", "http://www.reddit.com", "http://www.nytimes.com"},
                {"http://www.drudgereport.com","http://www.berkeley.edu","http://www.whitehouse.gov", "http://www.foxnews.com"}
        };
        // boolean check for if all our threads are out of work
        int isDone = 0;
        String TAG = "spidey";

        switch(args.length) {
            case 0:
                totalCrawlers = 2;
                Log.setOutputLevel(3);
                break;
            // crawlers
            case 1:
                totalCrawlers = Spidey.validateCrawlers(Integer.parseInt(args[0]));
                Log.setOutputLevel(3);
                break;
            // log level
            case 2:
                totalCrawlers = Spidey.validateCrawlers(Integer.parseInt(args[0]));
                Log.setOutputLevel(Spidey.validateLogOutput(Integer.parseInt(args[1])));
                break;
            // mysql database
            case 3:
                totalCrawlers = Spidey.validateCrawlers(Integer.parseInt(args[0]));
                Log.setOutputLevel(Spidey.validateLogOutput(Integer.parseInt(args[1])));
                Connector.setDatabaseAddress(args[2]);
                Connector.setDatabaseUser("root");
                Connector.setDatabasePassword("root");
                break;
            // mysql user
            case 4:
                totalCrawlers = Spidey.validateCrawlers(Integer.parseInt(args[0]));
                Log.setOutputLevel(Spidey.validateLogOutput(Integer.parseInt(args[1])));
                Connector.setDatabaseAddress(args[2]);
                Connector.setDatabaseUser(args[3]);
                Connector.setDatabasePassword("root");
                break;
            // database location
            case 5:
                totalCrawlers = Spidey.validateCrawlers(Integer.parseInt(args[0]));
                Log.setOutputLevel(Spidey.validateLogOutput(Integer.parseInt(args[1])));
                Connector.setDatabaseAddress(args[2]);
                Connector.setDatabaseUser(args[3]);
                Connector.setDatabasePassword(args[4]);
                break;
        }
        //Connector connector = Connector.getInstance();
        mThreadCount = totalCrawlers;
        isDone = totalCrawlers;

        LinkQueue.setNumThreads(mThreadCount);

        // This primes our seed for each crawler
        for(int i=0; i<totalCrawlers;i++) {
            // This allows us to save our crawler in our arraylist
            Crawler c = new Crawler(eduSites[i]);
            c.setTid(i);
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

        Log.d(TAG, "There are " + crawlers.size() + " crawlers ready to sling!", 7);

        int pos = 0;
        while(isDone > 0) {
            pos = 0;
            for(Crawler c : crawlers) {
                // we are done, but there might still be work to do
                if(c.getIsDone()) {
                    isDone--;
                    // check to see if there are things to crawl
                    if(c.getQueueSize() > 0) {
                        // restart the thread if the queue has urls
                        threads.add(pos, new Thread(c));
                        threads.get(pos).start();
                        isDone++;
                        Log.d(TAG, "============RESTARTING THREAD: " + pos + "============", 7);
                    }
                }
                pos++;
            }
        }
        /**
         // easy way to cycle through them all
         for(Crawler c : crawlers) {
         }
         **/
    Log.d(TAG, "All crawlers are done.", 1);
    }

    public static int getThreadCount() {return mThreadCount;}

    public static int validateCrawlers(int i) {
        if(i < 1 || i > 5) { return 2; }
        else { return i; }
    }

    public static int validateLogOutput(int i) {
        if(i < 1 || i > 7) { return 3; }
        else { return i; }
    }
}
