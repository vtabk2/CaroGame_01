package com.example.framgia.carobluetooth.ui.customview;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;

import com.example.framgia.carobluetooth.R;
import com.example.framgia.carobluetooth.data.enums.BoardCellState;
import com.example.framgia.carobluetooth.data.enums.GameState;
import com.example.framgia.carobluetooth.data.enums.TurnGame;
import com.example.framgia.carobluetooth.data.model.ItemCaro;
import com.example.framgia.carobluetooth.ui.listener.OnGetSingleBoardInfo;
import com.example.framgia.carobluetooth.utility.SoundUtils;
import com.example.framgia.carobluetooth.utility.ToastUtils;

/**
 * Created by framgia on 19/08/2016.
 */
public class SingleBoardView extends BoardView {
    private OnGetSingleBoardInfo mOnGetSingleBoardInfo;

    public SingleBoardView(Context context) {
        super(context);
        mTurnGame = TurnGame.HUMAN;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        mOnGetSingleBoardInfo = (OnGetSingleBoardInfo) getContext();
        mOnGetSingleBoardInfo.setPlayerTurnState(R.string.your_turn);
        initBoard();
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
                    if (mItemCaros[rowIndex][colIndex].getBoardCellState() !=
                        BoardCellState.EMPTY) return true;
                    mGameState = GameState.PLAYING;
                    if (mTurnGame == TurnGame.HUMAN) {
                        SoundUtils.playSound(getContext(), R.raw.step, false);
                        mOnGetSingleBoardInfo.setPlayerTurnState(R.string.your_turn);
                        mOnGetSingleBoardInfo.setPlayerBackground(R.drawable
                            .surround_item_player_selected, R.drawable.surround_item_player);
                        mItemCaros[rowIndex][colIndex].setBoardCellState(BoardCellState.HUMAN);
                        mPointLastMove.set(rowIndex, colIndex);
                        if (!isEndGame(mItemCaros[rowIndex][colIndex]))
                            mTurnGame = TurnGame.MACHINE;
                    }
                    if (mTurnGame == TurnGame.MACHINE) {
                        SoundUtils.playSound(getContext(), R.raw.step, false);
                        mOnGetSingleBoardInfo.setPlayerTurnState(R.string.opponent_turn);
                        mOnGetSingleBoardInfo.setPlayerBackground(R.drawable
                            .surround_item_player, R.drawable.surround_item_player_selected);
                        mPointLastMove = computerPlay(mItemCaros);
                        mItemCaros[mPointLastMove.x][mPointLastMove.y]
                            .setBoardCellState(BoardCellState.MACHINE);
                        mTurnGame = TurnGame.HUMAN;
                    }
                    invalidate();
                    if (isEndGame(mItemCaros[mPointLastMove.x][mPointLastMove.y])) {
                        showEndGame();
                        mTurnGame = TurnGame.NONE;
                    }
                    return true;
                } else ToastUtils.showToast(getContext(), R.string.opponent_turn);
        }
        return false;
    }

    @Override
    protected void showEndGame() {
        SoundUtils.playSound(getContext(), R.raw.finish, false);
        String title = null;
        if (mGameState == GameState.PLAYER_X_WIN) {
            title = getContext().getString(R.string.message_win_game_play);
            mEditor.putInt(WIN_HUMAN, mSharedPreferences.getInt(WIN_HUMAN, WIN_LOSE_DEFAULT)
                + INCREASE_DEFAULT);
            SoundUtils.playSound(getContext(), R.raw.win, false);
        } else if (mGameState == GameState.PLAYER_X_LOSE) {
            title = getContext().getString(R.string.message_lose_game_play);
            mEditor.putInt(LOSE_HUMAN, mSharedPreferences.getInt(LOSE_HUMAN, WIN_LOSE_DEFAULT)
                + INCREASE_DEFAULT);
            SoundUtils.playSound(getContext(), R.raw.lose, false);
        }
        mEditor.apply();
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
        int bestScore = 0;
        Point bestCell = new Point();
        int maxRow = mMaxRow + 1 >= ROW ? ROW - 1 : mMaxRow + 1;
        int maxCol = mMaxCol + 1 >= COL ? COL - 1 : mMaxCol + 1;
        int minRow = mMinRow - 1 < 0 ? 0 : mMinRow - 1;
        int minCol = mMinCol - 1 < 0 ? 0 : mMinCol - 1;
        if (maxRow == ROW - 1 || maxCol == COL - 1 || minRow == 0 || minCol == 0) {
            minRow = 0;
            maxRow = maxRow + 1 >= ROW - 1 ? ROW - 1 : maxRow + 1;
            minCol = 0;
            maxCol = maxCol + 1 >= COL - 1 ? COL - 1 : maxCol + 1;
        }
        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minCol; col <= maxCol; col++) {
                if (getBoardCellState(row, col) == BoardCellState.EMPTY.getValues()) {
                    int score = evaluate(itemCaros[row][col], row, col);
                    if (score >= bestScore) {
                        bestScore = score;
                        bestCell.x = row;
                        bestCell.y = col;
                        if (bestScore >= BEST_SCORE) return bestCell;
                    }
                }
            }
        }
        return bestCell;
    }

    private int evaluate(ItemCaro itemCaro, int row, int col) {
        int countBonus, totalPoint = 0;
        itemCaro.setBoardCellState(BoardCellState.MACHINE);
        for (int h = 0; h < LENGTH; h++) {
            Point pointUp, pointDown;
            pointUp = checkLine(row, col, DELTA_ROW[h], DELTA_COL[h]);
            pointDown = checkLine(row, col, -DELTA_ROW[h], -DELTA_COL[h]);
            int distance;
            if (pointUp.x != pointDown.x) distance = Math.abs(pointUp.x - pointDown.x);
            else distance = Math.abs(pointUp.y - pointDown.y);
            if (distance <= WIN_COUNT) {
                if (!(checkBoardCellState(pointUp) && checkBoardCellState(pointDown))) distance--;
                if (!(checkBoardCellState(pointUp) || checkBoardCellState(pointDown)))
                    distance = OUT_OF_DISTANCE;
            }
            switch (distance) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    totalPoint += ATTACK_SCORE[distance - 1];
                    break;
                default:
                    totalPoint += ATTACK_SCORE[5];
                    break;
            }
            if (totalPoint >= BEST_SCORE) {
                itemCaro.setBoardCellState(BoardCellState.EMPTY);
                return totalPoint;
            }
        }
        countBonus = 0;
        for (int h = 0; h < LENGTH; h++) {
            Point pointUp, pointUpMax;
            pointUp = checkLine(row, col, DELTA_ROW[h], DELTA_COL[h]);
            pointUpMax = pointUp;
            if (checkBoardCellState(pointUpMax))
                pointUpMax = checkLine(pointUpMax.x, pointUpMax.y, DELTA_ROW[h], DELTA_COL[h]);
            Point pointDown, pointDownMax;
            pointDown = checkLine(row, col, -DELTA_ROW[h], -DELTA_COL[h]);
            if (checkBoardCellState(pointUpMax) && checkBoardCellState(pointDown)) {
                if (Math.abs(pointUpMax.x - pointDown.x) >= WIN_COUNT + 1 ||
                    Math.abs(pointUpMax.y - pointDown.y) >= WIN_COUNT + 1) {
                    countBonus++;
                    if (countBonus == NUMBER_BONUS) {
                        totalPoint += BONUS[0];
                        break;
                    }
                }
            }
            pointDownMax = pointDown;
            if (checkBoardCellState(pointDownMax)) pointDownMax =
                checkLine(pointDownMax.x, pointDownMax.y, DELTA_ROW[h], DELTA_COL[h]);
            if (checkBoardCellState(pointDownMax) && checkBoardCellState(pointUp)) {
                if (Math.abs(pointDownMax.x - pointUp.x) >= WIN_COUNT + 1 ||
                    Math.abs(pointDownMax.y - pointUp.y) >= WIN_COUNT + 1) {
                    countBonus++;
                    if (countBonus == NUMBER_BONUS) {
                        totalPoint += BONUS[0];
                        break;
                    }
                }
            }
        }
        itemCaro.setBoardCellState(BoardCellState.HUMAN);
        for (int h = 0; h < LENGTH; h++) {
            Point pointUp, pointDown;
            pointUp = checkLine(row, col, DELTA_ROW[h], DELTA_COL[h]);
            pointDown = checkLine(row, col, -DELTA_ROW[h], -DELTA_COL[h]);
            int distance;
            if (pointUp.x != pointDown.x) distance = Math.abs(pointUp.x - pointDown.x);
            else distance = Math.abs(pointUp.y - pointDown.y);
            if (distance <= WIN_COUNT) {
                if (!(checkBoardCellState(pointUp) && checkBoardCellState(pointDown))) distance--;
                if (!isInside(pointUp.x, pointUp.y) || getBoardCellState(pointUp) ==
                    BoardCellState.MACHINE.getValues() ||
                    !isInside(pointDown.x, pointDown.y) ||
                    getBoardCellState(pointDown) == BoardCellState.MACHINE.getValues()) continue;
            }
            switch (distance) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    totalPoint += DEFENSE_SCORE[distance - 1];
                    break;
                default:
                    totalPoint += DEFENSE_SCORE[5];
                    break;
            }
        }
        countBonus = 0;
        for (int h = 0; h < LENGTH; h++) {
            Point pointUp, pointUpMax;
            pointUp = checkLine(row, col, DELTA_ROW[h], DELTA_COL[h]);
            pointUpMax = pointUp;
            if (checkBoardCellState(pointUpMax))
                pointUpMax = checkLine(pointUpMax.x, pointUpMax.y, DELTA_ROW[h], DELTA_COL[h]);
            Point pointDown, pointDownMax;
            pointDown = checkLine(row, col, -DELTA_ROW[h], -DELTA_COL[h]);
            if (checkBoardCellState(pointUpMax) && checkBoardCellState(pointDown))
                if (Math.abs(pointUpMax.x - pointDown.x) >= WIN_COUNT + 1 ||
                    Math.abs(pointUpMax.y - pointDown.y) >= WIN_COUNT + 1) {
                    countBonus++;
                    if (countBonus == NUMBER_BONUS) {
                        totalPoint += BONUS[1];
                        break;
                    }
                }
            pointDownMax = pointDown;
            if (checkBoardCellState(pointDownMax)) pointDownMax =
                checkLine(pointDownMax.x, pointDownMax.y, DELTA_ROW[h], DELTA_COL[h]);
            if (checkBoardCellState(pointDownMax) && checkBoardCellState(pointUp)) {
                if (Math.abs(pointDownMax.x - pointUp.x) >= WIN_COUNT + 1 ||
                    Math.abs(pointDownMax.y - pointUp.y) >= WIN_COUNT + 1) {
                    countBonus++;
                    if (countBonus == NUMBER_BONUS) {
                        totalPoint += BONUS[1];
                        break;
                    }
                }
            }
        }
        itemCaro.setBoardCellState(BoardCellState.EMPTY);
        return totalPoint;
    }

    private boolean checkBoardCellState(Point point) {
        return isInside(point.x, point.y) &&
            getBoardCellState(point) == BoardCellState.EMPTY.getValues();
    }

    private Point checkLine(int row, int col, int dx, int dy) {
        int x = row, y = col;
        do {
            x += dx;
            y += dy;
        } while (isInside(x, y) && getBoardCellState(x, y) == getBoardCellState(row, col));
        return (new Point(x, y));
    }
}
