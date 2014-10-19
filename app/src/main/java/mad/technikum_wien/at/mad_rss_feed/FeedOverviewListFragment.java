package mad.technikum_wien.at.mad_rss_feed;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
public class FeedOverviewListFragment extends Fragment implements ListView.OnItemClickListener, AbsListView.MultiChoiceModeListener {

    private OnFeedOverviewFragmentInteraction mListener;

    private ListView mListView;

    private ArrayAdapter mAdapter;

    private ArrayList<String> feedsTitleList = new ArrayList<String>();

    public FeedOverviewListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                android.R.id.text1, feedsTitleList);
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

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(this);


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
        mAdapter.notifyDataSetChanged();

    }

    /**
     * Methoden des {@link android.widget.AbsListView.MultiChoiceModeListener}
     */
    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        //setzt Titel der Contextual Action Bar
        mode.setTitle(mListView.getCheckedItemCount() + " selected");
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        //set list layout für multiple selection
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice,
                android.R.id.text1, feedsTitleList);
        mListView.setAdapter(mAdapter);

        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.contextual_feedoverviewlist, menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                Toast.makeText(getActivity(), "deleted", Toast.LENGTH_SHORT).show();
                //markierte feeds werden gelöscht
                SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
                for (int i = 0; i < checkedItems.size(); i++) {
                    if (checkedItems.valueAt(i)) {
                        String title = (String) mListView.getItemAtPosition(checkedItems.keyAt(i));
                        feedsTitleList.remove(title);
                        //callback in activity
                        mListener.onFeedDeleted(title);
                    }
                }
                mAdapter.notifyDataSetChanged();
                mode.finish();
        }
        return false;

    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //set list layout für normale ansicht
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                android.R.id.text1, feedsTitleList);
        mListView.setAdapter(mAdapter);

    }

    public interface OnFeedOverviewFragmentInteraction {
        public void onFeedSelection(String id);

        public void onFeedDeleted(String title);
    }

}
