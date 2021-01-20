package com.hanul.caramelhomecchiato.fragment;

import android.content.Intent;
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
import com.hanul.caramelhomecchiato.data.RecipeStep;
import com.hanul.caramelhomecchiato.util.GlideUtils;

public class RecipeStepFragment extends Fragment{
	public static final String ARG_RECIPE = "recipe";
	public static final String ARG_STEP_INDEX = "stepIndex";

	public static RecipeStepFragment newInstance(Recipe recipe, int stepIndex){
		RecipeStepFragment f = new RecipeStepFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_RECIPE, recipe);
		args.putInt(ARG_STEP_INDEX, stepIndex);
		f.setArguments(args);
		return f;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_recipe_step, container, false);

		Bundle arguments = getArguments();
		Parcelable recipeArg = arguments==null ? null : arguments.getParcelable(ARG_RECIPE);
		if(!(recipeArg instanceof Recipe))
			throw new IllegalStateException("RecipeStepImageFragment에 Recipe 제공되지 않음");

		Recipe recipe = (Recipe)recipeArg;

		int stepIndex = arguments.getInt(ARG_STEP_INDEX, -1);

		if(stepIndex<0||stepIndex >= recipe.steps().size())
			throw new IllegalStateException("RecipeStepImageFragment에 stepIndex 제공되지 않음");

		RecipeStep step = recipe.steps().get(stepIndex);

		View imageLayout = view.findViewById(R.id.imageLayout);
		ImageView imageView = view.findViewById(R.id.imageView);
		View colorLayout = view.findViewById(R.id.colorLayout);
		TextView textViewText = view.findViewById(R.id.textViewText);

		Uri image = step.getImage();
		if(image==null){
			imageLayout.setVisibility(View.GONE);
			colorLayout.setVisibility(View.VISIBLE);
			colorLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), recipe.getCover().getCategory().getColor()));
		}else{
			Glide.with(this)
					.load(image)
					.apply(GlideUtils.recipeCover())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageView);
			imageView.setOnClickListener(v -> {
				startActivity(new Intent(requireContext(), FullScreenImageActivity.class)
						.putExtra(FullScreenImageActivity.EXTRA_IMAGE_URI, image));
			});
		}

		textViewText.setText(step.getText());

		return view;
	}
}
