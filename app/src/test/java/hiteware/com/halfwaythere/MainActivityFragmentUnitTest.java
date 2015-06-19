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
import static junit.framework.Assert.assertNotSame;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

        Intent broadcastSteps = new Intent();
        broadcastSteps.setAction(StepService.ACTION_STEPS_OCCURRED);
        int expected = 45;
        broadcastSteps.putExtra(StepService.STEPS_OCCURRED, expected);
        CreatedActivity.sendBroadcast(broadcastSteps);

        assertThat(((TextView) CreatedActivity.findViewById(R.id.step_value)).getText().toString(), equalTo("45"));
    }

    @Test
    public void whenBroadcastOfStepsAndGoalAreReceivedThenProgressIsUpdated() {
        ((TestInjectableApplication) RuntimeEnvironment.application).setMock();

        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        Intent broadcastSteps = new Intent();
        broadcastSteps.setAction(StepService.ACTION_STEPS_OCCURRED);
        int expected = 45;
        broadcastSteps.putExtra(StepService.STEPS_OCCURRED, expected);
        CreatedActivity.sendBroadcast(broadcastSteps);

        Intent broadcastGoal = new Intent();
        broadcastGoal.setAction(StepService.ACTION_GOAL_CHANGED);
        int expectedGoal = 14000;
        broadcastGoal.putExtra(StepService.GOAL_SET, expectedGoal);
        CreatedActivity.sendBroadcast(broadcastGoal);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        View expecectedCircularProgress = CreatedActivity.findViewById(R.id.circularProgressWithHalfWay);

        ProgressUpdateInterface progressUpdate = ((TestInjectableApplication) RuntimeEnvironment.application).testModule.provideProgressUpdate();
        verify(progressUpdate, times(1)).SetCircularProgress((CircularProgressWithHalfWay) expecectedCircularProgress);
        verify(progressUpdate, atLeastOnce()).SetSteps(expected);
        verify(progressUpdate, atLeastOnce()).SetGoal(expectedGoal);
    }

    @Test
    public void whenBroadcastOfGoalIsReceivedThenGoalIsDisplayed() {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        Intent broadcastGoal = new Intent();
        broadcastGoal.setAction(StepService.ACTION_GOAL_CHANGED);
        int expected = 14000;
        broadcastGoal.putExtra(StepService.GOAL_SET, expected);
        CreatedActivity.sendBroadcast(broadcastGoal);

        assertThat(((TextView) CreatedActivity.findViewById(R.id.goal_value)).getText().toString(), equalTo("14000"));
    }

    @Test
    public void whenHalfWayButtonIsClickedThenBroadcastIsSentToService()
    {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        CreatedActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_HALF_WAY_SET));

        CreatedActivity.findViewById(R.id.HalfWayToggle).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertNotSame(testReceiver.getActualHalfWay(), -1);
    }

    @Test
    public void whenHalfWayButtonIsClickedThenCountIsUpdatedForHalfWayAndProgressUpdateIsUpdated()
    {
        ((TestInjectableApplication) RuntimeEnvironment.application).setMock();

        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        Intent broadcastSteps = new Intent();
        broadcastSteps.setAction(StepService.ACTION_STEPS_OCCURRED);
        int steps = 1000;
        broadcastSteps.putExtra(StepService.STEPS_OCCURRED, steps);
        CreatedActivity.sendBroadcast(broadcastSteps);

        Intent broadcastGoal = new Intent();
        broadcastGoal.setAction(StepService.ACTION_GOAL_CHANGED);
        int goal = 14000;
        broadcastGoal.putExtra(StepService.GOAL_SET, goal);
        CreatedActivity.sendBroadcast(broadcastGoal);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        CreatedActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_HALF_WAY_SET));

        ProgressUpdateInterface progressUpdate = ((TestInjectableApplication) RuntimeEnvironment.application).testModule.provideProgressUpdate();

        CreatedActivity.findViewById(R.id.HalfWayToggle).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView halfWayValue = (TextView) CreatedActivity.findViewById(R.id.HalfWayValue);

        assertThat(testReceiver.getActualHalfWay(), equalTo(1000 + (13000 / 2)));
        assertThat(halfWayValue.getText().toString(), equalTo("7500"));

        verify(progressUpdate, atLeastOnce()).SetHalfWay(7500);
    }
//
//    @Test //TODO: whenHalfWayButtonIsClickedAndIsInvalidFromProgressUpdateThenNoBroadcast
//    public void whenHalfWayButtonIsClickedAndIsInvalidFromProgressUpdateThenNoBroadcast()
//    {
//
//    }
//
//    @Test //TODO: whenServiceBroadcastsAClearThenTextIsClearedAndProgressUpdateIsCleared
//    public void whenServiceBroadcastsAClearThenTextIsClearedAndProgressUpdateIsCleared()
//    {
//
//    }

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
