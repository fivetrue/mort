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

import com.fivetrue.clubflash.R;
import com.fivetrue.clubflash.ui.view.VisualizerView;

/**
 * Created by Fivetrue on 2015-05-31.
 */
public class VisualizerFragment extends Fragment {

    public static final String TAG = "VisualizerFragment";
    private VisualizerView mVisualizerView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_visualizer, null);
//        mVisualizerView = (VisualizerView) view.findViewById(R.id.visualizer);
//        return view;
        mVisualizerView = new VisualizerView(getActivity());
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return mVisualizerView;
    }

    public void updateVisualizer(byte[] bytes){
        if(mVisualizerView != null){
            mVisualizerView.updateVisualizer(bytes);
        }

    }

}
