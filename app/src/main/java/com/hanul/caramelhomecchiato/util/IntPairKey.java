package com.hanul.caramelhomecchiato.util;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Key;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

final class IntPairKey implements Key{
	private final int id;
	private final int c;

	public IntPairKey(int id, int c){
		this.id = id;
		this.c = c;
	}

	@Override public void updateDiskCacheKey(@NonNull MessageDigest messageDigest){
		messageDigest.update(ByteBuffer.allocate(8).putInt(id).putInt(c).array());
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;

		IntPairKey intPairKey = (IntPairKey)o;

		if(id!=intPairKey.id) return false;
		return c==intPairKey.c;
	}
	@Override public int hashCode(){
		int result = id;
		result = 31*result+c;
		return result;
	}
}
