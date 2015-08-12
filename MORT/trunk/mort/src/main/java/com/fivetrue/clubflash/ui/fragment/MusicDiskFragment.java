package com.fivetrue.clubflash.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fivetrue.clubflash.R;
import com.fivetrue.clubflash.constants.NetworkActionConstatns;
import com.fivetrue.clubflash.ui.activity.player.PlayerActivity;
import com.fivetrue.common.utils.Logger;
import com.fivetrue.light.FlashHelper;
import com.fivetrue.light.fragment.RealtimeFlashMakerFragment;
import com.fivetrue.light.model.Flash;
import com.fivetrue.network.data.HostData;
import com.fivetrue.clubflash.ui.dialog.FindHostDialog;
import com.fivetrue.clubflash.ui.view.MusicDiskSeekView;
import com.fivetrue.network.data.UdpData;
import com.fivetrue.network.helper.UdpSendHelper;
import com.fivetrue.player.vos.MusicVO;

import java.util.ArrayList;

/**
 * Created by Fivetrue on 2015-05-31.
 */
public class MusicDiskFragment extends BaseMusicPlayFragment{

    public static final String TAG = "MusicDiskFragment";

    private MusicDiskSeekView mMusicDiskSeekView = null;
    private TextView mTvTitle = null;
    private TextView mTvAlbum = null;
    private TextView mTvArtist = null;
    private TextView mTvTime = null;
    private SeekBar mSeekbar = null;

    private ImageView mIvMore = null;
    private ImageView mIvPlayToggle = null;

    private ViewGroup mLayoutHost = null;

    private FindHostDialog mFindHostDialog = null;

    private PopupMenu mPopupMenu = null;


    private MusicDiskSeekView.MusicDiskSeekCallbackListener mMusicDiskSeekCallbackListener = null;
    private View.OnClickListener mOnClickPlayToggleListener = null;

    private RealtimeFlashMakerFragment mRealtimeFlashMakerFragment = null;

    private MusicVO mCurrentMusic = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_disk, null);
        mMusicDiskSeekView = (MusicDiskSeekView) view.findViewById(R.id.music_disk);
        mMusicDiskSeekView.setMusicDiskSeekCallbackListener(onMusicDiskSeekCallbackListener);
        mTvTitle = (TextView) view.findViewById(R.id.tv_music_title);
        mTvAlbum = (TextView) view.findViewById(R.id.tv_music_album);
        mTvArtist = (TextView) view.findViewById(R.id.tv_music_artist);
        mTvTime = (TextView) view.findViewById(R.id.tv_music_time);
        mSeekbar = (SeekBar) view.findViewById(R.id.sb_music_disk);
        mIvPlayToggle = (ImageView) view.findViewById(R.id.iv_music_play_toggle);
        mIvMore = (ImageView) view.findViewById(R.id.iv_music_more);
        mLayoutHost = (ViewGroup) view.findViewById(R.id.layout_music_host);

        mIvPlayToggle.setTag(false);
        mIvPlayToggle.setOnClickListener(onClickPlayToggleListener);
        mIvMore.setOnClickListener(onClickMoreListener);
        mSeekbar.setOnSeekBarChangeListener(this);
        mFindHostDialog = new FindHostDialog(getActivity());
        mFindHostDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(dialog != null && dialog instanceof FindHostDialog){
                    addHostsToLayout(((FindHostDialog) dialog).getSelectedHostList());
                }
            }
        });
        return view;
    }

    public void setMusicDiskSeekCallbackListener(MusicDiskSeekView.MusicDiskSeekCallbackListener l){
        mMusicDiskSeekCallbackListener = l;
    }

    public void setNext(){
        if(mMusicDiskSeekView != null){
            mMusicDiskSeekView.changeNext();
        }
    }

    public void setPlayImage(boolean b){
        if(mIvPlayToggle != null){
            mIvPlayToggle.setImageResource(b ? R.drawable.selector_control_pause : R.drawable.selector_control_play);
        }

    }

    public void addHost(HostData data){
        if(mFindHostDialog != null){
            mFindHostDialog.addHostData(data);
        }
    }

    public void showMorePopup(){
       if(mPopupMenu == null && getActivity() != null){
           mPopupMenu = new PopupMenu(getActivity(), mIvMore);
           mPopupMenu.getMenuInflater().inflate(R.menu.menu_music, mPopupMenu.getMenu());
           mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
               @Override
               public boolean onMenuItemClick(MenuItem item) {
                   switch(item.getItemId()){
                       case R.id.action_flash :
                           if(getActivity() != null && getActivity() instanceof PlayerActivity){
                               ((PlayerActivity) getActivity()).startFlashManageActivity();
                           }
                           break;

                       case R.id.action_search :
                            if(mLayoutHost != null){
                                mLayoutHost.removeAllViews();
                            }
                            UdpSendHelper.getInstance().startSearchHost(onSearchHostListener);

                           break;

                       case R.id.action_realtime_flash :
                           if(mRealtimeFlashMakerFragment == null){
                               mRealtimeFlashMakerFragment = new RealtimeFlashMakerFragment();
                               mRealtimeFlashMakerFragment.setOnRealtimeTouchListener(onRealtimeTouchListener);
                           }
                           if(mRealtimeFlashMakerFragment.isAdded()){
                               getFragmentManager().beginTransaction().remove(mRealtimeFlashMakerFragment)
                                       .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                       .commit();
                           }else{
                               getFragmentManager().beginTransaction().add(R.id.layout_music_disk, mRealtimeFlashMakerFragment, mRealtimeFlashMakerFragment.TAG)
                                       .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                       .commit();
                           }
                           break;
                   }


                   return false;
               }
           });
       }
        mPopupMenu.show();
    }

    public ViewGroup getHostContainer(){
        return mLayoutHost;
    }

    private void addHostsToLayout(ArrayList<HostData> list){
        if(list != null && list.size() > 0){
            if(mLayoutHost != null && getActivity() != null){
                for(HostData data : list){
                    AlphaAnimation ani = new AlphaAnimation(0, 1);
                    ani.setDuration(500L);
                    Button btn = new Button(getActivity());
                    btn.setText(data.getHostDescription());
                    btn.setTag(data);
                    btn.setAnimation(ani);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(v != null && v.getTag() != null && v.getTag() instanceof HostData){
                                AlphaAnimation ani = new AlphaAnimation(1, 0);
                                ani.setDuration(500L);
                                v.setAnimation(ani);
                                mLayoutHost.removeView(v);
                            }
                        }
                    });
                    mLayoutHost.addView(btn, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            }
        }
    }

    private View.OnClickListener onClickPlayToggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v != null && v.getTag() != null && v instanceof ImageView){
                boolean b = (Boolean) v.getTag();
                if(b){
                    ((ImageView) v).setImageResource(R.drawable.selector_control_pause);
                }else{
                    ((ImageView) v).setImageResource(R.drawable.selector_control_play);
                }
                if(mOnClickPlayToggleListener != null){
                    mOnClickPlayToggleListener.onClick(v);
                }
                v.setTag(!b);
            }
        }
    };

    private View.OnClickListener onClickMoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showMorePopup();
        }
    };

    private UdpSendHelper.OnSearchHostListener onSearchHostListener = new UdpSendHelper.OnSearchHostListener() {
        @Override
        public void onStartSearch() {
            if(getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFindHostDialog.show();
                        mFindHostDialog.setProgressVisible(View.VISIBLE);
                    }
                });
            }
        }

        @Override
        public void onCancelSearch() {
            if(getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }

        @Override
        public void onFinishSearch() {
            if(getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFindHostDialog.setProgressVisible(View.GONE);
                    }
                });
            }
        }
    };

    private MusicDiskSeekView.MusicDiskSeekCallbackListener onMusicDiskSeekCallbackListener = new MusicDiskSeekView.MusicDiskSeekCallbackListener() {
        @Override
        public void onSelectedMusic(MusicVO vo) {
            if(mMusicDiskSeekCallbackListener != null){
                mMusicDiskSeekCallbackListener.onSelectedMusic(vo);
            }
            mCurrentMusic = vo;
            setCurrentMusic(vo);
        }

        @Override
        public void onMoveChange(MusicVO vo) {
            if(mMusicDiskSeekCallbackListener != null){
                mMusicDiskSeekCallbackListener.onMoveChange(vo);
            }
            setCurrentMusic(vo);
        }

        @Override
        public void onMoveCancel() {
            if(mMusicDiskSeekCallbackListener != null){
                mMusicDiskSeekCallbackListener.onMoveCancel();
            }
            setCurrentMusic(mCurrentMusic);
        }
    };

    private RealtimeFlashMakerFragment.OnRealtimeTouchListener onRealtimeTouchListener = new RealtimeFlashMakerFragment.OnRealtimeTouchListener() {
        @Override
        public void onPress(boolean isPress) {
            ViewGroup viewGroup = getHostContainer();
            if(viewGroup != null){
                for(int i = 0 ; i < viewGroup.getChildCount() ; i++){
                    if(viewGroup.getChildCount() > i){
                        View childView = viewGroup.getChildAt(i);
                        if(childView != null && childView.getTag() != null && childView.getTag() instanceof HostData){
                            HostData host = (HostData) childView.getTag();
                            UdpData data = new UdpData();
                            data.setAddress(host.getHostAddress());
                            data.setPort(host.getHostPort());
                            data.setHeader(NetworkActionConstatns.ACTION_FLASH_SINGLE);
                            Flash f = new Flash();
                            f.setColor(isPress ? 0xFFFFFFFF : 0xFF000000);
                            data.setBody(FlashHelper.getInstance().flashToJson(f));
                            UdpSendHelper.getInstance().sendPacket(data);
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void initMusicInfo(MusicVO vo) {
        if(vo != null && mTvTitle != null && mTvAlbum != null && mTvArtist != null){
            mTvTitle.setVisibility(!TextUtils.isEmpty(vo.getTitle()) ? View.VISIBLE : View.GONE);
            mTvTitle.setText(!TextUtils.isEmpty(vo.getTitle()) ? vo.getTitle() : "");

            mTvAlbum.setVisibility(!TextUtils.isEmpty(vo.getAlbum()) ? View.VISIBLE : View.GONE);
            mTvAlbum.setText(!TextUtils.isEmpty(vo.getAlbum()) ? vo.getAlbum() : "");

            mTvArtist.setVisibility(!TextUtils.isEmpty(vo.getArtist()) ? View.VISIBLE : View.GONE);
            mTvArtist.setText(!TextUtils.isEmpty(vo.getArtist()) ? vo.getArtist() : "");
        }
    }

    @Override
    public void setMusicTime(int current, int duration){
        if(current > 0 && duration > 0 && mTvTime != null){
            mTvTime.setText(convertMilliSecToTime(current) + " / " + convertMilliSecToTime(duration));
        }

        if(mSeekbar != null && !isSeeking()){
            mSeekbar.setMax(duration);
            mSeekbar.setProgress(current);
        }
    }

    public void setOnClickPlayToggleListener(View.OnClickListener l){
        mOnClickPlayToggleListener = l;
    }

    @Override
    public void onPlayMusic(int audiosession) {
        setPlayImage(true);
    }

    @Override
    public void onPauseMusic() {
        setPlayImage(false);
    }

    @Override
    public void onResumeMusic() {
        setPlayImage(true);
    }

    @Override
    public void onPlayBackMusic() {
        setPlayImage(false);
    }

    @Override
    public void onStopMusic() {
        setPlayImage(false);
    }

    @Override
    public void onCompleteMusic(int audiosession) {
        setPlayImage(false);
    }

}
