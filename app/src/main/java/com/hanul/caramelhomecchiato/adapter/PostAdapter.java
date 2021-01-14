package com.hanul.caramelhomecchiato.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.FullScreenImageActivity;
import com.hanul.caramelhomecchiato.activity.WritePostActivity;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.UserViewHandler;

import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;
import xyz.hanks.library.bang.SmallBangView;

public class PostAdapter extends BaseAdapter<Post>{
	private static final String TAG = "PostAdapter";

	private final Set<ViewHolder> viewHolders = new HashSet<>();

	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post, parent, false));
	}

	@Override public void onViewAttachedToWindow(@NonNull BaseAdapter.ViewHolder<Post> holder){
		if(holder instanceof ViewHolder){
			ViewHolder h = (ViewHolder)holder;
			(h).userViewHandler.subscribeFollowEvent();
			viewHolders.add(h);
		}
	}

	@Override public void onViewRecycled(@NonNull BaseAdapter.ViewHolder<Post> holder){
		if(holder instanceof ViewHolder){
			((ViewHolder)holder).userViewHandler.unsubscribeFollowEvent();
			viewHolders.remove(holder);
		}
	}

	public void clearEvents(){
		for(ViewHolder viewHolder : viewHolders){
			viewHolder.userViewHandler.unsubscribeFollowEvent();
		}
		viewHolders.clear();
	}


	public static final class ViewHolder extends BaseAdapter.ViewHolder<Post>{
		private static int incr;

		private final int hash = incr++;

		private final UserViewHandler userViewHandler;

		private final ImageView imageViewPost;
		private final TextView textViewPost;

		private final View buttonLike;

		private final TextView textViewLikes;

		private final ImageButton buttonPostOption;

		private final SmallBangView buttonLikeAnimation;

		public ViewHolder(@NonNull View itemView){
			super(itemView);
			userViewHandler = new UserViewHandler(itemView);

			imageViewPost = itemView.findViewById(R.id.imageViewPost);
			imageViewPost.setOnClickListener(v -> {
				Post post = this.getItem();
				if(post!=null){
					Uri image = post.getImage();
					if(image!=null){
						Context context = this.itemView.getContext();
						context.startActivity(new Intent(context, FullScreenImageActivity.class)
								.putExtra(FullScreenImageActivity.EXTRA_IMAGE_URI, image));
					}
				}
			});
			textViewPost = itemView.findViewById(R.id.textViewPost);

			buttonLike = itemView.findViewById(R.id.buttonLike);

			textViewLikes = itemView.findViewById(R.id.textViewLikes);

			buttonPostOption = itemView.findViewById(R.id.buttonPostOption);

			buttonLikeAnimation = itemView.findViewById(R.id.buttonLikeAnimation);

			// TODO 작성자에게만 수정/삭제 권한이 있어야함!
			buttonPostOption.setOnClickListener(v -> {
				PopupMenu popupMenu = new PopupMenu(itemView.getContext(), v);
				popupMenu.getMenuInflater().inflate(R.menu.post_menu, popupMenu.getMenu());

				popupMenu.setOnMenuItemClickListener(item -> {
					int itemId = item.getItemId();
					if(itemId==R.id.postModify){
						Post post = getItem();
						Intent intent = new Intent(itemView.getContext(), WritePostActivity.class);
						intent.putExtra(WritePostActivity.EXTRA_POST, post);
						itemView.getContext().startActivity(intent);
					}else if(itemId==R.id.postDelete){
						deletePost();
					}
					return true;
				});
				popupMenu.show();
			});

			/*buttonLike.setOnClickListener(v -> {
				// TODO
				Toast.makeText(this.itemView.getContext(), ";)", Toast.LENGTH_SHORT).show();
			});*/

			buttonLikeAnimation.setOnClickListener(v -> {
				if(buttonLikeAnimation.isSelected()){
					buttonLikeAnimation.setSelected(false);
				}else{
					buttonLikeAnimation.setSelected(true);
					buttonLikeAnimation.likeAnimation();
				}
			});
		}

		@Override protected void setItem(int position, Post post){
			userViewHandler.setUser(post.getAuthor(), false);

			Glide.with(itemView)
					.load(post.getImage())
					.apply(GlideUtils.postImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewPost);

			textViewPost.setText(post.getText());

			textViewLikes.setText(itemView.getContext().getString(R.string.n_likes, post.getLikes()));

			buttonPostOption.setVisibility(
					post.getAuthor().getId()!=Auth.getInstance().expectLoginUser() ?
							View.GONE :
							View.VISIBLE); // TODO ????
		}

		private void deletePost(){
			new AlertDialog.Builder(itemView.getContext())
					.setTitle("정말 삭제하시겠습니까?")
					.setPositiveButton("예", (dialog, which) -> {
						PostService.INSTANCE.deletePost(getItem().getId()).enqueue(new BaseCallback(){
							@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
								Toast.makeText(itemView.getContext(), "포스트를 삭제했습니다.", Toast.LENGTH_SHORT).show();
							}
							@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
								Log.e(TAG, "deletePost: 예상치 못한 오류: "+error);
								Toast.makeText(itemView.getContext(), "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
							}
							@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
								Log.e(TAG, "Failure : "+response.errorBody());
								Toast.makeText(itemView.getContext(), "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
							}
							@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
								Log.e(TAG, "deletePost: Failure ", t);
								Toast.makeText(itemView.getContext(), "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
							}
						});
					})
					.setNegativeButton("아니오", (dialog, which) -> {})
					.show();
		}

		@Override public boolean equals(Object o){
			if(this==o) return true;
			if(o==null||getClass()!=o.getClass()) return false;

			ViewHolder that = (ViewHolder)o;

			return hash==that.hash;
		}
		@Override public int hashCode(){
			return hash;
		}
	}
}
