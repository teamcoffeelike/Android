package com.hanul.caramelhomecchiato;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SMSReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		//SMS를 받았을 경우에만 반응하도록 if문 삽입
		if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
			StringBuilder sms = new StringBuilder();	//SMS문자를 저장할 곳
			Bundle bundle = intent.getExtras();	//Bundle 객체에 문자 받아오기

			if (bundle != null) {
				//번들에 포함된 문자 데이터를 객체 배열로 받아오기
				Object[] pdusObj = (Object[]) bundle.get("pdus");

				//SMS를 받아올 SmsMessage 배열 만들기
				SmsMessage[] messages = new SmsMessage[pdusObj.length];
				for (int i = 0; i < pdusObj.length; i++) {
					messages[i]	= SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				}

				//SmsMessage 배열에 담긴 데이터를 append 메서드로 sms에 저장
				for (SmsMessage smsMessage : messages) {
					//문자 본문 받아오는 메서드
					sms.append(smsMessage.getMessageBody());
				}
				sms.toString();
			}
		}
	}
}