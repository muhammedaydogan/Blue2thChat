package com.ma.blue2thchat.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
// import com.ma.blue2thchat.bluetooth.SendReceive;
import com.ma.blue2thchat.objects.Message;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";
    EditText editTextMessage;
    ImageView sendButton;
    public static ArrayList<com.ma.blue2thchat.objects.Message> messages = new ArrayList<>();
    public static String clientName = null;
    public SearchFragment.SendReceive chat_sendReceive;
    public static ChatAdapter chatAdapter;
    public static RecyclerView recyclerView;
    // private Bundle bundle;


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

        // avatar no
        int avatarNo = ((SharedPreferences) ((MainActivity) getActivity()).getmPreferences()).getInt("avatar", -1);
        clientName = ((SharedPreferences) ((MainActivity) getActivity()).getmPreferences()).getString("username","User");
        int receiverAvatarNo = 7;



        chatAdapter = new ChatAdapter(getContext(), messages, avatarNo, receiverAvatarNo);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);

        recyclerView = view.findViewById(R.id.recyclerViewMessages);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.scrollToPosition(messages.size() - 1);

        editTextMessage = view.findViewById(R.id.editTextMessage);
        sendButton = view.findViewById(R.id.sendIcon);


        editTextMessage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top,
                                       int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                recyclerView.scrollToPosition(messages.size() - 1);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chat_sendReceive = SearchFragment.sendReceive;
                String message = String.valueOf(editTextMessage.getText());
                chat_sendReceive.write(message.getBytes());

                editTextMessage.setText("");

                messages.add(new Message(message, false));

                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1);

            }
        });

    }
    public static void readMsg(String msg){

        messages.add(new Message(msg, true));
        chatAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    @Override
    public void onResume() {
        super.onResume();

        Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();

        toolbar.setTitle(clientName);
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
