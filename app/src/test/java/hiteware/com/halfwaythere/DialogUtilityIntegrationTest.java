package hiteware.com.halfwaythere;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
 * Created on 6/3/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class DialogUtilityIntegrationTest {

    private MainActivity CreatedActivity;

    @Before
    public void Setup()
    {
        ActivityController controller = Robolectric.buildActivity(MainActivity.class).create().start();
        CreatedActivity = (MainActivity) controller.get();
        controller.resume();
    }

    public void SetCurrentSteps(int steps)
    {
        MenuItem item = new RoboMenuItem(R.id.action_set_current_steps);
        CreatedActivity.onOptionsItemSelected(item);

        ShadowAlertDialog shadow = shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        EditText editText = (EditText) shadow.getView();
        editText.setText(Integer.toString(steps));

        Button okButton = ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.performClick();
    }

    public void SetGoalSteps(int goal)
    {
        MenuItem item = new RoboMenuItem(R.id.action_set_goal_steps);
        CreatedActivity.onOptionsItemSelected(item);

        ShadowAlertDialog shadow = shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        EditText editText = (EditText) shadow.getView();
        editText.setText(Integer.toString(goal));

        Button okButton = ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.performClick();
    }

    @Test
    public void WhenStepsAreSetInTheDialogThenTheStepsAreDisplayedOnScreen()
    {
        StepService mStepService = new StepService();
        mStepService.onStartCommand(new Intent(), 0, 0);

        int expected = 66;
        SetCurrentSteps(expected);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertThat(((TextView) CreatedActivity.findViewById(R.id.step_value)).getText().toString(), equalTo("66"));
    }

    @Test
    public void WhenGoalStepsAreSetInTheDialogThenTheGoalStepAreDisplayedOnScreen()
    {
        StepService mStepService = new StepService();
        mStepService.onStartCommand(new Intent(),0, 0);

        int expected = 11000;
        SetGoalSteps(expected);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertThat(((TextView) CreatedActivity.findViewById(R.id.goal_value)).getText().toString(), equalTo("11000"));
    }
}
