package hiteware.com.halfwaythere;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by jasonhite on 4/28/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class SensorHandlerUnitTest
{
    @Inject
    SensorManager sensorManager;

    public MainActivity CreatedActivity;
    public TestDemoApplication application;

    @Before
    public void setUp() {
        application = (TestDemoApplication) RuntimeEnvironment.application;
        application.setMockLocationManager();
        application.inject(null, this);
    }

    @Test
    public void whenAppAndActivityAreConstructedTheSensorManagerAsksForStepCounter()
    {
//        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

//        Mockito.mock(Sensor.class);
//        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn();

        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().get();
        verify(sensorManager, times(1)).getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }
}
