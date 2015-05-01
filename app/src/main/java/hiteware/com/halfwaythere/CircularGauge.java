package hiteware.com.halfwaythere;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by jasonhite on 5/1/15.
 */
public class CircularGauge extends View
{
    Paint paint = new Paint();

    public CircularGauge(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth((float) 0.0);

        // first drawing to draw box around everything

        //top line
        canvas.drawLine(0, 0, (float)canvas.getWidth(), 0, paint);
        //right line
        canvas.drawLine((float)canvas.getWidth(), 0, (float)canvas.getWidth(), (float)canvas.getHeight(), paint);
        // bottom line
        canvas.drawLine((float)canvas.getWidth(), (float)canvas.getHeight(),
                0, (float)canvas.getHeight(), paint);
        // left line
        canvas.drawLine(0, (float)canvas.getHeight(), 0, 0, paint);
    }
}