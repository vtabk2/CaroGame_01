package com.example.framgia.carobluetooth.ui.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.framgia.carobluetooth.R;

import java.util.List;

/**
 * Created by framgia on 11/08/2016.
 */
public class BluetoothDeviceRecyclerViewAdapter
    extends RecyclerView.Adapter<BluetoothDeviceRecyclerViewAdapter.BluetoothDeviceViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<BluetoothDevice> mBluetoothDevices;

    public BluetoothDeviceRecyclerViewAdapter(Context context,
                                              List<BluetoothDevice> bluetoothDevices) {
        mLayoutInflater = LayoutInflater.from(context);
        mBluetoothDevices = bluetoothDevices;
    }

    @Override
    public BluetoothDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BluetoothDeviceViewHolder(mLayoutInflater.inflate(R.layout
            .item_bluetooth_device, parent, false));
    }

    @Override
    public void onBindViewHolder(BluetoothDeviceViewHolder holder, int position) {
        BluetoothDevice bluetoothDevice = mBluetoothDevices.get(position);
        holder.mTextViewDeviceName.setText(bluetoothDevice.getName());
        holder.mTextViewDeviceAddress.setText(bluetoothDevice.getAddress());
    }

    @Override
    public int getItemCount() {
        return mBluetoothDevices == null ? 0 : mBluetoothDevices.size();
    }

    public static class BluetoothDeviceViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewDeviceName;
        private TextView mTextViewDeviceAddress;

        public BluetoothDeviceViewHolder(View itemView) {
            super(itemView);
            mTextViewDeviceName = (TextView) itemView.findViewById(R.id.text_view_device_name);
            mTextViewDeviceAddress = (TextView) itemView.findViewById(R.id
                .text_view_device_address);
        }
    }
}
