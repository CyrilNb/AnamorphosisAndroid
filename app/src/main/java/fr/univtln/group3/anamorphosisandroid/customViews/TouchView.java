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
import java.util.List;

public class TouchView extends View {

    float x;
    float y;

    Paint paint;
    Path path;

    Point previousPoint = new Point(0,0);
    List<Point> curvePoints;

    /**
     * CONSTRUCTOR OF TOUCHVIEW
     * @param context
     * @param attrs
     */
    public TouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        curvePoints = new ArrayList<>();
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(6);
        setWillNotDraw(false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        x = event.getX();
        y = event.getY();
        Point currentPoint = new Point((int)x,(int)y);

        if(!previousPoint.equals(currentPoint)){
            curvePoints.add(currentPoint);
            System.out.println(currentPoint);
            previousPoint = currentPoint;
        }


        switch (event.getAction()){
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
     * @return list of points of the curve
     */
    public List<Point> getCurvePoints() {
        return curvePoints;
    }
}