package com.ma.blue2thchat.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ma.blue2thchat.fragments.SearchFragment;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class ClientClass extends Thread{

    private BluetoothSocket socket;
    private BluetoothDevice device;
    private BluetoothAdapter adapter;
    private Handler handler;
    private SendReceive sendReceive;

    public ClientClass(BluetoothDevice device, UUID MY_UUID, BluetoothAdapter adapter, Handler handler, SendReceive sendReceive){
        this.device = device;
        this.adapter = adapter;
        this.handler = handler;
        this.sendReceive = sendReceive;
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

            sendReceive = new SendReceive(socket, handler);
            if (sendReceive == null){
                System.out.println("CLIENT CLASS SENDRECEIVE IS NULL");
                Log.d("ClientClass", "CLIENT CLASS SENDRECEIVE IS NULL");
                return;
            }else{
                System.out.println("I M NOT NULL!!!");
                Log.d("ClientClass", "I M NOT NULL!!!");
            }
            sendReceive.start();

        }catch (IOException e){
            e.printStackTrace();
            Message message = Message.obtain();
            message.what = SearchFragment.STATE_CONNECTION_FAILED;
            handler.sendMessage(message);
        }
    }
}
