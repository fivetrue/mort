package com.fivetrue.commonsdk.network.data;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Fivetrue on 2015-04-05.
 */
public class ReceivedMORTDataQueue {
    private static String TAG = "ReceivedMORTDataQueue";
    private static final int BUFF_SIZE = 1024;
    private static ReceivedMORTDataQueue sInstance = null;
    private static ArrayList<MORTNetworkData> mBuffer = new ArrayList<MORTNetworkData>();
    private static boolean isUsing = false;

    public static synchronized  void addData(MORTNetworkData data){
        if(!isUsing){
            isUsing = true;
            if(mBuffer.size() <= BUFF_SIZE){
                mBuffer.add(data);
            }else{
                mBuffer.remove(0);
                mBuffer.add(data);
            }
            isUsing = false;
        }
    }
    public synchronized  MORTNetworkData getData(){
        if(!isUsing){
            isUsing = true;
            MORTNetworkData data = new MORTNetworkData( mBuffer.get(0));
            mBuffer.remove(0);
            isUsing = false;
            return data;
         }
        Log.e(TAG, "queue is used");
        return null;
}

    private void initBuffer(){
        mBuffer.clear();
    }
}