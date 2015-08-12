package com.fivetrue.clubflash.ui.activity.player;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.fivetrue.clubflash.R;
import com.fivetrue.player.activity.MusicPlayerActivity;
import com.fivetrue.player.impl.IMusicPlayer;


abstract public class PlayerBaseActivity extends MusicPlayerActivity implements IMusicPlayer{

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base);
        ViewGroup container = (ViewGroup) findViewById(R.id.layout_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(layoutResID, container);
    }

}

