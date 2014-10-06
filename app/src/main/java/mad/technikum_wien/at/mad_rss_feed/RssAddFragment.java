package mad.technikum_wien.at.mad_rss_feed;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mad.technikum_wien.at.mad_rss_feed.broadcastreceiver.AddRssReceiver;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link mad.technikum_wien.at.mad_rss_feed.RssAddFragment.OnAddRssFragmentListener} interface
 * to handle interaction events.
 * Use the {@link RssAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class RssAddFragment extends Fragment {
    private static String pattern = "^[a-zA-Z0-9\\-\\.]+\\.(com|org|net|mil|edu|COM|ORG|NET|MIL|EDU)$";

    private OnAddRssFragmentListener listener;

    public static RssAddFragment newInstance() {
        RssAddFragment fragment = new RssAddFragment();
        return fragment;
    }
    public RssAddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnAddRssFragmentListener) {
            listener = (OnAddRssFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet MyListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rss_add, container, false);

        final EditText et = (EditText)v.findViewById(R.id.rssLinkTextField);
        final Button addRssButton = (Button) v.findViewById(R.id.rssAddButton);
        addRssButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(IsMatch(et.getText().toString())) {
                    listener.onAddRss (et.getText().toString());
                    Toast.makeText(v.getContext(),"RSS Feed added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(v.getContext(),"Please insert a valid Link", Toast.LENGTH_LONG).show();
                }

            }
        });
        return v;
    }

    public interface OnAddRssFragmentListener {
        public void onAddRss(String feed);
    }

    private static boolean IsMatch(String s) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

}
