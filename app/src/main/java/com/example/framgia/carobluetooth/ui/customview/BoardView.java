package com.example.framgia.carobluetooth.ui.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.example.framgia.carobluetooth.R;
import com.example.framgia.carobluetooth.data.Constants;
import com.example.framgia.carobluetooth.data.enums.BoardCellState;
import com.example.framgia.carobluetooth.data.model.ItemCaro;

public class BoardView extends View implements Constants {
    private Paint mLinePaint, mBmpPaint;
    private Bitmap mBitmapBackground, mBitmapPlayerX, mBitmapPlayerO;
    private Rect mRectTable = new Rect();
    private Rect mRectCell = new Rect();
    private boolean isPlayerX = true;
    private ItemCaro[][] mItemCaros;

    public BoardView(Context context) {
        super(context);
        mBitmapBackground = getResBitmap(R.drawable.img_white);
        mBitmapPlayerX = getResBitmap(R.drawable.img_x);
        mBitmapPlayerO = getResBitmap(R.drawable.img_o);
        mBmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(STROKE_WIDTH);
        mLinePaint.setStyle(Style.STROKE);
        mRectCell.set(MARGIN, MARGIN, CELL_SIZE, CELL_SIZE);
    }

    private void initBoard() {
        mRectTable.set(MARGIN, MARGIN, (CELL_SIZE * COL) + MARGIN, (CELL_SIZE * ROW) + MARGIN);
        mItemCaros = new ItemCaro[ROW][COL];
        resetBoard();
    }

    private void resetBoard() {
        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++)
                if (mItemCaros[i][j] != null)
                    mItemCaros[i][j].setBoardCellState(BoardCellState.EMPTY);
                else mItemCaros[i][j] = new ItemCaro(i, j, BoardCellState.EMPTY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = mRectTable.left;
        int y = mRectTable.top;
        int xr = mRectTable.right;
        int yb = mRectTable.bottom;
        canvas.drawBitmap(mBitmapBackground,
            new Rect(MARGIN, MARGIN, mBitmapBackground.getWidth(),
                mBitmapBackground.getHeight()), mRectTable, mBmpPaint);
        canvas.drawRect(mRectTable, mLinePaint);
        for (int i = 1; i < ROW; i++)
            canvas.drawLine(x, y + i * CELL_SIZE, xr, x + i * CELL_SIZE, mLinePaint);
        for (int i = 1; i < COL; i++)
            canvas.drawLine(x + i * CELL_SIZE, y, x + i * CELL_SIZE, yb, mLinePaint);
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                switch (mItemCaros[row][col].getBoardCellState()) {
                    case PLAYER_X:
                        mRectCell
                            .offsetTo(col * CELL_SIZE + PADDING, row * CELL_SIZE + PADDING);
                        canvas.drawBitmap(mBitmapPlayerX, null, mRectCell, mBmpPaint);
                        break;
                    case PLAYER_O:
                        mRectCell
                            .offsetTo(col * CELL_SIZE + PADDING, row * CELL_SIZE + PADDING);
                        canvas.drawBitmap(mBitmapPlayerO, null, mRectCell, mBmpPaint);
                        break;
                }
            }
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(COL * CELL_SIZE, ROW * CELL_SIZE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_SCROLL:
                return true;
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                int curX = (int) event.getX();
                int curY = (int) event.getY();
                if (curX <= mRectTable.right && curX >= mRectTable.left
                    && curY <= mRectTable.bottom && curY >= mRectTable.top) {
                    int colIndex = (curX - MARGIN) / CELL_SIZE;
                    int rowIndex = (curY - MARGIN) / CELL_SIZE;
                    if (mItemCaros[rowIndex][colIndex].getBoardCellState() != BoardCellState.EMPTY)
                        return true;
                    if (isPlayerX) {
                        mItemCaros[rowIndex][colIndex].setBoardCellState(BoardCellState.PLAYER_X);
                    } else {
                        mItemCaros[rowIndex][colIndex].setBoardCellState(BoardCellState.PLAYER_O);
                    }
                    invalidate();
                }
                return true;
        }
        return false;
    }
}

