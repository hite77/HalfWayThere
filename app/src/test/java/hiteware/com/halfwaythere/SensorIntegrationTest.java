package hiteware.com.halfwaythere;

import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created on 4/29/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class SensorIntegrationTest
{
    private MainActivity CreatedActivity;
    private TestInjectableApplication application;

    private StepService stepService;

    @Before
    public void setUp()
    {
        application = (TestInjectableApplication) RuntimeEnvironment.application;
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        stepService = new StepService();
        stepService.onCreate();
    }

    @Test
    public void whenAStepCounterValueIsInjectedTheDisplayIsUpdated()
    {
        stepService.onSensorChanged(SensorValue.CreateSensorEvent(23));
        assertThat(((TextView) CreatedActivity.findViewById(R.id.step_value)).getText().toString(), equalTo("23"));
    }
}
