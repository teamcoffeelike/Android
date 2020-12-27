package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.RecipeCover;

public class RecipeCoverImageFragment extends Fragment{
	public static final String ARG_COVER = "cover";

	public static RecipeCoverImageFragment newInstance(RecipeCover cover){
		RecipeCoverImageFragment f = new RecipeCoverImageFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_COVER, cover);
		f.setArguments(args);
		return f;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_recipe_cover_image, container, false);

		Bundle arguments = getArguments();
		if(arguments==null) throw new IllegalStateException("No arguments");
		RecipeCover cover = arguments.getParcelable(ARG_COVER);
		if(cover==null) throw new IllegalStateException("No RecipeCover provided");

		ImageView imageView = view.findViewById(R.id.imageView);
		Glide.with(this)
				.load(cover.getPhoto())
				.into(imageView);

		return view;
	}
}
