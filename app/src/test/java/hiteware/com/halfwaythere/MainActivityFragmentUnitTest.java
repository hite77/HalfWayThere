package hiteware.com.halfwaythere;

import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.util.ActivityController;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created on 5/31/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityFragmentUnitTest {
    private MainActivity CreatedActivity;

    @Test
    public void whenTheAppIsRunningTheServiceWillBeStarted() {
        ActivityController controller = Robolectric.buildActivity(MainActivity.class).create().start();
        MainActivity createdActivity = (MainActivity) controller.get();
        controller.start();
        ShadowActivity shadowActivity = shadowOf(createdActivity);
        Intent startedIntent = shadowActivity.getNextStartedService();
        assertNotNull(startedIntent);
    }

    @Test
    public void whenBroadcastOfStepsIsReceivedThenStepsAreDisplayed() {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_STEPS_OCCURRED, StepService.STEPS_OCCURRED, 45);

        assertThat(((TextView) CreatedActivity.findViewById(R.id.step_value)).getText().toString(), equalTo("45"));
    }

    @Test
    public void whenBroadcastOfStepsAndGoalAreReceivedThenProgressIsUpdated() {
        ((TestInjectableApplication) RuntimeEnvironment.application).setMock();

        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        int expected = 45;
        int expectedGoal = 14000;
        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_STEPS_OCCURRED, StepService.STEPS_OCCURRED, expected);
        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_GOAL_CHANGED, StepService.GOAL_SET, expectedGoal);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        View expectedCircularProgress = CreatedActivity.findViewById(R.id.circularProgressWithHalfWay);

        ProgressUpdateInterface progressUpdate = ((TestInjectableApplication) RuntimeEnvironment.application).testModule.provideProgressUpdate();
        verify(progressUpdate, times(1)).SetCircularProgress((CircularProgressWithHalfWay) expectedCircularProgress);
        verify(progressUpdate, atLeastOnce()).SetSteps(expected);
        verify(progressUpdate, atLeastOnce()).SetGoal(expectedGoal);
    }

    @Test
    public void whenBroadcastOfGoalIsReceivedThenGoalIsDisplayed() {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_GOAL_CHANGED, StepService.GOAL_SET, 14000);

        assertThat(((TextView) CreatedActivity.findViewById(R.id.goal_value)).getText().toString(), equalTo("14000"));
    }

    @Test
    public void whenHalfWayButtonIsClickedThenBroadcastIsSentToService()
    {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_STEPS_OCCURRED, StepService.STEPS_OCCURRED, 1000);
        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_GOAL_CHANGED, StepService.GOAL_SET, 14000);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        CreatedActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_HALF_WAY_SET));

        CreatedActivity.findViewById(R.id.HalfWayToggle).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertThat(testReceiver.getActualHalfWay(), equalTo(7500));
    }

    @Test
    public void whenHalfWayButtonIsClickedThenProgressUpdateIsUpdated()
    {
        ((TestInjectableApplication) RuntimeEnvironment.application).setMock();
        ProgressUpdateInterface progressUpdate = ((TestInjectableApplication) RuntimeEnvironment.application).testModule.provideProgressUpdate();

        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_STEPS_OCCURRED, StepService.STEPS_OCCURRED, 4653);
        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_GOAL_CHANGED, StepService.GOAL_SET, 5778);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        CreatedActivity.findViewById(R.id.HalfWayToggle).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(progressUpdate, atLeastOnce()).SetHalfWay(5215);
    }

    @Test
    public void whenHalfWayButtonIsClickedThenCountIsUpdatedForHalfWay()
    {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_STEPS_OCCURRED, StepService.STEPS_OCCURRED, 1333);
        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_GOAL_CHANGED, StepService.GOAL_SET, 2000);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        CreatedActivity.findViewById(R.id.HalfWayToggle).performClick();

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView halfWayValue = (TextView) CreatedActivity.findViewById(R.id.HalfWayValue);

        assertThat(halfWayValue.getText().toString(), equalTo("1666"));
    }

    @Test
    public void whenHalfWayButtonIsClickedAndIsInvalidBecauseGoalIsZeroThenNoBroadcast()
    {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_GOAL_CHANGED, StepService.GOAL_SET, 0);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        CreatedActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_HALF_WAY_SET));

        CreatedActivity.findViewById(R.id.HalfWayToggle).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertThat(testReceiver.getActualHalfWay(), equalTo(-1));
    }
    @Test
    public void whenHalfWayButtonIsClickedAndIsInvalidBecauseHalfWayIsGreaterThenTextIsEmptyForHalfWay()
    {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_STEPS_OCCURRED, StepService.STEPS_OCCURRED, 15000);
        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_GOAL_CHANGED, StepService.GOAL_SET, 14000);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        CreatedActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_HALF_WAY_SET));

        CreatedActivity.findViewById(R.id.HalfWayToggle).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView halfWayValue = (TextView) CreatedActivity.findViewById(R.id.HalfWayValue);

        assertThat(halfWayValue.getText().toString(), equalTo(""));
    }

    @Test
    public void whenHalfWayButtonIsClickedAndIsValidThenItIsClickedWithInvalidThenTextIsEmptyForHalfWay()
    {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_STEPS_OCCURRED, StepService.STEPS_OCCURRED, 10000);
        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_GOAL_CHANGED, StepService.GOAL_SET, 14000);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        CreatedActivity.findViewById(R.id.HalfWayToggle).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_STEPS_OCCURRED, StepService.STEPS_OCCURRED, 15000);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        CreatedActivity.findViewById(R.id.HalfWayToggle).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView halfWayValue = (TextView) CreatedActivity.findViewById(R.id.HalfWayValue);

        assertThat(halfWayValue.getText().toString(), equalTo(""));
    }

    @Test
    public void whenHalfWayButtonIsClickedAndProgressSaysIsValidThenItIsClickedWithProgressSayingInvalidThenProgressUpdateIsCleared()
    {
        ((TestInjectableApplication) RuntimeEnvironment.application).setMock();
        ProgressUpdateInterface progressUpdate = ((TestInjectableApplication) RuntimeEnvironment.application).testModule.provideProgressUpdate();

        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        when(progressUpdate.SetHalfWay(anyInt())).thenReturn(true);

        View halfWayButton = CreatedActivity.findViewById(R.id.HalfWayToggle);
        halfWayButton.performClick();

        when(progressUpdate.SetHalfWay(anyInt())).thenReturn(false);

        halfWayButton.performClick();

        verify(progressUpdate).ClearHalfWay();
    }

    @Test
    public void whenServiceBroadcastsAClearThenTextIsClearedAndProgressUpdateIsCleared()
    {
        ((TestInjectableApplication) RuntimeEnvironment.application).setMock();
        ProgressUpdateInterface progressUpdate = ((TestInjectableApplication) RuntimeEnvironment.application).testModule.provideProgressUpdate();

        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        BroadcastHelper.sendBroadcast(CreatedActivity, StepService.ACTION_CLEAR_HALF_WAY);

        verify(progressUpdate).ClearHalfWay();
    }


    @Test
    public void whenActivityIsPausedItUnregistersReceiver() {
        ActivityController controller = Robolectric.buildActivity(MainActivity.class).create().start();
        CreatedActivity = (MainActivity) controller.get();

        controller.resume();

        List<ShadowApplication.Wrapper> registeredReceivers = ShadowApplication.getInstance().getRegisteredReceivers();
        assertThat(registeredReceivers.size(), equalTo(1));

        controller.pause();
        assertThat(ShadowApplication.getInstance().getRegisteredReceivers().size(), equalTo(0));
    }
}
