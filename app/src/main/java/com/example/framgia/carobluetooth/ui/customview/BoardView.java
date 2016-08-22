package com.example.framgia.carobluetooth.ui.customview;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;

import com.example.framgia.carobluetooth.R;
import com.example.framgia.carobluetooth.data.Constants;
import com.example.framgia.carobluetooth.data.enums.BoardCellState;
import com.example.framgia.carobluetooth.data.enums.GameState;
import com.example.framgia.carobluetooth.data.enums.Navigation;
import com.example.framgia.carobluetooth.data.enums.TurnGame;
import com.example.framgia.carobluetooth.data.model.GameData;
import com.example.framgia.carobluetooth.data.model.ItemCaro;
import com.example.framgia.carobluetooth.ui.listener.OnGetBoardInfo;
import com.example.framgia.carobluetooth.utility.ToastUtils;

public class BoardView extends View implements Constants {
    private Paint mLinePaint, mBmpPaint;
    private Bitmap mBitmapBackground, mBitmapPlayerX, mBitmapPlayerO;
    private Rect mRectTable = new Rect();
    private Rect mRectCell = new Rect();
    private boolean mIsPlayerX = true;
    private ItemCaro[][] mItemCaros;
    private TurnGame mTurnGame;
    private GameData mGameData;
    private int mMinCol, mMinRow, mMaxCol, mMaxRow;
    private OnGetBoardInfo mOnGetBoardInfo;
    private GameState mGameState;

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
        mGameData = new GameData();
        mTurnGame = TurnGame.YOUR_TURN;
        mOnGetBoardInfo = (OnGetBoardInfo) getContext();
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
        mMinCol = mMinRow = DEFAULT_MIN_ROW_COL;
        mMaxCol = mMaxRow = DEFAULT_MAX_ROW_COL;
        mGameState = GameState.NONE;
    }

    public void setGameState(GameState gameState) {
        mGameState = gameState;
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
                if (mOnGetBoardInfo.getConnectionState() != STATE_CONNECTED) {
                    ToastUtils.showToast(getContext(), R.string.not_connected_any_device);
                    return true;
                }
                if (mGameState != GameState.PLAYING) {
                    ToastUtils.showToast(getContext(), R.string.press_play_button);
                    return true;
                }
                if (mTurnGame == TurnGame.YOUR_TURN) {
                    int curX = (int) event.getX();
                    int curY = (int) event.getY();
                    if (curX <= mRectTable.right && curX >= mRectTable.left
                        && curY <= mRectTable.bottom && curY >= mRectTable.top) {
                        int colIndex = (curX - MARGIN) / CELL_SIZE;
                        int rowIndex = (curY - MARGIN) / CELL_SIZE;
                        if (colIndex < mMinCol) mMinCol = colIndex;
                        if (colIndex > mMaxCol) mMaxCol = colIndex;
                        if (rowIndex < mMinRow) mMinRow = rowIndex;
                        if (rowIndex > mMaxRow) mMaxRow = rowIndex;
                        if (mItemCaros[rowIndex][colIndex].getBoardCellState() !=
                            BoardCellState.EMPTY) return true;
                        if (mIsPlayerX) mItemCaros[rowIndex][colIndex]
                            .setBoardCellState(BoardCellState.PLAYER_X);
                        else mItemCaros[rowIndex][colIndex]
                            .setBoardCellState(BoardCellState.PLAYER_O);
                        invalidate();
                        if (isEndGame(
                            mIsPlayerX ? BoardCellState.PLAYER_X : BoardCellState.PLAYER_O)) {
                            mGameData.updateGameData(mItemCaros[rowIndex][colIndex], mGameState,
                                mTurnGame, Navigation.NONE);
                            showEndGame();
                        } else mGameData.updateGameData(mItemCaros[rowIndex][colIndex],
                            GameState.NONE, mTurnGame, Navigation.NONE);
                        mOnGetBoardInfo.sendGameData(mGameData);
                        mTurnGame = TurnGame.OPPONENT_TURN;
                        if (isPlayerX()) mOnGetBoardInfo.setPlayerBackground(
                            R.drawable.surround_item_player,
                            R.drawable.surround_item_player_selected);
                        else mOnGetBoardInfo.setPlayerBackground(
                            R.drawable.surround_item_player_selected,
                            R.drawable.surround_item_player);
                    }
                    return true;
                } else ToastUtils.showToast(getContext(), R.string.opponent_turn);
        }
        return false;
    }

    private void showEndGame() {
        String message = null;
        if (mIsPlayerX && mGameState == GameState.PLAYER_X_WIN)
            message = getContext().getString(R.string.message_win_game_play);
        else if (mIsPlayerX && mGameState == GameState.PLAYER_X_LOSE)
            message = getContext().getString(R.string.message_lose_game_play);
        else if (!mIsPlayerX && mGameState == GameState.PLAYER_X_WIN)
            message = getContext().getString(R.string.message_lose_game_play);
        else if (!mIsPlayerX && mGameState == GameState.PLAYER_X_LOSE)
            message = getContext().getString(R.string.message_win_game_play);
        new AlertDialog.Builder(getContext())
            .setMessage(message)
            .setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 19/08/2016
                    }
                })
            .setNegativeButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mOnGetBoardInfo.onFinishGame();
                    }
                })
            .show().setCanceledOnTouchOutside(false);
    }

    private boolean isEndGame(BoardCellState boardCellState) {
        boolean isEndGame = checkWinVertical(boardCellState) ||
            checkWinHorizontal(boardCellState) ||
            checkWinRightDiagonalUp(boardCellState) ||
            checkWinRightDiagonalDown(boardCellState) ||
            checkWinLeftDiagonalUp(boardCellState) ||
            checkWinLeftDiagonalDown(boardCellState);
        if (isEndGame && boardCellState == BoardCellState.PLAYER_X)
            mGameState = GameState.PLAYER_X_WIN;
        else if (isEndGame && boardCellState == BoardCellState.PLAYER_O)
            mGameState = GameState.PLAYER_X_LOSE;
        return isEndGame;
    }

    private boolean checkWinVertical(BoardCellState boardCellState) {
        int count, row;
        for (int col = mMinCol; col <= mMaxCol; col++) {
            row = mMinRow;
            count = DEFAULT_COUNT;
            while (count < WIN_COUNT && row <= mMaxRow) {
                if (mItemCaros[row][col].getBoardCellState() == boardCellState) count++;
                else count = DEFAULT_COUNT;
                row++;
            }
            if (count >= WIN_COUNT) return true;
        }
        return false;
    }

    private boolean checkWinHorizontal(BoardCellState boardCellState) {
        int count, col;
        for (int row = mMinRow; row <= mMaxRow; row++) {
            col = mMinCol;
            count = DEFAULT_COUNT;
            while (count < WIN_COUNT && col <= mMaxCol) {
                if (mItemCaros[row][col].getBoardCellState() == boardCellState) count++;
                else count = DEFAULT_COUNT;
                col++;
            }
            if (count >= WIN_COUNT) return true;
        }
        return false;
    }

    private boolean checkWinRightDiagonalUp(BoardCellState boardCellState) {
        int count, col, row, colIndex;
        for (col = mMinCol; col <= mMaxCol; col++) {
            row = mMinRow;
            count = DEFAULT_COUNT;
            colIndex = col;
            while (count < WIN_COUNT && row <= mMaxRow && colIndex <= mMaxCol) {
                if (mItemCaros[row][colIndex].getBoardCellState() == boardCellState) count++;
                else count = DEFAULT_COUNT;
                row++;
                colIndex++;
            }
            if (count >= WIN_COUNT) return true;
        }
        return false;
    }

    private boolean checkWinRightDiagonalDown(BoardCellState boardCellState) {
        int count, col, row, rowIndex;
        for (row = mMinRow; row <= mMaxRow; row++) {
            rowIndex = row;
            count = DEFAULT_COUNT;
            col = mMinCol;
            while (count < WIN_COUNT && rowIndex <= mMaxRow && col <= mMaxCol) {
                if (mItemCaros[rowIndex][col].getBoardCellState() == boardCellState) count++;
                else count = DEFAULT_COUNT;
                rowIndex++;
                col++;
            }
            if (count >= WIN_COUNT) return true;
        }
        return false;
    }

    private boolean checkWinLeftDiagonalUp(BoardCellState boardCellState) {
        int count, col, row, colIndex;
        for (col = mMinCol; col <= mMaxCol; col++) {
            row = mMaxRow;
            count = DEFAULT_COUNT;
            colIndex = col;
            while (count < WIN_COUNT && row >= mMinRow && colIndex <= mMaxCol) {
                if (mItemCaros[row][colIndex].getBoardCellState() == boardCellState) count++;
                else count = DEFAULT_COUNT;
                row--;
                colIndex++;
            }
            if (count >= WIN_COUNT) return true;
        }
        return false;
    }

    private boolean checkWinLeftDiagonalDown(BoardCellState boardCellState) {
        int count, col, row, rowIndex;
        for (row = mMinRow; row <= mMaxRow; row++) {
            rowIndex = row;
            count = DEFAULT_COUNT;
            col = mMinCol;
            while (count < WIN_COUNT && rowIndex >= mMinRow && col <= mMaxCol) {
                if (mItemCaros[rowIndex][col].getBoardCellState() == boardCellState) count++;
                else count = DEFAULT_COUNT;
                rowIndex--;
                col++;
            }
            if (count >= WIN_COUNT) return true;
        }
        return false;
    }

    public GameState getGameState() {
        return mGameState;
    }

    public void updateGameDataToBoardView(GameData gameData) {
        switch (gameData.getTurnGame()) {
            case OPPONENT_TURN:
                mIsPlayerX = !mIsPlayerX;
                mGameState = GameState.PLAYING;
                break;
            case YOUR_TURN:
                handleYourTurn(gameData);
                break;
        }
    }

    private void handleYourTurn(GameData gameData) {
        ItemCaro itemCaro = gameData.getItemCaro();
        int rowIndex = itemCaro.getPosX();
        int colIndex = itemCaro.getPosY();
        ToastUtils.showToast(getContext(), R.string.your_turn);
        GameState gameState = gameData.getGameState();
        if (gameState != GameState.NONE) mGameState = gameState;
        switch (itemCaro.getBoardCellState()) {
            case PLAYER_X:
                mItemCaros[rowIndex][colIndex].setBoardCellState(BoardCellState.PLAYER_X);
                break;
            case PLAYER_O:
                mItemCaros[rowIndex][colIndex].setBoardCellState(BoardCellState.PLAYER_O);
                break;
        }
        if (mGameState == GameState.PLAYER_X_WIN || mGameState == GameState.PLAYER_X_LOSE)
            showEndGame();
        invalidate();
        mTurnGame = TurnGame.YOUR_TURN;
    }

    public boolean isPlayerX() {
        return mIsPlayerX;
    }
}

