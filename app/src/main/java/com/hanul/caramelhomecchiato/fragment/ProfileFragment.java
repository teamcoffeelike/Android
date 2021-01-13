package com.hanul.caramelhomecchiato.fragment;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.hanul.caramelhomecchiato.util.lifecyclehandler.SpinnerHandler;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.UserViewHandler;
import com.hanul.caramelhomecchiato.widget.FollowButton;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileFragment extends Fragment{
	private static final String TAG = "ProfileFragment";

	private TextView textViewMotd;

	private View myProfileLayout;
	private View otherProfileLayout;

	@Nullable private UserProfile profile;

	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);
	private UserViewHandler userViewHandler;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		textViewMotd = view.findViewById(R.id.textViewMotd);

		myProfileLayout = view.findViewById(R.id.myProfileLayout);
		otherProfileLayout = view.findViewById(R.id.otherProfileLayout);

		FollowButton buttonFollow = view.findViewById(R.id.buttonFollow);

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

		view.findViewById(R.id.buttonNewPost).setOnClickListener(v -> {
			startActivity(new Intent(getContext(), WritePostActivity.class));
		});

		Context ctx = getContext();
		if(ctx==null) throw new IllegalStateException("ProfileFragment에 context 없음");

		RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new GridLayoutManager(ctx, 3, RecyclerView.VERTICAL, false));

		ProfilePostAdapter adapter = new ProfilePostAdapter();
		recyclerView.setAdapter(adapter); // TODO adapter

		userViewHandler = new UserViewHandler(ctx,
				view.findViewById(R.id.imageViewProfile),
				view.findViewById(R.id.textViewProfileName),
				null,
				buttonFollow);

		applyProfile();

		return view;
	}

	@Override public void onDestroy(){
		super.onDestroy();
		userViewHandler.unsubscribeFollowEvent();
	}

	public void setProfile(@Nullable UserProfile profile){
		this.profile = profile;
		if(getActivity()!=null) applyProfile();
	}

	private void applyProfile(){
		UserProfile profile = this.profile;
		userViewHandler.setUser(profile==null ? null : profile.getUser());

		if(profile==null){
			textViewMotd.setText("");
			textViewMotd.setVisibility(View.GONE);
			myProfileLayout.setVisibility(View.GONE);
			otherProfileLayout.setVisibility(View.GONE);
		}else{
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
		}
	}
}
