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
    public static String ACTION_REQUEST_STEPS = "halfWayThere.requestSteps";
    private float offset = 0;
    private float currentSteps = 0;
    private float setSteps = 0;
    private MyBroadCastReceiver receiver;

    @Inject
    SensorManager sensorManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void onCreate()
    {
        super.onCreate();
        ((InjectableApplication)getApplication()).inject(this);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (null != sensor)
        {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }

        receiver = new MyBroadCastReceiver();
        registerReceiver(receiver, new IntentFilter(ACTION_SET_STEPS));
        registerReceiver(receiver, new IntentFilter(ACTION_REQUEST_STEPS));
        SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
        offset = prefs.getFloat("offset", 0);
        SendStepBroadcast(prefs.getFloat("currentSteps", 0));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ACTION_REQUEST_STEPS)
            {
                SendStepBroadcast(currentSteps);
            }
            else if (intent.getAction() == ACTION_SET_STEPS)
            {
                setSteps = intent.getFloatExtra(
                        STEPS_OCCURRED, 0);
                SendStepBroadcast(setSteps);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (setSteps > 0) {
            offset = setSteps - event.values[0] + 1;
            SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
            prefs.edit().putFloat("offset", offset).apply();
            setSteps = 0;
        }
        currentSteps = event.values[0] + offset;
        SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
        prefs.edit().putFloat("currentSteps", currentSteps).apply();
        SendStepBroadcast(currentSteps);
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
