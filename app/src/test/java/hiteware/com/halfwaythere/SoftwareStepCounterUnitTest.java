package hiteware.com.halfwaythere;

import android.hardware.SensorManager;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created on 6/10/15.
 */
public class SoftwareStepCounterUnitTest {
    SoftwareStepCounter softwareStepCounter;

    public float CalculateValueForGivenGValue(float g)
    {
        return (float) Math.sqrt(g * SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
    }

    @Before
    public void Setup()
    {
        softwareStepCounter = new SoftwareStepCounter();
    }

    @Test
    public void WhenSoftwareStepCounterIsInitializedThenItStartsAtZero()
    {
        assertThat(softwareStepCounter.GetSteps(), equalTo(0));
    }

    @Test
    public void WhenCurrentStepIsSetThenTheStepCanBeRetrieved()
    {
        int expectedSteps = 45;
        softwareStepCounter.SetSteps(expectedSteps);
        assertThat(softwareStepCounter.GetSteps(), equalTo(expectedSteps));
    }

    @Test
    public void WhenThreeSetsAreSentAllXValuesWithAPeakThenTheCountGoesUpByTwo()
    {
        float valueForThreeG = CalculateValueForGivenGValue(3);
        float xPeak[] = {valueForThreeG, 0, 0};
        float lowValue[] = {0, 0, 0};
        softwareStepCounter.SensorUpdate(lowValue);
        softwareStepCounter.SensorUpdate(xPeak);
        softwareStepCounter.SensorUpdate(lowValue);

        assertThat(softwareStepCounter.GetSteps(), equalTo(2));
    }

    @Test
    public void WhenOnlyOneValueSentThatIsAboveTwoCountDoesNotGoUp()
    {
        float values[] = {3, 0, 0};
        softwareStepCounter.SensorUpdate(values);
        assertThat(softwareStepCounter.GetSteps(), equalTo(0));
    }

    @Test
    public void WhenTwoValuesAreSentThatHaveARiseToTwoCountDoesNotGoUp()
    {
        float values[] = {3, 0, 0};
        float values2[] = {0, 3, 0};
        softwareStepCounter.SensorUpdate(values);
        softwareStepCounter.SensorUpdate(values2);
        assertThat(softwareStepCounter.GetSteps(), equalTo(0));
    }
}
