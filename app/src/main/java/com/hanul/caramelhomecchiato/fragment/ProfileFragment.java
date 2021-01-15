package com.hanul.caramelhomecchiato.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.EditProfileActivity;
import com.hanul.caramelhomecchiato.activity.ProfileActivity;
import com.hanul.caramelhomecchiato.activity.WritePostActivity;
import com.hanul.caramelhomecchiato.adapter.ProfilePostAdapter;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.data.UserProfile;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.PostScrollHandler;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.SpinnerHandler;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.UserViewHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements PostScrollHandler.Listener<Post>{
	private static final String TAG = "ProfileFragment";

	private TextView textViewMotd;

	private View myProfileLayout;
	private View otherProfileLayout;

	private TextView textViewError;
	private View endOfList;

	@Nullable private UserProfile profile;

	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);
	private UserViewHandler userViewHandler;
	private final PostScrollHandler postScrollHandler = new PostScrollHandler(this,
			since -> PostService.INSTANCE.usersPosts(since, 12, profile.getUser().getId()),
			this);
	private ProfilePostAdapter profilePostAdapter;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		Context ctx = getContext();
		if(ctx==null) throw new IllegalStateException("ProfileFragment에 context 없음");

		textViewMotd = view.findViewById(R.id.textViewMotd);

		myProfileLayout = view.findViewById(R.id.myProfileLayout);
		otherProfileLayout = view.findViewById(R.id.otherProfileLayout);

		textViewError = view.findViewById(R.id.textViewError);
		endOfList = view.findViewById(R.id.endOfList);

		view.findViewById(R.id.buttonEditProfile).setOnClickListener(v -> {
			spinnerHandler.show();
			UserService.INSTANCE.profile(Auth.getInstance().expectLoginUser()).enqueue(new BaseCallback(){
				@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
					spinnerHandler.dismiss();
					startActivity(new Intent(getContext(), EditProfileActivity.class)
							.putExtra(EditProfileActivity.EXTRA_PROFILE, NetUtils.GSON.fromJson(result, UserProfile.class)));
				}
				@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
					Log.e(TAG, "profile: error : "+error);
					error();
				}
				@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
					Log.e(TAG, "profile: Failure : "+response.errorBody());
					error();
				}
				@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
					Log.e(TAG, "profile: 예상치 못한 오류", t);
					error();
				}

				private void error(){
					Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 프로필을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
					spinnerHandler.dismiss();
				}
			});
		});

		view.findViewById(R.id.buttonNewPost).setOnClickListener(v ->
				startActivityForResult(new Intent(getContext(), WritePostActivity.class),
						ProfileActivity.WRITE_POST_ACTIVITY));

		RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new GridLayoutManager(ctx, 3, RecyclerView.VERTICAL, false));

		profilePostAdapter = new ProfilePostAdapter();
		recyclerView.setAdapter(profilePostAdapter);

		userViewHandler = new UserViewHandler(ctx,
				view.findViewById(R.id.imageViewProfile),
				view.findViewById(R.id.textViewProfileName),
				null,
				view.findViewById(R.id.buttonFollow));

		NestedScrollView scrollView = view.findViewById(R.id.scrollView);
		scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)(v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
			int bottom = v.getChildAt(0).getBottom();
			int y = v.getHeight()+scrollY;

			if(bottom-2000<=y){
				if(profile!=null) postScrollHandler.enqueue();
			}
		});

		applyProfile(true);

		return view;
	}

	@Override public void onResume(){
		super.onResume();
		if(profile!=null) postScrollHandler.enqueue();
	}

	@Override public void onDestroy(){
		super.onDestroy();
		userViewHandler.unsubscribeFollowEvent();
	}

	@Override public void append(List<Post> posts, boolean endOfList, boolean reset){
		List<Post> elements = profilePostAdapter.elements();
		int size = elements.size();
		if(reset){
			elements.clear();
			profilePostAdapter.notifyItemRangeRemoved(0, size);
			elements.addAll(posts);
			profilePostAdapter.notifyItemRangeInserted(0, posts.size());
		}else{
			elements.addAll(posts);
			profilePostAdapter.notifyItemRangeInserted(size, posts.size());
		}
		profilePostAdapter.notifyDataSetChanged();

		textViewError.setVisibility(View.GONE);
		this.endOfList.setVisibility(endOfList ? View.VISIBLE : View.GONE);
	}

	@Override public void error(){
		textViewError.setVisibility(View.VISIBLE);
		endOfList.setVisibility(View.GONE);
	}

	public void setProfile(@Nullable UserProfile profile){
		int id = this.profile==null ? 0 : this.profile.getUser().getId();
		int id2 = profile==null ? 0 : profile.getUser().getId();
		this.profile = profile;
		if(getActivity()!=null) applyProfile(id!=id2);
	}

	private void applyProfile(boolean reset){
		UserProfile profile = this.profile;
		userViewHandler.setUser(profile==null ? null : profile.getUser());

		if(profile==null){
			textViewMotd.setText("");
			textViewMotd.setVisibility(View.GONE);
			myProfileLayout.setVisibility(View.GONE);
			otherProfileLayout.setVisibility(View.GONE);
		}else{
			if(reset) postScrollHandler.enqueue(true);
			String motd = profile.getMotd()==null ? "" : profile.getMotd();
			textViewMotd.setText(motd);
			textViewMotd.setVisibility(motd.isEmpty() ? View.GONE : View.VISIBLE);

			boolean myProfile = Auth.getInstance().expectLoginUser()==profile.getUser().getId();

			if(myProfile){
				myProfileLayout.setVisibility(View.VISIBLE);
				otherProfileLayout.setVisibility(View.GONE);
			}else{
				myProfileLayout.setVisibility(View.GONE);
				otherProfileLayout.setVisibility(View.VISIBLE);
			}
			postScrollHandler.enqueue();
		}
	}

	@Override public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==ProfileActivity.WRITE_POST_ACTIVITY&&resultCode==Activity.RESULT_OK){
			postScrollHandler.enqueue(true);
		}
	}
}
