package com.example.mob_dev_portfolio.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.mob_dev_portfolio.R;

public class QiblaFragment extends Fragment implements SensorEventListener {

    private TextView locationTV, degreeTV, qiblaBearingTV, facingQiblaTV;
    private ImageView qiblaIV;
    private RadioButton radioBtn;

    private SensorManager sm;
    private Sensor accelerometer, magnetometer;

    private final float[] lastAccelerometer = new float[3];
    private final float[] lastMagnetometer = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientation = new float[3];

    boolean lastAccelerometerSet = false;
    boolean lastMagnetometerSet = false;

    long lastUpdatedTime = 0;
    private float currentDegree = 0f;

    Location usersCurrentLocation = new Location("service provider");
    private float qiblaBearing;

    private SharedPreferences sp;
    private Vibrator vibrator;

    public QiblaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_qibla, container, false);

        sp = getContext().getSharedPreferences("locationData", Context.MODE_PRIVATE);
        this.vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);

        locationTV = v.findViewById(R.id.qf_location_name);
        degreeTV = v.findViewById(R.id.qf_degree);
        qiblaBearingTV = v.findViewById(R.id.qf_qibla_bearing_tv);
        facingQiblaTV = v.findViewById(R.id.qf_facing_qibla_txt);
        radioBtn = v.findViewById(R.id.qf_radio);

        qiblaIV = v.findViewById(R.id.qibla_compass);

        // Instantiating Sensors
        sm = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Setting current location from SharedPreference
        locationTV.setText(sp.getString("location", ""));

        usersCurrentLocation.setLatitude(sp.getFloat("latitude", 0f));
        usersCurrentLocation.setLongitude(sp.getFloat("longitude", 0f));

        getQiblaBearing();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();

        sm.unregisterListener(this, accelerometer);
        sm.unregisterListener(this, magnetometer);
    }

    // Adapted from https://www.techrepublic.com/article/pro-tip-create-your-own-magnetic-compass-using-androids-internal-sensors/
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == accelerometer) {
            System.arraycopy(sensorEvent.values, 0, lastAccelerometer, 0, sensorEvent.values.length);
            lastAccelerometerSet = true;
        } else if (sensorEvent.sensor == magnetometer) {
            System.arraycopy(sensorEvent.values, 0, lastMagnetometer, 0, sensorEvent.values.length);
            lastMagnetometerSet = true;
        }

        // If sensors are set, get the rotation matrix and orientation
        if (lastAccelerometerSet && lastMagnetometerSet && (System.currentTimeMillis() - lastUpdatedTime)>250) { //
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);

            // Get degree angle around the z-axis rotated
            float azimuthInRadians = orientation[0];
            float azimuthToDegree = (float) Math.toDegrees(azimuthInRadians);

            // Get direction of Qibla
            float direction = qiblaBearing - azimuthToDegree;

            // Start animation of compass ImageView
            setRotationAnimation(azimuthToDegree);

            // Set currentDegree and lastUpdatedTime
            currentDegree = -azimuthToDegree;
            lastUpdatedTime = System.currentTimeMillis();

            // Set the degree TextView
            int x = (int) azimuthToDegree;
            if (x<0) {
                x = x+360;
            }
            degreeTV.setText(x+"°");

            // Set off a vibration if user is facing towards the Qibla (+- 5°)
            // And set visibility of TextView and RatioButton to visible
            int a = (int) (qiblaBearing-5);
            int b = (int) (qiblaBearing+5);
            if (x>=a && x<=b) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                facingQiblaTV.setVisibility(View.VISIBLE);
                radioBtn.setVisibility(View.VISIBLE);
            // If user is not facing towards Qibla, make Views invisible
            } else {
                facingQiblaTV.setVisibility(View.INVISIBLE);
                radioBtn.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void setRotationAnimation(float degree) {
        RotateAnimation rotateAnimation = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // Set how long the animation for the compass image will take place
        rotateAnimation.setDuration(210);
        // Set the compass animation after the end of the reservation status
        rotateAnimation.setFillAfter(true);
        // Start the animation
        qiblaIV.startAnimation(rotateAnimation);
    }

    public void getQiblaBearing() {
        // Get bearing of Qibla from users current location
        Location qiblaDirection = new Location("service provider");
        qiblaDirection.setLatitude(21.4225);
        qiblaDirection.setLongitude(39.8262);
        qiblaBearing = usersCurrentLocation.bearingTo(qiblaDirection);
        // Set bearing TextView
        qiblaBearingTV.setText("Qibla Direction: "+qiblaBearing+"°");
    }

}