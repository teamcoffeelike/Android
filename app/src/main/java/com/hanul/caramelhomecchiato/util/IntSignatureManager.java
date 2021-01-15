package com.hanul.caramelhomecchiato.util;

import android.util.Log;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.Key;

import java.util.LinkedHashMap;
import java.util.Map;

public class IntSignatureManager{
	private static final String TAG = "IntSignatureManager";
	
	private final Map<Integer, Integer> map = new LinkedHashMap<>();

	public Key getKeyForId(int id){
		@Nullable Integer cache = map.get(id);
		int c;
		if(cache==null){
			c = 0;
			map.put(id, 0);
		}else c = cache;

		return new IntPairKey(id, c);
	}

	public void updateKeyForId(int id){
		Log.d(TAG, "updateKeyForId: "+id+" 시그니쳐 업데이트");
		@Nullable Integer cache = map.get(id);
		if(cache!=null){
			map.put(id, cache+1);
		}
	}
}
