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
import android.os.Vibrator;

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
    public static final String ACTION_GOAL_SET = "halfWayThere.goalSet";
    public static final String GOAL_SET = "goal.set";
    public static final String ACTION_GOAL_REQUEST = "halfWayThere.requestGoal";
    public static final String ACTION_GOAL_CHANGED = "halfWayThere.changed";
    public static final String ACTION_HALF_WAY_SET = "halfWayThere.halfWaySet";
    public static final String ACTION_CLEAR_HALF_WAY = "halfWayThere.clearHalfWay";
    public static final String HALF_WAY_VALUE = "halfWayValue";
    private int currentSteps = 0;
    private int goal = 0;
    private int halfWay;
    private boolean oneShotHalfWay = false;

    private MyBroadCastReceiver receiver;

    @Inject
    SensorManager sensorManager;

    @Inject
    Vibrator vibrator;

    private final SoftwareStepCounterInterface softwareStepCounter = new SoftwareStepCounter();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        ((InjectableApplication)getApplication()).inject(this);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (null != sensor)
        {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        receiver = new MyBroadCastReceiver();
        registerReceiver(receiver, new IntentFilter(ACTION_SET_STEPS));
        registerReceiver(receiver, new IntentFilter(ACTION_REQUEST_STEPS));
        registerReceiver(receiver, new IntentFilter(ACTION_GOAL_SET));
        registerReceiver(receiver, new IntentFilter(ACTION_GOAL_REQUEST));
        registerReceiver(receiver, new IntentFilter(ACTION_HALF_WAY_SET));

        SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);

        currentSteps = prefs.getInt("currentSteps", 0);
        BroadcastHelper.sendBroadcast(this, ACTION_STEPS_OCCURRED, STEPS_OCCURRED, currentSteps);
        softwareStepCounter.SetSteps(currentSteps);

        goal = prefs.getInt("goal", 0);
        BroadcastHelper.sendBroadcast(this, ACTION_GOAL_CHANGED, GOAL_SET, goal);

        return START_STICKY;
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
                BroadcastHelper.sendBroadcast(getApplication(), ACTION_STEPS_OCCURRED, STEPS_OCCURRED, currentSteps);
            }
            else if (intent.getAction().equals(ACTION_GOAL_REQUEST))
            {
                BroadcastHelper.sendBroadcast(getApplication(), ACTION_GOAL_CHANGED, GOAL_SET, goal);
            }
            else if (intent.getAction().equals(ACTION_SET_STEPS))
            {
                currentSteps = intent.getIntExtra(
                        STEPS_OCCURRED, -1);
                BroadcastHelper.sendBroadcast(getApplication(), ACTION_STEPS_OCCURRED, STEPS_OCCURRED, currentSteps);
                softwareStepCounter.SetSteps(currentSteps);
                SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
                prefs.edit().putInt("currentSteps", currentSteps).apply();
            }
            else if (intent.getAction().equals(ACTION_GOAL_SET))
            {
                goal = intent.getIntExtra(GOAL_SET, -1);
                BroadcastHelper.sendBroadcast(getApplication(),ACTION_GOAL_CHANGED,GOAL_SET,goal);
                SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
                prefs.edit().putInt("goal", goal).apply();
            }
            else if (intent.getAction().equals(ACTION_HALF_WAY_SET))
            {
                halfWay = intent.getIntExtra(HALF_WAY_VALUE, -1);
                oneShotHalfWay = true;
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        softwareStepCounter.SensorUpdate(event.values);
        if (softwareStepCounter.GetSteps() != currentSteps) {
            currentSteps = softwareStepCounter.GetSteps();
            SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
            prefs.edit().putInt("currentSteps", currentSteps).apply();
            BroadcastHelper.sendBroadcast(this, ACTION_STEPS_OCCURRED, STEPS_OCCURRED, currentSteps);
            if (oneShotHalfWay && (currentSteps > halfWay)) {
                vibrator.vibrate(2000);
                oneShotHalfWay = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
