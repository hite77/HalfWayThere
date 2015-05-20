package hiteware.com.halfwaythere;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
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
    public static String STARTFOREGROUND_ACTION = "hiteware.com.halfwaythere.action.startforeground";
    public static String STOPFOREGROUND_ACTION = "hiteware.com.halfwaythere.action.stopforeground";
    public static String MAIN_ACTION = "hiteware.com.halfwaythere.action.main";
    public static int FOREGROUND_SERVICE = 101;

    public static final String TAG = LocalService.class.getName();
    public static final int SCREEN_OFF_RECEIVER_DELAY = 500;

    private SensorManager mSensorManager = null;

    private PowerManager.WakeLock mWakeLock = null;

    private float stepCounterSteps;
    private float offset;
    private int countOfSteps;
    private int goal;
    private int startValue;
    private boolean setOffset = true;

    private NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    private void updateProgress()
    {
        int percentage = (countOfSteps > goal) ? 100 : Math.round((float)(countOfSteps)/(goal)*100);
        mBuilder.setProgress(100, percentage, false);
        mNotifyManager.notify(FOREGROUND_SERVICE, mBuilder.build());
    }

    /*
     * Register this as a sensor event listener.
     */
    private void registerListeners() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
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
                    registerListeners();
                }
            };

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "StartCommand");

        if (intent.getAction().equals(STARTFOREGROUND_ACTION)) {
            Log.i(TAG, "Received Start Foreground Intent ");
            mWakeLock.acquire();
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            mNotifyManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(this);

            Notification notification = mBuilder
                    .setContentTitle("Step Count")
                    .setTicker("Half Way There")
                    .setContentText("Steps toward Goal")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();
            startForeground(FOREGROUND_SERVICE,
                    notification);
            registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
            registerListeners();
            goal = (int)intent.getFloatExtra("goal", 10000);
            startValue = (int)intent.getFloatExtra("currentSteps", 0);
            countOfSteps = startValue;
            stepCounterSteps = startValue;
        } else if (intent.getAction().equals(
                STOPFOREGROUND_ACTION)) {
            Log.i(TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            unregisterReceiver(mReceiver);
            mWakeLock.release();
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER)
        {
            if (setOffset)
            {
                setOffset = false;
                offset = stepCounterSteps - event.values[0];
            }
            Intent broadcastSteps = new Intent();
            broadcastSteps.setAction("halfWayThere.stepsOccurred");
            stepCounterSteps = event.values[0] + offset;
            broadcastSteps.putExtra("steps", stepCounterSteps);
            this.sendBroadcast(broadcastSteps);
        }
        else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR)
        {
            Intent broadcastSteps = new Intent();
            broadcastSteps.setAction("halfWayThere.stepDetector");
            countOfSteps += event.values.length;
            broadcastSteps.putExtra("steps", countOfSteps);
            this.sendBroadcast(broadcastSteps);
            updateProgress();
        }

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
    }

    @Override
    public void onDestroy() {
        unregisterListener();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}