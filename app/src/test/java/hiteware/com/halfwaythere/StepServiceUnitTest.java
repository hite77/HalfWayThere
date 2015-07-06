package hiteware.com.halfwaythere;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
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
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;

import java.util.Calendar;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created on 5/14/15.
 */
@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class StepServiceUnitTest {
    private TestInjectableApplication application;
    private SensorManager sensorManager;
    private StepService mStepService;

    private final float value = SensorValue.CalculateForceToApplyOnEachAxisToGiveGValue((float) 2.01);
    private final float[] peak = {value, value, value};
    private final float[] lowValue = {0, 0, 0};

    private void generateStep() {
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(lowValue));
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(peak));
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(lowValue));
    }

    @Before
    public void setUp() {
        application = (TestInjectableApplication) RuntimeEnvironment.application;

        application.setMock();
        sensorManager = application.testModule.provideSensorManager();
        mStepService = Robolectric.buildService(StepService.class).create().startCommand(0,0).get();
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
        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();

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

        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();
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

        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();
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
        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();

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
        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();

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

        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();
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

        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView goalValue = (TextView) createdActivity.findViewById(R.id.goal_value);
        assertThat(goalValue.getText().toString(), equalTo("93"));
    }

    @Test
    public void GivenOnSensorEventOccursWhenThisDoesNotChangeTheStepCountThenBroadcastDoesNotOccur()
    {
        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_STEPS_OCCURRED));

        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{0, 0, 0}));

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertThat(testReceiver.getActualSteps(), equalTo(-1));
    }

    @Test
    public void GivenHalfWayHasBeenSetWhenStepsPassHalfWayThenPhoneVibrates()
    {
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, 12);
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, 15);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        Vibrator vibrator = application.testModule.provideVibrator();

        verify(vibrator, times(0)).vibrate(anyInt());

        generateStep();

        verify(vibrator, times(0)).vibrate(anyInt());

        generateStep();

        verify(vibrator).vibrate(2000);
    }

    @Test
    public void GivenHalfWayHasBeenSetWhenStepsPassHalfWayThenPhoneVibratesOnceAndNotForEveryStep() {
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, 14);
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, 15);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        Vibrator vibrator = application.testModule.provideVibrator();

        verify(vibrator, times(0)).vibrate(anyInt());

        generateStep();

        verify(vibrator).vibrate(2000);

        generateStep();

        verify(vibrator, times(1)).vibrate(anyInt());
    }

    @Test
    public void GivenHalfWayHasBeenSetWhenStepsPassHalfWayThenNotificationHappens() {
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, 14);
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, 15);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        generateStep();

        NotificationManager notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
        ShadowNotificationManager shadowNotificationManager = shadowOf(notificationManager);
        assertThat(shadowNotificationManager.getAllNotifications().size(), equalTo(1));

        Notification notification = shadowNotificationManager.getNotification(1);
        assertNotNull(notification);
        assertThat(notification.vibrate, equalTo(new long[]{1000, 1000, 1000, 1000, 1000, 1000}));
        assertThat(notification.defaults, equalTo(Notification.DEFAULT_ALL));

        ShadowNotification shadowNotification = shadowOf(notification);
        assertNotNull(shadowNotification);
        assertThat(shadowNotification.getContentText().toString(), equalTo("You Are halfWayThere"));
        assertThat(shadowNotification.getContentTitle().toString(), equalTo("HalfWayThere"));
    }

    @Test
    public void GivenHalfWayIsSetWhenStepServiceIsRestartedThenHalfWayIsRebroadcast()
    {
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, 15);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();
        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_HALF_WAY_SET));

        mStepService.onDestroy();
        mStepService = null;
        mStepService = new StepService();
        mStepService.onStartCommand(new Intent(), 0, 0);

        assertThat(testReceiver.getActualHalfWay(), equalTo(15));
    }

    @Test
    public void GivenHalfWayIsSetWhenGoalsAreChangedThenHalfWayClearedIsBroadcast()
    {
        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_CLEAR_HALF_WAY));

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, 15);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_GOAL_SET, StepService.GOAL_SET, 10000);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertThat(testReceiver.getClearedHalfWay(), equalTo(true));
    }

    @Test
    public void GivenHalfWayIsSetWhenStepsAreChangedThenHalfWayClearedIsBroadcast()
    {
        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_CLEAR_HALF_WAY));

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, 15);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, 14);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertThat(testReceiver.getClearedHalfWay(), equalTo(true));
    }

    @Test
    public void GivenHalfWayIsClearedBySettingGoalWhenServiceIsRestartedThenHalfWaySetIsNotBroadcast()
    {
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, 15);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_GOAL_SET, StepService.GOAL_SET, 10000);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_HALF_WAY_SET));

        mStepService.onDestroy();
        mStepService = null;
        mStepService = new StepService();
        mStepService.onStartCommand(new Intent(), 0, 0);

        assertThat(testReceiver.getActualHalfWay(), equalTo(-1));
    }

    @Test
    public void GivenHalfWayIsClearedBySettingStepsWhenServiceIsRestartedThenHalfWaySetIsNotBroadcast()
    {
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, 15);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, 14);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_HALF_WAY_SET));

        mStepService.onDestroy();
        mStepService = null;
        mStepService = new StepService();
        mStepService.onStartCommand(new Intent(), 0, 0);

        assertThat(testReceiver.getActualHalfWay(), equalTo(-1));
    }

    @Test
    public void GivenStepsAreCloseToHalfWayWhenHalfWayIsClearedByStepsBeingSetAndStepOccursThenNotificationIsNotSent()
    {
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, 14);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, 15);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, 14);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        generateStep();

        NotificationManager notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
        ShadowNotificationManager shadowNotificationManager = shadowOf(notificationManager);
        assertThat(shadowNotificationManager.getAllNotifications().size(), equalTo(0));
    }

    @Test
    public void GivenStepsAreCloseToHalfWayWhenHalfWayIsClearedByGoalSetAndStepOccursThenNotificationIsNotSent()
    {
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, 14);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, 15);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_GOAL_SET, StepService.GOAL_SET, 20);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        generateStep();

        NotificationManager notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
        ShadowNotificationManager shadowNotificationManager = shadowOf(notificationManager);
        assertThat(shadowNotificationManager.getAllNotifications().size(), equalTo(0));
    }

    @Test
    public void GivenStepsAreOverHalfWayWhenStepServiceIsRestartedAndStepHappensThenVibrateDoesNotHappenAgain()
    {
        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, 14);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, 15);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        generateStep();

        Vibrator vibrator = application.testModule.provideVibrator();

        verify(vibrator, times(1)).vibrate(anyInt());

        mStepService.onDestroy();
        mStepService = null;

        mStepService = new StepService();
        mStepService.onStartCommand(new Intent(), 0, 0);

        generateStep();

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(vibrator, times(1)).vibrate(anyInt());
    }

    // todo: test for setting the date each time the sensor is updated.
    // Date now = new Date();
    // calendar.setTime(now);

    // todo: each time the date is set, it is a different date that is set.

    @Test
    public void GivenStepsAreSetToNonZeroWhenDayIsDifferentBetweenSensorEventsThenStepsAreResetAndAStepHappeningWillOutputTwo()
    {
        Calendar date = application.testModule.provideCalendar();

        BroadcastHelper.sendBroadcast(application, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, 14);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        when(date.get(Calendar.DAY_OF_MONTH)).thenReturn(1);
        generateStep();

        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_STEPS_OCCURRED));

        when(date.get(Calendar.DAY_OF_MONTH)).thenReturn(2);
        generateStep();

        assertThat(testReceiver.getActualSteps(), equalTo(2));
    }

    // todo: test for setting the day of the month, and shutting down, and make sure it still wipes to zero for the day of the month.
    // should set the initial day to what was stored, or -1.
}