package edu.utdallas.spiritlevel;

/**
 * Get sensor data and display on screen through view drawing
 * Created by Peiyang Shangguan on04/17/2016
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    final float LOW_PASS_ALPHA = (float)0.1;

    // About sensor
    private SensorManager manager;
    private Sensor orientation;
    private float[] result;

    final int ACCE_FILTER_DATA_MIN_TIME = 10;  // Get data every 10ms
    private long lastSaved = System.currentTimeMillis();

    private MySurfaceView mySurfaceView;

    /**
     * Called when creating activity. Do all kinds of initialization
     * Author: Peiyang Shangguan
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        mySurfaceView = new MySurfaceView(this);
        layout.addView(mySurfaceView);


        // Get sensor manager and sensors
        manager=(SensorManager)getSystemService(SENSOR_SERVICE);
        orientation = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        // Initialize data
        result = new float[3];
        for(int i = 0; i < 3; i++) {
            result[i] = 0;
        }
    }

    /**
     * Called when new sensor data available, get data and update view
     * Author: Peiyang Shangguan
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            result = event.values.clone();

            // Get result on period to avoid too frequent getting results
            if ((System.currentTimeMillis() - lastSaved) > ACCE_FILTER_DATA_MIN_TIME) {
                lastSaved = System.currentTimeMillis();

                // Low pass to filter jitters
                lowPass(result);

                int y = (int) result[1]*10; // Pitch
                int x = (int) result[2]*10; // Roll

                // Update view
                mySurfaceView.update(x, y);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    /**
     * Register listener for sensor when activity start and resume
     * Author: Peiyang Shangguan
     */
    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Unregister listener for sensor when activity pause
     * Author: Peiyang Shangguan
     */
    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(this);
    }

    /**
     * Do low pass filtering process of sensor data
     * Author: Peiyang Shangguan
     */
    private void lowPass(float[] oldResult) {
        for(int i = 0; i < 3; i++) {
            result[i] = result[i] + LOW_PASS_ALPHA * (oldResult[i] - result[i]);
        }
    }
}