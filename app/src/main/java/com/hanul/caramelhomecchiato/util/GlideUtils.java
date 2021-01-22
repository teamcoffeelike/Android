package com.hanul.caramelhomecchiato.util;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import com.bumptech.glide.request.RequestOptions;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.drawable.EmptyAttachmentDrawable;

import java.util.Objects;

public final class GlideUtils{
	private GlideUtils(){}

	private static final int ATTACHMENT_PLACEHOLDER_ICON_SIZE = 100;

	private static RequestOptions profileImage;
	private static RequestOptions postImage;
	private static RequestOptions fullScreenPostImage;
	private static RequestOptions recipeCover;
	private static RequestOptions recipeCoverNoCenterCrop;

	public static void init(Application app){
		Context ctx = app.getApplicationContext();
		int dp = (int)Math.round(ATTACHMENT_PLACEHOLDER_ICON_SIZE*((double)ctx.getResources().getDisplayMetrics().densityDpi/DisplayMetrics.DENSITY_DEFAULT));
		Drawable emptyAttachment = new EmptyAttachmentDrawable(ctx,
				R.drawable.placeholder,
				dp,
				dp);

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

		recipeCoverNoCenterCrop = new RequestOptions()
				.placeholder(emptyAttachment)
				.fallback(emptyAttachment)
				.error(emptyAttachment);
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

	public static RequestOptions recipeCoverNoCenterCrop(){
		return Objects.requireNonNull(recipeCoverNoCenterCrop);
	}
}
