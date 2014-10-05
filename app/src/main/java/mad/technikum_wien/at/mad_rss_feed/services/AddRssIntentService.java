package mad.technikum_wien.at.mad_rss_feed.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class AddRssIntentService extends IntentService {

    private static final String ACTION_ADDRSSFEED = "mad.technikum_wien.at.mad_rss_feed.services.action.addrssfeed";

    private static final String EXTRA_RSSFEEDTOADD= "mad.technikum_wien.at.mad_rss_feed.services.extra.RSSFEEDTOADD";


    public AddRssIntentService() {
        super("AddRssIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

                final String param1 = intent.getStringExtra(EXTRA_RSSFEEDTOADD);
        }
    }

}
