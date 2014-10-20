package at.technikum_wien.daogenerator;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;


public class RssDaoGenerator {
    public static void main(String args[]) throws Exception {
        //wenn die struktur der db geändert wird muss die versionsnummer erhöht werden (1. parameter)
        Schema schema = new Schema(4, "mad.technikum_wien.at.mad_rss_feed");

        Entity feedItem = schema.addEntity("FeedItem");
        feedItem.setSuperclass("android.database.ContentObservable");
        feedItem.addIdProperty().autoincrement();
        feedItem.addStringProperty("title");
        feedItem.addBooleanProperty("read");
        feedItem.addBooleanProperty("starred");
        feedItem.addContentProvider().setClassName("FeedItemContentProvider");

        Entity feed = schema.addEntity("Feed");
        feed.setSuperclass("android.database.ContentObservable");
        feed.setTableName("FEEDS");
        feed.addIdProperty();
        feed.addStringProperty("url");
        feed.addStringProperty("title");
        feed.addDateProperty("lastUpdate");
        feed.addBooleanProperty("read");
        feed.addBooleanProperty("starred");
        feed.addContentProvider().setClassName("FeedContentProvider");

        Property feedId = feedItem.addLongProperty("feedId").notNull().getProperty();
        feedItem.addToOne(feed, feedId);

        ToMany feedToFeedItems = feed.addToMany(feedItem, feedId);
        feedToFeedItems.setName("items");


        new DaoGenerator().generateAll(schema, "app/src-gen");
    }
}