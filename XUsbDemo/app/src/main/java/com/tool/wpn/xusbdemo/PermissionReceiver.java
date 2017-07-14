package com.tool.wpn.xusbdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;


/**
 * 
 * @author xijie
 * 
 */
public class PermissionReceiver extends BroadcastReceiver {
	public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	protected UsbPermissionReceiverListener mUsbPeramissionRecevierListener = null;
	protected String mUsbDeviceName = "";

	public void onReceive(Context paramContext, Intent paramIntent){
		if (paramIntent == null){
			mUsbPeramissionRecevierListener.onNoAccredit();
	    }else{
	    	if (ACTION_USB_PERMISSION.equals(paramIntent.getAction())){
	    		UsbDevice localUsbDevice= (UsbDevice)paramIntent.getParcelableExtra("device");
	    		if(localUsbDevice != null){
	    			if(!(paramIntent.getBooleanExtra("permission", false))){//用户取消授权
						if(this.mUsbPeramissionRecevierListener != null){
							mUsbPeramissionRecevierListener.onNoAccredit();
						}
	    			}else{
	    				String deviceName = localUsbDevice.getDeviceName();
	    		        if (deviceName.equals(this.mUsbDeviceName)){//若获得的deviceName与_usbDeviceName一致
	    		            if (this.mUsbPeramissionRecevierListener != null){
								mUsbPeramissionRecevierListener.onAccredit();
	    		            }else{
	    		            }
	    		        }else{
							if (this.mUsbPeramissionRecevierListener != null){
								mUsbPeramissionRecevierListener.onNoAccredit();
							}
	    		        }
	    			}
	    		}else{
	    			return;
	    		}
	    	} else{
				mUsbPeramissionRecevierListener.onNoAccredit();
				return;
	    	}
	    }
	}

	public void setUsbPermisionReceiverListener(UsbPermissionReceiverListener paramAsyncTaskListener) {
		this.mUsbPeramissionRecevierListener = paramAsyncTaskListener;
	}

	public void setUsbDeviceName(String usbDeviceName) {
		this.mUsbDeviceName = usbDeviceName;
	}
}