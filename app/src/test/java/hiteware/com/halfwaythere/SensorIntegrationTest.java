package hiteware.com.halfwaythere;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.util.ActivityController;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created on 4/29/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class SensorIntegrationTest
{
    private MainActivity CreatedActivity;
    private ActivityController controller;
    private StepService stepService;

    @Before
    public void setUp()
    {
        controller = Robolectric.buildActivity(MainActivity.class).create().start();
        CreatedActivity = (MainActivity) controller.get();
        controller.postResume();

        stepService = new StepService();
        stepService.onStartCommand(new Intent(), 0, 0);
    }

    private void SetCurrentSteps(int steps)
    {
        MenuItem item = new RoboMenuItem(R.id.action_set_current_steps);
        CreatedActivity.onOptionsItemSelected(item);

        ShadowAlertDialog shadow = shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        EditText editText = (EditText) shadow.getView();
        editText.setText(Integer.toString(steps));

        Button okButton = ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.performClick();
    }

    @Test
    public void whenAStepCountHappensThenDisplayIsUpdated()
    {
        float value = SensorValue.CalculateForceToApplyOnEachAxisToGiveGValue(3);

        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{0, 0, 0}));
        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{value, value, value}));
        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{0, 0, 0}));

        assertThat(((TextView) CreatedActivity.findViewById(R.id.step_value)).getText().toString(), equalTo("2"));
    }

    @Test
    public void GivenStepsAreSetToAValueWhenStepsAreSetToZeroThenNextStepWillOutputTwo()
    {
        int setStepsValue = 45;
        SetCurrentSteps(setStepsValue);

        int zeroValue = 0;
        SetCurrentSteps(zeroValue);

        float value = SensorValue.CalculateForceToApplyOnEachAxisToGiveGValue(3);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{0, 0, 0}));
        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{value, value, value}));
        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{0, 0, 0}));

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertThat(((TextView) CreatedActivity.findViewById(R.id.step_value)).getText().toString(), equalTo("2"));
    }

    @Test
    public void GivenServiceIsRunningAndStepsAreSetAndStepEventHappensTwiceWhenActivityIsRestartedThenStepsAreRedisplayedFromServiceAndIsIncremented()
    {
        int setStepsValue = 15;

        SetCurrentSteps(setStepsValue);

        float value = SensorValue.CalculateForceToApplyOnEachAxisToGiveGValue(4);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{0, 0, 0}));
        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{value, value, value}));
        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{0, 0, 0}));

        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView stepValue = (TextView) createdActivity.findViewById(R.id.step_value);
        assertThat(stepValue.getText().toString(), CoreMatchers.equalTo("17"));
    }

    @Test
    public void GivenSetStepsIsCalledAndStepEventOccursAndServiceIsReconstructedThenCountShouldBeTwoMoreThanTheSetValue()
    {
        int setStepsValue = 13;

        SetCurrentSteps(setStepsValue);

        float value = SensorValue.CalculateForceToApplyOnEachAxisToGiveGValue(4);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{0, 0, 0}));
        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{value, value, value}));
        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{0, 0, 0}));

        stepService.onDestroy();
        stepService = null;

        controller.restart();

        stepService = new StepService();
        stepService.onStartCommand(new Intent(), 0, 0);

        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView stepValue = (TextView) createdActivity.findViewById(R.id.step_value);
        assertThat(stepValue.getText().toString(), CoreMatchers.equalTo("15"));
    }

    @Test
    public void GivenSetStepsIsCalledWithoutAnyStepEventsWhenAStepComesInThenValueIsEqualToTheStepsWeSet()
    {
        int setStepsValue = 10;

        SetCurrentSteps(setStepsValue);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        float value = SensorValue.CalculateForceToApplyOnEachAxisToGiveGValue(4);

        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{0, 0, 0}));
        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{value, value, value}));
        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{0, 0, 0}));

        assertThat(((TextView) CreatedActivity.findViewById(R.id.step_value)).getText().toString(), equalTo("12"));
    }

    @Test
    public void GivenServiceGoesThroughACreateDestroyCycleAndSensorEventOccursThenActivityReadsTheSteps()
    {
        int steps = 93;
        SetCurrentSteps(steps);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        stepService.onDestroy();
        stepService = null;
        stepService = new StepService();
        stepService.onStartCommand(new Intent(), 0, 0);
        stepService.onSensorChanged(SensorValue.CreateSensorEvent(new float[]{0, 0, 0}));

        MainActivity createdActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView stepValue = (TextView) createdActivity.findViewById(R.id.step_value);
        assertThat(stepValue.getText().toString(), CoreMatchers.equalTo("93"));
    }
}
