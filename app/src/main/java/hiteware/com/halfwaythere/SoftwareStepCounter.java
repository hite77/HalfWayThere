package hiteware.com.halfwaythere;

/**
 * Created on 6/10/15.
 */
public class SoftwareStepCounter implements SoftwareStepCounterInterface{
    private int steps = 0;
    private int tempcounter = 1;

    public int GetSteps() {
        return steps;
    }

    public void SetSteps(int Steps) {
        steps = Steps;
    }

    public void SensorUpdate(float[] values) {
        tempcounter++;
        if (tempcounter > 3) {
            steps++;
            steps++;
        }
    }
}
