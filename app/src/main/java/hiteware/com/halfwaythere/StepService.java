package hiteware.com.halfwaythere;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.TextView;

import javax.inject.Inject;

/**
 * Created by jasonhite on 5/14/15.
 */
public class StepService extends Service implements SensorEventListener
{
//    private final IBinder mBinder = new LocalBinder();

//    public class LocalBinder extends Binder {
//        StepService getService() {
//            // Return this instance of LocalService so clients can call public methods
//            return StepService.this;
//        }
//   }

//    @Override
//    public IBinder onBind(Intent intent) {
//        Toast.makeText(this, "binding", Toast.LENGTH_SHORT).show();
//        return mBinder;
//    }
//
    private TextView OutputView;

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

    }

    public float getSteps()
    {
        return 14;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (null != OutputView)
        {
            OutputView.setText(String.format("%.0f", event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setOutputView(TextView outputView)
    {
        OutputView = outputView;
    }

//    public void killService()
//    {
//        stopSelf();
//    }
}
