package lib.byhook.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

    /**
     * 圆形图像
     */
    private Bitmap mCircleBitmap;

    /**
     * 圆环画笔
     */
    private Paint mCirclePaint;

    public void init() {

        mPoints = new ArrayList<Point>();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setColor(Color.YELLOW);


        mWavePath = new Path();

        timer = new Timer();
        timer.schedule(new WaveTask(), 0, 10);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 记录平移总位移
            mMoveLength += SPEED;
            // 水位上升
//            mLine -= 0.1f;
//            if (mLine < 0)
//                mLine = 0;
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

            if (DEBUG) Log.d(TAG, "WaveTask...");
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

        mLine = mDrawableHeight / 2;
        // 根据View宽度计算波形峰值
        mWaveHeight = mDrawableWidth / 2.5f;

        mWaveWidth = mDrawableWidth * 4;

        mLeftSide = -mWaveWidth;

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
        //canvas.drawColor(Color.YELLOW);
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

        canvas.drawPath(mWavePath, mPaint);

        canvas.drawCircle(mDrawableWidth/2,mDrawableWidth/2,mDrawableWidth/2,mCirclePaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    private class WaveTask extends TimerTask {

        @Override
        public void run() {
            handler.sendEmptyMessage(0);
            //handler.sendMessage(handler.obtainMessage());
        }
    }

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
