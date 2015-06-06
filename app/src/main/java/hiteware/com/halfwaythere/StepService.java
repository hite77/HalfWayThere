package hiteware.com.halfwaythere;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import javax.inject.Inject;

/**
 * Created by jasonhite on 5/14/15.
 */
public class StepService extends Service implements SensorEventListener
{
    public static String STEPS_OCCURRED = "steps";
    public static String ACTION_STEPS_OCCURRED = "halfWayThere.stepsOccurred";
    public static String ACTION_SET_STEPS = "halfWayThere.setSteps";
    private float offset = 0;
    private float newStepCount = 0;
    private float lastCount = 0;
    private MyBroadCastReceiver receiver;

    @Inject
    SensorManager sensorManager;

    public void onCreate()
    {
        ((InjectableApplication)getApplication()).inject(this);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (null != sensor)
        {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }

        receiver = new MyBroadCastReceiver();
        registerReceiver(receiver, new IntentFilter(ACTION_SET_STEPS));
        SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
        newStepCount = prefs.getFloat("newStepCount", 0);
        SendStepBroadcast(newStepCount);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
        SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
        prefs.edit().putFloat("newStepCount", newStepCount).apply();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            float steps = intent.getFloatExtra(
                    STEPS_OCCURRED, 0);
            SendStepBroadcast(steps);
            newStepCount = steps;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (newStepCount > 0)
        {
            calculateOffsetForNewStepCount(event);
        }
        lastCount = event.values[0];

        SendStepBroadcast(lastCount+offset);
    }

    private void calculateOffsetForNewStepCount(SensorEvent event) {
        if (lastCount > 0)
        {
            offset = newStepCount - lastCount;
        }
        else
        {
            offset = newStepCount + 1 - event.values[0];
        }
        newStepCount = 0;
    }

    private void SendStepBroadcast(float steps) {
        Intent broadcastSteps = new Intent();
        broadcastSteps.setAction(ACTION_STEPS_OCCURRED);
        broadcastSteps.putExtra(STEPS_OCCURRED, steps);
        this.sendBroadcast(broadcastSteps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
