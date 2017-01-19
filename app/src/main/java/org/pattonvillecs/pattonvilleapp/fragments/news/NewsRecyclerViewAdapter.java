package org.pattonvillecs.pattonvilleapp.fragments.news;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.news.articles.NewsArticle;

import java.util.ArrayList;
import java.util.List;

class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.RecorderViewHolder> {

    private final View.OnClickListener onClickListener;
    private ArrayList<NewsArticle> newsArticles;

    NewsRecyclerViewAdapter(ArrayList<NewsArticle> NewsArticles, View.OnClickListener onClickListener) {
        this.newsArticles = NewsArticles;
        this.onClickListener = onClickListener;
    }

    @Override
    public RecorderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_news_listview_item, parent, false);
        v.setOnClickListener(onClickListener);
        return new RecorderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecorderViewHolder holder, int position) {

        holder.titleView.setText(newsArticles.get(position).getTitle());
        holder.imageView.setImageResource(R.drawable.test_news_1);
        holder.sourceView.setBackgroundColor(newsArticles.get(position).getSourceColor());
    }

    @Override
    public void onBindViewHolder(RecorderViewHolder holder, int position, List<Object> payloads) {

        holder.titleView.setText(newsArticles.get(position).getTitle());
        holder.imageView.setImageResource(R.drawable.test_news_1);
        holder.sourceView.setBackgroundColor(newsArticles.get(position).getSourceColor());
    }

    @Override
    public int getItemCount() {
        return newsArticles.size();
    }

    class RecorderViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        ImageView imageView, sourceView;

        RecorderViewHolder(View view) {
            super(view);

            titleView = (TextView) itemView.findViewById(R.id.home_news_listview_item_textView);
            imageView = (ImageView) itemView.findViewById(R.id.home_news_listview_item_imageView);
            sourceView = (ImageView) itemView.findViewById(R.id.home_news_listview_item_color);
        }
    }
}
