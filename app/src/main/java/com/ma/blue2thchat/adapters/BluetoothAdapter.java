package com.ma.blue2thchat.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ma.blue2thchat.R;
import com.ma.blue2thchat.bluetooth.BleDevice;

import java.util.ArrayList;

import static com.ma.blue2thchat.fragments.SecondFragment.avatarNames;
import static com.ma.blue2thchat.fragments.SecondFragment.avatarRes;

public class BluetoothAdapter extends RecyclerView.Adapter<BluetoothAdapter.MyViewHolder> {

    private static final String TAG = "BluetoothAdapter";
    ArrayList<BleDevice> bleDevices;

    public BluetoothAdapter(ArrayList<BleDevice> bleDevices) {
        this.bleDevices = bleDevices;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bleutooth_device, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BleDevice bleDevice = bleDevices.get(position);
        if (!(bleDevice.getAvatarNo() == -1))
            holder.avatarImageView.setImageResource(avatarRes[bleDevice.getAvatarNo()]);

        if (bleDevice.getName() == null)
            holder.avatarName.setText(avatarNames[bleDevice.getAvatarNo()]);
        else
            holder.avatarName.setText(bleDevice.getName());
        holder.macAddress.setText(bleDevice.getMacAddress());
        holder.bluetoothIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bleDevices.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatarImageView;
        public TextView avatarName;
        public TextView macAddress;
        public ImageView bluetoothIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            avatarImageView = itemView.findViewById(R.id.avatar);
            avatarName = itemView.findViewById(R.id.username);
            macAddress = itemView.findViewById(R.id.macAddress);
            bluetoothIcon = itemView.findViewById(R.id.bluetoothIcon);
        }
    }
}
