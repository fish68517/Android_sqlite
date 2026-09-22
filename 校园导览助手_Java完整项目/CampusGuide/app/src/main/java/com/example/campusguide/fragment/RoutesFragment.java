package com.example.campusguide.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.campusguide.R;
import com.example.campusguide.RouteDetailActivity;

public class RoutesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routes, container, false);
        view.findViewById(R.id.button_route_classic).setOnClickListener(v -> openRoute("classic"));
        view.findViewById(R.id.button_route_life).setOnClickListener(v -> openRoute("life"));
        view.findViewById(R.id.button_route_relax).setOnClickListener(v -> openRoute("relax"));
        return view;
    }

    private void openRoute(String type) {
        Intent intent = new Intent(requireContext(), RouteDetailActivity.class);
        intent.putExtra(RouteDetailActivity.EXTRA_ROUTE_TYPE, type);
        startActivity(intent);
    }
}
