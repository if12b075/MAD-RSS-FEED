package at.technikum_wien.daogenerator;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;


public class RssDaoGenerator {
    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(3, "mad.technikum_wien.at.mad_rss_feed");

        Entity feedItem = schema.addEntity("FeedItem");
        feedItem.addIdProperty().autoincrement();
        feedItem.addStringProperty("title");
        feedItem.addContentProvider();

        Entity feed = schema.addEntity("Feed");
        feed.setTableName("FEEDS");
        feed.addIdProperty();
        feed.addStringProperty("url");
        feed.addStringProperty("title");
        feed.addContentProvider();

        Property feedId = feedItem.addLongProperty("feedId").notNull().getProperty();
        feedItem.addToOne(feed, feedId);

        ToMany feedToFeedItems = feed.addToMany(feedItem, feedId);
        feedToFeedItems.setName("items");


        new DaoGenerator().generateAll(schema, "app/src-gen");
    }
}