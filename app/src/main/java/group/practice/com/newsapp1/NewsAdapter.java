package group.practice.com.newsapp1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, ArrayList<News> newsArrayList) {
        super(context, 0, newsArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_items, parent, false);

            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.title_text_view);
            holder.author = convertView.findViewById(R.id.author_text_view);
            holder.category = convertView.findViewById(R.id.category_text_view);
            holder.date = convertView.findViewById(R.id.date_text_view);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        News currentData = getItem(position);

        holder.title.setText(currentData.getTitle());
        holder.author.setText("Author :" + currentData.getAuthor());
        holder.category.setText("Category :" + currentData.getCategory());

        String dateTimeStamp = currentData.getTimestamp();
        String[] dateString = dateTimeStamp.split("T");
        holder.date.setText(dateString[0]);
        return convertView;
    }

    static class ViewHolder {
        private TextView title;
        private TextView author;
        private TextView category;
        private TextView date;
    }

}
