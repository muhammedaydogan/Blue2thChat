package com.ma.blue2thchat.fragments;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ma.blue2thchat.MainActivity;
import com.ma.blue2thchat.R;
import com.ma.blue2thchat.adapters.RecyclerViewBluetoothAdapter;
import com.ma.blue2thchat.bluetooth.BluetoothChatService;
import com.ma.blue2thchat.bluetooth.Constants;
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

    private final String NAME = null;
    private final UUID MY_UUID = null;

    private long lastBackPress;

    Animation rotateAroundItself;
    private View searchingIcon;
    private View searchButton;
    private View makeItVisibleButton;

    public static int REQUEST_ENABLE_BT = 12;
    private BluetoothAdapter bluetoothAdapter;

    private ArrayList<BleDevice> bleDevices;
    private RecyclerViewBluetoothAdapter adapter;

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
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
//                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
//                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
//                            setStatus(R.string.title_connecting);
                            Toast.makeText(getContext(), "Connecting", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
//                            setStatus(R.string.title_not_connected);
                            Toast.makeText(getContext(), "Not Connected", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
//                    if (null != activity) {
//                        Toast.makeText(activity, "Connected to "
//                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    }
                    break;
                case Constants.MESSAGE_TOAST:
//                    if (null != activity) {
//                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
//                                Toast.LENGTH_SHORT).show();
//                    }
                    break;
            }
        }
    };

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
//        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 12));
//        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 3));
//        bleDevices.add(new BleDevice("Finch", "ec:ab:58:da:12:49", -1));
//        bleDevices.add(new BleDevice("The Score", "ec:ab:58:da:12:49", 19));
//        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 13));
//        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 4));
//        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 6));
//        bleDevices.add(new BleDevice("John Mackerel", "ec:ab:58:da:12:49", 16));
//        bleDevices.add(new BleDevice("Harley Johnson", "ec:ab:58:da:12:49", 15));
//        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 12));
//        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 11));
//        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 18));
//        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", -1));
//        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 2));
//        bleDevices.add(new BleDevice(null, "ec:ab:58:da:12:49", 17));


        adapter = new RecyclerViewBluetoothAdapter(bleDevices);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
//        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_recycler_divider, null));

        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnStartConnectionListener(new RecyclerViewBluetoothAdapter.OnStartConnectionListener() {
            @Override
            public void onStartConnection(int position) {
                Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();

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

        searchingIcon.setOnClickListener(onClickListenerSearch);
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
        // Attempt to connect to the device
        ((MainActivity)getContext()).mChatService.connect(device, true);
    }

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
                if (((MainActivity)getContext()).mChatService == null) {
                    setupChat();
                }
                doDiscovery();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        ((MainActivity)getContext()).mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    private void doDiscovery() {

        bleDevices.clear();
        searchingIcon.startAnimation(rotateAroundItself);

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

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getContext().registerReceiver(receiver, filter);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

        Log.i(TAG, "startSearch: Started");

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

    @Override
    public void onPause() {
        super.onPause();
        if (searchingIcon.getAnimation() != null || (searchingIcon.getAnimation() != null && !searchingIcon.getAnimation().hasEnded())) {
            searchingIcon.clearAnimation();
        }
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket) {

    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    public static class MyBluetoothService {
        private static final String TAG = "MY_APP_DEBUG_TAG";
        private Handler handler; // handler that gets info from Bluetooth service

        // Defines several constants used when transmitting messages between the
        // service and the UI.
        private interface MessageConstants {
            public static final int MESSAGE_READ = 0;
            public static final int MESSAGE_WRITE = 1;
            public static final int MESSAGE_TOAST = 2;

            // ... (Add other message types here as needed.)
        }

        private class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;
            private byte[] mmBuffer; // mmBuffer store for the stream

            public ConnectedThread(BluetoothSocket socket) {
                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                // Get the input and output streams; using temp objects because
                // member streams are final.
                try {
                    tmpIn = socket.getInputStream();
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when creating input stream", e);
                }
                try {
                    tmpOut = socket.getOutputStream();
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when creating output stream", e);
                }

                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }

            public void run() {
                mmBuffer = new byte[1024];
                int numBytes; // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs.
                while (true) {
                    try {
                        // Read from the InputStream.
                        numBytes = mmInStream.read(mmBuffer);
                        // Send the obtained bytes to the UI activity.
                        Message readMsg = handler.obtainMessage(
                                MessageConstants.MESSAGE_READ, numBytes, -1,
                                mmBuffer);
                        readMsg.sendToTarget();
                    } catch (IOException e) {
                        Log.d(TAG, "Input stream was disconnected", e);
                        break;
                    }
                }
            }

            // Call this from the main activity to send data to the remote device.
            public void write(byte[] bytes) {
                try {
                    mmOutStream.write(bytes);

                    // Share the sent message with the UI activity.
                    Message writtenMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_WRITE, -1, -1, bytes);
                    writtenMsg.sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when sending data", e);

                    // Send a failure message back to the activity.
                    Message writeErrorMsg =
                            handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                    Bundle bundle = new Bundle();
                    bundle.putString("toast",
                            "Couldn't send data to the other device");
                    writeErrorMsg.setData(bundle);
                    handler.sendMessage(writeErrorMsg);
                }
            }

            // Call this method from the main activity to shut down the connection.
            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close the connect socket", e);
                }
            }
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