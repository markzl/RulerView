package com.markzl.android.rulerview;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.jiedu.android.simpletest.todo.ruler.Utils;

import java.util.Locale;

public class RulerView extends View {

    private Context mContext;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private void init() {

        longLineHeight = Utils.dp2px(mContext, 30);
        lineHeight = Utils.dp2px(mContext, 15);
        longLineWidth = Utils.dp2px(mContext, 2);
        lineWidth = Utils.dp2px(mContext, 1);
        spaceHeight = Utils.dp2px(mContext, 6);
        instrumentMargin = Utils.dp2px(mContext, 10);

        defaultInstrumentViewHeight = Utils.Companion.dp2px(mContext, 100);
        normalTextSize = Utils.sp2px(mContext, 16);
        valueTextSize = Utils.sp2px(mContext, 20);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        bgRect = new Rect();
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(Color.parseColor("#F6FAF7"));

        mTextPaint.setTextSize(normalTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        Rect bounds = new Rect();
        mTextPaint.getTextBounds("0", 0, 1, bounds);
        textHeight = bounds.height();


    }

    private int green = Color.rgb(28, 179, 103);

    ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progress", 0, 1);

    private float progress = 0;

    @SuppressWarnings("unused")
    public float getProgress() {
        return progress;
    }

    @SuppressWarnings("unused")
    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }


    private int longLineHeight;
    private int lineHeight;
    private int longLineWidth;
    private int lineWidth;
    private int contentWidth;
    private float textHeight;
    private int spaceHeight;
    private Rect bgRect;
    private int instrumentMargin;


    //每一个大刻度下小刻度的数量
    private int defaultInstrumentCount = 10;
    //模拟测试一共偏移多少个小刻度
    private int defaultMoveInstrumentCount = 65;

    //默认起始刻度
    private int defaultBeginCount = 0;

    //刻度尺中心刻度
    private int centerInstrumentValue;

    private int contentHeight;

    private int normalTextSize;
    private int valueTextSize;


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthMeasureSpec, getDefaultHeight(heightMeasureSpec));
        }
        contentWidth = getDefaultWidth(widthMeasureSpec) - getPaddingRight() - getPaddingLeft();
        contentHeight = getDefaultHeight(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
//        setMeasuredDimension(getDefaultWidth(widthMeasureSpec), getDefaultHeight(heightMeasureSpec));
        //计算首端距离中心的刻度,这个值相对是固定的
        centerInstrumentValue = (int) Math.floor((contentWidth / 2d) / instrumentMargin);
        defaultBeginCount = (int) Math.floor(defaultCurrentInstrumentValue - centerInstrumentValue);
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();

    }

    private int getDefaultWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = 0;
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                size = mContext.getResources().getDisplayMetrics().widthPixels;
                break;
            case MeasureSpec.EXACTLY:
                size = MeasureSpec.getSize(widthMeasureSpec);
                break;
        }
        return size;
    }

    private int defaultInstrumentViewHeight;

    private int getDefaultHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = 0;
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                //AT_MOST模式，当父容器的mode为AT_MOST或者EXACTLY模式时，子容器的高度为wrap_content
                size = defaultInstrumentViewHeight;
                break;
            case MeasureSpec.EXACTLY:
                size = MeasureSpec.getSize(heightMeasureSpec);
                break;
        }
        return size;
    }

    private int defaultCurrentInstrumentValue;

    @SuppressWarnings("unused")
    public void setInstrument(float value) {
        defaultCurrentInstrumentValue = (int) (value * 10);
        invalidate();
    }

    private int grey = Color.parseColor("#E4E4E4");

    private int paddingLeft;
    private int paddingTop;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景
        drawBackGround(canvas);
        //根据动画进度以及设置的初始刻度计算初始刻度
        int startCount = (int) (progress * defaultMoveInstrumentCount) + defaultBeginCount;
        //处理从左向右过程中出现绘制负刻度的情况
        if (startCount < 0) startCount = 0;

        int moveX = 0;
        for (int i = startCount; moveX <= contentWidth; i++) {

            int lineX = paddingLeft + moveX;
            if (i % defaultInstrumentCount == 0) {
                //绘制长的刻度线
                int longLineValueY = (int) (paddingTop + longLineHeight + textHeight + spaceHeight);
                paint.setStrokeWidth(longLineWidth);
                canvas.drawLine(lineX, 0, lineX, longLineHeight, paint);
                //绘制刻度值
                String text = String.valueOf(i / defaultInstrumentCount);
                mTextPaint.setTextSize(normalTextSize);
                mTextPaint.setColor(Color.BLACK);
                canvas.drawText(text, lineX, longLineValueY, mTextPaint);

            } else {
                //绘制短的刻度线
                paint.setStrokeWidth(lineWidth);
                canvas.drawLine(lineX, 0, lineX, lineHeight, paint);
            }
            moveX += instrumentMargin;
        }

        int middleX = getPaddingLeft() + centerInstrumentValue * instrumentMargin;
        //绘制中间的绿色刻度线
        paint.setColor(green);
        paint.setStrokeWidth(longLineWidth);
        canvas.drawLine(middleX, 0, middleX, longLineHeight, paint);

        //计算当前滑动偏移的刻度
        float offsetInstrumentValue = (float) startCount / defaultInstrumentCount;
        //计算当前总刻度
        float value = offsetInstrumentValue + centerInstrumentValue / 10f;
        //绘制总刻度值
        String text = String.format(Locale.CHINA, "%.1f", value);
        int textValueY = getPaddingTop() + contentHeight - spaceHeight;
        mTextPaint.setColor(green);
        mTextPaint.setTextSize(valueTextSize);
        canvas.drawText(text + "kg", middleX, textValueY, mTextPaint);

    }

    private void drawBackGround(Canvas canvas) {

        bgRect.set(paddingLeft, 0, contentWidth + paddingLeft, getHeight());
        canvas.drawRect(bgRect, mBgPaint);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(grey);
        canvas.drawLine(paddingLeft, 0, contentWidth + paddingLeft, 0, paint);
    }

    private float mStartX = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();

                break;
            case MotionEvent.ACTION_MOVE:
                float mCurrentX = event.getX();
                float distance = mCurrentX - mStartX;
                Log.d("xys", "distance: " + distance + "=  mCurrentX: " + mCurrentX + " - mStartX: " + mStartX);
                if (Math.abs(distance) > instrumentMargin) {
                    move((int) (distance / instrumentMargin));
                    mStartX = mCurrentX;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    private void move(int instrumentCount) {
        defaultMoveInstrumentCount = Math.abs(instrumentCount);
        if (instrumentCount < 0) {
            //  从右向左滑动
            defaultBeginCount += defaultMoveInstrumentCount;
            animator = ObjectAnimator.ofFloat(this, "progress", 0, 1);
        }
        if (instrumentCount > 0) {
            //从左向右滑动
            if (defaultMoveInstrumentCount > defaultBeginCount) {
                //移动距离大于起始刻度，移动起始的最大刻度
                defaultMoveInstrumentCount = defaultBeginCount;
            }
            defaultBeginCount = defaultBeginCount - defaultMoveInstrumentCount;
            //绘制到负数的情况在onDraw()方法中处理
            animator = ObjectAnimator.ofFloat(this, "progress", 0, -1);
        }
        animator.start();
    }

    @SuppressWarnings("unused")
    public int getDefaultInstrumentCount() {
        return defaultInstrumentCount;
    }

    @SuppressWarnings("unused")
    public void setDefaultInstrumentCount(int defaultInstrumentCount) {
        this.defaultInstrumentCount = defaultInstrumentCount;
    }
}
