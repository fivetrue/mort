package com.fivetrue.clubflash.ui.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.SeekBar;

import com.fivetrue.clubflash.R;
import com.fivetrue.player.impl.IMusicPlayer;
import com.fivetrue.player.vos.MusicVO;

/**
 * Created by Fivetrue on 2015-05-31.
 */
abstract public class BaseMusicPlayFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, IMusicPlayer{

    public static final String TAG = "BaseMusicFragment";

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = null;

    private boolean isSeek = false;
    private MusicVO mCurrentMusic = null;

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l){
        mOnSeekBarChangeListener = l;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(mOnSeekBarChangeListener != null){
            mOnSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(mOnSeekBarChangeListener != null){
            mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
        }
        isSeek = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mOnSeekBarChangeListener != null){
            mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
        }
        isSeek = false;
    }

    protected abstract void initMusicInfo(MusicVO vo);

    public void setMusicTime(int current, int duration){

    }

    public boolean isSeeking(){
        return isSeek;
    }

    public void setCurrentMusic(MusicVO vo){
        mCurrentMusic = vo;
        initMusicInfo(vo);
    }

    public String convertMilliSecToTime(int current){
        String time = "";
        if(current >= 0){
            int second =(current / 1000) % 60;
            long min =((current- second) /1000 ) /60;
            String strMin  = min >= 10 ? min +"" : "0" + min;
            String strSec = second >= 10 ? second +"" : "0" + second;
            time = strMin + ":" + strSec;
        }else{
            time += current;
        }
        return time;
    }

}
