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
    boolean needReset = false;
    boolean isDiagonalMode = false;
    Paint paint;
    Point currentPoint;
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
        paint.setStrokeWidth(60);
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
        System.out.println("debut");
        if (needReset) {
            path.reset();
            needReset = false;
            if(currentPoint != null){
                paint.setColor(Color.WHITE);
                canvas.drawPoint(currentPoint.x,currentPoint.y,paint);
            }
        }

        if (canvasHeight == -1) {
            canvasHeight = canvas.getHeight();
        }

        if (canvasWidth == -1) {
            canvasWidth = canvas.getWidth();
        }

        paint.setColor(Color.RED);
        if (isDiagonalMode) {
            paint.setStrokeWidth(50);
            if (this.getCurvePoints().size() <= 2) {
                //canvas.drawPath(path, paint);
                if(currentPoint != null){
                    canvas.drawPoint(currentPoint.x,currentPoint.y,paint);
                }
                if (this.getCurvePoints().size() == 2) {
                    canvas.drawLine(this.getCurvePoints().get(0).x, this.getCurvePoints().get(0).y, this.getCurvePoints().get(1).x, this.getCurvePoints().get(1).y, this.paint);
                }
            }
        } else {
            System.out.println("avan draw");
            canvas.drawPath(path, paint);
            System.out.println("apres drax");
        }
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
        currentPoint = new Point((int) x, (int) y);

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

    /**
     * SETTER of diagonalMode
     *
     * @param diagonalMode true if user selected Diagonal Mode
     */
    public void setDiagonalMode(boolean diagonalMode) {
        isDiagonalMode = diagonalMode;
    }
}