package com.bjudahdev.bioniccontrollermkii;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BTConnectionService {
    private static final String TAG = "BTConnectionService";

    private static final String appName = "Bionic Controller";

    //private static final UUID MY_UUID_INSECURE = UUID.fromString("c3074d45-0a1c-478d-9946-71491ad8568f");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter myBTAdapter;
    Context context;

    //
    private AcceptThread myInsecureAcceptThread;
    private ConnectThread myConnectThread;
    private BluetoothDevice myDevice;
    private UUID deviceUUID;
    ProgressDialog myProgressDialog;

    private ConnectedThread myConnectedThread;

    public BTConnectionService(Context context) {
        this.myBTAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;

        start();
    }

    // Listener for incoming connections
    private class AcceptThread extends Thread{
        // Local server socket
        private final BluetoothServerSocket myServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            // Listening server socket
            try {
                tmp = myBTAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);

                Log.d(TAG, "AcceptThread: Setting up server using: " + MY_UUID_INSECURE);
            } catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
                e.printStackTrace();
            }

            myServerSocket = tmp;
        }

        public void run(){
            Log.d(TAG, "Run: AcceptThread Running.");

            BluetoothSocket socket = null;

            try {
                Log.d(TAG, "run:RFCOM server socket starting");

                socket = myServerSocket.accept();

                Log.d(TAG, "run: RFCOM server socket accepted connection");
            }catch (IOException e){
                Log.e(TAG, "run: IOException: " + e.getMessage());
                e.printStackTrace();
            }

            //
            if(socket != null){
                connected(socket,myDevice);
            }
        }

        // Cancel function
        public void cancel (){
            Log.d(TAG, "cancel: Canceling AcceptThread ");
            try{
                myServerSocket.close();
            }catch(IOException e){
                Log.e(TAG, "cancel: IOException: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mySocket;
        private BluetoothDevice myDevice;

        public ConnectThread(BluetoothDevice device, UUID uuid){
            Log.d(TAG, "ConnectThread: starting");
            myDevice = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN: ConnectThread");

            // Get BTSocket for a connection with BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Creating InsecureRFCommSocket using UUID");
                //tmp = myDevice.createRfcommSocketToServiceRecord(deviceUUID);
                //tmp = myDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class } ).invoke(myDevice, 1);
                tmp = myDevice.createInsecureRfcommSocketToServiceRecord(myDevice.getUuids()[0].getUuid());

                //Log.d(TAG, "ConnectThread: Setting up server using: " + MY_UUID_INSECURE);
                Log.d(TAG, "ConnectThread: Setting up server using: " + myDevice.getUuids()[0].getUuid());
            } catch (IOException e){
                Log.e(TAG, "ConnectThread: Can't create InsecureRFCommSocket: " + e.getMessage());
                e.printStackTrace();
            }

            mySocket = tmp;

            // Cancel Discovery
            myBTAdapter.cancelDiscovery();

            // Connect to BT socket
            try {
                mySocket.connect();
                Log.d(TAG, "RUN: ConnectThread connected");
            } catch (IOException e) {
                Log.e(TAG, "RUN: ConnectThread: Could not connect to UUID");
                e.printStackTrace();
                //Close socket
                try {

                    mySocket.close();
                    Log.d(TAG, "RUN: socket closed");
                } catch (IOException e1) {
                    Log.e(TAG, "ConnectThread: Can't close socket connection" + e1.getMessage());
                }
            }

            connected(mySocket, myDevice);
        }

        public void cancel(){
            try{
                Log.d(TAG, "cancel: Canceling ConnectThread ");
                mySocket.close();
            }catch(IOException e){
                Log.e(TAG, "cancel: ConnectThread close failed. " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // init accept thread
    public synchronized void start(){
        Log.d(TAG, "start");

        if(myConnectThread !=null){
            myConnectThread.cancel();
            myConnectThread = null;
        }
        if(myInsecureAcceptThread == null){
            myInsecureAcceptThread = new AcceptThread();
            myInsecureAcceptThread.start();
        }
    }

    // init connect thread
    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startClient: Started");

        myProgressDialog = ProgressDialog.show(context, "Connecting Bluetooth", "Please Wait...", true);

        myConnectThread = new ConnectThread(device, uuid);
        myConnectThread.start();
    }

    //Manages BT Connection
    private class ConnectedThread extends Thread{
        private final BluetoothSocket mySocket;
        private final InputStream myInStream;
        private final OutputStream myOutStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "startClient: Started");

            mySocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // get rid of progressdialog when connected
            try{
                myProgressDialog.dismiss();
            }catch(NullPointerException e){
                e.printStackTrace();
            }


            try{
                tmpIn = mySocket.getInputStream();
                tmpOut = mySocket.getOutputStream();
            } catch (IOException e){
                e.printStackTrace();
            }

            myInStream = tmpIn;
            myOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024]; // buffer

            int bytes;

            while(true){
                //Read from instream
                try {
                    bytes = myInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                } catch (IOException e) {
                    Log.e(TAG, "run: Error reading from InStream, " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write:: Writing to Output: " + text);
            try {
                myOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to OutSteam, " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void cancel(){
            try{
                mySocket.close();
            } catch (IOException e){

            }
        }
    }

    private void connected(BluetoothSocket mySocket, BluetoothDevice myDevice) {
        Log.d(TAG, "connected: Starting");

        myConnectedThread = new ConnectedThread(mySocket);
        myConnectedThread.start();
    }

    public void write(byte[] out) {
        Log.d(TAG, "write:: write called ");
        myConnectedThread.write(out);
    }
}

