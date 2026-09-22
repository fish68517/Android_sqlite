package com.example.campusguide.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.campusguide.CampusData;
import com.example.campusguide.LoginActivity;
import com.example.campusguide.MainActivity;
import com.example.campusguide.R;
import com.example.campusguide.adapter.BannerAdapter;
import com.example.campusguide.adapter.PlaceAdapter;
import com.example.campusguide.model.Place;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class HomeFragment extends Fragment {
    private final Handler carouselHandler = new Handler(Looper.getMainLooper());
    private ViewPager2 viewPager;
    private final Runnable carouselRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager != null && viewPager.getAdapter() != null && viewPager.getAdapter().getItemCount() > 0) {
                int next = (viewPager.getCurrentItem() + 1) % viewPager.getAdapter().getItemCount();
                viewPager.setCurrentItem(next, true);
                carouselHandler.postDelayed(this, 3500);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        List<Place> places = CampusData.getPlaces();

        String username = requireContext().getSharedPreferences(LoginActivity.PREFS, Context.MODE_PRIVATE)
                .getString(LoginActivity.KEY_USERNAME, "同学");
        ((TextView) view.findViewById(R.id.text_greeting)).setText("你好，" + username + " 👋");

        viewPager = view.findViewById(R.id.view_pager_banner);
        viewPager.setAdapter(new BannerAdapter(requireContext(), places.subList(0, 3)));
        TabLayout tabLayout = view.findViewById(R.id.tab_banner_indicator);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> { }).attach();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_recommended);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new PlaceAdapter(requireContext(), places.subList(0, 3), null));

        view.findViewById(R.id.text_view_all).setOnClickListener(v ->
                ((MainActivity) requireActivity()).openTab(R.id.nav_places));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        carouselHandler.removeCallbacks(carouselRunnable);
        carouselHandler.postDelayed(carouselRunnable, 3500);
    }

    @Override
    public void onPause() {
        super.onPause();
        carouselHandler.removeCallbacks(carouselRunnable);
    }

    @Override
    public void onDestroyView() {
        carouselHandler.removeCallbacks(carouselRunnable);
        viewPager = null;
        super.onDestroyView();
    }
}
