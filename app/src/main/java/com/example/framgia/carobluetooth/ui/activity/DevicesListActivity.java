package com.example.framgia.carobluetooth.ui.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.framgia.carobluetooth.R;
import com.example.framgia.carobluetooth.ui.adapter.BluetoothDeviceRecyclerViewAdapter;
import com.example.framgia.carobluetooth.ui.utility.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by framgia on 11/08/2016.
 */
public class DevicesListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int TIME_SHOW_VISIBILITY = 300;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;
    private static final String IS_SCANNING_CODE = "scanning";
    private static final String IS_SCANNED_CODE = "scanned";
    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> mPairedDevicesList, mAvailableDevicesList;
    private RecyclerView mRecyclerViewPairedDevices, mRecyclerViewAvailableDevices;
    private TextView mTextViewNoPairedDevices, mTextViewNoAvailableDevices;
    private BluetoothDeviceRecyclerViewAdapter mPairedDevicesAdapter, mAvailableDevicesAdapter;
    private ProgressBar mProgressBar;
    private boolean mIsScanning, mIsScanned;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                        mAvailableDevicesList.add((BluetoothDevice) intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    mTextViewNoAvailableDevices.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mIsScanning = true;
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    mProgressBar.setVisibility(View.GONE);
                    mIsScanning = false;
                    mIsScanned = true;
                    if (mAvailableDevicesList.size() != 0) {
                        mTextViewNoAvailableDevices.setVisibility(View.GONE);
                        mAvailableDevicesAdapter.notifyDataSetChanged();
                    } else mTextViewNoAvailableDevices.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);
        initBluetooth();
        if (savedInstanceState != null) onChangeRotation(savedInstanceState);
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            new AlertDialog.Builder(this)
                .setMessage(R.string.bluetooth_not_supported)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show().setCanceledOnTouchOutside(false);
        } else {
            mPairedDevicesList = new ArrayList<>();
            mAvailableDevicesList = new ArrayList<>();
            initViews();
            mPairedDevicesAdapter =
                new BluetoothDeviceRecyclerViewAdapter(getApplicationContext(), mPairedDevicesList);
            mRecyclerViewPairedDevices.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerViewPairedDevices.setAdapter(mPairedDevicesAdapter);
            mAvailableDevicesAdapter = new BluetoothDeviceRecyclerViewAdapter
                (getApplicationContext(), mAvailableDevicesList);
            mRecyclerViewAvailableDevices.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerViewAvailableDevices.setAdapter(mAvailableDevicesAdapter);
        }
    }

    private void initViews() {
        ((TextView) findViewById(R.id.text_view_your_device_name)).setText(mBluetoothAdapter
            .getName());
        ((TextView) findViewById(R.id.text_view_your_device_address)).setText(mBluetoothAdapter
            .getAddress());
        mRecyclerViewPairedDevices = (RecyclerView) findViewById(R.id.recycler_view_paired_devices);
        mRecyclerViewAvailableDevices = (RecyclerView) findViewById
            (R.id.recycler_view_available_devices);
        mTextViewNoPairedDevices = (TextView) findViewById(R.id.text_view_no_paired_devices);
        mTextViewNoAvailableDevices = (TextView) findViewById(R.id.text_view_no_available_devices);
        if (mAvailableDevicesList.size() == 0)
            mTextViewNoAvailableDevices.setVisibility(View.VISIBLE);
        findViewById(R.id.button_scan_available_devices).setOnClickListener(this);
        findViewById(R.id.button_show_visibility).setOnClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);
    }

    private void turnBluetoothOn() {
        if (!mBluetoothAdapter.isEnabled())
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BLUETOOTH);
    }

    private void showVisibility() {
        Intent discoverableIntent = new
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
            TIME_SHOW_VISIBILITY);
        startActivity(discoverableIntent);
    }

    private void showPairedDevices() {
        if (!mBluetoothAdapter.isEnabled()) {
            ToastUtils.showToast(this, R.string.request_turn_on_bluetooth_to_play);
            turnBluetoothOn();
            return;
        }
        mPairedDevicesList.clear();
        Set<BluetoothDevice> bluetoothDevicesSet = mBluetoothAdapter.getBondedDevices();
        if (bluetoothDevicesSet.size() == 0)
            mTextViewNoPairedDevices.setVisibility(View.VISIBLE);
        else {
            mTextViewNoPairedDevices.setVisibility(View.GONE);
            for (BluetoothDevice bluetoothDevice : bluetoothDevicesSet)
                mPairedDevicesList.add(bluetoothDevice);
            mPairedDevicesAdapter.notifyDataSetChanged();
        }
    }

    private void onChangeRotation(@Nullable Bundle savedInstanceState) {
        mIsScanning = savedInstanceState.getBoolean(IS_SCANNING_CODE);
        mIsScanned = savedInstanceState.getBoolean(IS_SCANNED_CODE);
        if (mIsScanning && mIsScanned) {
            mProgressBar.setVisibility(View.VISIBLE);
            requestScanAvailableBluetoothDevices();
        } else {
            if (mIsScanning) {
                mProgressBar.setVisibility(View.VISIBLE);
                requestScanAvailableBluetoothDevices();
            }
            if (mIsScanned) requestScanAvailableBluetoothDevices();
        }
    }

    private void requestScanAvailableBluetoothDevices() {
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        else scanAvailableDevices();
    }

    private void scanAvailableDevices() {
        if (!mBluetoothAdapter.isEnabled()) {
            ToastUtils.showToast(this, R.string.request_permission_to_scan);
            turnBluetoothOn();
            return;
        }
        if (mBluetoothAdapter.isDiscovering()) mBluetoothAdapter.cancelDiscovery();
        else {
            mAvailableDevicesList.clear();
            mAvailableDevicesAdapter.notifyDataSetChanged();
            mBluetoothAdapter.startDiscovery();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mBroadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    ToastUtils.showToast(this, R.string.bluetooth_turned_on);
                    showPairedDevices();
                } else {
                    ToastUtils.showToast(this, R.string.turn_on_bluetooth_to_play);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    scanAvailableDevices();
                else ToastUtils.showToast(this, R.string.request_permission_to_scan);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_scan_available_devices:
                requestScanAvailableBluetoothDevices();
                break;
            case R.id.button_show_visibility:
                showVisibility();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPairedDevices();
        if (mIsScanning) requestScanAvailableBluetoothDevices();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsScanning) unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver.isOrderedBroadcast()) unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_SCANNING_CODE, mIsScanning);
        outState.putBoolean(IS_SCANNED_CODE, mIsScanned);
        if (mBluetoothAdapter != null) mBluetoothAdapter.cancelDiscovery();
    }
}
