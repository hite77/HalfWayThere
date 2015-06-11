package hiteware.com.halfwaythere;

import android.hardware.SensorManager;

/**
 * Created on 6/10/15.
 */
public class SoftwareStepCounter implements SoftwareStepCounterInterface{
    private int steps = 0;
    private int previousValuesCounter = 0;
    private final float[] previousGValues = {0, 0};

    public int GetSteps() {
        return steps;
    }

    public void SetSteps(int Steps) {
        steps = Steps;
    }

    public void SensorUpdate(float[] values) {
        if (values.length < 3) return;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        float g = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH* SensorManager.GRAVITY_EARTH);

        if (AllPreviousGValuesStored(g)) {
            if (
                    (previousGValues[1] > 2) &&
                    (previousGValues[1] > previousGValues[0]) &&
                    (previousGValues[1] > g)) {
                steps++;
                steps++;
            }
            ShiftPreviousValues(g);
        }
    }

    private void ShiftPreviousValues(float g) {
        previousGValues[0] = previousGValues[1];
        previousGValues[1] = g;
    }

    private boolean AllPreviousGValuesStored(float g) {
        boolean stored = true;
        if (previousValuesCounter < 2)
        {
            previousGValues[previousValuesCounter] = g;
            previousValuesCounter++;
            stored = false;
        }
        return stored;
    }
}
