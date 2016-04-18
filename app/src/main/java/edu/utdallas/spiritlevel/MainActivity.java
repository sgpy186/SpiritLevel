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

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager manager;

    private TextView text;
    private SeekBar perpendicularBar;
    private SeekBar levelBar;

    private float[] result;

    final float LOW_PASS_ALPHA = (float)0.1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView)findViewById(R.id.textView2);
        perpendicularBar = (SeekBar)findViewById(R.id.seekBar);
        levelBar = (SeekBar)findViewById(R.id.seekBar2);
        perpendicularBar.setMax(360);
        levelBar.setMax(360);

        manager=(SensorManager)getSystemService(SENSOR_SERVICE);

        result = new float[3];
        for(int i = 0; i < 3; i++) {
            result[i] = 0;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        lowPass(event.values);
        String XAngle = String.format(Locale.US, "%.2f", result[1]);
        String ZAngle = String.format(Locale.US, "%.2f", result[2]);
        text.setText("Angle around X  : " + XAngle + "\n" + "Angle around Z : " + ZAngle);

        perpendicularBar.setProgress( (int)result[1] + 180 );
        levelBar.setProgress( (int)result[2] + 180 );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 0, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        manager.unregisterListener(this);
    }

    private void lowPass(float[] oldResult) {
        for(int i = 0; i < 3; i++) {
            result[i] = result[i] + LOW_PASS_ALPHA * (oldResult[i] - result[i]);
        }
    }
}
