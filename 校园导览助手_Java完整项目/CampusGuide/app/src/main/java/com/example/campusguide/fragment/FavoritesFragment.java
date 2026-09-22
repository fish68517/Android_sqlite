package com.example.campusguide.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusguide.CampusData;
import com.example.campusguide.FavoriteStore;
import com.example.campusguide.R;
import com.example.campusguide.adapter.PlaceAdapter;
import com.example.campusguide.model.Place;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private PlaceAdapter adapter;
    private View emptyView;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        emptyView = view.findViewById(R.id.layout_empty);
        recyclerView = view.findViewById(R.id.recycler_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PlaceAdapter(requireContext(), new ArrayList<>(), this::loadFavorites);
        recyclerView.setAdapter(adapter);
        loadFavorites();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) loadFavorites();
    }

    private void loadFavorites() {
        List<Place> favorites = new ArrayList<>();
        for (Place place : CampusData.getPlaces()) {
            if (FavoriteStore.isFavorite(requireContext(), place.getId())) favorites.add(place);
        }
        adapter.setPlaces(favorites);
        emptyView.setVisibility(favorites.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(favorites.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
