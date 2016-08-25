package com.example.framgia.carobluetooth.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.framgia.carobluetooth.R;
import com.example.framgia.carobluetooth.data.Constants;

public class SettingActivity extends AppCompatActivity
    implements Constants, CompoundButton.OnCheckedChangeListener {
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private CheckBox mCheckBoxBlockTwoHeadWin, mCheckBoxSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initViews();
        handleViewsOnClick();
    }

    private void initViews() {
        mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context
            .MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mCheckBoxBlockTwoHeadWin = (CheckBox) findViewById(R.id.checkbox_block_two_head_win);
        mCheckBoxSound = (CheckBox) findViewById(R.id.checkbox_sound);
        mCheckBoxBlockTwoHeadWin
            .setChecked(mSharedPreferences.getBoolean(BLOCK_TWO_HEAD_WIN, false));
        mCheckBoxSound.setChecked(mSharedPreferences.getBoolean(SOUND, true));
    }

    private void handleViewsOnClick() {
        mCheckBoxBlockTwoHeadWin.setOnCheckedChangeListener(this);
        mCheckBoxSound.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.checkbox_block_two_head_win:
                mEditor.putBoolean(BLOCK_TWO_HEAD_WIN, isChecked);
                break;
            case R.id.checkbox_sound:
                mEditor.putBoolean(SOUND, isChecked);
                break;
        }
        mEditor.apply();
    }
}
