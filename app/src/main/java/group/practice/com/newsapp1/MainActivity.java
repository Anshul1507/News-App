package group.practice.com.newsapp1;


import android.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<News>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int NEWS_LOADER_ID = 1;
    View mProgressbar;
    TextView mEmptyTextView;
    CustomTabsSession customTabsSession;
    private String LOG_TAG = MainActivity.class.getSimpleName();
    private NewsAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CustomTabsServiceConnection customTabsServiceConnection;
    private CustomTabsClient mClient;
    private android.support.v4.widget.NestedScrollView layout;

    public static boolean isConnected(Context context) {
        boolean connected = false;

        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            connected = true;
        }
        return connected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressbar = findViewById(R.id.loading_progress_bar);
//        mEmptyTextView = findViewById(R.id.empty_text_view);
        mSwipeRefreshLayout = findViewById(R.id.swipeBar);
layout = findViewById(R.id.layout_offline);
        mSwipeRefreshLayout.setRefreshing(true);

        loadData();

        ListView list = findViewById(R.id.list_view);
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        list.setAdapter(mAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News currentData = mAdapter.getItem(position);

                customTabLinking(String.valueOf(Uri.parse(currentData.getUrl())));
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadData();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent, R.color.colorPrimary);

    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String SECTION_CHOICE = sharedPreferences.getString("topic", "tag=world/series/the-upside-weekly-report");
        String ORDER_BY = sharedPreferences.getString("order_by", "newest");

        String GUARDIAN_SECTION = UrlConstructor.constructUrl(SECTION_CHOICE, ORDER_BY);

        return new NewsLoader(this, GUARDIAN_SECTION);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsData) {
        Log.i(LOG_TAG, "Invoked onLoadFinished");
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressbar.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
//        mEmptyTextView.setText(" ");
        mAdapter.clear();
        if (newsData != null && !newsData.isEmpty()) {
            mAdapter.addAll(newsData);
        } else {
            if (isConnected(getBaseContext())) {
                mEmptyTextView.setText("@string/issue_no_news");
            } else {
                mEmptyTextView.setText("@string/no_internet");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.i(LOG_TAG, "Invoked onLoaderReset");
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            case R.id.action_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                loadData();
                return true;

            default:
                return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void loadData() {

        if (isConnected(getBaseContext())) {
            getLoaderManager().destroyLoader(1);
            layout.setVisibility(View.GONE);
//            mEmptyTextView.setText("");
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        } else {
            getLoaderManager().destroyLoader(1);
            mSwipeRefreshLayout.setRefreshing(false);
            layout.setVisibility(View.VISIBLE);
//            mEmptyTextView.setText("@string/no_internet");
        }
    }

    public void customTabLinking(String url) {
        customTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mClient = customTabsClient;
                mClient.warmup(0L);
                customTabsSession = mClient.newSession(null);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mClient = null;
            }
        };

        CustomTabsClient.bindCustomTabsService(this, "com.android.chrome", customTabsServiceConnection);
        Uri uri = Uri.parse(url);

        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        intentBuilder.setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.intent.setPackage("com.android.chrome");
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intentBuilder.setShowTitle(true);
        intentBuilder.enableUrlBarHiding();

        customTabsIntent.launchUrl(this, uri);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("order_by") ||
                key.equals("topic")) {
            mAdapter.clear();
            mEmptyTextView.setVisibility(View.GONE);

            View loadingIndicator = findViewById(R.id.loading_progress_bar);
            loadingIndicator.setVisibility(View.VISIBLE);

            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
    }
}
