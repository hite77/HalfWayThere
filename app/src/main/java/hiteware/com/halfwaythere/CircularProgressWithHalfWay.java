package hiteware.com.halfwaythere;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created on 5/1/15.
 */

public class CircularProgressWithHalfWay extends View {
     private float strokeWidth = 4;
    private float progress = 0;
    private int min = 0;
    private int max = 100;
    private int startAngle = -90;
    private int color = Color.GREEN;
    private int backgroundColor = Color.DKGRAY;
    private RectF rectF;
    private Paint backgroundPaint;
    private Paint foregroundPaint;

    public CircularProgressWithHalfWay(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        rectF = new RectF();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressBarWithHalfWay, 0, 0);
        try {
            strokeWidth = typedArray.getDimension(R.styleable.CircleProgressBarWithHalfWay_progressBarThickness, strokeWidth);
            progress = typedArray.getFloat(R.styleable.CircleProgressBarWithHalfWay_progress, progress);
            color = typedArray.getInt(R.styleable.CircleProgressBarWithHalfWay_progressbarColor, color);
            min = typedArray.getInt(R.styleable.CircleProgressBarWithHalfWay_min, min);
            max = typedArray.getInt(R.styleable.CircleProgressBarWithHalfWay_max, max);
        } finally {
            typedArray.recycle();
        }

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        backgroundPaint.setColor(adjustAlpha(backgroundColor, 0.3f));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);

        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(color);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);
    }

    private int adjustAlpha(int color, float factor)
    {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        rectF.set(0 + strokeWidth / 2, 0 + strokeWidth / 2, min - strokeWidth / 2, min - strokeWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawOval(rectF, backgroundPaint);
        float angle = 360 * progress / max;
        canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint);
    }

    public void setProgress(float progress)
    {
        this.progress = progress;
        invalidate();
    }
}