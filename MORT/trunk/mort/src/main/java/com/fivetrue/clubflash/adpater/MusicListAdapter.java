package com.fivetrue.clubflash.adpater;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fivetrue.clubflash.R;
import com.fivetrue.player.vos.MusicVO;

import java.util.ArrayList;

/**
 * Created by Fivetrue on 2015-05-30.
 */
public class MusicListAdapter extends ClubBaseAdapter <MusicVO> {

    public MusicListAdapter(Context context, int resource, ArrayList<MusicVO> datas) {
        super(context, resource, datas);
    }

    private View.OnClickListener mOnClickPlayButtonListenr = null;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if(convertView == null){
            convertView = getLayoutInflater().inflate(getLayoutResource(), null);
            holder.title = (TextView) convertView.findViewById(R.id.tv_music_item_title);
            holder.play = (Button) convertView.findViewById(R.id.btn_music_item_play);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
            if(holder == null){
                convertView = getLayoutInflater().inflate(getLayoutResource(), null);
                holder.title = (TextView) convertView.findViewById(R.id.tv_music_item_title);
                holder.play = (Button) convertView.findViewById(R.id.btn_music_item_play);
                convertView.setTag(holder);
            }
        }
        MusicVO vo = getItem(position);
        if(vo != null){
            holder.title.setText(vo.getTitle());
            holder.play.setTag(vo);
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnClickPlayButtonListenr != null){
                        mOnClickPlayButtonListenr.onClick(v);
                    }
                }
            });
        }
        return convertView;
    }

    private static final class ViewHolder{
        public TextView title = null;
        public Button play = null;
    }

    public void setOnClickPlayButtonListener(View.OnClickListener l){
        mOnClickPlayButtonListenr = l;
    }
}
