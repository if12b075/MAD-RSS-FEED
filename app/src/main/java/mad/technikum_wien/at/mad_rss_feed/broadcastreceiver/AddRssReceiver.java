package mad.technikum_wien.at.mad_rss_feed.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

public class AddRssReceiver extends BroadcastReceiver {
    public static final String ACTION_ADD_RSS_FEED = "mad.technikum_wien.at.mad_rss_feed.broadcastreceiver.AddRssReceiver.ACTION_ADDRSS";

    public AddRssReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        CharSequence intentData = intent.getCharSequenceExtra("rssfeed");
        Toast.makeText(context, "RSS-Feed: " + intentData + " added", Toast.LENGTH_LONG).show();
        // Vibrate the mobile phone
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);

    }
}
