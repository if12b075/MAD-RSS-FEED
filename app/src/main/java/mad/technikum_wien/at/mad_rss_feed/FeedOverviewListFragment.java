package mad.technikum_wien.at.mad_rss_feed;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnFeedOverviewFragmentInteraction}
 * interface.
 */
public class FeedOverviewListFragment extends Fragment implements ListView.OnItemClickListener {

    private OnFeedOverviewFragmentInteraction mListener;

    private ListView mListView;

    private ListAdapter mAdapter;

    private ArrayList<String> feedsTitleList = new ArrayList<String>();

    public FeedOverviewListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, feedsTitleList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedoverviewlist, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFeedOverviewFragmentInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
//            System.out.println(feedsTitleList.get(position));
            mListener.onFeedSelection(feedsTitleList.get(position));
        }
    }

    public void setFeeds(ArrayList<Feed> feedList) {
        feedsTitleList.clear();
        for (Feed feed : feedList) {
            feedsTitleList.add(feed.getTitle());
        }
        mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, feedsTitleList);
        mListView.setAdapter(mAdapter);

    }

    public interface OnFeedOverviewFragmentInteraction {
        public void onFeedSelection(String id);
    }

}
