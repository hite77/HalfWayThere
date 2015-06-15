package hiteware.com.halfwaythere;

/**
 * Created on 6/10/15.
 */
interface SoftwareStepCounterInterface {
    int GetSteps();
    void SetSteps(int Steps);
    void SensorUpdate(float[] values);
}
