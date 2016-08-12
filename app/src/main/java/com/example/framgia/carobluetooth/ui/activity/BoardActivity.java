package com.example.framgia.carobluetooth.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.framgia.carobluetooth.R;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        initOnListener();
    }

    private void initOnListener() {
        findViewById(R.id.image_button_back).setOnClickListener(this);
        findViewById(R.id.image_button_undo).setOnClickListener(this);
        findViewById(R.id.image_button_exit).setOnClickListener(this);
        findViewById(R.id.image_button_search).setOnClickListener(this);
        findViewById(R.id.image_button_visibility).setOnClickListener(this);
        findViewById(R.id.button_play).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_button_back:
                onBackPressed();
                break;
            case R.id.image_button_undo:
                // TODO: 11/08/2016
                break;
            case R.id.image_button_exit:
                // TODO: 11/08/2016
                break;
            case R.id.image_button_search:
                // TODO: 11/08/2016
                break;
            case R.id.image_button_visibility:
                // TODO: 11/08/2016
                break;
        }
    }
}
