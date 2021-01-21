package com.hanul.caramelhomecchiato.adapter;

import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.hanul.caramelhomecchiato.util.RecipeWriter;

import java.util.Objects;

public class WriteRecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
	private static final String TAG = "WriteRecipeAdapter";

	private final RecipeWriter writer;

	public WriteRecipeAdapter(RecipeWriter writer){
		this.writer = Objects.requireNonNull(writer);
	}

	@NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		if(viewType==0){
			return new Cover(LayoutInflater.from(parent.getContext())
					.inflate(R.layout.view_write_recipe_cover, parent, false));
		}else{
			return new Step(LayoutInflater.from(parent.getContext())
					.inflate(R.layout.view_write_recipe_step, parent, false)); // TODO
		}
	}
	@Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position){
		if(holder instanceof Cover) ((Cover)holder).bind();
		else if(holder instanceof Step) ((Step)holder).bind(position-1);
		else Log.w(TAG, "onBindViewHolder: "+holder+" 얜 또 누구냐");
	}
	@Override public int getItemCount(){
		return 1+writer.getNumberOfSteps();
	}
	@Override public int getItemViewType(int position){
		return position==0 ? 0 : 1;
	}


	private final class Cover extends RecyclerView.ViewHolder{
		private final ImageView imageViewCover;
		private final TextView textViewAttach;
		private final View categoryLayout;
		private final ImageView imageViewCategoryIcon;
		private final TextView textViewCategoryName;
		private final View addNextStepLayout;
		private final EditText editTextTitle;
		private final View backgroundLayout;

		private final PopupMenu recipeCategoryMenu;

		public Cover(@NonNull View itemView){
			super(itemView);

			imageViewCover = itemView.findViewById(R.id.imageViewCover);
			textViewAttach = itemView.findViewById(R.id.textViewAttach);
			categoryLayout = itemView.findViewById(R.id.categoryLayout);
			imageViewCategoryIcon = itemView.findViewById(R.id.imageViewCategoryIcon);
			textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
			addNextStepLayout = itemView.findViewById(R.id.addNextStepLayout);
			editTextTitle = itemView.findViewById(R.id.editTextTitle);
			backgroundLayout = itemView.findViewById(R.id.backgroundLayout);

			imageViewCover.setOnClickListener(v -> {
				writer.chooseCoverImage(this::updateImage);
			});

			recipeCategoryMenu = new PopupMenu(itemView.getContext(), categoryLayout);
			recipeCategoryMenu.getMenuInflater().inflate(R.menu.recipe_category_menu, recipeCategoryMenu.getMenu());
			recipeCategoryMenu.setOnMenuItemClickListener(item -> {
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

			categoryLayout.setOnClickListener(v -> recipeCategoryMenu.show());

			addNextStepLayout.setOnClickListener(v -> writer.insertStepAt(0));

			editTextTitle.addTextChangedListener(new TextWatcher(){
				@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}
				@Override public void onTextChanged(CharSequence s, int start, int before, int count){}
				@Override public void afterTextChanged(Editable s){
					writer.setTitle(s.toString());
				}
			});
		}

		private void bind(){
			updateImage();
			updateCategoryLayout();

			editTextTitle.setText(writer.getTitle());
		}

		private void setCategory(RecipeCategory category){
			writer.setCategory(category);
			updateCategoryLayout();
		}

		private void updateImage(){
			Uri photo = writer.getCoverImage();
			Glide.with(itemView)
					.load(photo)
					.apply(GlideUtils.recipeCover())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewCover);
			textViewAttach.setVisibility(photo!=null ? View.INVISIBLE : View.VISIBLE);
		}

		private void updateCategoryLayout(){
			RecipeCategory category = writer.getCategory();
			if(category!=null){
				imageViewCategoryIcon.setImageResource(category.getIcon());
				textViewCategoryName.setText(category.getName());
				backgroundLayout.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(itemView.getContext()), category.getColor()));
			}else{
				imageViewCategoryIcon.setImageDrawable(null);
				textViewCategoryName.setText("");
				backgroundLayout.setBackgroundColor(0);
			}
		}
	}

	private final class Step extends RecyclerView.ViewHolder{
		private final ImageView imageView;
		private final TextView textViewAttach;
		private final EditText editTextContent;
		private final View addPrevStepLayout;
		private final View removeStepLayout;
		private final View addNextStepLayout;
		private final View textViewDeleteImage;
		private final View textViewRevertImage;

		private int index = -1;

		public Step(@NonNull View itemView){
			super(itemView);

			imageView = itemView.findViewById(R.id.imageView);
			textViewAttach = itemView.findViewById(R.id.textViewAttach);
			editTextContent = itemView.findViewById(R.id.editTextContent);
			addPrevStepLayout = itemView.findViewById(R.id.addPrevStepLayout);
			addNextStepLayout = itemView.findViewById(R.id.addNextStepLayout);
			removeStepLayout = itemView.findViewById(R.id.removeStepLayout);
			textViewDeleteImage = itemView.findViewById(R.id.textViewDeleteImage);
			textViewRevertImage = itemView.findViewById(R.id.textViewRevertImage);

			imageView.setOnClickListener(v -> {
				if(this.index!=-1){
					writer.chooseStepImage(this.index, () -> {
						if(this.index!=-1) updateImage();
					});
				}
			});

			editTextContent.addTextChangedListener(new TextWatcher(){
				@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){ }
				@Override public void onTextChanged(CharSequence s, int start, int before, int count){ }
				@Override public void afterTextChanged(Editable s){
					if(Step.this.index!=-1){
						writer.setStepText(index, s.toString());
					}
				}
			});

			addPrevStepLayout.setOnClickListener(v -> {
				if(index >= 0) writer.insertStepAt(index);
			});
			removeStepLayout.setOnClickListener(v -> {
				if(index >= 0) writer.deleteStepAt(index);
			});
			addNextStepLayout.setOnClickListener(v -> {
				if(index >= 0) writer.insertStepAt(index+1);
			});

			textViewDeleteImage.setOnClickListener(v -> {
				if(index >= 0){
					writer.removeStepImage(index);
					updateImage();
				}
			});
			textViewRevertImage.setOnClickListener(v -> {
				if(index >= 0){
					writer.revertStepImage(index);
					updateImage();
				}
			});
		}

		protected void bind(int index){
			this.index = index;

			updateImage();

			editTextContent.setText(writer.getStepText(index));
		}

		private void updateImage(){
			Uri image = writer.getStepImage(index);
			if(image==null){
				Glide.with(itemView)
						.load((Uri)null)
						.apply(GlideUtils.recipeCover()) // TODO?
						.transition(DrawableTransitionOptions.withCrossFade())
						.into(imageView);
				textViewAttach.setVisibility(View.VISIBLE);

				textViewDeleteImage.setVisibility(View.GONE);
			}else{
				Glide.with(itemView)
						.load(image)
						.apply(GlideUtils.recipeCover()) // TODO?
						.transition(DrawableTransitionOptions.withCrossFade())
						.into(imageView);
				textViewAttach.setVisibility(View.INVISIBLE);

				textViewDeleteImage.setVisibility(View.VISIBLE);
			}
			if(writer.isEditMode()){
				RecipeWriter.ImageState stepImageState = writer.getStepImageState(index);
				switch(stepImageState){
					case REPLACED:
					case REMOVED:
						textViewRevertImage.setVisibility(View.VISIBLE);
						break;
					case ADDED:
					case UNEDITED:
						textViewRevertImage.setVisibility(View.GONE);
						break;
					default:
						Log.w(TAG, "updateImage: "+stepImageState);
				}
			}else textViewRevertImage.setVisibility(View.GONE);
		}
	}
}
