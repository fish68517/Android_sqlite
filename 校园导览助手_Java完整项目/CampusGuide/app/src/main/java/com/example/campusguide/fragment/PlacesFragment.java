package com.example.campusguide.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusguide.CampusData;
import com.example.campusguide.R;
import com.example.campusguide.adapter.PlaceAdapter;
import com.example.campusguide.model.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlacesFragment extends Fragment {
    private List<Place> allPlaces;
    private PlaceAdapter adapter;
    private TextView resultCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        allPlaces = CampusData.getPlaces();
        resultCount = view.findViewById(R.id.text_result_count);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_places);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PlaceAdapter(requireContext(), allPlaces, null);
        recyclerView.setAdapter(adapter);

        EditText search = view.findViewById(R.id.edit_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) { }
        });
        updateCount(allPlaces.size());
        return view;
    }

    private void filter(String keyword) {
        String query = keyword.trim().toLowerCase(Locale.ROOT);
        List<Place> filtered = new ArrayList<>();
        for (Place place : allPlaces) {
            String searchable = place.getName() + place.getCategory()
                    + place.getShortDescription() + place.getDescription();
            if (query.isEmpty() || searchable.toLowerCase(Locale.ROOT).contains(query)) {
                filtered.add(place);
            }
        }
        adapter.setPlaces(filtered);
        updateCount(filtered.size());
    }

    private void updateCount(int count) {
        resultCount.setText("共 " + count + " 个地点");
    }
}
