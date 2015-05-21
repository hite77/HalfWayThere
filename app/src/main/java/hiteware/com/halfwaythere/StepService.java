package hiteware.com.halfwaythere;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;

import javax.inject.Inject;

/**
 * Created by jasonhite on 5/14/15.
 */
public class StepService extends Service
{
    @Inject
    SensorManager sensorManager;

    public void onCreate()
    {
        ((InjectableApplication)getApplication()).inject(this);
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setSteps(float newSteps)
    {

    }

    public float getSteps()
    {
        return 14;
    }
}
