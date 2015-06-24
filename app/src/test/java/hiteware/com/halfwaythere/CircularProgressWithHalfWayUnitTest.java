package hiteware.com.halfwaythere;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created on 6/16/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class CircularProgressWithHalfWayUnitTest {
    private CircularProgressWithHalfWay progress;
    private Canvas mockCanvas;

    class LineEndpoints {
        public final float centerX;
        public final float centerY;
        public final float stopX;
        public final float stopY;

        public LineEndpoints(float angle, RectF rectF)
        {
            centerX = rectF.centerX();
            centerY = rectF.centerY();

            float radius = centerY-rectF.top;
            float angleRadians = (float)(angle*(3.14156)/180);

            stopX = centerX+radius*(float)Math.sin(angleRadians);
            stopY = centerY-radius*(float)Math.cos(angleRadians);
        }
    }

    @Before
    public void Setup()
    {
        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();
        AttributeSet attributes = Mockito.mock(AttributeSet.class);

        progress = new CircularProgressWithHalfWay(createdActivity, attributes);
        mockCanvas = Mockito.mock(Canvas.class);
    }


    @SuppressLint("WrongCall") //onDraw uses the passed canvas, draw does not.
    private void DrawToMockCanvas()
    {
        progress.onMeasure(300, 300);
        progress.onDraw(mockCanvas);
    }

    @Test
    public void WhenPercentageIsSetToZeroThenArcIsDrawnWithZeroAngle()
    {
        progress.setProgress(0);
        DrawToMockCanvas();

        float angle = 0;
        verify(mockCanvas, times(1)).drawArc(any(RectF.class), anyFloat(), eq(angle), anyBoolean(), any(Paint.class));
    }

    @Test
    public void WhenPercentageIsSetToTwentyFivePercentThenArcIsDrawnWithNinetyAngle()
    {
        progress.setProgress(25);
        DrawToMockCanvas();

        float angle = 90;
        verify(mockCanvas, times(1)).drawArc(any(RectF.class),anyFloat(),eq(angle),anyBoolean(),any(Paint.class));
    }

    @Test
    public void WhenPercentageIsSetToFiftyFivePercentThenArcIsDrawnWithOneEightyAngle()
    {
        progress.setProgress(50);
        DrawToMockCanvas();

        float angle = 180;
        verify(mockCanvas, times(1)).drawArc(any(RectF.class),anyFloat(),eq(angle),anyBoolean(),any(Paint.class));
    }

    @Test
    public void WhenProgressArcIsDrawnThenTheStartAngleIsMinusNinety()
    {
        DrawToMockCanvas();
        float startAngle = -90;
        verify(mockCanvas, times(1)).drawArc(any(RectF.class),eq(startAngle),anyFloat(),anyBoolean(),any(Paint.class));
    }

    @Test
    public void WhenProgressArcIsDrawnTheTheConnectedArgumentIsFalseToJustDrawAnArc()
    {
        DrawToMockCanvas();
        verify(mockCanvas, times(1)).drawArc(any(RectF.class),anyFloat(),anyFloat(),eq(false),any(Paint.class));
    }

    @Test
    public void WhenCanvasIsDrawnTheCircleIsDrawnAroundTheEntireCircle()
    {
        DrawToMockCanvas();
        verify(mockCanvas).drawOval(any(RectF.class), any(Paint.class));
    }

    @Test
    public void WhenAngleIsSetForHalfWayThenLineIsDrawnAtTheAngleSpecified()
    {
        float angleForHalfWay = 180;
        progress.setHalfWay(angleForHalfWay);

        DrawToMockCanvas();
        ArgumentCaptor<RectF> rectF = ArgumentCaptor.forClass(RectF.class);
        verify(mockCanvas).drawOval(rectF.capture(), any(Paint.class));

        LineEndpoints lineEndpoints = new LineEndpoints(angleForHalfWay,rectF.getValue());

        verify(mockCanvas).drawLine(
                eq(lineEndpoints.centerX),
                eq(lineEndpoints.centerY),
                eq(lineEndpoints.stopX),
                eq(lineEndpoints.stopY),
                any(Paint.class));
    }

    @Test
    public void WhenAngleIsSetToAnArbitraryAngleThenLineIsDrawnAtTheAngleSpecified()
    {
        float angle = 115;
        progress.setHalfWay(angle);

        DrawToMockCanvas();
        ArgumentCaptor<RectF> rectF = ArgumentCaptor.forClass(RectF.class);
        verify(mockCanvas).drawOval(rectF.capture(), any(Paint.class));

        LineEndpoints lineEndpoints = new LineEndpoints(angle,rectF.getValue());

        verify(mockCanvas).drawLine(
                eq(lineEndpoints.centerX),
                eq(lineEndpoints.centerY),
                eq(lineEndpoints.stopX),
                eq(lineEndpoints.stopY),
                any(Paint.class));
    }

    @Test
    public void WhenAngleIsNotSetForHalfWayThenLineIsNotDrawn()
    {
        DrawToMockCanvas();

        verify(mockCanvas, times(0)).drawLine(anyFloat(),anyFloat(),anyFloat(),anyFloat(),any(Paint.class));
    }

    @Test
    public void WhenAngleIsSetAndThenIsClearedThenLineIsNotDrawn()
    {
        progress.setHalfWay(15);
        progress.clearHalfWay();

        DrawToMockCanvas();
        verify(mockCanvas, times(0)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), any(Paint.class));
    }
}
