package com.hakimi.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.hakimi.R;
import com.hakimi.adapter.BannerAdapter;
import com.hakimi.model.FeatureItem;
import com.hakimi.ui.activity.AIAssistantActivity;
import com.hakimi.ui.activity.ClassScheduleActivity;
import com.hakimi.ui.activity.HealthDashboardActivity;
import com.hakimi.ui.activity.VirtualFitnessActivity;
import com.hakimi.ui.adapter.FeatureAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final long BANNER_DELAY_MILLIS = 3000L;

    private RecyclerView rvFeatures;
    private ViewPager2 vpHomeBanner;
    private LinearLayout llBannerIndicator;
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private final Runnable bannerRunnable = this::showNextBanner;
    private List<String> bannerItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupBannerCarousel();
        setupFeatureGrid();
    }

    @Override
    public void onResume() {
        super.onResume();
        startBannerAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopBannerAutoScroll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopBannerAutoScroll();
        vpHomeBanner = null;
        llBannerIndicator = null;
        rvFeatures = null;
    }

    private void initViews(View view) {
        rvFeatures = view.findViewById(R.id.rv_home_features);
        vpHomeBanner = view.findViewById(R.id.vp_home_banner);
        llBannerIndicator = view.findViewById(R.id.ll_banner_indicator);
    }

    private void setupBannerCarousel() {
        bannerItems = new ArrayList<>();
        bannerItems.add("res://drawable/home_banner_1");
        bannerItems.add("res://drawable/home_banner_2");
        bannerItems.add("res://drawable/home_banner_3");
        bannerItems.add("res://drawable/home_banner_4");
        bannerItems.add("res://drawable/home_banner_5");

        BannerAdapter bannerAdapter = new BannerAdapter(bannerItems);
        vpHomeBanner.setAdapter(bannerAdapter);
        vpHomeBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateBannerIndicator(position);
                restartBannerAutoScroll();
            }
        });

        setupBannerIndicator();
    }

    private void setupBannerIndicator() {
        llBannerIndicator.removeAllViews();
        Drawable selected = AppCompatResources.getDrawable(requireContext(),
                R.drawable.bg_banner_indicator_selected);
        Drawable normal = AppCompatResources.getDrawable(requireContext(),
                R.drawable.bg_banner_indicator_normal);

        for (int i = 0; i < bannerItems.size(); i++) {
            View dot = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(8), dpToPx(8));
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            dot.setLayoutParams(params);
            dot.setBackground(i == 0 ? selected : normal);
            llBannerIndicator.addView(dot);
        }
    }

    private void updateBannerIndicator(int position) {
        if (llBannerIndicator == null) {
            return;
        }
        Drawable selected = AppCompatResources.getDrawable(requireContext(),
                R.drawable.bg_banner_indicator_selected);
        Drawable normal = AppCompatResources.getDrawable(requireContext(),
                R.drawable.bg_banner_indicator_normal);

        for (int i = 0; i < llBannerIndicator.getChildCount(); i++) {
            View dot = llBannerIndicator.getChildAt(i);
            dot.setBackground(i == position ? selected : normal);
        }
    }

    private void startBannerAutoScroll() {
        if (bannerItems.size() > 1) {
            bannerHandler.removeCallbacks(bannerRunnable);
            bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY_MILLIS);
        }
    }

    private void restartBannerAutoScroll() {
        stopBannerAutoScroll();
        startBannerAutoScroll();
    }

    private void stopBannerAutoScroll() {
        bannerHandler.removeCallbacks(bannerRunnable);
    }

    private void showNextBanner() {
        if (vpHomeBanner == null || bannerItems.isEmpty()) {
            return;
        }
        int nextItem = (vpHomeBanner.getCurrentItem() + 1) % bannerItems.size();
        vpHomeBanner.setCurrentItem(nextItem, true);
    }

    private int dpToPx(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void setupFeatureGrid() {
        rvFeatures.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvFeatures.setNestedScrollingEnabled(false);

        List<FeatureItem> items = new ArrayList<>();
        items.add(new FeatureItem("\u5065\u5eb7\u6570\u636e", android.R.drawable.ic_menu_info_details));
        items.add(new FeatureItem("\u8bfe\u8868\u4fe1\u606f", android.R.drawable.ic_menu_my_calendar));
        items.add(new FeatureItem("AI\u52a9\u624b", android.R.drawable.ic_menu_manage));
        items.add(new FeatureItem("\u865a\u62df\u5065\u8eab", android.R.drawable.ic_menu_help));

        FeatureAdapter adapter = new FeatureAdapter(items);
        adapter.setOnItemClickListener(position -> {
            if (getActivity() == null) {
                return;
            }
            if (position == 0) {
                startActivity(new Intent(getActivity(), HealthDashboardActivity.class));
                return;
            }
            if (position == 1) {
                startActivity(new Intent(getActivity(), ClassScheduleActivity.class));
                return;
            }
            if (position == 2) {
                startActivity(new Intent(getActivity(), AIAssistantActivity.class));
                return;
            }
            if (position == 3) {
                startActivity(new Intent(getActivity(), VirtualFitnessActivity.class));
                return;
            }
            Toast.makeText(
                    getContext(),
                    "\u5373\u5c06\u8fdb\u5165\uff1a" + items.get(position).getName(),
                    Toast.LENGTH_SHORT
            ).show();
        });
        rvFeatures.setAdapter(adapter);
    }
}
