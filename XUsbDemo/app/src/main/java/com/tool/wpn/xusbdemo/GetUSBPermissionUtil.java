package com.tool.wpn.xusbdemo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

/**
 * Created by XiJie on 2016/12/23.
 */

public class GetUSBPermissionUtil {
    private PendingIntent mPermissionIntent = null;
    private PermissionReceiver mUsbReceiver = new PermissionReceiver();
    private Context mContext;
    private final static GetUSBPermissionUtil mGetUSBPermissionUtil=new GetUSBPermissionUtil();
    private GetUSBPermissionUtil(){};
    public static GetUSBPermissionUtil getInstance(){
        return mGetUSBPermissionUtil;
    }
    public void getPermission(Context context, UsbManager usbManager, UsbDevice usbDevice, final GetUsbPermissionCallBack callBack){
        if (usbManager.hasPermission(usbDevice)) {
            callBack.getPermissionOk();
            return;
        }
        mContext=context;
        this.mPermissionIntent = PendingIntent.getBroadcast(context, 0,new Intent("com.android.example.USB_PERMISSION"), 0);
        IntentFilter localIntentFilter = new IntentFilter("com.android.example.USB_PERMISSION");//意图过滤器
        mContext.registerReceiver(mUsbReceiver, localIntentFilter);//注册广播接收者
        mUsbReceiver.setUsbPermisionReceiverListener(new UsbPermissionReceiverListener() {
            @Override
            public void onAccredit() {//获得权限
                mContext.unregisterReceiver(mUsbReceiver);
                callBack.getPermissionOk();
                return;
            }

            @Override
            public void onNoAccredit() {
                mContext.unregisterReceiver(mUsbReceiver);
                callBack.getPermissionError();
                return;
            }
        });
        mUsbReceiver.setUsbDeviceName(usbDevice.getDeviceName());//为广播接受者设置DeviceName名称用于与接收的名称进行对比
        usbManager.requestPermission(usbDevice,mPermissionIntent);
    }
}
