package com.bjudahdev.bioniccontrollermkii.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bjudahdev.bioniccontrollermkii.MainActivity;
import com.bjudahdev.bioniccontrollermkii.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private HomeViewModel homeViewModel;

    Button btn_BTEnableDiscovery;
    ListView dashRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //Buttons
        Button btn_BTonOff = (Button) root.findViewById(R.id.btn_BTonOff);

        // Click Listeners
        btn_BTonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).enableDisableBT();
            }
        });

        dashRecyclerView = (ListView) root.findViewById(R.id.BTListView);

        dashRecyclerView.setOnItemClickListener(HomeFragment.this);

        return root;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((MainActivity)getActivity()).BTDevicesOnItemClick(parent, view, position,id);
    }
}