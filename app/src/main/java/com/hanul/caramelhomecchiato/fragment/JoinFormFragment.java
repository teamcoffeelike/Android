package com.hanul.caramelhomecchiato.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.JoinActivity.JoinType;
import com.hanul.caramelhomecchiato.network.JoinService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.SpinnerHandler;
import com.hanul.caramelhomecchiato.util.Validate;

import retrofit2.Call;
import retrofit2.Response;

public class JoinFormFragment extends Fragment{
	private static final String TAG = "JoinFormFragment";

	private EditText editTextEmailPhone;
	private EditText editTextName;
	private EditText editTextPassword;
	private EditText editTextPasswordConfirm;
	private TextView textViewPwCheck;
	private ImageView imgEmailPhone;

	private JoinType type;

	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_join_form, container, false);
		editTextEmailPhone = view.findViewById(R.id.editTextEmailPhone);
		editTextName = view.findViewById(R.id.editTextName);
		editTextPassword = view.findViewById(R.id.editTextPassword);
		editTextPasswordConfirm = view.findViewById(R.id.editTextPasswordConfirm);
		textViewPwCheck = view.findViewById(R.id.textViewPwCheck);
		imgEmailPhone = view.findViewById(R.id.imgEmailPhone);

		TextWatcher textWatcher = new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
				// 두 비밀번호 동일 여부 검사
				String password = editTextPassword.getText().toString();
				String confirm = editTextPasswordConfirm.getText().toString();

				if(password.isEmpty()||confirm.isEmpty()){
					textViewPwCheck.setVisibility(View.INVISIBLE);
				}else if(password.equals(confirm)){
					textViewPwCheck.setVisibility(View.VISIBLE);
					textViewPwCheck.setText("비밀번호가 일치합니다.");
					textViewPwCheck.setTextColor(getResources().getColor(R.color.ForestGreenTraditional));
				}else{
					textViewPwCheck.setVisibility(View.VISIBLE);
					textViewPwCheck.setText("비밀번호가 일치하지 않습니다.");
					textViewPwCheck.setTextColor(getResources().getColor(R.color.red));
				}
			}

			@Override
			public void afterTextChanged(Editable s){}
		};

		editTextPasswordConfirm.addTextChangedListener(textWatcher);
		editTextPassword.addTextChangedListener(textWatcher);


		view.findViewById(R.id.buttonConfirm).setOnClickListener(v -> {
			String name = editTextName.getText().toString().trim();
			String password = editTextPassword.getText().toString();
			String pwConfirm = editTextPasswordConfirm.getText().toString();
			String emailOrPhone = editTextEmailPhone.getText().toString().trim();

			Call<JsonObject> call;

			if(!Validate.name(name)){
				Toast.makeText(getContext(), "부적합한 이름입니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			if(!Validate.password(password)){
				Toast.makeText(getContext(), "부적합한 비밀번호입니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			if(!password.equals(pwConfirm)){
				Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			JoinType type = this.type;
			switch(type){
			case WITH_PHONE:
				if(!Validate.phoneNumber(emailOrPhone)){
					Toast.makeText(getContext(), "부적합한 전화번호입니다.", Toast.LENGTH_SHORT).show();
					return;
				}
				call = JoinService.INSTANCE.joinWithPhoneNumber(name, emailOrPhone, password);
				break;
			case WITH_EMAIL:
				if(!Validate.email(emailOrPhone)){
					Toast.makeText(getContext(), "부적합한 이메일입니다.", Toast.LENGTH_SHORT).show();
					return;
				}
				call = JoinService.INSTANCE.joinWithEmail(name, emailOrPhone, password);
				break;
			default:
				throw new IllegalArgumentException("type");
			}

			spinnerHandler.show();

			call.enqueue(new BaseCallback(){
				@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
					Auth.getInstance().setLoginData(result);
					FragmentActivity activity = getActivity();
					if(activity!=null){
						activity.setResult(Activity.RESULT_OK);
						activity.finish();
					}
				}
				@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
					switch(error){
					case "bad_name":
						Toast.makeText(getContext(), "부적합한 이름입니다.", Toast.LENGTH_SHORT).show();
						break;
					case "bad_email":
						Toast.makeText(getContext(), "부적합한 이메일입니다.", Toast.LENGTH_SHORT).show();
						break;
					case "bad_phone_number":
						Toast.makeText(getContext(), "부적합한 전화번호입니다.", Toast.LENGTH_SHORT).show();
						break;
					case "bad_password":
						Toast.makeText(getContext(), "부적합한 비밀번호입니다.", Toast.LENGTH_SHORT).show();
						break;
					case "user_exists":
						switch(type){
						case WITH_PHONE:
							Toast.makeText(getContext(), "동일한 전화번호를 가진 유저가 이미 존재합니다.", Toast.LENGTH_SHORT).show();
							break;
						case WITH_EMAIL:
							Toast.makeText(getContext(), "동일한 이메일을 가진 유저가 이미 존재합니다.", Toast.LENGTH_SHORT).show();
							break;
						}
						break;
					default:
						Log.e(TAG, error);
						Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 회원가입을 완료할 수 없습니다.", Toast.LENGTH_SHORT).show();
					}
					spinnerHandler.dismiss();
				}
				@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
					Log.e(TAG, "Failure : "+response.errorBody());
					Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 회원가입을 완료할 수 없습니다.", Toast.LENGTH_SHORT).show();
					spinnerHandler.dismiss();
				}
				@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
					Log.e(TAG, "예상치 못한 오류", t);
					Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 회원가입을 완료할 수 없습니다.", Toast.LENGTH_SHORT).show();
					spinnerHandler.dismiss();
				}
			});
		});

		return view;
	}

	public void setJoinType(JoinType type){
		if(this.type!=type){
			this.type = type;

			editTextEmailPhone.setText("");
			switch(type){
			case WITH_PHONE:
				editTextEmailPhone.setHint(R.string.join_form_phone_number);
				editTextEmailPhone.setInputType(InputType.TYPE_CLASS_PHONE);
				imgEmailPhone.setImageResource(R.drawable.ic_join_phone);
				break;
			case WITH_EMAIL:
				editTextEmailPhone.setHint(R.string.join_form_email);
				editTextEmailPhone.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				imgEmailPhone.setImageResource(R.drawable.ic_join_email);
				break;
			case WITH_KAKAO:
			default:
				throw new IllegalArgumentException("type");
			}
		}
	}
}
