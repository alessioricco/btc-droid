package it.alessioricco.btc.adapters;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import java.util.ArrayList;
import java.util.List;

import it.alessioricco.btc.BuildConfig;
import it.alessioricco.btc.TestEnvironment;
import it.alessioricco.btc.injection.TestObjectGraphSingleton;
import it.alessioricco.btc.models.feed.Channel;
import it.alessioricco.btc.util.CustomRobolectricTestRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Config(shadows = { ShadowResources.class },
        sdk = TestEnvironment.sdk,
        constants = BuildConfig.class,
        manifest = TestEnvironment.manifest)
@RunWith(CustomRobolectricTestRunner.class)
public class TestRSSListAdapters {

    @Before
    public void init() throws Exception {

        // Init the IoC and inject us
        TestObjectGraphSingleton.init();
        TestObjectGraphSingleton.getInstance().inject(this);

    }

    /**
     * Method executed after any test
     */
    @After
    public void tearDown() {

        TestObjectGraphSingleton.reset();

    }

    /**
     * GIVEN a feed item
     * WHEN we'll bind the item to the recycle view using the adapter
     * THEN the recycleView associated viewHolder will be populated with the feed item values
     * @throws Exception
     */
    @Test
    public void testRSSListAdaptersWithWellFormedFeed() throws Exception {

        Context context = RuntimeEnvironment.application;

        final List<Channel.FeedItem> feedItemList = new ArrayList<>();

        Channel.FeedItem item1 = new Channel.FeedItem();
        item1.setTitle("title");
        item1.setDescription("description");
        item1.setPubDate("");

        feedItemList.add(item1);

        final RSSListAdapter rssAdapter = new RSSListAdapter(feedItemList);
        assertThat(rssAdapter.getItemCount()).isEqualTo(1);

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setAdapter(rssAdapter);

        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        assertThat(adapter.getItemCount()).isEqualTo(1);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        assertThat(layoutManager).isNotNull();

        final RSSListAdapter.CustomViewHolder viewHolder =  rssAdapter.onCreateViewHolder(recyclerView, 0);
        assertThat(viewHolder).isNotNull();

        rssAdapter.onBindViewHolder(viewHolder, 0);
        assertThat(viewHolder.title.getText().toString()).isEqualTo(item1.getTitle());
        assertThat(viewHolder.description.getText().toString()).isEqualTo(item1.getDescription());
        assertThat(viewHolder.date.getText().toString()).isEqualTo(item1.getPubDate());
    }
}
