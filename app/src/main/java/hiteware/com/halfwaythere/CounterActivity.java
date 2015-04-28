package hiteware.com.halfwaythere;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jasonhite on 4/27/15.
 */
public class CounterActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView count;
    boolean activityRunning;
    private boolean firstRun = true;
    float offset = 0;
    private boolean halfWay = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        count = (TextView) findViewById(R.id.count);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        vibrate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (firstRun)
        {
            firstRun = false;
            // hard code count to 7768 -- this calculates offset to make it be 7768 no mater what value it starts as.
            offset = 7768 - event.values[0];
        }
        if (activityRunning) {
            count.setText(String.valueOf(event.values[0]+offset));
        }

        // important that this still takes place even if paused.  I locked the screen but it worked.
        if ((event.values[0]+offset > 8884) &&(!halfWay))  // if they are at half way point vibrate
        {
            halfWay = true;
            vibrate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void vibrate() {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(2000);
    }
}