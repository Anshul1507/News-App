package group.practice.com.newsapp1;

public class News {
    private String mTitle;
    private String mAuthor;
    private String mUrl;
    private String mCategory;
    private String mTimestamp;

    public News(String title, String author, String url, String category, String timestamp) {
        this.mTitle = title;
        this.mAuthor = author;
        this.mUrl = url;
        this.mCategory = category;
        this.mTimestamp = timestamp;


    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

}

