package com.archive.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.adapter.AttractionAdapter;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Attraction;
import com.example.myapplication.R;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private OpenHelperDataBase dbHelper;
    private List<Attraction> attractionList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_attractions);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        dbHelper = new OpenHelperDataBase(getContext());
        attractionList = dbHelper.getAllAttractions();

        adapter = new AttractionAdapter(getContext(), attractionList);
        recyclerView.setAdapter(adapter);

        return view;
    }
} 