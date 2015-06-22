package hiteware.com.halfwaythere;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created on 5/14/15.
 */
@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class StepServiceUnitTest {
    private TestInjectableApplication application;
    private SensorManager sensorManager;
    private StepService mStepService;

    @Before
    public void setUp() {
        application = (TestInjectableApplication) RuntimeEnvironment.application;
        application.setMock();
        sensorManager = application.testModule.provideSensorManager();
        mStepService = new StepService();
        mStepService.onStartCommand(new Intent(), 0, 0);
    }

    @Test
    public void GivenStepServiceCreatedThenTheSensorManagerAsksForAccelerometer() {
        verify(sensorManager, times(1)).getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Test
    public void whenAppAndActivityAreConstructedThenSensorManagerRegistersForUpdates() {
        Sensor sensor = mock(Sensor.class);
        when(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)).thenReturn(sensor);
        mStepService.onStartCommand(new Intent(), 0, 0);
        verify(sensorManager, times(1)).registerListener(any(SensorEventListener.class), eq(sensor), eq(SensorManager.SENSOR_DELAY_NORMAL));
    }

    @Test
    public void whenStartedAndNoStepCountThenShouldNotRegisterForUpdates() {
        when(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)).thenReturn(null);
        mStepService.onStartCommand(new Intent(), 0, 0);
        verify(sensorManager, times(0)).registerListener(any(SensorEventListener.class), any(Sensor.class), eq(SensorManager.SENSOR_DELAY_NORMAL));
    }

    @Test
    public void whenStartedThenItIsStartedAsSticky() {
        int returnValue = mStepService.onStartCommand(null, 0, 0);
        assertThat(returnValue, equalTo(Service.START_STICKY));
    }

    @Test
    public void GivenSetStepsIsCalledWhenStepServiceIsRestartedThenStepsComeBackUp()
    {
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_STEPS_OCCURRED));

        int setStepsValue = 10;

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, setStepsValue);

        mStepService.onDestroy();
        mStepService.onStartCommand(new Intent(), 0, 0);

        assertThat(testReceiver.getActualSteps(), equalTo(setStepsValue));
    }

    @Test
    public void GivenServiceIsRunningWhenActivityIsRestartedThenStepsAreRedisplayedFromService()
    {
        int setStepsValue = 13;

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, setStepsValue);

        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView stepValue = (TextView) createdActivity.findViewById(R.id.step_value);
        assertThat(stepValue.getText().toString(), equalTo("13"));
    }

    @Test
    public void GivenServiceGoesThroughTwoCreateDestroyCyclesWhenActivityReadsTheStepsItShouldBeCorrect()
    {
        int setStepsValue = 93;
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, setStepsValue);

        mStepService.onDestroy();
        mStepService.onStartCommand(new Intent(), 0, 0);
        mStepService.onDestroy();
        mStepService.onStartCommand(new Intent(), 0, 0);

        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView stepValue = (TextView) createdActivity.findViewById(R.id.step_value);
        assertThat(stepValue.getText().toString(), equalTo("93"));
    }

    @Test
    public void WhenStepServiceIsDestroyedThenNoReceiversAreRegistered() {
        mStepService.onDestroy();
        assertThat(ShadowApplication.getInstance().getRegisteredReceivers().size(), equalTo(0));
    }

    @Test
    public void WhenGoalsAreSetTheGoalIsOutput() {
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_GOAL_CHANGED));

        int expectedGoal = 15000;
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_GOAL_SET, StepService.GOAL_SET, expectedGoal);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertThat(testReceiver.getActualGoal(), equalTo(expectedGoal));
    }

    @Test
    public void GivenSetGoalIsCalledWhenStepServiceIsRestartedThenGoalComesBackUp()
    {
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_GOAL_CHANGED));

        int goal = 10;

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_GOAL_SET, StepService.GOAL_SET, goal);

        mStepService.onDestroy();
        mStepService.onStartCommand(new Intent(), 0, 0);

        assertThat(testReceiver.getActualGoal(), equalTo(goal));
    }

    @Test
    public void GivenServiceIsRunningWhenActivityIsRestartedThenGoalIsRedisplayedFromService()
    {
        int setGoalValue = 13000;

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_GOAL_SET, StepService.GOAL_SET, setGoalValue);

        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView goalValue = (TextView) createdActivity.findViewById(R.id.goal_value);
        assertThat(goalValue.getText().toString(), equalTo("13000"));
    }

    @Test
    public void GivenServiceGoesThroughTwoCreateDestroyCyclesWhenActivityReadsTheGoalItShouldBeCorrect()
    {
        int setGoal = 93;
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_GOAL_SET, StepService.GOAL_SET, setGoal);

        mStepService.onDestroy();
        mStepService = null;
        mStepService = new StepService();
        mStepService.onStartCommand(new Intent(), 0, 0);
        mStepService.onDestroy();
        mStepService = null;
        mStepService = new StepService();
        mStepService.onStartCommand(new Intent(), 0, 0);

        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView goalValue = (TextView) createdActivity.findViewById(R.id.goal_value);
        assertThat(goalValue.getText().toString(), equalTo("93"));
    }

    @Test
    public void GivenOnSensorEventOccursWhenThisDoesNotChangeTheStepCountThenBroadcastDoesNotOccur()
    {
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_STEPS_OCCURRED));

        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{0, 0, 0}));

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertThat(testReceiver.getActualSteps(), equalTo(-1));
    }

    //TODO: can set half way steps -- once steps are past half way then vibrate happens
    @Test
    public void GivenHalfWayHasBeenSetWhenStepsPassHalfWayThenPhoneVibrates()
    {
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, 12);
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, 15);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        float value = SensorValue.CalculateForceToApplyOnEachAxisToGiveGValue((float) 2.01);
        float peak[] = {value, value, value};
        float lowValue[] = {0, 0, 0};

        Vibrator vibrator = application.testModule.provideVibrator();

        verify(vibrator, times(0)).vibrate(anyInt());

        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(lowValue));
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(peak));
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(lowValue));

        verify(vibrator, times(0)).vibrate(anyInt());

        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(lowValue));
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(peak));
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(lowValue));

        verify(vibrator).vibrate(2000);
    }

    @Test
    public void GivenHalfWayHasBeenSetWhenStepsPassHalfWayThenPhoneVibratesOnceAndNotForEveryStep() {
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, 14);
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, 15);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        float value = SensorValue.CalculateForceToApplyOnEachAxisToGiveGValue((float) 2.01);
        float peak[] = {value, value, value};
        float lowValue[] = {0, 0, 0};

        Vibrator vibrator = application.testModule.provideVibrator();

        verify(vibrator, times(0)).vibrate(anyInt());

        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(lowValue));
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(peak));
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(lowValue));

        verify(vibrator).vibrate(2000);

        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(lowValue));
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(peak));
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(lowValue));

        verify(vibrator, times(1)).vibrate(anyInt());
    }
    //TODO: once vibrate for half way then should not vibrate on next step...
    //TODO: on start emit a broadcast of the half way if half way is set....
//    @Test
//    public void GivenHalfWayIsSetWhenStepServiceIsRestartedThenHalfWayIsRebroadcast()
//    {
//        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
//
//        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
//        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_HALF_WAY_SET));
//
//
//    }

    //TODO: goal changed, steps set, clear half way signal to fragment for it to clear the half way
    //TODO: once half way is cleared, if it is restarted, then broadcast does not happen.

    //TODO: steps happen, need to notify on goal achieved.
}