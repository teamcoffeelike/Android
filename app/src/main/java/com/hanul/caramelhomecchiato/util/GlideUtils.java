package com.hanul.caramelhomecchiato.util;

import android.app.Application;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.request.RequestOptions;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.drawable.EmptyAttachmentDrawable;

import java.util.Objects;

public final class GlideUtils{
	private GlideUtils(){}

	private static Drawable emptyAttachment;
	private static RequestOptions profileImage;
	private static RequestOptions postImage;
	private static RequestOptions recipeCover;

	private static Int64Key key = new Int64Key(System.currentTimeMillis());

	public static void init(Application app){
		emptyAttachment = new EmptyAttachmentDrawable(app.getApplicationContext(),
				android.R.drawable.ic_menu_gallery,
				32,
				32);

		profileImage = new RequestOptions()
				.placeholder(R.drawable.default_profile_image)
				.fallback(R.drawable.default_profile_image)
				.error(R.drawable.default_profile_image)
				.signature(key)
				.circleCrop();

		postImage = new RequestOptions()
				.placeholder(emptyAttachment)
				.fallback(emptyAttachment)
				.error(emptyAttachment)
				.centerCrop();

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

	public static RequestOptions recipeCover(){
		return Objects.requireNonNull(recipeCover);
	}

	public static void resetProfileImageSignature(){
		key = new Int64Key(System.currentTimeMillis());
		profileImage = profileImage.signature(key);
	}
}
