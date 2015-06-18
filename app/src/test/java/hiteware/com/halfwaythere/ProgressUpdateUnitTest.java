package hiteware.com.halfwaythere;

import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
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

    @Test
    public void WhenClearHalfWayThenCircularProgressIsCleared()
    {
        CircularProgressWithHalfWay progressIndication = Mockito.mock(CircularProgressWithHalfWay.class);
        ProgressUpdate progressUpdate = new ProgressUpdate();
        progressUpdate.SetCircularProgress(progressIndication);

        progressUpdate.ClearHalfWay();

        verify(progressIndication).clearHalfWay();
    }

    @Test
    public void WhenSetHalfWayIsCalledWithValueAndGoalIsSetThenHalfWayIsCalledWithAngleAndSetReturnsTrue()
    {
        CircularProgressWithHalfWay progressIndication = Mockito.mock(CircularProgressWithHalfWay.class);
        ProgressUpdate progressUpdate = new ProgressUpdate();
        progressUpdate.SetCircularProgress(progressIndication);

        progressUpdate.SetGoal(10000);
        boolean isOk = progressUpdate.SetHalfWay(7000);

        float expected = ((float)7000/10000) * 360;

        verify(progressIndication).setHalfWay(expected);
        assertThat(isOk, equalTo(true));
    }

    @Test
    public void WhenSetHalfWayIsCalledWithAnotherValueAndGoalIsSetThenHalfWayIsCalledWithAngleAndSetReturnsTrue()
    {
        CircularProgressWithHalfWay progressIndication = Mockito.mock(CircularProgressWithHalfWay.class);
        ProgressUpdate progressUpdate = new ProgressUpdate();
        progressUpdate.SetCircularProgress(progressIndication);

        progressUpdate.SetGoal(1000);
        boolean isOk = progressUpdate.SetHalfWay(900);

        float expected = ((float)900/1000) * 360;

        verify(progressIndication).setHalfWay(expected);
        assertThat(isOk, equalTo(true));
    }

    @Test
    public void WhenSetHalfWayIsCalledWithGoalOfZeroThenHalfWayIsNotSetReturnsFalse()
    {
        CircularProgressWithHalfWay progressIndication = Mockito.mock(CircularProgressWithHalfWay.class);
        ProgressUpdate progressUpdate = new ProgressUpdate();
        progressUpdate.SetCircularProgress(progressIndication);

        progressUpdate.SetGoal(0);
        boolean isOk = progressUpdate.SetHalfWay(15);

        verify(progressIndication, times(0)).setHalfWay(anyFloat());
        assertThat(isOk, equalTo(false));
    }

    @Test
    public void WhenHalfWayPointIsGreaterThanGoalThenHalfWayIsNotSetAndReturnsFalse()
    {
        CircularProgressWithHalfWay progressIndication = Mockito.mock(CircularProgressWithHalfWay.class);
        ProgressUpdate progressUpdate = new ProgressUpdate();
        progressUpdate.SetCircularProgress(progressIndication);

        progressUpdate.SetGoal(10000);
        boolean isOk = progressUpdate.SetHalfWay(15000);

        verify(progressIndication, times(0)).setHalfWay(anyFloat());
        assertThat(isOk, equalTo(false));
    }

    @Test
    public void WhenCircularProgressIsNotSetAndSetHalfWayIsCalledThenHalfWayReturnsFalse()
    {
        ProgressUpdate progressUpdate = new ProgressUpdate();

        progressUpdate.SetGoal(10000);
        boolean isOk = progressUpdate.SetHalfWay(5000);

        assertThat(isOk, equalTo(false));
    }
}
