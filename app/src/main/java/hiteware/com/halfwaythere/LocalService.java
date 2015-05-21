package hiteware.com.halfwaythere;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private float offset = 0;
    private float currentSteps = 0;
    private float softwareSteps = 0;
    private boolean aboveTwo = false;

    private SensorManager sensorManager;
    private Sensor defaultSensor;
    private Sensor accelerometer;

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
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float g = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
            if (g > 2)
            {
                aboveTwo = true;
            }
            else if (aboveTwo)
            {
                aboveTwo = false;
                ++softwareSteps;
            }
        }
        else {currentSteps = event.values[0] + offset;}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onCreate() {
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        defaultSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_UI);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
        offset = prefs.getFloat("offset", 0);
        Toast.makeText(this, "service starting -- offset:"+offset, Toast.LENGTH_LONG).show();
        softwareSteps = prefs.getFloat("softwareSteps", 0);
    }

    @Override
    public void onDestroy() {
        SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
        prefs.edit().putFloat("softwareSteps", softwareSteps).apply();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "binding", Toast.LENGTH_SHORT).show();
        return mBinder;
    }

    public float getSteps() {
        return currentSteps;
    }

    public float getSoftwareSteps() {
        return softwareSteps;
    }

    public void setSteps(float newSteps) {
        this.offset = newSteps - this.currentSteps + this.offset;
        softwareSteps = newSteps;
        SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
        prefs.edit().putFloat("offset", this.offset).apply();
        prefs.edit().putFloat("softwareSteps", softwareSteps).apply();
    }
}