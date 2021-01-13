package com.hanul.caramelhomecchiato.util.lifecyclehandler;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import java.util.HashMap;
import java.util.Map;

public class UriPermissionHandler extends LifecycleHandler{
	private static final String TAG = "UriPermissionHandler";
	
	private final Map<Uri, Flags> map = new HashMap<>();

	public UriPermissionHandler(ComponentActivity activity){
		super(activity);
	}
	public UriPermissionHandler(Fragment fragment){
		super(fragment);
	}
	public UriPermissionHandler(Context context, LifecycleOwner lifecycleOwner){
		super(context, lifecycleOwner);
	}

	public void grantPermissions(Intent intent, Uri uri, int flags){
		Log.d(TAG, "grantPermissions: Granted permission to "+uri);
		for(ResolveInfo resolveInfo : getContext()
				.getPackageManager()
				.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)){
			grant(resolveInfo.activityInfo.packageName, uri, flags);
		}
	}

	private void grant(String packageName, Uri uri, int flags){
		getContext().grantUriPermission(packageName, uri, flags);
		Flags f = map.get(uri);
		if(f==null){
			f = new Flags();
			map.put(uri, f);
		}
		f.flags |= flags;
	}

	public void revokePermissions(){
		Log.d(TAG, "revokePermissions: Revoked all "+map.size()+" granted permissions");
		for(Map.Entry<Uri, Flags> e : map.entrySet()){
			getContext().revokeUriPermission(e.getKey(), e.getValue().flags);
		}
		map.clear();
	}

	@Override protected void onDestroy(){
		revokePermissions();
	}

	private static final class Flags{
		private int flags;
	}
}
