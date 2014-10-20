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

        Bundle bundle = new Bundle();
        feedsFragment.setArguments(bundle);
        postingsFragment.setArguments(bundle);


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
//        daoSession.clear();
        feedDao = daoSession.getFeedDao();
        feedItemDao = daoSession.getFeedItemDao();
//        feedCP = new FeedContentProvider();
//        feedItemCP = new FeedItemContentProvider();


        feedList.addAll(feedDao.loadAll());

        if (feedList.isEmpty()) {
            System.out.println("RSSActivity.onCreate() feedList: " + feedList);
            feedDao.insertOrReplace(new Feed(null, "rss.orf.at/science.xml", "science.ORF.at", new Date(System.currentTimeMillis()), false, false));
            feedDao.insertOrReplace(new Feed(null, "http://derStandard.at/?page=rss&ressort=Seite1", "derStandard.at", new Date(System.currentTimeMillis()), false, false));
            feedList.addAll(feedDao.loadAll());
        }
        System.out.println("feedList: " + feedList);
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
        updateData();
        for (Feed feed : feedList) {
            if (feed.getTitle().equals(feedTitle)) {
                lastFeed = feed;
                Bundle b = postingsFragment.getArguments();
                b.putString("title", lastFeed.getTitle());

                ArrayList<FeedItem> itemsList = new ArrayList<FeedItem>();
                itemsList.addAll(feedItemDao._queryFeed_Items(lastFeed.getId()));
                System.out.println("itemsList: " + itemsList);
                if (itemsList.isEmpty()) {
                    postingsFragment.setPostings(new ArrayList<FeedItem>());
                    assister.runWebRequest(new RssHandler(), createGetRssRequest(lastFeed.getUrl(),
                            RssProcessor.ID), new RssProcessor());
                } else {
                    Toast.makeText(RSSActivity.this, "from db", Toast.LENGTH_SHORT).show();
                    postingsFragment.setPostings(itemsList);
                }

                break;
            }
        }
        getFragmentManager().beginTransaction().replace(R.id.main_frame, postingsFragment).
                addToBackStack(null).commit();
    }

    /**
     * Wird aufgerufen wenn Feeds gelöscht werden
     *
     * @param titles ArrayList mit Titel der zu löschenden Feeds
     */
    @Override
    public void onFeedDeleted(ArrayList<String> titles) {
        for (String title : titles) {
            for (Feed f : feedList) {
                if (f.getTitle().equals(title)) {
                    feedItemDao.deleteInTx(feedItemDao._queryFeed_Items(f.getId()));
                    feedDao.delete(f);
                    break;
                }
            }
        }
        updateData();
    }

    @Override
    public void onAddRss(String url) {
//        System.out.println(feedUrls);
        if (url.isEmpty()) {
            Toast.makeText(RSSActivity.this, "please insert RSS-Url", Toast.LENGTH_LONG).show();
            return;
        }
        if (!feedUrls.contains(url)) {
            lastFeed = new Feed();
            lastFeed.setUrl(url);
            feedDao.insertOrReplace(lastFeed);

            updateData();

            //webRequest zur abfrage des Titels
            assister.runWebRequest(new RssTitleHandler(), createGetRssRequest(url, RssTitleProcessor.ID),
                    new RssTitleProcessor());

            assister.runWebRequest(new RssDateHandler(), createGetRssRequest(lastFeed.getUrl(),
                    RssDateProcessor.ID), new RssDateProcessor());

            assister.runWebRequest(new RssHandler(), createGetRssRequest(lastFeed.getUrl(),
                    RssProcessor.ID), new RssProcessor());

        } else {
            Toast.makeText(RSSActivity.this, "Feed already exists", Toast.LENGTH_LONG).show();
        }
//        System.out.println("onAddRss().feedUrls: "+feedUrls);
    }

    /**
     * Update all feed data lists
     */
    private void updateData() {
        feedList.clear();
        feedList.addAll(feedDao.loadAll());
        feedUrls.clear();
        for (Feed f : feedList) {
            feedUrls.add(f.getUrl());
        }
        feedsFragment.setFeeds(feedList);
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
                    updateData();
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
                        FeedItem item = new FeedItem();
                        item.setTitle(title);
                        item.setFeedId(lastFeed.getId());
                        feedItemDao.insertOrReplace(item);
                    }
                    postingsFragment.setPostings(feedItemDao._queryFeed_Items(lastFeed.getId()));
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
                    try {
                        Date date = sdf.parse(d);
                        date.setTime(date.getTime());
                        System.out.println(date);
                        lastFeed.setLastUpdate(date);
                        feedDao.insertOrReplace(lastFeed);
                        updateData();
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
