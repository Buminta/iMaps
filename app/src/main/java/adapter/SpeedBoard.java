package adapter;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import net.windjs.imaps.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by me866chuan on 9/12/14.
 */
public class SpeedBoard extends View {

    private static final String TAG = SpeedBoard.class.getSimpleName();

    public static final double DEFAULT_MAX_SPEED = 100.0;
    public static final double DEFAULT_MAJOR_TICK_STEP = 20.0;
    public static final int DEFAULT_MINOR_TICKS = 4;
    public static final int DEFAULT_MINOR = 10;
    public static final int DEFAULT_LABEL_TEXT_SIZE_DP = 40;

    private double maxSpeed = DEFAULT_MAX_SPEED;
    private float maxMinor = DEFAULT_MINOR;
    private double speed = 0;
    private int defaultColor = Color.rgb(180, 180, 180);
    private double majorTickStep = DEFAULT_MAJOR_TICK_STEP;
    private int minorTicks = DEFAULT_MINOR_TICKS;
    private LabelConverter labelConverter;

    private Paint backgroundInnerPaint;
    private Paint needlePaint;
    private Paint ticksPaint;
    private Paint txtPaint;
    private Paint colorLinePaint;
    private int labelTextSize;
    private Paint backgroundneedlePaint;
    private Paint backgroundmeedlePaint;
    private Paint needlePaintBorder;
    private Paint ticksMinPaint;
    private Paint speedPaint;
    private Paint speedPaintKm;


    public SpeedBoard(Context context) {
        super(context);
        init();

        float density = getResources().getDisplayMetrics().density;
        setLabelTextSize(Math.round(DEFAULT_LABEL_TEXT_SIZE_DP * density));
    }

    public SpeedBoard(Context context, AttributeSet attrs) {
        super(context, attrs);

        float density = getResources().getDisplayMetrics().density;
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SpeedometerView,
                0, 0);

        try {
            // read attributes
            setMaxSpeed(attributes.getFloat(R.styleable.SpeedometerView_maxSpeed, (float) DEFAULT_MAX_SPEED));
            setSpeed(attributes.getFloat(R.styleable.SpeedometerView_speed, 0));
            setLabelTextSize(attributes.getDimensionPixelSize(R.styleable.SpeedometerView_labelTextSize, Math.round(DEFAULT_LABEL_TEXT_SIZE_DP * density)));
        } finally {
            attributes.recycle();
        }
        init();
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        if (maxSpeed <= 0)
            throw new IllegalArgumentException("Non-positive value specified as max speed.");
        this.maxSpeed = maxSpeed;
        invalidate();
    }

    public void setMaxMinor(int i){
        if (i <= 0)
            throw new IllegalArgumentException("Non-positive value specified as max speed.");
        this.maxMinor = i;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        if (speed < 0)
            throw new IllegalArgumentException("Non-positive value specified as a speed.");
        if (speed > maxSpeed)
            speed = maxSpeed;
        this.speed = speed;
        invalidate();
    }

    @TargetApi(11)
    public ValueAnimator setSpeed(double progress, long duration, long startDelay) {
        if (progress <= 0)
            throw new IllegalArgumentException("Non-positive value specified as a speed.");

        if (progress > maxSpeed)
            progress = maxSpeed;

        ValueAnimator va = ValueAnimator.ofObject(new TypeEvaluator<Double>() {
            @Override
            public Double evaluate(float fraction, Double startValue, Double endValue) {
                return startValue + fraction*(endValue-startValue);
            }
        }, Double.valueOf(getSpeed()), Double.valueOf(progress));

        va.setDuration(duration);
        va.setStartDelay(startDelay);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Double value = (Double) animation.getAnimatedValue();
                if (value != null)
                    setSpeed(value);
            }
        });
        va.start();
        return va;
    }

    @TargetApi(11)
    public ValueAnimator setSpeed(double progress, boolean animate) {
        return setSpeed(progress, 1500, 200);
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
        invalidate();
    }

    public double getMajorTickStep() {
        return majorTickStep;
    }

    public void setMajorTickStep(double majorTickStep) {
        if (majorTickStep <= 0)
            throw new IllegalArgumentException("Non-positive value specified as a major tick step.");
        this.majorTickStep = majorTickStep;
        invalidate();
    }

    public int getMinorTicks() {
        return minorTicks;
    }

    public void setMinorTicks(int minorTicks) {
        this.minorTicks = minorTicks;
        invalidate();
    }

    public LabelConverter getLabelConverter() {
        return labelConverter;
    }

    public void setLabelConverter(LabelConverter labelConverter) {
        this.labelConverter = labelConverter;
        invalidate();
    }

    public int getLabelTextSize() {
        return labelTextSize;
    }

    public void setLabelTextSize(int labelTextSize) {
        this.labelTextSize = labelTextSize;
        if (txtPaint != null) {
            txtPaint.setTextSize(labelTextSize);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Clear canvas
        canvas.drawColor(Color.TRANSPARENT);

        // Draw Metallic Arc and background
        drawBackground(canvas);

        // Draw Ticks and colored arc
        drawTicks(canvas);

        // Draw Needle
        drawNeedle(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
            //Must be this size
            width = widthSize;
        } else {
            width = -1;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
            //Must be this size
            height = heightSize;
        } else {
            height = -1;
        }

        if (height >= 0 && width >= 0) {
            width = Math.min(height, width);
            height = width;
        } else if (width >= 0) {
            height = width;
        } else if (height >= 0) {
            width = height;
        } else {
            width = 0;
            height = 0;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    private void drawNeedle(Canvas canvas) {
        float majorTicksLength = 30;
        RectF oval = getOval(canvas, 1);
        float radius = oval.width()/2.0f-majorTicksLength*1.5f;


        float angle = 115 + (float) (getSpeed()*260/ getMaxSpeed());

        canvas.save();
        canvas.rotate(angle, oval.centerX(), oval.centerY());
        canvas.drawLine(
                oval.centerX(),
                oval.centerY(),
                oval.centerX() + radius,
                oval.centerY(),
                needlePaintBorder
        );
        canvas.drawLine(
                oval.centerX(),
                oval.centerY(),
                oval.centerX() + radius,
                oval.centerY(),
                needlePaint
        );
        canvas.restore();

        RectF innerOval = getOval(canvas, 0.1f);
        canvas.drawArc(innerOval, 0, 360, true, backgroundneedlePaint);

        canvas.save();
        canvas.drawText(String.valueOf((float) getSpeed()), oval.centerX()+oval.width()/4, oval.centerY()+oval.width()/4, speedPaint);
        canvas.drawText("Km/h", oval.centerX()+oval.width()/4, oval.centerY()+oval.width()/4, speedPaintKm);
        canvas.restore();
    }

    private void drawTicks(Canvas canvas) {
        float availableAngle = 260;
        float majorStep = (float) (availableAngle/maxMinor);
        int majorTicks = (1 + minorTicks);
        float minorStep = majorStep/majorTicks;

        float majorTicksLength = 30;
        float minorTicksLength = majorTicksLength/4;

        RectF oval = getOval(canvas, 1);
        float radius = oval.width()/2.0f;

        float currentAngle = 115;
        float curProgress = 0;
        float majorSpeed = (float)maxSpeed/maxMinor;

        while (currentAngle <= 375) {

            canvas.drawLine(
                    (float) (oval.centerX() + Math.cos(Math.toRadians(currentAngle))*(radius-majorTicksLength)),
                    (float) (oval.centerY() + Math.sin(Math.toRadians(currentAngle))*(radius-majorTicksLength)),
                    (float) (oval.centerX() + Math.cos(Math.toRadians(currentAngle))*radius),
                    (float) (oval.centerY() + Math.sin(Math.toRadians(currentAngle))*radius),
                    ticksPaint
            );

            float maxAngle = currentAngle + majorStep;

            for(float i=currentAngle+minorStep; i<maxAngle; i+=minorStep){
                if(currentAngle >= 375){
                    break;
                }
                canvas.drawLine(
                        (float) (oval.centerX() + Math.cos(Math.toRadians(i))*(radius-majorTicksLength+minorTicksLength)),
                        (float) (oval.centerY() + Math.sin(Math.toRadians(i))*(radius-majorTicksLength+minorTicksLength)),
                        (float) (oval.centerX() + Math.cos(Math.toRadians(i))*(radius-minorTicksLength)),
                        (float) (oval.centerY() + Math.sin(Math.toRadians(i))*(radius-minorTicksLength)),
                        ticksMinPaint
                );
            }

            if (labelConverter != null) {

                canvas.save();
                canvas.rotate(currentAngle, oval.centerX(), oval.centerY());
                float txtX = oval.centerX() + radius - majorTicksLength - labelTextSize*1.5f;
                float txtY = oval.centerY();
                canvas.rotate(+90, txtX, txtY);
                canvas.drawText(labelConverter.getLabelFor(curProgress, maxSpeed), txtX, txtY, txtPaint);
                canvas.restore();
            }

            currentAngle = maxAngle;
            curProgress += majorSpeed;
        }

    }

    private RectF getOval(Canvas canvas, float factor) {
        RectF oval;
        final int canvasWidth = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
        final int canvasHeight = canvas.getHeight() - getPaddingTop() - getPaddingBottom();

        if (canvasHeight >= canvasWidth) {
            oval = new RectF(0, 0, canvasWidth*factor, canvasWidth*factor);
        } else {
            oval = new RectF(0, 0, canvasHeight*factor, canvasHeight*factor);
        }

        oval.offset((canvasWidth-oval.width())/2 + getPaddingLeft(), (canvasHeight-oval.height())/2 + getPaddingTop());

        return oval;
    }

    private void drawBackground(Canvas canvas) {
        RectF innerOval = getOval(canvas, 0.6f);
        canvas.drawArc(innerOval, 0, 360, true, backgroundInnerPaint);

        RectF smallOval = getOval(canvas, 0.2f);
        canvas.drawArc(smallOval, 0, 360, true, backgroundmeedlePaint);
    }

    @SuppressWarnings("NewApi")
    private void init() {
        if (Build.VERSION.SDK_INT >= 11 && !isInEditMode()) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        backgroundInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundInnerPaint.setStyle(Paint.Style.FILL);
        backgroundInnerPaint.setColor(Color.parseColor("#ff6861"));

        backgroundneedlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundneedlePaint.setStyle(Paint.Style.FILL);
        backgroundneedlePaint.setColor(Color.parseColor("#445569"));

        backgroundmeedlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundmeedlePaint.setStyle(Paint.Style.FILL);
        backgroundmeedlePaint.setColor(Color.parseColor("#f1645b"));

        speedPaintKm = new Paint(Paint.ANTI_ALIAS_FLAG);
        speedPaintKm.setColor(Color.parseColor("#55445569"));
        speedPaintKm.setTextSize(labelTextSize);
        speedPaintKm.setTextAlign(Paint.Align.LEFT);

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.parseColor("#445569"));
        txtPaint.setTextSize(labelTextSize);
        txtPaint.setTextAlign(Paint.Align.CENTER);

        speedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        speedPaint.setColor(Color.parseColor("#55445569"));
        speedPaint.setTextSize(80);
        speedPaint.setTextAlign(Paint.Align.RIGHT);

        ticksPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ticksPaint.setStrokeWidth(3.0f);
        ticksPaint.setStyle(Paint.Style.STROKE);
        ticksPaint.setColor(Color.parseColor("#445569"));

        ticksMinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ticksMinPaint.setStrokeWidth(1.0f);
        ticksMinPaint.setStyle(Paint.Style.STROKE);
        ticksMinPaint.setColor(Color.parseColor("#dbdbdb"));


        colorLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colorLinePaint.setStyle(Paint.Style.STROKE);
        colorLinePaint.setStrokeWidth(5);
        colorLinePaint.setColor(defaultColor);

        needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        needlePaint.setStrokeWidth(3);
        needlePaint.setStyle(Paint.Style.STROKE);
        needlePaint.setColor(Color.parseColor("#ff6861"));

        needlePaintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        needlePaintBorder.setStrokeWidth(5);
        needlePaintBorder.setStyle(Paint.Style.STROKE);
        needlePaintBorder.setColor(Color.parseColor("#ffffff"));
    }


    public static interface LabelConverter {

        String getLabelFor(double progress, double maxProgress);

    }

    public static class ColoredRange {

        private int color;
        private double begin;
        private double end;

        public ColoredRange(int color, double begin, double end) {
            this.color = color;
            this.begin = begin;
            this.end = end;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public double getBegin() {
            return begin;
        }

        public void setBegin(double begin) {
            this.begin = begin;
        }

        public double getEnd() {
            return end;
        }

        public void setEnd(double end) {
            this.end = end;
        }
    }

}