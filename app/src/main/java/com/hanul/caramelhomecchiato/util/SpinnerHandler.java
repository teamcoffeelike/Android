package com.hanul.caramelhomecchiato.util;

import android.app.ProgressDialog;

import androidx.activity.ComponentActivity;
import androidx.fragment.app.Fragment;

public class SpinnerHandler extends AutoDisposed{
	private ProgressDialog spinnerDialog;

	public SpinnerHandler(ComponentActivity activity){
		super(activity);
	}
	public SpinnerHandler(Fragment fragment){
		super(fragment);
	}

	@Override protected void onDestroy(){
		dismiss();
	}

	public void show(){
		if(spinnerDialog==null){
			spinnerDialog = new ProgressDialog(context);
			spinnerDialog.setCancelable(false);
			spinnerDialog.setCanceledOnTouchOutside(false);
			spinnerDialog.show();
		}
	}

	public void dismiss(){
		if(spinnerDialog!=null){
			spinnerDialog.dismiss();
			spinnerDialog = null;
		}
	}
}
