package edu.utdallas.spiritlevel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by kriszhang on 4/20/16.
 */
public class CircleView  extends SurfaceView{
    Bitmap mBitmap;
    private  SurfaceHolder surfaceHolder;
    private  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public CircleView(Context context,AttributeSet attrs) {
        super(context,attrs);
        surfaceHolder = getHolder();
        mBitmap = Bitmap.createBitmap(400,400, Bitmap.Config.ARGB_8888);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
    }

    protected void  onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.setBitmap(mBitmap);
        canvas.drawCircle(0, 0, 50, paint);
        //Toast.makeText(MainActivity.this, "DDD", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.BLACK);
                canvas.drawCircle(event.getX(), event.getY(), 50, paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
        return false;
    }

}
