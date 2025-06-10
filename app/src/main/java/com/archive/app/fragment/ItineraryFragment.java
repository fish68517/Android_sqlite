package com.archive.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.activity.AddItineraryActivity;
import com.archive.app.adapter.ItineraryAdapter;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Itinerary;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ItineraryFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItineraryAdapter adapter;
    private OpenHelperDataBase dbHelper;
    private List<Itinerary> itineraryList = new ArrayList<>();
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itinerary, container, false);

        dbHelper = new OpenHelperDataBase(getContext());
        recyclerView = view.findViewById(R.id.recycler_view_itineraries);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fab = view.findViewById(R.id.fab_add_itinerary);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddItineraryActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadItineraries();
    }

    private void loadItineraries() {
        // FIXME: 暂时硬编码加载ID为1的用户的行程。
        long userId = 1;
        itineraryList.clear();
        itineraryList.addAll(dbHelper.getItinerariesForUser(userId));

        if (adapter == null) {
            adapter = new ItineraryAdapter(getContext(), itineraryList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
} 