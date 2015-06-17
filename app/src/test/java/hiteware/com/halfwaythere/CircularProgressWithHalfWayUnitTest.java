package hiteware.com.halfwaythere;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created on 6/16/15.
 */

public class CircularProgressWithHalfWayUnitTest {
    CircularProgressWithHalfWay progress;
    Canvas mockCanvas;

    @Before
    public void Setup()
    {
        Context context = Mockito.mock(Context.class);
        progress = new CircularProgressWithHalfWay(context);
        mockCanvas = Mockito.mock(Canvas.class);
    }

    @SuppressLint("WrongCall") //onDraw uses the passed canvas, draw does not.
    private void DrawToMockCanvas()
    {
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
}
