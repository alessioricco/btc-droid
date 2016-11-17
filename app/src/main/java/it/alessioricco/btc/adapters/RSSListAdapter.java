package it.alessioricco.btc.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import it.alessioricco.btc.utils.StringUtils;


public class RSSListAdapter extends RecyclerView.Adapter<RSSListAdapter.CustomViewHolder> {
    private List<Channel.FeedItem> feedItemList;
    Context context;

    public RSSListAdapter(Context context, List<Channel.FeedItem> feedItemList) {
        if (feedItemList == null) {
            return;
        }
        this.feedItemList = feedItemList;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_feed_rss_row, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        Channel.FeedItem feedItem = feedItemList.get(i);

        customViewHolder.title.setText(feedItem.getTitle());
        customViewHolder.title.setTag(feedItem.getLink());
        customViewHolder.description.setText(feedItem.getDescription());
        customViewHolder.source.setText(feedItem.getSource());
        customViewHolder.date.setText(StringUtils.formatRSSDate(feedItem.getPubDate()));
        customViewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse((String)v.getTag()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.description)
        TextView description;
        @InjectView(R.id.source)
        TextView source;
        @InjectView(R.id.date)
        TextView date;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
