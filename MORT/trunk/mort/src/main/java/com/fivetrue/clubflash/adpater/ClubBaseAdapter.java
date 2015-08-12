package com.fivetrue.clubflash.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.fivetrue.player.vos.MediaVO;

import java.util.ArrayList;

/**
 * Created by Fivetrue on 2015-05-30.
 */
public abstract class ClubBaseAdapter <T> extends BaseAdapter {

    public static final int INVALID_VALUE = -1;
    private Context mContext = null;
    private int mLayoutResource = INVALID_VALUE;
    private ArrayList<T> mData = null;
    private LayoutInflater mLayoutInflater = null;

    public ClubBaseAdapter(Context context, int resource, ArrayList<T> datas){
        this.mContext = context;
        this.mLayoutResource = resource;
        this.mData = datas;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }


    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<T> getData(){
        return mData;
    }

    public void setData(ArrayList<T> data){
        mData = data;
        notifyDataSetChanged();
    }

    public int getLayoutResource(){
        return mLayoutResource;
    }

    public Context getContext(){
        return mContext;
    }

    protected LayoutInflater getLayoutInflater(){
        return mLayoutInflater;
    }
}
