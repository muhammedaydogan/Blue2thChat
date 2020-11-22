package com.ma.blue2thchat.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ma.blue2thchat.R;
import com.ma.blue2thchat.adapters.BluetoothAdapter;
import com.ma.blue2thchat.bluetooth.BleDevice;

import java.util.ArrayList;
import java.util.Calendar;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private long lastBackPress;

    Animation rotateAroundItself;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lastBackPress = 0;

        rotateAroundItself = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAroundItself.setRepeatCount(-1);
        rotateAroundItself.setDuration(500);
        rotateAroundItself.setInterpolator(new LinearInterpolator());

        View searchingIcon = view.findViewById(R.id.searchingIcon);
        searchingIcon.startAnimation(rotateAroundItself);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                long newClick = Calendar.getInstance().getTimeInMillis();
//                Log.i(TAG, "handleOnBackPressed: "+lastBackPress+"-"+newClick);
                // Handle the back button event
                if (newClick - lastBackPress > 2000) {
                    lastBackPress = Calendar.getInstance().getTimeInMillis();
                    Toast.makeText(getContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
                }
                else
                    getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        ArrayList<BleDevice> bleDevices = new ArrayList<>();
        bleDevices.add(new BleDevice(null,"ec:ab:58:da:12:49",16));
        bleDevices.add(new BleDevice(null,"ec:ab:58:da:12:49",3));
        bleDevices.add(new BleDevice("Finch","ec:ab:58:da:12:49",6));
        bleDevices.add(new BleDevice(null,"ec:ab:58:da:12:49",13));

        BluetoothAdapter adapter = new BluetoothAdapter(bleDevices);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}