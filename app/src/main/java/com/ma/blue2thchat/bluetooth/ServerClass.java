//package com.ma.blue2thchat.bluetooth;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothServerSocket;
//import android.bluetooth.BluetoothSocket;
//import android.os.Handler;
//import android.os.Message;
//
//import java.io.IOException;
//import java.util.UUID;
//
//public class ServerClass extends Thread{
//
//    private BluetoothServerSocket serverSocket;
//    private Handler handler;
//    private SendReceive sendReceive;
//
//
//    public ServerClass(BluetoothAdapter bluetoothAdapter, String APP_NAME, UUID MY_UUID, Handler handler, SendReceive sendReceive){
//
//        this.handler = handler;
//        this.sendReceive = sendReceive;
//        try{
//            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
//        }catch (IOException ex){
//            ex.printStackTrace();
//        }
//    }
//
//
//    public void run(){
//        BluetoothSocket socket = null;
//        while (socket == null){
//            try{
//                Message message = Message.obtain();
//                message.what = 2; // STATE_CONNECTING
//                handler.sendMessage(message);
//                socket = serverSocket.accept();
//            }catch (IOException e){
//                e.printStackTrace();
//                Message message = Message.obtain();
//                message.what = 4; // STATE_CONNECTION_FAILED
//                handler.sendMessage(message);
//
//            }
//
//            if (socket != null){
//                Message message = Message.obtain();
//                message.what = 3; // STATE_CONNECTED
//                handler.sendMessage(message);
//                sendReceive = new SendReceive(socket, handler);
//                sendReceive.start();
//                break;
//            }
//
//        }
//    }
//}
