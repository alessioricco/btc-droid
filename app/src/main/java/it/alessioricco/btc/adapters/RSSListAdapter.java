package it.alessioricco.btc.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.alessioricco.btc.R;
import it.alessioricco.btc.models.feed.Channel;
import it.alessioricco.btc.models.feed.RSS;
import it.alessioricco.btc.utils.Environment;
import it.alessioricco.btc.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;


public class RSSListAdapter extends RecyclerView.Adapter<RSSListAdapter.CustomViewHolder> {
    private List<Channel.FeedItem> feedItemList;
    private Context mContext;

    public RSSListAdapter(Context context, List<Channel.FeedItem> feedItemList) {
        if (feedItemList == null) {
            return;
        }
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_feed_rss_row, null);
        final CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        Channel.FeedItem feedItem = feedItemList.get(i);

        customViewHolder.title.setText(feedItem.getTitle());
        customViewHolder.description.setText(feedItem.getDescription());
        customViewHolder.source.setText(feedItem.getSource());
        customViewHolder.date.setText(StringUtils.formatRSSDate(feedItem.getPubDate()));
        customViewHolder.setLink(feedItem.getLink());
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        //protected ImageView imageView;
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.description)
        TextView description;
        @InjectView(R.id.source)
        TextView source;
        @InjectView(R.id.date)
        TextView date;
        @Getter @Setter String link;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            //this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            //this.textView = (TextView) view.findViewById(R.id.title);
        }
    }
}
