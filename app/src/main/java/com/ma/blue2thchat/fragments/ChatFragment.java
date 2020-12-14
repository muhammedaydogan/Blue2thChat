package com.ma.blue2thchat.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ma.blue2thchat.MainActivity;
import com.ma.blue2thchat.R;
import com.ma.blue2thchat.adapters.ChatAdapter;
import com.ma.blue2thchat.objects.Message;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    EditText editTextMessage;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int avatarNo = ((SharedPreferences) ((MainActivity) getActivity()).getmPreferences()).getInt("avatar", -1);
        int receiverAvatarNo = 7;

        ArrayList<com.ma.blue2thchat.objects.Message> messages = new ArrayList<>();
        messages.add(new Message("Hi", true));
        messages.add(new Message("Yo", false));
        messages.add(new Message("Wassup", true));
        messages.add(new Message("Playin Titanfall" + new String(Character.toChars(0x1F600)), false));
        messages.add(new Message("I will join, let me know when you finish this round", true));
        messages.add(new Message(new String(Character.toChars(0x1F44D)), false));
        messages.add(new Message("Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
                " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim " +
                "veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." +
                " Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat" +
                " nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia" +
                " deserunt mollit anim id est laborum.", false));
        messages.add(new Message("Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
                " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim " +
                "veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." +
                " Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat" +
                " nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia" +
                " deserunt mollit anim id est laborum.", true));
        messages.add(new Message("Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
                " deserunt mollit anim id est laborum.", false));
        messages.add(new Message("Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
                " deserunt mollit anim id est laborum.", true));
        messages.add(new Message("Lorem ipsum dolor sit amet" + new String(Character.toChars(0x1F600)), false));
        messages.add(new Message("Lorem ipsum dolor sit amet" + new String(Character.toChars(0x1F600)), true));

        ChatAdapter chatAdapter = new ChatAdapter(getContext(), messages, avatarNo, receiverAvatarNo);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewMessages);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.scrollToPosition(messages.size() - 1);

        editTextMessage = view.findViewById(R.id.editTextMessage);

//        editTextMessage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int left, int top,
//                                       int right, int bottom, int oldLeft, int oldTop,
//                                       int oldRight, int oldBottom) {
//                if (bottom < oldBottom) {
//                    recyclerView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getContext(), "123", Toast.LENGTH_SHORT).show();
//                            recyclerView.smoothScrollToPosition(messages.size() - 1);
//                        }
//                    }, 100);
//                }
//            }
//        });

        view.findViewById(R.id.sendIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editTextMessage.getText().toString();

                editTextMessage.setText("");

                messages.add(new Message(message, false));
                chatAdapter.notifyDataSetChanged();

                recyclerView.scrollToPosition(messages.size() - 1);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();

        toolbar.setTitle("Anonymous Panda");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ChatFragment.this).navigate(R.id.action_chatFragment_to_SearchFragment);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();

        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(null);
        toolbar.setNavigationOnClickListener(null);
    }
}
