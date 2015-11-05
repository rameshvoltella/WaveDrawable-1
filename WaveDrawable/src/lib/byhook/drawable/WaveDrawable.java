package lib.byhook.drawable;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by byhook on 15-11-4.
 * Mail : byhook@163.com
 */
public class WaveDrawable extends Drawable {

    private static final String TAG = "WaveDrawable";

    private static final boolean DEBUG = true;

    private static final float SPEED = 10f;
    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 水纹画笔
     */
    private Path mWavePath;

    /**
     * 计时器
     */
    private Timer timer;

    /**
     * Drawable宽
     */
    private float mDrawableWidth;

    /**
     * Drawable高
     */
    private float mDrawableHeight;

    /**
     * 水波长
     */
    private float mWaveWidth;

    /**
     * 水波高度
     */
    private float mWaveHeight;

    /**
     * 水平线
     */
    private float mLine;

    /**
     * 坐标点集合
     */
    private List<Point> mPoints;

    /**
     * 左侧的隐藏波形
     */
    private float mLeftSide;

    /**
     * 水平总位移
     */
    private int mMoveLength;

    private Bitmap bitmap;

    /**
     * 画布
     */
    private Canvas mCanvas;

    private Bitmap backBitmap;

    private Canvas backCanvas;

    private Paint backPaint;

    public void init() {

        mPoints = new ArrayList<Point>();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);

        mWavePath = new Path();

        Paint paint = new Paint(mPaint);
        paint.setColor(Color.BLUE);
        bitmap = Bitmap.createBitmap(240, 240, Bitmap.Config.ARGB_8888);
        //bitmap.eraseColor(Color.TRANSPARENT);
        mCanvas = new Canvas(bitmap);
        mCanvas.drawCircle(120, 120, 120, paint);


        backPaint = new Paint();
        //backPaint.setColor(Color);
        backBitmap = Bitmap.createBitmap(240, 240, Bitmap.Config.ARGB_8888);
        //backBitmap.eraseColor(Color.TRANSPARENT);
        backCanvas = new Canvas(backBitmap);
        backCanvas.drawRect(0, 0, 240, 240, backPaint);

        timer = new Timer();
        timer.schedule(new WaveTask(), 0, 10);
    }

    private Bitmap makeDst(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFFFFCC44);
        c.drawOval(new RectF(0, 0, w * 3 / 4, h * 3 / 4), p);
        return bm;
    }

    // create a bitmap with a rect, used for the "src" image
    private Bitmap makeSrc(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFF66AAFF);
        c.drawRect(w / 3, h / 3, w * 19 / 20, h * 19 / 20, p);
        return bm;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 记录平移总位移
            invalidateSelf();
        }
    };

    private void resetPoints() {
        mLeftSide = -mWaveWidth;
        for (int i = 0; i < mPoints.size(); i++) {
            mPoints.get(i).setX(i * mWaveWidth / 4 - mWaveWidth);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        mDrawableWidth = bounds.width();
        mDrawableHeight = bounds.height();

        //水平线
        mLine = mDrawableHeight;

        mWaveHeight = mDrawableHeight / 5.0f;

        mWaveWidth = mDrawableWidth * 2;

        mLeftSide = -mWaveWidth;

        calcPoint();
    }

    /**
     * 计算坐标点
     */
    private void calcPoint(){
        int n = (int) Math.round(mDrawableWidth / mWaveWidth + 0.5);

        for (int i = 0; i < (4 * n + 5); i++) {
            // 从P0开始初始化到P4n+4，总共4n+5个点
            float x = i * mWaveWidth / 4 - mWaveWidth;
            float y = 0;
            switch (i % 4) {
                case 0:
                case 2:
                    // 零点位于水位线上
                    y = mLine;
                    break;
                case 1:
                    // 往下波动的控制点
                    y = mLine + mWaveHeight;
                    break;
                case 3:
                    // 往上波动的控制点
                    y = mLine - mWaveHeight;
                    break;
            }
            mPoints.add(new Point(x, y));
            if (DEBUG) Log.d(TAG, n + "/" + mPoints.size());
        }
    }

    @Override
    public void draw(Canvas canvas) {

        canvas.drawBitmap(bitmap, 0f, 0f, mPaint);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mCanvas.drawBitmap(backBitmap, 0f, 0f, mPaint);

        mWavePath.reset();
        int i = 0;

        mWavePath.moveTo(mPoints.get(0).getX(), mPoints.get(0).getY());
        for (; i < mPoints.size() - 2; i = i + 2) {
            mWavePath.quadTo(mPoints.get(i + 1).getX(),
                    mPoints.get(i + 1).getY(), mPoints.get(i + 2)
                            .getX(), mPoints.get(i + 2).getY());
        }

        mWavePath.lineTo(mPoints.get(i).getX(), mDrawableHeight);
        mWavePath.lineTo(mLeftSide, mDrawableHeight);
        mWavePath.close();


        mPaint.setColor(Color.YELLOW);
        mCanvas.drawPath(mWavePath, mPaint);

        mPaint.setXfermode(null);
    }

    /**
     * 计算进度
     * @param progress
     */
    public void setProgress(float progress){
        float mStep = mDrawableHeight * (1-progress);
        mLine = mStep;
        Log.d("INFO","MLINE="+progress);
        if (mLine <= 0){
            mLine = 0;
            if(progress==1){
                timer.cancel();
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * 波浪计算器
     */
    private class WaveTask extends TimerTask {

        @Override
        public void run() {
            mMoveLength += SPEED;
            mLeftSide += SPEED;
            // 波形平移
            for (int i = 0; i < mPoints.size(); i++) {
                mPoints.get(i).setX(mPoints.get(i).getX() + SPEED);
                switch (i % 4) {
                    case 0:
                    case 2:
                        mPoints.get(i).setY(mLine);
                        break;
                    case 1:
                        mPoints.get(i).setY(mLine + mWaveHeight);
                        break;
                    case 3:
                        mPoints.get(i).setY(mLine - mWaveHeight);
                        break;
                }
            }
            if (mMoveLength >= mWaveWidth) {
                // 波形平移超过一个完整波形后复位
                mMoveLength = 0;
                resetPoints();
            }
            handler.sendEmptyMessage(0);
        }
    }

    /**
     * 坐标点
     */
    private class Point {
        private float x;
        private float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }

}
