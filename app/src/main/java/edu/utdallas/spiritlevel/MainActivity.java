package edu.utdallas.spiritlevel;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

/*
 * Get sensor data and display on screen through view drawing
 * 04/17/2016
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager manager;
    private Sensor accelerometer;
    private Sensor magnetic;
    private Sensor orient;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];


    private TextView text;
    private SeekBar perpendicularBar;
    private SeekBar levelBar;

    private float[] result;

    float LOW_PASS_ALPHA = (float)0.1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        text = (TextView)findViewById(R.id.textView2);
        perpendicularBar = (SeekBar)findViewById(R.id.seekBar);
        levelBar = (SeekBar)findViewById(R.id.seekBar2);
        perpendicularBar.setMax(360);
        levelBar.setMax(360);

        // Get sensor manager and sensors
        manager=(SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetic = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        orient = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        // Initialize data
        result = new float[3];
        for(int i = 0; i < 3; i++) {
            result[i] = 0;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gravity = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values.clone();
                break;
            case Sensor.TYPE_ORIENTATION:
                result = event.values.clone();
                lowPass(result);
                text.setText("Angle around X  : " + result[1] + "\n" + "Angle around Z : " + result[2]);
                perpendicularBar.setProgress( (int)result[1] + 180 );
                levelBar.setProgress( (int)result[2] + 180 );
                return;
        }

        if ((gravity == null) || (geomagnetic == null)) return;

        float[] RMatrix = new float[9];
        SensorManager.getRotationMatrix(RMatrix, null, gravity, geomagnetic);
        SensorManager.getOrientation(RMatrix, result);


        // Do low pass processing on sensor value
        lowPass(result);

        // Display result on screen
        String XAngle = String.format(Locale.US, "%.2f", Math.toDegrees(result[1]));
        String ZAngle = String.format(Locale.US, "%.2f", Math.toDegrees(result[2]));
        text.setText("Angle around X  : " + XAngle + "\n" + "Angle around Z : " + ZAngle);
        perpendicularBar.setProgress( (int)(Float.parseFloat(XAngle)) + 180 );
        levelBar.setProgress( (int)(Float.parseFloat(ZAngle)) + 180 );

        // Clean up
        gravity = null;
        geomagnetic = null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Register listener for sensor when activity runs
    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        manager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_UI);
//        manager.registerListener(this, orient, SensorManager.SENSOR_DELAY_UI);
    }

    // Unregister listener when activity pauses
    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(this);
    }

    // Do low pass filtering process of sensor data
    private void lowPass(float[] oldResult) {
        for(int i = 0; i < 3; i++) {
            result[i] = result[i] + LOW_PASS_ALPHA * (oldResult[i] - result[i]);
        }
    }
}

class UpdateView extends Thread {

}