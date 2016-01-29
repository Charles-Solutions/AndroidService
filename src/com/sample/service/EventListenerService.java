package com.sample.service;

import java.util.Date;









import android.app.AlarmManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class EventListenerService extends Service{

	private static final String TAG = "MyService";
	private EventsReceiver eventsReceiver = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		this.eventsReceiver = new EventsReceiver();
		startMonitoring();
		Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");	
	}
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
	}
	
    private void triggerEvent(String eventName, long datetime, String description){	
		
		Toast.makeText(this, eventName + " " + description, Toast.LENGTH_LONG).show();
	}
	
    public synchronized void startMonitoring() {
	    
		IntentFilter aFilter = new IntentFilter();
	    
	    aFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
	    
	    aFilter.addAction(BluetoothDevice.ACTION_FOUND);
	    aFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);	    
	    aFilter.addAction(BluetoothDevice.ACTION_FOUND);	    
	    
	    aFilter.addAction(Action.ALARM_ALERT_ACTION);
	    aFilter.addAction(Action.ALARM_SNOOZE_ACTION);
	    aFilter.addAction(Action.ALARM_DISMISS_ACTION);
	    aFilter.addAction(Action.ALARM_DONE_ACTION);
	    
	    aFilter.addAction(Intent.ACTION_HEADSET_PLUG);
	    
	    aFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
	    
	    registerReceiver(this.eventsReceiver, aFilter);	
	}
	
	public synchronized void stopMonitoring() {
	    unregisterReceiver(this.eventsReceiver);	  
	}	
	
    @SuppressWarnings("deprecation")
	private void handleWifiEvent(Context context, Intent intent){
		 boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        String eventName;
		NetworkInfo aNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
         if (!noConnectivity) {        	
        	 eventName = "NO-CONNECTIVITY-";            
         }
         else {
        	 eventName = "CONNECTIVITY-";        	
         }
         
         if (aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
             eventName +=  ConnectivityManager.TYPE_MOBILE;
             this.triggerEvent(eventName, new Date().getTime(), aNetworkInfo.getExtraInfo());
         }
         else if(aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI){
         	eventName +=  ConnectivityManager.TYPE_WIFI;
         	this.triggerEvent(eventName, new Date().getTime(), aNetworkInfo.getExtraInfo());
         }
	}
	
	private void handleBluetoothEvent(Context context, Intent intent){
		 this.triggerEvent("BLUETOOTH", new Date().getTime(), "testing Bluetooth");
	}
	
	private void handleHeadsetEvent(Context context, Intent intent, String action) {
		int state = intent.getIntExtra("state", -1);
		String statusDesc = "";
        switch (state) {
        case 0:
        	statusDesc = "Headset is unplugged";
            Log.d(TAG, "Headset is unplugged");
            break;
        case 1:
        	statusDesc = "Headset is plugged";
            Log.d(TAG, "Headset is plugged");
            break;
        default:
        	statusDesc = "I have no idea what the headset state is";
            Log.d(TAG, "I have no idea what the headset state is");
        }		
		this.triggerEvent("HEADSET-" + action, new Date().getTime(), statusDesc);
	}
	
	private void handleAlarmEvent(Context context, Intent intent, String action){
		 this.triggerEvent("ALARM-" + action, new Date().getTime(), "testing Alarm");
	}
	
	private void handleAudioEvent(Context context, Intent intent, String action){
		AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		String statusDesc = "";
		switch( audio.getRingerMode() ){
		case AudioManager.RINGER_MODE_NORMAL:
			statusDesc = "RINGER_MODE_NORMAL";
		   break;
		case AudioManager.RINGER_MODE_SILENT:
			statusDesc = "RINGER_MODE_SILENT";
		   break;
		case AudioManager.RINGER_MODE_VIBRATE:
			statusDesc = "RINGER_MODE_VIBRATE";
		   break;
		}
		
		this.triggerEvent("AUDIO", new Date().getTime(), statusDesc);
	}
	
	private class EventsReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			 
			 String action = intent.getAction();
			 
			 switch(action){
			 
			     case ConnectivityManager.CONNECTIVITY_ACTION:{
			    	 handleWifiEvent(context, intent); 
			     } break;
			     
			     case BluetoothDevice.ACTION_FOUND:{
			    	 handleBluetoothEvent(context, intent);
			     }break;
			     
			     case "android.media.VOLUME_CHANGED_ACTION":
			    	 handleAudioEvent(context, intent, action);
			    	 break;
			     
			     case Intent.ACTION_HEADSET_PLUG:
			    	 handleHeadsetEvent(context, intent, action);
			    	 break;
			     case Action.ALARM_DISMISS_ACTION:
			     case Action.ALARM_DONE_ACTION:
			     case Action.ALARM_SNOOZE_ACTION: {
			    	 handleAlarmEvent(context, intent, action); 
			     }break;			 
			 }
		}		
	}	
}
