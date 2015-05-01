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
    float offset;
    float initialSteps;
    float goalSteps = 10000;
    float halfWayThereValue;

    private TextView OutputView;
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
        if ((!halfWayThere) && (event.values[0]+offset > halfWayThereValue))
        {
            halfWayThere = true;
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

    public void setNumberOfSteps(float countOfSteps)
    {
        // first update count on display.  When next event comes in calculate offset
        OutputView.setText(String.format("%.0f", countOfSteps));
        // when step comes in set offset so it is 1 + expected steps.
        initialSteps = countOfSteps + 1;
        calculateOffset = true;
        calculateHalfWayPoint();
    }

    public void calculateHalfWayPoint()
    {
        halfWayThere = false;
        if (goalSteps > initialSteps) {
            halfWayThereValue = (float) Math.floor(((goalSteps - initialSteps - 1) / 2 + initialSteps - 1));
        }
        else
            halfWayThereValue = goalSteps;
    }

    public void SetGoalSteps(float countOfSteps)
    {
        goalSteps = countOfSteps;
        calculateHalfWayPoint();
    }
}
