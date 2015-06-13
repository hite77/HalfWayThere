package hiteware.com.halfwaythere;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import org.mockito.Mockito;

import java.lang.reflect.Field;

/**
 * Created on 4/29/15.
 */
class SensorValue
{
    public static SensorEvent CreateSensorEvent(float[] values) {
        SensorEvent sensorEvent = Mockito.mock(SensorEvent.class);

        try {
            Field valuesField = SensorEvent.class.getField("values");
            valuesField.setAccessible(true);
            float[] sensorValues = values;
            try {
                valuesField.set(sensorEvent, sensorValues);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return sensorEvent;
    }

    public static float CalculateValueForGivenGValue(float g)
    {
        return (float) Math.sqrt(g * SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
    }

    public static float CalculateForceToApplyOnEachAxisToGiveGValue(float g)
    {
        return (float) Math.sqrt(g * SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH / 3);
    }
}
