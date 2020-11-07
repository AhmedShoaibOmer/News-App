package aso.mo.newsapp.loader;

import android.content.Context;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import aso.mo.newsapp.models.NewsModel;
import aso.mo.newsapp.util.QueryUtils;

public class NewsLoader extends AsyncTaskLoader<List<NewsModel>> {

    public NewsLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<NewsModel> loadInBackground() {
        List<NewsModel> listOfNews = null;
        try {
            URL url = QueryUtils.createUrl();
            String jsonResponse = QueryUtils.makeHttpRequest(url);
            listOfNews = QueryUtils.parseJson(jsonResponse);
        } catch (IOException e) {
            Log.e("Queryutils", "Error Loader LoadInBackground: ", e);
        }
        return listOfNews;
    }
}
