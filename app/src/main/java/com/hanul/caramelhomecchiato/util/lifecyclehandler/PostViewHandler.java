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
import com.hanul.caramelhomecchiato.event.PostDeleteEventDispatcher;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.GlideUtils;

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

	public PostViewHandler(ComponentActivity activity, boolean fullScreenMode){
		this(activity,
				new UserViewHandler(activity),
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
			ViewGroup.LayoutParams layoutParams = imageViewPost.getLayoutParams();
			layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			imageViewPost.setLayoutParams(layoutParams);
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
			imageViewPost.setOnClickListener(v -> {
				if(post!=null){
					context.startActivity(new Intent(context, PostActivity.class)
							.putExtra(PostActivity.EXTRA_POST_ID, post.getId()));
				}
			});
		}

		PopupMenu popupMenu = new PopupMenu(context, buttonPostOption);
		popupMenu.getMenuInflater().inflate(R.menu.post_menu, popupMenu.getMenu());

		popupMenu.setOnMenuItemClickListener(item -> {
			int itemId = item.getItemId();
			if(itemId==R.id.postModify){
				context.startActivity(new Intent(context, WritePostActivity.class).putExtra(WritePostActivity.EXTRA_POST, post));
			}else if(itemId==R.id.postDelete){
				deletePost();
			}
			return true;
		});

		// TODO 작성자에게만 수정/삭제 권한이 있어야함!
		buttonPostOption.setOnClickListener(v -> popupMenu.show());

		buttonLike.setOnClickListener(v -> {
			// TODO
			if(buttonLike.isSelected()){
				buttonLike.setSelected(false);
			}else{
				buttonLike.setSelected(true);
				buttonLike.likeAnimation();
			}
		});
	}

	@Nullable public Post getPost(){
		return post;
	}
	public void setPost(@Nullable Post post){
		this.post = post;

		userViewHandler.setUser(post==null ? null : post.getAuthor());
		Glide.with(context)
				.load(post==null ? null : post.getImage())
				.apply(fullScreenMode ? GlideUtils.fullScreenPostImage() : GlideUtils.postImage())
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(imageViewPost);

		if(post!=null){
			textViewPost.setText(post.getText());

			textViewLikes.setText(context.getString(R.string.n_likes, post.getLikes()));

			buttonPostOption.setVisibility(
					post.getAuthor().getId()!=Auth.getInstance().expectLoginUser() ?
							View.GONE : View.VISIBLE); // TODO ????
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
							PostDeleteEventDispatcher.dispatch(post);
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
