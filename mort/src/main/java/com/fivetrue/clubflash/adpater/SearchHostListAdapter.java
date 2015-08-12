package com.fivetrue.clubflash.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.fivetrue.clubflash.R;
import com.fivetrue.network.data.HostData;

import java.util.ArrayList;

/**
 * Created by Fivetrue on 2015-06-05.
 */
public class SearchHostListAdapter extends ClubBaseAdapter<HostData> {

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = null;

    public SearchHostListAdapter(Context context, int resource, ArrayList<HostData> datas) {
        super(context, resource, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = getLayoutInflater().inflate(getLayoutResource(), null);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_search_item_name);
            holder.tvIp = (TextView) convertView.findViewById(R.id.tv_search_item_ip);
            holder.tvDesc = (TextView) convertView.findViewById(R.id.tv_search_item_desc);
            holder.chCheck = (CheckBox) convertView.findViewById(R.id.cb_search_item_check);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        HostData data = getItem(position);
        if(data != null){
            holder.tvName.setText(data.getHostName());
            holder.tvIp.setText(data.getHostAddress());
            holder.tvDesc.setText(data.getHostDescription());
            holder.chCheck.setTag(data);
            holder.chCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(mOnCheckedChangeListener != null){
                        mOnCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
                    }
                }
            });
        }
        return convertView;
    }

    private static class ViewHolder{
        TextView tvName = null;
        TextView tvIp = null;
        TextView tvDesc = null;
        CheckBox chCheck = null;
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener l){
        mOnCheckedChangeListener = l;
    }


}
