package com.hanul.caramelhomecchiato;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;

public class SMSReceiver extends BroadcastReceiver {
	public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:MM:SS");

	@Override
	public void onReceive(Context context, Intent intent) {

		//SMS를 받았을 경우에만 반응
		if (intent.getAction().equals(
			"android.provider.Telephony.SMS_RECEIVED")) {
			StringBuilder sms = new StringBuilder();	//SMS문자를 저장할 곳
			Bundle bundle = intent.getExtras();	//Bundle 객체에 문자를 받아옴

			if (bundle != null) {

			}
		}
	}

	private SmsMessage[] parseSmsMessage(Bundle bundle) {
	}
}
