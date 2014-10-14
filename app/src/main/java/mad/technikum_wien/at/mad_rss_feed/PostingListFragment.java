package mad.technikum_wien.at.mad_rss_feed;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.diamonddogs.data.dataobjects.WebRequest;
import at.diamonddogs.service.net.HttpServiceAssister;
import at.diamonddogs.service.processor.ServiceProcessor;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PostingListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSSActivity.class.getSimpleName());
    private Uri RSS = Uri.parse("http://rss.golem.de/rss.php?tp=inet&feed=RSS2.0");
    private HttpServiceAssister assister;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

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
        assister = new HttpServiceAssister(getActivity());
        assister.runWebRequest(new PostingListFragment.RssHandler(), createGetRssRequest(), new RssProcessor());


//        // TODO: Change Adapter to display your content
//        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
//                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postinglist, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        setEmptyText("loading..."); //warum geht das nicht? :'(
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        Bundle bundle = this.getArguments();
        if (!bundle.isEmpty()) {
            String temp = bundle.getString("feedTitle");
            if (!temp.isEmpty()) {
                TextView title = (TextView) view.findViewById(R.id.postingListTitle);
                title.setText(temp);

            }
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        assister.bindService();
    }

    @Override
    public void onPause() {
        super.onPause();
        assister.unbindService();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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
//            mListener.onPostingSelection(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    private WebRequest createGetRssRequest() {
        WebRequest wr = new WebRequest();
        Bundle b = getArguments();
        String url;
        if (b != null) {
            url = b.getString("feedTitle");
            if (!url.startsWith("http")) {
                RSS = Uri.parse("http://" + url);
            } else {
                RSS = Uri.parse(url);
            }
        }
        wr.setUrl(RSS);
        wr.setProcessorId(RssProcessor.ID);

        // This is the important part, telling HttpService how long a WebRequest
        // will be saved. Since RssProcessor extends XMLProcessor, which extends
        // DataProcessor, the WebRequest's data will be cached automatically,
        // provided that cacheTime is not CACHE_NO.
        wr.setCacheTime(50000);

        // Enables offline caching. usually, cache data is deleted on retrieval
        // if it has expired even if the device is not online. If this flag is
        // set to true, cache data will not be removed if it has expired as long
        // as the device was offline during the request
        wr.setUseOfflineCache(true);
        return wr;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onPostingSelection(String id);
    }

    protected final class RssHandler extends Handler {
        /**
         * {@inheritDoc}
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RssProcessor.ID) {
                if (msg.arg1 == ServiceProcessor.RETURN_MESSAGE_OK) {
                    String[] items = (String[]) msg.obj;
                    mListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                            android.R.id.text1, items));
                    Toast.makeText(getActivity(),
                            "From cache -> " + msg.getData().getSerializable(ServiceProcessor.BUNDLE_EXTRA_MESSAGE_FROMCACHE),
                            Toast.LENGTH_SHORT).show();
                } else {
                    LOGGER.error("An error has occured.", msg.getData().getSerializable(ServiceProcessor.BUNDLE_EXTRA_MESSAGE_THROWABLE));
                }
            }
        }
    }

}
