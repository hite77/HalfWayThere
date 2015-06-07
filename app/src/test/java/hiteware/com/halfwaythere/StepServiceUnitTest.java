package hiteware.com.halfwaythere;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowLooper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
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
    public TestInjectableApplication application;
    SensorManager sensorManager;
    StepService mStepService;

    class StepServiceUnitTestReceiver extends BroadcastReceiver {
        private StepService stepService;
        private float actual = -1;

        public StepServiceUnitTestReceiver(StepService stepService)
        {
            this.stepService = stepService;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ShadowIntent shadowIntent = Shadows.shadowOf(intent);
            assertThat(shadowIntent
                            .hasExtra(StepService.STEPS_OCCURRED),
                    equalTo(true));
            actual = shadowIntent.getFloatExtra(
                    StepService.STEPS_OCCURRED, 0);
        }

        public float getActualResult() {
            return actual;
        }
    }

    @Before
    public void setUp() {
        application = (TestInjectableApplication) RuntimeEnvironment.application;
        application.setMockSensorManager();
        sensorManager = application.testModule.provideSensorManager();
        mStepService = new StepService();
        mStepService.onCreate();
    }

    public void SetSteps(float value) {
        Intent broadcastSteps = new Intent();
        broadcastSteps.setAction(StepService.ACTION_SET_STEPS);
        broadcastSteps.putExtra(StepService.STEPS_OCCURRED, value);
        application.sendBroadcast(broadcastSteps);
    }

    @Test
    public void GivenStepServiceCreatedWhenStepsAreSetThenStepsAreEmitted() {
        float expectedValue = 14;

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver(mStepService);
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_STEPS_OCCURRED));

        SetSteps(expectedValue);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertThat(testReceiver.getActualResult(), equalTo(expectedValue));
    }

    @Test
    public void GivenStepServiceCreatedThenTheSensorManagerAsksForStepCounter() {
        verify(sensorManager, times(1)).getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    @Test
    public void whenAppAndActivityAreConstructedThenSensorManagerRegistersForUpdates() {
        Sensor sensor = mock(Sensor.class);
        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(sensor);
        mStepService.onCreate();
        verify(sensorManager, times(1)).registerListener(any(SensorEventListener.class), eq(sensor), eq(SensorManager.SENSOR_DELAY_NORMAL));
    }

    @Test
    public void whenStartedAndNoStepCountThenShouldNotRegisterForUpdates() {
        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(null);
        mStepService.onCreate();
        verify(sensorManager, times(0)).registerListener(any(SensorEventListener.class), any(Sensor.class), eq(SensorManager.SENSOR_DELAY_NORMAL));
    }

    @Test
    public void whenStartedThenItIsStartedAsSticky() {
        int returnValue = mStepService.onStartCommand(null, 0, 0);
        assertThat(returnValue, equalTo(Service.START_STICKY));
    }

    @Test
    public void whenStepEventComesOverThenServiceBroadCastsTheCountOfSteps() {
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        float expected = 33;
        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver(mStepService);

        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_STEPS_OCCURRED));

        SensorEvent stepEvent = SensorValue.CreateSensorEvent(expected);
        mStepService.onSensorChanged(stepEvent);

        assertThat(testReceiver.getActualResult(), equalTo(expected));
    }

    @Test
    public void whenSetStepsIsSetAndAnotherStepEventOccursThenStepsAreOffsetCorrectly()
    {
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver(mStepService);
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_STEPS_OCCURRED));

        float counterValue = 85;

        SensorEvent stepEvent = SensorValue.CreateSensorEvent(counterValue);
        mStepService.onSensorChanged(stepEvent);

        float expected = 26;

        SetSteps(expected - 1);
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(counterValue+1));

        assertThat(testReceiver.getActualResult(), equalTo(expected));
    }

    @Test
    public void GivenStepsHaveOccurredWhenYouSetStepsToAValueAndAStepHappensThenStepCountIsOneGreater()
    {
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver(mStepService);
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_STEPS_OCCURRED));

        float valueWithoutSetSteps = 99;

        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(valueWithoutSetSteps));
        assertThat(testReceiver.getActualResult(), equalTo(valueWithoutSetSteps));

        float setStepsValue = 10;
        float addedSteps = 1;

        SetSteps(setStepsValue);
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(valueWithoutSetSteps+addedSteps));

        assertThat(testReceiver.getActualResult(), equalTo(setStepsValue+addedSteps));
    }

    @Test
    public void GivenSetStepsIsCalledWithoutAnyStepEventsWhenAStepComesInThenValueIsEqualToTheStepsWeSet()
    {
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver(mStepService);
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_STEPS_OCCURRED));

        float setStepsValue = 10;

        SetSteps(setStepsValue);

        float anyValue = 109;

        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(anyValue));

        assertThat(testReceiver.getActualResult(), equalTo(setStepsValue + 1));
    }

    @Test
    public void GivenSetStepsIsCalledWhenStepServiceIsRestartedThenStepsComeBackUp()
    {
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver(mStepService);
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_STEPS_OCCURRED));

        float setStepsValue = 10;

        SetSteps(setStepsValue);
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(setStepsValue));

        mStepService.onDestroy();
        mStepService.onCreate();

        assertThat(testReceiver.getActualResult(), equalTo(setStepsValue+1));
    }

    @Test
    public void GivenSetStepsIsCalledAndServiceIsReconstructedThenAStepEventOccursThenTheCountShouldBeTwoMoreThanTheSetValue()
    {
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver(mStepService);
        createdActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_STEPS_OCCURRED));

        float setStepsValue = 13;

        SetSteps(setStepsValue);

        float anyValue = 145;
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(anyValue));

        mStepService.onDestroy();
        mStepService = null;
        mStepService = new StepService();
        mStepService.onCreate();

        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(anyValue+1));

        assertThat(testReceiver.getActualResult(), equalTo(setStepsValue + 2));
    }

    @Test
    public void GivenServiceIsRunningWhenActivityIsRestartedThenStepsAreRedisplayedFromService()
    {
        float setStepsValue = 13;

        SetSteps(setStepsValue);
        float arbitraryValue = 643;
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(arbitraryValue));

        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView stepValue = (TextView) createdActivity.findViewById(R.id.step_value);
        assertThat(stepValue.getText().toString(), equalTo("14"));
    }

    @Test
    public void GivenServiceIsRunningAndStepsAreSetAndStepEventHappensTwiceWhenActivityIsRestartedThenStepsAreRedisplayedFromServiceAndIsIncremented()
    {
        float setStepsValue = 15;

        SetSteps(setStepsValue);
        float anyValue = 104;
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(anyValue));
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(anyValue + 1));

        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView stepValue = (TextView) createdActivity.findViewById(R.id.step_value);
        assertThat(stepValue.getText().toString(), equalTo("17"));
    }

    @Test
    public void GivenServiceGoesThroughTwoCreateDestroyCyclesWhenActivityReadsTheStepsItShouldBeCorrect()
    {
        float setStepsValue = 93;
        SetSteps(setStepsValue);
        float counterValue = 545;
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(counterValue));

        mStepService.onDestroy();
        mStepService.onCreate();
        mStepService.onDestroy();
        mStepService.onCreate();

        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView stepValue = (TextView) createdActivity.findViewById(R.id.step_value);
        assertThat(stepValue.getText().toString(), equalTo("94"));
    }

    @Test
    public void GivenStepsAreSetToAValueWhenStepsAreSetToZeroThenNextStepWillOutputOne()
    {
        float setStepsValue = 45;
        SetSteps(setStepsValue);

        float counterValue = 678;
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(counterValue));

        float zeroValue = 0;
        SetSteps(zeroValue);
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(counterValue+1));

        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView stepValue = (TextView) createdActivity.findViewById(R.id.step_value);
        assertThat(stepValue.getText().toString(), equalTo("1"));
    }

    @Test
    public void WhenStepServiceIsDestroyedThenNoReceiversAreRegistered() {
        mStepService.onDestroy();
        assertThat(ShadowApplication.getInstance().getRegisteredReceivers().size(), equalTo(0));
    }
}