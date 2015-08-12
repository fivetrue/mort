package com.fivetrue.clubflash.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.fivetrue.clubflash.R;
import com.fivetrue.clubflash.adpater.SearchHostListAdapter;
import com.fivetrue.network.data.HostData;

import java.util.ArrayList;

/**
 * Created by Fivetrue on 2015-06-05.
 */
public class FindHostDialog extends Dialog {

    private ListView mListView = null;
    private ProgressBar mProgress = null;
    private SearchHostListAdapter mAdapter = null;
    private ArrayList<HostData> mHostList = new ArrayList<>();
    private ArrayList<HostData> mSelectedHostList = new ArrayList<>();

    public FindHostDialog(Context context) {
        super(context);
    }

    public FindHostDialog(Context context, int theme) {
        super(context, theme);
    }

    protected FindHostDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_find_host);
        mListView = (ListView) findViewById(R.id.lv_dialog_host);
        mProgress = (ProgressBar) findViewById(R.id.pb_dialog_host);
        findViewById(R.id.btn_dialog_host).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mAdapter = new SearchHostListAdapter(getContext(), R.layout.item_search_host, mHostList);
        mAdapter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView != null && buttonView.getTag() != null && buttonView.getTag() instanceof HostData){
                    HostData host = (HostData) buttonView.getTag();
                    if(isChecked){
                        mSelectedHostList.add(host);
                    }else{
                        mSelectedHostList.remove(host);
                    }
                }
            }
        });
        mListView.setAdapter(mAdapter);
    }


    public void addHostData(HostData host){
        if(host != null && mAdapter != null){
            for(HostData data : mAdapter.getData()){
                if(!TextUtils.isEmpty(host.getHostAddress())){
                    if(data.getHostAddress().equals(host.getHostAddress())){
                        return;
                    }
                }else{
                    return;
                }
            }
            mAdapter.getData().add(host);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setProgressVisible(int visible){
        if(mProgress != null){
            mProgress.setVisibility(visible);
        }
    }

    public ArrayList<HostData> getSelectedHostList(){
        return mSelectedHostList;
    }

    @Override
    public void show() {
        super.show();
        mSelectedHostList.clear();;
        mHostList.clear();
        Display display =((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height=display.getHeight();
        getWindow().setLayout(width, height);
    }
}
