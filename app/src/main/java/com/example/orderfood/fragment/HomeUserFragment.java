package com.example.orderfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.R;
import com.example.orderfood.activity.SearchResultsActivity;
import com.example.orderfood.adapter.MerchantAdapter;
import com.example.orderfood.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class HomeUserFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private SearchView searchView;
    private List<Fragment> fragments;
    private MerchantAdapter merchantAdapter;
    private DBMysqlHelper dbHelper;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DBMysqlHelper.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_user, container, false);
        initViews(view);
        setupViewPager();
        setupSearchView();
        return view;
    }

    private void initViews(View view) {
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        searchView = view.findViewById(R.id.searchView);

    }



    private String getCategoryTitle(int position) {
        switch (position) {
            case 0:
                return "南食堂";
            case 1:
                return "北食堂";
            case 2:
                return "南商业街";
            case 3:
                return "北商业街";
            case 4:
                return "免辣";
            default:
                return "";
        }
    }

    private void setupViewPager() {
        fragments = new ArrayList<>();
        fragments.add(new CategoryFragment("南食堂"));
        fragments.add(new CategoryFragment("北食堂"));
        fragments.add(new CategoryFragment("南商业街"));
        fragments.add(new CategoryFragment("北商业街"));
        fragments.add(new CategoryFragment("免辣"));
        
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);
        
        // 连接TabLayout和ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            View customTab = LayoutInflater.from(getContext()).inflate(R.layout.item_tab_category, null);
            ImageView tabIcon = customTab.findViewById(R.id.tabIcon);
            TextView tabText = customTab.findViewById(R.id.tabText);
            
            // 设置图标和文字
            tabIcon.setImageResource(getCategoryIcon(position));
            tabText.setText(getCategoryTitle(position));
            
            tab.setCustomView(customTab);
        }).attach();
    }

    private int getCategoryIcon(int position) {
        switch (position) {
            case 0:
                return R.mipmap.category_chaocai;  // 南食堂图标
            case 1:
                return R.mipmap.category_haixian;  // 北食堂图标
            case 2:
                return R.mipmap.category_jiushui;   // 南商业街图标
            case 3:
                return R.mipmap.category_recommond;   // 北商业街图标
            case 4:
                return R.mipmap.category_sahngjia;       // 免辣图标
            default:
                return R.drawable.ic_default;        // 默认图标
        }
    }

    private void setupSearchView() {
        searchView.setQueryHint("大米先生");
        searchView.setIconifiedByDefault(false);
        // 设置搜索框样式
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 处理搜索提交
                Intent intent = new Intent(getContext(), SearchResultsActivity.class);
                intent.putExtra("keyword", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


} 