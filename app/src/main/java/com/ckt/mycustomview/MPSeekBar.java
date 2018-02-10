package com.ckt.mycustomview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by D22434 on 2017/11/16.
 */

@SuppressLint("AppCompatCustomView")
public class MPSeekBar extends SeekBar {

    /**
     * SeekBar数值文字颜色
     */
    private int mTextColor;

    /**
     * SeekBar数值文字大小
     */
    private float mTextSize;

    /**
     * SeekBar数值文字内容
     */
    private String mText;

    /**
     * SeekBar数值文字背景宽高
     */
    private float mBgWidth, mBgHeight;

    /**
     * 画笔
     */
    private Paint mPaint;


    public MPSeekBar(Context context) {
        super(context);
    }

    public MPSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MPSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect ThumbRect = getThumb().getBounds();
        Rect ProgressRect = getProgressDrawable().getBounds();
        int i = (ProgressRect.bottom - ProgressRect.top) / 2;// 取默认高度的一半
        getProgressDrawable().setBounds(ProgressRect.left, ProgressRect.top + i - 2, ProgressRect.right, ProgressRect.top + i + 2);// 设置Progress的大小为4
        float f1 = mPaint.ascent();//tmd 居然是个负数
        float f2 = mPaint.descent() - f1;
        int ThumbWidth = ThumbRect.width();
        int ThumbHeight = ThumbRect.height();
        String progress=getProgress() +"";
        canvas.drawText(progress, ThumbRect.left + (ThumbWidth - mPaint.measureText(progress)) / 2.0f, ThumbRect.top -f1 + (ThumbHeight - f2) / 2.0F, mPaint);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
    }
}
