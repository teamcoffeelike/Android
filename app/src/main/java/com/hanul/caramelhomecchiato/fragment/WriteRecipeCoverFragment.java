package com.hanul.caramelhomecchiato.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.WriteRecipeActivity.RecipeCoverWriter;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.util.GlideUtils;

public class WriteRecipeCoverFragment extends Fragment{
	private static final String TAG = "WriteRecipeCoverFragmen";

	private RecipeCoverWriter writer;

	private ImageView imageViewCategoryIcon;
	private TextView textViewCategoryName;
	private View coverLayout;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		if(writer==null) throw new IllegalStateException("RecipeCoverWriter 제공되지 않음");

		View view = inflater.inflate(R.layout.fragment_write_recipe_cover, container, false);

		ImageView imageViewCover = view.findViewById(R.id.imageViewCover);
		TextView textViewAttach = view.findViewById(R.id.textViewAttach);
		View categoryLayout = view.findViewById(R.id.categoryLayout);
		imageViewCategoryIcon = view.findViewById(R.id.imageViewCategoryIcon);
		textViewCategoryName = view.findViewById(R.id.textViewCategoryName);
		EditText editTextTitle = view.findViewById(R.id.editTextTitle);
		coverLayout = view.findViewById(R.id.coverLayout);

		Context context = getContext();
		if(context==null) throw new IllegalStateException("Context 없음");

		Uri photo = writer.getCover().getPhoto();
		Glide.with(this)
				.load(photo)
				.apply(GlideUtils.recipeCover())
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(imageViewCover);
		if(photo!=null){
			textViewAttach.setVisibility(View.INVISIBLE);
		}

		PopupMenu popupMenu = new PopupMenu(context, categoryLayout);
		popupMenu.getMenuInflater().inflate(R.menu.recipe_category_menu, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(item -> {
			int id = item.getItemId();
			if(id==R.id.hotCoffee) setCategory(RecipeCategory.HOT_COFFEE);
			else if(id==R.id.iceCoffee) setCategory(RecipeCategory.ICE_COFFEE);
			else if(id==R.id.tea) setCategory(RecipeCategory.TEA);
			else if(id==R.id.ade) setCategory(RecipeCategory.ADE);
			else if(id==R.id.smoothie) setCategory(RecipeCategory.SMOOTHIE);
			else if(id==R.id.etc) setCategory(RecipeCategory.ETC);
			else{
				Log.e(TAG, "onCreateView: Invalid menu item "+id);
				return false;
			}
			return true;
		});

		categoryLayout.setOnClickListener(v -> popupMenu.show());

		updateCategoryLayout();

		return view;
	}

	public void setWriter(RecipeCoverWriter writer){
		this.writer = writer;
	}

	private void setCategory(RecipeCategory category){
		if(writer.getCover().getCategory()!=category){
			writer.setCategory(category);
			updateCategoryLayout();
		}
	}

	private void updateCategoryLayout(){
		RecipeCategory category = writer.getCover().getCategory();
		imageViewCategoryIcon.setImageResource(category.getIcon());
		textViewCategoryName.setText(category.getName());
		coverLayout.setBackgroundColor(ContextCompat.getColor(getContext(), category.getColor()));
	}
}
