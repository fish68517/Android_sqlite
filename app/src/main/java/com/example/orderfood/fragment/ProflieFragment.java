package com.example.orderfood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.activity.CustomerServiceFragment;

public class ProflieFragment extends Fragment {


    private TextView username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_priflie, container, false);

        view.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出登录
                getActivity().finish();
                // 退出app
                System.exit(0);
            }
        });

        username = view.findViewById(R.id.tv_username);
        username.setText(MyApplication.getUserName());
        view.findViewById(R.id.tv_id).setVisibility(View.GONE);


        view.findViewById(R.id.ll_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 替换成反馈Fragment
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new CustomerServiceFragment());
                transaction.commit();
            }
        });



        return view;
    }


}
