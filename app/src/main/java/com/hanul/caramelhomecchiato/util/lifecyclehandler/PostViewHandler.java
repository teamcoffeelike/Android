package com.hanul.caramelhomecchiato.util.lifecyclehandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.FullScreenImageActivity;
import com.hanul.caramelhomecchiato.activity.PostActivity;
import com.hanul.caramelhomecchiato.activity.WritePostActivity;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.event.PostDeleteEvent;
import com.hanul.caramelhomecchiato.event.PostLikeEvent;
import com.hanul.caramelhomecchiato.event.Ticket;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.hanul.caramelhomecchiato.util.SignatureManagers;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import xyz.hanks.library.bang.SmallBangView;

public class PostViewHandler{
	private static final String TAG = "PostViewHandler";

	private final Context context;
	private final UserViewHandler userViewHandler;

	private final ImageView imageViewPost;
	private final TextView textViewPost;

	private final SmallBangView buttonLike;
	private final TextView textViewLikes;

	private final ImageButton buttonPostOption;
	private final boolean fullScreenMode;

	@Nullable private Post post;

	private Ticket postLikeTicket;

	public PostViewHandler(ComponentActivity activity, boolean fullScreenMode){
		this(activity,
				new UserViewHandler(activity),
				activity.findViewById(R.id.postContentLayout),
				activity.findViewById(R.id.imageViewPost),
				activity.findViewById(R.id.textViewPost),
				activity.findViewById(R.id.buttonLike),
				activity.findViewById(R.id.textViewLikes),
				activity.findViewById(R.id.buttonPostOption),
				activity.findViewById(R.id.divider),
				fullScreenMode);
	}
	public PostViewHandler(View rootView, boolean fullScreenMode){
		this(rootView.getContext(),
				new UserViewHandler(rootView),
				rootView.findViewById(R.id.postContentLayout),
				rootView.findViewById(R.id.imageViewPost),
				rootView.findViewById(R.id.textViewPost),
				rootView.findViewById(R.id.buttonLike),
				rootView.findViewById(R.id.textViewLikes),
				rootView.findViewById(R.id.buttonPostOption),
				rootView.findViewById(R.id.divider),
				fullScreenMode);
	}
	public PostViewHandler(Context context,
	                       UserViewHandler userViewHandler,
	                       View postContentLayout,
	                       ImageView imageViewPost,
	                       TextView textViewPost,
	                       SmallBangView buttonLike,
	                       TextView textViewLikes,
	                       ImageButton buttonPostOption,
	                       View divider,
	                       boolean fullScreenMode){
		this.context = Objects.requireNonNull(context);
		this.userViewHandler = userViewHandler;

		this.imageViewPost = imageViewPost;
		this.textViewPost = textViewPost;

		this.buttonLike = buttonLike;
		this.textViewLikes = textViewLikes;

		this.buttonPostOption = buttonPostOption;
		this.fullScreenMode = fullScreenMode;

		if(fullScreenMode){
			imageViewPost.setAdjustViewBounds(true);

			// TODO:
			//   Glide는 WRAP_CONTENT를 그닥 좋아하지 않는 듯.
			ViewGroup.LayoutParams layoutParams = imageViewPost.getLayoutParams();
			layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			imageViewPost.setLayoutParams(layoutParams);

			imageViewPost.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageViewPost.setOnClickListener(v -> {
				if(post!=null){
					Uri image = post.getImage();
					if(image!=null){
						context.startActivity(new Intent(context, FullScreenImageActivity.class)
								.putExtra(FullScreenImageActivity.EXTRA_IMAGE_URI, image));
					}
				}
			});
			divider.setVisibility(View.GONE);
		}else{
			imageViewPost.setScaleType(ImageView.ScaleType.CENTER_CROP);
			postContentLayout.setOnClickListener(v -> {
				if(post!=null){
					context.startActivity(new Intent(context, PostActivity.class)
							.putExtra(PostActivity.EXTRA_POST_ID, post.getId()));
				}
			});
		}

		PopupMenu popupMenu = new PopupMenu(context, buttonPostOption);
		popupMenu.getMenuInflater().inflate(R.menu.post_option_menu, popupMenu.getMenu());

		popupMenu.setOnMenuItemClickListener(item -> {
			int itemId = item.getItemId();
			if(itemId==R.id.editPost) context.startActivity(new Intent(context, WritePostActivity.class).putExtra(WritePostActivity.EXTRA_POST, post));
			else if(itemId==R.id.deletePost) deletePost();
			else{
				Log.w(TAG, "PostViewHandler: post_option_menu의 알 수 없는 옵션 "+itemId);
				return false;
			}
			return true;
		});

		buttonPostOption.setOnClickListener(v -> popupMenu.show());

		buttonLike.setOnClickListener(v -> {
			if(this.post!=null){
				Boolean likedByYou = this.post.getLikedByYou();
				if(likedByYou!=null){
					boolean newLike = !likedByYou;
					int id = this.post.getId();
					PostService.INSTANCE.likePost(id, newLike).enqueue(new BaseCallback(){
						@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
							Log.d(TAG, "likePost: success");
							int likes = result.get("likes").getAsInt();
							PostLikeEvent.dispatch(id, newLike, likes);
						}
						@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
							Log.e(TAG, "likePost: "+error);
						}
						@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
							Log.e(TAG, "likePost: "+response.errorBody());
						}
						@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
							Log.e(TAG, "likePost: ", t);
						}
					});
				}
			}
		});
	}

	@Nullable public Post getPost(){
		return post;
	}
	public void setPost(@Nullable Post post){
		this.post = post;

		userViewHandler.setUser(post==null ? null : post.getAuthor());
		if(this.postLikeTicket!=null) postLikeTicket.unsubscribe();

		if(post!=null){
			Glide.with(context)
					.load(post.getImage())
					.apply(fullScreenMode ? GlideUtils.fullScreenPostImage() : GlideUtils.postImage())
					.signature(SignatureManagers.POST_IMAGE.getKeyForId(post.getId()))
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewPost);
			textViewPost.setText(post.getText());

			buttonLike.setSelected(post.getLikedByYou()!=null&&post.getLikedByYou());
			textViewLikes.setText(context.getString(R.string.n_likes, post.getLikes()));

			buttonPostOption.setVisibility(
					post.getAuthor().getId()!=Auth.getInstance().expectLoginUser() ?
							View.GONE : View.VISIBLE);

			this.postLikeTicket = PostLikeEvent.subscribe(post.getId(), this::onPostLiked);
		}else{
			Glide.with(context)
					.load((Uri)null)
					.apply(fullScreenMode ? GlideUtils.fullScreenPostImage() : GlideUtils.postImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewPost);
		}
	}

	private void onPostLiked(boolean liked, int likes){
		if(this.post!=null){
			this.post.setLikedByYou(liked);
			this.post.setLikes(likes);
			textViewLikes.setText(context.getString(R.string.n_likes, likes));
			setButtonLikeState(liked);
		}
	}
	private void setButtonLikeState(boolean liked){
		if(buttonLike.isSelected()!=liked){
			buttonLike.setSelected(liked);
			if(liked) buttonLike.likeAnimation();
		}
	}

	private void deletePost(){
		Post post = this.post;
		if(post==null) return;
		new AlertDialog.Builder(context)
				.setTitle("정말 삭제하시겠습니까?")
				.setPositiveButton("예", (dialog, which) -> {
					PostService.INSTANCE.deletePost(post.getId()).enqueue(new BaseCallback(){
						@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
							Toast.makeText(context, "포스트를 삭제했습니다.", Toast.LENGTH_SHORT).show();
							PostDeleteEvent.dispatch(post);
						}
						@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
							Log.e(TAG, "deletePost: 예상치 못한 오류: "+error);
							toastToast();
						}
						@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
							Log.e(TAG, "Failure : "+response.errorBody());
							toastToast();
						}
						@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
							Log.e(TAG, "deletePost: Failure ", t);
							toastToast();
						}

						private void toastToast(){
							Toast.makeText(context, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
						}
					});
				})
				.setNegativeButton("아니오", (dialog, which) -> {})
				.show();
	}
}
