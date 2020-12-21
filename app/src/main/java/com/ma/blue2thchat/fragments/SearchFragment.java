package com.ma.blue2thchat.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import com.ma.blue2thchat.bluetooth.ClientClass;
import com.ma.blue2thchat.bluetooth.SendReceive;
import com.ma.blue2thchat.objects.BleDevice;

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
    public SendReceive sr;

    public static final int STATE_LISTENING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTION_FAILED = 4;
    public static final int STATE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;


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

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
//    @SuppressLint("HandlerLeak")
//    private final Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case Constants.MESSAGE_STATE_CHANGE:
//                    switch (msg.arg1) {
//                        case BluetoothChatService.STATE_CONNECTED:
////                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//                            Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
////                            mConversationArrayAdapter.clear();
//                            break;
//                        case BluetoothChatService.STATE_CONNECTING:
////                            setStatus(R.string.title_connecting);
//                            Toast.makeText(getContext(), "Connecting", Toast.LENGTH_SHORT).show();
//                            break;
//                        case BluetoothChatService.STATE_LISTEN:
//                        case BluetoothChatService.STATE_NONE:
////                            setStatus(R.string.title_not_connected);
//                            Toast.makeText(getContext(), "Not Connected", Toast.LENGTH_SHORT).show();
//                            break;
//                    }
//                    break;
//                case Constants.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
////                    mConversationArrayAdapter.add("Me:  " + writeMessage);
//                    break;
//                case Constants.MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
//                    // construct a string from the valid bytes in the buffer
//                    String readMessage = new String(readBuf, 0, msg.arg1);
////                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
//                    break;
//                case Constants.MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
////                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
////                    if (null != activity) {
////                        Toast.makeText(activity, "Connected to "
////                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
////                    }
//                    break;
//                case Constants.MESSAGE_TOAST:
////                    if (null != activity) {
////                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
////                                Toast.LENGTH_SHORT).show();
////                    }
//                    break;
//            }
//        }
//    };

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

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
                ChatFragment.messages.clear(); // messages is deleted for new connection
                ChatFragment.clientName = bleDevices.get(position).getName(); // chat fragment title is solved
                ChatFragment.chat_sendReceive = sr;
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
                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            }
        });
    }

    private void connectDevice(int position) {
        // Get the device MAC address
        String address = bleDevices.get(position).getMacAddress();

        // Get the BluetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        ClientClass client = new ClientClass(device, MY_UUID, bluetoothAdapter, handler, sr);
        if (sr == null){
            Toast.makeText(getContext(), "CLIENT SendReceiver is NULL", Toast.LENGTH_SHORT).show();
            return;
        }
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

//        ServerClass server = new ServerClass(bluetoothAdapter, APP_NAME, MY_UUID, handler, sr);
//        server.start();



//        if (bluetoothAdapter.isDiscovering()) {
//            bluetoothAdapter.cancelDiscovery();
//        }


        // Log.i(TAG, "startSearch: Started");

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
                    // We ll write later
                    byte [] readBuffer = (byte[]) msg.obj;
                    String tempMsg = new String(readBuffer, 0, msg.arg1);
                    ChatFragment.messages.add(new com.ma.blue2thchat.objects.Message(tempMsg, true));
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
}