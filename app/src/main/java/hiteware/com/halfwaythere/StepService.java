package hiteware.com.halfwaythere;

import android.app.Service;
import android.content.Intent;
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
    private float offset = 0;
    private float newStepCount = 0;
    private float lastCount = 0;

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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setSteps(float newSteps)
    {
        SendStepBroadcast(newSteps);
        newStepCount = newSteps;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (newStepCount > 0)
        {
            offset = newStepCount - lastCount;
            newStepCount = 0;
        }
        lastCount = event.values[0];

        SendStepBroadcast(lastCount+offset);
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
