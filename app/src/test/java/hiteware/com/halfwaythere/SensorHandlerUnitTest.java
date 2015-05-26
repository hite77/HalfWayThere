package hiteware.com.halfwaythere;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
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
    SensorManager sensorManager;

    public MainActivity CreatedActivity;
    public TestInjectableApplication application;

    @Before
    public void setUp() {
        application = (TestInjectableApplication) RuntimeEnvironment.application;
        application.setMockSensorManager();
        sensorManager = application.testModule.provideSensorManager();
    }

    @Test
    public void whenAppAndActivityAreConstructedTheSensorManagerAsksForStepCounter() // this has hopefully been replicated.
    {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        verify(sensorManager, times(1)).getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    @Test
    public void whenAppAndActivityAreConstructedThenSensorManagerRegistersForUpdates() // this has hopefully been replicated.
    {
        Sensor sensor = Mockito.mock(Sensor.class);
        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(sensor);
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        verify(sensorManager, times(1)).registerListener(any(SensorEventListener.class), eq(sensor), eq(SensorManager.SENSOR_DELAY_UI));
    }

    @Test
    public void whenAppAndActivityAreConstructedAndSensorManagerIndicatesNoStepSensorThenMessageIsDisplayed()
    {
        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(null);
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        assertThat(((TextView) CreatedActivity.findViewById(R.id.steps_title)).getText().toString(), equalTo("Your device does not have Hardware Pedometer. Future versions of this software will have software pedometer and work with your device."));
    }

    @Test
    public void whenStartedAndNoStepCountThenShouldNotRegisterForUpdates()  // this has hopefully been replicated.
    {
        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(null);
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        verify(sensorManager, times(0)).registerListener(any(SensorEventListener.class), any(Sensor.class), eq(SensorManager.SENSOR_DELAY_UI));
    }

    @Test
    public void whenStartedAndThereIsAStepSensorTheTitleAtTopSaysSteps()
    {
        Sensor sensor = Mockito.mock(Sensor.class);
        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(sensor);
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        assertThat(((TextView) CreatedActivity.findViewById(R.id.steps_title)).getText().toString(), equalTo("Steps"));
    }

    @Test
    public void whenStartedAndDoesNotHaveSensorAndResumedAndThereIsASensorDisplaysSteps()
    {
        ActivityController controller = Robolectric.buildActivity(MainActivity.class).create().start();
        CreatedActivity = (MainActivity) controller.get();
        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(null);

        controller.resume();
        assertThat(((TextView) CreatedActivity.findViewById(R.id.steps_title)).getText().toString(), not("Steps"));
        controller.pause();

        Sensor sensor = Mockito.mock(Sensor.class);
        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(sensor);
        controller.resume();

        assertThat(((TextView) CreatedActivity.findViewById(R.id.steps_title)).getText().toString(), equalTo("Steps"));
    }
}
