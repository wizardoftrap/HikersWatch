package com.shivprakash.hikerswatch;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.ImageView;

public class CompassSensorManager implements SensorEventListener {
    private Context context;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] gravityValues;
    private float[] magneticValues;
    private ImageView compassImageView;

    public CompassSensorManager(Context context, ImageView compassImageView) {
        this.context = context;
        this.compassImageView = compassImageView;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void setCompassImage(int imageResource) {
        compassImageView.setImageResource(imageResource);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravityValues = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticValues = event.values;
        }
        if (gravityValues != null && magneticValues != null) {
            float[] rotationMatrix = new float[9];
            boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, gravityValues, magneticValues);
            if (success) {
                float[] orientationValues = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientationValues);
                float azimuthInRadians = orientationValues[0];
                float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);
                compassImageView.setRotation(-azimuthInDegrees);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this implementation
    }
}
