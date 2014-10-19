package mad.technikum_wien.at.mad_rss_feed;

import android.app.ActionBar;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import at.diamonddogs.data.dataobjects.WebRequest;
import at.diamonddogs.service.net.HttpServiceAssister;
import at.diamonddogs.service.processor.ServiceProcessor;
import mad.technikum_wien.at.mad_rss_feed.processors.RssDateProcessor;
import mad.technikum_wien.at.mad_rss_feed.processors.RssProcessor;
import mad.technikum_wien.at.mad_rss_feed.processors.RssTitleProcessor;

public class RSSActivity extends Activity implements RssAddFragment.OnAddRssFragmentListener,
        FeedOverviewListFragment.OnFeedOverviewFragmentInteraction {

    public DaoSession daoSession;
    //    FeedContentProvider feedCP;
//    FeedItemContentProvider feedItemCP;
    FeedDao feedDao;
    FeedItemDao feedItemDao;
    private ArrayList<String> feedUrls = new ArrayList<String>();
    private ArrayList<Feed> feedList = new ArrayList<Feed>();
    private Feed lastFeed;
    private FeedOverviewListFragment feedsFragment = new FeedOverviewListFragment();
    private PostingListFragment postingsFragment = new PostingListFragment();
    private HttpServiceAssister assister;
    private Uri RSS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssactivity);

        getFragmentManager().beginTransaction().replace(R.id.main_frame, feedsFragment).commit();
        ActionBar b = getActionBar();
        if (b != null) {
            b.setDisplayShowTitleEnabled(false);
        }

        assister = new HttpServiceAssister(this);


        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mad-rss-feed-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        feedDao = daoSession.getFeedDao();
        feedItemDao = daoSession.getFeedItemDao();
//        feedCP = new FeedContentProvider();
//        feedItemCP = new FeedItemContentProvider();

//        feedItemDao.deleteAll();
//        feedDao.deleteAll();

        feedList.addAll(feedDao.loadAll());

        if (feedList.isEmpty()) {
            feedDao.insertOrReplace(new Feed(null, "rss.orf.at/science.xml", "science.ORF.at"));
            feedDao.insertOrReplace(new Feed(null, "http://derStandard.at/?page=rss&ressort=Seite1", "derStandard.at"));
            feedList.addAll(feedDao.loadAll());
        }

    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rssactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.add_feed:
                getFragmentManager().beginTransaction().replace(R.id.main_frame,
                        RssAddFragment.newInstance()).addToBackStack(null).commit();
                break;
            case R.id.show_feeds:
                if (feedsFragment.isVisible()) {
                    break;
                }
                getFragmentManager().beginTransaction().replace(R.id.main_frame,
                        feedsFragment).addToBackStack(null).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        assister.bindService();
        feedsFragment.setFeeds(feedList);

    }

    @Override
    protected void onPause() {
        super.onPause();
        assister.unbindService();
    }

    @Override
    public void onFeedSelection(String feedTitle) {
        for (Feed feed : feedList) {
            if (feed.getTitle().equals(feedTitle)) {
                lastFeed = feed;
                assister.runWebRequest(new RssHandler(), createGetRssRequest(lastFeed.getUrl(),
                        RssProcessor.ID), new RssProcessor());
                break;
            }
        }
        getFragmentManager().beginTransaction().replace(R.id.main_frame, postingsFragment).
                addToBackStack(null).commit();
    }

    /**
     * Wird aufgerufen wenn Feeds gelöscht werden
     *
     * @param title Titel des zu löschenden Feeds
     */
    @Override
    public void onFeedDeleted(String title) {
        Feed feed;
        for (int i = 0; i < feedList.size(); i++) {
            feed = feedList.get(i);
            if (feed.getTitle().equals(title)) {
                feedUrls.remove(feed.getUrl());
                feedList.remove(feed);
                feedDao.delete(feed);
            }
        }

    }

    @Override
    public void onAddRss(String url) {
//        System.out.println(feedUrls);
        if (url.isEmpty()) {
            Toast.makeText(RSSActivity.this, "please insert RSS-Url", Toast.LENGTH_LONG).show();
            return;
        }
        if (!feedUrls.contains(url)) {
            feedUrls.add(url);
            lastFeed = new Feed();
            lastFeed.setUrl(url);
            feedDao.insertOrReplace(lastFeed);
            assister.runWebRequest(new RssTitleHandler(), createGetRssRequest(url, RssTitleProcessor.ID),
                    new RssTitleProcessor());
        } else {
            Toast.makeText(RSSActivity.this, "Feed already exists", Toast.LENGTH_LONG).show();
        }
//        System.out.println("onAddRss().feedUrls: "+feedUrls);
    }

    /**
     * @param url         url für webRequest als string
     * @param processorId ID des zu registrierenden Processors
     */
    private WebRequest createGetRssRequest(String url, int processorId) {
        WebRequest wr = new WebRequest();
        if (url != null && !url.isEmpty()) {
            if (!url.startsWith("http")) {
                RSS = Uri.parse("http://" + url);
            } else {
                RSS = Uri.parse(url);
            }
        }
        wr.setUrl(RSS);
        wr.setProcessorId(processorId);

        /**
         * Cache nur für Posts
         */
        if (processorId == RssProcessor.ID) {
            // This is the important part, telling HttpService how long a WebRequest
            // will be saved. Since RssProcessor extends XMLProcessor, which extends
            // DataProcessor, the WebRequest's data will be cached automatically,
            // provided that cacheTime is not CACHE_NO.
            wr.setCacheTime(50000);

            // Enables offline caching. usually, cache data is deleted on retrieval
            // if it has expired even if the device is not online. If this flag is
            // set to true, cache data will not be removed if it has expired as long
            // as the device was offline during the request
            wr.setUseOfflineCache(true);
        } else {
            wr.setCacheTime(0);
            wr.setUseOfflineCache(false);
        }
        return wr;
    }

    /**
     * Handler für Abfrage des Titels
     */
    protected final class RssTitleHandler extends Handler {
        /**
         * {@inheritDoc}
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String title;
            if (msg.what == RssTitleProcessor.ID) {
                if (msg.arg1 == ServiceProcessor.RETURN_MESSAGE_OK) {
                    title = (String) msg.obj;
                    lastFeed.setTitle(title);
                    feedList.add(lastFeed);
                    feedDao.insertOrReplace(lastFeed);
                    feedsFragment.setFeeds(feedList);
                    Toast.makeText(RSSActivity.this, "feed added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RSSActivity.this, "not a valid RSS", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    /**
     * Handler fur Abfrage der Posts
     */
    protected final class RssHandler extends Handler {
        /**
         * {@inheritDoc}
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RssProcessor.ID) {
                if (msg.arg1 == ServiceProcessor.RETURN_MESSAGE_OK) {
//                    System.err.println("msg.obj.class: " + msg.obj.getClass().getSimpleName());
                    System.out.println("lastFeed.getId: " + lastFeed.getId());
                    for (String title : (ArrayList<String>) msg.obj) {
                        feedItemDao.insertOrReplace(new FeedItem(null, title, lastFeed.getId()));
                    }
//                    lastFeed.setPosts((ArrayList<String>) msg.obj);
                    postingsFragment.setPostings(feedItemDao._queryFeed_Items(lastFeed.getId()));
                    postingsFragment.setTitle(lastFeed.getTitle());
                    Toast.makeText(RSSActivity.this,
                            "From cache -> " + msg.getData().getSerializable(ServiceProcessor.BUNDLE_EXTRA_MESSAGE_FROMCACHE),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RSSActivity.this, "error during download", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Handler für Abfrage von feedDate
     */
    protected final class RssDateHandler extends Handler {
        /**
         * {@inheritDoc}
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RssDateProcessor.ID) {
                if (msg.arg1 == ServiceProcessor.RETURN_MESSAGE_OK) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    StringTokenizer st = new StringTokenizer((String) msg.obj, "+");
                    String d = st.nextToken();
                    String tz = st.nextToken();
                    try {
                        Date date = sdf.parse(d);
                        sdf = new SimpleDateFormat("HH:mm");
                        Date tZone = sdf.parse(tz);
                        date.setTime(date.getTime() + tZone.getTime());
                        System.out.println(date);
                        //                    lastFeed.setTitle(title);
                        //                    feedList.add(lastFeed);
                        //                    feedDao.attachEntity(lastFeed);
                        //                    feedsFragment.setFeeds(feedList);
                        Toast.makeText(RSSActivity.this, "date parsed", Toast.LENGTH_SHORT).show();
                    } catch (ParseException e) {
                        Toast.makeText(RSSActivity.this, "problem with date", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RSSActivity.this, "not a valid RSS", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
