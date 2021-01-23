package com.hanul.caramelhomecchiato.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.hanul.caramelhomecchiato.R;

public class FullScreenImageActivity extends AppCompatActivity{
	public static final String EXTRA_IMAGE_URI = "imageUri";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setTheme(R.style.FullScreenTheme);
		setContentView(R.layout.activity_full_screen_image);

		Parcelable uri = getIntent().getParcelableExtra(EXTRA_IMAGE_URI);
		if(!(uri instanceof Uri)){
			throw new IllegalStateException("FullScreenImageActivity에 URI 제공되지 않음");
		}

		ImageView imageView = findViewById(R.id.imageView);
		Glide.with(this)
				.load(uri)
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(imageView);
	}
}
