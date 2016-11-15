package it.alessioricco.btc.models.feed;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static it.alessioricco.btc.utils.StringUtils.RFC_1123_DATE_TIME;

//https://gist.github.com/macsystems/01d7e80554efd344b1f9

@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2005/Atom", prefix = "atom")
})
@Root(strict = false)
public class Channel {
    // Tricky part in Simple XML because the link is named twice
    @ElementList(entry = "link", inline = true, required = false)
    public List<Link> links;

    @ElementList(name = "item", required = true, inline = true)
    public List<FeedItem> feedItemList;


    @Element
    @Getter @Setter String title;
    @Element
    String language;

    @Element(name = "ttl", required = false)
    int ttl;

    @Element(name = "pubDate", required = false)
    String pubDate;

    @Override
    public String toString() {
        return "Channel{" +
                "links=" + links +
                ", feedItemList=" + feedItemList +
                ", title='" + title + '\'' +
                ", language='" + language + '\'' +
                ", ttl=" + ttl +
                ", pubDate='" + pubDate + '\'' +
                '}';
    }

    public static class Link {
        @Attribute(required = false)
        public String href;

        @Attribute(required = false)
        public String rel;

        @Attribute(name = "type", required = false)
        public String contentType;

        @Text(required = false)
        public String link;
    }

    @Root(name = "item", strict = false)
    public static class FeedItem {

        @Element(name = "title", required = true)
        @Getter @Setter
        String title;//The title of the item.	Venice Film Festival Tries to Quit Sinking

        @Element(name = "link", required = true)
        @Getter @Setter
        String link;//The URL of the item.	http://www.nytimes.com/2002/09/07/movies/07FEST.html

        @Element(name = "description", required = true)
        @Getter @Setter
        String description;//The item synopsis.	Some of the most heated chatter at the Venice Film Festival this week was about the way that the arrival of the stars at the Palazzo del Cinema was being staged.

        @Element(name = "author", required = false)
        String author;//Email address of the author of the item. More.	oprah@oxygen.net
        //@Element(name = "category", required = false)

        @Path("category")
        @Text(required=false)
        String category;//Includes the item in one or more categories. More.	Simpsons Characters

        //@Element(name = "comments", required = false)
        @Path("comments")
        @Text(required=false)
        String comments;//URL of a page for comments relating to the item. More.	http://www.myblog.org/cgi-local/mt/mt-comments.cgi?entry_id=290

        @Element(name = "enclosure", required = false)
        @Getter @Setter
        String enclosure;//	Describes a media object that is attached to the item. More.	<enclosure url="http://live.curry.com/mp3/celebritySCms.mp3" length="1069871" type="audio/mpeg"/>

        @Element(name = "guid", required = false)
        String guid;//A string that uniquely identifies the item. More.	<guid isPermaLink="true">http://inessential.com/2002/09/01.php#a2</guid>

        @Element(name = "pubDate", required = false)
        @Getter @Setter
        String pubDate;//	Indicates when the item was published. More.	Sun, 19 May 2002 15:21:36 GMT

        @Element(name = "source", required = false)
        @Getter @Setter
        String source;//	The RSS channel that the item came from. More.

        public Date getDate() {
            DateFormat formatterInput = new SimpleDateFormat(RFC_1123_DATE_TIME);
            try {
                return formatterInput.parse(pubDate);
            } catch (ParseException e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return "FeedItem{" +
                    "title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", description='" + description + '\'' +
                    ", author='" + author + '\'' +
                    ", category='" + category + '\'' +
                    ", comments='" + comments + '\'' +
                    ", enclosure='" + enclosure + '\'' +
                    ", guid='" + guid + '\'' +
                    ", pubDate='" + pubDate + '\'' +
                    ", source='" + source + '\'' +
                    '}';
        }
    }
}