package hiteware.com.halfwaythere;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

/**
 * Created by jasonhite on 4/29/15.
 */
public class StepSensorChange implements SensorEventListener
{
    boolean calculateOffset = false;
    float offset;
    float initialSteps;
    float goalSteps = 10000;

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
    }

    public void SetGoalSteps(float countOfSteps)
    {
        goalSteps = countOfSteps;
    }
}
