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
    private SoftwareStepCounter softwareStepCounter;

    private float CalculateValueForGivenGValue(float g)
    {
        return (float) Math.sqrt(g * SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
    }

    private float CalculateForceToApplyOnEachAxisToGiveGValue(float g)
    {
        return (float) Math.sqrt(g * SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH / 3);
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
        float valueForFourG = CalculateValueForGivenGValue(4);
        float xPeak[] = {valueForFourG, 0, 0};
        float lowValue[] = {0, 0, 0};
        softwareStepCounter.SensorUpdate(lowValue);
        softwareStepCounter.SensorUpdate(xPeak);
        softwareStepCounter.SensorUpdate(lowValue);

        assertThat(softwareStepCounter.GetSteps(), equalTo(2));
    }

    @Test
    public void WhenTheStepIsSetToAValueAndAStepHappensThenTheValueGoesUpByTwo()
    {
        int setSteps = 47;
        int expectedSteps = setSteps + 2;

        softwareStepCounter.SetSteps(setSteps);

        float valueForFourG = CalculateValueForGivenGValue(4);
        float xPeak[] = {valueForFourG, 0, 0};
        float lowValue[] = {0, 0, 0};
        softwareStepCounter.SensorUpdate(lowValue);
        softwareStepCounter.SensorUpdate(xPeak);
        softwareStepCounter.SensorUpdate(lowValue);

        assertThat(softwareStepCounter.GetSteps(), equalTo(expectedSteps));
    }

    @Test
    public void WhenThreeSetsAreSentWithNoPeakInTheMiddleThenStepsDoNotGoUp()
    {
        float valueForThreeG = CalculateValueForGivenGValue(3);
        float xValue[] = {valueForThreeG, 0, 0};
        softwareStepCounter.SensorUpdate(xValue);
        softwareStepCounter.SensorUpdate(xValue);
        softwareStepCounter.SensorUpdate(xValue);

        assertThat(softwareStepCounter.GetSteps(), equalTo(0));
    }

    @Test
    public void WhenFourValuesAreSetFirstThreeHadAStepAndThereIsNotANewStepCausedByTheFourthThenItDoesNotAddSteps()
    {
        float valueForThreeG = CalculateValueForGivenGValue(3);
        float higherValue[] = {valueForThreeG, 0, 0};
        float lowerValue[] = {CalculateValueForGivenGValue(1), 0, 0};

        softwareStepCounter.SensorUpdate(lowerValue);
        softwareStepCounter.SensorUpdate(higherValue);
        softwareStepCounter.SensorUpdate(lowerValue);
        assertThat(softwareStepCounter.GetSteps(), equalTo(2));

        softwareStepCounter.SensorUpdate(lowerValue);
        assertThat(softwareStepCounter.GetSteps(), equalTo(2));
    }

    @Test
    public void WhenTwoSetsOfPeaksAreSentInThenThereAreTwoSetsOfSteps()
    {
        float valueForThreeG = CalculateValueForGivenGValue(3);
        float higherValue[] = {valueForThreeG, 0, 0};
        float lowerValue[] = {CalculateValueForGivenGValue(1), 0, 0};

        softwareStepCounter.SensorUpdate(lowerValue);
        softwareStepCounter.SensorUpdate(higherValue);
        softwareStepCounter.SensorUpdate(lowerValue);
        assertThat(softwareStepCounter.GetSteps(), equalTo(2));

        softwareStepCounter.SensorUpdate(higherValue);
        assertThat(softwareStepCounter.GetSteps(), equalTo(2));
        softwareStepCounter.SensorUpdate(lowerValue);
        assertThat(softwareStepCounter.GetSteps(), equalTo(4));
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

    @Test
    public void WhenThreeSetsAreSentAllYValuesWithAPeakThenTheCountGoesUpByTwo()
    {
        float valueForFourG = CalculateValueForGivenGValue(4);
        float yPeak[] = {0, valueForFourG, 0};
        float lowValue[] = {0, 0, 0};
        softwareStepCounter.SensorUpdate(lowValue);
        softwareStepCounter.SensorUpdate(yPeak);
        softwareStepCounter.SensorUpdate(lowValue);

        assertThat(softwareStepCounter.GetSteps(), equalTo(2));
    }

    @Test
    public void WhenThreeSetsAreSentAllZValuesWithAPeakThenTheCountGoesUpByTwo()
    {
        float valueForFourG = CalculateValueForGivenGValue(4);
        float zPeak[] = {0, 0, valueForFourG};
        float lowValue[] = {0, 0, 0};
        softwareStepCounter.SensorUpdate(lowValue);
        softwareStepCounter.SensorUpdate(zPeak);
        softwareStepCounter.SensorUpdate(lowValue);

        assertThat(softwareStepCounter.GetSteps(), equalTo(2));
    }

    @Test
    public void WhenThePeakIsLessThanTwoGForceThenNoStepsGenerated()
    {
        float gForce = (float) 1.9;
        float componentValue = CalculateForceToApplyOnEachAxisToGiveGValue(gForce);
        float peak[] = {componentValue, componentValue, componentValue};
        float lowValue[] = {0, 0, 0};

        softwareStepCounter.SensorUpdate(lowValue);
        softwareStepCounter.SensorUpdate(peak);
        softwareStepCounter.SensorUpdate(lowValue);

        assertThat(softwareStepCounter.GetSteps(), equalTo(0));
    }

    @Test
    public void WhenThePeakIsMoreThanTwoGForceThenNoStepsGenerated()
    {
        float gForce = (float) 2.01;
        float componentValue = CalculateForceToApplyOnEachAxisToGiveGValue(gForce);
        float peak[] = {componentValue, componentValue, componentValue};
        float lowValue[] = {0, 0, 0};

        softwareStepCounter.SensorUpdate(lowValue);
        softwareStepCounter.SensorUpdate(peak);
        softwareStepCounter.SensorUpdate(lowValue);

        assertThat(softwareStepCounter.GetSteps(), equalTo(2));
    }

    @Test
    public void WhenValuesPassedToStepCounterAreNotThreeAxisThenNoStepsAreGenerated()
    {
        float peak[] ={15};
        float low[] = {0};

        softwareStepCounter.SensorUpdate(low);
        softwareStepCounter.SensorUpdate(peak);
        softwareStepCounter.SensorUpdate(low);

        assertThat(softwareStepCounter.GetSteps(), equalTo(0));
    }
}
