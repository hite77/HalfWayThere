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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
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
 * Created by jasonhite on 5/14/15.
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
                            .hasExtra(stepService.STEPS_OCCURRED),
                    equalTo(true));
            actual = shadowIntent.getFloatExtra(
                    stepService.STEPS_OCCURRED, 0);
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

        createdActivity.registerReceiver(testReceiver, new IntentFilter(mStepService.ACTION_STEPS_OCCURRED));

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
        verify(sensorManager, times(1)).registerListener(any(SensorEventListener.class), eq(sensor), eq(SensorManager.SENSOR_DELAY_UI));
    }

    @Test
    public void whenStartedAndNoStepCountThenShouldNotRegisterForUpdates() {
        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(null);
        mStepService.onCreate();
        verify(sensorManager, times(0)).registerListener(any(SensorEventListener.class), any(Sensor.class), eq(SensorManager.SENSOR_DELAY_UI));
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

        createdActivity.registerReceiver(testReceiver, new IntentFilter(mStepService.ACTION_STEPS_OCCURRED));

        SensorEvent stepEvent = SensorValue.CreateSensorEvent(expected);
        mStepService.onSensorChanged(stepEvent);

        assertThat(testReceiver.getActualResult(), equalTo(expected));
    }

    @Test
    public void whenSetStepsIsSetAndAnotherStepEventOccursThenStepsAreOffsetCorrectly()
    {
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver(mStepService);
        createdActivity.registerReceiver(testReceiver, new IntentFilter(mStepService.ACTION_STEPS_OCCURRED));

        float counterValue = 85;

        SensorEvent stepEvent = SensorValue.CreateSensorEvent(counterValue);
        mStepService.onSensorChanged(stepEvent);

        float expected = 26;

        SetSteps(expected - 1);
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(counterValue+1));

        assertThat(testReceiver.getActualResult(), equalTo(expected));
    }

    @Test
    public void beforeSetStepsCountIsSolelyWhatSensorProvidesAfterSetStepsAdditionalStepsCountFromTheSetPoint()
    {
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver(mStepService);
        createdActivity.registerReceiver(testReceiver, new IntentFilter(mStepService.ACTION_STEPS_OCCURRED));

        float valueWithoutSetSteps = 99;

        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(valueWithoutSetSteps));
        assertThat(testReceiver.getActualResult(), equalTo(valueWithoutSetSteps));

        float setStepsValue = 10;
        float addedSteps = 2;

        SetSteps(setStepsValue);
        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(valueWithoutSetSteps+addedSteps));

        assertThat(testReceiver.getActualResult(), equalTo(setStepsValue+addedSteps));
    }

    @Test
    public void GivenSetStepsIsCalledWithoutAnyStepEventsWhenAStepComesInThenValueIsEqualToTheStepsWeSet()
    {
        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver(mStepService);
        createdActivity.registerReceiver(testReceiver, new IntentFilter(mStepService.ACTION_STEPS_OCCURRED));

        float setStepsValue = 10;

        SetSteps(setStepsValue);

        float anyValue = 109;

        mStepService.onSensorChanged(SensorValue.CreateSensorEvent(anyValue));

        assertThat(testReceiver.getActualResult(), equalTo(setStepsValue + 1));
    }
}