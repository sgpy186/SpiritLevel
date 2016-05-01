package edu.utdallas.spiritlevel;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

/*
 * Get sensor data and display on screen through view drawing
 * 04/17/2016
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private int AVG_COUNT = 10;
    private float LOW_PASS_ALPHA = (float)0.01;

    // About sensor
    private SensorManager manager;
    private Sensor accelerometer;
    private Sensor magnetic;
    private Sensor orientation;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private boolean haveGrav = false;
    private boolean haveMag = false;
    private float[] result;

    static int ACCE_FILTER_DATA_MIN_TIME = 10; // 1000ms
    long lastSaved = System.currentTimeMillis();

    private MySurfaceView mySurfaceView;

    static int x = 0;
    static int y = 0;

    // Views
    private TextView text;
    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private SeekBar seekBar3;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        text = (TextView)findViewById(R.id.textView2);
        seekBar1 = (SeekBar)findViewById(R.id.seekBar);
        seekBar2 = (SeekBar)findViewById(R.id.seekBar2);
        seekBar3 = (SeekBar)findViewById(R.id.seekBar3);
        layout = (RelativeLayout) findViewById(R.id.layout);
        mySurfaceView = new MySurfaceView(this);

        layout.addView(mySurfaceView);

        seekBar1.setMax(360);
        seekBar2.setMax(360);
        seekBar3.setMax(360);

        // Get sensor manager and sensors
        manager=(SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetic = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        orientation = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        //findViewById(R.id.btnStart).setOnClickListener(this);
        //findViewById(R.id.btnStop).setOnClickListener(this);

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
                haveGrav = true;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values.clone();
                haveMag = true;
                break;
            case Sensor.TYPE_ORIENTATION:
                result = event.values.clone();
                haveGrav = true;
                haveMag = true;
                break;
        }

        if (haveMag && haveGrav) {
            if ((System.currentTimeMillis() - lastSaved) > ACCE_FILTER_DATA_MIN_TIME) {
                lastSaved = System.currentTimeMillis();

                haveGrav = false;
                haveMag = false;
//                float[] RMatrix = new float[9];
//                SensorManager.getRotationMatrix(RMatrix, null, gravity, geomagnetic);
//                SensorManager.getOrientation(RMatrix, result);

                lowPass(result);

                // Display result on screen
//                result[0] = (float) Math.toDegrees(result[0]);
//                result[1] = (float) Math.toDegrees(result[1]);
//                result[2] = (float) Math.toDegrees(result[2]);
                String XAngle = String.format(Locale.US, "%.1f", result[1]);
                String YAngle = String.format(Locale.US, "%.1f", result[0]);
                String ZAngle = String.format(Locale.US, "%.1f", result[2]);

                XAngle = XAngle.replace(".", "");
                ZAngle = ZAngle.replace(".", "");
                x = Integer.parseInt(XAngle);
                y = Integer.parseInt(ZAngle);

                String str = "Angle around X : " + XAngle + "\nAngle around Z : " + ZAngle
                        + "\nAngle around Y : " + YAngle;
                text.setText(str);
                seekBar1.setProgress((int) result[1] + 180);
                seekBar2.setProgress((int) result[2] + 180);
                seekBar3.setProgress((int) result[0] + 180);

                mySurfaceView.update(y,x);
                //text.setText("Angle around X  : " + XAngle + "\n" + "Angle around Z : " + ZAngle);
                //perpendicularBar.setProgress( (int)(Float.parseFloat(XAngle)) + 180 );
                //levelBar.setProgress( (int)(Float.parseFloat(ZAngle)) + 180 );
            }
        }

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
        manager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_UI);
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

    private float[] getAvg(float[][] data) {
        float[] result = new float[3];
        for (int i = 0; i < 3; i++)
            result[i] = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < AVG_COUNT; j++) {
                result[i] += data[j][i];
            }
            result[i] = result[i] / AVG_COUNT;
        }
        return result;
    }
}