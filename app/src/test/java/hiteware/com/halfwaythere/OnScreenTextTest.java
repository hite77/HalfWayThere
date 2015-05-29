package hiteware.com.halfwaythere;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.TextView;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

/**
 * Created by jasonhite on 5/26/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class OnScreenTextTest {
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
    public void whenAppAndActivityAreConstructedAndSensorManagerIndicatesNoStepSensorThenMessageIsDisplayed()
    {
        when(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(null);
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        assertThat(((TextView) CreatedActivity.findViewById(R.id.steps_title)).getText().toString(), IsEqual.equalTo("Your device does not have Hardware Pedometer. Future versions of this software will have software pedometer and work with your device."));
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

    @Test
    public void whenTheAppIsRunningTheServiceWillBeStarted()
    {
        ActivityController controller = Robolectric.buildActivity(MainActivity.class).create().start();
        CreatedActivity = (MainActivity) controller.get();
        controller.resume();
        ShadowActivity shadowActivity = Shadows.shadowOf(CreatedActivity);
        Intent startedIntent = shadowActivity.getNextStartedService();
        assertNotNull(startedIntent);
    }
}