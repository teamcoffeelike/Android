package com.hanul.caramelhomecchiato.task;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WritePostTask<CONTEXT extends Context> extends JsonResponseTask<CONTEXT>{
	private final String text;
	private final Uri image;
	private final ContentResolver contentResolver;

	public WritePostTask(CONTEXT context, String text, Uri image){
		super(context, "writePost");
		this.text = text;
		this.image = image;
		this.contentResolver = context.getContentResolver();
	}

	@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
		byte[] bytes;

		try(InputStream is = contentResolver.openInputStream(image);
		    BufferedInputStream bis = new BufferedInputStream(is)){
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];

			while(true){
				int read = bis.read(buffer);
				if(read==-1) break;
				os.write(buffer, 0, read);
			}

			bytes = os.toByteArray();
		}catch(IOException e){
			throw new IllegalStateException("Couldn't process image", e);
		}

		builder.addTextBody("text", text);

		String mimeType = contentResolver.getType(image);
		ContentType contentType = mimeType!=null ? ContentType.create(mimeType) : ContentType.WILDCARD;
		builder.addBinaryBody("image", bytes, contentType, "Image");
	}
}
