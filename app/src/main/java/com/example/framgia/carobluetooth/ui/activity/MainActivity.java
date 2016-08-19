package com.example.framgia.carobluetooth.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.framgia.carobluetooth.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.button_single_player).setOnClickListener(this);
        findViewById(R.id.button_multiplayer).setOnClickListener(this);
        findViewById(R.id.button_about).setOnClickListener(this);
        findViewById(R.id.button_exit).setOnClickListener(this);
    }

    private void showAbout() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.about)
            .setMessage(R.string.about_game)
            .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                }
            })
            .setIcon(android.R.drawable.ic_dialog_info).show();
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
            .setMessage(R.string.message_exit_game)
            .setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_single_player:
                startActivity(new Intent(this, SinglePlayerActivity.class));
                break;
            case R.id.button_multiplayer:
                startActivity(new Intent(this, BoardActivity.class));
                break;
            case R.id.button_about:
                showAbout();
                break;
            case R.id.button_exit:
                showExitDialog();
                break;
        }
    }
}
