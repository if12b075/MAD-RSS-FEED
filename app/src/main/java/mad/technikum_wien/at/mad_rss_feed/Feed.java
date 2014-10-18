package mad.technikum_wien.at.mad_rss_feed;

import java.util.ArrayList;

public class Feed {

    private int id;
    private String url;
    private String title;
    private ArrayList<String> posts;

    public Feed(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public Feed() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<String> posts) {
        this.posts = posts;
    }
}
