package hiteware.com.halfwaythere;

import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by jasonhite on 4/29/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class SensorIntegrationTest
{
    public MainActivity CreatedActivity;
    public TestInjectableApplication application;

    @Inject
    StepSensorChange stepSensorChange;

    @Before
    public void setUp()
    {
        application = (TestInjectableApplication) RuntimeEnvironment.application;
        application.addToGraph(new IntegrationModule());
        application.inject(this);
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
    }

    @Test
    public void whenAStepCounterValueIsInjectedTheDisplayIsUpdated()
    {
        stepSensorChange.onSensorChanged(SensorValue.CreateSensorEvent(23));
        assertThat(((TextView) CreatedActivity.findViewById(R.id.step_value)).getText().toString(), equalTo("23"));
    }

}
