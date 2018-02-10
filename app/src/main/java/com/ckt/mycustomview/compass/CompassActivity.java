package com.ckt.mycustomview.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.mycustomview.R;

public class CompassActivity extends AppCompatActivity {

    private static String TAG = "CompassActivity";
    /**
     * 控件
     */
    private TextView mTvAccuracy;
    private CompassViewForMIUI mCompassViewForMIUI;
    /**
     * 传感器
     */
    private Sensor aSensor;
    private Sensor mSensor;
    private SensorManager sm;
    /**
     * 数据
     */
    protected final Handler mHandler = new Handler();
    //精确值次数
    private static final int MAX_ACCURATE_COUNT = 20;
    private static final int MAX_INACCURATE_COUNT = 20;
    private final float MAX_ROATE_DEGREE = 1.0f;
    private volatile int mAccurateCount;
    private volatile int mInaccurateCount;
    private volatile boolean mCalibration;
    private float mTargetDirection;
    private float mDirection;
    private boolean mStopDrawing;
    private AccelerateInterpolator mInterpolator;
    private int mMagneticFieldAccuracy = SensorManager.SENSOR_STATUS_UNRELIABLE;
    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];


    private void resetAccurateCount() {
        mAccurateCount = 0;
    }

    private void increaseAccurateCount() {
        mAccurateCount++;
    }

    private void resetInaccurateCount() {
        mInaccurateCount = 0;
    }

    private void increaseInaccurateCount() {
        mInaccurateCount++;
    }

    /**
     * 校准选择
     *
     * @param isCalibration
     */
    private void switchMode(boolean isCalibration) {
        mCalibration = isCalibration;
        if (mCalibration) {
            resetAccurateCount();
        } else {
            Toast.makeText(this, R.string.calibrate_success, Toast.LENGTH_SHORT).show();
            resetInaccurateCount();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msensor);
        initView();
        initData();
    }

    private void initView() {
        mTvAccuracy = (TextView) findViewById(R.id.tv_accuracy);
        mCompassViewForMIUI = (CompassViewForMIUI) findViewById(R.id.cv_compass);
    }

    private void initData() {
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mInterpolator = new AccelerateInterpolator();
        mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /***
     * 注册传感器的监听事件
     */
    @Override
    protected void onResume() {
        super.onResume();
        mStopDrawing = false;
        sm.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(listener, aSensor, SensorManager.SENSOR_DELAY_GAME);
        mHandler.postDelayed(mCompassViewUpdater, 20);
    }

    /***
     * 注销传感器的监听事件
     */
    @Override
    protected void onPause() {
        super.onPause();
        mStopDrawing = true;
        sm.unregisterListener(listener);
    }

    /**
     * 传感器的监听对象
     */
    SensorEventListener listener = new SensorEventListener() {
        //传感器改变时,一般是通过这个方法里面的参数确定传感器状态的改变
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mTvAccuracy.setText(getResources().getString(R.string.accuracy_compass, event.accuracy));
                magneticFieldValues = event.values;
                mMagneticFieldAccuracy = event.accuracy;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mTvAccuracy.setText(getResources().getString(R.string.accuracy_compass, accuracy));
            }
        }
    };


    protected Runnable mCompassViewUpdater = new Runnable() {
        @Override
        public void run() {
            if (mCompassViewForMIUI != null && !mStopDrawing) {
                //计算当前方向
                calculateTargetDirection();
                if (mDirection != mTargetDirection) {
                    // 计算指南针旋转
                    float to = mTargetDirection;
                    // 限制最大旋转速度为 MAX_ROTATE_DEGREE
                    float distance = to - mDirection;
                    if (Math.abs(distance) > MAX_ROATE_DEGREE) {
                        distance = distance > 0 ? MAX_ROATE_DEGREE : (-1.0f * MAX_ROATE_DEGREE);
                    }
                    //如果distance太小，减低旋转速度
                    mDirection = mDirection
                            + ((to - mDirection) * mInterpolator.getInterpolation(Math.abs(distance) > MAX_ROATE_DEGREE ? 0.4f : 0.3f));
                    mCompassViewForMIUI.setRotate(Math.round(mDirection));
                }
                mHandler.postDelayed(mCompassViewUpdater, 20);
            }
        }
    };

    /**
     * 先判断指南针是否校准，然后通过矩阵计算当前方向。
     */
    private void calculateTargetDirection() {
        //同步控制
        synchronized (this) {
            calibrateCompass();
            //校准成功后获取数据，生成方向
            if (magneticFieldValues != null && accelerometerValues != null) {
                float[] R = new float[9];
                //旋转矩阵
                if (SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues)) {
                    float[] orientation = new float[3];
                    //orientation[0] :Azimuth 方位角
                    SensorManager.getOrientation(R, orientation);
                    //将方位角 转换角度
                    float direction = (float) Math.toDegrees(orientation[0]) * -1.0f;
                    //计算出来的角度是 -180 到 180。所以需要转换成0 - 360的。
                    mTargetDirection = (-direction + 360) % 360;
                } else {
                    Log.d(TAG, "Error: SensorManager.getRotationMatrix");
                }
            }
        }
    }

    /**
     * 指南针校准
     */
    private void calibrateCompass() {
        //计算磁场
        double magneticField = Math.sqrt(magneticFieldValues[0] * magneticFieldValues[0]
                + magneticFieldValues[1] * magneticFieldValues[1]
                + magneticFieldValues[2] * magneticFieldValues[2]);
        //取20组数据判断指南针是否校准成功
        if (mCalibration) {
            if (mMagneticFieldAccuracy != SensorManager.SENSOR_STATUS_UNRELIABLE
                    && magneticField >= SensorManager.MAGNETIC_FIELD_EARTH_MIN
                    && magneticField <= SensorManager.MAGNETIC_FIELD_EARTH_MAX) {
                increaseAccurateCount();
            } else {
                resetAccurateCount();
            }
            Log.d(TAG, "accurate count = " + mAccurateCount);
            if (mAccurateCount >= MAX_ACCURATE_COUNT) {
                switchMode(false);
            }
        } else {
            if (mMagneticFieldAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE
                    || magneticField < SensorManager.MAGNETIC_FIELD_EARTH_MIN
                    || magneticField > SensorManager.MAGNETIC_FIELD_EARTH_MAX) {
                increaseInaccurateCount();
            } else {
                resetInaccurateCount();
            }
            Log.d(TAG, "inaccurate count = " + mInaccurateCount);
            if (mInaccurateCount >= MAX_INACCURATE_COUNT) {
                switchMode(true);
            }
        }
    }

}
