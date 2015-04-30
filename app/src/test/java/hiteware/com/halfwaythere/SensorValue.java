package hiteware.com.halfwaythere;

import android.hardware.SensorEvent;

import org.mockito.Mockito;

import java.lang.reflect.Field;

/**
 * Created by jasonhite on 4/29/15.
 */
public class SensorValue
{
    public static SensorEvent CreateSensorEvent(float value){
        SensorEvent sensorEvent = Mockito.mock(SensorEvent.class);

        try {
            Field valuesField = SensorEvent.class.getField("values");
            valuesField.setAccessible(true);
            float[] sensorValue = {value};
            try {
                valuesField.set(sensorEvent, sensorValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return sensorEvent;
    }
}
