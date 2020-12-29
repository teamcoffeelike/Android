package com.hanul.caramelhomecchiato.task;

import android.net.Uri;

import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;

public class WritePostTask<CONTEXT> extends JsonResponseTask<CONTEXT>{
	private final String text;
	private final Uri image;

	public WritePostTask(CONTEXT context, String text, Uri image){
		super(context, "writePost");
		this.text = text;
		this.image = image;
	}

	@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
		builder.addTextBody("text", text)
				.addBinaryBody("image", new File(image.getPath()));
	}
}
