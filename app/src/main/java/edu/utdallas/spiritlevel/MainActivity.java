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

    static int ACCE_FILTER_DATA_MIN_TIME = 50; // 1000ms
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

        mySurfaceView.getLayoutParams();
        layout.addView(mySurfaceView);

        seekBar1.setMax(360);
        seekBar2.setMax(360);
        seekBar3.setMax(360);

        // Get sensor manager and sensors
        manager=(SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetic = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        orientation = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

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
//                float[] inR = new float[9];
//                float[] outR = new float[9];
//                boolean success = SensorManager.getRotationMatrix(inR, null, gravity, geomagnetic);
//                if (success){
//                    switch (((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRotation()) {
////                        case Surface.ROTATION_90:
////                            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
////                            break;
////                        case Surface.ROTATION_180:
////                            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, outR);
////                            break;
////                        case Surface.ROTATION_270:
////                            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, outR);
////                            break;
//                        default:case Surface.ROTATION_0:
//                            outR = inR;
//                            break;
//                    }
//                    SensorManager.getOrientation(outR, result);
//                }
                //SensorManager.getRotationMatrix(RMatrix, null, gravity, geomagnetic);

                lowPass(result);

                // Display result on screen
//                result[0] = (float) Math.toDegrees(result[0]);
//                result[1] = (float) Math.toDegrees(result[1]);
//                result[2] = (float) Math.toDegrees(result[2]);

//                float azimuth = result[0];
//                float pitch = result[1];
//                float roll = result[2];
//                if (pitch < -45 && pitch > -135) {
//                    text.setText("Top side of the phone is Up!");
//
//                } else if (pitch > 45 && pitch < 135) {
//
//                    text.setText("Bottom side of the phone is Up!");
//
//                } else if (roll > 45) {
//
//                    text.setText("Right side of the phone is Up!");
//
//                } else if (roll < -45) {
//
//                    text.setText("Left side of the phone is Up!");
//                }
                String XAngle = String.format(Locale.US, "%.1f", result[1]);
                String YAngle = String.format(Locale.US, "%.1f", result[0]);
                String ZAngle = String.format(Locale.US, "%.1f", result[2]);

                XAngle = XAngle.replace(".", "");
                ZAngle = ZAngle.replace(".", "");
                x = Integer.parseInt(XAngle);
                y = Integer.parseInt(ZAngle);

                String str = "Result[1]: " + XAngle + "\nResult[2] " + ZAngle
                        + "\nResult[0]: " + YAngle;
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