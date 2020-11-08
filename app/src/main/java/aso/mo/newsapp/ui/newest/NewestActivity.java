package aso.mo.newsapp.ui.newest;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import aso.mo.newsapp.R;
import aso.mo.newsapp.loader.NewsLoader;
import aso.mo.newsapp.models.NewsModel;
import aso.mo.newsapp.util.OnItemClickListener;

public class NewestActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsModel>>, OnItemClickListener {

    private static final int LOADER_ID = 0;
    private NewestRecyclerViewAdapter mAdapter;
    private ProgressBar loadingProgressBar;
    private MaterialTextView networkErrorTV;
    private RecyclerView newestRv;
    private MaterialTextView noNewsTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingProgressBar = findViewById(R.id.progressBar);
        networkErrorTV = findViewById(R.id.network_error_tv);
        noNewsTv = findViewById(R.id.no_news_tv);

        //Setting up the Ui.
        mAdapter = new NewestRecyclerViewAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        newestRv = findViewById(R.id.recyclerView);
        newestRv.setLayoutManager(layoutManager);
        newestRv.setAdapter(mAdapter);

        MaterialButton refreshBtn = findViewById(R.id.refreshButton);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        if (isNotConnected()) {
            // Device is not connected
            // Display the error information
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.network_error_message)
                    .setTitle(R.string.network_error_title);
            AlertDialog dialog = builder.create();
            dialog.show();
            networkErrorTV.setVisibility(View.VISIBLE);
            loadingProgressBar.setVisibility(View.INVISIBLE);
        } else {
            LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
        }

    }

    @NonNull
    @Override
    public Loader<List<NewsModel>> onCreateLoader(int id, @Nullable Bundle args) {
        return new NewsLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsModel>> loader, List<NewsModel> data) {
        if (data != null && !data.isEmpty()) {
            newestRv.setVisibility(View.VISIBLE);
            loadingProgressBar.setVisibility(View.INVISIBLE);
            mAdapter.submitList(data);
        } else {
            loadingProgressBar.setVisibility(View.INVISIBLE);
            noNewsTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<NewsModel>> loader) {

    }

    private void refresh() {
        if (isNotConnected()) {
            networkErrorTV.setVisibility(View.VISIBLE);
            newestRv.setVisibility(View.INVISIBLE);
            loadingProgressBar.setVisibility(View.INVISIBLE);
            noNewsTv.setVisibility(View.INVISIBLE);
        } else {
            networkErrorTV.setVisibility(View.INVISIBLE);
            newestRv.setVisibility(View.INVISIBLE);
            noNewsTv.setVisibility(View.INVISIBLE);
            loadingProgressBar.setVisibility(View.VISIBLE);
            LoaderManager.getInstance(this).restartLoader(LOADER_ID, null, this);
        }

    }

    /**
     * Invoced when an item pressed.
     *
     * @param position The position of the item in the list.
     */
    @Override
    public void onItemClicked(int position) {
        NewsModel news = mAdapter.getCurrentList().get(position);
        String url = news.getUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    // Checks if device is connected to internet
    private boolean isNotConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnectedOrConnecting();
    }
}
