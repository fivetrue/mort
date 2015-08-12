package com.fivetrue.clubflash.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.fivetrue.network.data.UdpData;

/**
 * Created by Fivetrue on 2015-06-05.
 */
public class FlashClientView extends HostView<UdpData> {

    public static final int VIEW_SIZE = 20;

    private int mSize = INVALID_VALUE;

    public FlashClientView(Context context) {
        super(context);
    }

    public FlashClientView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlashClientView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mSize = VIEW_SIZE * (int) getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(getData() != null){
            Paint p = new Paint();
            p.setColor(Color.parseColor("#000000"));
            canvas.drawText(getData().getBody(),0, 0, p);
        }
    }
}
