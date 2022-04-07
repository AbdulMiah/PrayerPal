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
    private ImageView qiblaCompass, qiblaDirectionNeedle;
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
    private float currentDegreeNeedle = 0f;

    Location usersCurrentLocation = new Location("service provider");
    private float qiblaBearing;
    private float direction;

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

        qiblaCompass = v.findViewById(R.id.qibla_compass);
        qiblaDirectionNeedle = v.findViewById(R.id.qf_direction_needle);

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

            if (azimuthToDegree < 0) {
                azimuthToDegree = azimuthToDegree + 360;
            }

            // Get direction of Qibla
            direction = qiblaBearing - azimuthToDegree;

            // Start animation of compass and needle ImageView
            setRotationAnimation(currentDegree, -azimuthToDegree, qiblaCompass);
            setRotationAnimation(currentDegreeNeedle, direction, qiblaDirectionNeedle);

            // Set currentDegree, currentDegreeNeedle and lastUpdatedTime
            currentDegree = -azimuthToDegree;
            currentDegreeNeedle = direction;
            lastUpdatedTime = System.currentTimeMillis();

            // Set the degree TextView
            degreeTV.setText(azimuthToDegree+"°");

            // Set off a vibration if user is facing towards the Qibla (+- 5°)
            // And set visibility of TextView and RatioButton to visible
            int a = (int) (qiblaBearing-5);
            int b = (int) (qiblaBearing+5);
            if (azimuthToDegree>=a && azimuthToDegree<=b) {
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

    public void setRotationAnimation(float currDegree, float degree, ImageView imageView) {
        RotateAnimation rotateAnimation = new RotateAnimation(
                currDegree,
                degree,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // Set how long the animation for the compass image will take place
        rotateAnimation.setDuration(210);
        // Set the compass animation after the end of the reservation status
        rotateAnimation.setFillAfter(true);
        // Start the animation
        imageView.startAnimation(rotateAnimation);
    }

    public void getQiblaBearing() {
        // Get bearing of Qibla from users current location
        Location qiblaDirection = new Location("service provider");
        qiblaDirection.setLatitude(21.4225);
        qiblaDirection.setLongitude(39.8262);
        qiblaBearing = usersCurrentLocation.bearingTo(qiblaDirection);

        if (qiblaBearing < 0) {
            qiblaBearing = qiblaBearing + 360;
        }

        // Set bearing TextView
        qiblaBearingTV.setText("Qibla Direction: "+qiblaBearing+"°");
    }

}