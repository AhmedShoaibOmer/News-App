package aso.mo.newsapp.ui.newest;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingProgressBar = findViewById(R.id.progressBar);
        networkErrorTV = findViewById(R.id.network_error_tv);

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

        if (!isConnected()) {
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

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * <p>This will always be called from the process's main thread.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @NonNull
    @Override
    public Loader<List<NewsModel>> onCreateLoader(int id, @Nullable Bundle args) {
        return new NewsLoader(this);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     *
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     *
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#CursorAdapter(Context,
     * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * <p>This will always be called from the process's main thread.
     *  @param loader The Loader that has finished.
     *
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsModel>> loader, List<NewsModel> data) {
        if (data != null) {
            newestRv.setVisibility(View.VISIBLE);
            loadingProgressBar.setVisibility(View.INVISIBLE);
            mAdapter.submitList(data);
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * <p>This will always be called from the process's main thread.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<List<NewsModel>> loader) {

    }

    private void refresh() {
        if (!isConnected()) {
            networkErrorTV.setVisibility(View.VISIBLE);
            newestRv.setVisibility(View.INVISIBLE);
            loadingProgressBar.setVisibility(View.INVISIBLE);
        } else {
            networkErrorTV.setVisibility(View.INVISIBLE);
            newestRv.setVisibility(View.INVISIBLE);
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
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
