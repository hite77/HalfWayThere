package hiteware.com.halfwaythere;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
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

    private SensorManager mSensorManager = null;

    private float stepCounterSteps;
    private float offset;
    private int goal;
    private boolean setOffset = true;

    private NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    private void updateProgress()
    {
        int percentage = (stepCounterSteps > goal) ? 100 : Math.round(stepCounterSteps/goal*100);
        mBuilder.setProgress(100, percentage, false);
        mNotifyManager.notify(FOREGROUND_SERVICE, mBuilder.build());
    }

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "StartCommand");

        if (intent.getAction().equals(STARTFOREGROUND_ACTION)) {
            Log.i(TAG, "Received Start Foreground Intent ");
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
            registerListener();
            goal = (int)intent.getFloatExtra("goal", 10000);
            stepCounterSteps = (int)intent.getFloatExtra("currentSteps", 0);
        } else if (intent.getAction().equals(
                STOPFOREGROUND_ACTION)) {
            Log.i(TAG, "Received Stop Foreground Intent");
            unregisterListener();
            stopForeground(true);
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}