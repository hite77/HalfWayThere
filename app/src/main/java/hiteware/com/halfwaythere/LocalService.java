package hiteware.com.halfwaythere;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;

/**
 * Created by jasonhite on 5/12/15.
 */

// should tie into step counter
// should have public method for steps
// how to call for steps?  or to send step count updates?
// perhaps call on public methods within the class of the app?
// but only if running -- set a bool during onresume, and stop during onPause


public class LocalService  extends Service implements SensorEventListener {
    public static final String TAG = LocalService.class.getName();
    public static final int SCREEN_OFF_RECEIVER_DELAY = 500;

    private SensorManager mSensorManager = null;

    private PowerManager.WakeLock mWakeLock = null;

    /*
     * Register this as a sensor event listener.
     */
    private void registerListener() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
     * Un-register this as a sensor event listener.
     */
    private void unregisterListener() {
        mSensorManager.unregisterListener(this);
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive(" + intent + ")");

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }

            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i(TAG, "Runnable executing.");
                    unregisterListener();
                    registerListener();
                }
            };

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "StartCommand");

        startForeground(Process.myPid(), new Notification());
        registerListener();
        mWakeLock.acquire();

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Intent broadcastSteps = new Intent();
        broadcastSteps.setAction("halfWayThere.stepsOccurred");
        broadcastSteps.putExtra("steps", event.values[0]);
        this.sendBroadcast(broadcastSteps);
        Log.i(TAG, "Event:" + event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        PowerManager manager =
                (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterListener();
        mWakeLock.release();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}