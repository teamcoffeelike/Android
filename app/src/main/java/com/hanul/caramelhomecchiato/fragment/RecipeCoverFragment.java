package com.hanul.caramelhomecchiato.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.FullScreenImageActivity;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCover;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.UserViewHandler;

public class RecipeCoverFragment extends Fragment{
	public static final String ARG_RECIPE = "recipe";

	public static RecipeCoverFragment newInstance(Recipe recipe){
		RecipeCoverFragment f = new RecipeCoverFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_RECIPE, recipe);
		f.setArguments(args);
		return f;
	}

	private UserViewHandler userViewHandler;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_recipe_cover, container, false);

		Bundle arguments = getArguments();
		Parcelable recipeArg = arguments==null ? null : arguments.getParcelable(ARG_RECIPE);
		if(!(recipeArg instanceof Recipe))
			throw new IllegalStateException("RecipeCoverFragment에 Recipe 제공되지 않음");

		Recipe recipe = (Recipe)recipeArg;
		RecipeCover cover = recipe.getCover();

		View backgroundLayout = view.findViewById(R.id.backgroundLayout);
		TextView textViewTitle = view.findViewById(R.id.textViewTitle);
		ImageView imageViewCover = view.findViewById(R.id.imageViewCover);
		ImageView imageViewCategoryIcon = view.findViewById(R.id.imageViewCategoryIcon);

		backgroundLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), cover.getCategory().getColor()));

		textViewTitle.setText(cover.getTitle());

		Uri photo = cover.getCoverImage();

		Glide.with(this)
				.load(photo)
				.apply(GlideUtils.recipeCover())
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(imageViewCover);
		if(photo!=null){
			imageViewCover.setOnClickListener(v -> {
				startActivity(new Intent(getContext(), FullScreenImageActivity.class).putExtra(FullScreenImageActivity.EXTRA_IMAGE_URI, photo));
			});
		}

		userViewHandler = new UserViewHandler(view).setNameColor(0xFFFFFFFF);
		userViewHandler.setUser(cover.getAuthor());

		imageViewCategoryIcon.setImageResource(cover.getCategory().getIcon());
		imageViewCategoryIcon.setBackgroundTintList(new ColorStateList(
				new int[][]{new int[0]},
				new int[]{
						ContextCompat.getColor(requireContext(), cover.getCategory().getColor())
				}));

		return view;
	}
}
