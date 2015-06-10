package hiteware.com.halfwaythere;

/**
 * Created on 6/10/15.
 */
public interface SoftwareStepCounterInterface {
    int GetSteps();
    void SetSteps(int Steps);
    void SensorUpdate(float[] values);
}
