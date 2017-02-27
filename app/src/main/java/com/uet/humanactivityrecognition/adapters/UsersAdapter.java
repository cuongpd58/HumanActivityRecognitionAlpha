package com.uet.humanactivityrecognition.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uet.humanactivityrecognition.R;
import com.uet.humanactivityrecognition.userdata.UserData;

import java.util.ArrayList;

/**
 * Project name: HumanActivityRecognition
 * Created by Cuong Phan
 * Email: cuongphank58@gmail.com
 * on 20/02/2017.
 * University of Engineering and Technology,
 * Vietnam National University.
 */

public class UsersAdapter extends BaseAdapter {
    private ArrayList<UserData> userDatas;
    private Context mContext;
    private LayoutInflater inflater;

    public UsersAdapter(Context mContext,ArrayList<UserData> userDatas){
        this.mContext = mContext;
        this.userDatas = userDatas;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return userDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return userDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.user_item_layout,null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_name_in_user_item);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.tvName.setText(userDatas.get(position).getName());
        return convertView;
    }

    private class ViewHolder{
        TextView tvName;
    }
}
