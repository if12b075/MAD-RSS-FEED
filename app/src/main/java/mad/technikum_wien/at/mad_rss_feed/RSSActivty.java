package mad.technikum_wien.at.mad_rss_feed;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class RSSActivty extends Activity implements RssAddFragment.OnAddRssFragmentListener, FeedOverviewListFragment.OnFeedOverviewFragmentInteraction {
    private ArrayList<String> feeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssactivty);
        ActionBar b = getActionBar();

        if (b != null) {
            b.setDisplayShowTitleEnabled(false);
        }

        feeds = new ArrayList<String>();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rssactivty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.add_feed:
                getFragmentManager().beginTransaction().replace(R.id.main_frame,new RssAddFragment()).commit();


                break;
            case R.id.show_feeds:
                FeedOverviewListFragment feedOverviewListFragment = new FeedOverviewListFragment();
                Bundle b = new Bundle();
                b.putStringArrayList("feeds",feeds);
                feedOverviewListFragment.setArguments(b);
                getFragmentManager().beginTransaction().replace(R.id.main_frame,feedOverviewListFragment).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAddRss(String feed) {
        feeds.add(feed);
    }

    @Override
    public void onFeedSelection(String id) {
        PostingListFragment postingsFragment = new PostingListFragment();
        Bundle b = new Bundle();
        b.putString("feedId", id);
        postingsFragment.setArguments(b);

        getFragmentManager().beginTransaction().replace(R.id.main_frame, postingsFragment).addToBackStack("postings").commit();

    }
}
