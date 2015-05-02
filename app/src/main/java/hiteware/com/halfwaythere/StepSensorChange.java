package hiteware.com.halfwaythere;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Vibrator;
import android.widget.TextView;

/**
 * Created by jasonhite on 4/29/15.
 */
public class StepSensorChange implements SensorEventListener
{
    boolean calculateOffset = false;
    boolean halfWayThere = false;
    boolean allTheWayThere = false;

    float offset;
    float initialSteps;
    float goalSteps = 10000;
    float halfWayThereValue;
    float currentSteps = 0;

    private TextView OutputView;
    private TextView goalstepsView;
    private CircularProgressBar circularProgressBar;

    public StepSensorChange()
    {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (calculateOffset)
        {
            calculateOffset = false;
            offset = initialSteps - event.values[0];
        }
        if ((event != null) && (OutputView != null))
            OutputView.setText(String.format("%.0f", event.values[0]+offset));

        updateProgressBar();
        currentSteps = event.values[0] + offset;

        if ((!halfWayThere) && (event.values[0]+offset > halfWayThereValue))
        {
            halfWayThere = true;
            vibrate();
        }
        if ((!allTheWayThere) && (event.values[0]+offset > goalSteps))
        {
            allTheWayThere = true;
            vibrate();
        }

    }

    private void vibrate() {
        if (OutputView.getContext() != null) {
            Vibrator v = (Vibrator) OutputView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(2000);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setOutputView(TextView outputView)
    {
        OutputView = outputView;
    }
    public void setgoalStepsView(TextView goalStepsView) {
        this.goalstepsView = goalStepsView;
        goalstepsView.setText(String.format("%.0f", goalSteps));
        calculateHalfWayPoint();
    }
    public void setprogressView(CircularProgressBar circularProgressBar) {this.circularProgressBar = circularProgressBar; }

    public void updateProgressBar()
    {
        // calculate percentage
        float percentage = (currentSteps / goalSteps) * 100;
        if (percentage > 100) {
            percentage = 100;
        }
        circularProgressBar.setProgress(percentage);
    }

    public void setNumberOfSteps(float countOfSteps)
    {
        // first update count on display.  When next event comes in calculate offset
        OutputView.setText(String.format("%.0f", countOfSteps));
        // update the progressBar

        // when step comes in set offset so it is 1 + expected steps.
        initialSteps = countOfSteps + 1;
        calculateOffset = true;
        calculateHalfWayPoint();
        currentSteps = countOfSteps;
        updateProgressBar();
    }

    public void calculateHalfWayPoint()
    {
        halfWayThere = false;
        allTheWayThere = false;

        if (goalSteps > initialSteps) {
            halfWayThereValue = (float) Math.floor(((goalSteps - initialSteps - 1) / 2 + initialSteps - 1));
        }
        else
            halfWayThereValue = goalSteps;
    }

    public void SetGoalSteps(float countOfSteps)
    {
        goalSteps = countOfSteps;
        goalstepsView.setText(String.format("%.0f", goalSteps));
        calculateHalfWayPoint();
        updateProgressBar();
    }
}
