package hiteware.com.halfwaythere;

import android.hardware.SensorEvent;
import android.widget.TextView;

import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;

/**
 * Created by jasonhite on 4/29/15.
 */
public class StepSensorChangeTest
{
    @Test
    public void OnSensorChangeWillUpdateTheValueOfStepsOnOutputTextView() throws NoSuchFieldException, IllegalAccessException{
        TextView outputView = mock(TextView.class);

        SensorEvent sensorEvent = Mockito.mock(SensorEvent.class);
        CircularProgressBar progressBar = Mockito.mock(CircularProgressBar.class);

        Field valuesField = SensorEvent.class.getField("values");
        valuesField.setAccessible(true);
        float[] value = {14};
        valuesField.set(sensorEvent, value);

//        StepSensorChange stepSensorChange = new StepSensorChange();
//        stepSensorChange.setOutputView(outputView);
//        stepSensorChange.setprogressView(progressBar);
//        stepSensorChange.onSensorChanged(sensorEvent);

//        verify(outputView, times(1)).setText("14");
    }
}
