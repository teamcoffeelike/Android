package com.hanul.caramelhomecchiato.util.lifecyclehandler;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.activity.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

public class SpinnerHandler extends LifecycleHandler{
	private ProgressDialog spinnerDialog;

	public SpinnerHandler(ComponentActivity activity){
		super(activity);
	}
	public SpinnerHandler(Fragment fragment){
		super(fragment);
	}
	public SpinnerHandler(Context context, LifecycleOwner lifecycleOwner){
		super(context, lifecycleOwner);
	}

	@Override protected void onDestroy(){
		dismiss();
	}

	public void show(){
		if(spinnerDialog==null){
			spinnerDialog = new ProgressDialog(getContext());
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
