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
public class CircleView  extends SurfaceView implements SurfaceHolder.Callback{
    private  SurfaceHolder surfaceHolder;
    private  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public CircleView(Context context,AttributeSet attrs) {
        super(context,attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.YELLOW);
                canvas.drawCircle(event.getX(), event.getY(), 500, paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    private void InitDraw() {
        Canvas canvas=surfaceHolder.lockCanvas();
        canvas.drawRect(0, 0,this.getWidth(),this.getHeight(),paint);//the first method 绘制一个和手机屏幕一样大小的矩形
        canvas.drawColor(Color.GREEN);//the second methos绘制颜色填充整个屏幕
        canvas.drawRGB(0,0,0);//the third method 绘制颜色填充整个屏幕
        canvas.drawText("game", 10, 10, paint);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

}
