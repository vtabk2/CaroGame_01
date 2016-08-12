package com.example.framgia.carobluetooth.ui.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.framgia.carobluetooth.R;
import com.example.framgia.carobluetooth.data.Constants;
import com.example.framgia.carobluetooth.utility.ToastUtils;

public class BoardView extends View {
    private Paint mLinePaint, mBmpPaint;
    private Bitmap mBitmapBackground;
    private Rect mRectTable = new Rect();
    private GestureDetector mGestureDetector;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBitmapBackground = getResBitmap(R.drawable.img_white);
        mBmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(Constants.STROKE_WIDTH);
        mLinePaint.setStyle(Style.STROKE);
        initOnListener();
    }

    private void initOnListener() {
        GestureDetector.OnGestureListener gestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                        float distanceX, float distanceY) {
                    scrollBy((int) distanceX, (int) distanceY);
                    return true;
                }
            };
        mGestureDetector = new GestureDetector(getContext(), gestureListener);
    }

    private void initBoard() {
        mRectTable.set(Constants.MARGIN, Constants.MARGIN,
            (Constants.BOARD_WIDTH) + Constants.MARGIN,
            (Constants.BOARD_HEIGHT) + Constants.MARGIN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            int x = mRectTable.left;
            int y = mRectTable.top;
            int xr = mRectTable.right;
            int yb = mRectTable.bottom;
            canvas.drawBitmap(mBitmapBackground,
                new Rect(Constants.MARGIN, Constants.MARGIN, mBitmapBackground.getWidth(),
                    mBitmapBackground.getHeight()), mRectTable, mBmpPaint);
            canvas.drawRect(mRectTable, mLinePaint);
            for (int i = 1; i < Constants.ROW; i++) {
                canvas.drawLine(x, y + i * Constants.CELL_SIZE,
                    xr, x + i * Constants.CELL_SIZE, mLinePaint);
            }
            for (int i = 1; i < Constants.COL; i++) {
                canvas.drawLine(x + i * Constants.CELL_SIZE, y,
                    x + i * Constants.CELL_SIZE, yb, mLinePaint);
            }
        } catch (Exception e) {
            ToastUtils.showToast(getContext(), e.toString());
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        initBoard();
    }

    private Bitmap getResBitmap(int bmpResId) {
        return BitmapFactory.decodeResource(getResources(), bmpResId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }
}

