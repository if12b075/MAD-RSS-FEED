package mad.technikum_wien.at.mad_rss_feed;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class RSSActivity extends Activity implements RssAddFragment.OnAddRssFragmentListener, FeedOverviewListFragment.OnFeedOverviewFragmentInteraction, PostingListFragment.OnFragmentInteractionListener {
    private ArrayList<String> feeds = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssactivity);

        getFragmentManager().beginTransaction().replace(R.id.main_frame, new FeedOverviewListFragment()).commit();
        ActionBar b = getActionBar();
        if (b != null) {
            b.setDisplayShowTitleEnabled(false);
        }

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
        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.add_feed:
                getFragmentManager().beginTransaction().replace(R.id.main_frame, RssAddFragment.newInstance()).commit();


                break;
            case R.id.show_feeds:
                FeedOverviewListFragment feedOverviewListFragment = new FeedOverviewListFragment();
                Bundle b = new Bundle();
                b.putStringArrayList("feeds", feeds);
                feedOverviewListFragment.setArguments(b);
                getFragmentManager().beginTransaction().replace(R.id.main_frame, feedOverviewListFragment).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAddRss(String feed) {
        feeds.add(feed);

    }

    @Override
    public void onFeedSelection(String feedTitle) {
        PostingListFragment postingsFragment = new PostingListFragment();
        Bundle b = new Bundle();
        b.putString("feedTitle", feedTitle);
        postingsFragment.setArguments(b);

        getFragmentManager().beginTransaction().replace(R.id.main_frame, postingsFragment).addToBackStack("postingList").commit();

    }

    @Override
    public void onPostingSelection(String id) {
        //nothing to do here...
    }
}
