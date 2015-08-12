package com.fivetrue.clubflash.ui.view;

/**
 * Created by Fivetrue on 2015-05-31.
 */

import android.view.MotionEvent;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author fivetrue1101@gmail.com
 *
 */
abstract public class AbsCircleSeekView extends View {

    /**
     * 도형 정보
     */
    public static final int INVALID_VALUE = -1;
    public static final int CIRCLE_DEGREE = 360;

    public static final int STROKE_BACKGROUND = 50;
    public static final int STROKE_BACKGROUND_COLOR = Color.parseColor("#CC000000");

    public static final int STROKE_FOREGROUND = 10;
    public static final int STROKE_FOREGROUND_COLOR = Color.parseColor("#CCFF0000");

    public static final int STROKE_BAR_COLOR = Color.parseColor("#FFFFFF");

    public static final int TEXT_SIZE = 50;
    public static final int TEXT_COLOR = Color.parseColor("#FFFFFF");

    protected static int PADDING = 0;
    protected static boolean SHOWING_TOUCH_LINE = false;
    protected static boolean SHOWING_TEST_TEXT = false;

    private int mStrokeBack = INVALID_VALUE;
    private int mStrokeFore = INVALID_VALUE;
    private int mTextSize = INVALID_VALUE;
    private int mPadding = INVALID_VALUE;

    private int mWidth, mHeight;
    private float mDensity;
    private Context mContext;

    private Path mSeekPath = null;
    /**
     * Canvas & Paint
     */
    private Paint mPaintBackCircle;
    private Paint mPaintForeCircle;
    private Paint mPaintSeekBar;
    private Paint mPaintText;

    /**
     * Circle 정보
     */
    private Circle mCircleBack = null;
    private Circle mCircleFore = null;
    private Circle mCircleBar = null;

    /**
     * 터치 영역 Rect 정보
     */
    private Rect mRectOutTouchArea = null;
    private Rect mRectInTouchArea = null;

//	private ArrayList<String> arr  = new ArrayList<String>();

    private int mCurrentDegree = 0;
    private int mPreDegree = 0;
    private boolean isTouch = false;

    public AbsCircleSeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        initValues();
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        initCircles();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        drawMore(canvas);
        drawBackgroundCircle(canvas);
        drawForegroundCircle(canvas);
        drawSeekBar(canvas);
        if(SHOWING_TOUCH_LINE){
            drawTouchArea(canvas);
        }
        if(SHOWING_TEST_TEXT){
            drawText(canvas);
        }
    }

    /**
     * 추가로 canvas에 그릴 경우 사용
     * @param canvas
     */
    abstract protected void drawMore(Canvas canvas);

    /**
     * 배열 데이터의 최고 값
     * @return
     */
    abstract protected int getMaxValue();

    /**
     * 배열 데이터의 현재
     * @return
     */
    abstract protected int getCurrentValue();

    /**
     * 휠 동작시 드래그 하는 경우
     * @param pos
     */
    abstract protected void movePosition(int pos);

    /**
     * 휠 동작 중 터치 영역을 벗어나 캔슬 되는 경우
     */
    abstract protected void cancelMove();

    /**
     * 선택된 위치를 받아 설정한다
     * @param pos
     */
    abstract protected void selectedPosition(int pos);

    /**
     * 터치 가능한 영역을 확인하기 위해 터치 영역을 그린다
     * @param canvas
     */
    protected void drawTouchArea(Canvas canvas){
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Style.STROKE);
        p.setColor(STROKE_FOREGROUND_COLOR);
        canvas.drawRect(mRectInTouchArea, p);
        canvas.drawRect(mRectOutTouchArea, p);
    }

    /**
     * 현재 위치를 나타내는 Seek Bar를 그린다
     * @param canvas
     */
    protected void drawSeekBar(Canvas canvas){

        if(mSeekPath == null){
            mSeekPath = new Path();
        }
        mSeekPath.reset();
        float x1 = mCircleBar.outlinePt[mCurrentDegree].x;
        float y1 = mCircleBar.outlinePt[mCurrentDegree].y;
        float x2 = mCircleFore.outlinePt[mCurrentDegree].x;
        float y2 = mCircleFore.outlinePt[mCurrentDegree].y;
        mSeekPath.moveTo(x1, y1);
        mSeekPath.lineTo(x2, y2);
        canvas.drawPath(mSeekPath, mPaintSeekBar);
        mSeekPath.close();
    }

    /**
     * 뒤쪽 원형 그리는 메소드
     * @param canvas
     */
    protected void drawBackgroundCircle(Canvas canvas){
        canvas.drawCircle(mCircleBack.cx, mCircleBack.cy, mCircleBack.rad , mPaintBackCircle);
    }

    /**
     * 앞쪽 원형 그리는 메소드
     * @param canvas
     */
    protected void drawForegroundCircle(Canvas canvas){
        canvas.drawCircle(mCircleFore.cx, mCircleFore.cy, mCircleFore.rad , mPaintForeCircle);
    }

    /**
     * Text를 그린다
     * @param canvas
     */
    protected void drawText(Canvas canvas){

        mPaintText.setTextSize(mTextSize);

        if(isTouch){
            canvas.drawText(String.valueOf(getPositionDegree() + 1), mWidth / 2 , (mHeight / 2) - (mTextSize / 3) , mPaintText);
        }else{
            canvas.drawText(String.valueOf(getCurrentValue() + 1), mWidth / 2 , (mHeight / 2) - (mTextSize / 3) , mPaintText);
        }

        canvas.drawLine((mWidth / 2) - (mTextSize / 2),
                (mHeight / 2),
                (mWidth / 2) + (mTextSize / 2),
                (mHeight / 2), mPaintForeCircle);

        canvas.drawText(String.valueOf(getMaxValue())
                , mWidth / 2 , (mHeight / 2) + (mTextSize),
                mPaintText);
    }

    /**
     * 도형을 초기화 한다.
     */
    private void initCircles(){
        mCircleBack = new Circle(mWidth / 2 , mHeight / 2 , (mWidth / 2) - (mStrokeBack / 2) - mPadding);
        mCircleFore = new Circle(mWidth / 2 , mHeight / 2 , (mWidth / 2) - (mStrokeBack + (mStrokeFore / 2)) - mPadding);
        mCircleBar = new Circle(mWidth / 2 , mHeight / 2 , (mWidth / 2) - mPadding);

        mRectOutTouchArea = new Rect((int)(mCircleBack.cx - mCircleBack.rad) - (mStrokeBack / 2),
                (int)(mCircleBack.cy - mCircleBack.rad) - (mStrokeBack / 2),
                (int)(mCircleBack.cx + mCircleBack.rad) + (mStrokeBack / 2),
                (int)(mCircleBack.cy + mCircleBack.rad) + (mStrokeBack / 2));

        mRectInTouchArea = new Rect((int)(mCircleFore.cx - mCircleFore.rad) + (int)(mCircleFore.rad / 2),
                (int)(mCircleFore.cy - mCircleFore.rad) + (int)(mCircleFore.rad / 2),
                (int)(mCircleFore.cx + mCircleFore.rad) - (int)(mCircleFore.rad / 2),
                (int)(mCircleFore.cy + mCircleFore.rad) - (int)(mCircleFore.rad / 2));
    }

    /**
     * 도형을 그릴 떄 사용하는 size 값을 초기화
     */
    private void initValues(){
        // 값 설정
        mDensity = mContext.getResources().getDisplayMetrics().density;
        mStrokeBack = STROKE_BACKGROUND * (int)mDensity;
        mStrokeFore = STROKE_FOREGROUND * (int)mDensity;
        mPadding = PADDING * (int)mDensity;
        mTextSize  = TEXT_SIZE * (int)mDensity;
    }


    /**
     * Paint 객체 초기화
     */
    private void initPaint(){

        // Back Cicle Paint
        mPaintBackCircle = new Paint();
        mPaintBackCircle.setColor(STROKE_BACKGROUND_COLOR);
        mPaintBackCircle.setAntiAlias(true);
        mPaintBackCircle.setStyle(Paint.Style.STROKE);
        mPaintBackCircle.setStrokeWidth(mStrokeBack);

        // Fore Circle Paint
        mPaintForeCircle = new Paint();
        mPaintForeCircle.setAntiAlias(true);
        mPaintForeCircle.setStyle(Paint.Style.STROKE);
        mPaintForeCircle.setStrokeWidth(mStrokeFore);
        mPaintForeCircle.setColor(STROKE_FOREGROUND_COLOR);


        // SeekBar Paint
        mPaintSeekBar = new Paint();
        mPaintSeekBar.setAntiAlias(true);
        mPaintSeekBar.setStyle(Paint.Style.STROKE);
        mPaintSeekBar.setStrokeWidth(mStrokeFore);
        mPaintSeekBar.setColor(STROKE_BAR_COLOR);

        // Text Paint
        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setTextAlign(Align.CENTER);
        mPaintText.setColor(TEXT_COLOR);

    }

    /**
     * 두 점의 각도를 구한다
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private double getAngle(float x1,float y1, float x2,float y2){

        float dx = x2 - x1;
        float dy = y2 - y1;
        double rad= Math.atan2(dx, dy);
        double degree = (rad * (CIRCLE_DEGREE / 2)) / Math.PI ;

        if(degree <0){
            return CIRCLE_DEGREE - Math.abs(degree);
        }
        return degree;
    }

    public int getBackStrokeWidth(){
        return mStrokeBack;
    }

    public int getForeStrokeWidth(){
        return mStrokeFore;
    }

    public int getTextSize(){
        return mTextSize;
    }

    public boolean isTouch(){
        return isTouch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        float x = event.getX();
        float y = event.getY();

        if(mCircleBar == null ){
            return false;
        }

        //Check touch inline
        if((mRectInTouchArea.bottom > y && mRectInTouchArea.top  < y)
                &&  (mRectInTouchArea.left < x && mRectInTouchArea.right > x)){
            mCurrentDegree = mPreDegree;
            cancelMove();
            invalidate();
            return false;
        }

        //Check touch outline
        if(mRectOutTouchArea.bottom < y || mRectOutTouchArea.top > y
                || mRectOutTouchArea.left > x || mRectOutTouchArea.right < x){
            mCurrentDegree = mPreDegree;
            cancelMove();
            invalidate();
            return false;
        }

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN :
                mPreDegree = mCurrentDegree ;
                int p1 = (int) getAngle( mCircleBar.cx, mCircleBar.cy, x, y);
                int p2 = (int) getAngle( mCircleBar.cx, mCircleBar.cy, mCircleBar.outlinePt[mCurrentDegree].x, mCircleBar.outlinePt[mCurrentDegree].y);
                int pos = p2 - p1 ;

                pos = Math.abs(pos);
                if((pos  >= 0 && pos <= 5) ||  (pos <= CIRCLE_DEGREE && pos >= CIRCLE_DEGREE - 5)){
                    isTouch = true;
                    return true;
                }
                return false;

            case MotionEvent.ACTION_MOVE :
                if(isTouch){
                    mCurrentDegree = (int) getAngle( mCircleBar.cx, mCircleBar.cy, x, y);
                    movePosition(getPositionDegree());
                    invalidate();
                    return true;
                }
                return false;

            case MotionEvent.ACTION_UP :
            case MotionEvent.ACTION_CANCEL :
                if(isTouch){
                    selectedPosition(getPositionDegree());
                    isTouch = false;
                    return true;
                }
                return false;
        }
        return false;
    }

    /**
     * 최대 값과 최소 값을 구해 각도를 구함
     * @return
     */
    protected int getPositionDegree(){

//        log(mCurrentDegree);
        float val = (float)getMaxValue() / CIRCLE_DEGREE;
        return (int)(val * mCurrentDegree);
    }

    /**
     * 현재 SeekBar 위치의 각도를 설정
     * @param degree
     */
    protected void setCurrentDegree(int degree){
        mCurrentDegree = degree;
    }

    /**
     * 현재 SeekBar 위치를 얻오 옴
     * @return
     */
    protected int getCurrentDegree(){
        return mCurrentDegree;
    }

    protected Circle getBackgroundCircle(){
        return mCircleBack;
    }

    protected Circle getForegroundCircle(){
        return mCircleFore;
    }

    class Circle {

        public float cx;
        public float cy;
        public float rad;
        public Point[] outlinePt;

        public Circle(int cx, int cy, float rad){
            this.cx = cx;
            this.cy = cy;
            this.rad = rad;
            outlinePt = getCircleInfo(cx, cy, rad);
        }

        public Point[] getCircleInfo(double x1, double y1, double r){

            double fRadian;
            double x, y;
            Point []pt = new Point[CIRCLE_DEGREE];
            for (int i = 0 ; i < CIRCLE_DEGREE ; i++) {
                fRadian = Math.PI / 180 * (double) i;
                x = r * Math.sin(fRadian) + x1;
                y = r * Math.cos(fRadian) + y1;
                pt[i] = new Point((int) x, (int) y);
            }
            return pt;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Circle [cx=" + cx + ", cy=" + cy + ", rad=" + rad
                    + "]";
        }

    }

    protected void log(Object obj){
        System.out.println("fivetrue : " + obj.getClass().getSimpleName() + " = " + obj);
    }
}
