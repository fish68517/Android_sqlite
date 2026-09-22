package com.example.knowledgelabs.ch05;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import com.example.knowledgelabs.R;

public class FragmentNewsActivity extends Activity implements NewsListFragment.OnNewsSelectedListener {
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_news);
        twoPane = findViewById(R.id.newsDetailContainer) != null;
        if (savedInstanceState == null) {
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.newsListContainer, new NewsListFragment())
                    .commit();
            if (twoPane) {
                manager.beginTransaction()
                        .replace(R.id.newsDetailContainer, NewsDetailFragment.newInstance(0))
                        .commit();
            }
        }
    }

    @Override
    public void onNewsSelected(int position) {
        int target = twoPane ? R.id.newsDetailContainer : R.id.newsListContainer;
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction()
                .replace(target, NewsDetailFragment.newInstance(position));
        if (!twoPane) {
            transaction.addToBackStack("news-detail");
        }
        transaction.commit();
    }
}
