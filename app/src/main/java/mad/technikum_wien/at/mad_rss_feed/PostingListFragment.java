package mad.technikum_wien.at.mad_rss_feed;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 */
public class PostingListFragment extends Fragment implements AbsListView.MultiChoiceModeListener {

    private ArrayList<String> postings = new ArrayList<String>();

    private TextView title;

    private ArrayAdapter mAdapter;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostingListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         *c&p von CachingExampleActivity
         */
//        assister = new HttpServiceAssister(getActivity());
//        assister.runWebRequest(new PostingListFragment.RssHandler(), createGetRssRequest(), new RssProcessor());


//        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.postinglist_item, R.id.item_title, postings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postinglist, container, false);
        title = (TextView) view.findViewById(R.id.postingListTitle);
        title.setText(getArguments().getString("title"));

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        postings = getArguments().getStringArrayList("postings");
        mAdapter.notifyDataSetChanged();

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(this);

        return view;
    }

    public void setPostings(List<FeedItem> posts) {
        postings.clear();
        for (FeedItem fi : posts) {
            postings.add(fi.getTitle());
        }
//        System.out.println("postings: "+postings);
//        System.out.println("mListView: "+mListView);
        getArguments().putStringArrayList("postings", postings);
        if (mListView != null) {
            mAdapter.notifyDataSetChanged();
        }

    }

    public void setTitle(String rssTitle) {
//            System.out.println("PostingListFragment: title: "+title);
        getArguments().putString("title", rssTitle);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int i, long l, boolean b) {
        //setzt Titel der Contextual Action Bar
        mode.setTitle(mListView.getCheckedItemCount() + " selected");
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.contextual_postinglist, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
        switch (item.getItemId()) {
            case R.id.menu_unread:  //mark_read ist dann eh das selbe umgekehrt...
                for (int i = 0; i < checkedItems.size(); i++) {
                    if (checkedItems.valueAt(i)) {
                        //so in etwa sollt gehn text fett zu machen (das wär dann im cursorAdapter in bindView(glaub ich) zu machen)
                        String tempString = mListView.getItemAtPosition(checkedItems.keyAt(i)).toString();
                        SpannableString spanString = new SpannableString(tempString);
                        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                        TextView text = (TextView) getView().findViewById(R.id.item_title);
                        text.setText(spanString);

                        //hier müsste man eig nur bei den items unread auf true (bzw. read auf false) setzen
                    }
                }
                //hier wahrscheinlich noch irgendwie dem cursorAdapter mitteilen das sich die daten geändert haben
                mode.finish();
                break;
            case R.id.menu_star:
                for (int i = 0; i < checkedItems.size(); i++) {
                    if (checkedItems.valueAt(i)) {
                        //wie oben, das muss, denk ich, im CursorAdapter passieren
                        ImageView image = (ImageView) getView().findViewById(R.id.imageView1);
                        //star_true is ein gelbes sternchen, star_false ein graues
                        image.setImageResource(R.drawable.star_true);

                        //hier wieder nur in den items "starred" von true auf false oder umgekehrt setzen..
                    }
                }
                //und wieder änderungen bemerkbar machen
                mode.finish();
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}
