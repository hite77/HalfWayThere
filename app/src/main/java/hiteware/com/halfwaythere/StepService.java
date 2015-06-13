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
 * Created on 5/14/15.
 */
public class StepService extends Service implements SensorEventListener
{
    public static final String STEPS_OCCURRED = "steps";
    public static final String ACTION_STEPS_OCCURRED = "halfWayThere.stepsOccurred";
    public static final String ACTION_SET_STEPS = "halfWayThere.setSteps";
    public static final String ACTION_REQUEST_STEPS = "halfWayThere.requestSteps";
    private int currentSteps = 0;
    private MyBroadCastReceiver receiver;

    @Inject
    SensorManager sensorManager;

    @Inject
    SoftwareStepCounterInterface softwareStepCounter;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void onCreate()
    {
        super.onCreate();
        ((InjectableApplication)getApplication()).inject(this);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (null != sensor)
        {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        receiver = new MyBroadCastReceiver();
        registerReceiver(receiver, new IntentFilter(ACTION_SET_STEPS));
        registerReceiver(receiver, new IntentFilter(ACTION_REQUEST_STEPS));
        SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
        currentSteps = prefs.getInt("currentSteps", 0);
        SendStepBroadcast(currentSteps);
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
            if (intent.getAction().equals(ACTION_REQUEST_STEPS))
            {
                SendStepBroadcast(currentSteps);
            }
            else if (intent.getAction().equals(ACTION_SET_STEPS))
            {
                currentSteps = intent.getIntExtra(
                        STEPS_OCCURRED, -1);
                SendStepBroadcast(currentSteps);
                softwareStepCounter.SetSteps(currentSteps);
                SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
                prefs.edit().putInt("currentSteps", currentSteps).apply();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        softwareStepCounter.SensorUpdate(event.values);
        currentSteps = softwareStepCounter.GetSteps();
        SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
        prefs.edit().putInt("currentSteps", currentSteps).apply();
        SendStepBroadcast(currentSteps);
   }

    private void SendStepBroadcast(int steps) {
        Intent broadcastSteps = new Intent();
        broadcastSteps.setAction(ACTION_STEPS_OCCURRED);
        broadcastSteps.putExtra(STEPS_OCCURRED, steps);
        this.sendBroadcast(broadcastSteps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
