package com.example.framgia.carobluetooth.ui.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.framgia.carobluetooth.R;
import com.example.framgia.carobluetooth.data.Constants;
import com.example.framgia.carobluetooth.ui.customview.SingleBoardView;
import com.example.framgia.carobluetooth.ui.listener.OnGetSingleBoardInfo;

import java.util.Locale;

/**
 * Created by framgia on 19/08/2016.
 */
public class SinglePlayerActivity extends AppCompatActivity
    implements OnGetSingleBoardInfo, Constants, View.OnClickListener {
    private SharedPreferences mSharedPreferences;
    private TextView mTextViewHumanWinLose, mTextViewMachineWinLose;
    private TextView mTextViewMachineName;
    private ImageView mImageViewMachine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        initViews();
        updateProfile();
    }

    private void updateProfile() {
        mTextViewMachineName.setText(getString(R.string.computer_easy));
        mImageViewMachine.setImageResource(R.drawable.img_o);
        updateWinLose();
    }

    private void initViews() {
        findViewById(R.id.image_button_undo).setVisibility(View.INVISIBLE);
        findViewById(R.id.image_button_exit).setVisibility(View.INVISIBLE);
        findViewById(R.id.image_button_search).setVisibility(View.INVISIBLE);
        findViewById(R.id.image_button_visibility).setVisibility(View.INVISIBLE);
        findViewById(R.id.button_play).setVisibility(View.INVISIBLE);
        findViewById(R.id.image_button_back).setOnClickListener(this);
        LinearLayout linearLayoutMachine =
            (LinearLayout) findViewById(R.id.layout_profile_player_right);
        mTextViewHumanWinLose = (TextView) findViewById(R.id.layout_profile_player_left)
            .findViewById(R.id.text_player_win_lose);
        mTextViewMachineWinLose =
            (TextView) linearLayoutMachine.findViewById(R.id.text_player_win_lose);
        mTextViewMachineName = (TextView) linearLayoutMachine.findViewById(R.id.text_player_name);
        mImageViewMachine = (ImageView) linearLayoutMachine.findViewById(R.id.image_player);
        SingleBoardView singleBoardView = new SingleBoardView(this);
        ((HorizontalScrollView) findViewById(R.id.horizontal_scroll_board))
            .addView(singleBoardView);
        mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
    }

    @Override
    public void onFinishGame() {
        finish();
    }

    @Override
    public void updateWinLose() {
        String winLoseHuman = String.format(Locale.getDefault(), getString(R.string
                .win_lose_format),
            mSharedPreferences.getInt(WIN_HUMAN, WIN_LOSE_DEFAULT),
            mSharedPreferences.getInt(LOSE_HUMAN, WIN_LOSE_DEFAULT));
        String winLoseMachine = String.format(Locale.getDefault(), getString(R.string
                .win_lose_format),
            mSharedPreferences.getInt(LOSE_HUMAN, WIN_LOSE_DEFAULT),
            mSharedPreferences.getInt(WIN_HUMAN, WIN_LOSE_DEFAULT));
        mTextViewHumanWinLose.setText(winLoseHuman);
        mTextViewMachineWinLose.setText(winLoseMachine);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_button_back:
                showBackGame();
                break;
        }
    }

    private void showBackGame() {
        new AlertDialog.Builder(this)
            .setMessage(R.string.message_back_game_play)
            .setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        surrender();
                        finish();
                    }
                })
            .setNegativeButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
            .show().setCanceledOnTouchOutside(false);
    }

    private void surrender() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(LOSE_HUMAN,
            mSharedPreferences.getInt(LOSE_HUMAN, WIN_LOSE_DEFAULT) + INCREASE_DEFAULT);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        showBackGame();
    }
}
