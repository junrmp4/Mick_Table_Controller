package com.benqmedicaltech.bmt_table_controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ScanPage extends AppCompatActivity {
    /**
     * 全局的上下文.
     */
    private static Context mContext;


    ArrayList<String> BT_Devicelist = new ArrayList<>();
    ArrayList<String> BT_Addrlist = new ArrayList<>();
    ArrayList<String> BT_UUIDlist = new ArrayList<>();
    private ParcelUuid[] muuid = new ParcelUuid[]{};


    BluetoothHeadset mBluetoothHeadset;

    // Get the default adapter
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = (BluetoothHeadset) proxy;
            }
        }

        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = null;
            }
        }
    };


    /**
     * 获取Context.
     *
     * @return
     */
    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_page);

        mContext = getApplicationContext();

        TextView tex = (TextView) findViewById(R.id.textView234);
        Button nextPageBtn = (Button) findViewById(R.id.button234);
        ListView listview = (ListView) findViewById(R.id.listView234);

        tex.setText("Bluetooth scan start");

        if (mBluetoothAdapter == null) {
            tex.setText("您的裝置沒有支援藍芽");
        }

        int REQUEST_ENABLE_BT = 1; // need greater then 0

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            tex.setText("藍芽沒開拉，幹！");
        }

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            tex.setText(tex.getText() + "\n" + "cancelDiscovery");
        }


        BluetoothSocket mdeviceSocket = null;

        //Querying paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        mBluetoothAdapter.startDiscovery();
        tex.setText(tex.getText() + "\n" + "startDiscovery ");
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                //string mUUID = device.getUuids()[0].getUuid(); // UUID
                //tex.setText(tex.getText() + "\n" + deviceName + deviceHardwareAddress);
                BT_Devicelist.add(deviceName); //this adds an element to the list.
                BT_Addrlist.add(deviceHardwareAddress);

                //muuid =  device.getUuids();
                //BT_UUIDlist.add(muuid[0].toString());
            }
            //android.R.layout.simple_list_item_1 為內建樣式，還有其他樣式可自行研究
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ScanPage.this, android.R.layout.simple_list_item_1, BT_Devicelist);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(onClickListView);       //指定事件 Method
            tex.setText(tex.getText() + "\n" + "pair bluetooth is over");
        }

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        // Establish connection to the proxy.
        mBluetoothAdapter.getProfileProxy(ScanPage.this, mProfileListener, BluetoothProfile.HEADSET);

        // ... call functions on mBluetoothHeadset



        // Close proxy connection after use.
        //mBluetoothAdapter.closeProfileProxy(mBluetoothHeadset);


    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            TextView mytextview = (TextView) findViewById(R.id.textView234);
            ListView listview = (ListView) findViewById(R.id.listView234);
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                //mytextview.setText(mytextview.getText() + "\n" + deviceName + deviceHardwareAddress);
                BT_Devicelist.add(deviceName); //this adds an element to the list.
                BT_Addrlist.add(deviceHardwareAddress);


            }
            //android.R.layout.simple_list_item_1 為內建樣式，還有其他樣式可自行研究
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ScanPage.this, android.R.layout.simple_list_item_1, BT_Devicelist);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(onClickListView);       //指定事件 Method

        }
    };

    public ParcelUuid[] servicesFromDevice(BluetoothDevice device) {
        try {
            Class cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class[] par = {};
            Method method = cl.getMethod("getUuids", par);
            Object[] args = {};
            ParcelUuid[] retval = (ParcelUuid[]) method.invoke(device, args);
            return retval;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /***
     * 點擊ListView事件Method
     */
    private static final int REQUEST_ENABLE_BT = 1;
    //private BluetoothAdapter mBluetoothAdapter ;
    private boolean isSupportBT = true;
    private boolean isBTInitEnabled = false , isBTEnabled = false;

    private ListView listDevices;
    private SimpleAdapter adapter;
    private String[] from = {"name","addr","type"};
    //private int[] to = {R.id.item_name,R.id.item_addr,R.id.item_type};
    private LinkedList<HashMap<String,Object>> data;
    private MyBTReceiver receiver;
    private AcceptThread serverThread;

    private static final String TAG = "ScanPageActivity";
    //private final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    //private static final UUID MY_UUID = UUID.fromString("0000110E-0000-1000-8000-00805F9B34FB");

    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    //-----此UUID是安卓手機通用的

    BluetoothServerSocket tmp = null;
    BluetoothServerSocket mmServerSocket;

    BluetoothSocket BTSocket;

    //public static BluetoothChatService mChatService = null; // --BluetoothChatService.java

    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Toast 快顯功能 第三個參數 Toast.LENGTH_SHORT 2秒  LENGTH_LONG 5秒
            Toast.makeText(ScanPage.this, "點選第 " + (position + 1) + " 個 \n內容：" + BT_Addrlist.get(position).toString(), Toast.LENGTH_SHORT).show();

            mBluetoothAdapter.cancelDiscovery();

           BluetoothDevice connDevices = mBluetoothAdapter.getRemoteDevice(BT_Addrlist.get(position).toString());
//            try {
//                mmServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord( BT_Devicelist.get(position).toString(),MY_UUID);
//                mmServerSocket.accept();
//                ConnectThread clientThread = new ConnectThread(connDevices);
//                clientThread.start();
//                receiver = new MyBTReceiver();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


            try {
                BTSocket = connDevices.createRfcommSocketToServiceRecord(MY_UUID);
                BTSocket.connect();
                readThread mreadThread = new readThread();
                mreadThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }


//            try {
//                // MY_UUID is the app's UUID string, also used by the client code.
//                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(BT_Devicelist.get(position).toString(), MY_UUID);
//
//                //BluetoothSocket socket = null;
//                //socket = mmServerSocket.accept();
//            } catch (IOException e) {
//                Log.e(TAG, "Socket's listen() method failed", e);
//            }
//
//            mmServerSocket = tmp;


        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }
    private class MyBTReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (!isBTNameExists(device.getName())) {
                HashMap<String, Object> item = new HashMap<>();
                item.put(from[0], device.getName());
                item.put(from[1], device.getAddress());
                item.put(from[2], "scan");
                item.put("device",device);
                data.add(item);
                adapter.notifyDataSetChanged();
            }
        }
    }
    private boolean isBTNameExists (String name) {
        boolean isExists = false;
        for (HashMap<String,Object> devices : data) {
            if ( ((String)devices.get(from[1]) ).equals(name)) {
                isExists = true;
                break;
            }
        }
        return isExists;
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Hotlife_Mick_115200", MY_UUID);
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                    Log.d("Hotlife_Mick_115200","Connecting as a server  Success");
                    InputStream in = socket.getInputStream();
                    byte[] buf = new byte[1024];
                    int len = in.read(buf);
                    Log.d("Abner",new String(buf,0,len));
                    in.close();
                } catch (IOException e) {
                    //break;
                }
                // If a connection was accepted
//                if (socket != null) {
//                    // Do work to manage the connection (in a separate thread)
//                    manageConnectedSocket(socket);
//                    mmServerSocket.close();
//                    break;
//                }
            }
        }

        // Will cancel the listening socket, and cause the thread to finish //
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                Log.d("Hotlife_Mick_115200","Connecting as a client  Success");
                OutputStream out = mmSocket.getOutputStream();
                out.flush();
                out.close();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            //manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }



    }

    /**
     * 读取数据
     */
    private class readThread extends Thread {
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream is = null;
            try {
                is = BTSocket.getInputStream();
                //show("客户端:获得输入流");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            while (true) {
                try {
                    if ((bytes = is.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        String s = new String(buf_data);
                        //show("客户端:读取数据了" + s);
                    }
                } catch (IOException e) {
                    try {
                        is.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }



}


