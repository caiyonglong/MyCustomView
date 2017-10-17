package com.ckt.com.mycustomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by D22434 on 2017/8/25.
 */

public class CompassViewForMIUI extends View {

    private Paint mLinePaint;
    private Paint mMainLinePaint;
    private Paint mPaint;
    private Paint mRedPaint;
    private Paint mGeyPaint;

    private Paint mAnglePaint;
    private Paint mDegreePaint;
    private Path mPath;
    private Rect mRect;

    private float mAngleSize;
    private float mDegreeSize;
    private float mTextSize;
    private float mLineLength;
    private float mMainLineLength;
    private float mCircleRadius;

    private int rotate = 0;
    private int curDegree = 0;
    private int mCount = 360;
    private float x, y, r;
    private float[] temp;
    String degree;
    String mDegreeText;

    private float[] points = new float[2];

    //外置接口
    public void setRotate(int rotate) {
        curDegree = rotate;
        this.rotate = Math.round(rotate);//采用round方式转换为整型
        invalidate();
    }

    public CompassViewForMIUI(Context context) {
        super(context, null);
    }

    public CompassViewForMIUI(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.CompassViewForMIUI);
        int lineColor = t.getColor(R.styleable.CompassViewForMIUI_lineColor, Color.BLACK);
        int keyColor = t.getColor(R.styleable.CompassViewForMIUI_keyLineColor, Color.BLACK);
        int mainColor = t.getColor(R.styleable.CompassViewForMIUI_mainLineColor, Color.BLACK);
        mDegreeSize = t.getDimension(R.styleable.CompassViewForMIUI_mDegreeSize, 50f);
        mAngleSize = t.getDimension(R.styleable.CompassViewForMIUI_mAngleSize, 20f);
        mTextSize = t.getDimension(R.styleable.CompassViewForMIUI_mTextSize, 30f);
        mCount = t.getInt(R.styleable.CompassViewForMIUI_mCount, 360);

        mLineLength = t.getDimension(R.styleable.CompassViewForMIUI_mLineLength, 30f);
        mMainLineLength = t.getDimension(R.styleable.CompassViewForMIUI_mainLineLength, 30f);
        mCircleRadius = mLineLength;

        mLinePaint = new Paint();
        mLinePaint.setColor(lineColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);


        mMainLinePaint = new Paint();
        mMainLinePaint.setColor(mainColor);
        mMainLinePaint.setStrokeWidth(5);
        mMainLinePaint.setAntiAlias(true);
        mMainLinePaint.setStyle(Paint.Style.STROKE);


        mAnglePaint = new Paint();
        mAnglePaint.setAntiAlias(true);
        mAnglePaint.setColor(lineColor);
        mAnglePaint.setStyle(Paint.Style.STROKE);


        mDegreePaint = new Paint();
        mDegreePaint.setColor(keyColor);
        mDegreePaint.setTextSize(mDegreeSize);
        mDegreePaint.setAntiAlias(true);
        mDegreePaint.setStyle(Paint.Style.STROKE);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#F44336"));
        mPaint.setStyle(Paint.Style.FILL);

        mRedPaint = new Paint();
        mRedPaint.setAntiAlias(true);
        mRedPaint.setStrokeCap(Paint.Cap.ROUND);
        mRedPaint.setStrokeWidth(5);
        mRedPaint.setColor(Color.parseColor("#F44336"));
        mRedPaint.setStyle(Paint.Style.STROKE);


        mGeyPaint = new Paint();
        mGeyPaint.setAntiAlias(true);
        mGeyPaint.setStrokeWidth(5);
        mGeyPaint.setStrokeCap(Paint.Cap.ROUND);
        mGeyPaint.setColor(Color.parseColor("#BDBDBD"));
        mGeyPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();
        mRect = new Rect();
        t.recycle();

    }

    /**
     * 测量宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        x = measure(widthMeasureSpec) / 2;
        y = measure(heightMeasureSpec) / 2;
        if (x > y) {
            r = (float) (y * 0.7);
        } else {
            r = (float) (x * 0.7);
        }
    }

    protected int measure(int measureSpec) {
        int size = 0;
        int measureMode = MeasureSpec.getMode(measureSpec);
        if (measureMode == MeasureSpec.UNSPECIFIED) {
            size = 250;
        } else {
            size = MeasureSpec.getSize(measureSpec);
        }
        return size;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制圆弧
//        canvas.drawArc(
//                x - r - mLineLength - 15,
//                y - r - mLineLength - 15,
//                x + r + mLineLength + 15,
//                y + r + mLineLength + 15,
//                -90,
//                rotate < 180 ? -rotate + 5 : 360 - rotate + 5, false, mRedPaint);

        int tt = rotate < 180 ? -rotate : 360 - rotate;
        //绘制圆弧
        canvas.drawArc(
                x - r - mLineLength - 15,
                y - r - mLineLength - 15,
                x + r + mLineLength + 15,
                y + r + mLineLength + 15,
                -90 + tt+5 ,
                350, false, mGeyPaint);

        /*
        x - r - mLineLength - 5 + mLineLength / 2
        y - r - mLineLength - 5
        x + r + mLineLength + 5 - mLineLength / 2
        y + r + mLineLength + 5
         */


//        canvas.drawCircle(x, y, r, mRedPaint);
//        canvas.drawCircle(x, y, r + mLineLength, mRedPaint);
//        canvas.drawCircle(x, y, r + mMainLineLength, mRedPaint);
        //绘制标尺
//        canvas.drawLine(x, y - r, x, y - r - mMainLineLength, mMainLinePaint);
        //保存图层
        canvas.save();

        //绘制当前度数
        mDegreeText = curDegree + "°";
        mDegreePaint.getTextBounds(mDegreeText, 0, mDegreeText.length(), mRect);
        canvas.drawText(mDegreeText, x - mDegreePaint.measureText(mDegreeText) / 2,
                y + mRect.height() / 2, mDegreePaint);
        //顺时针旋转
        canvas.rotate(-rotate, x, y);

        //绘制刻度
        for (int i = 0; i < mCount; i++) {
            if (i == 0) {
                //绘制三角形
                mPath.moveTo(x - mLineLength, y - r - mLineLength - 15);
                mPath.lineTo(x + mLineLength, y - r - mLineLength - 15);
                mPath.lineTo(x, y - r - mLineLength - 30);
                canvas.drawPath(mPath, mPaint);
            }
            if (i % 90 == 0) {
                mLinePaint.setStrokeWidth(4);
                canvas.drawLine(x, y - r, x, y - r - mLineLength, mLinePaint);
            } else {
                mLinePaint.setStrokeWidth(1);
                canvas.drawLine(x, y - r, x, y - r - mLineLength, mLinePaint);
            }


            canvas.rotate(360 / mCount, x, y);
        }

//        //绘制文字.先旋转后还原，保持文字正常显示
        for (int i = 0; i < 12; i++) {
            degree = i * 30 + "°";
            temp = calculatePoint(30 * i, r - mCircleRadius * 2);
            if (i == 0) {
                degree = "北";
                mAnglePaint.setTextSize(mTextSize);
            } else if (i == 3) {
                degree = "东";
                mAnglePaint.setTextSize(mTextSize);
            } else if (i == 6) {
                degree = "南";
                mAnglePaint.setTextSize(mTextSize);
            } else if (i == 9) {
                degree = "西";
                mAnglePaint.setTextSize(mTextSize);
            } else {
                mAnglePaint.setTextSize(mAngleSize);
            }

            //绘制旋转的度数
            if (rotate != 0) {
                canvas.rotate(rotate, x + temp[1], y - temp[0]);
            }
            canvas.drawText(degree, x + temp[1] - mAnglePaint.measureText(degree) / 2, y - temp[0] + mAnglePaint.getTextSize() / 2, mAnglePaint);
            if (rotate != 0) {
                canvas.rotate(-rotate, x + temp[1], y - temp[0]);
            }
        }

        canvas.restore();

    }


    //计算角度数值的起始坐标
    private float[] calculatePoint(float angle, float r) {
        points[0] = (float) (r * Math.cos(Math.PI * angle / 180));
        points[1] = (float) (r * Math.sin(Math.PI * angle / 180));
        return points;
    }

}
