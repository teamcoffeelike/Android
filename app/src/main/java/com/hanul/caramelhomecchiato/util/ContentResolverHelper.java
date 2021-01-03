package com.hanul.caramelhomecchiato.util;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ContentResolverHelper{
	private ContentResolverHelper(){}

	/**
	 * URI에서 데이터를 읽어옵니다.
	 *
	 * @return URI의 리소스가 포함하는 바이너리 데이터.
	 */
	public static byte[] read(ContentResolver contentResolver, Uri uri) throws IOException{
		InputStream is = contentResolver.openInputStream(uri);
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];

		while(true){
			int read = bis.read(buffer);
			if(read==-1) break;
			os.write(buffer, 0, read);
		}

		return os.toByteArray();
	}
}
