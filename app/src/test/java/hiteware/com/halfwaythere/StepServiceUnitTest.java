package hiteware.com.halfwaythere;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

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
public class StepServiceUnitTest
{
    public TestInjectableApplication application;
    SensorManager sensorManager;

    @Before
    public void setUp() {
        application = (TestInjectableApplication) RuntimeEnvironment.application;
        application.setMockSensorManager();
        sensorManager = application.testModule.provideSensorManager();
    }

    @Test
    public void GivenStepServiceCreatedWhenStepsAreSetThenStepsCanBeRetrieved()
    {
        StepService stepService = new StepService();
        stepService.onCreate();

        float expectedValue = 14;
        stepService.setSteps(expectedValue);

        assertThat(stepService.getSteps(), equalTo(expectedValue));
    }

    @Test
    public void GivenStepServiceCreatedThenTheSensorManagerAsksForStepCounter()
    {
        StepService stepService = new StepService();
        stepService.onCreate();
        verify(sensorManager, times(1)).getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    @Test
    public void whenAppAndActivityAreConstructedThenSensorManagerRegistersForUpdates()
    {
        Sensor sensor = mock(Sensor.class);
        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(sensor);
        StepService stepService = new StepService();
        stepService.onCreate();
        verify(sensorManager, times(1)).registerListener(any(SensorEventListener.class), eq(sensor), eq(SensorManager.SENSOR_DELAY_UI));
    }

    @Test
    public void whenStartedAndNoStepCountThenShouldNotRegisterForUpdates()
    {
        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(null);
        StepService stepService = new StepService();
        stepService.onCreate();
        verify(sensorManager, times(0)).registerListener(any(SensorEventListener.class), any(Sensor.class), eq(SensorManager.SENSOR_DELAY_UI));
    }

    @Test
    public void whenStartedThenItIsStartedAsSticky()
    {
        StepService stepService = new StepService();
        int returnValue = stepService.onStartCommand(null, 0, 0);
        assertThat(returnValue, equalTo(Service.START_STICKY));
    }
}
