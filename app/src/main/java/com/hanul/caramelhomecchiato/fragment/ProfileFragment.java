package com.hanul.caramelhomecchiato.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.EditProfileActivity;
import com.hanul.caramelhomecchiato.activity.WritePostActivity;
import com.hanul.caramelhomecchiato.adapter.ProfilePostAdapter;
import com.hanul.caramelhomecchiato.data.UserProfile;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.hanul.caramelhomecchiato.util.SpinnerHandler;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileFragment extends Fragment{
	private static final String TAG = "ProfileFragment";

	private ImageView imageViewProfile;
	private TextView textViewProfileName;
	private TextView textViewMotd;

	private Button buttonEditProfile;

	private ProfilePostAdapter adapter;

	@Nullable private UserProfile profile;

	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		imageViewProfile = view.findViewById(R.id.imageViewProfile);
		textViewProfileName = view.findViewById(R.id.textViewProfileName);
		textViewMotd = view.findViewById(R.id.textViewMotd);

		/* 프로필 편집 버튼 */
		buttonEditProfile = view.findViewById(R.id.buttonEditProfile);

		RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

		Context ctx = getContext();
		if(ctx!=null){
			buttonEditProfile.setOnClickListener(v -> {
				spinnerHandler.show();
				UserService.INSTANCE.profile(Auth.getInstance().expectLoginUser()).enqueue(new BaseCallback(){
					@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
						spinnerHandler.dismiss();
						startActivity(new Intent(ctx, EditProfileActivity.class)
								.putExtra(EditProfileActivity.EXTRA_PROFILE, NetUtils.GSON.fromJson(result, UserProfile.class)));
					}
					@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
						Log.e(TAG, "profile: error : "+error);
						Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 프로필을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
						spinnerHandler.dismiss();
					}
					@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
						Log.e(TAG, "profile: Failure : "+response.errorBody());
						Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 프로필을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
						spinnerHandler.dismiss();
					}
					@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
						Log.e(TAG, "profile: 예상치 못한 오류", t);
						Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 프로필을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
						spinnerHandler.dismiss();
					}
				});
			});

			view.findViewById(R.id.buttonNewPost).setOnClickListener(v -> {
				startActivity(new Intent(ctx, WritePostActivity.class));
			});

			recyclerView.setLayoutManager(new GridLayoutManager(ctx, 3, RecyclerView.VERTICAL, false));

			adapter = new ProfilePostAdapter();
			recyclerView.setAdapter(adapter);

			applyProfile();
		}else{
			Log.e(TAG, "onCreateView: ProfileFragment에 context 없음");
		}

		return view;
	}

	public void setProfile(@Nullable UserProfile profile){
		this.profile = profile;
		if(getActivity()!=null) applyProfile();
	}

	private void applyProfile(){
		Glide.with(this)
				.load(profile==null ? null : profile.getUser().getProfileImage())
				.apply(GlideUtils.profileImage())
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(imageViewProfile);

		textViewProfileName.setText(profile==null ? "" : profile.getUser().getName());
		String motd = profile==null ? "" : profile.getMotd()==null ? "" : profile.getMotd();
		textViewMotd.setText(motd);
		textViewMotd.setVisibility(motd.isEmpty() ? View.GONE : View.VISIBLE);
	}
}
