package com.ma.blue2thchat.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.ma.blue2thchat.R;
import com.ma.blue2thchat.adapters.AvatarAdapter;

import static android.content.Context.MODE_PRIVATE;

public class SecondFragment extends Fragment {

    private static final String TAG = "SecondFragment";
    SharedPreferences mPreferences;
    private String sharedPrefFile = "com.blue2thchat.sharedprefs";

    int avatarNo;

    public static final int[] avatarRes = {R.drawable.ic_animal_avatar_1, R.drawable.ic_animal_avatar_2,
            R.drawable.ic_animal_avatar_3, R.drawable.ic_animal_avatar_4, R.drawable.ic_animal_avatar_5,
            R.drawable.ic_animal_avatar_6, R.drawable.ic_animal_avatar_7, R.drawable.ic_animal_avatar_8,
            R.drawable.ic_animal_avatar_9, R.drawable.ic_animal_avatar_10, R.drawable.ic_animal_avatar_11,
            R.drawable.ic_animal_avatar_12, R.drawable.ic_animal_avatar_13, R.drawable.ic_animal_avatar_14,
            R.drawable.ic_animal_avatar_15, R.drawable.ic_animal_avatar_16, R.drawable.ic_animal_avatar_17,
            R.drawable.ic_animal_avatar_18, R.drawable.ic_animal_avatar_19, R.drawable.ic_animal_avatar_20};

    public static final String[] avatarNames = {"Anonymous Cat", "Anonymous Dog", "Anonymous Fox", "Anonymous Bear"
            , "Anonymous Giraffe", "Anonymous Tiger", "Anonymous Wolf", "Anonymous Monkey"
            , "Anonymous Raccoon", "Anonymous Goat", "Anonymous Pig", "Anonymous Panda"
            , "Anonymous Lion", "Anonymous Koala", "Anonymous Deer", "Anonymous Cow"
            , "Anonymous Elephant", "Anonymous Serious Night Owl", "Anonymous Rabbit", "Anonymous Rooster"};

    ImageView avatarImageView;
    TextView avatarTextView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPreferences = getContext().getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        avatarNo = -1;
        avatarNo = mPreferences.getInt("avatar", avatarNo);

        Log.i(TAG, "onViewCreated: " + avatarNo);

        avatarImageView = view.findViewById(R.id.avatarImageView);
        avatarTextView = view.findViewById(R.id.avatarTextView);

        avatarImageView.setImageResource((avatarNo == -1) ? R.drawable.ic_baseline_account_circle_24 : avatarRes[avatarNo]);
        avatarTextView.setText((avatarNo == -1) ? getString(R.string.your_avatar) : avatarNames[avatarNo]);

        GridView gridView = view.findViewById(R.id.gridView);
        AvatarAdapter avatarAdapter = new AvatarAdapter(getContext(), avatarRes, avatarNames);
        gridView.setAdapter(avatarAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                avatarImageView.setImageResource(avatarRes[position]);
                avatarTextView.setText(avatarNames[position]);
                avatarNo = position;
            }
        });

        view.findViewById(R.id.button_finish_setup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.putInt("avatar", avatarNo);
                preferencesEditor.apply();

//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_SearchFragment);
                Navigation.findNavController(view).navigate(R.id.action_SecondFragment_to_SearchFragment);
            }
        });

        view.findViewById(R.id.button_prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }
}