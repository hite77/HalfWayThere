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
    private int color = Color.GREEN;
    private RectF rectF;
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private float halfWay;
    private boolean halfWaySet = false;

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

        int backgroundColor = Color.DKGRAY;
        backgroundPaint.setColor(adjustAlpha(backgroundColor));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);

        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(color);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);
    }

    private int adjustAlpha(int color)
    {
        int alpha = Math.round(Color.alpha(color) * 0.3f);
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawOval(rectF, backgroundPaint);
        float angle = 360 * progress / max;
        int startAngle = -90;
        canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint);

        if (halfWaySet) {
            DrawHalfWayLine(canvas);
        }
    }

    private void DrawHalfWayLine(Canvas canvas) {
        float angleRadian = (float) (halfWay * (3.14156) / 180);
        float radius = rectF.centerY() - rectF.top;
        canvas.drawLine(rectF.centerX(),
                rectF.centerY(),
                rectF.centerX() + radius * ((float) Math.sin(angleRadian)),
                rectF.centerY() - radius * ((float) Math.cos(angleRadian)),
                foregroundPaint);
    }

    public void setProgress(float progress)
    {
        this.progress = progress;
        invalidate();
    }

    public void setHalfWay(float halfWay)
    {
        this.halfWay = halfWay;
        halfWaySet = true;
        invalidate();
    }

    public void clearHalfWay() {
        halfWaySet = false;
    }
}
