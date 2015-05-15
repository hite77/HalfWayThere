package hiteware.com.halfwaythere;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jasonhite on 5/14/15.
 */
public class StepServiceUnitTest
{
    @Test
    public void GivenStepServiceCreatedWhenStepsAreSetThenStepsCanBeRetrieved()
    {
        StepService stepService = new StepService();
        stepService.onCreate();

        float expectedValue = 14;
        stepService.setSteps(expectedValue);

        assertThat(stepService.getSteps(), equalTo(expectedValue));
    }
}
