package hiteware.com.halfwaythere;

import android.hardware.SensorEvent;
import android.widget.TextView;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by jasonhite on 4/29/15.
 */
public class StepServiceChangeStepTest
{
    @Test
    public void OnSensorChangeWillUpdateTheValueOfStepsOnOutputTextView() {
        TextView outputView = mock(TextView.class);
        
        StepService stepService = new StepService();
        stepService.setOutputView(outputView);
        SensorEvent sensorValue = SensorValue.CreateSensorEvent(14);
        stepService.onSensorChanged(sensorValue);

        verify(outputView, times(1)).setText("14");
    }
}
