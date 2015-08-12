package com.fivetrue.clubflash.ui.view;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fivetrue.clubflash.R;
import com.fivetrue.player.helper.MusicHelper;
import com.fivetrue.player.vos.MusicVO;

import java.util.ArrayList;

/**
 * Created by Fivetrue on 2015-05-31.
 */
public class MusicDiskSeekView extends AbsCircleSeekView {
    public static final String TAG = "MusicDiskSeekView";
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    public interface MusicDiskSeekCallbackListener{
        void onSelectedMusic(MusicVO vo);
        void onMoveChange(MusicVO vo);
        void onMoveCancel();
    }

    private Rect mDestinationRect = null;
    private int mPadding = INVALID_VALUE;

    private ArrayList<MusicVO> mMusicList = new ArrayList<>();
    private MusicDiskSeekCallbackListener mMusicDiskSeekCallbackListener = null;
    private int mLastPos = 0;
    private Bitmap mCurrentImage = null;
    static private Bitmap sDefaultJaketImage = null;

    public MusicDiskSeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMusicList = MusicHelper.getInstance().findLoacalMusics();
        mPadding = STROKE_BACKGROUND * (int)getResources().getDisplayMetrics().density;
        sDefaultJaketImage = BitmapFactory.decodeResource(getResources(), R.drawable.jaket_default);
        loadImage(mMusicList.get(getCurrentValue()));
    }

    @Override
    protected void drawMore(Canvas canvas) {
        drawAlbumArt(canvas);
    }

    @Override
    protected void drawBackgroundCircle(Canvas canvas) {
//        super.drawBackgroundCircle(canvas);
        Circle back = getBackgroundCircle();
        Circle fore = getForegroundCircle();
        Paint backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        RadialGradient gradient = new RadialGradient(back.cx, back.cy, back.rad, Color.WHITE,
                STROKE_BACKGROUND_COLOR, android.graphics.Shader.TileMode.CLAMP);
        backgroundPaint.setDither(true);
        backgroundPaint.setShader(gradient);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth((STROKE_BACKGROUND * getResources().getDisplayMetrics().density));
        canvas.drawCircle(back.cx, back.cy, back.rad , backgroundPaint);


        Paint p = new Paint();
        p.setColor(Color.parseColor("#000000"));
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1);
        if(back != null){
            float value = back.rad + (STROKE_BACKGROUND * getResources().getDisplayMetrics().density) / 2;
            while(value > fore.rad - ((STROKE_FOREGROUND * getResources().getDisplayMetrics().density) / 2)){
                canvas.drawCircle(back.cx, back.cy, value, p);
                value -=  getResources().getDisplayMetrics().density * 3;
            }
        }
    }

    private void drawAlbumArt(Canvas canvas){
        if(canvas != null){
            if(mCurrentImage != null){
                if(mDestinationRect == null){
                    mDestinationRect = new Rect(mPadding, mPadding, getWidth() - mPadding,
                            getHeight() - mPadding );
                }
                Paint paint = new Paint();
                paint.setAntiAlias(true);

                Bitmap output = Bitmap.createBitmap(getWidth(),
                        getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(output);
                Rect rectSrouce = new Rect(0, 0, mCurrentImage. getWidth(),
                        mCurrentImage.getHeight());

                c.drawCircle(getWidth() / 2,
                        getHeight() / 2, (getWidth() / 2) - mPadding, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                c.drawBitmap(mCurrentImage, rectSrouce, mDestinationRect, paint);
                canvas.drawBitmap(output, 0, 0, null);
                output.recycle();
            }

//            Paint p = new Paint();
//            p.setAntiAlias(true);
//            p.setColor(STROKE_BACKGROUND_COLOR);
//            float rad = STROKE_FOREGROUND * getResources().getDisplayMetrics().density;
//            canvas.drawCircle(getWidth() / 2 , getWidth() /2, rad, p);
//            p.setColor(Color.parseColor("#FFFFFF"));
//            canvas.drawCircle(getWidth() / 2 , getWidth() /2, rad / 2, p);
        }

    }

    @Override
    protected int getMaxValue() {
        return mMusicList.size();
    }

    @Override
    protected int getCurrentValue() {
        return mLastPos;
    }

    @Override
    protected void movePosition(int pos) {
        if(getCurrentValue() != pos){
            if(mMusicDiskSeekCallbackListener != null && mMusicList != null && getMaxValue() > pos){
                mMusicDiskSeekCallbackListener.onMoveChange(mMusicList.get(pos));
            }
        }
        loadImage(mMusicList.get(pos));

    }

    @Override
    protected void cancelMove() {
        if(mMusicDiskSeekCallbackListener != null){
            mMusicDiskSeekCallbackListener.onMoveCancel();
        }
        loadImage(mMusicList.get(mLastPos));
    }

    @Override
    protected void selectedPosition(int pos) {
        if(mMusicDiskSeekCallbackListener != null && mMusicList != null && getMaxValue() > pos){
            mMusicDiskSeekCallbackListener.onSelectedMusic(mMusicList.get(pos));
        }
        mLastPos = pos;

    }

    public void setMusicDiskSeekCallbackListener(MusicDiskSeekCallbackListener l){
        mMusicDiskSeekCallbackListener = l;
    }

    public void changeNext(){
        if(mMusicList != null){
            if(mMusicList.size() > mLastPos + 1){
                mLastPos ++;
            }else{
                mLastPos = 0;
            }
            setCurrentDegree((CIRCLE_DEGREE / mMusicList.size()) * mLastPos);
            selectedPosition(mLastPos);
            loadImage(mMusicList.get(mLastPos));
        }
    }


    private void loadImage(MusicVO vo){
        if(vo != null && vo.getAlbumId() != null) {
            Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(vo.getAlbumId()));
            Glide.with(getContext())
                    .load(uri)
                    .asBitmap()
                    .error(R.mipmap.ic_launcher)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            if (resource != null) {
                                mCurrentImage = resource;
                                invalidate();
                            }
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            mCurrentImage = sDefaultJaketImage;
                            invalidate();
                        }

                    });
        }
    }
}
