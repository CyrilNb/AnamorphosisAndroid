package fr.univtln.group3.anamorphosisandroid.customViews;

/**
 * Created by Cyril Niobé on 22/05/2018.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Custom view with to draw points on canvas.
 */
public class TouchView extends View {

    /***********
     * MEMBERS *
     ***********/
    float x;
    float y;
    int canvasWidth = -1;
    int canvasHeight = -1;
    static boolean needReset = false;
    Paint paint;
    Path path;
    Point previousPoint = new Point(0, 0);
    ArrayList<Point> curvePoints;

    /**
     * CONSTRUCTOR OF TOUCHVIEW
     *
     * @param context
     * @param attrs
     */
    public TouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        curvePoints = new ArrayList<>();
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setAntiAlias(false);
        paint.setColor(Color.RED);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(20);
        setWillNotDraw(false);
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    /**
     * OnDraw method called every time the user is touching the canvas to draw points
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (needReset) {
            path.reset();
            needReset = false;
        }
        canvas.drawPath(path, paint);
        if(canvasHeight == -1){
            canvasHeight = canvas.getHeight();
        }

        if(canvasWidth == -1){
            canvasWidth = canvas.getWidth();
        }
        System.out.println(canvas.getWidth()+" "+canvas.getHeight());
    }

    /**
     * Clears the canvas and the list of point
     * by forcing onDraw to be triggered with needReset
     */
    public void resetCanvas() {
        needReset = true;
        this.getCurvePoints().clear();
        invalidate();
    }

    /**
     * onToucheEvent method to handle all user touch events
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        Point currentPoint = new Point((int) x, (int) y);

        if (!previousPoint.equals(currentPoint)) {
            curvePoints.add(currentPoint);
            System.out.println(currentPoint);
            previousPoint = currentPoint;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    /**
     * GETTER of curvePoints
     *
     * @return list of points of the curve
     */
    public ArrayList<Point> getCurvePoints() {
        return curvePoints;
    }
}