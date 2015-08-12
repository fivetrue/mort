package com.fivetrue.clubflash.ui.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fivetrue.clubflash.R;
import com.fivetrue.clubflash.adpater.MusicListAdapter;
import com.fivetrue.player.helper.MusicHelper;
import com.fivetrue.player.vos.MusicVO;

import java.util.ArrayList;

/**
 * Created by Fivetrue on 2015-05-31.
 */
public class MusicListFragment extends Fragment {

    public static final String TAG = "MusicListFragment";

    private MusicListAdapter mAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list, null);

        ListView listView = (ListView) view.findViewById(R.id.lv_music);
        ArrayList<MusicVO> arr = MusicHelper.getInstance().findLoacalMusics();
        mAdapter = new MusicListAdapter(getActivity(), R.layout.item_music, arr);
//        mAdapter.setOnClickPlayButtonListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(v != null && v.getTag() != null && v.getTag() instanceof MusicVO && getMusicService() != null){
//                    MusicVO vo = (MusicVO) v.getTag();
//                    playMusic(vo);
//                }
//            }
//        });
        listView.setAdapter(mAdapter);
        return view;
    }

}
