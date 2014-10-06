package mad.technikum_wien.at.mad_rss_feed;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import java.util.ArrayList;
import android.view.MenuItem;


public class RSSActivty extends Activity implements RssAddFragment.OnAddRssFragmentListener {
    private ArrayList<String> feeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssactivty);
        ActionBar b = getActionBar();

        b.setDisplayShowTitleEnabled(false);

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
}
