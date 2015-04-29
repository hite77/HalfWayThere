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
    private TextView OutputView;
    public StepSensorChange()
    {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        OutputView.setText(String.format("%.0f", event.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setOutputView(TextView outputView)
    {
        OutputView = outputView;
    }
}
