package com.ma.blue2thchat.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ma.blue2thchat.R;

public class AvatarAdapter extends BaseAdapter {

    private static final String TAG = "AvatarAdapter";
    private Context mContext;
    private static LayoutInflater inflater = null;
    int[] avatarRes;
    String[] avatarNames;

    public AvatarAdapter(Context context, int[] avatarRes, String[] avatarNames) {
        mContext = context;
        this.avatarRes = avatarRes;
        this.avatarNames = avatarNames;
    }

    @Override
    public int getCount() {
        return avatarRes.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        Log.i(TAG, "getView: " + position);

        if (inflater == null)
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        if (convertView == null) {
        convertView = inflater.inflate(R.layout.item_avatar, null);
//        }

        ImageView imageView = convertView.findViewById(R.id.avatarImageView);

        imageView.setImageResource(avatarRes[position]);

        if (convertView == null) Log.i(TAG, "getView: NULLL");

        return convertView;
    }
}
