package com.fivetrue.clubflash.ui.activity.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.fivetrue.clubflash.R;
import com.fivetrue.clubflash.ui.activity.flash.FlashActivity;
import com.fivetrue.clubflash.ui.activity.player.PlayerActivity;
import com.fivetrue.network.data.UdpData;
import com.fivetrue.network.helper.IUdpSender;
import com.fivetrue.network.service.NetworkServerService;

/**
 * Created by Fivetrue on 2015-05-30.
 */
public class IntroActivity extends FragmentActivity{

    public static final String TAG = "IntroActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FlashActivity.class));
            }
        });

        findViewById(R.id.btn_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class));
            }
        });
        NetworkServerService.startServer(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkServerService.stopServer(this);
    }
}
