package com.fivetrue.clubflash.ui.activity.player;

import android.content.Context;
import android.content.Intent;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.fivetrue.clubflash.R;
import com.fivetrue.clubflash.constants.NetworkActionConstatns;
import com.fivetrue.clubflash.ui.activity.flash.FlashActivity;
import com.fivetrue.clubflash.ui.fragment.MusicDiskFragment;
import com.fivetrue.clubflash.ui.fragment.VisualizerFragment;
import com.fivetrue.clubflash.ui.view.MusicDiskSeekView;
import com.fivetrue.common.utils.Logger;
import com.fivetrue.light.FlashHelper;
import com.fivetrue.network.data.HostData;
import com.fivetrue.network.data.UdpData;
import com.fivetrue.network.helper.UdpSendHelper;
import com.fivetrue.network.service.INetworkService;
import com.fivetrue.network.service.INetworkServiceCallback;
import com.fivetrue.network.service.NetworkServiceHelper;
import com.fivetrue.player.vos.MusicVO;
import com.google.gson.Gson;

/**
 * Created by Fivetrue on 2015-05-30.
 */
public class PlayerActivity extends PlayerBaseActivity {

    public static final String TAG = "IntroActivity";
    public static final long VIBRATE_MILLISEC = 5L;
    public static final long DATA_UPDATE_MILLISEC = 1000L;

    private Visualizer mVisualizer = null;
    private VisualizerFragment mVisualizerFragment = null;
    private MusicDiskFragment mMusicDiskFragment = null;
    private Vibrator mVibrator = null;
    private NetworkServiceHelper mNetworkServiceHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if(mMusicDiskFragment == null){
            mMusicDiskFragment = new MusicDiskFragment();
            mMusicDiskFragment.setMusicDiskSeekCallbackListener(mMusicDiskSeekCallbackListener);
            mMusicDiskFragment.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
            mMusicDiskFragment.setOnClickPlayToggleListener(mOnClickPlayToggleListener);
            addMusicPlayer(mMusicDiskFragment);
            getSupportFragmentManager().beginTransaction().add(R.id.layout_container, mMusicDiskFragment, MusicDiskFragment.TAG).commit();
        }
        mNetworkServiceHelper = NetworkServiceHelper.newInstance(this, networkServiceCallback, onNetworkServiceHelperListener);
        addMusicPlayer(this);
    }

    private MusicDiskSeekView.MusicDiskSeekCallbackListener mMusicDiskSeekCallbackListener = new MusicDiskSeekView.MusicDiskSeekCallbackListener() {
        @Override
        public void onSelectedMusic(MusicVO vo) {
            if(vo != null){
                playMusic(vo);
            }
        }

        @Override
        public void onMoveChange(MusicVO vo) {
            if(mVibrator != null){
                mVibrator.vibrate(VIBRATE_MILLISEC);
            }
        }

        @Override
        public void onMoveCancel() {

        }
    };


    private View.OnClickListener mOnClickPlayToggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v != null && v.getTag() != null){
                boolean b = (Boolean) v.getTag();
                if(b){
                    resumeMusic();
                }else{
                    pauseMusic();
                }
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if(seekBar != null){
                setCurrentMusicPosition(seekBar.getProgress());
            }
        }
    };

    @Override
    public void onPauseMusic() {
        if(mVisualizer != null){
            mVisualizer.setEnabled(false);
        }
    }

    @Override
    public void onResumeMusic() {
        if(mVisualizer != null){
            mVisualizer.setEnabled(true);
        }
    }

    @Override
    public void onPlayBackMusic() {

    }
    long preMillisecond = 0;
    int preFlashValue = 0;

    @Override
    public void onPlayMusic(int audiosession) {

        if(mVisualizer != null){
            mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
        }


        preMillisecond = System.currentTimeMillis();
        if(audiosession > 0){
            mVisualizer = new Visualizer(audiosession);
            mVisualizer.setCaptureSize(mVisualizer.getCaptureSizeRange()[1] / 2);

            mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                                  int samplingRate) {

                    if(System.currentTimeMillis() - preMillisecond > DATA_UPDATE_MILLISEC){
                        preMillisecond = System.currentTimeMillis();
                        if(mMusicDiskFragment != null) {
                            mMusicDiskFragment.setMusicTime(getCurrentMusicPosition(), getMusicDuration());
                        }
                    }


//                            if(mMusicDiskFragment.getData() != null){
//                                UdpData data = new UdpData(mMusicDiskFragment.getData());
//                                data.setHeader(NetworkActionConstatns.ACTION_VISUALIZER);
//                                data.setBody(Base64.encodeToString(bytes, Base64.DEFAULT));
//                                UdpSendHelper.getInstance().sendPacket(data);
//
//
//                            }


//                        }
//                    }
                }

                public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {

                }
            }, Visualizer.getMaxCaptureRate() / 2, true, false);
            mVisualizer.setEnabled(true);
        }
    }

    @Override
    public void onStopMusic() {
        if(mVisualizer != null){
            mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
        }
    }

    @Override
    public void onCompleteMusic(int audiosession) {
        if(mVisualizer != null){
            mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mNetworkServiceHelper.bind();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mNetworkServiceHelper.unBind();
        if(mVisualizer != null){
            mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
        }
    }

    private NetworkServiceHelper.OnNetworkServiceHelperListener onNetworkServiceHelperListener = new NetworkServiceHelper.OnNetworkServiceHelperListener() {
        @Override
        public void onServiceConnected(INetworkService service) {

        }

        @Override
        public void onServiceDisconnected() {

        }
    };

    INetworkServiceCallback networkServiceCallback = new INetworkServiceCallback.Stub() {
        @Override
        public void onReceivedFoundHost(final UdpData data) throws RemoteException {
            if(data != null){
                String jHost = data.getBody();
                final HostData host = new Gson().fromJson(jHost, HostData.class);
                if(host != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mMusicDiskFragment != null) {
                                mMusicDiskFragment.addHost(host);
                            }
                        }
                    });
                }
            }
        }

        @Override
        public void onReceivedUdpData(UdpData data) throws RemoteException {

        }
    };

    public void startFlashManageActivity(){
        FlashHelper.getInstance().startFashMaker(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(FlashHelper.getInstance().flashMakerResult(data, requestCode, resultCode)){

        }
    }
}
