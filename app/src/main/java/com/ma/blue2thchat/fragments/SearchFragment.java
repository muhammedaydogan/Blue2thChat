package com.ma.blue2thchat.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ma.blue2thchat.R;
import com.ma.blue2thchat.adapters.BluetoothAdapter;
import com.ma.blue2thchat.objects.BleDevice;

import java.util.ArrayList;
import java.util.Calendar;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private long lastBackPress;

    Animation rotateAroundItself;
    private View searchingIcon;

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

        searchingIcon = view.findViewById(R.id.searchingIcon);

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
                } else
                    getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        ArrayList<BleDevice> bleDevices = new ArrayList<>();
        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 12));
        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 3));
        bleDevices.add(new BleDevice("Finch", "ec:ab:58:da:12:49", -1));
        bleDevices.add(new BleDevice("The Score", "ec:ab:58:da:12:49", 19));
        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 13));
        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 4));
        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 6));
        bleDevices.add(new BleDevice("John Mackerel", "ec:ab:58:da:12:49", 16));
        bleDevices.add(new BleDevice("Harley Johnson", "ec:ab:58:da:12:49", 15));
        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 12));
        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 11));
        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 18));
        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", -1));
        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 2));
        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 17));


        BluetoothAdapter adapter = new BluetoothAdapter(bleDevices);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnStartConnectionListener(new BluetoothAdapter.OnStartConnectionListener() {
            @Override
            public void onStartConnection(int position) {
                Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();

                /* Start Connection
                 *
                 * Assume that connection established
                 * And display messaging ui */

                NavHostFragment.findNavController(SearchFragment.this)
                        .navigate(R.id.action_SearchFragment_to_chatFragment);
            }
        });

        searchingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchingIcon.getAnimation() == null || searchingIcon.getAnimation().hasEnded()) {
                    searchingIcon.startAnimation(rotateAroundItself);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Stop animation
                            searchingIcon.clearAnimation();
                        }
                    }, 3000);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        searchingIcon.startAnimation(rotateAroundItself);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Stop animation
                searchingIcon.clearAnimation();
            }
        }, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchingIcon.getAnimation() != null || (searchingIcon.getAnimation() != null && !searchingIcon.getAnimation().hasEnded())) {
            searchingIcon.clearAnimation();
        }
    }
}