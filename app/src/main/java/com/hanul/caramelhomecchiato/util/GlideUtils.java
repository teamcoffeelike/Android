package com.hanul.caramelhomecchiato.util;

import android.app.Application;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.request.RequestOptions;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.drawable.EmptyAttachmentDrawable;

import java.util.Objects;

public final class GlideUtils{
	private GlideUtils(){}

	private static RequestOptions profileImage;
	private static RequestOptions postImage;
	private static RequestOptions fullScreenPostImage;
	private static RequestOptions recipeCover;

	public static void init(Application app){
		Drawable emptyAttachment = new EmptyAttachmentDrawable(app.getApplicationContext(),
				R.drawable.placeholder,
				250,
				250);

		profileImage = new RequestOptions()
				.placeholder(R.drawable.default_profile_image)
				.fallback(R.drawable.default_profile_image)
				.error(R.drawable.default_profile_image)
				.circleCrop();

		postImage = new RequestOptions()
				.placeholder(emptyAttachment)
				.fallback(emptyAttachment)
				.error(emptyAttachment)
				.centerCrop();

		fullScreenPostImage = new RequestOptions()
				.placeholder(emptyAttachment)
				.fallback(emptyAttachment)
				.error(emptyAttachment)
				.dontTransform();

		recipeCover = new RequestOptions()
				.placeholder(emptyAttachment)
				.fallback(emptyAttachment)
				.error(emptyAttachment)
				.centerCrop();
	}

	public static RequestOptions profileImage(){
		return Objects.requireNonNull(profileImage);
	}

	public static RequestOptions postImage(){
		return Objects.requireNonNull(postImage);
	}

	public static RequestOptions fullScreenPostImage(){
		return Objects.requireNonNull(fullScreenPostImage);
	}

	public static RequestOptions recipeCover(){
		return Objects.requireNonNull(recipeCover);
	}
}
