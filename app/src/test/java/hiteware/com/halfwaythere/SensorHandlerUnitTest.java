package hiteware.com.halfwaythere;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        application.setMockSensorManager();
        application.buildGraph();
        application.inject(this);
    }

    @Test
    public void whenAppAndActivityAreConstructedTheSensorManagerAsksForStepCounter()
    {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        verify(sensorManager, times(1)).getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    @Test
    public void whenAppAndActivityAreConstructedThenSensorManagerRegistersForUpdates()
    {
        Sensor sensor = Mockito.mock(Sensor.class);
        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(sensor);
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        verify(sensorManager, times(1)).registerListener(any(SensorEventListener.class), eq(sensor), eq(SensorManager.SENSOR_DELAY_UI));
    }
}
