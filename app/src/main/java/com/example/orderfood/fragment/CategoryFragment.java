package com.example.orderfood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.R;
import com.example.orderfood.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {
    private String category;
    private TabLayout merchantTabLayout;
    private ViewPager2 merchantViewPager;
    private List<Fragment> merchantFragments;
    private DBMysqlHelper dbHelper;

    public CategoryFragment(String category) {
        this.category = category;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DBMysqlHelper.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        initViews(view);
        setupViewPager();
        return view;
    }

    private void initViews(View view) {
        merchantTabLayout = view.findViewById(R.id.merchantTabLayout);
        merchantViewPager = view.findViewById(R.id.merchantViewPager);
    }

    private void setupViewPager() {
        merchantFragments = new ArrayList<>();
        merchantFragments.add(MerchantListFragment.newInstance(category, false)); // 全部商家
        merchantFragments.add(MerchantListFragment.newInstance(category, true));  // 买过的店
        
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, merchantFragments);
        merchantViewPager.setAdapter(adapter);
        
        new TabLayoutMediator(merchantTabLayout, merchantViewPager, (tab, position) -> {
            tab.setText(getMerchantTabTitle(position));
        }).attach();
    }

    private String getMerchantTabTitle(int position) {
        return position == 0 ? "全部商家" : "买过的店";
    }
} 