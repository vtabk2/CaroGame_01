package com.example.framgia.carobluetooth.ui.customview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;

import com.example.framgia.carobluetooth.R;
import com.example.framgia.carobluetooth.ai.AI;
import com.example.framgia.carobluetooth.data.database.DBHelper;
import com.example.framgia.carobluetooth.data.enums.BoardCellState;
import com.example.framgia.carobluetooth.data.enums.GameState;
import com.example.framgia.carobluetooth.data.enums.TurnGame;
import com.example.framgia.carobluetooth.data.model.History;
import com.example.framgia.carobluetooth.data.model.ItemCaro;
import com.example.framgia.carobluetooth.ui.listener.OnGetSingleBoardInfo;
import com.example.framgia.carobluetooth.utility.ConvertUtils;
import com.example.framgia.carobluetooth.utility.SoundUtils;
import com.example.framgia.carobluetooth.utility.TextUtils;
import com.example.framgia.carobluetooth.utility.ToastUtils;

import java.util.Calendar;
import java.util.List;

/**
 * Created by framgia on 19/08/2016.
 */
public class SingleBoardView extends BoardView {
    private OnGetSingleBoardInfo mOnGetSingleBoardInfo;
    private long mTimeGameStart, mTimeGameEnd;
    private DBHelper mDBHelper;
    private boolean mIsNewGame;
    private int mIdDataGame;
    private AI mAI = new AI();
    private Handler mHandler = new Handler(new ComputerThinkHandler());

    public SingleBoardView(Context context, Intent intent) {
        super(context);
        mTurnGame = TurnGame.HUMAN;
        mTimeGameStart = Calendar.getInstance().getTimeInMillis();
        mDBHelper = new DBHelper(getContext());
        mIsNewGame = intent.getAction().equals(ACTION_NEW_GAME);
        mIdDataGame = intent.getIntExtra(ID_DATA_GAME, ID_DATA_GAME_DEFAULT);
    }

    public void setTimeGameEnd(long timeGameEnd) {
        mTimeGameEnd = timeGameEnd;
    }

    public String getTimeGame() {
        return ConvertUtils.formatTimeGame(getContext(), mTimeGameEnd - mTimeGameStart);
    }

    private class ComputerThinkHandler implements Handler.Callback {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == NEXT_TURN_COMPUTER) {
                mPointLastMove = computerPlay(mItemCaros);
                mItemCaros[mPointLastMove.x][mPointLastMove.y]
                    .setBoardCellState(BoardCellState.MACHINE);
                if (isEndGame(mItemCaros[mPointLastMove.x][mPointLastMove.y])) {
                    showEndGame();
                    mTurnGame = TurnGame.NONE;
                } else {
                    mTurnGame = TurnGame.HUMAN;
                    SoundUtils.playSound(getContext(), R.raw.step, false);
                    mOnGetSingleBoardInfo.setPlayerTurnState(R.string.your_turn);
                    mOnGetSingleBoardInfo.setPlayerBackground(
                        R.drawable.surround_item_player_selected, R.drawable.surround_item_player);
                }
                invalidate();
                return true;
            }
            return false;
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        mOnGetSingleBoardInfo = (OnGetSingleBoardInfo) getContext();
        mOnGetSingleBoardInfo.setPlayerTurnState(R.string.your_turn);
        mOnGetSingleBoardInfo.setPlayerBackground(
            R.drawable.surround_item_player_selected, R.drawable.surround_item_player);
        initBoard();
        if (!mIsNewGame) {
            mTurnGame = TurnGame.NONE;
            loadDataBoard();
        }
    }

    private void loadDataBoard() {
        List<String> dataGame = TextUtils.readData(getContext(), mIdDataGame);
        mItemCaros = new ItemCaro[ROW][COL];
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                mItemCaros[row][col] = new ItemCaro(row, col,
                    TextUtils.getBoardCellState(
                        Integer.parseInt(String.valueOf(dataGame.get(row).charAt(col)))));
            }
        }
        mGameState = GameState.NONE;
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
                    int colIndex = (curX - MARGIN) / mCellSize;
                    int rowIndex = (curY - MARGIN) / mCellSize;
                    if (mItemCaros[rowIndex][colIndex].getBoardCellState() != BoardCellState.EMPTY)
                        return true;
                    mGameState = GameState.PLAYING;
                    if (mTurnGame == TurnGame.HUMAN) {
                        SoundUtils.playSound(getContext(), R.raw.step, false);
                        mOnGetSingleBoardInfo.setPlayerTurnState(R.string.your_turn);
                        mOnGetSingleBoardInfo.setPlayerBackground(R.drawable
                            .surround_item_player_selected, R.drawable.surround_item_player);
                        mItemCaros[rowIndex][colIndex].setBoardCellState(BoardCellState.HUMAN);
                        mPointLastMove.set(rowIndex, colIndex);
                        if (!isEndGame(mItemCaros[rowIndex][colIndex])) {
                            mTurnGame = TurnGame.MACHINE;
                            SoundUtils.playSound(getContext(), R.raw.step, false);
                            mOnGetSingleBoardInfo.setPlayerTurnState(R.string.opponent_turn);
                            mOnGetSingleBoardInfo.setPlayerBackground(
                                R.drawable.surround_item_player,
                                R.drawable.surround_item_player_selected);
                            ToastUtils.showToast(getContext(), R.string.computer_thinking);
                            mHandler.sendEmptyMessageDelayed(NEXT_TURN_COMPUTER, COMPUTER_THINKING);
                        } else {
                            showEndGame();
                            mTurnGame = TurnGame.NONE;
                        }
                        invalidate();
                        return true;
                    } else ToastUtils.showToast(getContext(), R.string.opponent_turn);
                }
        }
        return false;
    }

    public void saveDataGame() {
        int[][] dataGame = new int[ROW][COL];
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                dataGame[row][col] = mItemCaros[row][col].getBoardCellState().getValues();
            }
        }
        TextUtils.writeData(getContext(), mDBHelper.getSizeHistoryList(), dataGame);
    }

    @Override
    protected void showEndGame() {
        mTimeGameEnd = Calendar.getInstance().getTimeInMillis();
        SoundUtils.playSound(getContext(), R.raw.finish, false);
        History history = null;
        String title = null;
        if (mGameState == GameState.PLAYER_X_WIN) {
            title = getContext().getString(R.string.message_win_game_play);
            mEditor.putInt(WIN_HUMAN, mSharedPreferences.getInt(WIN_HUMAN, WIN_LOSE_DEFAULT)
                + INCREASE_DEFAULT);
            SoundUtils.playSound(getContext(), R.raw.win, false);
            history =
                new History(WIN, LOSE, getContext().getString(R.string.game_easy), getTimeGame());
        } else if (mGameState == GameState.PLAYER_X_LOSE) {
            title = getContext().getString(R.string.message_lose_game_play);
            mEditor.putInt(LOSE_HUMAN, mSharedPreferences.getInt(LOSE_HUMAN, WIN_LOSE_DEFAULT)
                + INCREASE_DEFAULT);
            SoundUtils.playSound(getContext(), R.raw.lose, false);
            history =
                new History(LOSE, WIN, getContext().getString(R.string.game_easy), getTimeGame());
        }
        mDBHelper.addHistory(history);
        mEditor.apply();
        saveDataGame();
        new AlertDialog.Builder(getContext())
            .setTitle(title)
            .setMessage(R.string.request_play_new_game)
            .setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        resetGame();
                    }
                }
            )
            .setNegativeButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mOnGetSingleBoardInfo.onFinishGame();
                    }
                }
            ).show().setCanceledOnTouchOutside(false);
    }

    private void resetGame() {
        resetBoard();
        invalidate();
        mTurnGame = TurnGame.HUMAN;
        mOnGetSingleBoardInfo.updateWinLose();
    }

    public Point computerPlay(ItemCaro[][] itemCaros) {
        mAI.setBoard(itemCaros);
        return mAI.minMax();
    }
}
