package it.alessioricco.btc.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.alessioricco.btc.R;
import it.alessioricco.btc.models.feed.Channel;
import it.alessioricco.btc.models.feed.RSS;


public class RSSListAdapter extends RecyclerView.Adapter<RSSListAdapter.CustomViewHolder> {
    private List<Channel.FeedItem> feedItemList;
    private Context mContext;

    public RSSListAdapter(Context context, RSS feed) {
        if (feed == null || feed.getChannel() == null) {
            return;
        }
        this.feedItemList = feed.getChannel().feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rss_row, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        Channel.FeedItem feedItem = feedItemList.get(i);

        //Render image using Picasso library
//        if (!TextUtils.isEmpty(feedItem.getThumbnail())) {
//            Picasso.with(mContext).load(feedItem.getThumbnail())
//                    .error(R.drawable.placeholder)
//                    .placeholder(R.drawable.placeholder)
//                    .into(customViewHolder.imageView);
//        }

        //Setting text view title
//        customViewHolder.textView.setText(Html.fromHtml(feedItem.getTitle()));
        customViewHolder.textView.setText(feedItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textView = (TextView) view.findViewById(R.id.title);
        }
    }
}
