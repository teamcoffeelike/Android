package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.text.Editable;
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
import com.hanul.caramelhomecchiato.activity.JoinActivity.JoinType;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.network.JoinService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.Validate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinFormFragment extends Fragment{
	private static final String TAG = "JoinFormFragment";

	private EditText etEmailPhone;
	private EditText etName;
	private EditText etPassword;
	private EditText etPwConfirm;
	private TextView tvPwCheck;
	private ImageView imgEmailPhone;

	private JoinType type;


	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_join_form, container, false);
		etEmailPhone = view.findViewById(R.id.editTextEmailPhone);
		etName = view.findViewById(R.id.editTextName);
		etPassword = view.findViewById(R.id.editTextPassword);
		etPwConfirm = view.findViewById(R.id.editTextPasswordConfirm);
		tvPwCheck = view.findViewById(R.id.textViewPwCheck);
		imgEmailPhone = view.findViewById(R.id.imgEmailPhone);

		TextWatcher textWatcher = new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
				// 두 비밀번호 동일 여부 검사
				String password = etPassword.getText().toString();
				String confirm = etPwConfirm.getText().toString();
				if(password.equals(confirm)){
					tvPwCheck.setVisibility(View.VISIBLE);
					tvPwCheck.setText("비밀번호가 일치합니다");
					tvPwCheck.setTextColor(getResources().getColor(R.color.ForestGreenTraditional));
				}else{
					tvPwCheck.setVisibility(View.VISIBLE);
					tvPwCheck.setText("비밀번호가 일치하지 않습니다!");
					tvPwCheck.setTextColor(getResources().getColor(R.color.red));
				}
				if(etPassword.getText().toString().length()==0||etPwConfirm.getText().toString().length()==0){
					tvPwCheck.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable s){}
		};

		etPwConfirm.addTextChangedListener(textWatcher);
		etPassword.addTextChangedListener(textWatcher);


		view.findViewById(R.id.buttonConfirm).setOnClickListener(v -> {
			String name = etName.getText().toString().trim();
			String password = etPassword.getText().toString();
			String pwConfirm = etPwConfirm.getText().toString();
			String emailOrPhone = etEmailPhone.getText().toString().trim();

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

			call.enqueue(new Callback<JsonObject>(){
				@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
					JsonObject body = response.body();
					if(body.has("error")){
						String error = body.get("error").getAsString();
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
								Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 회원가입을 완료할 수 없습니다.", Toast.LENGTH_SHORT).show();
								Log.e(TAG, "예상치 못한 오류 : "+error);
						}
					}else{
						Auth.getInstance().setLoginData(body);
						FragmentActivity activity = getActivity();
						if(activity!=null) activity.finish();
					}
				}
				@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
					Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 회원가입을 완료할 수 없습니다.", Toast.LENGTH_SHORT).show();
					Log.e(TAG, "예상치 못한 오류", t);
				}
			});
		});

		return view;
	}


	public void setJoinType(JoinType type){
		this.type = type;

		switch(type){
		case WITH_PHONE:
			etEmailPhone.setHint("핸드폰 번호");
			imgEmailPhone.setImageResource(R.drawable.ic_join_phone);
			break;
		case WITH_EMAIL:
			etEmailPhone.setHint("이메일");
			imgEmailPhone.setImageResource(R.drawable.ic_join_email);
			break;
		case WITH_KAKAO:
		default:
			throw new IllegalArgumentException("type");
		}
	}
}
