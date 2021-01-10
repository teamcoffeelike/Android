package com.hanul.caramelhomecchiato.util;

import com.bumptech.glide.request.RequestOptions;
import com.hanul.caramelhomecchiato.R;

public final class GlideUtils{
	private GlideUtils(){}

	public static final RequestOptions PROFILE_IMAGE = new RequestOptions()
			.placeholder(R.drawable.default_profile_image)
			.fallback(R.drawable.default_profile_image)
			.error(R.drawable.default_profile_image)
			.circleCrop();

	// TODO Received null model 로그폭탄: RequestListener?
}
