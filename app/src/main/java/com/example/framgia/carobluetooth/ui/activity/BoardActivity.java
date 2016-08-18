package com.example.framgia.carobluetooth.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;

import com.example.framgia.carobluetooth.R;
import com.example.framgia.carobluetooth.data.Constants;
import com.example.framgia.carobluetooth.data.enums.GameState;
import com.example.framgia.carobluetooth.data.model.GameData;
import com.example.framgia.carobluetooth.service.BluetoothConnectionService;
import com.example.framgia.carobluetooth.ui.customview.BoardView;
import com.example.framgia.carobluetooth.ui.listener.OnSendGameData;
import com.example.framgia.carobluetooth.utility.ToastUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener, Constants,
    OnSendGameData {
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 2;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    private static final String DATE_FORMAT = "yyyy-MM-dd_hh:mm:ss";
    private static final String IMAGE_PATH_FORMAT = "%s/%s.jpg";
    private static final String SHARE_IMAGE_TYPE = "image/";
    private static final int IMAGE_QUALITY = 100;
    private static final int BATTERY_LOW = 15;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothConnectionService mBluetoothConnectionService;
    private SharedPreferences mSharedPreferences;
    private BoardView mBoardView;
    private Button mButtonPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        initBluetooth();
    }

    private void initViews() {
        mBoardView = new BoardView(this);
        HorizontalScrollView horizontalScrollView =
            (HorizontalScrollView) findViewById(R.id.horizontal_scroll_board);
        horizontalScrollView.addView(mBoardView);
        mButtonPlay = (Button) findViewById(R.id.button_play);
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
        mButtonPlay.setOnClickListener(this);
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
                if (mBoardView.getGameState() == GameState.PLAYING) showBackGame();
                else finish();
                break;
            case R.id.image_button_undo:
                if (mBoardView.getGameState() == GameState.PLAYING) showUndoGame();
                break;
            case R.id.image_button_exit:
                if (mBoardView.getGameState() == GameState.PLAYING) showExitGame();
                break;
            case R.id.image_button_search:
                startActivityForResult(new Intent(this, DevicesListActivity.class),
                    REQUEST_CONNECT_DEVICE);
                break;
            case R.id.image_button_visibility:
                showVisibility();
                break;
            case R.id.button_play:
                if (mBoardView == null) return;
                mBoardView.setGameState(GameState.PLAYING);
                mButtonPlay.setVisibility(View.INVISIBLE);
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
        checkLowBattery();
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

    @Override
    public void sendGameData(GameData gameData) {
        if (mBluetoothConnectionService.getState() != STATE_CONNECTED) {
            ToastUtils.showToast(this, R.string.not_connected);
            return;
        }
        mBluetoothConnectionService.write(gameData);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (message.arg1) {
                        case STATE_CONNECTED:
                            // TODO: 15/08/2016 show state connected
                            break;
                        case STATE_CONNECTING:
                            // TODO: 15/08/2016 show state connecting
                            break;
                        case STATE_LISTEN:
                        case STATE_NONE:
                            // TODO: 15/08/2016 show state listen
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    // TODO: 15/08/2016
                    break;
                case MESSAGE_READ:
                    try {
                        ByteArrayInputStream byteArrayInputStream =
                            new ByteArrayInputStream((byte[]) message.obj);
                        ObjectInputStream objectInputStream =
                            new ObjectInputStream(byteArrayInputStream);
                        GameData gameData = (GameData) objectInputStream.readObject();
                        objectInputStream.close();
                        byteArrayInputStream.close();
                        // TODO: 18/08/2016 update GameData to BoardState
                    } catch (IOException | ClassNotFoundException e) {
                        ToastUtils.showToast(getApplicationContext(), R.string.something_error);
                    }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    takeScreenshot();
                else ToastUtils.showToast(this, R.string.permission_to_share_image);
                break;
        }
    }

    private void checkLowBattery() {
        Intent batteryStatus =
            registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        assert batteryStatus != null;
        if (batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, Constants.DEFAULT_INTENT_VALUE) <
            BATTERY_LOW)
            new AlertDialog.Builder(this)
                .setTitle(R.string.low_battery_title)
                .setMessage(R.string.low_battery_message)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void requestShareScreenShot() {
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_EXTERNAL_STORAGE);
        else takeScreenshot();
    }

    private void takeScreenshot() {
        Date now = new Date();
        DateFormat.format(DATE_FORMAT, now);
        try {
            String path = String.format(IMAGE_PATH_FORMAT, Environment.getExternalStorageDirectory()
                .toString(), now);
            View rootView = getWindow().getDecorView().getRootView();
            rootView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
            rootView.setDrawingCacheEnabled(false);
            File imageFile = new File(path);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream);
            outputStream.flush();
            outputStream.close();
            shareImage(imageFile);
        } catch (IOException e) {
            ToastUtils.showToast(this, R.string.something_error);
        }
    }

    private void shareImage(File imageFile) {
        if (imageFile == null) {
            ToastUtils.showToast(this, R.string.something_error);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(SHARE_IMAGE_TYPE);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
        if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
        else ToastUtils.showToast(this, R.string.no_app_can_share_image);
    }
}
