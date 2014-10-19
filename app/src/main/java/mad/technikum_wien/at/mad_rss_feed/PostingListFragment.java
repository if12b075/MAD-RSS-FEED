package mad.technikum_wien.at.mad_rss_feed;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 */
public class PostingListFragment extends Fragment {

    private ArrayList<String> postings = new ArrayList<String>();

    private TextView title;


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
//        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
//                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postinglist, container, false);
        title = (TextView) view.findViewById(R.id.postingListTitle);


        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                android.R.id.text1, postings));

        return view;
    }

    public void setPostings(List<FeedItem> posts) {
        postings.clear();
        for (FeedItem fi : posts) {
            postings.add(fi.getTitle());
        }
        mListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                android.R.id.text1, postings));
    }

    public void setTitle(String rssTitle) {
        title.setText(rssTitle);
    }
}
