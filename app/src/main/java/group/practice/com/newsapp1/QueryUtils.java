package group.practice.com.newsapp1;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {
    private static String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    public static List<News> newsData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Caused by IOException");
        }
        List<News> myNews = parseJsonResponse(jsonResponse);
        return myNews;
    }

    public static List<News> parseJsonResponse(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        List<News> newsArrayList = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(jsonString);

            JSONObject response = root.getJSONObject("response");

            JSONArray result = response.getJSONArray("results");
            for (int i = 0; i < result.length(); i++) {
                JSONObject index = result.getJSONObject(i);

                JSONObject fields = index.getJSONObject("fields");

                String titleFromJson = index.optString("webTitle");
                String authorFromJson = fields.optString("byline");
                String urlFromJson = index.optString("webUrl");
                String categoryFromJson = index.optString("sectionName");
                String dateFromJson = index.optString("webPublicationDate");

                News news = new News(titleFromJson, authorFromJson, urlFromJson, categoryFromJson, dateFromJson);
                newsArrayList.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Caused by JSONException");
        }
        return newsArrayList;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            if (stringUrl == null) {
                return null;
            } else {
                url = new URL(stringUrl);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Caused by MalformedURLException");
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String response = "";
        if (url == null) {
            return response;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                response = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Response code : " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Caused by IOException");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return response;
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder jsonString = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                jsonString.append(line);
                line = bufferedReader.readLine();
            }
        }
        return jsonString.toString();
    }
}
