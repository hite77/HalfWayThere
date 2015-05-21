package hiteware.com.halfwaythere;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
}
