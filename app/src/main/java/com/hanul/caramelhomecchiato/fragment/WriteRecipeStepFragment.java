package com.hanul.caramelhomecchiato.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.WriteRecipeActivity.RecipeStepWriter;
import com.hanul.caramelhomecchiato.util.GlideUtils;

public class WriteRecipeStepFragment extends Fragment{
	private RecipeStepWriter writer;

	@Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		if(writer==null) throw new IllegalStateException("RecipeStepWriter 제공되지 않음");

		View view = inflater.inflate(R.layout.fragment_write_recipe_step, container, false);

		ImageView imageView = view.findViewById(R.id.imageView);
		TextView textViewAttach = view.findViewById(R.id.textViewAttach);
		EditText editTextContent = view.findViewById(R.id.editTextContent);

		Uri image = writer.getStep().getImage();
		Glide.with(view)
				.load(image)
				.apply(GlideUtils.recipeCover()) // TODO?
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(imageView);
		if(image!=null){
			textViewAttach.setVisibility(View.INVISIBLE);
		}

		return view;
	}

	public void setWriter(RecipeStepWriter writer){
		this.writer = writer;
	}
}
