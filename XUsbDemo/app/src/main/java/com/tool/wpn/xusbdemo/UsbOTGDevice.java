package com.tool.wpn.xusbdemo;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.nio.ByteBuffer;

/**
 * Created by XiJie on 2016/12/27.
 */

public class UsbOTGDevice {
    private final int BULK_TIMEOUT = 5000;
    public final int ERR_H1DEVICE_ARGUMENT = -2;
    public final int ERR_H1DEVICE_COMMAND = -4;

    private UsbManager mManager;
    private UsbDevice mDevice;
    private UsbDeviceConnection mDeviceConnection;
    private UsbEndpoint mEndpointIn;
    private UsbEndpoint mEndpointOut;
    private UsbInterface mInterface;

    public Byte mResCode;
    public ByteBuffer mResData;
    public Integer mResDataLength;

    public UsbOTGDevice(UsbManager paramUsbManager, UsbDevice paramUsbDevice){
        this.mManager = paramUsbManager;
        this.mDevice = paramUsbDevice;
        this.mResData = ByteBuffer.allocate(0);
    }

    public int sendData(byte[] paramArrayOfByte){
        if (this.mDeviceConnection == null){
            return ERR_H1DEVICE_ARGUMENT;
        }
        this.mDeviceConnection.claimInterface(this.mInterface, true);//调用了本地
        if (!(sendCommand(paramArrayOfByte))){//向设备传输数据
            this.mDeviceConnection.releaseInterface(this.mInterface);//释放接口
            return ERR_H1DEVICE_COMMAND;
        }
        return 0;
    }

    protected final boolean bulkTransfer(final UsbDeviceConnection paramUsbDeviceConnection, final UsbEndpoint paramUsbEndpoint, final byte[] paramArrayOfByte, final int paramInt1, final int paramInt2){
        int bulkTransferLength = paramUsbDeviceConnection.bulkTransfer(paramUsbEndpoint, paramArrayOfByte, paramInt1, paramInt2);
        return (bulkTransferLength>= 0);
    }

    /**
     * 关闭mDeviceConnection
     */
    public void close(){
        this.mDeviceConnection.close();
    }


    /**
     * 为H1Device设置Interface,Endpoint,mDeviceConnection
     * @return
     */
    public int open(){
        if (!(setInterface(this.mDevice))){
            return -1;
        }
        if (!(setEndpoint(this.mInterface))){
            return -1;
        }
        this.mDeviceConnection = this.mManager.openDevice(this.mDevice);
        if (this.mDeviceConnection == null){
            return -1;
        }
        return 0;
    }


    /**
     * 发送参数
     * @param paramArrayOfByte
     * @return
     */
    protected final boolean sendCommand(byte[] paramArrayOfByte){
        return bulkTransfer(this.mDeviceConnection, this.mEndpointOut, paramArrayOfByte, paramArrayOfByte.length, BULK_TIMEOUT);
    }

    /**
     * 设置Endpoint,根据Device的Interface
     * @param paramUsbInterface
     * @return
     */
    protected boolean setEndpoint(UsbInterface paramUsbInterface){
        if(paramUsbInterface.getEndpointCount()!=2){
            return false;
        }
        for (int m = 0; m < paramUsbInterface.getEndpointCount(); m++) {
            UsbEndpoint ep = paramUsbInterface.getEndpoint(m);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    this.mEndpointOut = ep;
                } else {
                    this.mEndpointIn= ep;
                }
            }else{
                return false;
            }
        }
        return true;
    }

    /**
     * 设置Interface,根据传进来的UsbDevice
     * @param paramUsbDevice
     * @return
     */
    protected boolean setInterface(UsbDevice paramUsbDevice){
        UsbInterface localUsbInterface = null;
        if (1 != paramUsbDevice.getInterfaceCount()){//判断传入的设备接口是否为1
            return false;
        }
        localUsbInterface = paramUsbDevice.getInterface(0);
        this.mInterface = localUsbInterface;
        if(2!= localUsbInterface.getEndpointCount()){//2
            return false;
        }
        return true;
    }
}
