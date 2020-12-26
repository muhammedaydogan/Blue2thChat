package com.ma.blue2thchat.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ma.blue2thchat.R;
import com.ma.blue2thchat.adapters.RecyclerViewBluetoothAdapter;
//import com.ma.blue2thchat.bluetooth.ClientClass;
//import com.ma.blue2thchat.bluetooth.SendReceive;
//import com.ma.blue2thchat.bluetooth.ServerClass;
import com.ma.blue2thchat.objects.BleDevice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";


    private long lastBackPress;

    Animation rotateAroundItself;
    private View searchingIcon;
    private View searchButton;
    private View makeItVisibleButton;
    private EditText mOutMessage;

    public static int REQUEST_ENABLE_BT = 12;
    private BluetoothAdapter bluetoothAdapter;

    private ArrayList<BleDevice> bleDevices;
    private RecyclerViewBluetoothAdapter adapter;

    private static final String APP_NAME = "Blue2thchat";
    private static final UUID MY_UUID = UUID.fromString("b4f8dbce-651c-419d-86c9-2d175e02b501");


    public static final int STATE_LISTENING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTION_FAILED = 4;
    public static final int STATE_MESSAGE_RECEIVED = 5;

    public static SendReceive sendReceive;




    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                bleDevices.add(new BleDevice(deviceName, deviceHardwareAddress, (int) (Math.random() * SecondFragment.avatarRes.length)));
                adapter.notifyDataSetChanged();

                Log.i(TAG, "onReceive: " + deviceName + " " + deviceHardwareAddress);
            }
        }
    };


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
        searchButton = view.findViewById(R.id.searchButton);
        makeItVisibleButton = view.findViewById(R.id.makeItVisibleButton);
        mOutMessage = view.findViewById(R.id.editTextMessage);
        // test_sr = ChatFragment.chat_sendReceive;

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

        bleDevices = new ArrayList<>();


        adapter = new RecyclerViewBluetoothAdapter(bleDevices);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
//        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_recycler_divider, null));

        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // When connect button is clicked , This function is start
        adapter.setOnStartConnectionListener(new RecyclerViewBluetoothAdapter.OnStartConnectionListener() {
            @Override
            public void onStartConnection(int position) {
                // Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();

                /* Start Connection
                 *
                 * Assume that connection established
                 * And display messaging ui */

//                NavHostFragment.findNavController(SearchFragment.this)
//                        .navigate(R.id.action_SearchFragment_to_chatFragment);
                try {
                    bluetoothAdapter.cancelDiscovery();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                connectDevice(position);
                // ChatFragment.messages.clear(); // messages is deleted for new connection
                ChatFragment.clientName = bleDevices.get(position).getName(); // chat fragment title is solved

//                ChatFragment chatFragment = new ChatFragment();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("sendReceive", ChatFragment.chat_sendReceive);
//                chatFragment.setArguments(bundle);
                // ChatFragment.chat_sendReceive = getSendReceive();

                Navigation.findNavController(view).navigate(R.id.action_SearchFragment_to_chatFragment);
            }

            @Override
            public void onStartChat(int position) {
                ChatFragment.clientName = bleDevices.get(position).getName();
                // ChatFragment.chat_sendReceive = getSendReceive();
                Navigation.findNavController(view).navigate(R.id.action_SearchFragment_to_chatFragment);
            }
        });

        View.OnClickListener onClickListenerSearch = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchingIcon.getAnimation() == null || searchingIcon.getAnimation().hasEnded()) {
                    doDiscovery();
                }
            }
        };

        // searchingIcon.setOnClickListener(onClickListenerSearch);
        searchButton.setOnClickListener(onClickListenerSearch);

        makeItVisibleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ServerClass server = new ServerClass(bluetoothAdapter, APP_NAME, MY_UUID);
                server.start();
            }
        });
    }

    private void connectDevice(int position) {
        // Get the device MAC address
        String address = bleDevices.get(position).getMacAddress();

        // Get the BluetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        ClientClass client = new ClientClass(device, MY_UUID);
        client.start();
        Toast.makeText(getContext(), "Connecting as client", Toast.LENGTH_SHORT).show();
    }

    // Bluetooth is starting from here .....
    @Override
    public void onResume() {
        super.onResume();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(getContext(), "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(getContext(), "Bluetooth is available", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {

                // doDiscovery(); // We are listing devices in here
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDiscovery() {

        bleDevices.clear();
        searchingIcon.startAnimation(rotateAroundItself);

        // Register for broadcasts when a device is discovered.
        bluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getContext().registerReceiver(receiver, filter);

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                bleDevices.add(new BleDevice(deviceName, deviceHardwareAddress, (int) (Math.random() * SecondFragment.avatarRes.length)));
                adapter.notifyDataSetChanged();

                Log.i(TAG, "startSearch: " + deviceName + " " + deviceHardwareAddress + " " + Arrays.toString(device.getUuids()));
            }
        }


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Stop animation
                searchingIcon.clearAnimation();
                // Don't forget to unregister the ACTION_FOUND receiver.
                Log.i(TAG, "run: Finished");
                try {
                    getContext().unregisterReceiver(receiver);
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }, 30000);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case STATE_LISTENING:
                    Toast.makeText(getContext(), "Listening", Toast.LENGTH_SHORT).show();
                    break;
                case STATE_CONNECTING:
                    Toast.makeText(getContext(), "Connecting", Toast.LENGTH_SHORT).show();
                    break;
                case STATE_CONNECTED:
                    Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
                    break;
                case STATE_CONNECTION_FAILED:
                    Toast.makeText(getContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                    break;
                case STATE_MESSAGE_RECEIVED:
                    Log.d("ClientClass", "State Message Received is working...");
                    // We ll write later
                    byte [] readBuffer = (byte[]) msg.obj;
                    String tempMsg = new String(readBuffer, 0, msg.arg1);
                    ChatFragment.readMsg(tempMsg);
                    //ChatFragment.messages.add(new com.ma.blue2thchat.objects.Message(tempMsg, true));
                    break;

            }

            return true ;
        }
    });



    @Override
    public void onPause() {
        super.onPause();
        if (searchingIcon.getAnimation() != null || (searchingIcon.getAnimation() != null && !searchingIcon.getAnimation().hasEnded())) {
            searchingIcon.clearAnimation();
        }
    }


    @Override
    public void onDestroyView() {
        try {
            if (bluetoothAdapter != null) {
                bluetoothAdapter.cancelDiscovery();
            }

            getContext().unregisterReceiver(receiver);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        super.onDestroyView();
    }

    public SendReceive getSendReceive() {
        return sendReceive;
    }

    public void setSendReceive(SendReceive sendReceive) {
        this.sendReceive = sendReceive;
    }

    public class ServerClass extends Thread {

        private BluetoothServerSocket serverSocket;

        public ServerClass(BluetoothAdapter bluetoothAdapter, String APP_NAME, UUID MY_UUID){


            try{
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }


        public void run(){
            BluetoothSocket socket = null;
            while (socket == null){
                try{
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING; // STATE_CONNECTING
                    handler.sendMessage(message);
                    socket = serverSocket.accept();
                }catch (IOException e){
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED; // STATE_CONNECTION_FAILED
                    handler.sendMessage(message);

                }

                if (socket != null){
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED; // STATE_CONNECTED
                    handler.sendMessage(message);
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                    break;
                }

            }
        }
    }

    public class ClientClass extends Thread {

        private BluetoothSocket socket;
        private BluetoothDevice device;



        public ClientClass(BluetoothDevice my_device, UUID MY_UUID){
            device = my_device;
            try{
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }


        public void run(){

            // adapter.cancelDiscovery();
            try{
                socket.connect();
                Message message = Message.obtain();
                message.what = SearchFragment.STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);

                sendReceive.start();

            }catch (IOException e){
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = SearchFragment.STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }

    }

    public class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;


        public SendReceive(BluetoothSocket socket){

            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;


            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            }catch (IOException e){
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run(){

            byte [] buffer = new byte[1024];
            int bytes;


            while(true){
                try{
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(SearchFragment.STATE_MESSAGE_RECEIVED, bytes,-1, buffer).sendToTarget();
                }catch (IOException e){
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            try{
                outputStream.write(bytes);

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}