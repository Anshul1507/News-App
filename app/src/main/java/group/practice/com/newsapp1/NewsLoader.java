package group.practice.com.newsapp1;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private static final String LOG_TAG = NewsLoader.class.getName();
    private String mUrl;

    public NewsLoader(Context context, String stringUrl) {
        super(context);
        mUrl = stringUrl;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "Invoked onStartLoading");
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        Log.i(LOG_TAG, "Invoked loadInBackground");
        if (mUrl == null) {
            return null;
        }
        List<News> myListNews = QueryUtils.newsData(mUrl);
        return myListNews;
    }
}
