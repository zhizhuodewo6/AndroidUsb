package com.tool.wpn.xusbdemo;

import android.app.Activity;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends Activity implements View.OnClickListener{
    private EditText et_productid,et_vendorid;
    private Button btn_open,btn_close,btn_send;

    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private UsbOTGDevice mUsbOTGDevice;
    private boolean mIsOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_productid= (EditText) findViewById(R.id.et_productid);
        et_vendorid= (EditText) findViewById(R.id.et_vendorid);
        btn_open= (Button) findViewById(R.id.btn_open);
        btn_close= (Button) findViewById(R.id.btn_close);
        btn_send= (Button) findViewById(R.id.btn_send);
        btn_open.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        btn_send.setOnClickListener(this);

    }

        @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_open:
                toOpen();
                break;
            case R.id.btn_close:
                closeDevice();
                break;
            case R.id.btn_send:
                toSend();
                break;
        }
    }

    private void toSend() {
        final byte[] byteContent={0x01,0x02,0x03};
        if(!mIsOpen){
            Toast.makeText(this,"设备未打开",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mUsbManager.hasPermission(mUsbDevice)) {
            GetUSBPermissionUtil.getInstance().getPermission(getApplicationContext(), mUsbManager, mUsbDevice, new GetUsbPermissionCallBack() {
                @Override
                public void getPermissionOk() {
                    int sendCode = mUsbOTGDevice.sendData(byteContent);
                    if(sendCode!=0){
                        Toast.makeText(MainActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this,"发送失败",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void getPermissionError() {
                    Toast.makeText(MainActivity.this,"无usb权限",Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            int sendCode = mUsbOTGDevice.sendData(byteContent);
            if(sendCode==0){
                Toast.makeText(this,"发送成功",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"发送失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 去打开设备
     */
    private void toOpen() {
        String productid = et_productid.getText().toString().trim();//获取用户输入的productId
        if(TextUtils.isEmpty(productid)){
            Toast.makeText(this,"请填写productid",Toast.LENGTH_SHORT).show();
            mIsOpen=false;
        }
        String vendorid = et_vendorid.getText().toString().trim();//获取用户输入的vendorId
        if(TextUtils.isEmpty(vendorid)){
            Toast.makeText(this,"请填写vendorid",Toast.LENGTH_SHORT).show();
            mIsOpen=false;
        }
        int productidInt = Integer.parseInt(productid);
        int vendoridInt = Integer.parseInt(vendorid);
        int resultCode = toChoiceDevice(productidInt, vendoridInt);
        if(resultCode!=0){
            Toast.makeText(this,"未找到设备",Toast.LENGTH_SHORT).show();
            mIsOpen=false;
        }
        if (!mUsbManager.hasPermission(mUsbDevice)) {
            GetUSBPermissionUtil.getInstance().getPermission(getApplicationContext(), mUsbManager, mUsbDevice, new GetUsbPermissionCallBack() {
                @Override
                public void getPermissionOk() {
                    mUsbOTGDevice=new UsbOTGDevice(mUsbManager,mUsbDevice);
                    int openCode = mUsbOTGDevice.open();
                    if(openCode!=0){
                        Toast.makeText(MainActivity.this,"打开失败,"+openCode,Toast.LENGTH_SHORT).show();
                        mIsOpen=false;
                    }else{
                        mIsOpen=true;
                    }

                }

                @Override
                public void getPermissionError() {
                    Toast.makeText(MainActivity.this,"无usb权限",Toast.LENGTH_SHORT).show();
                    mIsOpen=false;
                }
            });
        }else{
            mUsbOTGDevice=new UsbOTGDevice(mUsbManager,mUsbDevice);
            int openCode = mUsbOTGDevice.open();
            if(openCode!=0){
                Toast.makeText(MainActivity.this,"打开失败,"+openCode,Toast.LENGTH_SHORT).show();
                mIsOpen=false;
            }else{
                mIsOpen=true;
            }
        }

    }

    /**
     * 根据productId 与 vendorId，从系统usb列表中获取对应的设备
     */
    private int toChoiceDevice(int productidInt,int vendoridInt) {
        mUsbManager = ((UsbManager) this.getApplication().getSystemService(USB_SERVICE));
        HashMap<String, UsbDevice> deviceMap = mUsbManager.getDeviceList();//取出系统usb设备列表
        if (deviceMap == null || deviceMap.size() == 0) {
            return -1;
        }
        Iterator localIterator = deviceMap.values().iterator();
        UsbDevice localUsbDevice;
        while (localIterator.hasNext()) {
            localUsbDevice = (UsbDevice) localIterator.next();
            if(localUsbDevice.getVendorId()==vendoridInt||localUsbDevice.getProductId()==productidInt){
                mUsbDevice=localUsbDevice;
                return 0;
            }
        }
        return -1;
    }

    private void closeDevice() {
        if(mIsOpen){
            mUsbOTGDevice.close();
            mIsOpen=false;
        }
    }
}
