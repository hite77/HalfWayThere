package hiteware.com.halfwaythere;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.anyFloat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created on 6/15/15.
 */
public class ProgressUpdateUnitTest {

    @Test
    public void WhenSetStatusIsSetThenCircularProgressGetsUpdated()
    {
        ProgressUpdate progressUpdate = new ProgressUpdate();
        CircularProgressWithHalfWay progressIndication = Mockito.mock(CircularProgressWithHalfWay.class);
        progressUpdate.SetCircularProgress(progressIndication);

        progressUpdate.SetSteps(10);
        progressUpdate.SetGoal(100);
        verify(progressIndication, times(1)).setProgress(10);
    }

    @Test
    public void WhenValueIsGreaterThanOneHundredPercentThenItIsOneHundred()
    {
        ProgressUpdate progressUpdate = new ProgressUpdate();
        CircularProgressWithHalfWay progressIndication = Mockito.mock(CircularProgressWithHalfWay.class);
        progressUpdate.SetCircularProgress(progressIndication);

        progressUpdate.SetSteps(110);
        progressUpdate.SetGoal(100);

        verify(progressIndication, times(1)).setProgress(100);
    }

    @Test
    public void WhenGoalIsOnlySetThenNoProgressUpdate()
    {
        ProgressUpdate progressUpdate = new ProgressUpdate();
        CircularProgressWithHalfWay progressIndication = Mockito.mock(CircularProgressWithHalfWay.class);
        progressUpdate.SetCircularProgress(progressIndication);

        progressUpdate.SetGoal(0);
        verify(progressIndication, times(0)).setProgress(anyFloat());
    }

    @Test
    public void WhenStepIsOnlySetThenNoProgressUpdate()
    {
        ProgressUpdate progressUpdate = new ProgressUpdate();
        CircularProgressWithHalfWay progressIndication = Mockito.mock(CircularProgressWithHalfWay.class);
        progressUpdate.SetCircularProgress(progressIndication);

        progressUpdate.SetSteps(0);
        verify(progressIndication, times(0)).setProgress(anyFloat());
    }

    @Test
    public void WhenNoCircularProgressSetThenNoUpdates()
    {
        CircularProgressWithHalfWay progressIndication = Mockito.mock(CircularProgressWithHalfWay.class);
        ProgressUpdate progressUpdate = new ProgressUpdate();
        progressUpdate.SetSteps(110);
        progressUpdate.SetGoal(100);
        verify(progressIndication, times(0)).setProgress(anyFloat());
    }
}
