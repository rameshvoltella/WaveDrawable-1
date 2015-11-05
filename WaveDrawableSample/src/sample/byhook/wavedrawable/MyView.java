package sample.byhook.wavedrawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by byhook on 15-11-5.
 * Mail : byhook@163.com
 */
public class MyView extends View {

    private Paint mPaint;
    private Bitmap backBitmap;
    private Canvas backCanvas;
    private Bitmap bitmap;
    private Canvas mCanvas;

    public MyView(Context context) {
        super(context);
        initView();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.FILL);
        //mPaint.setColor(Color.BLUE);

        //setLayerType(LAYER_TYPE_SOFTWARE, null);

        Paint paint = new Paint(mPaint);
        paint.setColor(Color.YELLOW);
        bitmap = Bitmap.createBitmap(240,240, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.TRANSPARENT);
        mCanvas = new Canvas(bitmap);
        mCanvas.drawCircle(120, 120, 120, paint);

        Paint backPaint = new Paint(mPaint);
        backPaint.setColor(Color.RED);
        backBitmap = Bitmap.createBitmap(240,240, Bitmap.Config.ARGB_8888);
        backBitmap.eraseColor(Color.TRANSPARENT);
        backCanvas = new Canvas(backBitmap);
        backCanvas.drawRect(0, 0, 240, 240, backPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
//        canvas.save();
//        mPaint.setXfermode(null);

        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, 0f, 0f, mPaint);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mCanvas.drawBitmap(backBitmap, 0f, 0f, mPaint);

//        mPaint.setColor(Color.RED);
//        mCanvas.drawRect(0, 0, 240, 240, mPaint);

//      mPaint.setXfermode(null);
//      canvas.restore();
    }
}
