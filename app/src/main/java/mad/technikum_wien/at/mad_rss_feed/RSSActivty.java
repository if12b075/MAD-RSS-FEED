package mad.technikum_wien.at.mad_rss_feed;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class RSSActivty extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssactivty);
        ActionBar b = getActionBar();

        b.setDisplayShowTitleEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rssactivty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.add_feed:
                getFragmentManager().beginTransaction().replace(R.id.main_frame,new RssAddFragment()).commit();
                break;
            case R.id.show_feeds:
                getFragmentManager().beginTransaction().replace(R.id.main_frame,new FeedOverviewListFragment()).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
