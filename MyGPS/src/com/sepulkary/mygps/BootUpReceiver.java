package com.sepulkary.mygps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootUpReceiver extends BroadcastReceiver {// http://www.androidsnippets.com/autostart-an-application-at-bootup

	boolean autoReloadState = false;

	public static final String PREFS_NAME = "MyPrefsFile";

	@Override
	public void onReceive(Context context, Intent intent) {// http://stackoverflow.com/questions/9075030/shared-preferences-inside-broadcastreceiver
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		autoReloadState = settings.getBoolean("autoReloadState", false);

		if (autoReloadState) {
			Intent i = new Intent(context, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}
}
