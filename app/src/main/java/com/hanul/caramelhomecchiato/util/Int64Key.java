package com.hanul.caramelhomecchiato.util;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Key;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class Int64Key implements Key{
	private final long value;

	public Int64Key(long value){this.value = value;}

	public long getValue(){
		return value;
	}

	@Override public void updateDiskCacheKey(@NonNull MessageDigest messageDigest){
		messageDigest.update(ByteBuffer.allocate(8).putLong(value).array());
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(!(o instanceof Int64Key)) return false;

		return value==((Int64Key)o).value;
	}

	/**
	 * 아 그래서 api 버전이 뭔데ㅋㅋ
	 *
	 * @see Long#hashCode(long)
	 */
	@Override public int hashCode(){
		return (int)(value^(value >>> 32));
	}

	@NonNull @Override public String toString(){
		return "Int64Key{"+
				"value="+value+
				'}';
	}
}
