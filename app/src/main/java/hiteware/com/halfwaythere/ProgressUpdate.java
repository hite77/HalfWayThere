package hiteware.com.halfwaythere;

/**
 * Created on 6/15/15.
 */
public class ProgressUpdate implements ProgressUpdateInterface {
    private CircularProgressWithHalfWay progress;
    private int Steps;
    private int Goal;
    private boolean GoalSet = false;
    private boolean StepsSet = false;

    private void SetStatus() {
        float percentage;
        if (Goal > 0) {
            percentage = ((float) Steps / Goal * 100);
        }
        else { percentage = 0; }
        if (percentage > 100) {percentage = 100; }

        if (null != progress) {
            progress.setProgress(percentage);
        }
    }

    @Override
    public void SetCircularProgress(CircularProgressWithHalfWay indicator) {
        progress = indicator;
    }

    @Override
    public void SetSteps(int steps) {
        Steps = steps;
        StepsSet = true;
        if (GoalSet) {
            SetStatus();
        }
    }

    @Override
    public void SetGoal(int goal) {
        Goal = goal;
        GoalSet = true;
        if (StepsSet) {
            SetStatus();
        }
    }
}
