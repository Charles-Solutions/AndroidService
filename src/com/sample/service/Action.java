package com.sample.service;

public interface Action {
	
	//Alarm
	public static final String ALARM_ALERT_ACTION = "com.android.deskclock.ALARM_ALERT";
	public static final String ALARM_SNOOZE_ACTION = "com.android.deskclock.ALARM_SNOOZE";
	public static final String ALARM_DISMISS_ACTION = "com.android.deskclock.ALARM_DISMISS";
	public static final String ALARM_DONE_ACTION = "com.android.deskclock.ALARM_DONE";

	// Headset
	public static final String ACTION_HEADSET_PLUG = "";
	//App Actions
	public static final String EVENT_TRIGGER = "com.example.santiagodemo.services.EventListenerService";
	
}
