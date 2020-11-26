package com.ma.blue2thchat.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ma.blue2thchat.R;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class FirstFragment extends Fragment {

    private static final String TAG = "FirstFragment";

    EditText usernameEditText;
    EditText passwordEditText;

    SharedPreferences mPreferences;
    public static final String sharedPrefFile = "com.blue2thchat.sharedprefs";

    private long lastBackPress;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lastBackPress = 0;

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                long newClick = Calendar.getInstance().getTimeInMillis();
//                Log.i(TAG, "handleOnBackPressed: "+lastBackPress+"-"+newClick);
//                 Handle the back button event
                if (newClick - lastBackPress > 2000) {
                    lastBackPress = Calendar.getInstance().getTimeInMillis();
                    Toast.makeText(getContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
                } else
                    getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);

        mPreferences = getContext().getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        view.findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.putString("username", usernameEditText.getText().toString());
                preferencesEditor.putString("password", passwordEditText.getText().toString());
                preferencesEditor.apply();
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        setUserInfo(view);
    }

    private void setUserInfo(View view) {
        String username = "";
        String password = "";
        username = mPreferences.getString("username", username);
        password = mPreferences.getString("password", password);
//        Log.i(TAG, "setUserInfo: " + mPreferences.getAll());
        usernameEditText.setText(username);
        passwordEditText.setText(password);
    }
}