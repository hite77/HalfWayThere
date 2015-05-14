package hiteware.com.halfwaythere;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by jasonhite on 5/12/15.
 */

// should tie into step counter
// should have public method for steps
// how to call for steps?  or to send step count updates?
// perhaps call on public methods within the class of the app?
// but only if running -- set a bool during onresume, and stop during onPause


public class LocalService  extends Service implements SensorEventListener{
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    private boolean calculateOffset = false;
    private float offset = 0;
    private float initialSteps = 0;
    private float currentSteps = 0;
    private counterListener mCounterListener;
    private float totalCount = 0;

    private SensorManager sensorManager;
    private Sensor defaultSensor;
    private Sensor countSensor;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocalService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocalService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (calculateOffset)
        {
            calculateOffset = false;
            offset = initialSteps - event.values[0];
        }
        currentSteps = event.values[0] + offset;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class counterListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            totalCount = totalCount + 1;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        defaultSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mCounterListener = new counterListener();
        sensorManager.registerListener(mCounterListener, countSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }


    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "binding", Toast.LENGTH_SHORT).show();
        return mBinder;
    }

    public float getSteps() {
        return currentSteps;
    }

    public float getTotalSteps() {
        return totalCount;
    }

    public void setSteps(float currentSteps) {
        this.initialSteps = currentSteps;
        this.calculateOffset = true;
        this.totalCount = currentSteps;
    }
}