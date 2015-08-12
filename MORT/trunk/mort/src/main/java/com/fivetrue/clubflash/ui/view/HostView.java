package com.fivetrue.clubflash.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Fivetrue on 2015-06-05.
 */
abstract  public class HostView <T> extends View {

    public static final int INVALID_VALUE = -1;

    private T mData = null;
    public HostView(Context context) {
        super(context);
    }

    public HostView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HostView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setData(T data){
        mData = data;
    }

    public T getData(){
        return mData;
    }
}
