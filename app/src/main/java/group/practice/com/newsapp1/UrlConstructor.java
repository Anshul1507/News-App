package group.practice.com.newsapp1;

import android.util.Log;


public final class UrlConstructor {

    private static final String LOG_TAG = "UrlConstructor";
    private static final String URL_BASE = "https://content.guardianapis.com/search?";

    private static final String URL_EXTRAS = "&show-fields=headline,trailText,shortUrl,thumbnail,byline";

    private static final String URL_API_KEY = "&api-key=" + "ecc9f376-0d77-4fd4-82ce-81673caa525b";

    public static String constructUrl(String section, String orderBy) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(URL_BASE);

        if (section != null) {
            stringBuilder.append(section);
        } else {
            stringBuilder.append("tag=world/series/the-upside-weekly-report");
        }

        if (orderBy != null) {
            stringBuilder.append("&order-by="
                    + orderBy);
        } else {
            stringBuilder.append("&order-by="
                    + "newest");
        }

        stringBuilder.append(URL_EXTRAS);

        stringBuilder.append(URL_API_KEY);

        Log.i(LOG_TAG, "API GUARDIAN_REQUEST_URL: " + stringBuilder.toString());

        return stringBuilder.toString();
    }
}
