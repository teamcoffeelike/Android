package com.hanul.caramelhomecchiato.util;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IOUtils{
	private IOUtils(){}

	/**
	 * URI에서 데이터를 읽어옵니다.
	 *
	 * @return URI의 리소스가 포함하는 바이너리 데이터.
	 */
	public static byte[] read(ContentResolver contentResolver, Uri uri) throws IOException{
		try(InputStream is = contentResolver.openInputStream(uri);
		    BufferedInputStream bis = new BufferedInputStream(is);
		    ByteArrayOutputStream os = new ByteArrayOutputStream()){

			copyInto(bis, os);

			return os.toByteArray();
		}
	}

	public static void copyInto(InputStream in, OutputStream out) throws IOException{
		byte[] buffer = new byte[1024];

		while(true){
			int read = in.read(buffer);
			if(read==-1) break;
			out.write(buffer, 0, read);
		}
	}
}
