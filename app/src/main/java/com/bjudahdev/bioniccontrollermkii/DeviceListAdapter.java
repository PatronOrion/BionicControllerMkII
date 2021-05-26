package com.bjudahdev.bioniccontrollermkii;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bjudahdev.bioniccontrollermkii.R;

import java.util.ArrayList;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater layoutInflater;
    private ArrayList<BluetoothDevice> myDevices;
    private int viewResourceID;

    public DeviceListAdapter(Context context, int ResourceID, ArrayList<BluetoothDevice> devices){
        super(context, ResourceID, devices);
        this.myDevices = devices;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewResourceID = ResourceID;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        convertView = layoutInflater.inflate(viewResourceID, null);

        BluetoothDevice device = myDevices.get(position);

        if(device != null){
            TextView deviceName = (TextView) convertView.findViewById(R.id.BTdeviceName);
            TextView deviceAddress = (TextView) convertView.findViewById(R.id.BTdeviceAddress);

            if (deviceName != null){
                deviceName.setText(device.getName());
            }
            if (deviceAddress != null){
                deviceAddress.setText(device.getAddress());
            }
        }
        return convertView;
    }
}
