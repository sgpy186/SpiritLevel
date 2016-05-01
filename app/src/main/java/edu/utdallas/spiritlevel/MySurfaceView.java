package edu.utdallas.spiritlevel;

/**
 * Created by kriszhang on 4/21/16.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements
        SurfaceHolder.Callback  {

    private DrawThread drawThread;
    private Paint paint1 = new Paint();
    private Paint paint2 = new Paint();
    private static Point location;
    Paint paint = new Paint();
    Paint perpPaint = new Paint();
    static boolean isSleep = false;
    public MySurfaceView(Context context) {
        super(context);
        initialize();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        getHolder().addCallback(this);
        setFocusable(true);
        paint1.setColor(Color.parseColor("#907FFFD4"));
        paint1.setStrokeWidth(1);
        paint1.setAntiAlias(true);
        paint1.setStrokeCap(Paint.Cap.SQUARE);
        paint1.setStyle(Paint.Style.FILL);

        paint2.setColor(Color.parseColor("#90F9A7B0"));
        paint2.setStrokeWidth(1);
        paint2.setAntiAlias(true);
        paint2.setStrokeCap(Paint.Cap.SQUARE);
        paint2.setStyle(Paint.Style.FILL);

        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStyle(Paint.Style.FILL);

        perpPaint.setColor(Color.parseColor("#000000"));
        perpPaint.setStrokeWidth(10);
        perpPaint.setAntiAlias(true);
        perpPaint.setStrokeCap(Paint.Cap.SQUARE);
        perpPaint.setStyle(Paint.Style.FILL);

        location = new Point(0, 0);
    }

    public void startThread() {
        drawThread = new DrawThread(getHolder(), this);
        drawThread.setRunning(true);
        drawThread.start();
    }

    public void stopThread() {
        drawThread.setRunning(false);
        drawThread.interrupt();
    }

    public void update(int x,int y) {
        location.x = x; // Z angle
        location.y = y; // X angle
    }

    public void onDraw(Canvas canvas) {
        isSleep = false;
        if (Math.abs(location.x) <= 10 && Math.abs(location.y) <=10){
            canvas.drawColor(Color.parseColor("#5EFB6E"));
            canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, 400, paint);
            isSleep = true;
        } else if(Math.abs(location.y) >= 500){
            canvas.drawColor(Color.parseColor("#FFFFFF"));
            double angle = Math.abs(Math.toRadians(location.x/10));
            Point start = new Point(this.getWidth()/2, 0);
            Point end = new Point();
            if (location.x < 0) {
                end.x = (int) (this.getWidth()/2 + 1000*Math.sin(angle));
            } else {
                end.x = (int) (this.getWidth()/2 - 1000*Math.sin(angle));
            }
            end.y = (int) (1000*Math.cos(angle));
            canvas.drawLine(start.x, start.y, end.x, end.y, perpPaint);
//        } else if (location.y <= -500) {
//            canvas.drawColor(Color.parseColor("#FFFFFF"));
//            double angle = Math.abs(Math.toRadians(location.x/10));
//            Point start = new Point(this.getWidth()/2, -this.getHeight());
//            Point end = new Point();
//            if (location.x > 0) {
//                end.x = (int) (this.getWidth()/2 + 1000*Math.sin(angle));
//            } else {
//                end.x = (int) (this.getWidth()/2 - 1000*Math.sin(angle));
//            }
//            end.y = (int) (this.getHeight() - 1000*Math.cos(angle));
//            canvas.drawLine(start.x, start.y, end.x, end.y, perpPaint);
//        } else if (Math.abs(location.x) >= 500) {
//            canvas.drawColor(Color.parseColor("#FFFFFF"));
//            canvas.drawLine(this.getWidth(), this.getHeight()/2, this.getWidth(), -location.y, perpPaint);
        } else {
            canvas.drawColor(Color.parseColor("#FEFCFF"));
            canvas.drawCircle(this.getWidth() / 2 + 2*location.x, this.getHeight() / 2 - 2*location.y, 400, paint1);
            canvas.drawCircle(this.getWidth()/2 - 2*location.x, this.getHeight()/2 + 2*location.y, 400, paint2);
        }

    }

    class DrawThread extends Thread {
        private SurfaceHolder surfaceHolder;
        MySurfaceView mySurfaceView;
        private boolean run = false;

        public DrawThread(SurfaceHolder surfaceHolder,
                          MySurfaceView mySurfaceView) {
            this.surfaceHolder = surfaceHolder;
            this.mySurfaceView = mySurfaceView;
            run = false;
        }

        public void setRunning(boolean run) {
            this.run = run;
        }

        @Override
        public void run() {
            Canvas canvas = null;
            while (run) {
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        if (isSleep){
                            Thread.sleep(500);
                        }
                        mySurfaceView.onDraw(canvas);
                        long time = 10;
                        //mySurfaceView.update(location.x,location.y);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        drawThread = new DrawThread(getHolder(), this);
        drawThread.setRunning(true);
        drawThread.start();
    }
}
