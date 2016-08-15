package com.example.framgia.carobluetooth.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.example.framgia.carobluetooth.R;
import com.example.framgia.carobluetooth.data.Constants;
import com.example.framgia.carobluetooth.service.BluetoothConnectionService;
import com.example.framgia.carobluetooth.ui.customview.BoardView;
import com.example.framgia.carobluetooth.utility.ToastUtils;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener, Constants {
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 2;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothConnectionService mBluetoothConnectionService;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        initBluetooth();
    }

    private void initViews() {
        BoardView boardView = new BoardView(this);
        HorizontalScrollView horizontalScrollView =
            (HorizontalScrollView) findViewById(R.id.horizontal_scroll_board);
        horizontalScrollView.addView(boardView);
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            ToastUtils.showToast(this, R.string.bluetooth_not_supported);
            finish();
        } else {
            initViews();
            loadSharedPreferences();
            initOnListener();
        }
    }

    private void initOnListener() {
        findViewById(R.id.image_button_back).setOnClickListener(this);
        findViewById(R.id.image_button_undo).setOnClickListener(this);
        findViewById(R.id.image_button_exit).setOnClickListener(this);
        findViewById(R.id.image_button_search).setOnClickListener(this);
        findViewById(R.id.image_button_visibility).setOnClickListener(this);
        findViewById(R.id.button_play).setOnClickListener(this);
    }

    private void showVisibility() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
            TIME_SHOW_VISIBILITY);
        startActivity(discoverableIntent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_button_back:
                showBackGame();
                break;
            case R.id.image_button_undo:
                showUndoGame();
                break;
            case R.id.image_button_exit:
                showExitGame();
                break;
            case R.id.image_button_search:
                startActivityForResult(new Intent(this, DevicesListActivity.class),
                    REQUEST_CONNECT_DEVICE);
                break;
            case R.id.image_button_visibility:
                showVisibility();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled())
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BLUETOOTH);
        else if (mBluetoothConnectionService == null)
            setupConnection();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (mBluetoothConnectionService != null &&
            mBluetoothConnectionService.getState() == STATE_NONE)
            mBluetoothConnectionService.start();
    }

    private void setupConnection() {
        mBluetoothConnectionService = new BluetoothConnectionService(this, mHandler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothConnectionService != null) mBluetoothConnectionService.stop();
    }

    private void sendMessage(String message) {
        if (mBluetoothConnectionService.getState() != STATE_CONNECTED) {
            ToastUtils.showToast(this, R.string.not_connected);
            return;
        }
        if (message.length() > 0)
            mBluetoothConnectionService.write(message.getBytes());
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (message.arg1) {
                        case STATE_CONNECTED:
                            // TODO: 15/08/2016
                            break;
                        case STATE_CONNECTING:
                            // TODO: 15/08/2016
                            break;
                        case STATE_LISTEN:
                        case STATE_NONE:
                            // TODO: 15/08/2016
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    // TODO: 15/08/2016
                    break;
                case MESSAGE_READ:
                    ToastUtils.showToast(getApplicationContext(),
                        new String((byte[]) message.obj, 0, message.arg1));
                    break;
                case MESSAGE_DEVICE_NAME:
                    ToastUtils.showToast(getApplicationContext(),
                        String.format(getString(R.string.connect_to_device),
                            message.getData().getString(DEVICE_NAME)));
                    break;
                case MESSAGE_TOAST:
                    ToastUtils
                        .showToast(getApplicationContext(), message.getData().getString(TOAST));
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK)
                    mBluetoothConnectionService.connect(mBluetoothAdapter.getRemoteDevice
                        (data.getExtras().getString(INTENT_DEVICE_ADDRESS)));
                break;
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    ToastUtils.showToast(this, R.string.bluetooth_turned_on);
                    setupConnection();
                } else {
                    ToastUtils.showToast(this, R.string.turn_on_bluetooth_to_play);
                    finish();
                }
                break;
            case R.id.button_play:
                // TODO: 17/08/2016 start game
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
        editor.putInt(LOSE, mSharedPreferences.getInt(LOSE, WIN_LOSE_DEFAULT) + INCREASE_DEFAULT);
        editor.apply();
    }

    private void loadSharedPreferences() {
        mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
    }

    private void showExitGame() {
        new AlertDialog.Builder(this)
            .setMessage(R.string.message_exit_game_play)
            .setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        surrender();
                    }
                })
            .setNegativeButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
            .show().setCanceledOnTouchOutside(false);
    }

    private void showUndoGame() {
        new AlertDialog.Builder(this)
            .setMessage(R.string.message_undo_game_play)
            .setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 15/08/2016
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
    public void onBackPressed() {
        showBackGame();
    }
}
