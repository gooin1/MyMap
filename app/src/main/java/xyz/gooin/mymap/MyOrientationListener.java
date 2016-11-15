package xyz.gooin.mymap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by gooin on 2016/11/13.
 */

public class MyOrientationListener implements SensorEventListener {

    private static final String TAG = "MyOrientationListener";

    private SensorManager mSensorManager;

    private Context mContext;
    // 方向传感器
    private Sensor mOrientationSensor;
    // 加速度传感器
    private Sensor mAccelerationSensor;
    // 地磁传感器
    private Sensor mMagneticFieldSensor;

    private float lastX;

    public MyOrientationListener(Context context) {
        this.mContext = context;
    }




    public void start() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            // 获得方向/加速度/地磁传感器
            mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
//            mAccelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//            mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        if (mOrientationSensor != null) {
            mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_UI);
        }
//        if (mAccelerationSensor != null) {
//            mSensorManager.registerListener(this, mAccelerationSensor, SensorManager.SENSOR_DELAY_GAME);
//        }
//        if (mMagneticFieldSensor != null) {
//            mSensorManager.registerListener(this, mMagneticFieldSensor, SensorManager.SENSOR_DELAY_GAME);
//        }
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
    }

//    float[] accValue = new float[3];
//    float[] magnValue = new float[3];

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float x = sensorEvent.values[SensorManager.DATA_X];



            if (Math.abs(x - lastX) > 1.0) {
                if (mOnOrientationListener != null) {
                    mOnOrientationListener.onOrientationChanged(x);
                }
            }
            lastX = x;
        }
//        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            accValue = sensorEvent.values.clone();
//        }
//        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            magnValue = sensorEvent.values.clone();
//        }
//        float R[] = new float[9];
//        float values[] = new float[3];
//
//        SensorManager.getRotationMatrix(R, null, accValue, magnValue);
//        SensorManager.getOrientation(R, values);
//        Log.d(TAG, "onSensorChanged: value is " + Math.toDegrees(values[0]));
//
//
//            if (mOnOrientationListener != null) {
//                mOnOrientationListener.onOrientationChanged(x);
//            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private OnOrientationListener mOnOrientationListener;

    public void setOnOrientationListener(OnOrientationListener onOrientationListener) {
        this.mOnOrientationListener = onOrientationListener;
    }


    public interface OnOrientationListener {
        void onOrientationChanged(float x);
    }

}
