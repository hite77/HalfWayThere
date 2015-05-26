package hiteware.com.halfwaythere;

import android.hardware.SensorEvent;
import android.widget.TextView;

import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by jasonhite on 4/29/15.
 */
public class StepServiceChangeStepTest
{
    @Test
    public void OnSensorChangeWillUpdateTheValueOfStepsOnOutputTextView() throws NoSuchFieldException, IllegalAccessException{
        TextView outputView = mock(TextView.class);

        SensorEvent sensorEvent = Mockito.mock(SensorEvent.class);

        Field valuesField = SensorEvent.class.getField("values");
        valuesField.setAccessible(true);
        float[] value = {14};
        valuesField.set(sensorEvent, value);

        StepService stepService = new StepService();
        stepService.setOutputView(outputView);
        stepService.onSensorChanged(sensorEvent);

        verify(outputView, times(1)).setText("14");
    }
}
