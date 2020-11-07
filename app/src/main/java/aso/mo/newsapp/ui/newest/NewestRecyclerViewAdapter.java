package aso.mo.newsapp.ui.newest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import aso.mo.newsapp.R;
import aso.mo.newsapp.models.NewsModel;
import aso.mo.newsapp.util.OnItemClickListener;

public class NewestRecyclerViewAdapter extends ListAdapter<NewsModel, NewestRecyclerViewAdapter.CardItemViewHolder> {
    private final OnItemClickListener listener;

    public NewestRecyclerViewAdapter(OnItemClickListener onItemClickListener) {
        super(new DiffUtil.ItemCallback<NewsModel>() {
            @Override
            public boolean areItemsTheSame(@NonNull NewsModel oldItem, @NonNull NewsModel newItem) {
                return oldItem.getUrl().equals(newItem.getUrl());
            }

            @Override
            public boolean areContentsTheSame(@NonNull NewsModel oldItem, @NonNull NewsModel newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = onItemClickListener;
    }


    @NonNull
    @Override
    public CardItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        return new CardItemViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull CardItemViewHolder holder, int position) {
        final NewsModel currentNews = getItem(position);
        holder.setNews(currentNews.getCategory(),
                currentNews.getTitle(),
                currentNews.getAuthor(),
                currentNews.getDate());
        holder.setListener(listener);
    }

    public static class CardItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTV;
        private final TextView authorTV;
        private final TextView categoryTV;
        private final TextView dateTv;
        private final View mView;

        public CardItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.title_tv);
            categoryTV = itemView.findViewById(R.id.category_tv);
            dateTv = itemView.findViewById(R.id.date_tv);
            authorTV = itemView.findViewById(R.id.author_tv);
            mView = itemView;
        }

        public void setNews(String category, String title, String author, String date) {
            categoryTV.setText(category);
            titleTV.setText(title);
            authorTV.setText(author);
            dateTv.setText(date);
        }

        public void setListener(OnItemClickListener listener) {
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(getAdapterPosition());
                }
            });
        }
    }

}